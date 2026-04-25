# UI/UX Update Plan — Phase 3 (Production-Grade Polish)
## Hoàn Thiện Toàn Bộ Frontend Theo Design System

**Ngày:** 2026-04-25
**Phạm vi:** Phase 3 (CTDT, Học Phần, Học Kỳ, Lớp HP, Lớp HC, Profile, Errors) +
Refactor design tokens dùng chung + nâng cấp Phase 0/1/2 lên chuẩn production.
**Design system:** `docs/05_UI_DESIGN_SYSTEM.md` (v2)
**Tham chiếu:** `UI_UX_UPDATE_PLAN_PHASE_0_1_2.md`

---

## 0. Mục Tiêu Phase 3

Sau Phase 0/1/2 hệ thống đã có:
- Design tokens (`:root` trong `main.css`)
- Layout chuẩn (navbar fixed, sidebar 248 px, main content offset)
- Login 2-panel, Dashboard role-based, Người Dùng + Doanh Nghiệp module

Phase 3 đưa toàn bộ giao diện lên mức **production-grade** bằng cách:

| # | Mục tiêu | Phương pháp |
|---|---|---|
| 1 | **Tính nhất quán** trên 30+ template Thymeleaf | Card-header chuẩn hóa, status-badge chuẩn hóa, hero header chuẩn hóa |
| 2 | **Tuân thủ design system** không drift | Card-header default = light/neutral (đúng spec 9.3), dark variant phải opt-in |
| 3 | **Mật độ thông tin tối ưu** mà vẫn dễ đọc | Bảng compact 11–12 px padding, info-row 9 px, page header gọn |
| 4 | **Feedback dễ thấy** cho mọi action | Toast + flash + row-just-created highlight 3 s |
| 5 | **Hero header** trên trang chi tiết | Block "name + meta + actions" thống nhất, có status pill |
| 6 | **Filter toolbar** rõ ràng và sticky | `.filter-toolbar` riêng, không nhầm với card content |
| 7 | **Tab navigation** sạch trên detail dày | `.tab-nav-clean` cho CTDT, Học Phần |
| 8 | **Status pill** có dot + icon | `.status-pill.tone-*` thay nhiều badge inline |

---

## 1. Inventory — Tất Cả Template Cần Cập Nhật

### 1.1 Module Đào Tạo (Phase 3 trọng tâm)

| File | Hiện trạng | Phase 3 |
|---|---|---|
| `ctdt/danh-sach.html` | OK, header/filter sơ sài | Stat row + filter-toolbar + status-pill, pagination |
| `ctdt/form.html` | `bg-white border-bottom` override | Bỏ override, dùng card-header default mới |
| `ctdt/chi-tiet.html` | `bg-white` override + thiếu hero | Hero header (mã / khóa / status pill / actions), card-header default |
| `hoc-phan/danh-sach.html` | OK, status badge chưa có icon | Stat row + filter-toolbar + status-pill, pagination |
| `hoc-phan/form.html` | OK | Section heading rõ hơn, button group đáy chuẩn |
| `hoc-phan/chi-tiet.html` | OK | Hero header + tabs (Tổng quan / Đội ngũ GV) |
| `hoc-ky/danh-sach.html` | Có stat row tốt | status-pill thay badge thường, row đang diễn ra highlight |
| `hoc-ky/form.html` | Tốt | section-label đồng bộ, helper text giữ nguyên |
| `lop-hoc-phan/danh-sach.html` | `bg-white` override + filter mơ hồ | filter-toolbar + status-pill cho cột Trạng Thái |
| `lop-hoc-phan/chi-tiet.html` | `bg-white` override + 2 cột | Hero header + status-pill cảnh báo |
| `lop-hanh-chinh/danh-sach.html` | Stat row OK | Khóa pill chuẩn, action cluster icon-only nhất quán |
| `lop-hanh-chinh/form.html` | OK | Section-label đồng bộ |
| `lop-hanh-chinh/chi-tiet.html` | Thiếu hero | Hero header + 2 col layout giữ nguyên |

### 1.2 Module Quản Trị (rà lại sau Phase 2)

