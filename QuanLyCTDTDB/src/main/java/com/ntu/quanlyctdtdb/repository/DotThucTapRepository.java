package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.DotThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;

import java.util.List;

@Repository
public interface DotThucTapRepository extends JpaRepository<DotThucTap, Integer> {

    /**
     * Lay dot thuc tap theo hoc ky
     */
    List<DotThucTap> findByHocKy_MaHocKy(String maHocKy);

    List<DotThucTap> findByTrangThai(TrangThaiDotTT trangThai);

    List<DotThucTap> findByHocKy_MaHocKyAndTrangThai(String maHocKy, TrangThaiDotTT trangThai);

    /**
     * Dot thuc tap do nguoi nay tao
     */
    List<DotThucTap> findByNguoiTao_MaNguoiDung(String maNguoiTao);

    List<DotThucTap> findAllByOrderByMaDotTTDesc();
}
