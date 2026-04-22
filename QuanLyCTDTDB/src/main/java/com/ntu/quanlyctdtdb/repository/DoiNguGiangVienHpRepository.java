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
     * Fetch version: load GV + NguoiDung trong transaction — dung cho section
     * "Doi Ngu Giang Vien" trong hoc-phan/chi-tiet (open-in-view=false).
     */
    @Query("""
        SELECT d FROM DoiNguGiangVienHp d
        LEFT JOIN FETCH d.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE d.hocPhan.maHocPhan = :maHocPhan
        ORDER BY d.trangThai DESC, d.id.maGiangVien
        """)
    List<DoiNguGiangVienHp> findByHocPhanFetch(String maHocPhan);
}
