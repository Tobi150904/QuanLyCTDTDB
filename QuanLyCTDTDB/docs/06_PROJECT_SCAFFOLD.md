# 06_PROJECT_SCAFFOLD — Thuc Trang & Gap Analysis

> Phan anh thuc te ma nguon tai thoi diem hien tai (cuoi Phase 1, dau Phase 2).
> Dung doi chieu voi `04_DEV_CHECKLIST.md` de biet chuc nang nao da code, chuc nang nao con thieu.
> Cap nhat file nay moi khi add/remove mot lop nam duoi `src/main/java/com/ntu/quanlyctdtdb/`.

---

## 1. Cau truc package thuc te

```
com.ntu.quanlyctdtdb/
├── QuanLyCtdtDbApplication.java
├── config/
│   ├── SecurityConfig.java              (Spring Security 6 form-login)
│   └── WebMvcConfig.java                (serve /uploads/**)
├── controller/
│   ├── AuthController.java              /login, /logout
│   ├── DashboardController.java         /dashboard (thong ke theo role)
│   ├── NguoiDungController.java         /nguoi-dung/** (DONE — Phase 2)
│   ├── ProfileController.java           /profile (DONE)
│   ├── HocPhanController.java           /hoc-phan/** (Controller DONE, thieu templates)
│   ├── ChuongTrinhDaoTaoController.java /ctdt/**     (Controller DONE, thieu templates)
│   ├── LopHocPhanController.java        /lop-hoc-phan/** (Controller DONE, thieu templates)
│   ├── DotKienTapController.java        /kien-tap/** (Controller SKELETON, thieu 6 endpoint + templates)
│   └── DotThucTapController.java        /thuc-tap/** (Controller SKELETON, thieu 6 endpoint + templates)
├── dto/
│   ├── NguoiDungDTO.java, NguoiDungExcelDTO.java
│   ├── HocPhanDTO.java
│   ├── ChuongTrinhDaoTaoDTO.java, CtdtHocPhanDTO.java
│   ├── DotKienTapDTO.java, DotThucTapDTO.java, ThucTapExcelDTO.java
├── entity/    (27 file — 20 entity + 7 @Embeddable Id class)
├── enums/     (15 enum type)
├── exception/
│   ├── BusinessException.java
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java      (@ControllerAdvice + @ResponseBody cho REST API, view cho MVC)
├── repository/ (20 interface — day du cho 20 bang)
├── security/
│   ├── CustomUserDetails.java           (wrap NguoiDung)
│   └── UserDetailsServiceImpl.java      (map LoaiNguoiDung + VaiTro -> GrantedAuthority)
├── service/    (8 interface, bao gom EmailService)
├── service/impl/ (7 impl + MockEmailServiceImpl)
└── util/
    ├── ExcelImportUtil.java
    └── FileStorageUtil.java
```

### Templates thuc te (`src/main/resources/templates/`)

```
auth/login.html              (DONE)
dashboard/dashboard.html     (DONE)
error/{403,404,500}.html     (DONE)
layout/base.html             (DONE — layout:decorate fragment)
nguoi-dung/
  ├── danh-sach.html         (DONE)
  ├── chi-tiet.html          (DONE)
  ├── form.html              (DONE — them/sua chung 1 form)
  └── import.html            (DONE — Excel import)
profile/profile.html         (DONE)
```

---

## 2. Ma tran coverage theo module

| Module            | Entity | Repository | Service | Controller | Templates | Trang thai        |
|-------------------|--------|------------|---------|------------|-----------|-------------------|
| Auth + Dashboard  | -      | -          | -       | DONE       | DONE      | Hoat dong         |
| Nguoi Dung        | DONE   | DONE       | DONE    | DONE       | DONE      | Da fix LazyInit   |
| Profile           | -      | -          | -       | DONE       | DONE      | Hoat dong         |
| Doanh Nghiep      | DONE   | DONE       | DONE    | DONE       | DONE      | Hoat dong         |
| Hoc Ky Nam Hoc    | DONE   | DONE       | THIEU   | THIEU      | THIEU     | Gap — Phase 3     |
| Lop Hanh Chinh    | DONE   | DONE       | THIEU   | THIEU      | THIEU     | Gap — Phase 3     |
| Hoc Phan          | DONE   | DONE       | DONE    | DONE       | THIEU     | Controller 500    |
| CTDT              | DONE   | DONE       | DONE    | DONE       | THIEU     | Controller 500    |
| Lop Hoc Phan      | DONE   | DONE       | DONE    | DONE       | THIEU     | Controller 500    |
| Danh Gia + Canh Bao| DONE  | DONE       | THIEU   | THIEU      | THIEU     | Gap — Phase 4     |
| Kien Tap          | DONE   | DONE       | PARTIAL | PARTIAL    | THIEU     | Thieu endpoint hoan-thanh/huy/nhan-xet/toggle/dong-bo + template |
| Thuc Tap          | DONE   | DONE       | PARTIAL | PARTIAL    | THIEU     | Thieu endpoint bat-dau/ket-thuc/huy/import/ket-qua + template |
| Bao Cao           | -      | -          | THIEU   | THIEU      | THIEU     | Gap — Phase 6     |

