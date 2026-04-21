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

### 02_seed_data.sql
- 16 NguoiDung (1 Admin, 5 GV, 7 SV, 2 DN) — tat ca mat khau `Password@123` (BCrypt hash da san)
- 6 Hoc Ky (HK1-2022 .. HK2-2024)
- 4 Doanh Nghiep (3 DangHopTac, 1 TamNgung)
- 3 CTDT (2022, 2023 `DaDuyet`; 2024 `ChoDuyet`)
- 10 Hoc Phan, 18 entries `DoiNguGiangVienHP`, 18 entries `CTDT_HocPhan`
- 9 Lop Hoc Phan + 6 record `DanhSachSinhVienLopHocPhan` (co 2 canh bao)
- 3 Dot Kien Tap, 4 SV tham gia
- 2 Dot Thuc Tap, 2 phan cong thuc tap, 3 diem danh gia

Chi tiet so ban ghi + tai khoan test: xem phan cuoi cua `02_seed_data.sql` (khoi `KIEM TRA NHANH SAU KHI CHAY`) hoac `docs/02_Mô Tả & Thiết kế dữ liệu.md` § 4.

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
SELECT COUNT(*) FROM NguoiDung; -- Phai ra 16
SELECT COUNT(*) FROM HocPhan;   -- Phai ra 10
-- Kiem tra login: hash Password@123 co tai user 'admin'
SELECT MaNguoiDung, TenDangNhap, LoaiNguoiDung FROM NguoiDung WHERE TenDangNhap='admin';
```
