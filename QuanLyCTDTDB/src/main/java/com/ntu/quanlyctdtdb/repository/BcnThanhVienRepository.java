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
     * Fetch version dung cho tab "Ban Chu Nhiem" trong ctdt/chi-tiet — load
     * GiangVien + NguoiDung mot lan de template truy cap {@code tv.giangVien.hoTen}
     * ma khong LazyInitializationException (open-in-view=false).
     */
    @Query("""
        SELECT tv FROM BcnThanhVien tv
        LEFT JOIN FETCH tv.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE tv.chuongTrinhDaoTao.maCTDT = :maCTDT
        ORDER BY tv.id.chucDanh, tv.id.maGV
        """)
    List<BcnThanhVien> findByCtdtFetch(String maCTDT);

    /**
     * Tim thanh vien voi chuc danh cu the — dung khi check "CTDT da co
     * Chu Nhiem" truoc khi them chu nhiem moi (rule: 1 CTDT chi 1 chu nhiem).
     */
    Optional<BcnThanhVien> findFirstByChuongTrinhDaoTao_MaCTDTAndId_ChucDanh(
            String maCTDT, ChucDanhBCN chucDanh);
}
