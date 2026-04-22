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

Trong 2 tuần đầu học kỳ, giảng viên tải file đề cương chi tiết (PDF) lên hệ thống (LopHocPhan.FileDeCuongChiTiet). Hệ thống ghi nhận thời hạn nộp và ngày thực tế nộp. Chủ nhiệm học phần có trách nhiệm xem xét, đánh giá đề cương. Nếu đạt yêu cầu, CNHP phê duyệt và đề cương chính thức được công bố cho sinh viên. Nếu c��n điều chỉnh, CNHP gửi lại yêu cầu chỉnh sửa kèm góp ý cụ thể.

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

BCN lên kế hoạch tổ chức các đợt kiến tập cho sinh viên, xác định lớp hành chính tham gia, doanh nghiệp sẽ đến thăm, thời gian dự kiến, giảng viên phụ trách đoàn. Các thông tin này được lưu vào bảng DotKienTap. Chi phí tổ chức được ghi nhận qua hai trường KinhPhiChung và KinhPhiTungSV. File minh chứng (kế hoạch, công văn) được tải lên hệ thống.

Khi đợt kiến tập được tạo ở trạng thái ChuanBi, hệ thống TỰ ĐỘNG thêm toàn bộ sinh viên của lớp hành chính đó có trạng thái `TrangThaiSV = 'DangHoc'` vào bảng `DanhSachSinhVienKienTap` với cờ `DaThamGia = 1`. Nguyên tắc này bảo đảm không sót bất kỳ sinh viên nào của lớp khỏi dữ liệu kiến tập. Sinh viên có trạng thái khác (`BaoLuu`, `ThoiHoc`, `TotNghiep`) KHÔNG được thêm tự động để tránh làm sai lệch thống kê.

Trong quá trình chuẩn bị và thực hiện đợt kiến tập, BCN hoặc TTĐTSX có thể cập nhật cờ `DaThamGia` theo thực tế: đặt `DaThamGia = 0` cho sinh viên không thể tham gia (ốm đau, trùng lịch thi, đi thực tập chỗ khác...), và có thể đảo lại thành 1 khi sinh viên xác nhận tham gia. Hệ thống không xóa cứng bản ghi — luôn giữ lại để phục vụ audit và báo cáo. Khi một sinh viên mới được chuyển vào lớp sau khi tạo đợt, người quản lý có thể dùng thao tác "Đồng bộ danh sách" để thêm sinh viên `DangHoc` hiện tại nhưng chưa có trong danh sách (không xóa những bản ghi đã có).

Đợt kiến tập được trình lên TTĐTSX phê duyệt chính thức trên hệ thống. Sau khi phê duyệt, đợt có thể chuyển sang trạng thái `DaThucHien` khi BCN xác nhận đã hoàn thành, hoặc chuyển sang `DaHuy` nếu bị hủy bỏ (có thể xảy ra ở bất kỳ trạng thái nào trước đó). Sau khi kết thúc đợt kiến tập, giảng viên phụ trách và doanh nghiệp sẽ nhập nhận xét chung vào các trường NhanXetGV và NhanXetDN của bảng DotKienTap; hai nhận xét này độc lập và không ghi đè lẫn nhau.

6. Quy trình tổ chức và quản lý thực tập
Mục đích: Quản lý phân công và đánh giá kết quả thực tập của sinh viên. Thực tập được coi là một học phần chính thức trong chương trình đào tạo (có LoaiHocPhan = 'ThucTap').

Tác nhân tham gia: Ban chủ nhiệm chương trình (BCN), Trung tâm Đào tạo Xuất sắc (TTĐTSX), Doanh nghiệp, Giảng viên, Sinh viên.

Mô tả chi tiết:

BCN tạo đợt thực tập (DotThucTap) dựa trên học phần thực tập đã được cấu hình trong CTDT_HocPhan. Đợt thực tập được gắn với một học kỳ cụ thể và có thời gian bắt đầu, kết thúc.

Sau khi đợt thực tập được TTĐTSX phê duyệt, BCN tiến hành phân công sinh viên vào bảng DanhSachThucTap. Mỗi phân công xác định loại hình thực tập (LoaiThucTap): tại trường ('Truong') hoặc tại doanh nghiệp ('DoanhNghiep'). Nếu thực tập tại doanh nghiệp, cần chỉ định doanh nghiệp tiếp nhận (MaDoanhNghiep).

