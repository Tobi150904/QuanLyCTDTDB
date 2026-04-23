package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.NguoiDungDTO;
import com.ntu.quanlyctdtdb.dto.NguoiDungExcelDTO;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.service.NguoiDungService;
import com.ntu.quanlyctdtdb.util.ExcelImportUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @GetMapping
    public String danhSach(@RequestParam(defaultValue = "") String keyword,
                            @RequestParam(required = false) LoaiNguoiDung loai,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        Page<NguoiDung> pages = nguoiDungService.search(keyword, loai,
                PageRequest.of(page, 15, Sort.by("hoTen")));
        model.addAttribute("pages", pages);
        model.addAttribute("keyword", keyword);
        model.addAttribute("loaiFilter", loai);
        model.addAttribute("loaiList", LoaiNguoiDung.values());
        model.addAttribute("thongKe", nguoiDungService.getThongKe());
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "nguoi-dung/danh-sach";
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
                if (sv.getLopHanhChinh() != null) {
                    // Trigger lazy load khi con trong transaction (RequestMapping
                    // bao trum transaction readOnly qua service). Sau do truyen
                    // lop vao template - dam bao khong LazyInit o view.
                    model.addAttribute("lopHanhChinh", sv.getLopHanhChinh());
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
