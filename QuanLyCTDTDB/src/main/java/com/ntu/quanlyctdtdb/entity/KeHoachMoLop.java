package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Bang ke hoach mo lop theo tung HocKy cu the.
 *
 * <p>Truoc day SoLopDuKien nam trong CTDT_HocPhan (gan voi vong doi CTDT),
 * nhung thuc te moi HocKy co the mo so lop khac nhau. Vi vay tach ra bang
 * rieng voi khoa (MaHocPhan, MaHocKy).</p>
 *
 * <p>Logic tao LopHocPhan (xem {@code LopHocPhanServiceImpl#taoLopHocPhanChoCTDT}):
 * <ol>
 *   <li>Uu tien doc KeHoachMoLop cho (MaHocPhan, MaHocKy).</li>
 *   <li>Neu khong co -> fallback sang CTDT_HocPhan.SoLopDuKien (gia tri mac dinh).</li>
 * </ol>
 * </p>
 */
@Entity
@Table(name = "KeHoachMoLop")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KeHoachMoLop {

    @EmbeddedId
    private KeHoachMoLopId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHocPhan")
    @JoinColumn(name = "MaHocPhan")
    private HocPhan hocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHocKy")
    @JoinColumn(name = "MaHocKy")
    private HocKyNamHoc hocKy;

    @Column(name = "SoLopDuKien", nullable = false)
    private Integer soLopDuKien;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