Sau khi kết thúc đợt thực tập, việc đánh giá được thực hiện bởi nhiều người với các vai trò khác nhau. Hệ thống cho phép định nghĩa danh mục các vai trò đánh giá (VaiTroThucTap) như: Hướng dẫn, Phản biện, Giám sát, Ủy viên hội đồng... Mỗi người đánh giá (là giảng viên hoặc nhân viên doanh nghiệp được lưu trong bảng GiangVien) sẽ nhập điểm và nhận xét của mình cho từng sinh viên vào bảng KetQuaThucTap. Mỗi bản ghi trong bảng này tương ứng với một đánh giá của một người với một vai trò cụ thể. Điểm tổng kết của sinh viên có thể được tổng hợp từ các điểm thành phần này theo quy chế đào tạo.

Sinh viên có thể tra cứu thông tin phân công và kết quả đánh giá của mình trên hệ thống.


# 02_Data — Quy Uoc Du Lieu & Rang Buoc Nghiep Vu

> Nhất quán với `01_ERD_SCHEMA.md`, `scripts/01_create_tables.sql`, `scripts/02_seed_data.sql`.
> Day la nguon su that cho moi rang buoc nghiep vu khi code Service layer.

---

## 1. QUY UOC KHOA CHINH (MaNguoiDung FORMAT)

| Loai tai khoan | Format        | Vi du              | Nguon sinh                          |
|----------------|---------------|--------------------|-------------------------------------|
| Admin          | AD + 3 so     | AD001              | So tang tu DB (001, 002...)         |
| Giang Vien     | GV + 3 so     | GV001, GV002       | So tang tu DB                       |
| Sinh Vien      | SV + 7 ky tu  | SV2024001          | SV + nam + 3 so (SV2024001...)      |
| Doanh Nghiep   | DN + 3 so     | DN001              | So tang tu DB                       |
| Hoc Ky         | HKn-YYYY      | HK1-2024           | HK1 hoac HK2 + nam bat dau          |
| CTDT           | CTDT-NGANH-YYYY | CTDT-CNTT-2022   | Tu nhap khi tao                     |
| Hoc Phan       | HP-MATHE      | HP-LTW, HP-CSDL    | Tu nhap khi tao                     |
| Lop HC         | NGANH-KhoaLop | CNTT-K22A          | Tu nhap khi tao                     |
| Dot KT         | INT AUTO      | 1, 2, 3            | AUTO_INCREMENT                      |
| Dot TT         | INT AUTO      | 1, 2, 3            | AUTO_INCREMENT                      |
| TT record      | INT AUTO      | 1, 2, 3            | AUTO_INCREMENT                      |
| KQ TT          | INT AUTO      | 1, 2, 3            | AUTO_INCREMENT                      |

### 1.1 CHINH SACH KHOA BAT BIEN (IMMUTABLE KEYS)

> **QUAN TRONG — QUY UOC BAT BUOC CHO MOI TANG (DB / JPA / Service / UI):**
> Moi business key trong bang o tren la **IMMUTABLE** sau khi da INSERT. **Khong cho
> phep `UPDATE` cot khoa chinh** duoi bat ky hinh thuc nao (admin SQL, form Edit,
> Excel import, migration script, v.v.).

**Ly do thiet ke:**
- Tat ca FK trong DDL dung hanh vi mac dinh `ON UPDATE RESTRICT`. UPDATE khoa cha
  se bi MySQL chan (error 1451) neu co FK con tham chieu — de tranh phan manh du lieu.
- Khoa nghiep vu xuat hien o chung tu in (phieu diem, quyet dinh, bao cao Excel)
  va log audit. Neu doi mau, lich su 3-5 nam truoc se khong truy nguoc duoc.
- Project dung nhieu composite key (vd `LopHocPhan` co 4 cot PK, `DanhSachSinhVienLopHocPhan`
  co 5 cot PK). `ON UPDATE CASCADE` tren composite se khoa ban nhieu bang cung luc → dung
  risk o production.

**Quy trinh DUNG khi can "doi ma":**

