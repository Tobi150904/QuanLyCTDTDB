# DEVELOPMENT CHECKLIST - Track tien do, dam bao khong bo sot

---

## PHASE 0: SETUP (Lam mot lan dau)

### Database
- [ ] Chay `01_create_tables.sql` tren XAMPP MySQL
- [ ] Chay `02_seed_data.sql` de co data test
- [ ] Kiem tra: 14 bang ton tai, FK dung, data mau co
- [ ] Hash lai mat khau seed data bang BCrypt neu test thuc te

### Spring Boot Project
- [ ] Tao project Spring Initializr: Web, Thymeleaf, Security, JPA, MySQL, Validation, Mail, Lombok
- [ ] Them Apache POI vao pom.xml
- [ ] Cau hinh `application.properties` (DB, Mail, Upload dir)
- [ ] Test chay duoc `localhost:8080`

---

## PHASE 1: FOUNDATION (Khong code business truoc khi xong phase nay)

### Enums (13 files)
- [ ] VaiTro.java               (SV, GV, CVHT, BCN, CNHP, PDT, TTDTXS, DN)
- [ ] TrangThaiHocKy.java       (SapDienRa, DangDienRa, DaKetThuc)
- [ ] TrangThaiCTDT.java        (BanNhap, ChoDuyet, DaDuyet, DaHuy)
- [ ] TrangThaiHocPhan.java     (BanNhap, ChoDuyet, DaDuyet)
- [ ] TrangThaiLopHP.java       (DangMo, DaDong, DaHuy)
- [ ] LoaiTaiLieu.java          (DeCuongChiTiet, DeThiGiuaKy, DeThiCuoiKy)
- [ ] TrangThaiTaiLieu.java     (ChoDuyet, DaDuyet, TuChoi)
- [ ] LoaiNhanXet.java          (TichCuc, TieuCuc)
- [ ] TrangThaiDotKT.java       (ChuanBi, ChoDuyet, DaDuyet, DaThucHien, DaHuy)
- [ ] TrangThaiDotTT.java       (ChuanBi, ChoDuyet, DaDuyet, DangThucHien, DaKetThuc)
- [ ] TrangThaiPhanCong.java    (DaPhanCong, DangThucTap, DaKetThuc, DaHuy)
- [ ] TrangThaiDoanhNghiep.java (DangHopTac, TamNgung)
- [ ] TrangThaiSinhVien.java    (DangHoc, BaoLuu, ThoiHoc, TotNghiep)

### Entities (14 files)
- [ ] HocKyNamHoc.java
- [ ] LopHanhChinh.java
- [ ] NguoiDung.java
- [ ] NguoiDungVaiTro.java          <- @EmbeddedId + NguoiDungVaiTroId
- [ ] DoanhNghiep.java
- [ ] ChuongTrinhDaoTao.java
- [ ] HocPhan.java
- [ ] DoiNguGiangVienHP.java         <- @EmbeddedId + DoiNguGiangVienHPId
- [ ] LopHocPhan.java
- [ ] TaiLieuMonHoc.java             <- @UniqueConstraint(MaLopHP, Loai)
- [ ] DanhGiaVaCanhBao.java
- [ ] DotKienTap.java
- [ ] DotThucTap.java
- [ ] PhanCongThucTap.java           <- @UniqueConstraint(MaDotTT, MaSV)

### Repositories (14 files)
- [ ] HocKyNamHocRepository
       + findByTrangThai(TrangThaiHocKy)
       + findFirstByTrangThaiOrderByNgayBatDauAsc(TrangThaiHocKy)
- [ ] LopHanhChinhRepository
       + findByMaCoVan(String maCVHT)
- [ ] NguoiDungRepository
       + findByTenDangNhap(String tenDangNhap)   <- UserDetailsService dung
       + findByMaLopHC(String maLopHC)
       + findByVaiTros_Id_VaiTro(VaiTro vaiTro)
- [ ] NguoiDungVaiTroRepository
       + findByNguoiDung_MaNguoiDung(String maNguoiDung)
- [ ] DoanhNghiepRepository
       + findByTrangThai(TrangThaiDoanhNghiep)
       + Page<DoanhNghiep> findByTenDoanhNghiepContaining(String keyword, Pageable)
- [ ] ChuongTrinhDaoTaoRepository
       + findByTrangThai(TrangThaiCTDT)
       + findByNguoiTao(NguoiDung nguoiTao)
- [ ] HocPhanRepository
       + findByTrangThai(TrangThaiHocPhan)
       + findByChuNhiemHP(NguoiDung gnv)
- [ ] DoiNguGiangVienHPRepository
       + existsById_MaHocPhanAndId_MaGiangVienAndTrangThai(String maHP, String maGV, boolean ts)
       + findByHocPhan_MaHocPhan(String maHocPhan)
- [ ] LopHocPhanRepository
       + findByHocPhan_MaHocPhan(String maHocPhan)
       + findByGiangVien_MaNguoiDung(String maGV)
       + findByHocKy_MaHocKy(String maHocKy)
       + findByMaGiangVienIsNull()
