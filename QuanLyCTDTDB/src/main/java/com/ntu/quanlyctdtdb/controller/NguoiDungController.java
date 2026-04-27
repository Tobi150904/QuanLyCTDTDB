package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.NguoiDungDTO;
import com.ntu.quanlyctdtdb.dto.NguoiDungExcelDTO;
import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.service.NguoiDungService;
import com.ntu.quanlyctdtdb.util.CsvExportUtil;
import com.ntu.quanlyctdtdb.util.ExcelImportUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller quan ly Nguoi Dung.
 * Role (docs/03 §"SO DO TONG HOP QUYEN"):
 *   - PDT     : RW (CRUD, import Excel, khoa/mo khoa)
 *   - TTDTXS  : R  (chi xem — phuc vu bao cao, theo doi)
 *   - ADMIN   : RW (super-user)
 * Class-level cho phep ca 3 role vao module. Write-level endpoint (them,
 * sua, xoa, toggle, import) duoc chan them bang @PreAuthorize method-level
 * chi cho PDT + ADMIN. TTDTXS co the browse nhung khong nhin thay nut "Them"
 * / "Sua" trong template (ca sidebar da dung sec:authorize + nut da dung
 * sec:authorize).
 */
@Controller
@RequestMapping("/nguoi-dung")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
public class NguoiDungController {

    private static final String ACTIVE_MENU = "nguoi-dung";

    private final NguoiDungService nguoiDungService;
    private final LopHanhChinhRepository lopHCRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;

    /**
     * Chuyen empty string "" thanh null de form binding. Cuc ky can thiet cho
     * field {@code matKhau}: khi sua nguoi dung, form submit matKhau="" -> neu
     * khong trim thanh null, {@code @Size(min=8)} fail ngay vi "" co length=0.
     * User lo phai nhap mat khau moi dung it nhat 8 ky tu moi luu duoc - sai
     * UX. Trim -> null -> {@code @Size} bo qua null -> service chi update
     * password khi value non-blank.
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    /* ====================== DANH SACH ====================== */
    /**
     * Phase 2 — server-side Pageable + Sort + page-size selector.
     * - Mac dinh sort theo hoTen ASC, size=15 (giu tuong thich URL cu).
     * - URL co the override:  ?sort=hoTen,desc&size=50&page=2
     * - Whitelist sort field (chong injection thuoc tinh tuy y va lo prop nhay cam):
     *   {hoTen, tenDangNhap, email, loaiNguoiDung, trangThaiTK, maNguoiDung}.
     */
    @GetMapping
    public String danhSach(@RequestParam(defaultValue = "") String keyword,
                            @RequestParam(required = false) LoaiNguoiDung loai,
                            @PageableDefault(size = 15, sort = "hoTen") Pageable pageable,
                            Model model) {
        Pageable safe = sanitizePageable(pageable);
        Page<NguoiDung> pages = nguoiDungService.search(keyword, loai, safe);
        model.addAttribute("pages", pages);
        model.addAttribute("keyword", keyword);
        model.addAttribute("loaiFilter", loai);
        model.addAttribute("loaiList", LoaiNguoiDung.values());
        model.addAttribute("thongKe", nguoiDungService.getThongKe());
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "nguoi-dung/danh-sach";
    }

    /* ====================== EXPORT CSV ====================== */
    /**
     * Xuat CSV theo dung filter hien tai (keyword + loai). Khong dung
     * pageable - lay full dataset hop voi filter, sort theo hoTen ASC de
     * deterministic.
     *
     * <p>Quyen: PDT, TTDTXS, ADMIN deu duoc export (read-only operation).
     */
    @GetMapping("/export")
    public void exportCsv(@RequestParam(defaultValue = "") String keyword,
                           @RequestParam(required = false) LoaiNguoiDung loai,
                           HttpServletResponse response) throws IOException {
        // Lay het record (Integer.MAX_VALUE) thay vi page nho - dataset nguoi
        // dung thuong duoi vai chuc ngan, du an toan voi memory.
        Pageable all = org.springframework.data.domain.PageRequest.of(
                0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("hoTen")));
        Page<NguoiDung> rows = nguoiDungService.search(keyword, loai, all);

