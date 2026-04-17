Tôi sẽ viết lại **hoàn chỉnh** hai tài liệu quan trọng nhất: **Thiết kế Cơ sở dữ liệu (Mục 2.2)** và **Workflow chi tiết (toàn bộ 20 bước)** dựa trên tất cả các quyết định và điều chỉnh đã thống nhất.

---

# II. THIẾT KẾ CƠ SỞ DỮ LIỆU (HOÀN CHỈNH)

## 2.2. Thiết kế chi tiết các bảng

### 2.2.1. Nhóm phân quyền

**Bảng `NguoiDung` – Tài khoản đăng nhập**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaNguoiDung` | `VARCHAR(20)` | `PRIMARY KEY` | Mã định danh duy nhất |
| `TenDangNhap` | `VARCHAR(50)` | `UNIQUE, NOT NULL` | Tên đăng nhập |
| `MatKhauHash` | `VARCHAR(255)` | `NOT NULL` | Mật khẩu đã băm |
| `Email` | `VARCHAR(100)` | `UNIQUE, NOT NULL` | Email |
| `HoTen` | `NVARCHAR(100)` | `NOT NULL` | Họ tên đầy đủ |
| `SoDienThoai` | `VARCHAR(15)` | | Số điện thoại |
| `MaLopHC` | `VARCHAR(20)` | `FOREIGN KEY` | Mã lớp hành chính (chỉ có ý nghĩa với sinh viên), tham chiếu `LopHanhChinh(MaLopHC)` |
| `TrangThaiSV` | `ENUM('DangHoc','BaoLuu','ThoiHoc','TotNghiep')` | `DEFAULT 'DangHoc'` | Trạng thái học tập của sinh viên |
| `HocHam` | `NVARCHAR(50)` | | Học hàm (GS, PGS,…) – dành cho giảng viên |
| `HocVi` | `NVARCHAR(50)` | | Học vị (TS, ThS, CN,…) |
| `ChuyenNganh` | `NVARCHAR(200)` | | Chuyên ngành |
| `TrangThaiTK` | `BIT` | `DEFAULT 1` | 1: Hoạt động, 0: Khóa |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | Ngày tạo |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | Ngày cập nhật |

**Bảng `NguoiDung_VaiTro` – Phân quyền người dùng**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaNguoiDung` | `VARCHAR(20)` | `FOREIGN KEY` | Tham chiếu `NguoiDung(MaNguoiDung)` |
| `VaiTro` | `ENUM('SV','GV','CVHT','BCN','CNHP','PDT','TTDTXS','DN')` | `NOT NULL` | Vai trò của người dùng |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | Ngày gán vai trò |
| **Khóa chính** | `(MaNguoiDung, VaiTro)` | | |

### 2.2.2. Nhóm danh mục

**Bảng `HocKyNamHoc` – Học kỳ năm học**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaHocKy` | `VARCHAR(20)` | `PRIMARY KEY` | Mã học kỳ (VD: K64-HK1) |
| `TenHocKy` | `NVARCHAR(50)` | `NOT NULL` | Tên học kỳ |
| `NgayBatDau` | `DATE` | `NOT NULL` | Ngày bắt đầu |
| `NgayKetThuc` | `DATE` | `NOT NULL` | Ngày kết thúc |
| `TrangThai` | `ENUM('SapDienRa','DangDienRa','DaKetThuc')` | `DEFAULT 'SapDienRa'` | Trạng thái học kỳ |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |

**Bảng `ChuongTrinhDaoTao` – Chương trình đào tạo đặc biệt**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaCTDT` | `VARCHAR(20)` | `PRIMARY KEY` | Mã CTĐT, ví dụ: CNTT-CLC-K64 |
| `TenCTDT` | `NVARCHAR(200)` | `NOT NULL` | Tên chương trình |
| `Khoa` | `VARCHAR(20)` | | Khóa áp dụng (VD: K64) |
| `FileWord` | `VARCHAR(255)` | | Đường dẫn file Word mô tả tổng thể (tham khảo) |
| `TrangThai` | `ENUM('BanNhap','ChoDuyet','DaDuyet','DaHuy')` | `DEFAULT 'BanNhap'` | Trạng thái phê duyệt |
| `NguoiTao` | `VARCHAR(20)` | `FOREIGN KEY` | Người tạo (BCN), tham chiếu `NguoiDung` |
| `NguoiDuyet` | `VARCHAR(20)` | `FOREIGN KEY` | Người duyệt (TTĐTSX/PĐT) |
| `NgayDuyet` | `DATETIME` | | Ngày duyệt |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |

