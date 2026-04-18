package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.TrangThaiLopHocPhan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "LopHocPhan")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LopHocPhan {

    @EmbeddedId
    private LopHocPhanId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
        @JoinColumn(name = "MaCTDT",    insertable = false, updatable = false),
        @JoinColumn(name = "MaHocPhan", insertable = false, updatable = false)
    })
    private CtdtHocPhan ctdtHocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocKy", insertable = false, updatable = false)
    private HocKyNamHoc hocKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiangVien")
    private GiangVien giangVien;

    @Column(name = "SiSoToiDa", nullable = false)
    private Integer siSoToiDa;

    @Column(name = "SiSoThucTe")
    private Integer siSoThucTe = 0;

    @Column(name = "FileDeCuongChiTiet", length = 255)
    private String fileDeCuongChiTiet;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20)
    private TrangThaiLopHocPhan trangThai = TrangThaiLopHocPhan.DangMo;

    @OneToMany(mappedBy = "lopHocPhan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DanhSachSvLopHocPhan> danhSachSinhViens = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
