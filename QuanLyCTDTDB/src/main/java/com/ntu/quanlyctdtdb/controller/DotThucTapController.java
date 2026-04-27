package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.DotThucTapDTO;
import com.ntu.quanlyctdtdb.entity.DotThucTap;
import com.ntu.quanlyctdtdb.enums.LoaiThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.DoanhNghiepRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.DotThucTapService;
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

/**
 * Controller cho DotThucTap. Quy tac day du: docs/03 §WF-08.*
 *
 * <p>Role (docs/03 §"SO DO TONG HOP QUYEN"):</p>
 * <ul>
 *   <li>PDT, TTDTXS, ADMIN: RW (tao, sua, gui duyet, them SV).</li>
 *   <li>TTDTXS, ADMIN: phe duyet rieng.</li>
 *   <li>GV, CVHT, DN: W cap-nhat-kq (cho SV minh phu trach), R chi-tiet.</li>
 *   <li>SV: R chi-tiet (dot minh tham gia).</li>
 * </ul>
 *
 * <p>Class-level @PreAuthorize chan tat ca request den /thuc-tap/** chi cho
 * cac role co tham chieu nghiep vu. Method-level fine-grained guard:</p>
 * <ul>
 *   <li>Tao/sua/gui-duyet/them-sv : PDT, TTDTXS, ADMIN.</li>
 *   <li>Phe duyet                : TTDTXS, ADMIN.</li>
 *   <li>Cap nhat ket qua          : PDT, TTDTXS, ADMIN, GIANG_VIEN, CVHT, DOANH_NGHIEP.</li>
 *   <li>Danh sach + chi tiet      : tat ca role tren + SINH_VIEN (read).</li>
 * </ul>
 */
@Controller
@RequestMapping("/thuc-tap")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN','GIANG_VIEN','CVHT','DOANH_NGHIEP','SINH_VIEN')")
public class DotThucTapController {

    private final DotThucTapService dotTTService;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final HocPhanRepository hocPhanRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSach", dotTTService.findAll());
        // Phase 3 — stat-card row dong bo voi kien-tap. Truoc day count tinh
        // inline trong template (#lists.size + .?[trangThai==…]) — chuyen
        // sang COUNT() o DB de tach logic.
        model.addAttribute("thongKe", dotTTService.getThongKe());
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/danh-sach";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("dotTTDTO", new DotThucTapDTO());
        populateModel(model);
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/form";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("dotTTDTO") DotThucTapDTO dto,
                        BindingResult br,
                        @AuthenticationPrincipal CustomUserDetails ud,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            populateModel(model);
            model.addAttribute("activeMenu", "thuc-tap");
            return "thuc-tap/form";
        }
        try {
            // Bug fix Phase 5.2: redirect ve chi-tiet de user thay form import
            // SV ngay sau khi tao — truoc day chi redirect ve list mat 1 cu nhip.
            DotThucTap saved = dotTTService.create(dto, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg",
                    "Da tao dot thuc tap. Tiep tuc them sinh vien o phan ben duoi.");
            return "redirect:/thuc-tap/chi-tiet/" + saved.getMaDotTT();
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            // Re-render form de user khong mat input — giu errorMsg trong flash
            // de banner alert hien thi.
            return "redirect:/thuc-tap/them";
        }
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
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

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/sua/{id}")
    public String sua(@PathVariable Integer id,
                       @Valid @ModelAttribute("dotTTDTO") DotThucTapDTO dto,
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
            return "redirect:/thuc-tap/sua/" + id;
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/gui-phe-duyet/{id}")
    public String guiPheDuyet(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotTTService.guiPheDuyet(id);
            ra.addFlashAttribute("successMsg", "Da gui yeu cau phe duyet.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        // Stay on chi-tiet so user can see the new state immediately.
        return "redirect:/thuc-tap/chi-tiet/" + id;
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
        // Sau phe duyet -> ve chi-tiet de TTDTXS thay status moi va next-step.
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    @GetMapping("/chi-tiet/{id}")
    public String chiTiet(@PathVariable Integer id, Model model) {
        model.addAttribute("dot", dotTTService.findById(id));
        model.addAttribute("danhSachSV", dotTTService.findDanhSachSV(id));
        // DN list cho dropdown trong cap-nhat-kq + import — chi DN dang hop tac.
        model.addAttribute("doanhNghiepList",
                doanhNghiepRepo.findByTrangThai(TrangThaiDoanhNghiep.DangHopTac));
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/chi-tiet";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/chi-tiet/{id}/them-sv")
    public String themSV(@PathVariable Integer id,
                          @RequestParam String maSVList,
                          @RequestParam(required = false) String loaiThucTap,
                          @RequestParam(required = false) String maDoanhNghiep,
                          RedirectAttributes ra) {
        try {
            List<String> dsSV = Arrays.stream(maSVList.split("[,\\n;\\s]+"))
                    .map(String::trim).filter(s -> !s.isBlank()).toList();
            if (dsSV.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Vui long nhap it nhat 1 ma sinh vien.");
                return "redirect:/thuc-tap/chi-tiet/" + id;
            }

            // Default loai = Truong neu user khong chon. Truong hop chon
            // DoanhNghiep ma quen chon DN -> service se loi (rang buoc nghiep vu).
            LoaiThucTap loai = LoaiThucTap.Truong;
            if (loaiThucTap != null && !loaiThucTap.isBlank()) {
                try {
                    loai = LoaiThucTap.valueOf(loaiThucTap.trim());
                } catch (IllegalArgumentException ignore) { /* fallback Truong */ }
            }

            var result = dotTTService.importSinhVien(id, dsSV, loai, maDoanhNghiep);
            ra.addFlashAttribute("importResult", result);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    // Per spec: PDT/TTDTXS/ADMIN cap nhat hang loat; GV/CVHT/DN cap nhat
    // cho SV minh phu trach. Method-level @PreAuthorize chap nhan ca 6 role.
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN','GIANG_VIEN','CVHT','DOANH_NGHIEP')")
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
        // DN list cho form (truoc day chua co — gay form thieu dropdown
        // khi muon set DN tiep nhan ngay khi tao dot). Chi load DN dang hop tac.
        model.addAttribute("doanhNghiepList",
                doanhNghiepRepo.findByTrangThai(TrangThaiDoanhNghiep.DangHopTac));
    }
}