**Bảng `CTDT_HocPhan` – Chi tiết học phần trong CTĐT**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaCTDT` | `VARCHAR(20)` | `FOREIGN KEY` | Mã CTĐT, tham chiếu `ChuongTrinhDaoTao` |
| `MaHocPhan` | `VARCHAR(20)` | `FOREIGN KEY` | Mã học phần, tham chiếu `HocPhan` |
| `HocKyThu` | `INT` | `NOT NULL` | Học kỳ thứ mấy trong khóa học (1,2,3...) |
| `SoLopDuKien` | `INT` | `DEFAULT 1` | Số lớp dự kiến mở cho học phần này trong CTĐT |
| `BatBuoc` | `BIT` | `DEFAULT 1` | 1: Bắt buộc, 0: Tự chọn |
| `GhiChu` | `NVARCHAR(255)` | | Ghi chú |
| **Khóa chính** | `(MaCTDT, MaHocPhan)` | | |

**Bảng `HocPhan` – Học phần**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaHocPhan` | `VARCHAR(20)` | `PRIMARY KEY` | Mã học phần (VD: MAT322) |
| `TenHocPhan` | `VARCHAR(200)` | `NOT NULL` | Tên học phần |
| `SoTinChi` | `INT` | `NOT NULL` | Số tín chỉ |
| `ChuNhiemHP` | `VARCHAR(20)` | `FOREIGN KEY` | Chủ nhiệm học phần, tham chiếu `NguoiDung` |
| `FileDeCuong` | `VARCHAR(255)` | | Đường dẫn file PDF đề cương gốc |
| `TrangThai` | `ENUM('BanNhap','ChoDuyet','DaDuyet')` | `DEFAULT 'BanNhap'` | Trạng thái phê duyệt học phần |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |

**Bảng `DoiNguGiangVienHP` – Đội ngũ giảng viên của học phần**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaHocPhan` | `VARCHAR(20)` | `FOREIGN KEY` | Mã học phần |
| `MaGiangVien` | `VARCHAR(20)` | `FOREIGN KEY` | Mã giảng viên, tham chiếu `NguoiDung` |
| `TrangThai` | `BIT` | `DEFAULT 1` | 1: Đang trong đội ngũ, 0: Đã rời |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | Ngày thêm vào |
| **Khóa chính** | `(MaHocPhan, MaGiangVien)` | | |

**Bảng `DoanhNghiep` – Doanh nghiệp đối tác**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaDoanhNghiep` | `VARCHAR(20)` | `PRIMARY KEY` | Mã doanh nghiệp |
| `TenDoanhNghiep` | `NVARCHAR(200)` | `NOT NULL` | Tên doanh nghiệp |
| `LinhVuc` | `NVARCHAR(200)` | | Lĩnh vực hoạt động |
| `NguoiDaiDien` | `NVARCHAR(100)` | | Người đại diện |
| `Email` | `VARCHAR(100)` | | Email liên hệ |
| `SoDienThoai` | `VARCHAR(15)` | | Số điện thoại |
| `TrangThai` | `ENUM('DangHopTac','TamNgung')` | `DEFAULT 'DangHopTac'` | Trạng thái hợp tác |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |

### 2.2.3. Nhóm đào tạo

