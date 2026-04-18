# 01_ERD_SCHEMA — Schema Chi Tiet 20 Bang

> Nhất quán với `scripts/01_create_tables.sql` và `00_MASTER_REFERENCE.md`.
> Mọi thay đổi DDL phải cập nhật file này.

---

## SO DO QUAN HE (ERD Text)

```
HocKyNamHoc (MaHocKy)
    |-- LopHocPhan.MaHocKy
    |-- DotKienTap.MaHocKy
    |-- DotThucTap.MaHocKy

NguoiDung (MaNguoiDung)
    |-- GiangVien.MaNguoiDung       [1-1]
    |-- SinhVien.MaNguoiDung        [1-1]
    |-- NhomNguoiDung.MaNguoiDung   [1-N]
    |-- ChuongTrinhDaoTao.NguoiTao
    |-- ChuongTrinhDaoTao.NguoiDuyet
    |-- DotKienTap.NguoiTao
    |-- DotKienTap.NguoiDuyet
    |-- DotThucTap.NguoiTao
    |-- DotThucTap.NguoiDuyet
    |-- KetQuaThucTap.MaNguoiDanhGia

GiangVien (MaGV) -> NguoiDung
    |-- BCN_ThanhVien.MaGV
    |-- HocPhan.ChuNhiemHP
    |-- DoiNguGiangVienHP.MaGiangVien
    |-- LopHanhChinh.MaCoVan
    |-- LopHocPhan.MaGiangVien
    |-- DotKienTap.MaGVPhuTrach

SinhVien (MaSV) -> NguoiDung, LopHanhChinh
    |-- DanhSachSinhVienLopHocPhan.MaSV
    |-- DanhSachSinhVienKienTap.MaSV
    |-- DanhSachThucTap.MaSV

NhomNguoiDung (MaNguoiDung, VaiTro) -> NguoiDung   [PK composite]

DoanhNghiep (MaDoanhNghiep)
    |-- DotKienTap.MaDoanhNghiep
    |-- DanhSachThucTap.MaDoanhNghiep

ChuongTrinhDaoTao (MaCTDT) -> NguoiDung x2
    |-- BCN_ThanhVien.MaCTDT
    |-- CTDT_HocPhan.MaCTDT
    |-- LopHanhChinh.MaCTDT
    |-- DotThucTap.MaCTDT

BCN_ThanhVien (MaCTDT, MaGV, ChucDanh)              [PK composite]

HocPhan (MaHocPhan) -> GiangVien
    |-- DoiNguGiangVienHP.MaHocPhan
    |-- CTDT_HocPhan.MaHocPhan
    |-- DotThucTap.MaHocPhan

DoiNguGiangVienHP (MaHocPhan, MaGiangVien)           [PK composite]

LopHanhChinh (MaLopHC) -> ChuongTrinhDaoTao, GiangVien
    |-- SinhVien.MaLopHC
    |-- DotKienTap.MaLopHC

CTDT_HocPhan (MaCTDT, MaHocPhan)                     [PK composite]
    |-- LopHocPhan.(MaCTDT,MaHocPhan)
    |-- DotThucTap.(MaCTDT,MaHocPhan)

LopHocPhan (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan) [PK composite]
    |-- DanhSachSinhVienLopHocPhan.(MaCTDT,MaHocPhan,MaHocKy,MaLopHocPhan)

DanhSachSinhVienLopHocPhan (MaSV,...5 cols)           [PK composite 5 cot]

DotKienTap (MaDotKT AUTO_INCREMENT)
    |-- DanhSachSinhVienKienTap.MaDotKT

DanhSachSinhVienKienTap (MaDotKT, MaSV)               [PK composite]

DotThucTap (MaDotTT AUTO_INCREMENT)
    |-- DanhSachThucTap.MaDotTT

DanhSachThucTap (MaThucTap AUTO_INCREMENT)
    |-- KetQuaThucTap.MaThucTap

VaiTroThucTap (MaVaiTro)
    |-- KetQuaThucTap.MaVaiTro

KetQuaThucTap (MaKetQua AUTO_INCREMENT)
```

