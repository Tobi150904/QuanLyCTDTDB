package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.BcnThanhVienDTO;
import com.ntu.quanlyctdtdb.entity.BcnThanhVien;
import com.ntu.quanlyctdtdb.enums.ChucDanhBCN;

import java.util.List;
import java.util.Optional;

/**
 * Service quan ly thanh vien Ban Chu Nhiem (BCN) CTDT.
 * Mot CTDT co nhieu thanh vien BCN voi 3 chuc danh: ChuNhiem, ThuKy, UyVien
 * (xem enum {@link ChucDanhBCN}). Xem docs/02 §3.4 va
 * docs/03_WORKFLOW.md BUOC 2 cua WF-01.
 */
public interface BcnThanhVienService {

    /** Lay danh sach BCN cua 1 CTDT (fetch GV + NguoiDung de hien thi hoTen). */
    List<BcnThanhVien> findByCtdt(String maCTDT);

    /** Tim chu nhiem hien tai cua CTDT (neu co). */
    Optional<BcnThanhVien> findChuNhiem(String maCTDT);

    /**
     * Them thanh vien BCN. Reject neu:
     * <ul>
     *   <li>GV da la thanh vien BCN voi cung chuc danh nay — duplicate PK.</li>
     *   <li>Chuc danh {@code ChuNhiem} va CTDT da co chu nhiem khac.</li>
     * </ul>
     */
    BcnThanhVien themThanhVien(String maCTDT, BcnThanhVienDTO dto);

    /** Xoa 1 thanh vien BCN theo composite key (MaCTDT + MaGV + ChucDanh). */
    void xoaThanhVien(String maCTDT, String maGV, ChucDanhBCN chucDanh);

    /** True neu GV hien la chu nhiem cua CTDT — dung cho authorization check. */
    boolean laChuNhiem(String maCTDT, String maGV);
}
