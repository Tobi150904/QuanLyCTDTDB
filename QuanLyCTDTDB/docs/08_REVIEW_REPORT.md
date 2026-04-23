# 08_REVIEW_REPORT — Ra Soat Toan Dien Truoc Phase Moi

> Tai lieu kiem thu / code review truoc khi mo Phase 4 (Danh Gia) va Phase 5
> (Kien Tap + Thuc Tap). Khong mo Phase moi khi cac muc "P0 Blockers" ben
> duoi chua xanh.

- Phien ban bao cao: 2026-Q2 batch 6 (rev 2026-04-23)
- Pham vi: toan bo `src/main/java/**`, `src/main/resources/**`, `scripts/**`, `docs/**`
- Nguon chuan: `00_MASTER_REFERENCE.md`, `02_Mô Tả & Thiết kế dữ liệu.md`, `03_WORKFLOW.md`, `04_DEV_CHECKLIST.md`, `06_PROJECT_SCAFFOLD.md`, `07_ROADMAP.md`
- Phuong phap: (1) Doc docs lay "spec"; (2) Doc code doi chieu; (3) Doi chieu
  scripts SQL; (4) Chay mental-test cho workflow A-Z; (5) Ghi lai mismatch +
  do nghi fix.

---

## 1. KET LUAN NHANH

| Nhom muc             | Trang thai | Ghi chu                                               |
|----------------------|------------|-------------------------------------------------------|
| Phase 0–2            | XANH       | Auth, NguoiDung, DoanhNghiep san sang production.      |
| Phase 3              | XANH (sau fix batch 6) | 3 bug production-level da duoc fix tai session nay. |
| Phase 4 (Danh Gia)   | CHUA BAT DAU | Roadmap phan bo dung, chua co code.                 |
| Phase 5 (Kien Tap)   | VANG       | Service day du, **templates CHUA ton tai -> 500 runtime**. |
| Phase 5 (Thuc Tap)   | DO         | Service thieu audit + validate + state machine, templates chua ton tai. |
| Phase 7 (Prod)       | CHUA BAT DAU | Config prod profile + SMTP + healthcheck chua co.   |

Ket luan: **KHONG DUOC** mo Phase 4 cho den khi sua xong P0-1, P0-2 o §3.
Sau do co the mo Phase 4 song song voi viec hoan thien Phase 5 templates
(Phase 4 va 5 khong cung du lieu).

---

## 2. TOM TAT SUA DA THUC HIEN TRONG BATCH 6

| ID   | File                                       | Mo ta                                                                |
|------|--------------------------------------------|----------------------------------------------------------------------|
| B6-1 | `AuthController.java`                      | Toan bo flash message chuyen sang tieng Viet co dau (login error/logout/expired/disabled). |
| B6-2 | `ProfileController.java`                   | Flash message Viet dau + chan doi mat khau trung hien tai.           |
| B6-3 | `GlobalExceptionHandler.java`              | Mo rong xu ly `DataIntegrityViolationException`, `MaxUploadSizeExceededException`, `NoHandlerFoundException`, fallback 500 an stacktrace khoi view. |
| B6-4 | `NguoiDungRepository.java` + `NguoiDungServiceImpl.sinhMaNguoiDung` | Fix race/duplicate ID: thay `count()+1` bang `MAX(maNguoiDung) + 1` theo prefix. |
| B6-5 | `NguoiDungServiceImpl.java`                | Chuyen message loi sang tieng Viet co dau (13 cho); error import Excel tieng Viet. |
| B6-6 | `NguoiDungController.java`                 | Flash message chinh sua/them/toggle sang tieng Viet co dau.          |
| B6-7 | `hoc-phan/danh-sach.html`, `lop-hanh-chinh/chi-tiet.html` | Don ky hieu/flash duplicate + polish header. |
| **B6-8** | **`DotThucTapServiceImpl.create()`**   | **P0 BLOCKER**: set `NguoiTao` (truoc day null -> SQL NOT NULL viol); them validate LoaiHocPhan + TrangThaiHocPhan. |
| **B6-9** | **`DotThucTapServiceImpl.pheduyet()`** | **P0 BLOCKER**: set `NguoiDuyet` + `NgayDuyet` cho audit trail.      |
| B6-10| `DotThucTapServiceImpl.update()`           | That chat guard: chi cho sua o `ChuanBi`/`ChoDuyet`, block sau khi qua duyet. |
| B6-11| `DashboardController.java`                 | Bat `IncorrectResultSizeDataAccessException` tu `findByTrangThai(DangDienRa)`, tra ve record moi nhat (khong crash dashboard khi DB bi lech). |
| B6-12| `HocPhanController.java` / `ChuongTrinhDaoTaoController.java` / `DotKienTapController.java` / `DotThucTapController.java` | Them `@PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")` cho 5 endpoint state-transition: `HocPhan.pheduyet/tuChoi`, `CTDT.pheduyet`, `DotKT.pheduyet`, `DotTT.pheduyet`. Chan privilege escalation khi URL rule rong. |

