package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.NhanVienDoanhNghiep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Phase 7 — Repository cho {@link NhanVienDoanhNghiep}. Truy xuất NV DN
 * theo doanh nghiệp (dùng cho dropdown chọn người chấm cột DN trong UI
 * thực tập) và theo NguoiDung (dùng để validate khi service lưu KetQuaThucTap).
 */
@Repository
public interface NhanVienDoanhNghiepRepository extends JpaRepository<NhanVienDoanhNghiep, String> {

    /**
     * Liệt kê NV DN của một doanh nghiệp (đã fetch nguoiDung để tránh
     * LazyInitException khi template hiển thị họ tên).
     */
    @Query("""
        SELECT nv FROM NhanVienDoanhNghiep nv
        LEFT JOIN FETCH nv.nguoiDung
        LEFT JOIN FETCH nv.doanhNghiep
        WHERE nv.doanhNghiep.maDoanhNghiep = :maDN
        ORDER BY nv.nguoiDung.hoTen ASC
        """)
    List<NhanVienDoanhNghiep> findByDoanhNghiep(@Param("maDN") String maDoanhNghiep);

    /**
     * Tìm tất cả NV DN (đã fetch). Dùng cho admin / báo cáo.
     */
    @Query("""
        SELECT nv FROM NhanVienDoanhNghiep nv
        LEFT JOIN FETCH nv.nguoiDung
        LEFT JOIN FETCH nv.doanhNghiep
        ORDER BY nv.doanhNghiep.tenDoanhNghiep ASC, nv.nguoiDung.hoTen ASC
        """)
    List<NhanVienDoanhNghiep> findAllFetch();

    /**
     * Tra cứu 1 NguoiDung -> NhanVienDoanhNghiep (UNIQUE 1-1).
     * Dùng để validate khi capNhatDiem cho vai trò DN.
     */
    Optional<NhanVienDoanhNghiep> findByNguoiDung_MaNguoiDung(String maNguoiDung);

    /**
     * Liệt kê NV DN là cộng tác viên (Case A — vừa là NV DN, vừa thỉnh giảng).
     * Dùng cho báo cáo "đối tác hợp tác sâu".
     */
    List<NhanVienDoanhNghiep> findByLaCongTacVienTrue();

    boolean existsByNguoiDung_MaNguoiDung(String maNguoiDung);
}
