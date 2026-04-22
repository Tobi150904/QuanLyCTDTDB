# 05_UI_DESIGN_SYSTEM — He Thong Quan Ly Dao Tao Xuat Sac

> Doc truoc khi code bat ky template Thymeleaf nao.
> Moi thay doi design PHAI cap nhat file nay truoc.

---

## 1. IDENTITY & TONE

- He thong noi bo truong dai hoc, tone chinh thuc, tin cay, sach se
- Khong playful, khong dark mode, khong gradient loang
- Nguoi dung chinh: can bo, giang vien, sinh vien — uu tien legibility va efficiency
- Nguyen tac: 3 click toi da de hoan thanh 1 nghiep vu chinh

---

## 2. MAU SAC (COLOR PALETTE) — Chi dung 5 mau chinh nay

```css
:root {
    --primary:         #1e3a5f;  /* Navy Blue — navbar, sidebar header, card header chinh */
    --primary-600:     #25466f;  /* Hover cho button/link primary                         */
    --primary-lt:      #2d5f9e;  /* Blue nhat hon — active sidebar link                   */
    --accent:          #e8a020;  /* Amber — badge canh bao, border-left title, stat-card  */
    --surface:         #f4f6f9;  /* Background body, page chinh                           */
    --surface-card:    #ffffff;  /* Card, modal, sidebar body                             */
    --text-primary:    #111827;  /* Text chinh (hard black dju cho contrast tren surface) */
    --text-muted:      #6b7280;  /* Text phu                                              */
    --border-subtle:   #e5e7eb;  /* Border default                                        */
    --shadow-sm:       0 1px 2px 0 rgba(17,24,39,.05);
    --shadow-md:       0 4px 6px -1px rgba(17,24,39,.08), 0 2px 4px -2px rgba(17,24,39,.04);
    --radius-card:     10px;
    --radius-input:    8px;
    --transition:      .18s ease;
}

/* Neutral tu Bootstrap (khong override truc tiep, chi dung tokens tren): */
/* Danger: #dc3545   Success: #198754   Warning: #ffc107   Info: #0dcaf0 */
```

**Tokens v2 them trong `main.css`:**
- `--primary-600` (hover primary), `--text-primary`/`--text-muted` (thay dung truc tiep `#212529`/`#6c757d`)
- `--border-subtle`, `--shadow-sm`, `--shadow-md` (thay `shadow-sm` inline Bootstrap de dong nhat)
- `--radius-card` va `--radius-input` (thay inline `border-radius: 10px`)
- `--transition` (hover/focus nhat quan 180ms)

**Quy tac bat buoc:**
- Navbar + sidebar header background: `--primary`
- Page title `border-left: 3px solid var(--accent)` + `padding-left: 12px`
- Dashboard card icon: `color: var(--primary-lt)`
- `btn-primary` phai override sang `--primary` (xem main.css)
- KHONG dung `bg-primary` Bootstrap default `(#0d6efd)` truc tiep

---

## 3. TYPOGRAPHY

Font stack: `'Inter', 'Segoe UI', system-ui, sans-serif`
(Inter load tu Google Fonts trong `templates/layout/base.html`)

| Phan tu               | Font size | Font weight | Mau          | Ghi chu               |
|-----------------------|-----------|-------------|--------------|------------------------|
| Page title (h4)       | 1.2rem    | 600         | `--primary`  | border-left accent     |
| Section header (h6)   | 0.78rem   | 600         | `#6c757d`    | uppercase, ls 0.5px    |
| Body text             | 0.9rem    | 400         | `#212529`    | line-height 1.6        |
| Table header          | 0.78rem   | 500         | `#6c757d`    | uppercase              |
| Badge / tag           | 0.75rem   | 500         | (theo loai)  |                        |
| Button text           | 0.875rem  | 500         |              |                        |
| Label form            | 0.875rem  | 500         | `#212529`    |                        |

---

## 4. LAYOUT STRUCTURE — BAT BUOC

