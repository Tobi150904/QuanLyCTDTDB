# ERD & Database Schema - Hệ Thống Quản Lý Đào Tạo Xuất Sắc

## 1. ENTITY RELATIONSHIP DIAGRAM (ERD) - Text-based

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        AUTHENTICATION & AUTHORIZATION LAYER                 │
├─────────────────────────────────────────────────────────────────────────────┤

                    ┌──────────────────────┐
                    │    NguoiDung         │
                    │  (Users/Accounts)    │
                    ├──────────────────────┤
                    │ MaNguoiDung (PK)     │
                    │ TenDangNhap (UQ)     │
                    │ MatKhauHash          │
                    │ Email (UQ)           │
                    │ HoTen                │
                    │ SoDienThoai          │
                    │ MaLopHC (FK) ─┐      │
                    │ TrangThaiSV  │      │
                    │ HocHam       │      │
                    │ HocVi        │      │
                    │ ChuyenNganh  │      │
                    │ TrangThaiTK  │      │
                    │ created_at   │      │
                    │ updated_at   │      │
                    └──────┬───────┘      │
                           │              │
                  ┌────────┴─────────────┘
                  │ 1:N
                  ▼
         ┌─────────────────────────┐
         │ NguoiDung_VaiTro        │ ◄─── Composite PK: (MaNguoiDung, VaiTro)
         │ (Role Assignments)      │      Enum: SV,GV,CVHT,BCN,CNHP,PDT,TTDTXS,DN
         ├─────────────────────────┤
         │ MaNguoiDung (FK, PK)    │
         │ VaiTro (PK)             │
         │ created_at              │
         └─────────────────────────┘

└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                            CATALOG/MASTER DATA LAYER                        │
├─────────────────────────────────────────────────────────────────────────────┤

┌──────────────────────┐         ┌──────────────────────┐
│  HocKyNamHoc         │         │  DoanhNghiep         │
│  (Semesters)         │         │  (Partner Companies) │
├──────────────────────┤         ├──────────────────────┤
│ MaHocKy (PK)         │         │ MaDoanhNghiep (PK)   │
│ TenHocKy             │         │ TenDoanhNghiep       │
│ NgayBatDau           │         │ LinhVuc              │
│ NgayKetThuc          │         │ NguoiDaiDien         │
│ TrangThai            │         │ Email                │
│ created_at           │         │ SoDienThoai          │
│ updated_at           │         │ TrangThai            │
└──────────────────────┘         │ created_at           │
                                 │ updated_at           │
                                 └──────────────────────┘

┌──────────────────────┐
│ ChuongTrinhDaoTao    │
│ (CTDT Programs)      │
├──────────────────────┤
│ MaCTDT (PK)          │
│ TenCTDT              │
│ Khoa                 │
│ FileWord             │
│ TrangThai            │
│ NguoiTao (FK) ─┐     │
│ NguoiDuyet (FK)      │
│ NgayDuyet            │
│ created_at           │
│ updated_at           │
└──────────────────────┘
         │
         │ 1:N
         ▼
┌──────────────────────┐
│ HocPhan              │
│ (Course/Subject)     │
├──────────────────────┤
│ MaHocPhan (PK)       │
│ TenHocPhan           │
│ SoTinChi             │
│ ChuNhiemHP (FK)──┐   │
│ FileDeCuong      │   │
│ TrangThai        │   │
│ created_at       │   │
│ updated_at       │   │
└──────────────────────┘
         │
         │ 1:N
         ▼
┌──────────────────────────────────┐
│ DoiNguGiangVienHP                │ ◄─── Composite PK: (MaHocPhan, MaGiangVien)
│ (Lecturer Team per Course)       │      Only GV in this team can teach
├──────────────────────────────────┤
│ MaHocPhan (FK, PK)               │
│ MaGiangVien (FK, PK)             │
│ TrangThai                        │
│ created_at                       │
└──────────────────────────────────┘

└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                        TRAINING & CLASSES LAYER                             │
├─────────────────────────────────────────────────────────────────────────────┤

┌──────────────────────┐
│ LopHanhChinh         │
│ (Admin Classes)      │
├──────────────────────┤
│ MaLopHC (PK)         │
│ TenLop               │
│ MaCTDT (FK)          │
│ KhoaHoc              │
│ MaCoVan (FK)         │
│ created_at           │
│ updated_at           │
└──────────────────────┘
         │
         │ N:1 ◄── One CVHT per Admin Class
         │
    ◄─── Referenced by NguoiDung.MaLopHC (Many SV per Admin Class)
         │
         │ 1:N ◄─── Kiến Tập by Admin Class
         ▼
    DotKienTap

