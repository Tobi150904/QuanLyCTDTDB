-- =============================================================================
-- 01_create_tables.sql  —  SCHEMA FULL  (Phase 1 -> Phase 7 REFACTORED)
--                          NV DN lien ket truc tiep tu NguoiDung.MaDoanhNghiep
--                          (BO BANG NhanVienDoanhNghiep — Java code da dong bo)
-- =============================================================================
-- He Thong Quan Ly Chuong Trinh Dao Tao Xuat Sac (Truong DH Nha Trang).
-- Schema 1-1 voi @Entity JPA + 02_seed_data.sql.
--
-- Cach dung (XAMPP / phpMyAdmin):
--   1) Tao DB "QuanLyCTDTDB" charset utf8mb4_unicode_ci.
--   2) Tab SQL > paste file nay > Go.
--   3) Chay tiep scripts/02_seed_data.sql.
--
-- ===== THAY DOI LON SO VOI BAN TRUOC (refactor — bo bang NhanVienDoanhNghiep) =====
--
-- 1. **BO bang NhanVienDoanhNghiep** — He thong cua truong khong can quan ly
--    nhan su cua doanh nghiep (chuc vu / phong ban / chuyen mon). Mot NV DN
--    chi can co tai khoan trong NguoiDung (LoaiNguoiDung='DoanhNghiep') de
--    login + cham diem thuc tap. Lien ket "NV nay thuoc DN nao" duoc dua
--    truc tiep len NguoiDung qua cot mới `MaDoanhNghiep` (nullable FK).
--
-- 2. **NguoiDung.MaDoanhNghiep** (moi):
--      - LoaiNguoiDung='DoanhNghiep' -> bat buoc NOT NULL (ai cham cho DN nao).
--      - LoaiNguoiDung khac          -> luon NULL.
--      Rang buoc nay duoc bao dam boi CHECK constraint chk_nd_dn_required.
--
-- 3. **KetQuaThucTap.MaNguoiDanhGia FK -> NguoiDung** (giu nguyen tu Phase 7).
--    Cho phep ca GV (vai tro GV_HD/GV_PB/CVHT) lan NV DN (vai tro DN) deu la
--    nguoi danh gia hop le. Validate role-based o service layer.
--
-- 4. **Case A (NV DN giang day thinh giang)**: 1 NguoiDung loai DoanhNghiep
--    co the dong thoi co record GiangVien (LoaiGiangVien=GiangVienThinhGiang).
--    Khi do dropdown chon nguoi cham + dropdown phan giang vien hoc phan deu
--    nhin thay user nay (qua 2 truy van khac nhau). UI khong can phan biet
--    "NV DN" voi "GV thuong" — chi can dung dung repository.
--
-- 5. **VaiTroThucTap.TenVaiTro chuan hoa**:
--      GV_HD : "GV Giam Sat / Huong Dan"  (truong=cot1, DN=cot2)
--      GV_PB : "GV Phan Bien"             (truong=cot2)
--      DN    : "Nhan Vien Doanh Nghiep"   (DN=cot1)
--      CVHT  : "Co Van Hoc Tap"           (tham khao, ngoai 2 cot diem)
--      GV    : (legacy, giu de migrate cu)
--
-- ===== CHINH SACH FK — IMMUTABLE KEYS =====
--   Tat ca FK khong ON UPDATE => default RESTRICT.
--   Business key (MaGV, MaSV, MaHP, MaCTDT, MaHocKy, MaDoanhNghiep, MaLopHC,
--   MaNguoiDung) la BAT BIEN sau khi INSERT.
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
DROP TABLE IF EXISTS NhanVienDoanhNghiep;        -- legacy: drop neu schema cu con ton tai
DROP TABLE IF EXISTS GiangVien;
DROP TABLE IF EXISTS NguoiDung;                   -- NguoiDung now FK -> DoanhNghiep
DROP TABLE IF EXISTS DoanhNghiep;
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
-- 2. DoanhNghiep  (doc lap — phai tao TRUOC NguoiDung vi NguoiDung FK -> DN)
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
-- 3. NguoiDung  (root identity: AD/GV/SV/DN)
--    LoaiNguoiDung: Admin | GiangVien | SinhVien | DoanhNghiep
--    Mat khau Password@123 (BCrypt cost=10) cho seed test.
--
--    MaDoanhNghiep (refactor — bo bang NhanVienDoanhNghiep):
--      - LoaiNguoiDung='DoanhNghiep' -> NOT NULL: chi ro NV/dai dien thuoc DN nao.
--      - LoaiNguoiDung khac          -> luon NULL.
--      - Bao dam boi CHECK chk_nd_dn_required (yeu cau MySQL >= 8.0.16).
--      - Truong khong quan ly chi tiet "chuc vu / phong ban / chuyen mon" cua
--        NV DN — nhung thong tin ay khong can cho nghiep vu cham diem thuc tap.
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
    MaDoanhNghiep  VARCHAR(20)  NULL,
    created_at     DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at     DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaNguoiDung),
    UNIQUE KEY uk_nguoidung_tendangnhap (TenDangNhap),
    UNIQUE KEY uk_nguoidung_email (Email),
    CONSTRAINT fk_nguoidung_doanhnghiep
        FOREIGN KEY (MaDoanhNghiep) REFERENCES DoanhNghiep(MaDoanhNghiep),
    CONSTRAINT chk_nd_dn_required CHECK (
        LoaiNguoiDung <> 'DoanhNghiep' OR MaDoanhNghiep IS NOT NULL
    )
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- 4. GiangVien  <- NguoiDung
--    LoaiGiangVien: GiangVienTruong | GiangVienThinhGiang | DoanhNghiep
--    Case A: 1 NguoiDung loai DoanhNghiep co the dong thoi co GiangVien record
--    (LoaiGiangVien=GiangVienThinhGiang) -> NV DN thinh giang tai truong.
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
--     vien doanh nghiep (NguoiDung loaiNguoiDung=DoanhNghiep) cham. Truoc day
--     phai gia lap GV001 -> bao cao thong ke nguoi danh gia bi nhieu, audit
--     khong tin cay.
--
--     Validate o service:
--       MaVaiTro = 'DN'                            -> NguoiDung loai DoanhNghiep
--                                                     (cung NV/dai dien cua DN tiep nhan SV).
--       MaVaiTro IN ('GV_HD','GV_PB','CVHT','GV')  -> NguoiDung loai GiangVien
--                                                     (co row GiangVien tuong ung).
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