**Bảng `LopHanhChinh` – Lớp hành chính**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaLopHC` | `VARCHAR(20)` | `PRIMARY KEY` | Mã lớp hành chính (VD: 64.CNTT-CLC) |
| `TenLop` | `NVARCHAR(100)` | `NOT NULL` | Tên lớp |
| `MaCTDT` | `VARCHAR(20)` | `FOREIGN KEY` | Chương trình đào tạo áp dụng, tham chiếu `ChuongTrinhDaoTao` |
| `KhoaHoc` | `VARCHAR(20)` | | Khóa học (VD: K64) |
| `MaCoVan` | `VARCHAR(20)` | `FOREIGN KEY` | Cố vấn học tập, tham chiếu `NguoiDung` |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |

**Bảng `LopHocPhan` – Lớp học phần (nhóm học phần)**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaHocPhan` | `VARCHAR(20)` | `PRIMARY KEY, FOREIGN KEY` | Mã học phần, tham chiếu `HocPhan` |
| `MaHocKy` | `VARCHAR(20)` | `PRIMARY KEY, FOREIGN KEY` | Mã học kỳ, tham chiếu `HocKyNamHoc` |
| `NhomHocPhan` | `INT` | `PRIMARY KEY` | Nhóm học phần (do BCN chỉ định, phân biệt các lớp khác nhau của cùng học phần trong học kỳ) |
| `MaLopHC` | `VARCHAR(20)` | `FOREIGN KEY NOT NULL` | Lớp hành chính tham gia, xác định danh sách sinh viên |
| `MaGiangVien` | `VARCHAR(20)` | `FOREIGN KEY` | Giảng viên giảng dạy, tham chiếu `NguoiDung` (gán trực tiếp, không cần duyệt) |
| `SiSoToiDa` | `INT` | `NOT NULL` | Sĩ số tối đa |
| `SiSoThucTe` | `INT` | `DEFAULT 0` | Sĩ số thực tế |
| `TrangThai` | `ENUM('DangMo','DaDong','DaHuy')` | `DEFAULT 'DangMo'` | Trạng thái lớp |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |
| **Ràng buộc UNIQUE** | `(MaLopHC, MaHocPhan, MaHocKy)` | | Đảm bảo mỗi lớp hành chính chỉ có một nhóm cho một học phần trong học kỳ |

**Bảng `TaiLieuMonHoc` – Tài liệu môn học (đề cương chi tiết, đề thi)**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaTaiLieu` | `INT` | `AUTO_INCREMENT PRIMARY KEY` | Mã tài liệu |
| `MaHocPhan` | `VARCHAR(20)` | `FOREIGN KEY` | Một phần khóa ngoại đến `LopHocPhan` |
| `MaHocKy` | `VARCHAR(20)` | `FOREIGN KEY` | Một phần khóa ngoại |
| `NhomHocPhan` | `INT` | `FOREIGN KEY` | Một phần khóa ngoại |
| `Loai` | `ENUM('DeCuongChiTiet','DeThiGiuaKy','DeThiCuoiKy')` | `NOT NULL` | Loại tài liệu |
| `FileDinhKem` | `VARCHAR(255)` | `NOT NULL` | Đường dẫn file |
| `NgayNop` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | Ngày nộp |
| `TrangThai` | `ENUM('ChoDuyet','DaDuyet','TuChoi')` | `DEFAULT 'ChoDuyet'` | Trạng thái duyệt |
| `NguoiDuyet` | `VARCHAR(20)` | `FOREIGN KEY` | Người duyệt (CNHP), tham chiếu `NguoiDung` |
| `NgayDuyet` | `DATETIME` | | Ngày duyệt |
| `NhanXet` | `TEXT` | | Nhận xét của người duyệt |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |
| **Ràng buộc UNIQUE** | `(MaHocPhan, MaHocKy, NhomHocPhan, Loai)` | | Mỗi lớp chỉ có một tài liệu cho mỗi loại |

### 2.2.4. Nhóm đánh giá và cảnh báo

**Bảng `DanhGiaVaCanhBao` – Nhận xét và cảnh báo học vụ**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaDanhGia` | `INT` | `AUTO_INCREMENT PRIMARY KEY` | Mã nhận xét |
| `MaSV` | `VARCHAR(20)` | `FOREIGN KEY NOT NULL` | Sinh viên được nhận xét, tham chiếu `NguoiDung` |
| `MaHocPhan` | `VARCHAR(20)` | `FOREIGN KEY NULL` | Mã học phần (NULL nếu `LoaiDanhGia = 'TongKetKy'`) |
| `MaHocKy` | `VARCHAR(20)` | `FOREIGN KEY NULL` | Mã học kỳ (NULL nếu tổng kết kỳ) |
| `NhomHocPhan` | `INT` | `FOREIGN KEY NULL` | Nhóm học phần (NULL nếu tổng kết kỳ) |
| `NguoiNhanXet` | `VARCHAR(20)` | `FOREIGN KEY NOT NULL` | Người viết nhận xét (GV/CVHT) |
| `LoaiNhanXet` | `ENUM('TichCuc','TieuCuc')` | `NOT NULL` | Loại nhận xét |
| `LoaiDanhGia` | `ENUM('QuaTrinh','TongKetKy')` | `DEFAULT 'QuaTrinh' NOT NULL` | Phân biệt đánh giá trong quá trình học hay tổng kết kỳ |
| `NoiDung` | `TEXT` | `NOT NULL` | Nội dung nhận xét |
| `DaXuLy` | `BIT` | `DEFAULT 0` | 0: Chưa xử lý, 1: Đã xử lý |
| `KetQuaXuLy` | `TEXT` | | Kết quả tư vấn, hỗ trợ của CVHT |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | Thời điểm nhận xét |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | Thời điểm cập nhật xử lý |
| **Khóa ngoại** | `FOREIGN KEY (MaHocPhan, MaHocKy, NhomHocPhan) REFERENCES LopHocPhan(...) ON DELETE CASCADE` | | |

