package com.ntu.quanlyctdtdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ntu.quanlyctdtdb.dto.LopHocPhanDTO;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiLopHP;

import java.util.List;

/**
 * Contract cho LopHocPhanService.
 * Impl: LopHocPhanServiceImpl.java
 */
public interface LopHocPhanService {

    Page<LopHocPhan> findAll(String maHocKy, String maHocPhan,
                             String maGiangVien, Pageable pageable);

    LopHocPhan findById(String maLopHP);

    LopHocPhan create(LopHocPhanDTO dto);

    /**
     * Gan giang vien cho lop hoc phan.
     * Validate: GV phai thuoc DoiNgu active cua HocPhan.
     * Neu GV KHONG thuoc doi ngu: van cho gan nhung WARNING trong log + hien thi UI.
     * (Theo workflow: "hien canh bao nhung van cho gan" - doc checklist Phase 3)
     *
     * @return true neu GV thuoc doi ngu, false neu khong thuoc (warn)
     */
    boolean ganGiangVien(String maLopHP, String maGiangVien);

    /**
     * Huy gan giang vien khoi lop hoc phan (set MaGiangVien = null).
     */
    void huyGanGiangVien(String maLopHP);

    /**
     * Chuyen doi trang thai lop hoc phan.
     * Thu tu hop le: DangMo -> DaDong | DaHuy
     */
    void doiTrangThai(String maLopHP, TrangThaiLopHP trangThaiMoi);

    /**
     * Lay danh sach lop chua co giang vien (cho BCN thay de gan GV).
     */
    List<LopHocPhan> findChuaCoGiangVien(String maHocKy);

    /**
     * Lay danh sach lop duoc giao cho mot giang vien (cho GV xem lich day).
     */
    List<LopHocPhan> findByGiangVien(String maGiangVien, String maHocKy);
}
