package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ChuongTrinhDaoTao")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChuongTrinhDaoTao {

    @Id
    @Column(name = "MaCTDT", length = 20)
    private String maCTDT;

    @Column(name = "TenCTDT", nullable = false, length = 200)
    private String tenCTDT;

    @Column(name = "Khoa", length = 20)
    private String khoa;

    @Column(name = "FileWord", length = 255)
    private String fileWord;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20)
    private TrangThaiCTDT trangThai = TrangThaiCTDT.BanNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiTao", nullable = false)
    private NguoiDung nguoiTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet")
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @OneToMany(mappedBy = "chuongTrinhDaoTao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<BcnThanhVien> bcnThanhViens = new ArrayList<>();

    @OneToMany(mappedBy = "chuongTrinhDaoTao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CtdtHocPhan> ctdtHocPhans = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
