# 07_ROADMAP — Ke Hoach Lam Viec Chi Tiet

> Ke hoach bam sat `docs/04_DEV_CHECKLIST.md`, `docs/06_PROJECT_SCAFFOLD.md`, scripts va ma nguon hien tai.
> Thu tu cac Phase la BAT BUOC: khong bat dau Phase moi khi Phase hien tai chua xanh.

Ky hieu trang thai:
- `[x]` Hoan thanh va da test
- `[~]` Dang lam / lam mot phan
- `[ ]` Chua bat dau

---

## PHASE 0 — SETUP (DA XONG)

- [x] Tao cau truc Maven project, pom.xml (Spring Boot 3.5.6)
- [x] Cau hinh DataSource XAMPP MySQL
- [x] Viet `scripts/01_create_tables.sql` khop 20 entity
- [x] Viet `scripts/02_seed_data.sql` 16 user + du lieu toi thieu
- [x] Spring Security 6 form-login hoat dong
- [x] Dashboard co ban hien thong ke theo role

Ket qua dau ra: nguoi dung co the dang nhap, thay dashboard tuong ung.

---

## PHASE 1 — FOUNDATION (DA XONG)

- [x] 15 enum (`LoaiNguoiDung`, `VaiTro`, `TrangThaiXxx`, ...)
- [x] 20 entity + 7 `@Embeddable` Id class
- [x] 20 repository interface
- [x] `BusinessException` + `ResourceNotFoundException` + `GlobalExceptionHandler`
- [x] `UserDetailsServiceImpl` + `CustomUserDetails`
- [x] `SecurityConfig` (form login + permitAll static)
- [x] `WebMvcConfig` (serve uploads)
- [x] `layout/base.html`, `auth/login.html`, `dashboard/dashboard.html`

---

## PHASE 2 — CORE MODULES (DANG LAM)

### 2.1 Module Nguoi Dung [x]
- [x] `NguoiDungDTO`, `NguoiDungExcelDTO`
- [x] `NguoiDungService` + impl (CRUD + toggleStatus + importExcel)
- [x] `NguoiDungController` 6 endpoint
- [x] Templates: `danh-sach.html`, `form.html`, `chi-tiet.html`, `import.html`
- [x] Da fix LazyInit cho `nhomNguoiDungs` (Q2 2025 batch 2)

### 2.2 Module Doanh Nghiep [ ]

Dependency: chi can entity, repository (da co) + DTO moi.

| Tac vu                                    | File target                                           |
|-------------------------------------------|-------------------------------------------------------|
| Tao `DoanhNghiepDTO`                      | `dto/DoanhNghiepDTO.java`                              |
| Tao `DoanhNghiepService` interface         | `service/DoanhNghiepService.java`                      |
| Tao `DoanhNghiepServiceImpl`              | `service/impl/DoanhNghiepServiceImpl.java`             |
| Tao `DoanhNghiepController`               | `controller/DoanhNghiepController.java`                |
| Template `danh-sach.html`                  | `templates/doanh-nghiep/danh-sach.html`                |
| Template `form.html`, `chi-tiet.html`      | `templates/doanh-nghiep/*.html`                        |
| Them rule URL `/doanh-nghiep/**`           | `config/SecurityConfig.java` (ROLE_PDT, ROLE_TTDTXS)    |
| Them link menu sidebar                     | `templates/layout/base.html`                            |

Nghiep vu:
- CRUD Doanh Nghiep (create phai unique `MaDoanhNghiep`, email validation)
- `toggleTrangThai()`: DangHopTac <-> TamNgung
- Search theo `TenDoanhNghiep` voi phan trang (Pageable, mac dinh 20/trang)
- Khong cho xoa neu con record o `DotKienTap` hoac `DanhSachThucTap` tham chieu

Test case:
- PDT tao moi -> thay trong danh sach
- Doi TamNgung -> khong xuat hien o select cua form tao DotKienTap
- Xoa -> neu con tham chieu: bao loi BusinessException qua flash `errorMsg`

### 2.3 Bao mat toan cuc URL
- [ ] Them hasAnyRole rule cho tat ca URL module theo `02_Mô Tả & Thiết kế dữ liệu.md` § 7
- [ ] Viet test thu voi 2 tai khoan khac role, dam bao 403 khi sai quyen

---

## PHASE 3 — MODULES NGHIEP VU CHINH  (DA HOAN THANH 2026-Q2)

