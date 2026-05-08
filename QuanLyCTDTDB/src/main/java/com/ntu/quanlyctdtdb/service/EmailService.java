package com.ntu.quanlyctdtdb.service;

public interface EmailService {

    /** Gui email canh bao den CVHT khi SV bi danh dau canh bao */
    void guiCanhBaoSinhVien(String emailCVHT, String hoTenSV,
                             String tenHocPhan, String nhanXet);

    /** Thong bao GV ve lop HP duoc phan cong */
    void guiThongBaoPhanCongLop(String emailGV, String tenHocPhan,
                                  String maLopHP, String tenHocKy);

    /** Thong bao CNHP khi HP duoc phe duyet */
    void guiPheDuyetHocPhan(String emailCNHP, String maHocPhan,
                              String tenHocPhan, String ngayDuyet);

    /** Thong bao CNHP khi HP bi tu choi */
    void guiTuChoiHocPhan(String emailCNHP, String maHocPhan,
                           String tenHocPhan, String lyDo);
    
    /**
     * Thong bao TTDTXS khi CNHP gui Hoc Phan cho duyet (BanNhap -> ChoDuyet).
     * Goi tu HocPhanServiceImpl.guiChoDuyet() de giam lag duyet.
     */
    void guiThongBaoChoDuyetHocPhan(String emailTTDTXS, String maHocPhan,
                                     String tenHocPhan, String hoTenCNHP);

    /**
     * Thong bao TTDTXS khi CNHP/PDT gui Chuong Trinh Dao Tao cho duyet
     * (BanNhap -> ChoDuyet). Goi tu ChuongTrinhDaoTaoServiceImpl.guiChoDuyet().
     */
    void guiThongBaoChoDuyetCTDT(String emailTTDTXS, String maCTDT,
                                  String tenCTDT, String hoTenNguoiTao);
}
