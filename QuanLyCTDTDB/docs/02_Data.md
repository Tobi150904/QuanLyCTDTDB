I. PHÂN TÍCH VÀ ĐẶC TẢ YÊU CẦU
Hệ thống được xây dựng nhằm hỗ trợ Trung tâm Đào tạo Xuất sắc quản lý và điều hành các hoạt động liên quan đến đào tạo cho các chương trình đào tạo đặc biệt. Phạm vi hệ thống tập trung vào các nghiệp vụ cốt lõi: quản lý chương trình đào tạo, học phần, đội ngũ giảng viên, cố vấn học tập, doanh nghiệp đối tác, quy trình tổ chức kiến tập (theo đơn vị lớp) và thực tập, theo dõi và cảnh báo học vụ.

Các tác nhân chính tham gia hệ thống:
Phòng Đào tạo (PDT): Quản lý chung, phê duyệt chương trình đào tạo, quản lý học kỳ, người dùng và báo cáo thống kê.

Trung tâm Đào tạo Xuất sắc (TTĐTSX): Đầu mối xét duyệt chương trình đào tạo, kế hoạch thực tập, kiến tập và các hoạt động liên quan.

Ban chủ nhiệm chương trình (BCN): Xây dựng và quản lý chương trình đào tạo, mở lớp học phần, phân công giảng viên, lập danh sách sinh viên kiến tập và thực tập. BCN của mỗi CTĐT gồm các thành viên với các chức danh: Chủ nhiệm, Thư ký, Ủy viên.

Chủ nhiệm học phần (CNHP): Biên soạn và cập nhật đề cương học phần; quản lý đội ngũ giảng viên có thể giảng dạy học phần.

Giảng viên (GV): Giảng dạy lớp học phần, tải lên đề cương chi tiết, nhập nhận xét đánh giá sinh viên trong quá trình học; tham gia giám sát và đánh giá thực tập. Giảng viên có thể là giảng viên cơ hữu của trường hoặc chuyên gia từ doanh nghiệp được mời tham gia giảng dạy.

Cố vấn học tập (CVHT): Theo dõi cảnh báo học vụ, liên hệ hỗ trợ sinh viên và cập nhật kết quả xử lý.

Doanh nghiệp (DN): Tiếp nhận sinh viên kiến tập, thực tập; đăng nhập hệ thống để nhập nhận xét kiến tập; nhân viên doanh nghiệp có thể được phân công làm người hướng dẫn thực tập và tham gia đánh giá kết quả thực tập.

Sinh viên (SV): Tra cứu chương trình đào tạo, đề cương học phần, kế hoạch kiến tập, thực tập; nhận phản hồi từ giảng viên và doanh nghiệp.

1. Quy trình quản lý chương trình đào tạo đặc biệt
CTĐT đặc biệt là các chương trình tiên tiến, chương trình đặt hàng của doanh nghiệp, chương trình liên kết với nước ngoài.

Tác nhân tham gia: Ban chủ nhiệm chương trình (BCN), Trung tâm Đào tạo Xuất sắc (TTĐTSX), Phòng Đào tạo (PĐT), Chủ nhiệm học phần.

Mô tả chi tiết:

Quy trình bắt đầu khi Ban chủ nhiệm chương trình có nhu cầu xây dựng một chương trình đào tạo đặc biệt mới hoặc điều chỉnh chương trình hiện có. Dựa trên định hướng phát triển của nhà trường, nhu cầu thị trường lao động và tham khảo ý kiến các bên liên quan, BCN tiến hành xây dựng khung chương trình tổng thể. Các thông tin cần được xác định bao gồm: (I) Thông tin chung, (II) Mục tiêu chương trình đào tạo, (III) Chuẩn đầu ra chương trình đào tạo, (IV) Vị trí việc làm, (V) Quy định tuyển sinh và tốt nghiệp, (VI) Cấu trúc chương trình đào tạo, (VII) Nội dung chương trình đào tạo, (VIII) Hướng dẫn thực hiện và tổ chức chương trình, (IX) Hoạt động hỗ trợ sinh viên.

Sau khi hoàn thiện, BCN tải file mô tả CTĐT lên hệ thống và tiến hành cấu hình chi tiết chương trình trong bảng CTDT_HocPhan: với mỗi học phần thuộc CTĐT, xác định học kỳ thứ mấy (HocKyThu), số lớp dự kiến mở (SoLopDuKien), tính bắt buộc/tự chọn, và có thể tải lên file đề cương riêng của học phần cho CTĐT này (FileDeCuong). Đồng thời, BCN phân công các thành viên trong ban chủ nhiệm (BCN_ThanhVien) với các chức danh Chủ nhiệm, Thư ký, Ủy viên.