---

## CHI TIET TUNG BANG

---

### 1. HocKyNamHoc

| Cot          | Kieu du lieu              | Rang buoc                        | Mo ta                  |
|--------------|---------------------------|----------------------------------|------------------------|
| MaHocKy      | VARCHAR(20)               | PK                               | VD: HK1-2024           |
| TenHocKy     | VARCHAR(50)               | NOT NULL                         | VD: Hoc Ky 1 Nam 2024  |
| NgayBatDau   | DATE                      | NOT NULL                         | Ngay bat dau hoc ky    |
| NgayKetThuc  | DATE                      | NOT NULL                         | Ngay ket thuc hoc ky   |
| TrangThai    | ENUM                      | SapDienRa/DangDienRa/DaKetThuc   | Trang thai hoc ky      |
| created_at   | DATETIME                  | DEFAULT NOW()                    | Audit                  |
| updated_at   | DATETIME                  | DEFAULT NOW() ON UPDATE          | Audit                  |

**Java Enum:** `TrangThaiHocKy { SapDienRa, DangDienRa, DaKetThuc }`
**Rang buoc nghiep vu:** Chi co 1 hoc ky o trang thai `DangDienRa` tai mot thoi diem.

---

### 2. NguoiDung

| Cot            | Kieu du lieu              | Rang buoc                              | Mo ta                            |
|----------------|---------------------------|----------------------------------------|----------------------------------|
| MaNguoiDung    | VARCHAR(20)               | PK                                     | AD001, GV001, SV001, DN001       |
| TenDangNhap    | VARCHAR(50)               | NOT NULL, UNIQUE                       | Username dang nhap               |
| MatKhauHash    | VARCHAR(255)              | NOT NULL                               | BCrypt hash                      |
| Email          | VARCHAR(100)              | NOT NULL, UNIQUE                       | Email chinh thuc                 |
| HoTen          | VARCHAR(100)              | NOT NULL                               | Ho ten day du                    |
| SoDienThoai    | VARCHAR(15)               | NULL                                   | So dien thoai                    |
| TrangThaiTK    | BIT                       | DEFAULT 1                              | 1=Hoat dong, 0=Bi khoa           |
| LoaiNguoiDung  | ENUM                      | NOT NULL                               | Admin/GiangVien/SinhVien/DoanhNghiep |
| created_at     | DATETIME                  | DEFAULT NOW()                          | Audit                            |
| updated_at     | DATETIME                  | DEFAULT NOW() ON UPDATE                | Audit                            |

**Java Enum:** `LoaiNguoiDung { Admin, GiangVien, SinhVien, DoanhNghiep }`
**Quy tac MaNguoiDung:** AD001, GV001..GV999, SV2024001..., DN001...

---

### 3. SinhVien

| Cot          | Kieu du lieu  | Rang buoc                                   | Mo ta                   |
|--------------|---------------|---------------------------------------------|-------------------------|
| MaSV         | VARCHAR(20)   | PK                                          | = MaNguoiDung cua SV    |
| MaNguoiDung  | VARCHAR(20)   | NOT NULL, UNIQUE, FK -> NguoiDung           | Lien ket tai khoan      |
| MaLopHC      | VARCHAR(20)   | NOT NULL, FK -> LopHanhChinh (RESTRICT)     | Lop hanh chinh          |
| TrangThaiSV  | ENUM          | DangHoc/BaoLuu/ThoiHoc/TotNghiep           | Trang thai hoc tap      |

**Java Enum:** `TrangThaiSinhVien { DangHoc, BaoLuu, ThoiHoc, TotNghiep }`

---

### 4. GiangVien

