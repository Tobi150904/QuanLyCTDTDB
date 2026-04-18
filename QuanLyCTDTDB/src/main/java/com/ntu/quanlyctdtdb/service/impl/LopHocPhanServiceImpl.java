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
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;
    private final DanhSachSvLopHocPhanRepository dsSvRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final EmailService emailService;

    @Override
    public void taoLopHocPhanChoCTDT(String maCTDT, String maHocKy) {
        HocKyNamHoc hocKy = hocKyRepo.findById(maHocKy)
                .orElseThrow(() -> new ResourceNotFoundException("HocKyNamHoc", "MaHocKy", maHocKy));

        List<CtdtHocPhan> dsHocPhan = ctdtHocPhanRepo.findById_MaCTDT(maCTDT);
        for (CtdtHocPhan ctdtHP : dsHocPhan) {
            int soLop = ctdtHP.getSoLopDuKien() != null ? ctdtHP.getSoLopDuKien() : 1;
            for (int i = 1; i <= soLop; i++) {
                LopHocPhanId id = new LopHocPhanId(maCTDT, ctdtHP.getId().getMaHocPhan(), maHocKy, i);
                if (!lopHocPhanRepo.existsById(id)) {
                    LopHocPhan lhp = LopHocPhan.builder()
                            .id(id)
                            .ctdtHocPhan(ctdtHP)
                            .trangThai(TrangThaiLopHocPhan.DangMo)
                            .siSoToiDa(ctdtHP.getSoLopDuKien() != null ? 50 : 50)
                            .siSoThucTe(0)
                            .build();
                    lopHocPhanRepo.save(lhp);
                }
            }
        }
        log.info("[LopHocPhan] Tao lop cho CTDT={} HocKy={}: {} hoc phan", maCTDT, maHocKy, dsHocPhan.size());
    }

    @Override
    public LopHocPhan phanCongGiangVien(LopHocPhanId id, String maGV) {
        LopHocPhan lhp = findById(id);
        GiangVien gv = giangVienRepo.findById(maGV)
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", maGV));
        lhp.setGiangVien(gv);
        LopHocPhan saved = lopHocPhanRepo.save(lhp);

        // Gui email thong bao GV
        try {
            String tenHP = lhp.getCtdtHocPhan().getHocPhan().getTenHocPhan();
            String tenHocKy = lhp.getHocKy() != null ? lhp.getHocKy().getTenHocKy() : "";
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
        return lopHocPhanRepo.findById_MaCTDTAndId_MaHocKy(maCTDT, maHocKy);
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
                .id(dsId).sinhVien(sv).lopHocPhan(lhp).daCanhBao(false).build();
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

        // Gui email CVHT
        try {
            String hoTenSV = ds.getSinhVien().getNguoiDung().getHoTen();
            String tenHP = ds.getLopHocPhan().getCtdtHocPhan().getHocPhan().getTenHocPhan();
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

    // Helper
    private LopHocPhan findById(LopHocPhanId id) {
        return lopHocPhanRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("LopHocPhan", "id", id.toString()));
    }
}
