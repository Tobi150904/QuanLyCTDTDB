package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.DoiNguGvDTO;
import com.ntu.quanlyctdtdb.dto.HocPhanDTO;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.DoiNguGvService;
import com.ntu.quanlyctdtdb.service.HocPhanService;
import com.ntu.quanlyctdtdb.util.FileStorageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final DoiNguGvService doiNguService;

    @ModelAttribute("activeMenu")
    public String activeMenu() { return "hoc-phan"; }

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
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
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
            model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
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
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
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
            model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
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

    /* ====================== GUI CHO DUYET ====================== */
    @PostMapping("/gui-cho-duyet/{ma}")
    public String guiChoDuyet(@PathVariable String ma, RedirectAttributes ra) {
        try {
            hocPhanService.guiChoDuyet(ma);
            ra.addFlashAttribute("successMsg", "Da gui hoc phan cho phe duyet!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan";
    }

    /* ====================== PHE DUYET ====================== */
    // Chi TTDTXS hoac ADMIN moi duoc phe duyet Hoc Phan (docs/02 §4, review P0-4).
    // SecurityConfig cho phep ca CNHP vao /hoc-phan/**, can block cap method
    // de defence-in-depth.
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
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
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
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
        // Doi ngu GV cho HP — fetch ben service de trang chi-tiet render
        // duoc ho ten GV (open-in-view=false).
        model.addAttribute("doiNguList", doiNguService.findByHocPhan(ma));
        model.addAttribute("doiNguDTO", new DoiNguGvDTO());
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        return "hoc-phan/chi-tiet";
    }

    /* ====================== DOI NGU GV CUA HP ====================== */
    @PostMapping("/chi-tiet/{ma}/doi-ngu/them")
    public String themDoiNgu(@PathVariable String ma,
                              @Valid @ModelAttribute DoiNguGvDTO dto,
                              BindingResult br,
                              RedirectAttributes ra) {
        // Force maHocPhan theo path de tranh tampering
        dto.setMaHocPhan(ma);
        if (br.hasErrors()) {
            ra.addFlashAttribute("errorMsg", "Du lieu khong hop le");
            return "redirect:/hoc-phan/chi-tiet/" + ma;
        }
        try {
            doiNguService.them(dto);
            ra.addFlashAttribute("successMsg", "Da them GV vao doi ngu!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan/chi-tiet/" + ma;
    }

    @PostMapping("/chi-tiet/{ma}/doi-ngu/toggle")
    public String toggleDoiNgu(@PathVariable String ma,
                                @RequestParam String maGV,
                                RedirectAttributes ra) {
        try {
            doiNguService.toggleTrangThai(ma, maGV);
            ra.addFlashAttribute("successMsg", "Da cap nhat trang thai GV trong doi ngu.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan/chi-tiet/" + ma;
    }

    @PostMapping("/chi-tiet/{ma}/doi-ngu/xoa")
    public String xoaDoiNgu(@PathVariable String ma,
                             @RequestParam String maGV,
                             RedirectAttributes ra) {
        try {
            doiNguService.xoa(ma, maGV);
            ra.addFlashAttribute("successMsg", "Da xoa GV khoi doi ngu.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/hoc-phan/chi-tiet/" + ma;
    }
}