| Cot           | Kieu du lieu  | Rang buoc                         | Mo ta                          |
|---------------|---------------|-----------------------------------|--------------------------------|
| MaGV          | VARCHAR(20)   | PK                                | = MaNguoiDung cua GV           |
| MaNguoiDung   | VARCHAR(20)   | NOT NULL, UNIQUE, FK -> NguoiDung | Lien ket tai khoan             |
| HocHam        | VARCHAR(50)   | NULL                              | Giao Su, Pho Giao Su           |
| HocVi         | VARCHAR(50)   | NULL                              | Tien Si, Thac Si, Cu Nhan      |
| ChuyenNganh   | VARCHAR(200)  | NULL                              | Chuyen nganh chuyen sau        |
| LoaiGiangVien | ENUM          | DEFAULT GiangVienTruong           | GiangVienTruong/DoanhNghiep    |

**Java Enum:** `LoaiGiangVien { GiangVienTruong, DoanhNghiep }`

---

### 5. NhomNguoiDung

| Cot          | Kieu du lieu  | Rang buoc                          | Mo ta                   |
|--------------|---------------|------------------------------------|-------------------------|
| MaNguoiDung  | VARCHAR(20)   | PK (composite), FK -> NguoiDung    | Nguoi dung co vai tro   |
| VaiTro       | ENUM          | PK (composite), NOT NULL           | PDT/TTDTXS/CVHT/CNHP   |
| created_at   | DATETIME      | DEFAULT NOW()                      | Thoi diem gan vai tro   |

**PK Composite:** `(MaNguoiDung, VaiTro)` — 1 nguoi co the co nhieu vai tro.
**Java Enum:** `VaiTro { PDT, TTDTXS, CVHT, CNHP }`

---

### 6. ChuongTrinhDaoTao

| Cot         | Kieu du lieu  | Rang buoc                          | Mo ta                             |
|-------------|---------------|------------------------------------|-----------------------------------|
| MaCTDT      | VARCHAR(20)   | PK                                 | VD: CTDT-CNTT-2022                |
| TenCTDT     | VARCHAR(200)  | NOT NULL                           | Ten day du chuong trinh           |
| Khoa        | VARCHAR(20)   | NULL                               | Nam khoa hoc (2022, 2023...)      |
| FileWord    | VARCHAR(255)  | NULL                               | Duong dan file Word CTDT          |
| TrangThai   | ENUM          | BanNhap/ChoDuyet/DaDuyet/DaHuy    | Trang thai duyet CTDT             |
| NguoiTao    | VARCHAR(20)   | NOT NULL, FK -> NguoiDung          | Nguoi tao CTDT (BCN)              |
| NguoiDuyet  | VARCHAR(20)   | NULL, FK -> NguoiDung              | TTDTXS phe duyet                  |
| NgayDuyet   | DATETIME      | NULL                               | Thoi diem phe duyet               |
| created_at  | DATETIME      | DEFAULT NOW()                      | Audit                             |
| updated_at  | DATETIME      | DEFAULT NOW() ON UPDATE            | Audit                             |

**Java Enum:** `TrangThaiCTDT { BanNhap, ChoDuyet, DaDuyet, DaHuy }`
**Luong:** BanNhap -> ChoDuyet -> DaDuyet (hoac BanNhap neu tu choi), DaDuyet -> DaHuy

---

### 7. BCN_ThanhVien

| Cot          | Kieu du lieu  | Rang buoc                                    | Mo ta                    |
|--------------|---------------|----------------------------------------------|--------------------------|
| MaCTDT       | VARCHAR(20)   | PK (composite), FK -> ChuongTrinhDaoTao      | CTDT thuoc ve            |
| MaGV         | VARCHAR(20)   | PK (composite), FK -> GiangVien              | Giang vien thanh vien    |
| ChucDanh     | ENUM          | PK (composite), ChuNhiem/ThuKy/UyVien       | Chuc danh trong BCN      |
| NgayBoNhiem  | DATE          | NULL                                         | Ngay bo nhiem chinh thuc |
| GhiChu       | VARCHAR(255)  | NULL                                         | Ghi chu them             |
| created_at   | DATETIME      | DEFAULT NOW()                                | Audit                    |

**PK Composite:** `(MaCTDT, MaGV, ChucDanh)` — 1 GV co the la UyVien va ThuKy cua 2 CTDT khac nhau.
**Java Enum:** `ChucDanhBCN { ChuNhiem, ThuKy, UyVien }`

---