| Tinh huong                          | Khong duoc lam            | Thay vao do                                                     |
|-------------------------------------|---------------------------|-----------------------------------------------------------------|
| Doi format mã GV (`GV001`→`GV101`)  | `UPDATE GiangVien SET MaGV=...` | INSERT GV moi `GV101` + set `GiangVien.LoaiGiangVien='NghiViec'` cho `GV001` (neu co cot soft-delete) hoac de nguyen record cu, moi phan cong moi tro ve `GV101`. |
| SV chuyen nganh, doi MSSV           | `UPDATE SinhVien SET MaSV=...`  | INSERT SV moi voi MSSV moi + set `SinhVien.TrangThaiSV='ThoiHoc'` cho record cu. Diem/thuc tap cu van thuoc ve MSSV cu — dung nghiep vu "luu ban sao ho so". |
| Doi ten mon (`HP-LTW`→`HP-WEB`)     | `UPDATE HocPhan SET MaHocPhan=...` | Giu `MaHocPhan` nguyen, chi `UPDATE HocPhan SET TenHocPhan=...` (ten co the doi, ma thi khong). |
| Doi CTDT khi lam lai chuong trinh   | `UPDATE ChuongTrinhDaoTao SET MaCTDT=...` | Tao CTDT moi (vd `CTDT-CNTT-2025`) + `TrangThai='DaDuyet'`, CTDT cu giu nguyen `DaDuyet` cho SV khoa truoc. |
| Doi dinh danh dot KT/TT             | —                         | Da dung `INT AUTO_INCREMENT`, khong co nhu cau doi. |

**Cac truong DUOC UPDATE tu do (khong phai PK):**
`TrangThai`, `HoTen`, `Email`, `SoDienThoai`, `DiaChi`, `FileDeCuong`, `MoTa`,
`NhanXet`, cac field `updated_at`… Noi chung moi cot **khong** nam trong PK/FK
deu cap nhat binh thuong.

**Implementation o tang Service/Controller:**
- Form Edit cua cac entity tren **khong render input cho khoa chinh** (chi readonly).
- DTO Update KHONG chua field khoa (chi field edit duoc).
- Repository khong expose method `save()` cho luong "doi khoa" — chi `update non-key fields` + `insert new + deactivate old`.

---

## 2. ENUM VALUES CHINH XAC

> **QUAN TRONG:** Gia tri enum trong Java phai khop 100% voi gia tri ENUM trong MySQL DDL.
> Dung `@Enumerated(EnumType.STRING)` cho tat ca enum.

| Java Enum Class        | Values (chinh xac)                                     | Bang su dung              |
|------------------------|-------------------------------------------------------|---------------------------|
| TrangThaiHocKy         | `SapDienRa`, `DangDienRa`, `DaKetThuc`               | HocKyNamHoc               |
| LoaiNguoiDung          | `Admin`, `GiangVien`, `SinhVien`, `DoanhNghiep`      | NguoiDung                 |
| VaiTro                 | `PDT`, `TTDTXS`, `CVHT`, `CNHP`                     | NhomNguoiDung             |
| TrangThaiCTDT          | `BanNhap`, `ChoDuyet`, `DaDuyet`, `DaHuy`            | ChuongTrinhDaoTao         |
| ChucDanhBCN            | `ChuNhiem`, `ThuKy`, `UyVien`                        | BCN_ThanhVien             |
| LoaiHocPhan            | `LyThuyet`, `ThucHanh`, `DoAn`, `ThucTap`, `KienTap`| HocPhan                   |
| TrangThaiHocPhan       | `BanNhap`, `ChoDuyet`, `DaDuyet`                     | HocPhan                   |
| LoaiGiangVien          | `GiangVienTruong`, `DoanhNghiep`                     | GiangVien                 |
| TrangThaiDoanhNghiep   | `DangHopTac`, `TamNgung`                             | DoanhNghiep               |
| TrangThaiSinhVien      | `DangHoc`, `BaoLuu`, `ThoiHoc`, `TotNghiep`         | SinhVien                  |
| TrangThaiLopHocPhan    | `DangMo`, `DaDong`, `DaHuy`                          | LopHocPhan                |
| TrangThaiDotKT         | `ChuanBi`, `ChoDuyet`, `DaDuyet`, `DaThucHien`, `DaHuy` | DotKienTap            |
| TrangThaiDotTT         | `ChuanBi`, `ChoDuyet`, `DaDuyet`, `DangThucHien`, `DaKetThuc`, `DaHuy` | DotThucTap        |
| LoaiThucTap            | `Truong`, `DoanhNghiep`                              | DanhSachThucTap           |
| TrangThaiThucTap       | `DaPhanCong`, `DangThucTap`, `DaKetThuc`, `DaHuy`   | DanhSachThucTap           |

