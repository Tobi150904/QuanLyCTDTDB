# 05 · UI Design System — Hệ Thống Quản Lý Đào Tạo Xuất Sắc

> Tài liệu design-authority cho toàn bộ giao diện Thymeleaf của hệ thống.
> Bất kỳ thay đổi thị giác nào (màu sắc, typography, layout, component, tương tác)
> **phải được cập nhật ở file này TRƯỚC**, sau đó mới thực thi trong code. Mục tiêu:
> đảm bảo UX nhất quán, có tính kế thừa, và dễ bảo trì cho một sản phẩm nội bộ
> vận hành liên tục trong môi trường production của nhà trường.
>
> Stack áp dụng: Thymeleaf · Spring Security Extras · Bootstrap 5.3 · Bootstrap Icons ·
> Inter (Google Fonts) · CSS custom properties trong `static/css/main.css`.

---

## 1 · Design Principles

Hệ thống phục vụ cán bộ phòng Đào Tạo, trưởng Bộ môn, chủ nhiệm Học phần, Cố vấn
học tập, Giảng viên, Sinh viên và đại diện Doanh nghiệp. Giao diện cần **trung
tính về cảm xúc, dày dặn về thông tin, và nhanh trong thao tác**. 5 nguyên tắc nền
tảng:

1. **Clarity over cleverness** — Dùng ngôn ngữ hiển thị rõ ràng, không ẩn dụ.
   Mọi trạng thái phải được gắn nhãn. Không để người dùng phải đoán.
2. **3-click rule** — Mọi nghiệp vụ chính (mở lớp, phân công, phê duyệt, cảnh
   báo SV) phải đạt được trong tối đa 3 thao tác tính từ Dashboard.
3. **Density-first, whitespace-balanced** — Giao diện dành cho người dùng làm
   việc hàng ngày: ưu tiên hiển thị nhiều dòng/bảng trong một viewport, nhưng
   giữ padding dòng ≥ 10px để không mỏi mắt.
4. **Progressive disclosure** — Thông tin thứ cấp (tooltip, hint, modal xác
   nhận) chỉ xuất hiện khi cần. Form dài luôn được chia section.
5. **Predictable feedback loop** — Mọi action POST đều phải trả về flash
   message (`successMsg` / `errorMsg` / `warningMsg`) và/hoặc redirect về
   trang danh sách có trạng thái cập nhật. Không có thao tác "im lặng".

---

## 2 · Color System

Hệ thống chỉ dùng **5 tone chính** (primary, accent, surface, text, border), các
trạng thái bổ sung mượn từ Bootstrap nhưng luôn override qua CSS variable để
đồng bộ.

### 2.1 Design tokens

| Token             | Giá trị    | Vai trò                                                          |
|-------------------|------------|------------------------------------------------------------------|
| `--primary`       | `#1e3a5f`  | Brand primary — navbar, card header, button chính                |
| `--primary-lt`    | `#2d5f9e`  | Hover/link active, icon stat card, focus ring                    |
| `--primary-dk`    | `#172d4a`  | Gradient navbar, modal header                                    |
| `--accent`        | `#e8a020`  | Border-left title, border-left stat card, huy hiệu cảnh báo      |
| `--accent-lt`     | `#f4b944`  | Highlight icon thương hiệu trong navbar                          |
| `--surface`       | `#f4f6f9`  | Nền body, nền page                                               |
| `--surface-alt`   | `#eef2f7`  | Nền table head, code inline                                      |
| `--surface-card`  | `#ffffff`  | Nền card, modal, sidebar                                         |
| `--surface-hover` | `#f0f4fa`  | Nền hover sidebar link, hover row                                |
| `--text-main`     | `#212529`  | Văn bản chính                                                    |
| `--text-muted`    | `#6c757d`  | Văn bản phụ, label bảng                                          |
| `--text-subtle`   | `#8a94a6`  | Placeholder, icon empty state, metadata                          |
| `--border-color`  | `#e5e9f0`  | Đường kẻ mặc định                                                |
| `--border-strong` | `#d7dde6`  | Border input, scrollbar thumb                                    |
| `--radius-sm`     | `6px`      | Button, input, badge nhỏ                                         |
| `--radius-md`     | `10px`     | Card, modal, avatar lớn                                          |
| `--radius-lg`     | `14px`     | Dialog quan trọng, hero panel                                    |
| `--shadow-sm`     | —          | Shadow default cho card                                          |
| `--shadow-md`     | —          | Card hover, dropdown                                             |
| `--shadow-lg`     | —          | Modal, sidebar mobile                                            |
| `--transition`    | `180ms`    | Motion tiêu chuẩn (ease `cubic-bezier(0.4,0,0.2,1)`)             |

