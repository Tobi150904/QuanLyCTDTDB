# 04_DEV_CHECKLIST — He Thong Quan Ly Dao Tao Xuat Sac

> Checklist nay phai nhat quan voi 01_ERD_SCHEMA.md (20 bang), 02_Data.md va 03_WORKFLOW.md.
> Tick tung muc khi hoan thanh, KHONG skip phase truoc khi xong phase hien tai.

---

## PHASE 0: SETUP MOI TRUONG

### Database (MySQL XAMPP)
- [ ] Chay `scripts/01_create_tables.sql` -> 20 bang duoc tao thanh cong
- [ ] Chay `scripts/02_seed_data.sql` -> du lieu mau sac thuc te
- [ ] Kiem tra 20 bang ton tai: `SHOW TABLES IN QuanLyCTDTDB;`
- [ ] Kiem tra FK dung: `SELECT * FROM information_schema.KEY_COLUMN_USAGE WHERE TABLE_SCHEMA='QuanLyCTDTDB';`
- [ ] Kiem tra encoding: `SHOW CREATE DATABASE QuanLyCTDTDB;` -> utf8mb4_unicode_ci

### Spring Boot Project
- [ ] Chay `./mvnw spring-boot:run` -> khong co compile error
- [ ] Truy cap `localhost:8080` -> redirect den `/login`
- [ ] Kiem tra `application.properties`: DB URL, username, password dung XAMPP
- [ ] `spring.jpa.hibernate.ddl-auto=validate` -> Hibernate validate schema khop voi entity
- [ ] Thu muc `uploads/` duoc tao sau khi khoi dong (hoac tao tay)

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
- [ ] `HocKyNamHoc.java`          - @Id MaHocKy VARCHAR(20)
- [ ] `NguoiDung.java`            - @Id MaNguoiDung, LoaiNguoiDung enum, TrangThaiTK BIT
- [ ] `SinhVien.java`             - @Id MaSV, @OneToOne NguoiDung, @ManyToOne LopHanhChinh
- [ ] `GiangVien.java`            - @Id MaGV, @OneToOne NguoiDung, LoaiGiangVien enum
- [ ] `DoanhNghiep.java`          - @Id MaDoanhNghiep
- [ ] `LopHanhChinh.java`         - @Id MaLopHC, @ManyToOne CTDT (nullable), @ManyToOne GiangVien (coVan)
- [ ] `ChuongTrinhDaoTao.java`    - @Id MaCTDT, @ManyToOne NguoiTao, @ManyToOne NguoiDuyet
- [ ] `HocPhan.java`              - @Id MaHocPhan, @ManyToOne GiangVien (chuNhiemHP)
- [ ] `VaiTroThucTap.java`        - @Id MaVaiTro
- [ ] `DotKienTap.java`           - @Id MaDotKT (AUTO), @ManyToOne LopHanhChinh, DoanhNghiep, GiangVien
- [ ] `DotThucTap.java`           - @Id MaDotTT (AUTO), @ManyToOne CTDT_HocPhan (composite FK)

#### Composite PK entities (can EmbeddedId class)
- [ ] `NhomNguoiDungId.java`      - @Embeddable: MaNguoiDung, VaiTro
- [ ] `NhomNguoiDung.java`        - @EmbeddedId NhomNguoiDungId, @ManyToOne NguoiDung
- [ ] `BCN_ThanhVienId.java`      - @Embeddable: MaCTDT, MaGV, ChucDanh
- [ ] `BCN_ThanhVien.java`        - @EmbeddedId BCN_ThanhVienId, @ManyToOne CTDT, GiangVien
- [ ] `DoiNguGiangVienHPId.java`  - @Embeddable: MaHocPhan, MaGiangVien
- [ ] `DoiNguGiangVienHP.java`    - @EmbeddedId DoiNguGiangVienHPId
- [ ] `CTDT_HocPhanId.java`       - @Embeddable: MaCTDT, MaHocPhan
- [ ] `CTDT_HocPhan.java`         - @EmbeddedId CTDT_HocPhanId, @ManyToOne CTDT, HocPhan
- [ ] `LopHocPhanId.java`         - @Embeddable: MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan
- [ ] `LopHocPhan.java`           - @EmbeddedId LopHocPhanId, @ManyToOne GiangVien (nullable)
- [ ] `DanhSachSVLopHPId.java`    - @Embeddable: MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan
- [ ] `DanhSachSinhVienLopHocPhan.java` - @EmbeddedId, @ManyToOne SinhVien, LopHocPhan
- [ ] `DanhSachSinhVienKienTapId.java`  - @Embeddable: MaDotKT, MaSV
- [ ] `DanhSachSinhVienKienTap.java`    - @EmbeddedId
- [ ] `DanhSachThucTap.java`      - @Id MaThucTap (AUTO), @ManyToOne DotThucTap, SinhVien
- [ ] `KetQuaThucTap.java`        - @Id MaKetQua (AUTO), @ManyToOne DanhSachThucTap, VaiTroThucTap

