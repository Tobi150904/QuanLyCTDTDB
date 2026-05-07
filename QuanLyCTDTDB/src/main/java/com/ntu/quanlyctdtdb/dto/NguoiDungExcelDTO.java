package com.ntu.quanlyctdtdb.dto;

import lombok.Data;

@Data
public class NguoiDungExcelDTO {
    private int rowNum;
    private String tenDangNhap;
    private String matKhau;
    private String email;
    private String hoTen;
    private String soDienThoai;
    private String loaiNguoiDung; // String, parse sang enum trong service
    private String vaiTro;        // Comma-separated: "PDT,CNHP"
    private String maLopHC;
}