Trạng thái (semantic) lấy lại từ Bootstrap nhưng chuẩn hoá qua alert/badge:

| Ý nghĩa       | Màu mặc định | Lớp badge              | Lớp alert        |
|---------------|--------------|------------------------|------------------|
| Success       | `#198754`    | `badge bg-success`     | `alert-success`  |
| Info          | `#0dcaf0`    | `badge bg-info text-dark` | `alert-info` |
| Warning       | `#ffc107`    | `badge bg-warning text-dark` | `alert-warning` |
| Danger        | `#dc3545`    | `badge bg-danger`      | `alert-danger`   |

### 2.2 Usage rules (bắt buộc)

- **Chỉ được gán màu qua token** (`var(--primary)`), không hard-code HEX trong
  template hoặc inline style.
- Navbar + card header chính: nền `--primary` (hoặc gradient `--primary` →
  `--primary-dk` cho navbar/modal header).
- Page title: text `--primary`, `border-left: 3px solid var(--accent)`.
- Sidebar link `active`: nền `rgba(45, 95, 158, 0.10)`, text `--primary`, pseudo
  element `::before` 3px `--accent` làm chỉ thị.
- Stat card: `border-left: 4px solid var(--accent)`, icon `--primary-lt`.
- **Không dùng `bg-primary` mặc định của Bootstrap (`#0d6efd`)** — đã override
  trong `main.css` về `--primary`.
- **Không dùng gradient ngoài navbar và modal header** để tránh nhiễu thị giác.

---

## 3 · Typography

Font system: **Inter** (primary) + fallback hệ thống. Load qua Google Fonts
trong `templates/layout/base.html`.

```
font-family: 'Inter', 'Segoe UI', system-ui, sans-serif;
```

### 3.1 Type scale

| Role                  | Size        | Weight | Line-height | Color            | Ghi chú                               |
|-----------------------|-------------|--------|-------------|------------------|----------------------------------------|
| Page title (`.page-title`, h4) | 1.25 rem | 600 | 1.2         | `--primary`      | border-left accent + padding-left 14px |
| Page subtitle (`.page-subtitle`) | 0.72 rem | 600 | 1.2       | `--text-subtle`  | uppercase + letter-spacing 1px         |
| Card header           | 0.92 rem    | 500    | 1.3         | `#fff`           | trong header có nền primary            |
| Section label (h6, form group) | 0.78 rem | 600 | 1.3       | `--text-muted`   | uppercase + letter-spacing 0.5px       |
| Body text             | 0.9 rem     | 400    | 1.6         | `--text-main`    | mặc định body                          |
| Table header          | 0.72 rem    | 600    | 1.3         | `--text-muted`   | uppercase + nền `--surface-alt`        |
| Table body            | 0.875 rem   | 400    | 1.4         | `--text-main`    |                                        |
| Form label            | 0.875 rem   | 500    | 1.3         | `--text-main`    |                                        |
| Button                | 0.875 rem   | 500    | 1.2         | —                |                                        |
| Badge                 | 0.72 rem    | 600    | 1           | theo loại        | letter-spacing 0.3px                   |
| Inline code           | 0.78 rem    | 500    | 1           | `--primary-dk`   | nền `--surface-alt`, radius 6px        |

### 3.2 Quy ước viết

- **Tiêu đề** dùng Title Case tiếng Việt có dấu (`Danh Sách Lớp Học Phần`).
- **Label form** dùng capitalize đầu dòng (`Mã Sinh Viên`), kèm `*` màu danger
  khi bắt buộc.
- **Nội dung thoại tới người dùng** (flash message, empty state, tooltip):
  câu đầy đủ, kết thúc bằng dấu chấm.
- **Không dùng ALL CAPS** cho nội dung, chỉ dùng cho label nhỏ
  (section header, table header) kết hợp letter-spacing để dễ đọc.

---

## 4 · Layout Blueprint

```
┌────────────────────────────────────────────────────────────────┐
│ NAVBAR  (fixed-top · 60px · gradient primary)                  │
│  [Brand]                                         [User ▼]      │
├──────────┬─────────────────────────────────────────────────────┤
│ SIDEBAR  │ MAIN CONTENT                                        │
│ 248px    │  ├─ Breadcrumb  (mọi trang trừ login + dashboard)   │
│ fixed    │  ├─ Page Header (kicker + title + CTA)              │
│ surface  │  ├─ Flash Messages                                  │
│ -card    │  ├─ Filter / Toolbar                                │
│          │  ├─ Content (card · table · form · timeline)        │
│          │  └─ Pagination / Footer action                      │
└──────────┴─────────────────────────────────────────────────────┘
```

