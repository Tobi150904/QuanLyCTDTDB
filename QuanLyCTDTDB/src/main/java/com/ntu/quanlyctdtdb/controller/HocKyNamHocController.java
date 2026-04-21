package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.HocKyNamHocDTO;
import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;
import com.ntu.quanlyctdtdb.service.HocKyNamHocService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quan ly Hoc Ky - Nam Hoc (Phase 3).
 * Role: PDT, TTDTXS, ADMIN.
 */
@Controller
@RequestMapping("/hoc-ky")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
public class HocKyNamHocController {

    private static final String ACTIVE_MENU = "hoc-ky";

    private final HocKyNamHocService service;

    /* ====================== DANH SACH ====================== */
    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("hocKyList", service.findAll());
        model.addAttribute("thongKe", service.getThongKe());
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "hoc-ky/danh-sach";
    }

    /* ====================== THEM MOI ====================== */
    @GetMapping("/them")
    public String themForm(Model model) {
        HocKyNamHocDTO dto = new HocKyNamHocDTO();
        dto.setHocKyThu(1);
        int nam = java.time.Year.now().getValue();
        dto.setNamBatDau(nam);
        dto.setNamKetThuc(nam + 1);
        dto.setTrangThai(TrangThaiHocKy.SapDienRa);
        model.addAttribute("hocKyDTO", dto);
        model.addAttribute("trangThaiList", TrangThaiHocKy.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "hoc-ky/form";
    }

    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("hocKyDTO") HocKyNamHocDTO dto,
                        BindingResult br,
                        Model model,
                        RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("trangThaiList", TrangThaiHocKy.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "hoc-ky/form";
        }
        try {
            HocKyNamHoc saved = service.create(dto);
            ra.addFlashAttribute("successMsg",
                    "Tao hoc ky " + saved.getMaHocKy() + " thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/hoc-ky/them";
        }
        return "redirect:/hoc-ky";
    }

    /* ====================== CHINH SUA ====================== */
    @GetMapping("/sua/{ma}")
    public String suaForm(@PathVariable String ma, Model model) {
        HocKyNamHoc e = service.findById(ma);
        HocKyNamHocDTO dto = new HocKyNamHocDTO();
        dto.setMaHocKy(e.getMaHocKy());
        dto.setTenHocKy(e.getTenHocKy());
        dto.setHocKyThu(e.getHocKyThu());
        dto.setNamBatDau(e.getNamBatDau());
        dto.setNamKetThuc(e.getNamKetThuc());
        dto.setNgayBatDau(e.getNgayBatDau());
        dto.setNgayKetThuc(e.getNgayKetThuc());
        dto.setTrangThai(e.getTrangThai());
        model.addAttribute("hocKyDTO", dto);
        model.addAttribute("trangThaiList", TrangThaiHocKy.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "hoc-ky/form";
    }

    @PostMapping("/sua/{ma}")
    public String sua(@PathVariable String ma,
                       @Valid @ModelAttribute("hocKyDTO") HocKyNamHocDTO dto,
                       BindingResult br,
                       Model model,
                       RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("trangThaiList", TrangThaiHocKy.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "hoc-ky/form";
        }
        try {
            service.update(ma, dto);
            ra.addFlashAttribute("successMsg", "Cap nhat hoc ky " + ma + " thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-ky";
    }

    /* ====================== DOI TRANG THAI ====================== */
    @PostMapping("/doi-trang-thai/{ma}")
    public String doiTrangThai(@PathVariable String ma,
                                @RequestParam TrangThaiHocKy moi,
                                RedirectAttributes ra) {
        try {
            service.doiTrangThai(ma, moi);
            ra.addFlashAttribute("successMsg",
                    "Da chuyen hoc ky " + ma + " sang trang thai " + moi + ".");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-ky";
    }

    /* ====================== XOA ====================== */
    @PostMapping("/xoa/{ma}")
    public String xoa(@PathVariable String ma, RedirectAttributes ra) {
        try {
            service.delete(ma);
            ra.addFlashAttribute("successMsg", "Xoa hoc ky " + ma + " thanh cong.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-ky";
    }
}
