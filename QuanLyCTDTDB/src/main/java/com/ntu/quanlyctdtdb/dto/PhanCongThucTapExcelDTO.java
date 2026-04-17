package com.ntu.quanlyctdtdb.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO trung gian chi dung khi doc Excel PhanCongThucTap.
 * MaDotTT se duoc set boi Service sau khi doc file, khong co trong Excel.
 */
@Getter
@Setter
@NoArgsConstructor
public class PhanCongThucTapExcelDTO {
    private String maSV;
    private String maDoanhNghiep;
    private String maGiangVienGiamSat;
    // MaDotTT duoc inject boi ThucTapService.importPhanCong()
    private Integer maDotTT;
}
