# MASTER REFERENCE - He Thong Quan Ly Dao Tao Xuat Sac
# Spring Boot + Thymeleaf + MySQL (XAMPP)
# Doc nay truoc khi code bat ky module nao

---

## 1. THONG TIN DU AN

```
Project: He Thong Quan Ly Dao Tao Xuat Sac
Stack  : Spring Boot 3.x | Thymeleaf | Spring Security | Spring Data JPA | MySQL 8
Build  : Maven
DB     : QuanLyCTDTDB (MySQL / XAMPP)
Port   : 8080 (dev)
```

---

## 2. CAU TRUC THU MUC CHUAN (KHONG DUOC THAY DOI)

```
src/main/java/com/ntu/quanlyctdt/
|
|-- config/
|   |-- SecurityConfig.java          <- Spring Security, 8 roles, password encoder
|   |-- WebMvcConfig.java            <- File upload, static resources
|   |-- AuditConfig.java             <- AuditorAware for created_at/updated_at
|
|-- entity/                          <- 14 entities (map 1-1 voi bang DB)
|   |-- NguoiDung.java
|   |-- NguoiDungVaiTro.java
|   |-- HocKyNamHoc.java
|   |-- DoanhNghiep.java
|   |-- ChuongTrinhDaoTao.java
|   |-- HocPhan.java
|   |-- DoiNguGiangVienHP.java
|   |-- LopHanhChinh.java
|   |-- LopHocPhan.java
|   |-- TaiLieuMonHoc.java
|   |-- DanhGiaVaCanhBao.java
|   |-- DotKienTap.java
|   |-- DotThucTap.java
|   |-- PhanCongThucTap.java
|
|-- enums/                           <- TAT CA enum phai dat day, khong hard-code string
|   |-- VaiTro.java
|   |-- TrangThaiHocKy.java
|   |-- TrangThaiCTDT.java
|   |-- TrangThaiHocPhan.java
|   |-- TrangThaiLopHP.java
|   |-- LoaiTaiLieu.java
|   |-- TrangThaiTaiLieu.java
|   |-- LoaiNhanXet.java
|   |-- TrangThaiDotKT.java
|   |-- TrangThaiDotTT.java
|   |-- TrangThaiPhanCong.java
|   |-- TrangThaiDoanhNghiep.java
|   |-- TrangThaiSinhVien.java
|
|-- repository/                      <- Spring Data JPA, dat ten: <Entity>Repository
|   |-- NguoiDungRepository.java
|   |-- (14 repository files)
|
|-- service/
|   |-- interfaces/                  <- Interface truoc, impl sau
|   |   |-- NguoiDungService.java
|   |   |-- (1 interface per module)
|   |-- impl/
|       |-- NguoiDungServiceImpl.java
|       |-- (1 impl per interface)
|
|-- controller/                      <- @Controller (Thymeleaf), khong phai @RestController
|   |-- AuthController.java
|   |-- DashboardController.java
|   |-- NguoiDungController.java
|   |-- DoanhNghiepController.java
|   |-- HocPhanController.java
|   |-- ChuongTrinhDaoTaoController.java
|   |-- LopHocPhanController.java
|   |-- TaiLieuController.java
|   |-- DanhGiaController.java
|   |-- KienTapController.java
|   |-- ThucTapController.java
|   |-- BaoCaoController.java
|
|-- dto/                             <- DTO cho form input va view output
|   |-- NguoiDungDTO.java
|   |-- (1 DTO per module, co the co Request/Response variants)
|
|-- util/
|   |-- ExcelImportUtil.java         <- Apache POI, import Excel
|   |-- FileStorageUtil.java         <- Luu file upload vao disk
|   |-- EmailService.java            <- Spring Mail, gui email canh bao
|
|-- exception/
|   |-- BusinessException.java       <- Custom business rule violations
|   |-- GlobalExceptionHandler.java  <- @ControllerAdvice

src/main/resources/
|-- templates/                       <- Thymeleaf HTML files
|   |-- layout/
|   |   |-- base.html                <- Chua sidebar, navbar chung
|   |-- auth/
|   |   |-- login.html
|   |-- dashboard/
|   |   |-- index.html
|   |-- nguoidung/
|   |-- doanhnghiep/
|   |-- hocphan/
|   |-- ctdt/
|   |-- lophocphan/
|   |-- tailieu/
|   |-- danhgia/
|   |-- kientap/
|   |-- thuctap/
|   |-- baocao/
|-- static/
|   |-- css/
|   |-- js/
|   |-- uploads/                     <- Thu muc luu file upload (dev only)
|-- application.properties
```

