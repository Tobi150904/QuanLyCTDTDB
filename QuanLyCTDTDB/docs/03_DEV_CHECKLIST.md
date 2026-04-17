Tôi đã đối chiếu file `03_DEV_CHECKLIST.md` với **Thiết kế CSDL** và **Workflow** đã thống nhất. Dưới đây là phiên bản **cập nhật hoàn chỉnh**, đảm bảo tính nhất quán và chi tiết đến từng Entity, Repository, Service và Test case.

---

## 📋 DEVELOPMENT CHECKLIST (ĐÃ CẬP NHẬT THEO ĐẶC TẢ MỚI NHẤT)

### PHASE 0: SETUP MÔI TRƯỜNG

#### Database
- [ ] Chạy `01_create_tables.sql` (phiên bản mới với 15 bảng, bao gồm `CTDT_HocPhan`, khóa tổ hợp cho `LopHocPhan`).
- [ ] Chạy `02_seed_data.sql` để có dữ liệu test phù hợp với cấu trúc mới.
- [ ] Kiểm tra: 15 bảng tồn tại, FK đúng, ràng buộc UNIQUE hoạt động.
- [ ] Hash lại mật khẩu seed data bằng BCrypt nếu cần test thực tế.

#### Spring Boot Project
- [ ] Tạo project với các dependency: Web, Thymeleaf, Security, JPA, MySQL, Validation, Mail, Lombok.
- [ ] Thêm Apache POI vào `pom.xml`.
- [ ] Cấu hình `application.properties` (DB, Mail, Upload dir).
- [ ] Test chạy được `localhost:8080`.

---

### PHASE 1: FOUNDATION (KHÔNG CODE BUSINESS TRƯỚC KHI XONG)

#### Enums (13 files)
- [ ] `VaiTro.java` (SV, GV, CVHT, BCN, CNHP, PDT, TTDTXS, DN)
- [ ] `TrangThaiHocKy.java` (SapDienRa, DangDienRa, DaKetThuc)
- [ ] `TrangThaiCTDT.java` (BanNhap, ChoDuyet, DaDuyet, DaHuy)
- [ ] `TrangThaiHocPhan.java` (BanNhap, ChoDuyet, DaDuyet)
- [ ] `TrangThaiLopHP.java` (DangMo, DaDong, DaHuy)
- [ ] `LoaiTaiLieu.java` (DeCuongChiTiet, DeThiGiuaKy, DeThiCuoiKy)
- [ ] `TrangThaiTaiLieu.java` (ChoDuyet, DaDuyet, TuChoi)
- [ ] `LoaiNhanXet.java` (TichCuc, TieuCuc)
- [ ] `LoaiDanhGia.java` (QuaTrinh, TongKetKy)
- [ ] `TrangThaiDotKT.java` (ChuanBi, ChoDuyet, DaDuyet, DaThucHien, DaHuy)
- [ ] `TrangThaiDotTT.java` (ChuanBi, ChoDuyet, DaDuyet, DangThucHien, DaKetThuc)
- [ ] `TrangThaiPhanCong.java` (DaPhanCong, DangThucTap, DaKetThuc, DaHuy)
- [ ] `TrangThaiDoanhNghiep.java` (DangHopTac, TamNgung)
- [ ] `TrangThaiSinhVien.java` (DangHoc, BaoLuu, ThoiHoc, TotNghiep)

#### Entities (15 files – thêm `CTDT_HocPhan`)
- [ ] `HocKyNamHoc.java`
- [ ] `LopHanhChinh.java`
- [ ] `NguoiDung.java`
- [ ] `NguoiDungVaiTro.java` → `@EmbeddedId` với `NguoiDungVaiTroId`
- [ ] `DoanhNghiep.java`
- [ ] `ChuongTrinhDaoTao.java`
- [ ] `CTDT_HocPhan.java` → `@EmbeddedId` với `CTDT_HocPhanId` (MaCTDT, MaHocPhan)
- [ ] `HocPhan.java`
- [ ] `DoiNguGiangVienHP.java` → `@EmbeddedId` với `DoiNguGiangVienHPId`
- [ ] `LopHocPhan.java` → `@EmbeddedId` với `LopHocPhanId` (MaHocPhan, MaHocKy, NhomHocPhan) + `@ManyToOne` đến `LopHanhChinh`
- [ ] `TaiLieuMonHoc.java` → `@ManyToOne` đến `LopHocPhan` bằng `@JoinColumns`, `@UniqueConstraint` trên (MaHocPhan, MaHocKy, NhomHocPhan, Loai)
- [ ] `DanhGiaVaCanhBao.java` → `@ManyToOne` đến `LopHocPhan` (nullable), thêm `LoaiDanhGia`
- [ ] `DotKienTap.java` → thêm `FileMinhChung`
- [ ] `DotThucTap.java` → thêm `FileMinhChung`
- [ ] `PhanCongThucTap.java` → `@UniqueConstraint` (MaDotTT, MaSV)

