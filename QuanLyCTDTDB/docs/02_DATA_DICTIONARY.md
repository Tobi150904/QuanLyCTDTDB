# Data Dictionary - Chi Tiết Từng Trường Dữ Liệu

## 1. Bảng NguoiDung (Users)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaNguoiDung | VARCHAR(20) | PRIMARY KEY | Mã định danh duy nhất (kết hợp vai trò) | `SV001`, `GV001`, `PDT001`, `DN001` |
| TenDangNhap | VARCHAR(50) | UNIQUE, NOT NULL | Tên đăng nhập (có thể là email hoặc username) | `sv_trana`, `gv_nguyena`, `pdt_admin` |
| MatKhauHash | VARCHAR(255) | NOT NULL | Mật khẩu đã mã hóa BCrypt | `$2a$10$...` |
| Email | VARCHAR(100) | UNIQUE, NOT NULL | Email cá nhân | `tran.a@student.edu`, `nguyen.a@university.edu` |
| HoTen | NVARCHAR(100) | NOT NULL | Họ tên đầy đủ | `Trần Văn A`, `Nguyễn Văn A` |
| SoDienThoai | VARCHAR(15) | Optional | Số điện thoại liên lạc | `0938123456` |
| MaLopHC | VARCHAR(20) | FOREIGN KEY | Lớp hành chính (chỉ for SV) | `CNTT-CLC-K64` |
| TrangThaiSV | ENUM | Optional | Trạng thái SV: `DangHoc`, `BaoLuu`, `ThoiHoc`, `TotNghiep` | `DangHoc` |
| HocHam | NVARCHAR(50) | Optional | Học hàm: GS, PGS, ThS, TS, v.v. | `TS`, `ThS`, `PGS` |
| HocVi | NVARCHAR(50) | Optional | Học vị: TS, ThS, Cử nhân, v.v. | `TS`, `ThS` |
| ChuyenNganh | NVARCHAR(200) | Optional | Chuyên ngành của GV | `Lập trình`, `Cơ sở dữ liệu` |
| TrangThaiTK | BIT | DEFAULT 1 | `1` = Hoạt động, `0` = Khóa | `1` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo tài khoản | `2024-01-15 10:30:45` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật cuối cùng | `2024-06-20 14:20:15` |

**Quy tắc NamingConvention cho MaNguoiDung:**
- SV: `SV` + số (e.g., `SV001`, `SV002`)
- GV: `GV` + số (e.g., `GV001`, `GV002`)
- CVHT: `CVHT` + số (e.g., `CVHT001`)
- BCN: `BCN` + số (e.g., `BCN001`)
- PDT: `PDT` + số (e.g., `PDT001`)
- TTDTSX: `TTDT` + số (e.g., `TTDT001`)
- DN: `DN` + số + `_ACC` (e.g., `DN001_ACC`)

---

## 2. Bảng NguoiDung_VaiTro (Role Assignments)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaNguoiDung | VARCHAR(20) | FOREIGN KEY, PK | Tham chiếu NguoiDung | `SV001` |
| VaiTro | ENUM | PK | Vai trò: `SV`, `GV`, `CVHT`, `BCN`, `CNHP`, `PDT`, `TTDTXS`, `DN` | `GV` |
| created_at | DATETIME | DEFAULT NOW | Ngày gán vai trò | `2024-01-15 10:30:45` |

**Lưu ý:**
- Một người có thể có **nhiều vai trò** (e.g., GV vừa dạy vừa là CNHP)
- PK là composite: (MaNguoiDung, VaiTro)

---

## 3. Bảng HocKyNamHoc (Semesters)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaHocKy | VARCHAR(20) | PRIMARY KEY | Mã học kỳ duy nhất | `K64-HK1`, `2024.1`, `2024-HK1` |
| TenHocKy | NVARCHAR(50) | NOT NULL | Tên học kỳ | `Học kỳ 1 - Khóa 64` |
| NgayBatDau | DATE | NOT NULL | Ngày bắt đầu kỳ | `2024-09-01` |
| NgayKetThuc | DATE | NOT NULL, CHECK | Ngày kết thúc (phải > NgayBatDau) | `2024-12-31` |
| TrangThai | ENUM | DEFAULT `SapDienRa` | `SapDienRa`, `DangDienRa`, `DaKetThuc` | `SapDienRa` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2024-06-01 09:00:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật cuối | `2024-09-01 00:00:00` |

