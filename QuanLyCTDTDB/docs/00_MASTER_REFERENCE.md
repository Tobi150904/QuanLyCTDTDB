# 00_MASTER_REFERENCE — He Thong Quan Ly Dao Tao Xuat Sac (QuanLyCTDTDB)

> Tai lieu goc, doc truoc tat ca cac tai lieu khac. Moi thay doi schema, nghiep vu hay cong nghe PHAI cap nhat file nay truoc.

---

## 1. TONG QUAN HE THONG

| Thuoc tinh     | Gia tri                                              |
|----------------|------------------------------------------------------|
| Ten he thong   | He Thong Quan Ly Dao Tao Xuat Sac                   |
| Package goc    | com.ntu.quanlyctdtdb                                 |
| Spring Boot    | 3.5.6 (da ha tu 4.0.5 do layout-dialect khong tuong thich Groovy 5) |
| Java           | 17                                                   |
| Database       | MySQL 8+ (XAMPP), schema: QuanLyCTDTDB               |
| Frontend       | Thymeleaf 3.1 + Layout Dialect 3.3 + Bootstrap 5 + Bootstrap Icons |
| Build tool     | Maven Wrapper (`./mvnw`)                             |
| Port           | 8080                                                 |

### Muc dich
Quan ly toan bo quy trinh dao tao xuat sac cua truong dai hoc, bao gom:
- Quan ly Chuong Trinh Dao Tao (CTDT) va Hoc Phan
- Quan ly Lop Hoc Phan (phan cong giang vien, si so)
- Quan ly tai lieu mon hoc va de cuong chi tiet
- Quan ly danh gia, nhan xet, canh bao sinh vien
- Quan ly Kien Tap va Thuc Tap (lien ket voi Doanh Nghiep)
- Quan ly Nguoi Dung da vai tro

---

## 2. KIEN TRUC TONG THE

```
[Browser]
    |
    | HTTP
    v
[Spring Security Filter Chain]
    |
    v
[Controller Layer]         <- Thymeleaf View (templates/)
    |
    v
[Service Layer]            <- Business logic, validation, email
    |
    v
[Repository Layer]         <- Spring Data JPA
    |
    v
[Entity / Domain Layer]    <- JPA Entities + Enums
    |
    v
[MySQL 8+ Database]        <- QuanLyCTDTDB
```

### Package structure (thuc te)
```
com.ntu.quanlyctdtdb/
  config/          SecurityConfig, WebMvcConfig
  controller/      1 controller per module (Auth, Dashboard, NguoiDung, Profile, HocPhan, CTDT, LopHocPhan, DotKienTap, DotThucTap)
  dto/             Data Transfer Objects (form binding + Excel import)
  entity/          20 JPA Entities + 7 @Embeddable Id classes
  enums/           15 enum types (state machines + kinds)
  exception/       BusinessException, ResourceNotFoundException, GlobalExceptionHandler
  repository/      20 Spring Data JPA repositories
  security/        UserDetailsServiceImpl, CustomUserDetails
                   (KHONG co CustomAuthenticationProvider — dung DaoAuthenticationProvider qua AuthenticationManagerBuilder)
  service/         Service interfaces (bao gom EmailService)
  service/impl/    Service implementations (bao gom MockEmailServiceImpl cho dev)
  util/            ExcelImportUtil, FileStorageUtil
```

> Chi tiet coverage module + gap analysis: `06_PROJECT_SCAFFOLD.md`.

---

## 3. DATABASE SCHEMA — 20 BANG

