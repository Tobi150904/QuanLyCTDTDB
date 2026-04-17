package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;

/**
 * Bang: DoanhNghiep
 * PK : MaDoanhNghiep (VARCHAR 10) - VD: DN001
 */
@Entity
@Table(name = "DoanhNghiep")
@Getter
@Setter
@NoArgsConstructor
public class DoanhNghiep extends BaseAuditEntity {

    @Id
    @Column(name = "MaDoanhNghiep", length = 10, nullable = false)
    private String maDoanhNghiep;

    @Column(name = "TenDoanhNghiep", length = 150, nullable = false)
    private String tenDoanhNghiep;

    @Column(name = "DiaChiDN", length = 255)
    private String diaChiDN;

    @Column(name = "NguoiDaiDien", length = 100)
    private String nguoiDaiDien;

    @Column(name = "EmailDN", length = 100, unique = true)
    private String emailDN;

    @Column(name = "SoDienThoaiDN", length = 15)
    private String soDienThoaiDN;

    @Column(name = "LinhVucHoatDong", length = 200)
    private String linhVucHoatDong;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiDoanhNghiep trangThai = TrangThaiDoanhNghiep.DangHopTac;

    // ---- Relations (mappedBy) ----
    @OneToMany(mappedBy = "doanhNghiep", fetch = FetchType.LAZY)
    private List<NguoiDung> taiKhoans = new ArrayList<>();

    @OneToMany(mappedBy = "doanhNghiep", fetch = FetchType.LAZY)
    private List<PhanCongThucTap> phanCongs = new ArrayList<>();
}
