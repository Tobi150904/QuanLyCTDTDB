# 04_DEV_CHECKLIST — He Thong Quan Ly Dao Tao Xuat Sac

> Checklist nay phai nhat quan voi 01_ERD_SCHEMA.md (20 bang), 02_Data.md va 03_WORKFLOW.md.
> Tick tung muc khi hoan thanh, KHONG skip phase truoc khi xong phase hien tai.

---

## PHASE 0: SETUP MOI TRUONG (DA XONG)

### Database (MySQL XAMPP)
- [x] Chay `scripts/01_create_tables.sql` -> 20 bang duoc tao thanh cong
- [x] Chay `scripts/02_seed_data.sql` -> du lieu mau sac thuc te
- [x] Kiem tra 20 bang ton tai: `SHOW TABLES IN QuanLyCTDTDB;`
- [x] Kiem tra FK dung: `SELECT * FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA='QuanLyCTDTDB';`
- [x] Kiem tra encoding: `SHOW CREATE DATABASE QuanLyCTDTDB;` -> utf8mb4_unicode_ci

### Spring Boot Project
- [x] Chay `./mvnw spring-boot:run` -> khong co compile error
- [x] Truy cap `localhost:8080` -> redirect den `/login`
- [x] Kiem tra `application.properties`: DB URL, username, password dung XAMPP
- [x] `spring.jpa.hibernate.ddl-auto=validate` -> Hibernate validate schema khop voi entity
- [x] Thu muc `uploads/` duoc tao sau khi khoi dong (hoac tao tay)

---

## PHASE 1: FOUNDATION (KHONG CODE BUSINESS TRUOC KHI XONG)

### Enums (10 enum types — khop voi 01_create_tables.sql)
- [x] `LoaiNguoiDung.java`  -> Admin, GiangVien, SinhVien, DoanhNghiep
- [x] `VaiTro.java`          -> PDT, TTDTXS, CVHT, CNHP
- [x] `TrangThaiHocKy.java` -> SapDienRa, DangDienRa, DaKetThuc
- [x] `TrangThaiCTDT.java`  -> BanNhap, ChoDuyet, DaDuyet, DaHuy
- [x] `ChucDanhBCN.java`    -> ChuNhiem, ThuKy, UyVien
- [x] `LoaiHocPhan.java`    -> LyThuyet, ThucHanh, DoAn, ThucTap, KienTap
- [x] `TrangThaiHocPhan.java` -> BanNhap, ChoDuyet, DaDuyet
- [x] `LoaiGiangVien.java`  -> GiangVienTruong, DoanhNghiep
- [x] `TrangThaiDoanhNghiep.java` -> DangHopTac, TamNgung
- [x] `TrangThaiLopHocPhan.java`  -> DangMo, DaDong, DaHuy
- [x] `TrangThaiSinhVien.java`    -> DangHoc, BaoLuu, ThoiHoc, TotNghiep
- [x] `TrangThaiDotKT.java`       -> ChuanBi, ChoDuyet, DaDuyet, DaThucHien, DaHuy
- [x] `TrangThaiDotTT.java`       -> ChuanBi, ChoDuyet, DaDuyet, DangThucHien, DaKetThuc
- [x] `TrangThaiThucTap.java`     -> DaPhanCong, DangThucTap, DaKetThuc, DaHuy
- [x] `LoaiThucTap.java`          -> Truong, DoanhNghiep

### Entities (20 bang -> toi thieu 20 entity/id class)

#### Simple PK entities
- [x] `HocKyNamHoc.java`
- [x] `NguoiDung.java`
- [x] `SinhVien.java`
- [x] `GiangVien.java`
- [x] `DoanhNghiep.java`
- [x] `LopHanhChinh.java`
- [x] `ChuongTrinhDaoTao.java`
- [x] `HocPhan.java`
- [x] `VaiTroThucTap.java`
- [x] `DotKienTap.java`
- [x] `DotThucTap.java`

