@startuml
!theme plain
left to right direction
skinparam linetype ortho
hide circle

' ===== NHÓM NGƯỜI DÙNG =====
entity NguoiDung {
  MaNguoiDung: VARCHAR(20) <<PK>>
  TenDangNhap: VARCHAR(50) <<U>>
  MatKhauHash: VARCHAR(255)
  Email: VARCHAR(100) <<U>>
  HoTen: VARCHAR(100)
  SoDienThoai: VARCHAR(15)
  TrangThaiTK: BIT
  LoaiNguoiDung: ENUM
  created_at: DATETIME
  updated_at: DATETIME
}

entity SinhVien {
  MaSV: VARCHAR(20) <<PK>>
  MaNguoiDung: VARCHAR(20) <<FK>> <<U>>
  MaLopHC: VARCHAR(20) <<FK>>
  TrangThaiSV: ENUM
}

entity GiangVien {
  MaGV: VARCHAR(20) <<PK>>
  MaNguoiDung: VARCHAR(20) <<FK>> <<U>>
  HocHam: VARCHAR(50)
  HocVi: VARCHAR(50)
  ChuyenNganh: VARCHAR(200)
  LoaiGiangVien: ENUM
}

entity NhomNguoiDung {
  MaNguoiDung: VARCHAR(20) <<FK>>
  VaiTro: ENUM <<PK>>
  created_at: DATETIME
}

' ===== NHÓM CHƯƠNG TRÌNH ĐÀO TẠO =====
entity ChuongTrinhDaoTao {
  MaCTDT: VARCHAR(20) <<PK>>
  TenCTDT: VARCHAR(200)
  Khoa: VARCHAR(20)
  FileWord: VARCHAR(255)
  TrangThai: ENUM
  NguoiTao: VARCHAR(20) <<FK>>
  created_at: DATETIME
  NguoiDuyet: VARCHAR(20) <<FK>>
  NgayDuyet: DATETIME
  updated_at: DATETIME
}

entity BCN_ThanhVien {
  MaCTDT: VARCHAR(20) <<FK>>
  MaGV: VARCHAR(20) <<FK>>
  ChucDanh: ENUM <<PK>>
  NgayBoNhiem: DATE
  GhiChu: VARCHAR(255)
  created_at: DATETIME
}

' ===== NHÓM HỌC PHẦN =====
entity HocPhan {
  MaHocPhan: VARCHAR(20) <<PK>>
  TenHocPhan: VARCHAR(200)
  SoTinChi: INT
  LoaiHocPhan: ENUM
  ChuNhiemHP: VARCHAR(20) <<FK>>
  FileDeCuong: VARCHAR(255)
  TrangThai: ENUM
  created_at: DATETIME
  updated_at: DATETIME
}

entity DoiNguGiangVienHP {
  MaHocPhan: VARCHAR(20) <<FK>>
  MaGiangVien: VARCHAR(20) <<FK>>
  TrangThai: BIT
  created_at: DATETIME
  <<PK>> (MaHocPhan, MaGiangVien)
}

entity CTDT_HocPhan {
  MaCTDT: VARCHAR(20) <<FK>>
  MaHocPhan: VARCHAR(20) <<FK>>
  HocKyThu: INT
  SoLopDuKien: INT
  BatBuoc: BIT
  GhiChu: VARCHAR(255)
  FileDeCuong: VARCHAR(255)
  created_at: DATETIME
  updated_at: DATETIME
  <<PK>> (MaCTDT, MaHocPhan)
}

' ===== NHÓM LỚP HỌC PHẦN =====
entity LopHocPhan {
  MaCTDT: VARCHAR(20) <<FK>>
  MaHocPhan: VARCHAR(20) <<FK>>
  MaHocKy: VARCHAR(20) <<FK>>
  MaLopHocPhan: INT <<PK>>
  MaGiangVien: VARCHAR(20) <<FK>>
  SiSoToiDa: INT
  SiSoThucTe: INT
  FileDeCuongChiTiet: VARCHAR(255)
  TrangThai: ENUM
  created_at: DATETIME
  updated_at: DATETIME
  <<PK>> (MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)
}

