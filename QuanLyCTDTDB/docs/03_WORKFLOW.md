# 03_WORKFLOW — He Thong Quan Ly Dao Tao Xuat Sac

> Tai lieu nay mo ta luong nghiep vu chi tiet A-Z cho tung module.
> Moi buoc co: Actor, Action, Table bi tac dong, Rang buoc va Ket qua mong doi.
> Phai nhat quan voi 01_ERD_SCHEMA.md (20 bang) va 04_DEV_CHECKLIST.md.

---

## LICH SU TRANG THAI TOAN DIEN

```
CTDT:         BanNhap -> ChoDuyet -> DaDuyet
                                  -> BanNhap (tu choi)
              DaDuyet -> DaHuy

HocPhan:      BanNhap -> ChoDuyet -> DaDuyet
                                  -> BanNhap (tu choi)

LopHocPhan:   DangMo -> DaDong -> [ket thuc hoc ky]
              DangMo -> DaHuy   [huy bo]

DotKienTap:   ChuanBi -> ChoDuyet -> DaDuyet -> DaThucHien
              {ChuanBi,ChoDuyet,DaDuyet,DaThucHien} -> DaHuy
DotThucTap:   ChuanBi -> ChoDuyet -> DaDuyet -> DangThucHien -> DaKetThuc
              {any} -> DaHuy (truoc khi DaKetThuc)

DanhSachThucTap: DaPhanCong -> DangThucTap -> DaKetThuc -> DaHuy

HocKyNamHoc:  SapDienRa -> DangDienRa -> DaKetThuc
NguoiDung:    TrangThaiTK = 1 (hoat dong) | 0 (bi khoa)
DoanhNghiep:  DangHopTac | TamNgung
```

---

## MODULE 0: AUTHENTICATION & AUTHORIZATION

### WF-00.1: Dang nhap he thong

```
Actor: Tat ca nguoi dung

BUOC 1 — Nguoi dung truy cap bat ky URL
  -> Spring Security: chua co session -> redirect /login

BUOC 2 — Nguoi dung nhap TenDangNhap + MatKhau, submit POST /login
  -> UserDetailsServiceImpl.loadUserByUsername(tenDangNhap)
     1. Tim NguoiDung theo TenDangNhap (hoac Email) trong bang NguoiDung
     2. Neu khong tim thay: throw UsernameNotFoundException -> redirect /login?error
     3. Kiem tra TrangThaiTK = 1 (hoat dong)
        Neu = 0: throw DisabledException -> redirect /login?disabled
     4. Lay danh sach VaiTro tu bang NhomNguoiDung (PDT, TTDTXS, CVHT, CNHP)
     5. Map LoaiNguoiDung -> GrantedAuthority:
          Admin        -> ROLE_ADMIN
          GiangVien    -> ROLE_GIANG_VIEN
          SinhVien     -> ROLE_SINH_VIEN
          DoanhNghiep  -> ROLE_DOANH_NGHIEP
     6. Map VaiTro -> GrantedAuthority:
          PDT     -> ROLE_PDT
          TTDTXS  -> ROLE_TTDTXS
          CVHT    -> ROLE_CVHT
          CNHP    -> ROLE_CNHP
     7. DaoAuthenticationProvider (Spring Security standard):
        - UserDetailsServiceImpl.loadUserByUsername() tra ve CustomUserDetails
        - CustomUserDetails.getAuthorities() tra ve list GrantedAuthority tu NhomNguoiDung
        - PasswordEncoder.matches(password, hash) xac thuc via BCryptPasswordEncoder
        - Neu sai: redirect /login?error

BUOC 3 — Dang nhap thanh cong
  -> Spring Security tao HttpSession, luu SecurityContext
  -> Redirect /dashboard (defaultSuccessUrl)

BUOC 4 — DashboardController.dashboard()
  -> Lay role tu SecurityContextHolder
  -> Truyen model thong ke tuong ung (khac nhau theo role)
  -> Hien thi templates/dashboard/dashboard.html
  -> Sidebar hien menu dung voi role (sec:authorize)
```

**Rang buoc:**
- 1 nguoi dung co the co nhieu GrantedAuthority (vi du: GiangVien + CNHP + CVHT)
- Session timeout: 30 phut (cau hinh trong application.properties)
- Sau timeout: redirect /login, hien thong bao "Phien lam viec da het han"

---

### WF-00.2: Dang xuat

```
Actor: Nguoi dung dang dang nhap

-> POST /logout
-> Spring Security: xoa HttpSession, xoa SecurityContext
-> Redirect /login?logout
-> Hien thi message "Da dang xuat thanh cong"
```

---

## MODULE 1: QUAN LY NGUOI DUNG

### WF-01.1: Tao nguoi dung moi (thu cong)

