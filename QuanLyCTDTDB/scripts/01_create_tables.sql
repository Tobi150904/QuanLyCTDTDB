-- ============================================================
-- DATABASE: QuanLyCTDTDB
-- MÔ TẢ: Hệ thống Quản lý Vận hành Chương trình Đào tạo Đặc biệt
-- PHIÊN BẢN: Final - Đồng bộ Entity & Workflow
-- CHARSET: utf8mb4
-- ENGINE: InnoDB
-- ============================================================

DROP DATABASE IF EXISTS QuanLyCTDTDB;
CREATE DATABASE QuanLyCTDTDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE QuanLyCTDTDB;

SET FOREIGN_KEY_CHECKS = 0;

-- ------------------------------------------------------------
-- 1. HocKyNamHoc
-- ------------------------------------------------------------
DROP TABLE IF EXISTS HocKyNamHoc;
CREATE TABLE HocKyNamHoc (
    MaHocKy VARCHAR(20) PRIMARY KEY COMMENT 'Mã học kỳ (VD: K64-HK1)',
    TenHocKy VARCHAR(50) NOT NULL COMMENT 'Tên học kỳ',
    NgayBatDau DATE NOT NULL COMMENT 'Ngày bắt đầu',
    NgayKetThuc DATE NOT NULL COMMENT 'Ngày kết thúc',
    TrangThai ENUM('SapDienRa','DangDienRa','DaKetThuc') DEFAULT 'SapDienRa' COMMENT 'Trạng thái học kỳ',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_trangthai (TrangThai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. ChuongTrinhDaoTao
-- ------------------------------------------------------------
DROP TABLE IF EXISTS ChuongTrinhDaoTao;
CREATE TABLE ChuongTrinhDaoTao (
    MaCTDT VARCHAR(20) PRIMARY KEY COMMENT 'Mã CTĐT (VD: CNTT-CLC-K64)',
    TenCTDT VARCHAR(200) NOT NULL COMMENT 'Tên chương trình',
    Khoa VARCHAR(20) COMMENT 'Khóa áp dụng (VD: K64)',
    FileWord VARCHAR(255) COMMENT 'Đường dẫn file Word CTĐT (tham khảo)',
    TrangThai ENUM('BanNhap','ChoDuyet','DaDuyet','DaHuy') DEFAULT 'BanNhap' COMMENT 'Trạng thái phê duyệt',
    NguoiTao VARCHAR(20) NOT NULL COMMENT 'Người tạo (BCN)',
    NguoiDuyet VARCHAR(20) COMMENT 'Người duyệt (TTĐTSX/PDT)',
    NgayDuyet DATETIME COMMENT 'Ngày duyệt',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (NguoiTao) REFERENCES NguoiDung(MaNguoiDung) ON DELETE RESTRICT,
    FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    INDEX idx_trangthai (TrangThai),
    INDEX idx_khoa (Khoa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. LopHanhChinh
-- ------------------------------------------------------------
DROP TABLE IF EXISTS LopHanhChinh;
CREATE TABLE LopHanhChinh (
    MaLopHC VARCHAR(20) PRIMARY KEY COMMENT 'Mã lớp hành chính (VD: 64.CNTT-CLC)',
    TenLop VARCHAR(100) NOT NULL COMMENT 'Tên lớp',
    MaCTDT VARCHAR(20) COMMENT 'Mã CTĐT áp dụng',
    KhoaHoc VARCHAR(20) COMMENT 'Khóa học (VD: K64)',
    MaCoVan VARCHAR(20) COMMENT 'Mã cố vấn học tập',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaCTDT) REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE SET NULL,
    FOREIGN KEY (MaCoVan) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    INDEX idx_khoa (KhoaHoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. DoanhNghiep
-- ------------------------------------------------------------
DROP TABLE IF EXISTS DoanhNghiep;
CREATE TABLE DoanhNghiep (
    MaDoanhNghiep VARCHAR(20) PRIMARY KEY COMMENT 'Mã doanh nghiệp',
    TenDoanhNghiep VARCHAR(200) NOT NULL COMMENT 'Tên doanh nghiệp',
    LinhVuc VARCHAR(200) COMMENT 'Lĩnh vực hoạt động',
    NguoiDaiDien VARCHAR(100) COMMENT 'Người đại diện',
    Email VARCHAR(100) COMMENT 'Email liên hệ',
    SoDienThoai VARCHAR(15) COMMENT 'Số điện thoại',
    DiaChiDN VARCHAR(255) COMMENT 'Địa chỉ doanh nghiệp',
    TrangThai ENUM('DangHopTac','TamNgung') DEFAULT 'DangHopTac' COMMENT 'Trạng thái hợp tác',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_trangthai (TrangThai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 5. NguoiDung
-- ------------------------------------------------------------
DROP TABLE IF EXISTS NguoiDung;
CREATE TABLE NguoiDung (
    MaNguoiDung VARCHAR(20) PRIMARY KEY COMMENT 'Mã định danh duy nhất',
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE COMMENT 'Tên đăng nhập',
    MatKhauHash VARCHAR(255) NOT NULL COMMENT 'Mật khẩu đã băm',
    Email VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email',
    HoTen VARCHAR(100) NOT NULL COMMENT 'Họ tên',
    SoDienThoai VARCHAR(15) COMMENT 'Số điện thoại',
    MaLopHC VARCHAR(20) COMMENT 'Mã lớp hành chính (dành cho SV)',
    TrangThaiSV ENUM('DangHoc','BaoLuu','ThoiHoc','TotNghiep') DEFAULT 'DangHoc' COMMENT 'Trạng thái sinh viên',
    HocHam VARCHAR(50) COMMENT 'Học hàm (GS, PGS,...)',
    HocVi VARCHAR(50) COMMENT 'Học vị (TS, ThS, CN,...)',
    ChuyenNganh VARCHAR(200) COMMENT 'Chuyên ngành (dành cho GV)',
    TrangThaiTK BIT DEFAULT 1 COMMENT '1: Hoạt động, 0: Khóa',
    MaDoanhNghiep VARCHAR(20) COMMENT 'Mã doanh nghiệp (dành cho tài khoản DN)',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaLopHC) REFERENCES LopHanhChinh(MaLopHC) ON DELETE SET NULL,
    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep) ON DELETE SET NULL,
    INDEX idx_email (Email),
    INDEX idx_tendangnhap (TenDangNhap),
    INDEX idx_maloPhc (MaLopHC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 6. NguoiDung_VaiTro
-- ------------------------------------------------------------
DROP TABLE IF EXISTS NguoiDung_VaiTro;
CREATE TABLE NguoiDung_VaiTro (
    MaNguoiDung VARCHAR(20) NOT NULL,
    VaiTro ENUM('SV','GV','CVHT','BCN','CNHP','PDT','TTDTXS','DN') NOT NULL COMMENT 'Vai trò',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaNguoiDung, VaiTro),
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    INDEX idx_vaitro (VaiTro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 7. HocPhan
-- ------------------------------------------------------------
DROP TABLE IF EXISTS HocPhan;
CREATE TABLE HocPhan (
    MaHocPhan VARCHAR(20) PRIMARY KEY COMMENT 'Mã học phần (VD: MAT322)',
    TenHocPhan VARCHAR(200) NOT NULL COMMENT 'Tên học phần',
    SoTinChi INT NOT NULL COMMENT 'Số tín chỉ',
    ChuNhiemHP VARCHAR(20) NOT NULL COMMENT 'Chủ nhiệm học phần',
    FileDeCuong VARCHAR(255) COMMENT 'Đường dẫn file đề cương gốc (PDF)',
    TrangThai ENUM('BanNhap','ChoDuyet','DaDuyet') DEFAULT 'BanNhap' COMMENT 'Trạng thái phê duyệt',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (ChuNhiemHP) REFERENCES NguoiDung(MaNguoiDung) ON DELETE RESTRICT,
    INDEX idx_trangthai (TrangThai),
    CHECK (SoTinChi BETWEEN 2 AND 6)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 8. DoiNguGiangVienHP
-- ------------------------------------------------------------
DROP TABLE IF EXISTS DoiNguGiangVienHP;
CREATE TABLE DoiNguGiangVienHP (
    MaHocPhan VARCHAR(20) NOT NULL,
    MaGiangVien VARCHAR(20) NOT NULL,
    TrangThai BIT DEFAULT 1 COMMENT '1: Còn trong đội ngũ, 0: Đã rời',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaHocPhan, MaGiangVien),
    FOREIGN KEY (MaHocPhan) REFERENCES HocPhan(MaHocPhan) ON DELETE CASCADE,
    FOREIGN KEY (MaGiangVien) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    INDEX idx_trangthai (TrangThai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 9. LopHocPhan
-- ------------------------------------------------------------
DROP TABLE IF EXISTS LopHocPhan;
CREATE TABLE LopHocPhan (
    MaHocPhan VARCHAR(20) NOT NULL COMMENT 'Mã học phần',
    MaHocKy VARCHAR(20) NOT NULL COMMENT 'Mã học kỳ',
    MaLopHC VARCHAR(20) NOT NULL COMMENT 'Mã lớp hành chính',
    NhomHocPhan INT NOT NULL UNIQUE COMMENT 'Nhóm học phần (số định danh duy nhất)',
    MaGiangVien VARCHAR(20) COMMENT 'Giảng viên giảng dạy',
    SiSoToiDa INT NOT NULL COMMENT 'Sĩ số tối đa',
    SiSoThucTe INT DEFAULT 0 COMMENT 'Sĩ số thực tế',
    TrangThai ENUM('DangMo','DaDong','DaHuy') DEFAULT 'DangMo' COMMENT 'Trạng thái lớp',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaHocPhan, MaHocKy, MaLopHC),
    FOREIGN KEY (MaHocPhan) REFERENCES HocPhan(MaHocPhan) ON DELETE RESTRICT,
    FOREIGN KEY (MaHocKy) REFERENCES HocKyNamHoc(MaHocKy) ON DELETE RESTRICT,
    FOREIGN KEY (MaLopHC) REFERENCES LopHanhChinh(MaLopHC) ON DELETE RESTRICT,
    FOREIGN KEY (MaGiangVien) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    INDEX idx_trangthai (TrangThai),
    INDEX idx_magiangvien (MaGiangVien),
    INDEX idx_nhomhocphan (NhomHocPhan),
    CHECK (SiSoToiDa BETWEEN 30 AND 60)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 10. TaiLieuMonHoc
-- ------------------------------------------------------------
DROP TABLE IF EXISTS TaiLieuMonHoc;
CREATE TABLE TaiLieuMonHoc (
    MaTaiLieu INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã tài liệu',
    MaHocPhan VARCHAR(20) NOT NULL,
    MaHocKy VARCHAR(20) NOT NULL,
    MaLopHC VARCHAR(20) NOT NULL,
    Loai ENUM('DeCuongChiTiet','DeThiGiuaKy','DeThiCuoiKy') NOT NULL COMMENT 'Loại tài liệu',
    FileDinhKem VARCHAR(255) NOT NULL COMMENT 'Đường dẫn file',
    NgayNop DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Ngày nộp',
    TrangThai ENUM('ChoDuyet','DaDuyet','TuChoi') DEFAULT 'ChoDuyet' COMMENT 'Trạng thái duyệt',
    NguoiDuyet VARCHAR(20) COMMENT 'Người duyệt (CNHP)',
    NgayDuyet DATETIME COMMENT 'Ngày duyệt',
    NhanXet TEXT COMMENT 'Nhận xét của người duyệt',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaHocPhan, MaHocKy, MaLopHC) REFERENCES LopHocPhan(MaHocPhan, MaHocKy, MaLopHC) ON DELETE CASCADE,
    FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    UNIQUE KEY uk_taidieu_lophp_loai (MaHocPhan, MaHocKy, MaLopHC, Loai),
    INDEX idx_trangthai (TrangThai),
    INDEX idx_loai (Loai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 11. DanhGiaVaCanhBao
-- ------------------------------------------------------------
DROP TABLE IF EXISTS DanhGiaVaCanhBao;
CREATE TABLE DanhGiaVaCanhBao (
    MaDanhGia INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã nhận xét',
    MaSV VARCHAR(20) NOT NULL COMMENT 'Sinh viên',
    MaHocPhan VARCHAR(20) NULL COMMENT 'Mã học phần (NULL nếu là tổng kết kỳ)',
    MaHocKy VARCHAR(20) NULL COMMENT 'Mã học kỳ (NULL nếu tổng kết kỳ)',
    MaLopHC VARCHAR(20) NULL COMMENT 'Mã lớp HC (NULL nếu tổng kết kỳ)',
    NguoiNhanXet VARCHAR(20) NOT NULL COMMENT 'Người nhận xét (GV/CVHT)',
    LoaiNhanXet ENUM('TichCuc','TieuCuc') NOT NULL COMMENT 'Loại nhận xét',
    LoaiDanhGia ENUM('QuaTrinh','TongKetKy') DEFAULT 'QuaTrinh' NOT NULL COMMENT 'Phân biệt đánh giá quá trình / tổng kết',
    NoiDung TEXT NOT NULL COMMENT 'Nội dung nhận xét',
    DaXuLy BIT DEFAULT 0 COMMENT '0: Chưa xử lý, 1: CVHT đã xử lý',
    KetQuaXuLy TEXT COMMENT 'Kết quả tư vấn của CVHT',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaSV) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    FOREIGN KEY (MaHocPhan, MaHocKy, MaLopHC) REFERENCES LopHocPhan(MaHocPhan, MaHocKy, MaLopHC) ON DELETE CASCADE,
    FOREIGN KEY (NguoiNhanXet) REFERENCES NguoiDung(MaNguoiDung) ON DELETE RESTRICT,
    INDEX idx_masv (MaSV),
    INDEX idx_daxuly (DaXuLy),
    INDEX idx_loai (LoaiNhanXet),
    INDEX idx_loaidanhgia (LoaiDanhGia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 12. DotKienTap
-- ------------------------------------------------------------
DROP TABLE IF EXISTS DotKienTap;
CREATE TABLE DotKienTap (
    MaDotKT INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã đợt kiến tập',
    TenDotKT VARCHAR(200) NOT NULL COMMENT 'Tên đợt',
    MaLopHC VARCHAR(20) NOT NULL COMMENT 'Lớp hành chính tham gia',
    MaHocKy VARCHAR(20) NOT NULL COMMENT 'Học kỳ tổ chức',
    ThoiGian DATE COMMENT 'Ngày tổ chức',
    MaGVPhuTrach VARCHAR(20) COMMENT 'Giảng viên phụ trách',
    MaDoanhNghiep VARCHAR(20) NOT NULL COMMENT 'Doanh nghiệp tiếp đón',
    NhanXetGV TEXT COMMENT 'Nhận xét của GV',
    NhanXetDN TEXT COMMENT 'Nhận xét của DN',
    FileMinhChung VARCHAR(255) COMMENT 'File minh chứng (kế hoạch, công văn)',
    TrangThai ENUM('ChuanBi','ChoDuyet','DaDuyet','DaThucHien','DaHuy') DEFAULT 'ChuanBi' COMMENT 'Trạng thái',
    NguoiTao VARCHAR(20) NOT NULL COMMENT 'Người tạo (BCN)',
    NguoiDuyet VARCHAR(20) COMMENT 'Người duyệt (TTĐTSX)',
    NgayDuyet DATETIME COMMENT 'Ngày duyệt',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaLopHC) REFERENCES LopHanhChinh(MaLopHC) ON DELETE RESTRICT,
    FOREIGN KEY (MaHocKy) REFERENCES HocKyNamHoc(MaHocKy) ON DELETE RESTRICT,
    FOREIGN KEY (MaGVPhuTrach) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep) ON DELETE RESTRICT,
    FOREIGN KEY (NguoiTao) REFERENCES NguoiDung(MaNguoiDung) ON DELETE RESTRICT,
    FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    INDEX idx_trangthai (TrangThai),
    INDEX idx_maloPhc (MaLopHC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 13. DotThucTap
-- ------------------------------------------------------------
DROP TABLE IF EXISTS DotThucTap;
CREATE TABLE DotThucTap (
    MaDotTT INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã đợt thực tập',
    TenDotTT VARCHAR(200) NOT NULL COMMENT 'Tên đợt',
    MaHocKy VARCHAR(20) NOT NULL COMMENT 'Học kỳ',
    NgayBatDau DATE COMMENT 'Ngày bắt đầu',
    NgayKetThuc DATE COMMENT 'Ngày kết thúc',
    FileMinhChung VARCHAR(255) COMMENT 'File minh chứng (quyết định, kế hoạch)',
    TrangThai ENUM('ChuanBi','ChoDuyet','DaDuyet','DangThucHien','DaKetThuc') DEFAULT 'ChuanBi' COMMENT 'Trạng thái',
    NguoiTao VARCHAR(20) NOT NULL COMMENT 'Người tạo (BCN/PDT)',
    NguoiDuyet VARCHAR(20) COMMENT 'Người duyệt (TTĐTSX)',
    NgayDuyet DATETIME COMMENT 'Ngày duyệt',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaHocKy) REFERENCES HocKyNamHoc(MaHocKy) ON DELETE RESTRICT,
    FOREIGN KEY (NguoiTao) REFERENCES NguoiDung(MaNguoiDung) ON DELETE RESTRICT,
    FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    INDEX idx_trangthai (TrangThai),
    CHECK (NgayKetThuc >= NgayBatDau)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 14. PhanCongThucTap
-- ------------------------------------------------------------
DROP TABLE IF EXISTS PhanCongThucTap;
CREATE TABLE PhanCongThucTap (
    MaThucTap INT AUTO_INCREMENT PRIMARY KEY COMMENT 'Mã phân công',
    MaDotTT INT NOT NULL COMMENT 'Đợt thực tập',
    MaSV VARCHAR(20) NOT NULL COMMENT 'Sinh viên',
    MaDoanhNghiep VARCHAR(20) NOT NULL COMMENT 'Doanh nghiệp tiếp nhận',
    MaGiangVienGiamSat VARCHAR(20) COMMENT 'Giảng viên giám sát',
    DiemDN DECIMAL(4,2) COMMENT 'Điểm doanh nghiệp (thang 10)',
    NhanXetDN TEXT COMMENT 'Nhận xét của doanh nghiệp',
    DiemGV DECIMAL(4,2) COMMENT 'Điểm giảng viên (thang 10)',
    NhanXetGV TEXT COMMENT 'Nhận xét của giảng viên',
    NhanXetSV TEXT COMMENT 'Nhận xét cảm nhận của sinh viên',
    TrangThai ENUM('DaPhanCong','DangThucTap','DaKetThuc','DaHuy') DEFAULT 'DaPhanCong' COMMENT 'Trạng thái phân công',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaDotTT) REFERENCES DotThucTap(MaDotTT) ON DELETE CASCADE,
    FOREIGN KEY (MaSV) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep) ON DELETE RESTRICT,
    FOREIGN KEY (MaGiangVienGiamSat) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    UNIQUE KEY uk_thuctap_dottc_sv (MaDotTT, MaSV),
    INDEX idx_trangthai (TrangThai),
    INDEX idx_masv (MaSV),
    CHECK (DiemDN IS NULL OR (DiemDN >= 0 AND DiemDN <= 10)),
    CHECK (DiemGV IS NULL OR (DiemGV >= 0 AND DiemGV <= 10))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 15. CTDT_HocPhan
-- ------------------------------------------------------------
DROP TABLE IF EXISTS CTDT_HocPhan;
CREATE TABLE CTDT_HocPhan (
    MaCTDT VARCHAR(20) NOT NULL COMMENT 'Mã CTĐT',
    MaHocPhan VARCHAR(20) NOT NULL COMMENT 'Mã học phần',
    HocKyThu INT NOT NULL COMMENT 'Học kỳ thứ mấy trong khóa học',
    SoLopDuKien INT DEFAULT 1 COMMENT 'Số lớp dự kiến mở',
    BatBuoc BIT DEFAULT 1 COMMENT '1: Bắt buộc, 0: Tự chọn',
    GhiChu VARCHAR(255) COMMENT 'Ghi chú',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaHocPhan),
    FOREIGN KEY (MaCTDT) REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE CASCADE,
    FOREIGN KEY (MaHocPhan) REFERENCES HocPhan(MaHocPhan) ON DELETE RESTRICT,
    INDEX idx_hockythu (HocKyThu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;