### 4.1 Quy định bắt buộc

- **Sidebar**: cố định trái 248 px trên desktop (`lg ≥ 992px`), chuyển thành
  **offcanvas** (transform translateX) trên mobile. Menu chia section theo role
  thông qua `sec:authorize`.
- **Navbar**: cao 60 px, chỉ chứa brand + avatar + dropdown (Hồ sơ, Đăng xuất).
  Không dùng navbar để điều hướng nghiệp vụ — navigation tập trung ở sidebar.
- **Main content**: padding 24 px, `margin-left: var(--sidebar-width)`, fade-in
  200 ms khi route chuyển trang.
- **Breadcrumb**: dưới navbar, ngay trên page header. Tối đa 3 cấp.
- **Page header**: kicker (module) + title (h4 `.page-title`) + cụm CTA bên
  phải (cách nhau `gap-2`).
- **Toolbar / Filter**: đặt trong 1 `card` riêng TRƯỚC bảng dữ liệu. Filter
  dạng combobox + search + button `Tra Cứu`.
- Mobile (< 992 px): sidebar trượt vào bằng overlay `.sidebar-overlay.show`.

---

## 5 · Component Library

Mọi component đều triển khai dưới dạng mẫu Thymeleaf / class utility trong
`main.css`. Khi cần tạo biến thể mới, **ưu tiên mở rộng class có sẵn** hơn viết
inline style.

### 5.1 Page Header

```html
<div class="d-flex justify-content-between align-items-start mb-4 flex-wrap gap-2">
    <div>
        <p class="page-subtitle">Module / Phân hệ</p>
        <h4 class="page-title mb-0">Tên Trang</h4>
    </div>
    <div class="d-flex gap-2">
        <a th:href="@{/module/them}" class="btn btn-primary">
            <i class="bi bi-plus-lg me-1"></i>Thêm Mới
        </a>
    </div>
</div>
```

### 5.2 Card

```html
<div class="card mb-4">
    <div class="card-header">
        <span class="fw-semibold">
            <i class="bi bi-card-list me-2"></i>Tiêu đề card
        </span>
    </div>
    <div class="card-body">
        ...
    </div>
</div>
```

- Card mặc định: nền trắng, border `--border-color`, shadow-sm, radius
  `--radius-md`, header gradient nhẹ `--primary`.
- **Sub-card** (card bên trong filter hoặc modal): override card-header
  `bg-white` (header trắng) để tránh xung đột thị giác.
- **Không dùng** `card-header bg-success`/`bg-warning` trực tiếp, nếu cần
  phân loại, dùng badge bên cạnh tiêu đề card.

### 5.3 Stat Card (dashboard + tổng quan module)

```html
<div class="stat-card">
    <div class="stat-card-icon"><i class="bi bi-mortarboard"></i></div>
    <div>
        <div class="stat-card-value">124</div>
        <div class="stat-card-label">Chương trình đào tạo</div>
    </div>
</div>
```

- Grid: `col-12 col-sm-6 col-xl-3` trên dashboard.
- Có thể bọc `<a>` bên ngoài để tạo card điều hướng (nhấn chuyển trang).
- Biến thể theo màu (success / warning / danger / info / teal / purple):
  class prefix `.stat-card.variant-*`. Nếu dùng ≥ 3 trang, **nâng lên
  `main.css`**; nếu dùng 1 trang, khai báo trong `<style>` cục bộ.

### 5.4 Table

```html
<div class="table-responsive">
    <table class="table table-hover table-bordered align-middle mb-0">
        <thead>
            <tr>
                <th style="width:50px;">STT</th>
                <th>Cột A</th>
                <th class="text-center">Trạng Thái</th>
                <th class="text-center" style="width:150px;">Thao Tác</th>
            </tr>
        </thead>
        <tbody>
            <tr th:if="${#lists.isEmpty(items)}">
                <td colspan="4" class="text-center py-5">...</td>
            </tr>
            <tr th:each="item, stat : ${items}">
                <td class="text-muted" th:text="${stat.index + 1}"></td>
                ...
            </tr>
        </tbody>
    </table>
</div>
```

- **Tối đa 20 dòng / trang** + pagination.
- Luôn có search/filter ở card phía trên table.
- Cột `Thao Tác` dùng icon-only button + tooltip, tránh chữ dài.
- Sticky header: áp dụng cho bảng dài > 40 dòng (màn hình tra cứu nhanh).
- Không hiển thị `null` / `undefined` — fallback `'--'`.

### 5.5 Form

