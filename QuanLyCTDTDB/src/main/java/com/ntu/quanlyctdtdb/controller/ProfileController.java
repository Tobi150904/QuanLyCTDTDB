package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.repository.NguoiDungRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final NguoiDungRepository nguoiDungRepo;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String profile(@AuthenticationPrincipal CustomUserDetails currentUser, Model model) {
        NguoiDung nd = nguoiDungRepo.findById(currentUser.getMaNguoiDung())
                .orElseThrow(() -> new BusinessException("Khong tim thay nguoi dung"));
        model.addAttribute("nguoiDung", nd);
        model.addAttribute("activeMenu", "profile");
        return "profile/profile";
    }

    @PostMapping("/doi-mat-khau")
    public String doiMatKhau(@AuthenticationPrincipal CustomUserDetails currentUser,
                              @RequestParam @NotBlank String matKhauCu,
                              @RequestParam @NotBlank @Size(min = 8) String matKhauMoi,
                              @RequestParam @NotBlank String xacNhanMatKhau,
                              RedirectAttributes ra) {
        NguoiDung nd = nguoiDungRepo.findById(currentUser.getMaNguoiDung())
                .orElseThrow(() -> new BusinessException("Khong tim thay nguoi dung"));

        if (!passwordEncoder.matches(matKhauCu, nd.getMatKhauHash())) {
            ra.addFlashAttribute("errorMsg", "Mat khau cu khong dung.");
            return "redirect:/profile";
        }
        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            ra.addFlashAttribute("errorMsg", "Mat khau moi va xac nhan khong khop.");
            return "redirect:/profile";
        }
        nd.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepo.save(nd);
        ra.addFlashAttribute("successMsg", "Doi mat khau thanh cong!");
        return "redirect:/profile";
    }

    @PostMapping("/cap-nhat")
    public String capNhat(@AuthenticationPrincipal CustomUserDetails currentUser,
                           @RequestParam String hoTen,
                           @RequestParam(required = false) String soDienThoai,
                           RedirectAttributes ra) {
        NguoiDung nd = nguoiDungRepo.findById(currentUser.getMaNguoiDung())
                .orElseThrow(() -> new BusinessException("Khong tim thay nguoi dung"));
        if (hoTen != null && !hoTen.isBlank()) {
            nd.setHoTen(hoTen.trim());
        }
        nd.setSoDienThoai(soDienThoai);
        nguoiDungRepo.save(nd);
        ra.addFlashAttribute("successMsg", "Cap nhat thong tin thanh cong!");
        return "redirect:/profile";
    }
}