#### Repositories (15 files – điều chỉnh theo khóa tổ hợp)
- [ ] `HocKyNamHocRepository`
  - `findByTrangThai(TrangThaiHocKy)`
  - `findFirstByTrangThaiOrderByNgayBatDauAsc(TrangThaiHocKy)`
- [ ] `LopHanhChinhRepository`
  - `findByMaCoVan(String maCVHT)`
- [ ] `NguoiDungRepository`
  - `findByTenDangNhap(String tenDangNhap)`
  - `findByMaLopHC(String maLopHC)`
  - `findByVaiTros_Id_VaiTro(VaiTro vaiTro)`
- [ ] `NguoiDungVaiTroRepository`
  - `findByNguoiDung_MaNguoiDung(String maNguoiDung)`
- [ ] `DoanhNghiepRepository`
  - `findByTrangThai(TrangThaiDoanhNghiep)`
  - `Page<DoanhNghiep> findByTenDoanhNghiepContaining(String keyword, Pageable)`
- [ ] `ChuongTrinhDaoTaoRepository`
  - `findByTrangThai(TrangThaiCTDT)`
  - `findByNguoiTao(NguoiDung nguoiTao)`
- [ ] `CTDT_HocPhanRepository`
  - `findById_MaCTDT(String maCTDT)`
  - `existsById_MaCTDTAndId_MaHocPhan(String maCTDT, String maHocPhan)`
- [ ] `HocPhanRepository`
  - `findByTrangThai(TrangThaiHocPhan)`
  - `findByChuNhiemHP(NguoiDung cn)`
- [ ] `DoiNguGiangVienHPRepository`
  - `findById_MaHocPhan(String maHocPhan)`
  - `existsById_MaHocPhanAndId_MaGiangVienAndTrangThai(String maHP, String maGV, boolean trangThai)`
- [ ] `LopHocPhanRepository`
  - `findById_MaHocPhanAndId_MaHocKy(String maHocPhan, String maHocKy)`
  - `findById_MaHocKyAndMaGiangVienIsNull(String maHocKy)`
  - `findByMaGiangVien(String maGV)`
  - `findByMaLopHC(String maLopHC)`
- [ ] `TaiLieuMonHocRepository`
  - `findByLopHocPhan_Id(LopHocPhanId id)`
  - `findByLopHocPhan_IdAndLoai(LopHocPhanId id, LoaiTaiLieu loai)`
  - `findByTrangThai(TrangThaiTaiLieu trangThai)`
- [ ] `DanhGiaVaCanhBaoRepository`
  - `findByLopHocPhan_Id(LopHocPhanId id)` (cho đánh giá quá trình)
  - `findByMaSVAndLoaiDanhGia(String maSV, LoaiDanhGia loai)`
  - `findByLopHocPhan_MaLopHCAndDaXuLyFalse(String maLopHC)` (lấy cảnh báo chưa xử lý theo lớp HC)
  - `countByLopHocPhan_MaLopHCAndDaXuLyFalse(String maLopHC)`
- [ ] `DotKienTapRepository`
  - `findByLopHanhChinh_MaLopHC(String maLopHC)`
- [ ] `DotThucTapRepository`
  - `findByHocKy_MaHocKy(String maHocKy)`
- [ ] `PhanCongThucTapRepository`
  - `findByDotThucTap_MaDotTT(Integer maDotTT)`
  - `findBySinhVien_MaNguoiDung(String maSV)`
  - `existsByDotThucTap_MaDotTTAndSinhVien_MaNguoiDung(Integer maDotTT, String maSV)`

#### Security
- [ ] `UserDetailsServiceImpl.java`
  - `loadUserByUsername(tenDangNhap OR email)`
  - Map `VaiTro` → `GrantedAuthority "ROLE_XXX"`
  - Kiểm tra `TrangThaiTK = 1`
- [ ] `SecurityConfig.java`
  - `BCryptPasswordEncoder` bean
  - Authorization rules theo role
  - Login page `/login`, logout URL

#### Config
- [ ] `WebMvcConfig.java` (static resources + file upload config)
- [ ] Tạo thư mục `uploads/` trong resources (hoặc cấu hình đường dẫn tuyệt đối)

---

### PHASE 2: MODULE P0 - CORE