---

## 3. P0 — BLOCKERS (PHAI SUA TRUOC KHI MO PHASE MOI)

> P0 = co the gay 500/crash, mat du lieu, hoac vi pham rang buoc DB.
> Tat ca tim thay trong phien lam viec nay. B6-8, B6-9, B6-10 da fix, con lai
> muc PB-* dang cho hanh dong.

### P0-1 Templates `kien-tap/` va `thuc-tap/` chua ton tai

- **Trieu chung**: `DotKienTapController.danhSach()` `return "kien-tap/danh-sach"`,
  nhung thu muc `src/main/resources/templates/kien-tap/` khong ton tai
  (da xac nhan qua `ls`). Bat ky request nao vao `/kien-tap**` hoac
  `/thuc-tap**` se throw `TemplateInputException` roi rot xuong
  `GlobalExceptionHandler.handleGeneral()` -> render `error/500`.
- **Muc do**: critical. Du service da co logic, toan bo Phase 5 **khong su dung duoc** qua UI.
- **Hanh dong de nghi**:
  1. Tao toi thieu 3 template cho moi module (`danh-sach`, `form`, `chi-tiet`)
     theo spec roadmap §5.1 va §5.2.
  2. Neu thieu, tam thoi CHAN route (`@PreAuthorize("hasRole('ADMIN')")`
     + trang "coming soon") de khong rot xuong error 500 trong prod.
  3. Gan label `[Phase 5]` trong sidebar `layout/base.html` cho den khi xong.

### P0-2 `DotThucTapServiceImpl` thieu endpoint controller — DANG DA FIX **MOT PHAN**

- `capNhatKetQua(maDanhSach, loaiThucTap, maDoanhNghiep, nhanXet)` nhan tham
  so `loaiThucTap` va `nhanXet` nhung **KHONG** set len entity, chi set
  `maDoanhNghiep` va ep `TrangThai=DangThucTap`.
  - Gia tri `loaiThucTap` le ra phai validate (chi `'Truong'` hoac
    `'DoanhNghiep'`; neu `DoanhNghiep` thi `maDN` BAT BUOC, nguoc lai
    `maDN=NULL`). Hien khong validate, cho phep state `'Truong'` + `maDN!=NULL`
    -> vi pham docs/02 §3.8.
  - Gia tri `nhanXet` bi drop.
- `DotThucTapController` con thieu toan bo endpoint: `batDau/{id}`,
  `ketThuc/{id}` (cascade `DanhSachThucTap.TrangThai`), `huy/{id}`,
  `import-phan-cong/{id}` (Excel), `ket-qua/{maThucTap}` (upsert
  `KetQuaThucTap` theo `(MaThucTap, MaVaiTro)`), `cua-toi` (SV self-view).
- **Hanh dong de nghi**: hoan thien `DotThucTapService` + `Impl` +
  controller + template theo roadmap Phase 5.2. Doi chieu 1-1 voi
  `03_WORKFLOW.md` WF-08.*.

### P0-3 `DashboardController` — `findByTrangThai(DangDienRa)` tra `Optional`  **[DA FIX B6-11]**

