package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.DotThucTapDTO;
import com.ntu.quanlyctdtdb.entity.*;
import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiThucTap;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.service.DotThucTapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

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

    @Override
    @Transactional(readOnly = true)
    public List<DotThucTap> findAll() {
        return dotTTRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DotThucTap findById(Integer id) {
        return dotTTRepo.findById(id)
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
        dot.setTenDotTT(dto.getTenDotTT());
        dot.setNgayBatDau(dto.getNgayBatDau());
        dot.setNgayKetThuc(dto.getNgayKetThuc());
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
        DotThucTap dot = findById(maDotTT);
        int success = 0;
        List<String> errors = new ArrayList<>();

        for (String maSV : dsMaSV) {
            try {
                if (maSV == null || maSV.isBlank()) continue;
                SinhVien sv = sinhVienRepo.findById(maSV.trim()).orElse(null);
                if (sv == null) {
                    errors.add("MaSV '" + maSV + "' khong ton tai");
                    continue;
                }
                if (dsTTRepo.existsByDotThucTap_MaDotTTAndSinhVien_MaSV(maDotTT, maSV.trim())) {
                    errors.add("MaSV '" + maSV + "' da co trong dot thuc tap");
                    continue;
                }
                DanhSachThucTap ds = DanhSachThucTap.builder()
                        .dotThucTap(dot)
                        .sinhVien(sv)
                        .trangThai(TrangThaiThucTap.DaPhanCong)
                        .build();
                dsTTRepo.save(ds);
                success++;
            } catch (Exception e) {
                errors.add("MaSV '" + maSV + "': " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("errors", errors);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhSachThucTap> findDanhSachSV(Integer maDotTT) {
        return dsTTRepo.findByDotThucTap_MaDotTT(maDotTT);
    }

    @Override
    public DanhSachThucTap capNhatKetQua(Integer maDanhSach, String loaiThucTap,
                                          String maDoanhNghiep, String nhanXet) {
        DanhSachThucTap ds = dsTTRepo.findById(maDanhSach)
                .orElseThrow(() -> new ResourceNotFoundException("DanhSachThucTap", "MaThucTap", maDanhSach.toString()));
        ds.setTrangThai(TrangThaiThucTap.DangThucTap);

        if (maDoanhNghiep != null && !maDoanhNghiep.isBlank()) {
            DoanhNghiep dn = doanhNghiepRepo.findById(maDoanhNghiep).orElse(null);
            ds.setDoanhNghiep(dn);
        }
        return dsTTRepo.save(ds);
    }
}
