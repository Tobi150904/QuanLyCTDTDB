package com.ntu.quanlyctdtdb.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LopHanhChinhDTO {

    /**
     * Ma lop hanh chinh: VD <code>62KMT</code>, <code>62CNTT-A</code>.
     * Khoa hoc 2 chu so (62) + ma chuyen nganh + suffix lop neu nhieu lop cung khoa.
     */
    @NotBlank(message = "Ma lop hanh chinh khong duoc bo trong")
    @Size(max = 30)
    @Pattern(regexp = "^[A-Z0-9-]+$",
             message = "Ma chi gom chu hoa, so va dau '-' (vi du 62KMT, 62CNTT-A)")
    private String maLopHC;

    @NotBlank(message = "Ten lop khong duoc bo trong")
    @Size(max = 100)
    private String tenLop;

    /** Khoa hoc dang chuoi: 62, 63, ... (de mo rong). */
    @NotBlank(message = "Khoa hoc khong duoc bo trong")
    @Size(max = 10)
    private String khoaHoc;

    @NotNull(message = "Phai chon Chuong trinh dao tao")
    private String maCTDT;

    /** Co van hoc tap (GiangVien) - khong bat buoc khi moi tao. */
    private String maCoVan;
}
