# 06_PROJECT_SCAFFOLD — Thuc Trang & Gap Analysis

> Phan anh thuc te ma nguon tai 2026-Q2 batch 3 (sau khi hoan thanh
> Phase 3 — Modules Nghiep Vu Chinh + fix LazyInit + them BCN & DoiNguGV).
> Dung doi chieu voi `04_DEV_CHECKLIST.md` de biet chuc nang nao con thieu
> va `07_ROADMAP.md` de biet ke hoach ke tiep.
> Cap nhat file nay moi khi add/remove mot lop nam duoi `src/main/java/com/ntu/quanlyctdtdb/`.

---

## 1. Cau truc package thuc te (cap nhat 2026-Q2 batch 3)

```
com.ntu.quanlyctdtdb/
├── QuanLyCtdtDbApplication.java
├── config/
│   ├── SecurityConfig.java              (Spring Security 6 form-login + URL rules Phase 2+3)
│   └── WebMvcConfig.java                (serve /uploads/**)
├── controller/   (11 controller — 1 module / controller)
│   ├── AuthController.java              /login, /logout
│   ├── DashboardController.java         /dashboard (thong ke theo role)
│   ├── ProfileController.java           /profile (xem + doi mat khau)
│   ├── NguoiDungController.java         /nguoi-dung/**  (Phase 2.1)
│   ├── DoanhNghiepController.java       /doanh-nghiep/**(Phase 2.2)
│   ├── HocKyNamHocController.java       /hoc-ky/**      (Phase 3.1)
│   ├── LopHanhChinhController.java      /lop-hanh-chinh/**(Phase 3.2)
│   ├── HocPhanController.java           /hoc-phan/**    (Phase 3.3 + DoiNguGV endpoint)
│   ├── ChuongTrinhDaoTaoController.java /ctdt/**        (Phase 3.4 + BCN endpoint)
│   ├── LopHocPhanController.java        /lop-hoc-phan/**(Phase 3.5 + HocKyThu filter + soft-check)
│   ├── DotKienTapController.java        /kien-tap/**    (Phase 5.1 — SKELETON, thieu 6 endpoint + template)
│   └── DotThucTapController.java        /thuc-tap/**    (Phase 5.2 — SKELETON, thieu 6 endpoint + template)
├── dto/          (13 DTO)
│   ├── NguoiDungDTO.java, NguoiDungExcelDTO.java
│   ├── DoanhNghiepDTO.java
│   ├── HocKyNamHocDTO.java
│   ├── LopHanhChinhDTO.java
│   ├── HocPhanDTO.java
│   ├── DoiNguGvDTO.java                  [BATCH 3 — them]
│   ├── ChuongTrinhDaoTaoDTO.java, CtdtHocPhanDTO.java
│   ├── BcnThanhVienDTO.java              [BATCH 3 — them]
│   ├── DotKienTapDTO.java
│   └── DotThucTapDTO.java, ThucTapExcelDTO.java
├── entity/    (27 file — 20 entity + 7 @Embeddable Id class)
├── enums/     (15 enum type)
├── exception/
│   ├── BusinessException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java      (@ControllerAdvice)
├── repository/ (20 interface — day du cho 20 bang)
│                 [Batch 3: them findByHocPhanFetch, findByCtdtFetch,
│                  findByIdFetch, findByLopFetch, findByCtdtAndKyFetch]
├── security/
│   ├── CustomUserDetails.java           (wrap NguoiDung)
│   └── UserDetailsServiceImpl.java      (map LoaiNguoiDung + VaiTro -> GrantedAuthority)
├── service/     (10 interface)
│   ├── NguoiDungService, DoanhNghiepService, EmailService
│   ├── HocKyNamHocService, LopHanhChinhService
│   ├── HocPhanService, DoiNguGvService     [BATCH 3 — them DoiNguGvService]
│   ├── ChuongTrinhDaoTaoService, BcnThanhVienService  [BATCH 3 — them BcnThanhVienService]
│   ├── LopHocPhanService
│   └── DotKienTapService, DotThucTapService (skeleton)
├── service/impl/ (10 impl + MockEmailServiceImpl)
└── util/
    ├── ExcelImportUtil.java
    └── FileStorageUtil.java
```

