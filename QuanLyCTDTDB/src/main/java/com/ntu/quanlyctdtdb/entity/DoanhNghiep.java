package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DoanhNghiep")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DoanhNghiep {

    @Id
    @Column(name = "MaDoanhNghiep", length = 20)
    private String maDoanhNghiep;

    @Column(name = "TenDoanhNghiep", nullable = false, length = 200)
    private String tenDoanhNghiep;

    @Column(name = "LinhVuc", length = 200)
    private String linhVuc;

    @Column(name = "NguoiDaiDien", length = 100)
    private String nguoiDaiDien;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "DiaChiDN", length = 255)
    private String diaChiDN;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20)
    private TrangThaiDoanhNghiep trangThai = TrangThaiDoanhNghiep.DangHopTac;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
