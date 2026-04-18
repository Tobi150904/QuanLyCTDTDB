# 02_Data — Quy Uoc Du Lieu & Rang Buoc Nghiep Vu

> Nhất quán với `01_ERD_SCHEMA.md`, `scripts/01_create_tables.sql`, `scripts/02_seed_data.sql`.
> Day la nguon su that cho moi rang buoc nghiep vu khi code Service layer.

---

## 1. QUY UOC KHOA CHINH (MaNguoiDung FORMAT)

| Loai tai khoan | Format        | Vi du              | Nguon sinh                          |
|----------------|---------------|--------------------|-------------------------------------|
| Admin          | AD + 3 so     | AD001              | So tang tu DB (001, 002...)         |
| Giang Vien     | GV + 3 so     | GV001, GV002       | So tang tu DB                       |
| Sinh Vien      | SV + 7 ky tu  | SV2024001          | SV + nam + 3 so (SV2024001...)      |
| Doanh Nghiep   | DN + 3 so     | DN001              | So tang tu DB                       |
| Hoc Ky         | HKn-YYYY      | HK1-2024           | HK1 hoac HK2 + nam bat dau          |
| CTDT           | CTDT-NGANH-YYYY | CTDT-CNTT-2022   | Tu nhap khi tao                     |
| Hoc Phan       | HP-MATHE      | HP-LTW, HP-CSDL    | Tu nhap khi tao                     |
| Lop HC         | NGANH-KhoaLop | CNTT-K22A          | Tu nhap khi tao                     |
| Dot KT         | INT AUTO      | 1, 2, 3            | AUTO_INCREMENT                      |
| Dot TT         | INT AUTO      | 1, 2, 3            | AUTO_INCREMENT                      |
| TT record      | INT AUTO      | 1, 2, 3            | AUTO_INCREMENT                      |
| KQ TT          | INT AUTO      | 1, 2, 3            | AUTO_INCREMENT                      |

---

## 2. ENUM VALUES CHINH XAC

> **QUAN TRONG:** Gia tri enum trong Java phai khop 100% voi gia tri ENUM trong MySQL DDL.
> Dung `@Enumerated(EnumType.STRING)` cho tat ca enum.

| Java Enum Class        | Values (chinh xac)                                     | Bang su dung              |
|------------------------|-------------------------------------------------------|---------------------------|
| TrangThaiHocKy         | `SapDienRa`, `DangDienRa`, `DaKetThuc`               | HocKyNamHoc               |
| LoaiNguoiDung          | `Admin`, `GiangVien`, `SinhVien`, `DoanhNghiep`      | NguoiDung                 |
| VaiTro                 | `PDT`, `TTDTXS`, `CVHT`, `CNHP`                     | NhomNguoiDung             |
| TrangThaiCTDT          | `BanNhap`, `ChoDuyet`, `DaDuyet`, `DaHuy`            | ChuongTrinhDaoTao         |
| ChucDanhBCN            | `ChuNhiem`, `ThuKy`, `UyVien`                        | BCN_ThanhVien             |
| LoaiHocPhan            | `LyThuyet`, `ThucHanh`, `DoAn`, `ThucTap`, `KienTap`| HocPhan                   |
| TrangThaiHocPhan       | `BanNhap`, `ChoDuyet`, `DaDuyet`                     | HocPhan                   |
| LoaiGiangVien          | `GiangVienTruong`, `DoanhNghiep`                     | GiangVien                 |
| TrangThaiDoanhNghiep   | `DangHopTac`, `TamNgung`                             | DoanhNghiep               |
| TrangThaiSinhVien      | `DangHoc`, `BaoLuu`, `ThoiHoc`, `TotNghiep`         | SinhVien                  |
| TrangThaiLopHocPhan    | `DangMo`, `DaDong`, `DaHuy`                          | LopHocPhan                |
| TrangThaiDotKT         | `ChuanBi`, `ChoDuyet`, `DaDuyet`, `DaThucHien`, `DaHuy` | DotKienTap            |
| TrangThaiDotTT         | `ChuanBi`, `ChoDuyet`, `DaDuyet`, `DangThucHien`, `DaKetThuc` | DotThucTap        |
| LoaiThucTap            | `Truong`, `DoanhNghiep`                              | DanhSachThucTap           |
| TrangThaiThucTap       | `DaPhanCong`, `DangThucTap`, `DaKetThuc`, `DaHuy`   | DanhSachThucTap           |

---

## 3. RANG BUOC DU LIEU THEO BANG

