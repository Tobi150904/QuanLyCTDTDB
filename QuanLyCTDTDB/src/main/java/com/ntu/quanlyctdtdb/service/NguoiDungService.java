package com.ntu.quanlyctdtdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.ntu.quanlyctdtdb.dto.DoiMatKhauDTO;
import com.ntu.quanlyctdtdb.dto.NguoiDungDTO;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.VaiTro;

import java.util.List;

/**
 * Contract cho NguoiDungService.
 * Impl: NguoiDungServiceImpl.java
 */
public interface NguoiDungService {

    /**
     * Lay danh sach nguoi dung co filter va phan trang.
     * @param keyword tim kiem theo HoTen hoac TenDangNhap hoac Email
     * @param vaiTro  loc theo vai tro (null = tat ca)
     * @param pageable phan trang
     */
    Page<NguoiDung> findAll(String keyword, VaiTro vaiTro, Pageable pageable);

    /**
     * Tim nguoi dung theo ma.
     * Throw ResourceNotFoundException neu khong tim thay.
     */
    NguoiDung findById(String maNguoiDung);

    /**
     * Them moi nguoi dung.
     * - Hash MatKhau bang BCrypt truoc khi luu.
     * - Gan vai tro vao bang NguoiDung_VaiTro.
     * - Throw BusinessException neu MaNguoiDung hoac TenDangNhap hoac Email da ton tai.
     */
    NguoiDung create(NguoiDungDTO dto);

    /**
     * Cap nhat nguoi dung.
     * - Khong cap nhat MatKhau qua method nay (dung doiMatKhau()).
     * - Cap nhat lai VaiTro: xoa het vai tro cu, them lai vai tro moi.
     */
    NguoiDung update(String maNguoiDung, NguoiDungDTO dto);

    /**
     * Khoa / Mo khoa tai khoan.
     * @param trangThai true = mo khoa, false = khoa
     */
    void doiTrangThaiTK(String maNguoiDung, boolean trangThai);

    /**
     * Doi mat khau (nguoi dung tu doi hoac admin reset).
     * Neu isAdminReset=false: kiem tra matKhauHienTai truoc khi doi.
     */
    void doiMatKhau(String maNguoiDung, DoiMatKhauDTO dto, boolean isAdminReset);

    /**
     * Import danh sach nguoi dung tu file Excel.
     * Format Excel: MaNguoiDung | HoTen | Email | TenDangNhap | VaiTro | MaLopHC | MaDoanhNghiep
     * MatKhau mac dinh = MaNguoiDung.
     * @return so luong dong import thanh cong
     */
    int importFromExcel(MultipartFile file);

    /**
     * Lay danh sach giang vien (co VaiTro = GV).
     * Dung cho dropdown chon GV trong form.
     */
    List<NguoiDung> findAllGiangVien();

    /**
     * Lay danh sach sinh vien thuoc mot lop hanh chinh.
     */
    List<NguoiDung> findSVByLopHanhChinh(String maLopHC);
}