**Diem can chu y:**
- 5 controller da viet san (HocPhan, CTDT, LopHocPhan, DotKienTap, DotThucTap) se tra ve `TemplateInputException` khi truy cap vi thieu file HTML tuong ung. User se thay trang 500 hoac white-label error page.
- 3 module (HocKyNamHoc, LopHanhChinh, DanhGia) moi co Repository + Entity, chua co Service/Controller/Template.
- DotKienTap/DotThucTap service+controller moi la SKELETON:
  - Thieu logic Auto-add SV khi tao DotKienTap (quy tac Hybrid WF-07.1 BUOC 2).
  - Thieu endpoint transitions: `/hoan-thanh`, `/huy` (kien-tap); `/bat-dau`, `/ket-thuc`, `/huy` (thuc-tap).
  - Thieu endpoint toggle `DaThamGia`, dong bo danh sach, nhan xet GV/DN, import Excel phan cong, upsert ket qua.
  - Chua set `NguoiTao`, `NguoiDuyet`, `NgayDuyet` -> insert duoi NOT NULL se crash.

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

URL rules (SecurityConfig.filterChain):
    /login, /css/**, /js/**, /webjars/**, /uploads/**  -> permitAll
    mac dinh con lai                                    -> authenticated
```

Chinh sach phan quyen nghiep vu (hasRole / hasAnyRole) hien tai KHONG duoc khai bao cap URL pattern — chi kiem tra o cap Controller (`@PreAuthorize`). Can **bo sung rule URL cho cac module Phase 3+** theo `docs/02_Mô Tả & Thiết kế dữ liệu.md` § 7.

---

## 5. Thay doi gan day (Phase 0 -> Phase 2)

### Fix 2025-Q2 batch 1 — Login hoat dong tro lai
1. `pom.xml`: Ha `spring-boot-starter-parent` tu `4.0.5` xuong `3.5.6` vi `thymeleaf-layout-dialect 3.4.0` khong tuong thich voi Groovy 5 (shipped by Boot 4).
2. `SecurityConfig.java`: Them `import org.springframework.context.annotation.Primary`.
3. `application.properties`: Sua typo `logging.level.com.ntu.quanlyctdt` -> `com.ntu.quanlyctdtdb`.
4. `scripts/01_create_tables.sql`: Viet lai hoan toan de khop voi 20 entity + `02_seed_data.sql`.

### Fix 2025-Q2 batch 2 — Module Nguoi Dung hien thi duoc danh sach
1. `NguoiDungRepository`: Them `@EntityGraph(attributePaths = "nhomNguoiDungs")` vao `findByTenDangNhap` va `searchNguoiDung`. Them query `findWithRolesByMaNguoiDung(String)`.
2. `NguoiDungService` / `NguoiDungServiceImpl`: Them `findByIdWithRoles(ma)`.
3. `NguoiDungController`: Chuyen `chiTiet()` va `suaForm()` sang dung `findByIdWithRoles` (tranh LazyInit khi Thymeleaf render collection vai tro).
4. `application.properties`: Bo `spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect` (Hibernate 6 auto-detect).

### Fix 2026-Q2 batch 1 — UI Refactor Phase 1+2 + Nguoi Dung edit bug
1. Refactor toan bo `src/main/resources/static/css/main.css` theo design system v2 (tokenized, shadow/transition nhat quan, `.page-header`, `.stat-card`, `.stat-card-primary/accent/success/warning`, `.info-row/.info-label`, `.login-split`, sidebar active accent, focus-visible ring).
2. Refactor `templates/dashboard/dashboard.html`: group thong ke theo tung role/nghiep vu, xoa toan bo inline `style="..."`, dung variant `stat-card-*`.
3. Refactor `templates/auth/login.html`: split-panel brand (trai) + form (phai), bo inline styles, dua sang class `.login-split/.login-brand/.login-form-panel` o CSS.
4. Refactor detail pages (`nguoi-dung/chi-tiet.html`, `doanh-nghiep/chi-tiet.html`): dung chung pattern `.info-row/.info-label`.
5. **Fix bug nghiem trong** `templates/nguoi-dung/form.html`: `loaiNguoiDung` bi `disabled` khi edit khien HTML khong submit field -> `@NotNull` validation fail -> form khong save duoc. Thay bang input readonly + hidden field giu value.
6. `NguoiDungController.suaForm()`: populate them `hocHam/hocVi/chuyenNganh` (GV) va `maLopHC` (SV) khi dung cho template edit.
7. `NguoiDungServiceImpl.update()`: bo sung nhanh doi `maLopHC` cho SinhVien; tranh ghi de null cho GiangVien khi form khong gui field.
8. Bo sung `model.addAttribute("activeMenu", "nguoi-dung")` trong moi GET handler cua `NguoiDungController` de sidebar highlight dung.

---

## 6. Quy uoc scaffold cho cac module con lai

Moi khi them mot module moi, lam theo thu tu:

1. **Doc lai** `00_MASTER_REFERENCE.md` + `01_ERD_SCHEMA.md` de xac nhan entity va relationship.
2. **Entity + Repository** (da co 20/20 — hau het moudle chi can bo sung).
3. **DTO** — 1 DTO form (binding va validation) + 1 DTO Excel (neu co import/export).
4. **Service interface + Impl** trong `service/` va `service/impl/`.
   - Transactional readOnly cho query, transactional cho write.
   - Dung `@EntityGraph` o repository khi canh bao lazy collection.
5. **Controller** trong `controller/` — 1 controller / module.
   - URL `/kebab-case-tieng-viet-khong-dau` (vi du `/doanh-nghiep`, `/lop-hoc-phan`).
   - Method order: list, them (GET+POST), sua (GET+POST), chi-tiet, xoa/doi-trang-thai.
6. **Templates** trong `templates/<module>/` — toi thieu 3 file: `danh-sach.html`, `form.html`, `chi-tiet.html`.
   - Moi file phai dung `<html layout:decorate="~{layout/base}">`.
   - Them menu sidebar tuong ung trong `layout/base.html` voi `sec:authorize="hasAnyRole(...)"`.
7. **Bo sung URL rule** trong `SecurityConfig.java` (se chuyen sang `@PreAuthorize` neu phuc tap).
8. **Test**: dang nhap tung role, kiem tra deny/access hop le, kiem tra flash message, kiem tra empty state.
9. **Update docs**: check `04_DEV_CHECKLIST.md` mark phase tuong ung, cap nhat file nay neu co thay doi scaffold.

---

## 7. Known issues / tech debt

| Code   | Mo ta                                                                     | Phase xu ly |
|--------|---------------------------------------------------------------------------|-------------|
| TD-01  | 5 controller (HocPhan/CTDT/LopHocPhan/KienTap/ThucTap) thieu template -> 500 | Phase 3     |
| TD-02  | SecurityConfig chi co rule cap permitAll — chua co hasRole theo URL       | Phase 3     |
| TD-03  | Khong co URL rule cho `/bao-cao/**`                                       | Phase 6     |
| TD-04  | Chua co script migration (03_*.sql) — khi schema doi can lap              | On-demand   |
| TD-05  | `DoanhNghiepService`, `HocKyNamHocService`, `LopHanhChinhService` chua viet | Phase 3     |
| TD-06  | XAMPP MySQL 5.5 canh bao HHH000511 — can nang MySQL 8                     | Truoc prod  |
| TD-07  | File .env / application-prod.properties chua co                           | Phase 6     |
| TD-08  | Khong co unit test + integration test                                      | Lien tuc    |

---

## 8. Hanh dong tiep theo

Xem `docs/07_ROADMAP.md` de co breakdown theo tuan/phase cho cac module thieu + chien luoc test.