```
Actor: PDT

BUOC 1 — GET /nguoi-dung/them
  -> NguoiDungController: truyen model cac option (listLoaiNguoiDung, listVaiTro, listLopHC, listGV_CNHP)
  -> Hien thi form tao moi

BUOC 2 — POST /nguoi-dung/them (NguoiDungDTO)
  -> Validate:
       TenDangNhap unique trong NguoiDung
       Email unique trong NguoiDung
       MatKhau >= 8 ky tu
       LoaiNguoiDung hop le

BUOC 3 — NguoiDungService.create(dto):
  1. Hash MatKhau bang BCrypt
  2. INSERT INTO NguoiDung (MaNguoiDung, TenDangNhap, MatKhauHash, Email, HoTen, ...)
     MaNguoiDung = tu sinh (format: GV001, SV2024001, DN001, AD001)
  3. Neu LoaiNguoiDung = GiangVien:
       INSERT INTO GiangVien (MaGV = MaNguoiDung, MaNguoiDung, LoaiGiangVien, ...)
  4. Neu LoaiNguoiDung = SinhVien:
       INSERT INTO SinhVien (MaSV = MaNguoiDung, MaNguoiDung, MaLopHC, TrangThaiSV='DangHoc')
  5. Neu co VaiTro (PDT, TTDTXS, CVHT, CNHP):
       INSERT INTO NhomNguoiDung (MaNguoiDung, VaiTro)
       (1 nguoi co the co nhieu vai tro)

BUOC 4 — redirect /nguoi-dung?success -> flash "Tao nguoi dung thanh cong"
```

**Bang bi tac dong:** NguoiDung, GiangVien (neu GV), SinhVien (neu SV), NhomNguoiDung (neu co VaiTro)

---

### WF-01.2: Import Nguoi Dung tu Excel

```
Actor: PDT

BUOC 1 — POST /nguoi-dung/import (MultipartFile)
  -> Kiem tra extension: .xlsx hoac .xls
  -> Kich thuoc <= 20MB

BUOC 2 — ExcelImportUtil.parseNguoiDung(file):
  -> Doc tung dong (tu dong 2, dong 1 la header)
  -> Map sang NguoiDungExcelDTO: TenDangNhap, MatKhau, Email, HoTen, LoaiNguoiDung, [VaiTro], [MaLopHC]

BUOC 3 — Xu ly tung ban ghi:
  OK:    TenDangNhap chua ton tai AND Email chua ton tai -> createWithRole()
  SKIP:  TenDangNhap DA ton tai -> them vao danh sach loi "TenDangNhap da ton tai"
  SKIP:  Email DA ton tai      -> them vao danh sach loi "Email da ton tai"
  SKIP:  Validate that bai     -> them vao danh sach loi

BUOC 4 — redirect /nguoi-dung/import/result
  -> Hien thi: So ban ghi thanh cong, So ban ghi loi + chi tiet loi
```

---

### WF-01.3: Khoa / Mo khoa tai khoan

```
Actor: PDT

-> POST /nguoi-dung/{ma}/khoa
-> NguoiDungService.toggleStatus(ma):
     TrangThaiTK = 1 -> dat 0 (khoa)
     TrangThaiTK = 0 -> dat 1 (mo khoa)
-> UPDATE NguoiDung SET TrangThaiTK = !TrangThaiTK WHERE MaNguoiDung = ?
-> Neu tai khoan bi khoa: session hien tai cua nguoi dung do se invalid o lan request tiep theo
-> redirect /nguoi-dung -> flash message
```

---

## MODULE 2: QUAN LY DOANH NGHIEP

### WF-02.1: Tao Doanh Nghiep moi

```
Actor: PDT, TTDTXS

BUOC 1 — GET /doanh-nghiep/them -> form
BUOC 2 — POST /doanh-nghiep/them (DoanhNghiepDTO)
  -> Validate: TenDoanhNghiep khong trong, Email hop le (neu co)

BUOC 3 — DoanhNghiepService.create(dto):
  1. INSERT INTO DoanhNghiep (..., TrangThai='DangHopTac')
     MaDoanhNghiep = tu sinh (format: DN001, DN002, ...)
  2. [Tuy chon] Tao tai khoan NguoiDung cho DN:
       INSERT INTO NguoiDung (MaNguoiDung = MaDoanhNghiep, LoaiNguoiDung='DoanhNghiep',
                              TenDangNhap = MaDoanhNghiep, MatKhauHash = BCrypt("Dn@123456"))
       -> Thong bao PDT ve tai khoan mac dinh, yeu cau doi mat khau

BUOC 4 — redirect /doanh-nghiep -> flash message
```

**Bang bi tac dong:** DoanhNghiep, NguoiDung (neu tao tai khoan)

---

## MODULE 3: QUAN LY HOC PHaN

### WF-03.1: Tao va phe duyet Hoc Phan

```
Actor: CNHP tao, TTDTXS phe duyet

BUOC 1 — CNHP: GET /hoc-phan/them -> form
BUOC 2 — CNHP: POST /hoc-phan/them (HocPhanDTO)
  -> Validate: MaHocPhan unique, SoTinChi 1-10, ChuNhiemHP la GV ton tai
  -> INSERT INTO HocPhan (..., TrangThai='BanNhap', ChuNhiemHP=maGV)
  [NOTE — 2026-Q2 batch 3]
  Truoc day doc ta yeu cau tu dong them ChuNhiemHP vao DoiNguGiangVienHP
  khi tao HP. Hien tai code KHONG thuc hien auto-add: CNHP se duoc them
  tu tay qua tab "Doi Ngu Giang Vien" trong trang chi-tiet HP (xem WF-03.2).
  Rang buoc bao ve (service layer): KHONG cho xoa GV ra khoi doi ngu
  neu GV do la ChuNhiemHP cua chinh HP.

BUOC 3 — CNHP: POST /hoc-phan/{ma}/nop-duyet
  -> Kiem tra: TrangThai = 'BanNhap'
  -> UPDATE HocPhan SET TrangThai='ChoDuyet' WHERE MaHocPhan = ?
  -> [Tuy chon] Gui thong bao email cho TTDTXS

BUOC 4 — TTDTXS: GET /hoc-phan?trangThai=ChoDuyet -> xem danh sach can duyet

BUOC 5a — TTDTXS: POST /hoc-phan/{ma}/phe-duyet
  -> Kiem tra: TrangThai = 'ChoDuyet'
  -> UPDATE HocPhan SET TrangThai='DaDuyet'

BUOC 5b — TTDTXS: POST /hoc-phan/{ma}/tu-choi (kem ly do)
  -> UPDATE HocPhan SET TrangThai='BanNhap'
  -> Luu ly do vao GhiChu (hoac gui email cho CNHP)
```