#### Auth + Dashboard
- [ ] `AuthController.java` (GET /login, logout)
- [ ] `templates/auth/login.html`
- [ ] `DashboardController.java` (GET /dashboard, redirect theo role)
- [ ] `templates/dashboard/index.html` (thống kê tổng quan)
- [ ] `templates/layout/base.html` (sidebar menu theo role)
- [ ] **TEST:** Đăng nhập 8 role khác nhau, thấy đúng menu

#### Quản lý Người Dùng [PDT, TTDTXS]
- [ ] `NguoiDungService` interface + impl
- [ ] `NguoiDungController`
  - `GET  /nguoi-dung` (list + filter theo role + search)
  - `GET  /nguoi-dung/them` (form)
  - `POST /nguoi-dung/them`
  - `GET  /nguoi-dung/{id}/sua`
  - `POST /nguoi-dung/{id}/sua`
  - `POST /nguoi-dung/{id}/khoa`
  - `POST /nguoi-dung/import` (Excel)
- [ ] `templates/nguoidung/list.html`
- [ ] `templates/nguoidung/form.html`
- [ ] `ExcelImportUtil.java` (đọc file .xlsx, map sang NguoiDungDTO)
- [ ] **TEST:** Import file Excel mẫu, kiểm tra dữ liệu được tạo đúng

#### Quản lý Doanh Nghiệp [PDT, TTDTXS]
- [ ] `DoanhNghiepService` interface + impl
  - CRUD
  - `taoTaiKhoanDN()` – tự động khi tạo DN mới
- [ ] `DoanhNghiepController`
  - `GET  /doanh-nghiep`
  - `GET  /doanh-nghiep/them`
  - `POST /doanh-nghiep/them`
  - `GET  /doanh-nghiep/{ma}/sua`
  - `POST /doanh-nghiep/{ma}/sua`
  - `POST /doanh-nghiep/{ma}/doi-trang-thai`
- [ ] `templates/doanhnghiep/list.html`
- [ ] `templates/doanhnghiep/form.html`
- [ ] **TEST:** Tạo DN mới → Kiểm tra tài khoản `NguoiDung` với role `DN` được tạo

---

### PHASE 3: MODULE P1 - NGHIỆP VỤ CHÍNH

#### Quản lý Học Phần [BCN, CNHP, PDT]
- [ ] `HocPhanService` interface + impl
- [ ] `HocPhanController` (CRUD + đội ngũ + workflow)
- [ ] `templates/hocphan/list.html`
- [ ] `templates/hocphan/form.html`
- [ ] `templates/hocphan/detail.html` (bao gồm danh sách đội ngũ GV)
- [ ] **TEST:**
  - BCN tạo HP → `ChoDuyet` → TTDTXS duyệt → `DaDuyet`
  - CNHP thêm/xóa GV khỏi đội ngũ

#### Quản lý CTDT [BCN, TTDTXS, PDT]
- [ ] `ChuongTrinhDaoTaoService` interface + impl
  - `autoCreateLopHocPhan()` – **QUAN TRỌNG**: dựa vào `CTDT_HocPhan` để tạo `LopHocPhan` với `NhomHocPhan` và `MaLopHC`
- [ ] `ChuongTrinhDaoTaoController`
- [ ] `templates/ctdt/list.html`
- [ ] `templates/ctdt/form.html`
- [ ] `templates/ctdt/detail.html` (hiển thị danh sách học phần trong CTDT)
- [ ] **TEST:**
  - BCN tạo CTDT, nhập chi tiết `CTDT_HocPhan` → nộp duyệt → TTDTXS phê duyệt
  - Kiểm tra `LopHocPhan` được tạo tự động với `NhomHocPhan` và `MaLopHC` hợp lý
  - `MaGiangVien = null` ban đầu

#### Quản lý Lớp Học Phần [BCN, GV, CNHP]
- [ ] `LopHocPhanService` interface + impl
- [ ] `LopHocPhanController`
  - `GET  /lop-hoc-phan` (filter theo HK, HP)
  - `GET  /lop-hoc-phan/{maHocPhan}/{maHocKy}/{nhomHocPhan}`
  - `POST /lop-hoc-phan/{maHocPhan}/{maHocKy}/{nhomHocPhan}/gan-gv`
- [ ] `templates/lophocphan/list.html`
- [ ] `templates/lophocphan/detail.html`
- [ ] **TEST:**
  - BCN gán GV cho lớp HP
  - GV có trong đội ngũ → không cảnh báo
  - GV không trong đội ngũ → hiện cảnh báo nhưng vẫn cho gán

---

### PHASE 4: MODULE P2 - TÀI LIỆU & ĐÁNH GIÁ