### Templates thuc te (`src/main/resources/templates/`) — cap nhat 2026-Q2 batch 3

```
auth/login.html                                            [DONE Phase 1]
dashboard/dashboard.html                                   [DONE Phase 1]
error/{403,404,500}.html                                   [DONE Phase 1]
layout/base.html                                           [DONE Phase 1+UI refactor]
profile/profile.html                                       [DONE Phase 2]

nguoi-dung/  danh-sach.html, form.html, chi-tiet.html, import.html [DONE Phase 2.1]
doanh-nghiep/danh-sach.html, form.html, chi-tiet.html              [DONE Phase 2.2]
hoc-ky/      danh-sach.html, form.html                             [DONE Phase 3.1]
lop-hanh-chinh/ danh-sach.html, form.html, chi-tiet.html           [DONE Phase 3.2]
hoc-phan/    danh-sach.html, form.html, chi-tiet.html              [DONE Phase 3.3]
   chi-tiet.html bao gom section "Doi Ngu Giang Vien" voi
   modal them GV + nut toggle/xoa [BATCH 3]
ctdt/        danh-sach.html, form.html, chi-tiet.html              [DONE Phase 3.4]
   chi-tiet.html bao gom tab "Ban Chu Nhiem" [BATCH 3]
lop-hoc-phan/danh-sach.html, chi-tiet.html                         [DONE Phase 3.5]
   danh-sach.html bao gom card "Ke Hoach Mo Lop" + modal
   tao-hang-loat voi pre-fill SoLop per-HP [BATCH 3]

kien-tap/    (CHUA TON TAI — Phase 5.1)
thuc-tap/    (CHUA TON TAI — Phase 5.2)
danh-gia/    (CHUA TON TAI — Phase 4)
bao-cao/     (CHUA TON TAI — Phase 6)
```

---

## 2. Ma tran coverage theo module (cap nhat 2026-Q2 batch 3)

| Module            | Entity | Repository | Service | Controller | Templates | Trang thai                     |
|-------------------|--------|------------|---------|------------|-----------|--------------------------------|
| Auth + Dashboard  | -      | -          | -       | DONE       | DONE      | Hoat dong                      |
| Profile           | -      | -          | -       | DONE       | DONE      | Hoat dong                      |
| Nguoi Dung        | DONE   | DONE       | DONE    | DONE       | DONE      | Da fix LazyInit + UI refactor  |
| Doanh Nghiep      | DONE   | DONE       | DONE    | DONE       | DONE      | Hoat dong                      |
| Hoc Ky Nam Hoc    | DONE   | DONE       | DONE    | DONE       | DONE      | Auto-close HK cu (fix B5)      |
| Lop Hanh Chinh    | DONE   | DONE       | DONE    | DONE       | DONE      | Batch 3: fix LazyInit chi-tiet |
| Hoc Phan          | DONE   | DONE       | DONE    | DONE       | DONE      | Batch 3: fix LazyInit chi-tiet |
| Doi Ngu GV        | DONE   | DONE       | DONE    | DONE (piggyback HocPhanController) | DONE (piggyback hoc-phan/chi-tiet) | Phase 3.3 + BATCH 3 |
| CTDT              | DONE   | DONE       | DONE    | DONE       | DONE      | Fix B2/B4/B6, thiet ke manual LHP |
| BCN Thanh Vien    | DONE   | DONE       | DONE    | DONE (piggyback CtdtController) | DONE (piggyback ctdt/chi-tiet) | BATCH 3 — moi them |
| Lop Hoc Phan      | DONE   | DONE       | DONE    | DONE       | DONE      | Batch 3: HocKyThu filter + soft-check + Ke Hoach Mo Lop |
| Danh Gia + Canh Bao| DONE  | DONE       | THIEU   | THIEU      | THIEU     | Gap — Phase 4                  |
| Kien Tap          | DONE   | DONE       | PARTIAL | PARTIAL    | THIEU     | Phase 5.1 — thieu 6 endpoint + 3 template |
| Thuc Tap          | DONE   | DONE       | PARTIAL | PARTIAL    | THIEU     | Phase 5.2 — thieu 6 endpoint + 4 template |
| Bao Cao           | -      | -          | THIEU   | THIEU      | THIEU     | Gap — Phase 6                  |