Tat ca 5 module duoi day da co DTO + service + controller + templates + security rules.
Dot review 2026-Q2 phat hien va fix 6 bug code, cap nhat docs va checklist.

### 3.1 Hoc Ky Nam Hoc [x]

| Tac vu                                 | Trang thai                                             |
|----------------------------------------|-------------------------------------------------------|
| `HocKyNamHocService` + impl            | [x] CRUD + doiTrangThai + delete guard                |
| `HocKyNamHocController`                | [x] `/hoc-ky` (7 endpoint)                            |
| Templates                              | [x] `hoc-ky/{danh-sach,form}.html`                    |
| Nghiep vu                              | [x] Auto-close HK cu khi active HK moi (batch 4)      |
|                                        | [x] Status auto-derive tu ngay + validate (batch 4)   |
|                                        | [x] Kich hoat status logic -> chuyển sang deriveStatus |

### 3.2 Lop Hanh Chinh [x]

| Tac vu                                 | Trang thai                                             |
|----------------------------------------|-------------------------------------------------------|
| `LopHanhChinhService` + impl           | [x] CRUD + search + phanCongCoVan + thongKe           |
| `LopHanhChinhController`               | [x] `/lop-hanh-chinh` (7 endpoint)                    |
| Templates                              | [x] `lop-hanh-chinh/{danh-sach,form,chi-tiet}.html`   |
| Nghiep vu                              | [x] Guard xoa khi con SV; cho phep null de huy CVHT   |

### 3.3 Hoc Phan + Doi Ngu GV [x] (batch 2 + batch 3)

- [x] Controller + Service + 3 templates (`danh-sach`, `form`, `chi-tiet`)
- [x] Workflow state badge: BanNhap (secondary), ChoDuyet (warning), DaDuyet (success)
- [x] Action button: Nop duyet (CNHP), Phe duyet/Tu choi (TTDTXS modal ly do)
- [x] Gui email CNHP khi phe duyet / tu choi (MockEmailServiceImpl log)
- [x] **Fix 2026-Q2 (B1)**: doi `/files/...` -> `/uploads/...` de link tai file hoat dong
- [x] **Fix 2026-Q2 (B3)**: uu tien `dto.maHocPhan` (format `HP-MATHE`) thay vi force `HP001`
- [x] **BATCH 3 — DoiNguGV module**:
  - DTO `DoiNguGvDTO`, service `DoiNguGvService` + impl.
  - 3 endpoint moi tren `HocPhanController`:
    `/hoc-phan/chi-tiet/{ma}/doi-ngu/them`,
    `/hoc-phan/chi-tiet/{ma}/doi-ngu/toggle`,
    `/hoc-phan/chi-tiet/{ma}/doi-ngu/xoa`.
  - Section "Doi Ngu Giang Vien" trong `hoc-phan/chi-tiet.html` (table + modal them).
  - Service guard: khong duoc xoa GV la ChuNhiemHP cua HP.
  - Repository: `findByHocPhanFetch` JOIN FETCH GV + NguoiDung.
- [x] **BATCH 3 — Fix LazyInit chi-tiet HP**: `HocPhanRepository.findByIdFetch`
  JOIN FETCH ChuNhiemHP + NguoiDung -> trang chi-tiet HP hien ten GV dung.
- Test: CNHP tao -> nop -> TTDTXS duyet -> email log thanh cong.
  Them GV vao doi ngu / toggle tat / xoa thu -> guard hoat dong.

### 3.4 CTDT + CTDT_HocPhan + BCN [x] (batch 2 + batch 3)

- [x] Controller + Service + 3 templates
- [x] CRUD CTDT + them/xoa HP trong CTDT (CtdtHocPhan)
- [x] **Fix 2026-Q2 (B2)**: tach service method `updateFileWord()` vi truoc day
      controller set file path tren entity detached -> bi mat sau commit
