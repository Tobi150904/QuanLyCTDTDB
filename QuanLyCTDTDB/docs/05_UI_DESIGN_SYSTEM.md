# UI/UX DESIGN SYSTEM — He Thong Quan Ly Dao Tao Xuat Sac
# Doc truoc khi code bat ky template Thymeleaf nao

---

## 1. IDENTITY & TONE

- He thong noi bo truong dai hoc — tone chinh thuc, tin cay, sach se
- Khong playful, khong dark mode, khong gradient loang
- Nguoi dung chinh: can bo, giang vien, sinh vien — uu tien legibility va efficiency
- Nguyen tac: 3 click toi da de hoan thanh 1 nghiep vu chinh

---

## 2. MAU SAC (COLOR PALETTE) — Chi dung 5 mau nay

```
--primary:      #1e3a5f   (Navy Blue - brand chinh, navbar, header card)
--primary-lt:   #2d5f9e   (Blue nhat hon, hover state, active link)
--accent:       #e8a020   (Amber - badge, highlight, button warning action)
--surface:      #f4f6f9   (Background trang chinh, body)
--surface-card: #ffffff   (Card, modal, sidebar)

Neutral (tu Bootstrap, khong can override):
  Text chinh:   #212529
  Text phu:     #6c757d
  Border:       #dee2e6
  Danger:       #dc3545  (xoa, tu choi, canh bao do)
  Success:      #198754  (da duyet, hoan thanh)
  Warning:      #e8a020  (= --accent, cho duyet)
```

