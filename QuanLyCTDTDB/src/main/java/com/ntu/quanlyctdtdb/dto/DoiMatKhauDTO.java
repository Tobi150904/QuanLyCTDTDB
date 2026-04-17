package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO rieng cho chuc nang doi mat khau.
 * Tach rieng de khong lo mat khau khi bind form CRUD nguoi dung.
 */
@Getter
@Setter
@NoArgsConstructor
public class DoiMatKhauDTO {

    @NotBlank(message = "Mat khau hien tai khong duoc de trong")
    private String matKhauHienTai;

    @NotBlank(message = "Mat khau moi khong duoc de trong")
    @Size(min = 6, max = 100, message = "Mat khau moi tu 6 den 100 ky tu")
    private String matKhauMoi;

    @NotBlank(message = "Xac nhan mat khau khong duoc de trong")
    private String xacNhanMatKhau;
}
