package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.TaiLieuMonHoc;
import com.ntu.quanlyctdtdb.enums.LoaiTaiLieu;
import com.ntu.quanlyctdtdb.enums.TrangThaiTaiLieu;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaiLieuMonHocRepository extends JpaRepository<TaiLieuMonHoc, Integer> {

    /**
     * Lay tat ca tai lieu cua mot lop HP
     */
    List<TaiLieuMonHoc> findByLopHocPhan_MaLopHP(String maLopHP);

    /**
     * Rule 6: Tim tai lieu cu de UPDATE (thay vi INSERT moi)
     * Dung khi GV nop lai sau khi bi tu choi
     */
    Optional<TaiLieuMonHoc> findByLopHocPhan_MaLopHPAndLoai(String maLopHP, LoaiTaiLieu loai);

    /**
     * Lay tai lieu theo trang thai (cho CNHP xem danh sach can duyet)
     */
    List<TaiLieuMonHoc> findByTrangThai(TrangThaiTaiLieu trangThai);

    /**
     * Lay tai lieu cua lop HP theo trang thai
     */
    List<TaiLieuMonHoc> findByLopHocPhan_MaLopHPAndTrangThai(
            String maLopHP, TrangThaiTaiLieu trangThai);

    /**
     * Lay tai lieu GV da nop (xem lich su nop)
     */
    List<TaiLieuMonHoc> findByNguoiNop_MaNguoiDung(String maGV);

    /**
     * Dem so tai lieu cho duyet trong lop HP cua CNHP
     */
    long countByLopHocPhan_HocPhan_ChuNhiemHP_MaNguoiDungAndTrangThai(
            String maCNHP, TrangThaiTaiLieu trangThai);
}
