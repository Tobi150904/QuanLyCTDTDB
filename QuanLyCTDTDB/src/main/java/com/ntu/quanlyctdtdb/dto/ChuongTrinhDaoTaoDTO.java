package com.ntu.quanlyctdtdb.dto;

import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO cho ChuongTrinhDaoTao.
 *
 * QUAN TRONG - autoCreateLopHocPhan (Rule 5, docs/04_QUICK_LOOKUP.md):
 *   Khi TTDTXS/PDT phe duyet CTDT (trangThai -> DaDuyet),
 *   Service phai goi autoCreateLopHocPhan() trong cung @Transactional.
 *   Ket qua: moi HocPhan trong CTDT se co 1 LopHocPhan moi voi MaGiangVien = null.
 *
 * FileWord: duong dan sau khi upload, khong phai MultipartFile.
 */
@Getter
@Setter
@NoArgsConstructor
public class ChuongTrinhDaoTaoDTO {

    @NotBlank(message = "Ma CTDT khong duoc de trong")
    @Size(max = 20, message = "Ma CTDT toi da 20 ky tu (VD: CNTT-CLC-K64)")
    private String maCTDT;

    @NotBlank(message = "Ten CTDT khong duoc de trong")
    @Size(max = 200)
    private String tenCTDT;

    @Size(max = 20, message = "Khoa hoc toi da 20 ky tu (VD: K64)")
    private String khoa;

    // Duong dan file Word sau upload (set boi Service)
    private String fileWord;

    private TrangThaiCTDT trangThai = TrangThaiCTDT.BanNhap;

    // NguoiTao: lay tu @AuthenticationPrincipal, khong de nguoi dung nhap
    private String maNguoiTao;

    // NguoiDuyet: TTDTXS hoac PDT
    private String maNguoiDuyet;

    // Ly do tu choi khi trangThai -> TuChoi (tranh trung voi DaHuy)
    @Size(max = 1000)
    private String lyDoTuChoi;
}
