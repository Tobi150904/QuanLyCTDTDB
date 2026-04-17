package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiLopHP;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LopHocPhanDTO {

    @NotNull(message = "Vui lòng chọn học phần")
    private String maHocPhan;

    @NotNull(message = "Vui lòng chọn học kỳ")
    private String maHocKy;

    @NotNull(message = "Vui lòng chọn lớp hành chính")
    private String maLopHC;

    private Integer nhomHocPhan;

    private String maGiangVien;

    @NotNull(message = "Sĩ số tối đa không được để trống")
    @Min(value = 1)
    private Integer siSoToiDa = 50;

    private Integer siSoThucTe = 0;
    private TrangThaiLopHP trangThai = TrangThaiLopHP.DangMo;
}