package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Phase 7 refactor: <code>MaNguoiDanhGia</code> giờ tham chiếu trực tiếp về
 * {@link NguoiDung} (thay vì {@link GiangVien} như trước Phase 7).
 *
 * <p><b>Lý do thiết kế:</b></p>
 * <ul>
 *   <li>LoaiThucTap = <i>Truong</i>: cột 1 = GV_HD, cột 2 = GV_PB — cả hai
 *       đều là giảng viên (NguoiDung có row trong {@link GiangVien}).</li>
 *   <li>LoaiThucTap = <i>DoanhNghiep</i>: cột 1 = DN (do
 *       {@link NhanVienDoanhNghiep} chấm), cột 2 = GV_HD (giảng viên giám
 *       sát từ trường). Cột DN <b>không phải</b> giảng viên — vì vậy FK cũ
 *       về GiangVien là sai semantic, dẫn tới phải seed giả GV001 cho mọi
 *       cột DN ⇒ thống kê người đánh giá bị nhiễu, audit không tin cậy.</li>
 * </ul>
 *
 * <p>Sau refactor, <i>MaNguoiDanhGia</i> luôn trỏ về {@link NguoiDung}
 * (thực thể gốc) — service layer (DotThucTapServiceImpl#capNhatDiem) chịu
 * trách nhiệm validate role-based:</p>
 * <ul>
 *   <li>{@code MaVaiTro = 'DN'} ⇒ NguoiDung phải có {@link NhanVienDoanhNghiep}
 *       record và thuộc đúng DN tiếp nhận SV (hoặc DN cho phép cross-mentor).</li>
 *   <li>{@code MaVaiTro IN ('GV_HD','GV_PB','CVHT','GV')} ⇒ NguoiDung phải có
 *       {@link GiangVien} record.</li>
 * </ul>
 */
@Entity
@Table(name = "KetQuaThucTap")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class KetQuaThucTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maKetQua;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaThucTap", nullable = false)
    private DanhSachThucTap danhSachThucTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaVaiTro", nullable = false)
    private VaiTroThucTap vaiTroThucTap;

    /**
     * Phase 7: FK đổi từ {@code GiangVien(MaGV)} sang
     * {@code NguoiDung(MaNguoiDung)} để mô hình hoá cả vai trò DN
     * (nhân viên doanh nghiệp) lẫn vai trò GV (giảng viên trường).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaNguoiDanhGia", nullable = false)
    private NguoiDung nguoiDanhGia;

    @Column(name = "Diem", precision = 4, scale = 2)
    private BigDecimal diem;

    @Column(name = "NhanXet", columnDefinition = "TEXT")
    private String nhanXet;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
