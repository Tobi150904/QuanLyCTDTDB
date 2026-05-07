package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.ChucDanhBCN;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO tao/xoa thanh vien Ban chu nhiem (BCN) cho mot CTDT.
 * Composite PK cua entity BcnThanhVien gom 3 cot: MaCTDT + MaGV + ChucDanh
 * (xem {@link com.ntu.quanlyctdtdb.entity.BcnThanhVienId}) — tuc la 1 GV
 * co the cung luc la ChuNhiem va UyVien cua cung mot CTDT (hiem, nhung DDL
 * khong cam); service se reject truong hop do sau khi validate.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BcnThanhVienDTO {

    @NotBlank(message = "Vui long chon giang vien")
    private String maGV;

    @NotNull(message = "Vui long chon chuc danh")
    private ChucDanhBCN chucDanh;

    private LocalDate ngayBoNhiem;

    private String ghiChu;
}
