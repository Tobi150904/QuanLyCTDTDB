package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * MockEmailServiceImpl — Dev mode only.
 * Chi log ra console, khong gui email that.
 * Khi deploy production: thay the bang SmtpEmailServiceImpl.
 */
@Slf4j
@Service
public class MockEmailServiceImpl implements EmailService {

    @Override
    public void guiCanhBaoSinhVien(String emailCVHT, String hoTenSV,
                                    String tenHocPhan, String nhanXet) {
        log.info("[EMAIL-MOCK] GuiCanhBao -> To: {} | SV: {} | HP: {} | NhanXet: {}",
                 emailCVHT, hoTenSV, tenHocPhan, nhanXet);
    }

    @Override
    public void guiThongBaoPhanCongLop(String emailGV, String tenHocPhan,
                                        String maLopHP, String tenHocKy) {
        log.info("[EMAIL-MOCK] PhanCongLop -> To: {} | HP: {} | Lop: {} | HocKy: {}",
                 emailGV, tenHocPhan, maLopHP, tenHocKy);
    }

    @Override
    public void guiPheDuyetHocPhan(String emailCNHP, String maHocPhan,
                                    String tenHocPhan, String ngayDuyet) {
        log.info("[EMAIL-MOCK] PheDuyetHP -> To: {} | {} - {} | NgayDuyet: {}",
                 emailCNHP, maHocPhan, tenHocPhan, ngayDuyet);
    }

    @Override
    public void guiTuChoiHocPhan(String emailCNHP, String maHocPhan,
                                  String tenHocPhan, String lyDo) {
        log.info("[EMAIL-MOCK] TuChoiHP -> To: {} | {} - {} | LyDo: {}",
                 emailCNHP, maHocPhan, tenHocPhan, lyDo);
    }
}
