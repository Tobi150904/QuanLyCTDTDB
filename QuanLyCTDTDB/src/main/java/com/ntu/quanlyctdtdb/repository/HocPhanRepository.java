package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HocPhanRepository extends JpaRepository<HocPhan, String> {
    List<HocPhan> findByTrangThai(TrangThaiHocPhan trangThai);
    List<HocPhan> findByLoaiHocPhan(LoaiHocPhan loai);
    List<HocPhan> findByChuNhiemHP_MaGV(String maGV);
    List<HocPhan> findByTenHocPhanContainingIgnoreCase(String keyword);

    @Query("SELECT hp FROM HocPhan hp WHERE hp.trangThai = 'DaDuyet' ORDER BY hp.tenHocPhan")
    List<HocPhan> findAllDaDuyet();

    // Lay cac HP chua co trong 1 CTDT
    @Query("""
        SELECT hp FROM HocPhan hp
        WHERE hp.trangThai = 'DaDuyet'
        AND hp.maHocPhan NOT IN (
            SELECT ch.id.maHocPhan FROM CtdtHocPhan ch WHERE ch.id.maCTDT = :maCTDT
        )
        ORDER BY hp.tenHocPhan
        """)
    List<HocPhan> findHocPhanChuaCoTrongCTDT(String maCTDT);
}