```
+--------------------------------------------------+
| NAVBAR (fixed-top, --primary, height 56px)        |
|  [Brand: He Thong QLDTXS]         [User][Logout] |
+----------+---------------------------------------+
| SIDEBAR  | MAIN CONTENT AREA                     |
| 240px    |  padding: 24px                        |
| fixed    |  +-- Breadcrumb (tru dashboard/login) |
|          |  +-- Page Header (title + action btn) |
|          |  +-- Flash Message (neu co)           |
|          |  +-- Content (card, table, form)      |
|          |  +-- Pagination (neu can)             |
+----------+---------------------------------------+
```

- Sidebar: hien thi menu theo role (sec:authorize), active state ro rang
- KHONG dung top navbar cho navigation chinh
- Navbar chi chua: brand name, ten nguoi dung, logout
- Content area: `margin-left: 240px` tren desktop
- Mobile (< 992px): sidebar chuyen sang offcanvas

---

## 5. COMPONENT RULES

### 5.1 Page Header (bat buoc tren moi trang, tru login)
```html
<div class="d-flex justify-content-between align-items-start mb-4">
    <div>
        <p class="text-muted mb-1" style="font-size:0.78rem; text-transform:uppercase; letter-spacing:0.5px;">
            Module / Phan
        </p>
        <h4 class="mb-0 fw-semibold"
            style="color: var(--primary); border-left: 3px solid var(--accent); padding-left: 12px;">
            Ten Trang
        </h4>
    </div>
    <div class="d-flex gap-2">
        <!-- Button them moi, export -->
    </div>
</div>
```

### 5.2 Cards
```html
<div class="card border-0 shadow-sm" style="border-radius: 10px;">
    <div class="card-header text-white"
         style="background: var(--primary); border-radius: 10px 10px 0 0;">
        <span class="fw-semibold">Tieu de card</span>
    </div>
    <div class="card-body">
        <!-- noi dung -->
    </div>
</div>
```
- KHONG dung card-header mau sac tuy tien (bg-success, bg-warning truc tiep)
- Dashboard stat card: dung `border-left: 4px solid var(--accent)`

### 5.3 Tables
```html
<div class="table-responsive">
    <table class="table table-hover table-bordered align-middle mb-0">
        <thead style="background: #f8f9fa;">
            <tr>
                <th style="font-size:0.78rem; text-transform:uppercase; color:#6c757d;">STT</th>
                <!-- cac cot khac -->
                <th class="text-center">Thao Tac</th>
            </tr>
        </thead>
        <tbody>
            <!-- du lieu hoac empty state -->
        </tbody>
    </table>
</div>
```
- Moi trang toi da 20 dong + pagination
- Luon co o search/filter phia tren table
- Cot "Thao Tac": chi dung icon button + tooltip, KHONG dung text dai
- Neu khong co du lieu: hien empty state (xem muc 5.8)

### 5.4 Forms
```html
<!-- Input don (text, email, date, select) — dung form-floating -->
<div class="form-floating mb-3">
    <input type="text" class="form-control" id="hoTen" name="hoTen"
           th:value="${item?.hoTen}" placeholder="Ho Ten" required>
    <label for="hoTen">Ho Ten <span class="text-danger">*</span></label>
    <div class="invalid-feedback">Vui long nhap ho ten.</div>
</div>

<!-- Textarea — dung form-label thuong -->
<div class="mb-3">
    <label for="ghiChu" class="form-label fw-medium">Ghi Chu</label>
    <textarea class="form-control" id="ghiChu" name="ghiChu" rows="3"
              th:text="${item?.ghiChu}"></textarea>
</div>

<!-- File upload -->
<div class="mb-3">
    <label for="file" class="form-label fw-medium">File Dinh Kem</label>
    <input type="file" class="form-control" id="file" name="file"
           accept=".pdf,.doc,.docx,.xlsx,.xls">
    <div class="form-text">Dinh dang: PDF, DOC, DOCX, XLSX. Toi da 20MB.</div>
</div>
```
- Field bat buoc: them `*` mau do sau label
- Validate inline: `invalid-feedback` ngay duoi field
- Form card: `max-width: 800px`, centered
- Submit button: o cuoi form, `btn btn-primary`

