# QuanLyCTDTDB — He Thong Quan Ly Dao Tao Xuat Sac

He thong noi bo quan ly Chuong Trinh Dao Tao (CTDT), Hoc Phan, Lop Hoc Phan, Kien Tap va Thuc Tap cho khoi dao tao xuat sac truong dai hoc.

> **Trang thai:** Dang o giai doan Phase 2 (module Nguoi Dung da chay, cac module nghiep vu khac dang hoan thien layer controller/template). Xem `docs/06_PROJECT_SCAFFOLD.md` va `docs/07_ROADMAP.md` de biet phan viec chi tiet.

---

## 1. Tech stack

| Tang              | Cong nghe                                        |
|-------------------|--------------------------------------------------|
| Ngon ngu          | Java 17                                          |
| Framework         | Spring Boot 3.5.6 (Web, Data JPA, Security, Mail, Validation) |
| View              | Thymeleaf 3.1 + Layout Dialect 3.3 + Bootstrap 5 |
| ORM               | Hibernate 6.6 (JPA 3.1)                          |
| Database          | MySQL 8.0+ (dev: XAMPP 8.2)                      |
| Build             | Maven (wrapper: `./mvnw`)                        |
| File Excel        | Apache POI 5.2.x                                 |
| Log / Utility     | Lombok, SLF4J                                    |

Phien ban chi tiet xem `pom.xml`. Tat ca `spring-*`, `hibernate-*`, `thymeleaf-*` duoc quan ly boi BOM cua `spring-boot-starter-parent 3.5.6` nen KHONG khai bao version thu cong trong module con.

---

## 2. Yeu cau moi truong

- JDK 17 (`java -version` bao 17.x)
- MySQL 8.0+ hoac XAMPP co MySQL 8
- Maven Wrapper di kem (`./mvnw`) — khong can cai Maven rieng
- (Tuy chon) IDE: IntelliJ IDEA / VS Code + Spring Boot Tools

---

## 3. Cai dat & chay lan dau

### 3.1. Chuan bi Database

```bash
# Tao database (chay trong mysql client cua XAMPP)
mysql -u root -p < scripts/00_create_database.sql    # neu chua co
# Khoi tao 20 bang
mysql -u root -p QuanLyCTDTDB < scripts/01_create_tables.sql
# Seed du lieu mau (optional nhung khuyen dung cho dev)
mysql -u root -p QuanLyCTDTDB < scripts/02_seed_data.sql
```

> Chi tiet thu tu + kiem tra: `scripts/README.md`.

### 3.2. Cau hinh ket noi

Sua `src/main/resources/application.properties` neu MySQL khac mac dinh:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/QuanLyCTDTDB?...
spring.datasource.username=root
spring.datasource.password=
```

Mac dinh: user `root`, password rong (phu hop XAMPP). **KHONG commit password that ben ngoai localhost.**

### 3.3. Build + run

```bash
./mvnw spring-boot:run
```

Server khoi dong tren `http://localhost:8080` va redirect toi `/login`.

### 3.4. Dang nhap thu

Tat ca tai khoan seed co password: `Password@123`

| TenDangNhap   | Role chinh                  | Dung de thu nghiem                          |
|---------------|-----------------------------|---------------------------------------------|
| `admin`       | Admin                       | Toan quyen he thong                         |
| `tran.van.an` | GiangVien + PDT + TTDTXS    | Quan ly nguoi dung, duyet CTDT              |
| `le.thi.bich` | GiangVien + TTDTXS          | Phe duyet CTDT/HP                           |
| `nguyen.cong` | GiangVien + CVHT            | Xu ly canh bao sinh vien K22A               |
| `pham.dung`   | GiangVien + CVHT + CNHP     | Chu nhiem HP-LTW + co van K22B              |
| `hoang.em`    | GiangVien + CNHP            | Chu nhiem HP-KLTN                           |
| `sv.2022001`  | SinhVien                    | Sinh vien dang thuc tap tai FPT             |
| `dn.fpt`      | DoanhNghiep                 | Danh gia SV thuc tap                        |

Danh sach day du + so ban ghi seed: `docs/02_Mô Tả & Thiết kế dữ liệu.md` § 4.

---

## 4. Cau truc thu muc

