package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class ChuongTrinhDaoTaoDTO {

    @NotBlank @Size(max = 20)
    private String maCTDT;

    @NotBlank @Size(max = 200)
    private String tenCTDT;

    @Size(max = 20)
    private String khoa;

    private String fileWord;
    private TrangThaiCTDT trangThai = TrangThaiCTDT.BanNhap;
    private String maNguoiTao;
    private String maNguoiDuyet;
    private String lyDoTuChoi;

    private List<CTDT_HocPhanDTO> chiTietHocPhans = new ArrayList<>();

    @Getter @Setter
    @NoArgsConstructor
    public static class CTDT_HocPhanDTO {
        private String maHocPhan;
        private Integer hocKyThu;
        private Integer soLopDuKien = 1;
        private Boolean batBuoc = true;
        private String ghiChu;
    }
}