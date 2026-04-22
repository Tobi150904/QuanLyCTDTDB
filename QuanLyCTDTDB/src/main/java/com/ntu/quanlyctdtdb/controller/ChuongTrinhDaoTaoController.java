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
@RequestMapping("/ctdt")
@RequiredArgsConstructor
public class ChuongTrinhDaoTaoController {

    private final ChuongTrinhDaoTaoService ctdtService;
    private final BcnThanhVienService bcnService;
    private final GiangVienRepository giangVienRepo;
    private final FileStorageUtil fileStorageUtil;

    @ModelAttribute("activeMenu")
    public String activeMenu() { return "ctdt"; }

    @GetMapping
    public String danhSach(Model model) {
        model.addAttribute("danhSach", ctdtService.findAll());
        return "ctdt/danh-sach";
    }

    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("ctdtDTO", new ChuongTrinhDaoTaoDTO());
        model.addAttribute("isEdit", false);
        return "ctdt/form";
    }

    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("ctdtDTO") ChuongTrinhDaoTaoDTO dto,
                        BindingResult br,
                        @RequestParam(value = "fileWord", required = false) MultipartFile file,
                        @AuthenticationPrincipal CustomUserDetails ud,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
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
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt";
    }

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

    @PostMapping("/sua/{ma}")
    public String sua(@PathVariable String ma,
                       @Valid @ModelAttribute("ctdtDTO") ChuongTrinhDaoTaoDTO dto,
                       BindingResult br,
                       @RequestParam(value = "fileWord", required = false) MultipartFile file,
                       Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
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
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt";
    }

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

    /* ====================== QUAN LY HOC PHAN TRONG CTDT ====================== */
    @GetMapping("/chi-tiet/{ma}")
    public String chiTiet(@PathVariable String ma, Model model) {
        model.addAttribute("ctdt", ctdtService.findById(ma));
        model.addAttribute("hocPhanChuaThuoc", ctdtService.findHocPhanChuaThuoc(ma));
        model.addAttribute("ctdtHocPhanDTO", new CtdtHocPhanDTO());

        // Du lieu cho tab Ban Chu Nhiem
        model.addAttribute("bcnThanhViens", bcnService.findByCtdt(ma));
        model.addAttribute("bcnDTO", new BcnThanhVienDTO());
        model.addAttribute("chucDanhList", ChucDanhBCN.values());
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        return "ctdt/chi-tiet";
    }

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

    /* ====================== QUAN LY BAN CHU NHIEM ====================== */
    /** Them thanh vien vao BCN cua CTDT. */
    @PostMapping("/chi-tiet/{ma}/them-bcn")
    public String themBcn(@PathVariable String ma,
                           @Valid @ModelAttribute("bcnDTO") BcnThanhVienDTO dto,
                           BindingResult br,
                           RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("errorMsg", "Du lieu BCN khong hop le");
            return "redirect:/ctdt/chi-tiet/" + ma + "#tab-bcn";
        }
        try {
            bcnService.themThanhVien(ma, dto);
            ra.addFlashAttribute("successMsg", "Them thanh vien BCN thanh cong!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt/chi-tiet/" + ma + "#tab-bcn";
    }

    /** Xoa 1 thanh vien khoi BCN. */
    @PostMapping("/chi-tiet/{ma}/xoa-bcn")
    public String xoaBcn(@PathVariable String ma,
                          @RequestParam String maGV,
                          @RequestParam ChucDanhBCN chucDanh,
                          RedirectAttributes ra) {
        try {
            bcnService.xoaThanhVien(ma, maGV, chucDanh);
            ra.addFlashAttribute("successMsg", "Da xoa thanh vien khoi BCN!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/ctdt/chi-tiet/" + ma + "#tab-bcn";
    }
}