---

## 3. TAT CA ENUM - CHUAN KHONG DOI

```
VaiTro        : SV | GV | CVHT | BCN | CNHP | PDT | TTDTXS | DN
TrangThaiHocKy: SapDienRa | DangDienRa | DaKetThuc
TrangThaiCTDT : BanNhap | ChoDuyet | DaDuyet | DaHuy
TrangThaiHP   : BanNhap | ChoDuyet | DaDuyet
TrangThaiLopHP: DangMo | DaDong | DaHuy
LoaiTaiLieu   : DeCuongChiTiet | DeThiGiuaKy | DeThiCuoiKy
TrangThaiTL   : ChoDuyet | DaDuyet | TuChoi
LoaiNhanXet   : TichCuc | TieuCuc
TrangThaiDotKT: ChuanBi | ChoDuyet | DaDuyet | DaThucHien | DaHuy
TrangThaiDotTT: ChuanBi | ChoDuyet | DaDuyet | DangThucHien | DaKetThuc
TrangThaiPC   : DaPhanCong | DangThucTap | DaKetThuc | DaHuy
TrangThaiDN   : DangHopTac | TamNgung
TrangThaiSV   : DangHoc | BaoLuu | ThoiHoc | TotNghiep
TrangThaiTK   : 1 = Hoat dong | 0 = Khoa (BIT trong DB)
```

---

## 4. MAPPING: ROLE <-> PERMISSION <-> MENU

| Role     | Co the lam gi (tom tat)                                                      |
|----------|------------------------------------------------------------------------------|
| PDT      | Duyet CTDT, quan ly HocKy, quan ly NguoiDung (import), xem bao cao toan bo  |
| TTDTXS   | Duyet CTDT, duyet DotKienTap, duyet DotThucTap, xem dashboard                |
| BCN      | Tao/sua CTDT, tao/sua HocPhan, quan ly LopHocPhan (gan GV), xem lop minh     |
| CNHP     | Quan ly DoiNguGiangVienHP, duyet TaiLieu (DeCuong/DeThi), xem HP minh        |
| GV       | Upload TaiLieu cho lop minh day, nhap DanhGia SV, xem lop minh day           |
| CVHT     | Xem DanhGia SV cua lop minh, xu ly CanhBao (DaXuLy=1), xem lop HC minh      |
| SV       | Xem thong tin ca nhan, xem LopHocPhan dang hoc, nhap NhanXetSV (thuc tap)   |
| DN       | Xem SV thuc tap tai DN minh, nhap DiemDN + NhanXetDN, xem DotKienTap        |

### URL prefix theo role (Spring Security)
```
/admin/**      -> PDT, TTDTXS
/bcn/**        -> BCN
/cnhp/**       -> CNHP
/gv/**         -> GV
/cvht/**       -> CVHT
/sv/**         -> SV
/dn/**         -> DN
/common/**     -> Tat ca role da dang nhap
```

---

## 5. BUSINESS RULES TUYET DOI KHONG SAI

### Rule 1: GV chi duoc day lop HP neu co trong DoiNguGiangVienHP
```java
// Khi BCN gan GV vao LopHocPhan:
boolean inTeam = doiNguRepo.existsByMaHocPhanAndMaGiangVienAndTrangThai(maHP, maGV, true);
if (!inTeam) {
    // WARN (khong block) - hien thi canh bao nhung van cho ghi
}
```

### Rule 2: Workflow TrangThai chi di 1 chieu
```
CTDT   : BanNhap -> ChoDuyet -> DaDuyet hoac DaHuy
         DaDuyet KHONG the ve BanNhap
HP     : BanNhap -> ChoDuyet -> DaDuyet (khong co DaHuy)
TaiLieu: ChoDuyet -> DaDuyet hoac TuChoi
         TuChoi co the nop lai (tao ban moi, UNIQUE constraint cho phep)
DotKT  : ChuanBi -> ChoDuyet -> DaDuyet -> DaThucHien hoac DaHuy
DotTT  : ChuanBi -> ChoDuyet -> DaDuyet -> DangThucHien -> DaKetThuc
```