### 3.1 NguoiDung
- `TenDangNhap`: 3-50 ky tu, chi chua chu thuong, so, dau cham (vi du: `tran.van.a`)
- `MatKhau` (truoc hash): >= 8 ky tu, it nhat 1 chu hoa, 1 so, 1 ky tu dac biet
- `Email`: dinh dang email hop le, UNIQUE toan bang
- `TrangThaiTK`: mac dinh = 1 (hoat dong)
- `LoaiNguoiDung`: bat buoc, khong duoc null

### 3.2 SinhVien & GiangVien
- `MaSV = MaNguoiDung` (bat buoc khop khi tao)
- `MaGV = MaNguoiDung` (bat buoc khop khi tao)
- Khi xoa NguoiDung: xoa cascade SinhVien/GiangVien (ON DELETE CASCADE)
- `MaLopHC` trong SinhVien: RESTRICT — khong xoa LopHanhChinh neu con SinhVien

### 3.3 HocPhan
- `SoTinChi`: 1 <= x <= 10 (kiem tra o @Min @Max trong DTO)
- `TrangThai` moi tao: mac dinh `BanNhap`
- `ChuNhiemHP` bat buoc ton tai trong bang `GiangVien`
- Khi tao HocPhan: tu dong INSERT `DoiNguGiangVienHP(MaHocPhan, ChuNhiemHP, TrangThai=1)`
- **Khong duoc xoa CNHP khoi DoiNguGiangVienHP** (kiem tra trong service)
- Neu GV da duoc gan vao LopHocPhan: UPDATE TrangThai=0, khong DELETE

### 3.4 ChuongTrinhDaoTao
- `TrangThai` moi tao: `BanNhap`
- Chi them HocPhan vao CTDT khi `HocPhan.TrangThai = DaDuyet`
- `HocKyThu`: 1 <= x <= 10
- `SoLopDuKien`: >= 1
- Khi phe duyet CTDT (DaDuyet): tu dong tao `LopHocPhan` theo `SoLopDuKien`
- `NguoiDuyet` phai khac `NguoiTao` (kiem tra o service)

### 3.5 LopHocPhan
- `SiSoToiDa`: 30 <= x <= 60 (MySQL CHECK constraint + @Min @Max trong DTO)
- `SiSoThucTe`: <= SiSoToiDa (kiem tra truoc khi them SV)
- `MaGiangVien = NULL` khi moi tao (phan cong sau)
- Khi gan GV khong trong DoiNguGiangVienHP: canh bao nhung van cho phep (confirm dialog)

### 3.6 DanhSachSinhVienLopHocPhan
- `DaCanhBao`: Mac dinh 0, khi GV tich = 1 -> trigger gui email CVHT
- `DaCanhBao` giu = 1 sau khi da xu ly, chi them `KetQuaXuLy`
- Khong xoa record co `DaCanhBao = 1`

### 3.7 DotKienTap
- `MaDoanhNghiep`: phai o trang thai `DangHopTac`
- Khi tao: lay tat ca `SinhVien WHERE MaLopHC = ? AND TrangThaiSV = 'DangHoc'`
  → INSERT tat ca vao `DanhSachSinhVienKienTap (DaThamGia=1)`
- `KinhPhiChung`, `KinhPhiTungSV`: co the NULL (khong bat buoc)

### 3.8 DotThucTap & DanhSachThucTap
- `MaHocPhan` trong DotThucTap phai co `LoaiHocPhan IN ('ThucTap', 'KienTap')`
- `DanhSachThucTap`: UNIQUE nghiep vu `(MaDotTT, MaSV)` — kiem tra truoc INSERT
- Neu trung: SKIP, them vao danh sach loi (khong throw exception)
- `MaDoanhNghiep` bat buoc neu `LoaiThucTap = 'DoanhNghiep'`

### 3.9 KetQuaThucTap
- `Diem`: 0.00 <= x <= 10.00 (kiem tra o @DecimalMin @DecimalMax trong DTO)
- `MaNguoiDanhGia` tham chieu `GiangVien.MaGV` (chi GV nhap ket qua trong he thong)
- Cho moi `(MaThucTap, MaVaiTro)`: neu da co -> UPDATE, chua co -> INSERT

---

## 4. DU LIEU MAU (SEED DATA — TOM TAT)

Sau khi chay `02_seed_data.sql`:

| Bang                           | So ban ghi | Ghi chu quan trong                           |
|--------------------------------|------------|----------------------------------------------|
| HocKyNamHoc                    | 4          | HK1-2024 = DangDienRa                       |
| NguoiDung                      | 15         | 1 Admin, 5 GV, 7 SV, 2 DN                   |
| GiangVien                      | 5          | GV001..GV005                                |
| SinhVien                       | 7          | SV001..SV007                                |
| NhomNguoiDung                  | 6          | GV001: PDT+TTDTXS; GV002: TTDTXS; GV003: CVHT; GV004,GV005: CNHP |
| DoanhNghiep                    | 4          | DN001..DN003 DangHopTac, DN004 TamNgung     |
| LopHanhChinh                   | 4          | CNTT-K22A, K22B, K23A, K24A                 |
| ChuongTrinhDaoTao              | 3          | 2022+2023 DaDuyet, 2024 ChoDuyet            |
| BCN_ThanhVien                  | 7          |                                              |
| HocPhan                        | 10         | 9 DaDuyet, 1 ChoDuyet (HP-AI)               |
| DoiNguGiangVienHP              | 17         |                                              |
| CTDT_HocPhan                   | 17         |                                              |
| LopHocPhan                     | 8          | 6 DangMo (HK1-2024), 2 DaDong (HK1-2023)   |
| DanhSachSinhVienLopHocPhan     | 7          | 1 ban ghi DaCanhBao=1 chua xu ly (SV003)    |
| DotKienTap                     | 3          | 1 DaThucHien, 1 DaDuyet, 1 ChoDuyet         |
| DanhSachSinhVienKienTap        | 4          |                                              |
| DotThucTap                     | 2          | 1 DangThucHien, 1 ChuanBi                   |
| DanhSachThucTap                | 2          | SV006, SV007 DangThucTap                    |
| VaiTroThucTap                  | 4          | GV, DN, CVHT, SV                            |
| KetQuaThucTap                  | 3          | 2 cho SV006, 1 cho SV007                    |

### Tai khoan test (MatKhau: Password@123)

| TenDangNhap | LoaiNguoiDung | VaiTro          | Ghi chu                              |
|-------------|---------------|-----------------|--------------------------------------|
| admin       | Admin         | —               | Toan quyen he thong                  |
| tran.van.a  | GiangVien     | PDT, TTDTXS     | Truong phong dao tao + kiem TT DTXS  |
| le.thi.b    | GiangVien     | TTDTXS          | Thanh vien TT dao tao xuat sac        |
| nguyen.c    | GiangVien     | CVHT            | Co van hoc tap CNTT-K22A             |
| pham.d      | GiangVien     | CNHP            | Chu nhiem HP-LTW                     |
| hoang.e     | GiangVien     | CNHP            | Chu nhiem HP-KLTN                    |
| sv.2024001  | SinhVien      | —               | CNTT-K24A, DangHoc                   |
| sv.2024002  | SinhVien      | —               | CNTT-K24A, DangHoc                   |
| sv.2024003  | SinhVien      | —               | CNTT-K24A, DangHoc, DaCanhBao=1      |
| sv.2022001  | SinhVien      | —               | CNTT-K22A, DangThucTap               |
| sv.2022002  | SinhVien      | —               | CNTT-K22B, DangThucTap               |
| dn.fpt      | DoanhNghiep   | —               | FPT Software, DangHopTac             |
| dn.vng      | DoanhNghiep   | —               | VNG Corporation, DangHopTac          |

---

## 5. QUY UOC FORMAT NGAY THANG & SO

| Kieu du lieu     | Format            | Vi du                  | Ghi chu                           |
|------------------|-------------------|------------------------|-----------------------------------|
| DATE             | yyyy-MM-dd        | 2024-09-02             | MySQL DATE                        |
| DATETIME         | yyyy-MM-dd HH:mm  | 2024-09-02 09:00       | Hien thi trong form               |
| DATETIME (DB)    | DATETIME          | MySQL native           | Luu chuyen doi boi Hibernate      |
| DECIMAL(4,2)     | 0.00 - 10.00      | 8.50                   | Diem thuc tap                     |
| DECIMAL(15,2)    | 0.00 - 99999...   | 10000000.00            | Kinh phi                          |
| VARCHAR ma       | UPPER hoac CamelCase | HP-LTW, CNTT-K22A  | Khong co khoang trang, khong dau  |
| HoTen, TenLop    | Unicode UTF8MB4   | Nguyen Van An          | Co dau tieng Viet                 |

---

## 6. QUY UOC FILE UPLOAD