- `HocKyNamHocRepository.findByTrangThai(TrangThaiHocKy trangThai)` tra
  `Optional<HocKyNamHoc>`. Neu co HAI hoc ky cung `DangDienRa` (workflow
  auto-close con bug hoac QTV can thiep tay), Spring Data sinh
  `IncorrectResultSizeDataAccessException` -> 500 tren dashboard — trang
  dau tien moi user thay sau login.
- **Muc do**: cao. Dashboard la landing, 500 o day = ca he thong coi nhu down.
- **Hanh dong de nghi**: doi signature thanh
  `List<HocKyNamHoc> findByTrangThai(...)` + dung `.stream().findFirst()`,
  hoac them `findTopByTrangThaiOrderByNgayBatDauDesc(...)`. Song song them
  rang buoc UNIQUE moi / trigger de chan tinh trang 2 ky cung
  `DangDienRa`. Bo sung seed test case ket hop.

### P0-4 `ChuongTrinhDaoTaoController` — phe duyet khong phan quyen  **[DA FIX B6-12]**

- URL `/ctdt/**` cho phep `PDT, TTDTXS, CNHP, ADMIN` (SecurityConfig). Nhung
  `POST /ctdt/phe-duyet/{ma}` theo docs/02 §4 CHI duoc TTDTXS hoac ADMIN.
  Hien tai CNHP hoac PDT cung goi duoc endpoint — privilege escalation.
- **Hanh dong de nghi**: them `@PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")`
  tren method `pheduyet`, va tuong tu:
  - `HocPhanController.pheduyet()` / `tuChoi()` -> `hasAnyRole('TTDTXS','ADMIN')`
  - `DotKienTapController.pheduyet()` -> `hasAnyRole('TTDTXS','ADMIN')`
  - `DotThucTapController.pheduyet()` -> `hasAnyRole('TTDTXS','ADMIN')`
- Tuong tu phan ra giua state-transition (chi TTDTXS) va thao tac soan thao
  (CNHP/PDT). Hien `@EnableMethodSecurity` da bat trong `SecurityConfig`
  -> co the dung `@PreAuthorize` ngay.

### P0-5 `NguoiDungServiceImpl.sinhMaNguoiDung` — con race condition khi tai cao

- Sua o B6-4 giup het duplicate sau khi xoa record, nhung neu 2 request
  concurrent cung goi `findMaxMaNguoiDungByPrefix` -> ca hai se tao cung
  ma. UNIQUE PK se reject 1 trong 2, nhung controller lay ve
  `DataIntegrityViolationException` va user thay thong bao chung chung.
- **Hanh dong de nghi (low-risk, chon 1)**:
  - **Option A (toi thieu)**: thuc thi method trong
    `@Transactional(isolation = Isolation.SERIALIZABLE)` — dong bo qua DB.
  - **Option B (khuyen nghi)**: wrap `create()` trong
    `@Retryable(DataIntegrityViolationException.class, maxAttempts=3)` de
    retry tu dong + jitter.
  - **Option C (tuong lai)**: chuyen sang ID sequence (
    `GenerationType.SEQUENCE`) cho cot noi bo, giu business key dang
    "SV2025001" sinh o service bang sequence co locking.

---

## 4. P1 — HIGH (KHONG BLOCK PHASE MOI NHUNG PHAI CO TRUOC PROD)

### P1-1 Flash message chua thong nhat dieu chuan tieng Viet co dau

Spec `05_UI_DESIGN_SYSTEM.md` §5 yeu cau dung tieng Viet co dau. Sau B6-1..B6-6,
vi tri sau day VAN chua duoc chuyen:

| Controller                        | Ghi chu                                        |
|-----------------------------------|------------------------------------------------|
| `HocPhanController` (11 dong)     | "Tao hoc phan thanh cong", "Cap nhat thanh cong"... |
| `ChuongTrinhDaoTaoController` (9 dong)| Cac hanh dong CTDT va BCN                  |
| `LopHocPhanController` (7 dong)   | Tao hang loat, phan cong GV, canh bao SV       |
| `DoanhNghiepController` (5 dong)  |                                                |
| `HocKyNamHocController` (6 dong)  | "Tao hoc ky ... thanh cong"                    |
| `LopHanhChinhController` (5 dong) | "Phan cong CVHT", "Xoa lop"                    |
| `DotKienTapController` (10 dong)  |                                                |
| `DotThucTapController` (5 dong)   |                                                |