### Rule 3: Auto-create LopHocPhan khi CTDT duyet
```java
// Trong ChuongTrinhDaoTaoServiceImpl.pheduyet():
@Transactional
public void pheDuyetCTDT(String maCTDT, String maNguoiDuyet) {
    // 1. Cap nhat trang thai CTDT
    ctdt.setTrangThai(TrangThaiCTDT.DaDuyet);
    // 2. Lay danh sach HocPhan cua CTDT
    // 3. Lay HocKy hien tai (DangDienRa hoac SapDienRa)
    // 4. Tu dong tao LopHocPhan cho tung HP (MaGiangVien = null)
}
```

### Rule 4: DanhGia TieuCuc -> Auto alert + Email
```java
@Transactional
public void saveDanhGia(DanhGiaDTO dto) {
    DanhGiaVaCanhBao entity = mapper.toEntity(dto);
    repository.save(entity);
    if (entity.getLoaiNhanXet() == LoaiNhanXet.TieuCuc) {
        emailService.sendCanhBaoToSV(entity.getMaSV(), entity.getNoiDung());
        String maCVHT = nguoiDungRepo.findCVHTByMaSV(entity.getMaSV());
        emailService.sendCanhBaoToCVHT(maCVHT, entity.getMaSV(), entity.getNoiDung());
    }
}
```

### Rule 5: Deadline De Cuong Chi Tiet = HocKy.NgayBatDau + 14 ngay
```java
// Trong TaiLieuService khi kiem tra han nop:
LocalDate deadline = hocKy.getNgayBatDau().plusDays(14);
boolean quaHan = LocalDate.now().isAfter(deadline);
// Hien thi canh bao neu qua han
```

### Rule 6: UNIQUE constraint tren TaiLieuMonHoc(MaLopHP, Loai)
```
Moi lop HP chi co DUY NHAT 1 DeCuongChiTiet, 1 DeThiGiuaKy, 1 DeThiCuoiKy
Neu GV nop lai (sau khi bi TuChoi):
  -> Xoa ban cu HOAC update ban cu (khong tao ban moi)
  -> Set TrangThai = ChoDuyet, xoa NguoiDuyet, NgayDuyet cu
```

### Rule 7: UNIQUE constraint tren PhanCongThucTap(MaDotTT, MaSV)
```
1 SV chi duoc phan cong 1 lan trong 1 dot thuc tap
Khi import Excel: neu trung -> bao loi, bo qua dong do, tiep tuc import
```

---

## 6. URL MAPPING CHUAN (Controller -> Template)

