package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, LopHocPhanId> {

    List<LopHocPhan> findById_MaHocPhanAndId_MaHocKy(String maHocPhan, String maHocKy);
    List<LopHocPhan> findById_MaHocKy(String maHocKy);
    List<LopHocPhan> findByGiangVien_MaNguoiDung(String maGiangVien);
    List<LopHocPhan> findById_MaLopHC(String maLopHC);

    @Query("SELECT l FROM LopHocPhan l WHERE l.id.maHocKy = :maHocKy AND l.giangVien IS NULL")
    List<LopHocPhan> findChuaCoGiangVien(@Param("maHocKy") String maHocKy);
}