package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.LoaiNhanXet;
import com.ntu.quanlyctdtdb.enums.LoaiDanhGia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class DanhGiaDTO {

    @NotBlank(message = "Vui lòng chọn sinh viên")
    private String maSV;

    private String maHocPhan;
    private String maHocKy;
    private String maLopHC;

    private String maNguoiNhanXet;

    @NotNull(message = "Vui lòng chọn loại nhận xét")
    private LoaiNhanXet loaiNhanXet;

    @NotNull(message = "Vui lòng chọn loại đánh giá")
    private LoaiDanhGia loaiDanhGia = LoaiDanhGia.QuaTrinh;

    @NotBlank(message = "Nội dung nhận xét không được để trống")
    @Size(min = 10, max = 2000)
    private String noiDung;

    private Boolean daXuLy = false;
    private String ketQuaXuLy;
}