- [x] **Fix 2026-Q2 (B4)**: pheduyet set day du `nguoiDuyet` + `ngayDuyet`
- [x] **Fix 2026-Q2 (B6)**: xoaHocPhan co server-side guard chan xoa khi CTDT DaDuyet
- [x] **BATCH 3 — BCN module** (Ban Chu Nhiem CTDT):
  - DTO `BcnThanhVienDTO` (maGV, chucDanh, ngayBoNhiem, ghiChu).
  - Service `BcnThanhVienService` + impl: themThanhVien (guard 1 CTDT chi
    1 Chu Nhiem qua `findFirstByChuongTrinhDaoTao_MaCTDTAndId_ChucDanh`),
    xoaThanhVien, findByCtdt.
  - 2 endpoint tren `ChuongTrinhDaoTaoController`:
    `/ctdt/chi-tiet/{ma}/bcn/them`, `/ctdt/chi-tiet/{ma}/bcn/xoa`.
  - Tab "Ban Chu Nhiem" trong `ctdt/chi-tiet.html` + modal them.
  - Repository: `findByCtdtFetch` JOIN FETCH GV + NguoiDung.
- [x] **Thiet ke (khong phai defer)**: tao LopHocPhan = manual action
      `/lop-hoc-phan/tao-hang-loat?maCTDT=&maHocKy=` + HocKyThu filter +
      per-HP soLopOverride. Ly do: 1 CTDT mo nhieu nam lien tiep, moi
      ky so lop khac, phai cho user chon ro + cho override. Khong co
      khai niem "auto cascade khi pheduyet" nua.

### 3.5 Lop Hoc Phan [x] (batch 2 + batch 3)

- [x] Controller + Service + 2 templates (`danh-sach`, `chi-tiet`)
- [x] taoLopHocPhanChoCTDT(maCTDT, maHocKy, soLopOverride) — idempotent, skip neu ton tai.
  **BATCH 3 UPDATE**: signature them `Map<String,Integer> soLopOverride`.
  Service nay parse chu so sau "HK" trong maHocKy (format `HKn-YYYY`) va
  chi INSERT cho HP co `CtdtHocPhan.hocKyThu == parsedKy`. Fix bug:
  truoc day chon ky nao cung tao het tat ca HP cua CTDT.
- [x] phanCongGiangVien + email notification GV.
  **BATCH 3 UPDATE**: `LopHocPhanController.phanCong` them soft-check truoc
  khi goi service. GV khong thuoc `DoiNguGiangVienHP` (hoac bi TrangThai=0)
  van duoc gan, nhung flash `warningMsg` thay vi successMsg, huong dan user
  bo sung doi ngu tai trang Chi tiet Hoc Phan.
- [x] **BATCH 3 — Ke Hoach Mo Lop card**: `danhSach` handler them `hpDuKien`
  (list CtdtHocPhan theo maCTDT + hocKyThu fetch qua `findByCtdtAndKyFetch`)
  va `daMoCount` (map maHP -> so lop da mo) vao model. Template hien badge
  "Da mo N/M lop" / "Chua mo" tren tung HP du kien.
- [x] **BATCH 3 — Modal tao hang loat**: pre-fill SoLop = `ctdtHP.soLopDuKien`,
  cho user chinh per-HP truoc khi confirm (gui qua `soLop[hpCode]` map
  xuong controller).
- [x] dangKyLopHocPhan (guard trang thai DangMo + chan trung).
- [x] canhBaoSinhVien + gui email CVHT.
- Thiet ke dac biet: bo truc tiep `@ManyToOne` den `HocPhan`/`HocKy` trong
  `LopHocPhan` (tranh Hibernate 7 duplicate column error tren EmbeddedId 4 cot);
  template nhan `hocPhanMap` tu controller de render tenHocPhan.

---

## PHASE 3.X — BATCH 4 (2026-Q2 Hotfix: HK status machine + file upload + form error UX)

- [x] **HK status auto-derive + validation**:
  - [x] `deriveStatus(ngayBatDau, ngayKetThuc)`: compute TrangThai tu ngay hien tai.
  - [x] `create()` + `update()`: throw BusinessException neu state khong khop ngay.
  - [x] `doiTrangThai()`: validate (today >= ngayBatDau AND today <= ngayKetThuc).
  - [x] `update()`: cho phep "revive" HK DaKetThuc neu sua ngay sang tuong lai.
  - [x] `resyncStatuses()`: auto-sync moi lan `findAll()` — handle admin back-dating.
  - [x] UI: xoa nut "Kich hoat" (status tu-derive), update form hint ve 3 rule.

- [x] **File upload binding fix** (`@InitBinder`):
  - [x] `HocPhanController.initHocPhanBinder()` disallow `fileDeCuong`.
  - [x] `ChuongTrinhDaoTaoController.initCtdtBinder()` disallow `fileWord`.
  - [x] Root cause: Spring co gang convert `MultipartFile -> String`.