### 5.5 Buttons
| Muc dich              | Class                           | Ghi chu                        |
|-----------------------|---------------------------------|--------------------------------|
| Luu / Tao moi         | `btn btn-primary`               | Mau override = --primary       |
| Huy / Quay lai        | `btn btn-outline-secondary`     |                                |
| Xoa                   | `btn btn-outline-danger`        | PHAI co confirm truoc submit   |
| Phe duyet             | `btn btn-success`               |                                |
| Tu choi               | `btn btn-outline-danger`        |                                |
| Action trong table    | `btn btn-sm btn-outline-*`      | Icon only + tooltip            |
| Nop len duyet         | `btn btn-warning text-dark`     |                                |
| Export Excel          | `btn btn-outline-success`       | icon bi-file-earmark-excel     |
| Import Excel          | `btn btn-outline-primary`       | icon bi-upload                 |

### 5.6 Badge Trang Thai
| Trang thai              | Class Bootstrap                   |
|-------------------------|-----------------------------------|
| BanNhap / ChuanBi       | `badge bg-secondary`              |
| ChoDuyet                | `badge bg-warning text-dark`      |
| DaDuyet / DaThucHien    | `badge bg-success`                |
| TuChoi / DaHuy          | `badge bg-danger`                 |
| DangMo / DangDienRa     | `badge bg-primary`                |
| DaDong / DaKetThuc      | `badge bg-dark`                   |
| DangThucTap             | `badge bg-info text-dark`         |
| DaphanCong              | `badge bg-secondary`              |

### 5.7 Flash Messages (Alerts)
- Dat TREN page header (sau breadcrumb)
- Auto-dismiss sau 4 giay bang `main.js`
- 4 loai: `alert-success`, `alert-danger`, `alert-warning`, `alert-info`
- Bat buoc co icon Bootstrap Icons truoc text

```html
<!-- Trong base.html, truyen qua model attributes -->
<div th:if="${successMsg}" class="alert alert-success alert-dismissible fade show auto-dismiss" role="alert">
    <i class="bi bi-check-circle-fill me-2"></i>
    <span th:text="${successMsg}"></span>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<div th:if="${errorMsg}" class="alert alert-danger alert-dismissible fade show auto-dismiss" role="alert">
    <i class="bi bi-exclamation-triangle-fill me-2"></i>
    <span th:text="${errorMsg}"></span>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
```

### 5.8 Empty State (bat buoc khi table trong)
```html
<tr th:if="${#lists.isEmpty(items)}">
    <td th:colspan="6" class="text-center py-5">
        <i class="bi bi-inbox display-4 text-muted d-block mb-3"></i>
        <p class="text-muted mb-3">Chua co du lieu nao.</p>
        <a th:href="@{/url/them}" class="btn btn-primary">
            <i class="bi bi-plus-lg me-1"></i> Them Moi
        </a>
    </td>
</tr>
```

### 5.9 Loading / Submit protection
```html
<button type="submit" class="btn btn-primary" onclick="showLoading(this)">
    <span class="spinner-border spinner-border-sm d-none me-1" role="status"></span>
    <i class="bi bi-save me-1"></i> Luu
</button>
```
- `showLoading()` trong `main.js`: disable button, hien spinner, an icon
- Tranh nguoi dung bam 2 lan

### 5.11 Utility Classes CHUNG (bat buoc dung thay vi inline style)

Da dang ky trong `src/main/resources/static/css/main.css`. Cac template muon dung lai style chung PHAI
reuse cac class sau thay vi dat inline `style="..."`:

| Class                    | Tac dung                                                              |
|--------------------------|-----------------------------------------------------------------------|
| `.page-title`            | H4 primary color + border-left accent + padding-left 14px             |
| `.page-subtitle`         | Kicker/label nho phia tren title (`Module / Phan`)                    |
| `.stat-card`             | Stat card base (card + shadow + border-left accent + hover translateY)|
| `.stat-card-icon`        | Icon tron 44x44 tren trai, mac dinh mau `var(--primary-lt)`           |
| `.stat-card-value`       | So lieu lon (1.75rem 700 primary)                                     |
| `.stat-card-label`       | Label nho duoi so (uppercase muted 0.75rem)                           |
| `.info-row` + `.info-label` | Row trong table chi-tiet: label trai 38% muted, value phai         |
| `.empty-state`           | Container empty state (icon + text + CTA)                             |
| `.sidebar-section-header`| Ke section trong sidebar (uppercase, muted)                           |
| `.search-bar`            | Wrapper input-group cho o tim kiem co focus border unified            |
| `.soft-divider`          | 1px divider mau subtle                                                |

**Variant tai cho (scoped trong trang):**
Cac variant mau cho stat-card (success/warning/danger/info/teal/purple/orange) hien dang khai
bao local trong `<style>` cua `dashboard.html` dang `.stat-card.variant-success .stat-card-icon {...}`.
Khi reuse sang module khac, copy khoi `<style>` nay vao trang danh sach cua module do, HOAC nang
len `main.css` neu dung o >= 3 trang.

**Login page:** Login co layout rieng, su dung `<style>` scoped trong `auth/login.html` voi class
`login-shell`, `brand-panel`, `form-panel`, `brand-feature-list`, `btn-login` — khong anh huong toi
cac trang co layout khac.

### 5.10 Modals
- Dung cho: confirm xoa, xem nhanh chi tiet, form nho (1-3 field)
- KHONG dung modal cho form lon (> 5 field) — dung trang rieng
- Footer: button chinh (btn-danger hoac btn-primary) ben phai, "Huy" ben trai

---

## 6. SIDEBAR STRUCTURE

```html
<!-- Sidebar item don gian -->
<li class="nav-item">
    <a class="nav-link"
       th:href="@{/url}"
       th:classappend="${activeMenu == 'ten-menu'} ? ' active' : ''">
        <i class="bi bi-icon-name me-2"></i>
        <span>Ten Menu</span>
    </a>
</li>

<!-- Sidebar item co submenu -->
<li class="nav-item">
    <a class="nav-link d-flex align-items-center"
       href="#submenu-id" data-bs-toggle="collapse"
       th:classappend="${activeMenu != null and activeMenu.startsWith('ten-module')} ? '' : ' collapsed'">
        <i class="bi bi-icon-name me-2"></i>
        <span>Ten Menu</span>
        <i class="bi bi-chevron-down ms-auto"></i>
    </a>
    <div class="collapse" id="submenu-id"
         th:classappend="${activeMenu != null and activeMenu.startsWith('ten-module')} ? ' show' : ''">
        <ul class="nav flex-column ps-4">
            <li class="nav-item">
                <a class="nav-link" th:href="@{/url}">Sub item</a>
            </li>
        </ul>
    </div>
</li>
```

**Menu items theo role (sec:authorize):**
```
Admin/PDT/TTDTXS:  Dashboard, Nguoi Dung, Doanh Nghiep, Hoc Ky
BCN/CNHP:          Hoc Phan, CTDT, Lop Hoc Phan
GV:                Lop Hoc Phan (cua toi), Tai Lieu, Danh Gia
CVHT:              Canh Bao SV
SV:                Lich Hoc, Ket Qua
DN:                Kien Tap, Thuc Tap (cua toi)
PDT/TTDTXS:        Kien Tap, Thuc Tap, Bao Cao
```

**Bat buoc:** Controller truyen `model.addAttribute("activeMenu", "ten-menu")` tren moi GET handler.

---

## 7. BREADCRUMB (bat buoc tren moi trang tru login va dashboard)