| STT | Bang                         | Mo ta ngan                                 |
|-----|------------------------------|--------------------------------------------|
| 1   | HocKyNamHoc                  | Hoc ky nam hoc, trang thai                 |
| 2   | NguoiDung                    | Tai khoan nguoi dung (Admin, GV, SV, DN)   |
| 3   | SinhVien                     | Thong tin mo rong sinh vien                |
| 4   | GiangVien                    | Thong tin mo rong giang vien / chuyen gia  |
| 5   | NhomNguoiDung                | Bang trung gian: nguoi dung - vai tro      |
| 6   | ChuongTrinhDaoTao            | CTDT, trang thai duyet, nguoi tao/duyet    |
| 7   | BCN_ThanhVien                | Thanh vien Ban Chu Nhiem CTDT              |
| 8   | HocPhan                      | Mon hoc, tin chi, chu nhiem                |
| 9   | DoiNguGiangVienHP            | GV duoc phep day 1 hoc phan                |
| 10  | DoanhNghiep                  | Thong tin doanh nghiep lien ket            |
| 11  | LopHanhChinh                 | Lop hanh chinh, co van hoc tap             |
| 12  | CTDT_HocPhan                 | Hoc phan thuoc CTDT (chi tiet chuong trinh)|
| 13  | LopHocPhan                   | Lop hoc phan theo hoc ky                   |
| 14  | DanhSachSinhVienLopHocPhan   | SV dang ky + nhan xet + canh bao           |
| 15  | DotKienTap                   | Dot kien tap cho lop hanh chinh            |
| 16  | DanhSachSinhVienKienTap      | SV tham gia dot kien tap                   |
| 17  | DotThucTap                   | Dot thuc tap theo CTDT + Hoc Phan          |
| 18  | DanhSachThucTap              | Phan cong thuc tap tung sinh vien          |
| 19  | VaiTroThucTap                | Danh muc vai tro danh gia thuc tap         |
| 20  | KetQuaThucTap                | Ket qua / diem danh gia thuc tap           |

### Khoa chinh tong hop (Composite PK)
| Bang                        | Cau truc khoa                                                    |
|-----------------------------|------------------------------------------------------------------|
| NhomNguoiDung               | (MaNguoiDung, VaiTro)                                            |
| BCN_ThanhVien               | (MaCTDT, MaGV, ChucDanh)                                         |
| DoiNguGiangVienHP           | (MaHocPhan, MaGiangVien)                                         |
| CTDT_HocPhan                | (MaCTDT, MaHocPhan)                                              |
| LopHocPhan                  | (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)                       |
| DanhSachSinhVienLopHocPhan  | (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)                 |
| DanhSachSinhVienKienTap     | (MaDotKT, MaSV)                                                  |

---

## 4. VAI TRO NGUOI DUNG (8 ROLES)

| Role enum      | Hien thi        | Nguon               | Mo ta chuc nang chinh                                  |
|----------------|-----------------|---------------------|--------------------------------------------------------|
| PDT            | Phong Dao Tao   | NhomNguoiDung       | CRUD nguoi dung, doanh nghiep, bao cao tong hop        |
| TTDTXS         | TT Dao Tao XS   | NhomNguoiDung       | Phe duyet CTDT, HP, dot thuc tap, kien tap             |
| CVHT           | Co Van HT       | NhomNguoiDung       | Xu ly canh bao SV lop hanh chinh                       |
| CNHP           | Chu Nhiem HP    | NhomNguoiDung       | CRUD hoc phan, quan ly doi ngu GV, duyet tai lieu      |
| Admin          | Quan Tri He Thong| LoaiNguoiDung      | Toan quyen he thong                                    |
| GiangVien      | Giang Vien      | LoaiNguoiDung       | Quan ly lop HP duoc phan cong, upload TL, nhan xet SV  |
| SinhVien       | Sinh Vien       | LoaiNguoiDung       | Xem lich hoc, ket qua, nhan xet thuc tap               |
| DoanhNghiep    | Doanh Nghiep    | LoaiNguoiDung       | Nhan xet SV kien tap/thuc tap, nhap ket qua            |

> Mot GV co the dong thoi la CNHP hoac CVHT thong qua NhomNguoiDung.
> LoaiNguoiDung phan loai tai khoan, VaiTro trong NhomNguoiDung phan dinh quyen nghiep vu.

---

## 5. ENUMS — 15 ENUM TYPES