```html
<form th:action="@{/url}" method="post" novalidate>
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">

    <div class="form-floating mb-3">
        <input type="text" class="form-control" id="hoTen" name="hoTen"
               th:value="${item?.hoTen}" placeholder="Họ Tên" required>
        <label for="hoTen">Họ Tên <span class="text-danger">*</span></label>
        <div class="invalid-feedback">Vui lòng nhập họ tên.</div>
    </div>

    <div class="mb-3">
        <label for="ghiChu" class="form-label fw-medium">Ghi Chú</label>
        <textarea class="form-control" id="ghiChu" name="ghiChu" rows="3"
                  th:text="${item?.ghiChu}"></textarea>
    </div>

    <div class="mb-3">
        <label for="file" class="form-label fw-medium">File Đính Kèm</label>
        <input type="file" class="form-control" id="file" name="file"
               accept=".pdf,.doc,.docx,.xlsx,.xls">
        <div class="form-text">Chấp nhận: PDF, DOC, DOCX, XLSX. Tối đa 20 MB.</div>
    </div>

    <div class="d-flex gap-2 justify-content-end">
        <a th:href="@{/url}" class="btn btn-outline-secondary">Huỷ</a>
        <button type="submit" class="btn btn-primary" onclick="showLoading(this)">
            <span class="spinner-border spinner-border-sm d-none me-1"></span>
            <i class="bi bi-save me-1"></i>Lưu
        </button>
    </div>
</form>
```

- Form card nghiệp vụ chính: `max-width: 800px`, căn giữa.
- Inline text/select/date: ưu tiên `form-floating`. Textarea dùng label thường.
- Mọi field bắt buộc có `*` màu danger sau label + `invalid-feedback`.
- CSRF token: **bắt buộc** trên mọi form POST.
- Button submit gắn `showLoading(this)` để vô hiệu hoá trong lúc chờ server —
  tránh double-submit.

### 5.6 Buttons

| Hành động                 | Class tương ứng                     | Ghi chú                                   |
|---------------------------|-------------------------------------|-------------------------------------------|
| Tạo mới / Lưu              | `btn btn-primary`                   | Mặc định override `--primary`             |
| Huỷ / Quay lại             | `btn btn-outline-secondary`         |                                           |
| Xoá                        | `btn btn-outline-danger`            | Phải gọi `confirmDelete(...)` trước submit|
| Phê duyệt                  | `btn btn-success`                   |                                           |
| Từ chối                    | `btn btn-outline-danger`            |                                           |
| Nộp lên duyệt              | `btn btn-warning text-dark`         |                                           |
| Export Excel               | `btn btn-outline-success`           | Icon `bi-file-earmark-excel`              |
| Import Excel               | `btn btn-outline-primary`           | Icon `bi-upload`                          |
| Hành động trong table      | `btn btn-sm btn-outline-*`          | Icon-only + tooltip                       |
| Tác vụ nguy hiểm trong modal | `btn btn-danger`                  | Modal confirm bắt buộc                    |

### 5.7 Badges trạng thái

Mapping chuẩn (áp dụng cho mọi entity có enum trạng thái):

| Trạng thái nghiệp vụ                   | Class                          |
|----------------------------------------|--------------------------------|
| `BanNhap`, `ChuanBi`                   | `badge bg-secondary`           |
| `ChoDuyet`                             | `badge bg-warning text-dark`   |
| `DaDuyet`                              | `badge bg-success` hoặc `bg-info text-dark` (DotKienTap) |
| `DangThucHien`, `DangDienRa`, `DangMo` | `badge bg-primary` hoặc `bg-success` (LopHocPhan) |
| `DaThucHien`                           | `badge bg-success`             |
| `DaKetThuc`, `DaDong`                  | `badge bg-dark` / `bg-secondary` |
| `DangThucTap`                          | `badge bg-info text-dark`      |
| `TuChoi`, `DaHuy`                      | `badge bg-danger`              |
| `DaPhanCong` (DanhSachSV…)             | `badge bg-secondary`           |

Với dữ liệu boolean dạng "đã tham gia / đã cảnh báo": dùng cặp `bg-success` /
`bg-warning text-dark`.

### 5.8 Flash Messages

Đặt NGAY sau breadcrumb (trước khi vào content block). Tự đóng sau 4 giây qua
`.auto-dismiss` trong `main.js`.

