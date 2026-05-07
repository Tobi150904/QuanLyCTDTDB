package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.DotKienTapDTO;
import com.ntu.quanlyctdtdb.entity.*;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;
import com.ntu.quanlyctdtdb.enums.TrangThaiSinhVien;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.service.DotKienTapService;
import com.ntu.quanlyctdtdb.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service impl cho DotKienTap.
 * Quy tac nghiep vu: docs/02 §3.7, docs/03 WF-07.1..WF-07.4
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DotKienTapServiceImpl implements DotKienTapService {

    private final DotKienTapRepository dotKTRepo;
    private final DanhSachSvKienTapRepository dsSvKTRepo;
    private final LopHanhChinhRepository lopHCRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final GiangVienRepository giangVienRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;
    private final SinhVienRepository sinhVienRepo;
    private final NguoiDungRepository nguoiDungRepo;
    private final FileStorageUtil fileStorageUtil;

    // =========================================================================
    // READ
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public List<DotKienTap> findAll() {
        // Phase 4: dung findAllFetchAll() de tranh LazyInitException
        // khi template render lop/hocKy/gv/dn (OSIV=false).
        return dotKTRepo.findAllFetchAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DotKienTap findById(Integer id) {
        // Phase 4: full graph cho chi-tiet (audit panel can nguoiTao/nguoiDuyet).
        return dotKTRepo.findByIdFetchAll(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DotKienTap", "MaDotKT", id == null ? "null" : id.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhSachSvKienTap> findDanhSachSVKienTap(Integer maDotKT) {
        // Phase 4: bang SV kem hoTen + lop -> need fetch.
        return dsSvKTRepo.findById_MaDotKTFetchSV(maDotKT);
    }

    // =========================================================================
    // CREATE - WF-07.1 BUOC 2 (co Auto-Add SV)
    // =========================================================================

    @Override
    public DotKienTap create(DotKienTapDTO dto, MultipartFile fileMinhChung, String maNguoiDungTao) {
        // 1. Validate FK
        LopHanhChinh lhc = lopHCRepo.findById(dto.getMaLopHC())
                .orElseThrow(() -> new ResourceNotFoundException("LopHanhChinh", "MaLopHC", dto.getMaLopHC()));
        HocKyNamHoc hocKy = hocKyRepo.findById(dto.getMaHocKy())
                .orElseThrow(() -> new ResourceNotFoundException("HocKyNamHoc", "MaHocKy", dto.getMaHocKy()));
        DoanhNghiep dn = doanhNghiepRepo.findById(dto.getMaDoanhNghiep())
                .orElseThrow(() -> new ResourceNotFoundException("DoanhNghiep", "MaDN", dto.getMaDoanhNghiep()));

        // 2. Validate nghiep vu: DN phai DangHopTac (docs/02 §3.7)
        if (dn.getTrangThai() != TrangThaiDoanhNghiep.DangHopTac) {
            throw new BusinessException(
                    "Doanh nghiep '" + dn.getTenDoanhNghiep() + "' dang TamNgung hop tac, "
                    + "khong the tao dot kien tap voi DN nay.");
        }
        // Validate kinh phi >= 0 ngay khi tao moi (defense-in-depth — DTO validation
        // co the bi bypass neu controller bo @Valid).
        if (dto.getKinhPhiChung() != null && dto.getKinhPhiChung().signum() < 0) {
            throw new BusinessException("Kinh phi chung khong duoc am.");
        }
        if (dto.getKinhPhiTungSV() != null && dto.getKinhPhiTungSV().signum() < 0) {
            throw new BusinessException("Kinh phi tung SV khong duoc am.");
        }

        // 3. GV phu trach (optional luc tao)
        GiangVien gvPhuTrach = null;
        if (dto.getMaGVPhuTrach() != null && !dto.getMaGVPhuTrach().isBlank()) {
            gvPhuTrach = giangVienRepo.findById(dto.getMaGVPhuTrach())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "GiangVien", "MaGV", dto.getMaGVPhuTrach()));
        }

        // 4. NguoiTao (BAT BUOC NOT NULL trong DB)
        NguoiDung nguoiTao = nguoiDungRepo.findById(maNguoiDungTao)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NguoiDung", "MaNguoiDung", maNguoiDungTao));

        // 5. Luu file minh chung (neu co)
        String savedFile = null;
        if (fileMinhChung != null && !fileMinhChung.isEmpty()) {
            savedFile = fileStorageUtil.saveFile(fileMinhChung, "kien-tap", "DotKT");
        }

        // 6. INSERT DotKienTap
        DotKienTap dot = DotKienTap.builder()
                .tenDotKT(dto.getTenDotKT())
                .lopHanhChinh(lhc)
                .hocKy(hocKy)
                .thoiGian(dto.getThoiGian())
                .gvPhuTrach(gvPhuTrach)
                .doanhNghiep(dn)
                .kinhPhiChung(dto.getKinhPhiChung())
                .kinhPhiTungSV(dto.getKinhPhiTungSV())
                .fileMinhChung(savedFile)
                .trangThai(TrangThaiDotKT.ChuanBi)
                .nguoiTao(nguoiTao)
                .build();
        DotKienTap saved = dotKTRepo.save(dot);

        // 7. AUTO-ADD SV DangHoc cua lop (HYBRID RULE - docs/02 §3.7)
        List<SinhVien> svDangHoc = sinhVienRepo.findByLopAndTrangThai(
                lhc.getMaLopHC(), TrangThaiSinhVien.DangHoc);

        if (svDangHoc.isEmpty()) {
            log.warn("[DotKienTap {}] Lop {} khong co SV DangHoc nao de auto-add.",
                    saved.getMaDotKT(), lhc.getMaLopHC());
        } else {
            for (SinhVien sv : svDangHoc) {
                DanhSachSvKienTapId id = new DanhSachSvKienTapId(saved.getMaDotKT(), sv.getMaSV());
                DanhSachSvKienTap row = DanhSachSvKienTap.builder()
                        .id(id)
                        .dotKienTap(saved)
                        .sinhVien(sv)
                        .daThamGia(Boolean.TRUE)
                        .build();
                dsSvKTRepo.save(row);
            }
            log.info("[DotKienTap {}] Auto-add {} SV DangHoc tu lop {} vao danh sach kien tap.",
                    saved.getMaDotKT(), svDangHoc.size(), lhc.getMaLopHC());
        }

        return saved;
    }

    // =========================================================================
    // UPDATE
    // =========================================================================

    @Override
    public DotKienTap update(Integer id, DotKienTapDTO dto, MultipartFile fileMinhChung) {
        DotKienTap dot = findById(id);

        // Chi cho sua khi chua duoc phe duyet
        if (dot.getTrangThai() != TrangThaiDotKT.ChuanBi
                && dot.getTrangThai() != TrangThaiDotKT.ChoDuyet) {
            throw new BusinessException(
                    "Chi co the cap nhat dot o trang thai ChuanBi hoac ChoDuyet. "
                    + "Trang thai hien tai: " + dot.getTrangThai());
        }

        dot.setTenDotKT(dto.getTenDotKT());
        dot.setThoiGian(dto.getThoiGian());
        dot.setKinhPhiChung(dto.getKinhPhiChung());
        dot.setKinhPhiTungSV(dto.getKinhPhiTungSV());

        // Doi HocKy
        if (dto.getMaHocKy() != null && !dto.getMaHocKy().isBlank()
                && !dto.getMaHocKy().equals(dot.getHocKy().getMaHocKy())) {
            HocKyNamHoc hocKy = hocKyRepo.findById(dto.getMaHocKy())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "HocKyNamHoc", "MaHocKy", dto.getMaHocKy()));
            dot.setHocKy(hocKy);
        }

        // Doi DN (van giu rang buoc DangHopTac)
        if (dto.getMaDoanhNghiep() != null && !dto.getMaDoanhNghiep().isBlank()
                && !dto.getMaDoanhNghiep().equals(dot.getDoanhNghiep().getMaDoanhNghiep())) {
            DoanhNghiep dn = doanhNghiepRepo.findById(dto.getMaDoanhNghiep())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "DoanhNghiep", "MaDN", dto.getMaDoanhNghiep()));
            if (dn.getTrangThai() != TrangThaiDoanhNghiep.DangHopTac) {
                throw new BusinessException(
                        "Doanh nghiep '" + dn.getTenDoanhNghiep() + "' dang TamNgung hop tac.");
            }
            dot.setDoanhNghiep(dn);
        }

        // GV phu trach: cho phep CLEAR (gan null) khi user submit chuoi rong tu
        // dropdown "-- Chua phan cong --". Bug truoc day: chi update khi co gia tri,
        // user khong co cach nao bo GV neu da gan nham.
        if (dto.getMaGVPhuTrach() == null || dto.getMaGVPhuTrach().isBlank()) {
            dot.setGvPhuTrach(null);
        } else {
            GiangVien gv = giangVienRepo.findById(dto.getMaGVPhuTrach())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "GiangVien", "MaGV", dto.getMaGVPhuTrach()));
            dot.setGvPhuTrach(gv);
        }

        // Validate kinh phi (neu dien): khong am — bao ve UI khoi de quy am.
        if (dto.getKinhPhiChung() != null && dto.getKinhPhiChung().signum() < 0) {
            throw new BusinessException("Kinh phi chung khong duoc am.");
        }
        if (dto.getKinhPhiTungSV() != null && dto.getKinhPhiTungSV().signum() < 0) {
            throw new BusinessException("Kinh phi tung SV khong duoc am.");
        }

        // Luu file moi neu co
        if (fileMinhChung != null && !fileMinhChung.isEmpty()) {
            String savedFile = fileStorageUtil.saveFile(fileMinhChung, "kien-tap", "DotKT");
            // Xoa file cu (neu co) chi sau khi luu file moi thanh cong
            if (dot.getFileMinhChung() != null) {
                fileStorageUtil.deleteFile(dot.getFileMinhChung());
            }
            dot.setFileMinhChung(savedFile);
        }

        // LUU Y: khong doi maLopHC sau khi tao dot (vi da auto-add SV). Neu muon doi lop
        // phai huy dot nay va tao moi.
        return dotKTRepo.save(dot);
    }

    // =========================================================================
    // STATE TRANSITIONS - WF-07.1 BUOC 3, 4, 5
    // =========================================================================

    @Override
    public DotKienTap guiPheDuyet(Integer id) {
        DotKienTap dot = findById(id);
        if (dot.getTrangThai() != TrangThaiDotKT.ChuanBi) {
            throw new BusinessException(
                    "Chi co the gui phe duyet dot o trang thai ChuanBi.");
        }
        // Phai co it nhat 1 SV DaThamGia=1 (tranh nop danh sach rong)
        long soSVThamGia = dsSvKTRepo.findById_MaDotKT(id).stream()
                .filter(r -> Boolean.TRUE.equals(r.getDaThamGia()))
                .count();
        if (soSVThamGia == 0) {
            throw new BusinessException(
                    "Khong the gui phe duyet: danh sach SV tham gia dang rong. "
                    + "Vui long them SV hoac dong bo lai danh sach lop.");
        }
        dot.setTrangThai(TrangThaiDotKT.ChoDuyet);
        return dotKTRepo.save(dot);
    }

    @Override
    public DotKienTap pheduyet(Integer id, String maNguoiDuyet) {
        DotKienTap dot = findById(id);
        if (dot.getTrangThai() != TrangThaiDotKT.ChoDuyet) {
            throw new BusinessException(
                    "Chi co the phe duyet dot o trang thai ChoDuyet.");
        }
        NguoiDung nguoiDuyet = nguoiDungRepo.findById(maNguoiDuyet)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NguoiDung", "MaNguoiDung", maNguoiDuyet));

        dot.setTrangThai(TrangThaiDotKT.DaDuyet);
        dot.setNguoiDuyet(nguoiDuyet);
        dot.setNgayDuyet(LocalDateTime.now());
        return dotKTRepo.save(dot);
    }

    @Override
    public DotKienTap hoanThanh(Integer id) {
        DotKienTap dot = findById(id);
        if (dot.getTrangThai() != TrangThaiDotKT.DaDuyet) {
            throw new BusinessException(
                    "Chi co the chuyen sang DaThucHien khi dot dang o trang thai DaDuyet.");
        }
        dot.setTrangThai(TrangThaiDotKT.DaThucHien);
        return dotKTRepo.save(dot);
    }

    @Override
    public DotKienTap huy(Integer id) {
        DotKienTap dot = findById(id);
        if (dot.getTrangThai() == TrangThaiDotKT.DaHuy) {
            throw new BusinessException("Dot nay da bi huy truoc do.");
        }
        dot.setTrangThai(TrangThaiDotKT.DaHuy);
        return dotKTRepo.save(dot);
    }

    // =========================================================================
    // WF-07.2: Toggle DaThamGia
    // =========================================================================

    @Override
    public DanhSachSvKienTap capNhatDaThamGia(Integer maDotKT, String maSV, boolean daThamGia) {
        DotKienTap dot = findById(maDotKT);
        if (dot.getTrangThai() == TrangThaiDotKT.DaHuy) {
            throw new BusinessException(
                    "Dot da bi huy, khong the cap nhat trang thai tham gia cua sinh vien.");
        }

        DanhSachSvKienTapId id = new DanhSachSvKienTapId(maDotKT, maSV);
        DanhSachSvKienTap row = dsSvKTRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DanhSachSinhVienKienTap", "(MaDotKT,MaSV)", maDotKT + "," + maSV));

        row.setDaThamGia(daThamGia);
        return dsSvKTRepo.save(row);
    }

    // =========================================================================
    // WF-07.3: Dong bo danh sach SV
    // =========================================================================

    @Override
    public int dongBoDanhSachSV(Integer maDotKT) {
        DotKienTap dot = findById(maDotKT);
        if (dot.getTrangThai() == TrangThaiDotKT.DaHuy) {
            throw new BusinessException("Dot da bi huy, khong the dong bo danh sach.");
        }
        String maLopHC = dot.getLopHanhChinh().getMaLopHC();

        // Lay danh sach SV DangHoc hien tai cua lop
        List<SinhVien> svDangHoc = sinhVienRepo.findByLopAndTrangThai(
                maLopHC, TrangThaiSinhVien.DangHoc);

        // Lay danh sach MaSV da co trong bang de skip
        Set<String> daCo = new HashSet<>();
        for (DanhSachSvKienTap r : dsSvKTRepo.findById_MaDotKT(maDotKT)) {
            daCo.add(r.getId().getMaSV());
        }

        int added = 0;
        for (SinhVien sv : svDangHoc) {
            if (daCo.contains(sv.getMaSV())) continue;
            DanhSachSvKienTapId id = new DanhSachSvKienTapId(maDotKT, sv.getMaSV());
            DanhSachSvKienTap row = DanhSachSvKienTap.builder()
                    .id(id)
                    .dotKienTap(dot)
                    .sinhVien(sv)
                    .daThamGia(Boolean.TRUE)
                    .build();
            dsSvKTRepo.save(row);
            added++;
        }
        log.info("[DotKienTap {}] Dong bo danh sach: them moi {} SV.", maDotKT, added);
        return added;
    }

    // =========================================================================
    // WF-07.4: Nhan xet GV / DN
    // =========================================================================

    @Override
    public DotKienTap nhanXetGV(Integer maDotKT, String maNguoiDungHienTai, String nhanXet) {
        DotKienTap dot = findById(maDotKT);

        if (dot.getTrangThai() != TrangThaiDotKT.DaDuyet
                && dot.getTrangThai() != TrangThaiDotKT.DaThucHien) {
            throw new BusinessException(
                    "Chi co the nhap nhan xet khi dot o trang thai DaDuyet hoac DaThucHien.");
        }
        if (dot.getGvPhuTrach() == null) {
            throw new BusinessException("Dot nay chua co GV phu trach.");
        }
        // BUG-FIX: MaGV != MaNguoiDung. Truoc day so sanh truc tiep
        // gvPhuTrach.maGV voi maNguoiDung -> luon FALSE -> GV phu trach
        // KHONG BAO GIO nhap duoc nhan xet. Resolve qua giangVienRepo.
        GiangVien gvHienTai = giangVienRepo
                .findByNguoiDung_MaNguoiDung(maNguoiDungHienTai).orElse(null);
        if (gvHienTai == null
                || !dot.getGvPhuTrach().getMaGV().equals(gvHienTai.getMaGV())) {
            throw new BusinessException(
                    "Ban khong phai la GV phu trach cua dot nay.");
        }

        dot.setNhanXetGV(nhanXet);
        return dotKTRepo.save(dot);
    }

    @Override
    public DotKienTap nhanXetDN(Integer maDotKT, String maNguoiDungHienTai, String nhanXet) {
        DotKienTap dot = findById(maDotKT);

        if (dot.getTrangThai() != TrangThaiDotKT.DaDuyet
                && dot.getTrangThai() != TrangThaiDotKT.DaThucHien) {
            throw new BusinessException(
                    "Chi co the nhap nhan xet khi dot o trang thai DaDuyet hoac DaThucHien.");
        }

        // BUG-FIX: NV DN gio la NguoiDung loai DoanhNghiep voi FK doanhNghiep
        // (Phase 7 refactor). Resolve maNguoiDung -> NguoiDung.maDoanhNghiep
        // roi so voi dot.maDN (dung ID space).
        NguoiDung caller = nguoiDungRepo.findById(maNguoiDungHienTai).orElse(null);
        String maDnCuaCaller = caller != null ? caller.getMaDoanhNghiep() : null;
        String maDnCuaDot = dot.getDoanhNghiep() != null
                ? dot.getDoanhNghiep().getMaDoanhNghiep() : null;
        if (maDnCuaCaller == null || maDnCuaDot == null
                || !maDnCuaCaller.equals(maDnCuaDot)) {
            throw new BusinessException(
                    "Ban khong phai la doanh nghiep tiep don cua dot nay.");
        }

        dot.setNhanXetDN(nhanXet);
        return dotKTRepo.save(dot);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getThongKe() {
        // Datasize DotKienTap thuong nho (~hang chuc), dung COUNT() truc tiep
        // de nhat quan voi pattern HocPhan/CTDT/DoanhNghiep getThongKe.
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("tong",       dotKTRepo.count());
        map.put("chuanBi",    dotKTRepo.countByTrangThai(TrangThaiDotKT.ChuanBi));
        map.put("choDuyet",   dotKTRepo.countByTrangThai(TrangThaiDotKT.ChoDuyet));
        map.put("daDuyet",    dotKTRepo.countByTrangThai(TrangThaiDotKT.DaDuyet));
        map.put("daThucHien", dotKTRepo.countByTrangThai(TrangThaiDotKT.DaThucHien));
        return map;
    }
}
