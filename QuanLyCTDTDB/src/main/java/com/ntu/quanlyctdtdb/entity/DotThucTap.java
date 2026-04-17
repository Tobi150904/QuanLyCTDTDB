package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;

@Entity
@Table(name = "DotThucTap")
@Getter @Setter
@NoArgsConstructor
public class DotThucTap extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDotTT")
    private Integer maDotTT;

    @Column(name = "TenDotTT", length = 200, nullable = false)
    private String tenDotTT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocKy", nullable = false)
    private HocKyNamHoc hocKy;

    @Column(name = "NgayBatDau")
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;

    @Column(name = "FileMinhChung", length = 255)
    private String fileMinhChung;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiDotTT trangThai = TrangThaiDotTT.ChuanBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet")
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @OneToMany(mappedBy = "dotThucTap", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhanCongThucTap> phanCongs = new ArrayList<>();
}