package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHP;
import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHPId;

import java.util.List;

@Repository
public interface DoiNguGiangVienHPRepository
        extends JpaRepository<DoiNguGiangVienHP, DoiNguGiangVienHPId> {

    /**
     * Rule 1: Kiem tra GV co thuoc doi ngu HP khong (va dang hoat dong)
     * Dung khi BCN gan GV vao LopHocPhan
     */
    boolean existsById_MaHocPhanAndId_MaGiangVienAndTrangThai(
            String maHocPhan, String maGiangVien, Boolean trangThai);

    /**
     * Lay danh sach doi ngu cua mot hoc phan
     */
    List<DoiNguGiangVienHP> findByHocPhan_MaHocPhan(String maHocPhan);

    /**
     * Lay danh sach HP ma mot GV tham gia doi ngu
     */
    List<DoiNguGiangVienHP> findByGiangVien_MaNguoiDung(String maGiangVien);

    /**
     * Lay doi ngu dang hoat dong cua mot HP
     */
    List<DoiNguGiangVienHP> findByHocPhan_MaHocPhanAndTrangThai(
            String maHocPhan, Boolean trangThai);
}
