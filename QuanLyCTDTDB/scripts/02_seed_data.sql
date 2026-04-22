-- =============================================================================
-- 02_seed_data.sql  --  Du lieu mau day du, nhat quan voi docs + schema
--
-- Phien ban: v3 (Phase 3 review - Hybrid DaThamGia)
-- Nguon su that: docs/01_ERD_SCHEMA.md, docs/02_Mo Ta & Thiet Ke Du Lieu.md,
--               docs/03_WORKFLOW.md, scripts/01_create_tables.sql
--
-- THAY DOI v3 so voi v2:
--   + Them SV2023004 (CNTT-K23A, BaoLuu)   -> minh hoa quy tac "chi SV DangHoc moi auto-add"
--   + Them SV2022005 (CNTT-K22B, ThoiHoc)  -> tuong tu
--   + DotKienTap 2 (CNTT-K22B): SV2022004 bi danh dau DaThamGia=0 (khong tham gia)
--     -> minh hoa nghiep vu toggle hybrid (ban ghi van ton tai de audit).
--   + Cap nhat counts: NguoiDung 18 -> 20, SinhVien 10 -> 12 (DS KT van giu 7).
--
-- Chay SAU KHI da chay 01_create_tables.sql thanh cong.
-- Thu tu INSERT khop voi dependency chain FK — chay theo thu tu tu tren xuong.
--
-- MatKhau mac dinh cho moi tai khoan: Password@123
-- BCrypt hash:
--   $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13
--
-- Thong ke ban ghi sau khi chay: xem khoi "KIEM TRA NHANH" cuoi file.
-- =============================================================================

USE QuanLyCTDTDB;