#### Composite PK entities (can EmbeddedId class)
- [x] `NhomNguoiDungId.java` + `NhomNguoiDung.java`
- [x] `BcnThanhVienId.java` + `BcnThanhVien.java`
- [x] `DoiNguGiangVienHpId.java` + `DoiNguGiangVienHp.java`
- [x] `CtdtHocPhanId.java` + `CtdtHocPhan.java`
- [x] `LopHocPhanId.java` + `LopHocPhan.java`
- [x] `DanhSachSvLopHocPhanId.java` + `DanhSachSvLopHocPhan.java`
- [x] `DanhSachSvKienTapId.java` + `DanhSachSvKienTap.java`
- [x] `DanhSachThucTap.java` (@Id MaThucTap AUTO)
- [x] `KetQuaThucTap.java`   (@Id MaKetQua  AUTO)

#### BaseAuditEntity (khong hien thuc — bo qua, created_at/updated_at dat truc tiep o tung entity)
- [ ] `BaseAuditEntity.java`      - chua can thiet, deferred

> Ten file thuc te dung CamelCase nhe (Bcn thay vi BCN, Ctdt thay vi CTDT, Sv thay vi SV)
> de phu hop convention Java class naming. Ten BANG MySQL van giu nguyen dung
> `BCN_ThanhVien`, `CTDT_HocPhan` qua `@Table(name="...")`.

### Repositories (1 interface moi entity co nghiep vu) — DA XONG 20/20
- [x] `HocKyNamHocRepository`, `NguoiDungRepository` (+ `@EntityGraph` cho nhomNguoiDungs)
- [x] `SinhVienRepository`, `GiangVienRepository`, `NhomNguoiDungRepository`
- [x] `DoanhNghiepRepository`, `LopHanhChinhRepository`
- [x] `ChuongTrinhDaoTaoRepository`, `BcnThanhVienRepository`
- [x] `HocPhanRepository`, `DoiNguGiangVienHpRepository`, `CtdtHocPhanRepository`
- [x] `LopHocPhanRepository`, `DanhSachSvLopHocPhanRepository`
- [x] `DotKienTapRepository`, `DanhSachSvKienTapRepository`
- [x] `DotThucTapRepository`, `DanhSachThucTapRepository`
- [x] `VaiTroThucTapRepository`, `KetQuaThucTapRepository`

### Security
- [x] `CustomUserDetails.java`    - wrap NguoiDung, tra roles cho Spring Security
- [x] `UserDetailsServiceImpl.java` - loadUserByUsername (tim theo TenDangNhap)
  - Map LoaiNguoiDung -> ROLE_ADMIN, ROLE_GIANG_VIEN, ROLE_SINH_VIEN, ROLE_DOANH_NGHIEP
  - Map VaiTro trong NhomNguoiDung -> ROLE_PDT, ROLE_TTDTXS, ROLE_CVHT, ROLE_CNHP
  - Kiem tra TrangThaiTK = 1