```html
<div th:if="${successMsg}" class="alert alert-success alert-dismissible fade show auto-dismiss" role="alert">
    <i class="bi bi-check-circle-fill me-2"></i>
    <span th:text="${successMsg}"></span>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<div th:if="${warningMsg}" class="alert alert-warning alert-dismissible fade show auto-dismiss" role="alert">
    <i class="bi bi-exclamation-triangle-fill me-2"></i>
    <span th:text="${warningMsg}"></span>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
<div th:if="${errorMsg}" class="alert alert-danger alert-dismissible fade show auto-dismiss" role="alert">
    <i class="bi bi-x-octagon-fill me-2"></i>
    <span th:text="${errorMsg}"></span>
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
```

- 4 loại: `success` (hành động hoàn tất), `info` (thông tin trung tính),
  `warning` (soft-check không chặn), `danger` (thất bại).
- Luôn có icon Bootstrap Icons đi kèm.

### 5.9 Empty State

```html
<tr th:if="${#lists.isEmpty(items)}">
    <td colspan="6" class="text-center py-5">
        <i class="bi bi-inbox display-4 text-muted d-block mb-3"></i>
        <p class="text-muted mb-3">Chưa có dữ liệu nào.</p>
        <a th:href="@{/url/them}" class="btn btn-primary">
            <i class="bi bi-plus-lg me-1"></i>Thêm Mới
        </a>
    </td>
</tr>
```

- Luôn có **icon + mô tả + CTA** (trừ trường hợp không có hành động tạo mới
  tương ứng role).
- Khi empty-state trong card chiếm nguyên thân, dùng class `.empty-state`.

### 5.10 Modal

- Dùng cho: xác nhận xoá, form ngắn (≤ 3 field), xem nhanh chi tiết 1 entity.
- **Không** dùng modal cho form > 5 field — chuyển sang trang riêng.
- Header gradient primary, body padding 20px, footer: `Huỷ` bên trái
  (outline-secondary), button chính bên phải (primary / success / danger).

### 5.11 Loading Feedback

```html
<button type="submit" class="btn btn-primary" onclick="showLoading(this)">
    <span class="spinner-border spinner-border-sm d-none me-1"></span>
    <i class="bi bi-save me-1"></i>Lưu
</button>
```

- `showLoading(btn)` trong `main.js` disable button, ẩn icon, hiện spinner.
- Tự re-enable sau 15 s fallback (đề phòng network lỗi).

### 5.12 Utility classes dùng chung

Tái sử dụng thay vì viết inline style. Các class được đăng ký trong `main.css`:

| Class                       | Công dụng                                                                 |
|-----------------------------|---------------------------------------------------------------------------|
| `.page-title`               | H4 primary + border-left accent                                           |
| `.page-subtitle`            | Kicker uppercase phía trên title                                          |
| `.stat-card`                | Base stat card (card + shadow + hover translate)                          |
| `.stat-card-icon`           | Icon tròn 44×44 bên trái                                                  |
| `.stat-card-value`          | Số liệu lớn (1.75 rem · 700 · primary)                                    |
| `.stat-card-label`          | Label mô tả nhỏ phía dưới                                                 |
| `.info-row` + `.info-label` | Bảng 2 cột key/value trong trang chi tiết                                 |
| `.empty-state`              | Wrapper cho empty state full-card                                         |
| `.sidebar-section-header`   | Ký hiệu section trong sidebar (uppercase · muted)                         |
| `.search-bar`               | Wrapper input-group focus unified                                         |
| `.soft-divider`             | Đường kẻ 1 px màu subtle                                                  |

Biến thể scoped (dashboard quick actions, login panel) vẫn giữ trong `<style>`
của trang tương ứng, nhưng một khi đã dùng ở ≥ 3 trang thì **nâng lên**
`main.css`.

---

## 6 · Sidebar Navigation

