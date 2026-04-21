-- =============================================================================
-- 01_create_tables.sql
-- Tao toan bo schema cho DB QuanLyCTDTDB, dong bo 1-1 voi @Entity JPA
-- va file 02_seed_data.sql.
--
-- Cach dung:
--   1) Mo phpMyAdmin (XAMPP) > tao DB "QuanLyCTDTDB" (utf8mb4_unicode_ci)
--   2) Tab SQL > paste file nay > Go
--   3) Chay tiep scripts/02_seed_data.sql de seed du lieu mau
--
-- Tai sao DROP TABLE IF EXISTS: dam bao idempotent ngay ca khi da ton tai
-- schema cu lech cot. Chay lai an toan khi DB dang rong hoac chi co bang cu.
-- CANH BAO: Script nay se XOA toan bo du lieu hien co. Backup truoc khi chay.
-- =============================================================================

-- Tao database neu chua co (idempotent). Charset utf8mb4 cho tieng Viet co dau.
CREATE DATABASE IF NOT EXISTS QuanLyCTDTDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE QuanLyCTDTDB;
SET FOREIGN_KEY_CHECKS = 0;

-- Xoa theo thu tu nguoc phu thuoc FK de tranh loi
DROP TABLE IF EXISTS KetQuaThucTap;
DROP TABLE IF EXISTS DanhSachThucTap;
DROP TABLE IF EXISTS DotThucTap;
DROP TABLE IF EXISTS DanhSachSinhVienKienTap;
DROP TABLE IF EXISTS DotKienTap;
DROP TABLE IF EXISTS DanhSachSinhVienLopHocPhan;
DROP TABLE IF EXISTS LopHocPhan;
DROP TABLE IF EXISTS CTDT_HocPhan;
DROP TABLE IF EXISTS DoiNguGiangVienHP;
DROP TABLE IF EXISTS SinhVien;
DROP TABLE IF EXISTS LopHanhChinh;
DROP TABLE IF EXISTS HocPhan;
DROP TABLE IF EXISTS BCN_ThanhVien;
DROP TABLE IF EXISTS ChuongTrinhDaoTao;
DROP TABLE IF EXISTS NhomNguoiDung;
DROP TABLE IF EXISTS VaiTroThucTap;
DROP TABLE IF EXISTS DoanhNghiep;
DROP TABLE IF EXISTS GiangVien;
DROP TABLE IF EXISTS NguoiDung;
DROP TABLE IF EXISTS HocKyNamHoc;

