package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.DotKienTapDTO;
import com.ntu.quanlyctdtdb.entity.DotKienTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.repository.DoanhNghiepRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.DotKienTapService;
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

/**
 * Controller cho DotKienTap. Quy tac day du: docs/03 WF-07.*
 * activeMenu = "kien-tap" tren moi GET handler.
 *
 * Role (docs/03 §"SO DO TONG HOP QUYEN" + §WF-07.*):
 *   - TTDTXS, CNHP, ADMIN : RW (tao, duyet, quan ly SV, toggle DaThamGia)
 *   - PDT                 : R  (theo doi tong hop)
 *   - GiangVien           : R + W nhan-xet-gv (chi cho dot minh phu trach)
 *   - DoanhNghiep         : R + W nhan-xet-dn (chi cho dot tai DN minh)
 *   - SinhVien            : R  dot minh tham gia (GET /kien-tap/chi-tiet/{id})
 * Class-level cho doc, write-level qua @PreAuthorize method-level.
 *
 * [Phase 5 — P0-1 docs/08] Templates kien-tap/ chua ton tai. URL rule va
 * controller guards san sang cho khi module duoc hoan thien.
 */
@Controller
@RequestMapping("/kien-tap")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN','GIANG_VIEN','DOANH_NGHIEP','SINH_VIEN')")
public class DotKienTapController {

    private final DotKienTapService dotKTService;
    private final LopHanhChinhRepository lopHCRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final GiangVienRepository giangVienRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;