-- 02_seed_data.sql  —  SEED FULL  (Production-realistic timeline 2026-01-15)
-- =============================================================================
-- He Thong Quan Ly Chuong Trinh Dao Tao Xuat Sac (Truong DH Nha Trang).
-- Chay SAU KHI 01_create_tables.sql da chay xong.
--
-- ===== SUA LOI (Patch 2026-05-06) =====
--   Fix lon sau report [UnhandledException] path=/thuc-tap:
--     \"No enum constant com.ntu.quanlyctdtdb.enums.TrangThaiDotTT.DaThucHien\"
--
--   Nguyen nhan: seed cu dung gia tri khong hop le voi enum:
--     * DotThucTap.TrangThai = 'DaThucHien'   -> KHONG co trong enum TrangThaiDotTT
--     * DanhSachThucTap.TrangThai = 'DaHoanThanh' -> KHONG co trong enum TrangThaiThucTap
--
--   Giai thich ngu nghia:
--     - DotKienTap (kien tap = 1 buoi tham quan DN):
--         state-machine: ChuanBi -> ChoDuyet -> DaDuyet -> DaThucHien -> DaHuy
--         \"DaThucHien\" la trang thai CUOI (sau khi buoi tham quan dien ra xong).
--
--     - DotThucTap (thuc tap = 2-3 thang lam viec):
--         state-machine: ChuanBi -> ChoDuyet -> DaDuyet -> DangThucHien -> DaKetThuc -> DaHuy
--         Co ca \"DangThucHien\" (dang dien ra) VA \"DaKetThuc\" (da ket thuc) vi
--         thuc tap co thoi luong dai, can phan biet \"dang\" va \"ket thuc\".
--
--   Semantic khac nhau: 2 entity khac nhau, 2 workflow khac nhau. Khong the
--   dung chung ten enum. Seed truoc day nham lan giua 2 bang gay crash.
--
--   Fix da ap dung:
--     * DotThucTap (dot 1, thoi gian 2025-10-01..2025-12-30):
--         'DaThucHien' (sai) -> 'DaKetThuc' (dung — dot da ket thuc tu 2025-12-30).
--     * DanhSachThucTap (9 SV cua dot 1):
--         'DaHoanThanh' (sai) -> 'DaKetThuc' (dung — dong bo cascade tu dot).
--
-- ===== TIMELINE NGHIEP VU =====
--   \"Hom nay\" = 2026-01-15 (giua HK2 nam hoc 2025-2026).
--
--   Hoc ky:
--     HK2-2023 (NH 2023-2024)   DaKetThuc
--     HK1-2024 (NH 2024-2025)   DaKetThuc
--     HK2-2024 (NH 2024-2025)   DaKetThuc
--     HK1-2025 (NH 2025-2026)   DaKetThuc   <-- vua ket thuc 09/01/2026
--     HK2-2025 (NH 2025-2026)   DangDienRa  <-- hien tai (bat dau 19/01/2026)
--     HK1-2026 (NH 2026-2027)   SapDienRa   <-- du kien
--
--   Khoa sinh vien:
--     K22 (2022-2026): nam 4, dang HK8 = HK2-2025 -> hoc HP-KLTN (khoa luan)
--     K23 (2023-2027): nam 3, dang HK6 = HK2-2025 -> hoc HP-PTTK, HP-ATTT
--     K24 (2024-2028): nam 2, dang HK4 = HK2-2025 -> hoc HP-TTDL, HP-AI
--     K25 (2025-2029): nam 1, dang HK2 = HK2-2025 -> hoc HP-GTDL, HP-CSDL
--
--   Tat ca cac dot KT/TT o HK1-2025 da hoan thanh.
--   Dot thuc tap bo sung o HK2-2025 sap dien ra (DaDuyet).
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

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
TRUNCATE TABLE GiangVien;
TRUNCATE TABLE NguoiDung;
TRUNCATE TABLE DoanhNghiep;
TRUNCATE TABLE HocKyNamHoc;

-- =============================================================================
-- 1. HocKyNamHoc (timeline 4 HK da hoc + 1 HK hien tai + 1 HK sap toi)
-- =============================================================================
INSERT INTO HocKyNamHoc (MaHocKy, TenHocKy, NgayBatDau, NgayKetThuc, TrangThai) VALUES
('HK2-2023', 'Hoc Ky 2 Nam 2023-2024', '2024-01-22', '2024-05-31', 'DaKetThuc'),
('HK1-2024', 'Hoc Ky 1 Nam 2024-2025', '2024-09-02', '2025-01-10', 'DaKetThuc'),
('HK2-2024', 'Hoc Ky 2 Nam 2024-2025', '2025-01-20', '2025-05-30', 'DaKetThuc'),
('HK1-2025', 'Hoc Ky 1 Nam 2025-2026', '2025-09-01', '2026-01-09', 'DaKetThuc'),   -- da ket thuc
('HK2-2025', 'Hoc Ky 2 Nam 2025-2026', '2026-01-19', '2026-05-29', 'DangDienRa'), -- hien tai
('HK1-2026', 'Hoc Ky 1 Nam 2026-2027', '2026-09-07', '2027-01-15', 'SapDienRa');

-- =============================================================================
-- 2. DoanhNghiep (8 DN hoat dong + 1 DN tam ngung)
-- =============================================================================
INSERT INTO DoanhNghiep (MaDoanhNghiep, TenDoanhNghiep, LinhVuc, NguoiDaiDien, Email, SoDienThoai, DiaChiDN, TrangThai) VALUES
('DN001', 'Cong ty TNHH Phan mem FPT (FPT Software)', 'Phan mem & Dich vu CNTT', 'Nguyen Van Hung', 'tuyendung@fpt.com.vn', '0243768888', 'Toa nha FPT, So 17 Duy Tan, Cau Giay, Ha Noi', 'DangHopTac'),
('DN002', 'Cong ty Co phan VNG', 'Game & Giai tri so', 'Le Thi Thu', 'thuctap@vng.com.vn', '0283962828', 'So 182 Le Dai Hanh, Phuong 15, Quan 11, TP. HCM', 'DangHopTac'),
('DN003', 'Cong ty TNHH TMA Solutions', 'Phan mem & Outsourcing', 'Tran Minh Dang', 'hr@tma.com.vn', '0283997300', 'Cong vien Phan mem Quang Trung, Quan 12, TP. HCM', 'DangHopTac'),
('DN004', 'Tong Cong ty Giai phap Doanh nghiep Viettel', 'Vien thong & Ha tang CNTT', 'Pham Thi Lan', 'tuyendung@viettel.com.vn', '0243628400', 'So 1 Tran Huu Duc, My Dinh 2, Nam Tu Liem, Ha Noi', 'DangHopTac'),
('DN005', 'Cong ty Co phan MISA', 'Phan mem quan ly doanh nghiep', 'Dinh Thi Thuy Ha', 'tuyendung@misa.com.vn', '0243762868', 'Toa nha MISA, 218 Doi Can, Ba Dinh, Ha Noi', 'DangHopTac'),
('DN006', 'Cong ty TNHH KMS Technology Vietnam', 'Phan mem gia cong cho US', 'Doan Quoc Viet', 'careers@kms-technology.com', '0283811999', 'Toa QTSC 9, Cong vien Phan mem Quang Trung, TP. HCM', 'DangHopTac'),
('DN007', 'Cong ty Co phan Giai phap Thanh toan VNPay', 'Thanh toan dien tu & Fintech', 'Hoang Minh Tuan', 'tuyendung@vnpay.vn', '0247108998', 'Tang 8, Toa nha Mipec, 229 Tay Son, Dong Da, Ha Noi', 'DangHopTac'),
('DN008', 'Cong ty Co phan Tiki', 'Thuong mai dien tu', 'Phan Thi Thu Ha', 'hr@tiki.vn', '0283456789', 'So 52 Ut Tich, Phuong 4, Quan Tan Binh, TP. HCM', 'DangHopTac'),
('DN009', 'Cong ty TNHH NashTech Viet Nam', 'Phan mem & Dich vu CNTT', 'Vo Anh Tuan', 'hr@nashtechglobal.com', '0283815555', 'So 117 Nguyen Cuu Van, Binh Thanh, TP. HCM', 'TamNgung');

