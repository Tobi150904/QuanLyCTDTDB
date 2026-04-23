package com.ntu.quanlyctdtdb.util;

/**
 * Cac ham tien ich tinh toan lien quan den Hoc Ky va Nam Hoc.
 *
 * <h3>Bai toan cot loi: Mapping "HocKy cua Nam Hoc" -&gt; "HocKy thu cua CTDT"</h3>
 *
 * <p>Mot CTDT (vi du {@code CTDT-CNTT-2022} voi {@code khoa=2022}) thuong dai
 * 8 hoc ky nghiep vu (HK1..HK8). Cot {@code CtdtHocPhan.HocKyThu} luu so ky
 * NAY — nghia la "hoc phan duoc xep vao ky thu may cua chuong trinh".
 *
 * <p>Tuy nhien {@code HocKyNamHoc.MaHocKy} co format {@code HKn-YYYY} — trong
 * do {@code n in {1,2,3}} la so ky <b>trong mot nam hoc</b> (1 = hoc ky 1,
 * 2 = hoc ky 2, 3 = ky he). Hai khai niem nay KHONG giong nhau.
 *
 * <p>Cong thuc quy doi (bo qua ky he):
 * <pre>
 *     programSemester = (hkYear - ctdtStartYear) * 2 + hkIndexInYear
 * </pre>
 * Vi du CTDT khoa 2022 + {@code HK1-2023}:
 * <ul>
 *   <li>ctdtStartYear = 2022</li>
 *   <li>hkYear = 2023 (parse tu {@code HK1-2023})</li>
 *   <li>hkIndexInYear = 1</li>
 *   <li>=&gt; programSemester = (2023 - 2022) * 2 + 1 = 3</li>
 * </ul>
 * Tuc la khi user vao trang Lop Hoc Phan, chon CTDT khoa 2022 + HK1 nam
 * 2023-2024 thi he thong can hien cac HP xep o HK3 cua CTDT (khong phai HK1).
 *
 * <p>Truoc fix: code dung truc tiep {@code parseHkIndexInYear(maHocKy)} nen
 * bat ky CTDT nao cung chi ra duoc HP tai HK1/HK2 (tuong ung HK1-/HK2- cua
 * bat ky nam nao) — do la bug.
 */
public final class HocKyUtil {

    private HocKyUtil() {}

    /**
     * Parse chi so ky TRONG NAM HOC (1..3) tu {@code MaHocKy} dang {@code HKn-YYYY}.
     *
     * @return 1..3 neu hop le, 0 neu khong parse duoc.
     */
    public static int parseHkIndexInYear(String maHocKy) {
        if (maHocKy == null || maHocKy.length() < 3 || !maHocKy.startsWith("HK")) {
            return 0;
        }
        char c = maHocKy.charAt(2);
        return Character.isDigit(c) ? Character.getNumericValue(c) : 0;
    }

    /**
     * Parse nam bat dau nam hoc tu {@code MaHocKy} dang {@code HKn-YYYY}.
     *
     * @return YYYY neu hop le, 0 neu khong parse duoc.
     */
    public static int parseHkYear(String maHocKy) {
        if (maHocKy == null) return 0;
        int dash = maHocKy.indexOf('-');
        if (dash < 0 || dash + 1 >= maHocKy.length()) return 0;
        try {
            return Integer.parseInt(maHocKy.substring(dash + 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parse nam bat dau cua CTDT tu gia tri cot {@code ChuongTrinhDaoTao.Khoa}.
     *
     * <p>{@code Khoa} co the la:
     * <ul>
     *   <li>Chi nam: {@code "2022"}</li>
     *   <li>Hoac dang "YYYY-YYYY": {@code "2022-2026"} — lay phan dau.</li>
     * </ul>
     *
     * @return nam bat dau (YYYY) hoac 0 neu khong parse duoc.
     */
    public static int parseCtdtStartYear(String khoa) {
        if (khoa == null) return 0;
        String s = khoa.trim();
        if (s.isEmpty()) return 0;
        int dash = s.indexOf('-');
        String head = dash > 0 ? s.substring(0, dash) : s;
        try {
            return Integer.parseInt(head.replaceAll("[^0-9]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Tinh "hoc ky thu may cua CTDT" tuong ung voi 1 hoc ky nam hoc cu the.
     *
     * <p>Chi ho tro HK chinh khoa (n=1 hoac 2). Ky he (n=3) tra ve 0 vi
     * khong duoc tinh vao khung CTDT chinh khoa 8 ky.
     *
     * @param khoa     gia tri {@code ChuongTrinhDaoTao.Khoa} (vd {@code "2022"}).
     * @param maHocKy  ma hoc ky dang {@code HKn-YYYY}.
     * @return programSemester (>=1) hoac 0 neu khong hop le / ky he.
     */
    public static int toProgramSemester(String khoa, String maHocKy) {
        int startYear  = parseCtdtStartYear(khoa);
        int hkYear     = parseHkYear(maHocKy);
        int hkInYear   = parseHkIndexInYear(maHocKy);
        if (startYear <= 0 || hkYear <= 0) return 0;
        if (hkInYear != 1 && hkInYear != 2) return 0;     // bo qua ky he / invalid
        int yearOffset = hkYear - startYear;
        if (yearOffset < 0) return 0;                     // hoc ky truoc khi CTDT mo
        return yearOffset * 2 + hkInYear;
    }
}