### 2.2.5. Nhóm kiến tập

**Bảng `DotKienTap` – Đợt kiến tập**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaDotKT` | `INT` | `AUTO_INCREMENT PRIMARY KEY` | Mã đợt kiến tập |
| `TenDotKT` | `VARCHAR(200)` | `NOT NULL` | Tên đợt kiến tập |
| `MaLopHC` | `VARCHAR(20)` | `FOREIGN KEY` | Lớp hành chính tham gia |
| `MaHocKy` | `VARCHAR(20)` | `FOREIGN KEY` | Học kỳ tổ chức |
| `ThoiGian` | `DATE` | | Ngày tổ chức |
| `MaGVPhuTrach` | `VARCHAR(20)` | `FOREIGN KEY` | Giảng viên phụ trách đoàn |
| `MaDoanhNghiep` | `VARCHAR(20)` | `FOREIGN KEY` | Doanh nghiệp tiếp đón |
| `NhanXetGV` | `TEXT` | | Nhận xét của giảng viên sau đợt kiến tập |
| `NhanXetDN` | `TEXT` | | Nhận xét của doanh nghiệp |
| `FileMinhChung` | `VARCHAR(255)` | | Đường dẫn file minh chứng (kế hoạch, công văn) – 1 file duy nhất |
| `TrangThai` | `ENUM('ChuanBi','ChoDuyet','DaDuyet','DaThucHien','DaHuy')` | `DEFAULT 'ChuanBi'` | Trạng thái đợt kiến tập |
| `NguoiDuyet` | `VARCHAR(20)` | `FOREIGN KEY` | Người duyệt (TTĐTSX) |
| `NgayDuyet` | `DATETIME` | | Ngày duyệt |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |

### 2.2.6. Nhóm thực tập

**Bảng `DotThucTap` – Đợt thực tập**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaDotTT` | `INT` | `AUTO_INCREMENT PRIMARY KEY` | Mã đợt thực tập |
| `TenDotTT` | `NVARCHAR(200)` | `NOT NULL` | Tên đợt |
| `MaHocKy` | `VARCHAR(20)` | `FOREIGN KEY` | Học kỳ |
| `NgayBatDau` | `DATE` | | Ngày bắt đầu |
| `NgayKetThuc` | `DATE` | | Ngày kết thúc |
| `FileMinhChung` | `VARCHAR(255)` | | Đường dẫn file minh chứng (kế hoạch, quyết định) – 1 file duy nhất |
| `TrangThai` | `ENUM('ChuanBi','ChoDuyet','DaDuyet','DangThucHien','DaKetThuc')` | `DEFAULT 'ChuanBi'` | Trạng thái |
| `NguoiDuyet` | `VARCHAR(20)` | `FOREIGN KEY` | Người duyệt (TTĐTSX) |
| `NgayDuyet` | `DATETIME` | | Ngày duyệt |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |

**Bảng `PhanCongThucTap` – Phân công thực tập và đánh giá**

