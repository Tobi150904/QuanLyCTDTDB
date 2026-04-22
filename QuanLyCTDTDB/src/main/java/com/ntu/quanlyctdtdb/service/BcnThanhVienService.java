package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.BcnThanhVienDTO;
import com.ntu.quanlyctdtdb.entity.BcnThanhVien;
import com.ntu.quanlyctdtdb.enums.ChucDanhBCN;

import java.util.List;
import java.util.Optional;

public interface BcnThanhVienService {
    /** Lay danh sach thanh vien BCN cua 1 CTDT. */
    List<BcnThanhVien> findByCtdt(String maCTDT);

    /** Tim Chu nhiem CTDT (co the null). */
    Optional<BcnThanhVien> findChuNhiem(String maCTDT);

    /** Them thanh vien vao BCN cua CTDT. */
    BcnThanhVien themThanhVien(String maCTDT, BcnThanhVienDTO dto);

    /** Xoa thanh vien khoi BCN. */
    void xoaThanhVien(String maCTDT, String maGV, ChucDanhBCN chucDanh);
}
