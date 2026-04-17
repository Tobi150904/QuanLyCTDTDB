# ENTITY SPECIFICATION - Chi tiet tung Entity Java
# Dung lam cam nang viet code Entity / JPA

---

## NGUYEN TAC CHUNG CHO MOI ENTITY

```java
// 1. Annotation bat buoc tren class
@Entity
@Table(name = "TenBang")
@Getter @Setter @NoArgsConstructor  // Lombok
// KHONG dung @Data (tao loi voi Hibernate circular refs)

// 2. Audit fields (dung @EntityListeners neu co AuditConfig)
@CreationTimestamp
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;

@UpdateTimestamp
@Column(name = "updated_at")
private LocalDateTime updatedAt;

// 3. Enum mapping - PHAI dung @Enumerated(EnumType.STRING)
@Enumerated(EnumType.STRING)
@Column(name = "TrangThai")
private TrangThaiHocKy trangThai;

// 4. FK nullable - dung (optional = true) hoac nullable = true
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "MaGiangVien", nullable = true)
private NguoiDung giangVien;
```

---

## 1. NguoiDung.java

```java
@Entity
@Table(name = "NguoiDung")
public class NguoiDung {

    @Id
    @Column(name = "MaNguoiDung", length = 20)
    private String maNguoiDung;

    @Column(name = "TenDangNhap", length = 50, nullable = false, unique = true)
    private String tenDangNhap;

    @Column(name = "MatKhauHash", length = 255, nullable = false)
    private String matKhauHash;

    @Column(name = "Email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "HoTen", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHC", nullable = true)
    private LopHanhChinh lopHanhChinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThaiSV")
    private TrangThaiSinhVien trangThaiSV;

    @Column(name = "HocHam", length = 50)
    private String hocHam;

    @Column(name = "HocVi", length = 50)
    private String hocVi;

    @Column(name = "ChuyenNganh", length = 200)
    private String chuyenNganh;

    @Column(name = "TrangThaiTK", columnDefinition = "BIT DEFAULT 1")
    private boolean trangThaiTK = true;

    // Quan he: 1 NguoiDung co nhieu VaiTro
    @OneToMany(mappedBy = "nguoiDung", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NguoiDungVaiTro> vaiTros = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

---

## 2. NguoiDungVaiTro.java (Composite PK)

```java
// EmbeddedId cho composite PK
@Embeddable
public class NguoiDungVaiTroId implements Serializable {
    @Column(name = "MaNguoiDung", length = 20)
    private String maNguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "VaiTro")
    private VaiTro vaiTro;
    // equals() va hashCode() BAT BUOC
}

@Entity
@Table(name = "NguoiDung_VaiTro")
public class NguoiDungVaiTro {

    @EmbeddedId
    private NguoiDungVaiTroId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maNguoiDung")
    @JoinColumn(name = "MaNguoiDung")
    private NguoiDung nguoiDung;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
```

---

## 3. HocKyNamHoc.java

```java
@Entity
@Table(name = "HocKyNamHoc")
public class HocKyNamHoc {

    @Id
    @Column(name = "MaHocKy", length = 20)
    private String maHocKy;

    @Column(name = "TenHocKy", length = 50, nullable = false)
    private String tenHocKy;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiHocKy trangThai = TrangThaiHocKy.SapDienRa;

    // Helper method
    public LocalDate getDeadlineDeCuong() {
        return this.ngayBatDau.plusDays(14);
    }
}
```

---

## 4. DoanhNghiep.java

```java
@Entity
@Table(name = "DoanhNghiep")
public class DoanhNghiep {

    @Id
    @Column(name = "MaDoanhNghiep", length = 20)
    private String maDoanhNghiep;

    @Column(name = "TenDoanhNghiep", length = 200, nullable = false)
    private String tenDoanhNghiep;

    @Column(name = "LinhVuc", length = 200)
    private String linhVuc;

    @Column(name = "NguoiDaiDien", length = 100)
    private String nguoiDaiDien;

    @Column(name = "Email", length = 100)
    private String email;

    @Column(name = "SoDienThoai", length = 15)
    private String soDienThoai;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiDoanhNghiep trangThai = TrangThaiDoanhNghiep.DangHopTac;
}
```

---

## 5. ChuongTrinhDaoTao.java

```java
@Entity
@Table(name = "ChuongTrinhDaoTao")
public class ChuongTrinhDaoTao {

    @Id
    @Column(name = "MaCTDT", length = 20)
    private String maCTDT;

    @Column(name = "TenCTDT", length = 200, nullable = false)
    private String tenCTDT;

