package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;

/**
 * Bang: DotThucTap
 * PK : MaDotTT (INT, auto increment)
 * Workflow: ChuanBi -> ChoDuyet -> DaDuyet -> DangThucHien -> DaKetThuc
 */
@Entity
@Table(name = "DotThucTap")
@Getter
@Setter
@NoArgsConstructor
public class DotThucTap extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDotTT")
    private Integer maDotTT;

    @Column(name = "TenDot", length = 100, nullable = false)
    private String tenDot;

    // FK: HocKy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocKy", referencedColumnName = "MaHocKy", nullable = false)
    private HocKyNamHoc hocKy;

    @Column(name = "NgayBatDau")
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiDotTT trangThai = TrangThaiDotTT.ChuanBi;

    @Column(name = "MoTa", columnDefinition = "TEXT")
    private String moTa;

    // FK: NguoiTao -> NguoiDung (PDT/TTDTXS)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiTao", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung nguoiTao;

    // FK: NguoiPheduyet -> NguoiDung
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiPheduyet", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiPheduyet;

    @Column(name = "NgayPheduyet")
    private LocalDateTime ngayPheduyet;

    // ---- Relations (mappedBy) ----
    @OneToMany(mappedBy = "dotThucTap", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PhanCongThucTap> phanCongs = new ArrayList<>();
}
