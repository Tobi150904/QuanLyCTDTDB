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
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quan ly Hoc Phan.
 * Role (docs/03 §"SO DO TONG HOP QUYEN" + §WF-03.*):
 *   - CNHP       : RW (tao/sua/gui-duyet, quan ly doi ngu GV)
 *   - TTDTXS     : W duyet/tu choi + R toan bo
 *   - PDT        : R  (theo doi qua trinh tao HP cua cac CNHP)
 *   - ADMIN      : RW (super-user)
 *   - GiangVien  : R  (xem cac HP minh day / HP cua CTDT lien quan)
 *   - SinhVien   : R  (tra cuu thong tin HP truoc khi dang ky)
 * Class-level cho doc, method-level chan writes theo dung matrix.
 */
@Slf4j
@Controller
@RequestMapping("/hoc-phan")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN','GIANG_VIEN','SINH_VIEN')")
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
    // Tao + sua + upload de cuong la nghiep vu cua CNHP (TTDTXS/ADMIN co the
    // can thiep khi can). SV/GV/PDT chi READ — phai chan 403 o day.
    @PreAuthorize("hasAnyRole('CNHP','TTDTXS','ADMIN')")
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("hocPhanDTO", new HocPhanDTO());
        model.addAttribute("loaiHPList", LoaiHocPhan.values());
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        model.addAttribute("isEdit", false);
        return "hoc-phan/form";
    }

    @PreAuthorize("hasAnyRole('CNHP','TTDTXS','ADMIN')")
    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("hocPhanDTO") HocPhanDTO dto,
                        BindingResult br,
                        @RequestParam(value = "fileDeCuong", required = false) MultipartFile file,
                        @AuthenticationPrincipal CustomUserDetails userDetails,
                        Model model, RedirectAttributes ra) {
        // Log day du de developer xac dinh vi sao form khong tao duoc HP.
        // Truoc day neu binding/validation fail, controller return form ma
        // khong log gi - user lan lon vi "khong bao console, khong tao duoc".
        log.info("POST /hoc-phan/them dto={}", dto);
        if (br.hasErrors()) {
            log.warn("Validation errors khi tao HocPhan: {}",
                    br.getAllErrors().stream()
                            .map(err -> err.getDefaultMessage()).toList());
            // Gom loi thanh 1 chuoi ngan de hien thi o banner phia tren form.
            String msg = br.getAllErrors().stream()
                    .map(err -> err.getDefaultMessage())
                    .filter(s -> s != null && !s.isBlank())
                    .distinct()
                    .reduce((a, b) -> a + "; " + b).orElse("Du lieu khong hop le.");
            model.addAttribute("errorMsg", msg);
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
            log.error("Loi khi tao HocPhan dto={}: {}", dto, e.getMessage(), e);
            ra.addFlashAttribute("errorMsg",
                    e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
        }
        return "redirect:/hoc-phan";
    }

    /* ====================== CHINH SUA ====================== */
    @PreAuthorize("hasAnyRole('CNHP','TTDTXS','ADMIN')")
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

    @PreAuthorize("hasAnyRole('CNHP','TTDTXS','ADMIN')")
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
    @PreAuthorize("hasAnyRole('CNHP','TTDTXS','ADMIN')")
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
    @PreAuthorize("hasAnyRole('CNHP','TTDTXS','ADMIN')")
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
    // Quan ly doi ngu: CNHP, TTDTXS, PDT, ADMIN (docs/03 WF-03.2).
    // PDT co quyen them GV vao doi ngu de ho tro CNHP khi can.
    @PreAuthorize("hasAnyRole('PDT','CNHP','TTDTXS','ADMIN')")
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

    @PreAuthorize("hasAnyRole('PDT','CNHP','TTDTXS','ADMIN')")
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

    @PreAuthorize("hasAnyRole('PDT','CNHP','TTDTXS','ADMIN')")
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
