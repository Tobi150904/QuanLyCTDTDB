package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO cho DoanhNghiep.
 * Khi tao moi: he thong tu dong tao NguoiDung voi VaiTro=DN.
 * TenDangNhap DN = MaDoanhNghiep (VD: DN001), MatKhau mac dinh = MaDoanhNghiep.
 */
@Getter
@Setter
@NoArgsConstructor
public class DoanhNghiepDTO {

    @NotBlank(message = "Ma doanh nghiep khong duoc de trong")
    @Size(max = 20, message = "Ma doanh nghiep toi da 20 ky tu")
    private String maDoanhNghiep;

    @NotBlank(message = "Ten doanh nghiep khong duoc de trong")
    @Size(max = 200, message = "Ten doanh nghiep toi da 200 ky tu")
    private String tenDoanhNghiep;

    @Size(max = 200, message = "Linh vuc toi da 200 ky tu")
    private String linhVucHoatDong;

    @Size(max = 100, message = "Nguoi dai dien toi da 100 ky tu")
    private String nguoiDaiDien;

    @Email(message = "Email khong hop le")
    @Size(max = 100)
    private String emailDN;

    @Size(max = 15)
    private String soDienThoaiDN;

    @Size(max = 255)
    private String diaChiDN;

    private TrangThaiDoanhNghiep trangThai = TrangThaiDoanhNghiep.DangHopTac;
}
