package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;

import java.util.List;

@Repository
public interface DoanhNghiepRepository extends JpaRepository<DoanhNghiep, String> {

    List<DoanhNghiep> findByTrangThai(TrangThaiDoanhNghiep trangThai);

    Page<DoanhNghiep> findByTenDoanhNghiepContainingIgnoreCase(String keyword, Pageable pageable);

    Page<DoanhNghiep> findByTrangThaiAndTenDoanhNghiepContainingIgnoreCase(
            TrangThaiDoanhNghiep trangThai, String keyword, Pageable pageable);

    boolean existsByEmailDN(String emailDN);

    List<DoanhNghiep> findAllByOrderByTenDoanhNghiepAsc();
}
