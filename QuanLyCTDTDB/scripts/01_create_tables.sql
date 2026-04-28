-- =============================================================================
-- 01_create_tables.sql  —  SCHEMA FULL  (Phase 1 -> Phase 7, refactor 2 cot diem)
-- =============================================================================
-- He Thong Quan Ly Chuong Trinh Dao Tao Xuat Sac (Truong DH Nha Trang).
-- Schema 1-1 voi @Entity JPA + 02_seed_data.sql.
--
-- Cach dung (XAMPP / phpMyAdmin):
--   1) Tao DB "QuanLyCTDTDB" charset utf8mb4_unicode_ci.
--   2) Tab SQL > paste file nay > Go.
--   3) Chay tiep scripts/02_seed_data.sql.
--
-- ===== THAY DOI LON SO VOI BAN TRUOC (Phase 7 refactor — fix bug "diem TT") =====
--
-- 1. **Bang moi NhanVienDoanhNghiep** — luu can bo doanh nghiep huong dan SV
--    thuc tap. Truoc day chi co GiangVien, dan toi viec MaNguoiDanhGia phai
--    FK ve GiangVien va nguoi cham diem cho cot DN bi GIA LAP bang GV001
--    (sai semantic + bao cao thong ke nguoi danh gia bi nhieu).
--
-- 2. **KetQuaThucTap.MaNguoiDanhGia FK chuyen tu GiangVien sang NguoiDung**.
--    Cho phep ca GV (cot 1 Truong / cot 2 DN) lan NV DN (cot 1 DN) deu la
--    nguoi danh gia hop le. CHK enum-like duy tri qua app layer + UI dropdown.
--
-- 3. **VaiTroThucTap.TenVaiTro chuan hoa**:
--      GV_HD : "GV Giam Sat / Huong Dan"  (truong=cot1, DN=cot2)
--      GV_PB : "GV Phan Bien"             (truong=cot2)
--      DN    : "Nhan Vien Doanh Nghiep"   (DN=cot1)
--      CVHT  : "Co Van Hoc Tap"           (tham khao, ngoai 2 cot diem)
--      GV    : (legacy, giu de migrate cu)
--
-- 4. **Case A (NV DN giang day thinh giang)** duoc seed: 1 NguoiDung vua co
--    record NhanVienDoanhNghiep (lien ket DN001) vua co record GiangVien
--    (LoaiGiangVien=GiangVienThinhGiang). UI tu nhan dien qua 2 bang.
--
-- ===== CHINH SACH FK — IMMUTABLE KEYS =====
--   Tat ca FK khong ON UPDATE => default RESTRICT.
--   Business key (MaGV, MaSV, MaHP, MaCTDT, MaHocKy, MaDoanhNghiep, MaLopHC,
--   MaNguoiDung, MaNVDN) la BAT BIEN sau khi INSERT.
--   Muon "doi ma" -> tao record moi + soft-delete record cu, KHONG UPDATE PK.
--
-- ===== DROP IDEMPOTENT =====
--   DROP TABLE IF EXISTS theo thu tu nguoc phu thuoc FK.
--   CANH BAO: file nay XOA toan bo du lieu hien co. Backup truoc khi chay.
-- =============================================================================

CREATE DATABASE IF NOT EXISTS QuanLyCTDTDB
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE QuanLyCTDTDB;
SET FOREIGN_KEY_CHECKS = 0;

-- ----- DROP theo thu tu nguoc phu thuoc -----
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
DROP TABLE IF EXISTS NhanVienDoanhNghiep;        -- moi (Phase 7 refactor)
DROP TABLE IF EXISTS DoanhNghiep;
DROP TABLE IF EXISTS GiangVien;
DROP TABLE IF EXISTS NguoiDung;
DROP TABLE IF EXISTS HocKyNamHoc;