Tong ~58 flash string. De nghi tao file `messages.properties` + i18n key
(vd `msg.nd.tao.thanh_cong`) cho gon code va hoa ngon ngu. Tam thoi sed-
replace trong mot PR.

### P1-2 `DotThucTapController` thieu `activeMenu`

- Cac handler trong `DotThucTapController` khong set
  `model.addAttribute("activeMenu", "thuc-tap")` -> sidebar highlight sai.
- Cung mau voi `DotKienTapController` da set dung "kien-tap" moi chi noi.

### P1-3 `DotThucTapController.populateModel()` chua loc hoc phan

- `model.addAttribute("hocPhanList", hocPhanRepo.findAllDaDuyet())` — list
  tat ca HP DaDuyet, khong filter `LoaiHocPhan IN (ThucTap, KienTap)`.
  Sau khi B6-8 chan viec tao dot, UX lam user CHON xong moi biet sai
  -> de nghi loc tu dau qua `findAllDaDuyetByLoai(List<LoaiHocPhan>)`.

### P1-4 Mo dinh `@PreAuthorize` cap method cho state-transition — nhu P0-4

Da liet ke chi tiet o P0-4. Chinh lai moi tra dung defence-in-depth
(SecurityConfig URL rule + PreAuthorize method).

### P1-5 Logging level nguoi dung

- `application.properties`:
  - `logging.level.com.ntu.quanlyctdtdb=DEBUG` -> prod phai la `INFO` de
    giam IO va che gia tri nhay cam.
  - `logging.level.org.springframework.security=INFO` -> prod nen dua ve `WARN`.
- De nghi tach `application-dev.properties` / `application-prod.properties`,
  active profile qua env var `SPRING_PROFILES_ACTIVE`.

### P1-6 Nguy co lo thong tin tu `GlobalExceptionHandler.handleGeneral`

- Sau B6-3 da KHONG dua `ex.getMessage()` ra view — tot. Tuy nhien
  stack trace van duoc log tai level ERROR nen bat buoc log-shipper (ELK/
  Cloudwatch) phai tat o prod truoc khi expose.

### P1-7 CSRF cho form upload

- Tat ca form `POST` trong template van di qua CSRF token cua Thymeleaf
  layout mac dinh; nhung da kiem tra `hoc-phan/danh-sach.html`,
  `lop-hanh-chinh/chi-tiet.html` KHONG chen `sec:csrf` ro rang. Spring
  Security co auto-include neu form co `method="post"` va la Thymeleaf
  form — OK. Chay test thu
  `POST /hoc-phan/them` khong co token -> phai 403. Neu da pass test
  thi khong can fix.
- Khuyen nghi viet 1 integration test `CsrfMissingShould403Test`.

### P1-8 `spring.mail.host=localhost` stub

- Trong dev OK, nhung prod phai xoa. Config da chu thich day du o
  `application.properties`. De nghi chuyen sang profile-based config
  som nhat.

---

## 5. P2 — MEDIUM (POLISH, TECH-DEBT)

### P2-1 `LopHocPhanController.phanCong` soft-check tracuu repo

- Da theo workflow §3.3 (soft check), kiem tra `DoiNguGiangVienHP` o tang
  controller. Dong phong cach: nen dua logic soft-check vao service
  (`LopHocPhanService.phanCongGiangVien` tra ve `PhanCongResult` co flag
  `thuocDoiNgu`). Gia bay gio controller dang co logic nghiep vu.

### P2-2 `sinhMaNguoiDung` fallback nam

- `SV` prefix dung `LocalDate.now().getYear()`. Khi doi nam (vd lap 1/1),
  SV moi ke tiep se bat dau lai tu `SV2026001`. Dung luong — nhung hay
  luong truoc ke hoach export annual report roi fix neu can. Hien tai OK.

