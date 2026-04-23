package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class NguoiDungDTO {

    // Chi bat buoc khi tao moi; khi sua: bo trong de giu nguyen
    private String maNguoiDung; // null khi tao moi

    @NotBlank(message = "Ten dang nhap khong duoc de trong")
    @Size(min = 3, max = 50, message = "Ten dang nhap 3-50 ky tu")
    private String tenDangNhap;

    // Chi bat buoc khi tao moi. Khi cap nhat, de trong = giu nguyen mat khau cu.
    //
    // KHONG dung @Size(min=8) o day — @Size van validate chuoi RONG ("") va lam
    // form update fail voi message "Mat khau toi thieu 8 ky tu" du user da de
    // trong y nguyen vong. Thay vao do kiem tra length trong
    // NguoiDungServiceImpl.create() / update() khi matKhau != blank.
    private String matKhau;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong dung dinh dang")
    private String email;

    @NotBlank(message = "Ho ten khong duoc de trong")
    @Size(max = 100)
    private String hoTen;

    @Size(max = 15)
    private String soDienThoai;

    @NotNull(message = "Loai nguoi dung khong duoc de trong")
    private LoaiNguoiDung loaiNguoiDung;

    // ---- GiangVien fields (khi loaiNguoiDung = GiangVien) ----
    private String hocHam;
    private String hocVi;
    private String chuyenNganh;

    // ---- SinhVien fields (khi loaiNguoiDung = SinhVien) ----
    private String maLopHC;

    // ---- VaiTro nghiep vu (1 nguoi co the co nhieu vai tro) ----
    private List<VaiTro> vaiTros;
}