---

## 3. RANG BUOC DU LIEU THEO BANG

### 3.1 NguoiDung
- `TenDangNhap`: 3-50 ky tu, chi chua chu thuong, so, dau cham (vi du: `tran.van.a`)
- `MatKhau` (truoc hash): >= 8 ky tu, it nhat 1 chu hoa, 1 so, 1 ky tu dac biet
- `Email`: dinh dang email hop le, UNIQUE toan bang
- `TrangThaiTK`: mac dinh = 1 (hoat dong)
- `LoaiNguoiDung`: bat buoc, khong duoc null

### 3.2 SinhVien & GiangVien
- `MaSV = MaNguoiDung` (bat buoc khop khi tao)
- `MaGV = MaNguoiDung` (bat buoc khop khi tao)
- Khi xoa NguoiDung: xoa cascade SinhVien/GiangVien (ON DELETE CASCADE)
- `MaLopHC` trong SinhVien: RESTRICT — khong xoa LopHanhChinh neu con SinhVien

### 3.3 HocPhan
- `SoTinChi`: 1 <= x <= 10 (kiem tra o @Min @Max trong DTO)
- `TrangThai` moi tao: mac dinh `BanNhap`
- `ChuNhiemHP` bat buoc ton tai trong bang `GiangVien`
- Khi tao HocPhan: tu dong INSERT `DoiNguGiangVienHP(MaHocPhan, ChuNhiemHP, TrangThai=1)`
- **Khong duoc xoa CNHP khoi DoiNguGiangVienHP** (kiem tra trong service)
- Neu GV da duoc gan vao LopHocPhan: UPDATE TrangThai=0, khong DELETE

### 3.4 ChuongTrinhDaoTao
- `TrangThai` moi tao: `BanNhap`
- Chi them HocPhan vao CTDT khi `HocPhan.TrangThai = DaDuyet`
- `HocKyThu`: 1 <= x <= 10
- `SoLopDuKien`: >= 1
- Khi phe duyet CTDT (DaDuyet): tu dong tao `LopHocPhan` theo `SoLopDuKien`
- `NguoiDuyet` phai khac `NguoiTao` (kiem tra o service)

### 3.5 LopHocPhan
- `SiSoToiDa`: 30 <= x <= 60 (MySQL CHECK constraint + @Min @Max trong DTO)
- `SiSoThucTe`: <= SiSoToiDa (kiem tra truoc khi them SV)
- `MaGiangVien = NULL` khi moi tao (phan cong sau)
- Khi gan GV khong trong DoiNguGiangVienHP: canh bao nhung van cho phep (confirm dialog)

### 3.6 DanhSachSinhVienLopHocPhan
- `DaCanhBao`: Mac dinh 0, khi GV tich = 1 -> trigger gui email CVHT
- `DaCanhBao` giu = 1 sau khi da xu ly, chi them `KetQuaXuLy`
- Khong xoa record co `DaCanhBao = 1`

### 3.7 DotKienTap & DanhSachSinhVienKienTap (Hybrid Auto-Add + Toggle DaThamGia)

**Rang buoc khi TAO dot kien tap (TrangThai = ChuanBi):**
- `MaDoanhNghiep`: phai o trang thai `DangHopTac` (kiem tra o service, reject neu TamNgung)
- `MaLopHC`: phai ton tai trong bang LopHanhChinh
- `MaHocKy`: bat buoc, phai ton tai
- `MaGVPhuTrach`: co the NULL khi tao, phai set truoc khi nop duyet
- `NguoiTao`: lay tu SecurityContext (bat buoc NOT NULL trong DB)
- `KinhPhiChung`, `KinhPhiTungSV`: co the NULL (khong bat buoc)

