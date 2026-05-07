package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho action "GV nhap nhan xet 1 SV trong 1 lop hoc phan" — Phase 4.
 *
 * <p>Composite key cua {@code DanhSachSvLopHocPhan} gom 5 cot, can binding
 * day du tu form. Service nay khong tao moi ban ghi (record da co tu khi
 * SV dang ky vao lop), chi update {@code nhanXet} + {@code daCanhBao}.</p>
 *
 * <p>Khi {@code daCanhBao = true} va truoc do = false, service goi
 * {@code EmailService.guiCanhBaoSinhVien(...)} -> CVHT cua lop hanh chinh.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhapNhanXetDTO {

    @NotBlank
    private String maCTDT;

    @NotBlank
    private String maHocPhan;

    @NotBlank
    private String maHocKy;

    @NotNull
    private Integer maLop;

    @NotBlank
    private String maSV;

    /** Cho phep xoa nhan xet: trong = clear nhan xet truoc do. */
    private String nhanXet;

    /** Default false neu user khong tick box. */
    private Boolean daCanhBao = Boolean.FALSE;
}