-- =============================================================================
-- 3. NguoiDung
--    Password cho tat ca (test): Password@123  (BCrypt cost=12).
--    Hash: $2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa
-- =============================================================================
INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, SoDienThoai, TrangThaiTK, LoaiNguoiDung, MaDoanhNghiep) VALUES
('AD001', 'admin', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'admin@ntu.edu.vn', 'Quan Tri Vien He Thong', '0909000001', 1, 'Admin', NULL),
('GV001', 'tran.van.an', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'tran.van.an@ntu.edu.vn', 'PGS.TS. Tran Van An', '0912340001', 1, 'GiangVien', NULL),
('GV002', 'le.thi.binh', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'le.thi.binh@ntu.edu.vn', 'TS. Le Thi Binh', '0912340002', 1, 'GiangVien', NULL),
('GV003', 'nguyen.van.cuong', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'nguyen.van.cuong@ntu.edu.vn', 'TS. Nguyen Van Cuong', '0912340003', 1, 'GiangVien', NULL),
('GV004', 'pham.thi.dung', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'pham.thi.dung@ntu.edu.vn', 'ThS. Pham Thi Dung', '0912340004', 1, 'GiangVien', NULL),
('GV005', 'hoang.van.em', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'hoang.van.em@ntu.edu.vn', 'ThS. Hoang Van Em', '0912340005', 1, 'GiangVien', NULL),
('GV006', 'vu.thi.giang', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'vu.thi.giang@ntu.edu.vn', 'TS. Vu Thi Giang', '0912340006', 1, 'GiangVien', NULL),
('GV007', 'do.minh.hieu', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'do.minh.hieu@ntu.edu.vn', 'ThS. Do Minh Hieu', '0912340007', 1, 'GiangVien', NULL),
('GV008', 'bui.thanh.ha', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'bui.thanh.ha@ntu.edu.vn', 'ThS. Bui Thanh Ha', '0912340008', 1, 'GiangVien', NULL),
('GV009', 'ngo.thi.lan', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'ngo.thi.lan@ntu.edu.vn', 'TS. Ngo Thi Lan', '0912340009', 1, 'GiangVien', NULL),
('GV010', 'dang.van.minh', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'dang.van.minh@ntu.edu.vn', 'ThS. Dang Van Minh', '0912340010', 1, 'GiangVien', NULL),
('GV011', 'phan.thi.ngoc', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'phan.thi.ngoc@ntu.edu.vn', 'TS. Phan Thi Ngoc', '0912340011', 1, 'GiangVien', NULL),
('GV012', 'ly.quoc.phong', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'ly.quoc.phong@ntu.edu.vn', 'ThS. Ly Quoc Phong', '0912340012', 1, 'GiangVien', NULL),
-- K25 (nam 1)
('SV2025001', 'sv.2025001', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2025001@sv.ntu.edu.vn', 'Nguyen Hoang An', '0978000001', 1, 'SinhVien', NULL),
('SV2025002', 'sv.2025002', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2025002@sv.ntu.edu.vn', 'Tran Phuong Anh', '0978000002', 1, 'SinhVien', NULL),
('SV2025003', 'sv.2025003', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2025003@sv.ntu.edu.vn', 'Le Quang Bao', '0978000003', 1, 'SinhVien', NULL),
('SV2025004', 'sv.2025004', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2025004@sv.ntu.edu.vn', 'Pham Hai Dang', '0978000004', 1, 'SinhVien', NULL),
('SV2025005', 'sv.2025005', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2025005@sv.ntu.edu.vn', 'Hoang Thi Hanh', '0978000005', 1, 'SinhVien', NULL),
('SV2025006', 'sv.2025006', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2025006@sv.ntu.edu.vn', 'Vu Minh Khanh', '0978000006', 1, 'SinhVien', NULL),
('SV2025007', 'sv.2025007', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2025007@sv.ntu.edu.vn', 'Do Tuan Linh', '0978000007', 1, 'SinhVien', NULL),
('SV2025008', 'sv.2025008', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2025008@sv.ntu.edu.vn', 'Bui Khanh My', '0978000008', 1, 'SinhVien', NULL),
-- K24 (nam 2)
('SV2024001', 'sv.2024001', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024001@sv.ntu.edu.vn', 'Nguyen Thi Hoa', '0978001001', 1, 'SinhVien', NULL),
('SV2024002', 'sv.2024002', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024002@sv.ntu.edu.vn', 'Tran Minh Khoa', '0978001002', 1, 'SinhVien', NULL),
('SV2024003', 'sv.2024003', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024003@sv.ntu.edu.vn', 'Le Quoc Long', '0978001003', 1, 'SinhVien', NULL),
('SV2024004', 'sv.2024004', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024004@sv.ntu.edu.vn', 'Pham Ha My', '0978001004', 1, 'SinhVien', NULL),
('SV2024005', 'sv.2024005', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024005@sv.ntu.edu.vn', 'Hoang Tuan Nam', '0978001005', 1, 'SinhVien', NULL),
('SV2024006', 'sv.2024006', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024006@sv.ntu.edu.vn', 'Vu Thi Ngan', '0978001006', 1, 'SinhVien', NULL),
('SV2024007', 'sv.2024007', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024007@sv.ntu.edu.vn', 'Do Anh Quan', '0978001007', 1, 'SinhVien', NULL),
('SV2024008', 'sv.2024008', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024008@sv.ntu.edu.vn', 'Bui Thu Thao', '0978001008', 1, 'SinhVien', NULL),
('SV2024009', 'sv.2024009', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024009@sv.ntu.edu.vn', 'Ngo Van Thien', '0978001009', 1, 'SinhVien', NULL),
('SV2024010', 'sv.2024010', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2024010@sv.ntu.edu.vn', 'Ly Thanh Vy', '0978001010', 1, 'SinhVien', NULL),
-- K23 (nam 3)
('SV2023001', 'sv.2023001', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023001@sv.ntu.edu.vn', 'Pham Ngoc Mai', '0978002001', 1, 'SinhVien', NULL),
('SV2023002', 'sv.2023002', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023002@sv.ntu.edu.vn', 'Hoang Thi Nhu', '0978002002', 1, 'SinhVien', NULL),
('SV2023003', 'sv.2023003', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023003@sv.ntu.edu.vn', 'Do Quoc Tuan', '0978002003', 1, 'SinhVien', NULL),
('SV2023004', 'sv.2023004', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023004@sv.ntu.edu.vn', 'Truong Quoc Bao', '0978002004', 1, 'SinhVien', NULL),
('SV2023005', 'sv.2023005', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023005@sv.ntu.edu.vn', 'Nguyen Thao Vy', '0978002005', 1, 'SinhVien', NULL),
('SV2023006', 'sv.2023006', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023006@sv.ntu.edu.vn', 'Le Xuan Khang', '0978002006', 1, 'SinhVien', NULL),
('SV2023007', 'sv.2023007', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023007@sv.ntu.edu.vn', 'Vo Thuy Duong', '0978002007', 1, 'SinhVien', NULL),
('SV2023008', 'sv.2023008', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023008@sv.ntu.edu.vn', 'Tran Huy Hoang', '0978002008', 1, 'SinhVien', NULL),
('SV2023009', 'sv.2023009', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023009@sv.ntu.edu.vn', 'Phan Kim Anh', '0978002009', 1, 'SinhVien', NULL),
('SV2023010', 'sv.2023010', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2023010@sv.ntu.edu.vn', 'Bui Cong Dat', '0978002010', 1, 'SinhVien', NULL),
-- K22 (nam 4 — KLTN)
('SV2022001', 'sv.2022001', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022001@sv.ntu.edu.vn', 'Vo Thanh Phong', '0978003001', 1, 'SinhVien', NULL),
('SV2022002', 'sv.2022002', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022002@sv.ntu.edu.vn', 'Bui Thi Quynh', '0978003002', 1, 'SinhVien', NULL),
('SV2022003', 'sv.2022003', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022003@sv.ntu.edu.vn', 'Ngo Van Tan', '0978003003', 1, 'SinhVien', NULL),
('SV2022004', 'sv.2022004', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022004@sv.ntu.edu.vn', 'Dao Thi Uyen', '0978003004', 1, 'SinhVien', NULL),
('SV2022005', 'sv.2022005', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022005@sv.ntu.edu.vn', 'Ly Thi Van', '0978003005', 1, 'SinhVien', NULL),
('SV2022006', 'sv.2022006', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022006@sv.ntu.edu.vn', 'Le Hong Son', '0978003006', 1, 'SinhVien', NULL),
('SV2022007', 'sv.2022007', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022007@sv.ntu.edu.vn', 'Tran My Linh', '0978003007', 1, 'SinhVien', NULL),
('SV2022008', 'sv.2022008', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022008@sv.ntu.edu.vn', 'Nguyen Quang Huy', '0978003008', 1, 'SinhVien', NULL),
('SV2022009', 'sv.2022009', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022009@sv.ntu.edu.vn', 'Pham Hoang Kim', '0978003009', 1, 'SinhVien', NULL),
('SV2022010', 'sv.2022010', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'sv2022010@sv.ntu.edu.vn', 'Hoang Gia Bao', '0978003010', 1, 'SinhVien', NULL),
-- DN — tai khoan dai dien DN (login de xem chi tiet DN)
('DN001', 'dn.fpt', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'tuyendung@fpt.com.vn', 'FPT Software', '0243768888', 1, 'DoanhNghiep', 'DN001'),
('DN002', 'dn.vng', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'thuctap@vng.com.vn', 'VNG Corporation', '0283962828', 1, 'DoanhNghiep', 'DN002'),
('DN003', 'dn.tma', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'hr@tma.com.vn', 'TMA Solutions', '0283997300', 1, 'DoanhNghiep', 'DN003'),
('DN004', 'dn.viettel', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'tuyendung@viettel.com.vn', 'Viettel Solutions', '0243628400', 1, 'DoanhNghiep', 'DN004'),
('DN005', 'dn.misa', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'tuyendung@misa.com.vn', 'Cong ty Co phan MISA', '0243762868', 1, 'DoanhNghiep', 'DN005'),
('DN006', 'dn.kms', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'careers@kms-technology.com', 'KMS Technology', '0283811999', 1, 'DoanhNghiep', 'DN006'),
('DN007', 'dn.vnpay', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'tuyendung@vnpay.vn', 'VNPay', '0247108998', 1, 'DoanhNghiep', 'DN007'),
-- NV — cham diem thuc tap (1 NV / DN)
('NV001', 'nv.le.van.hung', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'le.van.hung@fpt.com.vn', 'Le Van Hung', '0987001001', 1, 'DoanhNghiep', 'DN001'),
('NV002', 'nv.tran.thi.mai', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'tran.thi.mai@vng.com.vn', 'Tran Thi Mai', '0987001002', 1, 'DoanhNghiep', 'DN002'),
('NV003', 'nv.hoang.van.quoc', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'hoang.van.quoc@tma.com.vn', 'Hoang Van Quoc', '0987001003', 1, 'DoanhNghiep', 'DN003'),
('NV004', 'nv.dao.anh.tu', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'dao.anh.tu@viettel.com.vn', 'Dao Anh Tu', '0987001004', 1, 'DoanhNghiep', 'DN004'),
('NV005', 'nv.vu.thi.linh', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'vu.thi.linh@misa.com.vn', 'Vu Thi Linh', '0987001005', 1, 'DoanhNghiep', 'DN005'),
('NV006', 'nv.pham.quoc.khang', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'pham.quoc.khang@kms.com.vn', 'Pham Quoc Khang', '0987001006', 1, 'DoanhNghiep', 'DN006'),
('NV007', 'nv.bui.hoang.nam', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'bui.hoang.nam@vnpay.vn', 'Bui Hoang Nam', '0987001007', 1, 'DoanhNghiep', 'DN007'),
-- NV DN cua DN008 (Tiki) va DN003 (TMA) de match du lieu KetQuaThucTap
('NV008', 'nv.dang.thi.tuyet', '$2a$12$snr3FhPtUfxrae1frO3fiOLSA0XzpWsiz/LwpUohgJBBlSfPzQIVa', 'dang.thi.tuyet@tiki.vn', 'Dang Thi Tuyet', '0987001008', 1, 'DoanhNghiep', 'DN008');

-- =============================================================================
-- 4. GiangVien  (12 GV truong + 2 GV thinh giang tu NV DN)
-- =============================================================================
INSERT INTO GiangVien (MaGV, MaNguoiDung, HocHam, HocVi, ChuyenNganh, LoaiGiangVien) VALUES
('GV001', 'GV001', 'Pho Giao Su', 'Tien Si', 'Cong Nghe Phan Mem', 'GiangVienTruong'),
('GV002', 'GV002', NULL, 'Tien Si', 'Mang May Tinh', 'GiangVienTruong'),
('GV003', 'GV003', NULL, 'Tien Si', 'He Thong Thong Tin', 'GiangVienTruong'),
('GV004', 'GV004', NULL, 'Thac Si', 'Cong Nghe Phan Mem', 'GiangVienTruong'),
('GV005', 'GV005', NULL, 'Thac Si', 'Co So Du Lieu', 'GiangVienTruong'),
('GV006', 'GV006', NULL, 'Tien Si', 'Khoa Hoc Du Lieu', 'GiangVienTruong'),
('GV007', 'GV007', NULL, 'Thac Si', 'Lap Trinh Web', 'GiangVienTruong'),
('GV008', 'GV008', NULL, 'Thac Si', 'Tri Tue Nhan Tao', 'GiangVienTruong'),
('GV009', 'GV009', NULL, 'Tien Si', 'An Toan Thong Tin', 'GiangVienTruong'),
('GV010', 'GV010', NULL, 'Thac Si', 'Ky Thuat Phan Mem', 'GiangVienThinhGiang'),
('GV011', 'GV011', NULL, 'Tien Si', 'Phan Tich Thiet Ke', 'GiangVienTruong'),
('GV012', 'GV012', NULL, 'Thac Si', 'Lap Trinh Di Dong', 'GiangVienTruong'),
('GV013', 'NV001', NULL, 'Thac Si', 'Lap Trinh Web Doanh Nghiep', 'GiangVienThinhGiang'),
('GV014', 'NV002', NULL, 'Thac Si', 'Tri Tue Nhan Tao Ung Dung', 'GiangVienThinhGiang');

-- =============================================================================
-- 5. VaiTroThucTap (danh muc vai tro cham diem)
-- =============================================================================
INSERT INTO VaiTroThucTap (MaVaiTro, TenVaiTro, MoTa) VALUES
('GV', 'Giang Vien (legacy)', 'Vai tro don nhat truoc Phase 7'),
('DN', 'Nhan Vien Doanh Nghiep', 'Cot diem 1 cho LoaiThucTap=DoanhNghiep'),
('CVHT', 'Co Van Hoc Tap', 'Phan hoi tham khao'),
('GV_HD', 'GV Giam Sat / Huong Dan', 'Cot 1 (Truong) hoac Cot 2 (DoanhNghiep)'),
('GV_PB', 'GV Phan Bien', 'Chi ap dung cho LoaiThucTap=Truong. Cot 2.');

-- =============================================================================
-- 6. NhomNguoiDung  (vai tro nghiep vu phu: PDT, TTDTXS, CVHT, CNHP)
--   GV001 la vai tro cap truong (PDT + TTDTXS).
--   GV003..GV010 la co van hoc tap (CVHT) cua 8 lop hanh chinh.
-- =============================================================================
INSERT INTO NhomNguoiDung (MaNguoiDung, VaiTro) VALUES
('GV001', 'PDT'), ('GV001', 'TTDTXS'),
('GV002', 'TTDTXS'), ('GV002', 'CNHP'),
('GV003', 'CVHT'), ('GV004', 'CVHT'), ('GV005', 'CVHT'), ('GV006', 'CVHT'),
('GV007', 'CVHT'), ('GV008', 'CVHT'), ('GV009', 'CVHT'), ('GV010', 'CVHT'),
('GV003', 'CNHP'), ('GV004', 'CNHP'), ('GV005', 'CNHP'), ('GV006', 'CNHP'),
('GV007', 'CNHP'), ('GV008', 'CNHP'), ('GV009', 'CNHP'), ('GV011', 'CNHP');

-- =============================================================================
-- 7. ChuongTrinhDaoTao
-- =============================================================================
INSERT INTO ChuongTrinhDaoTao (MaCTDT, TenCTDT, Khoa, FileWord, TrangThai, NguoiTao, NguoiDuyet, NgayDuyet) VALUES
('CTDT-CNTT-2022', 'CTDT Xuat Sac Nganh CNTT – Khoa 2022-2026', '2022', 'ctdt/CTDT-CNTT-2022_20220801_khung_ctdt.docx', 'DaDuyet', 'GV001', 'GV002', '2022-08-15 09:00:00'),
('CTDT-CNTT-2023', 'CTDT Xuat Sac Nganh CNTT – Khoa 2023-2027', '2023', 'ctdt/CTDT-CNTT-2023_20230801_khung_ctdt.docx', 'DaDuyet', 'GV001', 'GV002', '2023-08-20 10:30:00'),
('CTDT-CNTT-2024', 'CTDT Xuat Sac Nganh CNTT – Khoa 2024-2028', '2024', 'ctdt/CTDT-CNTT-2024_20240715_khung_ctdt.docx', 'DaDuyet', 'GV001', 'GV002', '2024-07-20 14:15:00'),
('CTDT-CNTT-2025', 'CTDT Xuat Sac Nganh CNTT – Khoa 2025-2029', '2025', 'ctdt/CTDT-CNTT-2025_20250710_khung_ctdt.docx', 'DaDuyet', 'GV001', 'GV002', '2025-07-15 10:00:00'),
('CTDT-CNTT-2026', 'CTDT Xuat Sac Nganh CNTT – Khoa 2026-2030', '2026', NULL, 'ChoDuyet', 'GV001', NULL, NULL);

-- =============================================================================
-- 8. BCN_ThanhVien
-- =============================================================================
INSERT INTO BCN_ThanhVien (MaCTDT, MaGV, ChucDanh, NgayBoNhiem, GhiChu) VALUES
('CTDT-CNTT-2022', 'GV001', 'ChuNhiem', '2022-08-01', 'Truong ban'),
('CTDT-CNTT-2022', 'GV002', 'ThuKy', '2022-08-01', NULL),
('CTDT-CNTT-2022', 'GV003', 'UyVien', '2022-08-01', NULL),
('CTDT-CNTT-2023', 'GV001', 'ChuNhiem', '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV004', 'ThuKy', '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV005', 'UyVien', '2023-08-01', NULL),
('CTDT-CNTT-2023', 'GV009', 'UyVien', '2023-08-01', 'Phu trach mang ATTT'),
('CTDT-CNTT-2024', 'GV002', 'ChuNhiem', '2024-07-15', NULL),
('CTDT-CNTT-2024', 'GV006', 'ThuKy', '2024-07-15', NULL),
('CTDT-CNTT-2024', 'GV007', 'UyVien', '2024-07-15', NULL),
('CTDT-CNTT-2025', 'GV001', 'ChuNhiem', '2025-07-15', NULL),
('CTDT-CNTT-2025', 'GV006', 'ThuKy', '2025-07-15', NULL),
('CTDT-CNTT-2025', 'GV008', 'UyVien', '2025-07-15', NULL),
('CTDT-CNTT-2026', 'GV001', 'ChuNhiem', '2025-12-01', 'CTDT du kien'),
('CTDT-CNTT-2026', 'GV011', 'ThuKy', '2025-12-01', NULL);

-- =============================================================================
-- 9. HocPhan
-- =============================================================================
INSERT INTO HocPhan (MaHocPhan, TenHocPhan, SoTinChi, LoaiHocPhan, ChuNhiemHP, FileDeCuong, TrangThai) VALUES
('HP-NNLT', 'Nhap Mon Lap Trinh', 3, 'LyThuyet', 'GV007', 'hocphan/HP-NNLT_20220901_decuong.pdf', 'DaDuyet'),
('HP-GTDL', 'Cau Truc Du Lieu & Giai Thuat', 4, 'LyThuyet', 'GV008', 'hocphan/HP-GTDL_20220901_decuong.pdf', 'DaDuyet'),
('HP-OOP', 'Lap Trinh Huong Doi Tuong', 3, 'LyThuyet', 'GV002', 'hocphan/HP-OOP_20220801_decuong.pdf', 'DaDuyet'),
('HP-CSDL', 'Co So Du Lieu', 3, 'LyThuyet', 'GV005', 'hocphan/HP-CSDL_20220801_decuong.pdf', 'DaDuyet'),
('HP-MMT', 'Mang May Tinh', 3, 'LyThuyet', 'GV002', 'hocphan/HP-MMT_20220801_decuong.pdf', 'DaDuyet'),
('HP-HTTT', 'Phan Tich & Thiet Ke He Thong Thong Tin', 3, 'LyThuyet', 'GV003', 'hocphan/HP-HTTT_20230201_decuong.pdf', 'DaDuyet'),
('HP-LTW', 'Lap Trinh Web', 3, 'LyThuyet', 'GV004', 'hocphan/HP-LTW_20230801_decuong.pdf', 'DaDuyet'),
('HP-TTDL', 'Thuc Hanh Thiet Ke Du Lieu', 2, 'ThucHanh', 'GV005', 'hocphan/HP-TTDL_20230801_decuong.pdf', 'DaDuyet'),
('HP-PTTK', 'Phan Tich Thiet Ke Phan Mem', 3, 'LyThuyet', 'GV011', 'hocphan/HP-PTTK_20230901_decuong.pdf', 'DaDuyet'),
('HP-ATTT', 'An Toan & Bao Mat Thong Tin', 3, 'LyThuyet', 'GV009', 'hocphan/HP-ATTT_20240101_decuong.pdf', 'DaDuyet'),
('HP-AI', 'Nhap Mon Tri Tue Nhan Tao', 3, 'LyThuyet', 'GV006', 'hocphan/HP-AI_20240701_decuong.pdf', 'DaDuyet'),
('HP-KT', 'Kien Tap Doanh Nghiep', 2, 'KienTap', 'GV003', 'hocphan/HP-KT_20230101_decuong.pdf', 'DaDuyet'),
('HP-TT', 'Thuc Tap Cuoi Khoa', 6, 'ThucTap', 'GV001', 'hocphan/HP-TT_20230101_decuong.pdf', 'DaDuyet'),
('HP-KLTN', 'Khoa Luan Tot Nghiep', 10, 'DoAn', 'GV001', 'hocphan/HP-KLTN_20230101_decuong.pdf', 'DaDuyet');

-- =============================================================================
-- 10. DoiNguGiangVienHP (GV giang day HP)
-- =============================================================================
INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien, TrangThai) VALUES
('HP-NNLT', 'GV007', 1), ('HP-NNLT', 'GV004', 1), ('HP-NNLT', 'GV010', 1),
('HP-GTDL', 'GV008', 1), ('HP-GTDL', 'GV011', 1),
('HP-OOP', 'GV002', 1), ('HP-OOP', 'GV004', 1), ('HP-OOP', 'GV010', 1), ('HP-OOP', 'GV012', 1),
('HP-CSDL', 'GV005', 1), ('HP-CSDL', 'GV001', 1), ('HP-CSDL', 'GV003', 1),
('HP-MMT', 'GV002', 1), ('HP-MMT', 'GV006', 1), ('HP-MMT', 'GV009', 1),
('HP-HTTT', 'GV003', 1), ('HP-HTTT', 'GV001', 1), ('HP-HTTT', 'GV011', 1),
('HP-LTW', 'GV004', 1), ('HP-LTW', 'GV007', 1), ('HP-LTW', 'GV012', 1), ('HP-LTW', 'GV013', 1),
('HP-TTDL', 'GV005', 1), ('HP-TTDL', 'GV004', 1),
('HP-PTTK', 'GV011', 1), ('HP-PTTK', 'GV003', 1), ('HP-PTTK', 'GV004', 1),
('HP-ATTT', 'GV009', 1), ('HP-ATTT', 'GV002', 1), ('HP-ATTT', 'GV006', 1),
('HP-AI', 'GV006', 1), ('HP-AI', 'GV008', 1), ('HP-AI', 'GV005', 1), ('HP-AI', 'GV014', 1),
('HP-KT', 'GV003', 1), ('HP-KT', 'GV001', 1), ('HP-KT', 'GV004', 1),
('HP-TT', 'GV001', 1),
('HP-KLTN', 'GV001', 1), ('HP-KLTN', 'GV002', 1), ('HP-KLTN', 'GV005', 1), ('HP-KLTN', 'GV006', 1);

-- =============================================================================
-- 11. LopHanhChinh
-- =============================================================================
INSERT INTO LopHanhChinh (MaLopHC, TenLop, MaCTDT, KhoaHoc, MaCoVan) VALUES
('CNTT-K22A', 'CNTT Xuat Sac K22 – Lop A', 'CTDT-CNTT-2022', '2022', 'GV003'),
('CNTT-K22B', 'CNTT Xuat Sac K22 – Lop B', 'CTDT-CNTT-2022', '2022', 'GV004'),
('CNTT-K23A', 'CNTT Xuat Sac K23 – Lop A', 'CTDT-CNTT-2023', '2023', 'GV005'),
('CNTT-K23B', 'CNTT Xuat Sac K23 – Lop B', 'CTDT-CNTT-2023', '2023', 'GV006'),
('CNTT-K24A', 'CNTT Xuat Sac K24 – Lop A', 'CTDT-CNTT-2024', '2024', 'GV007'),
('CNTT-K24B', 'CNTT Xuat Sac K24 – Lop B', 'CTDT-CNTT-2024', '2024', 'GV008'),
('CNTT-K25A', 'CNTT Xuat Sac K25 – Lop A', 'CTDT-CNTT-2025', '2025', 'GV009'),
('CNTT-K25B', 'CNTT Xuat Sac K25 – Lop B', 'CTDT-CNTT-2025', '2025', 'GV010');

-- =============================================================================
-- 12. SinhVien
-- =============================================================================
INSERT INTO SinhVien (MaSV, MaNguoiDung, MaLopHC, TrangThaiSV) VALUES
('SV2022001', 'SV2022001', 'CNTT-K22A', 'DangHoc'),
('SV2022002', 'SV2022002', 'CNTT-K22A', 'DangHoc'),
('SV2022003', 'SV2022003', 'CNTT-K22A', 'DangHoc'),
('SV2022004', 'SV2022004', 'CNTT-K22A', 'DangHoc'),
('SV2022005', 'SV2022005', 'CNTT-K22A', 'DangHoc'),
('SV2022006', 'SV2022006', 'CNTT-K22B', 'DangHoc'),
('SV2022007', 'SV2022007', 'CNTT-K22B', 'DangHoc'),
('SV2022008', 'SV2022008', 'CNTT-K22B', 'DangHoc'),
('SV2022009', 'SV2022009', 'CNTT-K22B', 'DangHoc'),
('SV2022010', 'SV2022010', 'CNTT-K22B', 'ThoiHoc'),
('SV2023001', 'SV2023001', 'CNTT-K23A', 'DangHoc'),
('SV2023002', 'SV2023002', 'CNTT-K23A', 'DangHoc'),
('SV2023003', 'SV2023003', 'CNTT-K23A', 'DangHoc'),
('SV2023004', 'SV2023004', 'CNTT-K23A', 'DangHoc'),
('SV2023005', 'SV2023005', 'CNTT-K23A', 'DangHoc'),
('SV2023006', 'SV2023006', 'CNTT-K23B', 'DangHoc'),
('SV2023007', 'SV2023007', 'CNTT-K23B', 'DangHoc'),
('SV2023008', 'SV2023008', 'CNTT-K23B', 'DangHoc'),
('SV2023009', 'SV2023009', 'CNTT-K23B', 'DangHoc'),
('SV2023010', 'SV2023010', 'CNTT-K23B', 'BaoLuu'),
('SV2024001', 'SV2024001', 'CNTT-K24A', 'DangHoc'),
('SV2024002', 'SV2024002', 'CNTT-K24A', 'DangHoc'),
('SV2024003', 'SV2024003', 'CNTT-K24A', 'DangHoc'),
('SV2024004', 'SV2024004', 'CNTT-K24A', 'DangHoc'),
('SV2024005', 'SV2024005', 'CNTT-K24A', 'DangHoc'),
('SV2024006', 'SV2024006', 'CNTT-K24B', 'DangHoc'),
('SV2024007', 'SV2024007', 'CNTT-K24B', 'DangHoc'),
('SV2024008', 'SV2024008', 'CNTT-K24B', 'DangHoc'),
('SV2024009', 'SV2024009', 'CNTT-K24B', 'DangHoc'),
('SV2024010', 'SV2024010', 'CNTT-K24B', 'DangHoc'),
('SV2025001', 'SV2025001', 'CNTT-K25A', 'DangHoc'),
('SV2025002', 'SV2025002', 'CNTT-K25A', 'DangHoc'),
('SV2025003', 'SV2025003', 'CNTT-K25A', 'DangHoc'),
('SV2025004', 'SV2025004', 'CNTT-K25A', 'DangHoc'),
('SV2025005', 'SV2025005', 'CNTT-K25B', 'DangHoc'),
('SV2025006', 'SV2025006', 'CNTT-K25B', 'DangHoc'),
('SV2025007', 'SV2025007', 'CNTT-K25B', 'DangHoc'),
('SV2025008', 'SV2025008', 'CNTT-K25B', 'DangHoc');

-- =============================================================================
-- 13. CTDT_HocPhan
-- =============================================================================
INSERT INTO CTDT_HocPhan (MaCTDT, MaHocPhan, HocKyThu, SoLopDuKien, BatBuoc, GhiChu, FileDeCuong) VALUES
('CTDT-CNTT-2022', 'HP-NNLT', 1, 2, 1, 'Nhap mon', NULL),
('CTDT-CNTT-2022', 'HP-OOP', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-GTDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-CSDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-MMT', 3, 1, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-LTW', 3, 2, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-TTDL', 4, 1, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-AI', 4, 1, 0, 'Tu chon', NULL),
('CTDT-CNTT-2022', 'HP-HTTT', 5, 1, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-KT', 5, 1, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-PTTK', 6, 1, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-ATTT', 6, 1, 0, 'Tu chon', NULL),
('CTDT-CNTT-2022', 'HP-TT', 7, 1, 1, NULL, NULL),
('CTDT-CNTT-2022', 'HP-KLTN', 8, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-NNLT', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-OOP', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-GTDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-CSDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-MMT', 3, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-LTW', 3, 2, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-TTDL', 4, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-AI', 4, 1, 0, NULL, NULL),
('CTDT-CNTT-2023', 'HP-HTTT', 5, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-KT', 5, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-PTTK', 6, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-ATTT', 6, 1, 0, NULL, NULL),
('CTDT-CNTT-2023', 'HP-TT', 7, 1, 1, NULL, NULL),
('CTDT-CNTT-2023', 'HP-KLTN', 8, 1, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-NNLT', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-OOP', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-GTDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-CSDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-MMT', 3, 1, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-LTW', 3, 2, 1, NULL, NULL),
('CTDT-CNTT-2024', 'HP-AI', 4, 1, 0, 'Tu chon', NULL),
('CTDT-CNTT-2024', 'HP-TTDL', 4, 1, 1, NULL, NULL),
('CTDT-CNTT-2025', 'HP-NNLT', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2025', 'HP-OOP', 1, 2, 1, NULL, NULL),
('CTDT-CNTT-2025', 'HP-GTDL', 2, 2, 1, NULL, NULL),
('CTDT-CNTT-2025', 'HP-CSDL', 2, 2, 1, NULL, NULL);

-- =============================================================================
-- 14. LopHocPhan
-- =============================================================================
INSERT INTO LopHocPhan (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, MaGiangVien, SiSoToiDa, SiSoThucTe, FileDeCuongChiTiet, TrangThai) VALUES
-- ===== Cac lop qua khu (da dong) =====
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 1, 'GV004', 45, 38, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-LTW',  'HK1-2024', 2, 'GV013', 45, 36, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-HTTT', 'HK1-2024', 1, 'GV003', 45, 40, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-PTTK', 'HK2-2024', 1, 'GV011', 40, 36, NULL, 'DaDong'),
('CTDT-CNTT-2022', 'HP-ATTT', 'HK2-2024', 1, 'GV009', 35, 30, NULL, 'DaDong'),
('CTDT-CNTT-2023', 'HP-MMT',  'HK1-2024', 1, 'GV002', 45, 38, NULL, 'DaDong'),
('CTDT-CNTT-2023', 'HP-LTW',  'HK1-2024', 1, 'GV004', 45, 39, NULL, 'DaDong'),
('CTDT-CNTT-2023', 'HP-LTW',  'HK1-2024', 2, 'GV007', 45, 36, NULL, 'DaDong'),
-- HK1-2025 da ket thuc -> DaDong
('CTDT-CNTT-2022', 'HP-TT',   'HK1-2025', 1, 'GV001', 30, 9,  NULL, 'DaDong'),
('CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, 'GV003', 45, 38, NULL, 'DaDong'),
('CTDT-CNTT-2023', 'HP-KT',   'HK1-2025', 1, 'GV003', 45, 9,  NULL, 'DaDong'),
('CTDT-CNTT-2024', 'HP-MMT',  'HK1-2025', 1, 'GV002', 45, 40, NULL, 'DaDong'),
('CTDT-CNTT-2024', 'HP-LTW',  'HK1-2025', 1, 'GV004', 45, 38, NULL, 'DaDong'),
('CTDT-CNTT-2024', 'HP-LTW',  'HK1-2025', 2, 'GV013', 45, 35, NULL, 'DaDong'),
('CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 1, 'GV007', 45, 40, NULL, 'DaDong'),
('CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 2, 'GV010', 45, 38, NULL, 'DaDong'),
('CTDT-CNTT-2025', 'HP-OOP',  'HK1-2025', 1, 'GV002', 45, 42, NULL, 'DaDong'),
('CTDT-CNTT-2025', 'HP-OOP',  'HK1-2025', 2, 'GV012', 45, 36, NULL, 'DaDong'),

-- ===== Hoc ky hien tai HK2-2025 (DangMo) =====
-- K22: HP-KLTN
('CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, 'GV001', 30, 9, NULL, 'DangMo'),
-- K23: HP-PTTK, HP-ATTT
('CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, 'GV011', 45, 40, NULL, 'DangMo'),
('CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, 'GV009', 45, 38, NULL, 'DangMo'),
-- K24: HP-TTDL, HP-AI
('CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, 'GV005', 30, 28, NULL, 'DangMo'),
('CTDT-CNTT-2024', 'HP-AI',   'HK2-2025', 1, 'GV006', 40, 35, NULL, 'DangMo'),
('CTDT-CNTT-2024', 'HP-AI',   'HK2-2025', 2, 'GV014', 40, 32, NULL, 'DangMo'),
-- K25: HP-GTDL, HP-CSDL
('CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, 'GV008', 45, 40, NULL, 'DangMo'),
('CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 1, 'GV005', 45, 42, NULL, 'DangMo'),
('CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 2, 'GV001', 45, 38, NULL, 'DangMo');

-- =============================================================================
-- 15. DanhSachSinhVienLopHocPhan (ghi danh SV vao LHP)
-- =============================================================================
INSERT INTO DanhSachSinhVienLopHocPhan (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan, NhanXet, DaCanhBao, KetQuaXuLy) VALUES
-- ===== HK1-2024 (K22 hoc LTW) =====
('SV2022001', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, 'Hoc tap tot.', 0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL, 0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL, 0, NULL),
('SV2022004', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, NULL, 0, NULL),
('SV2022005', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, 'Tham gia day du.', 0, NULL),
('SV2022006', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, 'Bo 4 buoi, khong nop bai tap lon.', 1, 'Da tu van 15/10/2024.'),
('SV2022007', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL, 0, NULL),
('SV2022008', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 1, NULL, 0, NULL),
('SV2022009', 'CTDT-CNTT-2022', 'HP-LTW', 'HK1-2024', 2, NULL, 0, NULL),
-- ===== HK1-2025 (K22 HP-TT, K23 HTTT, K24 LTW, K25 NNLT) =====
('SV2023001', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, 'Tham gia tich cuc.', 0, NULL),
('SV2023002', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2023003', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, 'Co gang hoc tap.', 0, NULL),
('SV2023004', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2023005', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2023006', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2023007', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, 'Thuong xuyen di tre.', 1, NULL),
('SV2023008', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2023009', 'CTDT-CNTT-2023', 'HP-HTTT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2024001', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 1, NULL, 0, NULL),
('SV2024002', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 1, NULL, 0, NULL),
('SV2024003', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 1, NULL, 0, NULL),
('SV2024004', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 1, NULL, 0, NULL),
('SV2024005', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 1, NULL, 0, NULL),
('SV2024006', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 2, NULL, 0, NULL),
('SV2024007', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 2, NULL, 0, NULL),
('SV2024008', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 2, NULL, 0, NULL),
('SV2024009', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 2, NULL, 0, NULL),
('SV2024010', 'CTDT-CNTT-2024', 'HP-LTW', 'HK1-2025', 2, NULL, 0, NULL),
('SV2025001', 'CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2025002', 'CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2025003', 'CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2025004', 'CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 1, NULL, 0, NULL),
('SV2025005', 'CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 2, NULL, 0, NULL),
('SV2025006', 'CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 2, 'SV moi, can theo doi.', 0, NULL),
('SV2025007', 'CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 2, NULL, 0, NULL),
('SV2025008', 'CTDT-CNTT-2025', 'HP-NNLT', 'HK1-2025', 2, NULL, 0, NULL),

-- ===== HK2-2025 (hien tai) =====
-- K22 – KLTN
('SV2022001', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
('SV2022002', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
('SV2022003', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
('SV2022004', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
('SV2022005', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
('SV2022006', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
('SV2022007', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
('SV2022008', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
('SV2022009', 'CTDT-CNTT-2022', 'HP-KLTN', 'HK2-2025', 1, NULL, 0, NULL),
-- K23 – PTTK, ATTT
('SV2023001', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023002', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023003', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023004', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023005', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023006', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023007', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023008', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023009', 'CTDT-CNTT-2023', 'HP-PTTK', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023001', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023002', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023003', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023004', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023005', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023006', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023007', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023008', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
('SV2023009', 'CTDT-CNTT-2023', 'HP-ATTT', 'HK2-2025', 1, NULL, 0, NULL),
-- K24 – TTDL, AI
('SV2024001', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024002', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024003', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024004', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024005', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024006', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024007', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024008', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024009', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024010', 'CTDT-CNTT-2024', 'HP-TTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024001', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024002', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024003', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024004', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024005', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 1, NULL, 0, NULL),
('SV2024006', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 2, NULL, 0, NULL),
('SV2024007', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 2, NULL, 0, NULL),
('SV2024008', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 2, NULL, 0, NULL),
('SV2024009', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 2, NULL, 0, NULL),
('SV2024010', 'CTDT-CNTT-2024', 'HP-AI', 'HK2-2025', 2, NULL, 0, NULL),
-- K25 – GTDL, CSDL
('SV2025001', 'CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025002', 'CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025003', 'CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025004', 'CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025005', 'CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025006', 'CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025007', 'CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025008', 'CTDT-CNTT-2025', 'HP-GTDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025001', 'CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025002', 'CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025003', 'CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025004', 'CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 1, NULL, 0, NULL),
('SV2025005', 'CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 2, NULL, 0, NULL),
('SV2025006', 'CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 2, NULL, 0, NULL),
('SV2025007', 'CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 2, NULL, 0, NULL),
('SV2025008', 'CTDT-CNTT-2025', 'HP-CSDL', 'HK2-2025', 2, NULL, 0, NULL);

-- =============================================================================
-- 16. DotKienTap  (TrangThaiDotKT: ChuanBi | ChoDuyet | DaDuyet | DaThucHien | DaHuy)
--   Luu y: \"DaThucHien\" la trang thai CUOI cua kien tap (khac voi thuc tap).
-- =============================================================================
INSERT INTO DotKienTap (MaDotKT, TenDotKT, MaLopHC, MaHocKy, ThoiGian, MaGVPhuTrach, MaDoanhNghiep, NhanXetGV, NhanXetDN, FileMinhChung, KinhPhiChung, KinhPhiTungSV, TrangThai, NguoiTao, NguoiDuyet, NgayDuyet) VALUES
-- Cac dot KT qua khu (da thuc hien xong)
(1, 'Kien Tap K22A tai FPT Software – HK1-2024', 'CNTT-K22A', 'HK1-2024', '2024-11-15', 'GV003', 'DN001',
    'Sinh vien tham gia nghiem tuc.', 'Nhom co 2 em noi bat ve coding.', 'kientap/DotKT_1_minh_chung.pdf', 10000000, 500000, 'DaThucHien', 'GV001', 'GV002', '2024-10-01 14:00:00'),
(2, 'Kien Tap K22B tai VNG – HK1-2024', 'CNTT-K22B', 'HK1-2024', '2024-11-20', 'GV004', 'DN002',
    'SV chu dong.', 'Nhom hoc viec tot.', 'kientap/DotKT_2_minh_chung.pdf', 8000000, 400000, 'DaThucHien', 'GV001', 'GV002', '2024-10-05 09:30:00'),
(3, 'Kien Tap K23A tai TMA – HK1-2025', 'CNTT-K23A', 'HK1-2025', '2025-12-08', 'GV005', 'DN003',
    'Hoan thanh tot, ky luat cao.', '3/5 em duoc de nghi giu lai thuc tap.', 'kientap/DotKT_3_minh_chung.pdf', 7500000, 375000, 'DaThucHien', 'GV001', 'GV002', '2025-10-15 11:00:00'),
(4, 'Kien Tap K23B tai Viettel – HK1-2025', 'CNTT-K23B', 'HK1-2025', '2025-12-15', 'GV006', 'DN004',
    'Hoan thanh dung tien do.', 'Co 2 em duoc tuyen dung ban thoi gian.', 'kientap/DotKT_4_minh_chung.pdf', 8500000, 425000, 'DaThucHien', 'GV001', 'GV002', '2025-10-20 14:30:00'),
-- Cac dot KT sap toi (HK2-2025)
(5, 'Kien Tap K23A tai MISA – HK2-2025 (bo sung)', 'CNTT-K23A', 'HK2-2025', NULL, 'GV005', 'DN005', NULL, NULL, NULL, NULL, NULL, 'ChoDuyet', 'GV001', NULL, NULL),
(6, 'Kien Tap K23B tai KMS – HK2-2025 (bo sung)', 'CNTT-K23B', 'HK2-2025', NULL, 'GV006', 'DN006', NULL, NULL, NULL, NULL, NULL, 'ChuanBi', 'GV001', NULL, NULL);

-- =============================================================================
-- 17. DanhSachSinhVienKienTap  (SV tham gia dot KT)
-- =============================================================================
INSERT INTO DanhSachSinhVienKienTap (MaDotKT, MaSV, DaThamGia) VALUES
(1, 'SV2022001', 1), (1, 'SV2022002', 1), (1, 'SV2022003', 1), (1, 'SV2022004', 1), (1, 'SV2022005', 1),
(2, 'SV2022006', 1), (2, 'SV2022007', 0), (2, 'SV2022008', 1), (2, 'SV2022009', 1),
(3, 'SV2023001', 1), (3, 'SV2023002', 1), (3, 'SV2023003', 1), (3, 'SV2023004', 1), (3, 'SV2023005', 1),
(4, 'SV2023006', 1), (4, 'SV2023007', 1), (4, 'SV2023008', 1), (4, 'SV2023009', 1);

-- =============================================================================
-- 18. DotThucTap
--   TrangThaiDotTT: ChuanBi | ChoDuyet | DaDuyet | DangThucHien | DaKetThuc | DaHuy
--   ** KHAC VOI DotKienTap ** — co \"DangThucHien\" (dang chay) va \"DaKetThuc\" (da xong).
--
--   Dot 1: 2025-10-01..2025-12-30 — thuc tap cua K22 HK1-2025.
--          \"Hom nay\" (2026-01-15) da qua ngay ket thuc -> DaKetThuc.
--          (Seed cu sai: dung 'DaThucHien' gay crash ApplicationException.)
-- =============================================================================
INSERT INTO DotThucTap (MaDotTT, TenDotTT, MaCTDT, MaHocPhan, MaHocKy, NgayBatDau, NgayKetThuc, FileMinhChung, TrangThai, NguoiTao, NguoiDuyet, NgayDuyet) VALUES
(1, 'Thuc Tap Cuoi Khoa K22 – Dot 1 (HK1-2025)', 'CTDT-CNTT-2022', 'HP-TT', 'HK1-2025',
    '2025-10-01', '2025-12-30', 'thuctap/DotTT_1_ke_hoach.pdf', 'DaKetThuc', 'GV001', 'GV002', '2025-09-15 10:00:00'),
(2, 'Thuc Tap Cuoi Khoa K22 – Dot 2 (HK2-2025)', 'CTDT-CNTT-2022', 'HP-TT', 'HK2-2025',
    '2026-02-15', '2026-05-20', NULL, 'DaDuyet', 'GV001', 'GV002', '2025-12-10 09:00:00'),
(3, 'Thuc Tap Cuoi Khoa K23 – Dot 1 (du kien)', 'CTDT-CNTT-2023', 'HP-TT', 'HK2-2025',
    NULL, NULL, NULL, 'ChuanBi', 'GV001', NULL, NULL);

-- =============================================================================
-- 19. DanhSachThucTap
--   TrangThaiThucTap: DaPhanCong | DangThucTap | DaKetThuc | DaHuy
--   Cascade tu DotThucTap.ketThuc(): dot -> DaKetThuc keo theo SV -> DaKetThuc.
--   (Seed cu sai: dung 'DaHoanThanh' — KHONG co trong enum.)
-- =============================================================================
INSERT INTO DanhSachThucTap (MaThucTap, MaDotTT, MaSV, LoaiThucTap, MaDoanhNghiep, TrangThai) VALUES
(1, 1, 'SV2022001', 'DoanhNghiep', 'DN001', 'DaKetThuc'),
(2, 1, 'SV2022002', 'DoanhNghiep', 'DN002', 'DaKetThuc'),
(3, 1, 'SV2022003', 'DoanhNghiep', 'DN003', 'DaKetThuc'),
(4, 1, 'SV2022004', 'Truong', NULL, 'DaKetThuc'),
(5, 1, 'SV2022005', 'DoanhNghiep', 'DN005', 'DaKetThuc'),
(6, 1, 'SV2022006', 'DoanhNghiep', 'DN001', 'DaKetThuc'),
(7, 1, 'SV2022007', 'DoanhNghiep', 'DN006', 'DaKetThuc'),
(8, 1, 'SV2022008', 'DoanhNghiep', 'DN007', 'DaKetThuc'),
(9, 1, 'SV2022009', 'DoanhNghiep', 'DN008', 'DaKetThuc');

-- =============================================================================
-- 20. KetQuaThucTap  (2-cot diem: DN + GV_HD, hoac GV_HD + GV_PB neu tai truong)
--
--   Row 4 (SV2022004) thuc tap tai TRUONG -> khong co cot DN, chi co GV_HD + GV_PB.
--   Cac row khac (DoanhNghiep) -> cot DN (NV cham) + cot GV_HD (GV cham).
--
--   MaNguoiDanhGia:
--     - Vai tro DN    -> NV cua dung DN tiep nhan SV (NV001..NV008 dung theo DN).
--     - Vai tro GV_HD / GV_PB -> GV cua truong (GV001 la GV huong dan HP-TT).
-- =============================================================================
INSERT INTO KetQuaThucTap (MaThucTap, MaVaiTro, MaNguoiDanhGia, Diem, NhanXet) VALUES
-- SV2022001 @ FPT (DN001)
(1, 'DN',    'NV001', 9.00, 'Xuat sac, de xuat tuyen dung.'),
(1, 'GV_HD', 'GV001', 8.50, 'Code chat luong, bao cao dung han.'),
-- SV2022002 @ VNG (DN002)
(2, 'DN',    'NV002', 7.80, 'Kha, can chu dong hon.'),
(2, 'GV_HD', 'GV001', 7.50, 'Bao cao tam on.'),
-- SV2022003 @ TMA (DN003)
(3, 'DN',    'NV003', 8.00, 'Tiep thu nhanh, code Java tot.'),
(3, 'GV_HD', 'GV001', 8.20, NULL),
-- SV2022004 @ TRUONG (2 cot GV)
(4, 'GV_HD', 'GV001', 7.20, 'Nghien cuu tot, can tang toc.'),
(4, 'GV_PB', 'GV011', 7.50, 'Kien thuc nen tang vung.'),
-- SV2022005 @ MISA (DN005)
(5, 'DN',    'NV005', 9.00, 'Tu duy phan tich tot.'),
(5, 'GV_HD', 'GV001', 8.80, 'Hoan thanh xuat sac.'),
-- SV2022006 @ FPT (DN001)
(6, 'DN',    'NV001', 8.50, 'Lam viec nghiem tuc.'),
(6, 'GV_HD', 'GV001', 8.00, 'Tot.'),
-- SV2022007 @ KMS (DN006)
(7, 'DN',    'NV006', 7.00, NULL),
(7, 'GV_HD', 'GV001', 7.00, NULL),
-- SV2022008 @ VNPay (DN007)
(8, 'DN',    'NV007', 8.50, NULL),
(8, 'GV_HD', 'GV001', 8.00, NULL),
-- SV2022009 @ Tiki (DN008) — NV008 da them vao NguoiDung
(9, 'DN',    'NV008', 6.50, 'Can cai thien ky nang giao tiep.'),
(9, 'GV_HD', 'GV001', 7.00, NULL);

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- TAI KHOAN TEST (mat khau cho tat ca: Password@123)
--   admin          (Admin)
--   tran.van.an    (GV001 — PDT + TTDTXS)
--   le.thi.binh    (GV002 — TTDTXS + CNHP)
--   nguyen.van.cuong (GV003 — CVHT K22A, CNHP)
--   sv.2022001     (SV K22A)
--   dn.fpt         (Dai dien DN001)
--   nv.le.van.hung (NV001 — NV FPT cham diem)
-- =============================================================================

