package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.SinhVien;
import com.ntu.quanlyctdtdb.enums.TrangThaiSinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, String> {
    Optional<SinhVien> findByNguoiDung_MaNguoiDung(String maNguoiDung);
    List<SinhVien> findByLopHanhChinh_MaLopHC(String maLopHC);
    List<SinhVien> findByTrangThaiSV(TrangThaiSinhVien trangThai);
    long countByLopHanhChinh_MaLopHC(String maLopHC);

    /**
     * Lay SV cua lop hanh chinh theo trang thai cu the.
     * Dung cho Auto-add SV vao DotKienTap (docs/02 §3.7 + WF-07.1 BUOC 2).
     */
    @Query("""
        SELECT sv FROM SinhVien sv
        JOIN FETCH sv.nguoiDung
        WHERE sv.lopHanhChinh.maLopHC = :maLopHC
          AND sv.trangThaiSV = :trangThai
        ORDER BY sv.maSV
        """)
    List<SinhVien> findByLopAndTrangThai(@Param("maLopHC") String maLopHC,
                                         @Param("trangThai") TrangThaiSinhVien trangThai);

    @Query("""
        SELECT sv FROM SinhVien sv
        JOIN sv.lopHanhChinh lhc
        WHERE lhc.chuongTrinhDaoTao.maCTDT = :maCTDT
        AND sv.trangThaiSV = 'DangHoc'
        ORDER BY sv.nguoiDung.hoTen
        """)
    List<SinhVien> findDangHocByMaCTDT(String maCTDT);
}
