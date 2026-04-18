package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChuongTrinhDaoTaoRepository extends JpaRepository<ChuongTrinhDaoTao, String> {
    List<ChuongTrinhDaoTao> findByTrangThai(TrangThaiCTDT trangThai);
    List<ChuongTrinhDaoTao> findByNguoiTao_MaNguoiDung(String maNguoiDung);

    long countByTrangThai(TrangThaiCTDT trangThai);

    @Query("SELECT c FROM ChuongTrinhDaoTao c WHERE c.trangThai = 'DaDuyet' ORDER BY c.khoa DESC")
    List<ChuongTrinhDaoTao> findAllDaDuyet();

    List<ChuongTrinhDaoTao> findByKhoa(String khoa);
}