```
GET  /login                           -> auth/login.html
POST /login                           -> [Spring Security xu ly]
GET  /dashboard                       -> dashboard/index.html

-- Nguoi dung --
GET  /nguoi-dung                      -> nguoidung/list.html
GET  /nguoi-dung/them                 -> nguoidung/form.html
POST /nguoi-dung/them                 -> redirect:/nguoi-dung
GET  /nguoi-dung/{id}/sua             -> nguoidung/form.html
POST /nguoi-dung/{id}/sua             -> redirect:/nguoi-dung
POST /nguoi-dung/{id}/khoa            -> redirect:/nguoi-dung
POST /nguoi-dung/import               -> redirect:/nguoi-dung (multipart)

-- Doanh nghiep --
GET  /doanh-nghiep                    -> doanhnghiep/list.html
GET  /doanh-nghiep/them               -> doanhnghiep/form.html
POST /doanh-nghiep/them               -> redirect:/doanh-nghiep
GET  /doanh-nghiep/{ma}/sua           -> doanhnghiep/form.html
POST /doanh-nghiep/{ma}/sua           -> redirect:/doanh-nghiep

-- Hoc phan --
GET  /hoc-phan                        -> hocphan/list.html
GET  /hoc-phan/them                   -> hocphan/form.html
POST /hoc-phan/them                   -> redirect:/hoc-phan
GET  /hoc-phan/{ma}                   -> hocphan/detail.html
POST /hoc-phan/{ma}/nop-duyet         -> redirect:/hoc-phan/{ma}
POST /hoc-phan/{ma}/doi-ngu/them-gv   -> redirect:/hoc-phan/{ma}
POST /hoc-phan/{ma}/doi-ngu/xoa-gv    -> redirect:/hoc-phan/{ma}

-- CTDT --
GET  /ctdt                            -> ctdt/list.html
GET  /ctdt/them                       -> ctdt/form.html
POST /ctdt/them                       -> redirect:/ctdt
GET  /ctdt/{ma}                       -> ctdt/detail.html
POST /ctdt/{ma}/nop-duyet             -> redirect:/ctdt/{ma}
POST /ctdt/{ma}/phe-duyet             -> redirect:/ctdt/{ma}  [TTDTXS/PDT]
POST /ctdt/{ma}/tu-choi               -> redirect:/ctdt/{ma}  [TTDTXS/PDT]

-- Lop Hoc Phan --
GET  /lop-hoc-phan                    -> lophocphan/list.html
GET  /lop-hoc-phan/{maLopHP}          -> lophocphan/detail.html
POST /lop-hoc-phan/{maLopHP}/gan-gv   -> redirect:/lop-hoc-phan/{maLopHP}

-- Tai lieu --
GET  /tai-lieu/lop/{maLopHP}          -> tailieu/list.html
POST /tai-lieu/lop/{maLopHP}/nop      -> redirect:/tai-lieu/lop/{maLopHP}
POST /tai-lieu/{maTL}/duyet           -> redirect:/tai-lieu/lop/{maLopHP}
POST /tai-lieu/{maTL}/tu-choi         -> redirect:/tai-lieu/lop/{maLopHP}

-- Danh gia & Canh bao --
GET  /danh-gia/lop/{maLopHP}          -> danhgia/list.html
POST /danh-gia/lop/{maLopHP}/them     -> redirect:/danh-gia/lop/{maLopHP}
GET  /canh-bao/lop-hc/{maLopHC}       -> danhgia/canhbao.html [CVHT]
POST /canh-bao/{maDG}/xu-ly           -> redirect:/canh-bao/lop-hc/{maLopHC}

-- Kien tap --
GET  /kien-tap                        -> kientap/list.html
GET  /kien-tap/them                   -> kientap/form.html
POST /kien-tap/them                   -> redirect:/kien-tap
GET  /kien-tap/{ma}                   -> kientap/detail.html
POST /kien-tap/{ma}/nop-duyet         -> redirect:/kien-tap/{ma}
POST /kien-tap/{ma}/phe-duyet         -> redirect:/kien-tap/{ma}
POST /kien-tap/{ma}/nhan-xet-gv       -> redirect:/kien-tap/{ma}
POST /kien-tap/{ma}/nhan-xet-dn       -> redirect:/kien-tap/{ma}

-- Thuc tap --
GET  /thuc-tap                        -> thuctap/list.html
GET  /thuc-tap/them                   -> thuctap/form.html
POST /thuc-tap/them                   -> redirect:/thuc-tap
GET  /thuc-tap/{ma}                   -> thuctap/detail.html
POST /thuc-tap/{ma}/phan-cong/import  -> redirect:/thuc-tap/{ma} (Excel import)
POST /thuc-tap/{ma}/phan-cong/{id}/nhap-diem -> redirect:/thuc-tap/{ma}

-- Bao cao --
GET  /bao-cao/tong-quan               -> baocao/tongquan.html
GET  /bao-cao/xuat-excel/{loai}       -> [download file]
```

---

## 7. DEPENDENCY (pom.xml) CHUAN

```xml
<!-- Spring Boot Parent -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.x</version>
</parent>

<!-- Core -->
spring-boot-starter-web
spring-boot-starter-thymeleaf
thymeleaf-extras-springsecurity6
spring-boot-starter-security
spring-boot-starter-data-jpa
spring-boot-starter-validation
spring-boot-starter-mail

<!-- DB -->
mysql-connector-j

<!-- Excel -->
org.apache.poi:poi-ooxml:5.2.5

<!-- Utilities -->
org.projectlombok:lombok
org.mapstruct:mapstruct
org.mapstruct:mapstruct-processor
```

