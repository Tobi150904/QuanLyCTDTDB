package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;

@Entity
@Table(name = "HocKyNamHoc")
@Getter @Setter
@NoArgsConstructor
public class HocKyNamHoc extends BaseAuditEntity {

    @Id
    @Column(name = "MaHocKy", length = 20, nullable = false)
    private String maHocKy;

    @Column(name = "TenHocKy", length = 50, nullable = false)
    private String tenHocKy;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiHocKy trangThai = TrangThaiHocKy.SapDienRa;

    @OneToMany(mappedBy = "hocKy")
    private List<LopHocPhan> lopHocPhans = new ArrayList<>();
}