entity DanhSachSinhVienLopHocPhan {
  MaSV: VARCHAR(20) <<FK>>
  MaCTDT: VARCHAR(20) <<FK>>
  MaHocPhan: VARCHAR(20) <<FK>>
  MaHocKy: VARCHAR(20) <<FK>>
  MaLopHocPhan: INT <<FK>>
  NhanXet: TEXT
  DaCanhBao: BIT
  KetQuaXuLy: TEXT
  created_at: DATETIME
  updated_at: DATETIME
  <<PK>> (MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)
}

' ===== NHÓM HỌC KỲ =====
entity HocKyNamHoc {
  MaHocKy: VARCHAR(20) <<PK>>
  TenHocKy: VARCHAR(50)
  NgayBatDau: DATE
  NgayKetThuc: DATE
  TrangThai: ENUM
  created_at: DATETIME
  updated_at: DATETIME
}

' ===== NHÓM DOANH NGHIỆP =====
entity DoanhNghiep {
  MaDoanhNghiep: VARCHAR(20) <<PK>>
  TenDoanhNghiep: VARCHAR(200)
  LinhVuc: VARCHAR(200)
  NguoiDaiDien: VARCHAR(100)
  Email: VARCHAR(100)
  SoDienThoai: VARCHAR(15)
  DiaChiDN: VARCHAR(255)
  TrangThai: ENUM
  created_at: DATETIME
  updated_at: DATETIME
}

' ===== NHÓM LỚP HÀNH CHÍNH =====
entity LopHanhChinh {
  MaLopHC: VARCHAR(20) <<PK>>
  TenLop: VARCHAR(100)
  MaCTDT: VARCHAR(20) <<FK>>
  KhoaHoc: VARCHAR(20)
  MaCoVan: VARCHAR(20) <<FK>>
  created_at: DATETIME
  updated_at: DATETIME
}

' ===== NHÓM KIẾN TẬP =====
entity DotKienTap {
  MaDotKT: INT <<PK>>
  TenDotKT: VARCHAR(200)
  MaLopHC: VARCHAR(20) <<FK>>
  MaHocKy: VARCHAR(20) <<FK>>
  ThoiGian: DATE
  MaGVPhuTrach: VARCHAR(20) <<FK>>
  MaDoanhNghiep: VARCHAR(20) <<FK>>
  NhanXetGV: TEXT
  NhanXetDN: TEXT
  FileMinhChung: VARCHAR(255)
  KinhPhiChung: DECIMAL(15,2)
  KinhPhiTungSV: DECIMAL(15,2)
  TrangThai: ENUM
  NguoiTao: VARCHAR(20) <<FK>>
  NguoiDuyet: VARCHAR(20) <<FK>>
  NgayDuyet: DATETIME
  created_at: DATETIME
  updated_at: DATETIME
}

entity DanhSachSinhVienKienTap {
  MaDotKT: INT <<FK>>
  MaSV: VARCHAR(20) <<FK>>
  DaThamGia: BIT
  created_at: DATETIME
  updated_at: DATETIME
  <<PK>> (MaDotKT, MaSV)
}

' ===== NHÓM THỰC TẬP =====
entity DotThucTap {
  MaDotTT: INT <<PK>>
  TenDotTT: VARCHAR(200)
  MaCTDT: VARCHAR(20) <<FK>>
  MaHocPhan: VARCHAR(20) <<FK>>
  MaHocKy: VARCHAR(20) <<FK>>
  NgayBatDau: DATE
  NgayKetThuc: DATE
  FileMinhChung: VARCHAR(255)
  TrangThai: ENUM
  NguoiTao: VARCHAR(20) <<FK>>
  NguoiDuyet: VARCHAR(20) <<FK>>
  NgayDuyet: DATETIME
  created_at: DATETIME
  updated_at: DATETIME
}

