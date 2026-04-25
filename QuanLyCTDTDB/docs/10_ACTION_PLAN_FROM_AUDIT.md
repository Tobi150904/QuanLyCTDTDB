# 10_ACTION_PLAN_FROM_AUDIT.md

> **Purpose:** Organized action plan to resolve all audit findings  
> **Owner:** Development Team  
> **Timeline:** Phase by phase (Immediate → Long-term)

---

## PHASE 1: IMMEDIATE (< 1 hour)

### Action 1.1: Fix Issue 3.3 — Thymeleaf `#fields` scope in docs
**Issue:** `05_UI_DESIGN_SYSTEM.md` shows `#fields.hasErrors('*')` outside `<form th:object>` (wrong).

**Steps:**
1. Open `05_UI_DESIGN_SYSTEM.md`, find section §11.2 "Form Patterns"
2. Locate code snippet with `#fields.hasErrors('*')`
3. Move the `<div th:if="${#fields.hasErrors('*')}">` block **inside** the `<form th:object>` element
4. Add comment: `<!-- MUST be inside <form th:object> for binding context -->`
5. Verify example matches actual templates (hoc-phan/form.html, ctdt/form.html)

**Effort:** 10 min  
**Done:** Git commit message: "docs: fix #fields scope example in UI_DESIGN_SYSTEM"

---

### Action 1.2: Rename file — Fix filename hygiene
**Issue:** `02_Mô Tả & Thiết kế dữ liệu.md` contains Vietnamese diacritics + `&` → fragile.

**Steps:**
1. Rename file: `mv docs/02_Mô\ Tả\ \&\ Thiết\ kế\ dữ\ liệu.md docs/02_DATA_DESIGN.md`
2. Update all cross-references:
   - `grep -r "02_Mô Tả" docs/*.md`
   - Replace `02_Mô Tả & Thiết kế dữ liệu.md` → `02_DATA_DESIGN.md`
   - Update table of contents in `00_MASTER_REFERENCE.md` if exists
3. Test: `ls docs/02*`

**Effort:** 5 min  
**Done:** Git commit: "docs: rename 02 file to ASCII (remove Vietnamese diacritics + &)"

---

### Action 1.3: Add @Deprecated annotation to `doiTrangThai()`
**Issue:** Issue 2.1 — method still callable but deprecated in batch 4 (status is auto-derive now).

**Steps:**
1. Open `HocKyNamHocServiceImpl.java`
2. Find method `doiTrangThai(String maHocKy, TrangThaiHocKy moi)`
3. Add annotation + JavaDoc:
   ```java
   /**
    * @deprecated as of batch 4: Status is now auto-derived from dates via deriveStatus().
    * User manual status changes are no longer exposed in UI. Method retained for
    * backward compatibility if external systems call it; validation still enforces
    * consistency. Use resyncStatuses() or update() instead.
    */
   @Deprecated(since = "batch4", forRemoval = false)
   public HocKyNamHoc doiTrangThai(String maHocKy, TrangThaiHocKy moi) {
     ...
   }
   ```

**Effort:** 5 min  
**Done:** Git commit: "code: mark doiTrangThai() deprecated (status auto-derive in batch 4)"

---

## PHASE 2: HIGH-PRIORITY (1-2 hours)

### Action 2.1: Update 03_WORKFLOW.md — Clarify `doiTrangThai()` deprecation
**Issue:** Issue 2.1 — docs don't explain status is auto-derive only.

