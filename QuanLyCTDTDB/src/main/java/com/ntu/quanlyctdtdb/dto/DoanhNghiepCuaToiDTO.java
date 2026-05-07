package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Bug-fix A2 (P1) — DTO cho route /doanh-nghiep/cua-toi.
 *
 * <p>DN tu cap nhat thong tin lien he cua chinh DN minh. Chi gom 4 field:
 * Email, SoDienThoai, NguoiDaiDien, DiaChiDN. KHONG cho phep DN tu doi
 * MaDoanhNghiep, TenDoanhNghiep, LinhVuc (de tranh gian lan ten/linh vuc),
 * va TrangThai (chi TTDTXS/ADMIN moi duoc dong/mo hop tac).</p>
 *
 * <p>Khong reuse {@link DoanhNghiepDTO} de tranh DN gui kem hidden field
 * tenDoanhNghiep/trangThai gian lan thay doi.</p>
 */
@Data
public class DoanhNghiepCuaToiDTO {

    @Size(max = 100, message = "Nguoi dai dien toi da 100 ky tu")
    private String nguoiDaiDien;

    @Email(message = "Email khong dung dinh dang")
    @Size(max = 100, message = "Email toi da 100 ky tu")
    private String email;

    @Pattern(regexp = "^$|^[0-9+-s]{8,15}$",
             message = "So dien thoai khong hop le (8-15 ky tu so, dau cach, + va -)")
    private String soDienThoai;

    @Size(max = 255, message = "Dia chi toi da 255 ky tu")
    private String diaChiDN;
}