#### Tài liệu Môn Học [GV, CNHP]
- [ ] `TaiLieuMonHocService` interface + impl
  - `nopTaiLieu()` – UPDATE nếu đã có (do UNIQUE), INSERT nếu chưa
  - `isQuaHanDeCuong()` (kiểm tra deadline 14 ngày)
- [ ] `TaiLieuController`
- [ ] `templates/tailieu/list.html` (theo lớp HP)
- [ ] **TEST:**
  - GV upload `DeCuongChiTiet`
  - CNHP duyệt → `DaDuyet`
  - CNHP từ chối → `TuChoi`
  - GV upload lại → UPDATE bản cũ (do UNIQUE)
  - Kiểm tra deadline (> 14 ngày phải hiện cảnh báo)

#### Đánh Giá & Cảnh Báo [GV, CVHT]
- [ ] `DanhGiaVaCanhBaoService` interface + impl
  - `taoNhanXet()` – có side effect gửi email nếu `TieuCuc` và `LoaiDanhGia = QuaTrinh`
- [ ] `EmailService` impl (Spring Mail hoặc mock trong dev)
- [ ] `DanhGiaController`
- [ ] `templates/danhgia/list.html` (theo lớp HP)
- [ ] `templates/danhgia/canhbao.html` (CVHT xử lý)
- [ ] **TEST:**
  - GV nhập nhận xét `TichCuc` → chỉ lưu, không gửi email
  - GV nhập nhận xét `TieuCuc` → lưu + gửi email (check log)
  - CVHT xử lý cảnh báo → `DaXuLy=1`, `KetQuaXuLy` có nội dung
  - Badge count trên dashboard (số cảnh báo chưa xử lý)

---

### PHASE 5: MODULE P2 - KIẾN TẬP & THỰC TẬP

#### Kiến Tập [BCN, GV, TTDTXS, DN]
- [ ] `KienTapService` interface + impl
- [ ] `KienTapController`
- [ ] `templates/kientap/list.html`
- [ ] `templates/kientap/form.html`
- [ ] `templates/kientap/detail.html`
- [ ] **TEST:**
  - BCN tạo đợt kiến tập → upload 1 file `FileMinhChung` → TTDTXS duyệt
  - GV nhập `NhanXetGV`
  - DN login → nhập `NhanXetDN`
  - Danh sách SV = lấy từ `LopHanhChinh`

#### Thực Tập [PDT, TTDTXS, GV, SV, DN]
- [ ] `ThucTapService` interface + impl
  - `importPhanCongFromExcel()` – xử lý trùng (skip + báo lỗi)
- [ ] `ThucTapController`
- [ ] `templates/thuctap/list.html` (danh sách đợt thực tập)
- [ ] `templates/thuctap/detail.html` (danh sách phân công)
- [ ] `templates/thuctap/phan-cong-form.html`
- [ ] **TEST:**
  - Import Excel phân công (cả trường hợp trùng + hợp lệ)
  - DN nhập điểm + nhận xét
  - GV nhập điểm + nhận xét
  - SV nhập `NhanXetSV` (cảm nhận)
  - Check UNIQUE constraint (1 SV chỉ 1 lần/đợt)

---

### PHASE 6: BÁO CÁO & DASHBOARD

- [ ] `BaoCaoService` interface + impl
- [ ] `BaoCaoController`
- [ ] `templates/baocao/tongquan.html` (charts, số liệu)
- [ ] Xuất Excel (Apache POI):
  - Danh sách người dùng
  - Danh sách lớp HP + trạng thái tài liệu
  - Báo cáo thực tập + điểm số
- [ ] **TEST:** Tải file Excel và kiểm tra dữ liệu

---

### KIỂM TRA CUỐI CÙNG TRƯỚC KHI DEMO

- [ ] Test login toàn bộ 8 role
- [ ] Test phân quyền: Role X không truy cập được URL của Role Y
- [ ] Test toàn bộ workflow: CTDT, HP, Tài liệu, Kiến tập, Thực tập
- [ ] Test import Excel: người dùng + phân công thực tập
- [ ] Test auto-create `LopHocPhan` sau khi duyệt CTDT (đúng `NhomHocPhan` và `MaLopHC`)
- [ ] Test email cảnh báo (hoặc check log)
- [ ] Test UNIQUE constraint: `TaiLieuMonHoc`, `PhanCongThucTap`
- [ ] Test deadline đề cương (quá 14 ngày)
- [ ] Kiểm tra encoding tiếng Việt trong DB và UI
- [ ] Test upload file (PDF, DOCX, XLSX)
- [ ] Kiểm tra responsive (nếu có yêu cầu)