- [x] **Form POST error UX** (re-render on error):
  - [x] `#fields.hasErrors('*')` BAT BUOC trong `<form th:object>` (binding context).
  - [x] `errorMsg` (non-binding) ngoai form, render qua `layout/base.html`.
  - [x] Controller catch -> `model.addAttribute("errorMsg", ...)` -> return form template.

- [x] **Sidebar RBAC expansion** (GV + SV read access):
  - [x] Sidebar section "Dao Tao" + menu Hoc Phan / CTDT / Lop Hoc Phan mo cho GV/SV (read-only).
  - [x] Writes chan qua `@PreAuthorize` method-level + inline `sec:authorize` button.

- [x] **Other**:
  - [x] NguoiDung chi-tiet: fix LazyInit `sv.getLopHanhChinh()`.
  - [x] Logout alert: xoa param.logout (thay bang successMsg).
  - [x] Edit button: icon-only `bi-pencil` (dong bo 4 icon action).

---

## PHASE 4 — DANH GIA & CANH BAO

### 4.1 Danh Gia SV trong Lop Hoc Phan [ ]

| Tac vu                                    | Target                                                      |
|-------------------------------------------|-------------------------------------------------------------|
| `DanhSachSVLopHPService` + impl           | nhapNhanXet, xuLyCanhBao, getSoCanhBaoChuaXuLy              |
| `DanhGiaController`                       | `/danh-gia`                                                  |
| Templates                                 | `templates/danh-gia/{nhan-xet,canh-bao}.html`                |
| Wire EmailService                         | Khi DaCanhBao = 1 -> MockEmailService gui email den CVHT     |

Nghiep vu:
- GV nhap nhan xet tung SV -> save vao `DanhSachSinhVienLopHocPhan.NhanXet`, `DaCanhBao`
- Neu `DaCanhBao = 1` va truoc day = 0 -> trigger email canh bao:
  - Lay `CVHT` = `LopHanhChinh.MaCoVan` cua SV do
  - Noi dung email: `HoTenSV, TenHocPhan, NhanXetGV`
- CVHT co man hinh rieng `/danh-gia/canh-bao` thay danh sach canh bao chua xu ly (KetQuaXuLy IS NULL)
- Khi CVHT nhap xong -> `KetQuaXuLy` duoc luu, `DaCanhBao` van = 1 (lich su)

Test:
- GV4 nhap `DaCanhBao = 1` cho SV3 -> MockEmailService log `to=GV3@...` (GV3 la CVHT cua CNTT-K22A)
- CVHT xu ly -> ban ghi ton tai + KetQuaXuLy da set

---

## PHASE 5 — KIEN TAP & THUC TAP

### 5.1 Kien Tap [~] — DANG LAM

**Trang thai hien tai:**
- Service + Controller la SKELETON: chi co create/update/list/pheduyet/guiPheDuyet/detail.
- Chua co logic Auto-add SV, chua set NguoiTao/NguoiDuyet, thieu 6 endpoint quan trong.
- Templates hoan toan chua ton tai -> controller tra ve 500.

**Cong viec can lam:**
- Fix service:
  - `create()`: SET `NguoiTao = currentUser.maNguoiDung`; validate DN `DangHopTac`;
    AUTO-ADD tat ca SV `DangHoc` cua lop vao `DanhSachSinhVienKienTap (DaThamGia=1)`.
  - `pheduyet()`: SET `NguoiDuyet + NgayDuyet`.
  - Them: `hoanThanh()`, `huy()`, `capNhatDaThamGia()`, `dongBoDanhSachSV()`,
    `nhanXetGV()`, `nhanXetDN()`.
- Bo sung controller endpoint:
  - POST `/kien-tap/hoan-thanh/{id}`  (DaDuyet -> DaThucHien)
  - POST `/kien-tap/huy/{id}`         ({ChuanBi,ChoDuyet,DaDuyet,DaThucHien} -> DaHuy)
  - POST `/kien-tap/chi-tiet/{id}/sv/{maSV}/danh-dau?daThamGia=0|1`
  - POST `/kien-tap/chi-tiet/{id}/dong-bo`
  - POST `/kien-tap/nhan-xet-gv/{id}`, POST `/kien-tap/nhan-xet-dn/{id}`