**Workflow Trạng thái:**
- **SapDienRa** → Chưa bắt đầu (cho tạo CTDT, HP, lớp)
- **DangDienRa** → Đang học (cho GV upload đề cương, nhập nhận xét)
- **DaKetThuc** → Kết thúc (lưu trữ, không thay đổi dữ liệu)

---

## 4. Bảng DoanhNghiep (Partner Companies)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaDoanhNghiep | VARCHAR(20) | PRIMARY KEY | Mã doanh nghiệp | `DN001`, `DN_FPT`, `FPT` |
| TenDoanhNghiep | NVARCHAR(200) | NOT NULL | Tên công ty đầy đủ | `Công ty Cổ phần FPT Solutions` |
| LinhVuc | NVARCHAR(200) | Optional | Lĩnh vực hoạt động | `Phần mềm & IT`, `Viễn thông` |
| NguoiDaiDien | NVARCHAR(100) | Optional | Người đại diện công ty | `Ông Trần Văn A`, `Bà Lê Thị C` |
| Email | VARCHAR(100) | Optional | Email liên hệ | `hr@fpt.com`, `contact@viettel.com.vn` |
| SoDienThoai | VARCHAR(15) | Optional | SĐT liên hệ | `0938123456` |
| TrangThai | ENUM | DEFAULT `DangHopTac` | `DangHopTac`, `TamNgung` | `DangHopTac` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2024-06-01 10:00:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2024-06-15 14:30:00` |

---

## 5. Bảng ChuongTrinhDaoTao (Programs)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaCTDT | VARCHAR(20) | PRIMARY KEY | Mã CTDT | `CNTT-CLC-K64`, `CNTT-SP-K64` |
| TenCTDT | NVARCHAR(200) | NOT NULL | Tên chương trình | `Công Nghệ Thông Tin - Chất lượng Cao - Khóa 64` |
| Khoa | VARCHAR(20) | Optional | Khóa áp dụng | `K64`, `K65` |
| FileWord | VARCHAR(255) | Optional | Đường dẫn file Word CTDT | `/uploads/ctdt/CNTT-CLC-K64.docx` |
| TrangThai | ENUM | DEFAULT `BanNhap` | `BanNhap` → `ChoDuyet` → `DaDuyet` ✓ (auto-create LP) → `DaHuy` | `DaDuyet` |
| NguoiTao | VARCHAR(20) | FOREIGN KEY, NOT NULL | BCN tạo CTDT | `BCN001` |
| NguoiDuyet | VARCHAR(20) | FOREIGN KEY | TTDTSX / PDT duyệt | `TTDT001` |
| NgayDuyet | DATETIME | Optional | Ngày duyệt | `2024-08-15 10:30:45` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2024-08-01 09:00:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2024-08-15 10:30:45` |

**Workflow Trạng thái:**
- **BanNhap**: BCN vừa tạo, chưa submit
- **ChoDuyet**: BCN submit, chờ TTDTSX/PDT duyệt
- **DaDuyet**: ✅ Phê duyệt thành công → **Hệ thống TỰ ĐỘNG tạo LopHocPhan cho tất cả HocPhan trong CTDT**
- **DaHuy**: Từ chối phê duyệt

---

