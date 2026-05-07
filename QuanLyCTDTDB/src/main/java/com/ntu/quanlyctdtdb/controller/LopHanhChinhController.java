package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.LopHanhChinhDTO;
import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.service.LopHanhChinhService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quan ly Lop Hanh Chinh.
 * Role (theo SecurityConfig): PDT, TTDTXS, ADMIN.
 */
@Controller
@RequestMapping("/lop-hanh-chinh")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
public class LopHanhChinhController {

    private static final String ACTIVE_MENU = "lop-hanh-chinh";

    private final LopHanhChinhService service;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;

    /* ====================== DANH SACH ====================== */
    @GetMapping
    public String danhSach(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String maCTDT,
                            @RequestParam(required = false) String khoaHoc,
                            Model model) {
        model.addAttribute("lopList", service.search(keyword, maCTDT, khoaHoc));
        model.addAttribute("ctdtList", ctdtRepo.findAll());
        model.addAttribute("thongKe", service.getThongKe());
        model.addAttribute("keyword", keyword);
        model.addAttribute("maCTDT", maCTDT);
        model.addAttribute("khoaHoc", khoaHoc);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "lop-hanh-chinh/danh-sach";
    }

    /* ====================== CHI TIET ====================== */
    @GetMapping("/chi-tiet/{ma}")
    public String chiTiet(@PathVariable String ma, Model model) {
        LopHanhChinh lop = service.findById(ma);
        model.addAttribute("lop", lop);
        // findByLopFetch fetch NguoiDung kem theo de render hoTen trong template
        // (open-in-view=false — xem SinhVienRepository).
        model.addAttribute("sinhVienList", sinhVienRepo.findByLopFetch(ma));
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "lop-hanh-chinh/chi-tiet";
    }

    /* ====================== THEM MOI ====================== */
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("lopDTO", new LopHanhChinhDTO());
        model.addAttribute("ctdtList", ctdtRepo.findAll());
        model.addAttribute("giangVienList", giangVienRepo.findAllActive());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "lop-hanh-chinh/form";
    }

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
