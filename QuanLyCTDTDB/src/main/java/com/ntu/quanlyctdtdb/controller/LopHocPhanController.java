package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
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
import com.ntu.quanlyctdtdb.repository.LopHocPhanRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.LopHocPhanService;
import com.ntu.quanlyctdtdb.util.CsvExportUtil;
import com.ntu.quanlyctdtdb.util.HocKyUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Controller quan ly Lop Hoc Phan.
 * Role (docs/03 §"SO DO TONG HOP QUYEN" + §WF-05.*):
 *   - PDT, TTDTXS, CNHP, ADMIN : RW (tao hang loat, phan cong GV, quan ly SV)
 *   - GiangVien                : R  lop minh duoc phan cong + W "canh-bao-sv"
 *   - SinhVien                 : R  lop minh dang ky (xem thoi khoa bieu, diem)
 * Class-level cho doc, write-level qua @PreAuthorize method-level.
 */
@Controller
@RequestMapping("/lop-hoc-phan")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN','GIANG_VIEN','SINH_VIEN')")
public class LopHocPhanController {

    private final LopHocPhanService lopHPService;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final GiangVienRepository giangVienRepo;
    private final HocPhanRepository hocPhanRepo;
    private final CtdtHocPhanRepository ctdtHocPhanRepo;
    private final DoiNguGiangVienHpRepository doiNguRepo;
    // Bug-fix phan quyen: can lookup MaGV/MaSV tu MaNguoiDung de auto-filter
    // theo role; can verify ownership truoc khi cho GV canh-bao SV.
    private final SinhVienRepository sinhVienRepo;
    private final LopHocPhanRepository lopHocPhanRepo;

    @ModelAttribute("activeMenu")
    public String activeMenu() { return "lop-hoc-phan"; }

    @GetMapping
    public String danhSach(@RequestParam(required = false) String maCTDT,
                            @RequestParam(required = false) String maHocKy,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            Model model) {
        model.addAttribute("ctdtList", ctdtRepo.findAll());
        model.addAttribute("hocKyList", hocKyRepo.findAllByOrderByNgayBatDauDesc());
        model.addAttribute("maCTDT", maCTDT);
        model.addAttribute("maHocKy", maHocKy);

        boolean hasCTDT  = maCTDT  != null && !maCTDT.isBlank();
        boolean hasHocKy = maHocKy != null && !maHocKy.isBlank();

        // ========================================================================
        // Bug-fix phan quyen (user feedback "GV bam vao xem lop minh thi vao lop
        // hoc phan trang trong khong logic"):
        //   GV / SV vao /lop-hoc-phan tu sidebar -> KHONG bat phai chon CTDT/HocKy
        //   truoc; auto-filter ve danh sach lop cua chinh ho. Khi ho chu dong
        //   chon CTDT/HocKy, du lieu se duoc filter binh thuong (giu UX cu).
        //   Cac role quan ly (PDT/TTDTXS/CNHP/ADMIN) van bat phai chon filter
        //   de tranh load full bang LopHocPhan vai nghin record.
        // ========================================================================
        boolean isStaffView = ud != null && ud.getAuthorities().stream().anyMatch(a ->
                  "ROLE_PDT".equals(a.getAuthority())
               || "ROLE_TTDTXS".equals(a.getAuthority())
               || "ROLE_CNHP".equals(a.getAuthority())
               || "ROLE_ADMIN".equals(a.getAuthority()));
        boolean isGV = ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> "ROLE_GIANG_VIEN".equals(a.getAuthority()));
        boolean isSV = ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SINH_VIEN".equals(a.getAuthority()));

