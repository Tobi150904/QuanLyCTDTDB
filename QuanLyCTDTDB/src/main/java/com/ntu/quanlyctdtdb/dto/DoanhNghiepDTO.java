package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DoanhNghiepDTO {

    /** Null khi tao moi (service se kiem tra PK neu nguoi dung tu nhap). */
    @Size(max = 20, message = "Ma doanh nghiep toi da 20 ky tu")
    private String maDoanhNghiep;

    @NotBlank(message = "Ten doanh nghiep khong duoc de trong")
    @Size(max = 200, message = "Ten doanh nghiep toi da 200 ky tu")
    private String tenDoanhNghiep;

    @Size(max = 200, message = "Linh vuc toi da 200 ky tu")
    private String linhVuc;

    @Size(max = 100, message = "Nguoi dai dien toi da 100 ky tu")
    private String nguoiDaiDien;

    @Email(message = "Email khong dung dinh dang")
    @Size(max = 100, message = "Email toi da 100 ky tu")
    private String email;

    @Pattern(regexp = "^$|^[0-9+\\-\\s]{8,15}$",
             message = "So dien thoai khong hop le (8-15 ky tu so, dau cach, + va -)")
    private String soDienThoai;

    @Size(max = 255, message = "Dia chi toi da 255 ky tu")
    private String diaChiDN;

    /** Mac dinh DangHopTac khi tao moi. */
    private TrangThaiDoanhNghiep trangThai;
}
