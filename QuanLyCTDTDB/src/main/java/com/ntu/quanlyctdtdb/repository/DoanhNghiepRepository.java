package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoanhNghiepRepository extends JpaRepository<DoanhNghiep, String> {

    List<DoanhNghiep> findByTrangThai(TrangThaiDoanhNghiep trangThai);

    List<DoanhNghiep> findByTenDoanhNghiepContainingIgnoreCase(String keyword);

    boolean existsByEmail(String email);

    boolean existsByEmailAndMaDoanhNghiepNot(String email, String maDoanhNghiep);

    long countByTrangThai(TrangThaiDoanhNghiep trangThai);

    /**
     * Search co filter trang thai + keyword (ten, linh vuc, nguoi dai dien, email).
     * Khong can @EntityGraph vi entity khong co collection LAZY can load.
     */
    @Query("SELECT d FROM DoanhNghiep d WHERE " +
           "(:keyword IS NULL OR LOWER(d.tenDoanhNghiep) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(d.linhVuc) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(d.nguoiDaiDien) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:trangThai IS NULL OR d.trangThai = :trangThai)")
    Page<DoanhNghiep> searchDoanhNghiep(@Param("keyword") String keyword,
                                        @Param("trangThai") TrangThaiDoanhNghiep trangThai,
                                        Pageable pageable);
}
