package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Bang: DoiNguGiangVienHP
 * PK (composite): (MaHocPhan, MaGiangVien) -> @EmbeddedId
 * TrangThai: BIT(1) - 1=Hoat dong, 0=Khong hoat dong
 */
@Entity
@Table(name = "DoiNguGiangVienHP")
@Getter
@Setter
@NoArgsConstructor
public class DoiNguGiangVienHP {

    @EmbeddedId
    private DoiNguGiangVienHPId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHocPhan")
    @JoinColumn(name = "MaHocPhan", referencedColumnName = "MaHocPhan")
    private HocPhan hocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maGiangVien")
    @JoinColumn(name = "MaGiangVien", referencedColumnName = "MaNguoiDung")
    private NguoiDung giangVien;

    /**
     * TrangThai: BIT(1) -> Boolean
     * true = 1 = Hoat dong trong doi ngu, false = 0 = Da rut khoi doi ngu
     */
    @Column(name = "TrangThai", nullable = false, columnDefinition = "BIT(1) DEFAULT 1")
    private Boolean trangThai = true;

    @Column(name = "NgayThem", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayThem = LocalDateTime.now();

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    // Constructor tien ich
    public DoiNguGiangVienHP(HocPhan hocPhan, NguoiDung giangVien) {
        this.id = new DoiNguGiangVienHPId(hocPhan.getMaHocPhan(), giangVien.getMaNguoiDung());
        this.hocPhan = hocPhan;
        this.giangVien = giangVien;
        this.trangThai = true;
        this.ngayThem = LocalDateTime.now();
    }
}
