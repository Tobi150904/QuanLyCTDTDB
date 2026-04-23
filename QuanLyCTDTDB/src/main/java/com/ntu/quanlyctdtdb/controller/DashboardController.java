package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.enums.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final NguoiDungRepository nguoiDungRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;
    private final HocPhanRepository hocPhanRepo;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final LopHocPhanRepository lopHocPhanRepo;
    private final DotKienTapRepository dotKienTapRepo;
    private final DotThucTapRepository dotThucTapRepo;
    private final HocKyNamHocRepository hocKyRepo;

    @GetMapping({"/", "/dashboard"})
    public String dashboard(@AuthenticationPrincipal CustomUserDetails currentUser,
                             Model model) {
        model.addAttribute("activeMenu", "dashboard");

        // --- Thong ke chung ---
        Map<String, Long> thongKe = new LinkedHashMap<>();
        thongKe.put("tongNguoiDung", nguoiDungRepo.count());
        thongKe.put("giangVien", nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.GiangVien));
        thongKe.put("sinhVien", nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.SinhVien));
        thongKe.put("doanhNghiep", (long) doanhNghiepRepo.findAll().size());
        thongKe.put("hocPhanDaDuyet", hocPhanRepo.countByTrangThai(TrangThaiHocPhan.DaDuyet));
        thongKe.put("hocPhanChoDuyet", hocPhanRepo.countByTrangThai(TrangThaiHocPhan.ChoDuyet));
        thongKe.put("ctdtDaDuyet", ctdtRepo.countByTrangThai(TrangThaiCTDT.DaDuyet));
        thongKe.put("lopHocPhanDangMo",
                lopHocPhanRepo.countByTrangThai(TrangThaiLopHocPhan.DangMo));
        thongKe.put("dotKienTapDangHoatDong",
                dotKienTapRepo.countByTrangThaiIn(
                        java.util.List.of(TrangThaiDotKT.ChuanBi, TrangThaiDotKT.ChoDuyet, TrangThaiDotKT.DaDuyet)));
        thongKe.put("dotThucTapDangDienRa",
                dotThucTapRepo.countByTrangThai(TrangThaiDotTT.DangThucHien));

        // Hoc ky hien tai. Theo nghiep vu CHI co 1 HK o trang thai DangDienRa tai
        // bat ky thoi diem nao (§ HocKyNamHocServiceImpl auto-close HK cu khi
        // kich hoat HK moi). Tuy nhien neu du lieu bi lech (nhap tay truc tiep
        // vao DB), `findByTrangThai(...)` se throw
        // IncorrectResultSizeDataAccessException va khien toan bo dashboard 500.
        // Bat defensive + log WARNING de ops co the fix du lieu, nhung khong
        // chet trang landing cho tat ca user.
        try {
            hocKyRepo.findByTrangThai(TrangThaiHocKy.DangDienRa)
                    .ifPresent(hk -> model.addAttribute("hocKyHienTai", hk));
        } catch (IncorrectResultSizeDataAccessException ex) {
            log.warn("[Dashboard] Phat hien >1 hoc ky DangDienRa. Lay ban ghi moi nhat. Msg={}",
                    ex.getMessage());
            hocKyRepo.findByTrangThaiNot(TrangThaiHocKy.DaKetThuc).stream()
                    .filter(hk -> hk.getTrangThai() == TrangThaiHocKy.DangDienRa)
                    .findFirst()
                    .ifPresent(hk -> model.addAttribute("hocKyHienTai", hk));
        }

        model.addAttribute("thongKe", thongKe);
        model.addAttribute("currentUser", currentUser);
        return "dashboard/dashboard";
    }
}
