package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.service.LopHocPhanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/lop-hoc-phan")
@RequiredArgsConstructor
public class LopHocPhanController {

    private final LopHocPhanService lopHPService;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final GiangVienRepository giangVienRepo;
    private final HocPhanRepository hocPhanRepo;

    @ModelAttribute("activeMenu")
    public String activeMenu() { return "lop-hoc-phan"; }

    @GetMapping
    public String danhSach(@RequestParam(required = false) String maCTDT,
                            @RequestParam(required = false) String maHocKy,
                            Model model) {
        model.addAttribute("ctdtList", ctdtRepo.findAll());
        model.addAttribute("hocKyList", hocKyRepo.findAllByOrderByNgayBatDauDesc());
        model.addAttribute("maCTDT", maCTDT);
        model.addAttribute("maHocKy", maHocKy);

        if (maCTDT != null && !maCTDT.isBlank() && maHocKy != null && !maHocKy.isBlank()) {
            model.addAttribute("danhSach", lopHPService.findByCTDTAndHocKy(maCTDT, maHocKy));
        }
        model.addAttribute("chuaPhanCong", lopHPService.findChuaPhanCongGV());

        // Map maHocPhan -> HocPhan de template hien thi tenHocPhan
        // (LopHocPhan khong map truc tiep sang HocPhan qua @EmbeddedId).
        Map<String, HocPhan> hocPhanMap = hocPhanRepo.findAll().stream()
                .collect(Collectors.toMap(HocPhan::getMaHocPhan, Function.identity()));
        model.addAttribute("hocPhanMap", hocPhanMap);

        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        return "lop-hoc-phan/danh-sach";
    }

    /** Tao hang loat lop HP cho CTDT trong mot hoc ky */
    @PostMapping("/tao-hang-loat")
    public String taoHangLoat(@RequestParam String maCTDT,
                               @RequestParam String maHocKy,
                               RedirectAttributes ra) {
        try {
            int created = lopHPService.taoLopHocPhanChoCTDT(maCTDT, maHocKy);
            if (created == 0) {
                ra.addFlashAttribute("errorMsg",
                        "Khong co lop nao duoc tao. Co the: (1) hoc ky nay khong khop voi tien trinh CTDT "
                      + "(dua tren Khoa + HocKyThu), hoac (2) cac lop da duoc tao truoc do.");
            } else {
                ra.addFlashAttribute("successMsg",
                        "Tao thanh cong " + created + " lop hoc phan moi cho CTDT nay trong hoc ky da chon!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hoc-phan?maCTDT=" + maCTDT + "&maHocKy=" + maHocKy;
    }

    /** Phan cong giang vien (soft-check: GV ngoai doi ngu van duoc gan + warning). */
    @PostMapping("/phan-cong")
    public String phanCong(@RequestParam String maCTDT,
                            @RequestParam String maHocPhan,
                            @RequestParam String maHocKy,
                            @RequestParam Integer maLop,
                            @RequestParam String maGV,
                            RedirectAttributes ra) {
        try {
            LopHocPhanId id = new LopHocPhanId(maCTDT, maHocPhan, maHocKy, maLop);
            boolean thuocDoiNgu = lopHPService.gvThuocDoiNguHocPhan(maHocPhan, maGV);
            lopHPService.phanCongGiangVien(id, maGV);
            if (thuocDoiNgu) {
                ra.addFlashAttribute("successMsg", "Phan cong giang vien thanh cong!");
            } else {
                ra.addFlashAttribute("warningMsg",
                        "Da phan cong GV " + maGV + " cho lop, NHUNG GV nay chua thuoc doi ngu giang vien "
                      + "cua hoc phan " + maHocPhan + ". Vui long them GV vao doi ngu hoc phan "
                      + "(/hoc-phan/chi-tiet/" + maHocPhan + ") de chuan hoa.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hoc-phan?maCTDT=" + maCTDT + "&maHocKy=" + maHocKy;
    }

    /** Chi tiet lop: danh sach SV */
    @GetMapping("/chi-tiet")
    public String chiTiet(@RequestParam String maCTDT,
                           @RequestParam String maHocPhan,
                           @RequestParam String maHocKy,
                           @RequestParam Integer maLop,
                           Model model) {
        LopHocPhanId id = new LopHocPhanId(maCTDT, maHocPhan, maHocKy, maLop);
        model.addAttribute("lopId", id);
        model.addAttribute("danhSachSV", lopHPService.findSinhVienTrongLop(id));
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        return "lop-hoc-phan/chi-tiet";
    }

    /** Dang ky SV vao lop */
    @PostMapping("/dang-ky-sv")
    public String dangKySV(@RequestParam String maCTDT,
                             @RequestParam String maHocPhan,
                             @RequestParam String maHocKy,
                             @RequestParam Integer maLop,
                             @RequestParam String maSV,
                             RedirectAttributes ra) {
        try {
            LopHocPhanId id = new LopHocPhanId(maCTDT, maHocPhan, maHocKy, maLop);
            lopHPService.dangKyLopHocPhan(id, maSV);
            ra.addFlashAttribute("successMsg", "Dang ky SV thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hoc-phan/chi-tiet?maCTDT=" + maCTDT +
               "&maHocPhan=" + maHocPhan + "&maHocKy=" + maHocKy + "&maLop=" + maLop;
    }

    /** Canh bao SV */
    @PostMapping("/canh-bao-sv")
    public String canhBaoSV(@RequestParam String maCTDT,
                              @RequestParam String maHocPhan,
                              @RequestParam String maHocKy,
                              @RequestParam Integer maLop,
                              @RequestParam String maSV,
                              @RequestParam String nhanXet,
                              @RequestParam String emailCVHT,
                              RedirectAttributes ra) {
        try {
            LopHocPhanId id = new LopHocPhanId(maCTDT, maHocPhan, maHocKy, maLop);
            lopHPService.canhBaoSinhVien(id, maSV, nhanXet, emailCVHT);
            ra.addFlashAttribute("successMsg", "Da canh bao sinh vien va gui email!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hoc-phan/chi-tiet?maCTDT=" + maCTDT +
               "&maHocPhan=" + maHocPhan + "&maHocKy=" + maHocKy + "&maLop=" + maLop;
    }
}
