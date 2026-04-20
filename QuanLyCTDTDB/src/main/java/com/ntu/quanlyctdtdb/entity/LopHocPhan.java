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

    // ctdtHocPhan va hocKy duoc lay qua Repository bang ID trong EmbeddedId
    // Khong map truc tiep de tranh Hibernate 7 duplicate column error
    // (MaCTDT, MaHocPhan, MaHocKy da co trong LopHocPhanId @EmbeddedId)

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

    // Inverse collection cua DanhSachSvLopHocPhan da bi bo
    // vi entity DanhSachSvLopHocPhan khong con field 'lopHocPhan' (da xoa de tranh duplicate column).
    // Truy van danh sach SV qua DanhSachSvLopHocPhanRepository bang LopHocPhanId.

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
