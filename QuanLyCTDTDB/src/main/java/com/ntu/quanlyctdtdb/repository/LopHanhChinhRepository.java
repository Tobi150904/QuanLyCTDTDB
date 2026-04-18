package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LopHanhChinhRepository extends JpaRepository<LopHanhChinh, String> {
    List<LopHanhChinh> findByChuongTrinhDaoTao_MaCTDT(String maCTDT);
    List<LopHanhChinh> findByCoVan_MaGV(String maGV);
    List<LopHanhChinh> findByKhoaHoc(String khoaHoc);
}
