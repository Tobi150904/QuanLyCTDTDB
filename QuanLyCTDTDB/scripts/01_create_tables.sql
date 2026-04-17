-- ============================================================
-- DATABASE: QuanLyCTDTDB (Phiên bản 15 bảng - HOÀN CHỈNH)
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
    MaHocKy VARCHAR(20) PRIMARY KEY,
    TenHocKy VARCHAR(50) NOT NULL,
    NgayBatDau DATE NOT NULL,
    NgayKetThuc DATE NOT NULL,
    TrangThai ENUM('SapDienRa','DangDienRa','DaKetThuc') DEFAULT 'SapDienRa',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_trangthai (TrangThai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. ChuongTrinhDaoTao
-- ------------------------------------------------------------
DROP TABLE IF EXISTS ChuongTrinhDaoTao;
CREATE TABLE ChuongTrinhDaoTao (
    MaCTDT VARCHAR(20) PRIMARY KEY,
    TenCTDT VARCHAR(200) NOT NULL,
    Khoa VARCHAR(20),
    FileWord VARCHAR(255),
    TrangThai ENUM('BanNhap','ChoDuyet','DaDuyet','DaHuy') DEFAULT 'BanNhap',
    NguoiTao VARCHAR(20) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    NguoiDuyet VARCHAR(20),
    NgayDuyet DATETIME,
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
    MaLopHC VARCHAR(20) PRIMARY KEY,
    TenLop VARCHAR(100) NOT NULL,
    MaCTDT VARCHAR(20),
    KhoaHoc VARCHAR(20),
    MaCoVan VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaCTDT) REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE SET NULL,
    FOREIGN KEY (MaCoVan) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    INDEX idx_khoa (KhoaHoc)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. NguoiDung
-- ------------------------------------------------------------
DROP TABLE IF EXISTS NguoiDung;
CREATE TABLE NguoiDung (
    MaNguoiDung VARCHAR(20) PRIMARY KEY,
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    MatKhauHash VARCHAR(255) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    HoTen VARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15),
    MaLopHC VARCHAR(20),
    TrangThaiSV ENUM('DangHoc','BaoLuu','ThoiHoc','TotNghiep') DEFAULT 'DangHoc',
    HocHam VARCHAR(50),
    HocVi VARCHAR(50),
    ChuyenNganh VARCHAR(200),
    TrangThaiTK BIT DEFAULT 1,
    MaDoanhNghiep VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaLopHC) REFERENCES LopHanhChinh(MaLopHC) ON DELETE SET NULL,
    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep) ON DELETE SET NULL,
    INDEX idx_email (Email),
    INDEX idx_tendangnhap (TenDangNhap),
    INDEX idx_maloPhc (MaLopHC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 5. NguoiDung_VaiTro
-- ------------------------------------------------------------
DROP TABLE IF EXISTS NguoiDung_VaiTro;
CREATE TABLE NguoiDung_VaiTro (
    MaNguoiDung VARCHAR(20) NOT NULL,
    VaiTro ENUM('SV','GV','CVHT','BCN','CNHP','PDT','TTDTXS','DN') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaNguoiDung, VaiTro),
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    INDEX idx_vaitro (VaiTro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 6. DoanhNghiep
-- ------------------------------------------------------------
DROP TABLE IF EXISTS DoanhNghiep;
CREATE TABLE DoanhNghiep (
    MaDoanhNghiep VARCHAR(20) PRIMARY KEY,
    TenDoanhNghiep VARCHAR(200) NOT NULL,
    LinhVuc VARCHAR(200),
    NguoiDaiDien VARCHAR(100),
    Email VARCHAR(100),
    SoDienThoai VARCHAR(15),
    TrangThai ENUM('DangHopTac','TamNgung') DEFAULT 'DangHopTac',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_trangthai (TrangThai)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 7. HocPhan
-- ------------------------------------------------------------
DROP TABLE IF EXISTS HocPhan;
CREATE TABLE HocPhan (
    MaHocPhan VARCHAR(20) PRIMARY KEY,
    TenHocPhan VARCHAR(200) NOT NULL,
    SoTinChi INT NOT NULL,
    ChuNhiemHP VARCHAR(20) NOT NULL,
    FileDeCuong VARCHAR(255),
    TrangThai ENUM('BanNhap','ChoDuyet','DaDuyet') DEFAULT 'BanNhap',
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
    TrangThai BIT DEFAULT 1,
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
    MaHocPhan VARCHAR(20) NOT NULL,
    MaHocKy VARCHAR(20) NOT NULL,
    MaLopHC VARCHAR(20) NOT NULL,
    NhomHocPhan INT NOT NULL UNIQUE,
    MaGiangVien VARCHAR(20),
    SiSoToiDa INT NOT NULL,
    SiSoThucTe INT DEFAULT 0,
    TrangThai ENUM('DangMo','DaDong','DaHuy') DEFAULT 'DangMo',
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
    MaTaiLieu INT AUTO_INCREMENT PRIMARY KEY,
    MaHocPhan VARCHAR(20) NOT NULL,
    MaHocKy VARCHAR(20) NOT NULL,
    MaLopHC VARCHAR(20) NOT NULL,
    Loai ENUM('DeCuongChiTiet','DeThiGiuaKy','DeThiCuoiKy') NOT NULL,
    FileDinhKem VARCHAR(255) NOT NULL,
    NgayNop DATETIME DEFAULT CURRENT_TIMESTAMP,
    TrangThai ENUM('ChoDuyet','DaDuyet','TuChoi') DEFAULT 'ChoDuyet',
    NguoiDuyet VARCHAR(20),
    NgayDuyet DATETIME,
    NhanXet TEXT,
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
    MaDanhGia INT AUTO_INCREMENT PRIMARY KEY,
    MaSV VARCHAR(20) NOT NULL,
    MaHocPhan VARCHAR(20) NULL,
    MaHocKy VARCHAR(20) NULL,
    MaLopHC VARCHAR(20) NULL,
    NguoiNhanXet VARCHAR(20) NOT NULL,
    LoaiNhanXet ENUM('TichCuc','TieuCuc') NOT NULL,
    LoaiDanhGia ENUM('QuaTrinh','TongKetKy') DEFAULT 'QuaTrinh' NOT NULL,
    NoiDung TEXT NOT NULL,
    DaXuLy BIT DEFAULT 0,
    KetQuaXuLy TEXT,
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
    MaDotKT INT AUTO_INCREMENT PRIMARY KEY,
    TenDotKT VARCHAR(200) NOT NULL,
    MaLopHC VARCHAR(20) NOT NULL,
    MaHocKy VARCHAR(20) NOT NULL,
    ThoiGian DATE,
    MaGVPhuTrach VARCHAR(20),
    MaDoanhNghiep VARCHAR(20) NOT NULL,
    NhanXetGV TEXT,
    NhanXetDN TEXT,
    FileMinhChung VARCHAR(255),
    TrangThai ENUM('ChuanBi','ChoDuyet','DaDuyet','DaThucHien','DaHuy') DEFAULT 'ChuanBi',
    NguoiDuyet VARCHAR(20),
    NgayDuyet DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaLopHC) REFERENCES LopHanhChinh(MaLopHC) ON DELETE RESTRICT,
    FOREIGN KEY (MaHocKy) REFERENCES HocKyNamHoc(MaHocKy) ON DELETE RESTRICT,
    FOREIGN KEY (MaGVPhuTrach) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep) ON DELETE RESTRICT,
    FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    INDEX idx_trangthai (TrangThai),
    INDEX idx_maloPhc (MaLopHC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 13. DotThucTap
-- ------------------------------------------------------------
DROP TABLE IF EXISTS DotThucTap;
CREATE TABLE DotThucTap (
    MaDotTT INT AUTO_INCREMENT PRIMARY KEY,
    TenDotTT VARCHAR(200) NOT NULL,
    MaHocKy VARCHAR(20) NOT NULL,
    NgayBatDau DATE,
    NgayKetThuc DATE,
    FileMinhChung VARCHAR(255),
    TrangThai ENUM('ChuanBi','ChoDuyet','DaDuyet','DangThucHien','DaKetThuc') DEFAULT 'ChuanBi',
    NguoiDuyet VARCHAR(20),
    NgayDuyet DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaHocKy) REFERENCES HocKyNamHoc(MaHocKy) ON DELETE RESTRICT,
    FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL,
    INDEX idx_trangthai (TrangThai),
    CHECK (NgayKetThuc >= NgayBatDau)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 14. PhanCongThucTap
-- ------------------------------------------------------------
DROP TABLE IF EXISTS PhanCongThucTap;
CREATE TABLE PhanCongThucTap (
    MaThucTap INT AUTO_INCREMENT PRIMARY KEY,
    MaDotTT INT NOT NULL,
    MaSV VARCHAR(20) NOT NULL,
    MaDoanhNghiep VARCHAR(20) NOT NULL,
    MaGiangVienGiamSat VARCHAR(20),
    DiemDN DECIMAL(4,2),
    NhanXetDN TEXT,
    DiemGV DECIMAL(4,2),
    NhanXetGV TEXT,
    NhanXetSV TEXT,
    TrangThai ENUM('DaPhanCong','DangThucTap','DaKetThuc','DaHuy') DEFAULT 'DaPhanCong',
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
    MaCTDT VARCHAR(20) NOT NULL,
    MaHocPhan VARCHAR(20) NOT NULL,
    HocKyThu INT NOT NULL,
    SoLopDuKien INT DEFAULT 1,
    BatBuoc BIT DEFAULT 1,
    GhiChu VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaHocPhan),
    FOREIGN KEY (MaCTDT) REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE CASCADE,
    FOREIGN KEY (MaHocPhan) REFERENCES HocPhan(MaHocPhan) ON DELETE RESTRICT,
    INDEX idx_hockythu (HocKyThu)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;