-- Lam sach du lieu cu de script idempotent (tranh xung dot PK/UK khi seed lai).
-- Khong DROP bang, chi TRUNCATE noi dung. Tat FK check trong suot qua trinh xoa.
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE KetQuaThucTap;
TRUNCATE TABLE DanhSachThucTap;
TRUNCATE TABLE DotThucTap;
TRUNCATE TABLE DanhSachSinhVienKienTap;
TRUNCATE TABLE DotKienTap;
TRUNCATE TABLE DanhSachSinhVienLopHocPhan;
TRUNCATE TABLE LopHocPhan;
TRUNCATE TABLE CTDT_HocPhan;
TRUNCATE TABLE DoiNguGiangVienHP;
TRUNCATE TABLE SinhVien;
TRUNCATE TABLE LopHanhChinh;
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
--    Quy uoc: MaHocKy = HK{1|2}-{NamBatDau}  (vi du HK1-2024)
--    Chi 1 hoc ky o trang thai DangDienRa tai moi thoi diem.
--    Business: docs/02 §2 (TrangThaiHocKy enum), docs/03 §1 workflow.
-- =============================================================================
INSERT INTO HocKyNamHoc (MaHocKy, TenHocKy, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('HK1-2023', 'Hoc Ky 1 Nam 2023-2024', '2023-09-04', '2024-01-12', 'DaKetThuc'),
('HK2-2023', 'Hoc Ky 2 Nam 2023-2024', '2024-01-22', '2024-05-31', 'DaKetThuc'),
('HK1-2024', 'Hoc Ky 1 Nam 2024-2025', '2024-09-02', '2025-01-10', 'DangDienRa'),
('HK2-2024', 'Hoc Ky 2 Nam 2024-2025', '2025-01-20', '2025-05-30', 'SapDienRa');

-- =============================================================================
-- 2. NguoiDung  (doc lap)
--    Format MaNguoiDung (docs/02 §1):
--      Admin: AD + 3 so          -> AD001
--      GV:    GV + 3 so          -> GV001
--      SV:    SV + nam + 3 so    -> SV2024001 (9 ky tu)
--      DN:    DN + 3 so          -> DN001
--    MatKhau (truoc hash) = Password@123  -- BCrypt 2a$10
-- =============================================================================
INSERT INTO NguoiDung
    (MaNguoiDung,  TenDangNhap,     MatKhauHash,                                                    Email,                         HoTen,                  SoDienThoai,  TrangThaiTK, LoaiNguoiDung) VALUES
-- ---- Admin ----
('AD001',      'admin',         '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'admin@ntu.edu.vn',            'Quan Tri Vien',        '0900000001', 1, 'Admin'),
-- ---- Giang Vien ----
('GV001',      'tran.van.an',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'tran.van.an@ntu.edu.vn',      'Tran Van An',          '0901000001', 1, 'GiangVien'),
('GV002',      'le.thi.binh',   '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'le.thi.binh@ntu.edu.vn',      'Le Thi Binh',          '0901000002', 1, 'GiangVien'),
('GV003',      'nguyen.cuong',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'nguyen.cuong@ntu.edu.vn',     'Nguyen Van Cuong',     '0901000003', 1, 'GiangVien'),
('GV004',      'pham.dung',     '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'pham.dung@ntu.edu.vn',        'Pham Thi Dung',        '0901000004', 1, 'GiangVien'),
('GV005',      'hoang.em',      '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'hoang.em@ntu.edu.vn',         'Hoang Van Em',         '0901000005', 1, 'GiangVien'),
('GV006',      'vu.thi.giang',  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'vu.thi.giang@ntu.edu.vn',     'Vu Thi Giang',         '0901000006', 1, 'GiangVien'),
-- ---- Sinh Vien: K24 (nam 1) ----
('SV2024001',  'sv.2024001',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024001@sv.ntu.edu.vn',     'Nguyen Thi Hoa',       '0912000001', 1, 'SinhVien'),
('SV2024002',  'sv.2024002',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024002@sv.ntu.edu.vn',     'Tran Minh Khoa',       '0912000002', 1, 'SinhVien'),
('SV2024003',  'sv.2024003',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2024003@sv.ntu.edu.vn',     'Le Quoc Long',         '0912000003', 1, 'SinhVien'),
-- ---- Sinh Vien: K23 (nam 2) ----
('SV2023001',  'sv.2023001',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023001@sv.ntu.edu.vn',     'Pham Ngoc Mai',        '0912000011', 1, 'SinhVien'),
('SV2023002',  'sv.2023002',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023002@sv.ntu.edu.vn',     'Hoang Thi Nhu',        '0912000012', 1, 'SinhVien'),
('SV2023003',  'sv.2023003',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023003@sv.ntu.edu.vn',     'Do Quoc Tuan',         '0912000013', 1, 'SinhVien'),
-- ---- Sinh Vien: K22 (nam 3, se thuc tap + kien tap) ----
('SV2022001',  'sv.2022001',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022001@sv.ntu.edu.vn',     'Vo Thanh Phong',       '0912000021', 1, 'SinhVien'),
('SV2022002',  'sv.2022002',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022002@sv.ntu.edu.vn',     'Bui Thi Quynh',        '0912000022', 1, 'SinhVien'),
('SV2022003',  'sv.2022003',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022003@sv.ntu.edu.vn',     'Ngo Van Tan',          '0912000023', 1, 'SinhVien'),
('SV2022004',  'sv.2022004',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022004@sv.ntu.edu.vn',     'Dao Thi Uyen',         '0912000024', 1, 'SinhVien'),
-- ---- Sinh Vien trang thai KHAC DangHoc (de minh hoa auto-add chi lay DangHoc) ----
('SV2022005',  'sv.2022005',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2022005@sv.ntu.edu.vn',     'Ly Thi Van',           '0912000025', 1, 'SinhVien'),
('SV2023004',  'sv.2023004',    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'sv2023004@sv.ntu.edu.vn',     'Truong Quoc Bao',      '0912000014', 1, 'SinhVien'),
-- ---- Doanh Nghiep (tai khoan login cho nguoi dai dien DN) ----
('DN001',      'dn.fpt',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'hr@fpt.com.vn',               'FPT Software',         '0281000001', 1, 'DoanhNghiep'),
('DN002',      'dn.vng',        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lh13', 'internship@vng.com.vn',       'VNG Corporation',      '0281000002', 1, 'DoanhNghiep');

-- =============================================================================
-- 3. GiangVien  <- NguoiDung
--    Rang buoc (docs/02 §3.2): MaGV = MaNguoiDung.
-- =============================================================================
INSERT INTO GiangVien (MaGV, MaNguoiDung, HocHam, HocVi, ChuyenNganh, LoaiGiangVien) VALUES
('GV001', 'GV001', 'Pho Giao Su', 'Tien Si', 'Cong Nghe Phan Mem',    'GiangVienTruong'),
('GV002', 'GV002', NULL,          'Tien Si', 'Mang May Tinh',         'GiangVienTruong'),
('GV003', 'GV003', NULL,          'Tien Si', 'He Thong Thong Tin',    'GiangVienTruong'),
('GV004', 'GV004', NULL,          'Thac Si', 'Cong Nghe Phan Mem',    'GiangVienTruong'),
('GV005', 'GV005', NULL,          'Thac Si', 'Co So Du Lieu',         'GiangVienTruong'),
('GV006', 'GV006', NULL,          'Thac Si', 'Khoa Hoc Du Lieu',      'GiangVienTruong');

-- =============================================================================
-- 4. DoanhNghiep  (doc lap)
--    TrangThai: DangHopTac | TamNgung
--    Business (docs/02 §3.7): DotKienTap chi tham chieu DN DangHopTac.
-- =============================================================================
INSERT INTO DoanhNghiep (MaDoanhNghiep, TenDoanhNghiep, LinhVuc, NguoiDaiDien, Email, SoDienThoai, DiaChiDN, TrangThai) VALUES
('DN001', 'FPT Software Co., Ltd',   'Phan Mem & CNTT',        'Nguyen Van Hung', 'hr@fpt.com.vn',          '0281000001', '17 Duy Tan, Ha Noi',         'DangHopTac'),
('DN002', 'VNG Corporation',         'Game & Cong Nghe',       'Le Thi Thu',      'internship@vng.com.vn',  '0281000002', '182 Le Dai Hanh, TP.HCM',    'DangHopTac'),
('DN003', 'TMA Solutions',           'Phan Mem & Outsourcing', 'Tran Minh Dang',  'hr@tmasolutions.com',    '0281000003', 'Khu CNC, TP.HCM',            'DangHopTac'),
('DN004', 'Tiki Corporation',        'Thuong Mai Dien Tu',     'Pham Thi Lan',    'hr@tiki.vn',             '0281000004', '52 Ut Tich, TP.HCM',         'TamNgung');

-- =============================================================================
-- 5. VaiTroThucTap  (danh muc vai tro danh gia trong KetQuaThucTap)
--    docs/02 §3.9, docs/03 §6.
-- =============================================================================
INSERT INTO VaiTroThucTap (MaVaiTro, TenVaiTro, MoTa) VALUES
('GV',   'Giang Vien Huong Dan', 'Giang vien phu trach huong dan thuc tap tai truong'),
('DN',   'Doanh Nghiep',         'Nguoi phu trach tai don vi tiep nhan sinh vien thuc tap'),
('CVHT', 'Co Van Hoc Tap',       'Co van hoc tap cua lop hanh chinh'),
('SV',   'Sinh Vien Tu Danh Gia','Cam nhan va tu nhan xet cua chinh sinh vien');

-- =============================================================================
-- 6. NhomNguoiDung  <- NguoiDung
--    Vai tro nghiep vu: PDT | TTDTXS | CVHT | CNHP  (docs/02 §2)
--    Business:
--      - GV001: Truong Phong Dao Tao + kiem vien TTDTXS
--      - GV002: TTDTXS
--      - GV003..GV006: CVHT tung lop + CNHP tung hoc phan
-- =============================================================================
INSERT INTO NhomNguoiDung (MaNguoiDung, VaiTro) VALUES
('GV001', 'PDT'),
('GV001', 'TTDTXS'),
('GV002', 'TTDTXS'),
('GV003', 'CVHT'),
('GV004', 'CVHT'),
('GV004', 'CNHP'),
('GV005', 'CVHT'),
('GV005', 'CNHP'),
('GV006', 'CVHT'),
('GV006', 'CNHP');

-- =============================================================================
-- 7. ChuongTrinhDaoTao  <- NguoiDung (NguoiTao + NguoiDuyet)
--    Business (docs/02 §3.4): NguoiDuyet phai khac NguoiTao; khi chuyen
--    trang thai DaDuyet -> auto tao LopHocPhan theo SoLopDuKien.
-- =============================================================================
INSERT INTO ChuongTrinhDaoTao (MaCTDT, TenCTDT, Khoa, FileWord, TrangThai, NguoiTao, NguoiDuyet, NgayDuyet) VALUES
('CTDT-CNTT-2022', 'CTDT Xuat Sac Nganh CNTT Khoa 2022-2026', '2022', 'ctdt/CTDT-CNTT-2022_20220801_khung_ctdt.docx', 'DaDuyet',  'GV001', 'GV002', '2022-08-15 09:00:00'),
('CTDT-CNTT-2023', 'CTDT Xuat Sac Nganh CNTT Khoa 2023-2027', '2023', 'ctdt/CTDT-CNTT-2023_20230801_khung_ctdt.docx', 'DaDuyet',  'GV001', 'GV002', '2023-08-20 10:30:00'),
('CTDT-CNTT-2024', 'CTDT Xuat Sac Nganh CNTT Khoa 2024-2028', '2024', NULL,                                             'ChoDuyet', 'GV001', NULL,    NULL);

-- =============================================================================
-- 8. BCN_ThanhVien  <- ChuongTrinhDaoTao, GiangVien
--    Chuc danh: ChuNhiem, ThuKy, UyVien.
--    Moi CTDT co 1 Chu Nhiem + 1 Thu Ky + 1 Uy Vien (9 record = 3 CTDT × 3 vi tri).
-- =============================================================================
INSERT INTO BCN_ThanhVien (MaCTDT, MaGV, ChucDanh, NgayBoNhiem) VALUES
('CTDT-CNTT-2022', 'GV001', 'ChuNhiem', '2022-08-01'),
('CTDT-CNTT-2022', 'GV002', 'ThuKy',    '2022-08-01'),
('CTDT-CNTT-2022', 'GV003', 'UyVien',   '2022-08-01'),
('CTDT-CNTT-2023', 'GV001', 'ChuNhiem', '2023-08-01'),
('CTDT-CNTT-2023', 'GV004', 'ThuKy',    '2023-08-01'),
('CTDT-CNTT-2023', 'GV005', 'UyVien',   '2023-08-01'),
('CTDT-CNTT-2024', 'GV001', 'ChuNhiem', '2024-08-01'),
('CTDT-CNTT-2024', 'GV005', 'ThuKy',    '2024-08-01'),
('CTDT-CNTT-2024', 'GV006', 'UyVien',   '2024-08-01');

-- =============================================================================
-- 9. HocPhan  <- GiangVien (ChuNhiemHP)
--    LoaiHocPhan: LyThuyet | ThucHanh | DoAn | ThucTap | KienTap
--    TrangThai: BanNhap | ChoDuyet | DaDuyet  (mac dinh BanNhap)
-- =============================================================================
INSERT INTO HocPhan (MaHocPhan, TenHocPhan, SoTinChi, LoaiHocPhan, ChuNhiemHP, FileDeCuong, TrangThai) VALUES
('HP-OOP',  'Lap Trinh Huong Doi Tuong',      3,  'LyThuyet', 'GV002', 'hocphan/HP-OOP_20220801_decuong.pdf',  'DaDuyet'),
('HP-CSDL', 'Co So Du Lieu',                  3,  'LyThuyet', 'GV005', 'hocphan/HP-CSDL_20220801_decuong.pdf', 'DaDuyet'),
('HP-MMT',  'Mang May Tinh',                  3,  'LyThuyet', 'GV002', 'hocphan/HP-MMT_20220801_decuong.pdf',  'DaDuyet'),
('HP-HTTT', 'He Thong Thong Tin',             3,  'LyThuyet', 'GV003', 'hocphan/HP-HTTT_20230201_decuong.pdf', 'DaDuyet'),
('HP-LTW',  'Lap Trinh Web',                  3,  'LyThuyet', 'GV004', 'hocphan/HP-LTW_20230801_decuong.pdf',  'DaDuyet'),
('HP-TTDL', 'Thuc Hanh Thiet Ke Du Lieu',     2,  'ThucHanh', 'GV005', 'hocphan/HP-TTDL_20230801_decuong.pdf', 'DaDuyet'),
('HP-AI',   'Tri Tue Nhan Tao',               3,  'LyThuyet', 'GV006', NULL,                                    'ChoDuyet'),
('HP-KT',   'Kien Tap Doanh Nghiep',          2,  'KienTap',  'GV003', 'hocphan/HP-KT_20230101_decuong.pdf',   'DaDuyet'),
('HP-TT',   'Thuc Tap Cuoi Khoa',             6,  'ThucTap',  'GV001', 'hocphan/HP-TT_20230101_decuong.pdf',   'DaDuyet'),
('HP-KLTN', 'Khoa Luan Tot Nghiep',           10, 'DoAn',     'GV001', 'hocphan/HP-KLTN_20230101_decuong.pdf', 'DaDuyet');

-- =============================================================================
-- 10. DoiNguGiangVienHP  <- HocPhan, GiangVien
--    Business (docs/02 §3.3): Khi tao HocPhan, TU DONG INSERT CNHP vao doi ngu.
--    Cac GV khac trong doi ngu do CNHP bo sung sau.
--    Seed mo phong day du: 10 CNHP (auto) + 13 GV bo sung.
-- =============================================================================
INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien, TrangThai) VALUES
-- CNHP auto-add (bat buoc ton tai cho moi HP)
('HP-OOP',  'GV002', 1),
('HP-CSDL', 'GV005', 1),
('HP-MMT',  'GV002', 1),
('HP-HTTT', 'GV003', 1),
('HP-LTW',  'GV004', 1),
('HP-TTDL', 'GV005', 1),
('HP-AI',   'GV006', 1),
('HP-KT',   'GV003', 1),
('HP-TT',   'GV001', 1),
('HP-KLTN', 'GV001', 1),
-- Bo sung GV co nang luc giang day (do CNHP them)
('HP-OOP',  'GV004', 1),
('HP-OOP',  'GV006', 1),
('HP-CSDL', 'GV001', 1),
('HP-CSDL', 'GV004', 1),
('HP-MMT',  'GV006', 1),
('HP-HTTT', 'GV001', 1),
('HP-LTW',  'GV002', 1),
('HP-LTW',  'GV003', 1),
('HP-LTW',  'GV006', 1),
('HP-TTDL', 'GV004', 1),
('HP-AI',   'GV005', 1),
('HP-KT',   'GV001', 1),
('HP-KT',   'GV004', 1),
('HP-KLTN', 'GV002', 1),
('HP-KLTN', 'GV005', 1);

-- =============================================================================
-- 11. LopHanhChinh  <- ChuongTrinhDaoTao, GiangVien (MaCoVan)
--    Quy uoc MaLopHC (docs/02 §1): NGANH-K{khoa}{lopChuCai}  (vi du CNTT-K22A)
-- =============================================================================
INSERT INTO LopHanhChinh (MaLopHC, TenLop, MaCTDT, KhoaHoc, MaCoVan) VALUES
('CNTT-K22A', 'CNTT Xuat Sac K22 - Lop A', 'CTDT-CNTT-2022', '2022', 'GV003'),
('CNTT-K22B', 'CNTT Xuat Sac K22 - Lop B', 'CTDT-CNTT-2022', '2022', 'GV004'),
('CNTT-K23A', 'CNTT Xuat Sac K23 - Lop A', 'CTDT-CNTT-2023', '2023', 'GV005'),
('CNTT-K24A', 'CNTT Xuat Sac K24 - Lop A', 'CTDT-CNTT-2024', '2024', 'GV006');

-- =============================================================================
-- 12. SinhVien  <- NguoiDung, LopHanhChinh
--    Rang buoc (docs/02 §3.2): MaSV = MaNguoiDung.
-- =============================================================================
INSERT INTO SinhVien (MaSV, MaNguoiDung, MaLopHC, TrangThaiSV) VALUES
-- K24A (nam 1 - CTDT-CNTT-2024 cho duyet, chua co LopHocPhan)
('SV2024001', 'SV2024001', 'CNTT-K24A', 'DangHoc'),
('SV2024002', 'SV2024002', 'CNTT-K24A', 'DangHoc'),
('SV2024003', 'SV2024003', 'CNTT-K24A', 'DangHoc'),
-- K23A (nam 2)
('SV2023001', 'SV2023001', 'CNTT-K23A', 'DangHoc'),
('SV2023002', 'SV2023002', 'CNTT-K23A', 'DangHoc'),
('SV2023003', 'SV2023003', 'CNTT-K23A', 'DangHoc'),
-- K22A (nam 3 - se di kien tap + chuan bi thuc tap)
('SV2022001', 'SV2022001', 'CNTT-K22A', 'DangHoc'),
('SV2022002', 'SV2022002', 'CNTT-K22A', 'DangHoc'),
-- K22B (nam 3)
('SV2022003', 'SV2022003', 'CNTT-K22B', 'DangHoc'),
('SV2022004', 'SV2022004', 'CNTT-K22B', 'DangHoc'),
-- Test case: SV KHONG thuoc trang thai DangHoc -> khong auto-add vao dot kien tap
('SV2022005', 'SV2022005', 'CNTT-K22B', 'ThoiHoc'),   -- da thoi hoc tu HK2-2023
('SV2023004', 'SV2023004', 'CNTT-K23A', 'BaoLuu');    -- dang bao luu HK1-2024

-- =============================================================================
-- 13. CTDT_HocPhan  <- ChuongTrinhDaoTao, HocPhan
--    Moi dong: HP thuoc CTDT, hoc o HocKyThu nao, so lop du kien, bat buoc/tu chon.
--    Business: chi them HP co TrangThai=DaDuyet (HP-AI ChoDuyet -> khong add cho
--    2022/2023 nhung van co the them cho 2024 de minh hoa).
-- =============================================================================
INSERT INTO CTDT_HocPhan (MaCTDT, MaHocPhan, HocKyThu, SoLopDuKien, BatBuoc, GhiChu, FileDeCuong) VALUES
-- CTDT-CNTT-2022 (K22, dang nam 3-4)
('CTDT-CNTT-2022', 'HP-OOP',  1, 2, 1, 'Co so lap trinh',                    NULL),
('CTDT-CNTT-2022', 'HP-MMT',  2, 1, 1, 'Nen tang mang',                      NULL),
('CTDT-CNTT-2022', 'HP-CSDL', 3, 2, 1, 'Thiet ke & truy van SQL',            NULL),
('CTDT-CNTT-2022', 'HP-HTTT', 4, 1, 1, 'Phan tich thiet ke HTTT',            NULL),
('CTDT-CNTT-2022', 'HP-LTW',  5, 2, 1, 'Full-stack web dev',                 NULL),
('CTDT-CNTT-2022', 'HP-TTDL', 5, 1, 1, 'Thuc hanh cung HP-CSDL',             NULL),
('CTDT-CNTT-2022', 'HP-KT',   6, 1, 1, 'Kien tap doanh nghiep theo lop',     NULL),
('CTDT-CNTT-2022', 'HP-TT',   8, 1, 1, 'Thuc tap cuoi khoa',                 NULL),
('CTDT-CNTT-2022', 'HP-KLTN', 8, 1, 1, 'Khoa luan tot nghiep',               NULL),
-- CTDT-CNTT-2023 (K23, dang nam 2)
('CTDT-CNTT-2023', 'HP-OOP',  1, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-CSDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-LTW',  3, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-TTDL', 4, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-KT',   5, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-TT',   7, 1, 1, NULL, NULL),
-- CTDT-CNTT-2024 (ChoDuyet, them HP-AI la tu chon)
('CTDT-CNTT-2024', 'HP-OOP',  1, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-LTW',  2, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-AI',   3, 1, 0, 'Hoc phan tu chon', NULL);

-- =============================================================================
-- 14. LopHocPhan  <- CTDT_HocPhan, HocKyNamHoc, GiangVien
--    Business (docs/02 §3.5): Khi CTDT duyet -> auto tao LopHocPhan theo
--    SoLopDuKien, MaGiangVien=NULL, TrangThai=DangMo.
--    Seed mo phong:
--      - K22 HK1-2023 & HK2-2023 (qua khu): DaDong, GV phan cong day du
--      - K22 HK1-2024 (hien tai): DangMo, da phan GV
--      - K23 HK1-2024: DangMo, 1 lop chua phan GV (test case "chua phan cong")
--      - K24 (CTDT ChoDuyet): khong co LopHocPhan (chua duyet)
--    SiSoToiDa: 30-60 (CHECK constraint), SiSoThucTe <= SiSoToiDa.
-- =============================================================================
INSERT INTO LopHocPhan (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, MaGiangVien, SiSoToiDa, SiSoThucTe, FileDeCuongChiTiet, TrangThai) VALUES
-- K22 HK1-2023 (qua khu - DaDong)
('CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 1, 'GV002', 45, 45, 'lophocphan/CTDT-CNTT-2022_HP-OOP_HK1-2023_1.pdf', 'DaDong'),
('CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 2, 'GV004', 45, 42, 'lophocphan/CTDT-CNTT-2022_HP-OOP_HK1-2023_2.pdf', 'DaDong'),
('CTDT-CNTT-2022', 'HP-MMT',  'HK1-2023', 1, 'GV002', 40, 38, NULL,                                               'DaDong'),
-- K22 HK2-2023 (qua khu - DaDong)
('CTDT-CNTT-2022', 'HP-CSDL', 'HK2-2023', 1, 'GV005', 45, 43, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-CSDL', 'HK2-2023', 2, 'GV001', 45, 41, NULL, 'DaDong'),
-- K22 HK1-2024 (DangDienRa - DangMo)
('CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'GV003', 45, 40, 'lophocphan/CTDT-CNTT-2022_HP-HTTT_HK1-2024_1.pdf', 'DangMo'),
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 1, 'GV004', 45, 38, 'lophocphan/CTDT-CNTT-2022_HP-LTW_HK1-2024_1.pdf',  'DangMo'),
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 2, 'GV002', 45, 35, NULL,                                                'DangMo'),
('CTDT-CNTT-2022', 'HP-TTDL', 'HK1-2024', 1, 'GV004', 35, 30, NULL,                                                'DangMo'),
('CTDT-CNTT-2022', 'HP-KT',   'HK1-2024', 1, NULL,    35, 4,  NULL,                                                'DangMo'),
-- K23 HK1-2024 (nam 2)
('CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, 'GV005', 45, 3,  NULL, 'DangMo'),
('CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 2, NULL,    45, 0,  NULL, 'DangMo');

-- =============================================================================
-- 15. DanhSachSinhVienLopHocPhan  <- SinhVien, LopHocPhan
--    Moi SV dang ky 1 LopHocPhan (1 record moi SV moi HP).
--    DaCanhBao=1: khi GV danh dau canh bao (docs/02 §3.6).
--    Seed co:
--      - 2 ban ghi canh bao: 1 da xu ly (KetQuaXuLy co gia tri), 1 CHUA xu ly
--      - Cac ban ghi khac: NhanXet binh thuong, DaCanhBao=0
-- =============================================================================
INSERT INTO DanhSachSinhVienLopHocPhan
    (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, NhanXet, DaCanhBao, KetQuaXuLy) VALUES
-- HK1-2023: SV2022001 + SV2022002 + SV2022003 + SV2022004 hoc HP-OOP
('SV2022001', 'CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 1, 'Tham gia day du, kien thuc vung',      0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 1, 'Hoc tap cham chi',                      0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 2, 'Nghi nhieu, khong chuan bi bai',        1,
 'Da tu van ngay 15/10/2023. Sinh vien cam ket cai thien.'),
('SV2022004', 'CTDT-CNTT-2022', 'HP-OOP',  'HK1-2023', 2, 'Co gang nhung can them ho tro',         0, NULL),
-- HK1-2024: SV2022001 + SV2022002 hoc HP-LTW lop 1, SV2022003 + SV2022004 hoc HP-LTW lop 2
('SV2022001', 'CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 1, NULL,                                    0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 1, 'Sinh vien hoc tap tich cuc',            0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 2, 'Bo 4 buoi, khong nop bai tap lon giua ky', 1,
 NULL),  -- DaCanhBao chua xu ly -> dashboard CVHT phai thay
('SV2022004', 'CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 2, NULL,                                    0, NULL),
-- HK1-2024: SV2022001..SV2022004 hoc HP-HTTT
('SV2022001', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Tham gia day du',                       0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                    0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, NULL,                                    0, NULL),
('SV2022004', 'CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'Co gang hoc tap',                       0, NULL),
-- HK1-2024: K23 dang ky HP-CSDL
('SV2023001', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023002', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL),
('SV2023003', 'CTDT-CNTT-2023', 'HP-CSDL', 'HK1-2024', 1, NULL, 0, NULL);

-- =============================================================================
-- 16. DotKienTap  <- LopHanhChinh, HocKyNamHoc, GiangVien, DoanhNghiep, NguoiDung
--    Business (docs/02 §3.7):
--      - MaDoanhNghiep phai o trang thai DangHopTac
--      - KinhPhiChung, KinhPhiTungSV: co the NULL
--    Seed:
--      1. K22A tai FPT - HK1-2024 - DaThucHien (co nhan xet GV + DN)
--      2. K22B tai VNG - HK1-2024 - DaDuyet (chua thuc hien)
--      3. K23A tai TMA - HK2-2024 - ChoDuyet
-- =============================================================================
INSERT INTO DotKienTap
    (MaDotKT, TenDotKT, MaLopHC, MaHocKy, ThoiGian, MaGVPhuTrach, MaDoanhNghiep,
     NhanXetGV, NhanXetDN, FileMinhChung, KinhPhiChung, KinhPhiTungSV, TrangThai,
     NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Kien Tap K22A tai FPT Software - HK1-2024',
    'CNTT-K22A', 'HK1-2024', '2024-11-15', 'GV003', 'DN001',
    'Sinh vien tham gia nghiem tuc, co y thuc tim hieu nghiep vu thuc te.',
    'Nhan vien nhiet tinh, ham hoc hoi, can nang cao ky nang giao tiep.',
    'kientap/DotKT_1_minh_chung.pdf',
    10000000, 500000, 'DaThucHien',
    'GV001', 'GV002', '2024-10-01 14:00:00'),
(2, 'Kien Tap K22B tai VNG Corporation - HK1-2024',
    'CNTT-K22B', 'HK1-2024', '2024-11-20', 'GV004', 'DN002',
    NULL, NULL,
    'kientap/DotKT_2_minh_chung.pdf',
    8000000, 400000, 'DaDuyet',
    'GV001', 'GV002', '2024-10-05 09:30:00'),
(3, 'Kien Tap K23A tai TMA Solutions - HK2-2024',
    'CNTT-K23A', 'HK2-2024', NULL, 'GV002', 'DN003',
    NULL, NULL, NULL, NULL, NULL, 'ChoDuyet',
    'GV001', NULL, NULL);

-- =============================================================================
-- 17. DanhSachSinhVienKienTap  <- DotKienTap, SinhVien  (HYBRID RULE)
--    Business (docs/02 §3.7 + docs/03 WF-07.1, WF-07.2):
--      1. Khi tao dot KT, service auto-add TAT CA SV co TrangThaiSV='DangHoc'
--         cua lop thuoc dot do (DaThamGia=1).
--      2. Admin/BCN co the TOGGLE DaThamGia (danh dau "khong tham gia") — KHONG
--         xoa ban ghi, luon giu de audit.
--
--    Seed mo phong:
--      Dot 1 (CNTT-K22A): SV2022001, SV2022002              (2 SV, ca 2 DaThamGia=1)
--      Dot 2 (CNTT-K22B): SV2022003 (DaThamGia=1),
--                         SV2022004 (DaThamGia=0)           (minh hoa toggle)
--          * SV2022005 (ThoiHoc) KHONG xuat hien - chung minh auto-add chi lay DangHoc.
--      Dot 3 (CNTT-K23A): SV2023001, SV2023002, SV2023003   (3 SV, ca 3 DaThamGia=1)
--          * SV2023004 (BaoLuu) KHONG xuat hien - chung minh auto-add chi lay DangHoc.
-- =============================================================================
INSERT INTO DanhSachSinhVienKienTap (MaDotKT, MaSV, DaThamGia) VALUES
(1, 'SV2022001', 1),
(1, 'SV2022002', 1),
(2, 'SV2022003', 1),
(2, 'SV2022004', 0),
(3, 'SV2023001', 1),
(3, 'SV2023002', 1),
(3, 'SV2023003', 1);

-- =============================================================================
-- 18. DotThucTap  <- CTDT_HocPhan, HocKyNamHoc, NguoiDung
--    Business (docs/02 §3.8): MaHocPhan phai co LoaiHocPhan IN ('ThucTap','KienTap').
--    Seed:
--      1. K22 Thuc Tap cuoi khoa - HK1-2024 - DangThucHien
--      2. K23 Thuc Tap - HK2-2024 - ChuanBi
-- =============================================================================
INSERT INTO DotThucTap
    (MaDotTT, TenDotTT, MaCTDT, MaHocPhan, MaHocKy,
     NgayBatDau, NgayKetThuc, FileMinhChung, TrangThai,
     NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Thuc Tap Cuoi Khoa K22 - Dot 1 - 2024-2025',
    'CTDT-CNTT-2022', 'HP-TT', 'HK1-2024',
    '2024-11-01', '2025-01-05',
    'thuctap/DotTT_1_ke_hoach.pdf',
    'DangThucHien', 'GV001', 'GV002', '2024-10-15 10:00:00'),
(2, 'Thuc Tap Cuoi Khoa K23 - Dot 1 - 2025',
    'CTDT-CNTT-2023', 'HP-TT', 'HK2-2024',
    '2025-03-01', '2025-05-20',
    NULL,
    'ChuanBi', 'GV001', NULL, NULL);

-- =============================================================================
-- 19. DanhSachThucTap  <- DotThucTap, SinhVien, DoanhNghiep
--    Business (docs/02 §3.8): UNIQUE (MaDotTT, MaSV); MaDoanhNghiep bat buoc
--    neu LoaiThucTap='DoanhNghiep'.
-- =============================================================================
INSERT INTO DanhSachThucTap
    (MaThucTap, MaDotTT, MaSV, LoaiThucTap, MaDoanhNghiep, TrangThai) VALUES
-- Dot 1 (K22): 4 SV di thuc tap DN
(1, 1, 'SV2022001', 'DoanhNghiep', 'DN001', 'DangThucTap'),
(2, 1, 'SV2022002', 'DoanhNghiep', 'DN002', 'DangThucTap'),
(3, 1, 'SV2022003', 'DoanhNghiep', 'DN003', 'DangThucTap'),
(4, 1, 'SV2022004', 'Truong',      NULL,    'DangThucTap');  -- thuc tap tai truong

-- =============================================================================
-- 20. KetQuaThucTap  <- DanhSachThucTap, VaiTroThucTap, GiangVien
--    Business (docs/02 §3.9):
--      - Diem: 0.00 - 10.00
--      - MaNguoiDanhGia la GV (chi GV nhap ket qua trong he thong)
--      - Moi (MaThucTap, MaVaiTro): 1 ban ghi duy nhat
--    Seed minh hoa da chieu danh gia: SV2022001 co danh gia tu GV + DN (2 ban ghi).
-- =============================================================================
INSERT INTO KetQuaThucTap (MaThucTap, MaVaiTro, MaNguoiDanhGia, Diem, NhanXet) VALUES
-- SV2022001 (tai FPT): GV huong dan + DN danh gia
(1, 'GV', 'GV001', 8.50, 'Sinh vien hoan thanh tot nhiem vu, tu giac va co trach nhiem.'),
(1, 'DN', 'GV001', 9.00, 'Nhan vien thuc tap xuat sac, kha nang lam viec nhom va doc lap cao.'),
-- SV2022002 (tai VNG): GV da cham, DN chua
(2, 'GV', 'GV001', 7.50, 'Sinh vien can chu dong hon trong viec tim hieu cong viec.'),
-- SV2022003 (tai TMA): chi moi co GV cham
(3, 'GV', 'GV001', 8.00, 'Hoan thanh tot cac nhiem vu, tinh than hoc hoi cao.');

-- =============================================================================
-- KIEM TRA NHANH SAU KHI CHAY
-- =============================================================================
-- Chay tung dong duoi day de xac nhan dung so luong ban ghi:
-- SELECT COUNT(*) AS HocKyNamHoc      FROM HocKyNamHoc;              -- 4
-- SELECT COUNT(*) AS NguoiDung        FROM NguoiDung;                -- 20
-- SELECT COUNT(*) AS GiangVien        FROM GiangVien;                -- 6
-- SELECT COUNT(*) AS DoanhNghiep      FROM DoanhNghiep;              -- 4
-- SELECT COUNT(*) AS VaiTroThucTap    FROM VaiTroThucTap;            -- 4
-- SELECT COUNT(*) AS NhomNguoiDung    FROM NhomNguoiDung;            -- 10
-- SELECT COUNT(*) AS CTDT             FROM ChuongTrinhDaoTao;        -- 3
-- SELECT COUNT(*) AS BCN              FROM BCN_ThanhVien;            -- 9
-- SELECT COUNT(*) AS HocPhan          FROM HocPhan;                  -- 10
-- SELECT COUNT(*) AS DoiNguGV         FROM DoiNguGiangVienHP;        -- 25
-- SELECT COUNT(*) AS LopHC            FROM LopHanhChinh;             -- 4
-- SELECT COUNT(*) AS SinhVien         FROM SinhVien;                 -- 12 (10 DangHoc + 1 BaoLuu + 1 ThoiHoc)
-- SELECT COUNT(*) AS SV_DangHoc       FROM SinhVien WHERE TrangThaiSV='DangHoc'; -- 10
-- SELECT COUNT(*) AS DSKT_ThamGia     FROM DanhSachSinhVienKienTap WHERE DaThamGia=1; -- 6
-- SELECT COUNT(*) AS DSKT_KhongThamGia FROM DanhSachSinhVienKienTap WHERE DaThamGia=0; -- 1
-- SELECT COUNT(*) AS CTDT_HP          FROM CTDT_HocPhan;             -- 18
-- SELECT COUNT(*) AS LopHocPhan       FROM LopHocPhan;               -- 12
-- SELECT COUNT(*) AS DSSV_LHP         FROM DanhSachSinhVienLopHocPhan; -- 15
-- SELECT COUNT(*) AS DotKienTap       FROM DotKienTap;               -- 3
-- SELECT COUNT(*) AS DSSV_KT          FROM DanhSachSinhVienKienTap;  -- 7
-- SELECT COUNT(*) AS DotThucTap       FROM DotThucTap;               -- 2
-- SELECT COUNT(*) AS DanhSachTT       FROM DanhSachThucTap;          -- 4
-- SELECT COUNT(*) AS KetQuaTT         FROM KetQuaThucTap;            -- 4

-- =============================================================================
-- TAI KHOAN TEST (MatKhau: Password@123)
-- =============================================================================
-- TenDangNhap    LoaiNguoiDung   VaiTro NhomNguoiDung   Ghi chu
-- admin          Admin           -                       Toan quyen he thong
-- tran.van.an    GiangVien       PDT + TTDTXS            Truong Phong Dao Tao + TTDTXS
-- le.thi.binh    GiangVien       TTDTXS                  Thanh vien TT Dao Tao Xuat Sac
-- nguyen.cuong   GiangVien       CVHT                    CVHT CNTT-K22A
-- pham.dung      GiangVien       CVHT + CNHP             CVHT CNTT-K22B, CNHP HP-LTW
-- hoang.em       GiangVien       CVHT + CNHP             CVHT CNTT-K23A, CNHP HP-CSDL + HP-TTDL
-- vu.thi.giang   GiangVien       CVHT + CNHP             CVHT CNTT-K24A, CNHP HP-OOP + HP-AI
-- sv.2024001     SinhVien        -                       CNTT-K24A, DangHoc
-- sv.2024002     SinhVien        -                       CNTT-K24A, DangHoc
-- sv.2024003     SinhVien        -                       CNTT-K24A, DangHoc
-- sv.2023001     SinhVien        -                       CNTT-K23A, DangHoc
-- sv.2022001     SinhVien        -                       CNTT-K22A, DangThucTap (Dot 1 @FPT)
-- sv.2022003     SinhVien        -                       CNTT-K22B, DaCanhBao HP-OOP (da xu ly)
-- sv.2022004     SinhVien        -                       CNTT-K22B, Dot KT 2 - DaThamGia=0 (minh hoa toggle)
-- sv.2022005     SinhVien        -                       CNTT-K22B, ThoiHoc - KHONG auto-add vao Dot KT
-- sv.2023004     SinhVien        -                       CNTT-K23A, BaoLuu  - KHONG auto-add vao Dot KT
-- dn.fpt         DoanhNghiep     -                       FPT Software, DangHopTac
-- dn.vng         DoanhNghiep     -                       VNG Corporation, DangHopTac

-- =============================================================================
-- HET SEED
-- =============================================================================
