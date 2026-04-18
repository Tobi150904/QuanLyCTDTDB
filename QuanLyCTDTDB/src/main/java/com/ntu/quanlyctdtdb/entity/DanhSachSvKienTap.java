package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DanhSachSinhVienKienTap")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DanhSachSvKienTap {

    @EmbeddedId
    private DanhSachSvKienTapId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maDotKT")
    @JoinColumn(name = "MaDotKT")
    private DotKienTap dotKienTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSV")
    @JoinColumn(name = "MaSV")
    private SinhVien sinhVien;

    @Column(name = "DaThamGia", columnDefinition = "BIT DEFAULT 1")
    private Boolean daThamGia = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