**Auto-add sinh vien (NGHIEP VU QUAN TRONG — khop WF-07.1 BUOC 2):**
- Ngay sau khi INSERT DotKienTap thanh cong, SERVICE phai chay dong bo:
  ```
  SELECT MaSV FROM SinhVien
  WHERE MaLopHC = <maLopHC cua dot>
    AND TrangThaiSV = 'DangHoc'
  ```
  → INSERT vao `DanhSachSinhVienKienTap (MaDotKT, MaSV, DaThamGia=1)` cho TAT CA sinh vien thu duoc.
- Sinh vien `BaoLuu / ThoiHoc / TotNghiep` KHONG duoc them tu dong (tranh du lieu "ma").
- Neu lop khong co SV `DangHoc` nao: van tao DotKienTap, nhung canh bao UI ("Lop chua co sinh vien DangHoc").

**Quy dinh cap nhat DaThamGia:**
- Vai tro duoc phep: PDT, TTDTXS, BCN (CNHP la thanh vien BCN) cua CTDT chua lop HC do.
- Endpoint: `POST /kien-tap/chi-tiet/{maDotKT}/sv/{maSV}/danh-dau?daThamGia=0|1`
- Chi cho phep toggle khi `DotKienTap.TrangThai IN ('ChuanBi','ChoDuyet','DaDuyet','DaThucHien')`.
  - `DaHuy`: khoa, khong cho thay doi.
- KHONG hard-DELETE ban ghi — luon giu lai de audit.
- Rang buoc: mot SV bi danh dau `DaThamGia=0` thi khong xuat hien trong bao cao "Thuc te tham gia" nhung van hien o tab "Toan bo danh sach lop".

**Dong bo danh sach sau khi tao (re-sync):**
- Khi lop co SV moi duoc chuyen den (sau thoi diem tao dot) hoac SV chuyen trang thai `BaoLuu -> DangHoc`,
  nguoi quan ly co the bam nut "Dong bo danh sach" (`POST /kien-tap/chi-tiet/{maDotKT}/dong-bo`):
  - SERVICE chay lai truy van o phan Auto-add tren.
  - Cac `MaSV` moi chua ton tai trong `DanhSachSinhVienKienTap`: INSERT voi `DaThamGia=1`.
  - Cac `MaSV` da ton tai: GIU NGUYEN ca ban ghi (bao gom gia tri `DaThamGia` do nguoi quan ly da set).
  - Cac `MaSV` da chuyen sang trang thai khac `DangHoc`: KHONG tu dong set `DaThamGia=0`; nguoi quan ly tu danh dau neu can (tranh ghi de y do nghiep vu).
- Chi cho phep chay dong bo khi dot CHUA o trang thai `DaHuy`.

**State machine DotKienTap (khop 03_WORKFLOW §7):**
```
ChuanBi  --(nop duyet)-->  ChoDuyet
ChoDuyet --(phe duyet)-->  DaDuyet
DaDuyet  --(xac nhan hoan thanh)--> DaThucHien
{ChuanBi,ChoDuyet,DaDuyet,DaThucHien} --(huy)--> DaHuy
```
Bat buoc set `NguoiDuyet + NgayDuyet` khi chuyen `ChoDuyet -> DaDuyet`.

### 3.8 DotThucTap & DanhSachThucTap

**DotThucTap (dot thuc tap):**
- `(MaCTDT, MaHocPhan)` phai ton tai trong `CTDT_HocPhan` va HocPhan do phai co `LoaiHocPhan IN ('ThucTap','KienTap')` (check o service, reject neu LyThuyet/ThucHanh/DoAn).
- `MaHocKy` bat buoc, phai ton tai trong HocKyNamHoc.
- `NgayBatDau <= NgayKetThuc` (validate o DTO).
- `NguoiTao` lay tu SecurityContext (NOT NULL).
- State machine:
  ```
  ChuanBi --(nop duyet)--> ChoDuyet --(phe duyet)--> DaDuyet --(bat dau)--> DangThucHien --(ket thuc)--> DaKetThuc
  ```
  Buoc `DaDuyet -> DangThucHien` co the auto chay ngay sau khi phe duyet (theo 03_WORKFLOW §8.1 BUOC 4) hoac tach thanh action rieng tuy trien khai.
