package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HocPhanDTO {

    private String maHocPhan; // null khi tao moi

    @NotBlank(message = "Ten hoc phan khong duoc de trong")
    @Size(max = 200)
    private String tenHocPhan;

    @NotNull(message = "So tin chi khong duoc de trong")
    @Min(value = 1, message = "So tin chi toi thieu la 1")
    @Max(value = 15, message = "So tin chi toi da la 15")
    private Integer soTinChi;

    @NotNull(message = "Loai hoc phan khong duoc de trong")
    private LoaiHocPhan loaiHocPhan;

    @NotBlank(message = "Chu nhiem hoc phan khong duoc de trong")
    private String maChuNhiemHP;

    // File upload xu ly rieng trong controller
    private String fileDeCuong;

    // Ly do khi TTDTXS tu choi
    private String lyDoTuChoi;
}