| Enum class            | Values                                                      | Dung trong bang              |
|-----------------------|-------------------------------------------------------------|------------------------------|
| TrangThaiHocKy        | SapDienRa, DangDienRa, DaKetThuc                           | HocKyNamHoc                  |
| LoaiNguoiDung         | Admin, GiangVien, SinhVien, DoanhNghiep                    | NguoiDung                    |
| VaiTro                | PDT, TTDTXS, CVHT, CNHP                                    | NhomNguoiDung                |
| TrangThaiCTDT         | BanNhap, ChoDuyet, DaDuyet, DaHuy                          | ChuongTrinhDaoTao            |
| ChucDanhBCN           | ChuNhiem, ThuKy, UyVien                                    | BCN_ThanhVien                |
| LoaiHocPhan           | LyThuyet, ThucHanh, DoAn, ThucTap, KienTap                | HocPhan                      |
| TrangThaiHocPhan      | BanNhap, ChoDuyet, DaDuyet                                 | HocPhan                      |
| LoaiGiangVien         | GiangVienTruong, DoanhNghiep                               | GiangVien                    |
| TrangThaiDoanhNghiep  | DangHopTac, TamNgung                                       | DoanhNghiep                  |
| TrangThaiLopHocPhan   | DangMo, DaDong, DaHuy                                      | LopHocPhan                   |
| TrangThaiSinhVien     | DangHoc, BaoLuu, ThoiHoc, TotNghiep                       | SinhVien                     |
| TrangThaiDotKT        | ChuanBi, ChoDuyet, DaDuyet, DaThucHien, DaHuy             | DotKienTap                   |
| TrangThaiDotTT        | ChuanBi, ChoDuyet, DaDuyet, DangThucHien, DaKetThuc, DaHuy | DotThucTap                   |
| TrangThaiThucTap      | DaPhanCong, DangThucTap, DaKetThuc, DaHuy                 | DanhSachThucTap              |
| LoaiThucTap           | Truong, DoanhNghiep                                        | DanhSachThucTap              |

---

## 6. NGHIEP VU CHINH — TOM TAT

### 6.1 Luong quan ly Hoc Phan
```
BCN tao HP (BanNhap)
  -> BCN nop len (ChoDuyet)
    -> TTDTXS phe duyet (DaDuyet)  |  TTDTXS tu choi (BanNhap + ly do)
BCN them GV vao DoiNguGiangVienHP
CNHP quan ly doi ngu GV, upload de cuong
```

### 6.2 Luong quan ly CTDT
```
BCN tao CTDT (BanNhap)
  -> BCN them HocPhan vao CTDT qua bang CTDT_HocPhan (HocKyThu, SoLopDuKien)
  -> BCN nop len (ChoDuyet)
    -> TTDTXS phe duyet (DaDuyet)
      => He thong TU DONG tao LopHocPhan theo SoLopDuKien cho tung HP trong CTDT
         (MaLopHocPhan = 1..N, MaGiangVien = NULL)
  -> BCN gan LopHanhChinh vao CTDT
```

### 6.3 Luong phan cong Lop Hoc Phan
```
BCN/TTDTXS xem danh sach LopHocPhan chua co GV
  -> Gan GiangVien cho LopHocPhan
     (Neu GV khong trong DoiNguGiangVienHP -> canh bao nhung van cho phep)
GV thay LopHocPhan cua minh
  -> Upload TaiLieu (DeCuongChiTiet, DeThiGiuaKy, DeThiCuoiKy)
  -> Nhap nhan xet SV (DanhSachSinhVienLopHocPhan)
     (Neu tieu cuc -> tu dong gui email canh bao den CVHT)
CVHT xu ly canh bao (DaCanhBao=1, KetQuaXuLy)
```

