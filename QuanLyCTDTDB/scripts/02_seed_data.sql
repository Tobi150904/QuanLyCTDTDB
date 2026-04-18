-- ============================================================
-- 02_seed_data.sql  --  Du lieu mau test he thong QuanLyCTDTDB
-- Chay SAU KHI da chay 01_create_tables.sql thanh cong
-- Thu tu INSERT phai khop voi dependency chain cua bang
--
-- MatKhau hash: BCrypt("Password@123")
--   $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13
-- ============================================================

USE QuanLyCTDTDB;

-- ============================================================
-- 1. HocKyNamHoc  (doc lap)
-- ============================================================
INSERT INTO HocKyNamHoc (MaHocKy, TenHocKy, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('HK1-2022', 'Hoc Ky 1 Nam 2022-2023', '2022-09-05', '2023-01-13', 'DaKetThuc'),
('HK2-2022', 'Hoc Ky 2 Nam 2022-2023', '2023-01-23', '2023-06-02', 'DaKetThuc'),
('HK1-2023', 'Hoc Ky 1 Nam 2023-2024', '2023-09-04', '2024-01-12', 'DaKetThuc'),
('HK2-2023', 'Hoc Ky 2 Nam 2023-2024', '2024-01-22', '2024-05-31', 'DaKetThuc'),
('HK1-2024', 'Hoc Ky 1 Nam 2024-2025', '2024-09-02', '2025-01-10', 'DangDienRa'),
('HK2-2024', 'Hoc Ky 2 Nam 2024-2025', '2025-01-20', '2025-05-30', 'SapDienRa');

-- ============================================================
-- 2. NguoiDung  (doc lap)
-- MatKhau chung: Password@123
-- ============================================================
INSERT INTO NguoiDung
    (MaNguoiDung, TenDangNhap,   MatKhauHash,                                                        Email,                       HoTen,             SoDienThoai, TrangThaiTK, LoaiNguoiDung) VALUES
-- ---- Admin ----
('AD001', 'admin',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'admin@ntu.edu.vn',          'Quan Tri Vien',   '0900000001', 1, 'Admin'),
-- ---- Giang vien ----
('GV001', 'tran.van.an',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tran.van.an@ntu.edu.vn',    'Tran Van An',     '0901000001', 1, 'GiangVien'),
('GV002', 'le.thi.bich',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'le.thi.bich@ntu.edu.vn',    'Le Thi Bich',     '0901000002', 1, 'GiangVien'),
('GV003', 'nguyen.cong',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'nguyen.cong@ntu.edu.vn',    'Nguyen Van Cong', '0901000003', 1, 'GiangVien'),
('GV004', 'pham.dung',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'pham.dung@ntu.edu.vn',      'Pham Thi Dung',   '0901000004', 1, 'GiangVien'),
('GV005', 'hoang.em',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'hoang.em@ntu.edu.vn',       'Hoang Van Em',    '0901000005', 1, 'GiangVien'),
-- ---- Sinh vien ----
('SV001', 'sv.2024001',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024001@sv.ntu.edu.vn',   'Nguyen Thi Hoa',  '0912000001', 1, 'SinhVien'),
('SV002', 'sv.2024002',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024002@sv.ntu.edu.vn',   'Tran Minh Khoa',  '0912000002', 1, 'SinhVien'),
('SV003', 'sv.2024003',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024003@sv.ntu.edu.vn',   'Le Quoc Long',    '0912000003', 1, 'SinhVien'),
('SV004', 'sv.2023001',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023001@sv.ntu.edu.vn',   'Pham Ngoc Mai',   '0912000004', 1, 'SinhVien'),
('SV005', 'sv.2023002',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023002@sv.ntu.edu.vn',   'Hoang Thi Nhu',   '0912000005', 1, 'SinhVien'),
('SV006', 'sv.2022001',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022001@sv.ntu.edu.vn',   'Vo Thanh Phong',  '0912000006', 1, 'SinhVien'),
('SV007', 'sv.2022002',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022002@sv.ntu.edu.vn',   'Bui Thi Quynh',   '0912000007', 1, 'SinhVien'),
-- ---- Doanh nghiep ----
('DN001', 'dn.fpt',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'hr@fpt.com.vn',             'FPT Software',    '0281000001', 1, 'DoanhNghiep'),
('DN002', 'dn.vng',       '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'internship@vng.com.vn',     'VNG Corporation', '0281000002', 1, 'DoanhNghiep');

-- ============================================================
-- 3. GiangVien  <- NguoiDung
-- ============================================================
INSERT INTO GiangVien (MaGV, MaNguoiDung, HocHam, HocVi, ChuyenNganh, LoaiGiangVien) VALUES
('GV001', 'GV001', 'Pho Giao Su', 'Tien Si', 'Cong Nghe Phan Mem',    'GiangVienTruong'),
('GV002', 'GV002', NULL,          'Thac Si',  'Mang May Tinh',         'GiangVienTruong'),
('GV003', 'GV003', NULL,          'Tien Si',  'He Thong Thong Tin',    'GiangVienTruong'),
('GV004', 'GV004', NULL,          'Thac Si',  'Cong Nghe Phan Mem',    'GiangVienTruong'),
('GV005', 'GV005', NULL,          'Thac Si',  'Khoa Hoc Du Lieu',      'GiangVienTruong');

-- ============================================================
-- 4. DoanhNghiep  (doc lap)
-- ============================================================
INSERT INTO DoanhNghiep (MaDoanhNghiep, TenDoanhNghiep, LinhVuc, NguoiDaiDien, Email, SoDienThoai, DiaChiDN, TrangThai) VALUES
('DN001', 'FPT Software Co., Ltd',   'Phan Mem & CNTT',        'Nguyen Van Hung', 'hr@fpt.com.vn',          '0281000001', '17 Duy Tan, Ha Noi',         'DangHopTac'),
('DN002', 'VNG Corporation',         'Game & Cong Nghe',       'Le Thi Thu',      'internship@vng.com.vn',  '0281000002', '182 Le Dai Hanh, TP.HCM',    'DangHopTac'),
('DN003', 'TMA Solutions',           'Phan Mem & Outsourcing', 'Tran Minh Dang',  'hr@tmasolutions.com',    '0281000003', 'Khu CNC, TP.HCM',            'DangHopTac'),
('DN004', 'Tiki Corporation',        'Thuong Mai Dien Tu',     'Pham Thi Lan',    'hr@tiki.vn',             '0281000004', '52 Ut Tich, TP.HCM',         'TamNgung');

-- ============================================================
-- 5. VaiTroThucTap  (doc lap - danh muc)
-- ============================================================
INSERT INTO VaiTroThucTap (MaVaiTro, TenVaiTro, MoTa) VALUES
('GV',   'Giang Vien Huong Dan', 'Giang vien phu trach huong dan thuc tap tai truong'),
('DN',   'Doanh Nghiep',         'Nguoi phu trach tai don vi tiep nhan sinh vien thuc tap'),
('CVHT', 'Co Van Hoc Tap',       'Co van hoc tap lop hanh chinh cua sinh vien'),
('SV',   'Sinh Vien Tu Danh Gia','Cam nhan va tu nhan xet cua chinh sinh vien thuc tap');

-- ============================================================
-- 6. NhomNguoiDung  <- NguoiDung
--    VaiTro nghiep vu: PDT | TTDTXS | CVHT | CNHP
-- ============================================================
INSERT INTO NhomNguoiDung (MaNguoiDung, VaiTro) VALUES
('GV001', 'PDT'),       -- GV001: Truong Phong Dao Tao
('GV001', 'TTDTXS'),    -- GV001: kiem TT DAO TAO XS
('GV002', 'TTDTXS'),    -- GV002: TT DAO TAO XS
('GV003', 'CVHT'),      -- GV003: Co Van Hoc Tap lop CNTT-K22A
('GV004', 'CVHT'),      -- GV004: Co Van Hoc Tap lop CNTT-K22B
('GV004', 'CNHP'),      -- GV004: Chu Nhiem HP-LTW
('GV005', 'CNHP');      -- GV005: Chu Nhiem HP-KLTN

-- ============================================================
-- 7. ChuongTrinhDaoTao  <- NguoiDung
-- ============================================================
INSERT INTO ChuongTrinhDaoTao (MaCTDT, TenCTDT, Khoa, TrangThai, NguoiTao, NguoiDuyet, NgayDuyet) VALUES
('CTDT-CNTT-2022', 'CTDT Xuat Sac Nganh CNTT Khoa 2022-2026', '2022', 'DaDuyet',  'GV001', 'GV002', '2022-08-15 09:00:00'),
('CTDT-CNTT-2023', 'CTDT Xuat Sac Nganh CNTT Khoa 2023-2027', '2023', 'DaDuyet',  'GV001', 'GV002', '2023-08-20 10:30:00'),
('CTDT-CNTT-2024', 'CTDT Xuat Sac Nganh CNTT Khoa 2024-2028', '2024', 'ChoDuyet', 'GV001', NULL,    NULL);

-- ============================================================
-- 8. BCN_ThanhVien  <- ChuongTrinhDaoTao, GiangVien
-- ============================================================
INSERT INTO BCN_ThanhVien (MaCTDT, MaGV, ChucDanh, NgayBoNhiem) VALUES
('CTDT-CNTT-2022', 'GV001', 'ChuNhiem', '2022-08-01'),
('CTDT-CNTT-2022', 'GV002', 'ThuKy',    '2022-08-01'),
('CTDT-CNTT-2022', 'GV003', 'UyVien',   '2022-08-01'),
('CTDT-CNTT-2023', 'GV001', 'ChuNhiem', '2023-08-01'),
('CTDT-CNTT-2023', 'GV004', 'ThuKy',    '2023-08-01'),
('CTDT-CNTT-2024', 'GV001', 'ChuNhiem', '2024-08-01'),
('CTDT-CNTT-2024', 'GV005', 'ThuKy',    '2024-08-01');

-- ============================================================
-- 9. HocPhan  <- GiangVien
-- ============================================================
INSERT INTO HocPhan (MaHocPhan, TenHocPhan, SoTinChi, LoaiHocPhan, ChuNhiemHP, TrangThai) VALUES
('HP-LTW',  'Lap Trinh Web',                    3,  'LyThuyet', 'GV004', 'DaDuyet'),
('HP-CSDL', 'Co So Du Lieu',                    3,  'LyThuyet', 'GV001', 'DaDuyet'),
('HP-OOP',  'Lap Trinh Huong Doi Tuong',         3,  'LyThuyet', 'GV002', 'DaDuyet'),
('HP-HTTT', 'He Thong Thong Tin',               3,  'LyThuyet', 'GV003', 'DaDuyet'),
('HP-MMT',  'Mang May Tinh',                    3,  'LyThuyet', 'GV002', 'DaDuyet'),
('HP-TTDL', 'Thuc Hanh Thiet Ke Du Lieu',        2,  'ThucHanh', 'GV004', 'DaDuyet'),
('HP-AI',   'Tri Tue Nhan Tao',                 3,  'LyThuyet', 'GV005', 'ChoDuyet'),
('HP-KT',   'Kien Tap Doanh Nghiep',            2,  'KienTap',  'GV003', 'DaDuyet'),
('HP-TT',   'Thuc Tap Cuoi Khoa',               6,  'ThucTap',  'GV001', 'DaDuyet'),
('HP-KLTN', 'Khoa Luan Tot Nghiep',             10, 'DoAn',     'GV005', 'DaDuyet');

-- ============================================================
-- 10. DoiNguGiangVienHP  <- HocPhan, GiangVien
-- ============================================================
INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien, TrangThai) VALUES
('HP-LTW',  'GV004', 1), ('HP-LTW',  'GV002', 1), ('HP-LTW',  'GV003', 1),
('HP-CSDL', 'GV001', 1), ('HP-CSDL', 'GV004', 1),
('HP-OOP',  'GV002', 1), ('HP-OOP',  'GV003', 1),
('HP-HTTT', 'GV003', 1), ('HP-HTTT', 'GV001', 1),
('HP-MMT',  'GV002', 1), ('HP-MMT',  'GV005', 1),
('HP-TTDL', 'GV004', 1),
('HP-AI',   'GV005', 1),
('HP-KT',   'GV003', 1),
('HP-TT',   'GV001', 1),
('HP-KLTN', 'GV005', 1), ('HP-KLTN', 'GV001', 1), ('HP-KLTN', 'GV002', 1);

-- ============================================================
-- 11. LopHanhChinh  <- ChuongTrinhDaoTao, GiangVien
-- ============================================================
INSERT INTO LopHanhChinh (MaLopHC, TenLop, MaCTDT, KhoaHoc, MaCoVan) VALUES
('CNTT-K22A', 'CNTT Xuat Sac K22 - Lop A', 'CTDT-CNTT-2022', '2022', 'GV003'),
('CNTT-K22B', 'CNTT Xuat Sac K22 - Lop B', 'CTDT-CNTT-2022', '2022', 'GV004'),
('CNTT-K23A', 'CNTT Xuat Sac K23 - Lop A', 'CTDT-CNTT-2023', '2023', 'GV002'),
('CNTT-K24A', 'CNTT Xuat Sac K24 - Lop A', 'CTDT-CNTT-2024', '2024', 'GV001');

-- ============================================================
-- 12. SinhVien  <- NguoiDung, LopHanhChinh
-- ============================================================
INSERT INTO SinhVien (MaSV, MaNguoiDung, MaLopHC, TrangThaiSV) VALUES
('SV001', 'SV001', 'CNTT-K24A', 'DangHoc'),
('SV002', 'SV002', 'CNTT-K24A', 'DangHoc'),
('SV003', 'SV003', 'CNTT-K24A', 'DangHoc'),
('SV004', 'SV004', 'CNTT-K23A', 'DangHoc'),
('SV005', 'SV005', 'CNTT-K23A', 'DangHoc'),
('SV006', 'SV006', 'CNTT-K22A', 'DangHoc'),
('SV007', 'SV007', 'CNTT-K22B', 'DangHoc');

-- ============================================================
-- 13. CTDT_HocPhan  <- ChuongTrinhDaoTao, HocPhan
-- ============================================================
INSERT INTO CTDT_HocPhan (MaCTDT, MaHocPhan, HocKyThu, SoLopDuKien, BatBuoc) VALUES
-- CTDT-CNTT-2022 (K22, dang nam 3-4)
('CTDT-CNTT-2022', 'HP-OOP',  1, 2, 1),
('CTDT-CNTT-2022', 'HP-MMT',  2, 1, 1),
('CTDT-CNTT-2022', 'HP-CSDL', 3, 2, 1),
('CTDT-CNTT-2022', 'HP-HTTT', 4, 1, 1),
('CTDT-CNTT-2022', 'HP-LTW',  5, 2, 1),
('CTDT-CNTT-2022', 'HP-TTDL', 5, 1, 1),
('CTDT-CNTT-2022', 'HP-KT',   6, 1, 1),
('CTDT-CNTT-2022', 'HP-KLTN', 8, 1, 1),
('CTDT-CNTT-2022', 'HP-TT',   8, 1, 1),
-- CTDT-CNTT-2023 (K23, dang nam 1-2)
('CTDT-CNTT-2023', 'HP-OOP',  1, 2, 1),
('CTDT-CNTT-2023', 'HP-CSDL', 2, 2, 1),
('CTDT-CNTT-2023', 'HP-LTW',  3, 2, 1),
('CTDT-CNTT-2023', 'HP-TTDL', 4, 1, 1),
('CTDT-CNTT-2023', 'HP-KT',   5, 1, 1),
('CTDT-CNTT-2023', 'HP-TT',   7, 1, 1),
-- CTDT-CNTT-2024 (ChoDuyet, them HP-AI tu chon)
('CTDT-CNTT-2024', 'HP-OOP',  1, 2, 1),
('CTDT-CNTT-2024', 'HP-LTW',  2, 2, 1),
('CTDT-CNTT-2024', 'HP-AI',   3, 1, 0);

-- ============================================================
-- 14. LopHocPhan  <- CTDT_HocPhan, HocKyNamHoc, GiangVien
-- ============================================================
INSERT INTO LopHocPhan (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, MaGiangVien, SiSoToiDa, SiSoThucTe, TrangThai) VALUES
-- HK1-2023: HP-OOP, HP-MMT (da dong)
('CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 1, 'GV002', 45, 45, 'DaDong'),
('CTDT-CNTT-2022', 'HP-MMT',  'HK1-2023', 1, 'GV002', 40, 38, 'DaDong'),
-- HK2-2023: HP-CSDL (da dong)
('CTDT-CNTT-2022', 'HP-CSDL', 'HK2-2023', 1, 'GV001', 45, 43, 'DaDong'),
('CTDT-CNTT-2022', 'HP-CSDL', 'HK2-2023', 2, 'GV004', 45, 41, 'DaDong'),
-- HK1-2024: dang mo (K22 nam 3, hoc ky 5)
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 1, 'GV004', 45, 30, 'DangMo'),
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 2, 'GV002', 45, 28, 'DangMo'),
('CTDT-CNTT-2022', 'HP-TTDL', 'HK1-2024', 1, 'GV004', 35, 30, 'DangMo'),
('CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'GV003', 40, 35, 'DangMo'),
-- HP-KT chua phan GV
('CTDT-CNTT-2022', 'HP-KT',   'HK1-2024', 1, NULL,    35, 7,  'DangMo');

-- ============================================================
-- 15. DanhSachSinhVienLopHocPhan  <- SinhVien, LopHocPhan
-- ============================================================
INSERT INTO DanhSachSinhVienLopHocPhan
    (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, NhanXet, DaCanhBao, KetQuaXuLy) VALUES
-- SV006, SV007 (K22) - lop HP-OOP HK1-2023 (da ket thuc)
('SV006', 'CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 1, 'Nghi nhieu, co the bi TN',          1, 'Da tu van ngay 15/10/2023. SV cam ket co gang.'),
('SV007', 'CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 1, 'Hoc tap binh thuong',               0, NULL),
-- SV006, SV007 (K22) - lop HP-LTW HK1-2024 (dang mo)
('SV006', 'CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 1, NULL,                                0, NULL),
('SV007', 'CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 2, 'SV hoc tap tich cuc',               0, NULL),
-- SV004, SV005 (K23) - lop HP-OOP HK1-2024 khong co trong seed (chua tao LopHocPhan K23 HK1-2024)
-- -> de trong cho thuc te tao
-- SV001, SV002, SV003 (K24) - chua co LopHocPhan (CTDT-CNTT-2024 chua duyet) -> de trong
-- SV006 bi canh bao HP-HTTT
('SV006', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Khong chuan bi bai, bo 3 buoi',     1, NULL),
('SV007', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Tham gia day du, can co gang hon',  0, NULL);

-- ============================================================
-- 16. DotKienTap  <- LopHanhChinh, HocKyNamHoc, GiangVien,
--                    DoanhNghiep, NguoiDung
-- ============================================================
INSERT INTO DotKienTap
    (MaDotKT, TenDotKT, MaLopHC, MaHocKy, ThoiGian, MaGVPhuTrach, MaDoanhNghiep,
     NhanXetGV, NhanXetDN, KinhPhiChung, KinhPhiTungSV, TrangThai,
     NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Kien Tap K22A tai FPT Software - HK1-2024',
    'CNTT-K22A', 'HK1-2024', '2024-11-15', 'GV003', 'DN001',
    'Sinh vien tham gia nghiem tuc, co y thuc tim hieu nghiep vu thuc te.',
    'Nhan vien nhiet tinh, ham hoc hoi, can nang cao ky nang giao tiep.',
    10000000, 500000, 'DaThucHien',
    'GV001', 'GV002', '2024-10-01 14:00:00'),
(2, 'Kien Tap K22B tai VNG Corporation - HK1-2024',
    'CNTT-K22B', 'HK1-2024', '2024-11-20', 'GV004', 'DN002',
    NULL, NULL, 8000000, 400000, 'DaDuyet',
    'GV001', 'GV002', '2024-10-05 09:30:00'),
(3, 'Kien Tap K23A tai TMA Solutions - HK2-2024',
    'CNTT-K23A', 'HK2-2024', NULL, 'GV002', 'DN003',
    NULL, NULL, NULL, NULL, 'ChoDuyet',
    'GV001', NULL, NULL);

-- ============================================================
-- 17. DanhSachSinhVienKienTap  <- DotKienTap, SinhVien
-- ============================================================
INSERT INTO DanhSachSinhVienKienTap (MaDotKT, MaSV, DaThamGia) VALUES
(1, 'SV006', 1),  -- Dot 1: CNTT-K22A
(2, 'SV007', 1),  -- Dot 2: CNTT-K22B
(3, 'SV004', 1),  -- Dot 3: CNTT-K23A
(3, 'SV005', 1);

-- ============================================================
-- 18. DotThucTap  <- CTDT_HocPhan, HocKyNamHoc, NguoiDung
-- ============================================================
INSERT INTO DotThucTap
    (MaDotTT, TenDotTT, MaCTDT, MaHocPhan, MaHocKy,
     NgayBatDau, NgayKetThuc, TrangThai, NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Thuc Tap Cuoi Khoa K22 - Dot 1 - 2024-2025',
    'CTDT-CNTT-2022', 'HP-TT', 'HK1-2024',
    '2024-11-01', '2025-01-05',
    'DangThucHien', 'GV001', 'GV002', '2024-10-15 10:00:00'),
(2, 'Thuc Tap Cuoi Khoa K23 - Dot 1 - 2025',
    'CTDT-CNTT-2023', 'HP-TT', 'HK2-2024',
    '2025-03-01', '2025-05-20',
    'ChuanBi', 'GV001', NULL, NULL);

-- ============================================================
-- 19. DanhSachThucTap  <- DotThucTap, SinhVien, DoanhNghiep
-- ============================================================
INSERT INTO DanhSachThucTap
    (MaThucTap, MaDotTT, MaSV, LoaiThucTap, MaDoanhNghiep, TrangThai) VALUES
(1, 1, 'SV006', 'DoanhNghiep', 'DN001', 'DangThucTap'),
(2, 1, 'SV007', 'DoanhNghiep', 'DN002', 'DangThucTap');

-- ============================================================
-- 20. KetQuaThucTap  <- DanhSachThucTap, VaiTroThucTap, GiangVien
-- ============================================================
INSERT INTO KetQuaThucTap (MaThucTap, MaVaiTro, MaNguoiDanhGia, Diem, NhanXet) VALUES
-- SV006 tai FPT: GV cho 8.5, DN cho 9.0
(1, 'GV', 'GV001', 8.50, 'Sinh vien hoan thanh tot nhiem vu duoc giao, tu giac va co trach nhiem.'),
(1, 'DN', 'GV001', 9.00, 'Nhan vien thuc tap xuat sac, co kha nang lam viec nhom va doc lap cao.'),
-- SV007 tai VNG: GV cho 7.0, chua co danh gia tu DN
(2, 'GV', 'GV001', 7.00, 'Sinh vien can co gang hon trong viec chu dong tim hieu cong viec.');

-- ============================================================
-- KIEM TRA NHANH SAU KHI CHAY
-- ============================================================
-- SELECT COUNT(*) AS HocKyNamHoc      FROM HocKyNamHoc;             -- 6
-- SELECT COUNT(*) AS NguoiDung        FROM NguoiDung;               -- 16
-- SELECT COUNT(*) AS GiangVien        FROM GiangVien;               -- 5
-- SELECT COUNT(*) AS DoanhNghiep      FROM DoanhNghiep;             -- 4
-- SELECT COUNT(*) AS VaiTroThucTap    FROM VaiTroThucTap;           -- 4
-- SELECT COUNT(*) AS NhomNguoiDung    FROM NhomNguoiDung;           -- 7
-- SELECT COUNT(*) AS CTDT             FROM ChuongTrinhDaoTao;       -- 3
-- SELECT COUNT(*) AS BCN              FROM BCN_ThanhVien;           -- 7
-- SELECT COUNT(*) AS HocPhan          FROM HocPhan;                 -- 10
-- SELECT COUNT(*) AS DoiNguGV         FROM DoiNguGiangVienHP;       -- 18
-- SELECT COUNT(*) AS LopHC            FROM LopHanhChinh;            -- 4
-- SELECT COUNT(*) AS SinhVien         FROM SinhVien;                -- 7
-- SELECT COUNT(*) AS CTDT_HP          FROM CTDT_HocPhan;            -- 18
-- SELECT COUNT(*) AS LopHocPhan       FROM LopHocPhan;              -- 9
-- SELECT COUNT(*) AS DSSV_LHP         FROM DanhSachSinhVienLopHocPhan; -- 6
-- SELECT COUNT(*) AS DotKienTap       FROM DotKienTap;              -- 3
-- SELECT COUNT(*) AS DSSV_KT          FROM DanhSachSinhVienKienTap; -- 4
-- SELECT COUNT(*) AS DotThucTap       FROM DotThucTap;              -- 2
-- SELECT COUNT(*) AS DanhSachTT       FROM DanhSachThucTap;         -- 2
-- SELECT COUNT(*) AS KetQuaTT         FROM KetQuaThucTap;           -- 3