### 8. HocPhan

| Cot          | Kieu du lieu  | Rang buoc                             | Mo ta                          |
|--------------|---------------|---------------------------------------|--------------------------------|
| MaHocPhan    | VARCHAR(20)   | PK                                    | VD: HP-LTW, HP-CSDL            |
| TenHocPhan   | VARCHAR(200)  | NOT NULL                              | Ten mon hoc day du             |
| SoTinChi     | INT           | NOT NULL                              | So tin chi (1-10)              |
| LoaiHocPhan  | ENUM          | LyThuyet/ThucHanh/DoAn/ThucTap/KienTap | Loai hoc phan                |
| ChuNhiemHP   | VARCHAR(20)   | NOT NULL, FK -> GiangVien             | GV chu nhiem hoc phan          |
| FileDeCuong  | VARCHAR(255)  | NULL                                  | Duong dan file de cuong        |
| TrangThai    | ENUM          | BanNhap/ChoDuyet/DaDuyet             | Trang thai phe duyet           |
| created_at   | DATETIME      | DEFAULT NOW()                         | Audit                          |
| updated_at   | DATETIME      | DEFAULT NOW() ON UPDATE               | Audit                          |

**Java Enum:** `LoaiHocPhan { LyThuyet, ThucHanh, DoAn, ThucTap, KienTap }`
**Java Enum:** `TrangThaiHocPhan { BanNhap, ChoDuyet, DaDuyet }`
**Rang buoc:** Chi them vao CTDT khi TrangThai = DaDuyet.

---

### 9. DoiNguGiangVienHP

| Cot          | Kieu du lieu  | Rang buoc                        | Mo ta                              |
|--------------|---------------|----------------------------------|------------------------------------|
| MaHocPhan    | VARCHAR(20)   | PK (composite), FK -> HocPhan    | Hoc phan thuoc ve                  |
| MaGiangVien  | VARCHAR(20)   | PK (composite), FK -> GiangVien  | GV co the giang day HP nay         |
| TrangThai    | BIT           | DEFAULT 1                        | 1=Hoat dong, 0=Da ngung (soft del) |
| created_at   | DATETIME      | DEFAULT NOW()                    | Audit                              |

**PK Composite:** `(MaHocPhan, MaGiangVien)`
**Quy tac:** ChuNhiemHP tu dong duoc them vao doi ngu khi tao HocPhan. Khong xoa CNHP.

---

### 10. DoanhNghiep

| Cot              | Kieu du lieu  | Rang buoc                | Mo ta                           |
|------------------|---------------|--------------------------|---------------------------------|
| MaDoanhNghiep    | VARCHAR(20)   | PK                       | VD: DN001                       |
| TenDoanhNghiep   | VARCHAR(200)  | NOT NULL                 | Ten cong ty                     |
| LinhVuc          | VARCHAR(200)  | NULL                     | Linh vuc hoat dong              |
| NguoiDaiDien     | VARCHAR(100)  | NULL                     | Ten nguoi dai dien              |
| Email            | VARCHAR(100)  | NULL                     | Email lien he                   |
| SoDienThoai      | VARCHAR(15)   | NULL                     | So dien thoai                   |
| DiaChiDN         | VARCHAR(255)  | NULL                     | Dia chi doanh nghiep            |
| TrangThai        | ENUM          | DangHopTac/TamNgung     | Trang thai hop tac              |
| created_at       | DATETIME      | DEFAULT NOW()            | Audit                           |
| updated_at       | DATETIME      | DEFAULT NOW() ON UPDATE  | Audit                           |

**Java Enum:** `TrangThaiDoanhNghiep { DangHopTac, TamNgung }`
**Rang buoc:** Chi chon DoanhNghiep o trang thai `DangHopTac` khi tao DotKienTap/DotThucTap.

---

### 11. LopHanhChinh

