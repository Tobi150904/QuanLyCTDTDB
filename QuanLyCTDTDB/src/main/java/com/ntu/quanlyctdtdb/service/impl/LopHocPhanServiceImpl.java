package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.entity.*;
import com.ntu.quanlyctdtdb.enums.TrangThaiLopHocPhan;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.service.EmailService;
import com.ntu.quanlyctdtdb.service.LopHocPhanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    private final KeHoachMoLopRepository keHoachMoLopRepo;
    private final DoiNguGiangVienHpRepository doiNguGvRepo;
    private final EmailService emailService;

    /**
     * Tao hang loat LopHocPhan cho 1 CTDT trong 1 HocKy cu the.
     *
     * <p>Chi tao lop cho nhung HP co {@code hocKyThu} KHOP voi vi tri cua
     * {@code maHocKy} trong tien trinh CTDT (dua tren {@code CTDT.khoa} +
     * parse {@code MaHocKy}).</p>
     *
     * <p>So lop cho moi HP duoc lay tu {@link KeHoachMoLop} (neu co); fallback
     * sang {@code CTDT_HocPhan.SoLopDuKien} (gia tri mac dinh khi dang ky CTDT).</p>
     *
     * @return so LopHocPhan duoc tao moi
     */
    @Override
    public int taoLopHocPhanChoCTDT(String maCTDT, String maHocKy) {
        HocKyNamHoc hocKy = hocKyRepo.findById(maHocKy)
                .orElseThrow(() -> new ResourceNotFoundException("HocKyNamHoc", "MaHocKy", maHocKy));
        ChuongTrinhDaoTao ctdt = ctdtRepo.findById(maCTDT)
                .orElseThrow(() -> new ResourceNotFoundException("ChuongTrinhDaoTao", "MaCTDT", maCTDT));

        // Tinh vi tri cua HocKy nay trong vong doi CTDT (1..10)
        // Vi du CTDT khoa 2023 + HocKy "HK2-2024" -> (2024-2023)*2 + 2 = 4
        int targetHocKyThu = tinhHocKyThuTrongCTDT(ctdt, hocKy);
        if (targetHocKyThu <= 0) {
            log.warn("[LopHocPhan] HocKy {} khong khop voi khoa CTDT {}. Khong tao lop.",
                    maHocKy, maCTDT);
            return 0;
        }

        List<CtdtHocPhan> dsHocPhan = ctdtHocPhanRepo.findById_MaCTDTAndHocKyThu(maCTDT, targetHocKyThu);
        int created = 0;
        for (CtdtHocPhan ctdtHP : dsHocPhan) {
            String maHP = ctdtHP.getId().getMaHocPhan();

            // Uu tien so lop trong KeHoachMoLop; neu khong co thi fallback CTDT_HocPhan.SoLopDuKien
            int soLop = keHoachMoLopRepo.findById(new KeHoachMoLopId(maHP, maHocKy))
                    .map(KeHoachMoLop::getSoLopDuKien)
                    .orElseGet(() -> ctdtHP.getSoLopDuKien() != null ? ctdtHP.getSoLopDuKien() : 1);

            for (int i = 1; i <= soLop; i++) {
                LopHocPhanId id = new LopHocPhanId(maCTDT, maHP, maHocKy, i);
                if (!lopHocPhanRepo.existsById(id)) {
                    LopHocPhan lhp = LopHocPhan.builder()
                            .id(id)
                            .trangThai(TrangThaiLopHocPhan.DangMo)
                            .siSoToiDa(50)
                            .siSoThucTe(0)
                            .build();
                    lopHocPhanRepo.save(lhp);
                    created++;
                }
            }
        }
        log.info("[LopHocPhan] Tao lop cho CTDT={} HocKy={} (HKthu={}): {} HP, {} lop moi",
                maCTDT, maHocKy, targetHocKyThu, dsHocPhan.size(), created);
        return created;
    }

    /**
     * Map tu (CTDT.khoa, MaHocKy) sang "hoc ky thu may" trong chuong trinh.
     * Quy uoc: HK1-YYYY la ky chinh (mua thu), HK2-YYYY la ky xuan,
     * HK3-YYYY la ky he. Tren thuc te moi nam hoc chi dem HK1 va HK2
     * vao tien trinh CTDT; HK3 (he) giu nguyen hoc ky thu cua HK2.
     *
     * @return so hoc ky trong CTDT (1..10) hoac 0 neu khong hop le
     */
    private int tinhHocKyThuTrongCTDT(ChuongTrinhDaoTao ctdt, HocKyNamHoc hocKy) {
        int namBatDauCTDT = parseKhoa(ctdt.getKhoa());
        if (namBatDauCTDT <= 0) return 0;

        int namBatDauHK = hocKy.getNamBatDau();
        int hkThu = hocKy.getHocKyThu();
        if (namBatDauHK <= 0 || hkThu <= 0) return 0;

        int diff = namBatDauHK - namBatDauCTDT;
        if (diff < 0) return 0;

        if (hkThu == 3) {
            // Ky he: dem nhu HK2 cua nam do
            return diff * 2 + 2;
        }
        return diff * 2 + hkThu;
    }

    private int parseKhoa(String khoa) {
        if (khoa == null) return 0;
        try {
            return Integer.parseInt(khoa.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public LopHocPhan phanCongGiangVien(LopHocPhanId id, String maGV) {
        LopHocPhan lhp = findById(id);
        GiangVien gv = giangVienRepo.findById(maGV)
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", maGV));

        // SOFT CHECK: GV nen thuoc DoiNguGiangVienHP cua HocPhan nay.
        // Van cho phan cong (quy che linh hoat) nhung log WARN de tracking.
        boolean thuocDoiNgu = doiNguGvRepo.existsByIdMaHocPhanAndIdMaGV(
                id.getMaHocPhan(), maGV);
        if (!thuocDoiNgu) {
            log.warn("[PhanCongGV] GV {} KHONG thuoc doi ngu giang vien cua HP {}. "
                    + "Lop ({},{},{},{}) van duoc phan cong nhung can review.",
                    maGV, id.getMaHocPhan(), id.getMaCTDT(), id.getMaHocPhan(),
                    id.getMaHocKy(), id.getMaLopHocPhan());
        }

        lhp.setGiangVien(gv);
        LopHocPhan saved = lopHocPhanRepo.save(lhp);

        // Gui email thong bao GV
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

    /** Kiem tra GV co thuoc doi ngu giang vien cua HP cua LopHocPhan khong. */
    @Override
    @Transactional(readOnly = true)
    public boolean gvThuocDoiNguHocPhan(String maHocPhan, String maGV) {
        return doiNguGvRepo.existsByIdMaHocPhanAndIdMaGV(maHocPhan, maGV);
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
        return lopHocPhanRepo.findByCtdtAndHocKyFetch(maCTDT, maHocKy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhan> findChuaPhanCongGV() {
        return lopHocPhanRepo.findChuaPhanCongGiangVien();
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

    private LopHocPhan findById(LopHocPhanId id) {
        return lopHocPhanRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LopHocPhan", "id", id.toString()));
    }
}