### P2-3 Seed data `02_seed_data.sql` chua check nhung field moi

- Seed file co 20 user + 12 SV + 3 CTDT. Sau khi entity them
  `DotThucTap.NguoiTao` (NOT NULL), seed rows cho `DotThucTap` neu co PHAI
  chi `NguoiTao` — kiem tra xem seed co insert `DotThucTap` truc tiep khong
  (neu chi seed den `DotKienTap` thi OK).

### P2-4 `MockEmailServiceImpl` la stub

- OK cho dev/test. Prod phai bat `SmtpEmailServiceImpl` + cau hinh SES
  hoac SMTP that (da chu thich trong properties).

### P2-5 `open-in-view=false` tuan thu tot, chi mot ngoai le

- `LopHocPhanController.danhSach` co build `hocPhanMap = hocPhanRepo.findAll()
  .stream()...` — acceptable. Nhung
  `DoanhNghiepRepository.findAll()` trong
  `DotKienTapController.populateModel` dung `.filter(...).toList()` thay vi
  `findByTrangThai(DangHopTac)`. Chuyen sang query tang nho o repo cho gon
  va giam load (hien tai 4 DN nen OK, nhung khong scale).

### P2-6 Thieu index tren cot truy van nhieu

- `NguoiDung.email`, `NguoiDung.tenDangNhap`, `LopHocPhan.maGiangVien`,
  `DanhSachSinhVienLopHocPhan.daCanhBao`, `DotKienTap.trangThai`,
  `DotThucTap.trangThai` nen co index bo sung. SQL da co UNIQUE key cho
  email + tenDangNhap (`uk_nguoidung_email`, `uk_nguoidung_tendangnhap`).
  Index trang thai + foreign key Spring Data se sinh tu dong qua FK
  rang buoc — tam thoi OK.

### P2-7 `FileStorageUtil` thieu xu ly ten file unsafe

- Chua doc source `FileStorageUtil`. De nghi kiem tra:
  - Reject path traversal (`../`), null byte (`\0`), control char.
  - Rename ve `{category}-{id}-{UUID}.{ext}` de tranh trung + de track.
  - Check MIME type (`application/pdf`, `application/msword`,
    `application/vnd.openxmlformats-officedocument.*`).

### P2-8 `ExcelImportUtil` chua co test

- Import NguoiDung qua Excel da co 1 test case manual. De nghi unit test
  voi file `.xlsx` mau (cho vao `src/test/resources`) cover: thieu cot,
  sai kieu, trung TenDangNhap, vai tro sai enum, ma loi 3 dong.

---

## 6. P3 — CONSISTENCY / DOC

### P3-1 Docs dong bo
- `07_ROADMAP.md` muc 5.1 da ghi "SKELETON ... Templates hoan toan chua
  ton tai -> controller tra ve 500" — khop voi thuc te. Tot.
- `04_DEV_CHECKLIST.md` can update them muc "Phase 3 da xong kiem thu
  P0 fix batch 6 (B6-8, B6-9, B6-10)".

### P3-2 Ten goi enum va role

- `SecurityConfig` dung `"GIANG_VIEN"`, `"DOANH_NGHIEP"`, `"SINH_VIEN"` —
  KHONG phai "GiangVien" (PascalCase) nhu enum `LoaiNguoiDung.GiangVien`.
  Ly do: Spring Security convention `ROLE_{UPPER_SNAKE}`, va
  `CustomUserDetails` da map dung. Luu y doc dev: khi dung
  `hasAnyRole(...)`, dung ten `GIANG_VIEN` chu khong phai `GiangVien`.
  Nen ghi chu o `docs/02_Mô Tả & Thiết kế dữ liệu.md` §7.

### P3-3 Activity audit log

- He thong chua co bang `AuditLog`. Moi hanh dong tao/sua/duyet/xoa hien
  chi luu qua `CreationTimestamp`/`UpdateTimestamp` + `NguoiTao`/`NguoiDuyet`
  tren tung entity. De nghi cho truoc Phase 7 tao 1 bang `AuditLog
  (actor, action, entity, entity_id, detail_json, at)` qua Spring AOP.