**Bang bi tac dong:** HocPhan, DoiNguGiangVienHP

---

### WF-03.2: Quan ly Doi Ngu Giang Vien Hoc Phan

```
Actor: CNHP, TTDTXS, PDT, ADMIN (security: hasAnyRole('PDT','TTDTXS','ADMIN'))
Implementation (2026-Q2 batch 3): DoiNguGvService + DoiNguGvServiceImpl,
                                  3 endpoint tren HocPhanController.

BUOC 1 — GET /hoc-phan/chi-tiet/{ma}
  -> Controller goi doiNguService.findByHocPhan(ma) — query
     DoiNguGiangVienHpRepository.findByHocPhanFetch JOIN FETCH GV + NguoiDung
     (bat buoc vi open-in-view=false).
  -> Hien thi tab "Doi Ngu Giang Vien" + modal "Them GV" (select GV tu
     giangVienRepo.findAllFetchNguoiDung).

BUOC 2 — Them GV: POST /hoc-phan/chi-tiet/{ma}/doi-ngu/them (DoiNguGvDTO)
  -> Validate: maGV khong trong, GV ton tai trong bang GiangVien
  -> Kiem tra chua ton tai trong DoiNguGiangVienHP (MaHocPhan, MaGiangVien)
     (phat sinh BusinessException neu da co)
  -> INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien, TrangThai= dto.trangThai ?: 1)
     (cot `created_at` do DB tu sinh qua @CreationTimestamp — khong set tay
      NgayThem vi schema khong co cot do.)

BUOC 3 — Toggle trang thai: POST /hoc-phan/chi-tiet/{ma}/doi-ngu/toggle (maGV)
  -> UPDATE DoiNguGiangVienHP SET TrangThai = !TrangThai
  -> Dung de "tam ngung" GV (TrangThai=0) ma khong mat record — GV nay
     khi duoc gan vao LopHocPhan van rot vao nhanh warningMsg o WF-05.1.

BUOC 4 — Xoa hoan toan: POST /hoc-phan/chi-tiet/{ma}/doi-ngu/xoa (maGV)
  -> Guard: GV KHONG duoc la ChuNhiemHP cua HP (throw BusinessException).
  -> DELETE FROM DoiNguGiangVienHP WHERE (MaHocPhan, MaGiangVien)
  -> Khuyen nghi: dung "Toggle" (soft disable) cho GV cu chua muon xoa,
     chi "Xoa" khi GV chuyen cong tac / khong con lien quan.
```

**Bang bi tac dong:** DoiNguGiangVienHP
**DTO:** `dto/DoiNguGvDTO.java` (maHocPhan, maGV, trangThai)

---

## MODULE 4: QUAN LY CHUONG TRINH DAO TAO

### WF-04.1: Tao CTDT va them Hoc Phan

```
Actor: BCN (ChuNhiem CTDT), TTDTXS phe duyet

BUOC 1 — BCN: GET /ctdt/them -> form
BUOC 2 — BCN: POST /ctdt/them (ChuongTrinhDaoTaoDTO)
  -> Validate: MaCTDT unique
  -> INSERT INTO ChuongTrinhDaoTao (..., TrangThai='BanNhap', NguoiTao=currentUser)
  -> KHONG tao BCN_ThanhVien o buoc nay (lam rieng)

BUOC 3 — BCN/PDT/ADMIN: GET /ctdt/chi-tiet/{ma} -> hien thi chi tiet
  -> Hien thi 2 section:
     (a) Ban Chu Nhiem (bcnList) — query
         BcnThanhVienRepository.findByCtdtFetch JOIN FETCH GV + NguoiDung.
         UI cho phep:
           POST /ctdt/chi-tiet/{ma}/bcn/them (BcnThanhVienDTO)
             Validate: maGV + chucDanh != null. Rang buoc: 1 CTDT chi co
             DUY NHAT 1 Chu Nhiem (BcnThanhVienService.themThanhVien guard
             bang findFirstByChuongTrinhDaoTao_MaCTDTAndId_ChucDanh).
           POST /ctdt/chi-tiet/{ma}/bcn/xoa (maGV, chucDanh)
             Hard-delete record (khong soft disable).
     (b) Danh sach Hoc Phan cua CTDT + modal them HP

BUOC 4 — BCN: POST /ctdt/{ma}/them-hoc-phan (CTDT_HocPhanDTO)
  -> Truyen: MaHocPhan, HocKyThu (1-10), SoLopDuKien (mac dinh 1), BatBuoc (mac dinh true)
  -> Validate: HocPhan o TrangThai='DaDuyet' (chi them HP da duoc duyet)
  -> Kiem tra chua ton tai trong CTDT_HocPhan (MaCTDT, MaHocPhan)
  -> INSERT INTO CTDT_HocPhan (MaCTDT, MaHocPhan, HocKyThu, SoLopDuKien, BatBuoc, ...)

BUOC 5 — BCN: POST /ctdt/{ma}/nop-duyet
  -> Kiem tra: TrangThai = 'BanNhap'
  -> Kiem tra: Co it nhat 1 HocPhan trong CTDT
  -> UPDATE ChuongTrinhDaoTao SET TrangThai='ChoDuyet'
```

