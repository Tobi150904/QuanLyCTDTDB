package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.HocPhanDTO;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.HocPhanService;
import com.ntu.quanlyctdtdb.util.FileStorageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/hoc-phan")
@RequiredArgsConstructor
public class HocPhanController {

    private final HocPhanService hocPhanService;
    private final GiangVienRepository giangVienRepo;
    private final FileStorageUtil fileStorageUtil;

    /* ====================== DANH SACH ====================== */
    @GetMapping
    public String danhSach(@RequestParam(defaultValue = "") String keyword, Model model) {
        model.addAttribute("danhSach", hocPhanService.findAll(keyword));
        model.addAttribute("keyword", keyword);
        return "hoc-phan/danh-sach";
    }

    /* ====================== THEM MOI ====================== */
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("hocPhanDTO", new HocPhanDTO());
        model.addAttribute("loaiHPList", LoaiHocPhan.values());
        model.addAttribute("giangVienList", giangVienRepo.findAll());
        model.addAttribute("isEdit", false);
        return "hoc-phan/form";
    }

    @PostMapping("/them")
    public String them(@Valid @ModelAttribute HocPhanDTO dto,
                        BindingResult br,
                        @RequestParam(value = "fileDeCuong", required = false) MultipartFile file,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("loaiHPList", LoaiHocPhan.values());
            model.addAttribute("giangVienList", giangVienRepo.findAll());
            model.addAttribute("isEdit", false);
            return "hoc-phan/form";
        }
        try {
            HocPhan hp = hocPhanService.create(dto, userDetails.getMaNguoiDung());
            if (file != null && !file.isEmpty()) {
                String path = fileStorageUtil.saveFile(file, "de-cuong", hp.getMaHocPhan());
                hocPhanService.uploadDeCuong(hp.getMaHocPhan(), path);
            }
            ra.addFlashAttribute("successMsg", "Tao hoc phan thanh cong! Cho phe duyet.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan";
    }

    /* ====================== CHINH SUA ====================== */
    @GetMapping("/sua/{ma}")
    public String suaForm(@PathVariable String ma, Model model) {
        HocPhan hp = hocPhanService.findById(ma);
        HocPhanDTO dto = new HocPhanDTO();
        dto.setMaHocPhan(hp.getMaHocPhan());
        dto.setTenHocPhan(hp.getTenHocPhan());
        dto.setSoTinChi(hp.getSoTinChi());
        dto.setLoaiHocPhan(hp.getLoaiHocPhan());
        dto.setMaChuNhiemHP(hp.getChuNhiemHP() != null ? hp.getChuNhiemHP().getMaGV() : null);
        model.addAttribute("hocPhanDTO", dto);
        model.addAttribute("loaiHPList", LoaiHocPhan.values());
        model.addAttribute("giangVienList", giangVienRepo.findAll());
        model.addAttribute("isEdit", true);
        return "hoc-phan/form";
    }

    @PostMapping("/sua/{ma}")
    public String sua(@PathVariable String ma,
                       @Valid @ModelAttribute HocPhanDTO dto,
                       BindingResult br,
                       @RequestParam(value = "fileDeCuong", required = false) MultipartFile file,
                       Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("loaiHPList", LoaiHocPhan.values());
            model.addAttribute("giangVienList", giangVienRepo.findAll());
            model.addAttribute("isEdit", true);
            return "hoc-phan/form";
        }
        try {
            hocPhanService.update(ma, dto);
            if (file != null && !file.isEmpty()) {
                String path = fileStorageUtil.saveFile(file, "de-cuong", ma);
                hocPhanService.uploadDeCuong(ma, path);
            }
            ra.addFlashAttribute("successMsg", "Cap nhat hoc phan thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan";
    }

    /* ====================== PHE DUYET ====================== */
    @PostMapping("/phe-duyet/{ma}")
    public String pheduyet(@PathVariable String ma,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            RedirectAttributes ra) {
        try {
            hocPhanService.pheduyet(ma, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Phe duyet hoc phan thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan";
    }

    /* ====================== TU CHOI ====================== */
    @PostMapping("/tu-choi/{ma}")
    public String tuChoi(@PathVariable String ma,
                          @RequestParam String lyDo,
                          @AuthenticationPrincipal CustomUserDetails ud,
                          RedirectAttributes ra) {
        try {
            hocPhanService.tuChoi(ma, lyDo, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Da tu choi hoc phan!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan";
    }

    /* ====================== TOGGLE ====================== */
    @PostMapping("/toggle/{ma}")
    public String toggle(@PathVariable String ma, RedirectAttributes ra) {
        try {
            hocPhanService.toggleTrangThai(ma);
            ra.addFlashAttribute("successMsg", "Cap nhat trang thai thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan";
    }

    /* ====================== CHI TIET ====================== */
    @GetMapping("/chi-tiet/{ma}")
    public String chiTiet(@PathVariable String ma, Model model) {
        model.addAttribute("hocPhan", hocPhanService.findById(ma));
        return "hoc-phan/chi-tiet";
    }
}