Toàn bộ CTĐT sau đó được trình lên TTĐTSX và PĐT xem xét, phê duyệt. Khi CTĐT được duyệt (trạng thái DaDuyet), hệ thống sẽ tự động tạo các lớp học phần (LopHocPhan) tương ứng với số lượng lớp dự kiến đã cấu hình, sẵn sàng cho việc phân công giảng viên ở học kỳ tương ứng.

2. Quy trình xây dựng đội ngũ giảng viên cho học phần và phân công giảng dạy
Mục đích: Xác định đội ngũ giảng viên có đủ năng lực tham gia giảng dạy cho từng học phần và phân công giảng dạy cụ thể cho từng học kỳ, đảm bảo giảng viên được phân công phải thuộc đội ngũ của học phần đó.

Tác nhân tham gia: Chủ nhiệm học phần, Ban chủ nhiệm chương trình, Trung tâm Đào tạo Xuất sắc (TTĐTSX), Phòng Đào tạo (PĐT).

Mô tả chi tiết:

Chủ nhiệm học phần (CNHP) có trách nhiệm xây dựng và quản lý đội ngũ giảng viên cho học phần – những giảng viên có đủ năng lực và chuyên môn để giảng dạy học phần đó. Đội ngũ này được lưu trong bảng DoiNguGiangVienHP. Việc xác định đội ngũ dựa trên các tiêu chí về trình độ chuyên môn: giảng viên có trình độ tiến sĩ được ưu tiên; giảng viên có trình độ thạc sĩ phải được đánh giá tốt về năng lực giảng dạy. Đối với các trường hợp đặc thù như mời chuyên gia từ doanh nghiệp tham gia giảng dạy (được lưu trong bảng GiangVien với LoaiGiangVien = 'DoanhNghiep'), BCN cần đề xuất lên TTĐTSX và PĐT phê duyệt.

Một giảng viên chỉ được gán giảng dạy một lớp học phần nếu họ nằm trong đội ngũ giảng viên của học phần đó.

Vào đầu mỗi học kỳ, căn cứ vào danh sách các lớp học phần đã được tạo tự động từ CTĐT, BCN thực hiện phân công giảng viên (LopHocPhan.MaGiangVien) từ đội ngũ hiện có cho từng lớp. Thông tin phân công được gửi lên TTĐTSX và PĐT để xét duyệt trước khi có hiệu lực chính thức.

3. Quy trình xây dựng đề cương học phần chi tiết
Mục đích: Đảm bảo chất lượng chuyên môn của từng lớp học phần thông qua việc xây dựng đề cương chi tiết.

Tác nhân tham gia: Chủ nhiệm học phần, Giảng viên giảng dạy.

Mô tả chi tiết:

Vào đầu mỗi học kỳ, giảng viên được phân công giảng dạy một lớp học phần có trách nhiệm xây dựng đề cương chi tiết cho lớp đó. Đề cương chi tiết bao gồm các nội dung: thông tin về giảng viên, lớp giảng dạy, mục tiêu, chuẩn đầu ra (CLOs), cấu trúc điểm thành phần, tài liệu dạy học, kế hoạch dạy học chi tiết theo từng chủ đề và yêu cầu đối với người học.

Trong 2 tuần đầu học kỳ, giảng viên tải file đề cương chi tiết (PDF) lên hệ thống (LopHocPhan.FileDeCuongChiTiet). Hệ thống ghi nhận thời hạn nộp và ngày thực tế nộp. Chủ nhiệm học phần có trách nhiệm xem xét, đánh giá đề cương. Nếu đạt yêu cầu, CNHP phê duyệt và đề cương chính thức được công bố cho sinh viên. Nếu cần điều chỉnh, CNHP gửi lại yêu cầu chỉnh sửa kèm góp ý cụ thể.

4. Quy trình theo dõi, đánh giá và cảnh báo học vụ
Mục đích: Theo dõi, đánh giá và cảnh báo kịp thời tình hình học tập của sinh viên dựa trên nhận xét của giảng viên, nhằm hỗ trợ sinh viên cải thiện kết quả học tập.

Tác nhân tham gia: Giảng viên giảng dạy, Sinh viên, Cố vấn học tập, Ban chủ nhiệm chương trình.

Mô tả chi tiết:

Trong suốt học kỳ, giảng viên có trách nhiệm nhập nhận xét cho từng sinh viên trong lớp học phần của mình. Nhận xét được lưu vào bảng DanhSachSinhVienLopHocPhan (là bảng liên kết giữa sinh viên và lớp học phần), bao gồm nội dung nhận xét và trạng thái cảnh báo (DaCanhBao).

Khi giảng viên nhập nhận xét mang tính cảnh báo (đặt DaCanhBao = 1), hệ thống tự động gửi thông báo ngay lập tức đến CVHT của lớp hành chính mà sinh viên đó thuộc về và tới chính sinh viên đó.

CVHT có trách nhiệm theo dõi các cảnh báo (DaCanhBao = 1), liên hệ với sinh viên để tư vấn, hỗ trợ, và cập nhật kết quả xử lý vào trường KetQuaXuLy trong cùng bảng DanhSachSinhVienLopHocPhan. BCN có thể xem tổng hợp các cảnh báo và mức độ xử lý của CVHT để đánh giá chất lượng học tập chung.

5. Quy trình tổ chức và quản lý kiến tập (theo đơn vị lớp)
Mục đích: Tổ chức các đợt kiến tập cho sinh viên theo đơn vị lớp hành chính, giúp họ quan sát, tìm hiểu thực tế tại doanh nghiệp. Kiến tập không có đánh giá điểm số mà chỉ có nhận xét từ các bên liên quan.

Tác nhân tham gia: Ban chủ nhiệm chương trình, Doanh nghiệp, Giảng viên phụ trách, Sinh viên, Trung tâm Đào tạo Xuất sắc (TTĐTSX).

Mô tả chi tiết:

BCN lên kế hoạch tổ chức các đợt kiến tập cho sinh viên, xác định lớp hành chính tham gia, doanh nghiệp sẽ đến thăm, thời gian dự kiến, giảng viên phụ trách đoàn. Các thông tin này được lưu vào bảng DotKienTap. Đồng thời, BCN nhập danh sách sinh viên tham gia (qua file Excel) vào bảng DanhSachSinhVienKienTap. Chi phí tổ chức được ghi nhận qua hai trường KinhPhiChung và KinhPhiTungSV. File minh chứng (kế hoạch, công văn) được tải lên hệ thống.

Đợt kiến tập được trình lên TTĐTSX phê duyệt chính thức trên hệ thống. Sau khi kết thúc đợt kiến tập, giảng viên phụ trách và doanh nghiệp sẽ nhập nhận xét chung vào các trường NhanXetGV và NhanXetDN của bảng DotKienTap.

6. Quy trình tổ chức và quản lý thực tập
Mục đích: Quản lý phân công và đánh giá kết quả thực tập của sinh viên. Thực tập được coi là một học phần chính thức trong chương trình đào tạo (có LoaiHocPhan = 'ThucTap').

Tác nhân tham gia: Ban chủ nhiệm chương trình (BCN), Trung tâm Đào tạo Xuất sắc (TTĐTSX), Doanh nghiệp, Giảng viên, Sinh viên.

Mô tả chi tiết:

BCN tạo đợt thực tập (DotThucTap) dựa trên học phần thực tập đã được cấu hình trong CTDT_HocPhan. Đợt thực tập được gắn với một học kỳ cụ thể và có thời gian bắt đầu, kết thúc.

Sau khi đợt thực tập được TTĐTSX phê duyệt, BCN tiến hành phân công sinh viên vào bảng DanhSachThucTap. Mỗi phân công xác định loại hình thực tập (LoaiThucTap): tại trường ('Truong') hoặc tại doanh nghiệp ('DoanhNghiep'). Nếu thực tập tại doanh nghiệp, cần chỉ định doanh nghiệp tiếp nhận (MaDoanhNghiep).

Sau khi kết thúc đợt thực tập, việc đánh giá được thực hiện bởi nhiều người với các vai trò khác nhau. Hệ thống cho phép định nghĩa danh mục các vai trò đánh giá (VaiTroThucTap) như: Hướng dẫn, Phản biện, Giám sát, Ủy viên hội đồng... Mỗi người đánh giá (là giảng viên hoặc nhân viên doanh nghiệp được lưu trong bảng GiangVien) sẽ nhập điểm và nhận xét của mình cho từng sinh viên vào bảng KetQuaThucTap. Mỗi bản ghi trong bảng này tương ứng với một đánh giá của một người với một vai trò cụ thể. Điểm tổng kết của sinh viên có thể được tổng hợp từ các điểm thành phần này theo quy chế đào tạo.

Sinh viên có thể tra cứu thông tin phân công và kết quả đánh giá của mình trên hệ thống.
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