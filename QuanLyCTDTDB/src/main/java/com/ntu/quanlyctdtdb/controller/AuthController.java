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
            model.addAttribute("errorMsg", "Ten dang nhap hoac mat khau khong dung.");
        }
        if (logout != null) {
            model.addAttribute("successMsg", "Da dang xuat thanh cong.");
        }
        if (expired != null) {
            model.addAttribute("warningMsg", "Phien lam viec da het han. Vui long dang nhap lai.");
        }
        if (disabled != null) {
            model.addAttribute("errorMsg", "Tai khoan da bi khoa. Vui long lien he quan tri vien.");
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