---

### WF-04.2: Phe duyet CTDT va tu dong tao Lop Hoc Phan (NGHIEP VU QUAN TRONG)

```
Actor: TTDTXS

BUOC 1 — TTDTXS: POST /ctdt/{ma}/phe-duyet
  -> Kiem tra: TrangThai = 'ChoDuyet'

BUOC 2 — UPDATE ChuongTrinhDaoTao SET
           TrangThai='DaDuyet', NguoiDuyet=currentUser, NgayDuyet=NOW()

BUOC 3 — [Doi quyet dinh thiet ke 2026-Q2 batch 3]
  LopHocPhan KHONG duoc tao tu dong khi CTDT DaDuyet.
  Ly do nghiep vu:
    - Mot CTDT mo nhieu nam lien tiep (vd: "CTDT CNTT Khoa 2023-2027"
      mo lop cho ca HK1-2023, HK1-2024, HK1-2025... khong biet ky nao).
    - So lop mo per-HP co the khac nhau moi ky theo tinh hinh tuyen sinh
      (vd: CNTT101 mo 3 lop o HK1-2023 nhung chi mo 2 o HK1-2024).
  Do do tao LopHocPhan la 1 ACTION THU CONG co tham so:
    POST /lop-hoc-phan/tao-hang-loat
         ?maCTDT=CTDT001&maHocKy=HK1-2023
         hpCode[]=CNTT101, soLop[]=3
         hpCode[]=CNTT102, soLop[]=4
    -> parseHocKyThu("HK1-2023") = 1 (parse chu so sau "HK")
    -> SELECT * FROM CTDT_HocPhan WHERE MaCTDT=? AND HocKyThu=1
       (chi HP thuoc ky 1 moi duoc mo lop cho MaHocKy=HK1-2023)
    -> Voi moi CtdtHocPhan:
         soLop = override.get(maHP) ?: ctdtHP.soLopDuKien
         Lap tu MaLopHocPhan = 1 den soLop:
           if NOT EXISTS (skip idempotent):
             INSERT INTO LopHocPhan (
                 MaCTDT, MaHocPhan, MaHocKy,
                 MaLopHocPhan (thu tu),
                 MaGiangVien = NULL,  <- chua phan cong
                 SiSoToiDa = 50,       <- mac dinh
                 SiSoThucTe = 0,
                 TrangThai = 'DangMo'
             )
    -> Flash:
         - "Tao moi N lop hoc phan thanh cong!" (neu N > 0)
         - "Khong tao moi lop nao (tat ca cac lop du kien da ton tai)"
           (neu N = 0 — idempotent skip).
  UI ho tro:
    - Trang /lop-hoc-phan?maCTDT=&maHocKy= hien luon section
      "Ke Hoach Mo Lop" liet ke HP du kien cua ky da chon + badge "Da mo/Chua mo".
    - Modal "Tao Hang Loat" pre-fill soLop = ctdtHP.soLopDuKien,
      cho user chinh lai truoc khi confirm (per-HP override).

BUOC 4 — Ket qua:
  - CTDT.TrangThai = 'DaDuyet'
  - NguoiDuyet + NgayDuyet duoc set (audit trail — fix 2026-Q2 B4)
  - LopHocPhan CHUA duoc tao. Admin / BCN / TTDTXS goi action thu cong o
    trang /lop-hoc-phan moi tao duoc.
  - MaGiangVien = NULL (chua co giang vien)
  - BCN/TTDTXS se phan cong GV sau (WF-05)
```

**Bang bi tac dong:** ChuongTrinhDaoTao, LopHocPhan

---

## MODULE 5: QUAN LY LOP HOC PHAN

### WF-05.1: Phan cong Giang Vien cho Lop Hoc Phan

```
Actor: BCN, TTDTXS

BUOC 1 — GET /lop-hoc-phan?maHocKy=HK20241&filter=chuaCoGV
  -> Hien thi danh sach LopHocPhan WHERE MaGiangVien IS NULL

BUOC 2 — POST /lop-hoc-phan/{ctdt}/{hp}/{hk}/{nhom}/gan-gv (maGV)
  -> Kiem tra GiangVien ton tai trong bang GiangVien

BUOC 3 — SOFT CHECK doi ngu (LopHocPhanController.phanCong):
  Truoc khi goi service lopHPService.phanCongGiangVien, controller query:
    SELECT * FROM DoiNguGiangVienHP
    WHERE MaHocPhan = ? AND MaGiangVien = ? AND TrangThai = TRUE
  Luong xu ly:
    (a) GV THUOC doi ngu va dang hoat dong:
        -> UPDATE LopHocPhan SET MaGiangVien = ? WHERE (composite PK)
        -> Flash successMsg: "Phan cong giang vien thanh cong!"
    (b) GV KHONG thuoc doi ngu (hoac ton tai nhung TrangThai=false):
        -> VAN UPDATE LopHocPhan SET MaGiangVien = ? (khong chan cung!)
        -> Flash warningMsg: "Da phan cong, nhung CANH BAO: GV <maGV> khong
           thuoc doi ngu cua HP <maHocPhan> (hoac dang tam ngung). Hay bo
           sung vao doi ngu tai trang Chi tiet Hoc Phan."
  Thiet ke nay tuan thu quyet dinh nghiep vu: khong chan phan cong khan
  cap (vd GV khac bi om dot xuat) nhung ghi nhan "exception" de user chu
  dong bo sung doi ngu sau do.

BUOC 4 — [Tuy chon] Gui thong bao email cho GV ve lop HP duoc phan cong
```

