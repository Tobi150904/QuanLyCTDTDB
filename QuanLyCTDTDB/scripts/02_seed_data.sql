-- =============================================================================
-- 02_seed_data.sql  --  Dữ liệu mẫu production-realistic
--
-- Phiên bản: v4 (Phase 3 — Production-realistic refactor)
-- Charset:   utf8mb4_unicode_ci (tên, nhận xét có dấu tiếng Việt)
-- Nguồn sự thật: docs/01_ERD_SCHEMA.md, docs/02_Mo Ta & Thiet Ke Du Lieu.md,
--               docs/03_WORKFLOW.md, scripts/01_create_tables.sql
--
-- THAY ĐỔI so với v3:
--   + Tên công ty, họ tên, địa chỉ, chuyên ngành, nhận xét — ĐỀU có dấu tiếng
--     Việt để phản ánh môi trường production thực tế của trường đại học VN.
--   + Mở rộng quy mô dữ liệu cho các trang list hiển thị đúng density production:
--       NguoiDung    20 → 52       (1 admin + 2 TTDTXS + 12 GV + 30 SV + 7 DN)
--       GiangVien     6 → 12
--       DoanhNghiep   4 → 9        (công ty phần mềm / công nghệ có thật ở VN)
--       LopHanhChinh  4 → 6        (thêm K23B, K24B)
--       SinhVien     12 → 30       (5 SV/lớp × 6 lớp) + 2 edge-case ThoiHoc/BaoLuu
--       HocPhan      10 → 14       (thêm HP cơ bản: NNLT, GTDL, PTTK, ATTT)
--       CTDT_HocPhan 18 → 29
--       LopHocPhan   12 → 20
--       DSSV_LHP     15 → 52       (mật độ học kỳ thật)
--       DotKienTap    3 → 5
--       DSSV_KT       7 → 17
--       DotThucTap    2 → 3
--       DSThucTap     4 → 10
--       KetQuaTT      4 → 12
--   + Hoàn toàn tương thích ngược: TẤT CẢ mã SV/GV/DN/CTDT/HP/LopHC cũ đều còn
--     (SV2022001..SV2022004, GV001..GV006, DN001..DN004, CTDT-CNTT-2022..2024,
--      HP-OOP..HP-KLTN, CNTT-K22A..CNTT-K24A) — không phá vỡ tham chiếu test/doc.
--
-- Mật khẩu mặc định cho mọi tài khoản: Password@123
-- BCrypt hash (BCrypt 2a, cost 10):
--   $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13
--
-- Thống kê chi tiết sau khi chạy: xem khối "KIỂM TRA NHANH" cuối file.
-- =============================================================================

USE QuanLyCTDTDB;