- [ ] TaiLieuMonHocRepository
       + findByLopHocPhan_MaLopHP(String maLopHP)
       + findByLopHocPhan_MaLopHPAndLoai(String maLopHP, LoaiTaiLieu loai)
       + findByTrangThai(TrangThaiTaiLieu)
- [ ] DanhGiaVaCanhBaoRepository
       + findByLopHocPhan_MaLopHP(String maLopHP)
       + findBySinhVien_MaNguoiDung(String maSV)
       + findByLopHocPhan_LopHanhChinh_MaLopHCAndDaXuLyFalse(String maLopHC)
       + countByLopHocPhan_LopHanhChinh_MaLopHCAndDaXuLyFalse(String maLopHC)
- [ ] DotKienTapRepository
       + findByLopHanhChinh_MaLopHC(String maLopHC)
- [ ] DotThucTapRepository
       + findByHocKy_MaHocKy(String maHocKy)
- [ ] PhanCongThucTapRepository
       + findByDotThucTap_MaDotTT(Integer maDotTT)
       + findBySinhVien_MaNguoiDung(String maSV)
       + existsByDotThucTap_MaDotTTAndSinhVien_MaNguoiDung(Integer maDotTT, String maSV)

### Security
- [ ] UserDetailsServiceImpl.java
       + loadUserByUsername(tenDangNhap OR email)
       + Map VaiTro -> GrantedAuthority "ROLE_PDT", "ROLE_GV", etc.
       + Kiem tra TrangThaiTK = 1
- [ ] SecurityConfig.java
       + BCryptPasswordEncoder bean
       + Authorization rules theo role (xem 00_MASTER_REFERENCE)
       + Login page /login, logout URL

### Config
- [ ] WebMvcConfig.java        <- Static resource + file upload config
- [ ] Tao thu muc uploads/ trong resources

---

## PHASE 2: MODULE P0 - CORE

### Auth + Dashboard
- [ ] AuthController.java (GET /login, logout)
- [ ] templates/auth/login.html
- [ ] DashboardController.java (GET /dashboard, redirect theo role)
- [ ] templates/dashboard/index.html (thong ke tong quan)
- [ ] templates/layout/base.html (sidebar menu theo role)
- [ ] TEST: Dang nhap 8 role khac nhau, thay dung menu

### Quan ly Nguoi Dung [PDT, TTDTXS]
- [ ] NguoiDungService interface + impl
- [ ] NguoiDungRepository (cac query can thiet)
- [ ] NguoiDungController
       + GET  /nguoi-dung (list + filter theo role + search)
       + GET  /nguoi-dung/them (form)
       + POST /nguoi-dung/them
       + GET  /nguoi-dung/{id}/sua
       + POST /nguoi-dung/{id}/sua
       + POST /nguoi-dung/{id}/khoa
       + POST /nguoi-dung/import (Excel)
- [ ] templates/nguoidung/list.html
- [ ] templates/nguoidung/form.html
- [ ] ExcelImportUtil.java (doc file .xlsx, map sang NguoiDungDTO)
- [ ] TEST: Import file Excel mau, kiem tra data duoc tao dung

### Quan ly Doanh Nghiep [PDT, TTDTXS]
- [ ] DoanhNghiepService interface + impl
       + CRUD
       + taoTaiKhoanDN() - tu dong khi tao DN moi
- [ ] DoanhNghiepController
       + GET  /doanh-nghiep
       + GET  /doanh-nghiep/them
       + POST /doanh-nghiep/them
       + GET  /doanh-nghiep/{ma}/sua
       + POST /doanh-nghiep/{ma}/sua
       + POST /doanh-nghiep/{ma}/doi-trang-thai
- [ ] templates/doanhnghiep/list.html
- [ ] templates/doanhnghiep/form.html
- [ ] TEST: Tao DN moi -> Kiem tra tai khoan NguoiDung DN duoc tao

---

## PHASE 3: MODULE P1 - NGHIEP VU CHINH

### Quan ly Hoc Phan [BCN, CNHP, PDT]
- [ ] HocPhanService interface + impl
- [ ] HocPhanController (CRUD + doi ngu + workflow)
- [ ] templates/hocphan/list.html
- [ ] templates/hocphan/form.html
- [ ] templates/hocphan/detail.html (bao gom doi ngu GV)
- [ ] TEST:
       - BCN tao HP -> ChoDuyet -> TTDTXS duyet -> DaDuyet
       - CNHP them/xoa GV khoi doi ngu

### Quan ly CTDT [BCN, TTDTXS, PDT]
- [ ] ChuongTrinhDaoTaoService interface + impl
       + autoCreateLopHocPhan() - QUAN TRONG
