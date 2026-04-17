package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.LoaiTaiLieu;
import com.ntu.quanlyctdtdb.enums.TrangThaiTaiLieu;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
public class TaiLieuMonHocDTO {

    private Integer maTaiLieu;

    @NotNull(message = "Vui lòng chọn học phần")
    private String maHocPhan;

    @NotNull(message = "Vui lòng chọn học kỳ")
    private String maHocKy;

    @NotNull(message = "Vui lòng chọn lớp hành chính")
    private String maLopHC;

    @NotNull(message = "Vui lòng chọn loại tài liệu")
    private LoaiTaiLieu loai;

    private String fileDinhKem;
    private LocalDateTime ngayNop;
    private TrangThaiTaiLieu trangThai = TrangThaiTaiLieu.ChoDuyet;
    private String maNguoiDuyet;
    private LocalDateTime ngayDuyet;
    private String nhanXet;
    private boolean quaHan = false;
}