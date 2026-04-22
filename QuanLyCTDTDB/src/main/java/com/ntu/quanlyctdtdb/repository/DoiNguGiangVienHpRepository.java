package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHp;
import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHpId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoiNguGiangVienHpRepository extends JpaRepository<DoiNguGiangVienHp, DoiNguGiangVienHpId> {
    List<DoiNguGiangVienHp> findByHocPhan_MaHocPhan(String maHocPhan);
    List<DoiNguGiangVienHp> findByGiangVien_MaGVAndTrangThai(String maGV, Boolean trangThai);

    /**
     * Danh sach GV thuoc doi ngu cua 1 HP, fetch ca GV+NguoiDung de template
     * hien thi ten day du (tranh LazyInit khi open-in-view=false).
     */
    @Query("""
        SELECT dn FROM DoiNguGiangVienHp dn
        LEFT JOIN FETCH dn.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE dn.id.maHocPhan = :maHocPhan
        ORDER BY dn.trangThai DESC, gv.maGV
        """)
    List<DoiNguGiangVienHp> findByHocPhanFetchGv(String maHocPhan);

    /**
     * Check nhanh GV co trong doi ngu HP (bat ke trang thai).
     * Dung cho soft-check trong phanCongGiangVien.
     */
    @Query("""
        SELECT COUNT(dn) > 0 FROM DoiNguGiangVienHp dn
        WHERE dn.id.maHocPhan = :maHocPhan
        AND dn.id.maGiangVien = :maGV
        AND dn.trangThai = true
        """)
    boolean existsByIdMaHocPhanAndIdMaGV(String maHocPhan, String maGV);
}
