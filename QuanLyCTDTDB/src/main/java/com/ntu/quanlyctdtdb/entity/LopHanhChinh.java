package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LopHanhChinh")
@Getter @Setter
@NoArgsConstructor
public class LopHanhChinh extends BaseAuditEntity {

    @Id
    @Column(name = "MaLopHC", length = 20, nullable = false)
    private String maLopHC;

    @Column(name = "TenLop", length = 100, nullable = false)
    private String tenLop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCTDT")
    private ChuongTrinhDaoTao chuongTrinhDaoTao;

    @Column(name = "KhoaHoc", length = 20)
    private String khoaHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCoVan", referencedColumnName = "MaNguoiDung")
    private NguoiDung coVan;

    @OneToMany(mappedBy = "lopHanhChinh")
    private List<NguoiDung> sinhViens = new ArrayList<>();

    @OneToMany(mappedBy = "lopHanhChinh")
    private List<DotKienTap> dotKienTaps = new ArrayList<>();
}