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

## PHASE 3 — MODULES NGHIEP VU CHINH

### 3.1 Hoc Ky Nam Hoc [ ]

| Tac vu                                 | Target                                                |
|----------------------------------------|-------------------------------------------------------|
| `HocKyNamHocService` + impl            | CRUD + setTrangThai (chi 1 HK o DangDienRa)          |
| `HocKyNamHocController`                | `/hoc-ky`                                            |
| Templates                              | `templates/hoc-ky/{danh-sach,form}.html`             |
| Nghiep vu                              | Khi chuyen 1 HK sang DangDienRa, tu dong chuyen HK truoc ve DaKetThuc |

### 3.2 Lop Hanh Chinh [ ]

| Tac vu                                 | Target                                                |
|----------------------------------------|-------------------------------------------------------|
| `LopHanhChinhService` + impl           | CRUD + assignCoVan(maLop, maGV)                      |
| `LopHanhChinhController`               | `/lop-hanh-chinh`                                    |
| Templates                              | `templates/lop-hanh-chinh/{danh-sach,form,chi-tiet}.html` |
| Nghiep vu                              | Hien thi si so SV, co van hoc tap, CTDT ap dung     |

### 3.3 Hoc Phan + Doi Ngu GV [~]

- Controller + Service da san, chi can templates
- Templates can viet:
  - `templates/hoc-phan/danh-sach.html` — filter theo trang thai, loai, CNHP
  - `templates/hoc-phan/form.html` — them/sua voi upload FileDeCuong
  - `templates/hoc-phan/chi-tiet.html` — danh sach doi ngu GV + nut them/xoa GV
- Workflow state badge: BanNhap (gray), ChoDuyet (warning), DaDuyet (success)
- Action button: Nop duyet (ChoDuyet), Duyet (TTDTXS), Tu choi (TTDTXS, modal ly do)
- Test: BCN tao -> nop -> TTDTXS duyet -> CNHP quan ly doi ngu

### 3.4 CTDT + CTDT_HocPhan [~]

- Controller + Service da san, chi can templates
- Khi CTDT chuyen sang `DaDuyet`, service se tu dong tao `LopHocPhan` theo `SoLopDuKien` (MaGiangVien=NULL)
- Template:
  - `templates/ctdt/danh-sach.html`
  - `templates/ctdt/form.html`
  - `templates/ctdt/chi-tiet.html` — show CTDT_HocPhan list + modal them HP + nut nop duyet
- Unit test: duyet CTDT -> verify so LopHocPhan tao ra = sum(SoLopDuKien)

### 3.5 Lop Hoc Phan [~]

- Controller + Service da san, chi can templates
- Template:
  - `templates/lop-hoc-phan/danh-sach.html` — filter (HocKy, HocPhan, GiangVien)
  - `templates/lop-hoc-phan/chi-tiet.html` — info + modal gan GV + danh sach SV + nut them/xoa SV
- Nghiep vu:
  - Gan GV khong thuoc DoiNguGiangVienHP -> hien cau hoi xac nhan (data-confirm)
  - Them SV vuot qua SiSoToiDa -> chan o service, tra loi BusinessException

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
