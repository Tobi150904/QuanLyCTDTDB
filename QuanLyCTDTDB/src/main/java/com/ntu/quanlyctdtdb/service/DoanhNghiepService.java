package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.DoanhNghiepDTO;
import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DoanhNghiepService {

    /** Tim kiem + phan trang theo keyword (ten / linh vuc / NDD / email) + trang thai. */
    Page<DoanhNghiep> search(String keyword, TrangThaiDoanhNghiep trangThai, Pageable pageable);

    DoanhNghiep findById(String ma);

    /** Dung cho select option tao DotKienTap, DotThucTap ... - chi DN DangHopTac. */
    List<DoanhNghiep> findAllDangHopTac();

    /** Tao moi. Neu dto.maDoanhNghiep null/blank se tu sinh. */
    DoanhNghiep create(DoanhNghiepDTO dto);

    DoanhNghiep update(String ma, DoanhNghiepDTO dto);

    /** Doi DangHopTac <-> TamNgung. */
    void toggleTrangThai(String ma);

    /**
     * Xoa cung — chi cho phep khi khong con ban ghi tham chieu o
     * DotKienTap hoac DanhSachThucTap. Nem BusinessException neu vi pham.
     */
    void delete(String ma);

    DoanhNghiep updateThongTinLienHe(String maDN,
            com.ntu.quanlyctdtdb.dto.DoanhNghiepCuaToiDTO dto);
    
    /** Sinh ma theo format DN001, DN002, ... dua tren count() + 1. */
    String sinhMaDoanhNghiep();

    /** Thong ke hien tren stat-card cua danh sach. */
    Map<String, Long> getThongKe();
}
