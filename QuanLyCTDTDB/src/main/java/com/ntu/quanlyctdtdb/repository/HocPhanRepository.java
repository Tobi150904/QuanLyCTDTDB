package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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

    /**
     * Detail view: fetch ChuNhiemHP + NguoiDung cua no de template
     * hoc-phan/chi-tiet hien thi duoc hoTen chu nhiem (open-in-view=false).
     */
    @Query("""
        SELECT hp FROM HocPhan hp
        LEFT JOIN FETCH hp.chuNhiemHP gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE hp.maHocPhan = :ma
        """)
    Optional<HocPhan> findByIdFetch(String ma);

    /**
     * Paged search cho list page (Phase 2 - server-side Pageable + Sort).
     * - keyword nullable: neu null thi tra ve tat ca.
     * - loaiHocPhan nullable: neu null thi khong filter theo loai.
     * - trangThai nullable: neu null thi khong filter theo trang thai.
     * Note: COUNT query rieng (tang hieu nang khi datasize lon).
     */
    @Query(value = """
        SELECT hp FROM HocPhan hp
        LEFT JOIN FETCH hp.chuNhiemHP gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE (:kw IS NULL
               OR LOWER(hp.tenHocPhan) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(hp.maHocPhan)  LIKE LOWER(CONCAT('%', :kw, '%')))
          AND (:loai     IS NULL OR hp.loaiHocPhan = :loai)
          AND (:trangThai IS NULL OR hp.trangThai  = :trangThai)
        """,
        countQuery = """
        SELECT COUNT(hp) FROM HocPhan hp
        WHERE (:kw IS NULL
               OR LOWER(hp.tenHocPhan) LIKE LOWER(CONCAT('%', :kw, '%'))
               OR LOWER(hp.maHocPhan)  LIKE LOWER(CONCAT('%', :kw, '%')))
          AND (:loai     IS NULL OR hp.loaiHocPhan = :loai)
          AND (:trangThai IS NULL OR hp.trangThai  = :trangThai)
        """)
    Page<HocPhan> searchPaged(@Param("kw") String kw,
                              @Param("loai") LoaiHocPhan loai,
                              @Param("trangThai") TrangThaiHocPhan trangThai,
                              Pageable pageable);

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
