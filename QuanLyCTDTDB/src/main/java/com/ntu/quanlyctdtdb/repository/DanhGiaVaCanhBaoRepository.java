package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.DanhGiaVaCanhBao;
import com.ntu.quanlyctdtdb.enums.LoaiNhanXet;

import java.util.List;

@Repository
public interface DanhGiaVaCanhBaoRepository extends JpaRepository<DanhGiaVaCanhBao, Integer> {

    /**
     * Lay tat ca nhan xet trong mot lop HP (GV, CNHP xem)
     */
    List<DanhGiaVaCanhBao> findByLopHocPhan_MaLopHP(String maLopHP);

    /**
     * Lay tat ca nhan xet cua mot SV
     */
    List<DanhGiaVaCanhBao> findBySinhVien_MaNguoiDung(String maSV);

    /**
     * CVHT xem canh bao chua xu ly cua lop minh phu trach (Rule 4)
     */
    List<DanhGiaVaCanhBao> findByLopHocPhan_LopHanhChinh_MaLopHCAndDaXuLyFalse(
            String maLopHC);

    /**
     * Badge count: Dem canh bao chua xu ly cho CVHT (hien thi tren menu)
     */
    long countByLopHocPhan_LopHanhChinh_MaLopHCAndDaXuLyFalse(String maLopHC);

    /**
     * Lay canh bao tieu cuc chua xu ly theo lop HC
     */
    List<DanhGiaVaCanhBao> findByLopHocPhan_LopHanhChinh_MaLopHCAndLoaiNhanXetAndDaXuLyFalse(
            String maLopHC, LoaiNhanXet loaiNhanXet);

    /**
     * Lay nhan xet GV da nhap trong lop HP
     */
    List<DanhGiaVaCanhBao> findByGiangVien_MaNguoiDungAndLopHocPhan_MaLopHP(
            String maGV, String maLopHP);
}
