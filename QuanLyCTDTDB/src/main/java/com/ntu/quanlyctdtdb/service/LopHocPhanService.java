package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;

import java.util.List;

public interface LopHocPhanService {

    /**
     * Tao hang loat LopHocPhan cho 1 CTDT trong 1 HocKy.
     * Loc theo {@code hocKyThu} khop voi tien trinh CTDT va doc so lop tu
     * {@code KeHoachMoLop} (fallback {@code CTDT_HocPhan.SoLopDuKien}).
     * @return so LopHocPhan duoc tao moi (lop da ton tai duoc bo qua)
     */
    int taoLopHocPhanChoCTDT(String maCTDT, String maHocKy);

    /** Phan cong giang vien cho lop HP. Soft-check: GV ngoai doi ngu van duoc phan cong nhung log WARN. */
    LopHocPhan phanCongGiangVien(LopHocPhanId id, String maGV);

    /** Kiem tra nhanh GV co thuoc doi ngu giang vien cua HP khong. */
    boolean gvThuocDoiNguHocPhan(String maHocPhan, String maGV);

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
