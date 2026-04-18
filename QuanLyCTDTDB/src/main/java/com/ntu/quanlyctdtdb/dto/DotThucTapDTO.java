package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class DotThucTapDTO {

    @NotBlank(message = "Ten dot thuc tap khong duoc de trong")
    @Size(max = 200)
    private String tenDotTT;

    @NotBlank(message = "CTDT khong duoc de trong")
    private String maCTDT;

    @NotBlank(message = "Hoc phan khong duoc de trong")
    private String maHocPhan;

    @NotBlank(message = "Hoc ky khong duoc de trong")
    private String maHocKy;

    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;

    // FileMinhChung xu ly rieng trong controller
}
