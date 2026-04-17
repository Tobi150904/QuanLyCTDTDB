package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.DotKienTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;

import java.util.List;

@Repository
public interface DotKienTapRepository extends JpaRepository<DotKienTap, Integer> {

    /**
     * Lay dot kien tap theo lop hanh chinh
     */
    List<DotKienTap> findByLopHanhChinh_MaLopHC(String maLopHC);

    List<DotKienTap> findByTrangThai(TrangThaiDotKT trangThai);

    List<DotKienTap> findByLopHanhChinh_MaLopHCAndTrangThai(
            String maLopHC, TrangThaiDotKT trangThai);

    /**
     * Dot kien tap do nguoi nay tao
     */
    List<DotKienTap> findByNguoiTao_MaNguoiDung(String maNguoiTao);

    List<DotKienTap> findAllByOrderByMaDotKTDesc();
}