**Diem can chu y:**
- Toan bo 7 module core + nghiep vu chinh (Nguoi Dung, Doanh Nghiep, HocKy, LopHC,
  HocPhan+DoiNguGV, CTDT+BCN, LopHP) da co: DTO + service + impl + controller +
  templates + URL rules + test dang nhap qua 8 role.
- 2 controller con skeleton (DotKienTap, DotThucTap) SE tra ve
  `TemplateInputException` 500 neu user truy cap — hien thoi chan bang quyen
  hoac ghi chu "dang phat trien" o sidebar.
- Module Danh Gia & Canh Bao: entity + repository san, chi can them
  `DanhSachSVLopHPService` + `DanhGiaController` + 2 template.

---

## 3. Cau hinh hien tai

| Key                                       | Gia tri                              | Ghi chu                              |
|-------------------------------------------|--------------------------------------|--------------------------------------|
| spring-boot-starter-parent                | 3.5.6                                | Da ha tu 4.0.5 (xem § 5)             |
| java.version                              | 17                                   | release + target                     |
| spring.jpa.hibernate.ddl-auto             | validate                             | Chay script SQL de tao schema        |
| spring.jpa.open-in-view                   | false                                | BAT BUOC dung @EntityGraph/JOIN FETCH|
| spring.jpa.properties.hibernate.dialect   | (removed)                            | Hibernate 6 auto-detect              |
| server.port                               | 8080                                 |                                       |
| spring.thymeleaf.cache                    | false                                | dev only                             |
| logging.level.com.ntu.quanlyctdtdb        | DEBUG                                | Da fix typo                          |

---

## 4. Security wiring chi tiet

```
HTTP -> Spring Security Filter Chain (SecurityConfig)
         |
         v
    AuthenticationManager (dung Builder voi UserDetailsServiceImpl + BCryptPasswordEncoder)
         |
         v
    UserDetailsServiceImpl
      -> nguoiDungRepo.findByTenDangNhap(username)   [@EntityGraph fetches nhomNguoiDungs]
      -> Kiem tra TrangThaiTK = 1
      -> Build CustomUserDetails
         authorities = { ROLE_<LoaiNguoiDung>, ROLE_<VaiTro>* }
         vi du: admin -> { ROLE_ADMIN }
                tran.van.an -> { ROLE_GIANG_VIEN, ROLE_PDT, ROLE_TTDTXS }

URL rules (SecurityConfig.filterChain) — cap nhat Phase 3 + batch 4:
    /login, /css/**, /js/**, /webjars/**, /uploads/**        -> permitAll
    /nguoi-dung/**          -> hasAnyRole('PDT','TTDTXS','ADMIN')
    /doanh-nghiep/**        -> hasAnyRole('PDT','TTDTXS','ADMIN')
    /hoc-ky/**              -> hasAnyRole('PDT','TTDTXS','ADMIN')
    /lop-hanh-chinh/**      -> hasAnyRole('PDT','TTDTXS','ADMIN')
    /hoc-phan/**            -> hasAnyRole('PDT','TTDTXS','CNHP','ADMIN',
                                         'GIANG_VIEN','SINH_VIEN')          [BATCH 4]
    /ctdt/**                -> hasAnyRole('PDT','TTDTXS','CNHP','ADMIN',
                                         'GIANG_VIEN','SINH_VIEN')          [BATCH 4]
    /lop-hoc-phan/**        -> hasAnyRole('PDT','TTDTXS','CNHP','ADMIN',
                                         'GIANG_VIEN','SINH_VIEN')          [BATCH 4]
    /kien-tap/**            -> hasAnyRole('PDT','TTDTXS','CNHP','ADMIN',
                                         'GIANG_VIEN','DOANH_NGHIEP','SINH_VIEN')
    /thuc-tap/**            -> hasAnyRole('PDT','TTDTXS','ADMIN','GIANG_VIEN',
                                         'CVHT','DOANH_NGHIEP','SINH_VIEN')
    /danh-gia/**            -> hasAnyRole('GIANG_VIEN','CVHT','SINH_VIEN','ADMIN')
    authenticated mac dinh                                    -> /profile, /dashboard
```

