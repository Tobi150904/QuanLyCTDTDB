package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.HocKyNamHocDTO;
import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface HocKyNamHocService {

    List<HocKyNamHoc> findAll();

    HocKyNamHoc findById(String maHocKy);

    /** Tim hoc ky hien dang dien ra (0 hoac 1 hoc ky). */
    Optional<HocKyNamHoc> findDangDienRa();

    HocKyNamHoc create(HocKyNamHocDTO dto);

    HocKyNamHoc update(String maHocKy, HocKyNamHocDTO dto);

    /**
     * Doi trang thai hoc ky. Chi cho phep 1 hoc ky `DangDienRa` tai moi thoi diem.
     * Khong cho phep rollback tu `DaKetThuc` ve `ChuanBi` / `DangDienRa`.
     */
    HocKyNamHoc doiTrangThai(String maHocKy, TrangThaiHocKy trangThai);

    void delete(String maHocKy);

    /** Thong ke so luong HocKy theo tung trang thai. */
    Map<String, Object> getThongKe();
}
