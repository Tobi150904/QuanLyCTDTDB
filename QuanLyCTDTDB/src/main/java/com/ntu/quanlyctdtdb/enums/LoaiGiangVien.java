package com.ntu.quanlyctdtdb.enums;

/**
 * Phan loai giang vien trong bang {@code GiangVien.LoaiGiangVien}.
 *
 * <ul>
 *   <li><b>GiangVienTruong</b> — giang vien co huu cua truong, giang day
 *       cac hoc phan chinh khoa.</li>
 *   <li><b>GiangVienThinhGiang</b> — giang vien moi thinh giang (part-time),
 *       thuong la chuyen gia ben ngoai hoac GV truong khac. Seed data
 *       {@code scripts/02_seed_data.sql} co 2 ban ghi GV010/GV012 kieu nay.</li>
 *   <li><b>DoanhNghiep</b> — nguoi huong dan ben phia doanh nghiep tham gia
 *       dao tao (Adjunct Lecturer tu industry).</li>
 * </ul>
 *
 * <p><b>Note:</b> truoc day enum chi co {@code GiangVienTruong} va
 * {@code DoanhNghiep}, dan den loi 500 khi Hibernate load GV010 / GV012 tu DB
 * — khien moi GET {@code /hoc-phan/*}, {@code /ctdt/*}, {@code /lop-hanh-chinh/*}
 * ma co JOIN sang GiangVien deu that bai am tham (form khong render).
 */
public enum LoaiGiangVien {
    GiangVienTruong,
    GiangVienThinhGiang,
    DoanhNghiep
}