| Cot       | Kieu du lieu  | Rang buoc                                    | Mo ta                        |
|-----------|---------------|----------------------------------------------|------------------------------|
| MaLopHC   | VARCHAR(20)   | PK                                           | VD: CNTT-K22A                |
| TenLop    | VARCHAR(100)  | NOT NULL                                     | Ten lop hien thi             |
| MaCTDT    | VARCHAR(20)   | NULL, FK -> ChuongTrinhDaoTao (SET NULL)     | CTDT cua lop nay             |
| KhoaHoc   | VARCHAR(20)   | NULL                                         | Nam nhap hoc (2022, 2023...) |
| MaCoVan   | VARCHAR(20)   | NULL, FK -> GiangVien (SET NULL)             | GV co van hoc tap            |
| created_at| DATETIME      | DEFAULT NOW()                                | Audit                        |
| updated_at| DATETIME      | DEFAULT NOW() ON UPDATE                      | Audit                        |

---

### 12. CTDT_HocPhan

| Cot           | Kieu du lieu  | Rang buoc                                   | Mo ta                              |
|---------------|---------------|---------------------------------------------|------------------------------------|
| MaCTDT        | VARCHAR(20)   | PK (composite), FK -> ChuongTrinhDaoTao     | CTDT chua HP                       |
| MaHocPhan     | VARCHAR(20)   | PK (composite), FK -> HocPhan (RESTRICT)    | HP thuoc CTDT                      |
| HocKyThu      | INT           | NOT NULL                                    | HP duoc day o hoc ky thu may (1-10)|
| SoLopDuKien   | INT           | DEFAULT 1                                   | So lop HP du kien mo moi hoc ky    |
| BatBuoc       | BIT           | DEFAULT 1                                   | 1=Bat buoc, 0=Tu chon              |
| GhiChu        | VARCHAR(255)  | NULL                                        | Ghi chu them                       |
| FileDeCuong   | VARCHAR(255)  | NULL                                        | File de cuong cua HP trong CTDT    |
| created_at    | DATETIME      | DEFAULT NOW()                               | Audit                              |
| updated_at    | DATETIME      | DEFAULT NOW() ON UPDATE                     | Audit                              |

**PK Composite:** `(MaCTDT, MaHocPhan)`
**Quan trong:** `SoLopDuKien` duoc dung khi CTDT duoc phe duyet -> tu dong tao LopHocPhan.

---

### 13. LopHocPhan

| Cot                  | Kieu du lieu  | Rang buoc                                           | Mo ta                        |
|----------------------|---------------|-----------------------------------------------------|------------------------------|
| MaCTDT               | VARCHAR(20)   | PK (composite), FK -> CTDT_HocPhan (RESTRICT)       | CTDT                         |
| MaHocPhan            | VARCHAR(20)   | PK (composite), FK -> CTDT_HocPhan (RESTRICT)       | Hoc phan                     |
| MaHocKy              | VARCHAR(20)   | PK (composite), FK -> HocKyNamHoc                   | Hoc ky mo lop                |
| MaLopHocPhan         | INT           | PK (composite)                                      | So thu tu lop (1, 2, 3...)   |
| MaGiangVien          | VARCHAR(20)   | NULL, FK -> GiangVien (SET NULL)                    | GV duoc phan cong            |
| SiSoToiDa            | INT           | NOT NULL, CHECK 30-60                               | Si so toi da cho phep        |
| SiSoThucTe           | INT           | DEFAULT 0                                           | So SV dang ky thuc te        |
| FileDeCuongChiTiet   | VARCHAR(255)  | NULL                                                | File de cuong chi tiet       |
| TrangThai            | ENUM          | DangMo/DaDong/DaHuy                                | Trang thai lop               |
| created_at           | DATETIME      | DEFAULT NOW()                                       | Audit                        |
| updated_at           | DATETIME      | DEFAULT NOW() ON UPDATE                             | Audit                        |

**PK Composite:** `(MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)`
**Java Enum:** `TrangThaiLopHocPhan { DangMo, DaDong, DaHuy }`
**Rang buoc:** `SiSoToiDa BETWEEN 30 AND 60` (MySQL CHECK constraint).

---

### 14. DanhSachSinhVienLopHocPhan

