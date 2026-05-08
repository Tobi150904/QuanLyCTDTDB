package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.BcnThanhVienDTO;
import com.ntu.quanlyctdtdb.dto.ChuongTrinhDaoTaoDTO;
import com.ntu.quanlyctdtdb.dto.CtdtHocPhanDTO;
import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.enums.ChucDanhBCN;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.BcnThanhVienService;
import com.ntu.quanlyctdtdb.service.ChuongTrinhDaoTaoService;
import com.ntu.quanlyctdtdb.util.CsvExportUtil;
import com.ntu.quanlyctdtdb.util.FileStorageUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller quan ly CTDT.
 * Role (docs/03 §"SO DO TONG HOP QUYEN" + §WF-04.*):
 *   - PDT        : R  (theo doi tien do BCN/CNHP tao CTDT)
 *   - TTDTXS     : RW (phe duyet CTDT)
 *   - CNHP/BCN   : RW (BCN tao CTDT, them HP, nop duyet)
 *   - ADMIN      : RW
 *   - GiangVien  : R  (GV thuoc BCN can xem CTDT minh quan ly)
 *   - SinhVien   : R  (xem khung CTDT minh dang theo hoc)
 * Class-level cho doc, write-level qua @PreAuthorize method-level.
 */
@Slf4j
@Controller
@RequestMapping("/ctdt")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN','GIANG_VIEN','SINH_VIEN')")
public class ChuongTrinhDaoTaoController {

    private final ChuongTrinhDaoTaoService ctdtService;
    private final FileStorageUtil fileStorageUtil;
    private final BcnThanhVienService bcnService;
    private final GiangVienRepository giangVienRepo;

    @ModelAttribute("activeMenu")
    public String activeMenu() { return "ctdt"; }

