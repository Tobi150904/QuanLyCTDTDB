package com.ntu.quanlyctdtdb.service;

/**
 * Contract cho EmailService.
 * Dev: MockEmailServiceImpl (chi log, khong gui that)
 * Prod: SmtpEmailServiceImpl (Spring Mail)
 *
 * Chuyen doi: doi profile hoac dung @ConditionalOnProperty.
 */
public interface EmailService {

    /**
     * Gui email canh bao hoc vu den sinh vien.
     * Goi boi DanhGiaVaCanhBaoService khi LoaiNhanXet = TieuCuc.
     */
    void guiCanhBaoDenSV(String maSV, String noiDungNhanXet);

    /**
     * Gui email thong bao canh bao den co van hoc tap.
     * Goi boi DanhGiaVaCanhBaoService khi LoaiNhanXet = TieuCuc.
     */
    void guiCanhBaoDenCVHT(String maLopHC, String maSV, String noiDungNhanXet);

    /**
     * Gui email thong bao tai khoan moi cho nguoi dung (sau import Excel).
     * Noi dung: TenDangNhap, MatKhauTamThoi, link he thong.
     */
    void guiThongBaoTaiKhoanMoi(String email, String hoTen,
                                 String tenDangNhap, String matKhauTamThoi);

    /**
     * Gui email thong bao tai lieu bi tu choi (den GV).
     */
    void guiThongBaoTaiLieuTuChoi(String emailGV, String tenLopHP,
                                   String loaiTaiLieu, String lyDo);
}
