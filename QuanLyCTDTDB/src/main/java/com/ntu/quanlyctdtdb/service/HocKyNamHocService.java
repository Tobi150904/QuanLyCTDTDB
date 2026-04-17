package com.ntu.quanlyctdtdb.service;

import java.util.List;
import java.util.Optional;

import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;

/**
 * Contract cho HocKyNamHocService.
 * Dung boi nhieu service khac de lay HocKy hien tai.
 */
public interface HocKyNamHocService {

    List<HocKyNamHoc> findAll();

    HocKyNamHoc findById(String maHocKy);

    HocKyNamHoc create(HocKyNamHoc entity);

    HocKyNamHoc update(String maHocKy, HocKyNamHoc entity);

    /**
     * Lay hoc ky dang dien ra (TrangThai = DangDienRa).
     * Tra ve Optional.empty() neu khong co hoc ky nao dang dien ra.
     * Dung boi autoCreateLopHocPhan(), isNopQuaHanDeCuong()...
     */
    Optional<HocKyNamHoc> findHocKyHienTai();

    List<HocKyNamHoc> findByTrangThai(TrangThaiHocKy trangThai);
}
