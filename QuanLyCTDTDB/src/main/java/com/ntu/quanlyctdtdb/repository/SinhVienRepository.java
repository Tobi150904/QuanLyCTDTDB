package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.SinhVien;
import com.ntu.quanlyctdtdb.enums.TrangThaiSinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, String> {
    Optional<SinhVien> findByNguoiDung_MaNguoiDung(String maNguoiDung);
    List<SinhVien> findByLopHanhChinh_MaLopHC(String maLopHC);
    List<SinhVien> findByTrangThaiSV(TrangThaiSinhVien trangThai);
    long countByLopHanhChinh_MaLopHC(String maLopHC);

    @Query("""
        SELECT sv FROM SinhVien sv
        JOIN sv.lopHanhChinh lhc
        WHERE lhc.chuongTrinhDaoTao.maCTDT = :maCTDT
        AND sv.trangThaiSV = 'DangHoc'
        ORDER BY sv.nguoiDung.hoTen
        """)
    List<SinhVien> findDangHocByMaCTDT(String maCTDT);
}
