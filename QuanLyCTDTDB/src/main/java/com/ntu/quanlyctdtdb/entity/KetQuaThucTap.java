package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "KetQuaThucTap")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KetQuaThucTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKetQua;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaThucTap", nullable = false)
    private DanhSachThucTap danhSachThucTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaVaiTro", nullable = false)
    private VaiTroThucTap vaiTroThucTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDanhGia", nullable = false)
    private GiangVien nguoiDanhGia;

    @Column(name = "Diem", precision = 4, scale = 2)
    private BigDecimal diem;

    @Column(name = "NhanXet", columnDefinition = "TEXT")
    private String nhanXet;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
