package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.DoanhNghiepDTO;
import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.service.DoanhNghiepService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/doanh-nghiep")
@RequiredArgsConstructor
public class DoanhNghiepController {

    private static final String ACTIVE_MENU = "doanh-nghiep";

    private final DoanhNghiepService doanhNghiepService;

    /* ====================== DANH SACH ====================== */
    @GetMapping
    public String danhSach(@RequestParam(defaultValue = "") String keyword,
                           @RequestParam(required = false) TrangThaiDoanhNghiep trangThai,
                           @RequestParam(defaultValue = "0") int page,
                           Model model) {
        Page<DoanhNghiep> pages = doanhNghiepService.search(
                keyword, trangThai,
                PageRequest.of(page, 15, Sort.by("tenDoanhNghiep")));
        model.addAttribute("pages", pages);
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThaiFilter", trangThai);
        model.addAttribute("trangThaiList", TrangThaiDoanhNghiep.values());
        model.addAttribute("thongKe", doanhNghiepService.getThongKe());
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "doanh-nghiep/danh-sach";
    }

    /* ====================== CHI TIET ====================== */
    @GetMapping("/chi-tiet/{ma}")
    public String chiTiet(@PathVariable String ma, Model model) {
        model.addAttribute("doanhNghiep", doanhNghiepService.findById(ma));
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "doanh-nghiep/chi-tiet";
    }

    /* ====================== THEM MOI ====================== */
    @GetMapping("/them")
    public String themForm(Model model) {
        DoanhNghiepDTO dto = new DoanhNghiepDTO();
        dto.setTrangThai(TrangThaiDoanhNghiep.DangHopTac);
        model.addAttribute("doanhNghiepDTO", dto);
        model.addAttribute("trangThaiList", TrangThaiDoanhNghiep.values());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "doanh-nghiep/form";
    }

    @PostMapping("/them")
    public String them(@Valid @ModelAttribute DoanhNghiepDTO doanhNghiepDTO,
                       BindingResult br,
                       Model model,
                       RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("trangThaiList", TrangThaiDoanhNghiep.values());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "doanh-nghiep/form";
        }
        try {
            DoanhNghiep dn = doanhNghiepService.create(doanhNghiepDTO);
            ra.addFlashAttribute("successMsg",
                    "Tao doanh nghiep thanh cong (" + dn.getMaDoanhNghiep() + ")");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/doanh-nghiep";
    }

    /* ====================== CHINH SUA ====================== */
    @GetMapping("/sua/{ma}")
    public String suaForm(@PathVariable String ma, Model model) {
        DoanhNghiep dn = doanhNghiepService.findById(ma);
        DoanhNghiepDTO dto = new DoanhNghiepDTO();
        dto.setMaDoanhNghiep(dn.getMaDoanhNghiep());
        dto.setTenDoanhNghiep(dn.getTenDoanhNghiep());
        dto.setLinhVuc(dn.getLinhVuc());
        dto.setNguoiDaiDien(dn.getNguoiDaiDien());
        dto.setEmail(dn.getEmail());
        dto.setSoDienThoai(dn.getSoDienThoai());
        dto.setDiaChiDN(dn.getDiaChiDN());
        dto.setTrangThai(dn.getTrangThai());

        model.addAttribute("doanhNghiepDTO", dto);
        model.addAttribute("trangThaiList", TrangThaiDoanhNghiep.values());
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "doanh-nghiep/form";
    }

    @PostMapping("/sua/{ma}")
    public String sua(@PathVariable String ma,
                      @Valid @ModelAttribute DoanhNghiepDTO doanhNghiepDTO,
                      BindingResult br,
                      Model model,
                      RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("trangThaiList", TrangThaiDoanhNghiep.values());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "doanh-nghiep/form";
        }
        try {
            doanhNghiepService.update(ma, doanhNghiepDTO);
            ra.addFlashAttribute("successMsg", "Cap nhat doanh nghiep thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/doanh-nghiep";
    }

    /* ====================== TOGGLE TRANG THAI ====================== */
    @PostMapping("/doi-trang-thai/{ma}")
    public String doiTrangThai(@PathVariable String ma, RedirectAttributes ra) {
        try {
            doanhNghiepService.toggleTrangThai(ma);
            ra.addFlashAttribute("successMsg", "Cap nhat trang thai doanh nghiep thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/doanh-nghiep";
    }

    /* ====================== XOA ====================== */
    @PostMapping("/xoa/{ma}")
    public String xoa(@PathVariable String ma, RedirectAttributes ra) {
        try {
            doanhNghiepService.delete(ma);
            ra.addFlashAttribute("successMsg", "Xoa doanh nghiep thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/doanh-nghiep";
    }
}
