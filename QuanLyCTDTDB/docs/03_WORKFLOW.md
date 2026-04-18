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

DotKienTap:   ChuanBi -> ChoDuyet -> DaDuyet -> DaThucHien -> DaHuy
DotThucTap:   ChuanBi -> ChoDuyet -> DaDuyet -> DangThucHien -> DaKetThuc

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
     7. CustomAuthenticationProvider: BCrypt.matches(password, hash)
        Neu sai: redirect /login?error

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
  -> INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien=ChuNhiemHP, TrangThai=1)
     [CNHP tu dong co trong doi ngu]

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
Actor: CNHP

BUOC 1 — GET /hoc-phan/{ma} -> hien thi chi tiet + danh sach doi ngu GV hien tai

BUOC 2 — Them GV: POST /hoc-phan/{ma}/them-gv (maGV)
  -> Kiem tra GV ton tai trong bang GiangVien
  -> Kiem tra chua ton tai trong DoiNguGiangVienHP (MaHocPhan, MaGiangVien)
  -> INSERT INTO DoiNguGiangVienHP (MaHocPhan, MaGiangVien, TrangThai=1)

BUOC 3 — Xoa GV: POST /hoc-phan/{ma}/xoa-gv/{maGV}
  -> Kiem tra: GV nay KHONG la ChuNhiemHP (khong duoc phep xoa CNHP)
  -> Kiem tra: GV nay chua duoc gan vao LopHocPhan nao dang mo
     Neu da duoc gan: UPDATE DoiNguGiangVienHP SET TrangThai=0 (an di, khong xoa)
     Neu chua duoc gan: DELETE FROM DoiNguGiangVienHP
```

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

BUOC 3 — BCN: GET /ctdt/{ma} -> hien thi chi tiet
  -> Hien thi 2 section: Thanh vien BCN + Danh sach Hoc Phan

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

BUOC 3 — autoCreateLopHocPhan(maCTDT, maHocKyDangDienRa):
  Lay danh sach CTDT_HocPhan WHERE MaCTDT = ?
  Lay MaHocKy hien tai (TrangThaiHocKy='DangDienRa')
  Lay MaLopHC cua cac lop hanh chinh dang theo hoc CTDT nay

  Voi moi CTDT_HocPhan:
    Lap tu MaLopHocPhan = 1 den SoLopDuKien:
      -> INSERT INTO LopHocPhan (
             MaCTDT, MaHocPhan, MaHocKy=maHocKyDangDienRa,
             MaLopHocPhan (thu tu),
             MaGiangVien = NULL,  <- QUAN TRONG: chua phan cong
             SiSoToiDa = 45,      <- mac dinh, co the chinh
             TrangThai = 'DangMo'
         )
      -> Ket qua: tao SoLopDuKien lop HP cho moi HP trong CTDT

BUOC 4 — Ket qua:
  - CTDT.TrangThai = 'DaDuyet'
  - LopHocPhan duoc tao tu dong theo so luong lop du kien
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

BUOC 3 — Kiem tra doi ngu:
  Neu GV co trong DoiNguGiangVienHP WHERE MaHocPhan = ? AND MaGiangVien = ? AND TrangThai = 1:
    -> Gan binh thuong
  Neu GV KHONG co trong doi ngu:
    -> Hien canh bao: "GV X khong co trong doi ngu HP Y. Xac nhan?"
    -> Van cho phep phan cong (sau khi nguoi dung xac nhan)

BUOC 4 — UPDATE LopHocPhan SET MaGiangVien = ? WHERE (composite PK)

BUOC 5 — [Tuy chon] Gui thong bao email cho GV ve lop HP duoc phan cong
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

### WF-07.1: Tao va phe duyet Dot Kien Tap

```
Actor: BCN tao, TTDTXS phe duyet, GV + DN nhan xet

BUOC 1 — BCN/TTDTXS: GET /kien-tap/them
  -> Option: LopHanhChinh (list), DoanhNghiep (list, TrangThai=DangHopTac), GiangVien (list)
  -> Form: TenDotKT, MaLopHC, MaHocKy, ThoiGian, MaGVPhuTrach, MaDoanhNghiep, KinhPhiChung, KinhPhiTungSV

