package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CtdtHocPhanDTO {

    @NotBlank(message = "Ma hoc phan khong duoc de trong")
    private String maHocPhan;

    @NotNull(message = "Hoc ky thu khong duoc de trong")
    @Min(value = 1, message = "Hoc ky thu toi thieu la 1")
    @Max(value = 10, message = "Hoc ky thu toi da la 10")
    private Integer hocKyThu;

    @NotNull(message = "So lop du kien khong duoc de trong")
    @Min(value = 1, message = "So lop du kien toi thieu la 1")
    private Integer soLopDuKien = 1;

    private Boolean batBuoc = true;

    private String ghiChu;
}