**Steps:**
1. Open `03_WORKFLOW.md`, find section about HocKyNamHoc workflow
2. Add subsection:
   ```markdown
   ### HocKyNamHoc Status State Machine (batch 4 update)
   
   Status is **auto-derived** from dates — user cannot manually select status.
   
   **Derivation rule:**
   - today < ngayBatDau → SapDienRa
   - ngayBatDau ≤ today ≤ ngayKetThuc → DangDienRa  
   - today > ngayKetThuc → DaKetThuc
   
   **When triggered:**
   - `create()`: always auto-assign status via deriveStatus()
   - `update()`: if user tries to change status manually → throw BusinessException with message:
     "Trạng thái 'X' không khớp với khoảng ngày..."
   - `findAll()`: resync all HK (in case admin back-dates ngay; except DaKetThuc stays immutable)
   
   **UI Behavior:**
   - Form for creating HK no longer has status dropdown (removed batch 4)
   - Nut "Kich Hoat" removed from danh-sach (status auto-updates on page reload)
   - If user tries to override: error message guides to adjust ngay instead
   
   **Legacy Note:** Method `doiTrangThai()` still exists for backward compatibility but is
   deprecated — not exposed in UI anymore.
   ```
3. Update batch 4 checklist to reference this section

**Effort:** 20 min  
**Done:** Git commit: "docs: clarify HK status auto-derive in workflow"

---

### Action 2.2: Update 06_PROJECT_SCAFFOLD.md — Batch 3 clarity
**Issue:** Issue 4.3 — Batch 3 BCN/DoiNguGV features marked as "implemented" but verification unclear.

**Steps:**
1. Open `06_PROJECT_SCAFFOLD.md`, find batch 3 section
2. Audit: Check if these endpoints are **fully implemented & tested**:
   - `POST /ctdt/chi-tiet/{ma}/bcn/them` — add BCN member
   - `POST /ctdt/chi-tiet/{ma}/bcn/xoa` — remove BCN member
   - `POST /hoc-phan/chi-tiet/{ma}/doi-ngu/them` — add GV to DoiNguGV
   - `POST /hoc-phan/chi-tiet/{ma}/doi-ngu/toggle` — toggle GV status
   - `POST /hoc-phan/chi-tiet/{ma}/doi-ngu/xoa` — remove GV from DoiNguGV
3. **If NOT fully implemented:**
   - Mark batch 3 as "[PARTIAL — UI/templates incomplete]"
   - Move specific endpoints to "Phase 3.X — Batch 4 Part 2" roadmap section
4. **If fully implemented:**
   - Add concrete verification URLs
   - Link to controller methods for audit trail

**Effort:** 30 min (investigation) + 15 min (doc update)  
**Done:** Git commit: "docs: clarify batch 3 implementation status"

---

## PHASE 3: MEDIUM-PRIORITY (2-4 hours)

### Action 3.1: Add @InitBinder documentation to 03_WORKFLOW.md
**Issue:** Issue 3.1 — Batch 4 @InitBinder pattern not documented.

**Steps:**
1. Open `03_WORKFLOW.md`
2. Find or create section "Form Data Binding & File Upload"
3. Add subsection:
   ```markdown
   ### @InitBinder — Disallow File Upload Field Binding
   
   **Problem:** When form has `<input type="file" name="fileDeCuong">` inside `<form th:object="hocPhanDTO">`,
   Spring MVC tries to convert MultipartFile → String into DTO field. This throws:
   ```
   ConversionNotSupportedException: Cannot convert value of type
   'org.springframework.web.multipart.support.StandardMultipartHttpServletRequest$StandardMultipartFile'
   to required type 'java.lang.String' for property 'fileDeCuong'
   ```
   
   **Solution:** Use `@InitBinder` to disallow the problematic field:
   ```java
   @InitBinder("hocPhanDTO")
   public void initHocPhanBinder(WebDataBinder binder) {
       binder.setDisallowedFields("fileDeCuong");
   }
   ```
   
   File is still received correctly via **separate** `@RequestParam("fileDeCuong") MultipartFile` in method signature.
   
   **Controllers using this pattern:**
   - `HocPhanController.initHocPhanBinder()` (field: fileDeCuong)
   - `ChuongTrinhDaoTaoController.initCtdtBinder()` (field: fileWord)
   ```

**Effort:** 15 min  
**Done:** Git commit: "docs: add @InitBinder pattern documentation"

---

