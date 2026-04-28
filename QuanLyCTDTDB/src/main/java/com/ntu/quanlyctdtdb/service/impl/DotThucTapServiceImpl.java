package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.DotThucTapDTO;
import com.ntu.quanlyctdtdb.entity.*;
import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.enums.LoaiThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiSinhVien;
import com.ntu.quanlyctdtdb.enums.TrangThaiThucTap;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.service.DotThucTapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DotThucTapServiceImpl implements DotThucTapService {

    private final DotThucTapRepository dotTTRepo;
    private final DanhSachThucTapRepository dsTTRepo;
    private final CtdtHocPhanRepository ctdtHPRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final SinhVienRepository sinhVienRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;
    private final NguoiDungRepository nguoiDungRepo;
    // Phase 7 — 2 cot diem
    private final KetQuaThucTapRepository ketQuaTTRepo;
    private final VaiTroThucTapRepository vaiTroRepo;
    private final GiangVienRepository giangVienRepo;

    @Override
    @Transactional(readOnly = true)
    public List<DotThucTap> findAll() {
        // Phase 4: fetch ctdtHocPhan/hocPhan/hocKy de render list page.
        return dotTTRepo.findAllFetchAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DotThucTap findById(Integer id) {
        // Phase 4: full graph cho chi-tiet.
        return dotTTRepo.findByIdFetchAll(id)
                .orElseThrow(() -> new ResourceNotFoundException("DotThucTap", "MaDotTT", id.toString()));
    }

    @Override
    public DotThucTap create(DotThucTapDTO dto, String maNguoiDungTao) {
        CtdtHocPhanId ctdtHPId = new CtdtHocPhanId(dto.getMaCTDT(), dto.getMaHocPhan());
        CtdtHocPhan ctdtHP = ctdtHPRepo.findById(ctdtHPId)
                .orElseThrow(() -> new ResourceNotFoundException("CtdtHocPhan", "id",
                        dto.getMaCTDT() + "+" + dto.getMaHocPhan()));
        HocKyNamHoc hocKy = hocKyRepo.findById(dto.getMaHocKy())
                .orElseThrow(() -> new ResourceNotFoundException("HocKyNamHoc", "MaHocKy", dto.getMaHocKy()));

        // Rang buoc nghiep vu: Dot thuc tap CHI duoc tao cho Hoc Phan thuoc
        // loai ThucTap hoac KienTap va da duoc phe duyet (docs/02 §3.8, roadmap 5.2).
        HocPhan hp = ctdtHP.getHocPhan();
        if (hp == null) {
            throw new BusinessException(
                    "Hoc Phan trong CTDT " + dto.getMaCTDT() + " chua duoc cau hinh dung.");
        }
        LoaiHocPhan loai = hp.getLoaiHocPhan();
        if (loai != LoaiHocPhan.ThucTap && loai != LoaiHocPhan.KienTap) {
            throw new BusinessException(
                    "Chi co the tao dot thuc tap cho hoc phan loai ThucTap hoac KienTap. "
                    + "Hoc phan '" + hp.getTenHocPhan() + "' dang la " + loai + ".");
        }
        if (hp.getTrangThai() != TrangThaiHocPhan.DaDuyet) {
            throw new BusinessException(
                    "Hoc phan '" + hp.getTenHocPhan() + "' chua duoc phe duyet, "
                    + "khong the tao dot thuc tap.");
        }

        // NguoiTao BAT BUOC NOT NULL (SQL & entity). Truoc day bo qua field nay
        // dan toi DataIntegrityViolationException khi goi create() tu controller.
        NguoiDung nguoiTao = nguoiDungRepo.findById(maNguoiDungTao)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NguoiDung", "MaNguoiDung", maNguoiDungTao));

        DotThucTap dot = DotThucTap.builder()
                .tenDotTT(dto.getTenDotTT())
                .ctdtHocPhan(ctdtHP)
                .hocKy(hocKy)
                .ngayBatDau(dto.getNgayBatDau())
                .ngayKetThuc(dto.getNgayKetThuc())
                .trangThai(TrangThaiDotTT.ChuanBi)
                .nguoiTao(nguoiTao)
                .build();
        return dotTTRepo.save(dot);
    }

    @Override
    public DotThucTap update(Integer id, DotThucTapDTO dto) {
        DotThucTap dot = findById(id);
        // Chi cho sua khi chua di qua buoc phe duyet — sau DaDuyet/DangThucHien/
        // DaKetThuc/DaHuy thi du lieu mang y nghia audit, khong the chinh.
        if (dot.getTrangThai() != TrangThaiDotTT.ChuanBi
                && dot.getTrangThai() != TrangThaiDotTT.ChoDuyet) {
            throw new BusinessException(
                    "Chi co the cap nhat dot thuc tap o trang thai ChuanBi hoac ChoDuyet. "
                    + "Trang thai hien tai: " + dot.getTrangThai());
        }
        // Validate ngay (neu nhap ca 2): NgayBatDau <= NgayKetThuc.
        if (dto.getNgayBatDau() != null && dto.getNgayKetThuc() != null
                && dto.getNgayBatDau().isAfter(dto.getNgayKetThuc())) {
            throw new BusinessException("Ngay bat dau phai truoc hoac bang ngay ket thuc.");
        }
        dot.setTenDotTT(dto.getTenDotTT());
        dot.setNgayBatDau(dto.getNgayBatDau());
        dot.setNgayKetThuc(dto.getNgayKetThuc());

        // Bug fix: cho phep doi HocKy khi sua (truoc day bi bo qua tu DTO,
        // dan toi user khong the dieu chinh ky to chuc sau khi tao).
        // CTDT/HocPhan van locked vi UI disable + auto-add SV phu thuoc cap (CTDT,HP).
        if (dto.getMaHocKy() != null && !dto.getMaHocKy().isBlank()
                && (dot.getHocKy() == null
                    || !dto.getMaHocKy().equals(dot.getHocKy().getMaHocKy()))) {
            HocKyNamHoc hocKy = hocKyRepo.findById(dto.getMaHocKy())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "HocKyNamHoc", "MaHocKy", dto.getMaHocKy()));
            dot.setHocKy(hocKy);
        }
        return dotTTRepo.save(dot);
    }

    @Override
    public DotThucTap guiPheDuyet(Integer id) {
        DotThucTap dot = findById(id);
        if (dot.getTrangThai() != TrangThaiDotTT.ChuanBi) {
            throw new BusinessException("Chi co the gui phe duyet dot o trang thai ChuanBi");
        }
        dot.setTrangThai(TrangThaiDotTT.ChoDuyet);
        return dotTTRepo.save(dot);
    }

    @Override
    public DotThucTap pheduyet(Integer id, String maNguoiDung) {
        DotThucTap dot = findById(id);
        if (dot.getTrangThai() != TrangThaiDotTT.ChoDuyet) {
            throw new BusinessException("Chi co the phe duyet dot o trang thai ChoDuyet");
        }
        // Audit bat buoc cho production: set NguoiDuyet + NgayDuyet (docs/02 §3.8,
        // roadmap Phase 5.2). Truoc day field bi bo qua, bao cao duyet khong truy xuat duoc.
        NguoiDung nguoiDuyet = nguoiDungRepo.findById(maNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "NguoiDung", "MaNguoiDung", maNguoiDung));
        dot.setTrangThai(TrangThaiDotTT.DaDuyet);
        dot.setNguoiDuyet(nguoiDuyet);
        dot.setNgayDuyet(LocalDateTime.now());
        return dotTTRepo.save(dot);
    }

    @Override
    public Map<String, Object> importSinhVien(Integer maDotTT, List<String> dsMaSV) {
        return importSinhVien(maDotTT, dsMaSV, LoaiThucTap.Truong, null);
    }

    @Override
    public Map<String, Object> importSinhVien(Integer maDotTT, List<String> dsMaSV,
                                               LoaiThucTap loaiTT, String maDoanhNghiep) {
        DotThucTap dot = findById(maDotTT);

        // Bug fix Phase 5.2: trang thai dot phai cho phep them SV. Sau DaKetThuc
        // hoac DaHuy thi danh sach phai bi khoa.
        if (dot.getTrangThai() == TrangThaiDotTT.DaHuy
                || dot.getTrangThai() == TrangThaiDotTT.DaKetThuc) {
            throw new BusinessException(
                    "Khong the them SV vao dot dang o trang thai " + dot.getTrangThai()
                    + ". Vui long tao dot moi.");
        }

        // Default loaiThucTap khi controller khong truyen — fix DataIntegrityViolation
        // truoc day xay ra do entity.LoaiThucTap nullable=false ma builder bo qua field.
        final LoaiThucTap finalLoaiTT = loaiTT == null ? LoaiThucTap.Truong : loaiTT;

        // Resolve DN 1 lan: chi can khi loaiTT == DoanhNghiep va co maDN.
        // Validate: DN ton tai + DangHopTac (theo §3.7, 3.8 + WF-08.2 BUOC 3).
        DoanhNghiep dnDefault = null;
        if (finalLoaiTT == LoaiThucTap.DoanhNghiep
                && maDoanhNghiep != null && !maDoanhNghiep.isBlank()) {
            dnDefault = doanhNghiepRepo.findById(maDoanhNghiep.trim()).orElse(null);
            if (dnDefault == null) {
                throw new BusinessException(
                        "Doanh nghiep '" + maDoanhNghiep + "' khong ton tai.");
            }
            if (dnDefault.getTrangThai() != TrangThaiDoanhNghiep.DangHopTac) {
                throw new BusinessException(
                        "Doanh nghiep '" + dnDefault.getTenDoanhNghiep()
                        + "' dang TamNgung — khong the phan cong SV.");
            }
        }

        int success = 0;
        List<String> errors = new ArrayList<>();

        for (String raw : dsMaSV) {
            try {
                if (raw == null || raw.isBlank()) continue;
                String maSV = raw.trim();

                SinhVien sv = sinhVienRepo.findById(maSV).orElse(null);
                if (sv == null) {
                    errors.add("MaSV '" + maSV + "': khong ton tai");
                    continue;
                }
                // Spec WF-08.2: chi cho phep SV DangHoc.
                if (sv.getTrangThaiSV() != TrangThaiSinhVien.DangHoc) {
                    errors.add("MaSV '" + maSV + "': khong o trang thai DangHoc (hien tai: "
                            + sv.getTrangThaiSV() + ")");
                    continue;
                }
                if (dsTTRepo.existsByDotThucTap_MaDotTTAndSinhVien_MaSV(maDotTT, maSV)) {
                    errors.add("MaSV '" + maSV + "': da co trong dot thuc tap");
                    continue;
                }

                DanhSachThucTap ds = DanhSachThucTap.builder()
                        .dotThucTap(dot)
                        .sinhVien(sv)
                        .loaiThucTap(finalLoaiTT)             // CRITICAL: NOT NULL
                        .doanhNghiep(dnDefault)               // null neu loai = Truong
                        .trangThai(TrangThaiThucTap.DaPhanCong)
                        .build();
                dsTTRepo.save(ds);
                success++;
            } catch (Exception e) {
                errors.add("MaSV '" + raw + "': " + e.getMessage());
            }
        }

        log.info("[DotThucTap {}] Import {} SV thanh cong, {} loi.",
                 maDotTT, success, errors.size());

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("errors", errors);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhSachThucTap> findDanhSachSV(Integer maDotTT) {
        // Phase 4: render hoTen + lop + DN -> need fetch.
        return dsTTRepo.findByDotThucTap_MaDotTTFetchSV(maDotTT);
    }

    @Override
    public DanhSachThucTap capNhatKetQua(Integer maDanhSach, String loaiThucTap,
                                          String maDoanhNghiep, String nhanXet) {
        DanhSachThucTap ds = dsTTRepo.findById(maDanhSach)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DanhSachThucTap", "MaThucTap", maDanhSach.toString()));

        // Bug fix Phase 5.2: capNhatKetQua truoc day:
        //  (1) bo qua tham so loaiThucTap (gay UI sai lech),
        //  (2) luon set TrangThai = DangThucTap (khong cho phep ChuyeN tiep
        //      DaKetThuc / DaHuy),
        //  (3) accept nhanXet nhung silently drop (entity khong co field).
        // Cap nhat:
        //  - Cho phep parse loaiThucTap (string -> enum) khi user chon trong UI.
        //  - Validate tinh nhat quan: DoanhNghiep -> bat buoc co MaDN; Truong
        //    -> clear MaDN.
        //  - Chi chuyen DaPhanCong -> DangThucTap khi user thuc su cap nhat
        //    (giu trang thai cu neu da DaKetThuc / DaHuy de bao toan audit).

        // 1. Parse + validate loaiThucTap (optional input).
        if (loaiThucTap != null && !loaiThucTap.isBlank()) {
            try {
                ds.setLoaiThucTap(LoaiThucTap.valueOf(loaiThucTap.trim()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(
                        "Loai thuc tap '" + loaiThucTap + "' khong hop le. "
                        + "Chi chap nhan 'Truong' hoac 'DoanhNghiep'.");
            }
        }

        // 2. DN: cho phep clear (chuoi rong) hoac set moi.
        // Khi loai = Truong va user khong nhap DN -> tu dong clear DN cu.
        if (maDoanhNghiep == null || maDoanhNghiep.isBlank()) {
            // Neu loai cuoi cung la Truong -> clear DN (avoid stale FK).
            if (ds.getLoaiThucTap() == LoaiThucTap.Truong) {
                ds.setDoanhNghiep(null);
            }
            // Neu loai = DoanhNghiep ma user khong dien DN -> giu DN cu (khong xoa).
        } else {
            DoanhNghiep dn = doanhNghiepRepo.findById(maDoanhNghiep.trim())
                    .orElseThrow(() -> new BusinessException(
                            "Doanh nghiep '" + maDoanhNghiep + "' khong ton tai."));
            if (dn.getTrangThai() != TrangThaiDoanhNghiep.DangHopTac) {
                throw new BusinessException(
                        "Doanh nghiep '" + dn.getTenDoanhNghiep()
                        + "' dang TamNgung — khong the phan cong SV.");
            }
            ds.setDoanhNghiep(dn);
            // Neu user nhap DN ma loai van la Truong -> tu nang loai len DoanhNghiep
            // de tranh inconsistent state.
            if (ds.getLoaiThucTap() == LoaiThucTap.Truong) {
                ds.setLoaiThucTap(LoaiThucTap.DoanhNghiep);
            }
        }

        // 3. Cuoi cung validate: neu loai = DoanhNghiep, MUST co DN.
        if (ds.getLoaiThucTap() == LoaiThucTap.DoanhNghiep && ds.getDoanhNghiep() == null) {
            throw new BusinessException(
                    "Loai thuc tap = 'DoanhNghiep' yeu cau ban chon Doanh Nghiep tiep nhan.");
        }

        // 4. Chuyen trang thai: chi nang capnhat DaPhanCong -> DangThucTap.
        // Khong ghi de trang thai cuoi DaKetThuc / DaHuy (audit-safe).
        if (ds.getTrangThai() == TrangThaiThucTap.DaPhanCong) {
            ds.setTrangThai(TrangThaiThucTap.DangThucTap);
        }

        // 5. nhanXet: hien tai entity khong co cot — log de phat hien neu
        // Phase 5.3 them cot. Tranh silently drop khong ai biet.
        if (nhanXet != null && !nhanXet.isBlank()) {
            log.debug("[DotThucTap capNhatKetQua] nhanXet input bi bo qua "
                    + "(entity DanhSachThucTap chua co cot NhanXet — Phase 5.3 deferred). "
                    + "MaThucTap={}, nhanXet='{}'", maDanhSach, nhanXet);
        }

        return dsTTRepo.save(ds);
    }

    // -------------------------------------------------------------------------
    // Phase 7 — He thong 2 cot diem cho Thuc Tap
    // -------------------------------------------------------------------------

    @Override
    @Transactional(readOnly = true)
    public Map<Integer, Map<String, KetQuaThucTap>> getKetQuaMapByDot(Integer maDotTT) {
        // Group: maThucTap -> (maVaiTro -> KetQuaThucTap).
        // Repo da fetch vaiTro + nguoiDanhGia.nguoiDung -> safe khi
        // open-in-view=false va template render gv.hoTen.
        List<KetQuaThucTap> all = ketQuaTTRepo.findByDotFetchAll(maDotTT);
        return all.stream().collect(Collectors.groupingBy(
                kq -> kq.getDanhSachThucTap().getMaThucTap(),
                Collectors.toMap(
                        kq -> kq.getVaiTroThucTap().getMaVaiTro(),
                        kq -> kq,
                        // Neu trung (khong xay ra do UNIQUE), giu cai moi nhat.
                        (a, b) -> b.getUpdatedAt() != null
                                && (a.getUpdatedAt() == null
                                    || b.getUpdatedAt().isAfter(a.getUpdatedAt())) ? b : a
                )));
    }

    @Override
    public KetQuaThucTap capNhatDiem(Integer maDanhSach, String maVaiTro,
                                      BigDecimal diem, String nhanXet,
                                      String maGiangVienDanhGia) {
        // 1. Validate inputs
        if (maVaiTro == null || maVaiTro.isBlank()) {
            throw new BusinessException("Vai tro danh gia khong duoc trong.");
        }
        VaiTroThucTap vaiTro = vaiTroRepo.findById(maVaiTro.trim())
                .orElseThrow(() -> new BusinessException(
                        "Vai tro '" + maVaiTro + "' khong ton tai. Chap nhan: GV_HD, GV_PB, DN, CVHT."));

        if (diem != null && (diem.compareTo(BigDecimal.ZERO) < 0
                            || diem.compareTo(BigDecimal.TEN) > 0)) {
            throw new BusinessException("Diem phai trong khoang [0, 10]. Nhap: " + diem);
        }

        DanhSachThucTap ds = dsTTRepo.findById(maDanhSach)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DanhSachThucTap", "MaThucTap", maDanhSach.toString()));

        // Khong cho nhap diem cho SV da bi huy.
        if (ds.getTrangThai() == TrangThaiThucTap.DaHuy) {
            throw new BusinessException(
                    "Khong the nhap diem cho SV da bi huy phan cong (" + ds.getSinhVien().getMaSV() + ").");
        }

        // 2. Tim row hien co (theo UNIQUE MaThucTap + MaVaiTro). Upsert.
        KetQuaThucTap kq = ketQuaTTRepo
                .findByDanhSachThucTap_MaThucTapAndVaiTroThucTap_MaVaiTro(maDanhSach, maVaiTro.trim())
                .orElse(null);

        // 3. Resolve nguoi danh gia (GiangVien) — bat buoc khi tao moi.
        GiangVien gvDanhGia = null;
        if (maGiangVienDanhGia != null && !maGiangVienDanhGia.isBlank()) {
            gvDanhGia = giangVienRepo.findById(maGiangVienDanhGia.trim())
                    .orElseThrow(() -> new BusinessException(
                            "Giang vien '" + maGiangVienDanhGia + "' khong ton tai."));
        }

        if (kq == null) {
            // Tao moi -> nguoiDanhGia bat buoc (NOT NULL trong SQL).
            if (gvDanhGia == null) {
                throw new BusinessException(
                        "Khi tao moi, can chi dinh nguoi danh gia (Ma GV).");
            }
            kq = KetQuaThucTap.builder()
                    .danhSachThucTap(ds)
                    .vaiTroThucTap(vaiTro)
                    .nguoiDanhGia(gvDanhGia)
                    .diem(diem)
                    .nhanXet(nhanXet != null && !nhanXet.isBlank() ? nhanXet.trim() : null)
                    .build();
        } else {
            // Update — chi cap nhat field user truyen vao. Diem co the duoc set null
            // de "xoa" diem.
            kq.setDiem(diem);
            kq.setNhanXet(nhanXet != null && !nhanXet.isBlank() ? nhanXet.trim() : null);
            if (gvDanhGia != null) {
                kq.setNguoiDanhGia(gvDanhGia);
            }
        }

        // Khi co ket qua -> tu nang trang thai DaPhanCong -> DangThucTap
        // (audit-safe: khong nang khi da DaKetThuc / DaHuy).
        if (ds.getTrangThai() == TrangThaiThucTap.DaPhanCong) {
            ds.setTrangThai(TrangThaiThucTap.DangThucTap);
            dsTTRepo.save(ds);
        }

        log.info("[KetQuaThucTap] Upsert: maDS={}, vaiTro={}, diem={}, gv={}",
                 maDanhSach, maVaiTro, diem, maGiangVienDanhGia);
        return ketQuaTTRepo.save(kq);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getThongKe() {
        // Phase 3 — stat-card row. DotThucTap co 6 trang thai chinh:
        // ChuanBi / ChoDuyet / DaDuyet / DangThucHien / DaKetThuc / DaHuy.
        // UI hien 5 cot dau (DaHuy de minh hoa noise nen bo qua) + 1 cot tong.
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("tong",         dotTTRepo.count());
        map.put("chuanBi",      dotTTRepo.countByTrangThai(TrangThaiDotTT.ChuanBi));
        map.put("choDuyet",     dotTTRepo.countByTrangThai(TrangThaiDotTT.ChoDuyet));
        map.put("daDuyet",      dotTTRepo.countByTrangThai(TrangThaiDotTT.DaDuyet));
        map.put("dangThucHien", dotTTRepo.countByTrangThai(TrangThaiDotTT.DangThucHien));
        map.put("daKetThuc",    dotTTRepo.countByTrangThai(TrangThaiDotTT.DaKetThuc));
        return map;
    }
}
