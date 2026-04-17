package com.ntu.quanlyctdtdb.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.ntu.quanlyctdtdb.service.EmailService;

/**
 * Mock implementation cua EmailService dung cho moi truong DEV / TEST.
 * Thay vi gui email that, chi ghi log ra console.
 *
 * Kich hoat khi:  spring.mail.enabled=false  (trong application.properties)
 * Hoac khi khong co SmtpEmailServiceImpl trong classpath.
 *
 * De chuyen sang SMTP that:
 *   1. Bat comment spring.mail.* trong application.properties
 *   2. Xoa @ConditionalOnProperty o day va SmtpEmailServiceImpl se duoc Spring chon thay
 */
@Service
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "false", matchIfMissing = true)
public class MockEmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(MockEmailServiceImpl.class);

    @Override
    public void guiCanhBaoDenSV(String maSV, String noiDungNhanXet) {
        log.info("[MOCK EMAIL] Gui canh bao den SV={} | NoiDung: {}", maSV, noiDungNhanXet);
    }

    @Override
    public void guiCanhBaoDenCVHT(String maLopHC, String maSV, String noiDungNhanXet) {
        log.info("[MOCK EMAIL] Gui canh bao den CVHT cua LopHC={} | SV={} | NoiDung: {}",
                maLopHC, maSV, noiDungNhanXet);
    }

    @Override
    public void guiThongBaoTaiKhoanMoi(String email, String hoTen,
                                        String tenDangNhap, String matKhauTamThoi) {
        log.info("[MOCK EMAIL] Tai khoan moi | Email={} | HoTen={} | TenDangNhap={} | MatKhau={}",
                email, hoTen, tenDangNhap, matKhauTamThoi);
    }

    @Override
    public void guiThongBaoTaiLieuTuChoi(String emailGV, String tenLopHP,
                                          String loaiTaiLieu, String lyDo) {
        log.info("[MOCK EMAIL] Tai lieu bi tu choi | GV={} | LopHP={} | Loai={} | LyDo={}",
                emailGV, tenLopHP, loaiTaiLieu, lyDo);
    }
}
