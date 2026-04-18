package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.TrangThaiSinhVien;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SinhVien")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SinhVien {

    @Id
    @Column(name = "MaSV", length = 20)
    private String maSV;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDung", nullable = false, unique = true)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHC", nullable = false)
    private LopHanhChinh lopHanhChinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThaiSV", length = 20)
    private TrangThaiSinhVien trangThaiSV = TrangThaiSinhVien.DangHoc;

    // Getter tien ich
    public String getHoTen() {
        return nguoiDung != null ? nguoiDung.getHoTen() : maSV;
    }

    public String getEmail() {
        return nguoiDung != null ? nguoiDung.getEmail() : null;
    }
}
