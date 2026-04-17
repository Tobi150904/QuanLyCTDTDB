package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiLopHP;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO cho LopHocPhan.
 * MaGiangVien co the null (chua gan GV).
 * MaLopHP duoc auto-generate: [MaHocPhan]-[MaHocKy]-[SoThuTu2Digit]
 *   VD: POL307-2024.1-01
 */
@Getter
@Setter
@NoArgsConstructor
public class LopHocPhanDTO {

    // Read-only sau khi tao, khong cho sua
    private String maLopHP;

    @NotBlank(message = "Vui long chon hoc phan")
    private String maHocPhan;

    @NotBlank(message = "Vui long chon hoc ky")
    private String maHocKy;

    /**
     * MaGiangVien: nullable - chua gan GV khi moi tao (auto-create tu CTDT).
     * Validate o tang Service: neu co gia tri, GV phai thuoc DoiNgu cua HP.
     */
    private String maGiangVien;

    private Integer nhomLop;

    @NotNull(message = "Si so toi da khong duoc de trong")
    @Min(value = 1)
    @Max(value = 200)
    private Integer siSoToiDa = 50;

    private Integer siSoThucTe = 0;

    private TrangThaiLopHP trangThai = TrangThaiLopHP.DangMo;

    // Gan GV: MaNguoiDung cua GV can gan
    private String maGVGan;
}