## 6. Bảng HocPhan (Courses)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaHocPhan | VARCHAR(20) | PRIMARY KEY | Mã học phần | `CSC101`, `POL307`, `WEB101` |
| TenHocPhan | VARCHAR(200) | NOT NULL | Tên học phần | `Lập trình cơ bản`, `Triết học Mác-Lê-Nin` |
| SoTinChi | INT | NOT NULL, CHECK(2-6) | Số tín chỉ (2-6) | `3`, `4`, `2` |
| ChuNhiemHP | VARCHAR(20) | FOREIGN KEY, NOT NULL | Chủ nhiệm HP (GV chịu trách nhiệm xây dựng đề cương gốc) | `GV001` |
| FileDeCuong | VARCHAR(255) | Optional | Đường dẫn file PDF đề cương gốc (chuẩn hóa cho tất cả lớp) | `/uploads/hp/CSC101_DeCuong_Goc.pdf` |
| TrangThai | ENUM | DEFAULT `BanNhap` | `BanNhap`, `ChoDuyet`, `DaDuyet` | `DaDuyet` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2024-01-01 10:00:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2024-06-15 14:30:00` |

**Lưu ý:**
- Một HP **tồn tại độc lập**, không phụ thuộc CTDT
- Có thể dùng cho **nhiều CTDT** khác nhau
- FileDeCuong là đề cương **gốc, chuẩn hóa** (shared cho tất cả lớp)

---

## 7. Bảng DoiNguGiangVienHP (Lecturer Teams per Course)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaHocPhan | VARCHAR(20) | FOREIGN KEY, PK | Mã học phần | `CSC101` |
| MaGiangVien | VARCHAR(20) | FOREIGN KEY, PK | Mã GV | `GV001` |
| TrangThai | BIT | DEFAULT 1 | `1` = Trong đội ngũ, `0` = Loại khỏi đội ngũ | `1` |
| created_at | DATETIME | DEFAULT NOW | Ngày thêm vào đội ngũ | `2024-01-15 10:00:00` |

**Workflow:**
- CNHP thêm GV vào đội ngũ → GV mới có quyền dạy HP này
- Khi tạo LopHocPhan, GV được gán **phải nằm trong DoiNguGiangVienHP** (kiểm tra, nếu không thì warn)

---

## 8. Bảng LopHanhChinh (Administrative Classes)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaLopHC | VARCHAR(20) | PRIMARY KEY | Mã lớp hành chính | `CNTT-CLC-K64`, `CNTT-SP-K64` |
| TenLop | NVARCHAR(100) | NOT NULL | Tên lớp | `Công Nghệ Thông Tin - Chất lượng Cao - Khóa 64` |
| MaCTDT | VARCHAR(20) | FOREIGN KEY | CTDT áp dụng cho lớp | `CNTT-CLC-K64` |
| KhoaHoc | VARCHAR(20) | Optional | Khóa học | `K64`, `K65` |
| MaCoVan | VARCHAR(20) | FOREIGN KEY | Cố vấn học tập (CVHT) | `CVHT001` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2024-01-01 10:00:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2024-06-15 14:30:00` |

**Lưu ý:**
- Một lớp HC có **1 CVHT**
- Chứa **~50 SV** (referenced via NguoiDung.MaLopHC)
- Kiến tập được **tổ chức theo lớp HC**

---

## 9. Bảng LopHocPhan (Course Classes)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaLopHP | VARCHAR(20) | PRIMARY KEY | Mã lớp HP | `CSC101-01`, `CSC101-02`, `POL307-01` |
| MaHocPhan | VARCHAR(20) | FOREIGN KEY, NOT NULL | Mã HP | `CSC101` |
| MaHocKy | VARCHAR(20) | FOREIGN KEY, NOT NULL | Mã HK | `K64-HK1` |
| MaGiangVien | VARCHAR(20) | FOREIGN KEY | GV dạy lớp (từ DoiNguGiangVienHP) | `GV002` |
| NhomLop | INT | Optional | Nhóm lớp (1, 2, 3, ...) | `1`, `2` |
| SiSoToiDa | INT | NOT NULL, CHECK(30-60) | Sĩ số tối đa (30-60) | `45` |
| SiSoThucTe | INT | DEFAULT 0 | Sĩ số thực tế (SV enrolled) | `42` |
| TrangThai | ENUM | DEFAULT `DangMo` | `DangMo`, `DaDong`, `DaHuy` | `DangMo` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo (auto khi CTDT duyệt) | `2024-08-15 10:30:45` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2024-09-01 00:00:00` |

**Workflow:**
1. BCN upload CTDT → TTDTSX/PDT duyệt → TrangThai = `DaDuyet`
2. Hệ thống **TỰ ĐỘNG** tạo LopHocPhan cho tất cả HP trong CTDT
3. **MaGiangVien = NULL** (chưa gán)
4. BCN gán GV → MaGiangVien được update
5. GV upload đề cương chi tiết

---

## 10. Bảng TaiLieuMonHoc (Course Documents)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaTaiLieu | INT | PRIMARY KEY, AUTO | Mã tài liệu | `1`, `2`, `3` |
| MaLopHP | VARCHAR(20) | FOREIGN KEY, NOT NULL | Lớp HP | `CSC101-01` |
| Loai | ENUM | NOT NULL | `DeCuongChiTiet`, `DeThiGiuaKy`, `DeThiCuoiKy` | `DeCuongChiTiet` |
| FileDinhKem | VARCHAR(255) | NOT NULL | Đường dẫn file | `/uploads/tai-lieu/CSC101-01_DeCuong_20241.pdf` |
| NgayNop | DATETIME | DEFAULT NOW | Ngày GV nộp | `2024-09-12 14:30:00` |
| TrangThai | ENUM | DEFAULT `ChoDuyet` | `ChoDuyet` → `DaDuyet` ✓ / `TuChoi` | `ChoDuyet` |
| NguoiDuyet | VARCHAR(20) | FOREIGN KEY | CNHP duyệt | `GV001` |
| NgayDuyet | DATETIME | Optional | Ngày CNHP duyệt | `2024-09-13 10:00:00` |
| NhanXet | TEXT | Optional | Nhận xét / góp ý từ CNHP | `Kế hoạch chi tiết, CLOs phù hợp` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2024-09-12 14:30:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2024-09-13 10:00:00` |

