package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;

@Entity
@Table(name = "DotKienTap")
@Getter @Setter
@NoArgsConstructor
public class DotKienTap extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDotKT")
    private Integer maDotKT;

    @Column(name = "TenDotKT", length = 200, nullable = false)
    private String tenDotKT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHC", nullable = false)
    private LopHanhChinh lopHanhChinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocKy", nullable = false)
    private HocKyNamHoc hocKy;

    @Column(name = "ThoiGian")
    private LocalDate thoiGian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGVPhuTrach")
    private NguoiDung gvPhuTrach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep", nullable = false)
    private DoanhNghiep doanhNghiep;

    @Column(name = "NhanXetGV", columnDefinition = "TEXT")
    private String nhanXetGV;

    @Column(name = "NhanXetDN", columnDefinition = "TEXT")
    private String nhanXetDN;

    @Column(name = "FileMinhChung", length = 255)
    private String fileMinhChung;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiDotKT trangThai = TrangThaiDotKT.ChuanBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet")
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;
}