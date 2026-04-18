package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "CTDT_HocPhan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CtdtHocPhan {

    @EmbeddedId
    private CtdtHocPhanId id;

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

    @Column(name = "BatBuoc", columnDefinition = "BIT DEFAULT 1")
    private Boolean batBuoc = true;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    @Column(name = "FileDeCuong", length = 255)
    private String fileDeCuong;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
