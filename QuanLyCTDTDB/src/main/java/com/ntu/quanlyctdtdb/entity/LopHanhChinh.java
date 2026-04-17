package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Bang: LopHanhChinh
 * PK : MaLopHC (VARCHAR 10) - VD: CNTT2021A
 */
@Entity
@Table(name = "LopHanhChinh")
@Getter
@Setter
@NoArgsConstructor
public class LopHanhChinh extends BaseAuditEntity {

    @Id
    @Column(name = "MaLopHC", length = 10, nullable = false)
    private String maLopHC;

    @Column(name = "TenLopHC", length = 50, nullable = false)
    private String tenLopHC;

    @Column(name = "Khoa", length = 20)
    private String khoa;

    @Column(name = "NamNhapHoc")
    private Integer namNhapHoc;

    /**
     * FK -> NguoiDung.MaNguoiDung (role CVHT)
     * Dung String de tranh circular reference voi NguoiDung
     */
    @Column(name = "MaCoVan", length = 10)
    private String maCoVan;

    // ---- Relations (mappedBy) ----
    @OneToMany(mappedBy = "lopHanhChinh", fetch = FetchType.LAZY)
    private List<NguoiDung> sinhViens = new ArrayList<>();

    @OneToMany(mappedBy = "lopHanhChinh", fetch = FetchType.LAZY)
    private List<DotKienTap> dotKienTaps = new ArrayList<>();
}