┌──────────────────────┐
│ LopHocPhan           │
│ (Course Classes)     │
├──────────────────────┤
│ MaLopHP (PK)         │
│ MaHocPhan (FK)───┐   │
│ MaHocKy (FK)──┐  │   │
│ MaGiangVien(FK)  │   │
│ NhomLop          │   │
│ SiSoToiDa        │   │
│ SiSoThucTe       │   │
│ TrangThai        │   │
│ created_at       │   │
│ updated_at       │   │
└──────────────────────┘
         │
         │ 1:N
         ├─────────────────────────────┐
         ▼                             ▼
   TaiLieuMonHoc          DanhGiaVaCanhBao
   (Documents)            (Evaluations & Alerts)
   - DeCuongChiTiet       - GV assessment of SV
   - DeThiGiuaKy          - Auto-create warning if negative
   - DeThiCuoiKy          - CVHT processes warning

└─────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    INTERNSHIP & PRACTICUM LAYER                             │
├─────────────────────────────────────────────────────────────────────────────┤

┌──────────────────────┐
│ DotKienTap           │
│ (Field Trip Batches) │
├──────────────────────┤
│ MaDotKT (PK)         │
│ TenDotKT             │
│ MaLopHC (FK)──┐      │
│ MaHocKy (FK)  │      │
│ ThoiGian      │      │
│ MaGVPhuTrach  │      │
│ MaDoanhNghiep │      │
│ NhanXetGV     │      │
│ NhanXetDN     │      │
│ TrangThai     │      │
│ NguoiDuyet    │      │
│ NgayDuyet     │      │
│ created_at    │      │
│ updated_at    │      │
└──────────────────────┘

┌──────────────────────┐         ┌──────────────────────┐
│ DotThucTap           │         │ PhanCongThucTap      │
│ (Internship Batches) │────────▶│ (Practicum Assign)   │
├──────────────────────┤ 1:N     ├──────────────────────┤
│ MaDotTT (PK)         │         │ MaThucTap (PK)       │
│ TenDotTT             │         │ MaDotTT (FK)         │
│ MaHocKy (FK)         │         │ MaSV (FK)            │
│ NgayBatDau           │         │ MaDoanhNghiep (FK)   │
│ NgayKetThuc          │         │ MaGiangVienGiamSat   │
│ TrangThai            │         │ DiemDN               │
│ NguoiDuyet           │         │ NhanXetDN            │
│ NgayDuyet            │         │ DiemGV               │
│ created_at           │         │ NhanXetGV            │
│ updated_at           │         │ NhanXetSV            │
└──────────────────────┘         │ TrangThai            │
                                 │ created_at           │
                                 │ updated_at           │
                                 └──────────────────────┘
                                   UQ: (MaDotTT, MaSV)

└─────────────────────────────────────────────────────────────────────────────┘

## 2. KEY RELATIONSHIPS SUMMARY

| Parent → Child | Cardinality | Description |
|---|---|---|
| NguoiDung → NguoiDung_VaiTro | 1:N | One person, many roles |
| NguoiDung → LopHanhChinh (MaCoVan) | 1:N | One CVHT, many admin classes |
| NguoiDung → DanhGiaVaCanhBao (NguoiNhanXet) | 1:N | One GV/CVHT, many evaluations |
| NguoiDung → LopHocPhan (MaGiangVien) | 1:N | One GV, many course classes |
| HocKyNamHoc → LopHocPhan | 1:N | One semester, many course classes |
| HocKyNamHoc → DotKienTap | 1:N | One semester, many field trips |
| HocKyNamHoc → DotThucTap | 1:N | One semester, many internships |
| HocPhan → DoiNguGiangVienHP | 1:N | One course, many lecturers in team |
| HocPhan → LopHocPhan | 1:N | One course, many course instances |
| HocPhan → TaiLieuMonHoc | 1:N | One course, many documents |
| LopHocPhan → TaiLieuMonHoc | 1:N | One course class, many documents |
| LopHocPhan → DanhGiaVaCanhBao | 1:N | One course class, many evaluations |
| LopHanhChinh → DotKienTap | 1:N | One admin class, many field trips |
| DoanhNghiep → DotKienTap | 1:N | One company, many field trips |
| DoanhNghiep → PhanCongThucTap | 1:N | One company, many practicum assignments |
| DotThucTap → PhanCongThucTap | 1:N | One internship batch, many assignments |

## 3. CONSTRAINT RULES

### Primary Keys (PK)
- All entity tables have exactly 1 PK
- Composite PKs: NguoiDung_VaiTro (MaNguoiDung + VaiTro), DoiNguGiangVienHP (MaHocPhan + MaGiangVien)

