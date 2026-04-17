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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ntu.quanlyctdtdb.dto.NguoiDungDTO;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import com.ntu.quanlyctdtdb.service.NguoiDungService;

/**
 * NguoiDungController: Quan ly nguoi dung (PDT, TTDTXS).
 * URL prefix: /nguoi-dung
 * Phan quyen: chi PDT va TTDTXS truy cap - khai bao trong SecurityConfig.
 */
@Controller
@RequestMapping("/nguoi-dung")
@PreAuthorize("hasAnyRole('PDT', 'TTDTXS')")
@RequiredArgsConstructor
public class NguoiDungController {

    private final NguoiDungService nguoiDungService;
    private final LopHanhChinhRepository lopHanhChinhRepository;

    private static final int PAGE_SIZE = 15;

    /**
     * GET /nguoi-dung — Danh sach nguoi dung co filter + phan trang
     */
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String vaiTro,
                       Model model) {

        VaiTro vaiTroEnum = null;
        if (vaiTro != null && !vaiTro.isBlank()) {
            try { vaiTroEnum = VaiTro.valueOf(vaiTro); }
            catch (IllegalArgumentException ignored) {}
        }

        PageRequest pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("maNguoiDung").ascending());
        Page<NguoiDung> pageResult = nguoiDungService.findAll(keyword, vaiTroEnum, pageable);

        model.addAttribute("page", pageResult);
        model.addAttribute("keyword", keyword);
        model.addAttribute("vaiTroFilter", vaiTro);
        model.addAttribute("danhSachVaiTro", VaiTro.values());
        model.addAttribute("activeMenu", "nguoi-dung");
        model.addAttribute("pageTitle", "Nguoi Dung");
        return "nguoidung/list";
    }

    /**
     * GET /nguoi-dung/them — Form them moi
     */
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("nguoiDungDTO", new NguoiDungDTO());
        model.addAttribute("danhSachVaiTro", VaiTro.values());
        model.addAttribute("danhSachLopHC", lopHanhChinhRepository.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", "nguoi-dung");
        model.addAttribute("pageTitle", "Them Nguoi Dung");
        return "nguoidung/form";
    }

    /**
     * POST /nguoi-dung/them — Luu moi
     */
    @PostMapping("/them")
    public String themSave(@Valid @ModelAttribute("nguoiDungDTO") NguoiDungDTO dto,
                           BindingResult bindingResult,
                           Model model,
                           RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("danhSachVaiTro", VaiTro.values());
            model.addAttribute("danhSachLopHC", lopHanhChinhRepository.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "nguoi-dung");
            model.addAttribute("pageTitle", "Them Nguoi Dung");
            return "nguoidung/form";
        }

        try {
            nguoiDungService.create(dto);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Them moi nguoi dung '" + dto.getHoTen() + "' thanh cong.");
        } catch (BusinessException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            model.addAttribute("danhSachVaiTro", VaiTro.values());
            model.addAttribute("danhSachLopHC", lopHanhChinhRepository.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", "nguoi-dung");
            model.addAttribute("pageTitle", "Them Nguoi Dung");
            return "nguoidung/form";
        }
        return "redirect:/nguoi-dung";
    }

    /**
     * GET /nguoi-dung/{id}/sua — Form sua
     */
    @GetMapping("/{id}/sua")
    public String suaForm(@PathVariable String id, Model model) {
        NguoiDung nd = nguoiDungService.findById(id);

        // Map entity -> DTO de dien vao form
        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setMaNguoiDung(nd.getMaNguoiDung());
        dto.setHoTen(nd.getHoTen());
        dto.setEmail(nd.getEmail());
        dto.setTenDangNhap(nd.getTenDangNhap());
        dto.setSoDienThoai(nd.getSoDienThoai());
        dto.setTrangThaiTK(nd.getTrangThaiTK());
        dto.setTrangThaiSV(nd.getTrangThaiSV());
        if (nd.getLopHanhChinh() != null) dto.setMaLopHC(nd.getLopHanhChinh().getMaLopHC());
        if (nd.getDoanhNghiep() != null) dto.setMaDoanhNghiep(nd.getDoanhNghiep().getMaDoanhNghiep());
        // Lay danh sach vai tro hien tai
        dto.setVaiTros(nd.getVaiTros().stream()
                .map(vt -> vt.getId().getVaiTro())
                .toList());

        model.addAttribute("nguoiDungDTO", dto);
        model.addAttribute("danhSachVaiTro", VaiTro.values());
        model.addAttribute("danhSachLopHC", lopHanhChinhRepository.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", "nguoi-dung");
        model.addAttribute("pageTitle", "Sua Nguoi Dung");
        return "nguoidung/form";
    }

    /**
     * POST /nguoi-dung/{id}/sua — Luu cap nhat
     */
    @PostMapping("/{id}/sua")
    public String suaSave(@PathVariable String id,
                          @Valid @ModelAttribute("nguoiDungDTO") NguoiDungDTO dto,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("danhSachVaiTro", VaiTro.values());
            model.addAttribute("danhSachLopHC", lopHanhChinhRepository.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "nguoi-dung");
            model.addAttribute("pageTitle", "Sua Nguoi Dung");
            return "nguoidung/form";
        }

        try {
            nguoiDungService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Cap nhat nguoi dung '" + dto.getHoTen() + "' thanh cong.");
        } catch (BusinessException ex) {
            model.addAttribute("errorMsg", ex.getMessage());
            model.addAttribute("danhSachVaiTro", VaiTro.values());
            model.addAttribute("danhSachLopHC", lopHanhChinhRepository.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", "nguoi-dung");
            model.addAttribute("pageTitle", "Sua Nguoi Dung");
            return "nguoidung/form";
        }
        return "redirect:/nguoi-dung";
    }

    /**
     * POST /nguoi-dung/{id}/khoa — Khoa / Mo khoa tai khoan
     */
    @PostMapping("/{id}/khoa")
    public String doiTrangThai(@PathVariable String id,
                               @RequestParam boolean trangThai,
                               RedirectAttributes redirectAttributes) {
        try {
            nguoiDungService.doiTrangThaiTK(id, trangThai);
            String msg = trangThai ? "Mo khoa" : "Khoa";
            redirectAttributes.addFlashAttribute("successMsg",
                    msg + " tai khoan '" + id + "' thanh cong.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        }
        return "redirect:/nguoi-dung";
    }

    /**
     * POST /nguoi-dung/import — Import tu file Excel
     */
    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Vui long chon file Excel (.xlsx).");
            return "redirect:/nguoi-dung";
        }
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.endsWith(".xlsx")) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Chi chap nhan file Excel dinh dang .xlsx.");
            return "redirect:/nguoi-dung";
        }

        try {
            int count = nguoiDungService.importFromExcel(file);
            redirectAttributes.addFlashAttribute("successMsg",
                    "Import thanh cong " + count + " nguoi dung.");
        } catch (BusinessException ex) {
            redirectAttributes.addFlashAttribute("errorMsg",
                    "Loi import: " + ex.getMessage());
        }
        return "redirect:/nguoi-dung";
    }
}