        boolean personalizedMode = false;
        if (!hasCTDT && !hasHocKy && !isStaffView && (isGV || isSV)) {
            List<LopHocPhan> myList = List.of();
            String personalizedHint = null;
            if (isGV) {
                var gv = giangVienRepo.findByNguoiDung_MaNguoiDung(ud.getMaNguoiDung()).orElse(null);
                if (gv != null) {
                    myList = lopHPService.findByGiangVien(gv.getMaGV());
                    personalizedHint = "Cac lop hoc phan ban duoc phan cong giang day.";
                }
            } else { // isSV
                var sv = sinhVienRepo.findByNguoiDung_MaNguoiDung(ud.getMaNguoiDung()).orElse(null);
                if (sv != null) {
                    myList = lopHPService.findBySinhVien(sv.getMaSV());
                    personalizedHint = "Cac lop hoc phan ban da dang ky.";
                }
            }
            personalizedMode = true;
            model.addAttribute("danhSach", myList);
            model.addAttribute("personalizedMode", true);
            model.addAttribute("personalizedHint", personalizedHint);

            // GV/SV view rai nhieu CTDT + nhieu HK -> bat ca 2 cot phu de user
            // dinh huong duoc lop nao thuoc CTDT/ky nao.
            model.addAttribute("forceShowCtdtCol", true);
            model.addAttribute("forceShowHkCol", true);

            // Hien dem so lop da mo cho tung HP (chi co y nghia trong personalized
            // view neu user tap trung 1 HP — bo qua cho gon UI).
            // GV: hien thi them widget "Lop chua phan cong" KHONG ap dung — chi
            // staff thay danh sach can xu ly. Set rong de template tu hide.
            model.addAttribute("chuaPhanCong", List.of());
            // Stat-card row: tat de UI personalized goon, focus vao danh sach.
            model.addAttribute("thongKe", null);
            // Map maHP -> HocPhan de hien thi tenHP trong bang.
            Map<String, HocPhan> hocPhanMap = hocPhanRepo.findAll().stream()
                    .collect(Collectors.toMap(HocPhan::getMaHocPhan, Function.identity()));
            model.addAttribute("hocPhanMap", hocPhanMap);
            // GV/SV khong duoc phep phan cong GV — bo dropdown.
            model.addAttribute("giangVienList", List.of());
            return "lop-hoc-phan/danh-sach";
        }
        model.addAttribute("personalizedMode", personalizedMode);

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

            // "Ke Hoach Mo Lop Toan Truong": khi user chon HocKy nhung KHONG
            // chon 1 CTDT cu the (= "Tat ca CTDT"), build per-CTDT plan.
            // Moi CTDT co khoa khac nhau nen HK1-2025 quy doi ra programSemester
            // khac nhau:
            //   CTDT khoa 2022 + HK1-2025 -> HK7
            //   CTDT khoa 2023 + HK1-2025 -> HK5
            //   CTDT khoa 2024 + HK1-2025 -> HK3
            //   CTDT khoa 2025 + HK1-2025 -> HK1
            // Template se hien mot bang gom cot "CTDT | HK CTDT | HP ..." de
            // user thay duoc "ke hoach day" cua hoc ky nay o cap truong.
            if (hasHocKy && !hasCTDT) {
                int hkInYear = HocKyUtil.parseHkIndexInYear(maHocKy);
                int hkYear   = HocKyUtil.parseHkYear(maHocKy);
                List<CtdtPlanRow> allCtdtPlans = new java.util.ArrayList<>();
                for (ChuongTrinhDaoTao c : ctdtRepo.findAll()) {
                    int programSem = HocKyUtil.toProgramSemester(c.getKhoa(), maHocKy);
                    if (programSem <= 0) {
                        // CTDT nam ngoai khung ky nay (vd HK1-2025 voi CTDT
                        // khoa 2026 -> offset am). Bo qua de view gon gang.
                        continue;
                    }
                    List<CtdtHocPhan> hps = ctdtHocPhanRepo
                            .findByCtdtAndKyFetch(c.getMaCTDT(), programSem);
                    if (hps.isEmpty()) continue;
                    allCtdtPlans.add(new CtdtPlanRow(
                            c.getMaCTDT(), c.getTenCTDT(), c.getKhoa(),
                            programSem, hps));
                }
                model.addAttribute("allCtdtPlans", allCtdtPlans);
                model.addAttribute("hkInYear", hkInYear);
                model.addAttribute("hkYear", hkYear);
            }

            // "Ke Hoach Mo Lop" + "Tao Hang Loat" CHI co y nghia khi user chon
            // BOTH CTDT va HocKy (vi can ca ma CTDT de join sang CtdtHocPhan
            // va ma HocKy de xac dinh ky). Neu chi co 1 tham so, bo 2 khoi UI
            // nay de tranh gay nham lan nghiep vu.
            if (hasCTDT && hasHocKy) {
                // BUG-FIX: hocKyThu cua CtdtHocPhan la so ky THEO CTDT (1..8
                // cho khung 4 nam), KHONG phai so ky trong 1 nam hoc (1..3).
                // Truoc day code parse "HK1-2023" -> 1 va dung lam hocKyThu
                // -> moi CTDT chi ra HP cua HK1 va HK2 (sai). Dung CTDT.khoa
                // (nam bat dau cua khoa) + HocKyNamHoc.maHocKy de quy doi
                // sang ky thu cua CTDT qua HocKyUtil.
                ChuongTrinhDaoTao ctdt = ctdtRepo.findById(maCTDT).orElse(null);
                String khoa = ctdt != null ? ctdt.getKhoa() : null;
                int hocKyThu = HocKyUtil.toProgramSemester(khoa, maHocKy);

                // Dung findByCtdtAndKyFetch de JOIN FETCH hocPhan — tranh
                // LazyInitializationException khi template goi ch.hocPhan.tenHocPhan
                // (open-in-view=false).
                List<CtdtHocPhan> hpDuKien = hocKyThu > 0
                        ? ctdtHocPhanRepo.findByCtdtAndKyFetch(maCTDT, hocKyThu)
                        : List.of();
                model.addAttribute("hocKyThu", hocKyThu);
                model.addAttribute("hpDuKien", hpDuKien);

                // Expose them thong tin de template giai thich mapping cho
                // user (giam confusion khi thay "HK1-2023" tra ra HP cua HK3).
                model.addAttribute("ctdtKhoa", khoa);
                model.addAttribute("hkInYear", HocKyUtil.parseHkIndexInYear(maHocKy));
                model.addAttribute("hkYear",  HocKyUtil.parseHkYear(maHocKy));
            }

