package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ntu.quanlyctdtdb.enums.LoaiTaiLieu;
import com.ntu.quanlyctdtdb.enums.TrangThaiTaiLieu;

/**
 * Bang: TaiLieuMonHoc
 * PK : MaTaiLieu (INT, auto increment)
 * UNIQUE constraint: (MaLopHP, Loai) - moi lop chi co 1 tai lieu moi loai
 * RULE: khi GV nop lai sau TuChoi -> UPDATE ban hien tai, KHONG INSERT moi
 */
@Entity
@Table(
    name = "TaiLieuMonHoc",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_tailieu_lophp_loai",
            columnNames = {"MaLopHP", "Loai"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class TaiLieuMonHoc extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTaiLieu")
    private Integer maTaiLieu;

    // FK: LopHocPhan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHP", referencedColumnName = "MaLopHP", nullable = false)
    private LopHocPhan lopHocPhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "Loai", length = 20, nullable = false)
    private LoaiTaiLieu loai;

    @Column(name = "TenFile", length = 255, nullable = false)
    private String tenFile;

    @Column(name = "DuongDanFile", length = 500, nullable = false)
    private String duongDanFile;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiTaiLieu trangThai = TrangThaiTaiLieu.ChoDuyet;

    // FK: NguoiNop -> NguoiDung (GV)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiNop", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung nguoiNop;

    @Column(name = "NgayNop", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayNop = LocalDateTime.now();

    /**
     * HanNop: Rule 5 - deadline = HocKy.NgayBatDau + 14 ngay (danh cho DeCuongChiTiet)
     */
    @Column(name = "HanNop")
    private LocalDate hanNop;

    /**
     * QuaHan: true neu NgayNop > HanNop
     */
    @Column(name = "QuaHan", columnDefinition = "BIT(1) DEFAULT 0")
    private Boolean quaHan = false;

    // FK: NguoiDuyet -> NguoiDung (CNHP)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDuyet", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "GhiChuTuChoi", columnDefinition = "TEXT")
    private String ghiChuTuChoi;

    @Column(name = "LanNop", nullable = false, columnDefinition = "INT DEFAULT 1")
    private Integer lanNop = 1;
}