---

## 8. application.properties CHUAN

```properties
# Server
server.port=8080

# DataSource (XAMPP MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/QuanLyCTDTDB?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Ho_Chi_Minh
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Thymeleaf
spring.thymeleaf.cache=false
spring.thymeleaf.encoding=UTF-8

# File Upload
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=25MB
file.upload-dir=uploads/

# Mail (cau hinh thuc te khi deploy)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your@email.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Logging
logging.level.org.springframework.security=DEBUG
```

---

## 9. SECURITY CONFIG CHUAN

```java
// SecurityConfig.java - CHEP Y HET, KHONG BO SOT ROLE NAO

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/login", "/error").permitAll()
            // PDT: quan ly toan bo nguoi dung va hoc ky
            .requestMatchers("/nguoi-dung/**").hasAnyRole("PDT", "TTDTXS")
            .requestMatchers("/hoc-ky/**").hasRole("PDT")
            // BCN: quan ly CTDT va HP
            .requestMatchers("/ctdt/**").hasAnyRole("BCN", "PDT", "TTDTXS")
            .requestMatchers("/hoc-phan/**").hasAnyRole("BCN", "CNHP", "PDT")
            // CNHP: quan ly doi ngu va tai lieu
            .requestMatchers("/tai-lieu/**").hasAnyRole("CNHP", "GV", "PDT")
            // GV: nhap danh gia
            .requestMatchers("/danh-gia/**").hasAnyRole("GV", "CVHT", "PDT")
            // CVHT: xu ly canh bao
            .requestMatchers("/canh-bao/**").hasAnyRole("CVHT", "PDT")
            // Kien tap & Thuc tap
            .requestMatchers("/kien-tap/**").hasAnyRole("BCN", "GV", "TTDTXS", "PDT", "DN")
            .requestMatchers("/thuc-tap/**").hasAnyRole("BCN", "GV", "TTDTXS", "PDT", "SV", "DN")
            // Bao cao
            .requestMatchers("/bao-cao/**").hasAnyRole("PDT", "TTDTXS", "BCN")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/dashboard")
            .failureUrl("/login?error")
            .permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        );
    return http.build();
}

// Password encoder - PHAI DUNG BCRYPT
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

---

## 10. THYMELEAF - QUY TAC VIEW

```html
<!-- Base layout: templates/layout/base.html -->
<!-- Su dung Thymeleaf Layout Dialect hoac th:replace -->

<!-- Kiem tra role trong template -->
<div sec:authorize="hasRole('PDT')">
    <!-- Chi hien thi voi PDT -->
</div>
<div sec:authorize="hasAnyRole('BCN', 'CNHP')">
    <!-- Hien thi voi BCN hoac CNHP -->
</div>

<!-- Hien thi ten nguoi dung dang nhap -->
<span sec:authentication="name"></span>

<!-- Form POST voi CSRF (Spring Security bat buoc) -->
<form th:action="@{/ctdt/them}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
</form>

<!-- Hien thi flash message -->
<div th:if="${successMessage}" class="alert alert-success">
    <span th:text="${successMessage}"></span>
</div>
<div th:if="${errorMessage}" class="alert alert-danger">
    <span th:text="${errorMessage}"></span>
</div>

<!-- Phan trang (spring data pageable) -->
<nav th:if="${page.totalPages > 1}">
    <a th:href="@{/danh-sach(page=${page.number - 1})}"
       th:class="${page.first} ? 'disabled' : ''">Truoc</a>
</nav>
```

---

## 11. TRAT TU CODE MODULE (PRIORITY)

```
P0 - BAN PHAI CODE TRUOC (nen tang, moi thu phu thuoc):
  [x] 1. Database setup (chay 01_create_tables.sql + 02_seed_data.sql)
  [ ] 2. Entity classes (14 entities)
  [ ] 3. Enum classes (13 enums)
  [ ] 4. Repository interfaces
  [ ] 5. SecurityConfig + UserDetailsServiceImpl
  [ ] 6. Login page + Dashboard

