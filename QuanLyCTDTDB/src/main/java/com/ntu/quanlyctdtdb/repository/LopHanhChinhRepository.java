package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.LopHanhChinh;

import java.util.List;

@Repository
public interface LopHanhChinhRepository extends JpaRepository<LopHanhChinh, String> {

    /**
     * Lay danh sach lop hanh chinh ma CVHT nay phu trach
     * Dung trong: CVHT xem canh bao SV
     */
    List<LopHanhChinh> findByMaCoVan(String maCVHT);

    List<LopHanhChinh> findByKhoa(String khoa);

    List<LopHanhChinh> findByNamNhapHoc(Integer namNhapHoc);

    List<LopHanhChinh> findAllByOrderByMaLopHCAsc();
}