```html
<nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb">
        <li class="breadcrumb-item">
            <a th:href="@{/dashboard}" class="text-decoration-none">
                <i class="bi bi-house-door me-1"></i>Trang chu
            </a>
        </li>
        <li class="breadcrumb-item">
            <a th:href="@{/nguoi-dung}" class="text-decoration-none">Nguoi Dung</a>
        </li>
        <li class="breadcrumb-item active" aria-current="page">Chi Tiet</li>
    </ol>
</nav>
```

---

## 8. THYMELEAF HELPERS (copy vao template khi can)

> Luu y: cac enum Java cua project dung PascalCase (`DaDuyet`, `ChoDuyet`, `ChuanBi`...),
> KHONG phai UPPER_SNAKE_CASE. `enum.name()` tra ve dung ten Java (`"DaDuyet"`).
> Moi comparison Thymeleaf phai dung chuoi PascalCase de khop.

### Badge trang thai CTDT / HocPhan (TrangThaiCTDT, TrangThaiHocPhan)
```html
<span th:with="tt=${ctdt.trangThai}"
      th:class="${tt.name() == 'DaDuyet'} ? 'badge bg-success' :
               (${tt.name() == 'ChoDuyet'} ? 'badge bg-warning text-dark' :
               (${tt.name() == 'DaHuy'} ? 'badge bg-danger' : 'badge bg-secondary'))"
      th:text="${tt.name()}">
</span>
```

### Badge trang thai DotKienTap (TrangThaiDotKT)
```html
<span th:with="tt=${dot.trangThai}"
      th:class="${tt.name() == 'DaDuyet'} ? 'badge bg-info text-dark' :
               (${tt.name() == 'DaThucHien'} ? 'badge bg-success' :
               (${tt.name() == 'ChoDuyet'} ? 'badge bg-warning text-dark' :
               (${tt.name() == 'DaHuy'} ? 'badge bg-danger' : 'badge bg-secondary')))"
      th:text="${tt.name()}">
</span>
```

### Badge trang thai DotThucTap (TrangThaiDotTT)

> Enum co 6 gia tri: `ChuanBi`, `ChoDuyet`, `DaDuyet`, `DangThucHien`, `DaKetThuc`, `DaHuy`.
> `DaHuy` PHAI hien thi mau do (`bg-danger`) de canh bao nguoi dung — dong nhat
> voi badge `TrangThaiCTDT` va `TrangThaiDotKT`. Fallback `bg-light` danh cho
> trang thai `ChuanBi` (moi tao, chua chay).

```html
<span th:with="tt=${dot.trangThai}"
      th:class="${tt.name() == 'DaHuy'} ? 'badge bg-danger' :
               (${tt.name() == 'DaKetThuc'} ? 'badge bg-secondary' :
               (${tt.name() == 'DangThucHien'} ? 'badge bg-success' :
               (${tt.name() == 'DaDuyet'} ? 'badge bg-info text-dark' :
               (${tt.name() == 'ChoDuyet'} ? 'badge bg-warning text-dark' : 'badge bg-light text-dark'))))"
      th:text="${tt.name()}">
</span>
```

### Badge DaThamGia (cho bang DanhSachSinhVienKienTap)
```html
<span th:if="${row.daThamGia}" class="badge bg-success">Tham gia</span>
<span th:unless="${row.daThamGia}" class="badge bg-warning text-dark">Khong tham gia</span>
```

### Nut xoa co confirm
```html
<form th:action="@{/url/{ma}/xoa(ma=${item.ma})}" method="post"
      onsubmit="return confirmDelete(this, event, 'ten item nay')">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
    <button type="submit" class="btn btn-sm btn-outline-danger"
            data-bs-toggle="tooltip" data-bs-title="Xoa">
        <i class="bi bi-trash3"></i>
    </button>
</form>
```

### Format ngay thang (Thymeleaf 3 dung #temporals)
```html
<td th:text="${item.ngayTao != null ? #temporals.format(item.ngayTao, 'dd/MM/yyyy') : '--'}"></td>
<td th:text="${item.thoiGian != null ? #temporals.format(item.thoiGian, 'dd/MM/yyyy HH:mm') : '--'}"></td>
```