| Cot            | Kieu du lieu  | Rang buoc                                                     | Mo ta                     |
|----------------|---------------|---------------------------------------------------------------|---------------------------|
| MaSV           | VARCHAR(20)   | PK (composite), FK -> SinhVien                               | Sinh vien                 |
| MaCTDT         | VARCHAR(20)   | PK (composite)                                               |                           |
| MaHocPhan      | VARCHAR(20)   | PK (composite)                                               |                           |
| MaHocKy        | VARCHAR(20)   | PK (composite), FK -> LopHocPhan (composite 4 cot)           |                           |
| MaLopHocPhan   | INT           | PK (composite)                                               | Thu tu lop                |
| NhanXet        | TEXT          | NULL                                                         | GV nhap nhan xet          |
| DaCanhBao      | BIT           | DEFAULT 0                                                    | 1=Da phat sinh canh bao   |
| KetQuaXuLy     | TEXT          | NULL                                                         | CVHT ghi ket qua xu ly    |
| created_at     | DATETIME      | DEFAULT NOW()                                                | Audit                     |
| updated_at     | DATETIME      | DEFAULT NOW() ON UPDATE                                      | Audit                     |

**PK Composite 5 cot:** `(MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)`
**Rang buoc:** DaCanhBao giu nguyen = 1 sau khi xu ly, chi cap nhat KetQuaXuLy.

---

### 15. DotKienTap

| Cot              | Kieu du lieu    | Rang buoc                              | Mo ta                           |
|------------------|-----------------|----------------------------------------|---------------------------------|
| MaDotKT          | INT             | PK AUTO_INCREMENT                      | ID tu tang                      |
| TenDotKT         | VARCHAR(200)    | NOT NULL                               | Ten dot kien tap                |
| MaLopHC          | VARCHAR(20)     | NOT NULL, FK -> LopHanhChinh           | Lop hanh chinh tham gia         |
| MaHocKy          | VARCHAR(20)     | NOT NULL, FK -> HocKyNamHoc            | Hoc ky thuc hien                |
| ThoiGian         | DATE            | NULL                                   | Ngay du kien thuc hien          |
| MaGVPhuTrach     | VARCHAR(20)     | NULL, FK -> GiangVien                  | GV phu trach                    |
| MaDoanhNghiep    | VARCHAR(20)     | NOT NULL, FK -> DoanhNghiep            | DN tiep nhan                    |
| NhanXetGV        | TEXT            | NULL                                   | Nhan xet cua GV                 |
| NhanXetDN        | TEXT            | NULL                                   | Nhan xet cua DN                 |
| FileMinhChung    | VARCHAR(255)    | NULL                                   | File bien ban / ky ket          |
| KinhPhiChung     | DECIMAL(15,2)   | NULL                                   | Kinh phi chung toan dot         |
| KinhPhiTungSV    | DECIMAL(15,2)   | NULL                                   | Kinh phi phan bo moi SV         |
| TrangThai        | ENUM            | ChuanBi/ChoDuyet/DaDuyet/DaThucHien/DaHuy | Trang thai dot               |
| NguoiTao         | VARCHAR(20)     | NOT NULL, FK -> NguoiDung              | Nguoi tao dot                   |
| NguoiDuyet       | VARCHAR(20)     | NULL, FK -> NguoiDung                  | TTDTXS phe duyet                |
| NgayDuyet        | DATETIME        | NULL                                   | Thoi diem phe duyet             |
| created_at       | DATETIME        | DEFAULT NOW()                          | Audit                           |
| updated_at       | DATETIME        | DEFAULT NOW() ON UPDATE                | Audit                           |

**Java Enum:** `TrangThaiDotKT { ChuanBi, ChoDuyet, DaDuyet, DaThucHien, DaHuy }`
**Luong:** Khi tao -> tu dong lay SV tu LopHanhChinh vao DanhSachSinhVienKienTap.

---

### 16. DanhSachSinhVienKienTap

