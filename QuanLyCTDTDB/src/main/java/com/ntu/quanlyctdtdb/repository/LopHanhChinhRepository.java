package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LopHanhChinhRepository extends JpaRepository<LopHanhChinh, String> {
    List<LopHanhChinh> findByChuongTrinhDaoTao_MaCTDT(String maCTDT);
    List<LopHanhChinh> findByCoVan_MaGV(String maGV);
    List<LopHanhChinh> findByKhoaHoc(String khoaHoc);

    /**
     * List view: fetch CTDT + CoVan(GiangVien) + NguoiDung de template hien thi
     * hoTen CVHT, maCTDT ma khong bi LazyInitializationException
     * (open-in-view=false).
     */
    @Query("""
        SELECT DISTINCT l FROM LopHanhChinh l
        LEFT JOIN FETCH l.chuongTrinhDaoTao
        LEFT JOIN FETCH l.coVan gv
        LEFT JOIN FETCH gv.nguoiDung
        ORDER BY l.maLopHC
        """)
    List<LopHanhChinh> findAllFetch();

    /** Detail view: load toan bo association mot lan cho chi tiet lop. */
    @Query("""
        SELECT l FROM LopHanhChinh l
        LEFT JOIN FETCH l.chuongTrinhDaoTao
        LEFT JOIN FETCH l.coVan gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE l.maLopHC = :ma
        """)
    Optional<LopHanhChinh> findByIdFetch(String ma);
}
