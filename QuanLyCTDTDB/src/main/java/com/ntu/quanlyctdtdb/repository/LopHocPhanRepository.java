package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.enums.TrangThaiLopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, LopHocPhanId> {
    List<LopHocPhan> findById_MaCTDTAndId_MaHocKy(String maCTDT, String maHocKy);
    List<LopHocPhan> findById_MaHocKy(String maHocKy);
    List<LopHocPhan> findByTrangThai(TrangThaiLopHocPhan trangThai);
    List<LopHocPhan> findByGiangVien_MaGV(String maGV);

    long countByTrangThai(TrangThaiLopHocPhan trangThai);

    @Query("""
        SELECT lhp FROM LopHocPhan lhp
        WHERE lhp.id.maCTDT = :maCTDT
        AND lhp.id.maHocPhan = :maHocPhan
        AND lhp.id.maHocKy = :maHocKy
        ORDER BY lhp.id.maLopHocPhan
        """)
    List<LopHocPhan> findByCtdtHocPhanAndHocKy(String maCTDT, String maHocPhan, String maHocKy);

    // Lop khong co GV (can phan cong)
    @Query("SELECT lhp FROM LopHocPhan lhp WHERE lhp.giangVien IS NULL AND lhp.trangThai = 'DangMo'")
    List<LopHocPhan> findChuaPhanCongGiangVien();
}
