package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "NguoiDung")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NguoiDung {

    @Id
    @Column(name = "MaNguoiDung", length = 20)
    private String maNguoiDung;

    @Column(name = "TenDangNhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "MatKhauHash", nullable = false, length = 255)
    private String matKhauHash;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "TrangThaiTK", columnDefinition = "BIT DEFAULT 1")
    private Boolean trangThaiTK = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiNguoiDung", nullable = false, length = 20)
    private LoaiNguoiDung loaiNguoiDung;

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<NhomNguoiDung> nhomNguoiDungs = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
