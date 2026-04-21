package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.DotKienTapDTO;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.DotKienTapService;
import com.ntu.quanlyctdtdb.repository.DoanhNghiepRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/kien-tap")
@RequiredArgsConstructor
public class DotKienTapController {

    private final DotKienTapService dotKTService;
    private final LopHanhChinhRepository lopHCRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final GiangVienRepository giangVienRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSach", dotKTService.findAll());
        return "kien-tap/danh-sach";
    }

    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("dotKTDTO", new DotKienTapDTO());
        populateModel(model);
        return "kien-tap/form";
    }

    @PostMapping("/them")
    public String them(@Valid @ModelAttribute DotKienTapDTO dto,
                        BindingResult br,
                        @AuthenticationPrincipal CustomUserDetails ud,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            populateModel(model);
            return "kien-tap/form";
        }
        try {
            dotKTService.create(dto, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Tao dot kien tap thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap";
    }

    @GetMapping("/sua/{id}")
    public String suaForm(@PathVariable Integer id, Model model) {
        var dot = dotKTService.findById(id);
        DotKienTapDTO dto = new DotKienTapDTO();
        dto.setTenDotKT(dot.getTenDotKT());
        dto.setMaLopHC(dot.getLopHanhChinh() != null ? dot.getLopHanhChinh().getMaLopHC() : null);
        dto.setMaHocKy(dot.getHocKy() != null ? dot.getHocKy().getMaHocKy() : null);
        dto.setThoiGian(dot.getThoiGian());
        dto.setMaGVPhuTrach(dot.getGvPhuTrach() != null ? dot.getGvPhuTrach().getMaGV() : null);
        dto.setMaDoanhNghiep(dot.getDoanhNghiep() != null ? dot.getDoanhNghiep().getMaDoanhNghiep() : null);
        dto.setKinhPhiChung(dot.getKinhPhiChung());
        dto.setKinhPhiTungSV(dot.getKinhPhiTungSV());
        model.addAttribute("dotKTDTO", dto);
        model.addAttribute("dotId", id);
        populateModel(model);
        return "kien-tap/form";
    }

    @PostMapping("/sua/{id}")
    public String sua(@PathVariable Integer id,
                       @Valid @ModelAttribute DotKienTapDTO dto,
                       BindingResult br,
                       Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("dotId", id);
            populateModel(model);
            return "kien-tap/form";
        }
        try {
            dotKTService.update(id, dto);
            ra.addFlashAttribute("successMsg", "Cap nhat dot kien tap thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap";
    }

    @PostMapping("/gui-phe-duyet/{id}")
    public String guiPheDuyet(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotKTService.guiPheDuyet(id);
            ra.addFlashAttribute("successMsg", "Da gui yeu cau phe duyet!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap";
    }

    @PostMapping("/phe-duyet/{id}")
    public String pheduyet(@PathVariable Integer id,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            RedirectAttributes ra) {
        try {
            dotKTService.pheduyet(id, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Phe duyet dot kien tap thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap";
    }

    @GetMapping("/chi-tiet/{id}")
    public String chiTiet(@PathVariable Integer id, Model model) {
        model.addAttribute("dot", dotKTService.findById(id));
        model.addAttribute("danhSachSV", dotKTService.findDanhSachSVKienTap(id));
        return "kien-tap/chi-tiet";
    }

    @PostMapping("/chi-tiet/{id}/them-sv")
    public String themSV(@PathVariable Integer id,
                          @RequestParam String maSVList,
                          RedirectAttributes ra) {
        try {
            List<String> dsSV = Arrays.asList(maSVList.split("[,\\n]"));
            var result = dotKTService.importSinhVienTuExcel(id, dsSV.stream()
                    .map(String::trim).filter(s -> !s.isBlank()).toList());
            ra.addFlashAttribute("importResult", result);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    private void populateModel(Model model) {
        model.addAttribute("lopHCList", lopHCRepo.findAll());
        model.addAttribute("hocKyList", hocKyRepo.findAllByOrderByNgayBatDauDesc());
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        model.addAttribute("doanhNghiepList", doanhNghiepRepo.findAll());
    }
}
