package com.ntu.quanlyctdtdb.util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Helper xuat CSV cho cac trang list.
 *
 * <p><b>Yeu cau key:</b>
 * <ol>
 *   <li><b>UTF-8 BOM</b> dau file → Excel/LibreOffice mo file Vietnamese
 *       co dau dung mac dinh khong can chinh encoding.</li>
 *   <li>Truong co dau {@code "}, {@code ,}, hoac newline → boc bang dau ngoac
 *       kep + escape {@code "} thanh {@code ""} (RFC 4180).</li>
 *   <li>Tat ca cac truong null → chuyen thanh chuoi rong.</li>
 *   <li>Phan cach dong dung CRLF — chuan CSV dung trong Excel Windows.</li>
 * </ol>
 *
 * <p><b>Khong dung thu vien ngoai (vd OpenCSV)</b>: dataset hoc vu vua phai
 * (toi da vai chuc ngan dong), CSV writer thu cong + StringBuilder du dap ung,
 * giam dependency.
 */
public final class CsvExportUtil {

    private CsvExportUtil() {}

    private static final char BOM = '\uFEFF';
    private static final String NEWLINE = "\r\n";

    /**
     * Ghi CSV vao response. Tu dong gan header Content-Type, Content-Disposition.
     *
     * @param response HttpServletResponse de stream
     * @param baseFileName ten file khong co duoi (dau .csv tu add) — vd "nguoi-dung"
     * @param headers ten cot, hien o dong dau tien
     * @param rows mang du lieu, moi phan tu la 1 dong gom cac cell theo thu tu headers
     */
    public static void write(HttpServletResponse response,
                              String baseFileName,
                              String[] headers,
                              List<String[]> rows) throws IOException {
        String fileName = baseFileName + "_"
                + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
                + ".csv";

        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // RFC 5987 + plain fallback de cross-browser dung dau Tieng Viet.
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"; "
                + "filename*=UTF-8''" + java.net.URLEncoder.encode(
                        fileName, StandardCharsets.UTF_8));

        try (Writer w = new OutputStreamWriter(
                response.getOutputStream(), StandardCharsets.UTF_8)) {
            // BOM
            w.write(BOM);

            // Header row
            writeRow(w, headers);

            // Data rows
            for (String[] row : rows) {
                writeRow(w, row);
            }

            w.flush();
        }
    }

    private static void writeRow(Writer w, String[] cells) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cells.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(escape(cells[i]));
        }
        sb.append(NEWLINE);
        w.write(sb.toString());
    }

    /**
     * Escape 1 cell theo RFC 4180.
     * Neu cell chua "," / "\"" / "\n" / "\r" → bao boi "..." va escape " thanh "".
     */
    private static String escape(String cell) {
        if (cell == null) return "";
        boolean needQuote =
                cell.indexOf(',')  >= 0 ||
                cell.indexOf('"')  >= 0 ||
                cell.indexOf('\n') >= 0 ||
                cell.indexOf('\r') >= 0;
        if (!needQuote) return cell;
        return '"' + cell.replace("\"", "\"\"") + '"';
    }

    /** Tien ich tien dung de tao mang String[] tu cac doi tuong tuy y. */
    public static String[] row(Object... cells) {
        String[] out = new String[cells.length];
        for (int i = 0; i < cells.length; i++) {
            out[i] = cells[i] == null ? "" : cells[i].toString();
        }
        return out;
    }
}