P1 - MODULE CHINH (sau khi P0 xong):
  [ ] 7. Quan ly Nguoi dung (import Excel, CRUD, khoa TK)
  [ ] 8. Quan ly Doanh nghiep (CRUD + tao tai khoan DN)
  [ ] 9. Quan ly Hoc phan (CRUD + doi ngu GV + duyet)
  [ ] 10. Quan ly CTDT (CRUD + upload Word + workflow duyet + auto-create LopHP)
  [ ] 11. Quan ly Lop Hoc Phan (gan GV, xem danh sach)

P2 - MODULE NGHIEP VU:
  [ ] 12. Tai lieu mon hoc (upload + workflow duyet)
  [ ] 13. Danh gia & Canh bao (nhap NX + auto alert + CVHT xu ly)
  [ ] 14. Kien tap (tao dot + workflow + nhan xet)
  [ ] 15. Thuc tap (tao dot + import Excel + nhap diem)

P3 - BAO CAO:
  [ ] 16. Dashboard thong ke
  [ ] 17. Xuat Excel

```

---

## 12. LOI HAY GAP - CANH BAO

```
1. KHONG dung @RestController cho Thymeleaf views -> Phai dung @Controller
2. KHONG tra ve JSON tu controller Thymeleaf -> Tra ve String (ten template)
3. Redirect sau POST: return "redirect:/url" (Post-Redirect-Get pattern)
4. File upload: MaLopHP + Loai phai UNIQUE -> kiem tra truoc khi save
5. BCrypt: Mat khau seed data dung $2a$10$... la placeholder, phai hash lai khi test
6. Lazy loading: Them @Transactional cho service method neu access lazy relation
7. CSRF: Spring Security bat buoc CSRF token trong moi form POST
8. Encoding: Tat ca VARCHAR tieng Viet phai utf8mb4 (da co trong create_tables.sql)
9. NguoiDungVaiTro: Composite PK = (MaNguoiDung, VaiTro) -> Phai dung @EmbeddedId hoac @IdClass
10. TaiLieuMonHoc: Khi GV nop lai sau TuChoi -> UPDATE ban hien tai, KHONG INSERT moi
```

---

## 13. DATA FLOW CHINH - DE NAM CHAC

```
LUONG 1: Tao CTDT -> Duyet -> Auto-create LopHP
  BCN -> POST /ctdt/them -> [TrangThai=BanNhap]
  BCN -> POST /ctdt/{ma}/nop-duyet -> [TrangThai=ChoDuyet]
  TTDTXS/PDT -> POST /ctdt/{ma}/phe-duyet -> [TrangThai=DaDuyet]
    -> Service.autoCreateLopHocPhan() chay trong cung transaction
    -> Tao LopHocPhan cho TUNG HocPhan, MaGiangVien=null
  BCN -> POST /lop-hoc-phan/{ma}/gan-gv -> [MaGiangVien=GVxxx]
    -> Check: GVxxx co trong DoiNguGiangVienHP khong? (warn neu khong)

LUONG 2: GV nop tai lieu -> CNHP duyet
  GV -> POST /tai-lieu/lop/{maLopHP}/nop -> [TrangThai=ChoDuyet]
  CNHP -> POST /tai-lieu/{ma}/duyet -> [TrangThai=DaDuyet]
  CNHP -> POST /tai-lieu/{ma}/tu-choi -> [TrangThai=TuChoi]
  GV -> POST /tai-lieu/lop/{maLopHP}/nop (lan 2) -> UPDATE ban cu (khong tao moi)

LUONG 3: GV nhan xet tieu cuc -> Canh bao
  GV -> POST /danh-gia/lop/{maLopHP}/them -> [LoaiNhanXet=TieuCuc]
    -> DanhGiaService.save() -> Email to SV + CVHT (async)
  CVHT -> GET /canh-bao/lop-hc/{maLopHC} -> Xem danh sach chua xu ly
  CVHT -> POST /canh-bao/{ma}/xu-ly -> [DaXuLy=1, KetQuaXuLy=...]

LUONG 4: Thuc tap
  PDT/TTDTXS -> Tao DotThucTap -> Duyet
  PDT -> Import Excel PhanCongThucTap -> Tao nhieu PhanCong
  DN -> Nhap DiemDN + NhanXetDN cho tung SV
  GV -> Nhap DiemGV + NhanXetGV cho SV minh giam sat
  SV -> Nhap NhanXetSV (cam nhan)
```