```html
<!-- Mục đơn -->
<li class="nav-item">
    <a class="nav-link"
       th:href="@{/url}"
       th:classappend="${activeMenu == 'ten-menu'} ? ' active' : ''">
        <i class="bi bi-icon-name"></i>
        <span>Tên Menu</span>
    </a>
</li>

<!-- Mục có submenu -->
<li class="nav-item">
    <a class="nav-link d-flex align-items-center"
       href="#submenu-id" data-bs-toggle="collapse"
       th:classappend="${activeMenu != null and activeMenu.startsWith('ten-module')} ? '' : ' collapsed'">
        <i class="bi bi-icon-name"></i>
        <span>Tên Menu</span>
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

### 6.1 Menu theo vai trò

| Vai trò              | Menu hiển thị (gộp `sec:authorize`)                                 |
|----------------------|----------------------------------------------------------------------|
| `ADMIN`, `PDT`       | Dashboard · Người Dùng · Doanh Nghiệp · Học Kỳ · Báo Cáo             |
| `TTDTXS`             | Dashboard · CTĐT · Lớp Học Phần · Đợt Kiến Tập / Thực Tập · Báo Cáo  |
| `CNHP` (Chủ nhiệm HP) | Học Phần · Đội ngũ GV · Lớp Học Phần của HP                         |
| `BCN` (Trưởng BM)    | CTĐT · Lớp Học Phần                                                  |
| `GIANG_VIEN`         | Lớp Học Phần của tôi · Đánh giá SV · Tài liệu                        |
| `CVHT`               | Cảnh báo SV                                                           |
| `SINH_VIEN`          | Lịch Học · Kết Quả                                                    |
| `DOANH_NGHIEP`       | Kiến Tập · Thực Tập (của tôi)                                         |

**Bắt buộc**: Mỗi GET controller phải `model.addAttribute("activeMenu", "<slug>")`
để item active được highlight (CSS có chỉ thị 3 px accent bên trái).

---

## 7 · Breadcrumb

Bắt buộc ở mọi trang (trừ `/login` và `/dashboard`), tối đa 3 cấp:

```html
<nav aria-label="breadcrumb" class="mb-3">
    <ol class="breadcrumb">
        <li class="breadcrumb-item">
            <a th:href="@{/dashboard}" class="text-decoration-none">
                <i class="bi bi-house-door me-1"></i>Trang Chủ
            </a>
        </li>
        <li class="breadcrumb-item">
            <a th:href="@{/nguoi-dung}" class="text-decoration-none">Người Dùng</a>
        </li>
        <li class="breadcrumb-item active" aria-current="page">Chi Tiết</li>
    </ol>
</nav>
```

---

## 8 · Thymeleaf Patterns

### 8.1 Badge trạng thái enum (PascalCase Java)

Enum Java trong project dùng PascalCase (`DaDuyet`, `ChoDuyet`, `ChuanBi`…),
**không phải** `UPPER_SNAKE_CASE`. `enum.name()` trả về tên Java gốc. Mọi so
sánh trong Thymeleaf phải đúng PascalCase.

```html
<!-- CTDT / HocPhan -->
<span th:with="tt=${ctdt.trangThai}"
      th:class="${tt.name() == 'DaDuyet'} ? 'badge bg-success' :
               (${tt.name() == 'ChoDuyet'} ? 'badge bg-warning text-dark' :
               (${tt.name() == 'DaHuy'} ? 'badge bg-danger' : 'badge bg-secondary'))"
      th:text="${tt.name()}">
</span>

<!-- DotKienTap -->
<span th:with="tt=${dot.trangThai}"
      th:class="${tt.name() == 'DaDuyet'} ? 'badge bg-info text-dark' :
               (${tt.name() == 'DaThucHien'} ? 'badge bg-success' :
               (${tt.name() == 'ChoDuyet'} ? 'badge bg-warning text-dark' :
               (${tt.name() == 'DaHuy'} ? 'badge bg-danger' : 'badge bg-secondary')))"
      th:text="${tt.name()}">
</span>

<!-- DotThucTap: 6 trạng thái, DaHuy phải hiển thị đỏ -->
<span th:with="tt=${dot.trangThai}"
      th:class="${tt.name() == 'DaHuy'} ? 'badge bg-danger' :
               (${tt.name() == 'DaKetThuc'} ? 'badge bg-secondary' :
               (${tt.name() == 'DangThucHien'} ? 'badge bg-success' :
               (${tt.name() == 'DaDuyet'} ? 'badge bg-info text-dark' :
               (${tt.name() == 'ChoDuyet'} ? 'badge bg-warning text-dark' : 'badge bg-light text-dark'))))"
      th:text="${tt.name()}">
</span>
```

### 8.2 Nút xoá kèm xác nhận

```html
<form th:action="@{/url/{ma}/xoa(ma=${item.ma})}" method="post"
      onsubmit="return confirmDelete(this, event, 'tên item này')">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
    <button type="submit" class="btn btn-sm btn-outline-danger"
            data-bs-toggle="tooltip" data-bs-title="Xoá">
        <i class="bi bi-trash3"></i>
    </button>
</form>
```

### 8.3 Format ngày tháng

```html
<td th:text="${item.ngayTao != null ? #temporals.format(item.ngayTao, 'dd/MM/yyyy') : '--'}"></td>
<td th:text="${item.thoiGian != null ? #temporals.format(item.thoiGian, 'dd/MM/yyyy HH:mm') : '--'}"></td>
```

### 8.4 Pagination chuẩn

```html
<nav th:if="${page != null and page.totalPages > 1}" class="mt-3">
    <ul class="pagination pagination-sm justify-content-end mb-0">
        <li class="page-item" th:classappend="${page.first} ? 'disabled'">
            <a class="page-link"
               th:href="@{/url(page=${page.number - 1}, size=${page.size}, keyword=${keyword})}">
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
            <a class="page-link"
               th:href="@{/url(page=${page.number + 1}, size=${page.size}, keyword=${keyword})}">
                <i class="bi bi-chevron-right"></i>
            </a>
        </li>
    </ul>
