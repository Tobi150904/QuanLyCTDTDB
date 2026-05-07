package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DotKienTapDTO {

    @NotBlank(message = "Ten dot kien tap khong duoc de trong")
    @Size(max = 200)
    private String tenDotKT;

    @NotBlank(message = "Lop hanh chinh khong duoc de trong")
    private String maLopHC;

    @NotBlank(message = "Hoc ky khong duoc de trong")
    private String maHocKy;

    private LocalDate thoiGian;

    private String maGVPhuTrach;

    @NotBlank(message = "Doanh nghiep khong duoc de trong")
    private String maDoanhNghiep;

    private BigDecimal kinhPhiChung;
    private BigDecimal kinhPhiTungSV;

    // FileMinhChung xu ly rieng trong controller
}