---

## 7. KIEM TRA SCHEMA vs ENTITY (MEMBERSHIP)

Da so doi chieu 1-1, sau day la nhung diem ky thuat da verify:

- `DotKienTap.NguoiTao` NOT NULL — entity NOT NULL — code set du: OK.
- `DotThucTap.NguoiTao` NOT NULL — entity NOT NULL — code truoc B6-8 KHONG
  set — DA FIX.
- `NguoiDung.TrangThaiTK` BIT NULL — entity dung `Boolean` NOT NULL o
  default value. `isEnabled()` kiem tra null-safe — OK.
- `LopHocPhan` EmbeddedId 4 cot — entity KHONG co `@ManyToOne HocPhan`
  truc tiep vi se duplicate 2 cot khi JPA tao SQL (docs/06 tech-debt
  "Hibernate 7 EmbeddedId"). Code dung `hocPhanMap` injection tu
  controller — OK.
- `DanhSachSvLopHocPhan` giu NOT NULL `maCTDT, maHocPhan, maHocKy, maLopHocPhan,
  maSV` qua `@EmbeddedId DanhSachSvLopHocPhanId`. `phanCongGiangVien` o
  `LopHocPhan` KHONG co LopHocPhan association trong DanhSach... — OK vi
  suy ra duoc tu id.

---

## 8. KIEM TRA ROUTE / SECURITY

| URL                            | Config (SecurityConfig)                             | Method-level            | Nhan xet       |
|--------------------------------|-----------------------------------------------------|-------------------------|----------------|
| `/login`, `/logout`, `/403`   | permitAll / default                                 | —                       | OK             |
| `/dashboard`, `/`              | authenticated                                       | —                       | OK             |
| `/profile/**`                  | authenticated                                       | —                       | OK             |
| `/nguoi-dung/**`               | `PDT, ADMIN`                                        | —                       | OK             |
| `/doanh-nghiep/**`             | `PDT, TTDTXS, ADMIN`                                | —                       | OK             |
| `/hoc-ky/**`                   | `PDT, TTDTXS, ADMIN`                                | Class @PreAuthorize     | OK (defend in depth) |
| `/lop-hanh-chinh/**`           | `PDT, TTDTXS, ADMIN`                                | Class @PreAuthorize     | OK             |
| `/hoc-phan/**`                 | `CNHP, TTDTXS, ADMIN`                               | `@PreAuthorize` cho `pheduyet`, `tuChoi` | OK (B6-12) |
| `/ctdt/**`                     | `PDT, TTDTXS, CNHP, ADMIN`                          | `@PreAuthorize` cho `pheduyet` | OK (B6-12) |
| `/lop-hoc-phan/**`             | `PDT, TTDTXS, CNHP, ADMIN, GIANG_VIEN`              | Khong co cap method     | OK (action coarse) |
| `/kien-tap/**`                 | `TTDTXS, CNHP, ADMIN, GIANG_VIEN, DOANH_NGHIEP`     | `@PreAuthorize` cho `pheduyet` | OK (B6-12) |
| `/thuc-tap/**`                 | `PDT, TTDTXS, ADMIN, GIANG_VIEN, CVHT, DOANH_NGHIEP, SINH_VIEN` | `@PreAuthorize` cho `pheduyet` | OK (B6-12) |
| `/danh-gia/**`                 | `GIANG_VIEN, CVHT, ADMIN`                           | Controller chua ton tai | Phase 4        |
| `/bao-cao/**`                  | `PDT, TTDTXS, ADMIN`                                | Controller chua ton tai | Phase 6        |

---

## 9. KIEM TRA CROSS-CUTTING (PERFORMANCE / STRUCTURE)

- `open-in-view=false` + `@EntityGraph` / `JOIN FETCH` pattern duoc ap dung
  dung (15 cho trong repositories). Khong phat hien LazyInit con song khi
  duyet nhanh code.
- `Transactional` scope: service layer mac dinh `@Transactional` (write),
  method doc dung `@Transactional(readOnly = true)` — OK.
