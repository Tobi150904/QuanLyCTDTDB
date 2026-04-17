package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.entity.TaiLieuMonHoc;
import com.ntu.quanlyctdtdb.enums.LoaiTaiLieu;
import com.ntu.quanlyctdtdb.enums.TrangThaiTaiLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaiLieuMonHocRepository extends JpaRepository<TaiLieuMonHoc, Integer> {

    @Query("SELECT t FROM TaiLieuMonHoc t WHERE t.lopHocPhan.id = :id")
    List<TaiLieuMonHoc> findByLopHocPhanId(@Param("id") LopHocPhanId id);

    @Query("SELECT t FROM TaiLieuMonHoc t WHERE t.lopHocPhan.id = :id AND t.loai = :loai")
    Optional<TaiLieuMonHoc> findByLopHocPhanIdAndLoai(@Param("id") LopHocPhanId id, @Param("loai") LoaiTaiLieu loai);

    List<TaiLieuMonHoc> findByTrangThai(TrangThaiTaiLieu trangThai);

    @Query("SELECT t FROM TaiLieuMonHoc t WHERE t.lopHocPhan.hocPhan.chuNhiemHP.maNguoiDung = :maCNHP AND t.trangThai = :trangThai")
    List<TaiLieuMonHoc> findByCNHPAndTrangThai(@Param("maCNHP") String maCNHP, @Param("trangThai") TrangThaiTaiLieu trangThai);
}