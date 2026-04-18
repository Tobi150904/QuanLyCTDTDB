-- ============================================================
-- DATABASE: QuanLyCTDTDB
-- Phien ban: 2.0  |  Engine: InnoDB  |  Charset: utf8mb4
-- Thu tu tao bang theo DEPENDENCY CHAIN (khong dung FOREIGN_KEY_CHECKS=0)
--
-- Dependency order:
--  1.  HocKyNamHoc         (doc lap)
--  2.  NguoiDung           (doc lap)
--  3.  GiangVien           <- NguoiDung
--  4.  DoanhNghiep         (doc lap)
--  5.  VaiTroThucTap       (doc lap)
--  6.  NhomNguoiDung       <- NguoiDung
--  7.  ChuongTrinhDaoTao   <- NguoiDung
--  8.  BCN_ThanhVien       <- ChuongTrinhDaoTao, GiangVien
--  9.  HocPhan             <- GiangVien
-- 10.  DoiNguGiangVienHP   <- HocPhan, GiangVien
-- 11.  LopHanhChinh        <- ChuongTrinhDaoTao, GiangVien
-- 12.  SinhVien            <- NguoiDung, LopHanhChinh
-- 13.  CTDT_HocPhan        <- ChuongTrinhDaoTao, HocPhan
-- 14.  LopHocPhan          <- CTDT_HocPhan, HocKyNamHoc, GiangVien
-- 15.  DanhSachSVLopHocPhan<- SinhVien, LopHocPhan
-- 16.  DotKienTap          <- LopHanhChinh, HocKyNamHoc, GiangVien, DoanhNghiep, NguoiDung
-- 17.  DanhSachSVKienTap   <- DotKienTap, SinhVien
-- 18.  DotThucTap          <- CTDT_HocPhan, HocKyNamHoc, NguoiDung
-- 19.  DanhSachThucTap     <- DotThucTap, SinhVien, DoanhNghiep
-- 20.  KetQuaThucTap       <- DanhSachThucTap, VaiTroThucTap, GiangVien
-- ============================================================

DROP DATABASE IF EXISTS QuanLyCTDTDB;
CREATE DATABASE QuanLyCTDTDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE QuanLyCTDTDB;