-- -----------------------------------------------------------------------------
-- DỌN DỮ LIỆU CŨ (idempotent)
--   MySQL 8+: TRUNCATE bị chặn bởi FK (lỗi #1701) ngay cả khi FK_CHECKS=0,
--   do đó dùng DELETE FROM theo thứ tự ngược phụ thuộc FK + reset AUTO_INCREMENT.
-- -----------------------------------------------------------------------------
SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM KetQuaThucTap;
DELETE FROM DanhSachThucTap;
DELETE FROM DotThucTap;
DELETE FROM DanhSachSinhVienKienTap;
DELETE FROM DotKienTap;
DELETE FROM DanhSachSinhVienLopHocPhan;
DELETE FROM LopHocPhan;
DELETE FROM CTDT_HocPhan;
DELETE FROM DoiNguGiangVienHP;
DELETE FROM SinhVien;
DELETE FROM LopHanhChinh;
DELETE FROM HocPhan;
DELETE FROM BCN_ThanhVien;
DELETE FROM ChuongTrinhDaoTao;
DELETE FROM NhomNguoiDung;
DELETE FROM VaiTroThucTap;
DELETE FROM DoanhNghiep;
DELETE FROM GiangVien;
DELETE FROM NguoiDung;
DELETE FROM HocKyNamHoc;

ALTER TABLE KetQuaThucTap   AUTO_INCREMENT = 1;
ALTER TABLE DanhSachThucTap AUTO_INCREMENT = 1;
ALTER TABLE DotThucTap      AUTO_INCREMENT = 1;
ALTER TABLE DotKienTap      AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 1. HocKyNamHoc  (độc lập)
--    Quy ước: MaHocKy = HK{1|2}-{NamBatDau}    (ví dụ HK1-2024)
--    Chỉ 1 học kỳ 'DangDienRa' tại một thời điểm (docs/02 §2).
-- =============================================================================
INSERT INTO HocKyNamHoc (MaHocKy, TenHocKy, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('HK1-2023', 'Học Kỳ 1 Năm 2023-2024', '2023-09-04', '2024-01-12', 'DaKetThuc'),
('HK2-2023', 'Học Kỳ 2 Năm 2023-2024', '2024-01-22', '2024-05-31', 'DaKetThuc'),
('HK1-2024', 'Học Kỳ 1 Năm 2024-2025', '2024-09-02', '2025-01-10', 'DangDienRa'),
('HK2-2024', 'Học Kỳ 2 Năm 2024-2025', '2025-01-20', '2025-05-30', 'SapDienRa'),
('HK1-2025', 'Học Kỳ 1 Năm 2025-2026', '2025-09-01', '2026-01-09', 'SapDienRa');

-- =============================================================================
-- 2. NguoiDung  (độc lập) — BCrypt hash cho Password@123
--    Format MaNguoiDung (docs/02 §1):
--      Admin  : AD + 3 số          → AD001
--      GV     : GV + 3 số          → GV001
--      SV     : SV + năm + 3 số    → SV2024001
--      DN     : DN + 3 số          → DN001
-- =============================================================================
INSERT INTO NguoiDung
    (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, SoDienThoai, TrangThaiTK, LoaiNguoiDung) VALUES

-- ---- Admin ----
('AD001', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'admin@ntu.edu.vn', 'Quản Trị Viên Hệ Thống', '0909000001', 1, 'Admin'),

-- ---- Giảng Viên: Ban Giám Hiệu / Phòng Đào Tạo / TTDTXS ----
('GV001', 'tran.van.an',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tran.van.an@ntu.edu.vn',    'PGS.TS. Trần Văn An',      '0912340001', 1, 'GiangVien'),
('GV002', 'le.thi.binh',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'le.thi.binh@ntu.edu.vn',    'TS. Lê Thị Bình',          '0912340002', 1, 'GiangVien'),

-- ---- Giảng Viên: CVHT + CNHP (CTDT K22) ----
('GV003', 'nguyen.van.cuong','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'nguyen.van.cuong@ntu.edu.vn','TS. Nguyễn Văn Cường',    '0912340003', 1, 'GiangVien'),
('GV004', 'pham.thi.dung',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'pham.thi.dung@ntu.edu.vn',  'ThS. Phạm Thị Dung',       '0912340004', 1, 'GiangVien'),

-- ---- Giảng Viên: CVHT + CNHP (CTDT K23) ----
('GV005', 'hoang.van.em',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'hoang.van.em@ntu.edu.vn',   'ThS. Hoàng Văn Em',        '0912340005', 1, 'GiangVien'),
('GV006', 'vu.thi.giang',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'vu.thi.giang@ntu.edu.vn',   'TS. Vũ Thị Giang',         '0912340006', 1, 'GiangVien'),

-- ---- Giảng Viên: CVHT + CNHP (CTDT K24) ----
('GV007', 'do.minh.hieu',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'do.minh.hieu@ntu.edu.vn',   'ThS. Đỗ Minh Hiếu',        '0912340007', 1, 'GiangVien'),
('GV008', 'bui.thanh.ha',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'bui.thanh.ha@ntu.edu.vn',   'ThS. Bùi Thanh Hà',        '0912340008', 1, 'GiangVien'),

-- ---- Giảng Viên: bộ môn chuyên môn (giảng dạy theo học phần) ----
('GV009', 'ngo.thi.lan',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'ngo.thi.lan@ntu.edu.vn',    'TS. Ngô Thị Lan',          '0912340009', 1, 'GiangVien'),
('GV010', 'dang.van.minh',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'dang.van.minh@ntu.edu.vn',  'ThS. Đặng Văn Minh',       '0912340010', 1, 'GiangVien'),
('GV011', 'phan.thi.ngoc',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'phan.thi.ngoc@ntu.edu.vn',  'TS. Phan Thị Ngọc',        '0912340011', 1, 'GiangVien'),
('GV012', 'ly.quoc.phong',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'ly.quoc.phong@ntu.edu.vn',  'ThS. Lý Quốc Phong',       '0912340012', 1, 'GiangVien'),

-- ---- Sinh Viên: K24 (năm 1 — CNTT-K24A + CNTT-K24B) ----
('SV2024001', 'sv.2024001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024001@sv.ntu.edu.vn', 'Nguyễn Thị Hoa',       '0978001001', 1, 'SinhVien'),
('SV2024002', 'sv.2024002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024002@sv.ntu.edu.vn', 'Trần Minh Khoa',       '0978001002', 1, 'SinhVien'),
('SV2024003', 'sv.2024003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024003@sv.ntu.edu.vn', 'Lê Quốc Long',         '0978001003', 1, 'SinhVien'),
('SV2024004', 'sv.2024004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024004@sv.ntu.edu.vn', 'Phạm Hà My',           '0978001004', 1, 'SinhVien'),
('SV2024005', 'sv.2024005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024005@sv.ntu.edu.vn', 'Hoàng Tuấn Nam',       '0978001005', 1, 'SinhVien'),
('SV2024006', 'sv.2024006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024006@sv.ntu.edu.vn', 'Vũ Thị Ngân',          '0978001006', 1, 'SinhVien'),
('SV2024007', 'sv.2024007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024007@sv.ntu.edu.vn', 'Đỗ Anh Quân',          '0978001007', 1, 'SinhVien'),
('SV2024008', 'sv.2024008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024008@sv.ntu.edu.vn', 'Bùi Thu Thảo',         '0978001008', 1, 'SinhVien'),
('SV2024009', 'sv.2024009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024009@sv.ntu.edu.vn', 'Ngô Văn Thiện',        '0978001009', 1, 'SinhVien'),
('SV2024010', 'sv.2024010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024010@sv.ntu.edu.vn', 'Lý Thanh Vy',          '0978001010', 1, 'SinhVien'),

-- ---- Sinh Viên: K23 (năm 2 — CNTT-K23A + CNTT-K23B) ----
('SV2023001', 'sv.2023001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023001@sv.ntu.edu.vn', 'Phạm Ngọc Mai',        '0978002001', 1, 'SinhVien'),
('SV2023002', 'sv.2023002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023002@sv.ntu.edu.vn', 'Hoàng Thị Như',        '0978002002', 1, 'SinhVien'),
('SV2023003', 'sv.2023003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023003@sv.ntu.edu.vn', 'Đỗ Quốc Tuấn',         '0978002003', 1, 'SinhVien'),
('SV2023004', 'sv.2023004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023004@sv.ntu.edu.vn', 'Trương Quốc Bảo',      '0978002004', 1, 'SinhVien'),
('SV2023005', 'sv.2023005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023005@sv.ntu.edu.vn', 'Nguyễn Thảo Vy',       '0978002005', 1, 'SinhVien'),
('SV2023006', 'sv.2023006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023006@sv.ntu.edu.vn', 'Lê Xuân Khang',        '0978002006', 1, 'SinhVien'),
('SV2023007', 'sv.2023007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023007@sv.ntu.edu.vn', 'Võ Thùy Dương',        '0978002007', 1, 'SinhVien'),
('SV2023008', 'sv.2023008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023008@sv.ntu.edu.vn', 'Trần Huy Hoàng',       '0978002008', 1, 'SinhVien'),
('SV2023009', 'sv.2023009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023009@sv.ntu.edu.vn', 'Phan Kim Anh',         '0978002009', 1, 'SinhVien'),
('SV2023010', 'sv.2023010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023010@sv.ntu.edu.vn', 'Bùi Công Đạt',         '0978002010', 1, 'SinhVien'),

-- ---- Sinh Viên: K22 (năm 3 — đi kiến tập + chuẩn bị thực tập) ----
('SV2022001', 'sv.2022001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022001@sv.ntu.edu.vn', 'Võ Thành Phong',       '0978003001', 1, 'SinhVien'),
('SV2022002', 'sv.2022002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022002@sv.ntu.edu.vn', 'Bùi Thị Quỳnh',        '0978003002', 1, 'SinhVien'),
('SV2022003', 'sv.2022003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022003@sv.ntu.edu.vn', 'Ngô Văn Tân',          '0978003003', 1, 'SinhVien'),
('SV2022004', 'sv.2022004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022004@sv.ntu.edu.vn', 'Đào Thị Uyên',         '0978003004', 1, 'SinhVien'),
('SV2022006', 'sv.2022006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022006@sv.ntu.edu.vn', 'Lê Hồng Sơn',          '0978003006', 1, 'SinhVien'),
('SV2022007', 'sv.2022007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022007@sv.ntu.edu.vn', 'Trần Mỹ Linh',         '0978003007', 1, 'SinhVien'),
('SV2022008', 'sv.2022008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022008@sv.ntu.edu.vn', 'Nguyễn Quang Huy',     '0978003008', 1, 'SinhVien'),
('SV2022009', 'sv.2022009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022009@sv.ntu.edu.vn', 'Phạm Hoàng Kim',       '0978003009', 1, 'SinhVien'),
('SV2022010', 'sv.2022010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022010@sv.ntu.edu.vn', 'Hoàng Gia Bảo',        '0978003010', 1, 'SinhVien'),

-- ---- Sinh Viên edge-case: KHÁC trạng thái DangHoc (để minh hoạ auto-add chỉ lấy DangHoc) ----
('SV2022005', 'sv.2022005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022005@sv.ntu.edu.vn', 'Lý Thị Vân',           '0978003005', 1, 'SinhVien'),

-- ---- Doanh Nghiệp (tài khoản login cho người đại diện DN) ----
('DN001', 'dn.fpt',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tuyendung@fpt.com.vn',      'FPT Software',           '0243768888', 1, 'DoanhNghiep'),
('DN002', 'dn.vng',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'thuctap@vng.com.vn',        'VNG Corporation',        '0283962828', 1, 'DoanhNghiep'),
('DN003', 'dn.tma',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'hr@tma.com.vn',             'TMA Solutions',          '0283997300', 1, 'DoanhNghiep'),
('DN004', 'dn.viettel', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tuyendung@viettel.com.vn',  'Viettel Solutions',      '0243628400', 1, 'DoanhNghiep'),
('DN005', 'dn.misa',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tuyendung@misa.com.vn',     'Công ty Cổ phần MISA',    '0243762868', 1, 'DoanhNghiep'),
('DN006', 'dn.kms',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'careers@kms-technology.com','KMS Technology',         '0283811999', 1, 'DoanhNghiep'),
('DN007', 'dn.vnpay',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tuyendung@vnpay.vn',        'VNPay',                  '0247108998', 1, 'DoanhNghiep');

-- =============================================================================
-- 3. GiangVien  ← NguoiDung
--    Ràng buộc (docs/02 §3.2): MaGV = MaNguoiDung (khoá chính trùng).
-- =============================================================================
INSERT INTO GiangVien (MaGV, MaNguoiDung, HocHam, HocVi, ChuyenNganh, LoaiGiangVien) VALUES
('GV001', 'GV001', 'Phó Giáo Sư', 'Tiến Sĩ', 'Công Nghệ Phần Mềm',  'GiangVienTruong'),
('GV002', 'GV002', NULL,          'Tiến Sĩ', 'Mạng Máy Tính',       'GiangVienTruong'),
('GV003', 'GV003', NULL,          'Tiến Sĩ', 'Hệ Thống Thông Tin',  'GiangVienTruong'),
('GV004', 'GV004', NULL,          'Thạc Sĩ', 'Công Nghệ Phần Mềm',  'GiangVienTruong'),
('GV005', 'GV005', NULL,          'Thạc Sĩ', 'Cơ Sở Dữ Liệu',       'GiangVienTruong'),
('GV006', 'GV006', NULL,          'Tiến Sĩ', 'Khoa Học Dữ Liệu',    'GiangVienTruong'),
('GV007', 'GV007', NULL,          'Thạc Sĩ', 'Lập Trình Web',       'GiangVienTruong'),
('GV008', 'GV008', NULL,          'Thạc Sĩ', 'Trí Tuệ Nhân Tạo',    'GiangVienTruong'),
('GV009', 'GV009', NULL,          'Tiến Sĩ', 'An Toàn Thông Tin',   'GiangVienTruong'),
('GV010', 'GV010', NULL,          'Thạc Sĩ', 'Kỹ Thuật Phần Mềm',   'GiangVienThinhGiang'),
('GV011', 'GV011', NULL,          'Tiến Sĩ', 'Phân Tích Thiết Kế',  'GiangVienTruong'),
('GV012', 'GV012', NULL,          'Thạc Sĩ', 'Lập Trình Di Động',   'GiangVienThinhGiang');

-- =============================================================================
-- 4. DoanhNghiep  (độc lập)
--    TrangThai: DangHopTac | TamNgung.
--    Business (docs/02 §3.7): DotKienTap chỉ tham chiếu DN 'DangHopTac'.
-- =============================================================================
INSERT INTO DoanhNghiep
    (MaDoanhNghiep, TenDoanhNghiep,                            LinhVuc,                          NguoiDaiDien,       Email,                       SoDienThoai,  DiaChiDN,                                                     TrangThai) VALUES
('DN001', 'Công ty TNHH Phần mềm FPT (FPT Software)',          'Phần mềm & Dịch vụ CNTT',         'Nguyễn Văn Hùng',  'tuyendung@fpt.com.vn',      '0243768888', 'Tòa nhà FPT, Số 17 Duy Tân, Cầu Giấy, Hà Nội',                'DangHopTac'),
('DN002', 'Công ty Cổ phần VNG',                               'Game & Giải trí số',              'Lê Thị Thu',       'thuctap@vng.com.vn',        '0283962828', 'Số 182 Lê Đại Hành, Phường 15, Quận 11, TP. HCM',             'DangHopTac'),
('DN003', 'Công ty TNHH TMA Solutions',                        'Phần mềm & Outsourcing',          'Trần Minh Đăng',  'hr@tma.com.vn',             '0283997300', 'Công viên Phần mềm Quang Trung, Quận 12, TP. HCM',            'DangHopTac'),
('DN004', 'Tổng Công ty Giải pháp Doanh nghiệp Viettel',       'Viễn thông & Hạ tầng CNTT',       'Phạm Thị Lan',    'tuyendung@viettel.com.vn',  '0243628400', 'Số 1 Trần Hữu Dực, Mỹ Đình 2, Nam Từ Liêm, Hà Nội',           'DangHopTac'),
('DN005', 'Công ty Cổ phần MISA',                              'Phần mềm quản lý doanh nghiệp',   'Đinh Thị Thuý Hà','tuyendung@misa.com.vn',     '0243762868', 'Tòa nhà MISA, 218 Đội Cấn, Ba Đình, Hà Nội',                  'DangHopTac'),
('DN006', 'Công ty TNHH KMS Technology Vietnam',               'Phần mềm gia công cho US',        'Đoàn Quốc Việt',  'careers@kms-technology.com','0283811999', 'Tòa QTSC 9, Công viên Phần mềm Quang Trung, TP. HCM',         'DangHopTac'),
('DN007', 'Công ty Cổ phần Giải pháp Thanh toán Việt Nam (VNPay)','Thanh toán điện tử & Fintech','Hoàng Minh Tuấn','tuyendung@vnpay.vn',        '0247108998', 'Tầng 8, Tòa nhà Mipec, 229 Tây Sơn, Đống Đa, Hà Nội',         'DangHopTac'),
('DN008', 'Công ty Cổ phần Tiki',                              'Thương mại điện tử',              'Phan Thị Thu Hà',  'hr@tiki.vn',                '0283456789', 'Số 52 Út Tịch, Phường 4, Quận Tân Bình, TP. HCM',             'DangHopTac'),
('DN009', 'Công ty TNHH NashTech Việt Nam',                    'Phần mềm & Dịch vụ CNTT',         'Võ Anh Tuấn',      'hr@nashtechglobal.com',     '0283815555', 'Số 117 Nguyễn Cửu Vân, Bình Thạnh, TP. HCM',                  'TamNgung');

-- =============================================================================
-- 5. VaiTroThucTap  (danh mục vai trò đánh giá trong KetQuaThucTap)
--    docs/02 §3.9, docs/03 §6.
--    LƯU Ý: KetQuaThucTap.fk_kqtt_nguoidanhgia REFERENCES GiangVien(MaGV),
--    nên mọi "MaNguoiDanhGia" phải có bản ghi trong bảng GiangVien. SV KHÔNG
--    lưu trong GiangVien ⇒ KHÔNG có vai trò 'SV' (SV nhận xét định tính dùng
--    trường text trên DanhSachThucTap, không phải 1 bản ghi KetQuaThucTap).
-- =============================================================================
INSERT INTO VaiTroThucTap (MaVaiTro, TenVaiTro, MoTa) VALUES
('GV',   'Giảng Viên Hướng Dẫn', 'Giảng viên phụ trách hướng dẫn sinh viên thực tập tại trường'),
('DN',   'Doanh Nghiệp',         'Cán bộ doanh nghiệp phụ trách sinh viên thực tập tại đơn vị'),
('CVHT', 'Cố Vấn Học Tập',       'Cố vấn học tập của lớp hành chính — đánh giá tổng thể');

-- =============================================================================
-- 6. NhomNguoiDung  ← NguoiDung (docs/02 §2)
--    Vai trò nghiệp vụ: PDT | TTDTXS | CVHT | CNHP
--    - GV001: Trưởng Phòng Đào Tạo + kiêm viên TTDTXS
--    - GV002: TTDTXS
--    - GV003..GV008: CVHT từng lớp (+ một số kiêm CNHP)
--    - GV004..GV012: CNHP một số học phần
-- =============================================================================
INSERT INTO NhomNguoiDung (MaNguoiDung, VaiTro) VALUES
-- Phòng Đào Tạo & TTDTXS
('GV001', 'PDT'),
('GV001', 'TTDTXS'),
('GV002', 'TTDTXS'),
-- Cố Vấn Học Tập (mỗi GV phụ trách 1 lớp)
('GV003', 'CVHT'),   -- CNTT-K22A
('GV004', 'CVHT'),   -- CNTT-K22B
('GV005', 'CVHT'),   -- CNTT-K23A
('GV006', 'CVHT'),   -- CNTT-K23B
('GV007', 'CVHT'),   -- CNTT-K24A
('GV008', 'CVHT'),   -- CNTT-K24B
-- Chủ Nhiệm Học Phần
('GV002', 'CNHP'),   -- HP-MMT, HP-OOP
('GV003', 'CNHP'),   -- HP-HTTT, HP-KT
('GV004', 'CNHP'),   -- HP-LTW, HP-PTTK
('GV005', 'CNHP'),   -- HP-CSDL, HP-TTDL
('GV006', 'CNHP'),   -- HP-AI
('GV007', 'CNHP'),   -- HP-NNLT
('GV008', 'CNHP'),   -- HP-GTDL
('GV009', 'CNHP'),   -- HP-ATTT
('GV011', 'CNHP');   -- HP-KLTN, HP-TT

-- =============================================================================
-- 7. ChuongTrinhDaoTao  ← NguoiDung (NguoiTao + NguoiDuyet)
--    Business (docs/02 §3.4): NguoiDuyet KHÁC NguoiTao; khi chuyển DaDuyet
--    → service auto tạo LopHocPhan theo SoLopDuKien.
-- =============================================================================
INSERT INTO ChuongTrinhDaoTao
    (MaCTDT,             TenCTDT,                                       Khoa,   FileWord,                                               TrangThai, NguoiTao, NguoiDuyet, NgayDuyet) VALUES
('CTDT-CNTT-2022', 'CTĐT Xuất Sắc Ngành Công Nghệ Thông Tin — Khoá 2022-2026', '2022', 'ctdt/CTDT-CNTT-2022_20220801_khung_ctdt.docx', 'DaDuyet',  'GV001', 'GV002', '2022-08-15 09:00:00'),
('CTDT-CNTT-2023', 'CTĐT Xuất Sắc Ngành Công Nghệ Thông Tin — Khoá 2023-2027', '2023', 'ctdt/CTDT-CNTT-2023_20230801_khung_ctdt.docx', 'DaDuyet',  'GV001', 'GV002', '2023-08-20 10:30:00'),
('CTDT-CNTT-2024', 'CTĐT Xuất Sắc Ngành Công Nghệ Thông Tin — Khoá 2024-2028', '2024', 'ctdt/CTDT-CNTT-2024_20240715_khung_ctdt.docx', 'DaDuyet',  'GV001', 'GV002', '2024-07-20 14:15:00'),
('CTDT-CNTT-2025', 'CTĐT Xuất Sắc Ngành Công Nghệ Thông Tin — Khoá 2025-2029', '2025', NULL,                                             'ChoDuyet','GV001', NULL,    NULL);

-- =============================================================================
-- 8. BCN_ThanhVien  ← ChuongTrinhDaoTao, GiangVien
--    Chức danh: ChuNhiem | ThuKy | UyVien. Ràng buộc BizCt (docs/02 §3.4):
--    mỗi CTDT có DUY NHẤT 1 ChuNhiem.
-- =============================================================================
INSERT INTO BCN_ThanhVien (MaCTDT, MaGV, ChucDanh, NgayBoNhiem, GhiChu) VALUES
-- CTDT K22 (đang năm 3-4)
('CTDT-CNTT-2022', 'GV001', 'ChuNhiem', '2022-08-01', 'Trưởng ban, phụ trách chuyên môn chung'),
('CTDT-CNTT-2022', 'GV002', 'ThuKy',    '2022-08-01', NULL),
('CTDT-CNTT-2022', 'GV003', 'UyVien',   '2022-08-01', NULL),
('CTDT-CNTT-2022', 'GV011', 'UyVien',   '2023-02-01', 'Bổ sung nhân sự PTTK'),
-- CTDT K23 (đang năm 2)
('CTDT-CNTT-2023', 'GV001', 'ChuNhiem', '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV004', 'ThuKy',    '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV005', 'UyVien',   '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV009', 'UyVien',   '2023-08-01', 'Phụ trách mảng ATTT'),
-- CTDT K24 (đang năm 1)
('CTDT-CNTT-2024', 'GV002', 'ChuNhiem', '2024-07-15', 'Thay GV001 để tập trung CTDT K25'),
('CTDT-CNTT-2024', 'GV006', 'ThuKy',    '2024-07-15', NULL),
('CTDT-CNTT-2024', 'GV007', 'UyVien',   '2024-07-15', NULL),
('CTDT-CNTT-2024', 'GV008', 'UyVien',   '2024-07-15', NULL),
-- CTDT K25 (chờ duyệt)
('CTDT-CNTT-2025', 'GV001', 'ChuNhiem', '2025-06-01', 'CTĐT dự kiến, đang hoàn thiện'),
('CTDT-CNTT-2025', 'GV006', 'ThuKy',    '2025-06-01', NULL),
('CTDT-CNTT-2025', 'GV008', 'UyVien',   '2025-06-01', NULL);

-- =============================================================================
-- 9. HocPhan  ← GiangVien (ChuNhiemHP)
--    LoaiHocPhan: LyThuyet | ThucHanh | DoAn | ThucTap | KienTap
--    TrangThai:   BanNhap | ChoDuyet | DaDuyet
-- =============================================================================
INSERT INTO HocPhan
    (MaHocPhan, TenHocPhan,                              SoTinChi, LoaiHocPhan, ChuNhiemHP, FileDeCuong,                               TrangThai) VALUES
-- Học phần đại cương & cơ sở ngành
('HP-NNLT', 'Nhập Môn Lập Trình',                         3,  'LyThuyet', 'GV007', 'hocphan/HP-NNLT_20220901_decuong.pdf',  'DaDuyet'),
('HP-GTDL', 'Cấu Trúc Dữ Liệu & Giải Thuật',              4,  'LyThuyet', 'GV008', 'hocphan/HP-GTDL_20220901_decuong.pdf',  'DaDuyet'),
-- Học phần chuyên ngành (đã duyệt)
('HP-OOP',  'Lập Trình Hướng Đối Tượng',                  3,  'LyThuyet', 'GV002', 'hocphan/HP-OOP_20220801_decuong.pdf',   'DaDuyet'),
('HP-CSDL', 'Cơ Sở Dữ Liệu',                              3,  'LyThuyet', 'GV005', 'hocphan/HP-CSDL_20220801_decuong.pdf',  'DaDuyet'),
('HP-MMT',  'Mạng Máy Tính',                              3,  'LyThuyet', 'GV002', 'hocphan/HP-MMT_20220801_decuong.pdf',   'DaDuyet'),
('HP-HTTT', 'Phân Tích & Thiết Kế Hệ Thống Thông Tin',    3,  'LyThuyet', 'GV003', 'hocphan/HP-HTTT_20230201_decuong.pdf',  'DaDuyet'),
('HP-LTW',  'Lập Trình Web',                              3,  'LyThuyet', 'GV004', 'hocphan/HP-LTW_20230801_decuong.pdf',   'DaDuyet'),
('HP-TTDL', 'Thực Hành Thiết Kế Dữ Liệu',                 2,  'ThucHanh', 'GV005', 'hocphan/HP-TTDL_20230801_decuong.pdf',  'DaDuyet'),
('HP-PTTK', 'Phân Tích Thiết Kế Phần Mềm',                3,  'LyThuyet', 'GV011', 'hocphan/HP-PTTK_20230901_decuong.pdf',  'DaDuyet'),
('HP-ATTT', 'An Toàn & Bảo Mật Thông Tin',                3,  'LyThuyet', 'GV009', 'hocphan/HP-ATTT_20240101_decuong.pdf',  'DaDuyet'),
('HP-AI',   'Nhập Môn Trí Tuệ Nhân Tạo',                  3,  'LyThuyet', 'GV006', 'hocphan/HP-AI_20240701_decuong.pdf',    'DaDuyet'),
-- Học phần thực hành / thực tập / khoá luận
('HP-KT',   'Kiến Tập Doanh Nghiệp',                      2,  'KienTap',  'GV003', 'hocphan/HP-KT_20230101_decuong.pdf',    'DaDuyet'),
('HP-TT',   'Thực Tập Cuối Khoá',                         6,  'ThucTap',  'GV001', 'hocphan/HP-TT_20230101_decuong.pdf',    'DaDuyet'),
('HP-KLTN', 'Khoá Luận Tốt Nghiệp',                       10, 'DoAn',     'GV001', 'hocphan/HP-KLTN_20230101_decuong.pdf',  'DaDuyet');

-- =============================================================================
-- 10. DoiNguGiangVienHP  ← HocPhan, GiangVien
--     Business (docs/02 §3.3): Khi tạo HocPhan, service TỰ ĐỘNG INSERT CNHP.
--     Các GV khác trong đội ngũ do CNHP bổ sung sau. Seed mô phỏng đầy đủ.
-- =============================================================================
INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien, TrangThai) VALUES
-- CNHP tự động (bắt buộc tồn tại cho mỗi HP)
('HP-NNLT', 'GV007', 1),
('HP-GTDL', 'GV008', 1),
('HP-OOP',  'GV002', 1),
('HP-CSDL', 'GV005', 1),
('HP-MMT',  'GV002', 1),
('HP-HTTT', 'GV003', 1),
('HP-LTW',  'GV004', 1),
('HP-TTDL', 'GV005', 1),
('HP-PTTK', 'GV011', 1),
('HP-ATTT', 'GV009', 1),
('HP-AI',   'GV006', 1),
('HP-KT',   'GV003', 1),
('HP-TT',   'GV001', 1),
('HP-KLTN', 'GV001', 1),
-- GV bổ sung (do CNHP thêm vào đội ngũ giảng dạy)
('HP-NNLT', 'GV004', 1), ('HP-NNLT', 'GV010', 1),
('HP-GTDL', 'GV011', 1), ('HP-GTDL', 'GV002', 1),
('HP-OOP',  'GV004', 1), ('HP-OOP',  'GV010', 1), ('HP-OOP',  'GV012', 1),
('HP-CSDL', 'GV001', 1), ('HP-CSDL', 'GV003', 1),
('HP-MMT',  'GV006', 1), ('HP-MMT',  'GV009', 1),
('HP-HTTT', 'GV001', 1), ('HP-HTTT', 'GV011', 1),
('HP-LTW',  'GV002', 1), ('HP-LTW',  'GV007', 1), ('HP-LTW',  'GV012', 1),
('HP-TTDL', 'GV004', 1),
('HP-PTTK', 'GV003', 1), ('HP-PTTK', 'GV004', 1),
('HP-ATTT', 'GV002', 1), ('HP-ATTT', 'GV006', 1),
('HP-AI',   'GV008', 1), ('HP-AI',   'GV005', 1),
('HP-KT',   'GV001', 1), ('HP-KT',   'GV004', 1),
('HP-KLTN', 'GV002', 1), ('HP-KLTN', 'GV005', 1), ('HP-KLTN', 'GV006', 1);

-- =============================================================================
-- 11. LopHanhChinh  ← ChuongTrinhDaoTao, GiangVien (MaCoVan)
--     Quy ước MaLopHC (docs/02 §1): {NGANH}-K{khoá}{chữCái}  (CNTT-K22A)
-- =============================================================================
INSERT INTO LopHanhChinh (MaLopHC, TenLop, MaCTDT, KhoaHoc, MaCoVan) VALUES
('CNTT-K22A', 'CNTT Xuất Sắc K22 — Lớp A', 'CTDT-CNTT-2022', '2022', 'GV003'),
('CNTT-K22B', 'CNTT Xuất Sắc K22 — Lớp B', 'CTDT-CNTT-2022', '2022', 'GV004'),
('CNTT-K23A', 'CNTT Xuất Sắc K23 — Lớp A', 'CTDT-CNTT-2023', '2023', 'GV005'),
('CNTT-K23B', 'CNTT Xuất Sắc K23 — Lớp B', 'CTDT-CNTT-2023', '2023', 'GV006'),
('CNTT-K24A', 'CNTT Xuất Sắc K24 — Lớp A', 'CTDT-CNTT-2024', '2024', 'GV007'),
('CNTT-K24B', 'CNTT Xuất Sắc K24 — Lớp B', 'CTDT-CNTT-2024', '2024', 'GV008');

-- =============================================================================
-- 12. SinhVien  ← NguoiDung, LopHanhChinh
--     Ràng buộc (docs/02 §3.2): MaSV = MaNguoiDung.
--     TrangThaiSV: DangHoc | BaoLuu | ThoiHoc | DaTotNghiep.
--     Chỉ SV 'DangHoc' được tự động thêm vào DotKienTap khi tạo đợt.
-- =============================================================================
INSERT INTO SinhVien (MaSV, MaNguoiDung, MaLopHC, TrangThaiSV) VALUES
-- K22A (5 SV)
('SV2022001', 'SV2022001', 'CNTT-K22A', 'DangHoc'),
('SV2022002', 'SV2022002', 'CNTT-K22A', 'DangHoc'),
('SV2022006', 'SV2022006', 'CNTT-K22A', 'DangHoc'),
('SV2022007', 'SV2022007', 'CNTT-K22A', 'DangHoc'),
('SV2022008', 'SV2022008', 'CNTT-K22A', 'DangHoc'),
-- K22B (5 SV + 1 edge case ThoiHoc)
('SV2022003', 'SV2022003', 'CNTT-K22B', 'DangHoc'),
('SV2022004', 'SV2022004', 'CNTT-K22B', 'DangHoc'),
('SV2022009', 'SV2022009', 'CNTT-K22B', 'DangHoc'),
('SV2022010', 'SV2022010', 'CNTT-K22B', 'DangHoc'),
('SV2022005', 'SV2022005', 'CNTT-K22B', 'ThoiHoc'),   -- đã thôi học — KHÔNG auto-add vào Đợt KT
-- K23A (5 SV + 1 edge case BaoLuu)
('SV2023001', 'SV2023001', 'CNTT-K23A', 'DangHoc'),
('SV2023002', 'SV2023002', 'CNTT-K23A', 'DangHoc'),
('SV2023003', 'SV2023003', 'CNTT-K23A', 'DangHoc'),
('SV2023005', 'SV2023005', 'CNTT-K23A', 'DangHoc'),
('SV2023006', 'SV2023006', 'CNTT-K23A', 'DangHoc'),
('SV2023004', 'SV2023004', 'CNTT-K23A', 'BaoLuu'),    -- đang bảo lưu — KHÔNG auto-add vào Đợt KT
-- K23B (5 SV)
('SV2023007', 'SV2023007', 'CNTT-K23B', 'DangHoc'),
('SV2023008', 'SV2023008', 'CNTT-K23B', 'DangHoc'),
('SV2023009', 'SV2023009', 'CNTT-K23B', 'DangHoc'),
('SV2023010', 'SV2023010', 'CNTT-K23B', 'DangHoc'),
-- K24A (5 SV)
('SV2024001', 'SV2024001', 'CNTT-K24A', 'DangHoc'),
('SV2024002', 'SV2024002', 'CNTT-K24A', 'DangHoc'),
('SV2024003', 'SV2024003', 'CNTT-K24A', 'DangHoc'),
('SV2024004', 'SV2024004', 'CNTT-K24A', 'DangHoc'),
('SV2024005', 'SV2024005', 'CNTT-K24A', 'DangHoc'),
-- K24B (5 SV)
('SV2024006', 'SV2024006', 'CNTT-K24B', 'DangHoc'),
('SV2024007', 'SV2024007', 'CNTT-K24B', 'DangHoc'),
('SV2024008', 'SV2024008', 'CNTT-K24B', 'DangHoc'),
('SV2024009', 'SV2024009', 'CNTT-K24B', 'DangHoc'),
('SV2024010', 'SV2024010', 'CNTT-K24B', 'DangHoc');

-- =============================================================================
-- 13. CTDT_HocPhan  ← ChuongTrinhDaoTao, HocPhan
--     Mỗi dòng: HP thuộc CTDT, học ở HocKyThu nào, số lớp dự kiến, bắt buộc/tự chọn.
--     Business: chỉ thêm HP có TrangThai='DaDuyet'.
-- =============================================================================
INSERT INTO CTDT_HocPhan (MaCTDT, MaHocPhan, HocKyThu, SoLopDuKien, BatBuoc, GhiChu, FileDeCuong) VALUES
-- CTDT-CNTT-2022 (K22 — đang năm 3-4, khung đầy đủ)
('CTDT-CNTT-2022', 'HP-NNLT', 1, 2, 1, 'Nhập môn lập trình',              NULL),
('CTDT-CNTT-2022', 'HP-OOP',  1, 2, 1, 'Cơ sở hướng đối tượng',           NULL),
('CTDT-CNTT-2022', 'HP-GTDL', 2, 2, 1, 'Cấu trúc dữ liệu & giải thuật',   NULL),
('CTDT-CNTT-2022', 'HP-MMT',  2, 1, 1, 'Nền tảng mạng',                   NULL),
('CTDT-CNTT-2022', 'HP-CSDL', 3, 2, 1, 'Thiết kế & truy vấn SQL',         NULL),
('CTDT-CNTT-2022', 'HP-HTTT', 4, 1, 1, 'Phân tích thiết kế HTTT',         NULL),
('CTDT-CNTT-2022', 'HP-LTW',  5, 2, 1, 'Full-stack web development',      NULL),
('CTDT-CNTT-2022', 'HP-TTDL', 5, 1, 1, 'Thực hành cùng HP-CSDL',          NULL),
('CTDT-CNTT-2022', 'HP-PTTK', 5, 1, 1, 'PTTK phần mềm',                   NULL),
('CTDT-CNTT-2022', 'HP-KT',   6, 1, 1, 'Kiến tập doanh nghiệp theo lớp',  NULL),
('CTDT-CNTT-2022', 'HP-ATTT', 6, 1, 0, 'Tự chọn chuyên sâu',              NULL),
('CTDT-CNTT-2022', 'HP-TT',   8, 1, 1, 'Thực tập cuối khoá',              NULL),
('CTDT-CNTT-2022', 'HP-KLTN', 8, 1, 1, 'Khoá luận tốt nghiệp',            NULL),
-- CTDT-CNTT-2023 (K23 — đang năm 2)
('CTDT-CNTT-2023', 'HP-NNLT', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-OOP',  1, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-GTDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-CSDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-LTW',  3, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-TTDL', 3, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-PTTK', 4, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-KT',   5, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-TT',   7, 1, 1, NULL, NULL),
-- CTDT-CNTT-2024 (K24 — đang năm 1)
('CTDT-CNTT-2024', 'HP-NNLT', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-OOP',  1, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-GTDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-CSDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-LTW',  3, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-AI',   3, 1, 0, 'Học phần tự chọn', NULL);

-- =============================================================================
-- 14. LopHocPhan  ← CTDT_HocPhan, HocKyNamHoc, GiangVien
--     Business (docs/02 §3.5): Khi CTDT duyệt → auto tạo LopHocPhan theo
--     SoLopDuKien, MaGiangVien=NULL, TrangThai='DangMo'.
--     CHECK: 30 ≤ SiSoToiDa ≤ 60; SiSoThucTe ≤ SiSoToiDa.
-- =============================================================================
INSERT INTO LopHocPhan
    (MaCTDT,            MaHocPhan, MaHocKy,   MaLopHocPhan, MaGiangVien, SiSoToiDa, SiSoThucTe, FileDeCuongChiTiet, TrangThai) VALUES
-- --- K22 — HK1-2023 (quá khứ, DaDong) ---
('CTDT-CNTT-2022', 'HP-NNLT', 'HK1-2023', 1, 'GV007', 45, 45, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 1, 'GV002', 45, 43, 'lophocphan/CTDT-CNTT-2022_HP-OOP_HK1-2023_1.pdf', 'DaDong'),
('CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 2, 'GV004', 45, 42, NULL, 'DaDong'),
-- --- K22 — HK2-2023 (quá khứ, DaDong) ---
('CTDT-CNTT-2022', 'HP-CSDL', 'HK2-2023', 1, 'GV005', 45, 43, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-CSDL', 'HK2-2023', 2, 'GV001', 45, 41, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-MMT',  'HK2-2023', 1, 'GV002', 40, 38, NULL, 'DaDong'),
-- --- K22 — HK1-2024 (đang diễn ra, DangMo) ---
('CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'GV003', 45, 40, 'lophocphan/CTDT-CNTT-2022_HP-HTTT_HK1-2024_1.pdf', 'DangMo'),
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 1, 'GV004', 45, 38, 'lophocphan/CTDT-CNTT-2022_HP-LTW_HK1-2024_1.pdf',  'DangMo'),
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 2, 'GV002', 45, 35, NULL, 'DangMo'),
('CTDT-CNTT-2022', 'HP-TTDL', 'HK1-2024', 1, 'GV004', 35, 30, NULL, 'DangMo'),
('CTDT-CNTT-2022', 'HP-PTTK', 'HK1-2024', 1, 'GV011', 40, 32, NULL, 'DangMo'),
('CTDT-CNTT-2022', 'HP-KT',   'HK1-2024', 1, NULL,    35, 9,  NULL, 'DangMo'),
-- --- K23 — HK1-2024 (đang diễn ra, DangMo) ---
('CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, 'GV005', 45, 36, NULL, 'DangMo'),
('CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 2, NULL,    45, 0,  NULL, 'DangMo'),
('CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, 'GV008', 45, 34, NULL, 'DangMo'),
-- --- K24 — HK1-2024 (đang diễn ra) — sinh viên năm 1 ---
('CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, 'GV007', 45, 40, NULL, 'DangMo'),
('CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, 'GV004', 45, 38, NULL, 'DangMo'),
('CTDT-CNTT-2024', 'HP-OOP',  'HK1-2024', 1, 'GV002', 45, 42, NULL, 'DangMo'),
('CTDT-CNTT-2024', 'HP-OOP',  'HK1-2024', 2, 'GV010', 45, 36, NULL, 'DangMo');

-- =============================================================================
-- 15. DanhSachSinhVienLopHocPhan  ← SinhVien, LopHocPhan
--     Mỗi SV đăng ký 1 LopHocPhan (1 record / SV / HP).
--     DaCanhBao=1: khi GV đánh dấu cảnh báo (docs/02 §3.6). 
-- =============================================================================
INSERT INTO DanhSachSinhVienLopHocPhan
    (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, NhanXet, DaCanhBao, KetQuaXuLy) VALUES
-- ---- HK1-2023 (quá khứ) — K22 học OOP ----
('SV2022001', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 1, 'Tham gia đầy đủ, kiến thức vững vàng.',    0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 1, 'Học tập chăm chỉ, có ý thức cầu tiến.',     0, NULL),
('SV2022006', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 1, 'Chủ động tham gia bài tập nhóm.',           0, NULL),
('SV2022007', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 1, NULL,                                         0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 2, 'Nghỉ nhiều, không chuẩn bị bài.',           1,
 'Đã tư vấn ngày 15/10/2023. Sinh viên cam kết cải thiện, tham gia đầy đủ các buổi sau.'),
('SV2022004', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 2, 'Cố gắng nhưng cần thêm hỗ trợ.',            0, NULL),
('SV2022009', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 2, NULL,                                         0, NULL),
('SV2022010', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 2, NULL,                                         0, NULL),

-- ---- HK1-2024 (đang diễn ra) — K22 học LTW ----
('SV2022001', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL,                                         0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, 'Sinh viên học tập tích cực.',               0, NULL),
('SV2022006', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL,                                         0, NULL),
('SV2022007', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL,                                         0, NULL),
('SV2022008', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL,                                         0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, 'Bỏ 4 buổi, không nộp bài tập lớn giữa kỳ.', 1, NULL),
('SV2022004', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, NULL,                                         0, NULL),
('SV2022009', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, NULL,                                         0, NULL),
('SV2022010', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, 'Ít tương tác trên lớp.',                   0, NULL),

-- ---- HK1-2024 — K22 học HTTT ----
('SV2022001', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Tham gia đầy đủ.',                         0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                        0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                        0, NULL),
('SV2022004', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Cố gắng học tập.',                         0, NULL),
('SV2022006', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                        0, NULL),
('SV2022007', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                        0, NULL),
('SV2022008', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                        0, NULL),
('SV2022009', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Thường xuyên đi trễ.',                     1, NULL),
('SV2022010', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                        0, NULL),

-- ---- HK1-2024 — K23 đăng ký HP-CSDL (lớp 1) ----
('SV2023001', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023002', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023003', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023005', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023006', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023007', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023008', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023009', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023010', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, 'Thành tích học tập tốt.',                  0, NULL),

-- ---- HK1-2024 — K23 đăng ký HP-GTDL ----
('SV2023001', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023002', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023003', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023005', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023006', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023007', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),

-- ---- HK1-2024 — K24 đăng ký HP-NNLT ----
('SV2024001', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024002', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024003', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024004', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024005', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024006', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, NULL, 0, NULL),
('SV2024007', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, NULL, 0, NULL),
('SV2024008', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, NULL, 0, NULL),
('SV2024009', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, NULL, 0, NULL),
('SV2024010', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, 'SV mới, cần theo dõi thêm.',              0, NULL);

-- =============================================================================
-- 16. DotKienTap  ← LopHanhChinh, HocKyNamHoc, GiangVien, DoanhNghiep, NguoiDung
--     Business (docs/02 §3.7):
--       - MaDoanhNghiep phải 'DangHopTac' (không cho tạo với DN 'TamNgung')
--       - KinhPhiChung / KinhPhiTungSV: có thể NULL
--     Seed 5 đợt phản ánh các trạng thái khác nhau trên dashboard.
-- =============================================================================
INSERT INTO DotKienTap
    (MaDotKT, TenDotKT,                                            MaLopHC,      MaHocKy,    ThoiGian,    MaGVPhuTrach, MaDoanhNghiep,
     NhanXetGV,                                                    NhanXetDN,                                                          FileMinhChung,                      KinhPhiChung, KinhPhiTungSV, TrangThai,     NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Kiến Tập K22A tại FPT Software — HK1-2024',                   'CNTT-K22A', 'HK1-2024', '2024-11-15', 'GV003', 'DN001',
    'Sinh viên tham gia nghiêm túc, có ý thức tìm hiểu nghiệp vụ thực tế. Nhóm có 2 em nổi bật về kỹ năng coding.',
    'Nhân viên nhiệt tình, ham học hỏi. Cần nâng cao kỹ năng giao tiếp bằng tiếng Anh và làm việc với khách hàng.',
    'kientap/DotKT_1_minh_chung.pdf',                              10000000,      500000,        'DaThucHien', 'GV001', 'GV002', '2024-10-01 14:00:00'),

(2, 'Kiến Tập K22B tại VNG Corporation — HK1-2024',                'CNTT-K22B', 'HK1-2024', '2024-11-20', 'GV004', 'DN002',
    NULL, NULL,
    'kientap/DotKT_2_minh_chung.pdf',                               8000000,      400000,        'DaDuyet',    'GV001', 'GV002', '2024-10-05 09:30:00'),

(3, 'Kiến Tập K23A tại TMA Solutions — HK2-2024',                  'CNTT-K23A', 'HK2-2024', NULL,        'GV002', 'DN003',
    NULL, NULL, NULL,                                               NULL,         NULL,          'ChoDuyet',   'GV001', NULL,    NULL),

(4, 'Kiến Tập K23B tại Viettel Solutions — HK2-2024',              'CNTT-K23B', 'HK2-2024', NULL,        'GV006', 'DN004',
    NULL, NULL, NULL,                                               NULL,         NULL,          'ChuanBi',    'GV001', NULL,    NULL),

(5, 'Kiến Tập K22A tại MISA — HK2-2024 (bổ sung)',                 'CNTT-K22A', 'HK2-2024', '2025-04-10', 'GV003', 'DN005',
    NULL, NULL, NULL,                                               7500000,      375000,        'DaDuyet',    'GV001', 'GV002', '2025-02-20 11:00:00');

-- =============================================================================
-- 17. DanhSachSinhVienKienTap  ← DotKienTap, SinhVien  (HYBRID RULE)
--     Business (docs/02 §3.7 + docs/03 WF-07.1, WF-07.2):
--       1. Khi tạo đợt KT, service auto-add TẤT CẢ SV có TrangThaiSV='DangHoc'
--          của lớp thuộc đợt đó (DaThamGia=1).
--       2. Admin/BCN có thể TOGGLE DaThamGia (đánh dấu "không tham gia") —
--          KHÔNG xoá bản ghi, luôn giữ để audit.
--     Edge-case minh hoạ: SV2022005 (ThoiHoc) + SV2023004 (BaoLuu) KHÔNG
--     xuất hiện → chứng minh auto-add chỉ lấy 'DangHoc'.
-- =============================================================================
INSERT INTO DanhSachSinhVienKienTap (MaDotKT, MaSV, DaThamGia) VALUES
-- Đợt 1 (K22A @FPT) — 5 SV DangHoc, cả 5 tham gia
(1, 'SV2022001', 1),
(1, 'SV2022002', 1),
(1, 'SV2022006', 1),
(1, 'SV2022007', 1),
(1, 'SV2022008', 1),

-- Đợt 2 (K22B @VNG) — 4 SV DangHoc; SV2022004 toggle không tham gia (ví dụ nghỉ việc riêng); SV2022005 ThoiHoc KHÔNG xuất hiện
(2, 'SV2022003', 1),
(2, 'SV2022004', 0),
(2, 'SV2022009', 1),
(2, 'SV2022010', 1),

-- Đợt 3 (K23A @TMA) — 5 SV DangHoc; SV2023004 BaoLuu KHÔNG xuất hiện
(3, 'SV2023001', 1),
(3, 'SV2023002', 1),
(3, 'SV2023003', 1),
(3, 'SV2023005', 1),
(3, 'SV2023006', 1),

-- Đợt 4 (K23B @Viettel) — 4 SV DangHoc
(4, 'SV2023007', 1),
(4, 'SV2023008', 1),
(4, 'SV2023009', 1),
(4, 'SV2023010', 1),

-- Đợt 5 (K22A @MISA bổ sung) — 1 số SV đăng ký lại
(5, 'SV2022001', 1),
(5, 'SV2022006', 1);

-- =============================================================================
-- 18. DotThucTap  ← CTDT_HocPhan, HocKyNamHoc, NguoiDung
--     Business (docs/02 §3.8): MaHocPhan phải có LoaiHocPhan ∈ ('ThucTap','KienTap').
-- =============================================================================
INSERT INTO DotThucTap
    (MaDotTT, TenDotTT,                                          MaCTDT,            MaHocPhan, MaHocKy,
     NgayBatDau,  NgayKetThuc, FileMinhChung,                      TrangThai,        NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Thực Tập Cuối Khoá K22 — Đợt 1 — 2024-2025',                 'CTDT-CNTT-2022', 'HP-TT', 'HK1-2024',
    '2024-11-01', '2025-01-05', 'thuctap/DotTT_1_ke_hoach.pdf',      'DangThucHien',   'GV001', 'GV002', '2024-10-15 10:00:00'),

(2, 'Thực Tập Cuối Khoá K22 — Đợt 2 — 2025',                      'CTDT-CNTT-2022', 'HP-TT', 'HK2-2024',
    '2025-02-15', '2025-05-20', NULL,                                'DaDuyet',        'GV001', 'GV002', '2025-01-10 09:00:00'),

(3, 'Thực Tập Cuối Khoá K23 — Đợt 1 — 2025',                      'CTDT-CNTT-2023', 'HP-TT', 'HK2-2024',
    '2025-03-01', '2025-05-20', NULL,                                'ChuanBi',        'GV001', NULL,    NULL);

-- =============================================================================
-- 19. DanhSachThucTap  ← DotThucTap, SinhVien, DoanhNghiep
--     Business (docs/02 §3.8): UNIQUE (MaDotTT, MaSV); MaDoanhNghiep bắt buộc
--     nếu LoaiThucTap='DoanhNghiep'.
-- =============================================================================
INSERT INTO DanhSachThucTap
    (MaThucTap, MaDotTT, MaSV,        LoaiThucTap,   MaDoanhNghiep, TrangThai) VALUES
-- Đợt 1 (K22 — HK1-2024): 5 SV đang thực tập thực tế
(1,  1, 'SV2022001', 'DoanhNghiep', 'DN001', 'DangThucTap'),
(2,  1, 'SV2022002', 'DoanhNghiep', 'DN002', 'DangThucTap'),
(3,  1, 'SV2022003', 'DoanhNghiep', 'DN003', 'DangThucTap'),
(4,  1, 'SV2022004', 'Truong',      NULL,    'DangThucTap'),   -- thực tập tại trường
(5,  1, 'SV2022006', 'DoanhNghiep', 'DN005', 'DangThucTap'),
-- Đợt 2 (K22 — HK2-2024): 3 SV chuẩn bị
(6,  2, 'SV2022007', 'DoanhNghiep', 'DN001', 'DaPhanCong'),
(7,  2, 'SV2022008', 'DoanhNghiep', 'DN006', 'DaPhanCong'),
(8,  2, 'SV2022009', 'DoanhNghiep', 'DN007', 'DaPhanCong'),
(9,  2, 'SV2022010', 'DoanhNghiep', 'DN008', 'DaPhanCong'),
-- Đợt 3 (K23 — HK2-2024): chỉ mới đang ChuanBi
(10, 3, 'SV2023001', 'DoanhNghiep', 'DN004', 'DaPhanCong');

-- =============================================================================
-- 20. KetQuaThucTap  ← DanhSachThucTap, VaiTroThucTap, GiangVien
--     Business (docs/02 §3.9):
--       - Điểm: 0.00 - 10.00
--       - MaNguoiDanhGia là GV (chỉ GV nhập kết quả trong hệ thống)
--       - Mỗi cặp (MaThucTap, MaVaiTro): 1 bản ghi duy nhất
--     Seed minh hoạ đa chiều đánh giá: SV2022001 có đánh giá từ GV + DN + CVHT.
-- =============================================================================
INSERT INTO KetQuaThucTap (MaThucTap, MaVaiTro, MaNguoiDanhGia, Diem, NhanXet) VALUES
-- SV2022001 @FPT (MaThucTap=1): GV hướng dẫn + DN + CVHT — đầy đủ 3 chiều đánh giá
(1, 'GV',   'GV001', 8.50, 'Sinh viên hoàn thành tốt nhiệm vụ, tự giác và có trách nhiệm với công việc được giao.'),
(1, 'DN',   'GV001', 9.00, 'Nhân viên thực tập xuất sắc, khả năng làm việc nhóm và độc lập đều cao. Đề xuất tuyển dụng sau tốt nghiệp.'),
(1, 'CVHT', 'GV003', 8.75, 'Sinh viên có ý thức học tập tốt, báo cáo tiến độ đều đặn với cố vấn.'),

-- SV2022002 @VNG (MaThucTap=2): mới có GV + CVHT
(2, 'GV',   'GV001', 7.50, 'Sinh viên cần chủ động hơn trong việc tìm hiểu công việc. Chất lượng báo cáo tạm ổn.'),
(2, 'CVHT', 'GV003', 7.75, 'Tham gia các buổi sinh hoạt lớp đầy đủ.'),

-- SV2022003 @TMA (MaThucTap=3): GV + DN
(3, 'GV',   'GV001', 8.00, 'Hoàn thành tốt các nhiệm vụ, tinh thần học hỏi cao.'),
(3, 'DN',   'GV001', 7.80, 'Tiếp thu nhanh, cần cải thiện kỹ năng trình bày.'),

-- SV2022004 tại trường (MaThucTap=4): chỉ GV (không có DN vì thực tập tại trường)
(4, 'GV',   'GV001', 7.00, 'Tham gia nghiên cứu đề tài với cố vấn. Cần tăng tốc độ triển khai.'),
(4, 'CVHT', 'GV004', 7.50, 'Sinh viên gặp khó khăn cá nhân, đã được hỗ trợ lịch làm việc linh hoạt.'),

-- SV2022006 @MISA (MaThucTap=5): GV + DN
(5, 'GV',   'GV001', 9.20, 'Một trong những sinh viên xuất sắc của đợt.'),
(5, 'DN',   'GV001', 9.00, 'Thành thạo các công cụ kế toán doanh nghiệp, tư duy tốt.');

-- =============================================================================
-- KIỂM TRA NHANH SAU KHI CHẠY
-- =============================================================================
-- Chạy từng dòng dưới đây để xác nhận đúng số lượng bản ghi:
-- SELECT COUNT(*) AS HocKyNamHoc      FROM HocKyNamHoc;                             -- 5
-- SELECT COUNT(*) AS NguoiDung        FROM NguoiDung;                               -- 52
-- SELECT COUNT(*) AS GiangVien        FROM GiangVien;                               -- 12
-- SELECT COUNT(*) AS DoanhNghiep      FROM DoanhNghiep;                             -- 9
-- SELECT COUNT(*) AS DN_DangHopTac    FROM DoanhNghiep WHERE TrangThai='DangHopTac';-- 8
-- SELECT COUNT(*) AS VaiTroThucTap    FROM VaiTroThucTap;                           -- 3
-- SELECT COUNT(*) AS NhomNguoiDung    FROM NhomNguoiDung;                           -- 18
-- SELECT COUNT(*) AS CTDT             FROM ChuongTrinhDaoTao;                       -- 4
-- SELECT COUNT(*) AS BCN              FROM BCN_ThanhVien;                           -- 15
-- SELECT COUNT(*) AS HocPhan          FROM HocPhan;                                 -- 14
-- SELECT COUNT(*) AS DoiNguGV         FROM DoiNguGiangVienHP;                       -- 42
-- SELECT COUNT(*) AS LopHC            FROM LopHanhChinh;                            -- 6
-- SELECT COUNT(*) AS SinhVien         FROM SinhVien;                                -- 30
-- SELECT COUNT(*) AS SV_DangHoc       FROM SinhVien WHERE TrangThaiSV='DangHoc';    -- 28
-- SELECT COUNT(*) AS CTDT_HP          FROM CTDT_HocPhan;                            -- 29
-- SELECT COUNT(*) AS LopHocPhan       FROM LopHocPhan;                              -- 19
-- SELECT COUNT(*) AS DSSV_LHP         FROM DanhSachSinhVienLopHocPhan;              -- 52
-- SELECT COUNT(*) AS CanhBaoChoXuLy   FROM DanhSachSinhVienLopHocPhan
--                                      WHERE DaCanhBao=1 AND KetQuaXuLy IS NULL;    -- 2
-- SELECT COUNT(*) AS DotKienTap       FROM DotKienTap;                              -- 5
-- SELECT COUNT(*) AS DSSV_KT          FROM DanhSachSinhVienKienTap;                 -- 20
-- SELECT COUNT(*) AS DSSV_KT_ThamGia  FROM DanhSachSinhVienKienTap WHERE DaThamGia=1;-- 19
-- SELECT COUNT(*) AS DotThucTap       FROM DotThucTap;                              -- 3
-- SELECT COUNT(*) AS DanhSachTT       FROM DanhSachThucTap;                         -- 10
-- SELECT COUNT(*) AS KetQuaTT         FROM KetQuaThucTap;                           -- 12

-- =============================================================================
-- TÀI KHOẢN TEST  (Mật khẩu: Password@123)
-- =============================================================================
-- TenDangNhap          LoaiNguoiDung   Vai Trò                       Ghi chú
-- admin                Admin           ADMIN                          Toàn quyền hệ thống
-- tran.van.an          GiangVien       PDT + TTDTXS                   Trưởng Phòng Đào Tạo
-- le.thi.binh          GiangVien       TTDTXS                         Thành viên TT Đào Tạo Xuất Sắc
-- nguyen.van.cuong     GiangVien       CVHT + CNHP                    CVHT CNTT-K22A, CNHP HP-HTTT / HP-KT
-- pham.thi.dung        GiangVien       CVHT + CNHP                    CVHT CNTT-K22B, CNHP HP-LTW / HP-PTTK
-- hoang.van.em         GiangVien       CVHT + CNHP                    CVHT CNTT-K23A, CNHP HP-CSDL / HP-TTDL
-- vu.thi.giang         GiangVien       CVHT + CNHP                    CVHT CNTT-K23B, CNHP HP-AI
-- do.minh.hieu         GiangVien       CVHT + CNHP                    CVHT CNTT-K24A, CNHP HP-NNLT
-- bui.thanh.ha         GiangVien       CVHT + CNHP                    CVHT CNTT-K24B, CNHP HP-GTDL
-- sv.2022001           SinhVien        -                              K22A, DangThucTap (Đợt 1 @FPT) — có đủ 3 chiều đánh giá
-- sv.2022003           SinhVien        -                              K22B, DaCanhBao HP-OOP (đã xử lý)
-- sv.2022004           SinhVien        -                              K22B, Đợt KT 2 — DaThamGia=0 (minh hoạ toggle)
-- sv.2022005           SinhVien        -                              K22B, ThoiHoc  → KHÔNG auto-add Đợt KT
-- sv.2023004           SinhVien        -                              K23A, BaoLuu   → KHÔNG auto-add Đợt KT
-- sv.2024001..010      SinhVien        -                              K24 — sinh viên năm 1
-- dn.fpt / dn.vng / ...DoanhNghiep     -                              7 doanh nghiệp đang hợp tác

-- =============================================================================
-- HẾT SEED
-- =============================================================================
