package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DanhSachSinhVienLopHocPhan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DanhSachSvLopHocPhan {

    @EmbeddedId
    private DanhSachSvLopHocPhanId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maSV")
    @JoinColumn(name = "MaSV")
    private SinhVien sinhVien;

    // LopHocPhan association bi bo de tranh Hibernate 7 duplicate column error:
    // maCTDT, maHocPhan, maHocKy, maLopHocPhan da co trong DanhSachSvLopHocPhanId (@EmbeddedId).
    // Muon lay LopHocPhan -> lookup qua LopHocPhanRepository bang ID.

    @Column(name = "NhanXet", columnDefinition = "TEXT")
    private String nhanXet;

    @Column(name = "DaCanhBao", columnDefinition = "BIT DEFAULT 0")
    private Boolean daCanhBao = false;

    @Column(name = "KetQuaXuLy", columnDefinition = "TEXT")
    private String ketQuaXuLy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
