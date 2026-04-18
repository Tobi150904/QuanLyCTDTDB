package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BCN_ThanhVien")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BcnThanhVien {

    @EmbeddedId
    private BcnThanhVienId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maCTDT")
    @JoinColumn(name = "MaCTDT")
    private ChuongTrinhDaoTao chuongTrinhDaoTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maGV")
    @JoinColumn(name = "MaGV")
    private GiangVien giangVien;

    @Column(name = "NgayBoNhiem")
    private LocalDate ngayBoNhiem;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
