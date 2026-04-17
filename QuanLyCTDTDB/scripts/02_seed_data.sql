-- ============================================================================
-- SEED DATA: Dữ liệu mẫu phù hợp với cấu trúc mới (Nhóm học phần)
-- ============================================================================

-- Học kỳ
INSERT INTO HocKyNamHoc (MaHocKy, TenHocKy, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('K64-HK1', 'Học kỳ 1 Khóa 64', '2024-09-01', '2024-12-31', 'SapDienRa'),
('K64-HK2', 'Học kỳ 2 Khóa 64', '2025-01-01', '2025-05-31', 'SapDienRa'),
('K64-HK3', 'Học kỳ 3 Khóa 64', '2025-06-01', '2025-08-31', 'SapDienRa');

-- Doanh nghiệp
INSERT INTO DoanhNghiep (MaDoanhNghiep, TenDoanhNghiep, LinhVuc, NguoiDaiDien, Email, SoDienThoai, TrangThai) VALUES
('DN001', 'Công ty Cổ phần FPT Solutions', 'Phần mềm & IT', 'Ông Trần Văn A', 'hr@fpt.com', '0938123456', 'DangHopTac'),
('DN002', 'Viettel Corporation', 'Viễn thông & Công nghệ', 'Ông Nguyễn Văn B', 'contact@viettel.com.vn', '0976543210', 'DangHopTac'),
('DN003', 'Samsung Electronics Vietnam', 'Điện tử & Công nghệ', 'Bà Lê Thị C', 'career@samsung.com.vn', '0934567890', 'DangHopTac'),
('DN004', 'Techcombank', 'Tài chính & Banking', 'Ông Hoàng Văn D', 'recruitment@techcombank.com.vn', '0919876543', 'DangHopTac');

-- Người dùng (PDT, TTDT, BCN, GV, CVHT, DN, SV)
INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, TrangThaiTK) VALUES
('PDT001', 'pdt_admin', '$2a$10$example_hash_pdt', 'pdt@university.edu', 'Trần Văn X - Phòng Đào tạo', 1);
INSERT INTO NguoiDung_VaiTro (MaNguoiDung, VaiTro) VALUES ('PDT001', 'PDT');

INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, TrangThaiTK) VALUES
('TTDT001', 'ttdtxs_admin', '$2a$10$example_hash_ttdt', 'ttdtxs@university.edu', 'Nguyễn Thị Y - Trung tâm ĐTXS', 1);
INSERT INTO NguoiDung_VaiTro (MaNguoiDung, VaiTro) VALUES ('TTDT001', 'TTDTXS');

INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, HocHam, HocVi, TrangThaiTK) VALUES
('BCN001', 'bcn_cntt', '$2a$10$example_hash_bcn', 'bcn.cntt@university.edu', 'PGS.TS Lê Minh Z - BCN CNTT', 'PGS', 'TS', 1),
('BCN002', 'bcn_khmt', '$2a$10$example_hash_bcn2', 'bcn.khmt@university.edu', 'TS Hoàng Văn K - BCN KHMT', 'TS', 'TS', 1);
INSERT INTO NguoiDung_VaiTro (MaNguoiDung, VaiTro) VALUES ('BCN001', 'BCN'), ('BCN002', 'BCN');

INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, HocHam, HocVi, ChuyenNganh, TrangThaiTK) VALUES
('GV001', 'gv_nguyena', '$2a$10$example_hash_gv1', 'nguyen.a@university.edu', 'TS Nguyễn Văn A', 'TS', 'TS', 'Lập trình', 1),
('GV002', 'gv_leb', '$2a$10$example_hash_gv2', 'le.b@university.edu', 'ThS Lê Văn B', 'ThS', 'ThS', 'Lập trình', 1),
('GV003', 'gv_hoangthi', '$2a$10$example_hash_gv3', 'hoang.thi@university.edu', 'ThS Hoàng Thị C', 'ThS', 'ThS', 'Cơ sở dữ liệu', 1),
('GV004', 'gv_dangvan', '$2a$10$example_hash_gv4', 'dang.van@university.edu', 'TS Đặng Văn D', 'TS', 'TS', 'Web & Cloud', 1),
('GV005', 'gv_nguyenthi', '$2a$10$example_hash_gv5', 'nguyen.e@university.edu', 'ThS Nguyễn Thị E', 'ThS', 'ThS', 'Cơ sở dữ liệu', 1);
INSERT INTO NguoiDung_VaiTro (MaNguoiDung, VaiTro) VALUES 
('GV001', 'GV'), ('GV001', 'CNHP'),
('GV002', 'GV'),
('GV003', 'GV'), ('GV003', 'CNHP'),
('GV004', 'GV'),
('GV005', 'GV');

INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, HocHam, HocVi, TrangThaiTK) VALUES
('CVHT001', 'cvht_class1', '$2a$10$example_hash_cvht1', 'cvht.cntt.clc@university.edu', 'ThS Trần Minh X - CVHT CNTT-CLC', 'ThS', 'ThS', 1),
('CVHT002', 'cvht_class2', '$2a$10$example_hash_cvht2', 'cvht.cntt.sp@university.edu', 'ThS Ngô Thị Y - CVHT CNTT-SP', 'ThS', 'ThS', 1),
('CVHT003', 'cvht_class3', '$2a$10$example_hash_cvht3', 'cvht.khmt@university.edu', 'ThS Phạm Văn Z - CVHT KHMT', 'ThS', 'ThS', 1);
INSERT INTO NguoiDung_VaiTro (MaNguoiDung, VaiTro) VALUES ('CVHT001', 'CVHT'), ('CVHT002', 'CVHT'), ('CVHT003', 'CVHT');

INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, TrangThaiTK) VALUES
('DN001_ACC', 'dn_fpt', '$2a$10$example_hash_dn1', 'hr@fpt.com', 'FPT Solutions - HR', 1),
('DN002_ACC', 'dn_viettel', '$2a$10$example_hash_dn2', 'contact@viettel.com.vn', 'Viettel - HR', 1),
('DN003_ACC', 'dn_samsung', '$2a$10$example_hash_dn3', 'career@samsung.com.vn', 'Samsung - HR', 1),
('DN004_ACC', 'dn_techcombank', '$2a$10$example_hash_dn4', 'recruitment@techcombank.com.vn', 'Techcombank - HR', 1);
INSERT INTO NguoiDung_VaiTro (MaNguoiDung, VaiTro) VALUES 
('DN001_ACC', 'DN'), ('DN002_ACC', 'DN'), ('DN003_ACC', 'DN'), ('DN004_ACC', 'DN');

-- Lớp hành chính
INSERT INTO LopHanhChinh (MaLopHC, TenLop, MaCTDT, KhoaHoc, MaCoVan) VALUES
('64.CNTT-CLC', 'CNTT Chất lượng cao K64', NULL, 'K64', 'CVHT001'),
('64.HTTT-CLC', 'HTTT Chất lượng cao K64', NULL, 'K64', 'CVHT002'),
('64.KHMT', 'Khoa học máy tính K64', NULL, 'K64', 'CVHT003');

-- Sinh viên (cập nhật MaLopHC theo mã mới)
INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, MaLopHC, TrangThaiSV, TrangThaiTK) VALUES
('SV001', 'sv_trana', '$2a$10$example_hash_sv1', 'tran.a@student.edu', 'Trần Văn A', '64.CNTT-CLC', 'DangHoc', 1),
('SV002', 'sv_nguyenb', '$2a$10$example_hash_sv2', 'nguyen.b@student.edu', 'Nguyễn Thị B', '64.CNTT-CLC', 'DangHoc', 1),
('SV003', 'sv_levic', '$2a$10$example_hash_sv3', 'le.c@student.edu', 'Lê Văn C', '64.CNTT-CLC', 'DangHoc', 1),
('SV004', 'sv_hoangd', '$2a$10$example_hash_sv4', 'hoang.d@student.edu', 'Hoàng Văn D', '64.HTTT-CLC', 'DangHoc', 1),
('SV005', 'sv_phame', '$2a$10$example_hash_sv5', 'pham.e@student.edu', 'Phạm Thị E', '64.KHMT', 'DangHoc', 1);
INSERT INTO NguoiDung_VaiTro (MaNguoiDung, VaiTro) VALUES ('SV001', 'SV'), ('SV002', 'SV'), ('SV003', 'SV'), ('SV004', 'SV'), ('SV005', 'SV');

