package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "HocKyNamHoc")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HocKyNamHoc {

    @Id
    @Column(name = "MaHocKy", length = 20)
    private String maHocKy;

    @Column(name = "TenHocKy", nullable = false, length = 50)
    private String tenHocKy;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20)
    private TrangThaiHocKy trangThai = TrangThaiHocKy.SapDienRa;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
