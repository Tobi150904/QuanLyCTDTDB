package com.ntu.quanlyctdtdb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    /**
     * Trang dang nhap.
     * Spring Security xu ly POST /login tu dong.
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            @RequestParam(required = false) String expired,
                            @RequestParam(required = false) String disabled,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMsg",
                    "Tên đăng nhập hoặc mật khẩu không đúng.");
        }
        if (logout != null) {
            model.addAttribute("successMsg",
                    "Đã đăng xuất thành công.");
        }
        if (expired != null) {
            model.addAttribute("warningMsg",
                    "Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.");
        }
        if (disabled != null) {
            model.addAttribute("errorMsg",
                    "Tài khoản đã bị khoá. Vui lòng liên hệ quản trị viên.");
        }
        return "auth/login";
    }

    /** Trang tu choi truy cap (403). */
    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("activeMenu", "");
        return "error/403";
    }
}
