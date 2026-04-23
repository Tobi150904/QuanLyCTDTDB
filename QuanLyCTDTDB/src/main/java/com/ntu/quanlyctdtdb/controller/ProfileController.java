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
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng."));
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
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng."));

        if (!passwordEncoder.matches(matKhauCu, nd.getMatKhauHash())) {
            ra.addFlashAttribute("errorMsg", "Mật khẩu cũ không đúng.");
            return "redirect:/profile";
        }
        if (!matKhauMoi.equals(xacNhanMatKhau)) {
            ra.addFlashAttribute("errorMsg",
                    "Mật khẩu mới và xác nhận không khớp.");
            return "redirect:/profile";
        }
        // Chan truong hop user dat lai mat khau trung voi mat khau hien tai —
        // tranh feeling "da doi" nhung thuc te khong co gi thay doi.
        if (passwordEncoder.matches(matKhauMoi, nd.getMatKhauHash())) {
            ra.addFlashAttribute("warningMsg",
                    "Mật khẩu mới không được trùng với mật khẩu hiện tại.");
            return "redirect:/profile";
        }
        nd.setMatKhauHash(passwordEncoder.encode(matKhauMoi));
        nguoiDungRepo.save(nd);
        ra.addFlashAttribute("successMsg", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }

    @PostMapping("/cap-nhat")
    public String capNhat(@AuthenticationPrincipal CustomUserDetails currentUser,
                           @RequestParam String hoTen,
                           @RequestParam(required = false) String soDienThoai,
                           RedirectAttributes ra) {
        NguoiDung nd = nguoiDungRepo.findById(currentUser.getMaNguoiDung())
                .orElseThrow(() -> new BusinessException("Không tìm thấy người dùng."));
        if (hoTen != null && !hoTen.isBlank()) {
            nd.setHoTen(hoTen.trim());
        }
        nd.setSoDienThoai(soDienThoai);
        nguoiDungRepo.save(nd);
        ra.addFlashAttribute("successMsg", "Cập nhật thông tin thành công!");
        return "redirect:/profile";
    }
}
