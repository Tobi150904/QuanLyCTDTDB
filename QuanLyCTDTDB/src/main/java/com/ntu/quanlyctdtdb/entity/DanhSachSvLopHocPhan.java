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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MaCTDT",       referencedColumnName = "MaCTDT",       insertable = false, updatable = false),
        @JoinColumn(name = "MaHocPhan",    referencedColumnName = "MaHocPhan",    insertable = false, updatable = false),
        @JoinColumn(name = "MaHocKy",      referencedColumnName = "MaHocKy",      insertable = false, updatable = false),
        @JoinColumn(name = "MaLopHocPhan", referencedColumnName = "MaLopHocPhan", insertable = false, updatable = false)
    })
    private LopHocPhan lopHocPhan;

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