            // Dem so lop da mo cho tung HP (de hien thi "da mo X/Y lop")
            Map<String, Long> daMoCount = danhSach.stream()
                    .collect(Collectors.groupingBy(l -> l.getId().getMaHocPhan(),
                            Collectors.counting()));
            model.addAttribute("daMoCount", daMoCount);
        }
        model.addAttribute("chuaPhanCong", lopHPService.findChuaPhanCongGV());
        // Phase 2 — stat-card row tren dau danh sach (4 KPI: tong / dang mo /
        // da dong / chua phan cong). Khong phu thuoc filter hien tai de
        // PDT/TTDTXS thay nhanh tinh hinh tong toan he thong.
        model.addAttribute("thongKe", lopHPService.getThongKe());

        // Map maHocPhan -> HocPhan de template hien thi tenHocPhan
        // (LopHocPhan khong map truc tiep sang HocPhan qua @EmbeddedId).
        Map<String, HocPhan> hocPhanMap = hocPhanRepo.findAll().stream()
                .collect(Collectors.toMap(HocPhan::getMaHocPhan, Function.identity()));
        model.addAttribute("hocPhanMap", hocPhanMap);

        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        return "lop-hoc-phan/danh-sach";
    }

    /* ====================== EXPORT CSV ====================== */
    /**
     * Xuat lop HP ra CSV theo filter hien tai (CTDT / HocKy / both).
     * Bat buoc co it nhat 1 filter de tranh export ca bang LopHocPhan
     * (datasize lon, khong co y nghia bao cao).
     */
    @GetMapping("/export")
    public void exportCsv(@RequestParam(required = false) String maCTDT,
                           @RequestParam(required = false) String maHocKy,
                           HttpServletResponse response) throws java.io.IOException {
        boolean hasCTDT  = maCTDT  != null && !maCTDT.isBlank();
        boolean hasHocKy = maHocKy != null && !maHocKy.isBlank();
        if (!hasCTDT && !hasHocKy) {
            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("text/plain; charset=UTF-8");
            response.getWriter().write("Hay chon CTDT hoac HocKy truoc khi xuat CSV.");
            return;
        }
        List<LopHocPhan> rows;
        if (hasCTDT && hasHocKy) {
            rows = lopHPService.findByCTDTAndHocKy(maCTDT, maHocKy);
        } else if (hasCTDT) {
            rows = lopHPService.findByCTDT(maCTDT);
        } else {
            rows = lopHPService.findByHocKy(maHocKy);
        }
        Map<String, HocPhan> hocPhanMap = hocPhanRepo.findAll().stream()
                .collect(Collectors.toMap(HocPhan::getMaHocPhan, Function.identity()));

        String[] headers = {
            "Ma CTDT", "Ma HP", "Ten HP", "Hoc Ky", "Ma Lop", "GV Phan Cong"
        };
        List<String[]> data = new ArrayList<>();
        for (LopHocPhan l : rows) {
            HocPhan hp = hocPhanMap.get(l.getId().getMaHocPhan());
            String tenHP = hp != null ? hp.getTenHocPhan() : "";
            String tenGV = "";
            if (l.getGiangVien() != null && l.getGiangVien().getNguoiDung() != null) {
                tenGV = l.getGiangVien().getNguoiDung().getHoTen();
            }
            data.add(CsvExportUtil.row(
                    l.getId().getMaCTDT(),
                    l.getId().getMaHocPhan(),
                    tenHP,
                    l.getId().getMaHocKy(),
                    l.getId().getMaLopHocPhan() != null ? String.valueOf(l.getId().getMaLopHocPhan()) : "",
                    tenGV
            ));
        }
        String fileBase = "lop-hoc-phan"
                + (hasCTDT ? "_" + maCTDT : "")
                + (hasHocKy ? "_" + maHocKy : "");
        CsvExportUtil.write(response, fileBase, headers, data);
    }

    /**
     * Tao hang loat lop HP cho CTDT trong mot hoc ky.
     * <p>Chi mo lop cho HP co {@code hocKyThu} trung voi ky hien tai
     * (parse tu {@code maHocKy}). Nhan them tuy chon override so lop mo
     * per-HP qua 2 array {@code hpCode}, {@code soLop}: cho phep TTDTXS
     * chinh so lop moi ky (vd ky nay tuyen it SV thi mo 2 lop, ky sau
     * tuyen nhieu mo 4 lop) ma khong can sua {@code CtdtHocPhan.soLopDuKien}.
     */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
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
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
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

    /** Dang ky SV vao lop. Chi BCN/TTDTXS/PDT/ADMIN (docs/03 WF-05.2). */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
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

    /**
     * Canh bao SV. Mo cho GV (nguoi day lop) + BCN/TTDTXS/CNHP/ADMIN.
     *
     * <p>Bug-fix phan quyen: GV chi duoc canh bao SV thuoc lop minh duoc
     * phan cong day. Truoc day endpoint nay thieu ownership check — bat
     * ky GV nao cung co the danh dau canh bao SV trong bat ky lop nao
     * (security hole). PDT/TTDTXS/CNHP/ADMIN bypass de ho tro support.
     * CVHT KHONG dung endpoint nay; CVHT xu ly canh bao tai /danh-gia/canh-bao
     * (set KetQuaXuLy) — quy tac docs/03 §6.2.
     */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN','GIANG_VIEN')")
    @PostMapping("/canh-bao-sv")
    public String canhBaoSV(@RequestParam String maCTDT,
                              @RequestParam String maHocPhan,
                              @RequestParam String maHocKy,
                              @RequestParam Integer maLop,
                              @RequestParam String maSV,
                              @RequestParam String nhanXet,
                              @RequestParam String emailCVHT,
                              @AuthenticationPrincipal CustomUserDetails ud,
                              RedirectAttributes ra) {
        try {
            // Ownership guard cho GIANG_VIEN: chi duoc canh bao SV o lop
            // minh dang day. Cac role quan ly (PDT/TTDTXS/CNHP/CVHT/ADMIN)
            // duoc bypass — ho dong vai tro giam sat / xu ly canh bao.
            boolean isStaff = ud != null && ud.getAuthorities().stream().anyMatch(a ->
                      "ROLE_PDT".equals(a.getAuthority())
                   || "ROLE_TTDTXS".equals(a.getAuthority())
                   || "ROLE_CNHP".equals(a.getAuthority())
                   || "ROLE_ADMIN".equals(a.getAuthority()));
            boolean isGv = ud != null && ud.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_GIANG_VIEN".equals(a.getAuthority()));
            if (!isStaff && isGv) {
                LopHocPhan lhp = lopHocPhanRepo
                        .findByIdFetchGv(maCTDT, maHocPhan, maHocKy, maLop)
                        .orElseThrow(() -> new AccessDeniedException(
                                "Khong tim thay lop hoc phan."));
                var gv = giangVienRepo
                        .findByNguoiDung_MaNguoiDung(ud.getMaNguoiDung()).orElse(null);
                String maGvCuaLop = lhp.getGiangVien() != null
                        ? lhp.getGiangVien().getMaGV() : null;
                if (gv == null || maGvCuaLop == null
                        || !gv.getMaGV().equals(maGvCuaLop)) {
                    throw new AccessDeniedException(
                            "Ban khong phai la giang vien cua lop hoc phan nay, "
                            + "khong the canh bao sinh vien.");
                }
            }
            LopHocPhanId id = new LopHocPhanId(maCTDT, maHocPhan, maHocKy, maLop);
            lopHPService.canhBaoSinhVien(id, maSV, nhanXet, emailCVHT);
            ra.addFlashAttribute("successMsg", "Da canh bao sinh vien va gui email!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hoc-phan/chi-tiet?maCTDT=" + maCTDT +
               "&maHocPhan=" + maHocPhan + "&maHocKy=" + maHocKy + "&maLop=" + maLop;
    }

    /**
     * View model cho "Ke Hoach Mo Lop Toan Truong": mot dong tuong ung 1 CTDT
     * - programSemester da duoc quy doi tu HocKyNamHoc. Template render cac
     * row duoc nhom theo CTDT, moi nhom hien maCTDT + khoa + HK CTDT + danh
     * sach HP du kien.
     */
    public record CtdtPlanRow(
            String maCTDT,
            String tenCTDT,
            String khoa,
            int hocKyThu,
            List<CtdtHocPhan> hocPhans) {}
}
