package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.entity.*;
import com.ntu.quanlyctdtdb.enums.TrangThaiLopHocPhan;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.service.EmailService;
import com.ntu.quanlyctdtdb.service.LopHocPhanService;
import com.ntu.quanlyctdtdb.util.HocKyUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class LopHocPhanServiceImpl implements LopHocPhanService {

    private final LopHocPhanRepository lopHocPhanRepo;
    private final CtdtHocPhanRepository ctdtHocPhanRepo;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;
    private final DanhSachSvLopHocPhanRepository dsSvRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final EmailService emailService;

    @Override
    public int taoLopHocPhanChoCTDT(String maCTDT, String maHocKy, Map<String, Integer> soLopOverride) {
        HocKyNamHoc hocKy = hocKyRepo.findById(maHocKy)
                .orElseThrow(() -> new ResourceNotFoundException("HocKyNamHoc", "MaHocKy", maHocKy));
        ChuongTrinhDaoTao ctdt = ctdtRepo.findById(maCTDT)
                .orElseThrow(() -> new ResourceNotFoundException("ChuongTrinhDaoTao", "MaCTDT", maCTDT));

        // BUG-FIX: CtdtHocPhan.hocKyThu la so ky THEO KHUNG CTDT (1..8 voi
        // CTDT 4 nam), KHONG phai so ky trong nam hoc. Truoc day code lay
        // parseHocKyThu(maHocKy) = 1 hoac 2 nen bat ky CTDT nao + HocKy nao
        // cung chi tao lop cho HP xep o HK1/HK2 — SAI. Phai quy doi dua tren
        // CTDT.khoa (nam bat dau) va HocKy.maHocKy.
        // Vi du: CTDT khoa 2022 + HK1-2023 -> hocKyThu = 3 (nam thu 2, ky 1).
        int hocKyThu = HocKyUtil.toProgramSemester(ctdt.getKhoa(), maHocKy);
        if (hocKyThu <= 0) {
            int hkInYear = HocKyUtil.parseHkIndexInYear(maHocKy);
            if (hkInYear == 3) {
                throw new BusinessException(
                        "Hoc ky he (" + maHocKy + ") khong nam trong khung CTDT chinh khoa. "
                      + "Khong the tao lop hang loat cho ky he qua tinh nang nay.");
            }
            throw new BusinessException(
                    "Khong the quy doi " + maHocKy + " sang ky cua CTDT " + maCTDT
                  + " (Khoa='" + ctdt.getKhoa() + "'). Kiem tra lai Khoa cua CTDT "
                  + "va dinh dang MaHocKy (HKn-YYYY).");
        }

        List<CtdtHocPhan> dsHocPhan = ctdtHocPhanRepo.findById_MaCTDTAndHocKyThu(maCTDT, hocKyThu);
        if (dsHocPhan.isEmpty()) {
            throw new BusinessException(
                    "CTDT " + maCTDT + " khong co hoc phan nao o HK" + hocKyThu
                    + " (tuong ung " + maHocKy + " cua khoa " + ctdt.getKhoa() + "). "
                    + "Vao CTDT chi tiet de them HP cho ky nay truoc.");
        }

        Map<String, Integer> override = soLopOverride == null ? Map.of() : soLopOverride;
        int tongTao = 0;
        for (CtdtHocPhan ctdtHP : dsHocPhan) {
            String maHocPhan = ctdtHP.getId().getMaHocPhan();
            int soLopDefault = ctdtHP.getSoLopDuKien() != null ? ctdtHP.getSoLopDuKien() : 1;
            int soLop = override.getOrDefault(maHocPhan, soLopDefault);
            if (soLop < 1) soLop = 1; // guard — luon mo it nhat 1 lop neu HP nam trong danh sach

            for (int i = 1; i <= soLop; i++) {
                LopHocPhanId id = new LopHocPhanId(maCTDT, maHocPhan, maHocKy, i);
                if (!lopHocPhanRepo.existsById(id)) {
                    LopHocPhan lhp = LopHocPhan.builder()
                            .id(id)
                            .trangThai(TrangThaiLopHocPhan.DangMo)
                            .siSoToiDa(50)
                            .siSoThucTe(0)
                            .build();
                    lopHocPhanRepo.save(lhp);
                    tongTao++;
                }
            }
        }
        log.info("[LopHocPhan] Tao lop cho CTDT={} HK{}={}: {} HP, {} lop moi",
                maCTDT, hocKyThu, maHocKy, dsHocPhan.size(), tongTao);
        return tongTao;
    }

    @Override
    public LopHocPhan phanCongGiangVien(LopHocPhanId id, String maGV) {
        LopHocPhan lhp = findById(id);
        GiangVien gv = giangVienRepo.findById(maGV)
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", maGV));
        lhp.setGiangVien(gv);
        LopHocPhan saved = lopHocPhanRepo.save(lhp);

        // Gui email thong bao GV - lay ten HP va ten HocKy qua repository
        try {
            CtdtHocPhanId ctdtHpId = new CtdtHocPhanId(id.getMaCTDT(), id.getMaHocPhan());
            String tenHP = ctdtHocPhanRepo.findById(ctdtHpId)
                    .map(c -> c.getHocPhan().getTenHocPhan()).orElse(id.getMaHocPhan());
            String tenHocKy = hocKyRepo.findById(id.getMaHocKy())
                    .map(HocKyNamHoc::getTenHocKy).orElse(id.getMaHocKy());
            emailService.guiThongBaoPhanCongLop(gv.getNguoiDung().getEmail(), tenHP,
                    id.getMaLopHocPhan().toString(), tenHocKy);
        } catch (Exception e) {
            log.warn("Khong gui duoc email phan cong GV: {}", e.getMessage());
        }
        return saved;
    }

    @Override
    public LopHocPhan toggleTrangThai(LopHocPhanId id) {
        LopHocPhan lhp = findById(id);
        lhp.setTrangThai(
            lhp.getTrangThai() == TrangThaiLopHocPhan.DangMo
                ? TrangThaiLopHocPhan.DaDong
                : TrangThaiLopHocPhan.DangMo
        );
        return lopHocPhanRepo.save(lhp);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhan> findByCTDTAndHocKy(String maCTDT, String maHocKy) {
        // Dung query JOIN FETCH de template hien thi giangVien.hoTen
        // ma khong bi LazyInitializationException (open-in-view=false).
        return lopHocPhanRepo.findByCtdtAndHocKyFetch(maCTDT, maHocKy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhan> findByCTDT(String maCTDT) {
        // Filter "OR" tren trang danh sach: cho phep nguoi dung chi chon CTDT
        // ma khong chon hoc ky — hien tat ca lop da/dang mo cua CTDT do.
        return lopHocPhanRepo.findByCtdtFetch(maCTDT);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhan> findByHocKy(String maHocKy) {
        // Filter "OR" tren trang danh sach: cho phep TTDTXS/PDT xem tong hop
        // toan bo lop mo trong mot ky across cac CTDT (vd HK1-2024).
        return lopHocPhanRepo.findByHocKyFetch(maHocKy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhan> findChuaPhanCongGV() {
        return lopHocPhanRepo.findChuaPhanCongGiangVien();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhan> findByGiangVien(String maGV) {
        if (maGV == null || maGV.isBlank()) return List.of();
        return lopHocPhanRepo.findByGiangVienMaGVFetch(maGV);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhan> findBySinhVien(String maSV) {
        if (maSV == null || maSV.isBlank()) return List.of();
        return lopHocPhanRepo.findBySinhVienMaSVFetch(maSV);
    }

    @Override
    public DanhSachSvLopHocPhan dangKyLopHocPhan(LopHocPhanId lopId, String maSV) {
        LopHocPhan lhp = findById(lopId);
        if (lhp.getTrangThai() != TrangThaiLopHocPhan.DangMo) {
            throw new BusinessException("Lop hoc phan chua mo dang ky");
        }
        SinhVien sv = sinhVienRepo.findById(maSV)
                .orElseThrow(() -> new ResourceNotFoundException("SinhVien", "MaSV", maSV));

        DanhSachSvLopHocPhanId dsId = new DanhSachSvLopHocPhanId(
                maSV, lopId.getMaCTDT(), lopId.getMaHocPhan(), lopId.getMaHocKy(), lopId.getMaLopHocPhan()
        );
        if (dsSvRepo.existsById(dsId)) {
            throw new BusinessException("Sinh vien da dang ky lop nay");
        }

        // lopHocPhan association da bi bo khoi DanhSachSvLopHocPhan de tranh duplicate column;
        // quan he voi LopHocPhan duoc suy ra tu @EmbeddedId (maCTDT + maHocPhan + maHocKy + maLopHocPhan).
        DanhSachSvLopHocPhan ds = DanhSachSvLopHocPhan.builder()
                .id(dsId).sinhVien(sv).daCanhBao(false).build();
        return dsSvRepo.save(ds);
    }

    @Override
    public DanhSachSvLopHocPhan canhBaoSinhVien(LopHocPhanId lopId, String maSV,
                                                  String nhanXet, String emailCVHT) {
        DanhSachSvLopHocPhanId dsId = new DanhSachSvLopHocPhanId(
                maSV, lopId.getMaCTDT(), lopId.getMaHocPhan(), lopId.getMaHocKy(), lopId.getMaLopHocPhan()
        );
        DanhSachSvLopHocPhan ds = dsSvRepo.findById(dsId)
                .orElseThrow(() -> new ResourceNotFoundException("DanhSachSvLopHocPhan", "id", dsId.toString()));
        ds.setDaCanhBao(true);
        ds.setNhanXet(nhanXet);
        dsSvRepo.save(ds);

        // Gui email CVHT - lay ten HP qua repository (dung ds.getId() de tranh navigation ngam)
        try {
            String hoTenSV = ds.getSinhVien().getNguoiDung().getHoTen();
            CtdtHocPhanId ctdtHpId = new CtdtHocPhanId(
                    ds.getId().getMaCTDT(), ds.getId().getMaHocPhan());
            String tenHP = ctdtHocPhanRepo.findById(ctdtHpId)
                    .map(c -> c.getHocPhan().getTenHocPhan()).orElse(ds.getId().getMaHocPhan());
            emailService.guiCanhBaoSinhVien(emailCVHT, hoTenSV, tenHP, nhanXet);
        } catch (Exception e) {
            log.warn("Khong gui duoc email canh bao SV: {}", e.getMessage());
        }
        return ds;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhSachSvLopHocPhan> findSinhVienTrongLop(LopHocPhanId lopId) {
        return dsSvRepo.findDanhSachSinhVienLop(
                lopId.getMaCTDT(), lopId.getMaHocPhan(), lopId.getMaHocKy(), lopId.getMaLopHocPhan()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getThongKe() {
        // Datasize lop HP co the len toi vai nghin record - dung COUNT()
        // truc tiep o DB de tranh load toan bo bang vao memory.
        Map<String, Long> map = new LinkedHashMap<>();
        long tong = lopHocPhanRepo.count();
        long dangMo = lopHocPhanRepo.countByTrangThai(TrangThaiLopHocPhan.DangMo);
        long daDong = lopHocPhanRepo.countByTrangThai(TrangThaiLopHocPhan.DaDong);
        // findChuaPhanCongGiangVien() chi tra ve lop "DangMo + chua co GV";
        // dung size() vi service hien chua co count rieng (datasize ket qua
        // thuong < 50 nen chap nhan duoc).
        long chuaPhanCong = lopHocPhanRepo.findChuaPhanCongGiangVien().size();
        map.put("tongLop", tong);
        map.put("dangMo", dangMo);
        map.put("daDong", daDong);
        map.put("chuaPhanCong", chuaPhanCong);
        return map;
    }

    // Helper
    private LopHocPhan findById(LopHocPhanId id) {
        return lopHocPhanRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LopHocPhan", "id", id.toString()));
    }
}
