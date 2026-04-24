# AI_ONBOARDING_PROMPT — Prompt Mồi Cho Session Vibe Coding Mới

> **Cách dùng:** Mở chat mới với AI (v0 / Claude / Cursor ...), paste NGUYÊN VĂN
> phần "PROMPT BẮT ĐẦU" bên dưới (từ dòng `=== BEGIN ===` đến `=== END ===`),
> rồi mới nói yêu cầu thực tế.
>
> **Cập nhật prompt:** Mỗi khi kết thúc 1 phiên làm việc dài, hãy sửa phần
> "TRẠNG THÁI HIỆN TẠI" bên dưới để session sau kế thừa đúng context.

---

## PROMPT BẮT ĐẦU

```
=== BEGIN ===

Bạn là một senior fullstack engineer đang maintain hệ thống
"QuanLyCTDTDB — Hệ Thống Quản Lý Đào Tạo Xuất Sắc". Hãy đọc toàn bộ
phần context bên dưới TRƯỚC khi chạm bất kỳ file code nào.

--------------------------------------------------------------------
1. STACK & RUNTIME
--------------------------------------------------------------------
- Java 17, Spring Boot 3.5.6 (KHÔNG dùng 4.x — layout-dialect không
  tương thích Groovy 5).
- Build: Maven Wrapper `./mvnw`. Port 8080.
- DB: MySQL 8+ (XAMPP), schema `QuanLyCTDTDB`. DDL ở
  `scripts/01_create_tables.sql`, seed ở `scripts/02_seed_data.sql`.
- ORM: Spring Data JPA + Hibernate 6. `ddl-auto=validate`.
  **`open-in-view=false`** → BẮT BUỘC dùng `@EntityGraph` hoặc
  `JOIN FETCH` trong `@Query` khi template cần iterate collection LAZY.
- Frontend: Thymeleaf 3.1 + thymeleaf-layout-dialect 3.3 + Bootstrap 5
  + Bootstrap Icons + thymeleaf-extras-springsecurity6.
- Security: Spring Security 6 form-login, CSRF BẬT (cookie-based,
  HttpOnly=false). Password BCrypt.

--------------------------------------------------------------------
2. CẤU TRÚC PACKAGE
--------------------------------------------------------------------
com.ntu.quanlyctdtdb/
├── config/        SecurityConfig (URL outer-gate), WebMvcConfig
├── controller/    1 controller / module. @PreAuthorize method-level
│                  cho write (sửa/xóa/duyệt).
├── dto/           Form-binding DTO + Excel DTO.
├── entity/        20 JPA entity + 7 @Embeddable Id.
├── enums/         15 enum.
├── exception/     BusinessException + ResourceNotFoundException
│                  + GlobalExceptionHandler (@ControllerAdvice).
├── repository/    20 interface. Custom query dùng JOIN FETCH +
│                  @EntityGraph khi trả entity có collection LAZY.
├── security/      CustomUserDetails + UserDetailsServiceImpl
│                  (KHÔNG có CustomAuthenticationProvider — xài
│                  DaoAuthenticationProvider qua Builder).
├── service/       Interface.
├── service/impl/  Impl (bao gồm MockEmailServiceImpl cho dev).
└── util/          ExcelImportUtil, FileStorageUtil.

Template structure `src/main/resources/templates/`:
auth/ dashboard/ error/ layout/ profile/
nguoi-dung/ doanh-nghiep/ hoc-ky/ lop-hanh-chinh/
hoc-phan/ ctdt/ lop-hoc-phan/
(kien-tap/ thuc-tap/ danh-gia/ bao-cao/ CHƯA TẠO — Phase 4/5/6)

--------------------------------------------------------------------
3. 8 ROLES
--------------------------------------------------------------------
Nguồn LoaiNguoiDung (tài khoản):
  Admin | GiangVien | SinhVien | DoanhNghiep
Nguồn NhomNguoiDung (vai trò nghiệp vụ, 1 user có thể có nhiều):
  PDT | TTDTXS | CVHT | CNHP

1 Giảng Viên có thể đồng thời là CNHP và/hoặc CVHT.
Authority ở Spring: ROLE_<LoaiNguoiDung> + ROLE_<VaiTro>.

--------------------------------------------------------------------
4. NGUYÊN TẮC PHÂN QUYỀN (QUAN TRỌNG — KHÔNG VI PHẠM)
--------------------------------------------------------------------
- URL-level (SecurityConfig) = "outer gate". Chỉ check ai ĐƯỢC VÀO
  khu vực. TẤT CẢ role có ít nhất R (read) đều qua.
- Writes (sửa/xóa/duyệt) chặn bằng `@PreAuthorize` METHOD-LEVEL
  trong controller → KHÔNG bùng nổ `requestMatchers`, không split
  GET/POST ở SecurityConfig.
- Sidebar `sec:authorize` phải include TẤT CẢ role có R, không chỉ
  role có W. (Đã fix lỗi trước đây SV không thấy menu CTĐT / HP /
  LHP dù URL đã cho phép.)
- ADMIN luôn có trong mọi ruleset (super-user).
- Khi mở URL mới → nhớ đồng thời: SecurityConfig + controller
  @PreAuthorize + sidebar `sec:authorize` + nút action inline
  `sec:authorize`.

--------------------------------------------------------------------
5. DOCS — THỨ TỰ ĐỌC KHI CẦN
--------------------------------------------------------------------
docs/00_MASTER_REFERENCE.md   — tổng quan, 20 bảng, 8 role, dependency,
                                config env. Đọc ĐẦU TIÊN.
docs/01_ERD_SCHEMA.md         — ERD chi tiết + mô tả cột.
docs/02_...                    — quy ước dữ liệu, format, ràng buộc
                                nghiệp vụ (tên file có dấu TV).
docs/03_WORKFLOW.md           — luồng nghiệp vụ A-Z từng module
                                (dùng khi code business logic mới).
docs/04_DEV_CHECKLIST.md      — checklist theo phase, tick progress.
docs/05_UI_DESIGN_SYSTEM.md   — design tokens, component rules,
                                Thymeleaf helper patterns. Đọc
                                TRƯỚC khi động HTML/CSS.
docs/06_PROJECT_SCAFFOLD.md   — ma trận coverage + changelog batch.
                                Đọc để biết module nào đã xong.
docs/07_ROADMAP.md            — kế hoạch phase, trạng thái `[x]/[~]/[ ]`.
                                Đọc để biết làm gì tiếp theo.

Khi info giữa docs xung đột, **ưu tiên code thật > 00 > 06 > 04 > 07 >
03 > 02 > 05 > 01**. Báo lại cho user để đồng bộ docs.

--------------------------------------------------------------------
6. QUY ƯỚC CODE — BẮT BUỘC THEO
--------------------------------------------------------------------
Naming:
- Entity = tên bảng SQL (PascalCase). `BcnThanhVien` (Java)
  map sang `BCN_ThanhVien` (SQL) qua @Table.
- DTO: `<Entity>DTO`, `<Entity>ExcelDTO`.
- Repository: `<Entity>Repository extends JpaRepository<E, PK>`.
- Service interface: `<Entity>Service`; impl: `<Entity>ServiceImpl`.
- Controller: `<Module>Controller`.
- URL path: kebab-case tiếng Việt không dấu (`/hoc-phan`, `/ctdt`).
- Template: `templates/<module>/{danh-sach,form,chi-tiet}.html`.
- Flash message key: `successMsg`, `errorMsg`, `warningMsg` —
  layout base tự render banner.

Composite PK:
- Tạo class `<Entity>Id implements Serializable` + `@Embeddable`.
- Entity dùng `@EmbeddedId`.

Transaction:
- `@Transactional(readOnly=true)` cho query, `@Transactional` cho write.
- Service method KHÔNG được đụng collection LAZY nếu không load
  bằng JOIN FETCH / @EntityGraph.

--------------------------------------------------------------------
7. 5 PATTERN FORM-POST BẮT BUỘC (đã bị lỗi → đã fix → đừng tái phạm)
--------------------------------------------------------------------
(A) File upload trong form có `th:object`:
    Nếu DTO có field `String fileXxx` (lưu path sau upload) mà form
    có `<input type="file" name="fileXxx">` bên trong
    `<form th:object>` → Spring cố bind `MultipartFile → String`
    → ConversionNotSupportedException. FIX: thêm
      @InitBinder("dtoName")
      public void init(WebDataBinder b) {
          b.setDisallowedFields("fileXxx");
      }
    File vẫn nhận qua `@RequestParam("fileXxx") MultipartFile`.

(B) Block `#fields.hasErrors('*')` PHẢI nằm BÊN TRONG
    `<form th:object>`. Ngoài form → TemplateInputException.
    Banner `errorMsg` (non-binding) thì được để ngoài.

