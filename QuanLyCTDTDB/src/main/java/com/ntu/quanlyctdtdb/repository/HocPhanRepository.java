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

    long countByTrangThai(TrangThaiHocPhan trangThai);

    @Query("SELECT hp FROM HocPhan hp WHERE hp.trangThai = 'DaDuyet' ORDER BY hp.tenHocPhan")
    List<HocPhan> findAllDaDuyet();

    /**
     * List view - fetch eagerly ChuNhiemHP + NguoiDung de template Thymeleaf
     * co the hien thi hoTen GV ma khong bi LazyInitializationException
     * (open-in-view=false).
     */
    @Query("""
        SELECT DISTINCT hp FROM HocPhan hp
        LEFT JOIN FETCH hp.chuNhiemHP gv
        LEFT JOIN FETCH gv.nguoiDung
        ORDER BY hp.maHocPhan
        """)
    List<HocPhan> findAllFetchChuNhiem();

    @Query("""
        SELECT DISTINCT hp FROM HocPhan hp
        LEFT JOIN FETCH hp.chuNhiemHP gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE LOWER(hp.tenHocPhan) LIKE LOWER(CONCAT('%', :kw, '%'))
           OR LOWER(hp.maHocPhan)  LIKE LOWER(CONCAT('%', :kw, '%'))
        ORDER BY hp.maHocPhan
        """)
    List<HocPhan> searchFetchChuNhiem(String kw);

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
