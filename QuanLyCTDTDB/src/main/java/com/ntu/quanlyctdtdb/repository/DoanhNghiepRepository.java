package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoanhNghiepRepository extends JpaRepository<DoanhNghiep, String> {
    List<DoanhNghiep> findByTrangThai(TrangThaiDoanhNghiep trangThai);
    List<DoanhNghiep> findByTenDoanhNghiepContainingIgnoreCase(String keyword);
    boolean existsByEmail(String email);
}
