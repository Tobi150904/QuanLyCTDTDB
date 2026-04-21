package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChuongTrinhDaoTaoRepository extends JpaRepository<ChuongTrinhDaoTao, String> {
    List<ChuongTrinhDaoTao> findByTrangThai(TrangThaiCTDT trangThai);
    List<ChuongTrinhDaoTao> findByNguoiTao_MaNguoiDung(String maNguoiDung);

    long countByTrangThai(TrangThaiCTDT trangThai);

    @Query("SELECT c FROM ChuongTrinhDaoTao c WHERE c.trangThai = 'DaDuyet' ORDER BY c.khoa DESC")
    List<ChuongTrinhDaoTao> findAllDaDuyet();

    List<ChuongTrinhDaoTao> findByKhoa(String khoa);

    /**
     * List view: fetch nguoiTao + danh sach CtdtHocPhan + HocPhan cho template
     * (open-in-view=false nen phai load trong service/transaction).
     * Dung LEFT JOIN FETCH voi DISTINCT de tranh N+1 va nhan ban entity cha.
     */
    @Query("""
        SELECT DISTINCT c FROM ChuongTrinhDaoTao c
        LEFT JOIN FETCH c.nguoiTao
        LEFT JOIN FETCH c.ctdtHocPhans ch
        LEFT JOIN FETCH ch.hocPhan
        ORDER BY c.maCTDT
        """)
    List<ChuongTrinhDaoTao> findAllFetchHocPhan();

    @Query("""
        SELECT c FROM ChuongTrinhDaoTao c
        LEFT JOIN FETCH c.nguoiTao
        LEFT JOIN FETCH c.ctdtHocPhans ch
        LEFT JOIN FETCH ch.hocPhan
        WHERE c.maCTDT = :ma
        """)
    Optional<ChuongTrinhDaoTao> findByIdFetchHocPhan(String ma);
}
