package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO binding cho form tao/sua HocKyNamHoc.
 * Quy uoc MaHocKy: {@code HK1-YYYY}, {@code HK2-YYYY}, {@code HK3-YYYY}
 * (YYYY la nam bat dau nam hoc).
 */
@Data
public class HocKyNamHocDTO {

    @NotBlank(message = "Ma hoc ky khong duoc de trong")
    @Size(max = 20)
    @Pattern(regexp = "^HK[1-3]-\\d{4}$",
             message = "Ma hoc ky phai theo dinh dang HK1-YYYY, HK2-YYYY hoac HK3-YYYY")
    private String maHocKy;

    @NotBlank(message = "Ten hoc ky khong duoc de trong")
    @Size(max = 50)
    private String tenHocKy;

    @NotNull(message = "Ngay bat dau khong duoc de trong")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngay ket thuc khong duoc de trong")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayKetThuc;

    @NotNull(message = "Trang thai khong duoc de trong")
    private TrangThaiHocKy trangThai;
}