- [x] `SecurityConfig.java`
  - [x] BCryptPasswordEncoder bean
  - [x] DaoAuthenticationProvider qua `AuthenticationManagerBuilder` (khong tao `CustomAuthenticationProvider` rieng)
  - [x] Cau hinh permitAll: /login, /css/**, /js/**, /webjars/**, /uploads/**
  - [ ] Cau hinh hasRole theo URL pattern — xem `02_Mô Tả & Thiết kế dữ liệu.md` § 7 (Phase 2.3)
  - [x] formLogin: /login, defaultSuccessUrl: /dashboard, failureUrl: /login?error
  - [x] logout: /logout -> /login?logout
  - [ ] sessionManagement: maximumSessions(1) — deferred

### Config
- [x] `WebMvcConfig.java` - addResourceHandlers cho /uploads/** -> file.upload-dir
- [ ] Tao thu muc `src/main/resources/static/uploads/.gitkeep` — deferred, dung `uploads/` o root

---

## PHASE 2: MODULE P0 — CORE

### Auth + Dashboard
- [x] `AuthController.java`               - GET /login, GET /
- [x] `DashboardController.java`          - GET /dashboard (thong ke theo role)
- [x] `ProfileController.java`            - GET /profile (khong co trong checklist goc, da them)
- [x] `templates/auth/login.html`
- [x] `templates/dashboard/dashboard.html`
- [x] `templates/layout/base.html`         - sidebar theo role + flash message + navbar
- [x] `templates/profile/profile.html`
- [x] `templates/error/{403,404,500}.html`
- [x] TEST: Dang nhap admin, tran.van.an, le.thi.bich -> redirect /dashboard OK

### Quan ly Nguoi Dung [PDT, TTDTXS] — DA XONG
- [x] `NguoiDungDTO.java`, `NguoiDungExcelDTO.java`
- [x] `NguoiDungService.java` + `NguoiDungServiceImpl.java`
  - [x] search(Pageable, keyword, loai), findById, create, update, toggleStatus
  - [x] createWithRole (tao NguoiDung + NhomNguoiDung + SinhVien/GiangVien)
  - [x] importFromExcel(MultipartFile)
  - [x] findByIdWithRoles (them 2025-Q2 batch 2 — fix LazyInit)
- [x] `NguoiDungController.java`
  - [x] GET  /nguoi-dung                  - list + filter + search
  - [x] GET  /nguoi-dung/them             - form tao moi
  - [x] POST /nguoi-dung/them
  - [x] GET  /nguoi-dung/sua/{ma}
  - [x] POST /nguoi-dung/sua/{ma}
  - [x] POST /nguoi-dung/doi-trang-thai/{ma}
  - [x] POST /nguoi-dung/import           - Excel import
  - [x] GET  /nguoi-dung/chi-tiet/{ma}
- [x] `templates/nguoi-dung/danh-sach.html`, `form.html`, `chi-tiet.html`, `import.html`
  > Template path dung kebab-case tieng Viet khong dau (thay vi `nguoidung/list.html`)
- [x] `ExcelImportUtil.java`, `FileStorageUtil.java`
- [x] TEST: Import Excel (hop le + loi trung TenDangNhap), list hien thi day du vai tro

### Quan ly Doanh Nghiep [PDT, TTDTXS] — DA XONG (Phase 2.2)
- [x] Repository va Entity da co (Phase 1)
- [x] `DoanhNghiepDTO.java` (validate @NotBlank, @Email, @Pattern phone)
- [x] `DoanhNghiepService.java` + `DoanhNghiepServiceImpl.java`
  - [x] search(Pageable, keyword, trangThai)
  - [x] CRUD (create voi sinh ma tu dong neu bo trong)
  - [x] toggleTrangThai (DangHopTac <-> TamNgung)
  - [x] delete voi guard: chan khi con DotKienTap / DanhSachThucTap tham chieu
  - [x] sinhMaDoanhNghiep (DN001, DN002, ...)
  - [x] getThongKe
- [x] `DoanhNghiepController.java`
  - [x] GET  /doanh-nghiep                  - list + filter + search + thongKe
  - [x] GET  /doanh-nghiep/them              - form tao
  - [x] POST /doanh-nghiep/them
  - [x] GET  /doanh-nghiep/sua/{ma}
  - [x] POST /doanh-nghiep/sua/{ma}
  - [x] POST /doanh-nghiep/doi-trang-thai/{ma}
  - [x] POST /doanh-nghiep/xoa/{ma}         - kem guard khi con tham chieu
  - [x] GET  /doanh-nghiep/chi-tiet/{ma}
- [x] `templates/doanh-nghiep/danh-sach.html`, `form.html`, `chi-tiet.html`
- [x] Them menu sidebar trong `layout/base.html`
- [x] Them URL rule `hasAnyRole('PDT','TTDTXS','ADMIN')` trong `SecurityConfig.java`
- [x] TEST: Tao DN moi, doi trang thai, xem list voi filter

### UI Refactor Phase 1+2 (2026-Q2) — DA XONG
- [x] Refactor `static/css/main.css` theo design system v2 (shadows, transitions, focus ring, stat-card variant)
- [x] Refactor `layout/base.html` navbar gradient + active sidebar accent bar
- [x] Refactor `auth/login.html` split-panel brand + form layout
- [x] Refactor `dashboard/dashboard.html` — bo inline style, group stat theo nghiep vu
- [x] Refactor detail pages (Nguoi Dung + Doanh Nghiep) dung `.info-row` pattern
- [x] Fix bug nghiem trong: `nguoi-dung/form.html` truong `loaiNguoiDung` bi disabled khi edit
      lam form khong submit duoc gia tri (gay `@NotNull` validation) — thay bang readonly text + hidden input
- [x] Fix `NguoiDungController.suaForm`: populate hocHam/hocVi/chuyenNganh (GV) va maLopHC (SV)
- [x] Fix `NguoiDungServiceImpl.update`: ho tro doi maLopHC cho SinhVien, khong ghi de null cho GV
- [x] Bo sung `activeMenu="nguoi-dung"` trong moi handler cua `NguoiDungController`

---

## PHASE 3: MODULE P1 — NGHIEP VU CHINH  (DANG THUC HIEN)

### Tien do Phase 3 (cap nhat 2026-Q2)
- [x] **Fix compile error** `HocKyNamHocController`: loai bo enum sai `ChuanBi`,
      bo sung derived getter `getHocKyThu/getNamBatDau/getNamKetThuc` tren entity,
      them field tuong ung vao DTO, tu sinh `MaHocKy` + `TenHocKy` o service.
- [x] **Fix LazyInitializationException** 4 endpoint (`hoc-phan/form`, `ctdt/*`,
      `lop-hanh-chinh/*`, `lop-hoc-phan/*`): them JOIN FETCH o tat ca repository
      chinh + doi `giangVienRepo.findAll()` -> `findAllFetchNguoiDung()` trong
      cac controller dropdown GV.
- [x] **Tao templates lop-hoc-phan** (chua co) gom `danh-sach.html` + `chi-tiet.html`
      + truyen `hocPhanMap` tu controller de hien thi tenHocPhan cho EmbeddedId LHP.
- [x] **Viet hoa co dau toan bo UI** 4 module (`ctdt`, `hoc-phan`, `lop-hanh-chinh`,
      `lop-hoc-phan`) + module `nguoi-dung` + layout `base.html` (menu sidebar,
      navbar, modal). Map enum `LoaiHocPhan`, `LoaiNguoiDung`, `VaiTro` sang nhan
      tieng Viet truc tiep trong Thymeleaf.
- [x] **Fix nut "Chinh Sua" nguoi dung**: doi tu icon-only sang nut co label
      "Sua" + `bi-pencil-square`, mo rong cot Thao Tac `110px -> 170px`.
- [x] **Fix field sai o `nguoi-dung/form.html`**: `lhc.tenLopHC` -> `lhc.tenLop`
      (khop entity `LopHanhChinh`).
- [x] **Refresh seed v2** `scripts/02_seed_data.sql`:
      - Doi format `MaSV` tu `SV001` sang `SV2024001` theo docs/02 §1.
      - Them `CREATE DATABASE IF NOT EXISTS` vao `01_create_tables.sql` (khop README).
      - Mo rong thanh 18 NguoiDung, 6 GV, 10 SV, 12 LopHocPhan; auto-add all SV
        DangHoc vao DanhSachSinhVienKienTap (docs/02 §3.7).
      - Bao gom 2 ban ghi `DaCanhBao=1` minh hoa 2 trang thai: da xu ly + chua xu ly.
      - Cap nhat `docs/02 §4` dong bo so ban ghi moi.
- [ ] **Controller + Service Phase 3 con lai** (duoi day) — dang trien khai

### Quan ly Hoc Ky Nam Hoc [PDT, TTDTXS]
- [x] `HocKyNamHocService.java` interface + impl
  - CRUD, setTrangThai (chi 1 HK o trang thai DangDienRa)
- [x] `HocKyNamHocController.java` (CRUD + toggle trang thai)
- [x] `templates/hoc-ky/danh-sach.html`, `form.html`

### Quan ly Lop Hanh Chinh [PDT, TTDTXS]
- [ ] `LopHanhChinhService.java` interface + impl
  - CRUD, assignCoVan
- [ ] `LopHanhChinhController.java`
- [ ] `templates/lophanhchinh/list.html`, `form.html`

### Quan ly Hoc Phan [BCN/CNHP, TTDTXS]
- [ ] `HocPhanDTO.java`
- [ ] `HocPhanService.java` interface + `HocPhanServiceImpl.java`
  - CRUD, submitForApproval (BanNhap -> ChoDuyet)
  - approve/reject (ChoDuyet -> DaDuyet/BanNhap)
  - addToDoiNgu, removeFromDoiNgu
- [ ] `HocPhanController.java`
  - GET  /hoc-phan                       - list + filter
  - GET  /hoc-phan/them
  - POST /hoc-phan/them
  - GET  /hoc-phan/{ma}                  - detail + danh sach doi ngu GV
  - GET  /hoc-phan/{ma}/sua
  - POST /hoc-phan/{ma}/sua
  - POST /hoc-phan/{ma}/nop-duyet        - BanNhap -> ChoDuyet
  - POST /hoc-phan/{ma}/phe-duyet        - ChoDuyet -> DaDuyet [TTDTXS]
  - POST /hoc-phan/{ma}/tu-choi          - ChoDuyet -> BanNhap [TTDTXS]
  - POST /hoc-phan/{ma}/them-gv          - them vao DoiNguGiangVienHP
  - POST /hoc-phan/{ma}/xoa-gv/{maGV}   - xoa khoi doi ngu
- [ ] `templates/hocphan/list.html`, `form.html`, `detail.html`
- [ ] TEST: BCN tao -> nop -> TTDTXS duyet -> CNHP quan ly doi ngu

### Quan ly CTDT [BCN, TTDTXS]
- [ ] `ChuongTrinhDaoTaoDTO.java`
- [ ] `CTDT_HocPhanDTO.java`            - mapping chi tiet HP trong CTDT
- [ ] `ChuongTrinhDaoTaoService.java` interface + impl
  - CRUD CTDT
  - addHocPhan, removeHocPhan (quan ly CTDT_HocPhan)
  - submitForApproval, approve (khi duyet: tu dong tao LopHocPhan)
  - autoCreateLopHocPhan(maCTDT, maHocKy)  <- NGHIEP VU QUAN TRONG
- [ ] `ChuongTrinhDaoTaoController.java`
  - GET  /ctdt
  - GET  /ctdt/them
  - POST /ctdt/them
  - GET  /ctdt/{ma}                      - detail + danh sach HP
  - GET  /ctdt/{ma}/sua
  - POST /ctdt/{ma}/sua
  - POST /ctdt/{ma}/them-hoc-phan
  - POST /ctdt/{ma}/xoa-hoc-phan/{maHP}
  - POST /ctdt/{ma}/nop-duyet
  - POST /ctdt/{ma}/phe-duyet            - [TTDTXS] -> trigger autoCreateLopHocPhan
  - POST /ctdt/{ma}/tu-choi              - [TTDTXS]
- [ ] `templates/ctdt/list.html`, `form.html`, `detail.html`
- [ ] TEST: Duyet CTDT -> LopHocPhan duoc tao theo SoLopDuKien, MaGiangVien=NULL

### Quan ly Lop Hoc Phan [BCN, TTDTXS, GV]
- [ ] `LopHocPhanDTO.java`
- [ ] `LopHocPhanService.java` interface + impl
  - getByHocKy, getByGiangVien, getByLopHanhChinh
  - assignGiangVien, removeGiangVien
  - addSinhVien, removeSinhVien
- [ ] `LopHocPhanController.java`
  - GET  /lop-hoc-phan                   - list + filter theo HK, HP, GV
  - GET  /lop-hoc-phan/{ctdt}/{hp}/{hk}/{nhom}  - chi tiet + danh sach SV
  - POST /lop-hoc-phan/{ctdt}/{hp}/{hk}/{nhom}/gan-gv
  - POST /lop-hoc-phan/{ctdt}/{hp}/{hk}/{nhom}/them-sv
  - POST /lop-hoc-phan/{ctdt}/{hp}/{hk}/{nhom}/xoa-sv/{maSV}
- [ ] `templates/lophocphan/list.html`, `detail.html`
- [ ] TEST: Gan GV ngoai doi ngu -> hien canh bao, van cho phep

---

## PHASE 4: MODULE P2 — DANH GIA & CANH BAO

### Nhan Xet Sinh Vien + Canh Bao [GV, CVHT]
- [ ] `DanhSachSVLopHPService.java` interface + impl
  - nhapNhanXet(id, nhanXet, daCanhBao) - neu daCanhBao=1: gui email den CVHT
  - xuLyCanhBao(id, ketQuaXuLy)
  - getSoCanhBaoChuaXuLy(maLopHC)
- [ ] `EmailService.java` interface
- [ ] `MockEmailServiceImpl.java`  - log ra console thay vi gui that (dung trong dev)
- [ ] `DanhGiaController.java`
  - GET  /danh-gia/lop/{ctdt}/{hp}/{hk}/{nhom}   - list SV + nhan xet
  - POST /danh-gia/lop/{ctdt}/{hp}/{hk}/{nhom}/nhan-xet/{maSV}
  - GET  /danh-gia/canh-bao                       - CVHT xem canh bao chua xu ly
  - POST /danh-gia/canh-bao/{id}/xu-ly
- [ ] `templates/danhgia/list.html`, `canhbao.html`
- [ ] TEST: GV nhan xet DaCanhBao=1 -> MockEmailService log email CVHT
- [ ] TEST: CVHT xu ly canh bao -> DaCanhBao van la 1 nhung KetQuaXuLy duoc luu

---

## PHASE 5: MODULE P3 — KIEN TAP & THUC TAP

### Quan ly Kien Tap [BCN, TTDTXS, GV, DN]
- [ ] `DotKienTapDTO.java`
- [ ] `KienTapService.java` interface + impl
  - CRUD DotKienTap, submitForApproval, approve, markDone
  - getSinhVienCuaLop (lay tu LopHanhChinh -> DanhSachSinhVienKienTap)
- [ ] `KienTapController.java`
  - GET  /kien-tap
  - GET  /kien-tap/them
  - POST /kien-tap/them                 - tao dot + add SV tu LopHC
  - GET  /kien-tap/{ma}                 - detail + list SV
  - POST /kien-tap/{ma}/nop-duyet
  - POST /kien-tap/{ma}/phe-duyet       - [TTDTXS]
  - POST /kien-tap/{ma}/hoan-thanh      - DaDuyet -> DaThucHien [BCN/TTDTXS]
  - POST /kien-tap/{ma}/nhan-xet-gv     - [GV]
  - POST /kien-tap/{ma}/nhan-xet-dn     - [DN]
- [ ] `templates/kientap/list.html`, `form.html`, `detail.html`
- [ ] `FileStorageUtil.java`            - luu file upload, kiem tra extension
- [ ] TEST: Tao dot -> duyet -> GV + DN nhap nhan xet rieng

### Quan ly Thuc Tap [PDT, TTDTXS, GV, SV, DN]
- [ ] `DotThucTapDTO.java`
- [ ] `DanhSachThucTapDTO.java`
- [ ] `ThucTapExcelDTO.java`
- [ ] `ThucTapService.java` interface + impl
  - CRUD DotThucTap, submitForApproval, approve
  - importPhanCong(MultipartFile): doc Excel, skip trung (MaDotTT, MaSV), bao loi
  - nhapKetQua(maThucTap, maVaiTro, maNguoiDanhGia, diem, nhanXet)
- [ ] `ThucTapController.java`
  - GET  /thuc-tap
  - GET  /thuc-tap/them
  - POST /thuc-tap/them
  - GET  /thuc-tap/{ma}                 - detail + phan cong list
  - POST /thuc-tap/{ma}/nop-duyet
  - POST /thuc-tap/{ma}/phe-duyet       - [TTDTXS]
  - POST /thuc-tap/{ma}/import-phan-cong - [PDT] Excel import
  - POST /thuc-tap/ket-qua/{maThucTap}  - [DN/GV/CVHT]
  - GET  /thuc-tap/cua-toi              - [SV] xem phan cong cua ban than
- [ ] `templates/thuctap/list.html`, `form.html`, `detail.html`
- [ ] TEST: Import Excel co ban ghi trung -> skip + hien bao cao
- [ ] TEST: DN nhap ket qua, GV nhap ket qua -> 2 ban ghi KetQuaThucTap voi MaVaiTro khac nhau

---

## PHASE 6: BAO CAO & DASHBOARD

- [ ] `BaoCaoController.java`
  - GET  /bao-cao/tong-quan              - thong ke tong hop theo role
  - GET  /bao-cao/nguoi-dung/export      - Excel: danh sach nguoi dung
  - GET  /bao-cao/lop-hoc-phan/export    - Excel: LHP + trang thai tai lieu
  - GET  /bao-cao/thuc-tap/export        - Excel: phan cong + diem
- [ ] `BaoCaoService.java` interface + impl
  - exportNguoiDung, exportLopHocPhan, exportThucTap (tra ve ByteArrayInputStream)
- [ ] `templates/baocao/tongquan.html`   - charts (Chart.js)
- [ ] TEST: Tai 3 file Excel va kiem tra header + du lieu

---

## KIEM TRA CUOI CUNG TRUOC DEMO

### Chuc nang
- [ ] Dang nhap du 8 role (4 LoaiNguoiDung + 4 VaiTro trong NhomNguoiDung)
- [ ] Phan quyen URL: Role X khong truy cap duoc endpoint cua Role Y (nhan 403 hoac redirect)
- [ ] Workflow CTDT hoan chinh: Tao -> Them HP -> Nop -> Duyet -> Auto-tao LopHocPhan
- [ ] Workflow Kien Tap hoan chinh: Tao -> Duyet -> GV+DN nhan xet
- [ ] Workflow Thuc Tap hoan chinh: Tao -> Import Excel -> Duyet -> Nhap ket qua
- [ ] Import Excel: Nguoi dung + Phan cong thuc tap (kiem tra bao loi trung)
- [ ] Export Excel: 3 loai bao cao

### Ky thuat
- [ ] Encoding tieng Viet trong DB, response va file Excel
- [ ] Upload file: PDF, DOCX, XLSX dung len 20MB
- [ ] CSRF token tren moi form POST
- [ ] Confirm dialog truoc khi xoa
- [ ] Pagination tren moi list > 20 ban ghi
- [ ] Empty state khi list trong
- [ ] Flash message hien thi sau redirect
- [ ] Auto-dismiss alert sau 4 giay (main.js)
- [ ] Session timeout 30 phut -> redirect login
- [ ] BCrypt password hash (khong luu plain text)
- [ ] `spring.jpa.open-in-view=false` -> khong co LazyInitializationException