**Quy tac mau:**
- Navbar + sidebar header: --primary (#1e3a5f)
- Page title border-left accent: --accent (#e8a020)
- Dashboard card icon: dung text-primary (--primary-lt override)
- Button chinh: btn-primary (override = --primary)
- KHONG dung bg-primary Bootstrap default (#0d6efd) truc tiep — phai qua CSS variable

---

## 3. TYPOGRAPHY

Font: 'Inter', 'Segoe UI', system-ui  (Inter load tu Google Fonts trong base.html)

```
Page title (h4/h5):    600 weight, #1e3a5f
Section header (h6):   600 weight, uppercase, #6c757d, letter-spacing 0.5px
Body text:             400 weight, 0.9rem, line-height 1.6
Table header:          500 weight, 0.78rem, uppercase, #6c757d
Badge / tag text:      500 weight, 0.75rem
Button text:           500 weight, 0.875rem
```

---

## 4. LAYOUT STRUCTURE — BAT BUOC

```
+--------------------------------------------------+
| NAVBAR (fixed-top, --primary, height 56px)       |
+----------+---------------------------------------+
| SIDEBAR  | MAIN CONTENT AREA                     |
| 240px    |  - Breadcrumb                         |
| (fixed)  |  - Page Header (title + action btn)  |
|          |  - Content (card, table, form)        |
|          |  - Pagination / footer note           |
+----------+---------------------------------------+
```

**Sidebar**: hien thi menu doc theo role, active state ro rang
**Khong dung top navbar cho navigation chinh** — chi dung navbar cho brand + user info + logout
**Content area**: max-width khong co, full width tru 240px sidebar
**Mobile**: sidebar an thanh offcanvas khi < 992px

---

## 5. COMPONENT RULES

### 5.1 Page Header (bat buoc tren moi trang)
```html
<div class="page-header">
  <div>
    <div class="page-header-label">Module / Phan</div>
    <h4 class="page-header-title">Ten Trang</h4>
  </div>
  <div class="page-header-actions">
    <!-- Button them moi, export... -->
  </div>
</div>
```

### 5.2 Cards
- border-radius: 10px, border: none, box-shadow: 0 1px 4px rgba(0,0,0,0.08)
- card-header: background --primary, color white, border-radius 10px 10px 0 0
- KHONG dung card mau sac random (bg-success, bg-warning truc tiep tren card header)

### 5.3 Tables
- Luon dung .table .table-hover .table-bordered .align-middle
- Header row: background #f8f9fa, font 0.78rem uppercase
- Action buttons: group trong 1 cell, chi dung icon + tooltip, khong dung text dai
- Moi trang toi da 20 dong, co pagination
- Luon co o search/filter phia tren table

### 5.4 Forms
- Dung form-floating cho tat ca input don (text, select, date)
- Dung form-label + form-control cho input phuc tap (textarea, file)
- Required field: them * do sau label
- Validate inline: invalid-feedback ngay duoi field, khong dung alert chung
- Submit button: luon o cuoi, full-width tren mobile
- Form card: max-width 800px, centered

### 5.5 Buttons
```
Primary action  : btn btn-primary      (luu, tao moi)
Secondary action: btn btn-outline-secondary (huy, quay lai)
Danger action   : btn btn-outline-danger   (xoa — phai co confirm)
Approve action  : btn btn-success          (phe duyet)
Reject action   : btn btn-outline-danger   (tu choi)
Table action    : btn btn-sm btn-outline-* (icon only + tooltip)
```
- KHONG dung btn khong co class ro rang
- Button xoa PHAI co confirm dialog truoc khi submit

### 5.6 Badges / Status Pills
```
Trang Thai        Class                     Mau
-----------       -----                     ---
Ban Nhap          badge bg-secondary        Xam
Cho Duyet         badge bg-warning text-dark Vang
Da Duyet          badge bg-success          Xanh la
Tu Choi           badge bg-danger           Do
Dang Mo           badge bg-primary          Xanh duong
Da Dong           badge bg-dark             Den
```
Dung Thymeleaf utility class trong 05_UI_DESIGN_SYSTEM.md section 8 de set dong

### 5.7 Alerts / Flash Messages
- LUON hien thi o dau content area, truoc page header
- Auto dismiss sau 4 giay (JS trong main.js)
- 4 loai: success (xanh), danger (do), warning (vang), info (xanh nhat)
- Icon phai co truoc text

### 5.8 Empty State
- Khi table trong, hien thi empty state card: icon lon + text mo ta + button action
- KHONG de table trong hoan toan

### 5.9 Loading
- Khi submit form: disable button + hien spinner (dung showLoading() trong main.js)
- Khong de nguoi dung bam 2 lan

### 5.10 Modals
- Dung cho: confirm xoa, xem nhanh chi tiet, form nho (1-3 field)
- KHONG dung modal cho form lon (> 5 field) — dung trang rieng
- Footer modal: button chinh ben phai, cancel ben trai

---

## 6. SIDEBAR STRUCTURE

```html
<!-- Sidebar item don gian -->
<li class="sidebar-item">
  <a class="sidebar-link" th:href="@{/url}"
     th:classappend="${activeMenu == 'ten-menu'} ? 'active' : ''">
    <i class="bi bi-icon-name sidebar-icon"></i>
    <span>Ten Menu</span>
  </a>
</li>

<!-- Sidebar item co submenu -->
<li class="sidebar-item">
  <a class="sidebar-link sidebar-toggle collapsed" href="#submenu-id"
     data-bs-toggle="collapse">
    <i class="bi bi-icon-name sidebar-icon"></i>
    <span>Ten Menu</span>
    <i class="bi bi-chevron-down ms-auto toggle-arrow"></i>
  </a>
  <div class="collapse" id="submenu-id">
    <ul class="sidebar-sub">
      <li><a th:href="@{/url}">Sub item</a></li>
    </ul>
  </div>
</li>
```

**Active state**: Controller phai truyen `model.addAttribute("activeMenu", "ten-menu")` vao tat ca GET handler

---

## 7. BREADCRUMB — Bat buoc tren moi trang (tru dashboard)

```html
<nav aria-label="breadcrumb">
  <ol class="breadcrumb">
    <li class="breadcrumb-item">
      <a th:href="@{/dashboard}">Trang chu</a>
    </li>
    <li class="breadcrumb-item">
      <a th:href="@{/nguoi-dung}">Nguoi Dung</a>
    </li>
    <li class="breadcrumb-item active">Chi Tiet</li>
  </ol>
</nav>
```

---

## 8. THYMELEAF HELPERS (copy vao moi template can dung)

### Badge trang thai CTDT
```html
<span th:with="tt=${ctdt.trangThai}"
      th:class="${tt.name() == 'DA_DUYET'} ? 'badge bg-success' :
                (${tt.name() == 'CHO_DUYET'} ? 'badge bg-warning text-dark' :
                (${tt.name() == 'TU_CHOI'} ? 'badge bg-danger' : 'badge bg-secondary'))"
      th:text="${tt.moTa}">
</span>
```

### Nut xoa co confirm
```html
<form th:action="@{/url/{id}/xoa(id=${item.id})}" method="post"
      onsubmit="return confirmDelete(event, 'Ten item nay')">
  <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
  <button type="submit" class="btn btn-sm btn-outline-danger"
          data-bs-toggle="tooltip" title="Xoa">
    <i class="bi bi-trash"></i>
  </button>
</form>
```

### Format ngay thang (Thymeleaf)
```html
<!-- LocalDate -->
<td th:text="${#temporals.format(item.ngayTao, 'dd/MM/yyyy')}"></td>
<!-- LocalDateTime -->
<td th:text="${#temporals.format(item.ngayTao, 'dd/MM/yyyy HH:mm')}"></td>
```

### Phan trang chuan
```html
<nav th:if="${page.totalPages > 1}">
  <ul class="pagination pagination-sm justify-content-end mb-0">
    <li class="page-item" th:classappend="${page.first} ? 'disabled'">
      <a class="page-link" th:href="@{/url(page=${page.number - 1}, size=${page.size})}">
        <i class="bi bi-chevron-left"></i>
      </a>
    </li>
    <li class="page-item" th:each="i : ${#numbers.sequence(0, page.totalPages - 1)}"
        th:classappend="${i == page.number} ? 'active'">
      <a class="page-link"
         th:href="@{/url(page=${i}, size=${page.size})}"
         th:text="${i + 1}">1</a>
    </li>
    <li class="page-item" th:classappend="${page.last} ? 'disabled'">
      <a class="page-link" th:href="@{/url(page=${page.number + 1}, size=${page.size})}">
        <i class="bi bi-chevron-right"></i>
      </a>
    </li>
  </ul>
</nav>
```

---

## 9. NAMING CONVENTION CHO MODEL ATTRIBUTES (Controller)

| Attribute      | Kieu             | Mo ta                              |
|----------------|------------------|------------------------------------|
| `page`         | Page<T>          | Ket qua phan trang                 |
| `item`         | Entity/DTO       | Object dang xem/sua chi tiet       |
| `listX`        | List<T>          | Danh sach cho select option        |
| `successMsg`   | String           | Flash message thanh cong           |
| `errorMsg`     | String           | Flash message that bai             |
| `warningMsg`   | String           | Flash message canh bao             |
| `activeMenu`   | String           | Ten menu dang active trong sidebar |
| `pageTitle`    | String           | Tieu de trang (breadcrumb)         |
| `keyword`      | String           | Gia tri tim kiem hien tai          |

---

## 10. DO / DON'T — Kiem tra truoc khi commit

DO:
- Moi template extends layout/base.html
- Moi trang co breadcrumb (tru login, dashboard)
- Moi table co empty state khi rong
- Moi form co CSRF token
- Moi nut xoa co confirm
- activeMenu truyen tu controller
- Trang thai hien thi bang badge dung mau

DON'T:
- Khong dung inline style (tat ca vao main.css)
- Khong dung mau Bootstrap default truc tiep (phai qua CSS variable)
- Khong de text "null", "undefined", "--" hien ra nguoi dung
- Khong dung alert() cua JS thuan — phai dung confirmDelete() hoac modal
- Khong mix icon tu nhieu library (chi dung Bootstrap Icons bi bi-*)
- Khong dat button submit trong div rieng bi floating giua trang