- Templates:
  - `templates/kien-tap/danh-sach.html`
  - `templates/kien-tap/form.html` — upload FileMinhChung, chon LopHC + DN + GVPhuTrach + HocKy
  - `templates/kien-tap/chi-tiet.html`:
    - Header: badge trang thai + action transitions conditional theo role/state.
    - Bang DS SV: cot DaThamGia hien toggle + nut "Danh dau khong tham gia"/"Xac nhan tham gia".
    - Nut "Dong bo danh sach lop" (tooltip giai thich khi nao dung).
    - 2 textarea NhanXet (GV/DN) hien conditional theo role.
- Workflow state:
  `ChuanBi -> ChoDuyet -> DaDuyet -> DaThucHien`
  `{ChuanBi,ChoDuyet,DaDuyet,DaThucHien} -> DaHuy`

**Test:**
- Tao dot cho lop K22B -> tu dong them 2 SV DangHoc (SV2022003, SV2022004); SV2022005 ThoiHoc KHONG duoc them.
- Toggle DaThamGia=0 cho SV2022004 -> ban ghi van ton tai, COUNT DaThamGia=1 = 1.
- Dong bo sau khi them SV moi vao lop -> insert ban ghi moi, giu nguyen DaThamGia cua SV2022004.
- Duyet dot -> GV4 + DN1 nhap nhan xet rieng (2 textarea khong ghi de nhau).
- Huy dot -> toggle DaThamGia bi khoa.

### 5.2 Thuc Tap + Ket Qua [~] — DANG LAM

**Trang thai hien tai:**
- Service + Controller la SKELETON tuong tu Kien Tap.
- Chua validate `LoaiHocPhan IN ('ThucTap','KienTap')` khi tao dot.
- Thieu 6 endpoint quan trong + logic import Excel + upsert KetQuaThucTap.
- Templates chua ton tai.

**Cong viec can lam:**
- Fix service:
  - `create()`: validate `HocPhan.LoaiHocPhan` + SET `NguoiTao`.
  - `pheduyet()`: SET `NguoiDuyet + NgayDuyet`.
  - Them: `batDau()`, `ketThuc()` (cascade DanhSachThucTap.TrangThai), `huy()`,
    `importPhanCong()`, `nhapKetQua()`.
- Controller endpoint bo sung:
  - POST `/thuc-tap/bat-dau/{id}`, `/ket-thuc/{id}`, `/huy/{id}`
  - POST `/thuc-tap/import-phan-cong/{id}` (Excel)
  - POST `/thuc-tap/ket-qua/{maThucTap}` (upsert KetQuaThucTap)
  - GET `/thuc-tap/cua-toi` (SV xem phan cong cua minh)
- Templates:
  - `templates/thuc-tap/danh-sach.html`
  - `templates/thuc-tap/form.html` — chon CTDT_HocPhan (filter loai ThucTap/KienTap) + HocKy
  - `templates/thuc-tap/chi-tiet.html` — list phan cong + import Excel + modal nhap ket qua
  - `templates/thuc-tap/cua-toi.html` — SV xem phan cong cua minh
- Import Excel: dung `ThucTapExcelDTO` + `ExcelImportUtil.parseMultipartToDTO(...)`,
  validate rules LoaiThucTap/MaDN theo §3.8 cua docs/02, skip trung `(MaDotTT, MaSV)`,
  tra ve `ImportReport { totalRows, inserted, skipped, errors[] }`.
- Nhap ket qua: modal chon `VaiTro` (GV/DN/CVHT/SV), Diem (0-10), NhanXet.
  Upsert theo `(MaThucTap, MaVaiTro)`.

**Test:**
- Tao dot voi HP `LoaiHocPhan=LyThuyet` -> reject BusinessException.
- Import Excel chua 2 dong: 1 dong trung + 1 dong LoaiThucTap='DoanhNghiep' thieu MaDN
  -> report 0 inserted, 2 skipped + cac loi cu the.
- DN + GV cung nhap cho `(MaThucTap=1)` -> tao 2 record `KetQuaThucTap` voi `MaVaiTro` khac.
- Ket thuc dot -> cascade `DanhSachThucTap.TrangThai = DaKetThuc`.

---

## PHASE 6 — BAO CAO & DASHBOARD CAO CAP

### 6.1 Bao Cao Excel [ ]

- `BaoCaoController`:
  - GET `/bao-cao/tong-quan` — thong ke theo role (dashboard nang cao)
  - GET `/bao-cao/nguoi-dung/export` — Excel NguoiDung
  - GET `/bao-cao/lop-hoc-phan/export` — Excel LopHocPhan + trang thai tai lieu
  - GET `/bao-cao/thuc-tap/export` — Excel phan cong + diem
