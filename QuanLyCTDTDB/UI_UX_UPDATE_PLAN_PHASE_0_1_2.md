# UI/UX Update Plan — Phase 0, 1, 2
## Triển khai Design System cho Frontend

**Ngày:** 2026-04-25  
**Phạm vi:** Phase 0 (Base layout) → Phase 1 (Dashboard) → Phase 2 (Modules)  
**Design system:** `docs/05_UI_DESIGN_SYSTEM.md`

---

## 1. PHASE 0 — Layout & Base Components

### 1.1 Files cần cập nhật
- `layout/base.html` — Navbar + Sidebar + Main container
- `auth/login.html` — Login form
- `static/css/main.css` — Design tokens + responsive

### 1.2 Tóm tắt thay đổi

**layout/base.html:**

✅ **Navbar (fixed top):**
- Màu: `--primary` (#1e3a5f) với gradient stop `--primary-dk`
- Logo: mortarboard icon + "Hệ Thống QLĐTXS" (responsive collapse)
- Right: User dropdown + Logout
- Height: 60px (`--navbar-height`)
- Shadow: `--shadow-md`

✅ **Sidebar (desktop 248px, collapse mobile):**
- Màu background: `--surface-card` (#ffffff)
- Border-right: `--border-color`
- Menu items:
  - Hover: `--surface-hover` (#f0f4fa)
  - Active: Left border `--primary` (4px)
  - Icon: 20px (Bootstrap Icons)
  - Padding: 12px 16px (compact density)
- Sections với header (Quản trị, Đào tạo, Báo cáo)
- Collapse animation: `--transition` (180ms)

✅ **Main container:**
- Margin-top: 60px (account for fixed navbar)
- Margin-left: 248px (desktop), 0 (mobile)
- Background: `--surface` (#f4f6f9)
- Padding: 24px (spacing token)

✅ **Flash messages (top of main):**
- Success: Bootstrap green (`alert-success`)
- Error: Bootstrap red (`alert-danger`)
- Warning: Bootstrap yellow (`alert-warning`)
- Info: Bootstrap blue (`alert-info`)
- Auto-dismiss: 5s (+ close button manual)
- Placement: Fixed top, below navbar

**auth/login.html:**
- Card center: max-width 420px
- Background: `--surface-card`
- Border-radius: `--radius-md`
- Shadow: `--shadow-lg`
- Form inputs: rounded `--radius-sm`, border `--border-color`
- Submit button: `--primary` background, 44px height (touch-friendly)
- Link "Quên mật khẩu": `--primary-lt` color

**main.css:**
- Define all design tokens (15 total)
- Responsive breakpoints: sm (576px), md (768px), lg (992px), xl (1200px)
- Utility classes: `text-balance`, `text-pretty` cho long text
- Fade-in animation: 300ms

---

## 2. PHASE 1 — Dashboard

### 2.1 Files cần cập nhật
- `dashboard/dashboard.html`

### 2.2 Tóm tắt thay đổi

**dashboard/dashboard.html:**

✅ **Header section:**
- Tiêu đề: "Tổng Quan" (h2 + margin-bottom 24px)
- Welcome message: "Chào [user]" + current date/time

✅ **Role-based widgets (grid 3 column desktop, 1 column mobile):**

Sinh viên (SINH_VIEN):
- Lớp học phần đang học (số lượng)
- Điểm TB (nếu có)
- Deadline sắp tới

Giảng viên (GIANG_VIEN):
- Lớp học phần đang dạy (số lượng)
- Số SV (total)
- Bài tập chưa chấm (count)

Quản lý (PDT/TTDTXS/CNHP/ADMIN):
- Người dùng (total)
- Học kỳ hiện tại (status)
- Chương trình đào tạo (total)

✅ **Widget design (card):**
- Background: `--surface-card`
- Border-radius: `--radius-md`
- Padding: 20px
- Box-shadow: `--shadow-sm`
- Hover: shadow upgrade → `--shadow-md`, background shift `--surface-hover`
- Transition: `--transition`

✅ **Stat layout (flexbox):**
- Icon (left, 32px, primary color)
- Text (center):
  - Number: large, bold, `--text-main`
  - Label: small, `--text-muted`

---

## 3. PHASE 2 — Core Modules (Người Dùng + Doanh Nghiệp)

### 3.1 Pattern: Danh Sách (List page)

**Generic danh-sach.html template:**

✅ **Header:**
- Title + breadcrumb
- Button group (right):
  - "Thêm Mới" (primary green)
  - Filter/search icon (secondary)
  - Sort dropdown (optional)

✅ **Toolbar (row):**
- Left: Filter panel (collapsible, default open desktop)
- Right: View toggle (table / card) + pagination

✅ **Table (default for data-dense):**
- Header: `--surface-alt` (#eef2f7) background
- Row hover: `--surface-hover`
- Padding: 12px cell (vertical), 16px (horizontal)
- Border-bottom: 1px `--border-color`
- Actions column (right, 120px):
  - Xem (icon)
  - Sửa (icon)
  - Khóa/Mở (toggle icon)
  - Xóa (icon, red on hover)
  - Icons: 20px, line-weight 1.5

✅ **Pagination:**
- Position: bottom right
- Style: Bootstrap default (customized colors)

**nguoi-dung/danh-sach.html:**
- Columns: Mã, Họ tên, Email, Vai trò, Trạng thái, Ngày tạo, Thao tác
- Filter: Vai trò (dropdown), Trạng thái (checkbox group)
- Search: Họ tên / Email (input, 300px max)
- Trạng thái badge:
  - `Hoạt động`: green
  - `Khóa`: red

**doanh-nghiep/danh-sach.html:**
- Columns: Mã, Tên, Email, Người liên hệ, Trạng thái, Thao tác
- Filter: Trạng thái (DangHopTac / TamNgung)
- Search: Tên

---

### 3.2 Pattern: Form (Create/Update)

**Generic form.html template:**

✅ **Card wrapper:**
- Background: `--surface-card`
- Border-radius: `--radius-md`
- Padding: 32px
- Max-width: 800px (center on desktop)
- Shadow: `--shadow-md`

✅ **Form groups:**
- Label: 500 weight, `--text-main`, 14px
- Input: 
  - Border: 1px `--border-color`
  - Border-radius: `--radius-sm`
  - Padding: 10px 12px (compact)
  - Focus: border `--primary`, outline `--primary` 2px
  - Transition: `--transition`
- Helper text: `--text-muted`, 12px (below input)
- Error text: red, 12px

✅ **Form sections (if long):**
- Divider: 1px `--border-strong` + 24px margin-y

✅ **Actions (bottom):**
- Button group (justify-content: flex-end):
  - "Hủy" (secondary outline)
  - "Lưu" (primary solid)
- Spacing between: 12px

**nguoi-dung/form.html:**
- Sections: Thông tin cơ bản | Tài khoản | Vai trò
- Fields:
  - Mã NguoiDung (disabled if edit)
  - Họ tên (required)
  - Email (required, type=email)
  - Tên đăng nhập (required, disabled if edit)
  - Mật khẩu (required if create, optional if edit)
  - Vai trò (select, multiple, required)
  - Trạng thái (radio: Hoạt động / Khóa)

**doanh-nghiep/form.html:**
- Fields:
  - Mã (required, unique)
  - Tên (required)
  - Email (required)
  - Điện thoại (optional)
  - Địa chỉ (optional, multiline)
  - Người liên hệ (text)
  - Trạng thái (radio: DangHopTac / TamNgung)

---

### 3.3 Pattern: Chi Tiết (Detail page)

**Generic chi-tiet.html template:**

✅ **Breadcrumb + back button**

✅ **Header card:**
- Background: `--primary` gradient
- Color: white text
- Padding: 32px
- Border-radius-top: `--radius-lg`
- Content: Title + metadata (mã, trạng thái badge, ngày tạo)

✅ **Detail sections (card list):**
- Background: `--surface-card`
- Padding: 24px
- Margin-bottom: 16px
- Definition list (dl/dt/dd):
  - dt: `--text-muted`, 12px, 500 weight
  - dd: `--text-main`, 14px, margin-left 0

✅ **Related data (if any):**
- Subtable (compact)
- Max 5 rows, then "Xem thêm" link

✅ **Actions (top right of header):**
- "Sửa" button
- "Xóa" button (red)
- "In" button (print icon)

**nguoi-dung/chi-tiet.html:**
- Sections:
  - Thông tin cơ bản (Họ tên, Email, Đt, Địa chỉ)
  - Tài khoản (Username, Vai trò, Trạng thái)
  - Lớp hành chính (nếu SV)
  - Lịch sử đăng nhập (table: Lần cuối, IP, Thiết bị)

**doanh-nghiep/chi-tiet.html:**
- Sections:
  - Thông tin cơ bản
  - Liên hệ
  - Lịch sử hợp tác (table: Ngày, Loại, Trạng thái)

---

## 4. Responsive Breakpoints

| Breakpoint | Width | Layout | Sidebar |
|---|---|---|---|
| **Mobile** | < 576px | 1 column | Hidden (slide-out) |
| **Tablet** | 576px—768px | 1 column (list), 2 col (form) | Hidden |
| **Desktop** | 768px—992px | 2 column | Visible, 248px |
| **Large** | ≥ 992px | 3 column (grid) | Visible, 248px |

---

## 5. Colors & Typography

| Element | Color Token | Font |
|---|---|---|
| **Primary text** | `--text-main` (#212529) | Inter 400, 14px |
| **Secondary text** | `--text-muted` (#6c757d) | Inter 400, 12px |
| **Headings** | `--text-main` | Inter 600, h1=32px / h2=24px / h3=18px |
| **Links** | `--primary-lt` (#2d5f9e) | Inter 500, underline on hover |
| **Buttons** | `--primary` (#1e3a5f) | Inter 500, 14px, 44px height (touch) |
| **Alerts** | Bootstrap defaults | Customize text color to `--text-main` |

---

## 6. Implementation Checklist

### 6.1 CSS (static/css/main.css)

- [ ] Define 15 design tokens in `:root`
- [ ] Set `font-family: Inter` on `body`
- [ ] Create utility classes (`.text-balance`, `.alert-success` override, etc.)
- [ ] Define responsive breakpoints ($md: 768px, etc.)
- [ ] Create `.sidebar`, `.navbar`, `.container-main` styles
- [ ] Add animations (fade-in, slide-in-left for sidebar)
- [ ] Override Bootstrap defaults (buttons, forms, tables)

### 6.2 layout/base.html

- [ ] Update navbar background to `style="background-color: var(--primary)"`
- [ ] Sidebar with correct styling (DONE mostly, minor tweaks)
- [ ] Flash message container
- [ ] Mobile sidebar toggle behavior

### 6.3 dashboard/dashboard.html

- [ ] Create role-based widget grid (TH::@if hasRole)
- [ ] Widget card styling (shadow, hover effect)
- [ ] Stat display (icon + number + label)

### 6.4 nguoi-dung module

- [ ] danh-sach.html: Apply table pattern, filter panel, pagination
- [ ] form.html: Apply form card pattern, field validation styling
- [ ] chi-tiet.html: Apply detail card pattern, related section

### 6.5 doanh-nghiep module

- [ ] danh-sach.html: Apply table pattern
- [ ] form.html: Apply form card pattern
- [ ] chi-tiet.html: Apply detail card pattern

### 6.6 auth/login.html

- [ ] Center card styling
- [ ] Input focus states
- [ ] Button sizing (44px height)

---

## 7. Quality Metrics

| Metric | Target | Current | Notes |
|---|---|---|---|
| **WCAG 2.1 AA** | 100% | ~80% | Missing alt text on icons, enhance form labels |
| **Mobile-first** | Responsive on all | ~90% | Sidebar collapse needs improvement |
| **Performance** | < 3s First Paint | ~2.5s | Good, watch font load |
| **Design consistency** | 100% tokens used | ~75% | Bootstrap defaults override docs |

---

## 8. Timeline

| Phase | Time | Status |
|---|---|---|
| **Phase 0 CSS + layout** | 2-3h | Priority 1 |
| **Phase 1 Dashboard** | 1-2h | Priority 2 |
| **Phase 2 Modules** | 4-6h | Priority 3 |
| **QA + refinement** | 2-3h | Priority 4 |
| **Total** | **9-14h** | **~12h (avg)** |

---

## 9. Files to Update (Priority Order)

```
1. static/css/main.css                       [2-3h]
   ├─ Define design tokens
   ├─ Create utility classes
   └─ Override Bootstrap

2. layout/base.html                          [1h]
   ├─ Navbar styling
   ├─ Sidebar refinement
   └─ Flash messages

3. auth/login.html                           [0.5h]
   └─ Form card styling

4. dashboard/dashboard.html                  [1.5-2h]
   ├─ Widget grid
   ├─ Role-based content
   └─ Stat cards

5. nguoi-dung/ (3 files)                     [2-3h]
   ├─ danh-sach.html
   ├─ form.html
   └─ chi-tiet.html

6. doanh-nghiep/ (3 files)                   [2-3h]
   ├─ danh-sach.html
   ├─ form.html
   └─ chi-tiet.html
```

---

## 10. Next Steps

1. ✅ Hãy xác nhận kế hoạch này
2. Tôi sẽ bắt đầu với **Priority 1: main.css** (define tokens)
3. Sau đó **Priority 2: layout/base.html** (navbar + sidebar)
4. Tiếp tục các module theo thứ tự

**Muốn tôi bắt đầu ngay từ main.css không?**