---

### WF-05.2: Quan ly Sinh Vien trong Lop Hoc Phan

```
Actor: BCN, GV

BUOC 1 — GET /lop-hoc-phan/{ctdt}/{hp}/{hk}/{nhom} -> chi tiet + danh sach SV

BUOC 2 — Them SV: POST .../them-sv (maSV)
  -> Kiem tra: SV ton tai, TrangThaiSV = 'DangHoc'
  -> Kiem tra: SV chua co trong lop nay (PK unique)
  -> Kiem tra: SiSoThucTe < SiSoToiDa
  -> INSERT INTO DanhSachSinhVienLopHocPhan (composite PK)
  -> UPDATE LopHocPhan SET SiSoThucTe = SiSoThucTe + 1

BUOC 3 — Xoa SV: POST .../xoa-sv/{maSV}
  -> Kiem tra: DaCanhBao = 0 (khong xoa neu da co canh bao)
  -> DELETE FROM DanhSachSinhVienLopHocPhan
  -> UPDATE LopHocPhan SET SiSoThucTe = SiSoThucTe - 1
```

---

## MODULE 6: DANH GIA VA CANH BAO SINH VIEN

### WF-06.1: GV nhap Nhan Xet Sinh Vien

```
Actor: GiangVien (chi duoc nhap cho LopHocPhan minh duoc phan cong)

BUOC 1 — GET /danh-gia/lop/{ctdt}/{hp}/{hk}/{nhom}
  -> Kiem tra: currentUser = MaGiangVien cua LopHocPhan nay (hoac CNHP, BCN)
  -> Lay danh sach DanhSachSinhVienLopHocPhan cua lop

BUOC 2 — POST /danh-gia/lop/{ctdt}/{hp}/{hk}/{nhom}/nhan-xet/{maSV}
  Form: NhanXet (text), DaCanhBao (checkbox)

BUOC 3 — DanhSachSVLopHPService.nhapNhanXet():
  -> UPDATE DanhSachSinhVienLopHocPhan SET NhanXet=?, DaCanhBao=? WHERE (composite PK)

BUOC 4 — Neu DaCanhBao = 1 (co canh bao):
  -> Lay CVHT cua SV: NguoiDung.MaSV -> SinhVien.MaLopHC -> LopHanhChinh.MaCoVan -> GiangVien -> NguoiDung.Email
  -> EmailService.guiCanhBao(emailCVHT, hoTenSV, tenHocPhan, nhanXet)
     [MockEmailServiceImpl: chi log ra console, khong gui that]

KET QUA:
  - DanhSachSinhVienLopHocPhan.NhanXet, DaCanhBao duoc cap nhat
  - CVHT duoc thong bao qua email (neu DaCanhBao=1)
```

---

### WF-06.2: CVHT xu ly Canh Bao

```
Actor: CVHT

BUOC 1 — GET /danh-gia/canh-bao
  -> Lay danh sach SV trong lop HC minh quan ly co DaCanhBao = 1 (chua xu ly = KetQuaXuLy IS NULL)
  -> Hien thi badge so luong canh bao chua xu ly tren navbar

BUOC 2 — POST /danh-gia/canh-bao/{maSV}/{ctdt}/{hp}/{hk}/{nhom}/xu-ly
  Form: KetQuaXuLy (text, bat buoc)
  -> UPDATE DanhSachSinhVienLopHocPhan SET KetQuaXuLy = ? WHERE (composite PK)
  [NOTE: DaCanhBao van giu nguyen = 1, chi them KetQuaXuLy]
```

---

## MODULE 7: QUAN LY KIEN TAP

### WF-07.1: Tao Dot Kien Tap va AUTO-ADD Sinh Vien (HYBRID RULE)