| File | Cập nhật |
|---|---|
| `nguoi-dung/danh-sach.html` | status-pill, action cluster nhất quán |
| `nguoi-dung/form.html` | Đã đẹp, chỉ refine spacing |
| `nguoi-dung/chi-tiet.html` | Hero header + role-pill (đã có) |
| `nguoi-dung/import.html` | Card-header tone-info |
| `doanh-nghiep/*` | status-pill, hero header chi-tiet |

### 1.3 Trang dùng chung

| File | Cập nhật |
|---|---|
| `layout/base.html` | Chip "đường tắt" trong navbar, focus-skip-link a11y |
| `auth/login.html` | Refine subtle gradient, padding nhỏ hơn, footer link policy |
| `dashboard/dashboard.html` | Welcome card refine, stat group label, quick-action group |
| `profile/profile.html` | Avatar lớn ở header card, tabs avatar/account/security |
| `error/403.html`, `404.html`, `500.html` | OK, chỉ thống nhất CTA |

---

## 2. Refactor `static/css/main.css`

### 2.1 Card header — đổi default về **light** (đúng design system 9.3)

**Trước:**
```css
.card-header {
    background: var(--primary);     /* dark blue gradient */
    color: #fff;
    /* ... */
}
```

**Sau:**
```css
.card-header {
    background: var(--surface-card);
    color: var(--text-main);
    border-bottom: 1px solid var(--border-color);
    border-radius: var(--radius-md) var(--radius-md) 0 0;
    padding: 14px 18px;
    font-weight: 600;
    font-size: 0.92rem;
    letter-spacing: 0.1px;
}
.card-header .badge.bg-light { background: var(--surface-alt) !important; }

/* Opt-in: header dày, dùng cho hero card hoặc card "kết quả import" */
.card-header.card-header-hero {
    background: linear-gradient(180deg, var(--primary) 0%, var(--primary-dk) 100%);
    color: #fff;
    border-bottom: none;
}
.card-header.card-header-hero .badge.bg-light {
    background: rgba(255,255,255,0.18) !important;
    color: #fff !important;
}
```

**Hệ quả:** Mọi card hiện tại tự động trở thành "light header" đúng design system.
Các template đã `bg-white border-bottom` (CTDT, lop-hoc-phan) **không cần đổi**
vì kết quả render trùng. Card-header dark gradient **chỉ** dành cho hero block.

### 2.2 Hero header trên trang chi tiết

```css
.detail-hero {
    background: linear-gradient(135deg, var(--primary) 0%, var(--primary-dk) 100%);
    color: #fff;
    border-radius: var(--radius-md);
    padding: 24px 28px;
    margin-bottom: 24px;
    position: relative;
    overflow: hidden;
}
.detail-hero::after {
    content: '';
    position: absolute;
    right: -60px;
    top: -60px;
    width: 220px;
    height: 220px;
    background: radial-gradient(circle, rgba(232,160,32,0.18) 0%, transparent 70%);
    pointer-events: none;
}
.detail-hero .detail-hero-eyebrow {
    text-transform: uppercase;
    font-size: 0.72rem;
    letter-spacing: 1.2px;
    opacity: 0.78;
    font-weight: 600;
    margin-bottom: 6px;
}
.detail-hero .detail-hero-title {
    font-size: 1.4rem;
    font-weight: 700;
    margin-bottom: 8px;
    letter-spacing: -0.012em;
}
.detail-hero .detail-hero-meta {
    font-size: 0.85rem;
    opacity: 0.86;
    display: flex;
    flex-wrap: wrap;
    gap: 14px;
    align-items: center;
}
.detail-hero .detail-hero-actions {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
}
.detail-hero code {
    background: rgba(255,255,255,0.18);
    color: #fff;
    border-radius: var(--radius-sm);
    padding: 2px 8px;
    font-size: 0.78rem;
}
```

### 2.3 Filter toolbar dedicated

```css
.filter-toolbar {
    background: var(--surface-card);
    border: 1px solid var(--border-color);
    border-radius: var(--radius-md);
    padding: 14px 16px;
    margin-bottom: 16px;
    box-shadow: var(--shadow-sm);
}
.filter-toolbar .filter-toolbar-title {
    font-size: 0.72rem;
    text-transform: uppercase;
    letter-spacing: 0.6px;
    color: var(--text-subtle);
    font-weight: 600;
    margin-bottom: 10px;
    display: flex;
    align-items: center;
    gap: 6px;
}
```