### 6.4 Luong Kien Tap (Hybrid Auto-Add + Toggle DaThamGia)
```
BCN/TTDTXS tao DotKienTap (TrangThai='ChuanBi', NguoiTao=currentUser)
  -> Chon LopHanhChinh + DoanhNghiep (bat buoc DangHopTac) + GVPhuTrach + HocKy
  -> Upload FileMinhChung
  -> AUTO-ADD: he thong select SinhVien WHERE MaLopHC=? AND TrangThaiSV='DangHoc'
               INSERT DanhSachSinhVienKienTap (..., DaThamGia=1) cho TAT CA SV du dieu kien.
               (SV BaoLuu/ThoiHoc/TotNghiep KHONG duoc them tu dong.)
Admin/BCN co the TOGGLE DaThamGia (POST /kien-tap/chi-tiet/{id}/sv/{maSV}/danh-dau):
  -> Danh dau "Khong tham gia" giu lai ban ghi (audit), chi dao cac loai bao cao
     "thuc te tham gia".
  -> Chi khoa toggle khi dot da o trang thai DaHuy.
Nut "Dong bo danh sach" re-sync khi lop co SV moi (chi INSERT, khong XOA).
Nop len (ChoDuyet) -> TTDTXS duyet (set NguoiDuyet+NgayDuyet, -> DaDuyet)
  -> BCN/TTDTXS xac nhan hoan thanh (DaDuyet -> DaThucHien)
  -> BCN/TTDTXS huy bat ky trang thai nao (tru DaHuy) -> DaHuy
GV (MaGVPhuTrach)     nhap NhanXetGV  (doc lap)
DN (MaDoanhNghiep)    nhap NhanXetDN  (doc lap, khong ghi de)
```

### 6.5 Luong Thuc Tap
```
PDT/TTDTXS tao DotThucTap (ChuanBi, NguoiTao=currentUser)
  -> Chon CTDT_HocPhan (bat buoc la HP loai ThucTap/KienTap, DaDuyet) + HocKy
  -> Nop len (ChoDuyet) -> TTDTXS duyet (set NguoiDuyet+NgayDuyet, -> DaDuyet)
  -> TTDTXS bat dau (DaDuyet -> DangThucHien)
  -> TTDTXS ket thuc (DangThucHien -> DaKetThuc + cascade DanhSachThucTap.TrangThai)
  -> TTDTXS huy bat ky truoc DaKetThuc -> DaHuy
PDT import Excel phan cong (DanhSachThucTap): MaSV, LoaiThucTap, MaDoanhNghiep
  -> UNIQUE (MaDotTT, MaSV) - bo qua ban ghi trung
  -> Validate: LoaiThucTap='DoanhNghiep' BAT BUOC MaDN + DN DangHopTac;
               LoaiThucTap='Truong' BAT BUOC MaDN=NULL.
DN / GV / CVHT / SV nhap KetQuaThucTap (Diem, NhanXet theo VaiTroThucTap)
  -> Upsert theo (MaThucTap, MaVaiTro) - 1 vai tro 1 ban ghi.
PDT xuat bao cao Excel
```

---

## 7. DEPENDENCIES CHINH (pom.xml)

| Artifact                            | Muc dich                                        |
|-------------------------------------|-------------------------------------------------|
| spring-boot-starter-web             | REST + MVC                                      |
| spring-boot-starter-thymeleaf       | Server-side HTML rendering                      |
| spring-boot-starter-data-jpa        | ORM / Hibernate                                 |
| spring-boot-starter-security        | Authentication + Authorization                  |
| spring-boot-starter-validation      | Bean Validation (jakarta.validation)            |
| spring-boot-starter-mail            | Gui email canh bao (comment trong dev)          |
| spring-boot-devtools                | Hot reload trong dev                            |
| mysql-connector-j                   | MySQL JDBC driver                               |
| lombok                              | Boilerplate reduction                           |
| thymeleaf-layout-dialect            | Template inheritance (layout:decorate)          |
| thymeleaf-extras-springsecurity6    | Thymeleaf sec:authorize                         |
| poi-ooxml (5.2.3)                   | Excel import/export                             |
| jackson-datatype-jsr310             | Java 8 Date/Time JSON support                   |
| spring-boot-starter-test            | JUnit 5 + Mockito                               |
| spring-security-test                | Test with @WithMockUser                         |

