package com.ntu.quanlyctdtdb.dto;

import lombok.Data;

@Data
public class ThucTapExcelDTO {
    private int rowNum;
    private String maSV;
    private String loaiThucTap; // "Truong" hoac "DoanhNghiep"
    private String maDoanhNghiep; // co the null neu loai = Truong
}
