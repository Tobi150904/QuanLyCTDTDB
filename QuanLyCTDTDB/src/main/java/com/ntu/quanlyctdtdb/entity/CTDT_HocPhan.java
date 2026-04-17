package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CTDT_HocPhan")
@Getter @Setter
@NoArgsConstructor
public class CTDT_HocPhan extends BaseAuditEntity {

    @EmbeddedId
    private CTDT_HocPhanId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maCTDT")
    @JoinColumn(name = "MaCTDT")
    private ChuongTrinhDaoTao chuongTrinhDaoTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHocPhan")
    @JoinColumn(name = "MaHocPhan")
    private HocPhan hocPhan;

    @Column(name = "HocKyThu", nullable = false)
    private Integer hocKyThu;

    @Column(name = "SoLopDuKien")
    private Integer soLopDuKien = 1;

    @Column(name = "BatBuoc")
    private Boolean batBuoc = true;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;
}