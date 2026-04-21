# scripts/ — SQL Setup & Seed

> Tat ca script trong thu muc nay chay tren MySQL 8.0+ voi charset `utf8mb4_unicode_ci`. Neu dang dung XAMPP MySQL 5.5 vui long nang cap truoc.

## 1. Thu tu chay

```bash
# Bat buoc theo thu tu nay
mysql -u root -p < scripts/01_create_tables.sql    # Tao database + 20 bang + FK
mysql -u root -p QuanLyCTDTDB < scripts/02_seed_data.sql   # Du lieu mau
```

Script `01_create_tables.sql` da co `CREATE DATABASE IF NOT EXISTS QuanLyCTDTDB` va `USE` ngay dau, nen chay lan dau khong can tao DB thu cong.

## 2. Noi dung tung file

### 01_create_tables.sql
- DROP tat ca bang theo thu tu nguoc dependency (idempotent — chay lai khong loi)
- CREATE 20 bang: `HocKyNamHoc`, `NguoiDung`, `GiangVien`, `DoanhNghiep`, `VaiTroThucTap`, `NhomNguoiDung`, `ChuongTrinhDaoTao`, `BCN_ThanhVien`, `HocPhan`, `DoiNguGiangVienHP`, `LopHanhChinh`, `SinhVien`, `CTDT_HocPhan`, `LopHocPhan`, `DanhSachSinhVienLopHocPhan`, `DotKienTap`, `DanhSachSinhVienKienTap`, `DotThucTap`, `DanhSachThucTap`, `KetQuaThucTap`
- Tat ca FOREIGN KEY deu dat name de easier debug
- Tat ca ENUM trong DDL khop 100% voi enum Java (`@Enumerated(EnumType.STRING)`)
- CHECK constraint cho `SiSoToiDa BETWEEN 30 AND 60`, `Diem BETWEEN 0 AND 10`

### 02_seed_data.sql  (v2 — Phase 3)
Script idempotent (TRUNCATE truoc khi INSERT, chay lai an toan bao nhieu lan cung duoc).

- **18 NguoiDung** (1 Admin, 6 GV, 10 SV, 2 DN) — tat ca mat khau `Password@123` (BCrypt da hash san).
  MaSV theo quy uoc `SV + nam + 3 so` (vd `SV2024001`) — khop `docs/02 §1`.
- **4 Hoc Ky**: HK1-2023, HK2-2023 (DaKetThuc); HK1-2024 (DangDienRa); HK2-2024 (SapDienRa).
- **4 Doanh Nghiep** (3 DangHopTac, 1 TamNgung).
- **3 CTDT** (2022, 2023 `DaDuyet`; 2024 `ChoDuyet`). Moi CTDT co du 3 vi tri BCN: ChuNhiem/ThuKy/UyVien.
- **10 Hoc Phan** (9 DaDuyet, 1 ChoDuyet HP-AI), **25 record `DoiNguGiangVienHP`** (10 CNHP auto + 15 GV bo sung), **18 record `CTDT_HocPhan`**.
- **4 Lop Hanh Chinh** (K22A, K22B, K23A, K24A), moi lop co CVHT + thuoc CTDT khac nhau.
- **10 Sinh Vien** trai deu 4 lop.
- **12 Lop Hoc Phan**: 5 DaDong (HK1-2023, HK2-2023) + 7 DangMo (HK1-2024, co 2 lop chua phan cong GV de test).
- **15 record `DanhSachSinhVienLopHocPhan`** trong do co 2 `DaCanhBao=1`: 1 da xu ly, 1 CHUA xu ly (de dashboard CVHT nhin thay).
- **3 Dot Kien Tap** (DaThucHien, DaDuyet, ChoDuyet), **7 SV tham gia** (auto add het SV DangHoc cua lop theo `docs/02 §3.7`).
- **2 Dot Thuc Tap**, **4 phan cong thuc tap** (3 DN + 1 tai Truong), **4 ket qua danh gia** (1 SV co danh gia tu ca GV lan DN).

Chi tiet so ban ghi + tai khoan test: xem phan cuoi `02_seed_data.sql` (khoi `KIEM TRA NHANH SAU KHI CHAY` + `TAI KHOAN TEST`) hoac `docs/02_Mô Tả & Thiết kế dữ liệu.md` § 4.

## 3. Quy tac khi sua schema

**Khong chinh sua `01_create_tables.sql` khi da trien khai moi truong prod/stg.** Thay vao do:

- Tao `03_*.sql`, `04_*.sql` ... theo thu tu tang dan.
- Dat ten mo ta muc dich: `03_add_hocky_indexes.sql`, `04_alter_kinh_phi_length.sql`.
- Moi file dung `ALTER TABLE` idempotent (kem `IF NOT EXISTS` neu MySQL 8) de rollback dot gian.
- Cap nhat `docs/01_ERD_SCHEMA.md` + `docs/02_Mô Tả & Thiết kế dữ liệu.md` tuong ung.

## 4. Reset du lieu dev

```bash
# CANH BAO: xoa toan bo du lieu
mysql -u root -p -e "DROP DATABASE IF EXISTS QuanLyCTDTDB;"
mysql -u root -p < scripts/01_create_tables.sql
mysql -u root -p QuanLyCTDTDB < scripts/02_seed_data.sql
```

## 5. Kiem tra nhanh sau khi chay

```sql
USE QuanLyCTDTDB;
SHOW TABLES;                    -- Phai tra ve 20 bang
SELECT COUNT(*) FROM NguoiDung; -- Phai ra 18
SELECT COUNT(*) FROM HocPhan;   -- Phai ra 10
SELECT COUNT(*) FROM SinhVien;  -- Phai ra 10
SELECT COUNT(*) FROM LopHocPhan;                  -- Phai ra 12
SELECT COUNT(*) FROM DanhSachSinhVienKienTap;     -- Phai ra 7

-- Kiem tra login: hash Password@123 co tai user 'admin'
SELECT MaNguoiDung, TenDangNhap, LoaiNguoiDung FROM NguoiDung WHERE TenDangNhap='admin';

-- Kiem tra canh bao hoc vu chua xu ly (dashboard CVHT)
SELECT MaSV, MaHocPhan, NhanXet
FROM DanhSachSinhVienLopHocPhan
WHERE DaCanhBao = 1 AND (KetQuaXuLy IS NULL OR KetQuaXuLy = '');

-- Kiem tra CNHP auto-add vao DoiNguGiangVienHP (bat buoc ton tai)
SELECT hp.MaHocPhan, hp.ChuNhiemHP
FROM HocPhan hp
LEFT JOIN DoiNguGiangVienHP dn
  ON dn.MaHocPhan = hp.MaHocPhan AND dn.MaGiangVien = hp.ChuNhiemHP
WHERE dn.MaHocPhan IS NULL;   -- Phai rong
```
