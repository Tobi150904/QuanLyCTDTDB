package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể gốc định danh người dùng hệ thống.
 *
 * <p><b>Refactor Phase 7:</b> Bỏ bảng {@code NhanVienDoanhNghiep} riêng —
 * Nhân viên doanh nghiệp (NV DN) giờ được liên kết trực tiếp qua
 * {@link #doanhNghiep} (nullable FK). Khi {@code loaiNguoiDung = DoanhNghiep},
 * field này bắt buộc NOT NULL (CHECK constraint ở SQL).</p>
 *
 * <p>Trường không cần quản lý chức vụ / phòng ban / chuyên môn của NV DN —
 * chỉ cần biết "NV này thuộc DN nào" để filter dropdown chấm điểm thực tập.</p>
 */
@Entity
@Table(name = "NguoiDung")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NguoiDung {

    @Id
    @Column(name = "MaNguoiDung", length = 20)
    private String maNguoiDung;

    @Column(name = "TenDangNhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "MatKhauHash", nullable = false, length = 255)
    private String matKhauHash;

    @Column(name = "Email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "HoTen", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @Column(name = "TrangThaiTK", columnDefinition = "BIT DEFAULT 1")
    private Boolean trangThaiTK = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiNguoiDung", nullable = false, length = 20)
    private LoaiNguoiDung loaiNguoiDung;

    /**
     * Doanh nghiệp mà NV DN này thuộc về (chỉ áp dụng khi
     * {@code loaiNguoiDung = DoanhNghiep}).
     *
     * <p>- {@code loaiNguoiDung = DoanhNghiep} → NOT NULL (bắt buộc).</p>
     * <p>- Các loại khác → NULL.</p>
     *
     * <p>Ràng buộc này được đảm bảo bởi CHECK constraint
     * {@code chk_nd_dn_required} trong SQL schema.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep")
    private DoanhNghiep doanhNghiep;

    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<NhomNguoiDung> nhomNguoiDungs = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Getter tiện ích =====

    /**
     * Lấy mã doanh nghiệp (nếu có).
     */
    public String getMaDoanhNghiep() {
        return doanhNghiep != null ? doanhNghiep.getMaDoanhNghiep() : null;
    }

    /**
     * Lấy tên doanh nghiệp (nếu có).
     */
    public String getTenDoanhNghiep() {
        return doanhNghiep != null ? doanhNghiep.getTenDoanhNghiep() : null;
    }
}