entity DanhSachThucTap {
  MaThucTap: INT <<PK>>
  MaDotTT: INT <<FK>>
  MaSV: VARCHAR(20) <<FK>>
  LoaiThucTap: ENUM
  MaDoanhNghiep: VARCHAR(20) <<FK>> <<NULL>>
  TrangThai: ENUM
  created_at: DATETIME
  updated_at: DATETIME
}

entity VaiTroThucTap {
  MaVaiTro: VARCHAR(10) <<PK>>
  TenVaiTro: VARCHAR(100)
  MoTa: VARCHAR(255)
}

entity KetQuaThucTap {
  MaKetQua: INT <<PK>>
  MaThucTap: INT <<FK>>
  MaVaiTro: VARCHAR(10) <<FK>>
  MaNguoiDanhGia: VARCHAR(20) <<FK>>
  Diem: DECIMAL(4,2)
  NhanXet: TEXT
  created_at: DATETIME
  updated_at: DATETIME
}

' ===== QUAN HỆ =====
NguoiDung ||--|| SinhVien : "1-1"
NguoiDung ||--|| GiangVien : "1-1"
NguoiDung ||--o{ NhomNguoiDung : "có"
NguoiDung ||--o{ ChuongTrinhDaoTao : "tạo (NguoiTao)"
NguoiDung ||--o{ ChuongTrinhDaoTao : "duyệt (NguoiDuyet)"
NguoiDung ||--o{ DotKienTap : "tạo (NguoiTao)"
NguoiDung ||--o{ DotKienTap : "duyệt (NguoiDuyet)"
NguoiDung ||--o{ DotThucTap : "tạo (NguoiTao)"
NguoiDung ||--o{ DotThucTap : "duyệt (NguoiDuyet)"

SinhVien }o--|| LopHanhChinh : "thuộc"
SinhVien ||--o{ DanhSachSinhVienLopHocPhan : "tham gia"
SinhVien ||--o{ DanhSachSinhVienKienTap : "tham gia"
SinhVien ||--o{ DanhSachThucTap : "được phân công"

GiangVien ||--o{ BCN_ThanhVien : "tham gia"
GiangVien ||--o{ DoiNguGiangVienHP : "tham gia"
GiangVien ||--o{ LopHocPhan : "giảng dạy"
GiangVien ||--o{ LopHanhChinh : "cố vấn (MaCoVan)"
GiangVien ||--o{ DotKienTap : "phụ trách"
GiangVien ||--o{ KetQuaThucTap : "đánh giá"

ChuongTrinhDaoTao ||--o{ BCN_ThanhVien : "có ban chủ nhiệm"
ChuongTrinhDaoTao ||--o{ LopHanhChinh : "áp dụng"
ChuongTrinhDaoTao ||--o{ CTDT_HocPhan : "chi tiết"

HocPhan ||--o{ DoiNguGiangVienHP : "có"
HocPhan ||--o{ CTDT_HocPhan : "thuộc"
HocPhan }o--|| GiangVien : "chủ nhiệm (ChuNhiemHP)"

CTDT_HocPhan ||--o{ LopHocPhan : "mở lớp"
CTDT_HocPhan ||--o{ DotThucTap : "thuộc học phần"

HocKyNamHoc ||--o{ LopHocPhan : "chứa"
HocKyNamHoc ||--o{ DotKienTap : "tổ chức"
HocKyNamHoc ||--o{ DotThucTap : "tổ chức"

DoanhNghiep ||--o{ DotKienTap : "tiếp đón"
DoanhNghiep ||--o{ DanhSachThucTap : "tiếp nhận"

LopHocPhan ||--o{ DanhSachSinhVienLopHocPhan : "có"

DotKienTap ||--o{ DanhSachSinhVienKienTap : "có"

DotThucTap ||--o{ DanhSachThucTap : "có"
DanhSachThucTap ||--o{ KetQuaThucTap : "có kết quả"
VaiTroThucTap ||--o{ KetQuaThucTap : "vai trò"

LopHanhChinh ||--o{ DotKienTap : "tham gia"

@enduml
