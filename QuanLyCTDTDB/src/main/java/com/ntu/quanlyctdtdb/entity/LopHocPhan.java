package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiLopHP;

@Entity
@Table(name = "LopHocPhan")
@Getter @Setter
@NoArgsConstructor
public class LopHocPhan extends BaseAuditEntity {

    @EmbeddedId
    private LopHocPhanId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHocPhan")
    @JoinColumn(name = "MaHocPhan")
    private HocPhan hocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHocKy")
    @JoinColumn(name = "MaHocKy")
    private HocKyNamHoc hocKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maLopHC")
    @JoinColumn(name = "MaLopHC")
    private LopHanhChinh lopHanhChinh;

    @Column(name = "NhomHocPhan", nullable = false, unique = true)
    private Integer nhomHocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiangVien")
    private NguoiDung giangVien;

    @Column(name = "SiSoToiDa", nullable = false)
    private Integer siSoToiDa;

    @Column(name = "SiSoThucTe")
    private Integer siSoThucTe = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiLopHP trangThai = TrangThaiLopHP.DangMo;

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaiLieuMonHoc> taiLieus = new ArrayList<>();

    @OneToMany(mappedBy = "lopHocPhan")
    private List<DanhGiaVaCanhBao> danhGias = new ArrayList<>();
}