**Constraints:**
- UNIQUE(MaLopHP, Loai): Mỗi lớp chỉ có **1 đề cương chi tiết, 1 đề thi giữa kỳ, 1 đề thi cuối kỳ**

**Deadline:**
- **DeCuongChiTiet**: Phải nộp trong **14 ngày đầu kỳ** (tính từ HocKy.NgayBatDau)

---

## 11. Bảng DanhGiaVaCanhBao (Evaluations & Alerts)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaDanhGia | INT | PRIMARY KEY, AUTO | Mã nhận xét / cảnh báo | `1`, `2`, `3` |
| MaSV | VARCHAR(20) | FOREIGN KEY, NOT NULL | SV được nhận xét | `SV001` |
| MaLopHP | VARCHAR(20) | FOREIGN KEY, NOT NULL | Lớp HP | `CSC101-01` |
| NguoiNhanXet | VARCHAR(20) | FOREIGN KEY, NOT NULL | GV/CVHT nhập | `GV002` |
| LoaiNhanXet | ENUM | NOT NULL | `TichCuc`, `TieuCuc` | `TieuCuc` |
| NoiDung | TEXT | NOT NULL | Nội dung nhận xét chi tiết | `Em bài tập sai nhiều, code chưa sạch...` |
| DaXuLy | BIT | DEFAULT 0 | `0` = Chưa xử lý, `1` = CVHT đã xử lý | `0` |
| KetQuaXuLy | TEXT | Optional | Kết quả tư vấn từ CVHT | `Đã liên hệ, gợi ý xem video Khan...` |
| created_at | DATETIME | DEFAULT NOW | Ngày GV nhập | `2024-09-25 14:30:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày CVHT xử lý | `2024-09-26 10:00:00` |

**Workflow:**
- GV nhập nhận xét → Nếu `TieuCuc`:
  - ✅ Tự động tạo **cảnh báo**
  - ✅ Email gửi **SV** & **CVHT**
  - ✅ CVHT xử lý → KetQuaXuLy → DaXuLy = 1

---

## 12. Bảng DotKienTap (Field Trip Batches)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaDotKT | INT | PRIMARY KEY, AUTO | Mã đợt kiến tập | `1`, `2`, `3` |
| TenDotKT | VARCHAR(200) | NOT NULL | Tên đợt | `Kiến tập Công ty FPT - HK1/K64` |
| MaLopHC | VARCHAR(20) | FOREIGN KEY, NOT NULL | Lớp hành chính | `CNTT-CLC-K64` |
| MaHocKy | VARCHAR(20) | FOREIGN KEY, NOT NULL | Học kỳ tổ chức | `K64-HK1` |
| ThoiGian | DATE | Optional | Ngày tổ chức | `2024-11-18` |
| MaGVPhuTrach | VARCHAR(20) | FOREIGN KEY | GV phụ trách đoàn | `GV001` |
| MaDoanhNghiep | VARCHAR(20) | FOREIGN KEY, NOT NULL | DN tiếp đón | `DN001` |
| NhanXetGV | TEXT | Optional | Nhận xét GV về đợt KT | `SV chủ động, chuẩn bị tốt...` |
| NhanXetDN | TEXT | Optional | Nhận xét DN về đợt KT | `SV chuyên nghiệp, học hỏi tích cực...` |
| TrangThai | ENUM | DEFAULT `ChuanBi` | `ChuanBi` → `ChoDuyet` → `DaDuyet` → `DaThucHien` / `DaHuy` | `ChuanBi` |
| NguoiDuyet | VARCHAR(20) | FOREIGN KEY | Người duyệt (TTDTSX) | `TTDT001` |
| NgayDuyet | DATETIME | Optional | Ngày duyệt | `2024-11-15 10:00:00` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2024-10-01 09:00:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2024-11-18 14:00:00` |

**Lưu ý:**
- Kiến tập **không có điểm số**, chỉ có nhận xét
- Danh sách SV = tất cả SV của lớp HC (mặc định)

