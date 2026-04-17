package com.ntu.quanlyctdtdb.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ntu.quanlyctdtdb.dto.NguoiDungDTO;
import com.ntu.quanlyctdtdb.dto.PhanCongThucTapExcelDTO;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import com.ntu.quanlyctdtdb.exception.BusinessException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility doc file Excel (.xlsx) de import NguoiDung va PhanCongThucTap.
 *
 * Format Excel NguoiDung (Sheet "NguoiDung", bat dau tu row 2):
 *   Col A: MaNguoiDung  (VD: SV240001)
 *   Col B: HoTen
 *   Col C: Email
 *   Col D: TenDangNhap
 *   Col E: VaiTro       (VD: SV / GV / CVHT...)
 *   Col F: MaLopHC      (chi dung khi VaiTro=SV, de trong neu khong co)
 *   Col G: MaDoanhNghiep (chi dung khi VaiTro=DN, de trong neu khong co)
 *
 * Format Excel PhanCongThucTap (Sheet "PhanCong", bat dau tu row 2):
 *   Col A: MaSV
 *   Col B: MaDoanhNghiep
 *   Col C: MaGiangVienGiamSat
 */
@Component
public class ExcelImportUtil {

    private static final int HEADER_ROW = 0; // Row index 0 = row 1 trong Excel

    // ---- NguoiDung ----

    /**
     * Doc file Excel va tra ve danh sach NguoiDungDTO.
     * Dong trong hoac thieu MaNguoiDung se duoc bo qua (skip).
     * @throws BusinessException neu file sai format hoac khong the doc
     */
    public List<NguoiDungDTO> readNguoiDung(MultipartFile file) {
        List<NguoiDungDTO> result = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("NguoiDung");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0); // fallback: lay sheet dau tien
            }

            int totalRows = sheet.getLastRowNum();
            for (int i = 1; i <= totalRows; i++) { // Bo qua row 0 (header)
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String maNguoiDung = getCellString(row, 0);
                if (maNguoiDung.isBlank()) continue; // Bo qua dong trong

                NguoiDungDTO dto = new NguoiDungDTO();
                dto.setMaNguoiDung(maNguoiDung.trim());
                dto.setHoTen(getCellString(row, 1).trim());
                dto.setEmail(getCellString(row, 2).trim());
                dto.setTenDangNhap(getCellString(row, 3).trim());
                dto.setMatKhau(maNguoiDung.trim()); // Mat khau mac dinh = MaNguoiDung

                // Parse VaiTro
                String vaiTroStr = getCellString(row, 4).trim().toUpperCase();
                try {
                    dto.setVaiTros(List.of(VaiTro.valueOf(vaiTroStr)));
                } catch (IllegalArgumentException e) {
                    throw new BusinessException(
                        "Row " + (i + 1) + ": VaiTro khong hop le '" + vaiTroStr + "'. "
                        + "Gia tri cho phep: SV, GV, CVHT, BCN, CNHP, PDT, TTDTXS, DN");
                }

                // MaLopHC (col F, index 5)
                String maLopHC = getCellString(row, 5).trim();
                if (!maLopHC.isBlank()) dto.setMaLopHC(maLopHC);

                // MaDoanhNghiep (col G, index 6)
                String maDN = getCellString(row, 6).trim();
                if (!maDN.isBlank()) dto.setMaDoanhNghiep(maDN);

                result.add(dto);
            }

        } catch (IOException e) {
            throw new BusinessException("Khong the doc file Excel: " + e.getMessage());
        }

        return result;
    }

    // ---- PhanCongThucTap ----

    /**
     * Doc file Excel phan cong thuc tap.
     * Skip dong trung (MaDotTT + MaSV da ton tai) se duoc kiem tra o Service.
     */
    public List<PhanCongThucTapExcelDTO> readPhanCongThucTap(MultipartFile file) {
        List<PhanCongThucTapExcelDTO> result = new ArrayList<>();

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("PhanCong");
            if (sheet == null) {
                sheet = workbook.getSheetAt(0);
            }

            int totalRows = sheet.getLastRowNum();
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String maSV = getCellString(row, 0).trim();
                if (maSV.isBlank()) continue;

                PhanCongThucTapExcelDTO dto = new PhanCongThucTapExcelDTO();
                dto.setMaSV(maSV);
                dto.setMaDoanhNghiep(getCellString(row, 1).trim());
                dto.setMaGiangVienGiamSat(getCellString(row, 2).trim());
                result.add(dto);
            }

        } catch (IOException e) {
            throw new BusinessException("Khong the doc file Excel phan cong: " + e.getMessage());
        }

        return result;
    }

    // ---- Private helpers ----

    private String getCellString(Row row, int colIndex) {
        Cell cell = row.getCell(colIndex);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING  -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default      -> "";
        };
    }
}