    @Column(name = "Khoa", length = 20)
    private String khoa;

    @Column(name = "FileWord", length = 255)
    private String fileWord;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiCTDT trangThai = TrangThaiCTDT.BanNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiTao", nullable = false)
    private NguoiDung nguoiTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet", nullable = true)
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;
}
```

---

## 6. HocPhan.java

```java
@Entity
@Table(name = "HocPhan")
public class HocPhan {

    @Id
    @Column(name = "MaHocPhan", length = 20)
    private String maHocPhan;

    @Column(name = "TenHocPhan", length = 200, nullable = false)
    private String tenHocPhan;

    @Column(name = "SoTinChi", nullable = false)
    private int soTinChi;  // CHECK 2-6 trong DB

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ChuNhiemHP", nullable = false)
    private NguoiDung chuNhiemHP;

    @Column(name = "FileDeCuong", length = 255)
    private String fileDeCuong;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiHocPhan trangThai = TrangThaiHocPhan.BanNhap;

    // Relation: Doi ngu giang vien
    @OneToMany(mappedBy = "hocPhan", fetch = FetchType.LAZY)
    private List<DoiNguGiangVienHP> doiNguGiangVien = new ArrayList<>();
}
```

---

## 7. DoiNguGiangVienHP.java (Composite PK)

```java
@Embeddable
public class DoiNguGiangVienHPId implements Serializable {
    @Column(name = "MaHocPhan", length = 20)
    private String maHocPhan;

    @Column(name = "MaGiangVien", length = 20)
    private String maGiangVien;
    // equals() va hashCode() BAT BUOC
}

@Entity
@Table(name = "DoiNguGiangVienHP")
public class DoiNguGiangVienHP {

    @EmbeddedId
    private DoiNguGiangVienHPId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maHocPhan")
    @JoinColumn(name = "MaHocPhan")
    private HocPhan hocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maGiangVien")
    @JoinColumn(name = "MaGiangVien")
    private NguoiDung giangVien;

    @Column(name = "TrangThai", columnDefinition = "BIT DEFAULT 1")
    private boolean trangThai = true;
}
```

---

## 8. LopHanhChinh.java

```java
@Entity
@Table(name = "LopHanhChinh")
public class LopHanhChinh {

    @Id
    @Column(name = "MaLopHC", length = 20)
    private String maLopHC;

    @Column(name = "TenLop", length = 100, nullable = false)
    private String tenLop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCTDT", nullable = true)
    private ChuongTrinhDaoTao chuongTrinhDaoTao;

    @Column(name = "KhoaHoc", length = 20)
    private String khoaHoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaCoVan", nullable = true)
    private NguoiDung coVan;
}
```

---

## 9. LopHocPhan.java

```java
@Entity
@Table(name = "LopHocPhan")
public class LopHocPhan {

    @Id
    @Column(name = "MaLopHP", length = 20)
    private String maLopHP;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocPhan", nullable = false)
    private HocPhan hocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocKy", nullable = false)
    private HocKyNamHoc hocKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiangVien", nullable = true)
    private NguoiDung giangVien;  // nullable - chua gan GV

    @Column(name = "NhomLop")
    private Integer nhomLop;

    @Column(name = "SiSoToiDa", nullable = false)
    private int siSoToiDa;  // CHECK 30-60 trong DB

    @Column(name = "SiSoThucTe", columnDefinition = "INT DEFAULT 0")
    private int siSoThucTe = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiLopHP trangThai = TrangThaiLopHP.DangMo;
}
```

---

## 10. TaiLieuMonHoc.java

```java
@Entity
@Table(name = "TaiLieuMonHoc",
    uniqueConstraints = @UniqueConstraint(columnNames = {"MaLopHP", "Loai"}))
public class TaiLieuMonHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaTaiLieu")
    private Integer maTaiLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHP", nullable = false)
    private LopHocPhan lopHocPhan;

    @Enumerated(EnumType.STRING)
    @Column(name = "Loai", nullable = false)
    private LoaiTaiLieu loai;

    @Column(name = "FileDinhKem", length = 255, nullable = false)
    private String fileDinhKem;

