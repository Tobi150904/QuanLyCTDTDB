package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.LopHocPhanDTO;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.enums.TrangThaiLopHP;

import java.util.List;

public interface LopHocPhanService {

    List<LopHocPhan> findAll(String maHocKy, String maHocPhan, String maGiangVien);
    LopHocPhan findById(LopHocPhanId id);
    LopHocPhan create(LopHocPhanDTO dto);
    boolean ganGiangVien(LopHocPhanId id, String maGiangVien);
    void huyGanGiangVien(LopHocPhanId id);
    void doiTrangThai(LopHocPhanId id, TrangThaiLopHP trangThaiMoi);
    List<LopHocPhan> findChuaCoGiangVien(String maHocKy);
    List<LopHocPhan> findByGiangVien(String maGiangVien, String maHocKy);
}