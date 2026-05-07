package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.LopHanhChinhDTO;
import com.ntu.quanlyctdtdb.entity.LopHanhChinh;

import java.util.List;
import java.util.Map;

public interface LopHanhChinhService {

    List<LopHanhChinh> findAll();

    List<LopHanhChinh> search(String keyword, String maCTDT, String khoaHoc);

    List<LopHanhChinh> searchByCoVan(String keyword, String maCTDT,
            String khoaHoc, String maCoVan);
    
    LopHanhChinh findById(String maLopHC);

    LopHanhChinh create(LopHanhChinhDTO dto);

    LopHanhChinh update(String maLopHC, LopHanhChinhDTO dto);

    /** Phan cong CVHT (Co Van Hoc Tap) cho lop. */
    LopHanhChinh phanCongCoVan(String maLopHC, String maGV);

    void delete(String maLopHC);

    Map<String, Object> getThongKe();
    
    Map<String, Object> getThongKeByCoVan(String maCoVan);
}