| Cot        | Kieu du lieu  | Rang buoc                           | Mo ta                    |
|------------|---------------|-------------------------------------|--------------------------|
| MaDotKT    | INT           | PK (composite), FK -> DotKienTap   | Dot kien tap             |
| MaSV       | VARCHAR(20)   | PK (composite), FK -> SinhVien     | Sinh vien tham gia       |
| DaThamGia  | BIT           | DEFAULT 1                           | 1=Da/se tham gia, 0=Vang |
| created_at | DATETIME      | DEFAULT NOW()                       | Audit                    |
| updated_at | DATETIME      | DEFAULT NOW() ON UPDATE             | Audit                    |

**PK Composite:** `(MaDotKT, MaSV)`

---

### 17. DotThucTap

| Cot           | Kieu du lieu  | Rang buoc                                  | Mo ta                    |
|---------------|---------------|--------------------------------------------|--------------------------|
| MaDotTT       | INT           | PK AUTO_INCREMENT                          | ID tu tang               |
| TenDotTT      | VARCHAR(200)  | NOT NULL                                   | Ten dot thuc tap         |
| MaCTDT        | VARCHAR(20)   | NOT NULL                                   |                          |
| MaHocPhan     | VARCHAR(20)   | NOT NULL, FK(composite) -> CTDT_HocPhan    | HP loai ThucTap/KienTap  |
| MaHocKy       | VARCHAR(20)   | NOT NULL, FK -> HocKyNamHoc                | Hoc ky thuc hien         |
| NgayBatDau    | DATE          | NULL                                       | Ngay bat dau thuc tap    |
| NgayKetThuc   | DATE          | NULL                                       | Ngay ket thuc thuc tap   |
| FileMinhChung | VARCHAR(255)  | NULL                                       | File bien ban ky ket      |
| TrangThai     | ENUM          | ChuanBi/ChoDuyet/DaDuyet/DangThucHien/DaKetThuc | Trang thai dot      |
| NguoiTao      | VARCHAR(20)   | NOT NULL, FK -> NguoiDung                  | PDT/TTDTXS tao dot       |
| NguoiDuyet    | VARCHAR(20)   | NULL, FK -> NguoiDung                      | TTDTXS phe duyet         |
| NgayDuyet     | DATETIME      | NULL                                       | Thoi diem phe duyet      |
| created_at    | DATETIME      | DEFAULT NOW()                              | Audit                    |
| updated_at    | DATETIME      | DEFAULT NOW() ON UPDATE                    | Audit                    |

**Java Enum:** `TrangThaiDotTT { ChuanBi, ChoDuyet, DaDuyet, DangThucHien, DaKetThuc }`
**Rang buoc:** `MaHocPhan.LoaiHocPhan IN (ThucTap, KienTap)` — kiem tra o service layer.

---

### 18. DanhSachThucTap

| Cot            | Kieu du lieu  | Rang buoc                             | Mo ta                           |
|----------------|---------------|---------------------------------------|---------------------------------|
| MaThucTap      | INT           | PK AUTO_INCREMENT                     | ID tu tang                      |
| MaDotTT        | INT           | NOT NULL, FK -> DotThucTap            | Dot thuc tap                    |
| MaSV           | VARCHAR(20)   | NOT NULL, FK -> SinhVien              | Sinh vien duoc phan cong        |
| LoaiThucTap    | ENUM          | NOT NULL, Truong/DoanhNghiep         | Thuc tap tai truong hay DN      |
| MaDoanhNghiep  | VARCHAR(20)   | NULL, FK -> DoanhNghiep (SET NULL)    | DN tiep nhan (neu co)           |
| TrangThai      | ENUM          | DaPhanCong/DangThucTap/DaKetThuc/DaHuy | Trang thai phan cong           |
| created_at     | DATETIME      | DEFAULT NOW()                         | Audit                           |
| updated_at     | DATETIME      | DEFAULT NOW() ON UPDATE               | Audit                           |

**Java Enum:** `LoaiThucTap { Truong, DoanhNghiep }`, `TrangThaiThucTap { DaPhanCong, DangThucTap, DaKetThuc, DaHuy }`
**Rang buoc UNIQUE nghiep vu:** `(MaDotTT, MaSV)` — 1 SV chi duoc phan cong 1 lan trong 1 dot.