```
Actor: BCN/TTDTXS tao, TTDTXS phe duyet

BUOC 1 — BCN/TTDTXS: GET /kien-tap/them
  -> Option: LopHanhChinh (list), DoanhNghiep (list, chi TrangThai=DangHopTac),
             GiangVien (list fetch NguoiDung), HocKyNamHoc (list desc NgayBatDau)
  -> Form: TenDotKT, MaLopHC, MaHocKy, ThoiGian, MaGVPhuTrach (optional),
           MaDoanhNghiep, KinhPhiChung, KinhPhiTungSV
  -> activeMenu = "kien-tap"

BUOC 2 — POST /kien-tap/them (DotKienTapDTO + MultipartFile fileMinhChung)
  -> Validate server:
       - MaLopHC ton tai
       - MaHocKy ton tai
       - MaDoanhNghiep.TrangThai = 'DangHopTac'  (reject neu 'TamNgung')
       - MaGVPhuTrach ton tai neu != null
  -> @Transactional:
       1. INSERT INTO DotKienTap (..., TrangThai='ChuanBi', NguoiTao=currentUser.maNguoiDung)
       2. [Tuy chon] FileStorageUtil.save(fileMinhChung) -> UPDATE FileMinhChung
       3. AUTO-ADD sinh vien (NGHIEP VU HYBRID):
          SELECT MaSV FROM SinhVien WHERE MaLopHC = ? AND TrangThaiSV = 'DangHoc'
          Voi moi maSV:
            INSERT INTO DanhSachSinhVienKienTap (MaDotKT, MaSV, DaThamGia=1)
          (Sinh vien BaoLuu/ThoiHoc/TotNghiep KHONG duoc them tu dong.)
       4. Neu lop KHONG co SV 'DangHoc' nao: flash warningMsg
          "Dot duoc tao nhung lop chua co sinh vien DangHoc. Hay them sinh vien truoc khi nop duyet."

BUOC 3 — BCN: POST /kien-tap/gui-phe-duyet/{id}
  -> Kiem tra: TrangThai = 'ChuanBi'
  -> Kiem tra: co it nhat 1 SV DaThamGia=1 trong DanhSachSinhVienKienTap (chan truong hop danh sach rong)
  -> UPDATE TrangThai = 'ChoDuyet'

BUOC 4 — TTDTXS: POST /kien-tap/phe-duyet/{id}
  -> Kiem tra: TrangThai = 'ChoDuyet'
  -> UPDATE DotKienTap SET
       TrangThai='DaDuyet',
       NguoiDuyet=currentUser.maNguoiDung,
       NgayDuyet=NOW()

BUOC 5a — Hoan thanh: POST /kien-tap/hoan-thanh/{id}  [BCN/TTDTXS]
  -> Kiem tra: TrangThai = 'DaDuyet'
  -> UPDATE TrangThai = 'DaThucHien'

BUOC 5b — Huy dot: POST /kien-tap/huy/{id}  [BCN/TTDTXS]
  -> Kiem tra: TrangThai IN ('ChuanBi','ChoDuyet','DaDuyet','DaThucHien')
  -> UPDATE TrangThai = 'DaHuy'
  -> Luu ly do vao log (neu co field).
```

**Bang bi tac dong:** DotKienTap, DanhSachSinhVienKienTap

---

### WF-07.2: Cap nhat DaThamGia (Danh dau khong tham gia / tham gia)

```
Actor: BCN / TTDTXS / ADMIN

Use case: Sinh vien om om, trung lich, di thuc tap cho khac -> can loai khoi thong ke
"thuc te tham gia" nhung KHONG xoa ban ghi (giu lai de audit).

BUOC 1 — GET /kien-tap/chi-tiet/{maDotKT}
  -> Hien thi bang DanhSachSinhVienKienTap:
     | MaSV | HoTen | TrangThaiSV | DaThamGia (toggle switch) | Thao tac |
  -> Sinh vien DaThamGia=0 hien badge "Khong tham gia" mau warning
  -> Sinh vien DaThamGia=1 hien badge "Tham gia" mau success

BUOC 2 — POST /kien-tap/chi-tiet/{maDotKT}/sv/{maSV}/danh-dau (param daThamGia=0|1)
  -> Kiem tra: DotKienTap.TrangThai != 'DaHuy' (dot da huy thi khoa)
  -> Kiem tra: ban ghi (maDotKT, maSV) ton tai
  -> UPDATE DanhSachSinhVienKienTap SET DaThamGia = ? WHERE (MaDotKT, MaSV)
  -> Flash successMsg: "Da cap nhat trang thai tham gia cho SV <maSV>"

KET QUA:
  - Ban ghi duoc GIU LAI (khong DELETE).
  - Thong ke "So SV thuc te tham gia" = COUNT WHERE DaThamGia=1.
  - Lich su thay doi co the theo doi qua `updated_at`.
```

---

### WF-07.3: Dong bo danh sach SV (re-sync sau khi lop co bien dong)

```
Actor: BCN / TTDTXS / ADMIN

Use case: Sau khi tao dot, co SV moi duoc chuyen vao lop HC, hoac SV BaoLuu quay lai hoc.

BUOC 1 — GET /kien-tap/chi-tiet/{maDotKT}
  -> Hien nut "Dong bo danh sach lop" (tooltip giai thich)

BUOC 2 — POST /kien-tap/chi-tiet/{maDotKT}/dong-bo
  -> Kiem tra: DotKienTap.TrangThai != 'DaHuy'
  -> Tim maLopHC cua dot
  -> SELECT MaSV FROM SinhVien WHERE MaLopHC = ? AND TrangThaiSV='DangHoc'
  -> Voi moi maSV KHONG co trong DanhSachSinhVienKienTap (maDotKT, ?):
       INSERT (MaDotKT, MaSV, DaThamGia=1)
  -> KHONG XOA ban ghi hien co (giu toan ven du lieu audit).
  -> Flash: "Da them N sinh vien moi vao dot kien tap"
```

---

### WF-07.4: GV va DN nhap Nhan Xet Kien Tap