**Van con thieu (defer sang Pre-Prod Hardening):**
- Integration test `@WithMockUser` chung minh 403 khi sai role.
- Fine-grain quyen `@PreAuthorize` cho action xoa + phe duyet.

---

## 5. Thay doi gan day

### 2025-Q2 batch 1 — Login hoat dong tro lai
1. `pom.xml`: Ha `spring-boot-starter-parent` tu `4.0.5` xuong `3.5.6` (thymeleaf-layout-dialect khong tuong thich Groovy 5).
2. `SecurityConfig.java`: Them `import org.springframework.context.annotation.Primary`.
3. `application.properties`: Sua typo `logging.level.com.ntu.quanlyctdt` -> `com.ntu.quanlyctdtdb`.
4. `scripts/01_create_tables.sql`: Viet lai khop voi 20 entity + `02_seed_data.sql`.

### 2025-Q2 batch 2 — Module Nguoi Dung hien thi duoc
1. `NguoiDungRepository`: Them `@EntityGraph("nhomNguoiDungs")` vao `findByTenDangNhap` + `searchNguoiDung`.
2. `NguoiDungService`: Them `findByIdWithRoles(ma)` (tranh LazyInit).
3. `application.properties`: Bo `hibernate.dialect` (auto-detect o Hibernate 6).

### 2026-Q2 batch 1 — UI Refactor + Nguoi Dung edit bug
1. Refactor `static/css/main.css` theo design system v2.
2. Refactor `layout/base.html` navbar + sidebar accent.
3. Refactor `auth/login.html` split-panel.
4. Refactor `dashboard/dashboard.html` bo inline style, group theo role.
5. Refactor detail pages dung `.info-row` / `.info-label`.
6. Fix bug: `nguoi-dung/form.html` disabled field khien submit fail -> thay bang readonly + hidden.
7. `NguoiDungController.suaForm()`: populate them hocHam/hocVi/chuyenNganh (GV) + maLopHC (SV).
8. `NguoiDungServiceImpl.update()`: ho tro doi maLopHC; khong ghi de null cho GV.

### 2026-Q2 batch 2 — Hoan thanh Phase 3 (5 module nghiep vu)
1. Them HocKyNamHocService, LopHanhChinhService, HocPhanService, ChuongTrinhDaoTaoService, LopHocPhanService.
2. Bo sung templates cho 5 module + `viet hoa co dau toan bo UI`.
3. Fix B1-B6 (xem `04_DEV_CHECKLIST.md` § "Fix 2026-Q2 (dot review Phase 3)").
4. Seed data v2: 18 NguoiDung + 10 SV + 12 LopHocPhan + bao phu case DaCanhBao + DaThamGia=0.

### 2026-Q2 batch 3 — Mo rong BCN + DoiNguGV + soft-check + HocKyThu filter
1. **BCN module** (Ban Chu Nhiem CTDT):
   - `BcnThanhVienDTO`, `BcnThanhVienService` + impl.
   - `BcnThanhVienRepository.findByCtdtFetch` (JOIN FETCH GV+NguoiDung).
   - 2 endpoint moi: `/ctdt/chi-tiet/{ma}/bcn/them`, `/ctdt/chi-tiet/{ma}/bcn/xoa`.
   - Tab "Ban Chu Nhiem" trong `ctdt/chi-tiet.html` + modal them.
   - Guard: 1 CTDT chi co duy nhat 1 Chu Nhiem.
