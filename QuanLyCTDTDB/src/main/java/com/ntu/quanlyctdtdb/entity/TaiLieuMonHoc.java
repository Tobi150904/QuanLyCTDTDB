package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.ntu.quanlyctdtdb.enums.LoaiTaiLieu;
import com.ntu.quanlyctdtdb.enums.TrangThaiTaiLieu;

@Entity
@Table(name = "TaiLieuMonHoc", uniqueConstraints = {
    @UniqueConstraint(name = "uk_taidieu_lophp_loai", columnNames = {"MaHocPhan", "MaHocKy", "MaLopHC", "Loai"})
})
@Getter @Setter
@NoArgsConstructor
public class TaiLieuMonHoc extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTaiLieu")
    private Integer maTaiLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MaHocPhan", referencedColumnName = "MaHocPhan"),
        @JoinColumn(name = "MaHocKy", referencedColumnName = "MaHocKy"),
        @JoinColumn(name = "MaLopHC", referencedColumnName = "MaLopHC")
    })
    private LopHocPhan lopHocPhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "Loai", length = 20, nullable = false)
    private LoaiTaiLieu loai;

    @Column(name = "FileDinhKem", length = 255, nullable = false)
    private String fileDinhKem;

    @Column(name = "NgayNop", nullable = false)
    private LocalDateTime ngayNop = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiTaiLieu trangThai = TrangThaiTaiLieu.ChoDuyet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet")
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "NhanXet", columnDefinition = "TEXT")
    private String nhanXet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiNop", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiNop;
}