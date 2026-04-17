package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiSinhVien;
import com.ntu.quanlyctdtdb.enums.VaiTro;

/**
 * DTO cho NguoiDung - dung cho form them moi / sua / import Excel.
 * Khong cho phep sua MatKhau qua form chinh, co DTO rieng cho doi mat khau.
 */
@Getter
@Setter
@NoArgsConstructor
public class NguoiDungDTO {

    /**
     * MaNguoiDung:
     *   - SV: SV + 6 so (VD: SV240001)
     *   - GV/CVHT/BCN/CNHP/PDT/TTDTXS: GV001, PDT001...
     *   - DN: DN001
     * Import Excel se tu sinh, form them moi PDT nhap tay.
     */
    @NotBlank(message = "Ma nguoi dung khong duoc de trong")
    @Size(max = 20, message = "Ma nguoi dung toi da 20 ky tu")
    private String maNguoiDung;

    @NotBlank(message = "Ho ten khong duoc de trong")
    @Size(max = 100, message = "Ho ten toi da 100 ky tu")
    private String hoTen;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong hop le")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "Ten dang nhap khong duoc de trong")
    @Size(min = 3, max = 50, message = "Ten dang nhap tu 3 den 50 ky tu")
    private String tenDangNhap;

    /**
     * MatKhau: Chi dung khi tao moi. Sua nguoi dung khong doi mat khau qua truong nay.
     * Khi import Excel: mac dinh = MaNguoiDung.
     */
    @Size(min = 6, message = "Mat khau toi thieu 6 ky tu")
    private String matKhau;

    @Size(max = 15)
    private String soDienThoai;

    @Size(max = 255)
    private String diaChi;

    @Size(max = 100)
    private String chuyenNganh;

    // Chi dung khi role = SV
    private TrangThaiSinhVien trangThaiSV;

    // Chi dung khi role = SV
    private String maLopHC;

    // Chi dung khi role = DN
    private String maDoanhNghiep;

    // Danh sach vai tro (mot nguoi co the co nhieu vai tro)
    @NotNull(message = "Vui long chon vai tro")
    private List<VaiTro> vaiTros;

    // Trang thai tai khoan: true = hoat dong, false = khoa
    private Boolean trangThaiTK = true;
}
