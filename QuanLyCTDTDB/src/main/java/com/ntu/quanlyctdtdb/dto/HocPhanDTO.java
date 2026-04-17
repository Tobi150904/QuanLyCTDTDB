package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO cho HocPhan.
 * FileDeCuong: duong dan sau khi upload, khong phai MultipartFile (MultipartFile xu ly o Controller).
 */
@Getter
@Setter
@NoArgsConstructor
public class HocPhanDTO {

    @NotBlank(message = "Ma hoc phan khong duoc de trong")
    @Size(max = 20)
    private String maHocPhan;

    @NotBlank(message = "Ten hoc phan khong duoc de trong")
    @Size(max = 200)
    private String tenHocPhan;

    @NotNull(message = "So tin chi khong duoc de trong")
    @Min(value = 1, message = "So tin chi toi thieu la 1")
    @Max(value = 10, message = "So tin chi toi da la 10")
    private Integer soTinChi;

    /**
     * ChuNhiemHP: MaNguoiDung cua giang vien lam chu nhiem.
     * Validate: phai la GV co VaiTro CNHP.
     */
    @NotBlank(message = "Vui long chon chu nhiem hoc phan")
    private String maChuNhiemHP;

    // Duong dan file sau khi upload (set boi Service, khong phai nguoi dung nhap)
    private String fileDeCuong;

    private TrangThaiHocPhan trangThai = TrangThaiHocPhan.BanNhap;

    // Ly do tu choi / go y khi CNHP/BCN tu choi duyet
    private String lyDoTuChoi;
}
