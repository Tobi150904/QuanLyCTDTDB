# 05 · UI Design System

> **Document owner:** Design Authority · **Stack:** Thymeleaf · Bootstrap 5.3 · Bootstrap Icons · Inter · Custom CSS tokens (`static/css/main.css`) · Spring Security Extras
>
> **Scope:** Là nguồn chân lý (single source of truth) cho mọi quyết định thị giác và hành vi tương tác của toàn bộ giao diện. Mọi thay đổi UI phải được cập nhật ở file này **trước**, sau đó mới triển khai trong code.
>
> **Audience:** Developer fullstack, reviewer PR, QA, và thành viên mới onboarding dự án.

---

## Mục lục

0. [Triết lý thiết kế](#0--triết-lý-thiết-kế)
1. [Design principles](#1--design-principles)
2. [Design tokens](#2--design-tokens)
3. [Color system](#3--color-system)
4. [Typography](#4--typography)
5. [Spacing & Layout grid](#5--spacing--layout-grid)
6. [Elevation & Radius](#6--elevation--radius)
7. [Iconography & Media](#7--iconography--media)
8. [Motion & Transitions](#8--motion--transitions)
9. [Component library](#9--component-library)
10. [Page templates](#10--page-templates)
11. [State patterns](#11--state-patterns)
12. [Role-based UI matrix](#12--role-based-ui-matrix)
13. [Module UX blueprints](#13--module-ux-blueprints)
14. [Accessibility](#14--accessibility)
15. [Responsive breakpoints](#15--responsive-breakpoints)
16. [Content & Voice](#16--content--voice)
17. [Feedback contract](#17--feedback-contract)
18. [Implementation checklist](#18--implementation-checklist)

---

## 0 · Triết lý thiết kế

Hệ thống phục vụ **8 nhóm người dùng** (PDT, TTDTXS, CNHP, CVHT, Giảng viên, Sinh viên, Doanh nghiệp, Admin) làm việc liên tục trong môi trường học vụ. Giao diện phải đáp ứng ba đặc tính cốt lõi:

| Đặc tính | Hệ quả thiết kế |
|---|---|
| **Institutional** — phong cách cơ quan, không "app thời trang" | Palette trầm, typography sạch, không gradient rực rỡ, icon line-weight đều |
| **Information-dense** — người dùng nhập/tra hàng chục bản ghi/ngày | Bảng compact, filter panel luôn hiện, không thu gọn menu chính |
| **Deterministic** — không có thao tác "im lặng" | Mọi POST đều feedback bằng flash message; destructive action luôn có confirm modal |

> "**Boring on purpose.**" Thiết kế không cố gây ấn tượng — nó cố biến mất để người dùng tập trung vào dữ liệu.

---

## 1 · Design principles

1. **Clarity over cleverness** — Gọi tên trạng thái bằng từ nguyên bản tiếng Việt (`Đang Diễn Ra`, `Chờ Duyệt`). Tránh ẩn dụ (`Pipeline`, `Flow`).
2. **3-click rule** — Mọi nghiệp vụ chính phải hoàn tất trong ≤ 3 thao tác tính từ Dashboard.
3. **Density-first, whitespace-balanced** — Ưu tiên hiển thị nhiều dòng/bảng; nhưng `padding-y` của cell ≥ 10px để không mỏi mắt.
4. **Progressive disclosure** — Form dài phải chia section; action phụ đẩy vào dropdown; detail đẩy vào modal/off-canvas.
5. **Predictable feedback** — Mọi action phải kết thúc bằng một trong: flash banner · toast · re-render form với `errorMsg` · redirect + highlight row mới.
6. **Forgiveness by default** — Không bao giờ xóa ngay; luôn confirm modal. Lỗi validation không xóa input của user.
7. **Least privilege visible** — User không có quyền thì không thấy action button (qua `sec:authorize`), thay vì thấy rồi bị chặn.

---

## 2 · Design tokens

Tất cả token được định nghĩa trong `static/css/main.css → :root`. **Nguyên tắc sử dụng: luôn gọi qua `var(--token)`, không hard-code HEX trong Thymeleaf template.**

### 2.1 Token reference

| Token | Giá trị | Vai trò chính |
|---|---|---|
| **Brand** | | |
| `--primary` | `#1e3a5f` | Navbar, card header, button primary |
| `--primary-lt` | `#2d5f9e` | Link, hover, focus ring, icon stat |
| `--primary-dk` | `#172d4a` | Navbar gradient stop, modal header |
| `--accent` | `#e8a020` | Border-left title/stat, huy hiệu cảnh báo |
| `--accent-lt` | `#f4b944` | Icon thương hiệu navbar, highlight |
| **Surface** | | |
| `--surface` | `#f4f6f9` | Body/page background |
| `--surface-alt` | `#eef2f7` | Table header, code inline, toolbar filter |
| `--surface-card` | `#ffffff` | Card, modal, sidebar |
| `--surface-hover` | `#f0f4fa` | Hover sidebar, hover row |
| **Text** | | |
| `--text-main` | `#212529` | Body text |
| `--text-muted` | `#6c757d` | Label phụ, metadata |
| `--text-subtle` | `#8a94a6` | Placeholder, icon empty state |
| **Border** | | |
| `--border-color` | `#e5e9f0` | Kẻ mặc định |
| `--border-strong` | `#d7dde6` | Input border, scrollbar thumb |
| **Radius** | | |
| `--radius-sm` | `6px` | Button, input, badge |
| `--radius-md` | `10px` | Card, modal, avatar lớn |
| `--radius-lg` | `14px` | Dialog quan trọng, hero panel |
| **Elevation** | | |
| `--shadow-sm` | — | Card default |
| `--shadow-md` | — | Card hover, dropdown |
| `--shadow-lg` | — | Modal, offcanvas |
| **Motion** | | |
| `--transition` | `180ms cubic-bezier(.4,0,.2,1)` | Chuẩn toàn hệ |
| **Layout** | | |
| `--sidebar-width` | `248px` | Sidebar desktop |
| `--navbar-height` | `60px` | Top navbar fixed |

### 2.2 Thêm token mới — quy trình

1. Đề xuất tên token (kebab-case, namespace `--<group>-<role>`).
2. Cập nhật `:root` trong `main.css` + bảng trên.
3. Ghi chú lý do vào changelog cuối file (mục 19).
4. PR phải ghi rõ "touches design token".

---

## 3 · Color system

### 3.1 Semantic mapping

| Ý nghĩa | Foreground | Badge | Alert | Ví dụ |
|---|---|---|---|---|
| **Success** | `#198754` | `badge bg-success` | `alert-success` | Tạo thành công, Kích hoạt |
| **Info** | `#0dcaf0` | `badge bg-info text-dark` | `alert-info` | Hint, ghi chú |
| **Warning** | `#ffc107` | `badge bg-warning text-dark` | `alert-warning` | Chờ duyệt, sắp hết hạn |
| **Danger** | `#dc3545` | `badge bg-danger` | `alert-danger` | Xóa, cảnh báo SV, từ chối |
| **Neutral** | `#6c757d` | `badge bg-secondary` | `alert-secondary` | Nháp, không xác định |

### 3.2 Status color grammar (áp dụng toàn hệ)

| Trạng thái nghiệp vụ | Màu | Icon |
|---|---|---|
| `Sắp Diễn Ra` | `bg-info text-dark` | `bi-clock` |
| `Đang Diễn Ra` | `bg-success` | `bi-play-circle` |
| `Đã Kết Thúc` | `bg-secondary` | `bi-check-circle` |
| `Chờ Duyệt` | `bg-warning text-dark` | `bi-hourglass-split` |
| `Đã Duyệt` | `bg-success` | `bi-patch-check` |
| `Từ Chối` | `bg-danger` | `bi-x-octagon` |
| `Bắt Buộc` (HP) | `bg-primary` | `bi-star-fill` |
| `Tự Chọn` (HP) | `bg-secondary` | `bi-star` |
| `Cảnh Báo` (SV) | `bg-danger` | `bi-exclamation-triangle-fill` |

### 3.3 Rules

- **Không dùng purple/violet** ở bất kỳ đâu — không hợp brand.
- **Gradient** chỉ dùng cho navbar (`--primary → --primary-dk`). Nơi khác cấm.
- Text trên nền màu: luôn chọn `text-white` hoặc `text-dark` theo WCAG AA (contrast ≥ 4.5:1).
- Background của card **luôn** là `--surface-card`; không tô màu khác trừ empty-state panel.

---

## 4 · Typography

### 4.1 Stack

```css
font-family: 'Inter', 'Segoe UI', system-ui, -apple-system, sans-serif;
font-size: 0.9rem;  /* 14.4px base */
line-height: 1.55;
```

Inter load qua `<link>` Google Fonts trong `layout/base.html`. **Chỉ một font family** cho toàn hệ.

### 4.2 Scale

| Cấp | Class/Token | Size | Weight | Line-height | Usage |
|---|---|---|---|---|---|
| **Display** | `.display-6` | `1.75rem` | 600 | 1.2 | Trang đăng nhập, hero empty-state |
| **H1 / Page title** | `<h1>` với `.page-title` | `1.375rem` | 600 | 1.3 | Tiêu đề trang, border-left `--accent` 4px |
| **H2 / Section** | `<h2>` / `.card-header strong` | `1.0625rem` | 600 | 1.4 | Header card, section trong form |
| **H3 / Subsection** | `<h3>` | `0.9375rem` | 600 | 1.4 | Nhóm field trong form dài |
| **Body** | default | `0.9rem` | 400 | 1.55 | Text chính |
| **Small / Meta** | `.small`, `<small>` | `0.8125rem` | 400 | 1.4 | Metadata, helper text |
| **Code / ID** | `<code>` | `0.8125rem` | 500 | 1.4 | Mã CTDT, mã HP, mã SV |
| **Caption** | `.form-text` | `0.75rem` | 400 | 1.4 | Ghi chú dưới input |

### 4.3 Rules

- Không dùng `text-decoration: underline` trên link nội bộ; chỉ dùng hover color change.
- Không dùng `font-style: italic` cho body; chỉ cho metadata phụ (timestamp, tác giả).
- Weight chỉ có: `400` (regular), `500` (medium — label), `600` (semibold — heading). **Không dùng 700/800.**
- Tiêu đề luôn dùng `text-balance` hoặc `text-pretty` để tránh orphan line.

---

## 5 · Spacing & Layout grid

### 5.1 Spacing scale (theo Bootstrap utility)

| Token | Pixel | Dùng cho |
|---|---|---|
| `0` | 0 | — |
| `1` | 4px | Gap giữa icon và text trong button |
| `2` | 8px | Gap nhỏ giữa badge, gap trong flex |
| `3` | 16px | Gap giữa card, section margin |
| `4` | 24px | `card-body` padding dọc, gap giữa title và content |
| `5` | 48px | Khoảng cách page top, giữa hero và content |

**Nguyên tắc:** luôn dùng utility class (`p-3`, `gap-3`, `mt-4`), không dùng arbitrary value (`p-[17px]`).

### 5.2 Layout grid (desktop ≥ 992px)

```
┌───────────────────────────────────────────────────────────────┐
│  NAVBAR (60px, fixed-top, z-1040)                             │
├──────────┬────────────────────────────────────────────────────┤
│          │                                                    │
│ SIDEBAR  │   MAIN CONTENT (fluid container, max-width 1400px) │
│ (248px   │                                                    │
│  fixed)  │   ┌──────────────────────────────────────────────┐ │
│          │   │ PAGE HEADER (title + actions)                │ │
│          │   ├──────────────────────────────────────────────┤ │
│          │   │ FILTER TOOLBAR (sticky trong list view)      │ │
│          │   ├──────────────────────────────────────────────┤ │
│          │   │ CONTENT CARDS / TABLE                         │ │
│          │   └──────────────────────────────────────────────┘ │
└──────────┴────────────────────────────────────────────────────┘
```

- **Mobile (< 992px):** sidebar thu vào offcanvas, toggle bằng nút hamburger ở navbar.
- **Table hẹp hơn 768px:** wrap trong `.table-responsive`; không ép breakpoint responsive table.

### 5.3 Nguyên tắc layout

- **Luôn** dùng `flex` cho layout 1 chiều, `grid` cho 2 chiều phức tạp.
- **Không** dùng `space-*` cho spacing giữa children — luôn dùng `gap-*`.
- **Không** mix `margin` + `gap` trên cùng một element.
- Card luôn dùng `mb-3` hoặc `mb-4` để tạo rhythm dọc, không dùng `margin-top`.

---

## 6 · Elevation & Radius

### 6.1 Elevation hierarchy (thứ tự từ thấp đến cao)

| Level | Shadow token | Usage |
|---|---|---|
| 0 (flat) | — | Background, section không nổi |
| 1 | `--shadow-sm` | Card mặc định, filter toolbar |
| 2 | `--shadow-md` | Card khi hover, dropdown menu |
| 3 | `--shadow-lg` | Modal, offcanvas, toast |

### 6.2 Radius map

- `--radius-sm` (6px): input, button, badge, select, tag
- `--radius-md` (10px): card, alert, modal body, avatar 64×64+
- `--radius-lg` (14px): dialog xác nhận quan trọng (xóa cấp cao), hero panel

**Không** dùng `border-radius: 50%` trừ avatar và icon tròn. Button pill (`rounded-pill`) chỉ dùng cho filter chip.

---

## 7 · Iconography & Media

### 7.1 Icon set

- **Bộ duy nhất:** Bootstrap Icons 1.11 (`bi-*`).
- **Size:** `16px` trong inline text; `20px` trong button; `24px` trong stat card; `40px` trong empty state.
- **Màu:** kế thừa từ parent (`currentColor`); chỉ override khi đứng độc lập (ví dụ stat card dùng `--primary-lt`).
- **Không** dùng emoji thay icon. **Không** trộn 2 icon set.

### 7.2 Icon conventions

| Hành động / Khái niệm | Icon |
|---|---|
| Tạo mới | `bi-plus-circle` / `bi-plus-lg` |
| Sửa | `bi-pencil` |
| Xóa | `bi-trash` |
| Xem chi tiết | `bi-eye` |
| Khóa / Mở khóa | `bi-lock` / `bi-unlock` |
| Duyệt | `bi-check2-circle` |
| Từ chối | `bi-x-circle` |
| Tải xuống | `bi-download` |
| Tải lên | `bi-upload` |
| Tìm kiếm | `bi-search` |
| Lọc | `bi-funnel` |
| Cài đặt | `bi-gear` |
| Đăng xuất | `bi-box-arrow-right` |
| CTDT | `bi-mortarboard` |
| Học phần | `bi-book` |
| Lớp học phần | `bi-collection` |
| Học kỳ | `bi-calendar-event` |
| Người dùng | `bi-people` |
| Dashboard | `bi-speedometer2` |

### 7.3 Media

- Ảnh đại diện (avatar): placeholder từ chữ cái đầu tên, nền `--primary` + text trắng nếu chưa upload.
- Icon decoration lớn trong empty state: 1 icon Bootstrap, opacity 0.35, size 80–96px.
- **Cấm** abstract shapes / gradient blob / SVG hand-drawn làm filler.

---

## 8 · Motion & Transitions

### 8.1 Duration & easing

- **Standard:** `180ms cubic-bezier(.4, 0, .2, 1)` (token `--transition`).
- **Quick** (feedback nhỏ, hover): `120ms ease`.
- **Slow** (dialog enter/exit): `240ms cubic-bezier(.4, 0, .2, 1)`.

### 8.2 What animates

| Element | Property | Duration |
|---|---|---|
| Button / Link hover | `background-color`, `color` | 120ms |
| Card hover | `box-shadow`, `transform: translateY(-1px)` | 180ms |
| Sidebar link hover | `background-color`, `padding-left` | 180ms |
| Modal enter | `opacity` + `transform: scale(.98→1)` | 240ms |
| Toast / flash | `opacity` + `translateY(-8px→0)` | 180ms |

### 8.3 What NEVER animates

- Layout shifts lớn (đừng animate `width`/`height` của sidebar).
- Color của text body khi hover row (đổi background là đủ).
- Page transition (Thymeleaf server-rendered, không SPA).
- Loading spinner tự chế — **dùng Bootstrap `.spinner-border`**.

---

## 9 · Component library

### 9.1 Navbar (top, fixed)

```
┌─────────────────────────────────────────────────────────────┐
│  🎓 Quản Lý Đào Tạo  ·  Trường XYZ     [🔔] [👤 User ▾]    │
└─────────────────────────────────────────────────────────────┘
```

- Height: `60px`, `position: fixed-top`, `z-index: 1040`.
- Background: gradient `--primary → --primary-dk`.
- Brand: `bi-mortarboard-fill` màu `--accent-lt` + tên hệ thống.
- Right cluster: notification bell (badge số lượng), user menu dropdown (avatar + tên + role + link Profile/Logout).

### 9.2 Sidebar (left, fixed)

- Width: `248px`, background `--surface-card`, border-right `--border-color`.
- Section header: uppercase, `--text-subtle`, `letter-spacing: 0.05em`, `font-size: 0.75rem`.
- Link item: padding `10px 16px`, icon 16px + margin-right 10px.
- State **active**: background `--surface-hover`, border-left `3px solid --primary-lt`, font-weight 600.
- State **hover**: background `--surface-hover`, `padding-left: +2px`.
- Hiển thị theo role qua `sec:authorize` — **không** hiện item mà user không click được.

### 9.3 Card

```html
<article class="card">
  <header class="card-header">
    <strong><i class="bi bi-collection me-2"></i>Tiêu đề</strong>
    <span class="badge bg-light text-dark">12 mục</span>
  </header>
  <div class="card-body">...</div>
  <footer class="card-footer bg-transparent small text-muted">...</footer>
</article>
```

- Radius: `--radius-md`, shadow `--shadow-sm`, border `1px solid --border-color`.
- Header: `bg-transparent` + `font-weight: 600`. **Không** tô màu đậm.
- Hover (chỉ card dẫn đến detail): `translateY(-1px)` + shadow lên `--shadow-md`.

### 9.4 Table

- Class mặc định: `table table-hover align-middle`.
- Header: `background: var(--surface-alt)`, uppercase, font-size `0.75rem`, `letter-spacing: 0.03em`.
- Cell: `padding: 10px 12px`, vertical-align middle.
- Row hover: `background: var(--surface-hover)`.
- Zebra: **không** dùng striping trừ trang thống kê dày đặc.
- Action cluster cuối dòng: `btn-group-sm` với icon-only buttons + tooltip.

### 9.5 Button

| Loại | Class | Usage |
|---|---|---|
| Primary | `btn btn-primary` | Action chính của trang (Tạo mới, Lưu) — **tối đa 1 mỗi section** |
| Secondary outline | `btn btn-outline-secondary` | Hủy, Quay lại |
| Danger | `btn btn-danger` | Xác nhận xóa (trong modal) |
| Danger outline | `btn btn-outline-danger` | Icon xóa trong row |
| Icon-only | `btn btn-sm btn-outline-*` | Action cluster — **bắt buộc** có `data-bs-toggle="tooltip"` + `aria-label` |
| Link | `btn btn-link` | Action ngữ nghĩa nhẹ (xem thêm, bỏ qua) |

**Sizing:** default cho action chính, `btn-sm` cho action trong table row.

### 9.6 Form

```html
<form th:object="${dto}" novalidate>
  <!-- Error banners (non-field) -->
  <div th:if="${errorMsg}" class="alert alert-danger">...</div>

  <!-- Validation summary (field) — BẮT BUỘC trong form th:object -->
  <div th:if="${#fields.hasErrors('*')}" class="alert alert-warning">...</div>

  <!-- Section -->
  <fieldset class="mb-4">
    <legend class="fs-6 fw-semibold text-uppercase text-muted">Thông Tin Chung</legend>
    <div class="row g-3">
      <div class="col-md-6">
        <label class="form-label">Tên Học Phần <span class="text-danger">*</span></label>
        <input th:field="*{tenHocPhan}" class="form-control"
               th:classappend="${#fields.hasErrors('tenHocPhan')} ? ' is-invalid'"/>
        <div class="invalid-feedback" th:errors="*{tenHocPhan}"></div>
        <small class="form-text text-muted">Ví dụ: Lập Trình Hướng Đối Tượng</small>
      </div>
    </div>
  </fieldset>

  <div class="d-flex justify-content-end gap-2">
    <a class="btn btn-outline-secondary">Hủy</a>
    <button class="btn btn-primary">Lưu</button>
  </div>
</form>
```

**Quy tắc:**
- Label luôn có dấu `*` đỏ cho trường bắt buộc.
- Validation error hiển thị ngay dưới input qua `.invalid-feedback`.
- Helper text `.form-text` cho ví dụ/giới hạn.
- Nút action cuối: phải đặt ở **dưới-phải**, Hủy trước Lưu.
- **Cấm** disable nút Lưu cho đến khi validate client-side — để user submit rồi show error.
- Form có file upload: `@InitBinder` disallow field `String` trùng tên với `@RequestParam MultipartFile`.

### 9.7 Badge

- Radius: `--radius-sm`, padding `4px 8px`, font-size `0.75rem`, font-weight 500.
- Không dùng icon bên trong badge trừ status (ví dụ `bi-check` trong `Đã Duyệt`).
- Status badge trong table: bọc trong `<span class="badge">`, không `<div>`.

### 9.8 Modal

```
┌────────────────────────────────────────────┐
│ ⚠️  Xác Nhận Xóa                      [✕]  │  <- header: --primary-dk, text white
├────────────────────────────────────────────┤
│  Bạn có chắc chắn muốn xóa học phần        │
│  LTHDT101 - Lập Trình Hướng Đối Tượng?     │
│                                            │
│  Hành động này KHÔNG thể hoàn tác.         │  <- text-danger small
├────────────────────────────────────────────┤
│                      [ Hủy ] [ Xóa ]       │  <- btn-outline-secondary + btn-danger
└────────────────────────────────────────────┘
```

- Size: `modal-md` default, `modal-lg` cho form phức tạp, `modal-xl` cho wizard.
- Backdrop: luôn click-to-close trừ modal đang xử lý POST.
- Destructive confirm: bắt buộc có icon cảnh báo `bi-exclamation-triangle-fill` + dòng "không thể hoàn tác".

### 9.9 Alert (flash + inline)

| Variant | Icon | Dùng cho |
|---|---|---|
| `alert-success` | `bi-check-circle-fill` | Tạo/cập nhật thành công |
| `alert-danger` | `bi-exclamation-triangle-fill` | Exception, lỗi business |
| `alert-warning` | `bi-exclamation-circle-fill` | Chờ duyệt, gần hết hạn |
| `alert-info` | `bi-info-circle-fill` | Hint, hướng dẫn |

Flash alert (render trong `layout/base.html`) auto-dismiss sau 5s. Inline alert (trong form) **không** auto-dismiss.

### 9.10 Empty state

```
┌───────────────────────────────────────────┐
│                                           │
│                  📋                        │  <- icon 80px, opacity 0.35
│                                           │
│        Chưa có học phần nào                │  <- h3
│        Bắt đầu bằng cách tạo học phần     │  <- text-muted
│        đầu tiên cho chương trình đào tạo. │
│                                           │
│           [ + Tạo Học Phần ]              │  <- btn-primary, nếu có quyền
│                                           │
└───────────────────────────────────────────┘
```

- Padding dọc: 64px.
- Text-align: center.
- Icon: 1 icon Bootstrap cỡ lớn, màu `--text-subtle`.
- CTA: chỉ 1, và chỉ hiển thị nếu user có quyền tạo.

### 9.11 Skeleton loader

**Không** dùng skeleton — hệ thống server-rendered, trang load < 300ms trong LAN. Dùng `.spinner-border` nhỏ nếu có fetch AJAX (ví dụ tìm kiếm giảng viên).

### 9.12 Pagination

- Dùng Bootstrap `.pagination` size default.
- Luôn hiện: First · Prev · [page numbers] · Next · Last.
- Page size selector bên phải: 10 / 25 / 50 / 100.
- Hiển thị "Hiển thị X–Y trong tổng Z bản ghi" bên trái.

### 9.13 Breadcrumb

- Chỉ xuất hiện ở chi tiết (depth ≥ 2).
- Separator: `bi-chevron-right`.
- Item cuối: `aria-current="page"`, không click được, màu `--text-muted`.

---

## 10 · Page templates

### 10.1 Dashboard

```
┌─ Page header ────────────────────────────────────────────┐
│ Xin chào, [Tên] · Vai trò: [Role]    [Chu kỳ: HK1-2025]  │
└──────────────────────────────────────────────────────────┘
┌─ Stat cards (4 cột) ─────────────────────────────────────┐
│ [CTDT: 12] [HP đã duyệt: 148] [Lớp đang mở: 32] [SV...]  │
└──────────────────────────────────────────────────────────┘
┌─ Widget 2 cột ───────────────────────────────────────────┐
│ ┌─ Việc cần làm ──────┐ ┌─ Hoạt động gần đây ──────────┐ │
│ │ · 3 HP chờ duyệt    │ │ · PDT duyệt HP LTHDT101      │ │
│ │ · 2 LHP chưa GV     │ │ · CNHP tạo HP mới            │ │
│ └──────────────────────┘ └──────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

- Stat card: số lớn (2.25rem, weight 600), label phía dưới, icon góc trên phải với màu `--primary-lt`, border-left `4px solid --accent`.
- Widget: height tự động, không fix; có "Xem tất cả" link ở footer.

### 10.2 List view (trang danh sách)

```
┌─ Page header ────────────────────────────────────────────┐
│ Học Phần                       [ + Tạo Học Phần ]        │
│ Quản lý danh sách học phần trong toàn trường.            │
└──────────────────────────────────────────────────────────┘
┌─ Filter toolbar (sticky, --surface-alt) ─────────────────┐
│ 🔍 [Tìm kiếm...]  [Loại HP ▾] [Trạng thái ▾] [Đặt lại]   │
└──────────────────────────────────────────────────────────┘
┌─ Table card ─────────────────────────────────────────────┐
│ ┌──┬────────┬──────────────┬────┬──────┬──────┬────────┐ │
│ │☐ │ Mã HP  │ Tên HP       │ TC │ Loại │ TT   │ Actions│ │
│ ├──┼────────┼──────────────┼────┼──────┼──────┼────────┤ │
│ │☐ │ LTHDT1 │ Lập Trình... │ 3  │ BB   │ 🟢   │ 👁 ✏ 🗑│ │
│ └──┴────────┴──────────────┴────┴──────┴──────┴────────┘ │
│ Hiển thị 1–25 / 148       [1] 2 3 ... [50/page ▾]       │
└──────────────────────────────────────────────────────────┘
```

- Filter toolbar: `sticky top-0` khi scroll, background `--surface-alt`.
- Bulk action bar (xuất hiện khi ≥ 1 row selected): fixed-bottom, với các nút "Duyệt hàng loạt", "Xuất Excel", "Hủy".

### 10.3 Detail view

```
┌─ Breadcrumb ─────────────────────────────────────────────┐
│ Dashboard › Học Phần › LTHDT101                          │
└──────────────────────────────────────────────────────────┘
┌─ Hero header ────────────────────────────────────────────┐
│ LTHDT101 · Lập Trình Hướng Đối Tượng      [🟢 Đã Duyệt]  │
│ 3 TC · Bắt Buộc · Khoa CNTT              [✏ Sửa] [🗑]   │
└──────────────────────────────────────────────────────────┘
┌─ Tabs ───────────────────────────────────────────────────┐
│ [Tổng quan] [CTĐT đang dùng] [Lớp đã mở] [Đề cương] [Log]│
└──────────────────────────────────────────────────────────┘
┌─ Tab content (grid 2 cột) ───────────────────────────────┐
│ ┌─ Thông tin chung ───┐ ┌─ Thống kê ──────────────────┐ │
│ │ Mã: LTHDT101        │ │ Lớp đang mở: 3              │ │
│ │ Tên: ...            │ │ SV đang học: 142            │ │
│ └──────────────────────┘ └──────────────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

### 10.4 Form view

Chia thành fieldset theo section, mỗi fieldset có `<legend>` uppercase. Nút action dưới cùng, fixed-bottom trên mobile.

### 10.5 Wizard (multi-step)

Dành cho: Tạo CTDT (4 bước), Tạo lớp hàng loạt (3 bước), Đăng ký kiến tập (3 bước).

```
[①━Thông tin━━②━━Học phần━━③━━Xem lại━━④━━Hoàn tất]
   done         active         todo         todo
```

- Step indicator: circle 32px, border 2px, số bên trong.
- State: `done` (nền `--primary`, check icon), `active` (nền `--primary`, border `--accent`), `todo` (nền transparent, border `--border-strong`).
- Nút: `[← Quay lại]` trái, `[Tiếp →]` / `[Hoàn tất]` phải.
- **Back** không làm mất data các bước trước (server session hoặc hidden inputs).

---

## 11 · State patterns

Mọi màn hình phải có đủ các state sau:

| State | Trigger | UI |
|---|---|---|
| **Loading** | Request > 300ms | Spinner-border nhỏ cạnh label, disable submit |
| **Empty** | Không có dữ liệu | Empty state (9.10) với CTA nếu user có quyền |
| **Error** | Exception hoặc business rule | Alert banner + re-render form giữ nguyên input |
| **Success** | Save thành công | Redirect + flash `successMsg` + highlight row mới (class `.row-just-created` 3s) |
| **Forbidden** | Thiếu quyền | Redirect về `/error/403` với icon và CTA "Về dashboard" |
| **Not found** | URL sai / bản ghi đã xóa | `/error/404` với icon và CTA "Về danh sách" |
| **Confirming** | Trước destructive action | Modal xác nhận, nút Xóa màu `btn-danger` |
| **Read-only** | User không có quyền sửa | Ẩn action cluster, form input `readonly` + banner `alert-info` |

---

## 12 · Role-based UI matrix

| Menu / Feature | PDT | TTDTXS | CNHP | CVHT | GV | SV | DN | Admin |
|---|:---:|:---:|:---:|:---:|:---:|:---:|:---:|:---:|
| Dashboard | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **Đào tạo** | | | | | | | | |
| Chương Trình Đào Tạo (R) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ |
| CTDT (W — Tạo/Sửa/Xóa/Duyệt) | ✓ | ✓ | ✓ | — | — | — | — | ✓ |
| Học Phần (R) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ |
| Học Phần (W) | — | ✓ | ✓ | — | — | — | — | ✓ |
| Lớp Học Phần (R) | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | — | ✓ |
| Lớp Học Phần (W) | ✓ | ✓ | ✓ | — | GV chỉ lớp mình | — | — | ✓ |
| Học Kỳ (R/W) | ✓ | ✓ | — | — | — | — | — | ✓ |
| Lớp Hành Chính | ✓ | ✓ | — | ✓ | — | — | — | ✓ |
| **Con Người** | | | | | | | | |
| Người Dùng (list/R) | ✓ | ✓ | — | — | — | — | — | ✓ |
| Người Dùng (W) | — | — | — | — | — | — | — | ✓ |
| Hồ sơ cá nhân | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ | ✓ |
| **Đánh Giá** | | | | | | | | |
| Điểm của tôi | — | — | — | — | — | ✓ | — | — |
| Nhập điểm | — | — | — | — | ✓ | — | — | ✓ |
| Cảnh báo học vụ | ✓ | — | — | ✓ | — | — | — | ✓ |
| **Kiến Tập & Thực Tập** | | | | | | | | |
| Đợt kiến tập/thực tập | ✓ | ✓ | — | ✓ | ✓ | ✓ | ✓ | ✓ |
| Tạo đợt | ✓ | ✓ | — | — | — | — | — | ✓ |
| Đăng ký | — | — | — | — | — | ✓ | — | — |
| Chấm điểm | — | — | — | — | ✓ | — | ✓ (nhận xét) | ✓ |
| Doanh nghiệp (CRUD) | — | ✓ | — | — | — | — | — | ✓ |

**Nguyên tắc:**
- `R` = read, hiện menu + list + detail, ẩn mọi nút W.
- `W` = write, hiện nút Tạo/Sửa/Xóa/Duyệt.
- Menu hiện/ẩn qua `sec:authorize` trong `layout/base.html`.
- Action button trong list/detail hiện/ẩn qua `sec:authorize` inline.
- Method-level `@PreAuthorize` ở Controller là **last line of defense** — bắt buộc có dù UI đã ẩn.

---

## 13 · Module UX blueprints

### 13.1 Chương Trình Đào Tạo

**List:** filter theo khoa + khóa + trạng thái duyệt. Mỗi row: mã CTDT, tên, khóa, số HP, trạng thái, actions.

**Detail:** tab `Tổng quan` / `Khung chương trình (8 kỳ)` / `HP theo kỳ` / `File Word` / `Lịch sử duyệt`.

**Flow tạo CTDT (wizard 4 bước):**
1. Thông tin chung (mã, tên, khóa, số TC tối thiểu).
2. Khung kỳ (chọn kỳ, gán HP bắt buộc / tự chọn, số lớp dự kiến).
3. Xem lại (bảng tổng hợp HP × kỳ).
4. Tải file Word + gửi duyệt.

### 13.2 Học Phần

**List:** card view tùy chọn (toggle grid/list), filter theo loại + trạng thái + khoa chủ quản.

**Detail:** card hero + tabs `Tổng quan` / `CTDT đang sử dụng` / `Lớp đã mở` / `File đề cương (preview PDF)` / `Giảng viên đủ điều kiện`.

**State đặc biệt:**
- `Chờ Duyệt`: banner warning trên đầu trang + nút "Duyệt" / "Từ chối" cho PDT.
- `Từ Chối`: banner danger kèm lý do từ chối (multi-line text area).

### 13.3 Lớp Học Phần

**Filter 2 chiều bắt buộc:** CTDT × Học Kỳ.

**3 chế độ hiển thị:**
1. **Chỉ HK** → bảng "Kế hoạch mở lớp toàn trường" (nhóm theo CTDT, mỗi CTDT hiện kỳ quy đổi).
2. **CTDT + HK** → bảng "HP dự kiến" + "Tạo hàng loạt" wizard.
3. **Không filter** → bảng lớp đã mở (full list).

### 13.4 Học Kỳ

**List:** table theo thứ tự ngày bắt đầu giảm dần, row active (`Đang Diễn Ra`) có background nhạt `--accent-lt / 20%`.

**Form:**
- Ngày bắt đầu / kết thúc là nguồn chân lý cho trạng thái.
- Field trạng thái có hint giải thích quy tắc derive.
- Validation: nếu trạng thái chọn khác derived status → throw `BusinessException` với message rõ.

### 13.5 Người Dùng

**List:** tab theo loại (`Sinh Viên` / `Giảng Viên` / `Cán Bộ` / `Doanh Nghiệp`), mỗi tab có filter riêng.

**Row actions:** icon-only `btn-group-sm`: Xem · Sửa · Khóa/Mở khóa · Xóa. **Tất cả** đồng bộ icon-only (không mix text+icon).

**Detail:** tab động theo loại user:
- SV: `Hồ sơ` / `Lớp hành chính` / `Điểm theo kỳ` / `Cảnh báo` / `Đăng ký KT-TT`.
- GV: `Hồ sơ` / `HP đủ điều kiện dạy` / `Lớp đang dạy` / `Nhận xét sinh viên`.

### 13.6 Đánh Giá

**SV xem điểm:** bảng theo HK giảm dần, cột `Mã HP · Tên HP · TC · Điểm QT · Điểm TH · Điểm Tổng · Trạng thái`. GPA tổng hiển thị ở header.

**GV nhập điểm:** view "Nhập điểm lớp" — bảng có SV × cột điểm, inline edit, nút "Lưu Nháp" + "Công Bố".

**CVHT cảnh báo:** danh sách SV có GPA < 2.0 hoặc nợ ≥ 2 HP, sort theo mức nghiêm trọng.

### 13.7 Kiến Tập & Thực Tập

**Đợt:** list các đợt theo HK, mỗi đợt có: số SV đăng ký / tối đa, DN tham gia, trạng thái.

**Đăng ký (SV):** chọn đợt → chọn DN (nếu được phép) → xác nhận → trạng thái `Chờ CVHT duyệt`.

**Chấm điểm (GV + DN):** form chia 2 section "Nhận xét DN" (read-only cho GV) + "Điểm GV".

---

## 14 · Accessibility

### 14.1 Contrast (WCAG 2.1 AA)

- Text trên nền: ratio ≥ 4.5:1. Text cỡ ≥ 18pt hoặc ≥ 14pt bold: ratio ≥ 3:1.
- Icon-only button: phải có `aria-label` và tooltip.
- Link không được phân biệt chỉ bằng màu — phải có underline hoặc icon.

### 14.2 Keyboard

- Tab order: tuyến tính theo DOM, không dùng `tabindex > 0`.
- Focus ring: visible, dùng `--primary-lt` outline 2px.
- Modal: trap focus, `Esc` đóng modal.
- Dropdown/menu: `↑↓` điều hướng, `Enter` chọn, `Esc` đóng.

### 14.3 Semantic

- Dùng `<main>`, `<nav>`, `<header>`, `<aside>` đúng ngữ nghĩa.
- Icon decoration (`bi-*`) trong nút có text: `aria-hidden="true"`.
- Table phức tạp: `<caption>`, `<th scope="col">` / `scope="row"`.
- Form label: luôn gắn `<label for="...">`, không dùng placeholder thay label.

### 14.4 ARIA

- `aria-live="polite"` cho flash alert container.
- `aria-current="page"` cho sidebar link active.
- `aria-expanded` cho dropdown toggle.
- `role="alert"` cho inline error message.

---

## 15 · Responsive breakpoints

Theo Bootstrap 5.3, viết mobile-first:

| Breakpoint | Width | Hành vi |
|---|---|---|
| `xs` | < 576px | Sidebar offcanvas, stat card stack 1 cột, table scroll ngang |
| `sm` | ≥ 576px | Stat card 2 cột, toolbar wrap |
| `md` | ≥ 768px | Stat card 2 cột, form 2 cột |
| `lg` | ≥ 992px | Sidebar cố định, stat card 4 cột, desktop layout đầy đủ |
| `xl` | ≥ 1200px | Max container 1400px, filter toolbar không wrap |
| `xxl` | ≥ 1400px | — |

**Nguyên tắc:**
- Không có horizontal scroll ở viewport ≥ `md` trừ table.
- Touch target ≥ 44×44px trên mobile (tăng padding button).
- Modal trên mobile: `fullscreen` cho form phức tạp, `bottom sheet` cho confirm đơn giản.

---

## 16 · Content & Voice

### 16.1 Ngữ điệu

- **Lịch sự, trung tính, ngắn gọn.** Không dùng "bạn", thay bằng động từ trực tiếp: "Chọn học kỳ" thay vì "Bạn hãy chọn học kỳ".
- Không dùng viết hoa toàn bộ trừ `MÃ HP`, `CTDT`, `HK`.
- Không có dấu cảm thán (`!`). Thông báo thành công dùng dấu chấm: "Đã tạo học phần LTHDT101."

### 16.2 Error message format

```
[Tên nghiệp vụ] [không thể | bị từ chối] [lý do cụ thể] [gợi ý khắc phục].
```

Ví dụ:
- ✓ "Không thể kích hoạt học kỳ HK1-2025: ngày bắt đầu là 01/09/2025 (hôm nay 15/06/2025). Hãy chờ đến ngày bắt đầu hoặc chỉnh ngày."
- ✗ "Error: invalid state transition."

### 16.3 Labels

| Dùng | Không dùng |
|---|---|
| Mã Học Phần | Ma HP, HP Code |
| Ngày Bắt Đầu | Start Date, Ngày BD |
| Giảng Viên | Teacher, GV (trừ trong table header nơi cần ngắn) |
| Đang Diễn Ra | Active, Current |

### 16.4 Button text

- Động từ + danh từ: "Tạo Học Phần", "Duyệt Lớp", "Hủy Đăng Ký".
- Không dùng "OK" / "Submit" / "Go".

---

## 17 · Feedback contract

Mọi action POST **bắt buộc** tuân theo một trong 4 mô hình feedback:

### 17.1 Success-redirect (CRUD thành công)

```
POST /resource/them  →  redirect:/resource
                         + flash successMsg "Đã tạo {id}."
                         + row mới có class .row-just-created (highlight 3s)
```

### 17.2 Error-rerender (validation / business fail)

```
POST /resource/them  →  return "resource/form"
                         + model.errorMsg = e.getMessage()
                         + giữ nguyên input của user (bind lại th:object)
                         + log.error stack trace ở server
```

**Cấm** redirect + flash errorMsg vì flash có thể drop và user mất toàn bộ input.

### 17.3 Toast (inline action, không rời trang)

```
AJAX POST /resource/{id}/action  →  JSON { ok: true, msg: "..." }
                                    → Toast góc dưới phải, auto-dismiss 4s
```

Dùng cho: toggle yêu thích, quick-approve trong bulk view.

### 17.4 Confirm-then-act (destructive)

```
Click [Xóa]  →  Modal xác nhận
             →  Nút [Xóa] = btn-danger
             →  Nếu OK: submit form ẩn → success-redirect (17.1)
```

---

## 18 · Implementation checklist

### 18.1 Mỗi page mới

- [ ] Có `activeMenu` đúng key để sidebar highlight.
- [ ] Có page title (`<h1>` hoặc `.page-title`) với border-left `--accent`.
- [ ] Có breadcrumb nếu depth ≥ 2.
- [ ] Tất cả hard-coded color đã thay bằng `var(--token)`.
- [ ] Tất cả icon là `bi-*`.
- [ ] Mọi button icon-only có `data-bs-toggle="tooltip"` + `aria-label`.
- [ ] Form có validation inline + banner errorMsg.
- [ ] Có empty state khi list rỗng.
- [ ] Responsive check ở 375px, 768px, 1280px.

### 18.2 Mỗi component mới

- [ ] Documented trong section 9 của file này **trước** khi code.
- [ ] Dùng token, không magic number.
- [ ] Có đủ 4 state: default / hover / active / disabled.
- [ ] Keyboard accessible (Tab, Enter, Esc).
- [ ] Screen reader test với `aria-*`.

### 18.3 Mỗi PR UI

- [ ] Screenshot before/after đính kèm.
- [ ] Nếu touches design token: ghi vào changelog (section 19).
- [ ] Nếu touches role visibility: cập nhật section 12.
- [ ] QA pass ở desktop Chrome/Firefox + mobile Chrome.

---

## 19 · Changelog

| Ngày | Tác giả | Thay đổi |
|---|---|---|
| 2026-04-24 | Design Authority | Rebuild toàn bộ thành tài liệu production-grade; bổ sung section 10 (Page templates), 11 (State patterns), 12 (Role-based UI matrix), 13 (Module blueprints), 17 (Feedback contract), 18 (Checklist). |
| 2025-12-01 | Initial | Tạo file v1 với token + palette + typography cơ bản. |

---

> **Ghi chú cho developer:** Khi bạn tự hỏi "component/màu/khoảng cách này dùng cái nào?", nếu câu trả lời không nằm trong file này, **đừng tự quyết định** — đề xuất bổ sung vào file trước, sau đó mới implement. Điều đó đảm bảo hệ thống UI không bị drift theo thời gian.
