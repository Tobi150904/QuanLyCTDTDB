package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiSinhVien;

/**
 * Bang: NguoiDung
 * PK : MaNguoiDung (VARCHAR 10) - VD: SV001, GV001, DN001
 * TrangThaiTK: BIT(1) - 1=Hoat dong, 0=Khoa
 */
@Entity
@Table(name = "NguoiDung")
@Getter
@Setter
@NoArgsConstructor
public class NguoiDung extends BaseAuditEntity {

    @Id
    @Column(name = "MaNguoiDung", length = 10, nullable = false)
    private String maNguoiDung;

    @Column(name = "HoTen", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "Email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "TenDangNhap", length = 50, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "MatKhauHash", length = 255, nullable = false)
    private String matKhauHash;

    /**
     * TrangThaiTK: BIT(1) -> Boolean trong Java
     * true = 1 = Hoat dong, false = 0 = Khoa
     */
    @Column(name = "TrangThaiTK", nullable = false, columnDefinition = "BIT(1) DEFAULT 1")
    private Boolean trangThaiTK = true;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "DiaChi", length = 255)
    private String diaChi;

    /**
     * ChuyenNganh: Chi dung voi GV
     */
    @Column(name = "ChuyenNganh", length = 100)
    private String chuyenNganh;

    /**
     * TrangThaiSV: Chi dung voi role SV
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThaiSV", length = 20)
    private TrangThaiSinhVien trangThaiSV;

    /**
     * MaLopHC: Chi dung voi role SV
     * FK -> LopHanhChinh.MaLopHC
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHC", referencedColumnName = "MaLopHC")
    private LopHanhChinh lopHanhChinh;

    /**
     * MaDoanhNghiep: Chi dung voi role DN
     * FK -> DoanhNghiep.MaDoanhNghiep
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep", referencedColumnName = "MaDoanhNghiep")
    private DoanhNghiep doanhNghiep;

    // ---- Relations (mappedBy) ----
    @OneToMany(mappedBy = "nguoiDung", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<NguoiDungVaiTro> vaiTros = new ArrayList<>();

    @OneToMany(mappedBy = "giangVien", fetch = FetchType.LAZY)
    private List<LopHocPhan> lopHocPhans = new ArrayList<>();
}
