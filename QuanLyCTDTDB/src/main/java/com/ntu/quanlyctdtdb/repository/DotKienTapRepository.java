package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DotKienTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DotKienTapRepository extends JpaRepository<DotKienTap, Integer> {
    List<DotKienTap> findByLopHanhChinh_MaLopHC(String maLopHC);
    List<DotKienTap> findByHocKyNamHoc_MaHocKy(String maHocKy);
    List<DotKienTap> findByTrangThai(TrangThaiDotKT trangThai);
    List<DotKienTap> findByDoanhNghiep_MaDoanhNghiep(String maDoanhNghiep);

    long countByTrangThaiIn(java.util.List<TrangThaiDotKT> trangThais);

    @Query("SELECT d FROM DotKienTap d WHERE d.trangThai IN ('ChuanBi','ChoDuyet') ORDER BY d.createdAt DESC")
    List<DotKienTap> findPendingDots();
}
