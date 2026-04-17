# QUICK LOOKUP - Tra cuu nhanh khi dang code

---

## FK TREE (bang cha -> bang con, can biet truoc khi INSERT)

```
HocKyNamHoc
    └── LopHocPhan (MaHocKy)
    └── DotKienTap (MaHocKy)
    └── DotThucTap (MaHocKy)

LopHanhChinh
    └── NguoiDung (MaLopHC)        <- SV phai co lop HC
    └── DotKienTap (MaLopHC)

NguoiDung
    └── NguoiDung_VaiTro (MaNguoiDung)
    └── HocPhan.ChuNhiemHP (MaGiangVien)
    └── LopHocPhan.MaGiangVien
    └── TaiLieuMonHoc.NguoiDuyet
    └── DanhGiaVaCanhBao.MaSV
    └── DanhGiaVaCanhBao.NguoiNhanXet
    └── DotKienTap.MaGVPhuTrach
    └── DotKienTap.NguoiDuyet
    └── DotThucTap.NguoiDuyet
    └── PhanCongThucTap.MaSV
    └── PhanCongThucTap.MaGiangVienGiamSat
    └── ChuongTrinhDaoTao.NguoiTao
    └── ChuongTrinhDaoTao.NguoiDuyet
    └── LopHanhChinh.MaCoVan

ChuongTrinhDaoTao
    └── LopHanhChinh (MaCTDT)

HocPhan
    └── DoiNguGiangVienHP (MaHocPhan)
    └── LopHocPhan (MaHocPhan)

LopHocPhan
    └── TaiLieuMonHoc (MaLopHP)
    └── DanhGiaVaCanhBao (MaLopHP)

DoanhNghiep
    └── DotKienTap (MaDoanhNghiep)
    └── PhanCongThucTap (MaDoanhNghiep)

DotThucTap
    └── PhanCongThucTap (MaDotTT)
```

---

## UNIQUE CONSTRAINTS QUAN TRONG

| Bang | Columns | Y nghia |
|------|---------|---------|
| NguoiDung | TenDangNhap | Khong trung ten dang nhap |
| NguoiDung | Email | Khong trung email |
| NguoiDung_VaiTro | (MaNguoiDung, VaiTro) | PK composite |
| DoiNguGiangVienHP | (MaHocPhan, MaGiangVien) | PK composite |
| LopHocPhan | (MaHocPhan, MaHocKy, NhomLop) | Khong trung nhom lop cung HP cung HK |
| TaiLieuMonHoc | (MaLopHP, Loai) | 1 lop chi 1 tai lieu moi loai |
| PhanCongThucTap | (MaDotTT, MaSV) | 1 SV chi 1 phan cong moi dot |

---

## WORKFLOW TRANG THAI - SO DO CHUYEN DOI

```
CTDT:
  BanNhap --[BCN nop]--> ChoDuyet --[TTDTXS/PDT duyet]--> DaDuyet (*AUTO LopHP)
                                   --[TTDTXS/PDT tu choi]--> DaHuy

HocPhan:
  BanNhap --[CNHP nop]--> ChoDuyet --[PDT/TTDTXS duyet]--> DaDuyet
                                    --[tu choi]--> BanNhap (tra ve de sua)

TaiLieu:
  (GV nop) --> ChoDuyet --[CNHP duyet]--> DaDuyet
                         --[CNHP tu choi]--> TuChoi --> (GV nop lai) UPDATE ban cu

DotKienTap:
  ChuanBi --[nop]--> ChoDuyet --[TTDTXS duyet]--> DaDuyet --[thuc hien]--> DaThucHien
                               --[tu choi]--> DaHuy (bat cu luc nao)

DotThucTap:
  ChuanBi -> ChoDuyet -> DaDuyet -> DangThucHien -> DaKetThuc
  (duyet boi TTDTXS)

PhanCongThucTap:
  DaPhanCong -> DangThucTap -> DaKetThuc
                             -> DaHuy
```

---

## AI ROLE CHECK TRONG THYMELEAF

