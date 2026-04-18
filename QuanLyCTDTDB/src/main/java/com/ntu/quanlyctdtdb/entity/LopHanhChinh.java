package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "LopHanhChinh")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LopHanhChinh {

    @Id
    @Column(name = "MaLopHC", length = 20)
    private String maLopHC;

    @Column(name = "TenLop", nullable = false, length = 100)
    private String tenLop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCTDT")
    private ChuongTrinhDaoTao chuongTrinhDaoTao;

    @Column(name = "KhoaHoc", length = 20)
    private String khoaHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCoVan")
    private GiangVien coVan;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