```
Actor: GiangVien (MaGVPhuTrach), DoanhNghiep (tai khoan DN tuong ung MaDoanhNghiep)

Dieu kien: DotKienTap.TrangThai IN ('DaDuyet','DaThucHien')
(Khong cho nhap khi ChuanBi/ChoDuyet vi dot chua duoc duyet chinh thuc.)

GV nhan xet:
-> GET  /kien-tap/chi-tiet/{id} -> form NhanXetGV (hien thi neu currentUser.maGV == MaGVPhuTrach)
-> POST /kien-tap/nhan-xet-gv/{id}
-> Kiem tra: currentUser.maGV == DotKienTap.MaGVPhuTrach
-> UPDATE DotKienTap SET NhanXetGV = ?

DN nhan xet:
-> GET  /kien-tap/chi-tiet/{id} -> form NhanXetDN (hien thi neu currentUser.maNguoiDung == MaDoanhNghiep)
-> POST /kien-tap/nhan-xet-dn/{id}
-> Kiem tra: currentUser.maNguoiDung == DotKienTap.MaDoanhNghiep
-> UPDATE DotKienTap SET NhanXetDN = ?

2 textarea NhanXetGV va NhanXetDN HOAN TOAN DOC LAP — khong ghi de len nhau.
```

---

## MODULE 8: QUAN LY THUC TAP

### WF-08.1: Tao va phe duyet Dot Thuc Tap

```
Actor: PDT / TTDTXS tao, TTDTXS phe duyet

BUOC 1 — GET /thuc-tap/them
  -> Option:
     - CTDT_HocPhan: chi lay cap (CTDT,HP) voi HP.LoaiHocPhan IN ('ThucTap','KienTap')
       AND HP.TrangThai='DaDuyet' AND CTDT.TrangThai='DaDuyet'
     - HocKyNamHoc: list sap xep theo NgayBatDau DESC
  -> activeMenu = "thuc-tap"

BUOC 2 — POST /thuc-tap/them (DotThucTapDTO + [optional] MultipartFile fileMinhChung)
  -> Validate server:
     - (MaCTDT, MaHocPhan) ton tai trong CTDT_HocPhan
     - HocPhan.LoaiHocPhan IN ('ThucTap','KienTap')  (reject neu LyThuyet/ThucHanh/DoAn)
     - MaHocKy ton tai
     - NgayBatDau <= NgayKetThuc (validate bean)
  -> INSERT INTO DotThucTap (..., TrangThai='ChuanBi', NguoiTao=currentUser.maNguoiDung)
  -> [Tuy chon] FileStorageUtil.save(fileMinhChung) -> UPDATE FileMinhChung

BUOC 3 — POST /thuc-tap/gui-phe-duyet/{id}
  -> Kiem tra: TrangThai = 'ChuanBi'
  -> UPDATE TrangThai = 'ChoDuyet'

BUOC 4 — TTDTXS: POST /thuc-tap/phe-duyet/{id}
  -> Kiem tra: TrangThai = 'ChoDuyet'
  -> UPDATE DotThucTap SET
       TrangThai='DaDuyet',
       NguoiDuyet=currentUser.maNguoiDung,
       NgayDuyet=NOW()

BUOC 5a — TTDTXS: POST /thuc-tap/bat-dau/{id}
  -> Kiem tra: TrangThai = 'DaDuyet'
  -> UPDATE TrangThai = 'DangThucHien'
  (Co the ghep voi BUOC 4 neu deploy muon auto-chuyen; hien de rieng de audit.)

BUOC 5b — TTDTXS: POST /thuc-tap/ket-thuc/{id}
  -> Kiem tra: TrangThai = 'DangThucHien'
  -> UPDATE DotThucTap SET TrangThai = 'DaKetThuc'
  -> Cascade: UPDATE DanhSachThucTap SET TrangThai='DaKetThuc'
              WHERE MaDotTT = ? AND TrangThai='DangThucTap'

BUOC 5c — TTDTXS: POST /thuc-tap/huy/{id}
  -> Kiem tra: TrangThai != 'DaKetThuc'
  -> UPDATE TrangThai = 'DaHuy'
```

---

### WF-08.2: Phan cong Thuc Tap tu Excel

```
Actor: PDT

BUOC 1 — POST /thuc-tap/{ma}/import-phan-cong (MultipartFile Excel)
  -> Kiem tra: DotThucTap.TrangThai = 'DangThucHien' hoac 'DaDuyet'

BUOC 2 — ExcelImportUtil.parsePhanCongThucTap(file):
  -> Doc tung dong: MaSV, LoaiThucTap (Truong/DoanhNghiep), MaDoanhNghiep (neu DoanhNghiep)

BUOC 3 — Xu ly tung ban ghi:
  CHECK UNIQUE: existsByMaDotTT_MaDotTTAndMaSV_MaSV(maDotTT, maSV)
    Co trung: SKIP, them vao danh sach loi "SV da duoc phan cong"
    Khong trung:
      Validate: SinhVien ton tai, TrangThaiSV='DangHoc'
      Neu LoaiThucTap='DoanhNghiep': MaDoanhNghiep khong trong va DoanhNghiep DangHopTac
      -> INSERT INTO DanhSachThucTap (MaDotTT, MaSV, LoaiThucTap, MaDoanhNghiep, TrangThai='DaPhanCong')

BUOC 4 — redirect /thuc-tap/{ma}
  -> Hien thi: so ban ghi thanh cong / so ban ghi loi + chi tiet loi
```

---

### WF-08.3: Nhap Ket Qua Thuc Tap