#### BaseAuditEntity (nen co)
- [ ] `BaseAuditEntity.java`      - created_at, updated_at (@CreationTimestamp, @UpdateTimestamp)

### Repositories (1 interface moi entity co nghiep vu)
- [ ] `HocKyNamHocRepository`     - findByTrangThai, findFirstByTrangThaiOrderByNgayBatDauAsc
- [ ] `NguoiDungRepository`       - findByTenDangNhap, findByEmail, findByLoaiNguoiDung
- [ ] `SinhVienRepository`        - findByMaLopHC, findByNguoiDung_MaNguoiDung
- [ ] `GiangVienRepository`       - findByNguoiDung_MaNguoiDung
- [ ] `NhomNguoiDungRepository`   - findById_MaNguoiDung, deleteById_MaNguoiDungAndId_VaiTro
- [ ] `DoanhNghiepRepository`     - findByTrangThai, findByTenDoanhNghiepContaining(Pageable)
- [ ] `LopHanhChinhRepository`    - findByMaCoVan, findByMaCTDT
- [ ] `ChuongTrinhDaoTaoRepository` - findByTrangThai, findByNguoiTao_MaNguoiDung
- [ ] `BCN_ThanhVienRepository`   - findById_MaCTDT, existsById_MaCTDTAndId_MaGV
- [ ] `HocPhanRepository`         - findByTrangThai, findByChuNhiemHP_MaGV
- [ ] `DoiNguGiangVienHPRepository` - findById_MaHocPhan, existsById_MaHocPhanAndId_MaGiangVien
- [ ] `CTDT_HocPhanRepository`    - findById_MaCTDT, existsById_MaCTDTAndId_MaHocPhan
- [ ] `LopHocPhanRepository`      - findById_MaCTDTAndId_MaHocPhan, findByMaGiangVienIsNull, findByMaGiangVien
- [ ] `DanhSachSVLopHPRepository` - findById_MaCTDTAndId_MaHocPhanAndId_MaHocKyAndId_MaLopHocPhan
- [ ] `DotKienTapRepository`      - findByMaLopHC, findByMaHocKy_MaHocKy, findByTrangThai
- [ ] `DanhSachSVKienTapRepository` - findById_MaDotKT, existsById
- [ ] `DotThucTapRepository`      - findByMaHocKy_MaHocKy, findByTrangThai
- [ ] `DanhSachThucTapRepository` - findByMaDotTT_MaDotTT, findByMaSV_MaSV, existsByMaDotTT_MaDotTTAndMaSV_MaSV
- [ ] `VaiTroThucTapRepository`   - findAll (dung cho select option)
- [ ] `KetQuaThucTapRepository`   - findByMaThucTap_MaThucTap

### Security
- [ ] `CustomUserDetails.java`    - implement UserDetails, wrap NguoiDung
- [ ] `UserDetailsServiceImpl.java` - loadUserByUsername (tim theo TenDangNhap hoac Email)
  - Map LoaiNguoiDung -> ROLE_ADMIN, ROLE_GIANG_VIEN, ROLE_SINH_VIEN, ROLE_DOANH_NGHIEP
  - Map VaiTro trong NhomNguoiDung -> ROLE_PDT, ROLE_TTDTXS, ROLE_CVHT, ROLE_CNHP
  - Kiem tra TrangThaiTK = 1
