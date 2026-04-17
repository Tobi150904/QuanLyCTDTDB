package com.ntu.quanlyctdtdb.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * AuthController: Xu ly login/logout page.
 * Spring Security tu xu ly POST /login - controller chi xu ly GET /login.
 */
@Controller
public class AuthController {

    /**
     * GET /login
     * - Neu da dang nhap roi thi redirect ve /dashboard
     * - Neu chua thi hien trang login
     * @param error  ?error=true  -> sai ten dang nhap / mat khau
     * @param logout ?logout=true -> dang xuat thanh cong
     * @param expired ?expired=true -> session het han
     * @param locked ?locked=true -> tai khoan bi khoa (isAccountNonLocked = false)
     */
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error",   required = false) String error,
            @RequestParam(value = "logout",  required = false) String logout,
            @RequestParam(value = "expired", required = false) String expired,
            @RequestParam(value = "locked",  required = false) String locked,
            Model model) {

        // Neu da dang nhap roi -> khong cho vao trang login nua
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !auth.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            // Spring Security set loi trong session, lay ra de hien thi ro rang hon
            model.addAttribute("errorMsg",
                    "Ten dang nhap hoac mat khau khong dung. Vui long thu lai.");
        }
        if (logout != null) {
            model.addAttribute("logoutMsg", "Ban da dang xuat thanh cong.");
        }
        if (expired != null) {
            model.addAttribute("errorMsg",
                    "Phien lam viec da het han. Vui long dang nhap lai.");
        }
        if (locked != null) {
            model.addAttribute("errorMsg",
                    "Tai khoan cua ban dang bi khoa. Vui long lien he quan tri vien.");
        }

        return "auth/login"; // -> templates/auth/login.html
    }

    /**
     * GET / -> redirect ve /dashboard
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
}