2. **DoiNguGV module** (Doi Ngu Giang Vien Hoc Phan):
   - `DoiNguGvDTO`, `DoiNguGvService` + impl.
   - `DoiNguGiangVienHpRepository.findByHocPhanFetch`.
   - 3 endpoint: `/hoc-phan/chi-tiet/{ma}/doi-ngu/{them|toggle|xoa}`.
   - Section "Doi Ngu Giang Vien" trong `hoc-phan/chi-tiet.html`.
   - Guard: KHONG duoc xoa GV la ChuNhiemHP.
3. **Soft-check phan cong GV LopHocPhan**:
   - `LopHocPhanController.phanCong` query `DoiNguGiangVienHpRepository.findById`
     truoc khi gan. GV khong thuoc doi ngu van duoc gan, nhung flash warningMsg.
4. **HocKyThu filter + per-HP SoLop override**:
   - `LopHocPhanServiceImpl.taoLopHocPhanChoCTDT(maCTDT, maHocKy, soLopOverride)`:

### 2026-Q2 batch 4 — HK status auto-derive + file upload @InitBinder + UI RBAC expansion + form error UX
1. **HocKy status machine**:
   - `deriveStatus(ngayBatDau, ngayKetThuc)`: auto-compute TrangThai theo ngay hien tai.
   - `doiTrangThai()`: validate - throw BusinessException neu state khong khop ngay.
   - `update()`: cho phep "revive" HK DaKetThuc neu admin sua ngay sang tuong lai.
   - `resyncStatuses()` call moi lan `findAll()` - tuong thich với đôi khi admin back-date ngay.
   - UI: xoa nut "Kich Hoat" khoi danh-sach (status tu-derive, khong can user action).
2. **File upload binding fix** (`@InitBinder`):
   - `HocPhanController`: disallow `fileDeCuong` field binding -> Spring khong co gang convert `MultipartFile -> String`.
   - `ChuongTrinhDaoTaoController`: disallow `fileWord` field binding.
   - File vẫn được nhận qua `@RequestParam MultipartFile` — unaffected.
3. **Form POST error UX**:
   - **Never redirect on validation/business error** — re-render form, giu input, show `errorMsg`.
   - `#fields.hasErrors('*')` phải nằm **BEN TRONG** `<form th:object>` (binding context requirement).
   - `errorMsg` (non-binding) có thể nằm ngoài form, được render qua `layout/base.html` global block.
   - Controller: Catch exception -> `model.addAttribute("errorMsg", ...)` -> return form template (KHÔNG redirect).
4. **Sidebar RBAC expansion** (sidebar menu chi hien voi role phu hop):
   - Before: GV/SV bi ẩn Học Phần, CTDT, Lớp Học Phần.
   - After: Mở `sec:authorize` cho GV + SV xem (read-only). Writes vẫn bị chặn qua `@PreAuthorize` method-level + inline `sec:authorize` trên button Tạo/Sửa/Xóa.
   - Sidebar section "Đào Tạo" bây giờ hiện cho PDT/TTDTXS/CNHP/ADMIN/GV/SV (trước chỉ 4 role đầu).
5. **Other fixes**:
   - NguoiDung chi-tiet: fix LazyInit `sv.getLopHanhChinh()` -> fetch qua repo.
   - Logout alert dedup: bỏ `param.logout` alert ngoài form login.html (thừa vì backend đã set successMsg).
   - Edit button: đổi từ "Sửa" text sang icon-only `bi-pencil` để đồng bộ với 3 nút hành động khác (Xem/Khóa/Xóa).
---

