package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.LoaiNhanXet;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO cho DanhGiaVaCanhBao.
 *
 * QUAN TRONG - Side effect (doc workflow Giai Doan 5):
 *   Khi LoaiNhanXet = TieuCuc:
 *     - He thong tu dong tao canh bao
 *     - Gui email den SV va CVHT
 *   Logic nay nam trong DanhGiaVaCanhBaoServiceImpl.taoNhanXet(),
 *   KHONG dat trong Controller.
 */
@Getter
@Setter
@NoArgsConstructor
public class DanhGiaDTO {

    @NotBlank(message = "Vui long chon sinh vien")
    private String maSV;

    @NotBlank(message = "Vui long chon lop hoc phan")
    private String maLopHP;

    /**
     * NguoiNhanXet: lay tu @AuthenticationPrincipal trong Controller,
     * khong de nguoi dung tu nhap.
     */
    private String maNguoiNhanXet;

    @NotNull(message = "Vui long chon loai nhan xet")
    private LoaiNhanXet loaiNhanXet;

    @NotBlank(message = "Noi dung nhan xet khong duoc de trong")
    @Size(min = 10, max = 2000, message = "Noi dung nhan xet tu 10 den 2000 ky tu")
    private String noiDung;

    // CVHT cap nhat sau khi xu ly canh bao
    private Boolean daXuLy = false;

    @Size(max = 2000)
    private String ketQuaXuLy;
}