| Tên trường | Kiểu dữ liệu | Ràng buộc | Mô tả |
| :--- | :--- | :--- | :--- |
| `MaThucTap` | `INT` | `AUTO_INCREMENT PRIMARY KEY` | Mã phân công |
| `MaDotTT` | `INT` | `FOREIGN KEY` | Đợt thực tập |
| `MaSV` | `VARCHAR(20)` | `FOREIGN KEY` | Sinh viên |
| `MaDoanhNghiep` | `VARCHAR(20)` | `FOREIGN KEY` | Doanh nghiệp tiếp nhận |
| `MaGiangVienGiamSat` | `VARCHAR(20)` | `FOREIGN KEY` | Giảng viên giám sát |
| `DiemDN` | `DECIMAL(4,2)` | | Điểm doanh nghiệp đánh giá (thang 10) |
| `NhanXetDN` | `TEXT` | | Nhận xét của doanh nghiệp |
| `DiemGV` | `DECIMAL(4,2)` | | Điểm giảng viên đánh giá (thang 10) |
| `NhanXetGV` | `TEXT` | | Nhận xét của giảng viên |
| `NhanXetSV` | `TEXT` | | Nhận xét cảm nhận của sinh viên |
| `TrangThai` | `ENUM('DaPhanCong','DangThucTap','DaKetThuc','DaHuy')` | `DEFAULT 'DaPhanCong'` | Trạng thái phân công |
| `created_at` | `DATETIME` | `DEFAULT CURRENT_TIMESTAMP` | |
| `updated_at` | `DATETIME` | `ON UPDATE CURRENT_TIMESTAMP` | |
| **Ràng buộc UNIQUE** | `(MaDotTT, MaSV)` | | Mỗi sinh viên chỉ có một phân công trong một đợt thực tập |

---

# III. WORKFLOW CHI TIẾT HỆ THỐNG (HOÀN CHỈNH)

## GIAI ĐOẠN 1: XÂY DỰNG CƠ SỞ DỮ LIỆU NỀN

### Bước 1: Khởi tạo Người dùng & Phân quyền

**Tác nhân:** Phòng Đào tạo (PDT)

**Mục tiêu:** Đưa toàn bộ danh sách Giảng viên, Cố vấn học tập, Sinh viên, và Quản trị viên vào hệ thống.

**Thao tác chi tiết:**
1. **PDT** truy cập màn hình `Quản lý người dùng`.
2. **Import Excel:**
   - **Sheet 1: Giảng viên** (`MaNguoiDung`, `HoTen`, `Email`, `HocHam`, `HocVi`, `ChuyenNganh`). Hệ thống tạo `NguoiDung` và tự động gán `VaiTro = 'GV'`.
   - **Sheet 2: Cố vấn học tập** (`MaNguoiDung`, `HoTen`, `Email`, `MaLopHC`). Hệ thống tạo `NguoiDung` và gán `VaiTro = 'CVHT'`.
   - **Sheet 3: Sinh viên** (`MaNguoiDung`, `HoTen`, `Email`, `MaLopHC`, `TrangThaiSV`). Hệ thống tạo `NguoiDung` và gán `VaiTro = 'SV'`.
3. **Xử lý hệ thống:**
   - Mật khẩu mặc định được hash và gửi qua Email cho người dùng.
   - Ghi nhận vào bảng `NguoiDung_VaiTro`.

**Output:**
- ✅ Database đã có tất cả người dùng với vai trò tương ứng.

### Bước 2: Tạo Học kỳ và Lớp Hành chính

**Tác nhân:** PDT

**Mục tiêu:** Thiết lập khung thời gian và đơn vị lớp cố định cho chương trình đặc biệt.

**Thao tác chi tiết:**
1. **Tạo Học kỳ:**
   - Nhập `MaHocKy` (VD: `K64-HK1`), `TenHocKy`, `NgayBatDau`, `NgayKetThuc`.
   - Trạng thái ban đầu: `'SapDienRa'`.
2. **Tạo Lớp Hành chính:**
   - Nhập `MaLopHC` (VD: `64.CNTT-CLC`), `TenLop`.
   - Chọn `MaCTDT` (nếu đã có) và `MaCoVan` (từ danh sách CVHT đã import).