</nav>
```

### 8.5 CSRF (bắt buộc trong mọi form POST)

```html
<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
```

### 8.6 `sec:authorize` theo role

```html
<div sec:authorize="hasAnyRole('PDT','TTDTXS','ADMIN')">...</div>
<div sec:authorize="hasRole('GIANG_VIEN')">...</div>
<div sec:authorize="hasRole('ADMIN')">...</div>
```

Role key → quyền: xem `docs/03_WORKFLOW.md` phần "MA TRAN PHAN QUYEN".

---

## 9 · Interaction Patterns

### 9.1 Filter / Search

- Filter luôn nằm trong 1 card độc lập phía trên table/list.
- Khi có nhiều field filter tuỳ chọn (ví dụ: `CTĐT`, `Học Kỳ` ở trang Lớp Học
  Phần), cho phép logic **OR** giữa các field — user chỉ cần nhập 1 trong các
  field để tra cứu. Hiển thị gợi ý: *"Chọn ít nhất một tiêu chí."*
- Khi chỉ có 1 search box: bắt buộc có `keyword` và giữ state qua query string
  để pagination hoạt động đúng.

### 9.2 Batch / Mass action

- Dùng modal với pre-filled default value từ nguồn dữ liệu gốc (ví dụ: tạo
  hàng loạt lớp học phần — pre-fill số lớp từ `CtdtHocPhan.soLopDuKien`).
- Thể hiện rõ "idempotent" trong tooltip / alert: re-run không phá dữ liệu
  hiện tại.
- Trả về thông báo chính xác: số bản ghi được tạo thêm vs số bản ghi đã bỏ qua.

### 9.3 Soft-check vs Hard-check

- **Hard-check** (chặn cứng): kiểm tra FK tồn tại, ràng buộc ký tự, trạng thái
  sai workflow. Trả về `errorMsg` + giữ nguyên form input (model attribute).
- **Soft-check** (cảnh báo mềm): kiểm tra nghiệp vụ có rủi ro nhưng vẫn cho
  phép (ví dụ: phân công GV ngoài đội ngũ HP). Trả về `warningMsg` mô tả rõ
  lý do + hướng dẫn khắc phục.

### 9.4 Toast / Auto-dismiss

- Flash message `auto-dismiss` đóng sau 4 s.
- Toast (Bootstrap `toast`) dành cho thông báo real-time không liên quan đến
  submit form (ví dụ: notification email đã gửi).

### 9.5 Destructive action

- Bắt buộc đi qua modal xác nhận (`confirmDelete` helper).
- Nội dung modal phải: tên item + hậu quả không thể hoàn tác + hai nút rõ
  ràng (`Huỷ` vs `Xoá`).
- **Không** dùng `alert()` / `confirm()` JavaScript thuần cho action chính —
  chỉ dùng fallback khi modal chưa render.

---

## 10 · Model Attribute Contract

Để template và controller giao tiếp đồng nhất, mọi `Model.addAttribute` tuân
theo bảng dưới. Tên attribute là **key cố định**, controller không được đổi.

| Attribute      | Kiểu Java           | Mục đích                                                |
|----------------|---------------------|---------------------------------------------------------|
| `page`         | `Page<T>`           | Kết quả phân trang                                      |
| `item`         | Entity / DTO        | Object đang xem / sửa chi tiết                          |
| `items`        | `List<T>`           | Danh sách đơn giản (không phân trang)                   |
| `listX`        | `List<T>`           | Option list cho select (vd: `listHocPhan`, `listGV`)    |
| `successMsg`   | `String`            | Flash thành công (`RedirectAttributes`)                 |
| `errorMsg`     | `String`            | Flash thất bại                                          |
| `warningMsg`   | `String`            | Flash cảnh báo mềm (soft-check)                         |
| `activeMenu`   | `String`            | Slug menu đang active trong sidebar                     |
| `keyword`      | `String`            | Giá trị ô search hiện tại (giữ state qua pagination)    |
| `currentUser`  | `CustomUserDetails` | User đang đăng nhập (inject từ `@AuthenticationPrincipal`) |

Flash message: **luôn** thêm qua `RedirectAttributes.addFlashAttribute(...)`
rồi `redirect:`, không dùng `model.addAttribute` cho thông báo sau POST.

---

## 11 · Accessibility (a11y) checklist

Hệ thống không phải là trang công cộng WCAG, nhưng phải đủ khả năng hỗ trợ
người dùng nội bộ có khiếm thị nhẹ / thao tác bàn phím. Mọi PR UI phải đạt:

- [x] Mỗi `<input>` / `<select>` có `<label>` liên kết qua `for` / `id`.
- [x] Icon-only button có `data-bs-title` (tooltip) hoặc `aria-label`.
- [x] Nút đóng modal / alert có nội dung (`btn-close` có text ẩn `.visually-hidden`
      hoặc tooltip).
- [x] Contrast ratio text/nền ≥ 4.5 : 1 cho body, ≥ 3 : 1 cho heading.
- [x] Focus ring rõ ràng trên mọi control (dùng chung CSS focus-visible trong
      `main.css`, 3 px `rgba(45, 95, 158, 0.22)`).
- [x] Table có `<thead>` đúng ngữ nghĩa; bảng dữ liệu có `scope="col"` khi
      phức tạp.
- [x] Flash alert có `role="alert"`.

---

## 12 · Performance & Hardening

- **`open-in-view = false`**: mọi association được `LEFT JOIN FETCH` trong
  repository tương ứng. Tránh `LazyInitializationException` khi render
  Thymeleaf.
- **Không trộn icon library**: chỉ dùng `bi bi-*` (Bootstrap Icons).
- **Không import** font-family khác Inter — giảm FOUT / network.
- **CSP-friendly**: không inline `<script>` với thông tin nhạy cảm; JS dùng
  chung đặt ở `main.js`.
- **No `@ResponseBody` trong MVC controller** — controller MVC chỉ return
  view name hoặc `redirect:`.
- **Sanitise output**: luôn dùng `th:text` (escape); `th:utext` chỉ dùng cho
  nội dung đã được whitelist / trust.
- **Upload file**: kiểm tra extension ở frontend (`accept`) **và** validation
  bắt buộc ở service layer (size, MIME).

---

## 13 · DO / DON'T

### DO

- Mọi template extends `layout/base.html` qua `layout:decorate`.
- Mọi trang có breadcrumb (trừ login và dashboard).
- Mọi table có empty-state khi không có dữ liệu.
- Mọi form POST có CSRF token.
- Mọi nút xoá có `confirmDelete(...)` trước submit.
- `activeMenu` được truyền từ controller trong mọi GET handler.
- Trạng thái hiển thị qua badge theo bảng §5.7.

### DON'T

- Không dùng inline style (tất cả quy về `main.css` hoặc class utility).
- Không dùng màu Bootstrap default trực tiếp — luôn đi qua token.
- Không để text `null` / `undefined` lọt ra UI (fallback `'--'`).
- Không dùng `alert()` / `confirm()` JS thuần cho action chính.
- Không mix icon từ nhiều library (Font Awesome / Material / etc.).
- Không đặt button submit floating giữa trang — luôn ở cuối form.
- Không dùng `@ResponseBody` cho view MVC.
- Không để `LazyInitializationException` xảy ra trong render (sửa ở service
  layer bằng `@Transactional(readOnly = true)` + `JOIN FETCH`).

---

## 14 · Login Page

Login có layout độc lập (`brand-panel` bên trái + `form-panel` bên phải) và
**không** decorate `layout/base.html`. Class CSS đặt scoped trong
`templates/auth/login.html`:

- `.login-shell` — grid 2 cột (brand | form)
- `.brand-panel` — nền gradient primary + minh hoạ brand + `brand-feature-list`
- `.form-panel` — nền `--surface`, form đăng nhập
- `.btn-login` — full-width, shadow primary hover

Lý do tách rời: login là surface public (trước authentication), yêu cầu
branding rõ ràng và không có navbar/sidebar.

---

## 15 · Versioning & Change Log

Mọi thay đổi design token (color / radius / shadow / typography) phải kèm theo
ghi chú version trong đầu file `main.css` và cập nhật bảng dưới:

| Version | Ngày        | Nội dung chính                                                   |
|---------|-------------|------------------------------------------------------------------|
| v1      | 2025-Q4     | Design system khởi tạo: palette, component, Thymeleaf patterns   |
| v2      | 2026-Q1     | Tokens `--primary-600`, `--text-primary/muted`, `--radius-card`  |
| v3      | 2026-Q2     | Rewrite production-grade: a11y checklist, interaction patterns,  |
|         |             | filter OR-logic cho Lớp Học Phần, model attribute contract       |

Nguyên tắc: **không breaking change** với template đang có. Mọi deprecation
phải có path di cư trong ≥ 1 release.