| Loai file      | Truong luu       | Thu muc                | Extension cho phep    | Kich thuoc toi da |
|----------------|------------------|------------------------|-----------------------|-------------------|
| CTDT Word      | ChuongTrinhDaoTao.FileWord  | uploads/ctdt/ | .doc, .docx           | 20 MB             |
| De cuong HP    | HocPhan.FileDeCuong         | uploads/hocphan/ | .pdf, .doc, .docx   | 20 MB             |
| De cuong CT    | LopHocPhan.FileDeCuongChiTiet | uploads/lophocphan/ | .pdf, .doc, .docx | 20 MB           |
| Minh chung KT  | DotKienTap.FileMinhChung    | uploads/kientap/  | .pdf, .doc, .docx   | 20 MB             |
| Minh chung TT  | DotThucTap.FileMinhChung    | uploads/thuctap/  | .pdf, .doc, .docx   | 20 MB             |
| Excel import   | (khong luu)                 | temp/             | .xlsx, .xls           | 20 MB             |

**Quy tac dat ten file luu:** `{MaDanhMuc}_{timestamp}_{original_filename}`
Vi du: `HP-LTW_20241015_decuong.pdf`

---

## 7. QUY UOC PHAN QUYEN THEO URL

| URL Pattern            | Roles duoc phep truy cap                           |
|------------------------|----------------------------------------------------|
| /login, /logout        | permitAll                                          |
| /dashboard             | authenticated                                      |
| /nguoi-dung/**         | ROLE_PDT, ROLE_ADMIN                               |
| /doanh-nghiep/**       | ROLE_PDT, ROLE_TTDTXS, ROLE_ADMIN                 |
| /hoc-ky/**             | ROLE_PDT, ROLE_TTDTXS, ROLE_ADMIN                 |
| /hoc-phan/**           | ROLE_CNHP, ROLE_TTDTXS, ROLE_ADMIN                |
| /ctdt/**               | ROLE_PDT, ROLE_TTDTXS, ROLE_CNHP, ROLE_ADMIN      |
| /lop-hoc-phan/**       | ROLE_PDT, ROLE_TTDTXS, ROLE_CNHP, ROLE_ADMIN, ROLE_GIANG_VIEN |
| /danh-gia/**           | ROLE_GIANG_VIEN, ROLE_CVHT, ROLE_ADMIN             |
| /kien-tap/**           | ROLE_TTDTXS, ROLE_CNHP, ROLE_ADMIN, ROLE_GIANG_VIEN, ROLE_DOANH_NGHIEP |
| /thuc-tap/**           | ROLE_PDT, ROLE_TTDTXS, ROLE_ADMIN, ROLE_GIANG_VIEN, ROLE_CVHT, ROLE_DOANH_NGHIEP, ROLE_SINH_VIEN |
| /bao-cao/**            | ROLE_PDT, ROLE_TTDTXS, ROLE_ADMIN                 |
| /profile/**            | authenticated                                      |
| /403                   | permitAll                                          |
| /error                 | permitAll                                          |

---

## 8. FLASH MESSAGE CONVENTION

| Key           | Y nghia                        | Kieu Bootstrap |
|---------------|--------------------------------|----------------|
| successMsg    | Thao tac thanh cong            | alert-success  |
| errorMsg      | Loi nghiep vu (da xu ly)       | alert-danger   |
| warningMsg    | Canh bao (khong chan lai)       | alert-warning  |
| infoMsg       | Thong tin tham khao            | alert-info     |

Su dung: `redirectAttributes.addFlashAttribute("successMsg", "Tao thanh cong")`

---

## 9. EMAIL TEMPLATE (MockEmailServiceImpl — Dev)

Chi log ra `System.out` / logger, khong gui that. Khi deploy: bat spring.mail config.

| Loai email         | Trigger                        | Nguoi nhan      | Noi dung chinh                         |
|--------------------|--------------------------------|-----------------|----------------------------------------|
| CanhBaoSinhVien    | DaCanhBao duoc dat = 1         | CVHT cua SV     | HoTenSV, TenHocPhan, NhanXetGV         |
| PhanCongLopHP      | GV duoc gan vao LopHocPhan     | GiangVien       | TenHocPhan, LopHocPhan, HocKy         |
| PheDuyetHocPhan    | HP duoc duyet                  | ChuNhiemHP      | MaHocPhan, TenHocPhan, NgayDuyet       |
| TuChoiHocPhan      | HP bi tu choi                  | ChuNhiemHP      | MaHocPhan, LyDoTuChoi                  |
