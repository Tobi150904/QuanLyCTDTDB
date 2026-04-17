package com.ntu.quanlyctdtdb.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.ntu.quanlyctdtdb.security.CustomUserDetails;

/**
 * DashboardController: Hien thi trang chu sau khi dang nhap.
 * Template dashboard.html tu render khac nhau dua tren sec:hasRole(...)
 * trong Thymeleaf, khong can tao nhieu controller/view theo role.
 */
@Controller
public class DashboardController {

    /**
     * GET /dashboard
     * Truyen thong tin nguoi dung vao model de hien thi header / welcome message.
     * Trang dashboard.html dung sec:hasRole("PDT") v.v. de hien thi block tuong ung.
     */
    @GetMapping("/dashboard")
    public String dashboard(
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Model model) {

        if (currentUser != null) {
            model.addAttribute("hoTen",       currentUser.getHoTen());
            model.addAttribute("maNguoiDung", currentUser.getMaNguoiDung());
            // Lay danh sach role de hien thi role chinh tren header
            model.addAttribute("roles",       currentUser.getAuthorities());
        }

        return "dashboard/dashboard"; // -> templates/dashboard/dashboard.html
    }
}