---

## 8. CAU HINH MOI TRUONG

| Key property                          | Gia tri mac dinh                        |
|---------------------------------------|-----------------------------------------|
| server.port                           | 8080                                    |
| spring.datasource.url                 | jdbc:mysql://localhost:3306/QuanLyCTDTDB|
| spring.datasource.username            | root                                    |
| spring.datasource.password            | (trong)                                 |
| spring.jpa.hibernate.ddl-auto         | validate                                |
| spring.jpa.open-in-view               | false (BAT BUOC — xem § 9)              |
| spring.thymeleaf.cache                | false (dev), true (prod)                |
| file.upload-dir                       | uploads/                                |
| server.servlet.session.timeout        | 30m                                     |

> Tat ca config chi tiet o: `src/main/resources/application.properties`
> Huong dan setup moi truong: `README.md` (root)

---

## 9. QUY UOC CODE

### Naming
- Entity: PascalCase, ten = ten bang SQL (vi du: `HocKyNamHoc`)
- Repository: `<Entity>Repository extends JpaRepository<Entity, PK>`
- Service interface: `<Entity>Service` (package service/)
- Service impl: `<Entity>ServiceImpl` (package service/impl/)
- Controller: `<Module>Controller` (1 controller = 1 module)
- DTO: `<Entity>DTO` cho form binding, `<Entity>ExcelDTO` cho Excel

### Composite PK
- Tao class `<Entity>Id` implement Serializable voi @Embeddable
- Entity dung @EmbeddedId hoac @IdClass

### Security
- URL pattern: `/api/**` (khong dung), chi dung MVC `/**`
- Login: POST /login (Spring Security default form)
- Logout: POST /logout
- Access denied: redirect /403

### Flash message
- Dung RedirectAttributes: `addFlashAttribute("successMsg", "...")` hoac `"errorMsg"`
- Template tu dong hien thi qua layout base.html

### Lazy loading & open-in-view
- `spring.jpa.open-in-view=false` — KHONG duoc doc collection LAZY ngoai transaction.
- Neu template (Thymeleaf) can iterate `@OneToMany` / `@ManyToMany` LAZY, repository PHAI dung `@EntityGraph(attributePaths = "...")` hoac `JOIN FETCH` trong `@Query`.
- Truong hop muon giu LAZY nhung can iterate mot lan: bo sung method `findXxxWithAssoc(...)` tra entity da fetch sang, KHONG bat `open-in-view` lai.

---

## 10. TAI LIEU LIEN QUAN

| File                        | Noi dung                                           |
|-----------------------------|----------------------------------------------------|
| 01_ERD_SCHEMA.md                      | So do ERD day du, mo ta tung bang va cot           |
| 02_Mô Tả & Thiết kế dữ liệu.md        | Quy uoc du lieu, format, rang buoc nghiep vu       |
| 03_WORKFLOW.md                        | Workflow chi tiet A-Z tung module (co flowchart)   |
| 04_DEV_CHECKLIST.md                   | Checklist phat trien theo phase, kiem tra truoc demo|
| 05_UI_DESIGN_SYSTEM.md                | Design tokens, component rules, Thymeleaf helpers  |
| 06_PROJECT_SCAFFOLD.md                | Cau truc thuc te + ma tran coverage + tech debt    |
| 07_ROADMAP.md                         | Ke hoach lam viec chi tiet theo phase              |
| scripts/README.md                     | Huong dan chay SQL + quy tac migration             |
| scripts/01_create_tables.sql          | DDL tao database + 20 bang                         |
| scripts/02_seed_data.sql              | DML du lieu mau test day du (20 user, 12 SV, 3 CTDT, test case DaThamGia=0, SV BaoLuu, ThoiHoc) |