---

## 13. Bảng DotThucTap (Practicum Batches)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaDotTT | INT | PRIMARY KEY, AUTO | Mã đợt thực tập | `1`, `2`, `3` |
| TenDotTT | NVARCHAR(200) | NOT NULL | Tên đợt | `Đợt thực tập HK2/K64` |
| MaHocKy | VARCHAR(20) | FOREIGN KEY, NOT NULL | Học kỳ | `K64-HK2` |
| NgayBatDau | DATE | Optional | Ngày bắt đầu | `2025-01-15` |
| NgayKetThuc | DATE | Optional, CHECK | Ngày kết thúc (≥ NgayBatDau) | `2025-05-31` |
| TrangThai | ENUM | DEFAULT `ChuanBi` | `ChuanBi` → `ChoDuyet` → `DaDuyet` → `DangThucHien` → `DaKetThuc` | `ChuanBi` |
| NguoiDuyet | VARCHAR(20) | FOREIGN KEY | Người duyệt (TTDTSX) | `TTDT001` |
| NgayDuyet | DATETIME | Optional | Ngày duyệt | `2024-12-20 10:00:00` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2024-11-01 09:00:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2024-12-20 10:00:00` |

---

## 14. Bảng PhanCongThucTap (Practicum Assignments)

| Tên Trường | Kiểu | Constraints | Mô Tả | Ví Dụ |
|---|---|---|---|---|
| MaThucTap | INT | PRIMARY KEY, AUTO | Mã phân công | `1`, `2`, `3` |
| MaDotTT | INT | FOREIGN KEY, NOT NULL | Đợt thực tập | `1` |
| MaSV | VARCHAR(20) | FOREIGN KEY, NOT NULL | Sinh viên | `SV001` |
| MaDoanhNghiep | VARCHAR(20) | FOREIGN KEY, NOT NULL | Doanh nghiệp | `DN001` |
| MaGiangVienGiamSat | VARCHAR(20) | FOREIGN KEY | GV giám sát | `GV001` |
| DiemDN | DECIMAL(4,2) | CHECK(0-10) | Điểm DN (thang 10, e.g., 8.5) | `8.5` |
| NhanXetDN | TEXT | Optional | Nhận xét DN | `Em chuyên nghiệp, chủ động học hỏi...` |
| DiemGV | DECIMAL(4,2) | CHECK(0-10) | Điểm GV (thang 10) | `9.0` |
| NhanXetGV | TEXT | Optional | Nhận xét GV | `Em hoàn thành tốt đợt thực tập...` |
| NhanXetSV | TEXT | Optional | Nhận xét cảm nhận của SV | `Tôi học được nhiều kinh nghiệm...` |
| TrangThai | ENUM | DEFAULT `DaPhanCong` | `DaPhanCong` → `DangThucTap` → `DaKetThuc` / `DaHuy` | `DaPhanCong` |
| created_at | DATETIME | DEFAULT NOW | Ngày tạo | `2025-01-10 10:00:00` |
| updated_at | DATETIME | AUTO UPDATE | Ngày cập nhật | `2025-05-30 14:00:00` |

**Constraints:**
- UNIQUE(MaDotTT, MaSV): Mỗi SV chỉ được gán **1 lần** trong mỗi đợt

---

## Tóm Tắt Constraints & Rules

### NOT NULL Fields (Bắtbuộc)
- NguoiDung: TenDangNhap, MatKhauHash, Email, HoTen
- HocKyNamHoc: MaHocKy, TenHocKy, NgayBatDau, NgayKetThuc
- ChuongTrinhDaoTao: MaCTDT, TenCTDT, NguoiTao
- HocPhan: MaHocPhan, TenHocPhan, SoTinChi, ChuNhiemHP
- LopHocPhan: MaLopHP, MaHocPhan, MaHocKy, SiSoToiDa
- TaiLieuMonHoc: MaTaiLieu, MaLopHP, Loai, FileDinhKem
- Etc.

### UNIQUE Fields
- NguoiDung.TenDangNhap
- NguoiDung.Email
- TaiLieuMonHoc(MaLopHP, Loai)
- PhanCongThucTap(MaDotTT, MaSV)

### FOREIGN KEY Constraints
- Hầu hết là ON DELETE CASCADE hoặc SET NULL (soft delete)
- Kiểm soát referential integrity

### ENUM Values (Fixed)
- Tất cả ENUM đã được liệt kê trong tài liệu ERD