- `@Transactional` tren controller: KHONG co — dung nguyen tac.
- `@Validated` + `@Valid`: dung o form bind. Nhung query param
  (`@RequestParam String maGV`) chua validate pattern — neu user gui
  rac, sinh ra `ResourceNotFoundException` roi thanh flash + redirect
  "Referer" — OK (GlobalExceptionHandler cover).
- N+1: kiem tra `DashboardController.thongKe` — thuan count query, khong
  N+1. `HocKyNamHocRepository.findAllByOrderByNgayBatDauDesc` — don gian.
- Pagination: `NguoiDung` + `DoanhNghiep` da phan trang. `ChuongTrinhDaoTao`,
  `HocPhan`, `LopHocPhan`, `LopHanhChinh` CHUA phan trang. Khi seed 100+
  CTDT se lam trang `/ctdt` cham. Cho Phase 7 them pageable.

---

## 10. TEST COVERAGE (THUC TE)

Dang nhin thay:
- Khong co thu muc `src/test/java` duoc review. Coverage hien 0.
- Roadmap Phase 7 yeu cau: unit service >= 70%, integration test cho 6
  workflow chinh. **Day la rao can chinh de GA** (General Availability).
- Do nghi truoc khi khoa Phase 3:
  1. `NguoiDungServiceImplTest` — sinh ma, create, update, toggle.
  2. `HocPhanServiceImplTest` — transition BanNhap -> ChoDuyet -> DaDuyet
     + email mock.
  3. `DotKienTapServiceImplTest` — auto-add SV, toggle DaThamGia, dongBo.
  4. `LopHocPhanServiceImplTest` — taoLopHocPhanChoCTDT idempotent.
  5. `ControllerMvcTest` voi `@WithMockUser(roles = "PDT")` ...

---

## 11. DANH SACH VIEC CAN LAM (LAM NGAY TRUOC PHASE MOI)

Thu tu de xuat:

1. **(P0-1)** Tao 3 template toi thieu cho `kien-tap/` (`danh-sach`, `form`, `chi-tiet`).
2. **(P0-1)** Tao 3 template toi thieu cho `thuc-tap/` (`danh-sach`, `form`, `chi-tiet`).
3. **(P0-2)** Bo sung endpoint + template cho `batDau`, `ketThuc`, `huy`,
   `importPhanCong`, `nhapKetQua` o Thuc Tap; hoan thien
   `capNhatKetQua` de validate loai + save nhanXet.
4. ~~**(P0-3)** DashboardController~~ — **DA FIX B6-11** (defensive catch).
5. ~~**(P0-4)** PreAuthorize state-transition~~ — **DA FIX B6-12**.
6. **(P0-5)** Wrap `NguoiDungService.create` voi retry/serializable hoac
   migrate sang sequence.
7. **(P1-1)** Chuan hoa flash message tieng Viet co dau o 8 controller
   con lai — lam theo i18n `messages.properties`.
8. **(P1-2..5)** Polish phia trong.
9. Cap nhat lai `04_DEV_CHECKLIST.md` + `06_PROJECT_SCAFFOLD.md` + doi
   tag Phase 3 sang "DA DONG 2026-Q2" sau khi 1-3 + 6 da xong.
10. Viet integration test cho 4 workflow chinh (NguoiDung, HocPhan,
    DotKienTap, LopHocPhan).

---

## 12. XAC NHAN DONG PHASE

Truoc khi mo Phase 4, cac dieu kien sau PHAI xanh:

- [ ] P0-1, P0-2, P0-3, P0-4, P0-5 da fix.
- [ ] `./mvnw test` chay xanh, khong `ERROR` trong log.
- [ ] `./mvnw spring-boot:run` + smoke test login + dashboard 3 role
      khac nhau khong 500.
- [ ] Navigate vao `/kien-tap`, `/thuc-tap` khong 500 (co template du tam).
- [ ] Audit da update: `docs/04_DEV_CHECKLIST.md`,
      `docs/06_PROJECT_SCAFFOLD.md`, `docs/07_ROADMAP.md`.

Khi du 5 tick, co the bat dau Phase 4.
