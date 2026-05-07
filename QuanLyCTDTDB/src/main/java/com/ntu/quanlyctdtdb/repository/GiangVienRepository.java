package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.enums.LoaiGiangVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiangVienRepository extends JpaRepository<GiangVien, String> {
    Optional<GiangVien> findByNguoiDung_MaNguoiDung(String maNguoiDung);
    List<GiangVien> findByLoaiGiangVien(LoaiGiangVien loai);

    /**
     * Fetch GV + NguoiDung (trang thai active) cho dropdown/list view.
     * Can LEFT JOIN FETCH thay vi JOIN FETCH de gv khong co NguoiDung
     * (edge case du lieu loi) van hien thi.
     * Dung khi template render gv.hoTen / gv.email (open-in-view=false).
     */
    @Query("""
        SELECT gv FROM GiangVien gv
        LEFT JOIN FETCH gv.nguoiDung nd
        WHERE nd.trangThaiTK = true
        ORDER BY nd.hoTen
        """)
    List<GiangVien> findAllActive();

    /** Tat ca GV (ke ca inactive) co fetch NguoiDung cho dropdown/list view. */
    @Query("""
        SELECT gv FROM GiangVien gv
        LEFT JOIN FETCH gv.nguoiDung nd
        ORDER BY COALESCE(nd.hoTen, gv.maGV)
        """)
    List<GiangVien> findAllFetchNguoiDung();
}
