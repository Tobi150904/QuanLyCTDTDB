package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO binding cho form tao/sua HocKyNamHoc.
 *
 * Cac truong nghiep vu user nhap: {@link #hocKyThu}, {@link #namBatDau},
 * {@link #namKetThuc}. Service se tu dong suy ra {@link #maHocKy}
 * (format {@code HKn-YYYY}, vd {@code HK1-2024}) va {@link #tenHocKy}
 * theo quy uoc trong {@code docs/02_Data § 1}.
 *
 * <p>Enum {@link TrangThaiHocKy} chi co 3 gia tri hop le:
 * {@code SapDienRa}, {@code DangDienRa}, {@code DaKetThuc}.
 */
@Data
public class HocKyNamHocDTO {

    /**
     * PK, format {@code HKn-YYYY}. Khi tao moi: de trong, service sinh.
     * Khi sua: gui lai gia tri goc de tranh doi khoa chinh.
     */
    @Size(max = 20)
    @Pattern(regexp = "^HK[1-3]-\\d{4}$",
             message = "Ma hoc ky phai theo dinh dang HK1-YYYY, HK2-YYYY hoac HK3-YYYY")
    private String maHocKy;

    /** Ten hien thi — tu sinh neu de trong. */
    @Size(max = 50)
    private String tenHocKy;

    /* ---------- Cac truong nguoi dung nhap o form ---------- */

    @NotNull(message = "Ky khong duoc de trong")
    @Min(value = 1, message = "Ky phai tu 1 den 3")
    @Max(value = 3, message = "Ky phai tu 1 den 3")
    private Integer hocKyThu;

    @NotNull(message = "Nam bat dau khong duoc de trong")
    @Min(value = 2020, message = "Nam bat dau phai tu 2020 tro di")
    @Max(value = 2099, message = "Nam bat dau khong qua 2099")
    private Integer namBatDau;

    @NotNull(message = "Nam ket thuc khong duoc de trong")
    @Min(value = 2020, message = "Nam ket thuc phai tu 2020 tro di")
    @Max(value = 2099, message = "Nam ket thuc khong qua 2099")
    private Integer namKetThuc;

    /* ---------- Thoi gian ---------- */

    @NotNull(message = "Ngay bat dau khong duoc de trong")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngay ket thuc khong duoc de trong")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayKetThuc;

    @NotNull(message = "Trang thai khong duoc de trong")
    private TrangThaiHocKy trangThai;
}