    /**
     * Ngan Spring bind field {@code fileWord} vao DTO.
     *
     * Ly do: {@link ChuongTrinhDaoTaoDTO#fileWord} la {@code String} (luu
     * duong dan file sau khi upload), nhung form HTML chua
     * {@code <input type="file" name="fileWord">} nam trong
     * {@code <form th:object="ctdtDTO">}. Neu khong disallow, Spring se co
     * gang convert {@code MultipartFile -> String} va throw
     * {@code ConversionNotSupportedException}. File thuc te van duoc doc
     * qua {@code @RequestParam("fileWord") MultipartFile} - khong bi anh
     * huong.
     */
    @InitBinder("ctdtDTO")
    public void initCtdtBinder(WebDataBinder binder) {
        binder.setDisallowedFields("fileWord");
    }

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSach", ctdtService.findAll());
        // Phase 2 — stat-card row dong bo voi nguoi-dung/hoc-phan/lop-hoc-phan.
        // Datasize CTDT nho nhung van dung COUNT() de tach logic dem khoi
        // template (truoc day count duoc compute inline trong Thymeleaf).
        model.addAttribute("thongKe", ctdtService.getThongKe());
        return "ctdt/danh-sach";
    }

    /* ====================== EXPORT CSV ====================== */
    /**
     * Xuat danh sach CTDT ra CSV (bao gom: ma, ten, khoa, so HP, trang thai,
     * nguoi tao, ngay duyet). Dataset CTDT thuong nho (~10-30 record), khong
     * can phan trang/filter; xuat tat ca cho on gian.
     */
    @GetMapping("/export")
    public void exportCsv(HttpServletResponse response) throws java.io.IOException {
        var rows = ctdtService.findAll();
        String[] headers = {
            "Ma CTDT", "Ten CTDT", "Khoa", "So HP", "Trang Thai",
            "Nguoi Tao", "Ngay Duyet"
        };
        java.util.List<String[]> data = new java.util.ArrayList<>();
        for (var c : rows) {
            int soHP = c.getCtdtHocPhans() == null ? 0 : c.getCtdtHocPhans().size();
            data.add(CsvExportUtil.row(
                    c.getMaCTDT(),
                    c.getTenCTDT(),
                    c.getKhoa(),
                    soHP,
                    c.getTrangThai() != null ? c.getTrangThai().name() : "",
                    c.getNguoiTao() != null ? c.getNguoiTao().getHoTen() : "",
                    c.getNgayDuyet() != null
                        ? c.getNgayDuyet().toString()
                        : ""
            ));
        }
        CsvExportUtil.write(response, "ctdt", headers, data);
    }

    // Tao / sua / quan ly HP trong CTDT la cua BCN (CNHP), TTDTXS, PDT, ADMIN.
    // GV/SV chi duoc READ — chan writes bang @PreAuthorize method-level.
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("ctdtDTO", new ChuongTrinhDaoTaoDTO());
        model.addAttribute("isEdit", false);
        return "ctdt/form";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("ctdtDTO") ChuongTrinhDaoTaoDTO dto,
                        BindingResult br,
                        @RequestParam(value = "fileWord", required = false) MultipartFile file,
                        @AuthenticationPrincipal CustomUserDetails ud,
                        Model model, RedirectAttributes ra) {
        log.info("POST /ctdt/them dto={}", dto);
        if (br.hasErrors()) {
            log.warn("Validation errors khi tao CTDT: {}",
                    br.getAllErrors().stream().map(o -> o.getDefaultMessage()).toList());
            model.addAttribute("isEdit", false);
            return "ctdt/form";
        }
        try {
            ChuongTrinhDaoTao ctdt = ctdtService.create(dto, ud.getMaNguoiDung());
            if (file != null && !file.isEmpty()) {
                String path = fileStorageUtil.saveFile(file, "ctdt", ctdt.getMaCTDT());
                // Phai goi qua service de save vao DB — neu chi setFileWord()
                // tren reference da detached sau khi create() ket thuc, gia tri
                // se KHONG duoc persist (tx da commit).
                ctdtService.updateFileWord(ctdt.getMaCTDT(), path);
            }
            ra.addFlashAttribute("successMsg", "Tao CTDT thanh cong!");
            return "redirect:/ctdt";
        } catch (Exception e) {
            // Log ro nguyen nhan (stack trace) + re-render form de user thay loi
            // va khong bi mat input (truoc day bi redirect + flash: neu flash bi
            // drop sau 1 redirect hoac session thay doi, user khong biet tai sao
            // "khong tao duoc CTDT, khong co thong bao").
            log.error("Loi khi tao CTDT dto={}: {}", dto, e.getMessage(), e);
            model.addAttribute("errorMsg",
                    e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            model.addAttribute("isEdit", false);
            return "ctdt/form";
        }
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @GetMapping("/sua/{ma}")
    public String suaForm(@PathVariable String ma, Model model) {
        ChuongTrinhDaoTao ctdt = ctdtService.findById(ma);
        ChuongTrinhDaoTaoDTO dto = new ChuongTrinhDaoTaoDTO();
        dto.setMaCTDT(ctdt.getMaCTDT());
        dto.setTenCTDT(ctdt.getTenCTDT());
        dto.setKhoa(ctdt.getKhoa());
        model.addAttribute("ctdtDTO", dto);
        model.addAttribute("isEdit", true);
        return "ctdt/form";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @PostMapping("/sua/{ma}")
    public String sua(@PathVariable String ma,
                       @Valid @ModelAttribute("ctdtDTO") ChuongTrinhDaoTaoDTO dto,
                       BindingResult br,
                       @RequestParam(value = "fileWord", required = false) MultipartFile file,
                       Model model, RedirectAttributes ra) {
        log.info("POST /ctdt/sua/{} dto={}", ma, dto);
        if (br.hasErrors()) {
            log.warn("Validation errors khi sua CTDT {}: {}", ma,
                    br.getAllErrors().stream().map(o -> o.getDefaultMessage()).toList());
            model.addAttribute("isEdit", true);
            return "ctdt/form";
        }
        try {
            ctdtService.update(ma, dto);
            if (file != null && !file.isEmpty()) {
                String path = fileStorageUtil.saveFile(file, "ctdt", ma);
                ctdtService.updateFileWord(ma, path);
            }
            ra.addFlashAttribute("successMsg", "Cap nhat CTDT thanh cong!");
            return "redirect:/ctdt";
        } catch (Exception e) {
            log.error("Loi khi sua CTDT {} dto={}: {}", ma, dto, e.getMessage(), e);
            model.addAttribute("errorMsg",
                    e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName());
            model.addAttribute("isEdit", true);
            return "ctdt/form";
        }
    }

    // Phe duyet CTDT: chi TTDTXS hoac ADMIN moi co quyen (docs/02 §4, review P0-4).
    // URL-level rule cho ca PDT va CNHP vao /ctdt/**, can chan cap method.
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
    @PostMapping("/phe-duyet/{ma}")
    public String pheduyet(@PathVariable String ma,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            RedirectAttributes ra) {
        try {
            ctdtService.pheduyet(ma, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Phe duyet CTDT thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt";
    }
    
    /**
     * Gui duyet CTDT: BCN/CNHP/PDT bam "Gui duyet" de chuyen CTDT tu trang thai
     * BanNhap -> ChoDuyet. Sau buoc nay, TTDTXS/ADMIN moi co the phe duyet.
     * Tach rieng khoi /phe-duyet de respect quyet trinh duyet (CNHP soan
     * khung CTDT, sau do TTDTXS duyet cuoi).
     */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @PostMapping("/gui-duyet/{ma}")
    public String guiduyet(@PathVariable String ma, RedirectAttributes ra) {
        try {
            ctdtService.guiChoDuyet(ma);
            ra.addFlashAttribute("successMsg", "Da gui CTDT cho duyet!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt";
    }

    /* ====================== QUAN LY HOC PHAN TRONG CTDT ====================== */
    @GetMapping("/chi-tiet/{ma}")
    public String chiTiet(@PathVariable String ma, Model model) {
        model.addAttribute("ctdt", ctdtService.findById(ma));
        model.addAttribute("hocPhanChuaThuoc", ctdtService.findHocPhanChuaThuoc(ma));
        model.addAttribute("ctdtHocPhanDTO", new CtdtHocPhanDTO());

        // Ban Chu Nhiem — fetch qua service rieng de tranh
        // MultipleBagFetchException khi cung load 2 @OneToMany List tu CTDT.
        model.addAttribute("bcnList", bcnService.findByCtdt(ma));
        model.addAttribute("bcnThanhVienDTO", new BcnThanhVienDTO());
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        model.addAttribute("chucDanhList", ChucDanhBCN.values());
        return "ctdt/chi-tiet";
    }

    /* ====================== BCN: THEM / XOA THANH VIEN ====================== */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @PostMapping("/chi-tiet/{ma}/bcn/them")
    public String themBcn(@PathVariable String ma,
                           @Valid @ModelAttribute BcnThanhVienDTO dto,
                           BindingResult br,
                           RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("errorMsg", "Du lieu khong hop le");
            return "redirect:/ctdt/chi-tiet/" + ma;
        }
        try {
            bcnService.themThanhVien(ma, dto);
            ra.addFlashAttribute("successMsg", "Them thanh vien BCN thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt/chi-tiet/" + ma;
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @PostMapping("/chi-tiet/{ma}/bcn/xoa")
    public String xoaBcn(@PathVariable String ma,
                          @RequestParam String maGV,
                          @RequestParam ChucDanhBCN chucDanh,
                          RedirectAttributes ra) {
        try {
            bcnService.xoaThanhVien(ma, maGV, chucDanh);
            ra.addFlashAttribute("successMsg", "Da xoa thanh vien BCN.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt/chi-tiet/" + ma;
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @PostMapping("/chi-tiet/{ma}/them-hp")
    public String themHocPhan(@PathVariable String ma,
                               @Valid @ModelAttribute CtdtHocPhanDTO dto,
                               BindingResult br,
                               RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("errorMsg", "Du lieu khong hop le");
            return "redirect:/ctdt/chi-tiet/" + ma;
        }
        try {
            ctdtService.themHocPhan(ma, dto);
            ra.addFlashAttribute("successMsg", "Them hoc phan vao CTDT thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt/chi-tiet/" + ma;
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','CNHP','ADMIN')")
    @PostMapping("/chi-tiet/{ma}/xoa-hp/{maHP}")
    public String xoaHocPhan(@PathVariable String ma, @PathVariable String maHP,
                              RedirectAttributes ra) {
        try {
            ctdtService.xoaHocPhan(ma, maHP);
            ra.addFlashAttribute("successMsg", "Da xoa hoc phan khoi CTDT!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt/chi-tiet/" + ma;
    }
}
