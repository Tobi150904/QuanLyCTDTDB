package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.HocKyNamHocDTO;
import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;

import java.util.List;
import java.util.Optional;

public interface HocKyNamHocService {

    List<HocKyNamHoc> findAll();

    HocKyNamHoc findById(String maHocKy);

    /** Tim hoc ky hien dang dien ra (0 hoac 1 hoc ky). */
    Optional<HocKyNamHoc> findDangDienRa();

    HocKyNamHoc create(HocKyNamHocDTO dto);

    HocKyNamHoc update(String maHocKy, HocKyNamHocDTO dto);

    /**
     * Doi trang thai hoc ky. Neu set sang {@link TrangThaiHocKy#DangDienRa},
     * hoc ky khac dang {@code DangDienRa} se tu dong chuyen sang {@code DaKetThuc}.
     */
    HocKyNamHoc setTrangThai(String maHocKy, TrangThaiHocKy trangThai);

    void delete(String maHocKy);
}