- [ ] ChuongTrinhDaoTaoController
- [ ] templates/ctdt/list.html
- [ ] templates/ctdt/form.html
- [ ] templates/ctdt/detail.html
- [ ] TEST:
       - BCN tao CTDT -> nop duyet -> TTDTXS phe duyet
       - Kiem tra LopHocPhan duoc tao tu dong (so luong HP x HK)
       - MaGiangVien = null ban dau

### Quan ly Lop Hoc Phan [BCN, GV, CNHP]
- [ ] LopHocPhanService interface + impl
- [ ] LopHocPhanController
       + GET  /lop-hoc-phan (filter theo HK, HP)
       + GET  /lop-hoc-phan/{maLopHP}
       + POST /lop-hoc-phan/{maLopHP}/gan-gv
- [ ] templates/lophocphan/list.html
- [ ] templates/lophocphan/detail.html
- [ ] TEST:
       - BCN gan GV cho lop HP
       - GV co trong doi ngu -> khong warn
       - GV khong trong doi ngu -> hien canh bao nhung van cho gan

---

## PHASE 4: MODULE P2 - TAI LIEU & DANH GIA

### Tai lieu Mon Hoc [GV, CNHP]
- [ ] TaiLieuMonHocService interface + impl
       + nopTaiLieu() - UPDATE neu da co, INSERT neu chua
       + isQuaHanDeCuong()
- [ ] TaiLieuController
- [ ] templates/tailieu/list.html (theo lop HP)
- [ ] TEST:
       - GV upload DeCuongChiTiet
       - CNHP duyet -> DaDuyet
       - CNHP tu choi -> TuChoi
       - GV upload lai -> UPDATE ban cu (UNIQUE constraint)
       - Kiem tra deadline (> 14 ngay phai hien canh bao)

### Danh Gia & Canh Bao [GV, CVHT]
- [ ] DanhGiaVaCanhBaoService interface + impl
       + taoNhanXet() - co side effect gui email neu TieuCuc
- [ ] EmailService impl (Spring Mail hoac mock trong dev)
- [ ] DanhGiaController
- [ ] templates/danhgia/list.html (theo lop HP)
- [ ] templates/danhgia/canhbao.html (CVHT xu ly)
- [ ] TEST:
       - GV nhap nhan xet TichCuc -> chi luu, khong gui email
       - GV nhap nhan xet TieuCuc -> luu + gui email (check log)
       - CVHT xu ly canh bao -> DaXuLy=1
       - Badge count tren dashboard (so canh bao chua xu ly)

---

## PHASE 5: MODULE P2 - KIEN TAP & THUC TAP

### Kien Tap [BCN, GV, TTDTXS, DN]
- [ ] KienTapService interface + impl
- [ ] KienTapController
- [ ] templates/kientap/list.html
- [ ] templates/kientap/form.html
- [ ] templates/kientap/detail.html
- [ ] TEST:
       - BCN tao dot kien tap -> TTDTXS duyet
       - GV nhap nhan xet GV
       - DN login -> nhap nhan xet DN
       - Danh sach SV = lay tu LopHanhChinh

### Thuc Tap [PDT, TTDTXS, GV, SV, DN]
- [ ] ThucTapService interface + impl
       + importPhanCongFromExcel() - xu ly trung (skip + bao loi)
- [ ] ThucTapController
- [ ] templates/thuctap/list.html (dot thuc tap)
- [ ] templates/thuctap/detail.html (danh sach phan cong)
- [ ] templates/thuctap/phan-cong-form.html
- [ ] TEST:
       - Import Excel phan cong (ca truong hop trung + hop le)
       - DN nhap diem + nhan xet
       - GV nhap diem + nhan xet
       - SV nhap nhan xet cam nhan
       - Check UNIQUE constraint (1 SV chi 1 lan/dot)

---

## PHASE 6: BAO CAO & DASHBOARD

- [ ] BaoCaoService interface + impl
- [ ] BaoCaoController
- [ ] templates/baocao/tongquan.html (charts, so lieu)
- [ ] Xuat Excel (Apache POI):
       - Danh sach nguoi dung
       - Danh sach lop HP + trang thai tai lieu
       - Bao cao thuc tap + diem so
- [ ] TEST: Tai file Excel va kiem tra data

---

## KIEM TRA CUOI CUNG TRUOC KHI DEMO

- [ ] Test login toan bo 8 role
- [ ] Test phan quyen: Role X khong truy cap duoc URL cua Role Y
- [ ] Test toan bo workflow: CTDT, HP, TaiLieu, KienTap, ThucTap
- [ ] Test import Excel: nguoi dung + phan cong thuc tap
- [ ] Test auto-create LopHocPhan sau khi duyet CTDT
- [ ] Test email canh bao (hoac check log)
- [ ] Test UNIQUE constraint: TaiLieu, PhanCongThucTap
- [ ] Test deadline DeCuong (qua 14 ngay)
- [ ] Kiem tra encoding tieng Viet trong DB va UI
- [ ] Test upload file (PDF, DOCX, XLSX)
- [ ] Kiem tra responsive (neu co yeu cau)
