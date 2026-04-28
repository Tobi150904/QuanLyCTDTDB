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
    public String accessDenied(jakarta.servlet.http.HttpServletRequest req, Model model) {
        model.addAttribute("activeMenu", "");
        // Phase 7 — pass original request URI (truoc khi Spring Security forward
        // sang /403) de view co the hien cho user dang bi tu choi cai gi. Spring
        // luu URL goc trong attribute "jakarta.servlet.forward.request_uri".
        Object original = req.getAttribute("jakarta.servlet.forward.request_uri");
        if (original == null) {
            original = req.getAttribute("javax.servlet.forward.request_uri");
        }
        if (original != null) {
            String uri = original.toString();
            Object qs = req.getAttribute("jakarta.servlet.forward.query_string");
            if (qs == null) {
                qs = req.getAttribute("javax.servlet.forward.query_string");
            }
            if (qs != null && !qs.toString().isEmpty()) {
                uri = uri + "?" + qs;
            }
            model.addAttribute("requestUri", uri);
        } else {
            // Khong forwarded -> user truc tiep go /403 -> chi can hien dong dan hien tai
            model.addAttribute("requestUri", req.getRequestURI());
        }
        return "error/403";
    }
}
