package com.ntu.quanlyctdtdb.util;

import com.ntu.quanlyctdtdb.dto.NguoiDungExcelDTO;
import com.ntu.quanlyctdtdb.dto.ThucTapExcelDTO;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelImportUtil {

    private ExcelImportUtil() {}

    /**
     * Parse file Excel tao nguoi dung hang loat.
     * Cau truc sheet: TenDangNhap | MatKhau | Email | HoTen | SoDienThoai | LoaiNguoiDung | VaiTro | MaLopHC
     */
    public static List<NguoiDungExcelDTO> parseNguoiDung(MultipartFile file) {
        validateExcel(file);
        List<NguoiDungExcelDTO> result = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            // Bo qua dong header (dong 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                NguoiDungExcelDTO dto = new NguoiDungExcelDTO();
                dto.setRowNum(i + 1);
                dto.setTenDangNhap(getCellString(row, 0));
                dto.setMatKhau(getCellString(row, 1));
                dto.setEmail(getCellString(row, 2));
                dto.setHoTen(getCellString(row, 3));
                dto.setSoDienThoai(getCellString(row, 4));
                dto.setLoaiNguoiDung(getCellString(row, 5));
                dto.setVaiTro(getCellString(row, 6));
                dto.setMaLopHC(getCellString(row, 7));
                result.add(dto);
            }
        } catch (IOException e) {
            throw new BusinessException("Khong the doc file Excel: " + e.getMessage());
        }
        return result;
    }

    /**
     * Parse file Excel phan cong thuc tap.
     * Cau truc sheet: MaSV | LoaiThucTap | MaDoanhNghiep
     */
    public static List<ThucTapExcelDTO> parsePhanCongThucTap(MultipartFile file) {
        validateExcel(file);
        List<ThucTapExcelDTO> result = new ArrayList<>();
        try (Workbook wb = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) continue;

                ThucTapExcelDTO dto = new ThucTapExcelDTO();
                dto.setRowNum(i + 1);
                dto.setMaSV(getCellString(row, 0));
                dto.setLoaiThucTap(getCellString(row, 1));
                dto.setMaDoanhNghiep(getCellString(row, 2));
                result.add(dto);
            }
        } catch (IOException e) {
            throw new BusinessException("Khong the doc file Excel: " + e.getMessage());
        }
        return result;
    }

    // ---- Helper methods ----

    private static void validateExcel(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("File Excel khong duoc de trong.");
        }
        String name = file.getOriginalFilename();
        if (name == null || (!name.endsWith(".xlsx") && !name.endsWith(".xls"))) {
            throw new BusinessException("Chi chap nhan file .xlsx hoac .xls.");
        }
        if (file.getSize() > 20 * 1024 * 1024L) {
            throw new BusinessException("File qua lon. Toi da 20MB.");
        }
    }

    private static String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                // Tranh loi so thap phan cho ma nhu "SV001.0"
                double d = cell.getNumericCellValue();
                if (d == Math.floor(d)) yield String.valueOf((long) d);
                yield String.valueOf(d);
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCachedFormulaResultType() == CellType.STRING
                    ? cell.getStringCellValue().trim()
                    : String.valueOf((long) cell.getNumericCellValue());
            default -> null;
        };
    }

    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK
                    && !getCellString(row, c).isBlank()) {
                return false;
            }
        }
        return true;
    }
}