### Phan trang chuan
```html
<nav th:if="${page != null and page.totalPages > 1}" class="mt-3">
    <ul class="pagination pagination-sm justify-content-end mb-0">
        <li class="page-item" th:classappend="${page.first} ? 'disabled'">
            <a class="page-link" th:href="@{/url(page=${page.number - 1}, size=${page.size}, keyword=${keyword})}">
                <i class="bi bi-chevron-left"></i>
            </a>
        </li>
        <li class="page-item" th:each="i : ${#numbers.sequence(0, page.totalPages - 1)}"
            th:classappend="${i == page.number} ? 'active'">
            <a class="page-link"
               th:href="@{/url(page=${i}, size=${page.size}, keyword=${keyword})}"
               th:text="${i + 1}">1</a>
        </li>
        <li class="page-item" th:classappend="${page.last} ? 'disabled'">
            <a class="page-link" th:href="@{/url(page=${page.number + 1}, size=${page.size}, keyword=${keyword})}">
                <i class="bi bi-chevron-right"></i>
            </a>
        </li>
    </ul>
</nav>
```

### CSRF hidden input (bat buoc trong moi form POST)
```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
```

### sec:authorize theo LoaiNguoiDung va VaiTro
```html
<!-- Hien chi voi PDT hoac TTDTXS -->
<div sec:authorize="hasAnyRole('PDT','TTDTXS')">...</div>
<!-- Hien chi voi GiangVien -->
<div sec:authorize="hasRole('GIANG_VIEN')">...</div>
<!-- Hien chi voi Admin -->
<div sec:authorize="hasRole('ADMIN')">...</div>
```

---

## 9. MODEL ATTRIBUTES QUY UOC (Controller -> Template)

| Attribute       | Kieu             | Mo ta                                          |
|-----------------|------------------|------------------------------------------------|
| `page`          | Page<T>          | Ket qua phan trang                             |
| `item`          | Entity/DTO       | Object dang xem/sua chi tiet                   |
| `items`         | List<T>          | Danh sach don gian (khong phan trang)          |
| `listX`         | List<T>          | Danh sach cho option select (listHocPhan, ...) |
| `successMsg`    | String           | Flash message thanh cong (RedirectAttributes)  |
| `errorMsg`      | String           | Flash message that bai  (RedirectAttributes)   |
| `warningMsg`    | String           | Flash message canh bao  (RedirectAttributes)   |
| `activeMenu`    | String           | Ten menu dang active trong sidebar             |
| `keyword`       | String           | Gia tri tim kiem hien tai (giu trang thai)     |
| `currentUser`   | CustomUserDetails| Nguoi dung dang dang nhap (truyen tu Security) |

---

## 10. DO / DON'T

### DO
- Moi template extends `layout/base.html` (Thymeleaf Layout Dialect: `layout:decorate`)
- Moi trang co breadcrumb (tru login va dashboard)
- Moi table co empty state khi khong co du lieu
- Moi form POST co CSRF token
- Moi nut xoa co confirm truoc khi submit
- `activeMenu` truyen tu controller trong moi GET handler
- Trang thai hien bang badge dung mau theo bang o muc 5.6
- LUON escape noi dung nguoi nhap bang `th:text` (khong dung `th:utext` tru khi tin tuong)
- File upload: kiem tra extension va kich thuoc o ca frontend (accept) va backend (service)

### DON'T
- Khong dung inline style (tat ca vao `src/main/resources/static/css/main.css`)
- Khong dung mau Bootstrap default truc tiep (phai qua CSS variable)
- Khong de text "null", "undefined" hien ra nguoi dung
- Khong dung `alert()` JS thuan — phai dung `confirmDelete()` hoac modal Bootstrap
- Khong mix icon tu nhieu library (chi dung Bootstrap Icons `bi bi-*`)
- Khong dat button submit floating giua trang
- Khong dung `@ResponseBody` trong Controller MVC (chi dung return view name / redirect)
- Khong de LazyInitializationException xay ra trong view (dung @Transactional o service, hoac JOIN FETCH)
