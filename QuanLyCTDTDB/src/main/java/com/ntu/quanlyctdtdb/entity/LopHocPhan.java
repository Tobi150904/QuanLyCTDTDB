package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiLopHP;

/**
 * Bang: LopHocPhan
 * PK : MaLopHP (VARCHAR 20) - VD: HP001-HK1-24-25-01
 * Duoc auto-create khi CTDT duoc phe duyet (Rule 3)
 */
@Entity
@Table(name = "LopHocPhan")
@Getter
@Setter
@NoArgsConstructor
public class LopHocPhan extends BaseAuditEntity {

    @Id
    @Column(name = "MaLopHP", length = 20, nullable = false)
    private String maLopHP;

    // FK: HocPhan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocPhan", referencedColumnName = "MaHocPhan", nullable = false)
    private HocPhan hocPhan;

    // FK: HocKy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocKy", referencedColumnName = "MaHocKy", nullable = false)
    private HocKyNamHoc hocKy;

    /**
     * GiangVien: Nullable - null ngay sau khi auto-create
     * BCN se gan GV sau (Rule 3)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiangVien", referencedColumnName = "MaNguoiDung")
    private NguoiDung giangVien;

    // FK: LopHanhChinh (SV trong lop nay)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHC", referencedColumnName = "MaLopHC")
    private LopHanhChinh lopHanhChinh;

    @Column(name = "SiSo")
    private Integer siSo;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiLopHP trangThai = TrangThaiLopHP.DangMo;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    // ---- Relations (mappedBy) ----
    @OneToMany(mappedBy = "lopHocPhan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<TaiLieuMonHoc> taiLieus = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan", fetch = FetchType.LAZY)
    private List<DanhGiaVaCanhBao> danhGias = new ArrayList<>();
}