- [ ] `CustomAuthenticationProvider.java` - dung BCrypt verify password
- [ ] `SecurityConfig.java`
  - BCryptPasswordEncoder bean
  - Cau hinh permitAll: /login, /css/**, /js/**, /webjars/**
  - Cau hinh hasRole: /nguoi-dung/** (PDT), /doanh-nghiep/** (PDT), /hoc-phan/** (CNHP, BCN)...
  - formLogin: /login, defaultSuccessUrl: /dashboard, failureUrl: /login?error
  - logout: /logout -> /login?logout
  - sessionManagement: maximumSessions(1)

### Config
- [ ] `WebMvcConfig.java` - addResourceHandlers cho /uploads/** -> file.upload-dir
- [ ] Tao thu muc `src/main/resources/static/uploads/.gitkeep`

---

## PHASE 2: MODULE P0 — CORE

### Auth + Dashboard
- [ ] `AuthController.java`
  - GET /login -> templates/auth/login.html
  - GET / -> redirect /dashboard
- [ ] `DashboardController.java`
  - GET /dashboard -> lay thong ke theo role, truyen model
  - activeMenu = "dashboard"
- [ ] `templates/auth/login.html`         - form login, khong extend base
- [ ] `templates/dashboard/dashboard.html` - thong ke tong quan theo role
- [ ] `templates/layout/base.html`         - sidebar theo role + flash message + navbar
- [ ] TEST: Dang nhap 4 LoaiNguoiDung + 4 VaiTro trong NhomNguoiDung

### Quan ly Nguoi Dung [PDT, TTDTXS]
- [ ] `NguoiDungDTO.java`               - form binding (tao/sua GV, SV, Admin)
- [ ] `NguoiDungExcelDTO.java`          - Excel import mapping
- [ ] `NguoiDungService.java` interface
- [ ] `NguoiDungServiceImpl.java`
  - getAll(Pageable, filter), getById, create, update, toggleStatus
  - createWithRole (tao NguoiDung + NhomNguoiDung + SinhVien/GiangVien)
  - importFromExcel(MultipartFile)
- [ ] `NguoiDungController.java`
  - GET  /nguoi-dung                  - list + filter + search
  - GET  /nguoi-dung/them             - form tao moi
  - POST /nguoi-dung/them
  - GET  /nguoi-dung/{ma}/sua
  - POST /nguoi-dung/{ma}/sua
  - POST /nguoi-dung/{ma}/khoa        - toggle TrangThaiTK
  - POST /nguoi-dung/import           - Excel import
- [ ] `templates/nguoidung/list.html`
- [ ] `templates/nguoidung/form.html`
- [ ] `ExcelImportUtil.java`            - doc .xlsx, map sang NguoiDungExcelDTO
- [ ] TEST: Import Excel (hop le + loi trung TenDangNhap)

### Quan ly Doanh Nghiep [PDT, TTDTXS]
- [ ] `DoanhNghiepDTO.java`
- [ ] `DoanhNghiepService.java` interface + `DoanhNghiepServiceImpl.java`
  - CRUD, toggleTrangThai
- [ ] `DoanhNghiepController.java`
  - GET  /doanh-nghiep
  - GET  /doanh-nghiep/them
  - POST /doanh-nghiep/them
  - GET  /doanh-nghiep/{ma}/sua
  - POST /doanh-nghiep/{ma}/sua
  - POST /doanh-nghiep/{ma}/doi-trang-thai
- [ ] `templates/doanhnghiep/list.html`
- [ ] `templates/doanhnghiep/form.html`
- [ ] TEST: Tao DN moi, doi trang thai, xem list voi filter

---

## PHASE 3: MODULE P1 — NGHIEP VU CHINH

### Quan ly Hoc Ky Nam Hoc [PDT, TTDTXS]
- [ ] `HocKyNamHocService.java` interface + impl
  - CRUD, setTrangThai (chi 1 HK o trang thai DangDienRa)
- [ ] `HocKyNamHocController.java` (CRUD + toggle trang thai)
- [ ] `templates/hocky/list.html`, `form.html`

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
