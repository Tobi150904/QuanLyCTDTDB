package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.CtdtHocPhanDTO;
import com.ntu.quanlyctdtdb.dto.ChuongTrinhDaoTaoDTO;
import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.entity.CtdtHocPhan;

import java.util.List;
import java.util.Map;

public interface ChuongTrinhDaoTaoService {

    List<ChuongTrinhDaoTao> findAll();

    ChuongTrinhDaoTao findById(String ma);

    /** PDT tao moi CTDT → trang thai ChuaDuyet */
    ChuongTrinhDaoTao create(ChuongTrinhDaoTaoDTO dto, String maNguoiDungTao);

    /** PDT cap nhat thong tin CTDT */
    ChuongTrinhDaoTao update(String ma, ChuongTrinhDaoTaoDTO dto);

    /** PDT gui cho duyet BanNhap → ChoDuyet */
    ChuongTrinhDaoTao guiChoDuyet(String ma);

    /** BGH/TTDTXS phe duyet CTDT */
    ChuongTrinhDaoTao pheduyet(String ma, String maNguoiDungDuyet);

    /** Them hoc phan vao CTDT */
    CtdtHocPhan themHocPhan(String maCTDT, CtdtHocPhanDTO dto);

    /** Xoa hoc phan khoi CTDT */
    void xoaHocPhan(String maCTDT, String maHocPhan);

    /** Lay danh sach HP chua co trong CTDT */
    List<com.ntu.quanlyctdtdb.entity.HocPhan> findHocPhanChuaThuoc(String maCTDT);

    /** Cap nhat duong dan File Word cua CTDT (goi sau khi da upload file thanh cong). */
    ChuongTrinhDaoTao updateFileWord(String maCTDT, String fileWordPath);

    /**
     * Phase 2 — thong ke nhanh cho stat-card row tren danh sach CTDT.
     * Key:
     *   tongCTDT : tong so CTDT
     *   daDuyet  : so CTDT da duyet
     *   choDuyet : so CTDT cho duyet
     *   banNhap  : so CTDT ban nhap
     */
    Map<String, Long> getThongKe();
}
