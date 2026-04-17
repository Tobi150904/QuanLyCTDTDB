package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiLopHP;

import java.util.List;
import java.util.Optional;

@Repository
public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, String> {

    /**
     * Lay danh sach lop HP theo hoc phan
     */
    List<LopHocPhan> findByHocPhan_MaHocPhan(String maHocPhan);

    /**
     * Lay danh sach lop HP do GV day
     */
    List<LopHocPhan> findByGiangVien_MaNguoiDung(String maGV);

    /**
     * Lay danh sach lop HP theo hoc ky
     */
    List<LopHocPhan> findByHocKy_MaHocKy(String maHocKy);

    /**
     * Lay lop HP theo hoc phan va hoc ky (kiem tra da co truoc khi auto-create)
     */
    Optional<LopHocPhan> findByHocPhan_MaHocPhanAndHocKy_MaHocKy(
            String maHocPhan, String maHocKy);

    /**
     * Lay cac lop HP chua co GV (sau khi CTDT duyet, cho BCN gan GV)
     */
    List<LopHocPhan> findByGiangVienIsNull();

    /**
     * Lay lop HP chua co GV theo HP
     */
    List<LopHocPhan> findByHocPhan_MaHocPhanAndGiangVienIsNull(String maHocPhan);

    Page<LopHocPhan> findByHocKy_MaHocKy(String maHocKy, Pageable pageable);

    List<LopHocPhan> findByHocPhan_MaHocPhanAndTrangThai(
            String maHocPhan, TrangThaiLopHP trangThai);

    /**
     * Lay lop HP theo lop hanh chinh
     */
    List<LopHocPhan> findByLopHanhChinh_MaLopHC(String maLopHC);

    /**
     * Lay lop HP cua GV trong hoc ky cu the
     */
    List<LopHocPhan> findByGiangVien_MaNguoiDungAndHocKy_MaHocKy(
            String maGV, String maHocKy);
}