### Action 3.2: Document MockEmailServiceImpl
**Issue:** Issue 4.2 — Dev email config not explained.

**Steps:**
1. Open `06_PROJECT_SCAFFOLD.md`
2. Find "Dependencies" section
3. Add note after spring-boot-starter-mail entry:
   ```markdown
   **Email Service (Development Mode):**
   - Default: `MockEmailServiceImpl` — all emails logged to console (no SMTP)
   - Production: must enable SMTP in `application-prod.properties` + configure `EmailServiceImpl`
   - Trigger: GV sets DaCanhBao=1 → auto-send email to CVHT + student
   ```

**Effort:** 5 min  
**Done:** Git commit: "docs: clarify MockEmailServiceImpl dev-only config"

---

### Action 3.3: Standardize enum notation in 01_ERD_SCHEMA.md + 02_DATA_DESIGN.md
**Issue:** Issue 4.1 — Inconsistent enum value notation (backticks vs plain text).

**Steps:**
1. Open both files
2. Find all enum value tables
3. Replace backtick notation (`` `BanNhap` ``) with plain PascalCase (`BanNhap`) to match Java code
4. Verify: All enum values match `com.ntu.quanlyctdtdb.enums/*.java` exactly

**Effort:** 10 min  
**Done:** Git commit: "docs: standardize enum notation to PascalCase"

---

## PHASE 4: VERIFICATION & SIGN-OFF (1 hour)

### Action 4.1: Cross-validate all fixes
**Steps:**
1. Read `09_COMPREHENSIVE_AUDIT_REPORT.md` section 7 (Recommendations)
2. Verify each fix listed above has been applied
3. Grep for any remaining references to old filename:
   ```bash
   grep -r "02_Mô Tả" /vercel/share/v0-project/QuanLyCTDTDB/docs/
   ```
4. Verify `05_UI_DESIGN_SYSTEM.md` examples now have correct scope

**Effort:** 10 min  
**Done:** Git commit: "docs: audit fixes verified"

---

### Action 4.2: Update 00_MASTER_REFERENCE.md index
**Steps:**
1. Open `00_MASTER_REFERENCE.md`, section 10 (Related Documents)
2. Add entry for new files:
   - `09_COMPREHENSIVE_AUDIT_REPORT.md` — Code + docs consistency audit (annual validation)
   - `10_ACTION_PLAN_FROM_AUDIT.md` — Action items from audit findings
3. Update changelog with batch 4 date + link to commit

**Effort:** 5 min  
**Done:** Git commit: "docs: add new files to master index"

---

## SUMMARY TABLE

| Phase | Action | Issue | Time | Git Commit |
|-------|--------|-------|------|-----------|
| **1** | Fix #fields scope | 3.3 | 10m | fix #fields scope example |
| **1** | Rename file | 4.1 | 5m | rename 02 file to ASCII |
| **1** | Mark @Deprecated | 2.1 | 5m | mark doiTrangThai() deprecated |
| **2** | Update 03 workflow | 2.1 | 20m | clarify HK status auto-derive |
| **2** | Audit batch 3 status | 4.3 | 45m | clarify batch 3 implementation |
| **3** | Doc @InitBinder pattern | 3.1 | 15m | add @InitBinder documentation |
| **3** | Doc MockEmailService | 4.2 | 5m | clarify MockEmailServiceImpl config |
| **3** | Standardize enums | 4.1 | 10m | standardize enum notation |
| **4** | Cross-validate | All | 10m | verify audit fixes |
| **4** | Update master index | All | 5m | add new files to index |
| | **TOTAL** | | **130 min** | **10 commits** |

---

## SUCCESS CRITERIA

✅ All 7 medium/low issues resolved  
✅ No critical inconsistencies remain  
✅ Batch 4 implementation 100% verified  
✅ All docs updated and cross-referenced  
✅ New audit files (09, 10) integrated  
✅ Ready for production deployment  

---

**Next Review:** After Phase 4 (Danh Gia & Canh Bao features added) — plan for August 2026.

