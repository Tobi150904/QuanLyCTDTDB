package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DotKienTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DotKienTapRepository extends JpaRepository<DotKienTap, Integer> {
    List<DotKienTap> findByLopHanhChinh_MaLopHC(String maLopHC);
    List<DotKienTap> findByHocKy_MaHocKy(String maHocKy);
    List<DotKienTap> findByTrangThai(TrangThaiDotKT trangThai);
    List<DotKienTap> findByDoanhNghiep_MaDoanhNghiep(String maDoanhNghiep);

    long countByTrangThaiIn(java.util.List<TrangThaiDotKT> trangThais);

    @Query("SELECT d FROM DotKienTap d WHERE d.trangThai IN ('ChuanBi','ChoDuyet') ORDER BY d.createdAt DESC")
    List<DotKienTap> findPendingDots();

    /**
     * Phase 4 - kien-tap UI: list page can render lop, hocKy, gv, dn
     * trong khi spring.jpa.open-in-view=false. Tat ca @ManyToOne deu LAZY,
     * nen phai JOIN FETCH truoc khi tx dong, neu khong se LazyInitException.
     */
    @Query("SELECT DISTINCT d FROM DotKienTap d "
         + "LEFT JOIN FETCH d.lopHanhChinh lhc "
         + "LEFT JOIN FETCH d.hocKy "
         + "LEFT JOIN FETCH d.gvPhuTrach gv "
         + "LEFT JOIN FETCH gv.nguoiDung "
         + "LEFT JOIN FETCH d.doanhNghiep "
         + "ORDER BY d.createdAt DESC")
    List<DotKienTap> findAllFetchAll();

    /**
     * Phase 4 - kien-tap/chi-tiet: ngoai cac quan he o list, can them
     * lhc.chuongTrinhDaoTao + nguoiTao + nguoiDuyet (audit panel).
     */
    @Query("SELECT d FROM DotKienTap d "
         + "LEFT JOIN FETCH d.lopHanhChinh lhc "
         + "LEFT JOIN FETCH lhc.chuongTrinhDaoTao "
         + "LEFT JOIN FETCH d.hocKy "
         + "LEFT JOIN FETCH d.gvPhuTrach gv "
         + "LEFT JOIN FETCH gv.nguoiDung "
         + "LEFT JOIN FETCH d.doanhNghiep "
         + "LEFT JOIN FETCH d.nguoiTao "
         + "LEFT JOIN FETCH d.nguoiDuyet "
         + "WHERE d.maDotKT = :id")
    Optional<DotKienTap> findByIdFetchAll(@Param("id") Integer id);
}
