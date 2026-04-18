package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.LoaiGiangVien;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "GiangVien")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GiangVien {

    @Id
    @Column(name = "MaGV", length = 20)
    private String maGV;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDung", nullable = false, unique = true)
    private NguoiDung nguoiDung;

    @Column(name = "HocHam", length = 50)
    private String hocHam;

    @Column(name = "HocVi", length = 50)
    private String hocVi;

    @Column(name = "ChuyenNganh", length = 200)
    private String chuyenNganh;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiGiangVien", length = 20)
    private LoaiGiangVien loaiGiangVien = LoaiGiangVien.GiangVienTruong;

    // Getter tien ich
    public String getHoTen() {
        return nguoiDung != null ? nguoiDung.getHoTen() : maGV;
    }

    public String getEmail() {
        return nguoiDung != null ? nguoiDung.getEmail() : null;
    }
}