### 2.4 Status pill (thay thế nhiều badge inline)

```css
.status-pill {
    display: inline-flex;
    align-items: center;
    gap: 6px;
    padding: 4px 10px;
    border-radius: 999px;
    font-size: 0.75rem;
    font-weight: 600;
    letter-spacing: 0.2px;
    line-height: 1.4;
    border: 1px solid transparent;
}
.status-pill .pill-dot {
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: currentColor;
    display: inline-block;
}
.status-pill.tone-success { background: #e9f7ef; color: #0f5132; border-color: #c6e9d3; }
.status-pill.tone-warning { background: #fff5e0; color: #7a5d05; border-color: #ffe4a3; }
.status-pill.tone-danger  { background: #fbeaea; color: #842029; border-color: #f1c2c2; }
.status-pill.tone-info    { background: #e7f1fb; color: #0c4667; border-color: #c7dcf3; }
.status-pill.tone-neutral { background: var(--surface-alt); color: var(--text-muted); border-color: var(--border-color); }
.status-pill.tone-primary { background: rgba(45,95,158,0.12); color: var(--primary); border-color: rgba(45,95,158,0.2); }
```

### 2.5 Tab navigation cho detail pages

```css
.tab-nav-clean {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
    border-bottom: 1px solid var(--border-color);
    margin-bottom: 18px;
    padding-bottom: 0;
}
.tab-nav-clean .tab-link {
    padding: 10px 14px;
    color: var(--text-muted);
    text-decoration: none;
    font-size: 0.875rem;
    font-weight: 500;
    border-bottom: 2px solid transparent;
    margin-bottom: -1px;
    transition: color var(--transition), border-color var(--transition);
}
.tab-nav-clean .tab-link:hover { color: var(--primary-lt); }
.tab-nav-clean .tab-link.active {
    color: var(--primary);
    border-bottom-color: var(--accent);
    font-weight: 600;
}
```

### 2.6 Row "vừa tạo" highlight

```css
@keyframes rowHighlight {
    0%   { background-color: rgba(232, 160, 32, 0.22); }
    100% { background-color: transparent; }
}
.row-just-created { animation: rowHighlight 3s ease-out; }
```

### 2.7 Action cluster (đồng bộ icon-only buttons)

```css
.action-cluster {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    flex-wrap: nowrap;
    justify-content: center;
}
.action-cluster .btn { padding: 4px 8px; }
.action-cluster .btn i { font-size: 0.95rem; }
```

### 2.8 Skip-link a11y (navbar)

```css
.skip-link {
    position: absolute;
    top: -48px;
    left: 8px;
    background: var(--primary);
    color: #fff;
    padding: 8px 14px;
    border-radius: var(--radius-sm);
    z-index: 1100;
    transition: top 180ms;
    font-weight: 500;
}
.skip-link:focus { top: 8px; }
```

---

## 3. Sửa `layout/base.html`

- Thêm `<a href="#mainContent" class="skip-link">Bỏ qua menu</a>` đầu `<body>`.
- Flash banner: thêm `aria-live="polite"`.
- Đảm bảo `id="mainContent"` ở `<main>` để skip-link nhảy đúng chỗ.

---

## 4. Trang Chi Tiết — Mẫu Hero Header

```html
<section class="detail-hero">
    <div class="d-flex flex-wrap justify-content-between gap-3 align-items-start">
        <div>
            <p class="detail-hero-eyebrow">Đào Tạo / Học Phần</p>
            <h1 class="detail-hero-title">LTHDT101 — Lập Trình Hướng Đối Tượng</h1>
            <div class="detail-hero-meta">
                <span><i class="bi bi-bookmark-star me-1"></i>3 TC</span>
                <span><i class="bi bi-tag me-1"></i>Bắt Buộc</span>
                <span class="status-pill tone-success">
                    <span class="pill-dot"></span>Đã Duyệt
                </span>
            </div>
        </div>
        <div class="detail-hero-actions">
            <a class="btn btn-light btn-sm">Sửa</a>
            <a class="btn btn-outline-light btn-sm">Quay lại</a>
        </div>
    </div>
</section>
```

