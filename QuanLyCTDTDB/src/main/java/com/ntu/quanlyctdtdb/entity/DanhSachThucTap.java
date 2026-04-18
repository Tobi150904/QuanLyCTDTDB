package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.LoaiThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiThucTap;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DanhSachThucTap")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class DanhSachThucTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maThucTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDotTT", nullable = false)
    private DotThucTap dotThucTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSV", nullable = false)
    private SinhVien sinhVien;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiThucTap", nullable = false)
    private LoaiThucTap loaiThucTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep")
    private DoanhNghiep doanhNghiep;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiThucTap trangThai = TrangThaiThucTap.DaPhanCong;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
