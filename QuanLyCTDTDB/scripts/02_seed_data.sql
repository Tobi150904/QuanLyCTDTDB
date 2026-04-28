-- =============================================================================
-- 02_seed_data.sql
-- =============================================================================
-- He Thong Quan Ly Chuong Trinh Dao Tao Xuat Sac (NTU)
-- Seed du lieu day du Phase 1 -> Phase 7 (Profile / Danh-gia / Error / Thuc-tap
-- 2 cot diem). File nay phai duoc chay SAU KHI 01_create_tables.sql da chay xong.
--
-- Pham vi du lieu:
--   - 5 hoc ky 2023-2026
--   - 1 admin + 12 giang vien + 30 sinh vien (4 khoa K22..K25 — co edge case
--     ThoiHoc / BaoLuu de minh hoa rule auto-add)
--   - 7 doanh nghiep dang hop tac + 2 doanh nghiep tam ngung
--   - 4 chuong trinh dao tao + 14 hoc phan + 19 lop hoc phan + 52 dang ky
--   - 5 dot kien tap (du moi trang thai workflow) + 20 SV-KT
--   - 3 dot thuc tap (DangThucHien / DaDuyet / ChuanBi) + 10 SV-TT
--   - 12 ket qua thuc tap minh hoa Phase 7 "2 cot diem":
--       * Truong       : GV_HD (cot 1) + GV_PB (cot 2)
--       * DoanhNghiep  : DN    (cot 1) + GV_HD (cot 2)
--     Vai tro 'GV' (legacy don nhat) van duoc giu lai trong VaiTroThucTap
--     de backward-compat voi du lieu cu, nhung KHONG con sinh ra row moi.
--
-- Convention:
--   - MaSV = MaNguoiDung, MaGV = MaNguoiDung, MaDoanhNghiep = MaNguoiDung
--     (rang buoc tham chieu cheo dat trong service layer va docs/02 §3.2).
--   - Mat khau test: Password@123  (BCrypt cost=10).
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------------
-- Truncate theo thu tu nguoc voi FK de tranh constraint violation.
-- ---------------------------------------------------------------------------
TRUNCATE TABLE KetQuaThucTap;
TRUNCATE TABLE DanhSachThucTap;
TRUNCATE TABLE DotThucTap;
TRUNCATE TABLE DanhSachSinhVienKienTap;
TRUNCATE TABLE DotKienTap;
TRUNCATE TABLE DanhSachSinhVienLopHocPhan;
TRUNCATE TABLE LopHocPhan;
TRUNCATE TABLE CTDT_HocPhan;
TRUNCATE TABLE SinhVien;
TRUNCATE TABLE LopHanhChinh;
TRUNCATE TABLE DoiNguGiangVienHP;
TRUNCATE TABLE HocPhan;
TRUNCATE TABLE BCN_ThanhVien;
TRUNCATE TABLE ChuongTrinhDaoTao;
TRUNCATE TABLE NhomNguoiDung;
TRUNCATE TABLE VaiTroThucTap;
TRUNCATE TABLE DoanhNghiep;
TRUNCATE TABLE GiangVien;
TRUNCATE TABLE NguoiDung;
TRUNCATE TABLE HocKyNamHoc;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 1. HocKyNamHoc  (doc lap)
--    Quy uoc: MaHocKy = HK{1|2}-{NamBatDau}    (vi du HK1-2024)
--    Chi 1 hoc ky 'DangDienRa' tai mot thoi diem (docs/02 §2).
-- =============================================================================
INSERT INTO HocKyNamHoc (MaHocKy, TenHocKy, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('HK1-2023', 'Hoc Ky 1 Nam 2023-2024', '2023-09-04', '2024-01-12', 'DaKetThuc'),
('HK2-2023', 'Hoc Ky 2 Nam 2023-2024', '2024-01-22', '2024-05-31', 'DaKetThuc'),
('HK1-2024', 'Hoc Ky 1 Nam 2024-2025', '2024-09-02', '2025-01-10', 'DangDienRa'),
('HK2-2024', 'Hoc Ky 2 Nam 2024-2025', '2025-01-20', '2025-05-30', 'SapDienRa'),
('HK1-2025', 'Hoc Ky 1 Nam 2025-2026', '2025-09-01', '2026-01-09', 'SapDienRa');

-- =============================================================================
-- 2. NguoiDung  (doc lap) — BCrypt hash cho Password@123
--    Format MaNguoiDung (docs/02 §1):
--      Admin  : AD + 3 so          -> AD001
--      GV     : GV + 3 so          -> GV001
--      SV     : SV + nam + 3 so    -> SV2024001
--      DN     : DN + 3 so          -> DN001
-- =============================================================================
INSERT INTO NguoiDung
    (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, SoDienThoai, TrangThaiTK, LoaiNguoiDung) VALUES

-- ---- Admin ----
('AD001', 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'admin@ntu.edu.vn', 'Quan Tri Vien He Thong', '0909000001', 1, 'Admin'),

-- ---- Giang Vien: Phong Dao Tao / TTDTXS ----
('GV001', 'tran.van.an',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tran.van.an@ntu.edu.vn',     'PGS.TS. Tran Van An',      '0912340001', 1, 'GiangVien'),
('GV002', 'le.thi.binh',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'le.thi.binh@ntu.edu.vn',     'TS. Le Thi Binh',          '0912340002', 1, 'GiangVien'),

-- ---- Giang Vien: CVHT + CNHP (CTDT K22) ----
('GV003', 'nguyen.van.cuong','$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'nguyen.van.cuong@ntu.edu.vn','TS. Nguyen Van Cuong',     '0912340003', 1, 'GiangVien'),
('GV004', 'pham.thi.dung',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'pham.thi.dung@ntu.edu.vn',   'ThS. Pham Thi Dung',       '0912340004', 1, 'GiangVien'),

-- ---- Giang Vien: CVHT + CNHP (CTDT K23) ----
('GV005', 'hoang.van.em',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'hoang.van.em@ntu.edu.vn',    'ThS. Hoang Van Em',        '0912340005', 1, 'GiangVien'),
('GV006', 'vu.thi.giang',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'vu.thi.giang@ntu.edu.vn',    'TS. Vu Thi Giang',         '0912340006', 1, 'GiangVien'),

-- ---- Giang Vien: CVHT + CNHP (CTDT K24) ----
('GV007', 'do.minh.hieu',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'do.minh.hieu@ntu.edu.vn',    'ThS. Do Minh Hieu',        '0912340007', 1, 'GiangVien'),
('GV008', 'bui.thanh.ha',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'bui.thanh.ha@ntu.edu.vn',    'ThS. Bui Thanh Ha',        '0912340008', 1, 'GiangVien'),

-- ---- Giang Vien: bo mon chuyen mon (giang day theo hoc phan + Phan bien thuc tap) ----
('GV009', 'ngo.thi.lan',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'ngo.thi.lan@ntu.edu.vn',     'TS. Ngo Thi Lan',          '0912340009', 1, 'GiangVien'),
('GV010', 'dang.van.minh',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'dang.van.minh@ntu.edu.vn',   'ThS. Dang Van Minh',       '0912340010', 1, 'GiangVien'),
('GV011', 'phan.thi.ngoc',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'phan.thi.ngoc@ntu.edu.vn',   'TS. Phan Thi Ngoc',        '0912340011', 1, 'GiangVien'),
('GV012', 'ly.quoc.phong',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'ly.quoc.phong@ntu.edu.vn',   'ThS. Ly Quoc Phong',       '0912340012', 1, 'GiangVien'),

-- ---- Sinh Vien: K24 (nam 1 — CNTT-K24A + CNTT-K24B) ----
('SV2024001', 'sv.2024001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024001@sv.ntu.edu.vn', 'Nguyen Thi Hoa',       '0978001001', 1, 'SinhVien'),
('SV2024002', 'sv.2024002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024002@sv.ntu.edu.vn', 'Tran Minh Khoa',       '0978001002', 1, 'SinhVien'),
('SV2024003', 'sv.2024003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024003@sv.ntu.edu.vn', 'Le Quoc Long',         '0978001003', 1, 'SinhVien'),
('SV2024004', 'sv.2024004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024004@sv.ntu.edu.vn', 'Pham Ha My',           '0978001004', 1, 'SinhVien'),
('SV2024005', 'sv.2024005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024005@sv.ntu.edu.vn', 'Hoang Tuan Nam',       '0978001005', 1, 'SinhVien'),
('SV2024006', 'sv.2024006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024006@sv.ntu.edu.vn', 'Vu Thi Ngan',          '0978001006', 1, 'SinhVien'),
('SV2024007', 'sv.2024007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024007@sv.ntu.edu.vn', 'Do Anh Quan',          '0978001007', 1, 'SinhVien'),
('SV2024008', 'sv.2024008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024008@sv.ntu.edu.vn', 'Bui Thu Thao',         '0978001008', 1, 'SinhVien'),
('SV2024009', 'sv.2024009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024009@sv.ntu.edu.vn', 'Ngo Van Thien',        '0978001009', 1, 'SinhVien'),
('SV2024010', 'sv.2024010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024010@sv.ntu.edu.vn', 'Ly Thanh Vy',          '0978001010', 1, 'SinhVien'),

-- ---- Sinh Vien: K23 (nam 2 — CNTT-K23A + CNTT-K23B) ----
('SV2023001', 'sv.2023001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023001@sv.ntu.edu.vn', 'Pham Ngoc Mai',        '0978002001', 1, 'SinhVien'),
('SV2023002', 'sv.2023002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023002@sv.ntu.edu.vn', 'Hoang Thi Nhu',        '0978002002', 1, 'SinhVien'),
('SV2023003', 'sv.2023003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023003@sv.ntu.edu.vn', 'Do Quoc Tuan',         '0978002003', 1, 'SinhVien'),
('SV2023004', 'sv.2023004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023004@sv.ntu.edu.vn', 'Truong Quoc Bao',      '0978002004', 1, 'SinhVien'),
('SV2023005', 'sv.2023005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023005@sv.ntu.edu.vn', 'Nguyen Thao Vy',       '0978002005', 1, 'SinhVien'),
('SV2023006', 'sv.2023006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023006@sv.ntu.edu.vn', 'Le Xuan Khang',        '0978002006', 1, 'SinhVien'),
('SV2023007', 'sv.2023007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023007@sv.ntu.edu.vn', 'Vo Thuy Duong',        '0978002007', 1, 'SinhVien'),
('SV2023008', 'sv.2023008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023008@sv.ntu.edu.vn', 'Tran Huy Hoang',       '0978002008', 1, 'SinhVien'),
('SV2023009', 'sv.2023009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023009@sv.ntu.edu.vn', 'Phan Kim Anh',         '0978002009', 1, 'SinhVien'),
('SV2023010', 'sv.2023010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023010@sv.ntu.edu.vn', 'Bui Cong Dat',         '0978002010', 1, 'SinhVien'),

-- ---- Sinh Vien: K22 (nam 3 — di kien tap + chuan bi thuc tap) ----
('SV2022001', 'sv.2022001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022001@sv.ntu.edu.vn', 'Vo Thanh Phong',       '0978003001', 1, 'SinhVien'),
('SV2022002', 'sv.2022002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022002@sv.ntu.edu.vn', 'Bui Thi Quynh',        '0978003002', 1, 'SinhVien'),
('SV2022003', 'sv.2022003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022003@sv.ntu.edu.vn', 'Ngo Van Tan',          '0978003003', 1, 'SinhVien'),
('SV2022004', 'sv.2022004', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022004@sv.ntu.edu.vn', 'Dao Thi Uyen',         '0978003004', 1, 'SinhVien'),
('SV2022006', 'sv.2022006', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022006@sv.ntu.edu.vn', 'Le Hong Son',          '0978003006', 1, 'SinhVien'),
('SV2022007', 'sv.2022007', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022007@sv.ntu.edu.vn', 'Tran My Linh',         '0978003007', 1, 'SinhVien'),
('SV2022008', 'sv.2022008', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022008@sv.ntu.edu.vn', 'Nguyen Quang Huy',     '0978003008', 1, 'SinhVien'),
('SV2022009', 'sv.2022009', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022009@sv.ntu.edu.vn', 'Pham Hoang Kim',       '0978003009', 1, 'SinhVien'),
('SV2022010', 'sv.2022010', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022010@sv.ntu.edu.vn', 'Hoang Gia Bao',        '0978003010', 1, 'SinhVien'),

-- ---- Sinh Vien edge-case: KHAC trang thai DangHoc (de minh hoa auto-add chi lay DangHoc) ----
('SV2022005', 'sv.2022005', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022005@sv.ntu.edu.vn', 'Ly Thi Van',           '0978003005', 1, 'SinhVien'),

-- ---- Doanh Nghiep (tai khoan login cho nguoi dai dien DN) ----
('DN001', 'dn.fpt',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tuyendung@fpt.com.vn',      'FPT Software',           '0243768888', 1, 'DoanhNghiep'),
('DN002', 'dn.vng',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'thuctap@vng.com.vn',        'VNG Corporation',        '0283962828', 1, 'DoanhNghiep'),
('DN003', 'dn.tma',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'hr@tma.com.vn',             'TMA Solutions',          '0283997300', 1, 'DoanhNghiep'),
('DN004', 'dn.viettel', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tuyendung@viettel.com.vn',  'Viettel Solutions',      '0243628400', 1, 'DoanhNghiep'),
('DN005', 'dn.misa',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tuyendung@misa.com.vn',     'Cong ty Co phan MISA',   '0243762868', 1, 'DoanhNghiep'),
('DN006', 'dn.kms',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'careers@kms-technology.com','KMS Technology',         '0283811999', 1, 'DoanhNghiep'),
('DN007', 'dn.vnpay',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tuyendung@vnpay.vn',        'VNPay',                  '0247108998', 1, 'DoanhNghiep');

-- =============================================================================
-- 3. GiangVien  <- NguoiDung
--    Rang buoc (docs/02 §3.2): MaGV = MaNguoiDung (khoa chinh trung).
-- =============================================================================
INSERT INTO GiangVien (MaGV, MaNguoiDung, HocHam, HocVi, ChuyenNganh, LoaiGiangVien) VALUES
('GV001', 'GV001', 'Pho Giao Su', 'Tien Si', 'Cong Nghe Phan Mem',  'GiangVienTruong'),
('GV002', 'GV002', NULL,          'Tien Si', 'Mang May Tinh',       'GiangVienTruong'),
('GV003', 'GV003', NULL,          'Tien Si', 'He Thong Thong Tin',  'GiangVienTruong'),
('GV004', 'GV004', NULL,          'Thac Si', 'Cong Nghe Phan Mem',  'GiangVienTruong'),
('GV005', 'GV005', NULL,          'Thac Si', 'Co So Du Lieu',       'GiangVienTruong'),
('GV006', 'GV006', NULL,          'Tien Si', 'Khoa Hoc Du Lieu',    'GiangVienTruong'),
('GV007', 'GV007', NULL,          'Thac Si', 'Lap Trinh Web',       'GiangVienTruong'),
('GV008', 'GV008', NULL,          'Thac Si', 'Tri Tue Nhan Tao',    'GiangVienTruong'),
('GV009', 'GV009', NULL,          'Tien Si', 'An Toan Thong Tin',   'GiangVienTruong'),
('GV010', 'GV010', NULL,          'Thac Si', 'Ky Thuat Phan Mem',   'GiangVienThinhGiang'),
('GV011', 'GV011', NULL,          'Tien Si', 'Phan Tich Thiet Ke',  'GiangVienTruong'),
('GV012', 'GV012', NULL,          'Thac Si', 'Lap Trinh Di Dong',   'GiangVienThinhGiang');

-- =============================================================================
-- 4. DoanhNghiep  (doc lap)
--    TrangThai: DangHopTac | TamNgung.
--    Business (docs/02 §3.7): DotKienTap chi tham chieu DN 'DangHopTac'.
-- =============================================================================
INSERT INTO DoanhNghiep
    (MaDoanhNghiep, TenDoanhNghiep,                            LinhVuc,                          NguoiDaiDien,       Email,                       SoDienThoai,  DiaChiDN,                                                     TrangThai) VALUES
('DN001', 'Cong ty TNHH Phan mem FPT (FPT Software)',          'Phan mem & Dich vu CNTT',         'Nguyen Van Hung',  'tuyendung@fpt.com.vn',      '0243768888', 'Toa nha FPT, So 17 Duy Tan, Cau Giay, Ha Noi',                'DangHopTac'),
('DN002', 'Cong ty Co phan VNG',                               'Game & Giai tri so',              'Le Thi Thu',       'thuctap@vng.com.vn',        '0283962828', 'So 182 Le Dai Hanh, Phuong 15, Quan 11, TP. HCM',             'DangHopTac'),
('DN003', 'Cong ty TNHH TMA Solutions',                        'Phan mem & Outsourcing',          'Tran Minh Dang',   'hr@tma.com.vn',             '0283997300', 'Cong vien Phan mem Quang Trung, Quan 12, TP. HCM',            'DangHopTac'),
('DN004', 'Tong Cong ty Giai phap Doanh nghiep Viettel',       'Vien thong & Ha tang CNTT',       'Pham Thi Lan',     'tuyendung@viettel.com.vn',  '0243628400', 'So 1 Tran Huu Duc, My Dinh 2, Nam Tu Liem, Ha Noi',           'DangHopTac'),
('DN005', 'Cong ty Co phan MISA',                              'Phan mem quan ly doanh nghiep',   'Dinh Thi Thuy Ha', 'tuyendung@misa.com.vn',     '0243762868', 'Toa nha MISA, 218 Doi Can, Ba Dinh, Ha Noi',                  'DangHopTac'),
('DN006', 'Cong ty TNHH KMS Technology Vietnam',               'Phan mem gia cong cho US',        'Doan Quoc Viet',   'careers@kms-technology.com','0283811999', 'Toa QTSC 9, Cong vien Phan mem Quang Trung, TP. HCM',         'DangHopTac'),
('DN007', 'Cong ty Co phan Giai phap Thanh toan VNPay',        'Thanh toan dien tu & Fintech',    'Hoang Minh Tuan',  'tuyendung@vnpay.vn',        '0247108998', 'Tang 8, Toa nha Mipec, 229 Tay Son, Dong Da, Ha Noi',         'DangHopTac'),
('DN008', 'Cong ty Co phan Tiki',                              'Thuong mai dien tu',              'Phan Thi Thu Ha',  'hr@tiki.vn',                '0283456789', 'So 52 Ut Tich, Phuong 4, Quan Tan Binh, TP. HCM',             'DangHopTac'),
('DN009', 'Cong ty TNHH NashTech Viet Nam',                    'Phan mem & Dich vu CNTT',         'Vo Anh Tuan',      'hr@nashtechglobal.com',     '0283815555', 'So 117 Nguyen Cuu Van, Binh Thanh, TP. HCM',                  'TamNgung');

-- =============================================================================
-- 5. VaiTroThucTap  (danh muc vai tro danh gia trong KetQuaThucTap)
--    docs/02 §3.9, docs/03 §6 + Phase 7 §4 (he thong 2 cot diem).
--
--    Phase 7 — He thong 2 cot diem:
--      + Truong       : GV_HD (cot 1) + GV_PB (cot 2)
--      + DoanhNghiep  : DN    (cot 1) + GV_HD (cot 2 — giam sat tu truong)
--    'GV' (legacy) van duoc giu lai trong bang de khong vo ket qua cu da seed
--    ngoai he thong (idempotent migration). UI moi se chi sinh GV_HD/GV_PB.
--
--    LUU Y: KetQuaThucTap.fk_kqtt_nguoidanhgia REFERENCES GiangVien(MaGV),
--    nen MaNguoiDanhGia luon la GV. SV nhan xet dinh tinh dung TEXT tren
--    DanhSachThucTap (chua co cot rieng — luu trong NhanXetGV neu co), KHONG
--    luu thanh KetQuaThucTap.
-- =============================================================================
INSERT INTO VaiTroThucTap (MaVaiTro, TenVaiTro, MoTa) VALUES
('GV',    'Giang Vien (legacy)',          'Vai tro don nhat truoc Phase 7 — chi giu de back-compat. Khong dung cho ban ghi moi.'),
('DN',    'Doanh Nghiep',                 'Can bo doanh nghiep phu trach SV thuc tap tai don vi. La cot diem 1 cho LoaiThucTap=DoanhNghiep.'),
('CVHT',  'Co Van Hoc Tap',               'Co van hoc tap cua lop hanh chinh — danh gia tong the (khong thuoc 2 cot diem).'),
('GV_HD', 'GV Huong Dan / Giam Sat',      'GV huong dan (Truong) hoac giam sat (DoanhNghiep). Cot diem 1 cho Truong, cot diem 2 cho DoanhNghiep.'),
('GV_PB', 'GV Phan Bien',                 'GV phan bien — chi ap dung cho LoaiThucTap=Truong. La cot diem 2 doi voi loai do.');

-- =============================================================================
-- 6. NhomNguoiDung  <- NguoiDung (docs/02 §2)
--    Vai tro nghiep vu: PDT | TTDTXS | CVHT | CNHP
--    - GV001: Truong Phong Dao Tao + kiem vien TTDTXS
--    - GV002: TTDTXS
--    - GV003..GV008: CVHT tung lop (+ mot so kiem CNHP)
-- =============================================================================
INSERT INTO NhomNguoiDung (MaNguoiDung, VaiTro) VALUES
-- Phong Dao Tao & TTDTXS
('GV001', 'PDT'),
('GV001', 'TTDTXS'),
('GV002', 'TTDTXS'),
-- Co Van Hoc Tap (moi GV phu trach 1 lop)
('GV003', 'CVHT'),   -- CNTT-K22A
('GV004', 'CVHT'),   -- CNTT-K22B
('GV005', 'CVHT'),   -- CNTT-K23A
('GV006', 'CVHT'),   -- CNTT-K23B
('GV007', 'CVHT'),   -- CNTT-K24A
('GV008', 'CVHT'),   -- CNTT-K24B
-- Chu Nhiem Hoc Phan
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
-- 7. ChuongTrinhDaoTao  <- NguoiDung (NguoiTao + NguoiDuyet)
--    Business (docs/02 §3.4): NguoiDuyet KHAC NguoiTao; khi chuyen DaDuyet
--    -> service auto tao LopHocPhan theo SoLopDuKien.
-- =============================================================================
INSERT INTO ChuongTrinhDaoTao
    (MaCTDT,             TenCTDT,                                                     Khoa,   FileWord,                                               TrangThai, NguoiTao, NguoiDuyet, NgayDuyet) VALUES
('CTDT-CNTT-2022', 'CTDT Xuat Sac Nganh Cong Nghe Thong Tin — Khoa 2022-2026', '2022', 'ctdt/CTDT-CNTT-2022_20220801_khung_ctdt.docx', 'DaDuyet',  'GV001', 'GV002', '2022-08-15 09:00:00'),
('CTDT-CNTT-2023', 'CTDT Xuat Sac Nganh Cong Nghe Thong Tin — Khoa 2023-2027', '2023', 'ctdt/CTDT-CNTT-2023_20230801_khung_ctdt.docx', 'DaDuyet',  'GV001', 'GV002', '2023-08-20 10:30:00'),
('CTDT-CNTT-2024', 'CTDT Xuat Sac Nganh Cong Nghe Thong Tin — Khoa 2024-2028', '2024', 'ctdt/CTDT-CNTT-2024_20240715_khung_ctdt.docx', 'DaDuyet',  'GV001', 'GV002', '2024-07-20 14:15:00'),
('CTDT-CNTT-2025', 'CTDT Xuat Sac Nganh Cong Nghe Thong Tin — Khoa 2025-2029', '2025', NULL,                                             'ChoDuyet','GV001', NULL,    NULL);

-- =============================================================================
-- 8. BCN_ThanhVien  <- ChuongTrinhDaoTao, GiangVien
--    Chuc danh: ChuNhiem | ThuKy | UyVien. Rang buoc BizCt (docs/02 §3.4):
--    moi CTDT co DUY NHAT 1 ChuNhiem.
-- =============================================================================
INSERT INTO BCN_ThanhVien (MaCTDT, MaGV, ChucDanh, NgayBoNhiem, GhiChu) VALUES
('CTDT-CNTT-2022', 'GV001', 'ChuNhiem', '2022-08-01', 'Truong ban, phu trach chuyen mon chung'),
('CTDT-CNTT-2022', 'GV002', 'ThuKy',    '2022-08-01', NULL),
('CTDT-CNTT-2022', 'GV003', 'UyVien',   '2022-08-01', NULL),
('CTDT-CNTT-2022', 'GV011', 'UyVien',   '2023-02-01', 'Bo sung nhan su PTTK'),
('CTDT-CNTT-2023', 'GV001', 'ChuNhiem', '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV004', 'ThuKy',    '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV005', 'UyVien',   '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV009', 'UyVien',   '2023-08-01', 'Phu trach mang ATTT'),
('CTDT-CNTT-2024', 'GV002', 'ChuNhiem', '2024-07-15', 'Thay GV001 de tap trung CTDT K25'),
('CTDT-CNTT-2024', 'GV006', 'ThuKy',    '2024-07-15', NULL),
('CTDT-CNTT-2024', 'GV007', 'UyVien',   '2024-07-15', NULL),
('CTDT-CNTT-2024', 'GV008', 'UyVien',   '2024-07-15', NULL),
('CTDT-CNTT-2025', 'GV001', 'ChuNhiem', '2025-06-01', 'CTDT du kien, dang hoan thien'),
('CTDT-CNTT-2025', 'GV006', 'ThuKy',    '2025-06-01', NULL),
('CTDT-CNTT-2025', 'GV008', 'UyVien',   '2025-06-01', NULL);

-- =============================================================================
-- 9. HocPhan  <- GiangVien (ChuNhiemHP)
--    LoaiHocPhan: LyThuyet | ThucHanh | DoAn | ThucTap | KienTap
--    TrangThai:   BanNhap | ChoDuyet | DaDuyet
-- =============================================================================
INSERT INTO HocPhan
    (MaHocPhan, TenHocPhan,                              SoTinChi, LoaiHocPhan, ChuNhiemHP, FileDeCuong,                               TrangThai) VALUES
('HP-NNLT', 'Nhap Mon Lap Trinh',                         3,  'LyThuyet', 'GV007', 'hocphan/HP-NNLT_20220901_decuong.pdf',  'DaDuyet'),
('HP-GTDL', 'Cau Truc Du Lieu & Giai Thuat',              4,  'LyThuyet', 'GV008', 'hocphan/HP-GTDL_20220901_decuong.pdf',  'DaDuyet'),
('HP-OOP',  'Lap Trinh Huong Doi Tuong',                  3,  'LyThuyet', 'GV002', 'hocphan/HP-OOP_20220801_decuong.pdf',   'DaDuyet'),
('HP-CSDL', 'Co So Du Lieu',                              3,  'LyThuyet', 'GV005', 'hocphan/HP-CSDL_20220801_decuong.pdf',  'DaDuyet'),
('HP-MMT',  'Mang May Tinh',                              3,  'LyThuyet', 'GV002', 'hocphan/HP-MMT_20220801_decuong.pdf',   'DaDuyet'),
('HP-HTTT', 'Phan Tich & Thiet Ke He Thong Thong Tin',    3,  'LyThuyet', 'GV003', 'hocphan/HP-HTTT_20230201_decuong.pdf',  'DaDuyet'),
('HP-LTW',  'Lap Trinh Web',                              3,  'LyThuyet', 'GV004', 'hocphan/HP-LTW_20230801_decuong.pdf',   'DaDuyet'),
('HP-TTDL', 'Thuc Hanh Thiet Ke Du Lieu',                 2,  'ThucHanh', 'GV005', 'hocphan/HP-TTDL_20230801_decuong.pdf',  'DaDuyet'),
('HP-PTTK', 'Phan Tich Thiet Ke Phan Mem',                3,  'LyThuyet', 'GV011', 'hocphan/HP-PTTK_20230901_decuong.pdf',  'DaDuyet'),
('HP-ATTT', 'An Toan & Bao Mat Thong Tin',                3,  'LyThuyet', 'GV009', 'hocphan/HP-ATTT_20240101_decuong.pdf',  'DaDuyet'),
('HP-AI',   'Nhap Mon Tri Tue Nhan Tao',                  3,  'LyThuyet', 'GV006', 'hocphan/HP-AI_20240701_decuong.pdf',    'DaDuyet'),
('HP-KT',   'Kien Tap Doanh Nghiep',                      2,  'KienTap',  'GV003', 'hocphan/HP-KT_20230101_decuong.pdf',    'DaDuyet'),
('HP-TT',   'Thuc Tap Cuoi Khoa',                         6,  'ThucTap',  'GV001', 'hocphan/HP-TT_20230101_decuong.pdf',    'DaDuyet'),
('HP-KLTN', 'Khoa Luan Tot Nghiep',                       10, 'DoAn',     'GV001', 'hocphan/HP-KLTN_20230101_decuong.pdf',  'DaDuyet');

-- =============================================================================
-- 10. DoiNguGiangVienHP  <- HocPhan, GiangVien
--     Business (docs/02 §3.3): Khi tao HocPhan, service TU DONG INSERT CNHP.
--     Cac GV khac trong doi ngu do CNHP bo sung sau. Seed mo phong day du.
-- =============================================================================
INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien, TrangThai) VALUES
-- CNHP tu dong (bat buoc ton tai cho moi HP)
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
-- GV bo sung (do CNHP them vao doi ngu giang day)
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
-- 11. LopHanhChinh  <- ChuongTrinhDaoTao, GiangVien (MaCoVan)
-- =============================================================================
INSERT INTO LopHanhChinh (MaLopHC, TenLop, MaCTDT, KhoaHoc, MaCoVan) VALUES
('CNTT-K22A', 'CNTT Xuat Sac K22 — Lop A', 'CTDT-CNTT-2022', '2022', 'GV003'),
('CNTT-K22B', 'CNTT Xuat Sac K22 — Lop B', 'CTDT-CNTT-2022', '2022', 'GV004'),
('CNTT-K23A', 'CNTT Xuat Sac K23 — Lop A', 'CTDT-CNTT-2023', '2023', 'GV005'),
('CNTT-K23B', 'CNTT Xuat Sac K23 — Lop B', 'CTDT-CNTT-2023', '2023', 'GV006'),
('CNTT-K24A', 'CNTT Xuat Sac K24 — Lop A', 'CTDT-CNTT-2024', '2024', 'GV007'),
('CNTT-K24B', 'CNTT Xuat Sac K24 — Lop B', 'CTDT-CNTT-2024', '2024', 'GV008');

-- =============================================================================
-- 12. SinhVien  <- NguoiDung, LopHanhChinh
-- =============================================================================
INSERT INTO SinhVien (MaSV, MaNguoiDung, MaLopHC, TrangThaiSV) VALUES
-- K22A
('SV2022001', 'SV2022001', 'CNTT-K22A', 'DangHoc'),
('SV2022002', 'SV2022002', 'CNTT-K22A', 'DangHoc'),
('SV2022006', 'SV2022006', 'CNTT-K22A', 'DangHoc'),
('SV2022007', 'SV2022007', 'CNTT-K22A', 'DangHoc'),
('SV2022008', 'SV2022008', 'CNTT-K22A', 'DangHoc'),
-- K22B (+ 1 ThoiHoc)
('SV2022003', 'SV2022003', 'CNTT-K22B', 'DangHoc'),
('SV2022004', 'SV2022004', 'CNTT-K22B', 'DangHoc'),
('SV2022009', 'SV2022009', 'CNTT-K22B', 'DangHoc'),
('SV2022010', 'SV2022010', 'CNTT-K22B', 'DangHoc'),
('SV2022005', 'SV2022005', 'CNTT-K22B', 'ThoiHoc'),
-- K23A (+ 1 BaoLuu)
('SV2023001', 'SV2023001', 'CNTT-K23A', 'DangHoc'),
('SV2023002', 'SV2023002', 'CNTT-K23A', 'DangHoc'),
('SV2023003', 'SV2023003', 'CNTT-K23A', 'DangHoc'),
('SV2023005', 'SV2023005', 'CNTT-K23A', 'DangHoc'),
('SV2023006', 'SV2023006', 'CNTT-K23A', 'DangHoc'),
('SV2023004', 'SV2023004', 'CNTT-K23A', 'BaoLuu'),
-- K23B
('SV2023007', 'SV2023007', 'CNTT-K23B', 'DangHoc'),
('SV2023008', 'SV2023008', 'CNTT-K23B', 'DangHoc'),
('SV2023009', 'SV2023009', 'CNTT-K23B', 'DangHoc'),
('SV2023010', 'SV2023010', 'CNTT-K23B', 'DangHoc'),
-- K24A
('SV2024001', 'SV2024001', 'CNTT-K24A', 'DangHoc'),
('SV2024002', 'SV2024002', 'CNTT-K24A', 'DangHoc'),
('SV2024003', 'SV2024003', 'CNTT-K24A', 'DangHoc'),
('SV2024004', 'SV2024004', 'CNTT-K24A', 'DangHoc'),
('SV2024005', 'SV2024005', 'CNTT-K24A', 'DangHoc'),
-- K24B
('SV2024006', 'SV2024006', 'CNTT-K24B', 'DangHoc'),
('SV2024007', 'SV2024007', 'CNTT-K24B', 'DangHoc'),
('SV2024008', 'SV2024008', 'CNTT-K24B', 'DangHoc'),
('SV2024009', 'SV2024009', 'CNTT-K24B', 'DangHoc'),
('SV2024010', 'SV2024010', 'CNTT-K24B', 'DangHoc');

-- =============================================================================
-- 13. CTDT_HocPhan  <- ChuongTrinhDaoTao, HocPhan
-- =============================================================================
INSERT INTO CTDT_HocPhan (MaCTDT, MaHocPhan, HocKyThu, SoLopDuKien, BatBuoc, GhiChu, FileDeCuong) VALUES
-- CTDT-CNTT-2022 (K22 — dang nam 3-4)
('CTDT-CNTT-2022', 'HP-NNLT', 1, 2, 1, 'Nhap mon lap trinh',              NULL),
('CTDT-CNTT-2022', 'HP-OOP',  1, 2, 1, 'Co so huong doi tuong',           NULL),
('CTDT-CNTT-2022', 'HP-GTDL', 2, 2, 1, 'Cau truc du lieu & giai thuat',   NULL),
('CTDT-CNTT-2022', 'HP-MMT',  2, 1, 1, 'Nen tang mang',                   NULL),
('CTDT-CNTT-2022', 'HP-CSDL', 3, 2, 1, 'Thiet ke & truy van SQL',         NULL),
('CTDT-CNTT-2022', 'HP-HTTT', 4, 1, 1, 'Phan tich thiet ke HTTT',         NULL),
('CTDT-CNTT-2022', 'HP-LTW',  5, 2, 1, 'Full-stack web development',      NULL),
('CTDT-CNTT-2022', 'HP-TTDL', 5, 1, 1, 'Thuc hanh cung HP-CSDL',          NULL),
('CTDT-CNTT-2022', 'HP-PTTK', 5, 1, 1, 'PTTK phan mem',                   NULL),
('CTDT-CNTT-2022', 'HP-KT',   6, 1, 1, 'Kien tap doanh nghiep theo lop',  NULL),
('CTDT-CNTT-2022', 'HP-ATTT', 6, 1, 0, 'Tu chon chuyen sau',              NULL),
('CTDT-CNTT-2022', 'HP-TT',   8, 1, 1, 'Thuc tap cuoi khoa',              NULL),
('CTDT-CNTT-2022', 'HP-KLTN', 8, 1, 1, 'Khoa luan tot nghiep',            NULL),
-- CTDT-CNTT-2023 (K23 — dang nam 2)
('CTDT-CNTT-2023', 'HP-NNLT', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-OOP',  1, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-GTDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-CSDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-LTW',  3, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-TTDL', 3, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-PTTK', 4, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-KT',   5, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-TT',   7, 1, 1, NULL, NULL),
-- CTDT-CNTT-2024 (K24 — dang nam 1)
('CTDT-CNTT-2024', 'HP-NNLT', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-OOP',  1, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-GTDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-CSDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-LTW',  3, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-AI',   3, 1, 0, 'Hoc phan tu chon', NULL);

-- =============================================================================
-- 14. LopHocPhan  <- CTDT_HocPhan, HocKyNamHoc, GiangVien
-- =============================================================================
INSERT INTO LopHocPhan
    (MaCTDT,            MaHocPhan, MaHocKy,   MaLopHocPhan, MaGiangVien, SiSoToiDa, SiSoThucTe, FileDeCuongChiTiet, TrangThai) VALUES
-- --- K22 — HK1-2023 (qua khu, DaDong) ---
('CTDT-CNTT-2022', 'HP-NNLT', 'HK1-2023', 1, 'GV007', 45, 45, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 1, 'GV002', 45, 43, 'lophocphan/CTDT-CNTT-2022_HP-OOP_HK1-2023_1.pdf', 'DaDong'),
('CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 2, 'GV004', 45, 42, NULL, 'DaDong'),
-- --- K22 — HK2-2023 (qua khu, DaDong) ---
('CTDT-CNTT-2022', 'HP-CSDL', 'HK2-2023', 1, 'GV005', 45, 43, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-CSDL', 'HK2-2023', 2, 'GV001', 45, 41, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-MMT',  'HK2-2023', 1, 'GV002', 40, 38, NULL, 'DaDong'),
-- --- K22 — HK1-2024 (dang dien ra, DangMo) ---
('CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'GV003', 45, 40, 'lophocphan/CTDT-CNTT-2022_HP-HTTT_HK1-2024_1.pdf', 'DangMo'),
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 1, 'GV004', 45, 38, 'lophocphan/CTDT-CNTT-2022_HP-LTW_HK1-2024_1.pdf',  'DangMo'),
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 2, 'GV002', 45, 35, NULL, 'DangMo'),
('CTDT-CNTT-2022', 'HP-TTDL', 'HK1-2024', 1, 'GV004', 35, 30, NULL, 'DangMo'),
('CTDT-CNTT-2022', 'HP-PTTK', 'HK1-2024', 1, 'GV011', 40, 32, NULL, 'DangMo'),
('CTDT-CNTT-2022', 'HP-KT',   'HK1-2024', 1, NULL,    35, 9,  NULL, 'DangMo'),
-- --- K23 — HK1-2024 (dang dien ra) ---
('CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, 'GV005', 45, 36, NULL, 'DangMo'),
('CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 2, NULL,    45, 0,  NULL, 'DangMo'),
('CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, 'GV008', 45, 34, NULL, 'DangMo'),
-- --- K24 — HK1-2024 (sinh vien nam 1) ---
('CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, 'GV007', 45, 40, NULL, 'DangMo'),
('CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, 'GV004', 45, 38, NULL, 'DangMo'),
('CTDT-CNTT-2024', 'HP-OOP',  'HK1-2024', 1, 'GV002', 45, 42, NULL, 'DangMo'),
('CTDT-CNTT-2024', 'HP-OOP',  'HK1-2024', 2, 'GV010', 45, 36, NULL, 'DangMo');

-- =============================================================================
-- 15. DanhSachSinhVienLopHocPhan  <- SinhVien, LopHocPhan
--     DaCanhBao=1: khi GV danh dau canh bao (docs/02 §3.6).
-- =============================================================================
INSERT INTO DanhSachSinhVienLopHocPhan
    (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, NhanXet, DaCanhBao, KetQuaXuLy) VALUES
-- ---- HK1-2023 (qua khu) — K22 hoc OOP ----
('SV2022001', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 1, 'Tham gia day du, kien thuc vung vang.',           0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 1, 'Hoc tap cham chi, co y thuc cau tien.',            0, NULL),
('SV2022006', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 1, 'Chu dong tham gia bai tap nhom.',                  0, NULL),
('SV2022007', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 1, NULL,                                                0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 2, 'Nghi nhieu, khong chuan bi bai.',                  1,
 'Da tu van ngay 15/10/2023. Sinh vien cam ket cai thien, tham gia day du cac buoi sau.'),
('SV2022004', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 2, 'Co gang nhung can them ho tro.',                   0, NULL),
('SV2022009', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 2, NULL,                                                0, NULL),
('SV2022010', 'CTDT-CNTT-2022', 'HP-OOP', 'HK1-2023', 2, NULL,                                                0, NULL),
-- ---- HK1-2024 (dang dien ra) — K22 hoc LTW ----
('SV2022001', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, 'Sinh vien hoc tap tich cuc.',                       0, NULL),
('SV2022006', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022007', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022008', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, 'Bo 4 buoi, khong nop bai tap lon giua ky.',         1, NULL),
('SV2022004', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, NULL,                                                0, NULL),
('SV2022009', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, NULL,                                                0, NULL),
('SV2022010', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, 'It tuong tac tren lop.',                            0, NULL),
-- ---- HK1-2024 — K22 hoc HTTT ----
('SV2022001', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Tham gia day du.',                                 0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022004', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Co gang hoc tap.',                                 0, NULL),
('SV2022006', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022007', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022008', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                                0, NULL),
('SV2022009', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Thuong xuyen di tre.',                             1, NULL),
('SV2022010', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                                0, NULL),
-- ---- HK1-2024 — K23 dang ky HP-CSDL ----
('SV2023001', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023002', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023003', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023005', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023006', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023007', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023008', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023009', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023010', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, 'Thanh tich hoc tap tot.',                          0, NULL),
-- ---- HK1-2024 — K23 dang ky HP-GTDL ----
('SV2023001', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023002', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023003', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023005', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023006', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023007', 'CTDT-CNTT-2023', 'HP-GTDL', 'HK1-2024', 1, NULL, 0, NULL),
-- ---- HK1-2024 — K24 dang ky HP-NNLT ----
('SV2024001', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024002', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024003', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024004', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024005', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 1, NULL, 0, NULL),
('SV2024006', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, NULL, 0, NULL),
('SV2024007', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, NULL, 0, NULL),
('SV2024008', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, NULL, 0, NULL),
('SV2024009', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, NULL, 0, NULL),
('SV2024010', 'CTDT-CNTT-2024', 'HP-NNLT', 'HK1-2024', 2, 'SV moi, can theo doi them.',                       0, NULL);

-- =============================================================================
-- 16. DotKienTap  <- LopHanhChinh, HocKyNamHoc, GiangVien, DoanhNghiep, NguoiDung
-- =============================================================================
INSERT INTO DotKienTap
    (MaDotKT, TenDotKT,                                        MaLopHC,      MaHocKy,    ThoiGian,    MaGVPhuTrach, MaDoanhNghiep,
     NhanXetGV,                                                NhanXetDN,                                                          FileMinhChung,                      KinhPhiChung, KinhPhiTungSV, TrangThai,    NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Kien Tap K22A tai FPT Software — HK1-2024',               'CNTT-K22A', 'HK1-2024', '2024-11-15', 'GV003', 'DN001',
    'Sinh vien tham gia nghiem tuc, co y thuc tim hieu nghiep vu thuc te. Nhom co 2 em noi bat ve ky nang coding.',
    'Nhan vien nhiet tinh, ham hoc hoi. Can nang cao ky nang giao tiep bang tieng Anh va lam viec voi khach hang.',
    'kientap/DotKT_1_minh_chung.pdf',                          10000000,      500000,        'DaThucHien', 'GV001', 'GV002', '2024-10-01 14:00:00'),
(2, 'Kien Tap K22B tai VNG Corporation — HK1-2024',            'CNTT-K22B', 'HK1-2024', '2024-11-20', 'GV004', 'DN002',
    NULL, NULL,
    'kientap/DotKT_2_minh_chung.pdf',                           8000000,      400000,        'DaDuyet',    'GV001', 'GV002', '2024-10-05 09:30:00'),
(3, 'Kien Tap K23A tai TMA Solutions — HK2-2024',              'CNTT-K23A', 'HK2-2024', NULL,        'GV002', 'DN003',
    NULL, NULL, NULL,                                           NULL,         NULL,          'ChoDuyet',   'GV001', NULL,    NULL),
(4, 'Kien Tap K23B tai Viettel Solutions — HK2-2024',          'CNTT-K23B', 'HK2-2024', NULL,        'GV006', 'DN004',
    NULL, NULL, NULL,                                           NULL,         NULL,          'ChuanBi',    'GV001', NULL,    NULL),
(5, 'Kien Tap K22A tai MISA — HK2-2024 (bo sung)',             'CNTT-K22A', 'HK2-2024', '2025-04-10', 'GV003', 'DN005',
    NULL, NULL, NULL,                                           7500000,      375000,        'DaDuyet',    'GV001', 'GV002', '2025-02-20 11:00:00');

-- =============================================================================
-- 17. DanhSachSinhVienKienTap  <- DotKienTap, SinhVien  (HYBRID RULE)
--     Edge-case: SV2022005 (ThoiHoc) + SV2023004 (BaoLuu) KHONG xuat hien —
--     auto-add chi lay 'DangHoc'.
-- =============================================================================
INSERT INTO DanhSachSinhVienKienTap (MaDotKT, MaSV, DaThamGia) VALUES
-- Dot 1 (K22A @FPT) — 5 SV DangHoc, ca 5 tham gia
(1, 'SV2022001', 1),
(1, 'SV2022002', 1),
(1, 'SV2022006', 1),
(1, 'SV2022007', 1),
(1, 'SV2022008', 1),
-- Dot 2 (K22B @VNG) — 4 SV DangHoc; SV2022004 toggle khong tham gia; SV2022005 ThoiHoc khong xuat hien
(2, 'SV2022003', 1),
(2, 'SV2022004', 0),
(2, 'SV2022009', 1),
(2, 'SV2022010', 1),
-- Dot 3 (K23A @TMA) — 5 SV DangHoc; SV2023004 BaoLuu khong xuat hien
(3, 'SV2023001', 1),
(3, 'SV2023002', 1),
(3, 'SV2023003', 1),
(3, 'SV2023005', 1),
(3, 'SV2023006', 1),
-- Dot 4 (K23B @Viettel) — 4 SV DangHoc
(4, 'SV2023007', 1),
(4, 'SV2023008', 1),
(4, 'SV2023009', 1),
(4, 'SV2023010', 1),
-- Dot 5 (K22A @MISA bo sung) — 1 so SV dang ky lai
(5, 'SV2022001', 1),
(5, 'SV2022006', 1);

-- =============================================================================
-- 18. DotThucTap  <- CTDT_HocPhan, HocKyNamHoc, NguoiDung
-- =============================================================================
INSERT INTO DotThucTap
    (MaDotTT, TenDotTT,                                        MaCTDT,            MaHocPhan, MaHocKy,
     NgayBatDau,  NgayKetThuc, FileMinhChung,                    TrangThai,        NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Thuc Tap Cuoi Khoa K22 — Dot 1 — 2024-2025',               'CTDT-CNTT-2022', 'HP-TT', 'HK1-2024',
    '2024-11-01', '2025-01-05', 'thuctap/DotTT_1_ke_hoach.pdf',   'DangThucHien',   'GV001', 'GV002', '2024-10-15 10:00:00'),
(2, 'Thuc Tap Cuoi Khoa K22 — Dot 2 — 2025',                    'CTDT-CNTT-2022', 'HP-TT', 'HK2-2024',
    '2025-02-15', '2025-05-20', NULL,                             'DaDuyet',        'GV001', 'GV002', '2025-01-10 09:00:00'),
(3, 'Thuc Tap Cuoi Khoa K23 — Dot 1 — 2025',                    'CTDT-CNTT-2023', 'HP-TT', 'HK2-2024',
    '2025-03-01', '2025-05-20', NULL,                             'ChuanBi',        'GV001', NULL,    NULL);

-- =============================================================================
-- 19. DanhSachThucTap  <- DotThucTap, SinhVien, DoanhNghiep
-- =============================================================================
INSERT INTO DanhSachThucTap
    (MaThucTap, MaDotTT, MaSV,        LoaiThucTap,   MaDoanhNghiep, TrangThai) VALUES
-- Dot 1 (K22 — HK1-2024): 5 SV dang thuc tap thuc te
(1,  1, 'SV2022001', 'DoanhNghiep', 'DN001', 'DangThucTap'),
(2,  1, 'SV2022002', 'DoanhNghiep', 'DN002', 'DangThucTap'),
(3,  1, 'SV2022003', 'DoanhNghiep', 'DN003', 'DangThucTap'),
(4,  1, 'SV2022004', 'Truong',      NULL,    'DangThucTap'),   -- thuc tap tai truong
(5,  1, 'SV2022006', 'DoanhNghiep', 'DN005', 'DangThucTap'),
-- Dot 2 (K22 — HK2-2024): 3 SV chuan bi
(6,  2, 'SV2022007', 'DoanhNghiep', 'DN001', 'DaPhanCong'),
(7,  2, 'SV2022008', 'DoanhNghiep', 'DN006', 'DaPhanCong'),
(8,  2, 'SV2022009', 'DoanhNghiep', 'DN007', 'DaPhanCong'),
(9,  2, 'SV2022010', 'DoanhNghiep', 'DN008', 'DaPhanCong'),
-- Dot 3 (K23 — HK2-2024): chi moi dang ChuanBi
(10, 3, 'SV2023001', 'DoanhNghiep', 'DN004', 'DaPhanCong');

-- =============================================================================
-- 20. KetQuaThucTap — Phase 7: he thong 2 cot diem
--
--     Quy uoc:
--       - LoaiThucTap = 'Truong'      -> GV_HD (cot 1) + GV_PB (cot 2)
--       - LoaiThucTap = 'DoanhNghiep' -> DN    (cot 1) + GV_HD (cot 2)
--       - CVHT (neu co): cham diem tong the cua co van hoc tap, KHONG nam
--         trong 2 cot tinh diem ket thuc — chi tham khao.
--     UNIQUE (MaThucTap, MaVaiTro): moi vai tro chi 1 row / SV.
-- =============================================================================
INSERT INTO KetQuaThucTap (MaThucTap, MaVaiTro, MaNguoiDanhGia, Diem, NhanXet) VALUES
-- ---- SV2022001 @FPT (DoanhNghiep) — du 2 cot diem + tham khao CVHT ----
(1, 'DN',    'GV001', 9.00, 'Nhan vien thuc tap xuat sac, kha nang lam viec nhom va doc lap deu cao. De xuat tuyen dung sau tot nghiep.'),
(1, 'GV_HD', 'GV001', 8.50, 'Sinh vien hoan thanh tot nhiem vu, tu giac va co trach nhiem voi cong viec duoc giao. Bao cao tien do dung han.'),
(1, 'CVHT',  'GV003', 8.75, 'Sinh vien co y thuc hoc tap tot, bao cao tien do deu dan voi co van.'),

-- ---- SV2022002 @VNG (DoanhNghiep) — moi co cot DN + tham khao CVHT ----
(2, 'DN',    'GV001', 7.80, 'Nang luc kha, can chu dong hon. Phan dau dat hieu qua hon o nhung thang sau.'),
(2, 'GV_HD', 'GV001', 7.50, 'Sinh vien can chu dong hon trong viec tim hieu cong viec. Chat luong bao cao tam on.'),
(2, 'CVHT',  'GV003', 7.75, 'Tham gia cac buoi sinh hoat lop day du.'),

-- ---- SV2022003 @TMA (DoanhNghiep) — du 2 cot diem ----
(3, 'DN',    'GV001', 7.80, 'Tiep thu nhanh, can cai thien ky nang trinh bay va viet bao cao chuyen mon.'),
(3, 'GV_HD', 'GV001', 8.00, 'Hoan thanh tot cac nhiem vu, tinh than hoc hoi cao. Code style on dinh.'),

-- ---- SV2022004 tai Truong — du 2 cot diem (GV_HD + GV_PB) ----
(4, 'GV_HD', 'GV001', 7.00, 'Tham gia nghien cuu de tai voi co van. Can tang toc do trien khai.'),
(4, 'GV_PB', 'GV011', 7.50, 'Bao cao trinh bay ro rang, kien thuc nen tang vung. De xuat ke hoach qua chi tiet.'),
(4, 'CVHT',  'GV004', 7.50, 'Sinh vien gap kho khan ca nhan, da duoc ho tro lich lam viec linh hoat.'),

-- ---- SV2022006 @MISA (DoanhNghiep) — du 2 cot diem ----
(5, 'DN',    'GV001', 9.00, 'Thanh thao cac cong cu ke toan doanh nghiep, tu duy tot. De xuat hop tac dai han.'),
(5, 'GV_HD', 'GV001', 9.20, 'Mot trong nhung sinh vien xuat sac cua dot. Bao cao chat luong cao, dung tien do.');

-- =============================================================================
-- KIEM TRA NHANH SAU KHI CHAY  (SELECT thu cong de xac nhan)
-- =============================================================================
-- SELECT COUNT(*) AS HocKyNamHoc      FROM HocKyNamHoc;                              -- 5
-- SELECT COUNT(*) AS NguoiDung        FROM NguoiDung;                                -- 50
-- SELECT COUNT(*) AS GiangVien        FROM GiangVien;                                -- 12
-- SELECT COUNT(*) AS DoanhNghiep      FROM DoanhNghiep;                              -- 9
-- SELECT COUNT(*) AS DN_DangHopTac    FROM DoanhNghiep WHERE TrangThai='DangHopTac'; -- 8
-- SELECT COUNT(*) AS VaiTroThucTap    FROM VaiTroThucTap;                            -- 5
-- SELECT COUNT(*) AS NhomNguoiDung    FROM NhomNguoiDung;                            -- 18
-- SELECT COUNT(*) AS CTDT             FROM ChuongTrinhDaoTao;                        -- 4
-- SELECT COUNT(*) AS BCN              FROM BCN_ThanhVien;                            -- 15
-- SELECT COUNT(*) AS HocPhan          FROM HocPhan;                                  -- 14
-- SELECT COUNT(*) AS DoiNguGV         FROM DoiNguGiangVienHP;                        -- 42
-- SELECT COUNT(*) AS LopHC            FROM LopHanhChinh;                             -- 6
-- SELECT COUNT(*) AS SinhVien         FROM SinhVien;                                 -- 30
-- SELECT COUNT(*) AS SV_DangHoc       FROM SinhVien WHERE TrangThaiSV='DangHoc';     -- 28
-- SELECT COUNT(*) AS CTDT_HP          FROM CTDT_HocPhan;                             -- 29
-- SELECT COUNT(*) AS LopHocPhan       FROM LopHocPhan;                               -- 19
-- SELECT COUNT(*) AS DSSV_LHP         FROM DanhSachSinhVienLopHocPhan;               -- 52
-- SELECT COUNT(*) AS CanhBaoChoXuLy   FROM DanhSachSinhVienLopHocPhan
--                                      WHERE DaCanhBao=1 AND KetQuaXuLy IS NULL;     -- 2
-- SELECT COUNT(*) AS DotKienTap       FROM DotKienTap;                               -- 5
-- SELECT COUNT(*) AS DSSV_KT          FROM DanhSachSinhVienKienTap;                  -- 20
-- SELECT COUNT(*) AS DotThucTap       FROM DotThucTap;                               -- 3
-- SELECT COUNT(*) AS DanhSachTT       FROM DanhSachThucTap;                          -- 10
-- SELECT COUNT(*) AS KetQuaTT         FROM KetQuaThucTap;                            -- 13
-- SELECT MaVaiTro, COUNT(*) AS so_dong FROM KetQuaThucTap GROUP BY MaVaiTro;
--   -> CVHT=3, DN=4, GV_HD=5, GV_PB=1

-- =============================================================================
-- TAI KHOAN TEST  (Mat khau: Password@123)
-- =============================================================================
-- TenDangNhap          LoaiNguoiDung   Vai Tro                       Ghi chu
-- admin                Admin           ADMIN                          Toan quyen he thong
-- tran.van.an          GiangVien       PDT + TTDTXS                   Truong Phong Dao Tao
-- le.thi.binh          GiangVien       TTDTXS                         Thanh vien TT Dao Tao Xuat Sac
-- nguyen.van.cuong     GiangVien       CVHT + CNHP                    CVHT CNTT-K22A, CNHP HP-HTTT / HP-KT
-- pham.thi.dung        GiangVien       CVHT + CNHP                    CVHT CNTT-K22B, CNHP HP-LTW / HP-PTTK
-- hoang.van.em         GiangVien       CVHT + CNHP                    CVHT CNTT-K23A, CNHP HP-CSDL / HP-TTDL
-- vu.thi.giang         GiangVien       CVHT + CNHP                    CVHT CNTT-K23B, CNHP HP-AI
-- do.minh.hieu         GiangVien       CVHT + CNHP                    CVHT CNTT-K24A, CNHP HP-NNLT
-- bui.thanh.ha         GiangVien       CVHT + CNHP                    CVHT CNTT-K24B, CNHP HP-GTDL
-- ngo.thi.lan          GiangVien       CNHP                           CNHP HP-ATTT (cung kiem GV phan bien thuc tap)
-- phan.thi.ngoc        GiangVien       CNHP                           CNHP HP-PTTK / HP-KLTN — vai tro GV_PB
-- sv.2022001           SinhVien        -                              K22A, DangThucTap @FPT — du 3 chieu danh gia (DN+GV_HD+CVHT)
-- sv.2022003           SinhVien        -                              K22B, DaCanhBao HP-OOP (da xu ly)
-- sv.2022004           SinhVien        -                              K22B, Thuc tap TAI TRUONG — du 2 cot diem GV_HD+GV_PB
-- sv.2022005           SinhVien        -                              K22B, ThoiHoc — KHONG auto-add Dot KT
-- sv.2023004           SinhVien        -                              K23A, BaoLuu  — KHONG auto-add Dot KT
-- sv.2024001..010      SinhVien        -                              K24 — sinh vien nam 1
-- dn.fpt / dn.vng / ...DoanhNghiep     -                              7 doanh nghiep dang hop tac

-- =============================================================================
-- HET SEED
-- =============================================================================
