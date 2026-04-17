package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ntu.quanlyctdtdb.enums.TrangThaiPhanCong;

/**
 * Bang: PhanCongThucTap
 * PK : MaPhanCong (INT, auto increment)
 * UNIQUE constraint: (MaDotTT, MaSinhVien) - Rule 7: 1 SV chi phan cong 1 lan/dot
 */
@Entity
@Table(
    name = "PhanCongThucTap",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_phancong_dot_sv",
            columnNames = {"MaDotTT", "MaSinhVien"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class PhanCongThucTap extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaPhanCong")
    private Integer maPhanCong;

    // FK: DotThucTap
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDotTT", referencedColumnName = "MaDotTT", nullable = false)
    private DotThucTap dotThucTap;

    // FK: SinhVien -> NguoiDung (role SV)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSinhVien", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung sinhVien;

    // FK: DoanhNghiep
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep", referencedColumnName = "MaDoanhNghiep", nullable = false)
    private DoanhNghiep doanhNghiep;

    // FK: GiangVienGiamSat -> NguoiDung (role GV)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiangVienGiamSat", referencedColumnName = "MaNguoiDung")
    private NguoiDung giangVienGiamSat;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiPhanCong trangThai = TrangThaiPhanCong.DaPhanCong;

    /**
     * Diem tu DN (0.00 - 10.00)
     */
    @Column(name = "DiemDN", precision = 4, scale = 2)
    private BigDecimal diemDN;

    @Column(name = "NhanXetDN", columnDefinition = "TEXT")
    private String nhanXetDN;

    /**
     * Diem tu GV giam sat (0.00 - 10.00)
     */
    @Column(name = "DiemGV", precision = 4, scale = 2)
    private BigDecimal diemGV;

    @Column(name = "NhanXetGV", columnDefinition = "TEXT")
    private String nhanXetGV;

    /**
     * NhanXetSV: Cam nhan cua sinh vien sau thuc tap
     */
    @Column(name = "NhanXetSV", columnDefinition = "TEXT")
    private String nhanXetSV;

    @Column(name = "NgayPhanCong", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayPhanCong = LocalDateTime.now();
}
