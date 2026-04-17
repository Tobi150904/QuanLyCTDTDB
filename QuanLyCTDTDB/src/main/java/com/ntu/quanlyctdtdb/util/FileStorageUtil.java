package com.ntu.quanlyctdtdb.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ntu.quanlyctdtdb.exception.BusinessException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Utility upload file len disk (thu muc uploads/).
 * Tat ca duong dan luu vao DB la relative path, khong phai absolute.
 * VD: "tailieu/2025/03/POL307-2024.1-01_DeCuongChiTiet_a1b2c3.pdf"
 */
@Component
public class FileStorageUtil {

    private static final List<String> ALLOWED_DOC_TYPES =
            Arrays.asList("application/pdf",
                          "application/msword",
                          "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

    private static final List<String> ALLOWED_EXCEL_TYPES =
            Arrays.asList("application/vnd.ms-excel",
                          "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024; // 20 MB

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    /**
     * Luu tai lieu mon hoc (PDF, DOCX).
     * @param file     MultipartFile tu form
     * @param subDir   thu muc con (VD: "tailieu", "ctdt", "hocphan")
     * @param prefix   tien to ten file (VD: "POL307-2024.1-01_DeCuong")
     * @return relative path da luu (de luu vao DB)
     */
    public String saveTaiLieu(MultipartFile file, String subDir, String prefix) {
        validateDocFile(file);
        return saveFile(file, subDir, prefix);
    }

    /**
     * Luu file Excel (cho import nguoi dung, phan cong thuc tap).
     */
    public String saveExcel(MultipartFile file, String subDir, String prefix) {
        validateExcelFile(file);
        return saveFile(file, subDir, prefix);
    }

    /**
     * Xoa file cu tren disk (dung khi GV nop lai tai lieu).
     * Khong throw neu file khong ton tai (silent).
     */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path filePath = Paths.get(uploadDir).resolve(relativePath).normalize();
            Files.deleteIfExists(filePath);
        } catch (IOException ignored) {
            // Silent: file co the da bi xoa tay
        }
    }

    /**
     * Lay full path tren disk tu relative path (dung de doc file trong service).
     */
    public Path getFullPath(String relativePath) {
        return Paths.get(uploadDir).resolve(relativePath).normalize();
    }

    // ---- Private helpers ----

    private String saveFile(MultipartFile file, String subDir, String prefix) {
        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() != null ? file.getOriginalFilename() : "file");
        String extension = getExtension(originalName);
        String uniqueName = prefix + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        // Tao thu muc con theo thang (VD: tailieu/2025/03/)
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String relativePath = subDir + "/" + datePath + "/" + uniqueName;

        try {
            Path targetDir = Paths.get(uploadDir).resolve(subDir + "/" + datePath).normalize();
            Files.createDirectories(targetDir);
            Path targetFile = targetDir.resolve(uniqueName);
            Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
            return relativePath;
        } catch (IOException e) {
            throw new BusinessException("Khong the luu file: " + e.getMessage());
        }
    }

    private void validateDocFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File khong duoc de trong");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("File qua lon (toi da 20MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_DOC_TYPES.contains(contentType)) {
            throw new BusinessException("Chi chap nhan file PDF hoac Word (.docx, .doc)");
        }
    }

    private void validateExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File Excel khong duoc de trong");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("File qua lon (toi da 20MB)");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_EXCEL_TYPES.contains(contentType)) {
            throw new BusinessException("Chi chap nhan file Excel (.xlsx, .xls)");
        }
    }

    private String getExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex >= 0) ? filename.substring(dotIndex) : "";
    }
}