-- =============================================================================
-- 1. HocKyNamHoc
-- =============================================================================
CREATE TABLE HocKyNamHoc (
    MaHocKy     VARCHAR(20)  NOT NULL,
    TenHocKy    VARCHAR(50)  NOT NULL,
    NgayBatDau  DATE         NOT NULL,
    NgayKetThuc DATE         NOT NULL,
    TrangThai   VARCHAR(20)  DEFAULT 'SapDienRa',
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaHocKy)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 2. NguoiDung  (map entity NguoiDung)
--    Cot TenDangNhap + MatKhauHash + TrangThaiTK la BAT BUOC cho login.
-- =============================================================================
CREATE TABLE NguoiDung (
    MaNguoiDung    VARCHAR(20)  NOT NULL,
    TenDangNhap    VARCHAR(50)  NOT NULL,
    MatKhauHash    VARCHAR(255) NOT NULL,
    Email          VARCHAR(100) NOT NULL,
    HoTen          VARCHAR(100) NOT NULL,
    SoDienThoai    VARCHAR(15),
    TrangThaiTK    BIT          DEFAULT 1,
    LoaiNguoiDung  VARCHAR(20)  NOT NULL,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaNguoiDung),
    UNIQUE KEY uk_nguoidung_tendangnhap (TenDangNhap),
    UNIQUE KEY uk_nguoidung_email (Email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 3. GiangVien
-- =============================================================================
CREATE TABLE GiangVien (
    MaGV          VARCHAR(20)  NOT NULL,
    MaNguoiDung   VARCHAR(20)  NOT NULL,
    HocHam        VARCHAR(50),
    HocVi         VARCHAR(50),
    ChuyenNganh   VARCHAR(200),
    LoaiGiangVien VARCHAR(20)  DEFAULT 'GiangVienTruong',
    PRIMARY KEY (MaGV),
    UNIQUE KEY uk_giangvien_nguoidung (MaNguoiDung),
    CONSTRAINT fk_giangvien_nguoidung
        FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 4. DoanhNghiep
-- =============================================================================
CREATE TABLE DoanhNghiep (
    MaDoanhNghiep  VARCHAR(20)  NOT NULL,
    TenDoanhNghiep VARCHAR(200) NOT NULL,
    LinhVuc        VARCHAR(200),
    NguoiDaiDien   VARCHAR(100),
    Email          VARCHAR(100),
    SoDienThoai    VARCHAR(15),
    DiaChiDN       VARCHAR(255),
    TrangThai      VARCHAR(20)  DEFAULT 'DangHopTac',
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaDoanhNghiep)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 5. VaiTroThucTap  (danh muc VARCHAR PK: 'GV','DN','CVHT','SV')
-- =============================================================================
CREATE TABLE VaiTroThucTap (
    MaVaiTro   VARCHAR(10)  NOT NULL,
    TenVaiTro  VARCHAR(100) NOT NULL,
    MoTa       VARCHAR(255),
    PRIMARY KEY (MaVaiTro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 6. NhomNguoiDung  (PDT/TTDTXS/CVHT/CNHP)
-- =============================================================================
CREATE TABLE NhomNguoiDung (
    MaNguoiDung VARCHAR(20) NOT NULL,
    VaiTro      VARCHAR(20) NOT NULL,
    created_at  DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaNguoiDung, VaiTro),
    CONSTRAINT fk_nhomnd_nguoidung
        FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 7. ChuongTrinhDaoTao
-- =============================================================================
CREATE TABLE ChuongTrinhDaoTao (
    MaCTDT     VARCHAR(20)  NOT NULL,
    TenCTDT    VARCHAR(200) NOT NULL,
    Khoa       VARCHAR(20),
    FileWord   VARCHAR(255),
    TrangThai  VARCHAR(20)  DEFAULT 'BanNhap',
    NguoiTao   VARCHAR(20)  NOT NULL,
    NguoiDuyet VARCHAR(20),
    NgayDuyet  DATETIME,
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT),
    CONSTRAINT fk_ctdt_nguoitao   FOREIGN KEY (NguoiTao)   REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT fk_ctdt_nguoiduyet FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 8. BCN_ThanhVien  (@EmbeddedId: MaCTDT + MaGV + ChucDanh)
-- =============================================================================
CREATE TABLE BCN_ThanhVien (
    MaCTDT      VARCHAR(20) NOT NULL,
    MaGV        VARCHAR(20) NOT NULL,
    ChucDanh    VARCHAR(20) NOT NULL,
    NgayBoNhiem DATE,
    GhiChu      VARCHAR(255),
    created_at  DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaGV, ChucDanh),
    CONSTRAINT fk_bcn_ctdt FOREIGN KEY (MaCTDT) REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE CASCADE,
    CONSTRAINT fk_bcn_gv   FOREIGN KEY (MaGV)   REFERENCES GiangVien(MaGV)           ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 9. HocPhan
-- =============================================================================
CREATE TABLE HocPhan (
    MaHocPhan    VARCHAR(20)  NOT NULL,
    TenHocPhan   VARCHAR(200) NOT NULL,
    SoTinChi     INT          NOT NULL,
    LoaiHocPhan  VARCHAR(20),
    ChuNhiemHP   VARCHAR(20)  NOT NULL,
    FileDeCuong  VARCHAR(255),
    TrangThai    VARCHAR(20)  DEFAULT 'BanNhap',
    created_at   DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaHocPhan),
    CONSTRAINT fk_hocphan_cnhp FOREIGN KEY (ChuNhiemHP) REFERENCES GiangVien(MaGV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 10. DoiNguGiangVienHP  (@EmbeddedId: MaHocPhan + MaGiangVien)
-- =============================================================================
CREATE TABLE DoiNguGiangVienHP (
    MaHocPhan   VARCHAR(20) NOT NULL,
    MaGiangVien VARCHAR(20) NOT NULL,
    TrangThai   BIT         DEFAULT 1,
    created_at  DATETIME    DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (MaHocPhan, MaGiangVien),
    CONSTRAINT fk_dngv_hp FOREIGN KEY (MaHocPhan)   REFERENCES HocPhan(MaHocPhan) ON DELETE CASCADE,
    CONSTRAINT fk_dngv_gv FOREIGN KEY (MaGiangVien) REFERENCES GiangVien(MaGV)    ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 11. LopHanhChinh
-- =============================================================================
CREATE TABLE LopHanhChinh (
    MaLopHC    VARCHAR(20)  NOT NULL,
    TenLop     VARCHAR(100) NOT NULL,
    MaCTDT     VARCHAR(20),
    KhoaHoc    VARCHAR(20),
    MaCoVan    VARCHAR(20),
    created_at DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaLopHC),
    CONSTRAINT fk_lophc_ctdt  FOREIGN KEY (MaCTDT)  REFERENCES ChuongTrinhDaoTao(MaCTDT),
    CONSTRAINT fk_lophc_covan FOREIGN KEY (MaCoVan) REFERENCES GiangVien(MaGV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 12. SinhVien
-- =============================================================================
CREATE TABLE SinhVien (
    MaSV        VARCHAR(20) NOT NULL,
    MaNguoiDung VARCHAR(20) NOT NULL,
    MaLopHC     VARCHAR(20) NOT NULL,
    TrangThaiSV VARCHAR(20) DEFAULT 'DangHoc',
    PRIMARY KEY (MaSV),
    UNIQUE KEY uk_sinhvien_nguoidung (MaNguoiDung),
    CONSTRAINT fk_sv_nguoidung FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    CONSTRAINT fk_sv_lophc     FOREIGN KEY (MaLopHC)     REFERENCES LopHanhChinh(MaLopHC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 13. CTDT_HocPhan  (@EmbeddedId: MaCTDT + MaHocPhan; cot HocKyThu NOT NULL)
-- =============================================================================
CREATE TABLE CTDT_HocPhan (
    MaCTDT       VARCHAR(20) NOT NULL,
    MaHocPhan    VARCHAR(20) NOT NULL,
    HocKyThu     INT         NOT NULL,
    SoLopDuKien  INT         DEFAULT 1,
    BatBuoc      BIT         DEFAULT 1,
    GhiChu       VARCHAR(255),
    FileDeCuong  VARCHAR(255),
    created_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaHocPhan),
    CONSTRAINT fk_ctdthp_ctdt FOREIGN KEY (MaCTDT)    REFERENCES ChuongTrinhDaoTao(MaCTDT) ON DELETE CASCADE,
    CONSTRAINT fk_ctdthp_hp   FOREIGN KEY (MaHocPhan) REFERENCES HocPhan(MaHocPhan)        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 14. LopHocPhan  (@EmbeddedId: MaCTDT + MaHocPhan + MaHocKy + MaLopHocPhan)
-- =============================================================================
CREATE TABLE LopHocPhan (
    MaCTDT             VARCHAR(20)  NOT NULL,
    MaHocPhan          VARCHAR(20)  NOT NULL,
    MaHocKy            VARCHAR(20)  NOT NULL,
    MaLopHocPhan       INT          NOT NULL,
    MaGiangVien        VARCHAR(20),
    SiSoToiDa          INT          NOT NULL,
    SiSoThucTe         INT          DEFAULT 0,
    FileDeCuongChiTiet VARCHAR(255),
    TrangThai          VARCHAR(20)  DEFAULT 'DangMo',
    created_at         DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at         DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan),
    CONSTRAINT fk_lhp_ctdthp FOREIGN KEY (MaCTDT, MaHocPhan) REFERENCES CTDT_HocPhan(MaCTDT, MaHocPhan),
    CONSTRAINT fk_lhp_hocky  FOREIGN KEY (MaHocKy)           REFERENCES HocKyNamHoc(MaHocKy),
    CONSTRAINT fk_lhp_gv     FOREIGN KEY (MaGiangVien)       REFERENCES GiangVien(MaGV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 15. DanhSachSinhVienLopHocPhan
--     (@EmbeddedId: MaSV + MaCTDT + MaHocPhan + MaHocKy + MaLopHocPhan)
-- =============================================================================
CREATE TABLE DanhSachSinhVienLopHocPhan (
    MaSV         VARCHAR(20) NOT NULL,
    MaCTDT       VARCHAR(20) NOT NULL,
    MaHocPhan    VARCHAR(20) NOT NULL,
    MaHocKy      VARCHAR(20) NOT NULL,
    MaLopHocPhan INT         NOT NULL,
    NhanXet      TEXT,
    DaCanhBao    BIT         DEFAULT 0,
    KetQuaXuLy   TEXT,
    created_at   DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan),
    CONSTRAINT fk_dssvlhp_sv FOREIGN KEY (MaSV) REFERENCES SinhVien(MaSV) ON DELETE CASCADE,
    CONSTRAINT fk_dssvlhp_lhp FOREIGN KEY (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)
        REFERENCES LopHocPhan(MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 16. DotKienTap
-- =============================================================================
CREATE TABLE DotKienTap (
    MaDotKT        INT AUTO_INCREMENT,
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
    TrangThai      VARCHAR(20)  DEFAULT 'ChuanBi',
    NguoiTao       VARCHAR(20)  NOT NULL,
    NguoiDuyet     VARCHAR(20),
    NgayDuyet      DATETIME,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaDotKT),
    CONSTRAINT fk_dotkt_lophc    FOREIGN KEY (MaLopHC)       REFERENCES LopHanhChinh(MaLopHC),
    CONSTRAINT fk_dotkt_hocky    FOREIGN KEY (MaHocKy)       REFERENCES HocKyNamHoc(MaHocKy),
    CONSTRAINT fk_dotkt_gv       FOREIGN KEY (MaGVPhuTrach)  REFERENCES GiangVien(MaGV),
    CONSTRAINT fk_dotkt_dn       FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep),
    CONSTRAINT fk_dotkt_nguoitao FOREIGN KEY (NguoiTao)      REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT fk_dotkt_nguoiduyet FOREIGN KEY (NguoiDuyet)  REFERENCES NguoiDung(MaNguoiDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 17. DanhSachSinhVienKienTap  (@EmbeddedId: MaDotKT + MaSV)
-- =============================================================================
CREATE TABLE DanhSachSinhVienKienTap (
    MaDotKT    INT         NOT NULL,
    MaSV       VARCHAR(20) NOT NULL,
    DaThamGia  BIT         DEFAULT 1,
    created_at DATETIME    DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaDotKT, MaSV),
    CONSTRAINT fk_dsskt_dotkt FOREIGN KEY (MaDotKT) REFERENCES DotKienTap(MaDotKT) ON DELETE CASCADE,
    CONSTRAINT fk_dsskt_sv    FOREIGN KEY (MaSV)    REFERENCES SinhVien(MaSV)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 18. DotThucTap
-- =============================================================================
CREATE TABLE DotThucTap (
    MaDotTT       INT AUTO_INCREMENT,
    TenDotTT      VARCHAR(200) NOT NULL,
    MaCTDT        VARCHAR(20),
    MaHocPhan     VARCHAR(20),
    MaHocKy       VARCHAR(20)  NOT NULL,
    NgayBatDau    DATE,
    NgayKetThuc   DATE,
    FileMinhChung VARCHAR(255),
    TrangThai     VARCHAR(30)  DEFAULT 'ChuanBi',
    NguoiTao      VARCHAR(20)  NOT NULL,
    NguoiDuyet    VARCHAR(20),
    NgayDuyet     DATETIME,
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaDotTT),
    CONSTRAINT fk_dottt_ctdthp FOREIGN KEY (MaCTDT, MaHocPhan) REFERENCES CTDT_HocPhan(MaCTDT, MaHocPhan),
    CONSTRAINT fk_dottt_hocky  FOREIGN KEY (MaHocKy)           REFERENCES HocKyNamHoc(MaHocKy),
    CONSTRAINT fk_dottt_nguoitao   FOREIGN KEY (NguoiTao)   REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT fk_dottt_nguoiduyet FOREIGN KEY (NguoiDuyet) REFERENCES NguoiDung(MaNguoiDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 19. DanhSachThucTap
-- =============================================================================
CREATE TABLE DanhSachThucTap (
    MaThucTap     INT AUTO_INCREMENT,
    MaDotTT       INT          NOT NULL,
    MaSV          VARCHAR(20)  NOT NULL,
    LoaiThucTap   VARCHAR(20)  NOT NULL,
    MaDoanhNghiep VARCHAR(20),
    TrangThai     VARCHAR(20)  DEFAULT 'DaPhanCong',
    created_at    DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaThucTap),
    CONSTRAINT fk_dstt_dottt FOREIGN KEY (MaDotTT)       REFERENCES DotThucTap(MaDotTT) ON DELETE CASCADE,
    CONSTRAINT fk_dstt_sv    FOREIGN KEY (MaSV)          REFERENCES SinhVien(MaSV)     ON DELETE CASCADE,
    CONSTRAINT fk_dstt_dn    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 20. KetQuaThucTap
-- =============================================================================
CREATE TABLE KetQuaThucTap (
    MaKetQua       INT AUTO_INCREMENT,
    MaThucTap      INT          NOT NULL,
    MaVaiTro       VARCHAR(10)  NOT NULL,
    MaNguoiDanhGia VARCHAR(20)  NOT NULL,
    Diem           DECIMAL(4,2),
    NhanXet        TEXT,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaKetQua),
    CONSTRAINT fk_kqtt_dstt FOREIGN KEY (MaThucTap)      REFERENCES DanhSachThucTap(MaThucTap) ON DELETE CASCADE,
    CONSTRAINT fk_kqtt_vt   FOREIGN KEY (MaVaiTro)       REFERENCES VaiTroThucTap(MaVaiTro),
    CONSTRAINT fk_kqtt_nguoidanhgia FOREIGN KEY (MaNguoiDanhGia) REFERENCES GiangVien(MaGV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- Kiem tra nhanh
-- =============================================================================
SHOW TABLES;
