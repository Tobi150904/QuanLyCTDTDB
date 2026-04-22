package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;

import java.util.List;
import java.util.Map;

public interface LopHocPhanService {

    /**
     * Tao cac lop HP tu dong cho mot CTDT trong mot hoc ky.
     * Chi tao cho cac HP co {@code CtdtHocPhan.hocKyThu} trung voi
     * so thu tu ky parse tu {@code maHocKy} (format {@code HKn-YYYY}).
     *
     * @param soLopOverride map {@code maHocPhan -> soLopMoMoi}. Neu rong
     *                      hoac thieu key, dung {@code CtdtHocPhan.soLopDuKien}.
     * @return so lop HP thuc su duoc tao moi.
     */
    int taoLopHocPhanChoCTDT(String maCTDT, String maHocKy, Map<String, Integer> soLopOverride);

    /** Giu lai signature cu de backward compatible — goi version co override voi map rong. */
    default void taoLopHocPhanChoCTDT(String maCTDT, String maHocKy) {
        taoLopHocPhanChoCTDT(maCTDT, maHocKy, Map.of());
    }

    /** Phan cong giang vien cho lop HP */
    LopHocPhan phanCongGiangVien(LopHocPhanId id, String maGV);

    /** Mo/Dong lop HP */
    LopHocPhan toggleTrangThai(LopHocPhanId id);

    /** Lay danh sach lop HP theo CTDT va hoc ky (lay full cua 1 ky cua CTDT). */
    List<LopHocPhan> findByCTDTAndHocKy(String maCTDT, String maHocKy);

    /**
     * Lay tat ca lop HP cua mot CTDT across nhieu ky — dung khi nguoi dung
     * chi loc theo CTDT ma khong chi dinh hoc ky.
     */
    List<LopHocPhan> findByCTDT(String maCTDT);

    /**
     * Lay tat ca lop HP trong mot hoc ky across CTDT — dung khi nguoi dung
     * chi loc theo hoc ky (view cap truong: TTDTXS/PDT).
     */
    List<LopHocPhan> findByHocKy(String maHocKy);

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
