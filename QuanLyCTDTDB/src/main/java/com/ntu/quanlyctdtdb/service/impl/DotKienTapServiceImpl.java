package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.DotKienTapDTO;
import com.ntu.quanlyctdtdb.entity.*;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.service.DotKienTapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Override
    @Transactional(readOnly = true)
    public List<DotKienTap> findAll() {
        return dotKTRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public DotKienTap findById(Integer id) {
        return dotKTRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DotKienTap", "MaDotKT", id.toString()));
    }

    @Override
    public DotKienTap create(DotKienTapDTO dto, String maNguoiDungTao) {
        LopHanhChinh lhc = lopHCRepo.findById(dto.getMaLopHC())
                .orElseThrow(() -> new ResourceNotFoundException("LopHanhChinh", "MaLopHC", dto.getMaLopHC()));
        HocKyNamHoc hocKy = hocKyRepo.findById(dto.getMaHocKy())
                .orElseThrow(() -> new ResourceNotFoundException("HocKyNamHoc", "MaHocKy", dto.getMaHocKy()));
        DoanhNghiep dn = doanhNghiepRepo.findById(dto.getMaDoanhNghiep())
                .orElseThrow(() -> new ResourceNotFoundException("DoanhNghiep", "MaDN", dto.getMaDoanhNghiep()));

        GiangVien gvPhuTrach = null;
        if (dto.getMaGVPhuTrach() != null && !dto.getMaGVPhuTrach().isBlank()) {
            gvPhuTrach = giangVienRepo.findById(dto.getMaGVPhuTrach())
                    .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", dto.getMaGVPhuTrach()));
        }

        DotKienTap dot = DotKienTap.builder()
                .tenDotKT(dto.getTenDotKT())
                .lopHanhChinh(lhc)
                .hocKyNamHoc(hocKy)
                .thoiGian(dto.getThoiGian())
                .giangVienPhuTrach(gvPhuTrach)
                .doanhNghiep(dn)
                .kinhPhiChung(dto.getKinhPhiChung())
                .kinhPhiTungSV(dto.getKinhPhiTungSV())
                .trangThai(TrangThaiDotKT.ChuanBi)
                .build();
        return dotKTRepo.save(dot);
    }

    @Override
    public DotKienTap update(Integer id, DotKienTapDTO dto) {
        DotKienTap dot = findById(id);
        if (dot.getTrangThai() == TrangThaiDotKT.DaDuyet) {
            throw new BusinessException("Khong the sua dot kien tap da duoc phe duyet");
        }
        dot.setTenDotKT(dto.getTenDotKT());
        dot.setThoiGian(dto.getThoiGian());
        dot.setKinhPhiChung(dto.getKinhPhiChung());
        dot.setKinhPhiTungSV(dto.getKinhPhiTungSV());

        if (dto.getMaGVPhuTrach() != null && !dto.getMaGVPhuTrach().isBlank()) {
            GiangVien gv = giangVienRepo.findById(dto.getMaGVPhuTrach()).orElse(null);
            dot.setGiangVienPhuTrach(gv);
        }
        return dotKTRepo.save(dot);
    }

    @Override
    public DotKienTap guiPheDuyet(Integer id) {
        DotKienTap dot = findById(id);
        if (dot.getTrangThai() != TrangThaiDotKT.ChuanBi) {
            throw new BusinessException("Chi co the gui phe duyet dot o trang thai ChuanBi");
        }
        dot.setTrangThai(TrangThaiDotKT.ChoDuyet);
        return dotKTRepo.save(dot);
    }

    @Override
    public DotKienTap pheduyet(Integer id, String maNguoiDung) {
        DotKienTap dot = findById(id);
        if (dot.getTrangThai() != TrangThaiDotKT.ChoDuyet) {
            throw new BusinessException("Chi co the phe duyet dot o trang thai ChoDuyet");
        }
        dot.setTrangThai(TrangThaiDotKT.DaDuyet);
        return dotKTRepo.save(dot);
    }

    @Override
    public Map<String, Object> importSinhVienTuExcel(Integer maDotKT, List<String> dsMaSV) {
        DotKienTap dot = findById(maDotKT);
        int success = 0;
        List<String> errors = new ArrayList<>();

        for (String maSV : dsMaSV) {
            try {
                if (maSV == null || maSV.isBlank()) { continue; }
                SinhVien sv = sinhVienRepo.findById(maSV.trim()).orElse(null);
                if (sv == null) {
                    errors.add("MaSV '" + maSV + "' khong ton tai");
                    continue;
                }
                DanhSachSvKienTapId dsId = new DanhSachSvKienTapId(maDotKT, maSV.trim());
                if (dsSvKTRepo.existsById(dsId)) {
                    errors.add("MaSV '" + maSV + "' da co trong dot kien tap");
                    continue;
                }
                DanhSachSvKienTap ds = DanhSachSvKienTap.builder()
                        .id(dsId).dotKienTap(dot).sinhVien(sv).daThamGia(true).build();
                dsSvKTRepo.save(ds);
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
    public List<DanhSachSvKienTap> findDanhSachSVKienTap(Integer maDotKT) {
        return dsSvKTRepo.findById_MaDotKT(maDotKT);
    }
}