        String[] headers = {
            "Ma", "Ho Ten", "Ten Dang Nhap", "Email", "So Dien Thoai",
            "Loai", "Vai Tro", "Trang Thai"
        };
        List<String[]> data = new ArrayList<>();
        for (NguoiDung nd : rows.getContent()) {
            String vaiTros = nd.getNhomNguoiDungs() == null ? "" :
                    nd.getNhomNguoiDungs().stream()
                            .map(n -> n.getId().getVaiTro().name())
                            .reduce((a, b) -> a + "; " + b).orElse("");
            data.add(CsvExportUtil.row(
                    nd.getMaNguoiDung(),
                    nd.getHoTen(),
                    nd.getTenDangNhap(),
                    nd.getEmail(),
                    nd.getSoDienThoai(),
                    nd.getLoaiNguoiDung() != null ? nd.getLoaiNguoiDung().name() : "",
                    vaiTros,
                    Boolean.TRUE.equals(nd.getTrangThaiTK()) ? "HoatDong" : "DaKhoa"
            ));
        }
        CsvExportUtil.write(response, "nguoi-dung", headers, data);
    }

    /* ====================== BULK TOGGLE ====================== */
    /**
     * Khoa/mo khoa hang loat tai khoan.
     * <p>Khong xoa cung — module Nguoi Dung dac biet quan trong (luu lich su
     * dang ky / phan cong), chi cho phep toggle TrangThaiTK, dong bo voi
     * single-toggle action.</p>
     *
     * <p>Action: "lock" → set false, "unlock" → set true. {@code ids} la
     * danh sach maNguoiDung lay tu checkbox bulk-select.</p>
     */
    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @PostMapping("/bulk-toggle")
    public String bulkToggle(@RequestParam(value = "ids", required = false) List<String> ids,
                              @RequestParam(defaultValue = "lock") String action,
                              RedirectAttributes ra) {
        if (ids == null || ids.isEmpty()) {
            ra.addFlashAttribute("warningMsg", "Chua chon nguoi dung nao.");
            return "redirect:/nguoi-dung";
        }
        boolean targetActive = "unlock".equalsIgnoreCase(action);
        int ok = 0;
        List<String> failed = new ArrayList<>();
        for (String ma : ids) {
            try {
                NguoiDung nd = nguoiDungService.findById(ma);
                if (Boolean.TRUE.equals(nd.getTrangThaiTK()) != targetActive) {
                    nguoiDungService.toggleTrangThai(ma);
                    ok++;
                }
            } catch (Exception e) {
                failed.add(ma);
            }
        }
        if (failed.isEmpty()) {
            ra.addFlashAttribute("successMsg",
                    String.format("Da %s %d tai khoan.",
                            targetActive ? "mo khoa" : "khoa", ok));
        } else {
            ra.addFlashAttribute("warningMsg",
                    String.format("Da %s %d/%d tai khoan. That bai: %s",
                            targetActive ? "mo khoa" : "khoa",
                            ok, ids.size(),
                            String.join(", ", failed)));
        }
        return "redirect:/nguoi-dung";
    }

    /* === HELPER === */
    /**
     * Whitelist sort field cho list nguoi dung. Spring Data dat truc tiep
     * property tu URL ?sort=... sang Sort, neu user nhap field khong ton tai
     * → JPA throw IllegalArgumentException luc execute query. Loc va fallback
     * ve "hoTen" cho moi field khong nam trong allow-list.
     */
    private static final java.util.Set<String> ALLOWED_SORT = java.util.Set.of(
            "hoTen", "tenDangNhap", "email", "loaiNguoiDung",
            "trangThaiTK", "maNguoiDung");

    private static Pageable sanitizePageable(Pageable in) {
        Sort safe = Sort.by(in.getSort().stream()
                .map(o -> ALLOWED_SORT.contains(o.getProperty())
                        ? o
                        : Sort.Order.asc("hoTen"))
                .toList());
        if (safe.isUnsorted()) safe = Sort.by(Sort.Order.asc("hoTen"));
        // Cap size toi da 200 de chong nguoi dung tu them ?size=99999 lam
        // app load het bang ra browser.
        int size = Math.min(in.getPageSize(), 200);
        return org.springframework.data.domain.PageRequest.of(
                in.getPageNumber(), size, safe);
    }

    /* ====================== THEM MOI ====================== */
    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("nguoiDungDTO", new NguoiDungDTO());
        model.addAttribute("loaiList", LoaiNguoiDung.values());
        model.addAttribute("vaiTroList", VaiTro.values());
        model.addAttribute("lopHCList", lopHCRepo.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "nguoi-dung/form";
    }

    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @PostMapping("/them")
    public String them(@Valid @ModelAttribute NguoiDungDTO dto,
                        BindingResult br,
                        Model model,
                        RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("loaiList", LoaiNguoiDung.values());
            model.addAttribute("vaiTroList", VaiTro.values());
            model.addAttribute("lopHCList", lopHCRepo.findAll());
            model.addAttribute("isEdit", false);
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "nguoi-dung/form";
        }
        try {
            nguoiDungService.create(dto);
            ra.addFlashAttribute("successMsg", "Tạo người dùng thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/nguoi-dung";
    }

    /* ====================== CHINH SUA ====================== */
    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @GetMapping("/sua/{ma}")
    public String suaForm(@PathVariable String ma, Model model) {
        NguoiDung nd = nguoiDungService.findByIdWithRoles(ma);
        NguoiDungDTO dto = new NguoiDungDTO();
        dto.setMaNguoiDung(nd.getMaNguoiDung());
        dto.setTenDangNhap(nd.getTenDangNhap());
        dto.setEmail(nd.getEmail());
        dto.setHoTen(nd.getHoTen());
        dto.setSoDienThoai(nd.getSoDienThoai());
        dto.setLoaiNguoiDung(nd.getLoaiNguoiDung());
        // Populate cac field mo rong theo loai nguoi dung
        if (nd.getLoaiNguoiDung() == LoaiNguoiDung.GiangVien) {
            giangVienRepo.findById(ma).ifPresent(gv -> {
                dto.setHocHam(gv.getHocHam());
                dto.setHocVi(gv.getHocVi());
                dto.setChuyenNganh(gv.getChuyenNganh());
            });
        } else if (nd.getLoaiNguoiDung() == LoaiNguoiDung.SinhVien) {
            sinhVienRepo.findById(ma).ifPresent(sv -> {
                if (sv.getLopHanhChinh() != null) {
                    dto.setMaLopHC(sv.getLopHanhChinh().getMaLopHC());
                }
            });
        }
        // Lay vai tro hien co
        List<VaiTro> dsVT = nd.getNhomNguoiDungs().stream()
                .map(n -> n.getId().getVaiTro()).toList();
        dto.setVaiTros(dsVT);

        model.addAttribute("nguoiDungDTO", dto);
        model.addAttribute("loaiList", LoaiNguoiDung.values());
        model.addAttribute("vaiTroList", VaiTro.values());
        model.addAttribute("lopHCList", lopHCRepo.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "nguoi-dung/form";
    }

    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @PostMapping("/sua/{ma}")
    public String sua(@PathVariable String ma,
                       @Valid @ModelAttribute NguoiDungDTO dto,
                       BindingResult br,
                       Model model,
                       RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("loaiList", LoaiNguoiDung.values());
            model.addAttribute("vaiTroList", VaiTro.values());
            model.addAttribute("lopHCList", lopHCRepo.findAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("activeMenu", ACTIVE_MENU);
            return "nguoi-dung/form";
        }
        try {
            nguoiDungService.update(ma, dto);
            ra.addFlashAttribute("successMsg", "Cập nhật thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/nguoi-dung";
    }

    /* ====================== TOGGLE TRANG THAI ====================== */
    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @PostMapping("/toggle/{ma}")
    public String toggle(@PathVariable String ma, RedirectAttributes ra) {
        try {
            nguoiDungService.toggleTrangThai(ma);
            ra.addFlashAttribute("successMsg", "Cập nhật trạng thái thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/nguoi-dung";
    }

    /* ====================== IMPORT EXCEL ====================== */
    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @GetMapping("/import")
    public String importForm(Model model) {
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "nguoi-dung/import";
    }

    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @PostMapping("/import")
    public String importExcel(@RequestParam("file") MultipartFile file,
                               RedirectAttributes ra) {
        try {
            List<NguoiDungExcelDTO> rows = ExcelImportUtil.parseNguoiDung(file);
            Map<String, Object> result = nguoiDungService.importFromExcel(rows);
            ra.addFlashAttribute("importResult", result);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", "Loi import: " + e.getMessage());
        }
        return "redirect:/nguoi-dung/import";
    }

    /* ====================== CHI TIET ====================== */
    @GetMapping("/chi-tiet/{ma}")
    public String chiTiet(@PathVariable String ma, Model model) {
        NguoiDung nd = nguoiDungService.findByIdWithRoles(ma);
        model.addAttribute("nguoiDung", nd);

        // Hien thi thong tin mo rong theo LoaiNguoiDung. Truoc day chi-tiet
        // cho SV khong hien thi LopHanhChinh - user khong biet SV thuoc lop
        // nao. Load SinhVien kem LopHanhChinh + ChuongTrinhDaoTao va truyen
        // vao template de section "Thong Tin Sinh Vien" co du du lieu render.
        if (nd.getLoaiNguoiDung() == LoaiNguoiDung.SinhVien) {
            sinhVienRepo.findById(ma).ifPresent(sv -> {
                model.addAttribute("sinhVien", sv);
                // FIX LazyInit: sv.getLopHanhChinh() la proxy LAZY. Truy cap
                // getMaLopHC() tren proxy khong trigger init (ID la san). Sau
                // do dung lopHCRepo.findByIdFetch(...) de load LopHanhChinh
                // KEM ChuongTrinhDaoTao trong 1 query - template
                // (nguoi-dung/chi-tiet) truy cap lopHanhChinh.tenLop va
                // lopHanhChinh.chuongTrinhDaoTao.maCTDT se khong nem
                // LazyInitializationException (open-in-view=false).
                LopHanhChinh lopProxy = sv.getLopHanhChinh();
                if (lopProxy != null) {
                    lopHCRepo.findByIdFetch(lopProxy.getMaLopHC())
                            .ifPresent(lop -> model.addAttribute("lopHanhChinh", lop));
                }
            });
        } else if (nd.getLoaiNguoiDung() == LoaiNguoiDung.GiangVien) {
            giangVienRepo.findById(ma).ifPresent(gv ->
                    model.addAttribute("giangVien", gv));
        }
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "nguoi-dung/chi-tiet";
    }

    /* ====================== XOA ====================== */
    /**
     * Xoa nguoi dung. Chan:
     *  - Xoa chinh minh (tranh user tu khoa minh ra).
     *  - Xoa nguoi dung co du lieu nghiep vu tham chieu (SV da dang ky lop hoc
     *    phan, GV dang phu trach hoc phan...). Service se throw
     *    BusinessException voi thong bao cu the.
     */
    @PreAuthorize("hasAnyRole('PDT','ADMIN')")
    @PostMapping("/xoa/{ma}")
    public String xoa(@PathVariable String ma,
                       Authentication auth,
                       RedirectAttributes ra) {
        try {
            String currentMa = auth != null && auth.getPrincipal()
                    instanceof com.ntu.quanlyctdtdb.security.CustomUserDetails cud
                    ? cud.getMaNguoiDung() : null;
            if (ma.equals(currentMa)) {
                throw new IllegalStateException("Không thể xoá chính tài khoản đang đăng nhập.");
            }
            nguoiDungService.delete(ma);
            ra.addFlashAttribute("successMsg", "Đã xoá người dùng " + ma + " thành công.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/nguoi-dung";
    }
}
