package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;

@Entity
@Table(name = "ChuongTrinhDaoTao")
@Getter @Setter
@NoArgsConstructor
public class ChuongTrinhDaoTao extends BaseAuditEntity {

    @Id
    @Column(name = "MaCTDT", length = 20, nullable = false)
    private String maCTDT;

    @Column(name = "TenCTDT", length = 200, nullable = false)
    private String tenCTDT;

    @Column(name = "Khoa", length = 20)
    private String khoa;

    @Column(name = "FileWord", length = 255)
    private String fileWord;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20, nullable = false)
    private TrangThaiCTDT trangThai = TrangThaiCTDT.BanNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiTao", referencedColumnName = "MaNguoiDung", nullable = false)
    private NguoiDung nguoiTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @OneToMany(mappedBy = "chuongTrinhDaoTao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CTDT_HocPhan> chiTietHocPhans = new ArrayList<>();

    public void addChiTietHocPhan(CTDT_HocPhan chiTiet) {
        chiTietHocPhans.add(chiTiet);
        chiTiet.setChuongTrinhDaoTao(this);
    }

    public void removeChiTietHocPhan(CTDT_HocPhan chiTiet) {
        chiTietHocPhans.remove(chiTiet);
        chiTiet.setChuongTrinhDaoTao(null);
    }
}