**Output:**
- ✅ Học kỳ và lớp hành chính sẵn sàng.

### Bước 3: Tạo Doanh nghiệp đối tác

**Tác nhân:** PDT, TTĐTSX

**Mục tiêu:** Lưu trữ thông tin doanh nghiệp và cấp tài khoản cho họ.

**Thao tác chi tiết:**
1. **Nhập thông tin DN:** `TenDoanhNghiep`, `LinhVuc`, `NguoiDaiDien`, `Email`.
2. **Hệ thống tự động:**
   - Tạo bản ghi trong bảng `DoanhNghiep`.
   - **Tự động tạo tài khoản** trong bảng `NguoiDung` với `TenDangNhap` sinh từ tên DN, `VaiTro = 'DN'`.
   - Gửi Email kích hoạt tài khoản cho Doanh nghiệp.

**Output:**
- ✅ Doanh nghiệp đã có tài khoản đăng nhập hệ thống.

---

## GIAI ĐOẠN 2: QUẢN LÝ HỌC PHẦN VÀ ĐỘI NGŨ GIẢNG VIÊN

### Bước 4: Xây dựng Thư viện Học phần

**Tác nhân:** Chủ nhiệm học phần (CNHP), PDT

**Mục tiêu:** Chuẩn hóa danh mục học phần gốc trước khi đưa vào CTĐT.

**Thao tác chi tiết:**
1. **CNHP** upload file `FileDeCuong` (PDF) – đề cương khung của môn học.
2. **Trình duyệt:** Gửi yêu cầu phê duyệt (`ChoDuyet`).
3. **PDT/TTĐTSX** duyệt: `TrangThai` chuyển thành `DaDuyet`.

**Output:**
- ✅ Thư viện học phần đã được phê duyệt.

### Bước 5: Gán Đội ngũ Giảng viên cho Học phần

**Tác nhân:** CNHP

**Mục tiêu:** Xác định những Giảng viên nào đủ thẩm quyền để dạy môn này.

**Thao tác chi tiết:**
1. Chọn `HocPhan` (VD: `MAT322`).
2. Chọn `Giảng viên` từ danh sách người dùng có `VaiTro = 'GV'`.
3. Hệ thống ghi nhận vào bảng `DoiNguGiangVienHP`.

**Output:**
- ✅ Mỗi học phần có danh sách giảng viên trong đội ngũ.

---

## GIAI ĐOẠN 3: XÂY DỰNG CTĐT VÀ MỞ LỚP HỌC PHẦN

### Bước 6: Xây dựng & Cấu hình chi tiết CTĐT

**Tác nhân:** Ban chủ nhiệm CTĐT (BCN), TTĐTSX, PDT

**Mục tiêu:** Định nghĩa chương trình học gồm những môn nào, ở kỳ thứ mấy.

**Thao tác chi tiết:**
1. **BCN** tạo mới `ChuongTrinhDaoTao`: Nhập `TenCTDT`, `Khoa`, upload `FileWord` (mô tả tổng quan).
2. **Cấu hình chi tiết (bảng `CTDT_HocPhan`):**
   - Nhập danh sách học phần kèm `HocKyThu` và `SoLopDuKien`.
   - Ví dụ: `MAT322` học ở `HocKyThu = 1`, `SoLopDuKien = 2`.
3. **Trình duyệt:** Gửi lên TTĐTSX và PDT.
4. **PDT/TTĐTSX** phê duyệt: Cập nhật `TrangThai = 'DaDuyet'`.

**Output:**
- ✅ CTĐT được duyệt và có cấu trúc chi tiết.

### Bước 7: Hệ thống TỰ ĐỘNG sinh Lớp Học Phần

**Kích hoạt:** `ChuongTrinhDaoTao.TrangThai` chuyển sang `'DaDuyet'`.

