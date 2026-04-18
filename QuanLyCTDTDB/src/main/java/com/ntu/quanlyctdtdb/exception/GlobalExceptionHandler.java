package com.ntu.quanlyctdtdb.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMsg", ex.getMessage());
        model.addAttribute("statusCode", 404);
        return "error/404";
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex,
                                  RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());
        return "redirect:/dashboard";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorMsg", "Da xay ra loi he thong. Vui long thu lai.");
        model.addAttribute("detail", ex.getMessage());
        return "error/500";
    }
}
