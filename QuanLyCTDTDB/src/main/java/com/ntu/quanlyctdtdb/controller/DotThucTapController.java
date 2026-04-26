package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.DotThucTapDTO;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.DotThucTapService;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/thuc-tap")
@RequiredArgsConstructor
public class DotThucTapController {

    private final DotThucTapService dotTTService;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final HocPhanRepository hocPhanRepo;
    private final HocKyNamHocRepository hocKyRepo;

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSach", dotTTService.findAll());
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/danh-sach";
    }

    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("dotTTDTO", new DotThucTapDTO());
        populateModel(model);
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/form";
    }

    @PostMapping("/them")
    public String them(@Valid @ModelAttribute DotThucTapDTO dto,
                        BindingResult br,
                        @AuthenticationPrincipal CustomUserDetails ud,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            populateModel(model);
            model.addAttribute("activeMenu", "thuc-tap");
            return "thuc-tap/form";
        }
        try {
            dotTTService.create(dto, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Da tao dot thuc tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap";
    }

    @GetMapping("/sua/{id}")
    public String suaForm(@PathVariable Integer id, Model model) {
        var dot = dotTTService.findById(id);
        DotThucTapDTO dto = new DotThucTapDTO();
        dto.setTenDotTT(dot.getTenDotTT());
        dto.setMaCTDT(dot.getCtdtHocPhan() != null ? dot.getCtdtHocPhan().getId().getMaCTDT() : null);
        dto.setMaHocPhan(dot.getCtdtHocPhan() != null ? dot.getCtdtHocPhan().getId().getMaHocPhan() : null);
        dto.setMaHocKy(dot.getHocKy() != null ? dot.getHocKy().getMaHocKy() : null);
        dto.setNgayBatDau(dot.getNgayBatDau());
        dto.setNgayKetThuc(dot.getNgayKetThuc());
        model.addAttribute("dotTTDTO", dto);
        model.addAttribute("dotId", id);
        populateModel(model);
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/form";
    }

    @PostMapping("/sua/{id}")
    public String sua(@PathVariable Integer id,
                       @Valid @ModelAttribute DotThucTapDTO dto,
                       BindingResult br,
                       Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("dotId", id);
            populateModel(model);
            model.addAttribute("activeMenu", "thuc-tap");
            return "thuc-tap/form";
        }
        try {
            dotTTService.update(id, dto);
            ra.addFlashAttribute("successMsg", "Da cap nhat dot thuc tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap";
    }

    @PostMapping("/gui-phe-duyet/{id}")
    public String guiPheDuyet(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotTTService.guiPheDuyet(id);
            ra.addFlashAttribute("successMsg", "Da gui yeu cau phe duyet.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap";
    }

    // Phe duyet dot thuc tap — chi TTDTXS hoac ADMIN (review P0-4).
    // URL rule cho PDT, GV, CVHT, DN, SV cung vao /thuc-tap/**, nen phai chan cap method.
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
    @PostMapping("/phe-duyet/{id}")
    public String pheduyet(@PathVariable Integer id,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            RedirectAttributes ra) {
        try {
            dotTTService.pheduyet(id, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Da phe duyet dot thuc tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap";
    }

    @GetMapping("/chi-tiet/{id}")
    public String chiTiet(@PathVariable Integer id, Model model) {
        model.addAttribute("dot", dotTTService.findById(id));
        model.addAttribute("danhSachSV", dotTTService.findDanhSachSV(id));
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/chi-tiet";
    }

    @PostMapping("/chi-tiet/{id}/them-sv")
    public String themSV(@PathVariable Integer id,
                          @RequestParam String maSVList,
                          RedirectAttributes ra) {
        try {
            List<String> dsSV = Arrays.asList(maSVList.split("[,\\n]"));
            var result = dotTTService.importSinhVien(id, dsSV.stream()
                    .map(String::trim).filter(s -> !s.isBlank()).toList());
            ra.addFlashAttribute("importResult", result);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    @PostMapping("/chi-tiet/{id}/cap-nhat-kq/{maDanhSach}")
    public String capNhatKetQua(@PathVariable Integer id,
                                  @PathVariable Integer maDanhSach,
                                  @RequestParam(required = false) String loaiThucTap,
                                  @RequestParam(required = false) String maDoanhNghiep,
                                  @RequestParam(required = false) String nhanXet,
                                  RedirectAttributes ra) {
        try {
            dotTTService.capNhatKetQua(maDanhSach, loaiThucTap, maDoanhNghiep, nhanXet);
            ra.addFlashAttribute("successMsg", "Da cap nhat ket qua sinh vien.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    private void populateModel(Model model) {
        model.addAttribute("ctdtList", ctdtRepo.findAll());
        model.addAttribute("hocPhanList", hocPhanRepo.findAllDaDuyet());
        model.addAttribute("hocKyList", hocKyRepo.findAllByOrderByNgayBatDauDesc());
    }
}
