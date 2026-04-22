package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.entity.CtdtHocPhan;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.CtdtHocPhanRepository;
import com.ntu.quanlyctdtdb.repository.DoiNguGiangVienHpRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.service.LopHocPhanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
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
    private final CtdtHocPhanRepository ctdtHocPhanRepo;
    private final DoiNguGiangVienHpRepository doiNguRepo;

    /** Parse so ky (1..n) tu maHocKy dang "HKn-YYYY"; tra 0 neu khong hop le. */
    private static int parseHocKyThu(String maHocKy) {
        if (maHocKy == null || !maHocKy.startsWith("HK") || maHocKy.length() < 3) return 0;
        char c = maHocKy.charAt(2);
        return Character.isDigit(c) ? Character.getNumericValue(c) : 0;
    }

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

        boolean hasCTDT  = maCTDT  != null && !maCTDT.isBlank();
        boolean hasHocKy = maHocKy != null && !maHocKy.isBlank();

        // Cho phep tra cuu bang "hoac CTDT, hoac HocKy, hoac ca hai":
        //   - Chi CTDT         -> tat ca lop cua CTDT across nhieu ky (bao cao tong hop theo CTDT)
        //   - Chi HocKy        -> tat ca lop mo trong ky do across CTDT (view cap truong)
        //   - CTDT + HocKy     -> lop cua 1 ky cua 1 CTDT, kem "Ke Hoach Mo Lop" va "Tao Hang Loat"
        // Tinh chinh 2026-Q2 batch 5: truoc day BUOC lam ca 2 tieu chi — khong
        // phu hop voi nhu cau bao cao tong hop va xem cheo CTDT.
        if (hasCTDT || hasHocKy) {
            List<LopHocPhan> danhSach;
            if (hasCTDT && hasHocKy) {
                danhSach = lopHPService.findByCTDTAndHocKy(maCTDT, maHocKy);
            } else if (hasCTDT) {
                danhSach = lopHPService.findByCTDT(maCTDT);
            } else {
                danhSach = lopHPService.findByHocKy(maHocKy);
            }
            model.addAttribute("danhSach", danhSach);

            // "Ke Hoach Mo Lop" + "Tao Hang Loat" CHI co y nghia khi user chon
            // BOTH CTDT va HocKy (vi can ca ma CTDT de join sang CtdtHocPhan
            // va ma HocKy de xac dinh ky). Neu chi co 1 tham so, bo 2 khoi UI
            // nay de tranh gay nham lan nghiep vu.
            if (hasCTDT && hasHocKy) {
                int hocKyThu = parseHocKyThu(maHocKy);
                // Dung findByCtdtAndKyFetch de JOIN FETCH hocPhan — tranh
                // LazyInitializationException khi template goi ch.hocPhan.tenHocPhan
                // (open-in-view=false).
                List<CtdtHocPhan> hpDuKien = hocKyThu > 0
                        ? ctdtHocPhanRepo.findByCtdtAndKyFetch(maCTDT, hocKyThu)
                        : List.of();
                model.addAttribute("hocKyThu", hocKyThu);
                model.addAttribute("hpDuKien", hpDuKien);
            }

            // Dem so lop da mo cho tung HP (de hien thi "da mo X/Y lop")
            Map<String, Long> daMoCount = danhSach.stream()
                    .collect(Collectors.groupingBy(l -> l.getId().getMaHocPhan(),
                            Collectors.counting()));
            model.addAttribute("daMoCount", daMoCount);
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

    /**
     * Tao hang loat lop HP cho CTDT trong mot hoc ky.
     * <p>Chi mo lop cho HP co {@code hocKyThu} trung voi ky hien tai
     * (parse tu {@code maHocKy}). Nhan them tuy chon override so lop mo
     * per-HP qua 2 array {@code hpCode}, {@code soLop}: cho phep TTDTXS
     * chinh so lop moi ky (vd ky nay tuyen it SV thi mo 2 lop, ky sau
     * tuyen nhieu mo 4 lop) ma khong can sua {@code CtdtHocPhan.soLopDuKien}.
     */
    @PostMapping("/tao-hang-loat")
    public String taoHangLoat(@RequestParam String maCTDT,
                               @RequestParam String maHocKy,
                               @RequestParam(value = "hpCode", required = false) List<String> hpCode,
                               @RequestParam(value = "soLop", required = false) List<Integer> soLop,
                               RedirectAttributes ra) {
        try {
            Map<String, Integer> override = new HashMap<>();
            if (hpCode != null && soLop != null) {
                int n = Math.min(hpCode.size(), soLop.size());
                for (int i = 0; i < n; i++) {
                    String code = hpCode.get(i);
                    Integer so = soLop.get(i);
                    if (code != null && !code.isBlank() && so != null && so > 0) {
                        override.put(code.trim(), so);
                    }
                }
            }
            int tao = lopHPService.taoLopHocPhanChoCTDT(maCTDT, maHocKy, override);
            if (tao == 0) {
                ra.addFlashAttribute("warningMsg",
                        "Khong tao moi lop nao (tat ca cac lop du kien da ton tai).");
            } else {
                ra.addFlashAttribute("successMsg", "Tao moi " + tao + " lop hoc phan thanh cong!");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hoc-phan?maCTDT=" + maCTDT + "&maHocKy=" + maHocKy;
    }

    /**
     * Phan cong giang vien cho lop HP — kiem tra mem (soft check) GV co thuoc
     * {@code DoiNguGiangVienHP} cua mon hay khong. Theo docs/03_WORKFLOW.md
     * (MTW-03.3 BUOC 3), neu GV khong thuoc doi ngu: van cho phep gan
     * nhung hien {@code warningMsg} — KHONG throw chan cung.
     */
    @PostMapping("/phan-cong")
    public String phanCong(@RequestParam String maCTDT,
                            @RequestParam String maHocPhan,
                            @RequestParam String maHocKy,
                            @RequestParam Integer maLop,
                            @RequestParam String maGV,
                            RedirectAttributes ra) {
        try {
            LopHocPhanId id = new LopHocPhanId(maCTDT, maHocPhan, maHocKy, maLop);
            // Soft check TRUOC khi goi service — tranh phai goi repo tu trong service
            // layer (giu trach nhiem hep cho moi service).
            var doiNguId = new com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHpId(maHocPhan, maGV);
            boolean thuocDoiNgu = doiNguRepo.findById(doiNguId)
                    .map(d -> Boolean.TRUE.equals(d.getTrangThai()))
                    .orElse(false);
            lopHPService.phanCongGiangVien(id, maGV);
            if (thuocDoiNgu) {
                ra.addFlashAttribute("successMsg", "Phan cong giang vien thanh cong!");
            } else {
                ra.addFlashAttribute("warningMsg",
                        "Da phan cong, nhung CANH BAO: GV " + maGV
                        + " khong thuoc doi ngu cua HP " + maHocPhan
                        + " (hoac dang tam ngung). Hay bo sung vao doi ngu tai trang Chi tiet Hoc Phan.");
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