```
QuanLyCTDTDB/
├── docs/                    # Tai lieu thiet ke (BAT BUOC doc truoc khi code)
│   ├── 00_MASTER_REFERENCE.md
│   ├── 01_ERD_SCHEMA.md
│   ├── 02_Mô Tả & Thiết kế dữ liệu.md
│   ├── 03_WORKFLOW.md
│   ├── 04_DEV_CHECKLIST.md
│   ├── 05_UI_DESIGN_SYSTEM.md
│   ├── 06_PROJECT_SCAFFOLD.md   <- Scaffold thuc te + gap analysis
│   └── 07_ROADMAP.md            <- Ke hoach lam viec chi tiet theo phase
├── scripts/                 # SQL setup + seed
│   ├── README.md
│   ├── 01_create_tables.sql
│   └── 02_seed_data.sql
├── src/main/java/com/ntu/quanlyctdtdb/
│   ├── QuanLyCtdtDbApplication.java
│   ├── config/              # SecurityConfig, WebMvcConfig
│   ├── controller/          # 1 controller / module
│   ├── dto/                 # Form binding + Excel import DTOs
│   ├── entity/              # JPA entities + @Embeddable Id classes
│   ├── enums/               # 15 enum types (state machines + kinds)
│   ├── exception/           # BusinessException, GlobalExceptionHandler
│   ├── repository/          # Spring Data JPA repositories
│   ├── security/            # UserDetailsServiceImpl, CustomUserDetails
│   ├── service/             # Interfaces
│   ├── service/impl/        # Implementations (bao gom MockEmailService)
│   └── util/                # ExcelImportUtil, FileStorageUtil
├── src/main/resources/
│   ├── application.properties
│   ├── static/              # css, js, images
│   └── templates/           # Thymeleaf views (kebab-case + tieng Viet)
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md                # File ban dang doc
```

---

## 5. Nguyen tac phat trien

1. Doc thu tu: `docs/00` -> `01` -> `02` -> `03` -> `04` -> `05`. Khi co xung dot, `00_MASTER_REFERENCE` la nguon su that.
2. Moi thay doi schema/nghiep vu/cong nghe PHAI cap nhat docs tuong ung **truoc** khi code.
3. Khong chay `ddl-auto=update`. Luon tao script migration moi trong `scripts/` (`03_*.sql`, `04_*.sql`, ...) va giu phep thay doi co the review qua git.
4. Entity theo tieng Viet khong dau (`HocPhan`, `NguoiDung`...) khop 1-1 voi ten bang MySQL. Dung `@Enumerated(EnumType.STRING)` cho toan bo enum.
5. `spring.jpa.open-in-view=false` — KHONG duoc doc collection LAZY ngoai transaction. Dung `@EntityGraph` o repository hoac `JOIN FETCH` trong custom query.
6. Template path theo kebab-case + tieng Viet khong dau: `templates/nguoi-dung/danh-sach.html`, khong phai `templates/nguoidung/list.html`.
7. Moi form POST phai co CSRF token (Spring Security bat mac dinh).

Chi tiet: `docs/05_UI_DESIGN_SYSTEM.md` + `docs/04_DEV_CHECKLIST.md`.

---

## 6. Build & deploy

### Build JAR

```bash
./mvnw clean package -DskipTests
java -jar target/quanlyctdtdb-0.0.1-SNAPSHOT.jar
```

### Test

```bash
./mvnw test
```

### Production checklist

Truoc khi trien khai, xem `docs/04_DEV_CHECKLIST.md` § "KIEM TRA CUOI CUNG TRUOC DEMO" + `docs/07_ROADMAP.md` Phase 6.

---

## 7. Tai lieu them

- Master reference: `docs/00_MASTER_REFERENCE.md`
- ERD: `docs/01_ERD_SCHEMA.md` (PlantUML)
- Work plan / Roadmap chi tiet: `docs/07_ROADMAP.md`
- Lich su fix: `docs/06_PROJECT_SCAFFOLD.md` § "Thay doi gan day"

---

## 8. Huong dan dong gop (dev noi bo)

1. Tao branch tu `spring-boot-project-manager`: `feat/<module>-<mo-ta>` hoac `fix/<issue>`.
2. Commit nho, message ro rang (tieng Anh hoac tieng Viet khong dau).
3. Khi chinh schema: **bat buoc** them file SQL moi trong `scripts/`, khong sua file `01_create_tables.sql` da chay production.
4. Mo PR vao `spring-boot-project-manager`, kem screenshot + step-to-test.