    // =========================================================================
    // LIST + CRUD
    // =========================================================================

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSach", dotKTService.findAll());
        // Phase 3 — stat-card row dong bo voi thuc-tap/hoc-ky/lop-hanh-chinh.
        // Truoc day count duoc tinh inline trong template (#lists.size +
        // .?[trangThai==…]) — chuyen sang COUNT() o DB de tach logic.
        model.addAttribute("thongKe", dotKTService.getThongKe());
        model.addAttribute("activeMenu", "kien-tap");
        return "kien-tap/danh-sach";
    }

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("dotKTDTO", new DotKienTapDTO());
        populateModel(model);
        model.addAttribute("activeMenu", "kien-tap");
        return "kien-tap/form";
    }

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("dotKTDTO") DotKienTapDTO dto,
                       BindingResult br,
                       @RequestParam(value = "fileMinhChung", required = false) MultipartFile fileMinhChung,
                       @AuthenticationPrincipal CustomUserDetails ud,
                       Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            populateModel(model);
            model.addAttribute("activeMenu", "kien-tap");
            return "kien-tap/form";
        }
        try {
            DotKienTap saved = dotKTService.create(dto, fileMinhChung, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg",
                    "Da tao dot kien tap. He thong tu dong them sinh vien DangHoc cua lop vao danh sach.");
            return "redirect:/kien-tap/chi-tiet/" + saved.getMaDotKT();
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/kien-tap/them";
        }
    }

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @GetMapping("/sua/{id}")
    public String suaForm(@PathVariable Integer id, Model model) {
        DotKienTap dot = dotKTService.findById(id);
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
        model.addAttribute("dotFile", dot.getFileMinhChung());
        populateModel(model);
        model.addAttribute("activeMenu", "kien-tap");
        return "kien-tap/form";
    }

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @PostMapping("/sua/{id}")
    public String sua(@PathVariable Integer id,
                      @Valid @ModelAttribute("dotKTDTO") DotKienTapDTO dto,
                      BindingResult br,
                      @RequestParam(value = "fileMinhChung", required = false) MultipartFile fileMinhChung,
                      Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("dotId", id);
            populateModel(model);
            model.addAttribute("activeMenu", "kien-tap");
            return "kien-tap/form";
        }
        try {
            dotKTService.update(id, dto, fileMinhChung);
            ra.addFlashAttribute("successMsg", "Da cap nhat dot kien tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    @GetMapping("/chi-tiet/{id}")
    public String chiTiet(@PathVariable Integer id, Model model,
                          @AuthenticationPrincipal CustomUserDetails ud) {
        DotKienTap dot = dotKTService.findById(id);
        model.addAttribute("dot", dot);
        model.addAttribute("danhSachSV", dotKTService.findDanhSachSVKienTap(id));

        // Flag dieu kien hien thi form nhan xet
        boolean laGVPhuTrach = dot.getGvPhuTrach() != null
                && ud != null
                && ud.getMaNguoiDung() != null
                && ud.getMaNguoiDung().equals(dot.getGvPhuTrach().getMaGV());
        boolean laDNTiepDon = dot.getDoanhNghiep() != null
                && ud != null
                && ud.getMaNguoiDung() != null
                && ud.getMaNguoiDung().equals(dot.getDoanhNghiep().getMaDoanhNghiep());
        model.addAttribute("laGVPhuTrach", laGVPhuTrach);
        model.addAttribute("laDNTiepDon", laDNTiepDon);
        model.addAttribute("activeMenu", "kien-tap");
        return "kien-tap/chi-tiet";
    }

    // =========================================================================
    // STATE TRANSITIONS
    // =========================================================================

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @PostMapping("/gui-phe-duyet/{id}")
    public String guiPheDuyet(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotKTService.guiPheDuyet(id);
            ra.addFlashAttribute("successMsg", "Da gui yeu cau phe duyet.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    // Phe duyet dot kien tap — chi TTDTXS hoac ADMIN (review P0-4).
    // URL rule cho GV, CNHP, DN cung vao /kien-tap/**, nen phai guard o method.
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
    @PostMapping("/phe-duyet/{id}")
    public String pheduyet(@PathVariable Integer id,
                           @AuthenticationPrincipal CustomUserDetails ud,
                           RedirectAttributes ra) {
        try {
            dotKTService.pheduyet(id, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Da phe duyet dot kien tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @PostMapping("/hoan-thanh/{id}")
    public String hoanThanh(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotKTService.hoanThanh(id);
            ra.addFlashAttribute("successMsg", "Da chuyen dot sang trang thai DaThucHien.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @PostMapping("/huy/{id}")
    public String huy(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotKTService.huy(id);
            ra.addFlashAttribute("successMsg", "Da huy dot kien tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    // =========================================================================
    // WF-07.2: Toggle DaThamGia
    // =========================================================================

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @PostMapping("/chi-tiet/{id}/sv/{maSV}/danh-dau")
    public String capNhatDaThamGia(@PathVariable Integer id,
                                   @PathVariable String maSV,
                                   @RequestParam boolean daThamGia,
                                   RedirectAttributes ra) {
        try {
            dotKTService.capNhatDaThamGia(id, maSV, daThamGia);
            ra.addFlashAttribute("successMsg",
                    "Da cap nhat trang thai tham gia cho SV " + maSV + ".");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    // =========================================================================
    // WF-07.3: Dong bo danh sach SV
    // =========================================================================

    @PreAuthorize("hasAnyRole('TTDTXS','CNHP','ADMIN')")
    @PostMapping("/chi-tiet/{id}/dong-bo")
    public String dongBo(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            int added = dotKTService.dongBoDanhSachSV(id);
            if (added == 0) {
                ra.addFlashAttribute("infoMsg",
                        "Danh sach da dong bo - khong co sinh vien DangHoc moi nao can them.");
            } else {
                ra.addFlashAttribute("successMsg",
                        "Da them " + added + " sinh vien moi vao danh sach kien tap.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    // =========================================================================
    // WF-07.4: Nhan xet
    // =========================================================================

    // Nhan xet GV: chi GIANG_VIEN moi co the goi (service co them double-check
    // chinh xac la GV phu trach cua dot — defense-in-depth).
    @PreAuthorize("hasAnyRole('GIANG_VIEN','ADMIN')")
    @PostMapping("/nhan-xet-gv/{id}")
    public String nhanXetGV(@PathVariable Integer id,
                            @RequestParam String nhanXet,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            RedirectAttributes ra) {
        try {
            dotKTService.nhanXetGV(id, ud.getMaNguoiDung(), nhanXet);
            ra.addFlashAttribute("successMsg", "Da luu nhan xet cua giang vien phu trach.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    // Nhan xet DN: chi DOANH_NGHIEP moi co the goi.
    @PreAuthorize("hasAnyRole('DOANH_NGHIEP','ADMIN')")
    @PostMapping("/nhan-xet-dn/{id}")
    public String nhanXetDN(@PathVariable Integer id,
                            @RequestParam String nhanXet,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            RedirectAttributes ra) {
        try {
            dotKTService.nhanXetDN(id, ud.getMaNguoiDung(), nhanXet);
            ra.addFlashAttribute("successMsg", "Da luu nhan xet cua doanh nghiep.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/kien-tap/chi-tiet/" + id;
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private void populateModel(Model model) {
        model.addAttribute("lopHCList", lopHCRepo.findAll());
        model.addAttribute("hocKyList", hocKyRepo.findAllByOrderByNgayBatDauDesc());
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        // Chi list DN DangHopTac - docs/02 §3.7. Su dung index query thay vi
        // findAll().stream().filter() de tranh quet bang n+1 voi data lon.
        model.addAttribute("doanhNghiepList",
                doanhNghiepRepo.findByTrangThai(TrangThaiDoanhNghiep.DangHopTac));
    }
}
