II. THIẾT KẾ CƠ SỞ DỮ LIỆU
2.1. Mô hình thực thể - kết hợp (ERD)
(Sử dụng sơ đồ PlantUML như đã thống nhất)

2.2. Thiết kế chi tiết các bảng
2.2.1. Nhóm người dùng và phân quyền
Bảng NguoiDung – Tài khoản đăng nhập chung

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaNguoiDung	VARCHAR(20)	PRIMARY KEY	Mã định danh duy nhất
TenDangNhap	VARCHAR(50)	UNIQUE, NOT NULL	Tên đăng nhập
MatKhauHash	VARCHAR(255)	NOT NULL	Mật khẩu đã băm
Email	VARCHAR(100)	UNIQUE, NOT NULL	Email
HoTen	VARCHAR(100)	NOT NULL	Họ tên
SoDienThoai	VARCHAR(15)		SĐT
TrangThaiTK	BIT	DEFAULT 1	1: Hoạt động, 0: Khóa
LoaiNguoiDung	ENUM('Admin','GiangVien','SinhVien','DoanhNghiep')	NOT NULL	Loại người dùng
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
Bảng SinhVien – Kế thừa từ NguoiDung (1-1)

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaSV	VARCHAR(20)	PRIMARY KEY	Mã sinh viên
MaNguoiDung	VARCHAR(20)	FOREIGN KEY, UNIQUE	Liên kết NguoiDung
MaLopHC	VARCHAR(20)	FOREIGN KEY	Lớp hành chính
TrangThaiSV	ENUM('DangHoc','BaoLuu','ThoiHoc','TotNghiep')	DEFAULT 'DangHoc'	Trạng thái học tập
Bảng GiangVien – Kế thừa từ NguoiDung (1-1)

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaGV	VARCHAR(20)	PRIMARY KEY	Mã giảng viên
MaNguoiDung	VARCHAR(20)	FOREIGN KEY, UNIQUE	Liên kết NguoiDung
HocHam	VARCHAR(50)		Học hàm
HocVi	VARCHAR(50)		Học vị
ChuyenNganh	VARCHAR(200)		Chuyên ngành
LoaiGiangVien	ENUM('GiangVienTruong','DoanhNghiep')	DEFAULT 'GiangVienTruong'	Phân loại
Bảng NhomNguoiDung – Phân quyền cho các vai trò quản trị (PDT, TTDTXS, CVHT, CNHP)

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaNguoiDung	VARCHAR(20)	FOREIGN KEY	
VaiTro	ENUM('PDT','TTDTXS','CVHT','CNHP')	NOT NULL	Vai trò
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
PRIMARY KEY	(MaNguoiDung, VaiTro)		
2.2.2. Nhóm chương trình đào tạo
Bảng ChuongTrinhDaoTao

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaCTDT	VARCHAR(20)	PRIMARY KEY	Mã CTĐT
TenCTDT	VARCHAR(200)	NOT NULL	Tên chương trình
Khoa	VARCHAR(20)		Khóa áp dụng
FileWord	VARCHAR(255)		File mô tả
TrangThai	ENUM('BanNhap','ChoDuyet','DaDuyet','DaHuy')	DEFAULT 'BanNhap'	Trạng thái
NguoiTao	VARCHAR(20)	FOREIGN KEY	Người tạo
NguoiDuyet	VARCHAR(20)	FOREIGN KEY	Người duyệt
NgayDuyet	DATETIME		
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
Bảng BCN_ThanhVien – Ban chủ nhiệm CTĐT

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaCTDT	VARCHAR(20)	FOREIGN KEY	
MaGV	VARCHAR(20)	FOREIGN KEY	
ChucDanh	ENUM('ChuNhiem','ThuKy','UyVien')	NOT NULL	
NgayBoNhiem	DATE		
GhiChu	VARCHAR(255)		
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
PRIMARY KEY	(MaCTDT, MaGV, ChucDanh)		
2.2.3. Nhóm học phần
Bảng HocPhan

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaHocPhan	VARCHAR(20)	PRIMARY KEY	Mã học phần
TenHocPhan	VARCHAR(200)	NOT NULL	Tên học phần
SoTinChi	INT	NOT NULL	Số tín chỉ
LoaiHocPhan	ENUM('LyThuyet','ThucHanh','DoAn','ThucTap','KienTap')		
ChuNhiemHP	VARCHAR(20)	FOREIGN KEY (GiangVien)	Chủ nhiệm HP
FileDeCuong	VARCHAR(255)		File đề cương gốc
TrangThai	ENUM('BanNhap','ChoDuyet','DaDuyet')	DEFAULT 'BanNhap'	
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
Bảng DoiNguGiangVienHP

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaHocPhan	VARCHAR(20)	FOREIGN KEY	
MaGiangVien	VARCHAR(20)	FOREIGN KEY (GiangVien)	
TrangThai	BIT	DEFAULT 1	1: Còn trong đội ngũ
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
PRIMARY KEY	(MaHocPhan, MaGiangVien)		
Bảng CTDT_HocPhan – Chi tiết học phần trong CTĐT

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaCTDT	VARCHAR(20)	FOREIGN KEY	
MaHocPhan	VARCHAR(20)	FOREIGN KEY	
HocKyThu	INT	NOT NULL	Học kỳ thứ mấy
SoLopDuKien	INT	DEFAULT 1	Số lớp dự kiến
BatBuoc	BIT	DEFAULT 1	1: Bắt buộc
GhiChu	VARCHAR(255)		
FileDeCuong	VARCHAR(255)		Đề cương riêng cho CTĐT
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
PRIMARY KEY	(MaCTDT, MaHocPhan)		
2.2.4. Nhóm lớp học phần
Bảng LopHocPhan

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaCTDT	VARCHAR(20)	FOREIGN KEY	
MaHocPhan	VARCHAR(20)	FOREIGN KEY	
MaHocKy	VARCHAR(20)	FOREIGN KEY	
MaLopHocPhan	INT	NOT NULL	Số thứ tự lớp (1,2,3...)
MaGiangVien	VARCHAR(20)	FOREIGN KEY (GiangVien)	
SiSoToiDa	INT	NOT NULL	
SiSoThucTe	INT	DEFAULT 0	
FileDeCuongChiTiet	VARCHAR(255)		Đề cương chi tiết của lớp
TrangThai	ENUM('DangMo','DaDong','DaHuy')	DEFAULT 'DangMo'	
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
PRIMARY KEY	(MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)		
Bảng DanhSachSinhVienLopHocPhan – Danh sách sinh viên tham gia lớp HP

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaSV	VARCHAR(20)	FOREIGN KEY	
MaCTDT	VARCHAR(20)	FOREIGN KEY	
MaHocPhan	VARCHAR(20)	FOREIGN KEY	
MaHocKy	VARCHAR(20)	FOREIGN KEY	
MaLopHocPhan	INT	FOREIGN KEY	
NhanXet	TEXT		Nhận xét của GV (nếu cần cảnh báo)
DaCanhBao	BIT	DEFAULT 0	0: Chưa, 1: Đã cảnh báo
KetQuaXuLy	TEXT		Kết quả xử lý của CVHT
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
PRIMARY KEY	(MaSV, MaCTDT, MaHocPhan, MaHocKy, MaLopHocPhan)		
2.2.5. Nhóm học kỳ
Bảng HocKyNamHoc

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaHocKy	VARCHAR(20)	PRIMARY KEY	
TenHocKy	VARCHAR(50)	NOT NULL	
NgayBatDau	DATE	NOT NULL	
NgayKetThuc	DATE	NOT NULL	
TrangThai	ENUM('SapDienRa','DangDienRa','DaKetThuc')	DEFAULT 'SapDienRa'	
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
2.2.6. Nhóm doanh nghiệp và lớp hành chính
Bảng DoanhNghiep

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaDoanhNghiep	VARCHAR(20)	PRIMARY KEY	
TenDoanhNghiep	VARCHAR(200)	NOT NULL	
LinhVuc	VARCHAR(200)		
NguoiDaiDien	VARCHAR(100)		
Email	VARCHAR(100)		
SoDienThoai	VARCHAR(15)		
DiaChiDN	VARCHAR(255)		
TrangThai	ENUM('DangHopTac','TamNgung')	DEFAULT 'DangHopTac'	
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
Bảng LopHanhChinh

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaLopHC	VARCHAR(20)	PRIMARY KEY	
TenLop	VARCHAR(100)	NOT NULL	
MaCTDT	VARCHAR(20)	FOREIGN KEY	
KhoaHoc	VARCHAR(20)		
MaCoVan	VARCHAR(20)	FOREIGN KEY (GiangVien)	
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
2.2.7. Nhóm kiến tập
Bảng DotKienTap

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaDotKT	INT	AUTO_INCREMENT PRIMARY KEY	
TenDotKT	VARCHAR(200)	NOT NULL	
MaLopHC	VARCHAR(20)	FOREIGN KEY	
MaHocKy	VARCHAR(20)	FOREIGN KEY	
ThoiGian	DATE		
MaGVPhuTrach	VARCHAR(20)	FOREIGN KEY (GiangVien)	
MaDoanhNghiep	VARCHAR(20)	FOREIGN KEY	
NhanXetGV	TEXT		
NhanXetDN	TEXT		
FileMinhChung	VARCHAR(255)		
KinhPhiChung	DECIMAL(15,2)		
KinhPhiTungSV	DECIMAL(15,2)		
TrangThai	ENUM('ChuanBi','ChoDuyet','DaDuyet','DaThucHien','DaHuy')	DEFAULT 'ChuanBi'	
NguoiTao	VARCHAR(20)	FOREIGN KEY (NguoiDung)	
NguoiDuyet	VARCHAR(20)	FOREIGN KEY (NguoiDung)	
NgayDuyet	DATETIME		
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
Bảng DanhSachSinhVienKienTap

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaDotKT	INT	FOREIGN KEY	
MaSV	VARCHAR(20)	FOREIGN KEY	
DaThamGia	BIT	DEFAULT 1	
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
PRIMARY KEY	(MaDotKT, MaSV)		
2.2.8. Nhóm thực tập
Bảng DotThucTap

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaDotTT	INT	AUTO_INCREMENT PRIMARY KEY	
TenDotTT	VARCHAR(200)	NOT NULL	
MaCTDT	VARCHAR(20)	FOREIGN KEY	
MaHocPhan	VARCHAR(20)	FOREIGN KEY	
MaHocKy	VARCHAR(20)	FOREIGN KEY	
NgayBatDau	DATE		
NgayKetThuc	DATE		
FileMinhChung	VARCHAR(255)		
TrangThai	ENUM('ChuanBi','ChoDuyet','DaDuyet','DangThucHien','DaKetThuc')	DEFAULT 'ChuanBi'	
NguoiTao	VARCHAR(20)	FOREIGN KEY (NguoiDung)	
NguoiDuyet	VARCHAR(20)	FOREIGN KEY (NguoiDung)	
NgayDuyet	DATETIME		
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
Bảng DanhSachThucTap

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaThucTap	INT	AUTO_INCREMENT PRIMARY KEY	
MaDotTT	INT	FOREIGN KEY	
MaSV	VARCHAR(20)	FOREIGN KEY	
LoaiThucTap	ENUM('Truong','DoanhNghiep')	NOT NULL	
MaDoanhNghiep	VARCHAR(20)	FOREIGN KEY (NULL)	
TrangThai	ENUM('DaPhanCong','DangThucTap','DaKetThuc','DaHuy')	DEFAULT 'DaPhanCong'	
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
Bảng VaiTroThucTap

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaVaiTro	VARCHAR(10)	PRIMARY KEY	Mã vai trò (HD, PB, GS, UV...)
TenVaiTro	VARCHAR(100)	NOT NULL	Tên vai trò
MoTa	VARCHAR(255)		
Bảng KetQuaThucTap

