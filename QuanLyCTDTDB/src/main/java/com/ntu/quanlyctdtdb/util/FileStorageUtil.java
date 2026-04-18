package com.ntu.quanlyctdtdb.util;

import com.ntu.quanlyctdtdb.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class FileStorageUtil {

    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("pdf", "doc", "docx", "xlsx", "xls");
    private static final long MAX_SIZE = 20 * 1024 * 1024L; // 20 MB

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    /**
     * Luu file upload vao thu muc subDir, tra ve ten file da luu.
     * Ten file: {prefix}_{timestamp}_{originalName}
     */
    public String saveFile(MultipartFile file, String subDir, String prefix) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateFile(file);

        try {
            // Tao thu muc neu chua ton tai
            Path targetDir = Paths.get(uploadDir, subDir).toAbsolutePath().normalize();
            Files.createDirectories(targetDir);

            // Dat ten file
            String originalName = sanitizeFilename(
                    Objects.requireNonNull(file.getOriginalFilename()));
            String timestamp = String.valueOf(Instant.now().toEpochMilli());
            String storedName = prefix + "_" + timestamp + "_" + originalName;

            Path targetPath = targetDir.resolve(storedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Tra ve duong dan tuong doi de luu vao DB
            return subDir + "/" + storedName;

        } catch (IOException e) {
            throw new BusinessException("Luu file that bai: " + e.getMessage());
        }
    }

    /** Xoa file khoi he thong (khong bat loi neu khong ton tai) */
    public void deleteFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) return;
        try {
            Path path = Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // Khong bat loi - file co the da bi xoa
        }
    }

    private void validateFile(MultipartFile file) {
        // Kiem tra kich thuoc
        if (file.getSize() > MAX_SIZE) {
            throw new BusinessException("File qua lon. Toi da 20MB.");
        }

        // Kiem tra extension
        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new BusinessException("Ten file khong hop le.");
        }

        String ext = getExtension(originalName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new BusinessException(
                    "Dinh dang file khong hop le. Chi chap nhan: " +
                    String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return (lastDot >= 0) ? filename.substring(lastDot + 1) : "";
    }

    private String sanitizeFilename(String filename) {
        // Giu lai phan duoi cot cuoi (ten file thuan)
        String name = Paths.get(filename).getFileName().toString();
        // Thay the ky tu dac biet (tru dau cham va go ngang)
        return name.replaceAll("[^a-zA-Z0-9._\\-]", "_");
    }
}
