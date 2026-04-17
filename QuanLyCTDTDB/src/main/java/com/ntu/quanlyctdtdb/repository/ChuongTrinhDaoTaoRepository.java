package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;

import java.util.List;

@Repository
public interface ChuongTrinhDaoTaoRepository extends JpaRepository<ChuongTrinhDaoTao, String> {

    List<ChuongTrinhDaoTao> findByTrangThai(TrangThaiCTDT trangThai);

    /**
     * Lay CTDT do mot BCN tao
     */
    List<ChuongTrinhDaoTao> findByNguoiTao(NguoiDung nguoiTao);

    List<ChuongTrinhDaoTao> findByNguoiTao_MaNguoiDung(String maNguoiTao);

    Page<ChuongTrinhDaoTao> findByTenCTDTContainingIgnoreCase(String keyword, Pageable pageable);

    Page<ChuongTrinhDaoTao> findByTrangThaiAndTenCTDTContainingIgnoreCase(
            TrangThaiCTDT trangThai, String keyword, Pageable pageable);

    List<ChuongTrinhDaoTao> findAllByOrderByNgayTaoDesc();
}
