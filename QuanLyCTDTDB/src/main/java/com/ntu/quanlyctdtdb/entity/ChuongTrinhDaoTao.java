package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;

/**
 * Bang: ChuongTrinhDaoTao
 * PK : MaCTDT (VARCHAR 10) - VD: CTDT2024
 * created_at / updated_at tu dong qua BaseAuditEntity.
 */
@Entity
@Table(name = "ChuongTrinhDaoTao")
@Getter
@Setter
@NoArgsConstructor
public class ChuongTrinhDaoTao extends BaseAuditEntity {

    @Id
    @Column(name = "MaCTDT", length = 10, nullable = false)
    private String maCTDT;

    @Column(name = "TenCTDT", length = 200, nullable = false)
    private String tenCTDT;

    @Column(name = "NamApDung")
    private Integer namApDung;

    @Column(name = "MoTa", columnDefinition = "TEXT")
    private String moTa;

    /**
     * DuongDanFile: Duong dan file Word CTDT (luu tren disk)
     */
    @Column(name = "DuongDanFile", length = 500)
    private String duongDanFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiCTDT trangThai = TrangThaiCTDT.BanNhap;

    // FK: NguoiTao -> NguoiDung (BCN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiTao", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiTao;

    // FK: NguoiPheduyet -> NguoiDung (TTDTXS / PDT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiPheduyet", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiPheduyet;

    @Column(name = "NgayPheduyet")
    private LocalDateTime ngayPheduyet;

    @Column(name = "GhiChuTuChoi", columnDefinition = "TEXT")
    private String ghiChuTuChoi;

    // ---- Relations (mappedBy) ----
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "CTDT_HocPhan",
        joinColumns = @JoinColumn(name = "MaCTDT"),
        inverseJoinColumns = @JoinColumn(name = "MaHocPhan")
    )
    private List<HocPhan> hocPhans = new ArrayList<>();
}