Áp dụng cho:
- `ctdt/chi-tiet.html`
- `hoc-phan/chi-tiet.html`
- `lop-hoc-phan/chi-tiet.html`
- `lop-hanh-chinh/chi-tiet.html`
- `nguoi-dung/chi-tiet.html`
- `doanh-nghiep/chi-tiet.html`

---

## 5. Trang Danh Sách — Pattern Chuẩn

```html
<!-- 1. Breadcrumb -->
<nav aria-label="breadcrumb"> ... </nav>

<!-- 2. Page header: title + primary action -->
<div class="d-flex justify-content-between align-items-start mb-4 flex-wrap gap-2">
    <div>
        <p class="page-subtitle">Module / Section</p>
        <h4 class="page-title mb-0">Tên Trang</h4>
    </div>
    <a class="btn btn-primary btn-sm">+ Tạo Mới</a>
</div>

<!-- 3. Stat row (nếu module có thống kê) -->
<div class="row g-3 mb-4">
    <div class="col-6 col-md-3">
        <div class="stat-card variant-info">...</div>
    </div>
</div>

<!-- 4. Filter toolbar -->
<div class="filter-toolbar">
    <p class="filter-toolbar-title">
        <i class="bi bi-funnel"></i>Bộ Lọc
    </p>
    <form class="row g-2 align-items-end">
        <!-- search + filters + actions -->
    </form>
</div>

<!-- 5. Result card -->
<div class="card">
    <div class="card-header d-flex justify-content-between align-items-center">
        <span class="fw-semibold">
            <i class="bi bi-table me-2"></i>Danh Sách ...
        </span>
        <span class="badge bg-light text-dark">Tổng: N</span>
    </div>
    <div class="card-body p-0">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">...</table>
        </div>
    </div>
    <div class="card-footer bg-transparent">
        <!-- pagination + page-size + "Hiển thị X-Y / Z" -->
    </div>
</div>
```

---

## 6. Status Pills — Quy Ước

| Trạng thái | Tone | Icon dot |
|---|---|---|
| `Đã Duyệt`, `Đang Hợp Tác`, `Đang Mở`, `Đang Diễn Ra`, `Hoạt Động` | `tone-success` | ● xanh |
| `Chờ Duyệt` | `tone-warning` | ● vàng |
| `Bản Nháp`, `Đã Đóng`, `Đã Khoá`, `Tạm Ngưng`, `Đã Kết Thúc` | `tone-neutral` | ● xám |
| `Từ Chối`, `Đã Hủy`, `Cảnh Báo SV` | `tone-danger` | ● đỏ |
| `Sắp Diễn Ra` | `tone-info` | ● xanh dương |
| `Bắt Buộc` (HP) | `tone-primary` | ● navy |

---

## 7. Action Cluster Quy Ước

Tất cả action button trong table row **icon-only**, dùng `data-bs-toggle="tooltip"`
và `aria-label`. Thứ tự cố định:

1. **Xem** (`bi-eye`, `btn-outline-secondary`)
2. **Sửa** (`bi-pencil`, `btn-outline-primary`) — ẩn nếu trạng thái cấm
3. **Toggle** (Khoá/Mở, Pause/Play) — `btn-outline-warning` ↔ `btn-outline-success`
4. **Duyệt** (`bi-check-lg`, `btn-outline-success`) — chỉ khi `Chờ Duyệt`
5. **Từ Chối** (`bi-x-lg`, `btn-outline-danger`) — chỉ khi `Chờ Duyệt`
6. **Xoá** (`bi-trash`, `btn-outline-danger`) — luôn cuối, có confirm modal

---

## 8. Form Pattern Chuẩn (đã áp dụng cho 90% form)

```html
<div class="row justify-content-center">
    <div class="col-lg-9">
        <!-- Banner errorMsg (non-field) ở ngoài form là đúng -->
        <div th:if="${errorMsg}" class="alert alert-danger">...</div>

        <form th:object="${dto}" novalidate>
            <!-- Banner #fields.hasErrors('*') BẮT BUỘC trong form -->
            <div th:if="${#fields.hasErrors('*')}" class="alert alert-warning">...</div>

            <div class="card mb-3">
                <div class="card-header">
                    <i class="bi bi-..."></i>
                    <span class="fw-semibold">Tên Section</span>
                </div>
                <div class="card-body">
                    <h6 class="section-label">Nhóm Field 1</h6>
                    <!-- form-floating với validation -->
                </div>
            </div>

            <div class="d-flex justify-content-end gap-2">
                <a class="btn btn-outline-secondary">Huỷ</a>
                <button class="btn btn-primary">Lưu</button>
            </div>
        </form>
    </div>
</div>
```

