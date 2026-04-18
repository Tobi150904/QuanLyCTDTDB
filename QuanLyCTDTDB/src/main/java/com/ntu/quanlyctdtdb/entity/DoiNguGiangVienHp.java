package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DoiNguGiangVienHP")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DoiNguGiangVienHp {

    @EmbeddedId
    private DoiNguGiangVienHpId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHocPhan")
    @JoinColumn(name = "MaHocPhan")
    private HocPhan hocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maGiangVien")
    @JoinColumn(name = "MaGiangVien")
    private GiangVien giangVien;

    @Column(name = "TrangThai", columnDefinition = "BIT DEFAULT 1")
    private Boolean trangThai = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