Tên trường	Kiểu dữ liệu	Ràng buộc	Mô tả
MaKetQua	INT	AUTO_INCREMENT PRIMARY KEY	
MaThucTap	INT	FOREIGN KEY	Liên kết DanhSachThucTap
MaVaiTro	VARCHAR(10)	FOREIGN KEY	
MaNguoiDanhGia	VARCHAR(20)	FOREIGN KEY (GiangVien)	Người đánh giá
Diem	DECIMAL(4,2)		Điểm (thang 10)
NhanXet	TEXT		Nhận xét
created_at	DATETIME	DEFAULT CURRENT_TIMESTAMP	
updated_at	DATETIME	ON UPDATE CURRENT_TIMESTAMP	
2.3. Mối quan hệ giữa các bảng
Bảng cha	Bảng con	Quan hệ	Mô tả
NguoiDung	SinhVien	1-1	Mỗi sinh viên có 1 tài khoản
NguoiDung	GiangVien	1-1	Mỗi giảng viên có 1 tài khoản
NguoiDung	NhomNguoiDung	1-n	Một người có nhiều vai trò quản trị
GiangVien	BCN_ThanhVien	1-n	Giảng viên tham gia nhiều BCN
ChuongTrinhDaoTao	BCN_ThanhVien	1-n	Một CTĐT có nhiều thành viên BCN
ChuongTrinhDaoTao	CTDT_HocPhan	1-n	Một CTĐT có nhiều học phần
HocPhan	CTDT_HocPhan	1-n	Một học phần có thể thuộc nhiều CTĐT
CTDT_HocPhan	LopHocPhan	1-n	Một cấu hình HP mở nhiều lớp
HocKyNamHoc	LopHocPhan	1-n	Một học kỳ có nhiều lớp HP
GiangVien	LopHocPhan	1-n	Một GV dạy nhiều lớp
LopHocPhan	DanhSachSinhVienLopHocPhan	1-n	Một lớp có nhiều SV
SinhVien	DanhSachSinhVienLopHocPhan	1-n	Một SV tham gia nhiều lớp
LopHanhChinh	DotKienTap	1-n	Một lớp HC có nhiều đợt kiến tập
DotKienTap	DanhSachSinhVienKienTap	1-n	Một đợt kiến tập có nhiều SV
DotThucTap	DanhSachThucTap	1-n	Một đợt thực tập có nhiều phân công
DanhSachThucTap	KetQuaThucTap	1-n	Một phân công có nhiều kết quả đánh giá
VaiTroThucTap	KetQuaThucTap	1-n	Một vai trò xuất hiện trong nhiều kết quả
GiangVien	KetQuaThucTap	1-n	Một GV có nhiều đánh giá