- `BaoCaoService` impl tra `ByteArrayInputStream` + proper headers (`Content-Disposition: attachment`)

### 6.2 Dashboard charts [ ]
- Them Chart.js vao `static/js/main.js`
- Widget: So SV bi canh bao / tong SV (donut), So LopHocPhan chua co GV / tong (bar)

---

## PHASE 7 — PRE-PROD HARDENING

- [ ] Cau hinh `application-prod.properties` — ddl-auto=validate, cache=true, password trong ENV
- [ ] Chuyen `MockEmailServiceImpl` sang `SmtpEmailServiceImpl` (profile prod)
- [ ] Them healthcheck `/actuator/health` + readiness probe
- [ ] Bat `spring.thymeleaf.cache=true`, `spring.jpa.show-sql=false`
- [ ] Cau hinh Nginx / reverse proxy (deploy team phu trach)
- [ ] Backup schedule cho MySQL (dump hang ngay)
- [ ] Nang cap MySQL 5.5 -> 8.0+ (giai quyet TD-06)

---

## CHIEN LUOC TEST

| Loai test          | Cong cu                         | Khi nao dung                                      |
|--------------------|---------------------------------|---------------------------------------------------|
| Unit (Service)     | JUnit 5 + Mockito               | Moi service method co nhanh logic (if/switch)     |
| Repository         | `@DataJpaTest` + H2             | Custom @Query, @EntityGraph                       |
| Controller (MVC)   | `@WebMvcTest` + `MockMvc`       | Route + phan quyen + flash message                |
| Integration        | `@SpringBootTest` + Testcontainers MySQL | Workflow nhieu module (CTDT -> auto LopHocPhan) |
| E2E (optional)     | Playwright                      | Smoke test sau deploy                             |

**Muc dich toi thieu:**
- Coverage service layer >= 70%
- Moi workflow chinh (6 workflow o `03_WORKFLOW.md`) co it nhat 1 integration test

---

## RUI RO + GIAM THIEU

| Rui ro                                                   | Tac dong      | Giam thieu                                                           |
|----------------------------------------------------------|---------------|----------------------------------------------------------------------|
| LazyInit khi render Thymeleaf khi them module moi         | 500 error     | Bat buoc `@EntityGraph` tren custom query tra entity co collection    |
| `ddl-auto=validate` fail sau khi alter bang bang tay      | App khong start | Chi doi schema qua `scripts/03_*.sql`, cap nhat entity dong thoi     |
| MySQL 5.5 XAMPP qua cu, SQL moi khong chay                | Dev bi block  | Nang XAMPP len 8.2 hoac dung Docker MySQL 8                          |
| Enum mismatch Java <-> MySQL                             | Hibernate throw ở load | Moi khi them enum value: sua ca DDL + Java + 02_Mô Tả... .md § 2 |
| CSRF bi disabled trong SecurityConfig                    | Bao mat keu   | Khong dong CSRF — giu mac dinh Spring Security                        |
| Import Excel file lon -> OOM                              | Server crash  | `spring.servlet.multipart.max-file-size=20MB`, dung stream POI        |

---

## UOC LUONG SCOPE CON LAI

| Phase   | So file moi (du kien) | So file sua (du kien) |
|---------|------------------------|------------------------|
| 2.2     | 5 (DN service+ctrl+3 tpl) | 2 (Security, base.html) |
| 3.1+3.2 | 10 (HK + LopHC moi)    | 1                      |
| 3.3+3.4+3.5 | 9 templates thieu   | 1                      |
| 4       | 3 (DanhGiaController + 2 tpl) | 2              |
| 5       | 7 templates            | 0                      |
| 6       | 5 (BaoCao + tpl)       | 1                      |
| 7       | 3 (prod properties, SMTP impl, healthcheck) | 2 |

Tong khoi luong con lai: ~40 file moi, ~10 file sua.

---

## CHUNG TU TRUOC KHI DOI PHASE

Truoc khi bat dau Phase n+1, Phase n phai dap ung:

1. Tat ca muc `[x]` trong section cua Phase n.
2. Khong con `TemplateInputException` o trang hoat dong.
3. `./mvnw test` xanh.
4. Checklist lien quan trong `04_DEV_CHECKLIST.md` da tick.
5. File `06_PROJECT_SCAFFOLD.md` va file nay duoc cap nhat.
