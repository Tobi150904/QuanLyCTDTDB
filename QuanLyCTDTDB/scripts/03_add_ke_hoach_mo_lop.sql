-- =====================================================================
-- MIGRATION 03: Them bang KeHoachMoLop
-- =====================================================================
--
-- MUC DICH
--   Tach "so lop du kien" ra khoi CTDT_HocPhan (cap do vong doi CTDT)
--   thanh bang rieng gan voi (MaHocPhan, MaHocKy) — moi hoc ky thuc te
--   co the mo so lop khac nhau.
--
-- QUY TAC SU DUNG
--   - Khi "Tao hang loat" lop HP: service tra KeHoachMoLop truoc;
--     neu khong co thi fallback CTDT_HocPhan.SoLopDuKien (mac dinh).
--   - Vi vay CTDT_HocPhan.SoLopDuKien van duoc giu trong script 01
--     de tuong thich nguoc — khong ALTER TABLE.
--
-- THU TU CHAY
--   Sau khi 01_create_tables.sql va 02_seed_data.sql da chay.
-- =====================================================================

USE QuanLyCTDTDB;

-- 1. Bang KeHoachMoLop
DROP TABLE IF EXISTS KeHoachMoLop;
CREATE TABLE KeHoachMoLop (
    MaHocPhan     VARCHAR(20)  NOT NULL,
    MaHocKy       VARCHAR(20)  NOT NULL,
    SoLopDuKien   INT          NOT NULL CHECK (SoLopDuKien BETWEEN 1 AND 50),
    GhiChu        VARCHAR(255)     NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                           ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (MaHocPhan, MaHocKy),
    CONSTRAINT fk_khml_hp  FOREIGN KEY (MaHocPhan) REFERENCES HocPhan(MaHocPhan),
    CONSTRAINT fk_khml_hk  FOREIGN KEY (MaHocKy)   REFERENCES HocKyNamHoc(MaHocKy)
);

-- 2. Seed mau (dua theo nghiep vu cua cac CTDT da co)
-- Voi CTDT-CNTT-2022 (K22, dang hoc nam 3): HK1-2024 mo 2 lop CSDL, 1 lop LTW, 1 lop HTTT
-- Voi CTDT-CNTT-2023 (K23, dang hoc nam 2): HK1-2024 mo 2 lop CSDL
-- Voi HK1-2023 (qua khu): ke hoach da thuc thi (chi de phuc vu report)
INSERT INTO KeHoachMoLop (MaHocPhan, MaHocKy, SoLopDuKien, GhiChu) VALUES
  ('HP-OOP',   'HK1-2023', 2, 'K22 HK1: 2 lop'),
  ('HP-MMT',   'HK1-2023', 1, 'K22 HK1'),
  ('HP-HTTT',  'HK1-2024', 1, 'K22 HK3'),
  ('HP-LTW',   'HK1-2024', 1, 'K22 HK3'),
  ('HP-CSDL',  'HK1-2024', 2, 'K23 HK3: 2 lop'),
  ('HP-LTW',   'HK2-2024', 2, 'K22 HK4: 2 lop du kien')
;

-- 3. Kiem tra
-- SELECT COUNT(*) AS KeHoachMoLop FROM KeHoachMoLop; -- 6
