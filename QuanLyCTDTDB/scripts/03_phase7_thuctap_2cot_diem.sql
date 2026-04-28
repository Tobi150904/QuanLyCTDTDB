-- =============================================================================
-- Phase 7 — He thong 2 cot diem cho Thuc Tap
--
-- Yeu cau nghiep vu (theo nguoi dung):
--   - Thuc tap TAI TRUONG : 2 cot diem
--       * Cot 1 = Giang Vien Huong Dan / Giam Sat
--       * Cot 2 = Giang Vien Phan Bien
--   - Thuc tap TAI DOANH NGHIEP : 2 cot diem
--       * Cot 1 = Doanh Nghiep (nhan vien DN)
--       * Cot 2 = Giang Vien Giam Sat (= GV_HD)
--
-- Khoa du lieu:
--   - Bang KetQuaThucTap da co UNIQUE (MaThucTap, MaVaiTro) -> moi (SV, vai tro)
--     chi co toi da 1 row diem.
--   - Vai tro 'GV' cu (don nhat) khong du de phan biet HD vs PB ->
--     them 2 vai tro moi 'GV_HD' va 'GV_PB'. 'GV' cu giu lai cho backward compat
--     voi du lieu hien co (neu co); UI moi se uu tien GV_HD/GV_PB.
--
-- File nay an toan idempotent — chay nhieu lan khong bi loi (INSERT IGNORE).
-- MySQL: dung CONCAT() de noi chuoi (KHONG dung || vi MySQL diac dinh khac
-- voi PostgreSQL/Oracle).
-- =============================================================================

INSERT IGNORE INTO VaiTroThucTap (MaVaiTro, TenVaiTro, MoTa) VALUES
  ('GV_HD', 'GV Huong Dan / Giam Sat',
   'Giang vien huong dan (tai truong) hoac giam sat (tai doanh nghiep). La cot diem 1 cho ca 2 loai thuc tap.'),
  ('GV_PB', 'GV Phan Bien',
   'Giang vien phan bien — chi ap dung cho thuc tap tai truong. La cot diem 2 doi voi loai thuc tap = Truong.');

-- =============================================================================
-- Sample grades (chi cho moi truong test/dev). An toan idempotent —
-- INSERT IGNORE bo qua neu da ton tai (theo UNIQUE).
--
-- Gia su dot thuc tap dau tien (MaDotTT=1) co cac DanhSachThucTap voi MaThucTap=1..N.
-- Chi seed neu thuc su co du lieu.
-- =============================================================================
INSERT IGNORE INTO KetQuaThucTap (MaThucTap, MaVaiTro, MaNguoiDanhGia, Diem, NhanXet)
SELECT dst.MaThucTap, 'GV_HD', 'GV001', 8.5,
       'Sinh vien tham gia day du, hoan thanh tot bao cao.'
FROM DanhSachThucTap dst
WHERE dst.MaDotTT = 1 AND dst.LoaiThucTap = 'Truong'
LIMIT 3;

INSERT IGNORE INTO KetQuaThucTap (MaThucTap, MaVaiTro, MaNguoiDanhGia, Diem, NhanXet)
SELECT dst.MaThucTap, 'GV_PB', 'GV002', 8.0,
       'Bao cao trinh bay ro rang, kien thuc nen tang vung.'
FROM DanhSachThucTap dst
WHERE dst.MaDotTT = 1 AND dst.LoaiThucTap = 'Truong'
LIMIT 3;

-- Verify
SELECT 'VaiTroThucTap' AS bang, MaVaiTro, TenVaiTro FROM VaiTroThucTap;
SELECT 'KetQuaThucTap' AS bang, COUNT(*) AS so_dong FROM KetQuaThucTap;
