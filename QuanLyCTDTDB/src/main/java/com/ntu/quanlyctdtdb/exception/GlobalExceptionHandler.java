package com.ntu.quanlyctdtdb.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Trung tam xu ly exception cho toan bo ung dung.
 *
 * <p>Nguyen tac:
 * <ul>
 *   <li>BusinessException: loi nghiep vu - redirect + flash message (giu nguyen URL goc).</li>
 *   <li>ResourceNotFoundException: entity khong ton tai - render trang 404 kem detail.</li>
 *   <li>AccessDeniedException: quyen bi tu choi - de Spring Security chuyen sang /403.</li>
 *   <li>MaxUploadSizeExceededException: file qua lon - flash va quay lai.</li>
 *   <li>Exception (fallback): loi he thong - log stacktrace day du va render 500.</li>
 * </ul>
 *
 * <p>Toan bo message hien thi cho nguoi dung dung tieng Viet co dau, theo chuan
 * tai lieu docs/05_UI_DESIGN_SYSTEM.md §5 (toast / inline alert).
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        log.warn("[ResourceNotFound] {}", ex.getMessage());
        model.addAttribute("errorMsg", ex.getMessage());
        model.addAttribute("statusCode", 404);
        return "error/404";
    }

    @ExceptionHandler(BusinessException.class)
    public String handleBusiness(BusinessException ex,
                                  HttpServletRequest req,
                                  RedirectAttributes redirectAttributes) {
        log.info("[BusinessException] path={} msg={}", req.getRequestURI(), ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMsg", ex.getMessage());

        // Quay ve trang truoc neu co Referer, nguoc lai ve dashboard.
        // Tranh truong hop submit form loi -> mat context trang dang dung.
        String referer = req.getHeader("Referer");
        if (referer != null && !referer.isBlank() && !referer.contains("/login")) {
            return "redirect:" + referer;
        }
        return "redirect:/dashboard";
    }

    /**
     * Re-throw AccessDeniedException de Spring Security tu xu ly qua
     * accessDeniedPage("/403") da khai bao trong SecurityConfig.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDenied(AccessDeniedException ex) throws AccessDeniedException {
        throw ex;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUpload(MaxUploadSizeExceededException ex,
                                   HttpServletRequest req,
                                   RedirectAttributes redirectAttributes) {
        log.warn("[MaxUploadSize] path={} msg={}", req.getRequestURI(), ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMsg",
                "Tệp tải lên vượt quá dung lượng cho phép. Tối đa 20 MB mỗi tệp.");
        String referer = req.getHeader("Referer");
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/dashboard");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrity(DataIntegrityViolationException ex,
                                       HttpServletRequest req,
                                       RedirectAttributes redirectAttributes) {
        // Thuong xay ra khi xoa entity dang duoc tham chieu (FK) hoac insert trung UNIQUE.
        log.warn("[DataIntegrityViolation] path={} cause={}",
                req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        redirectAttributes.addFlashAttribute("errorMsg",
                "Không thể thực hiện do ràng buộc dữ liệu. "
                        + "Có thể bản ghi đang được sử dụng ở nơi khác "
                        + "hoặc trùng với dữ liệu đã tồn tại.");
        String referer = req.getHeader("Referer");
        return "redirect:" + (referer != null && !referer.isBlank() ? referer : "/dashboard");
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNoHandler(NoHandlerFoundException ex, Model model) {
        log.warn("[NoHandlerFound] {} {}", ex.getHttpMethod(), ex.getRequestURL());
        model.addAttribute("errorMsg", "Trang bạn yêu cầu không tồn tại.");
        model.addAttribute("statusCode", 404);
        return "error/404";
    }

    /**
     * Xu ly rieng {@link NoResourceFoundException} (Spring MVC 6.1+) cho cac
     * request static khong ton tai (vd: {@code /favicon.ico}, {@code /apple-touch-icon.png}).
     * Truoc day bi handleGeneral() bat va log ERROR kem full stack trace moi
     * lan browser auto-fetch favicon, khien log bi spam va app co ve "bi loi nang".
     *
     * <p>O day chi tra ve 404 voi body rong va log DEBUG (khong stacktrace).
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Void> handleNoStaticResource(NoResourceFoundException ex,
                                                        HttpServletRequest req) {
        log.debug("[NoResource] {} (path={})", ex.getResourcePath(), req.getRequestURI());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneral(Exception ex, HttpServletRequest req, Model model) {
        // Log full stack trace de ops/dev co the trace; user chi thay thong diep than thien.
        log.error("[UnhandledException] path={} ", req.getRequestURI(), ex);
        model.addAttribute("errorMsg",
                "Đã xảy ra lỗi hệ thống. Vui lòng thử lại sau ít phút "
                        + "hoặc liên hệ quản trị viên nếu sự cố tiếp diễn.");
        // Khong dua ex.getMessage() ra view de tranh lo thong tin nhay cam
        // (vi du: ten bang, SQL state). Dev xem chi tiet trong log backend.
        return "error/500";
    }
}