### Unique Keys (UQ)
- `NguoiDung.TenDangNhap` - Must be unique
- `NguoiDung.Email` - Must be unique
- `TaiLieuMonHoc(MaLopHP, Loai)` - Only 1 document per type per class
- `PhanCongThucTap(MaDotTT, MaSV)` - Only 1 assignment per student per batch

### Foreign Keys (FK)
- All FKs reference PK of parent table
- Cascade rules: Most are RESTRICT (prevent delete if child exists)
- Exception: Soft delete where possible (use TrangThai instead)

### Enums (Standard Values)
```
NguoiDung_VaiTro.VaiTro:
  - 'SV' (Sinh Viên / Student)
  - 'GV' (Giảng Viên / Lecturer)
  - 'CVHT' (Cố Vấn Học Tập / Academic Advisor)
  - 'BCN' (Ban Chủ Nhiệm / Program Chair)
  - 'CNHP' (Chủ Nhiệm Học Phần / Course Chair)
  - 'PDT' (Phòng Đào Tạo / Dean of Training)
  - 'TTDTXS' (Trung Tâm Đào Tạo Xuất Sắc / Center for Excellence)
  - 'DN' (Doanh Nghiệp / Company)

HocKyNamHoc.TrangThai:
  - 'SapDienRa' (Upcoming)
  - 'DangDienRa' (Ongoing)
  - 'DaKetThuc' (Finished)

ChuongTrinhDaoTao.TrangThai:
  - 'BanNhap' (Draft)
  - 'ChoDuyet' (Pending Approval)
  - 'DaDuyet' (Approved) → Auto-creates LopHocPhan
  - 'DaHuy' (Cancelled)

HocPhan.TrangThai:
  - 'BanNhap' (Draft)
  - 'ChoDuyet' (Pending)
  - 'DaDuyet' (Approved)

LopHocPhan.TrangThai:
  - 'DangMo' (Open, accepting students)
  - 'DaDong' (Closed)
  - 'DaHuy' (Cancelled)

TaiLieuMonHoc.Loai:
  - 'DeCuongChiTiet' (Detailed Syllabus) → Deadline: 14 days from semester start
  - 'DeThiGiuaKy' (Midterm Exam)
  - 'DeThiCuoiKy' (Final Exam)

TaiLieuMonHoc.TrangThai:
  - 'ChoDuyet' (Pending Review)
  - 'DaDuyet' (Approved)
  - 'TuChoi' (Rejected)

DanhGiaVaCanhBao.LoaiNhanXet:
  - 'TichCuc' (Positive)
  - 'TieuCuc' (Negative) → Auto-creates alert, sends email to CVHT & SV

DotKienTap.TrangThai:
  - 'ChuanBi' (Preparing)
  - 'ChoDuyet' (Pending Approval)
  - 'DaDuyet' (Approved)
  - 'DaThucHien' (Executed)
  - 'DaHuy' (Cancelled)

DotThucTap.TrangThai:
  - 'ChuanBi' (Preparing)
  - 'ChoDuyet' (Pending)
  - 'DaDuyet' (Approved)
  - 'DangThucHien' (Ongoing)
  - 'DaKetThuc' (Finished)

PhanCongThucTap.TrangThai:
  - 'DaPhanCong' (Assigned)
  - 'DangThucTap' (In Progress)
  - 'DaKetThuc' (Completed)
  - 'DaHuy' (Cancelled)

DoanhNghiep.TrangThai:
  - 'DangHopTac' (Active)
  - 'TamNgung' (Paused)

NguoiDung.TrangThaiSV:
  - 'DangHoc' (Currently Studying)
  - 'BaoLuu' (Deferred)
  - 'ThoiHoc' (Dropped Out)
  - 'TotNghiep' (Graduated)

NguoiDung.TrangThaiTK:
  - 1 (Active)
  - 0 (Locked/Inactive)
```

## 4. CRITICAL WORKFLOW TRIGGERS

| Trigger | Condition | Action |
|---|---|---|
| Auto-create LopHocPhan | ChuongTrinhDaoTao.TrangThai = 'DaDuyet' | System reads HocPhan list, creates ~60 LopHocPhan for current semester |
| Create Alert | DanhGiaVaCanhBao.LoaiNhanXet = 'TieuCuc' | Auto-create alert, email CVHT & SV |
| Validate Assignment | GV assigned to LopHocPhan | Check: GV must be in DoiNguGiangVienHP (warn if not) |
| Check Deadline | TaiLieuMonHoc.Loai = 'DeCuongChiTiet' | Deadline = HocKy.NgayBatDau + 14 days |

