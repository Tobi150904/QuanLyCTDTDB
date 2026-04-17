package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.ntu.quanlyctdtdb.enums.LoaiNhanXet;
import com.ntu.quanlyctdtdb.enums.LoaiDanhGia;

@Entity
@Table(name = "DanhGiaVaCanhBao")
@Getter @Setter
@NoArgsConstructor
public class DanhGiaVaCanhBao extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDanhGia")
    private Integer maDanhGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSV", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MaHocPhan", referencedColumnName = "MaHocPhan"),
        @JoinColumn(name = "MaHocKy", referencedColumnName = "MaHocKy"),
        @JoinColumn(name = "MaLopHC", referencedColumnName = "MaLopHC")
    })
    private LopHocPhan lopHocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiNhanXet", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung nguoiNhanXet;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiNhanXet", length = 10, nullable = false)
    private LoaiNhanXet loaiNhanXet;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiDanhGia", length = 20, nullable = false)
    private LoaiDanhGia loaiDanhGia = LoaiDanhGia.QuaTrinh;

    @Column(name = "NoiDung", columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Column(name = "DaXuLy", nullable = false)
    private Boolean daXuLy = false;

    @Column(name = "KetQuaXuLy", columnDefinition = "TEXT")
    private String ketQuaXuLy;
}