```
Actor: GiangVien (giang day lop), Doanh Nghiep, CVHT

BUOC 1 — Xac dinh quyen nhap:
  GV     -> MaVaiTro = 'GV'     (khai bao truoc trong VaiTroThucTap)
  DN     -> MaVaiTro = 'DN'     (lay tu DoanhNghiep cua SV thuc tap)
  CVHT   -> MaVaiTro = 'CVHT'

BUOC 2 — POST /thuc-tap/ket-qua/{maThucTap} (Diem, NhanXet, MaVaiTro)
  -> Kiem tra: DanhSachThucTap.MaThucTap ton tai
  -> Kiem tra: currentUser co quyen nhap (GV phu trach, DN cua SV, CVHT cua lop)
  -> Kiem tra: Chua co KetQuaThucTap voi (MaThucTap, MaVaiTro) tuong tu
     Neu co roi: UPDATE (cho phep sua)
     Neu chua: INSERT INTO KetQuaThucTap (MaThucTap, MaVaiTro, MaNguoiDanhGia, Diem, NhanXet)

-- BUOC 3 (da loai bo khoi MVP): SV KHONG tu danh gia chinh thuc vao
--   KetQuaThucTap. Ly do: fk_kqtt_nguoidanhgia REFERENCES GiangVien(MaGV),
--   nen chi GV/DN/CVHT (cac nguoi luu trong bang GiangVien) moi nhap duoc.
--   Neu co yeu cau ghi nhan cam nhan dinh tinh cua SV: dung truong text tren
--   DanhSachThucTap (vd `NhanXetSV`), khong tao ban ghi KetQuaThucTap.

BUOC 3 — [Khi ket thuc] POST /thuc-tap/{ma}/ket-thuc [PDT/TTDTXS]
  -> UPDATE DotThucTap SET TrangThai='DaKetThuc'
  -> UPDATE DanhSachThucTap SET TrangThai='DaKetThuc' WHERE MaDotTT = ?
```

---

## MODULE 9: BAO CAO & EXPORT

### WF-09.1: Xuat Bao Cao Excel

```
Actor: PDT, TTDTXS, BCN (tuy loai bao cao)

Bao cao Nguoi Dung:
  GET /bao-cao/nguoi-dung/export?filter=...
  -> BaoCaoService.exportNguoiDung(filter)
  -> Apache POI: tao Workbook, Sheet "NguoiDung"
  -> Header: MaNguoiDung, HoTen, TenDangNhap, Email, LoaiNguoiDung, VaiTro, TrangThai
  -> Du lieu: join NguoiDung + NhomNguoiDung
  -> Response: Content-Type application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
  -> Content-Disposition: attachment; filename="NguoiDung_yyyy-MM-dd.xlsx"

Bao cao Lop Hoc Phan:
  GET /bao-cao/lop-hoc-phan/export?maHocKy=...
  -> Header: MaHocPhan, TenHocPhan, MaLopHocPhan, GiangVien, SiSo, TrangThai

Bao cao Thuc Tap:
  GET /bao-cao/thuc-tap/export?maDotTT=...
  -> Header: MaSV, HoTen, LoaiThucTap, DoanhNghiep, DiemGV, DiemDN, NhanXet
```

---

## SO DO TONG HOP QUYEN THEO MODULE

| Module              | PDT | TTDTXS | CVHT | CNHP | Admin | GV  | SV  | DN  |
|---------------------|-----|--------|------|------|-------|-----|-----|-----|
| Quan ly Nguoi Dung  | RW  | R      |      |      | RW    |     |     |     |
| Quan ly Doanh Nghiep| RW  | RW     |      |      | RW    |     |     |     |
| Hoc Ky Nam Hoc      | RW  | RW     |      |      | RW    |     |     |     |
| Lop Hanh Chinh      | RW  | RW     |      |      | RW    |     |     |     |
| Hoc Phan (tao/sua)  |     |        |      | RW   |       |     |     |     |
| Hoc Phan (duyet)    |     | W      |      |      |       |     |     |     |
| CTDT (tao)          | R   | RW     |      | RW   |       |     |     |     |
| CTDT (duyet)        |     | W      |      |      |       |     |     |     |
| Lop Hoc Phan        | RW  | RW     |      | RW   |       | R   | R   |     |
| Phan cong GV        | RW  | RW     |      | RW   |       |     |     |     |
| Danh gia SV         |     |        | R    |      |       | RW  |     |     |
| Xu ly canh bao      |     |        | RW   |      |       |     |     |     |
| Kien Tap (tao/duyet)|     | RW     |      | RW   |       |     |     |     |
| Kien Tap (nhan xet) |     |        |      |      |       | W   |     | W   |
| Thuc Tap (tao)      | RW  | RW     |      |      |       |     |     |     |
| Thuc Tap (import)   | W   |        |      |      |       |     |     |     |
| Ket qua Thuc Tap    |     |        | W    |      |       | W   | W   | W   |
| Bao cao             | R   | R      |      | R    | R     |     |     |     |

*R = Read, W = Write/Create, RW = Read + Write*

---

## XU LY LOI CHUNG

| Tinh huong                     | Xu ly                                                          |
|-------------------------------|----------------------------------------------------------------|
| Khong co quyen truy cap URL   | Spring Security -> 403 -> redirect /error/403                  |
| Resource khong tim thay       | ResourceNotFoundException -> 404 -> redirect /error/404         |
| Loi nghiep vu (validate fail) | BusinessException -> redirect back + flash errorMsg            |
| Loi DB (constraint violation) | GlobalExceptionHandler bat -> log + flash "Loi he thong"       |
| File upload qua lon           | MaxUploadSizeExceededException -> flash "File qua lon (toi da 20MB)" |
| Session het han               | redirect /login?timeout -> flash "Phien lam viec da het han"   |
