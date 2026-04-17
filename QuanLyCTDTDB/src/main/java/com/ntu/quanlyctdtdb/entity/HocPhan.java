package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;

@Entity
@Table(name = "HocPhan")
@Getter @Setter
@NoArgsConstructor
public class HocPhan extends BaseAuditEntity {

    @Id
    @Column(name = "MaHocPhan", length = 20, nullable = false)
    private String maHocPhan;

    @Column(name = "TenHocPhan", length = 200, nullable = false)
    private String tenHocPhan;

    @Column(name = "SoTinChi", nullable = false)
    private Integer soTinChi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChuNhiemHP", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung chuNhiemHP;

    @Column(name = "FileDeCuong", length = 255)
    private String fileDeCuong;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiHocPhan trangThai = TrangThaiHocPhan.BanNhap;

    @OneToMany(mappedBy = "hocPhan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DoiNguGiangVienHP> doiNguGiangViens = new ArrayList<>();
}