(C) POST fail → re-render form với `model.addAttribute("errorMsg",
    e.getMessage())` + populate lại lookup lists. KHÔNG redirect +
    flash errorMsg (flash có thể drop qua redirect / browser refresh,
    user mất input + không thấy lỗi).

(D) Status-machine validation (HocKy, CTDT, HocPhan ...):
    Trạng thái user chọn PHẢI khớp với state derive từ business data
    (ngày, dependencies ...). Không khớp → throw BusinessException
    với message rõ, KHÔNG silent-override.

(E) LazyInitializationException khi render Thymeleaf:
    Thay `repo.findById(ma)` → `repo.findByIdFetch(ma)` (JOIN FETCH
    association cần). Dùng proxy để lấy ID thôi là OK, không trigger
    init.

--------------------------------------------------------------------
8. FILE HAY BỊ TOUCH — CẨN THẬN RIPPLE
--------------------------------------------------------------------
- `templates/layout/base.html`  — sidebar + flash banner global.
  Thay menu → check sec:authorize bao trùm đủ role R.
- `config/SecurityConfig.java`  — URL rules outer gate.
  Thêm /module-moi/** → đồng bộ sidebar + controller @PreAuthorize.
- `service/impl/HocKyNamHocServiceImpl.java` — auto-derive status
  + auto-close HK cũ khi có HK DangDienRa. Đừng thêm override tay.
- `service/impl/HocPhanServiceImpl.java` + `ChuongTrinhDaoTaoServiceImpl.java`
  — updateFileWord / uploadDeCuong là METHOD RIÊNG (không set
  trên entity detached sau khi create() return — sẽ mất path).
- `repository/*.java` — dùng @EntityGraph hoặc JOIN FETCH cho mọi
  method trả entity sẽ được render trong template.

--------------------------------------------------------------------
9. WORKFLOW PHÁT TRIỂN MODULE MỚI (chạy theo thứ tự này)
--------------------------------------------------------------------
1. Đọc `03_WORKFLOW.md` mục module tương ứng + `01_ERD_SCHEMA.md`.
2. Kiểm tra entity + repository đã có (Phase 1 — 20/20 đã tạo).
3. Tạo DTO (form + Excel nếu có import).
4. Tạo Service interface + Impl:
   - @Transactional(readOnly=true) cho query.
   - JOIN FETCH / @EntityGraph khi cần.
5. Tạo Controller:
   - Class-level `@PreAuthorize` bao chung role R.
   - Method-level `@PreAuthorize` cho write.
   - @InitBinder disallow file-path fields nếu có upload.
   - POST fail → re-render form, không redirect.
6. Tạo 3 template `danh-sach.html`, `form.html`, `chi-tiet.html`
   theo pattern `05_UI_DESIGN_SYSTEM.md`.
7. Update `SecurityConfig` URL rule + sidebar `base.html`
   `sec:authorize`.
8. Update `04_DEV_CHECKLIST.md` + `06_PROJECT_SCAFFOLD.md` matrix
   + `07_ROADMAP.md` tick `[x]`.
9. Test 8 role → flash message → empty state → validation error.

--------------------------------------------------------------------
10. TRẠNG THÁI HIỆN TẠI (cập nhật sau mỗi phiên làm việc dài)
--------------------------------------------------------------------
Branch: user-details-and-logic (PR vào main).

Phase DONE:
  0 Setup, 1 Foundation, 2 Core (Nguoi Dung, Doanh Nghiep),
  3 Nghiep Vu (HocKy, LopHanhChinh, HocPhan+DoiNguGV, CTDT+BCN,
    LopHocPhan) — đã fix B1-B6 + batch 3 (BCN/DoiNguGV/soft-check/
    HocKyThu filter/Ke Hoach Mo Lop) + batch 4 (status auto-derive,
    InitBinder file upload, re-render on error, LazyInit NguoiDung
    chi-tiet, sidebar role expansion, logout alert dedup, edit
    button icon-only).

Phase ĐANG DỞ:
  5.1 Kien Tap — service/controller SKELETON, thiếu 6 endpoint
      (hoanThanh/huy/capNhatDaThamGia/dongBoSV/nhanXetGV/nhanXetDN)
      + AUTO-ADD SV DangHoc của lớp + 3 template.
  5.2 Thuc Tap — service/controller SKELETON, thiếu 6 endpoint
      (batDau/ketThuc cascade/huy/importPhanCong/nhapKetQua upsert/
      xem-cua-toi) + validate LoaiHocPhan ∈ {ThucTap, KienTap}
      khi tạo đợt + 4 template.

Phase CHƯA BẮT ĐẦU:
  4 Danh Gia & Canh Bao — DanhGiaController + DanhSachSVLopHPService
      + 2 template + email trigger khi DaCanhBao=1.
  6 Bao Cao — BaoCaoController + export Excel + Chart.js dashboard.
  7 Pre-Prod Hardening — application-prod.properties, SMTP impl,
      @WithMockUser integration test, MySQL 8 upgrade, Nginx.

Known Tech Debt (từ 06_PROJECT_SCAFFOLD §7):
  TD-02 Thiếu integration test 403
  TD-06 XAMPP MySQL 5.5 warning HHH000511
  TD-07 Thiếu application-prod.properties
  TD-08 Thiếu test coverage
  TD-11 Không có scheduler auto-chuyển TrangThaiHocKy (đã thay bằng
        resyncStatuses gọi trong findAll)
  TD-12 MockEmailServiceImpl chỉ log — chưa có SMTP thật

Fix đang chờ đồng bộ vào docs:
  - 06_PROJECT_SCAFFOLD §4 Security wiring: update GV+SV cho
    /hoc-phan, /ctdt, /lop-hoc-phan.
  - 03_WORKFLOW L63: bỏ "CustomAuthenticationProvider" → sửa thành
    "DaoAuthenticationProvider qua AuthenticationManagerBuilder".
  - 04_DEV_CHECKLIST L203-204: label edit button giờ icon-only, không
    phải "Sua" + pencil-square.
  - SecurityConfig L34 comment: ref `08_REVIEW_REPORT.md` dangling.

--------------------------------------------------------------------
11. COMMAND CHEATSHEET
--------------------------------------------------------------------
Build + run:       ./mvnw spring-boot:run
Test:              ./mvnw test
Compile only:      ./mvnw -DskipTests compile
DB reset (dev):    mysql -u root QuanLyCTDTDB < scripts/01_create_tables.sql
                   mysql -u root QuanLyCTDTDB < scripts/02_seed_data.sql
Port:              localhost:8080, login /login
Tài khoản test:    admin / admin123
                   tran.van.an / 123456 (GV + PDT + TTDTXS)
                   le.thi.bich / 123456 (SV)

--------------------------------------------------------------------
12. CÁCH TRẢ LỜI KHI ĐƯỢC HỎI
--------------------------------------------------------------------
- Context Gathering TRƯỚC khi code: Glob/Grep/Read song song để hiểu
  hệ thống hiện tại. Không "đoán".
- Parallel tool calls khi độc lập; sequential khi phụ thuộc.
- Khi sửa file: KHÔNG xóa import trước khi xóa usage. Prefer Edit
  over Write khi chỉ thay 1 đoạn.
- Mọi thay đổi logic / schema → note lại vào changelog của 06 và
  tick `[x]/[~]` trong 07.
- Commit message tiếng Việt hoặc Anh đều được, body nên liệt kê
  files bị đụng + lý do.
- Nếu phát hiện docs vs code mâu thuẫn → hỏi user "Dùng code hay
  dùng docs làm nguồn sự thật?" rồi mới sửa.

Giờ tôi sẽ chờ yêu cầu cụ thể của user. Đừng code trước khi user
nói rõ muốn làm gì.

=== END ===
```

---

## HƯỚNG DẪN BẢO TRÌ FILE NÀY

1. **Sau mỗi phiên dài, update section 10 "TRẠNG THÁI HIỆN TẠI"**:
   - Di chuyển item `[~]` → `[x]` khi hoàn thành.
   - Thêm entry "Fix đang chờ đồng bộ vào docs" khi kết thúc phiên
     nhưng chưa kịp update docs 00-07.
2. **Khi thêm pattern lỗi mới đã fix**: append vào section 7 "5 pattern
   form-POST bắt buộc" với nguyên tắc "lỗi gốc → fix → rule".
3. **Khi stack thay đổi (nâng Spring Boot, thêm dependency lớn)**:
   update section 1 "STACK & RUNTIME".
4. **Không đưa secret vào đây** (password, API key). Chỉ giữ test
   account placeholder.

---

## CÁCH DÙNG TRONG PRACTICE

**Chat mới, câu 1:**
```
Tôi muốn tiếp tục project QuanLyCTDTDB. Đây là context:

<PASTE nguyên block giữa === BEGIN === và === END === >

Bây giờ tôi muốn: <yêu cầu thực tế, vd "Build Phase 4 Danh Gia">
```

**Chat dài (đã có pattern riêng của chat đó):**
Không cần paste lại prompt — AI đã có context. Chỉ nói yêu cầu.

**Khi chuyển máy / chuyển team:**
Gửi kèm:
- File này (`docs/AI_ONBOARDING_PROMPT.md`)
- File `docs/00_MASTER_REFERENCE.md`
- Link repo GitHub
là đủ để AI mới hoặc dev mới onboard.
