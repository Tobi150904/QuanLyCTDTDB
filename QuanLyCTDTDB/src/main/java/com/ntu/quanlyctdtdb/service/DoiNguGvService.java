package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.DoiNguGvDTO;
import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHp;

import java.util.List;

/**
 * Quan ly doi ngu giang vien cua mot hoc phan (bang DoiNguGiangVienHP).
 * <p>Theo docs: doi ngu la danh sach GV du dieu kien day 1 HP — dung de
 * goi y/khuyen nghi khi phan cong lop HP, khong rang buoc chan cung.
 */
public interface DoiNguGvService {

    /** Lay danh sach doi ngu GV cua 1 HP (ke ca GV dang tam ngung). */
    List<DoiNguGiangVienHp> findByHocPhan(String maHocPhan);

    /**
     * Them GV vao doi ngu.
     * @throws com.ntu.quanlyctdtdb.exception.BusinessException
     *         neu GV da ton tai trong doi ngu cua HP nay.
     */
    void them(DoiNguGvDTO dto);

    /** Toggle trang thai (dang day / tam ngung). */
    void toggleTrangThai(String maHocPhan, String maGV);

    /** Xoa GV khoi doi ngu. */
    void xoa(String maHocPhan, String maGV);
}
