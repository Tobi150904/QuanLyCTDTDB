package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.ntu.quanlyctdtdb.enums.LoaiNhanXet;

/**
 * Bang: DanhGiaVaCanhBao
 * PK : MaDanhGia (INT, auto increment)
 * RULE: LoaiNhanXet = TieuCuc -> tu dong gui email + tao canh bao cho CVHT
 * created_at/updated_at tu dong qua BaseAuditEntity.
 */
@Entity
@Table(name = "DanhGiaVaCanhBao")
@Getter
@Setter
@NoArgsConstructor
public class DanhGiaVaCanhBao extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDanhGia")
    private Integer maDanhGia;

    // FK: LopHocPhan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHP", referencedColumnName = "MaLopHP", nullable = false)
    private LopHocPhan lopHocPhan;

    // FK: SinhVien -> NguoiDung (role SV)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSinhVien", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung sinhVien;

    // FK: GiangVien -> NguoiDung (role GV, nguoi nhap nhan xet)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiangVien", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung giangVien;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiNhanXet", length = 10, nullable = false)
    private LoaiNhanXet loaiNhanXet;

    @Column(name = "NoiDung", columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Column(name = "NgayNhanXet", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayNhanXet = LocalDateTime.now();

    /**
     * DaXuLy: CVHT xu ly canh bao
     * true = 1 = Da xu ly, false = 0 = Chua xu ly
     */
    @Column(name = "DaXuLy", nullable = false, columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean daXuLy = false;

    @Column(name = "KetQuaXuLy", columnDefinition = "TEXT")
    private String ketQuaXuLy;

    @Column(name = "NgayXuLy")
    private LocalDateTime ngayXuLy;

    // FK: NguoiXuLy -> NguoiDung (role CVHT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiXuLy", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiXuLy;
}
