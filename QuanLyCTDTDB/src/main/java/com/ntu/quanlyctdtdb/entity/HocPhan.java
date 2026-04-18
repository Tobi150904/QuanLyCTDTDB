package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "HocPhan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HocPhan {

    @Id
    @Column(name = "MaHocPhan", length = 20)
    private String maHocPhan;

    @Column(name = "TenHocPhan", nullable = false, length = 200)
    private String tenHocPhan;

    @Column(name = "SoTinChi", nullable = false)
    private Integer soTinChi;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiHocPhan", length = 20)
    private LoaiHocPhan loaiHocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChuNhiemHP", nullable = false)
    private GiangVien chuNhiemHP;

    @Column(name = "FileDeCuong", length = 255)
    private String fileDeCuong;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20)
    private TrangThaiHocPhan trangThai = TrangThaiHocPhan.BanNhap;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