    @Column(name = "NgayNop")
    private LocalDateTime ngayNop;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiTaiLieu trangThai = TrangThaiTaiLieu.ChoDuyet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet", nullable = true)
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @Column(name = "NhanXet", columnDefinition = "TEXT")
    private String nhanXet;
}
```

---

## 11. DanhGiaVaCanhBao.java

```java
@Entity
@Table(name = "DanhGiaVaCanhBao")
public class DanhGiaVaCanhBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDanhGia")
    private Integer maDanhGia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSV", nullable = false)
    private NguoiDung sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHP", nullable = false)
    private LopHocPhan lopHocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiNhanXet", nullable = false)
    private NguoiDung nguoiNhanXet;

    @Enumerated(EnumType.STRING)
    @Column(name = "LoaiNhanXet", nullable = false)
    private LoaiNhanXet loaiNhanXet;

    @Column(name = "NoiDung", columnDefinition = "TEXT", nullable = false)
    private String noiDung;

    @Column(name = "DaXuLy", columnDefinition = "BIT DEFAULT 0")
    private boolean daXuLy = false;

    @Column(name = "KetQuaXuLy", columnDefinition = "TEXT")
    private String ketQuaXuLy;
}
```

---

## 12. DotKienTap.java

```java
@Entity
@Table(name = "DotKienTap")
public class DotKienTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDotKT")
    private Integer maDotKT;

    @Column(name = "TenDotKT", length = 200, nullable = false)
    private String tenDotKT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaLopHC", nullable = false)
    private LopHanhChinh lopHanhChinh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocKy", nullable = false)
    private HocKyNamHoc hocKy;

    @Column(name = "ThoiGian")
    private LocalDate thoiGian;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGVPhuTrach", nullable = true)
    private NguoiDung gvPhuTrach;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep", nullable = false)
    private DoanhNghiep doanhNghiep;

    @Column(name = "NhanXetGV", columnDefinition = "TEXT")
    private String nhanXetGV;

    @Column(name = "NhanXetDN", columnDefinition = "TEXT")
    private String nhanXetDN;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiDotKT trangThai = TrangThaiDotKT.ChuanBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet", nullable = true)
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;
}
```

---

## 13. DotThucTap.java

```java
@Entity
@Table(name = "DotThucTap")
public class DotThucTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaDotTT")
    private Integer maDotTT;

    @Column(name = "TenDotTT", length = 200, nullable = false)
    private String tenDotTT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaHocKy", nullable = false)
    private HocKyNamHoc hocKy;

    @Column(name = "NgayBatDau")
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc")
    private LocalDate ngayKetThuc;  // CHECK ngayKetThuc >= ngayBatDau

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiDotTT trangThai = TrangThaiDotTT.ChuanBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "NguoiDuyet", nullable = true)
    private NguoiDung nguoiDuyet;

    @Column(name = "NgayDuyet")
    private LocalDateTime ngayDuyet;

    @OneToMany(mappedBy = "dotThucTap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhanCongThucTap> danhSachPhanCong = new ArrayList<>();
}
```

---

## 14. PhanCongThucTap.java

```java
@Entity
@Table(name = "PhanCongThucTap",
    uniqueConstraints = @UniqueConstraint(columnNames = {"MaDotTT", "MaSV"}))
public class PhanCongThucTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaThucTap")
    private Integer maThucTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDotTT", nullable = false)
    private DotThucTap dotThucTap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaSV", nullable = false)
    private NguoiDung sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaDoanhNghiep", nullable = false)
    private DoanhNghiep doanhNghiep;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MaGiangVienGiamSat", nullable = true)
    private NguoiDung giangVienGiamSat;

    @Column(name = "DiemDN", precision = 4, scale = 2)
    private BigDecimal diemDN;  // CHECK 0-10

    @Column(name = "NhanXetDN", columnDefinition = "TEXT")
    private String nhanXetDN;

    @Column(name = "DiemGV", precision = 4, scale = 2)
    private BigDecimal diemGV;  // CHECK 0-10

    @Column(name = "NhanXetGV", columnDefinition = "TEXT")
    private String nhanXetGV;

    @Column(name = "NhanXetSV", columnDefinition = "TEXT")
    private String nhanXetSV;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai")
    private TrangThaiPhanCong trangThai = TrangThaiPhanCong.DaPhanCong;
}
```

---

## CHECKLIST ENTITY

```
[ ] Tat ca 14 entity da tao
[ ] 2 Composite PK (NguoiDungVaiTro, DoiNguGiangVienHP) dung @EmbeddedId
[ ] 2 AUTO_INCREMENT PK (TaiLieuMonHoc, DanhGiaVaCanhBao...) dung @GeneratedValue(IDENTITY)
[ ] Tat ca ENUM field dung @Enumerated(EnumType.STRING)
[ ] FetchType.LAZY cho tat ca @ManyToOne (tranh N+1)
[ ] @CreationTimestamp va @UpdateTimestamp cho created_at/updated_at
[ ] equals() va hashCode() cho @Embeddable classes
[ ] Khong co circular @ToString (Lombok) giua cac entity
```