-- CTĐT
INSERT INTO ChuongTrinhDaoTao (MaCTDT, TenCTDT, Khoa, TrangThai, NguoiTao) VALUES
('CNTT-CLC-K64', 'Công Nghệ Thông Tin - Chất lượng Cao - Khóa 64', 'K64', 'BanNhap', 'BCN001');

-- Học phần
INSERT INTO HocPhan (MaHocPhan, TenHocPhan, SoTinChi, ChuNhiemHP, TrangThai) VALUES
('MAT322', 'Xác suất - Thống kê', 3, 'GV001', 'DaDuyet'),
('CSC101', 'Lập trình cơ bản', 4, 'GV001', 'DaDuyet'),
('CSC102', 'Cấu trúc dữ liệu', 3, 'GV003', 'DaDuyet'),
('CSC103', 'Lập trình hướng đối tượng', 4, 'GV002', 'DaDuyet'),
('CSC201', 'Cơ sở dữ liệu', 3, 'GV003', 'DaDuyet'),
('WEB101', 'Lập trình Web cơ bản', 4, 'GV004', 'DaDuyet');

-- Đội ngũ GV
INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien, TrangThai) VALUES
('MAT322', 'GV001', 1), ('MAT322', 'GV002', 1),
('CSC101', 'GV001', 1), ('CSC101', 'GV002', 1), ('CSC101', 'GV004', 1),
('CSC102', 'GV003', 1), ('CSC102', 'GV005', 1),
('CSC103', 'GV002', 1), ('CSC103', 'GV004', 1),
('CSC201', 'GV003', 1), ('CSC201', 'GV005', 1),
('WEB101', 'GV004', 1), ('WEB101', 'GV002', 1);

-- Cấu trúc CTDT chi tiết
INSERT INTO CTDT_HocPhan (MaCTDT, MaHocPhan, HocKyThu, SoLopDuKien, BatBuoc) VALUES
('CNTT-CLC-K64', 'MAT322', 1, 2, 1),
('CNTT-CLC-K64', 'CSC101', 1, 2, 1),
('CNTT-CLC-K64', 'CSC102', 1, 1, 1),
('CNTT-CLC-K64', 'CSC103', 2, 1, 1),
('CNTT-CLC-K64', 'CSC201', 2, 1, 1),
('CNTT-CLC-K64', 'WEB101', 2, 1, 1);

-- Lớp học phần (theo cấu trúc mới: MaHocPhan, MaHocKy, NhomHocPhan, MaLopHC)
INSERT INTO LopHocPhan (MaHocPhan, MaHocKy, NhomHocPhan, MaLopHC, MaGiangVien, SiSoToiDa, SiSoThucTe, TrangThai) VALUES
-- MAT322: 2 nhóm cho 2 lớp hành chính khác nhau
('MAT322', 'K64-HK1', 9, '64.CNTT-CLC', 'GV001', 70, 60, 'DangMo'),
('MAT322', 'K64-HK1', 15, '64.HTTT-CLC', 'GV002', 70, 59, 'DangMo'),
-- CSC101: 2 nhóm cho cùng 1 lớp hành chính (chia nhóm thực hành)
('CSC101', 'K64-HK1', 1, '64.CNTT-CLC', 'GV002', 45, 45, 'DangMo'),
('CSC101', 'K64-HK1', 2, '64.CNTT-CLC', 'GV004', 45, 40, 'DangMo'),
-- CSC102: 1 nhóm
('CSC102', 'K64-HK1', 1, '64.CNTT-CLC', 'GV003', 50, 48, 'DangMo');