- Bat buoc set `NguoiDuyet + NgayDuyet` khi chuyen `ChoDuyet -> DaDuyet`.

**DanhSachThucTap (phan cong thuc tap cho tung SV):**
- UNIQUE nghiep vu `(MaDotTT, MaSV)` — kiem tra truoc INSERT.
- Neu trung: SKIP, them vao danh sach loi (import Excel khong throw exception cho ca batch).
- `LoaiThucTap IN ('Truong','DoanhNghiep')`:
  - `DoanhNghiep`: `MaDoanhNghiep` BAT BUOC va DN phai `DangHopTac`.
  - `Truong`: `MaDoanhNghiep` PHAI NULL.
- `SinhVien`: phai co `TrangThaiSV='DangHoc'` tai thoi diem import.
- State machine 1 ban ghi: `DaPhanCong -> DangThucTap -> DaKetThuc` (hoac `DaHuy` tu bat ky trang thai nao).
- Khi DotThucTap chuyen sang `DaKetThuc`: UPDATE cascade `DanhSachThucTap.TrangThai = 'DaKetThuc'` cho ban ghi dang `DangThucTap`.

### 3.9 KetQuaThucTap
- `Diem`: 0.00 <= x <= 10.00 (kiem tra o @DecimalMin @DecimalMax trong DTO)
- `MaNguoiDanhGia` tham chieu `GiangVien.MaGV` (chi GV nhap ket qua trong he thong)
- Cho moi `(MaThucTap, MaVaiTro)`: neu da co -> UPDATE, chua co -> INSERT

---

## 4. DU LIEU MAU (SEED DATA — TOM TAT)

Sau khi chay `02_seed_data.sql` (v3, Phase 3 review — bo sung test case cho DaThamGia hybrid):

| Bang                           | So ban ghi | Ghi chu quan trong                                          |
|--------------------------------|------------|-------------------------------------------------------------|
| HocKyNamHoc                    | 4          | HK1-2024 = DangDienRa, HK2-2024 = SapDienRa                |
| NguoiDung                      | 20         | 1 Admin, 6 GV, 12 SV, 2 DN (them 2 SV de mo phong BaoLuu/ThoiHoc) |
| GiangVien                      | 6          | GV001..GV006                                                |
| SinhVien                       | 12         | 10 DangHoc + SV2023004 BaoLuu (K23A) + SV2022005 ThoiHoc (K22B) |
| NhomNguoiDung                  | 10         | GV001: PDT+TTDTXS; GV002: TTDTXS; GV003..GV006: CVHT; GV004..GV006: CNHP |
| DoanhNghiep                    | 4          | DN001,DN002,DN003 DangHopTac; DN004 TamNgung                |
| LopHanhChinh                   | 4          | CNTT-K22A, K22B, K23A, K24A                                 |
| ChuongTrinhDaoTao              | 3          | 2022+2023 DaDuyet, 2024 ChoDuyet                            |
| BCN_ThanhVien                  | 9          | 3 CTDT × (ChuNhiem, ThuKy, UyVien)                         |
| HocPhan                        | 10         | 9 DaDuyet, 1 ChoDuyet (HP-AI)                              |
| DoiNguGiangVienHP              | 25         | 10 CNHP auto + 15 GV bo sung                                |
| CTDT_HocPhan                   | 18         |                                                              |
| LopHocPhan                     | 12         | 5 DaDong (HK1-2023, HK2-2023), 7 DangMo (HK1-2024)         |
| DanhSachSinhVienLopHocPhan     | 15         | 2 ban ghi DaCanhBao=1: 1 da xu ly (SV2022003 HK1-2023), 1 chua xu ly (SV2022003 HP-LTW HK1-2024) |
| DotKienTap                     | 3          | 1 DaThucHien, 1 DaDuyet, 1 ChoDuyet                         |
| DanhSachSinhVienKienTap        | 7          | Auto tao tu SV DangHoc (K22A:2, K22B:2, K23A:3); Dot 2 co 1 SV DaThamGia=0 minh hoa nghiep vu toggle (SV van duoc giu, khong xoa cung) |
| DotThucTap                     | 2          | 1 DangThucHien, 1 ChuanBi                                   |
| DanhSachThucTap                | 4          | SV2022001..004, trong do 1 thuc tap tai Truong (SV2022004)  |
| VaiTroThucTap                  | 3          | GV, DN, CVHT (SV khong tu danh gia — FK bat nguoi danh gia la GiangVien) |
| KetQuaThucTap                  | 4          | SV2022001: GV+DN; SV2022002,003: GV                         |

