package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO them/xoa GV vao doi ngu cua mot HocPhan ({@code DoiNguGiangVienHP}).
 * <p>{@code maHocPhan} duoc set boi controller tu path variable de tranh
 * tampering; user chi chon {@code maGV} tu dropdown va tuy chon
 * {@code trangThai}. Ngay them duoc DB tu sinh o cot {@code created_at}.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DoiNguGvDTO {

    /** Set boi controller tu @PathVariable — khong bind tu form input. */
    private String maHocPhan;

    @NotBlank(message = "Vui long chon giang vien")
    private String maGV;

    /** true = dang day, false = tam ngung. */
    private Boolean trangThai = true;
}