```html
<!-- Chia se cho tat ca -->
<span sec:authentication="principal.username"></span>
<span sec:authentication="principal.hoTen"></span>   <!-- Neu extend UserDetails -->

<!-- PDT va TTDTXS -->
<div sec:authorize="hasAnyRole('PDT','TTDTXS')">...</div>

<!-- Giang vien (co the la CNHP, GV, hoac GV+CNHP) -->
<div sec:authorize="hasAnyRole('GV','CNHP')">...</div>

<!-- Chi CNHP -->
<div sec:authorize="hasRole('CNHP')">...</div>

<!-- BCN -->
<div sec:authorize="hasRole('BCN')">...</div>

<!-- CVHT -->
<div sec:authorize="hasRole('CVHT')">...</div>

<!-- SV -->
<div sec:authorize="hasRole('SV')">...</div>

<!-- DN -->
<div sec:authorize="hasRole('DN')">...</div>
```

---

## SAMPLE USER/PASS DE TEST (sau khi hash BCrypt)

| Role    | MaNguoiDung | TenDangNhap   | Mat khau goc |
|---------|-------------|---------------|--------------|
| PDT     | PDT001      | pdt_admin     | pdt123       |
| TTDTXS  | TTDT001     | ttdtxs_admin  | ttdt123      |
| BCN     | BCN001      | bcn_cntt      | bcn123       |
| GV+CNHP | GV001       | gv_nguyena    | gv123        |
| GV      | GV002       | gv_leb        | gv123        |
| CVHT    | CVHT001     | cvht_class1   | cvht123      |
| SV      | SV001       | sv_trana      | sv123        |
| DN      | DN001_ACC   | dn_fpt        | dn123        |

Ghi chu: Mat khau tren la goc, phai hash BCrypt khi update vao DB:
  UPDATE NguoiDung SET MatKhauHash = '$2a$10$...' WHERE MaNguoiDung = 'PDT001';

---

## EXCEL IMPORT FORMAT

### File import NguoiDung
| Col | Header | Type | Required | Note |
|-----|--------|------|----------|------|
| A | MaNguoiDung | String | Y | SV001, GV001, ... |
| B | HoTen | String | Y | |
| C | Email | String | Y | Phai unique |
| D | TenDangNhap | String | Y | Phai unique |
| E | VaiTro | String | Y | SV/GV/CVHT/... |
| F | MaLopHC | String | N | Chi can neu VaiTro=SV |
| G | HocHam | String | N | Chi can neu GV |
| H | HocVi | String | N | Chi can neu GV |
| I | ChuyenNganh | String | N | Chi can neu GV |

### File import PhanCongThucTap
| Col | Header | Type | Required |
|-----|--------|------|----------|
| A | MaSV | String | Y |
| B | MaDoanhNghiep | String | Y |
| C | MaGiangVienGiamSat | String | N |

---

## PACKAGE STRUCTURE NHANH

```
com.example.qlctdt
  ├── config/           SecurityConfig, WebMvcConfig
  ├── entity/           14 entity classes
  ├── enums/            13 enum files
  ├── repository/       14 repository interfaces
  ├── service/
  │   ├── interfaces/   Service interfaces (contract)
  │   └── impl/         Service implementations
  ├── controller/       @Controller (Thymeleaf)
  ├── dto/              DTO classes
  ├── util/             ExcelImportUtil, FileStorageService, EmailService
  └── exception/        BusinessException, GlobalExceptionHandler
```

---

## NHUNG DIEM HAY SAI - NHAC NHO

1. `@Controller` cho Thymeleaf, KHONG phai `@RestController`
2. Return String ten template: `return "ctdt/list"` (khong co prefix /templates/)
3. Redirect sau POST: `return "redirect:/ctdt"` (Post/Redirect/Get pattern)
4. CSRF token bat buoc trong moi form: `<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>`
5. `@Transactional` cho service method co nhieu DB operation
6. Fetch LAZY cho toan bo @ManyToOne, dung @Transactional khi can access
7. `@Enumerated(EnumType.STRING)` cho moi truong enum (khong dung ORDINAL)
8. `equals()` + `hashCode()` bat buoc cho @Embeddable composite PK
9. Mat khau seed data can hash BCrypt truoc khi test login that
10. UNIQUE(MaLopHP, Loai): Khi GV nop lai, phai UPDATE don cu khong INSERT moi
11. `@EnableAsync` tren main class de dung @Async cho EmailService
12. `@PageableDefault(size=10)` cho list endpoint co phan trang