-- =============================================================================
-- 1. HocKyNamHoc  (doc lap, root cua time-axis)
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
-- 2. NguoiDung  (root identity: AD/GV/SV/DN)
--    LoaiNguoiDung: Admin | GiangVien | SinhVien | DoanhNghiep
--    Mat khau Password@123 (BCrypt cost=10) cho seed test.
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
-- 3. GiangVien  <- NguoiDung
--    LoaiGiangVien: GiangVienTruong | GiangVienThinhGiang | DoanhNghiep
--    MOT NguoiDung co the dong thoi co record NhanVienDoanhNghiep (Case A)
--    -> dung de mo ta NV DN tham gia thinh giang tai truong.
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
-- 4. DoanhNghiep  (doc lap)
--    TrangThai: DangHopTac | TamNgung
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
-- 5. NhanVienDoanhNghiep  (Phase 7 — moi)
--
--    Mo hinh hoa "can bo doanh nghiep" tham gia huong dan SV thuc tap tai DN.
--    Tach khoi GiangVien vi ban chat khac (khong giang day chinh khoa). Tuy
--    nhien, MOT NguoiDung co the cung luc co GiangVien record (LoaiGiangVien
--    =GiangVienThinhGiang) -> Case A: NV DN tham gia thinh giang tai truong.
--
--    UNIQUE(MaNguoiDung): 1 NV DN map 1-1 voi 1 NguoiDung.
--    LaCongTacVien=1: NV DN co tham gia hop tac voi truong (vd thinh giang).
--    TrangThaiTK ke thua tu NguoiDung — khong tach trang thai rieng.
-- =============================================================================
CREATE TABLE NhanVienDoanhNghiep (
    MaNVDN          VARCHAR(20)  NOT NULL,
    MaNguoiDung     VARCHAR(20)  NOT NULL,
    MaDoanhNghiep   VARCHAR(20)  NOT NULL,
    ChucVu          VARCHAR(100),
    PhongBan        VARCHAR(100),
    ChuyenMon       VARCHAR(200),
    LaCongTacVien   BIT          DEFAULT 0,
    GhiChu          VARCHAR(255),
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaNVDN),
    UNIQUE KEY uk_nvdn_nguoidung (MaNguoiDung),
    CONSTRAINT fk_nvdn_nguoidung
        FOREIGN KEY (MaNguoiDung) REFERENCES NguoiDung(MaNguoiDung) ON DELETE CASCADE,
    CONSTRAINT fk_nvdn_doanhnghiep
        FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 6. VaiTroThucTap  (danh muc, dung trong KetQuaThucTap)
