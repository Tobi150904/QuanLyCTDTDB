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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/nguoi-dung")
@RequiredArgsConstructor
public class NguoiDungController {

    private static final String ACTIVE_MENU = "nguoi-dung";

    private final NguoiDungService nguoiDungService;
    private final LopHanhChinhRepository lopHCRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;

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
    @GetMapping("/import")
    public String importForm(Model model) {
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "nguoi-dung/import";
    }

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
        model.addAttribute("nguoiDung", nguoiDungService.findByIdWithRoles(ma));
        model.addAttribute("activeMenu", ACTIVE_MENU);
        return "nguoi-dung/chi-tiet";
    }
}
