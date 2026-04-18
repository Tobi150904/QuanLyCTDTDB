package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.enums.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.LinkedHashMap;
import java.util.Map;

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

        // Hoc ky hien tai
        hocKyRepo.findByTrangThai(TrangThaiHocKy.DangDienRa)
                .ifPresent(hk -> model.addAttribute("hocKyHienTai", hk));

        model.addAttribute("thongKe", thongKe);
        model.addAttribute("currentUser", currentUser);
        return "dashboard/dashboard";
    }
}