**Test case "SV khong tham gia" minh hoa trong seed:**
- `SV2023004` (K23A, `TrangThaiSV=BaoLuu`): KHONG duoc auto-add vao DotKienTap 3 (du DotKienTap 3 chon lop CNTT-K23A) — minh hoa quy tac "chi SV DangHoc moi duoc auto-add".
- `SV2022005` (K22B, `TrangThaiSV=ThoiHoc`): KHONG duoc auto-add vao DotKienTap 2.
- DotKienTap 2 (CNTT-K22B) co `SV2022004` bi set `DaThamGia=0` — minh hoa thao tac "danh dau khong tham gia" (bao luu ban ghi de audit).

### Tai khoan test (MatKhau: Password@123)

| TenDangNhap    | LoaiNguoiDung | VaiTro NhomNguoiDung | Ghi chu                                           |
|----------------|---------------|----------------------|---------------------------------------------------|
| admin          | Admin         | —                    | Toan quyen he thong                               |
| tran.van.an    | GiangVien     | PDT + TTDTXS         | Truong Phong Dao Tao + TTDTXS                    |
| le.thi.binh    | GiangVien     | TTDTXS               | Thanh vien TT Dao Tao Xuat Sac                   |
| nguyen.cuong   | GiangVien     | CVHT                 | CVHT CNTT-K22A                                    |
| pham.dung      | GiangVien     | CVHT + CNHP          | CVHT CNTT-K22B, CNHP HP-LTW                      |
| hoang.em       | GiangVien     | CVHT + CNHP          | CVHT CNTT-K23A, CNHP HP-CSDL + HP-TTDL           |
| vu.thi.giang   | GiangVien     | CVHT + CNHP          | CVHT CNTT-K24A, CNHP HP-OOP + HP-AI              |
| sv.2024001..3  | SinhVien      | —                    | CNTT-K24A, DangHoc                                |
| sv.2023001..3  | SinhVien      | —                    | CNTT-K23A, DangHoc                                |
| sv.2022001..2  | SinhVien      | —                    | CNTT-K22A, DangThucTap Dot 1                      |
| sv.2022003..4  | SinhVien      | —                    | CNTT-K22B, DangThucTap Dot 1                      |
| dn.fpt         | DoanhNghiep   | —                    | FPT Software, DangHopTac                          |
| dn.vng         | DoanhNghiep   | —                    | VNG Corporation, DangHopTac                       |

---

## 5. QUY UOC FORMAT NGAY THANG & SO

| Kieu du lieu     | Format            | Vi du                  | Ghi chu                           |
|------------------|-------------------|------------------------|-----------------------------------|
| DATE             | yyyy-MM-dd        | 2024-09-02             | MySQL DATE                        |
| DATETIME         | yyyy-MM-dd HH:mm  | 2024-09-02 09:00       | Hien thi trong form               |
| DATETIME (DB)    | DATETIME          | MySQL native           | Luu chuyen doi boi Hibernate      |
| DECIMAL(4,2)     | 0.00 - 10.00      | 8.50                   | Diem thuc tap                     |
| DECIMAL(15,2)    | 0.00 - 99999...   | 10000000.00            | Kinh phi                          |
| VARCHAR ma       | UPPER hoac CamelCase | HP-LTW, CNTT-K22A  | Khong co khoang trang, khong dau  |
| HoTen, TenLop    | Unicode UTF8MB4   | Nguyen Van An          | Co dau tieng Viet                 |

---

## 6. QUY UOC FILE UPLOAD

