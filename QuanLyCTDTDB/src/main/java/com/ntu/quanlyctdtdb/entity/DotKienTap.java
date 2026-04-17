package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;

/**
 * Bang: DotKienTap
 * PK : MaDotKT (INT, auto increment)
 * Workflow: ChuanBi -> ChoDuyet -> DaDuyet -> DaThucHien | DaHuy
 */
@Entity
@Table(name = "DotKienTap")
@Getter
@Setter
@NoArgsConstructor
public class DotKienTap extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDotKT")
    private Integer maDotKT;

    @Column(name = "TenDot", length = 100, nullable = false)
    private String tenDot;

    // FK: LopHanhChinh (lop duoc kien tap)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHC", referencedColumnName = "MaLopHC", nullable = false)
    private LopHanhChinh lopHanhChinh;

    @Column(name = "NgayBatDau")
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiDotKT trangThai = TrangThaiDotKT.ChuanBi;

    @Column(name = "NoiDungKienTap", columnDefinition = "TEXT")
    private String noiDungKienTap;

    // FK: NguoiTao -> NguoiDung (BCN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiTao", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung nguoiTao;

    // FK: NguoiPheduyet -> NguoiDung (TTDTXS/PDT)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiPheduyet", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiPheduyet;

    @Column(name = "NgayPheduyet")
    private LocalDateTime ngayPheduyet;

    @Column(name = "NhanXetGV", columnDefinition = "TEXT")
    private String nhanXetGV;

    @Column(name = "NhanXetDN", columnDefinition = "TEXT")
    private String nhanXetDN;

    // FK: DoanhNghiepTiepNhan -> DoanhNghiep
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep", referencedColumnName = "MaDoanhNghiep")
    private DoanhNghiep doanhNghiep;
}
