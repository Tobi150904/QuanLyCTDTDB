package com.ntu.quanlyctdtdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ntu.quanlyctdtdb.dto.DoanhNghiepDTO;
import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;

import java.util.List;

/**
 * Contract cho DoanhNghiepService.
 * Impl: DoanhNghiepServiceImpl.java
 */
public interface DoanhNghiepService {

    Page<DoanhNghiep> findAll(String keyword, TrangThaiDoanhNghiep trangThai, Pageable pageable);

    DoanhNghiep findById(String maDoanhNghiep);

    /**
     * Tao moi doanh nghiep.
     * Side effect: Tu dong tao NguoiDung voi VaiTro=DN:
     *   - MaNguoiDung = MaDoanhNghiep
     *   - TenDangNhap = MaDoanhNghiep
     *   - MatKhau     = BCrypt(MaDoanhNghiep)
     *   - Email       = emailDN cua doanh nghiep
     * Ca hai (DoanhNghiep + NguoiDung) duoc luu trong cung @Transactional.
     */
    DoanhNghiep create(DoanhNghiepDTO dto);

    DoanhNghiep update(String maDoanhNghiep, DoanhNghiepDTO dto);

    /**
     * Chuyen doi trang thai hop tac (DangHopTac <-> TamNgung).
     * Khi TamNgung: khoa luon tai khoan NguoiDung tuong ung (TrangThaiTK=0).
     * Khi DangHopTac: mo khoa lai tai khoan NguoiDung (TrangThaiTK=1).
     */
    void doiTrangThai(String maDoanhNghiep, TrangThaiDoanhNghiep trangThai);

    List<DoanhNghiep> findAllDangHopTac();
}
