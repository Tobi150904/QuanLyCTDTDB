package com.ntu.quanlyctdtdb.controller;
import com.ntu.quanlyctdtdb.dto.LopHanhChinhDTO;
import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.LopHanhChinhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quan ly Lop Hanh Chinh.
 *
 * <p>Role (theo SecurityConfig):
 * <ul>
 *   <li><b>PDT, TTDTXS, ADMIN</b>: full RW (xem danh sach toan truong,
 *       them/sua/xoa, phan cong CVHT).</li>
 *   <li><b>CVHT</b>: read-only, chi thay nhung lop minh duoc phan cong
 *       lam co van hoc tap (filter theo coVan.maGV = current user).
 *       Khong duoc them/sua/xoa/phan cong.</li>
 * </ul>
 *
 * <p>Bug fix: truoc day class-level @PreAuthorize bo CVHT ra khoi
 * danh sach -> CVHT login khong vao duoc lop hanh chinh cua minh
 * (403 ngay tu URL gate / class gate). Phase 8 fix: them CVHT vao
 * gate, dat method-level @PreAuthorize chan ghi cho CVHT, them
 * ownership guard cho chi-tiet, va filter list theo coVan.</p>
 */
@Controller
@RequestMapping("/lop-hanh-chinh")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN','CVHT')")
public class LopHanhChinhController {

    private static final String ACTIVE_MENU = "lop-hanh-chinh";

    private final LopHanhChinhService service;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;