| Loai file      | Truong luu       | Thu muc                | Extension cho phep    | Kich thuoc toi da |
|----------------|------------------|------------------------|-----------------------|-------------------|
| CTDT Word      | ChuongTrinhDaoTao.FileWord  | uploads/ctdt/ | .doc, .docx           | 20 MB             |
| De cuong HP    | HocPhan.FileDeCuong         | uploads/hocphan/ | .pdf, .doc, .docx   | 20 MB             |
| De cuong CT    | LopHocPhan.FileDeCuongChiTiet | uploads/lophocphan/ | .pdf, .doc, .docx | 20 MB           |
| Minh chung KT  | DotKienTap.FileMinhChung    | uploads/kientap/  | .pdf, .doc, .docx   | 20 MB             |
| Minh chung TT  | DotThucTap.FileMinhChung    | uploads/thuctap/  | .pdf, .doc, .docx   | 20 MB             |
| Excel import   | (khong luu)                 | temp/             | .xlsx, .xls           | 20 MB             |

**Quy tac dat ten file luu:** `{MaDanhMuc}_{timestamp}_{original_filename}`
Vi du: `HP-LTW_20241015_decuong.pdf`

---

## 7. QUY UOC PHAN QUYEN THEO URL

| URL Pattern            | Roles duoc phep truy cap                           |
|------------------------|----------------------------------------------------|
| /login, /logout        | permitAll                                          |
| /dashboard             | authenticated                                      |
| /nguoi-dung/**         | ROLE_PDT, ROLE_ADMIN                               |
| /doanh-nghiep/**       | ROLE_PDT, ROLE_TTDTXS, ROLE_ADMIN                 |
| /hoc-ky/**             | ROLE_PDT, ROLE_TTDTXS, ROLE_ADMIN                 |
| /hoc-phan/**           | ROLE_CNHP, ROLE_TTDTXS, ROLE_ADMIN                |
| /ctdt/**               | ROLE_PDT, ROLE_TTDTXS, ROLE_CNHP, ROLE_ADMIN      |
| /lop-hoc-phan/**       | ROLE_PDT, ROLE_TTDTXS, ROLE_CNHP, ROLE_ADMIN, ROLE_GIANG_VIEN |
| /danh-gia/**           | ROLE_GIANG_VIEN, ROLE_CVHT, ROLE_ADMIN             |
| /kien-tap/**           | ROLE_TTDTXS, ROLE_CNHP, ROLE_ADMIN, ROLE_GIANG_VIEN, ROLE_DOANH_NGHIEP |
| /thuc-tap/**           | ROLE_PDT, ROLE_TTDTXS, ROLE_ADMIN, ROLE_GIANG_VIEN, ROLE_CVHT, ROLE_DOANH_NGHIEP, ROLE_SINH_VIEN |
| /bao-cao/**            | ROLE_PDT, ROLE_TTDTXS, ROLE_ADMIN                 |
| /profile/**            | authenticated                                      |
| /403                   | permitAll                                          |
| /error                 | permitAll                                          |

---

## 8. FLASH MESSAGE CONVENTION

| Key           | Y nghia                        | Kieu Bootstrap |
|---------------|--------------------------------|----------------|
| successMsg    | Thao tac thanh cong            | alert-success  |
| errorMsg      | Loi nghiep vu (da xu ly)       | alert-danger   |
| warningMsg    | Canh bao (khong chan lai)       | alert-warning  |
| infoMsg       | Thong tin tham khao            | alert-info     |

Su dung: `redirectAttributes.addFlashAttribute("successMsg", "Tao thanh cong")`

---

## 9. EMAIL TEMPLATE (MockEmailServiceImpl — Dev)

Chi log ra `System.out` / logger, khong gui that. Khi deploy: bat spring.mail config.

| Loai email         | Trigger                        | Nguoi nhan      | Noi dung chinh                         |
|--------------------|--------------------------------|-----------------|----------------------------------------|
| CanhBaoSinhVien    | DaCanhBao duoc dat = 1         | CVHT cua SV     | HoTenSV, TenHocPhan, NhanXetGV         |
| PhanCongLopHP      | GV duoc gan vao LopHocPhan     | GiangVien       | TenHocPhan, LopHocPhan, HocKy         |
| PheDuyetHocPhan    | HP duoc duyet                  | ChuNhiemHP      | MaHocPhan, TenHocPhan, NgayDuyet       |
| TuChoiHocPhan      | HP bi tu choi                  | ChuNhiemHP      | MaHocPhan, LyDoTuChoi                  |