-- ============================================================
-- 1. HocKyNamHoc  (doc lap)
-- ============================================================
CREATE TABLE HocKyNamHoc (
    MaHocKy      VARCHAR(20)  PRIMARY KEY,
    TenHocKy     VARCHAR(50)  NOT NULL,
    NgayBatDau   DATE         NOT NULL,
    NgayKetThuc  DATE         NOT NULL,
    TrangThai    ENUM('SapDienRa','DangDienRa','DaKetThuc') DEFAULT 'SapDienRa',
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_hocky_dates CHECK (NgayKetThuc > NgayBatDau)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 2. NguoiDung  (doc lap)
-- ============================================================
CREATE TABLE NguoiDung (
    MaNguoiDung  VARCHAR(20)  PRIMARY KEY,
    TenDangNhap  VARCHAR(50)  NOT NULL UNIQUE,
    MatKhauHash  VARCHAR(255) NOT NULL,
    Email        VARCHAR(100) NOT NULL UNIQUE,
    HoTen        VARCHAR(100) NOT NULL,
    SoDienThoai  VARCHAR(15),
    TrangThaiTK  BIT          DEFAULT 1,
    LoaiNguoiDung ENUM('Admin','GiangVien','SinhVien','DoanhNghiep') NOT NULL,
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 3. GiangVien  <- NguoiDung
-- ============================================================
CREATE TABLE GiangVien (
    MaGV          VARCHAR(20)  PRIMARY KEY,
    MaNguoiDung   VARCHAR(20)  NOT NULL UNIQUE,
    HocHam        VARCHAR(50),
    HocVi         VARCHAR(50),
    ChuyenNganh   VARCHAR(200),
    LoaiGiangVien ENUM('GiangVienTruong','DoanhNghiep') DEFAULT 'GiangVienTruong',
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 4. DoanhNghiep  (doc lap)
-- ============================================================
CREATE TABLE DoanhNghiep (
    MaDoanhNghiep  VARCHAR(20)   PRIMARY KEY,
    TenDoanhNghiep VARCHAR(200)  NOT NULL,
    LinhVuc        VARCHAR(200),
    NguoiDaiDien   VARCHAR(100),
    Email          VARCHAR(100),
    SoDienThoai    VARCHAR(15),
    DiaChiDN       VARCHAR(255),
    TrangThai      ENUM('DangHopTac','TamNgung') DEFAULT 'DangHopTac',
    created_at     DATETIME      DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 5. VaiTroThucTap  (doc lap - danh muc)
-- ============================================================
CREATE TABLE VaiTroThucTap (
    MaVaiTro  VARCHAR(10)  PRIMARY KEY,
    TenVaiTro VARCHAR(100) NOT NULL,
    MoTa      VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 6. NhomNguoiDung  <- NguoiDung
--    Vai tro nghiep vu (PDT, TTDTXS, CVHT, CNHP) - khac LoaiNguoiDung
-- ============================================================
CREATE TABLE NhomNguoiDung (
    MaNguoiDung  VARCHAR(20) NOT NULL,
    VaiTro       ENUM('PDT','TTDTXS','CVHT','CNHP') NOT NULL,
    created_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaNguoiDung, VaiTro),
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 7. ChuongTrinhDaoTao  <- NguoiDung (NguoiTao, NguoiDuyet)
-- ============================================================
CREATE TABLE ChuongTrinhDaoTao (
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

-- ============================================================
-- 8. BCN_ThanhVien  <- ChuongTrinhDaoTao, GiangVien
--    Ban Chu Nhiem CTDT (composite PK: MaCTDT + MaGV + ChucDanh)
-- ============================================================
CREATE TABLE BCN_ThanhVien (
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

-- ============================================================
-- 9. HocPhan  <- GiangVien (ChuNhiemHP)
-- ============================================================
CREATE TABLE HocPhan (
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

-- ============================================================
-- 10. DoiNguGiangVienHP  <- HocPhan, GiangVien
-- ============================================================
CREATE TABLE DoiNguGiangVienHP (
    MaHocPhan    VARCHAR(20) NOT NULL,
    MaGiangVien  VARCHAR(20) NOT NULL,
    TrangThai    BIT         DEFAULT 1,
    created_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaHocPhan, MaGiangVien),
    FOREIGN KEY (MaHocPhan)   REFERENCES HocPhan(MaHocPhan)  ON DELETE CASCADE,
    FOREIGN KEY (MaGiangVien) REFERENCES GiangVien(MaGV)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 11. LopHanhChinh  <- ChuongTrinhDaoTao, GiangVien (MaCoVan)
-- ============================================================
CREATE TABLE LopHanhChinh (
    MaLopHC    VARCHAR(20)  PRIMARY KEY,
    TenLop     VARCHAR(100) NOT NULL,
    MaCTDT     VARCHAR(20),
    KhoaHoc    VARCHAR(20),
    MaCoVan    VARCHAR(20),
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaCTDT)  REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE SET NULL,
    FOREIGN KEY (MaCoVan) REFERENCES GiangVien(MaGV)           ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 12. SinhVien  <- NguoiDung, LopHanhChinh
-- ============================================================
CREATE TABLE SinhVien (
    MaSV          VARCHAR(20) PRIMARY KEY,
    MaNguoiDung   VARCHAR(20) NOT NULL UNIQUE,
    MaLopHC       VARCHAR(20) NOT NULL,
    TrangThaiSV   ENUM('DangHoc','BaoLuu','ThoiHoc','TotNghiep') DEFAULT 'DangHoc',
    FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung)     ON DELETE CASCADE,
    FOREIGN KEY (MaLopHC)     REFERENCES LopHanhChinh(MaLopHC)      ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 13. CTDT_HocPhan  <- ChuongTrinhDaoTao, HocPhan
--     Bang trung gian: 1 CTDT co nhieu HocPhan, co them meta
-- ============================================================
CREATE TABLE CTDT_HocPhan (
    MaCTDT        VARCHAR(20) NOT NULL,
    MaHocPhan     VARCHAR(20) NOT NULL,
    HocKyThu      INT         NOT NULL COMMENT 'Hoc ky thu may trong CTDT (1-8)',
    SoLopDuKien   INT         DEFAULT 1,
    BatBuoc       BIT         DEFAULT 1,
    GhiChu        VARCHAR(255),
    FileDeCuong   VARCHAR(255),
    created_at    DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaHocPhan),
    CONSTRAINT chk_hocky_thu CHECK (HocKyThu BETWEEN 1 AND 8),
    FOREIGN KEY (MaCTDT)    REFERENCES ChuongTrinhDaoTao(MaCTDT)  ON DELETE CASCADE,
    FOREIGN KEY (MaHocPhan) REFERENCES HocPhan(MaHocPhan)          ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 14. LopHocPhan  <- CTDT_HocPhan, HocKyNamHoc, GiangVien
--     Composite PK: (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)
-- ============================================================
CREATE TABLE LopHocPhan (
    MaCTDT              VARCHAR(20) NOT NULL,
    MaHocPhan           VARCHAR(20) NOT NULL,
    MaHocKy             VARCHAR(20) NOT NULL,
    MaLopHocPhan        INT         NOT NULL,
    MaGiangVien         VARCHAR(20),
    SiSoToiDa           INT         NOT NULL,
    SiSoThucTe          INT         DEFAULT 0,
    FileDeCuongChiTiet  VARCHAR(255),
    TrangThai           ENUM('DangMo','DaDong','DaHuy') DEFAULT 'DangMo',
    created_at          DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan),
    CONSTRAINT chk_siso CHECK (SiSoToiDa BETWEEN 30 AND 60),
    FOREIGN KEY (MaCTDT, MaHocPhan) REFERENCES CTDT_HocPhan(MaCTDT, MaHocPhan) ON DELETE RESTRICT,
    FOREIGN KEY (MaHocKy)           REFERENCES HocKyNamHoc(MaHocKy),
    FOREIGN KEY (MaGiangVien)       REFERENCES GiangVien(MaGV) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 15. DanhSachSinhVienLopHocPhan  <- SinhVien, LopHocPhan
-- ============================================================
CREATE TABLE DanhSachSinhVienLopHocPhan (
    MaSV          VARCHAR(20) NOT NULL,
    MaCTDT        VARCHAR(20) NOT NULL,
    MaHocPhan     VARCHAR(20) NOT NULL,
    MaHocKy       VARCHAR(20) NOT NULL,
    MaLopHocPhan  INT         NOT NULL,
    NhanXet       TEXT,
    DaCanhBao     BIT         DEFAULT 0,
    KetQuaXuLy   TEXT,
    created_at    DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan),
    FOREIGN KEY (MaSV) REFERENCES SinhVien(MaSV) ON DELETE CASCADE,
    FOREIGN KEY (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)
        REFERENCES LopHocPhan(MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 16. DotKienTap  <- LopHanhChinh, HocKyNamHoc, GiangVien,
--                    DoanhNghiep, NguoiDung
-- ============================================================
CREATE TABLE DotKienTap (
    MaDotKT        INT          AUTO_INCREMENT PRIMARY KEY,
    TenDotKT       VARCHAR(200) NOT NULL,
    MaLopHC        VARCHAR(20)  NOT NULL,
    MaHocKy        VARCHAR(20)  NOT NULL,
    ThoiGian       DATE,
    MaGVPhuTrach   VARCHAR(20),
    MaDoanhNghiep  VARCHAR(20)  NOT NULL,
    NhanXetGV      TEXT,
    NhanXetDN      TEXT,
    FileMinhChung  VARCHAR(255),
    KinhPhiChung   DECIMAL(15,2),
    KinhPhiTungSV  DECIMAL(15,2),
    TrangThai      ENUM('ChuanBi','ChoDuyet','DaDuyet','DaThucHien','DaHuy') DEFAULT 'ChuanBi',
    NguoiTao       VARCHAR(20)  NOT NULL,
    NguoiDuyet     VARCHAR(20),
    NgayDuyet      DATETIME,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaLopHC)       REFERENCES LopHanhChinh(MaLopHC),
    FOREIGN KEY (MaHocKy)       REFERENCES HocKyNamHoc(MaHocKy),
    FOREIGN KEY (MaGVPhuTrach)  REFERENCES GiangVien(MaGV),
    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep),
    FOREIGN KEY (NguoiTao)      REFERENCES NguoiDung(MaNguoiDung),
    FOREIGN KEY (NguoiDuyet)    REFERENCES NguoiDung(MaNguoiDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 17. DanhSachSinhVienKienTap  <- DotKienTap, SinhVien
-- ============================================================
CREATE TABLE DanhSachSinhVienKienTap (
    MaDotKT    INT         NOT NULL,
    MaSV       VARCHAR(20) NOT NULL,
    DaThamGia  BIT         DEFAULT 1,
    created_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaDotKT, MaSV),
    FOREIGN KEY (MaDotKT) REFERENCES DotKienTap(MaDotKT) ON DELETE CASCADE,
    FOREIGN KEY (MaSV)    REFERENCES SinhVien(MaSV)       ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 18. DotThucTap  <- CTDT_HocPhan, HocKyNamHoc, NguoiDung
-- ============================================================
CREATE TABLE DotThucTap (
    MaDotTT        INT          AUTO_INCREMENT PRIMARY KEY,
    TenDotTT       VARCHAR(200) NOT NULL,
    MaCTDT         VARCHAR(20)  NOT NULL,
    MaHocPhan      VARCHAR(20)  NOT NULL,
    MaHocKy        VARCHAR(20)  NOT NULL,
    NgayBatDau     DATE,
    NgayKetThuc    DATE,
    FileMinhChung  VARCHAR(255),
    TrangThai      ENUM('ChuanBi','ChoDuyet','DaDuyet','DangThucHien','DaKetThuc') DEFAULT 'ChuanBi',
    NguoiTao       VARCHAR(20)  NOT NULL,
    NguoiDuyet     VARCHAR(20),
    NgayDuyet      DATETIME,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaCTDT, MaHocPhan) REFERENCES CTDT_HocPhan(MaCTDT, MaHocPhan),
    FOREIGN KEY (MaHocKy)           REFERENCES HocKyNamHoc(MaHocKy),
    FOREIGN KEY (NguoiTao)          REFERENCES NguoiDung(MaNguoiDung),
    FOREIGN KEY (NguoiDuyet)        REFERENCES NguoiDung(MaNguoiDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 19. DanhSachThucTap  <- DotThucTap, SinhVien, DoanhNghiep
-- ============================================================
CREATE TABLE DanhSachThucTap (
    MaThucTap      INT          AUTO_INCREMENT PRIMARY KEY,
    MaDotTT        INT          NOT NULL,
    MaSV           VARCHAR(20)  NOT NULL,
    LoaiThucTap    ENUM('Truong','DoanhNghiep') NOT NULL,
    MaDoanhNghiep  VARCHAR(20),
    TrangThai      ENUM('DaPhanCong','DangThucTap','DaKetThuc','DaHuy') DEFAULT 'DaPhanCong',
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (MaDotTT)       REFERENCES DotThucTap(MaDotTT)          ON DELETE CASCADE,
    FOREIGN KEY (MaSV)          REFERENCES SinhVien(MaSV)               ON DELETE CASCADE,
    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep)   ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- 20. KetQuaThucTap  <- DanhSachThucTap, VaiTroThucTap, GiangVien
-- ============================================================
CREATE TABLE KetQuaThucTap (
    MaKetQua        INT          AUTO_INCREMENT PRIMARY KEY,
    MaThucTap       INT          NOT NULL,
    MaVaiTro        VARCHAR(10)  NOT NULL,
    MaNguoiDanhGia  VARCHAR(20)  NOT NULL,
    Diem            DECIMAL(4,2),
    NhanXet         TEXT,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_diem CHECK (Diem BETWEEN 0 AND 10),
    FOREIGN KEY (MaThucTap)      REFERENCES DanhSachThucTap(MaThucTap) ON DELETE CASCADE,
    FOREIGN KEY (MaVaiTro)       REFERENCES VaiTroThucTap(MaVaiTro),
    FOREIGN KEY (MaNguoiDanhGia) REFERENCES GiangVien(MaGV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- KIEM TRA CAU TRUC (chay tung dong sau khi import thanh cong)
-- ============================================================
-- SHOW TABLES;
-- SELECT TABLE_NAME, TABLE_ROWS FROM information_schema.TABLES WHERE TABLE_SCHEMA='QuanLyCTDTDB';