BUOC 2 — POST /kien-tap/them (DotKienTapDTO + file)
  -> Validate: LopHC ton tai, DoanhNghiep DangHopTac
  -> INSERT INTO DotKienTap (..., TrangThai='ChuanBi', NguoiTao=currentUser)
  -> FileStorageUtil.save(FileMinhChung)
  -> Lay danh sach SinhVien tu LopHanhChinh.MaLopHC:
       SELECT MaSV FROM SinhVien WHERE MaLopHC = ? AND TrangThaiSV = 'DangHoc'
  -> INSERT INTO DanhSachSinhVienKienTap (MaDotKT, MaSV, DaThamGia=1) cho tung SV

BUOC 3 — BCN: POST /kien-tap/{ma}/nop-duyet
  -> TrangThai: ChuanBi -> ChoDuyet

BUOC 4 — TTDTXS: POST /kien-tap/{ma}/phe-duyet
  -> TrangThai: ChoDuyet -> DaDuyet

BUOC 5 — Sau khi thuc hien: POST /kien-tap/{ma}/hoan-thanh [BCN/TTDTXS]
  -> TrangThai: DaDuyet -> DaThucHien
```

---

### WF-07.2: GV va DN nhap Nhan Xet Kien Tap

```
Actor: GiangVien (MaGVPhuTrach), DoanhNghiep (MaDoanhNghiep)

GV nhan xet:
-> POST /kien-tap/{ma}/nhan-xet-gv
-> Kiem tra: currentUser.maGV == DotKienTap.MaGVPhuTrach
-> UPDATE DotKienTap SET NhanXetGV = ? WHERE MaDotKT = ?

DN nhan xet:
-> POST /kien-tap/{ma}/nhan-xet-dn
-> Kiem tra: currentUser.maDoanhNghiep == DotKienTap.MaDoanhNghiep
-> UPDATE DotKienTap SET NhanXetDN = ? WHERE MaDotKT = ?

Xem danh sach SV:
-> GET /kien-tap/{ma} -> SELECT * FROM DanhSachSinhVienKienTap WHERE MaDotKT = ?
   JOIN SinhVien ON MaSV -> JOIN NguoiDung ON MaNguoiDung
```

---

## MODULE 8: QUAN LY THUC TAP

### WF-08.1: Tao Dot Thuc Tap

```
Actor: PDT, TTDTXS

BUOC 1 — GET /thuc-tap/them
  -> Option: CTDT_HocPhan (chi HP loai ThucTap hoac KienTap, DaDuyet)
  -> Option: HocKyNamHoc

BUOC 2 — POST /thuc-tap/them (DotThucTapDTO)
  -> Validate: (MaCTDT, MaHocPhan) ton tai trong CTDT_HocPhan, HocPhan.LoaiHocPhan IN ('ThucTap','KienTap')
  -> INSERT INTO DotThucTap (..., TrangThai='ChuanBi', NguoiTao=currentUser)

BUOC 3 — POST /thuc-tap/{ma}/nop-duyet
  -> TrangThai: ChuanBi -> ChoDuyet

BUOC 4 — TTDTXS: POST /thuc-tap/{ma}/phe-duyet
  -> TrangThai: ChoDuyet -> DaDuyet -> DangThucHien
  -> UPDATE DotThucTap SET TrangThai='DangThucHien', NguoiDuyet=currentUser, NgayDuyet=NOW()
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

BUOC 3 — [Tuy chon] SV nhap cam nhan (NhanXet rieng cho SV)
  -> POST /thuc-tap/cam-nhan/{maThucTap} (NhanXetSV)
  -> Kiem tra: currentUser.maSV = DanhSachThucTap.MaSV
  -> MaVaiTro = 'SV' trong VaiTroThucTap
  -> INSERT/UPDATE KetQuaThucTap

BUOC 4 — [Khi ket thuc] POST /thuc-tap/{ma}/ket-thuc [PDT/TTDTXS]
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