## 6. Quy uoc scaffold cho cac module con lai

Moi khi them mot module moi, lam theo thu tu:

1. **Doc lai** `00_MASTER_REFERENCE.md` + `01_ERD_SCHEMA.md` + workflow tuong ung.
2. **Entity + Repository** (20/20 da co — chi bo sung method JOIN FETCH khi can).
3. **DTO** — 1 DTO form + 1 DTO Excel (neu co import/export).
4. **Service interface + Impl** trong `service/` va `service/impl/`.
   - Transactional readOnly cho query, transactional cho write.
   - Dung `@EntityGraph` hoac `JOIN FETCH` khi query tra entity co collection LAZY.
5. **Controller** trong `controller/` — 1 controller / module, URL kebab-case tieng Viet khong dau.
   - Method order: list, them (GET+POST), sua (GET+POST), chi-tiet, action state transitions.
6. **Templates** trong `templates/<module>/` — toi thieu 3 file: `danh-sach.html`, `form.html`, `chi-tiet.html`.
   - Dung `<html layout:decorate="~{layout/base}">`.
   - Them menu sidebar trong `layout/base.html` voi `sec:authorize="hasAnyRole(...)"`.
7. **Bo sung URL rule** trong `SecurityConfig.filterChain`.
8. **Test**: dang nhap tung role, kiem tra deny/access, empty state, flash message.
9. **Update docs**: check `04_DEV_CHECKLIST.md`, cap nhat file nay + `07_ROADMAP.md`.

---

## 7. Known issues / tech debt (cap nhat 2026-Q2 batch 3)

| Code   | Mo ta                                                                                | Phase xu ly |
|--------|--------------------------------------------------------------------------------------|-------------|
| ~~TD-01~~ | ~~5 controller thieu template~~ -> **RESOLVED** sau Phase 3 batch 2               | DONE        |
| TD-02  | SecurityConfig co URL rules Phase 2+3 nhung CHUA co integration test 403              | Pre-Prod    |
| TD-03  | Khong co URL rule cho `/bao-cao/**` (chua code module)                                | Phase 6     |
| TD-04  | Chua co script migration (03_*.sql) — khi schema doi can lap                          | On-demand   |
| ~~TD-05~~ | ~~DoanhNghiep/HocKy/LopHanhChinh service chua viet~~ -> **RESOLVED** Phase 3 batch 2 | DONE    |
| TD-06  | XAMPP MySQL 5.5 canh bao HHH000511 — can nang MySQL 8                                 | Truoc prod  |
| TD-07  | File .env / application-prod.properties chua co                                       | Phase 7     |
| TD-08  | Khong co unit test + integration test                                                  | Lien tuc    |
| TD-09  | 2 service Kien Tap + Thuc Tap con SKELETON (thieu auto-add SV, transitions, import Excel) | Phase 5 |
| TD-10  | Danh Gia & Canh Bao chua co service/controller/template                                | Phase 4     |
| TD-11  | Khong co scheduler auto-chuyen TrangThai HocKy (SapDienRa -> DangDienRa -> DaKetThuc)  | On-demand   |
| TD-12  | MockEmailServiceImpl chi log console, chua co SmtpEmailServiceImpl thuc te             | Pre-Prod    |

---

## 8. Hanh dong tiep theo

Xem `docs/07_ROADMAP.md`:
- **Phase 4** (Danh Gia & Canh Bao): `DanhGiaController` + `DanhSachSVLopHPService` + 2 template.
- **Phase 5.1** (Kien Tap): Fix service auto-add SV + 6 endpoint + 3 template.
- **Phase 5.2** (Thuc Tap): Fix service validate LoaiHocPhan + 6 endpoint + 4 template.
- **Phase 6** (Bao Cao): `BaoCaoController` + export Excel + Chart.js dashboard.
- **Phase 7** (Pre-Prod): `application-prod.properties`, SmtpEmailServiceImpl, integration test @WithMockUser.
