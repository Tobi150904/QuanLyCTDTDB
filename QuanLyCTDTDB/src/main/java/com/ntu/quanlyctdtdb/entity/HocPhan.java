package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;

/**
 * Bang: HocPhan
 * PK : MaHocPhan (VARCHAR 10) - VD: HP001
 */
@Entity
@Table(name = "HocPhan")
@Getter
@Setter
@NoArgsConstructor
public class HocPhan extends BaseAuditEntity {

    @Id
    @Column(name = "MaHocPhan", length = 10, nullable = false)
    private String maHocPhan;

    @Column(name = "TenHocPhan", length = 150, nullable = false)
    private String tenHocPhan;

    @Column(name = "SoTinChi", nullable = false)
    private Integer soTinChi;

    @Column(name = "MoTa", columnDefinition = "TEXT")
    private String moTa;

    /**
     * DuongDanDeCuongGoc: File Word de cuong goc (truoc khi tao LopHP)
     */
    @Column(name = "DuongDanDeCuongGoc", length = 500)
    private String duongDanDeCuongGoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiHocPhan trangThai = TrangThaiHocPhan.BanNhap;

    // FK: ChuNhiemHP -> NguoiDung (role CNHP)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaChuNhiemHP", referencedColumnName = "MaNguoiDung")
    private NguoiDung chuNhiemHP;

    // FK: NguoiTao -> NguoiDung (BCN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiTao", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiTao;

    // FK: NguoiPheduyet -> NguoiDung (PDT/TTDTXS)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiPheduyet", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiPheduyet;

    // ---- Relations (mappedBy) ----
    @OneToMany(mappedBy = "hocPhan", fetch = FetchType.LAZY)
    private List<DoiNguGiangVienHP> doiNguGiangViens = new ArrayList<>();

    @OneToMany(mappedBy = "hocPhan", fetch = FetchType.LAZY)
    private List<LopHocPhan> lopHocPhans = new ArrayList<>();

    @ManyToMany(mappedBy = "hocPhans", fetch = FetchType.LAZY)
    private List<ChuongTrinhDaoTao> chuongTrinhDaoTaos = new ArrayList<>();
}
