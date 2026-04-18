package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DotThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DotThucTapRepository extends JpaRepository<DotThucTap, Integer> {
    List<DotThucTap> findByCtdtHocPhan_Id_MaCTDT(String maCTDT);
    List<DotThucTap> findByHocKy_MaHocKy(String maHocKy);
    List<DotThucTap> findByTrangThai(TrangThaiDotTT trangThai);

    long countByTrangThai(TrangThaiDotTT trangThai);

    @Query("SELECT d FROM DotThucTap d WHERE d.trangThai IN ('ChuanBi','ChoDuyet') ORDER BY d.createdAt DESC")
    List<DotThucTap> findPendingDots();
}
