package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.BcnThanhVien;
import com.ntu.quanlyctdtdb.entity.BcnThanhVienId;
import com.ntu.quanlyctdtdb.enums.ChucDanhBCN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BcnThanhVienRepository extends JpaRepository<BcnThanhVien, BcnThanhVienId> {
    List<BcnThanhVien> findByChuongTrinhDaoTao_MaCTDT(String maCTDT);
    List<BcnThanhVien> findByGiangVien_MaGV(String maGV);

    /**
     * Danh sach thanh vien BCN cua 1 CTDT co fetch GV + NguoiDung
     * de template Thymeleaf render ten day du (tranh LazyInit khi
     * open-in-view=false).
     */
    @Query("""
        SELECT bcn FROM BcnThanhVien bcn
        LEFT JOIN FETCH bcn.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE bcn.id.maCTDT = :maCTDT
        ORDER BY
          CASE bcn.id.chucDanh
            WHEN com.ntu.quanlyctdtdb.enums.ChucDanhBCN.ChuNhiem THEN 1
            WHEN com.ntu.quanlyctdtdb.enums.ChucDanhBCN.ThuKy    THEN 2
            ELSE 3
          END,
          gv.maGV
        """)
    List<BcnThanhVien> findByCtdtFetchGv(String maCTDT);

    /** Tim Chu nhiem cua CTDT (neu co). */
    @Query("""
        SELECT bcn FROM BcnThanhVien bcn
        LEFT JOIN FETCH bcn.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE bcn.id.maCTDT = :maCTDT
        AND bcn.id.chucDanh = com.ntu.quanlyctdtdb.enums.ChucDanhBCN.ChuNhiem
        """)
    Optional<BcnThanhVien> findChuNhiemByCtdt(String maCTDT);

    /** Dem so luong cua mot chuc danh trong 1 CTDT (dung check rang buoc ChuNhiem duy nhat). */
    long countByIdMaCTDTAndIdChucDanh(String maCTDT, ChucDanhBCN chucDanh);
}