---

## 9. Accessibility — Bắt Buộc

- [ ] `<main id="mainContent">` + skip-link.
- [ ] Mọi icon-only button có `aria-label` (đa số đã có).
- [ ] Flash container: `role="alert"` + `aria-live="polite"`.
- [ ] Focus ring rõ trên tất cả input/button (đã có via `:focus-visible`).
- [ ] Sidebar link active: `aria-current="page"`.
- [ ] Không dùng `tabindex` dương; chỉ `0` hoặc `-1`.

---

## 10. Implementation Checklist

### 10.1 CSS (`static/css/main.css`)

- [x] Card-header default → light (`--surface-card` + border-bottom)
- [x] Thêm `.card-header-hero` opt-in cho dark
- [x] Thêm `.detail-hero` block + utilities
- [x] Thêm `.filter-toolbar`
- [x] Thêm `.status-pill` + 6 tone variants
- [x] Thêm `.tab-nav-clean`
- [x] Thêm `.row-just-created` animation
- [x] Thêm `.action-cluster`
- [x] Thêm `.skip-link`

### 10.2 layout

- [x] Skip-link đầu body
- [x] `aria-live="polite"` cho flash container

### 10.3 Module Đào Tạo

- [x] `ctdt/danh-sach.html`: stat row + status-pill + pagination
- [x] `ctdt/chi-tiet.html`: detail-hero + bỏ override `bg-white`
- [x] `ctdt/form.html`: bỏ override `bg-white`
- [x] `hoc-phan/danh-sach.html`: filter-toolbar + status-pill
- [x] `hoc-phan/chi-tiet.html`: detail-hero
- [x] `hoc-ky/danh-sach.html`: status-pill cho cột trạng thái
- [x] `lop-hoc-phan/danh-sach.html`: bỏ override `bg-white`, status-pill
- [x] `lop-hoc-phan/chi-tiet.html`: detail-hero, bỏ override `bg-white`
- [x] `lop-hanh-chinh/chi-tiet.html`: detail-hero

### 10.4 Module Quản Trị (refine)

- [x] `nguoi-dung/danh-sach.html`: status-pill cho trạng thái + loại
- [x] `nguoi-dung/chi-tiet.html`: detail-hero (avatar + tên + role pills)
- [x] `doanh-nghiep/danh-sach.html`: status-pill
- [x] `doanh-nghiep/chi-tiet.html`: detail-hero

### 10.5 Trang chung

- [x] `auth/login.html`: refine padding, footer link
- [x] `dashboard/dashboard.html`: refine welcome card
- [x] `profile/profile.html`: avatar header

---

## 11. Quality Targets

| Metric | Trước | Sau Phase 3 |
|---|---|---|
| Card header consistency | ~60% | 100% |
| Status badge có icon/dot | ~30% | 100% (status-pill) |
| Detail page có hero | ~10% | 100% |
| Filter toolbar có visual identity | 0% | 100% |
| Action cluster đồng bộ | ~70% | 100% |
| WCAG 2.1 AA contrast | ~85% | ~95% (skip-link + aria-live + focus ring) |

---

## 12. Changelog

| Ngày | Tác giả | Nội dung |
|---|---|---|
| 2026-04-25 | UI Polish Pass 3 | Hero header, status-pill, filter-toolbar, tab-nav-clean, card-header-hero opt-in, skip-link a11y; refactor 25+ template Thymeleaf cho production. |
| 2026-04-24 | Phase 0/1/2 Plan | Layout + Dashboard + Người Dùng + Doanh Nghiệp. |

---

> **Quy tắc cho dev sau Phase 3:** trước khi tạo card-header dark, badge trạng thái
> mới, hoặc layout chi tiết mới — đối chiếu file này. Nếu không có pattern phù hợp,
> **đề xuất bổ sung vào file này trước**, sau đó mới implement. Đó là cách giữ
> design system không drift theo thời gian.