    /* ====================== HELPERS ROLE ====================== */
    private boolean hasRole(CustomUserDetails ud, String role) {
        return ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> ("ROLE_" + role).equals(a.getAuthority()));
    }

    /** Lay maGV cua CVHT hien tai. Tra ve null neu khong phai GV. */
    private String currentMaGV(CustomUserDetails ud) {
        if (ud == null) return null;
        GiangVien gv = giangVienRepo.findByNguoiDung_MaNguoiDung(ud.getMaNguoiDung())
                .orElse(null);
        return gv != null ? gv.getMaGV() : null;
    }

    /* ====================== DANH SACH ====================== */
    @GetMapping
    public String danhSach(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String maCTDT,
                            @RequestParam(required = false) String khoaHoc,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            Model model) {
        boolean isPdtAdminTtdtxs = hasRole(ud, "PDT") || hasRole(ud, "ADMIN")
                || hasRole(ud, "TTDTXS");
        boolean isCvht = hasRole(ud, "CVHT");

        if (!isPdtAdminTtdtxs && isCvht) {
            // CVHT: chi xem cac lop minh phu trach.
            String maGV = currentMaGV(ud);
            model.addAttribute("lopList",
                    service.searchByCoVan(keyword, maCTDT, khoaHoc, maGV));
            // Stat va filter list (CTDT, khoaHoc) van load binh thuong de UI nhat quan.
            model.addAttribute("ctdtList", ctdtRepo.findAll());
            model.addAttribute("thongKe", service.getThongKeByCoVan(maGV));
            model.addAttribute("isCvhtView", true);
        } else {
            model.addAttribute("lopList", service.search(keyword, maCTDT, khoaHoc));
            model.addAttribute("ctdtList", ctdtRepo.findAll());
            model.addAttribute("thongKe", service.getThongKe());
            model.addAttribute("isCvhtView", false);
        }
        model.addAttribute("keyword", keyword);
        model.addAttribute("maCTDT", maCTDT);
        model.addAttribute("khoaHoc", khoaHoc);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "lop-hanh-chinh/danh-sach";
    }

    /* ====================== CHI TIET ====================== */
    @GetMapping("/chi-tiet/{ma}")
    public String chiTiet(@PathVariable String ma,
                           @AuthenticationPrincipal CustomUserDetails ud,
                           Model model) {
        LopHanhChinh lop = service.findById(ma);

        // Ownership guard: CVHT chi xem duoc lop chinh minh phu trach.
        // PDT/TTDTXS/ADMIN bypass.
        boolean isPdtAdminTtdtxs = hasRole(ud, "PDT") || hasRole(ud, "ADMIN")
                || hasRole(ud, "TTDTXS");
        boolean isCvht = hasRole(ud, "CVHT");
        if (!isPdtAdminTtdtxs && isCvht) {
            String maGvHienTai = currentMaGV(ud);
            String maCoVan = lop.getCoVan() != null ? lop.getCoVan().getMaGV() : null;
            if (maGvHienTai == null || !maGvHienTai.equals(maCoVan)) {
                throw new AccessDeniedException(
                        "Ban khong phai la co van hoc tap cua lop " + ma + ".");
            }
        }

        model.addAttribute("lop", lop);
        // findByLopFetch fetch NguoiDung kem theo de render hoTen trong template
        // (open-in-view=false — xem SinhVienRepository).
        model.addAttribute("sinhVienList", sinhVienRepo.findByLopFetch(ma));
        model.addAttribute("activeMenu", ACTIVE_MENU);
        model.addAttribute("isCvhtView", !isPdtAdminTtdtxs && isCvht);
        return "lop-hanh-chinh/chi-tiet";
    }

    /* ====================== THEM MOI ====================== */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("lopDTO", new LopHanhChinhDTO());
        model.addAttribute("ctdtList", ctdtRepo.findAll());
        model.addAttribute("giangVienList", giangVienRepo.findAllActive());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "lop-hanh-chinh/form";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("lopDTO") LopHanhChinhDTO dto,
                        BindingResult br,
                        Model model,
                        RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("ctdtList", ctdtRepo.findAll());
            model.addAttribute("giangVienList", giangVienRepo.findAllActive());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "lop-hanh-chinh/form";
        }
        try {
            LopHanhChinh saved = service.create(dto);
            ra.addFlashAttribute("successMsg",
                    "Tao lop " + saved.getMaLopHC() + " thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/lop-hanh-chinh/them";
        }
        return "redirect:/lop-hanh-chinh";
    }

    /* ====================== CHINH SUA ====================== */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @GetMapping("/sua/{ma}")
    public String suaForm(@PathVariable String ma, Model model) {
        LopHanhChinh e = service.findById(ma);
        LopHanhChinhDTO dto = new LopHanhChinhDTO();
        dto.setMaLopHC(e.getMaLopHC());
        dto.setTenLop(e.getTenLop());
        dto.setKhoaHoc(e.getKhoaHoc());
        if (e.getChuongTrinhDaoTao() != null) {
            dto.setMaCTDT(e.getChuongTrinhDaoTao().getMaCTDT());
        }
        if (e.getCoVan() != null) {
            dto.setMaCoVan(e.getCoVan().getMaGV());
        }
        model.addAttribute("lopDTO", dto);
        model.addAttribute("ctdtList", ctdtRepo.findAll());
        model.addAttribute("giangVienList", giangVienRepo.findAllActive());
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "lop-hanh-chinh/form";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/sua/{ma}")
    public String sua(@PathVariable String ma,
                       @Valid @ModelAttribute("lopDTO") LopHanhChinhDTO dto,
                       BindingResult br,
                       Model model,
                       RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("ctdtList", ctdtRepo.findAll());
            model.addAttribute("giangVienList", giangVienRepo.findAllActive());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "lop-hanh-chinh/form";
        }
        try {
            service.update(ma, dto);
            ra.addFlashAttribute("successMsg", "Cap nhat lop " + ma + " thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hanh-chinh";
    }

    /* ====================== PHAN CONG CVHT ====================== */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/phan-cong-cvht/{ma}")
    public String phanCongCVHT(@PathVariable String ma,
                                 @RequestParam(required = false) String maGV,
                                 RedirectAttributes ra) {
        try {
            service.phanCongCoVan(ma, maGV);
            if (maGV == null || maGV.isBlank()) {
                ra.addFlashAttribute("successMsg", "Da huy phan cong CVHT cho lop " + ma);
            } else {
                ra.addFlashAttribute("successMsg",
                        "Da phan cong CVHT " + maGV + " cho lop " + ma);
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hanh-chinh/chi-tiet/" + ma;
    }

    /* ====================== XOA ====================== */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/xoa/{ma}")
    public String xoa(@PathVariable String ma, RedirectAttributes ra) {
        try {
            service.delete(ma);
            ra.addFlashAttribute("successMsg", "Xoa lop " + ma + " thanh cong.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/lop-hanh-chinh";
    }
}
