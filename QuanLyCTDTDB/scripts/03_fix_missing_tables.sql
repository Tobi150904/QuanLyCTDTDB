-- =============================================================================
-- 03_fix_missing_tables.sql
-- MIGRATION IDEMPOTENT: chay lai bao nhieu lan cung an toan.
-- Tao bat ky bang nao con thieu trong DB QuanLyCTDTDB.
-- Cach dung: Mo phpMyAdmin (XAMPP) > chon DB QuanLyCTDTDB > tab SQL > paste file nay > Go
-- =============================================================================

USE QuanLyCTDTDB;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------------
-- 1. HocKyNamHoc
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS HocKyNamHoc (
    MaHocKy    VARCHAR(20)  PRIMARY KEY,
    TenHocKy   VARCHAR(100) NOT NULL,
    NamHoc     VARCHAR(20)  NOT NULL,
    NgayBatDau DATE         NOT NULL,
    NgayKetThuc DATE        NOT NULL,
    TrangThai  ENUM('SapDienRa','DangDienRa','DaKetThuc') DEFAULT 'SapDienRa',
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 2. NguoiDung
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS NguoiDung (
    MaNguoiDung   VARCHAR(20)  PRIMARY KEY,
    HoTen         VARCHAR(150) NOT NULL,
    Email         VARCHAR(150) UNIQUE NOT NULL,
    MatKhau       VARCHAR(255) NOT NULL,
    SoDienThoai   VARCHAR(20),
    LoaiNguoiDung ENUM('Admin','GiangVien','SinhVien','DoanhNghiep') NOT NULL,
    TrangThaiHoatDong BOOLEAN DEFAULT TRUE,
    created_at    DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 3. GiangVien
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS GiangVien (
    MaGV          VARCHAR(20) PRIMARY KEY,
    MaNguoiDung   VARCHAR(20) UNIQUE NOT NULL,
    HocVi         VARCHAR(50),
    ChuyenMon     VARCHAR(200),
    LoaiGiangVien ENUM('GiangVienTruong','DoanhNghiep') DEFAULT 'GiangVienTruong',
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 4. DoanhNghiep
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS DoanhNghiep (
    MaDN        VARCHAR(20)  PRIMARY KEY,
    MaNguoiDung VARCHAR(20),
    TenDN       VARCHAR(200) NOT NULL,
    DiaChi      VARCHAR(300),
    NguoiLienHe VARCHAR(100),
    EmailLienHe VARCHAR(150),
    SDTLienHe   VARCHAR(20),
    TrangThai   ENUM('DangHopTac','TamNgung') DEFAULT 'DangHopTac',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 5. VaiTroThucTap
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS VaiTroThucTap (
    MaVaiTro   INT AUTO_INCREMENT PRIMARY KEY,
    TenVaiTro  VARCHAR(100) NOT NULL,
    MoTa       TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 6. NhomNguoiDung (vai tro PDT/TTDTXS/CVHT/CNHP)
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS NhomNguoiDung (
    MaNguoiDung VARCHAR(20) NOT NULL,
    VaiTro      ENUM('PDT','TTDTXS','CVHT','CNHP') NOT NULL,
    GhiChu      VARCHAR(255),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaNguoiDung, VaiTro),
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 7. ChuongTrinhDaoTao
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ChuongTrinhDaoTao (
    MaCTDT     VARCHAR(20)  PRIMARY KEY,
    TenCTDT    VARCHAR(200) NOT NULL,
    Khoa       VARCHAR(20),
    FileWord   VARCHAR(255),
    TrangThai  ENUM('BanNhap','ChoDuyet','DaDuyet','DaHuy') DEFAULT 'BanNhap',
    NguoiTao   VARCHAR(20)  NOT NULL,
    NguoiDuyet VARCHAR(20),
    NgayDuyet  DATETIME,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (NguoiTao)   REFERENCES NguoiDung(MaNguoiDung),
    FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 8. BCN_ThanhVien  <<< BANG NAY LA NGUYEN NHAN CHINH CUA LOI SCHEMA VALIDATION
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS BCN_ThanhVien (
    MaCTDT       VARCHAR(20) NOT NULL,
    MaGV         VARCHAR(20) NOT NULL,
    ChucDanh     ENUM('ChuNhiem','ThuKy','UyVien') NOT NULL,
    NgayBoNhiem  DATE,
    GhiChu       VARCHAR(255),
    created_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaGV, ChucDanh),
    FOREIGN KEY (MaCTDT) REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE CASCADE,
    FOREIGN KEY (MaGV)   REFERENCES GiangVien(MaGV) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 9. HocPhan
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS HocPhan (
    MaHocPhan    VARCHAR(20)  PRIMARY KEY,
    TenHocPhan   VARCHAR(200) NOT NULL,
    SoTinChi     INT          NOT NULL,
    LoaiHocPhan  ENUM('LyThuyet','ThucHanh','DoAn','ThucTap','KienTap'),
    ChuNhiemHP   VARCHAR(20)  NOT NULL,
    FileDeCuong  VARCHAR(255),
    TrangThai    ENUM('BanNhap','ChoDuyet','DaDuyet') DEFAULT 'BanNhap',
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_hocphan_sotinchi CHECK (SoTinChi BETWEEN 1 AND 15),
    FOREIGN KEY (ChuNhiemHP) REFERENCES GiangVien(MaGV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 10. DoiNguGiangVienHP
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS DoiNguGiangVienHP (
    MaHocPhan   VARCHAR(20) NOT NULL,
    MaGiangVien VARCHAR(20) NOT NULL,
    VaiTro      VARCHAR(50),
    PRIMARY KEY (MaHocPhan, MaGiangVien),
    FOREIGN KEY (MaHocPhan)   REFERENCES HocPhan(MaHocPhan) ON DELETE CASCADE,
    FOREIGN KEY (MaGiangVien) REFERENCES GiangVien(MaGV)   ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 11. LopHanhChinh
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS LopHanhChinh (
    MaLopHC    VARCHAR(20)  PRIMARY KEY,
    TenLopHC   VARCHAR(100) NOT NULL,
    Khoa       VARCHAR(20),
    NamBatDau  INT,
    MaCVHT     VARCHAR(20),
    MaCTDT     VARCHAR(20),
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaCVHT) REFERENCES GiangVien(MaGV),
    FOREIGN KEY (MaCTDT) REFERENCES ChuongTrinhDaoTao(MaCTDT)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 12. SinhVien
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS SinhVien (
    MaSV         VARCHAR(20) PRIMARY KEY,
    MaNguoiDung  VARCHAR(20) UNIQUE NOT NULL,
    MaLopHC      VARCHAR(20),
    NgaySinh     DATE,
    DiaChi       VARCHAR(300),
    TrangThai    ENUM('DangHoc','BaoLuu','ThoiHoc','TotNghiep') DEFAULT 'DangHoc',
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    FOREIGN KEY (MaLopHC)     REFERENCES LopHanhChinh(MaLopHC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 13. CTDT_HocPhan
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS CTDT_HocPhan (
    MaCTDT       VARCHAR(20) NOT NULL,
    MaHocPhan    VARCHAR(20) NOT NULL,
    HocKyDeNghi  INT,
    BatBuoc      BOOLEAN DEFAULT TRUE,
    SoLopDuKien  INT,
    PRIMARY KEY (MaCTDT, MaHocPhan),
    FOREIGN KEY (MaCTDT)    REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE CASCADE,
    FOREIGN KEY (MaHocPhan) REFERENCES HocPhan(MaHocPhan)        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 14. LopHocPhan
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS LopHocPhan (
    MaCTDT       VARCHAR(20) NOT NULL,
    MaHocPhan    VARCHAR(20) NOT NULL,
    MaHocKy      VARCHAR(20) NOT NULL,
    MaLopHocPhan INT         NOT NULL,
    TenLopHP     VARCHAR(150),
    MaGV         VARCHAR(20),
    SiSoToiDa    INT DEFAULT 50,
    SiSoThucTe   INT DEFAULT 0,
    TrangThai    ENUM('DangMo','DaDong','DaHuy') DEFAULT 'DangMo',
    created_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan),
    FOREIGN KEY (MaCTDT, MaHocPhan) REFERENCES CTDT_HocPhan(MaCTDT, MaHocPhan),
    FOREIGN KEY (MaHocKy) REFERENCES HocKyNamHoc(MaHocKy),
    FOREIGN KEY (MaGV)    REFERENCES GiangVien(MaGV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 15. DanhSachSinhVienLopHocPhan
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS DanhSachSinhVienLopHocPhan (
    MaCTDT       VARCHAR(20) NOT NULL,
    MaHocPhan    VARCHAR(20) NOT NULL,
    MaHocKy      VARCHAR(20) NOT NULL,
    MaLopHocPhan INT         NOT NULL,
    MaSV         VARCHAR(20) NOT NULL,
    DiemCuoiKy   DECIMAL(4,2),
    NhanXetCVHT  TEXT,
    created_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, MaSV),
    FOREIGN KEY (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)
        REFERENCES LopHocPhan(MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan) ON DELETE CASCADE,
    FOREIGN KEY (MaSV) REFERENCES SinhVien(MaSV) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 16. DotKienTap
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS DotKienTap (
    MaDotKT         INT AUTO_INCREMENT PRIMARY KEY,
    TenDotKT        VARCHAR(200) NOT NULL,
    MaLopHC         VARCHAR(20),
    MaHocKy         VARCHAR(20),
    ThoiGian        VARCHAR(100),
    MaGVPhuTrach    VARCHAR(20),
    MaDN            VARCHAR(20),
    KinhPhiChung    DECIMAL(15,2) DEFAULT 0,
    KinhPhiTungSV   DECIMAL(15,2) DEFAULT 0,
    TrangThai       ENUM('ChuanBi','ChoDuyet','DaDuyet','DaThucHien','DaHuy') DEFAULT 'ChuanBi',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaLopHC)      REFERENCES LopHanhChinh(MaLopHC),
    FOREIGN KEY (MaHocKy)      REFERENCES HocKyNamHoc(MaHocKy),
    FOREIGN KEY (MaGVPhuTrach) REFERENCES GiangVien(MaGV),
    FOREIGN KEY (MaDN)         REFERENCES DoanhNghiep(MaDN)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 17. DanhSachSinhVienKienTap
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS DanhSachSinhVienKienTap (
    MaDotKT    INT         NOT NULL,
    MaSV       VARCHAR(20) NOT NULL,
    GhiChu     VARCHAR(255),
    created_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaDotKT, MaSV),
    FOREIGN KEY (MaDotKT) REFERENCES DotKienTap(MaDotKT) ON DELETE CASCADE,
    FOREIGN KEY (MaSV)    REFERENCES SinhVien(MaSV)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 18. DotThucTap
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS DotThucTap (
    MaDotTT      INT AUTO_INCREMENT PRIMARY KEY,
    TenDotTT     VARCHAR(200) NOT NULL,
    MaCTDT       VARCHAR(20),
    MaHocPhan    VARCHAR(20),
    MaHocKy      VARCHAR(20),
    NgayBatDau   DATE,
    NgayKetThuc  DATE,
    TrangThai    ENUM('ChuanBi','ChoDuyet','DaDuyet','DangThucHien','DaKetThuc') DEFAULT 'ChuanBi',
    created_at   DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaCTDT, MaHocPhan) REFERENCES CTDT_HocPhan(MaCTDT, MaHocPhan),
    FOREIGN KEY (MaHocKy) REFERENCES HocKyNamHoc(MaHocKy)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 19. DanhSachThucTap
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS DanhSachThucTap (
    MaDSTT      INT AUTO_INCREMENT PRIMARY KEY,
    MaDotTT     INT         NOT NULL,
    MaSV        VARCHAR(20) NOT NULL,
    MaDN        VARCHAR(20),
    MaGVHuongDan VARCHAR(20),
    MaVaiTro    INT,
    LoaiThucTap ENUM('Truong','DoanhNghiep') DEFAULT 'Truong',
    TrangThai   ENUM('DaPhanCong','DangThucTap','DaKetThuc','DaHuy') DEFAULT 'DaPhanCong',
    GhiChu      VARCHAR(255),
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaDotTT)     REFERENCES DotThucTap(MaDotTT) ON DELETE CASCADE,
    FOREIGN KEY (MaSV)        REFERENCES SinhVien(MaSV)     ON DELETE CASCADE,
    FOREIGN KEY (MaDN)        REFERENCES DoanhNghiep(MaDN),
    FOREIGN KEY (MaGVHuongDan) REFERENCES GiangVien(MaGV),
    FOREIGN KEY (MaVaiTro)    REFERENCES VaiTroThucTap(MaVaiTro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------------
-- 20. KetQuaThucTap
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS KetQuaThucTap (
    MaKQTT      INT AUTO_INCREMENT PRIMARY KEY,
    MaDSTT      INT UNIQUE  NOT NULL,
    DiemTT      DECIMAL(4,2),
    NhanXet     TEXT,
    FileBaoCao  VARCHAR(255),
    NgayChamDiem DATETIME,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (MaDSTT) REFERENCES DanhSachThucTap(MaDSTT) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- KIEM TRA: chay de xem cac bang da duoc tao
-- =============================================================================
SHOW TABLES;
