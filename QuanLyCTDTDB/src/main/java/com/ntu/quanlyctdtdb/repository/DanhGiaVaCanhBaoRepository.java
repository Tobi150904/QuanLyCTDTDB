package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DanhGiaVaCanhBao;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.enums.LoaiNhanXet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaVaCanhBaoRepository extends JpaRepository<DanhGiaVaCanhBao, Integer> {

    @Query("SELECT d FROM DanhGiaVaCanhBao d WHERE d.lopHocPhan.id = :id AND d.loaiDanhGia = 'QuaTrinh'")
    List<DanhGiaVaCanhBao> findByLopHocPhanId(@Param("id") LopHocPhanId id);

    List<DanhGiaVaCanhBao> findBySinhVien_MaNguoiDung(String maSV);

    @Query("SELECT d FROM DanhGiaVaCanhBao d WHERE d.lopHocPhan.lopHanhChinh.maLopHC = :maLopHC AND d.daXuLy = false AND d.loaiDanhGia = 'QuaTrinh'")
    List<DanhGiaVaCanhBao> findCanhBaoChuaXuLyByLopHC(@Param("maLopHC") String maLopHC);

    @Query("SELECT COUNT(d) FROM DanhGiaVaCanhBao d WHERE d.lopHocPhan.lopHanhChinh.maLopHC = :maLopHC AND d.daXuLy = false AND d.loaiDanhGia = 'QuaTrinh'")
    long countCanhBaoChuaXuLyByLopHC(@Param("maLopHC") String maLopHC);

    @Query("SELECT d FROM DanhGiaVaCanhBao d WHERE d.lopHocPhan.lopHanhChinh.maLopHC = :maLopHC AND d.loaiNhanXet = :loai AND d.daXuLy = false AND d.loaiDanhGia = 'QuaTrinh'")
    List<DanhGiaVaCanhBao> findCanhBaoTieuCucChuaXuLy(@Param("maLopHC") String maLopHC, @Param("loai") LoaiNhanXet loai);
}