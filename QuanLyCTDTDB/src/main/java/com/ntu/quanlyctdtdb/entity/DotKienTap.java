package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "DotKienTap")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DotKienTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDotKT")
    private Integer maDotKT;

    @Column(name = "TenDotKT", nullable = false, length = 200)
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
    private GiangVien gvPhuTrach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep", nullable = false)
    private DoanhNghiep doanhNghiep;

    @Column(name = "NhanXetGV", columnDefinition = "TEXT")
    private String nhanXetGV;

    @Column(name = "NhanXetDN", columnDefinition = "TEXT")
    private String nhanXetDN;

    @Column(name = "FileMinhChung", length = 255)
    private String fileMinhChung;

    @Column(name = "KinhPhiChung", precision = 15, scale = 2)
    private BigDecimal kinhPhiChung;

    @Column(name = "KinhPhiTungSV", precision = 15, scale = 2)
    private BigDecimal kinhPhiTungSV;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20)
    private TrangThaiDotKT trangThai = TrangThaiDotKT.ChuanBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiTao", nullable = false)
    private NguoiDung nguoiTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet")
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @OneToMany(mappedBy = "dotKienTap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DanhSachSvKienTap> danhSachSinhViens = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
