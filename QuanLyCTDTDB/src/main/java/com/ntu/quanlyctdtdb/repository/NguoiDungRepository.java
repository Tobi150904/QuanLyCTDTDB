package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {

    /**
     * Dung cho UserDetailsServiceImpl (login) - can load LUON nhomNguoiDungs
     * de tranh LazyInitializationException khi OSIV = false.
     */
    @EntityGraph(attributePaths = "nhomNguoiDungs")
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    Optional<NguoiDung> findByEmail(String email);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByEmail(String email);

    boolean existsByEmailAndMaNguoiDungNot(String email, String maNguoiDung);

    List<NguoiDung> findByLoaiNguoiDung(LoaiNguoiDung loaiNguoiDung);

    /**
     * Search co fetch nhomNguoiDungs de template danh-sach.html iterate duoc
     * khi OSIV = false. Hibernate se log warning
     * "firstResult/maxResults specified with collection fetch" - chap nhan
     * duoc vi page size nho (15) va collection nhomNguoiDungs moi user rat it.
     */
    @EntityGraph(attributePaths = "nhomNguoiDungs")
    @Query("SELECT n FROM NguoiDung n WHERE " +
           "(:keyword IS NULL OR LOWER(n.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(n.tenDangNhap) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(n.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:loai IS NULL OR n.loaiNguoiDung = :loai)")
    Page<NguoiDung> searchNguoiDung(@Param("keyword") String keyword,
                                    @Param("loai") LoaiNguoiDung loai,
                                    Pageable pageable);

    /**
     * Dung cho chi-tiet, form sua - can load nhomNguoiDungs de controller
     * stream vai tro va template render badge vai tro.
     */
    @EntityGraph(attributePaths = "nhomNguoiDungs")
    Optional<NguoiDung> findWithRolesByMaNguoiDung(String maNguoiDung);

    // Dem so luong theo loai
    long countByLoaiNguoiDung(LoaiNguoiDung loaiNguoiDung);

    /**
     * Lay MaNguoiDung lon nhat theo prefix (vi du: "SV2025" / "GV" / "AD" / "DN").
     * Dung de sinh ID moi deterministic - khong phu thuoc count() gay duplicate
     * khi da co record bi xoa. Ket hop voi @Transactional(SERIALIZABLE) hoac
     * locking tang cuong de tranh race condition giua 2 request song song.
     *
     * <p>Tra ve NULL neu chua co record nao.
     */
    @Query("SELECT MAX(n.maNguoiDung) FROM NguoiDung n "
            + "WHERE n.maNguoiDung LIKE CONCAT(:prefix, '%')")
    String findMaxMaNguoiDungByPrefix(@Param("prefix") String prefix);

    // =====================================================================
    // Phase 7 Refactor — NV DN query (thay thế NhanVienDoanhNghiepRepository)
    // =====================================================================

    /**
     * Liệt kê tất cả NguoiDung loại DoanhNghiep (nhân viên / đại diện DN)
     * đã fetch doanhNghiep để tránh LazyInitException khi template hiển thị
     * tên DN trong dropdown chọn người chấm điểm.
     *
     * <p>Dùng thay thế cho {@code NhanVienDoanhNghiepRepository.findAllFetch()}.</p>
     */
    @Query("""
        SELECT n FROM NguoiDung n
        LEFT JOIN FETCH n.doanhNghiep
        WHERE n.loaiNguoiDung = com.ntu.quanlyctdtdb.enums.LoaiNguoiDung.DoanhNghiep
          AND n.trangThaiTK = true
        ORDER BY n.doanhNghiep.tenDoanhNghiep ASC, n.hoTen ASC
        """)
    List<NguoiDung> findAllNhanVienDNFetch();

    /**
     * Liệt kê NguoiDung loại DoanhNghiep thuộc một DN cụ thể.
     *
     * <p>Dùng thay thế cho {@code NhanVienDoanhNghiepRepository.findByDoanhNghiep()}.</p>
     */
    @Query("""
        SELECT n FROM NguoiDung n
        LEFT JOIN FETCH n.doanhNghiep
        WHERE n.loaiNguoiDung = com.ntu.quanlyctdtdb.enums.LoaiNguoiDung.DoanhNghiep
          AND n.doanhNghiep.maDoanhNghiep = :maDN
          AND n.trangThaiTK = true
        ORDER BY n.hoTen ASC
        """)
    List<NguoiDung> findNhanVienDNByDoanhNghiep(@Param("maDN") String maDoanhNghiep);
}
