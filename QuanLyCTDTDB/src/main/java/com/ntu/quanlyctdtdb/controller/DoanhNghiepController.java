package com.ntu.quanlyctdtdb.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ntu.quanlyctdtdb.dto.DoanhNghiepDTO;
import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.service.DoanhNghiepService;

/**
 * DoanhNghiepController: Quan ly doanh nghiep doi tac (PDT, TTDTXS).
 * URL prefix: /doanh-nghiep
 * Side effect quan trong: Tao moi DN -> tu dong tao tai khoan NguoiDung voi role=DN.
 */
@Controller
@RequestMapping("/doanh-nghiep")
@PreAuthorize("hasAnyRole('PDT', 'TTDTXS')")
@RequiredArgsConstructor
public class DoanhNghiepController {

    private final DoanhNghiepService doanhNghiepService;

    private static final int PAGE_SIZE = 15;

    /**
     * GET /doanh-nghiep — Danh sach co filter + phan trang
     */
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String trangThai,
                       Model model) {

        TrangThaiDoanhNghiep trangThaiEnum = null;
        if (trangThai != null && !trangThai.isBlank()) {
            try { trangThaiEnum = TrangThaiDoanhNghiep.valueOf(trangThai); }
            catch (IllegalArgumentException ignored) {}
        }

        PageRequest pageable = PageRequest.of(page, PAGE_SIZE,
                Sort.by("tenDoanhNghiep").ascending());
        Page<DoanhNghiep> pageResult = doanhNghiepService.findAll(keyword, trangThaiEnum, pageable);

        model.addAttribute("page", pageResult);
        model.addAttribute("keyword", keyword);
        model.addAttribute("trangThaiFilter", trangThai);
        model.addAttribute("danhSachTrangThai", TrangThaiDoanhNghiep.values());
        model.addAttribute("activeMenu", "doanh-nghiep");
        model.addAttribute("pageTitle", "Doanh Nghiep");
        return "doanhnghiep/list";
    }

    /**
     * GET /doanh-nghiep/them — Form them moi
     */
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("doanhNghiepDTO", new DoanhNghiepDTO());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", "doanh-nghiep");
        model.addAttribute("pageTitle", "Them Doanh Nghiep");
        return "doanhnghiep/form";
    }

    /**
     * POST /doanh-nghiep/them — Luu moi + auto-create NguoiDung DN
     */
    @PostMapping("/them")
    public String themSave(@Valid @ModelAttribute("doanhNghiepDTO") DoanhNghiepDTO dto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "doanh-nghiep");
            model.addAttribute("pageTitle", "Them Doanh Nghiep");
            return "doanhnghiep/form";
        }

        try {
            doanhNghiepService.create(dto);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Them moi doanh nghiep '" + dto.getTenDoanhNghiep()
                    + "' thanh cong. Tai khoan dang nhap ('" + dto.getMaDoanhNghiep()
                    + "') da duoc tao tu dong.");
        } catch (BusinessException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "doanh-nghiep");
            model.addAttribute("pageTitle", "Them Doanh Nghiep");
            return "doanhnghiep/form";
        }
        return "redirect:/doanh-nghiep";
    }

    /**
     * GET /doanh-nghiep/{ma}/sua — Form sua
     */
    @GetMapping("/{ma}/sua")
    public String suaForm(@PathVariable String ma, Model model) {
        DoanhNghiep dn = doanhNghiepService.findById(ma);

        DoanhNghiepDTO dto = new DoanhNghiepDTO();
        dto.setMaDoanhNghiep(dn.getMaDoanhNghiep());
        dto.setTenDoanhNghiep(dn.getTenDoanhNghiep());
        dto.setLinhVucHoatDong(dn.getLinhVucHoatDong());
        dto.setNguoiDaiDien(dn.getNguoiDaiDien());
        dto.setEmailDN(dn.getEmailDN());
        dto.setSoDienThoaiDN(dn.getSoDienThoaiDN());
        dto.setDiaChiDN(dn.getDiaChiDN());
        dto.setTrangThai(dn.getTrangThai());

        model.addAttribute("doanhNghiepDTO", dto);
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", "doanh-nghiep");
        model.addAttribute("pageTitle", "Sua Doanh Nghiep");
        return "doanhnghiep/form";
    }

    /**
     * POST /doanh-nghiep/{ma}/sua — Luu cap nhat
     */
    @PostMapping("/{ma}/sua")
    public String suaSave(@PathVariable String ma,
                          @Valid @ModelAttribute("doanhNghiepDTO") DoanhNghiepDTO dto,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "doanh-nghiep");
            model.addAttribute("pageTitle", "Sua Doanh Nghiep");
            return "doanhnghiep/form";
        }

        try {
            doanhNghiepService.update(ma, dto);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Cap nhat doanh nghiep '" + dto.getTenDoanhNghiep() + "' thanh cong.");
        } catch (BusinessException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "doanh-nghiep");
            model.addAttribute("pageTitle", "Sua Doanh Nghiep");
            return "doanhnghiep/form";
        }
        return "redirect:/doanh-nghiep";
    }

    /**
     * POST /doanh-nghiep/{ma}/doi-trang-thai — Doi trang thai hop tac
     * Khi TamNgung: tu dong khoa tai khoan NguoiDung DN.
     */
    @PostMapping("/{ma}/doi-trang-thai")
    public String doiTrangThai(@PathVariable String ma,
                               @RequestParam String trangThai,
                               RedirectAttributes redirectAttributes) {
        try {
            TrangThaiDoanhNghiep tt = TrangThaiDoanhNghiep.valueOf(trangThai);
            doanhNghiepService.doiTrangThai(ma, tt);
            String msg = tt == TrangThaiDoanhNghiep.TamNgung
                    ? "Da chuyen doanh nghiep sang Tam Ngung. Tai khoan DN da bi khoa."
                    : "Da kich hoat lai hop tac voi doanh nghiep.";
            redirectAttributes.addFlashAttribute("successMsg", msg);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", "Trang thai khong hop le.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:/doanh-nghiep";
    }
}