**Logic hệ thống:**
1. Đọc toàn bộ dữ liệu từ `CTDT_HocPhan` của CTĐT vừa duyệt.
2. **Ánh xạ `HocKyThu` sang `MaHocKy` thực tế** dựa trên năm bắt đầu của khóa học.
3. Với mỗi dòng, tạo số lượng lớp tương ứng (`SoLopDuKien`):
   - Xác định `MaLopHC` (BCN có thể phân bổ trước hoặc hệ thống gợi ý).
   - Gán `NhomHocPhan` (số nguyên tăng dần hoặc do BCN chỉ định).
   - Tạo bản ghi `LopHocPhan` với `MaGiangVien = NULL`, `TrangThai = 'DangMo'`.

**Output:**
- ✅ Danh sách lớp học phần (nhóm học phần) sẵn sàng để phân công giảng viên.

### Bước 8: BCN Phân công Giảng viên cho Lớp Học Phần

**Tác nhân:** BCN

**Mục tiêu:** Gán người đứng lớp cụ thể.

**Thao tác chi tiết:**
1. **BCN** vào màn hình `Phân công giảng dạy`.
2. Lọc theo `HocKy` và `MaHocPhan`.
3. Tại mỗi dòng (`MaHocPhan, MaHocKy, NhomHocPhan`), chọn `MaGiangVien` từ dropdown.
4. **Lưu thay đổi:** Cập nhật trực tiếp `LopHocPhan.MaGiangVien`. **Không cần phê duyệt từ TTĐTSX/PDT.**
5. **Hệ thống gửi Email thông báo** cho giảng viên được gán kèm yêu cầu upload đề cương chi tiết.

**Output:**
- ✅ Tất cả lớp học phần đã có giảng viên.

---

## GIAI ĐOẠN 4: VẬN HÀNH GIẢNG DẠY & TÀI LIỆU

### Bước 9: Giảng viên Upload Đề cương Chi tiết

**Tác nhân:** Giảng viên (GV)

**Thao tác chi tiết:**
1. **GV** đăng nhập, chọn lớp học phần (dựa trên `MaHocPhan, MaHocKy, NhomHocPhan`).
2. Upload file PDF vào mục `TaiLieuMonHoc` với `Loai = 'DeCuongChiTiet'`.
3. **Hệ thống ghi nhận:**
   - `NgayNop` thực tế.
   - `TrangThai = 'ChoDuyet'`.
   - Gửi thông báo cho **CNHP** của học phần đó.

### Bước 10: Chủ nhiệm Học phần Duyệt Đề cương

**Tác nhân:** CNHP

**Thao tác chi tiết:**
1. **CNHP** xem file PDF, đối chiếu với `CLOs` gốc của học phần.
2. **Phê duyệt:**
   - **Đạt:** `TrangThai = 'DaDuyet'`. Đề cương được công bố ngay cho sinh viên thuộc lớp hành chính tương ứng.
   - **Không đạt:** `TrangThai = 'TuChoi'`, nhập `NhanXet` yêu cầu chỉnh sửa. GV upload lại file mới.

### Bước 11: Quy trình Đề thi

- Tương tự Bước 9-10, áp dụng cho `Loai = 'DeThiGiuaKy'` và `'DeThiCuoiKy'`.

---

## GIAI ĐOẠN 5: THEO DÕI CẢNH BÁO & TƯ VẤN HỌC TẬP

### Bước 12: Giảng viên Nhập Nhận xét Quá trình

**Tác nhân:** Giảng viên

**Thao tác chi tiết:**
1. **GV** chọn lớp học phần (`MaHocPhan, MaHocKy, NhomHocPhan`).
2. Hệ thống hiển thị danh sách sinh viên dựa trên `MaLopHC` của lớp học phần đó (JOIN `LopHocPhan` -> `NguoiDung` WHERE `MaLopHC` = ...).
3. GV chọn sinh viên, chọn `LoaiNhanXet` (`TichCuc`/`TieuCuc`), nhập `NoiDung`.
4. Lưu vào `DanhGiaVaCanhBao` với `LoaiDanhGia = 'QuaTrinh'`.
5. **Trigger hệ thống (nếu `LoaiNhanXet = 'TieuCuc'`):**
   - Gửi **Email cảnh báo ngay lập tức** tới Sinh viên và **CVHT** của lớp hành chính đó.
   - `DaXuLy` mặc định = `0`.

### Bước 13: Cố vấn Học tập Xử lý Cảnh báo

**Tác nhân:** CVHT