---

### 19. VaiTroThucTap

| Cot       | Kieu du lieu  | Rang buoc  | Mo ta                      |
|-----------|---------------|------------|----------------------------|
| MaVaiTro  | VARCHAR(10)   | PK         | GV, DN, CVHT, SV           |
| TenVaiTro | VARCHAR(100)  | NOT NULL   | Ten day du                  |
| MoTa      | VARCHAR(255)  | NULL       | Mo ta chuc nang danh gia   |

**Du lieu co dinh (seed):** GV, DN, CVHT, SV

---

### 20. KetQuaThucTap

| Cot               | Kieu du lieu   | Rang buoc                           | Mo ta                           |
|-------------------|----------------|-------------------------------------|---------------------------------|
| MaKetQua          | INT            | PK AUTO_INCREMENT                   | ID tu tang                      |
| MaThucTap         | INT            | NOT NULL, FK -> DanhSachThucTap     | Phan cong thuc tap tuong ung    |
| MaVaiTro          | VARCHAR(10)    | NOT NULL, FK -> VaiTroThucTap       | Vai tro nguoi danh gia          |
| MaNguoiDanhGia    | VARCHAR(20)    | NOT NULL, FK -> GiangVien           | GV/CVHT nhap ket qua            |
| Diem              | DECIMAL(4,2)   | NULL                                | Diem (0.00 - 10.00)             |
| NhanXet           | TEXT           | NULL                                | Nhan xet chi tiet               |
| created_at        | DATETIME       | DEFAULT NOW()                       | Audit                           |
| updated_at        | DATETIME       | DEFAULT NOW() ON UPDATE             | Audit                           |

**Rang buoc nghiep vu:** 1 (MaThucTap, MaVaiTro) chi co 1 ban ghi -> kiem tra truoc khi INSERT, neu co thi UPDATE.

---

## BANG TOM TAT ENTITY -> JAVA CLASS

| Bang SQL                        | Java Entity Class           | PK Type                              |
|---------------------------------|-----------------------------|--------------------------------------|
| HocKyNamHoc                     | HocKyNamHoc                 | String (MaHocKy)                     |
| NguoiDung                       | NguoiDung                   | String (MaNguoiDung)                 |
| SinhVien                        | SinhVien                    | String (MaSV)                        |
| GiangVien                       | GiangVien                   | String (MaGV)                        |
| NhomNguoiDung                   | NhomNguoiDung               | @EmbeddedId NhomNguoiDungId          |
| ChuongTrinhDaoTao               | ChuongTrinhDaoTao           | String (MaCTDT)                      |
| BCN_ThanhVien                   | BcnThanhVien                | @EmbeddedId BcnThanhVienId           |
| HocPhan                         | HocPhan                     | String (MaHocPhan)                   |
| DoiNguGiangVienHP               | DoiNguGiangVienHp           | @EmbeddedId DoiNguGiangVienHpId      |
| DoanhNghiep                     | DoanhNghiep                 | String (MaDoanhNghiep)               |
| LopHanhChinh                    | LopHanhChinh                | String (MaLopHC)                     |
| CTDT_HocPhan                    | CtdtHocPhan                 | @EmbeddedId CtdtHocPhanId            |
| LopHocPhan                      | LopHocPhan                  | @EmbeddedId LopHocPhanId             |
| DanhSachSinhVienLopHocPhan      | DanhSachSvLopHocPhan        | @EmbeddedId DanhSachSvLopHocPhanId   |
| DotKienTap                      | DotKienTap                  | Integer (MaDotKT)                    |
| DanhSachSinhVienKienTap         | DanhSachSvKienTap           | @EmbeddedId DanhSachSvKienTapId      |
| DotThucTap                      | DotThucTap                  | Integer (MaDotTT)                    |
| DanhSachThucTap                 | DanhSachThucTap             | Integer (MaThucTap)                  |
| VaiTroThucTap                   | VaiTroThucTap               | String (MaVaiTro)                    |
| KetQuaThucTap                   | KetQuaThucTap               | Integer (MaKetQua)                   |
