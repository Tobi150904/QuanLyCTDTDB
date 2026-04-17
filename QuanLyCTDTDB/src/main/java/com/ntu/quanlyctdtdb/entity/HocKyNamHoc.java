package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;

/**
 * Bang: HocKyNamHoc
 * PK : MaHocKy (VARCHAR 10) - VD: HK1-24-25
 * created_at / updated_at tu dong qua BaseAuditEntity.
 */
@Entity
@Table(name = "HocKyNamHoc")
@Getter
@Setter
@NoArgsConstructor
public class HocKyNamHoc extends BaseAuditEntity {

    @Id
    @Column(name = "MaHocKy", length = 10, nullable = false)
    private String maHocKy;

    @Column(name = "TenHocKy", length = 50, nullable = false)
    private String tenHocKy;

    @Column(name = "NamHoc", length = 9, nullable = false)
    private String namHoc;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiHocKy trangThai = TrangThaiHocKy.SapDienRa;

    // ---- Relations (mappedBy) ----
    @OneToMany(mappedBy = "hocKy", fetch = FetchType.LAZY)
    private List<LopHocPhan> lopHocPhans = new ArrayList<>();

    @OneToMany(mappedBy = "hocKy", fetch = FetchType.LAZY)
    private List<DotThucTap> dotThucTaps = new ArrayList<>();
}
