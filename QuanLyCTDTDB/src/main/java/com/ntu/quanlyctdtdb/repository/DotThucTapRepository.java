package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DotThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DotThucTapRepository extends JpaRepository<DotThucTap, Integer> {
    List<DotThucTap> findByCtdtHocPhan_Id_MaCTDT(String maCTDT);
    List<DotThucTap> findByHocKy_MaHocKy(String maHocKy);
    List<DotThucTap> findByTrangThai(TrangThaiDotTT trangThai);

    long countByTrangThai(TrangThaiDotTT trangThai);

    @Query("SELECT d FROM DotThucTap d WHERE d.trangThai IN ('ChuanBi','ChoDuyet') ORDER BY d.createdAt DESC")
    List<DotThucTap> findPendingDots();

    /**
     * Phase 4 - thuc-tap UI: list page need ctdtHocPhan -> hocPhan + ctdt + hocKy
     * eagerly because spring.jpa.open-in-view=false.
     */
    @Query("SELECT DISTINCT d FROM DotThucTap d "
         + "LEFT JOIN FETCH d.ctdtHocPhan ctdtHP "
         + "LEFT JOIN FETCH ctdtHP.hocPhan "
         + "LEFT JOIN FETCH ctdtHP.chuongTrinhDaoTao "
         + "LEFT JOIN FETCH d.hocKy "
         + "ORDER BY d.createdAt DESC")
    List<DotThucTap> findAllFetchAll();

    /**
     * Phase 4 - thuc-tap/chi-tiet: full graph (audit + business panel).
     */
    @Query("SELECT d FROM DotThucTap d "
         + "LEFT JOIN FETCH d.ctdtHocPhan ctdtHP "
         + "LEFT JOIN FETCH ctdtHP.hocPhan "
         + "LEFT JOIN FETCH ctdtHP.chuongTrinhDaoTao "
         + "LEFT JOIN FETCH d.hocKy "
         + "LEFT JOIN FETCH d.nguoiTao "
         + "LEFT JOIN FETCH d.nguoiDuyet "
         + "WHERE d.maDotTT = :id")
    Optional<DotThucTap> findByIdFetchAll(@Param("id") Integer id);
}
