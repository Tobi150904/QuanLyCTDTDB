package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Phase 7 — Mới: lưu cán bộ doanh nghiệp phụ trách hướng dẫn / chấm điểm
 * sinh viên thực tập tại doanh nghiệp.
 *
 * <p>Trước Phase 7, hệ thống chỉ có {@link GiangVien}; do đó cột 1
 * ({@code MaVaiTro = 'DN'}) của LoaiThucTap=DoanhNghiep buộc phải seed
 * giả GV001 — vi phạm semantic, nhiễu thống kê, audit không tin cậy.
 * Tách bảng riêng cho phép:</p>
 * <ul>
 *   <li>Lưu chính xác người chấm cột DN (nhân viên DN, không phải GV).</li>
 *   <li>Phân biệt rõ "GV thỉnh giảng" với "NV DN giám sát SV thực tập"
 *       về mặt báo cáo / phân quyền.</li>
 * </ul>
 *
 * <p><b>Case A (NV DN giảng dạy thỉnh giảng tại trường):</b> một
 * {@link NguoiDung} có thể đồng thời có cả record {@link GiangVien}
 * (LoaiGiangVien=GiangVienThinhGiang) lẫn record {@code NhanVienDoanhNghiep}.
 * Khi đó <code>laCongTacVien=1</code> để đánh dấu mức độ hợp tác sâu với
 * nhà trường. Ví dụ: nhân viên FPT vừa làm việc tại doanh nghiệp, vừa giảng
 * thỉnh giảng môn Lập Trình Web tại Khoa CNTT.</p>
 *
 * <p><b>Quan hệ:</b> 1-1 với {@link NguoiDung} (UNIQUE), N-1 với
 * {@link DoanhNghiep}. Trạng thái tài khoản kế thừa từ
 * {@code NguoiDung.trangThaiTK} — không tách trạng thái riêng.</p>
 */
@Entity
@Table(name = "NhanVienDoanhNghiep")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NhanVienDoanhNghiep {

    @Id
    @Column(name = "MaNVDN", length = 20)
    private String maNVDN;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDung", nullable = false, unique = true)
    private NguoiDung nguoiDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep", nullable = false)
    private DoanhNghiep doanhNghiep;

    @Column(name = "ChucVu", length = 100)
    private String chucVu;

    @Column(name = "PhongBan", length = 100)
    private String phongBan;

    @Column(name = "ChuyenMon", length = 200)
    private String chuyenMon;

    /**
     * Đánh dấu cộng tác viên / hợp tác sâu với nhà trường (vd: vừa là NV DN,
     * vừa thỉnh giảng — Case A). Mặc định 0.
     */
    @Column(name = "LaCongTacVien", columnDefinition = "BIT DEFAULT 0")
    private Boolean laCongTacVien = false;

    @Column(name = "GhiChu", length = 255)
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ===== Getter tiện ích (delegate sang NguoiDung / DoanhNghiep) =====

    public String getHoTen() {
        return nguoiDung != null ? nguoiDung.getHoTen() : maNVDN;
    }

    public String getEmail() {
        return nguoiDung != null ? nguoiDung.getEmail() : null;
    }

    public String getMaNguoiDung() {
        return nguoiDung != null ? nguoiDung.getMaNguoiDung() : null;
    }

    public String getMaDoanhNghiep() {
        return doanhNghiep != null ? doanhNghiep.getMaDoanhNghiep() : null;
    }

    public String getTenDoanhNghiep() {
        return doanhNghiep != null ? doanhNghiep.getTenDoanhNghiep() : null;
    }
}