**Thao tác chi tiết:**
1. **CVHT** vào Dashboard `Cảnh báo chưa xử lý`.
2. Xem chi tiết cảnh báo (`MaSV`, `NoiDung` từ GV).
3. **Thực hiện tư vấn** (gặp mặt/gọi điện).
4. **Cập nhật hệ thống:**
   - Nhập `KetQuaXuLy`.
   - Tích chọn `DaXuLy = 1`.
5. Hệ thống gửi Email thông báo "Đã tiếp nhận tư vấn" cho Sinh viên.

### Bước 14: CVHT Đánh giá Tổng kết Học kỳ

**Tác nhân:** CVHT

**Thời điểm:** Cuối học kỳ.

**Thao tác chi tiết:**
1. **CVHT** chọn `HocKy` và `MaSV`.
2. Nhập nhận xét tổng kết.
3. Lưu vào `DanhGiaVaCanhBao` với:
   - `LoaiDanhGia = 'TongKetKy'`.
   - Các trường `MaHocPhan, MaHocKy, NhomHocPhan` để `NULL`.

---

## GIAI ĐOẠN 6: KIẾN TẬP & THỰC TẬP DOANH NGHIỆP

### Bước 15: Lập Kế hoạch Kiến tập

**Tác nhân:** BCN

**Thao tác chi tiết:**
1. **BCN** tạo `DotKienTap`: Chọn `MaLopHC`, `MaDoanhNghiep`, `MaGVPhuTrach`.
2. Upload **01 file duy nhất** vào trường `FileMinhChung` (file bao gồm Kế hoạch + Công văn).
3. Trình TTĐTSX duyệt (`ChoDuyet` -> `DaDuyet`).

### Bước 16: Nhận xét Kiến tập

- **GV:** Nhập `NhanXetGV` (về ý thức đoàn).
- **DN:** Nhập `NhanXetDN` (về sự chuẩn bị của SV).

### Bước 17: Lập Kế hoạch Thực tập

**Tác nhân:** BCN

**Thao tác chi tiết:**
1. **BCN** tạo `DotThucTap`: Chọn `MaHocKy`, `NgayBatDau`, `NgayKetThuc`.
2. Upload **01 file duy nhất** vào trường `FileMinhChung` (Quyết định mở đợt thực tập).
3. Trình TTĐTSX duyệt.

### Bước 18: Phân công Thực tập hàng loạt

**Tác nhân:** BCN

**Thao tác chi tiết:**
1. **BCN** chuẩn bị file Excel gồm: `MaSV`, `MaDoanhNghiep`, `MaGiangVienGiamSat`.
2. **Import vào hệ thống.**
3. **Validation:** Kiểm tra SV có đang trong đợt thực tập khác không (`UNIQUE KEY MaDotTT, MaSV`).
4. **Insert hàng loạt** vào `PhanCongThucTap`.
5. Gửi Email thông báo cho SV, DN, GV giám sát.

### Bước 19: Đánh giá Thực tập

- **DN** đăng nhập, nhập `DiemDN` và `NhanXetDN`.
- **GV giám sát** đăng nhập, nhập `DiemGV` và `NhanXetGV`.
- **Sinh viên** đăng nhập xem kết quả và nhập `NhanXetSV` (cảm nghĩ cá nhân).

---

## GIAI ĐOẠN 7: TỔNG KẾT VÀ BÁO CÁO

### Bước 20: Báo cáo & Chuyển kỳ

**Tác nhân:** CVHT, PDT

**Thao tác chi tiết:**
1. **Xuất báo cáo:**
   - **Báo cáo Cảnh báo:** Thống kê tỷ lệ sinh viên bị cảnh báo, tỷ lệ đã xử lý.
   - **Báo cáo Thực tập:** Xuất danh sách điểm và nhận xét từ doanh nghiệp.
2. **Chốt học kỳ:**
   - PDT cập nhật `HocKyNamHoc.TrangThai` thành `'DaKetThuc'`.
   - Kích hoạt học kỳ tiếp theo và lặp lại từ **Bước 6**.

---

Hai tài liệu trên đã được cập nhật đầy đủ, nhất quán và sẵn sàng để sử dụng làm cơ sở cho việc phát triển và kiểm thử hệ thống.