--
--    He thong 2 cot diem (Phase 7):
--      LoaiThucTap = Truong       -> Cot 1 = GV_HD,  Cot 2 = GV_PB
--      LoaiThucTap = DoanhNghiep  -> Cot 1 = DN,     Cot 2 = GV_HD
--
--    Vai tro CVHT (tham khao) khong nam trong cong thuc TB nhung van duoc luu
--    de phuc vu bao cao "phan hoi 360 do".
--
--    'GV' la legacy (truoc Phase 7) — giu de back-compat du lieu cu, ung dung
--    moi se khong sinh row vai tro nay.
-- =============================================================================
CREATE TABLE VaiTroThucTap (
    MaVaiTro   VARCHAR(10)  NOT NULL,
    TenVaiTro  VARCHAR(100) NOT NULL,
    MoTa       VARCHAR(255),
    PRIMARY KEY (MaVaiTro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 7. NhomNguoiDung  (PDT/TTDTXS/CVHT/CNHP - vai tro nghiep vu phu them)
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
-- 8. ChuongTrinhDaoTao
--    NguoiDuyet KHAC NguoiTao (validate o service); TrangThai DaDuyet =>
--    auto sinh LopHocPhan theo SoLopDuKien.
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
-- 9. BCN_ThanhVien  (@EmbeddedId: MaCTDT + MaGV + ChucDanh)
--    Moi CTDT phai co duy nhat 1 ChuNhiem (validate nghiep vu).
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
-- 10. HocPhan
--    LoaiHocPhan: LyThuyet | ThucHanh | DoAn | ThucTap | KienTap
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
-- 11. DoiNguGiangVienHP  (@EmbeddedId: MaHocPhan + MaGiangVien)
--     Service tu dong INSERT CNHP khi tao HP. Cac GV khac do CNHP bo sung sau.
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
-- 12. LopHanhChinh
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
-- 13. SinhVien
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
-- 14. CTDT_HocPhan  (@EmbeddedId: MaCTDT + MaHocPhan)
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
-- 15. LopHocPhan  (@EmbeddedId: MaCTDT + MaHocPhan + MaHocKy + MaLopHocPhan)
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
    CONSTRAINT fk_lhp_gv     FOREIGN KEY (MaGiangVien)       REFERENCES GiangVien(MaGV),
    CONSTRAINT chk_lhp_siso_toida    CHECK (SiSoToiDa  BETWEEN 30 AND 60),
    CONSTRAINT chk_lhp_siso_thucte   CHECK (SiSoThucTe >= 0 AND SiSoThucTe <= SiSoToiDa)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 16. DanhSachSinhVienLopHocPhan  (DSSV-LHP)
--     DaCanhBao=1 -> CVHT cua lop nhan email canh bao (Phase 4).
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
-- 17. DotKienTap
--     1 lop -> 1 dot/hocky -> 1 DN tiep don. Ket qua tham gia luu trong
--     DanhSachSinhVienKienTap.DaThamGia (toggle BIT).
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
    CONSTRAINT fk_dotkt_lophc      FOREIGN KEY (MaLopHC)       REFERENCES LopHanhChinh(MaLopHC),
    CONSTRAINT fk_dotkt_hocky      FOREIGN KEY (MaHocKy)       REFERENCES HocKyNamHoc(MaHocKy),
    CONSTRAINT fk_dotkt_gv         FOREIGN KEY (MaGVPhuTrach)  REFERENCES GiangVien(MaGV),
    CONSTRAINT fk_dotkt_dn         FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep),
    CONSTRAINT fk_dotkt_nguoitao   FOREIGN KEY (NguoiTao)      REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT fk_dotkt_nguoiduyet FOREIGN KEY (NguoiDuyet)    REFERENCES NguoiDung(MaNguoiDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 18. DanhSachSinhVienKienTap  (@EmbeddedId: MaDotKT + MaSV)
--     Auto-add SV trang thai DangHoc cua lop khi tao Dot.
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
-- 19. DotThucTap
--     Cap (MaCTDT, MaHocPhan) tham chieu CTDT_HocPhan loai ThucTap/KienTap.
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
    CONSTRAINT fk_dottt_ctdthp     FOREIGN KEY (MaCTDT, MaHocPhan) REFERENCES CTDT_HocPhan(MaCTDT, MaHocPhan),
    CONSTRAINT fk_dottt_hocky      FOREIGN KEY (MaHocKy)           REFERENCES HocKyNamHoc(MaHocKy),
    CONSTRAINT fk_dottt_nguoitao   FOREIGN KEY (NguoiTao)          REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT fk_dottt_nguoiduyet FOREIGN KEY (NguoiDuyet)        REFERENCES NguoiDung(MaNguoiDung)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 20. DanhSachThucTap
--     LoaiThucTap: Truong | DoanhNghiep
--     MaDoanhNghiep NULL khi LoaiThucTap=Truong, NOT NULL khi DoanhNghiep
--     (validate o service — khong constraint o DB de cho phep "switch" loai
--      truoc khi chot DN trong workflow).
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
    CONSTRAINT fk_dstt_dn    FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep),
    CONSTRAINT uk_dstt_dot_sv UNIQUE (MaDotTT, MaSV)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 21. KetQuaThucTap  (Phase 7 refactor)
--
--     **CHANGE QUAN TRONG**: MaNguoiDanhGia FK -> NguoiDung(MaNguoiDung)
--     thay vi GiangVien(MaGV).
--
--     Ly do: vai tro 'DN' (cot 1 cua LoaiThucTap=DoanhNghiep) phai do nhan
--     vien doanh nghiep (NguoiDung loaiNguoiDung=DoanhNghiep, co record
--     NhanVienDoanhNghiep) cham. Truoc day phai gia lap GV001 -> bao cao
--     thong ke nguoi danh gia bi nhieu, audit khong tin cay.
--
--     Validate o service:
--       MaVaiTro = 'DN'                    -> MaNguoiDanhGia phai la NV DN
--                                             (co row NhanVienDoanhNghiep)
--                                             VA thuoc dung DN cua DanhSachThucTap
--                                             (hoac DN cho phep "thinh-mentor" cheo)
--       MaVaiTro IN ('GV_HD','GV_PB','CVHT','GV')  -> MaNguoiDanhGia phai la GV
--                                             (co row GiangVien)
--
--     UNIQUE(MaThucTap, MaVaiTro): moi vai tro chi 1 row / SV.
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
    CONSTRAINT fk_kqtt_dstt           FOREIGN KEY (MaThucTap)      REFERENCES DanhSachThucTap(MaThucTap) ON DELETE CASCADE,
    CONSTRAINT fk_kqtt_vt             FOREIGN KEY (MaVaiTro)       REFERENCES VaiTroThucTap(MaVaiTro),
    CONSTRAINT fk_kqtt_nguoidanhgia   FOREIGN KEY (MaNguoiDanhGia) REFERENCES NguoiDung(MaNguoiDung),
    CONSTRAINT chk_kqtt_diem CHECK (Diem IS NULL OR (Diem >= 0 AND Diem <= 10)),
    CONSTRAINT uk_kqtt_thuctap_vaitro UNIQUE (MaThucTap, MaVaiTro)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- Kiem tra nhanh: liet ke bang vua tao
-- =============================================================================
SHOW TABLES;
