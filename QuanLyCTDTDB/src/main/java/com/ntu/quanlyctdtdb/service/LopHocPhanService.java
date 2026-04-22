package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;

import java.util.List;

public interface LopHocPhanService {

    /** Tao cac lop HP tu dong cho mot CTDT trong mot hoc ky */
    void taoLopHocPhanChoCTDT(String maCTDT, String maHocKy);

    /** Phan cong giang vien cho lop HP */
    LopHocPhan phanCongGiangVien(LopHocPhanId id, String maGV);

    /** Mo/Dong lop HP */
    LopHocPhan toggleTrangThai(LopHocPhanId id);

    /** Lay danh sach lop HP theo CTDT va hoc ky */
    List<LopHocPhan> findByCTDTAndHocKy(String maCTDT, String maHocKy);

    /** Lay danh sach cac lop chua co GV */
    List<LopHocPhan> findChuaPhanCongGV();

    /** Dang ky SV vao lop HP */
    DanhSachSvLopHocPhan dangKyLopHocPhan(LopHocPhanId lopId, String maSV);

    /** Canh bao SV (CVHT ghi nhan xet) */
    DanhSachSvLopHocPhan canhBaoSinhVien(LopHocPhanId lopId, String maSV,
                                           String nhanXet, String emailCVHT);

    /** Lay danh sach SV trong lop */
    List<DanhSachSvLopHocPhan> findSinhVienTrongLop(LopHocPhanId lopId);
}
