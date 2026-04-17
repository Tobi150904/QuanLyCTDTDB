package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.ntu.quanlyctdtdb.enums.LoaiTaiLieu;
import com.ntu.quanlyctdtdb.enums.TrangThaiTaiLieu;

/**
 * DTO cho TaiLieuMonHoc.
 *
 * QUAN TRONG - Rule 6 (doc docs/04_QUICK_LOOKUP.md):
 *   nopTaiLieu() phai la UPDATE neu da co ban (MaLopHP, Loai) do UNIQUE constraint.
 *   Service se kiem tra findByLopHocPhan_MaLopHPAndLoai() truoc khi quyet dinh INSERT hay UPDATE.
 *
 * FileDinhKem: duong dan sau khi upload, khong phai MultipartFile.
 */
@Getter
@Setter
@NoArgsConstructor
public class TaiLieuMonHocDTO {

    private Integer maTaiLieu; // null khi tao moi

    @NotBlank(message = "Vui long chon lop hoc phan")
    private String maLopHP;

    @NotNull(message = "Vui long chon loai tai lieu")
    private LoaiTaiLieu loai;

    // Duong dan file sau upload (set boi Service)
    private String fileDinhKem;

    private LocalDateTime ngayNop;

    private TrangThaiTaiLieu trangThai = TrangThaiTaiLieu.ChoDuyet;

    // Nguoi duyet: MaNguoiDung (CNHP)
    private String maNguoiDuyet;

    private LocalDateTime ngayDuyet;

    // Nhan xet khi CNHP tu choi
    @Size(max = 1000, message = "Nhan xet toi da 1000 ky tu")
    private String nhanXet;

    // Flag: true = file duoc nop qua han (> 2 tuan dau ky)
    private boolean quaHan = false;
}
