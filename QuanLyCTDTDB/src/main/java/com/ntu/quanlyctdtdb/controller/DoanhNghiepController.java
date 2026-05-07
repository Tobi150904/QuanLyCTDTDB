package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.DoanhNghiepCuaToiDTO;
import com.ntu.quanlyctdtdb.dto.DoanhNghiepDTO;
import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.DoanhNghiepService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller cho Doanh Nghiep.
 *
 * <p>Quyen (docs/03 §"SO DO TONG HOP QUYEN"):
 * <ul>
 *   <li>PDT, TTDTXS, ADMIN : RW (CRUD + toggle trang thai).</li>
 *   <li>DOANH_NGHIEP       : RW <b>chi cho chinh DN cua minh</b> qua
 *                            route {@code /doanh-nghiep/cua-toi}
 *                            (Bug-fix A2 — chi update Email/SDT/DiaChi/NguoiDaiDien).</li>
 * </ul>
 *
 * <p>Bug-fix A1 (P1) — defense-in-depth: tat ca handler co {@code @PreAuthorize}
 * method-level (truoc day chi co URL-level guard tai SecurityConfig). Neu sau
 * nay URL rule duoc mo rong (vd them DOANH_NGHIEP cho /doanh-nghiep/**), cac
 * handler quan ly toan he thong (sua/xoa DN khac) van bi chan o method-level.</p>
 */
@Controller
@RequestMapping("/doanh-nghiep")
@RequiredArgsConstructor
public class DoanhNghiepController {

    private static final String ACTIVE_MENU = "doanh-nghiep";

    private final DoanhNghiepService doanhNghiepService;

    /* ====================== DANH SACH ====================== */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
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

    /* ====================== DN TU XEM/SUA THONG TIN CHINH MINH ====================== */
    /**
     * Bug-fix A2 (P1) — DN tu xem/sua thong tin lien he cua chinh DN minh.
     *
     * <p>Quyen: chi ROLE_DOANH_NGHIEP. ADMIN khong vao route nay (dung route
     * {@code /sua/{ma}} de quan tri toan he thong).</p>
     *
     * <p>maDN duoc lay tu {@code ud.nguoiDung.maDoanhNghiep} — KHONG nhan tu
     * URL/form parameter (chong DN doi sang DN khac qua hidden field).</p>
     */
    @PreAuthorize("hasRole('DOANH_NGHIEP')")
    @GetMapping("/cua-toi")
    public String cuaToiForm(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        if (ud == null || ud.getNguoiDung() == null
                || ud.getNguoiDung().getLoaiNguoiDung() != LoaiNguoiDung.DoanhNghiep) {
            throw new AccessDeniedException("Tinh nang nay chi danh cho tai khoan doanh nghiep.");
        }
        String maDN = ud.getNguoiDung().getMaDoanhNghiep();
        if (maDN == null || maDN.isBlank()) {
            throw new AccessDeniedException("Tai khoan doanh nghiep cua ban chua duoc gan voi ho so DN.");
        }
        DoanhNghiep dn = doanhNghiepService.findById(maDN);
        DoanhNghiepCuaToiDTO dto = new DoanhNghiepCuaToiDTO();
        dto.setNguoiDaiDien(dn.getNguoiDaiDien());
        dto.setEmail(dn.getEmail());
        dto.setSoDienThoai(dn.getSoDienThoai());
        dto.setDiaChiDN(dn.getDiaChiDN());

        model.addAttribute("doanhNghiepCuaToiDTO", dto);
        model.addAttribute("doanhNghiep", dn);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "doanh-nghiep/form-cua-toi";
    }

    @PreAuthorize("hasRole('DOANH_NGHIEP')")
    @PostMapping("/cua-toi")
    public String cuaToiSua(@AuthenticationPrincipal CustomUserDetails ud,
                            @Valid @ModelAttribute("doanhNghiepCuaToiDTO") DoanhNghiepCuaToiDTO dto,
                            BindingResult br,
                            Model model,
                            RedirectAttributes ra) {
        if (ud == null || ud.getNguoiDung() == null
                || ud.getNguoiDung().getLoaiNguoiDung() != LoaiNguoiDung.DoanhNghiep) {
            throw new AccessDeniedException("Tinh nang nay chi danh cho tai khoan doanh nghiep.");
        }
        String maDN = ud.getNguoiDung().getMaDoanhNghiep();
        if (maDN == null || maDN.isBlank()) {
            throw new AccessDeniedException("Tai khoan doanh nghiep cua ban chua duoc gan voi ho so DN.");
        }

        if (br.hasErrors()) {
            model.addAttribute("doanhNghiep", doanhNghiepService.findById(maDN));
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "doanh-nghiep/form-cua-toi";
        }
        try {
            // Force maDN = current — DN khong duoc doi sang DN khac qua hidden field.
            doanhNghiepService.updateThongTinLienHe(maDN, dto);
            ra.addFlashAttribute("successMsg", "Da cap nhat thong tin lien he cua doanh nghiep.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/doanh-nghiep/cua-toi";
    }

    /* ====================== CHI TIET ====================== */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @GetMapping("/chi-tiet/{ma}")
    public String chiTiet(@PathVariable String ma, Model model) {
        model.addAttribute("doanhNghiep", doanhNghiepService.findById(ma));
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "doanh-nghiep/chi-tiet";
    }

    /* ====================== THEM MOI ====================== */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
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

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
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
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
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

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
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
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
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
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
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
