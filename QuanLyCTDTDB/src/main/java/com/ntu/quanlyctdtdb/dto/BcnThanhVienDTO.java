package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.ChucDanhBCN;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BcnThanhVienDTO {

    @NotBlank(message = "Ma GV khong duoc de trong")
    private String maGV;

    @NotNull(message = "Chuc danh khong duoc de trong")
    private ChucDanhBCN chucDanh;

    private LocalDate ngayBoNhiem;

    private String ghiChu;
}
