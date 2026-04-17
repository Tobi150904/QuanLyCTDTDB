package com.ntu.quanlyctdtdb.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler cho Thymeleaf controllers.
 * Bat exception va hien thi trang loi hoac redirect voi flash message.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bat BusinessException: vi pham quy tac nghiep vu
     * Hien thi trang loi voi thong bao cu the
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBusinessException(BusinessException ex, Model model) {
        model.addAttribute("errorTitle", "Loi nghiep vu");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("backUrl", "javascript:history.back()");
        return "error/business-error";
    }

    /**
     * Bat ResourceNotFoundException: khong tim thay entity theo ID
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Khong tim thay du lieu");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("backUrl", "javascript:history.back()");
        return "error/not-found";
    }

    /**
     * Bat EntityNotFoundException: khong tim thay entity (JPA)
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Khong tim thay du lieu");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("backUrl", "javascript:history.back()");
        return "error/not-found";
    }

    /**
     * Bat DataIntegrityViolationException: vi pham constraint DB
     * VD: UNIQUE constraint TaiLieuMonHoc(MaLopHP, Loai) khi code bi loi
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDataIntegrity(DataIntegrityViolationException ex, Model model) {
        model.addAttribute("errorTitle", "Loi rang buoc du lieu");
        model.addAttribute("errorMessage",
                "Du lieu bi trung lap hoac vi pham rang buoc cua he thong.");
        model.addAttribute("backUrl", "javascript:history.back()");
        return "error/business-error";
    }

    /**
     * Bat loi chung con lai
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Loi he thong");
        model.addAttribute("errorMessage",
                "Da xay ra loi khong mong muon. Vui long thu lai hoac lien he quan tri.");
        return "error/general-error";
    }
}
