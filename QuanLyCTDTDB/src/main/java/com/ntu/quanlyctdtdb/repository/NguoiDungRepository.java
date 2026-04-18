package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {

    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    Optional<NguoiDung> findByEmail(String email);

    boolean existsByTenDangNhap(String tenDangNhap);

    boolean existsByEmail(String email);

    boolean existsByEmailAndMaNguoiDungNot(String email, String maNguoiDung);

    List<NguoiDung> findByLoaiNguoiDung(LoaiNguoiDung loaiNguoiDung);

    @Query("SELECT n FROM NguoiDung n WHERE " +
           "(:keyword IS NULL OR LOWER(n.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(n.tenDangNhap) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(n.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:loai IS NULL OR n.loaiNguoiDung = :loai)")
    Page<NguoiDung> searchNguoiDung(@Param("keyword") String keyword,
                                    @Param("loai") LoaiNguoiDung loai,
                                    Pageable pageable);

    // Dem so luong theo loai
    long countByLoaiNguoiDung(LoaiNguoiDung loaiNguoiDung);
}
