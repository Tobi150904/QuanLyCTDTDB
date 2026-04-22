package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO them/xoa GV vao doi ngu cua mot HocPhan ({@code DoiNguGiangVienHP}).
 * MaHocPhan lay tu path variable; DTO chi can MaGV (user chon tu dropdown)
 * va trangThai mac dinh = true (active).
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DoiNguGvDTO {

    @NotBlank(message = "Vui long chon giang vien")
    private String maGV;

    /** true = active (co the nhan phan cong lop), false = tam ngung. */
    private Boolean trangThai = true;
}
