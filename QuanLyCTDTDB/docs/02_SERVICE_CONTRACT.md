# SERVICE CONTRACT - Dinh nghia Interface truoc khi code
# Moi Service phai implement dung cac method nay, khong them/bot tuy tien

---

## 1. NguoiDungService

```java
public interface NguoiDungService {

    // --- CRUD ---
    NguoiDungDTO findById(String maNguoiDung);
    Page<NguoiDungDTO> findAll(String keyword, VaiTro vaiTro, Pageable pageable);
    NguoiDungDTO create(NguoiDungCreateDTO dto);
    NguoiDungDTO update(String maNguoiDung, NguoiDungUpdateDTO dto);
    void toggleTrangThai(String maNguoiDung);  // khoa/mo khoa

    // --- Role management ---
    void ganVaiTro(String maNguoiDung, VaiTro vaiTro);
    void xoaVaiTro(String maNguoiDung, VaiTro vaiTro);
    List<VaiTro> getVaiTros(String maNguoiDung);

    // --- Import Excel ---
    ImportResultDTO importFromExcel(MultipartFile file);

    // --- Queries ---
    List<NguoiDungDTO> findByVaiTro(VaiTro vaiTro);
    List<NguoiDungDTO> findSinhVienByLopHC(String maLopHC);
    String findMaCVHTByMaSV(String maSV);  // Dung trong DanhGia service

    // --- Security ---
    // Implement UserDetailsService.loadUserByUsername() trong impl
}
```

**Excel import columns (thu tu):**
```
Col A: MaNguoiDung  | Col B: HoTen     | Col C: Email
Col D: TenDangNhap  | Col E: VaiTro    | Col F: MaLopHC (neu la SV)
Col G: HocHam       | Col H: HocVi     | Col I: ChuyenNganh
```

---

## 2. DoanhNghiepService

```java
public interface DoanhNghiepService {

    DoanhNghiepDTO findById(String maDoanhNghiep);
    Page<DoanhNghiepDTO> findAll(String keyword, TrangThaiDoanhNghiep trangThai, Pageable pageable);
    DoanhNghiepDTO create(DoanhNghiepDTO dto);
    DoanhNghiepDTO update(String maDoanhNghiep, DoanhNghiepDTO dto);
    void toggleTrangThai(String maDoanhNghiep);

    // Khi tao DN moi, tu dong tao tai khoan NguoiDung voi role DN
    // MaNguoiDung = maDoanhNghiep + "_ACC"
    // TenDangNhap = "dn_" + maDoanhNghiep.toLowerCase()
    // MatKhau mac dinh = "DN@" + maDoanhNghiep (se hash BCrypt)
    void taoTaiKhoanDN(DoanhNghiep doanhNghiep);

    List<DoanhNghiepDTO> findAllActive();  // TrangThai = DangHopTac
}
```

---

## 3. HocPhanService

```java
public interface HocPhanService {

    HocPhanDTO findById(String maHocPhan);
    Page<HocPhanDTO> findAll(String keyword, TrangThaiHocPhan trangThai, Pageable pageable);
    HocPhanDTO create(HocPhanDTO dto);
    HocPhanDTO update(String maHocPhan, HocPhanDTO dto);

    // Workflow
    void nopDuyet(String maHocPhan);   // BanNhap -> ChoDuyet
    void pheDuyet(String maHocPhan);   // ChoDuyet -> DaDuyet
    void tuChoi(String maHocPhan);     // ChoDuyet -> BanNhap (tra ve de sua)

    // File upload
    String uploadFileDeCuong(String maHocPhan, MultipartFile file);

    // Doi ngu GV
    void themGiangVienVaoDoiNgu(String maHocPhan, String maGiangVien);
    void xoaGiangVienKhoiDoiNgu(String maHocPhan, String maGiangVien);
    List<NguoiDungDTO> getDoiNguGiangVien(String maHocPhan);
    boolean isGiangVienTrongDoiNgu(String maHocPhan, String maGiangVien);
}
```

---

## 4. ChuongTrinhDaoTaoService

```java
public interface ChuongTrinhDaoTaoService {

    ChuongTrinhDaoTaoDTO findById(String maCTDT);
    Page<ChuongTrinhDaoTaoDTO> findAll(String keyword, TrangThaiCTDT trangThai, Pageable pageable);
    ChuongTrinhDaoTaoDTO create(ChuongTrinhDaoTaoDTO dto, String maNguoiTao);
    ChuongTrinhDaoTaoDTO update(String maCTDT, ChuongTrinhDaoTaoDTO dto);

    // Workflow
    void nopDuyet(String maCTDT);                           // BanNhap -> ChoDuyet
    // AUTO-CREATES LopHocPhan khi duyet:
    void pheDuyet(String maCTDT, String maNguoiDuyet);      // ChoDuyet -> DaDuyet
    void tuChoi(String maCTDT, String maNguoiDuyet);        // ChoDuyet -> DaHuy

    // File
    String uploadFileWord(String maCTDT, MultipartFile file);

    // LOGIC AUTO-CREATE (goi trong pheDuyet, CUNG TRANSACTION)
    // Private method:
    // void autoCreateLopHocPhan(ChuongTrinhDaoTao ctdt)
    //   -> Lay danh sach HocPhan
    //   -> Lay HocKy sap toi (SapDienRa hoac DangDienRa)
    //   -> Tao LopHocPhan: MaLopHP = maHocPhan + "-01", NhomLop=1, MaGV=null
    //   -> Save tat ca trong cung @Transactional
}
```

---

## 5. LopHocPhanService

```java
public interface LopHocPhanService {

    LopHocPhanDTO findById(String maLopHP);
    Page<LopHocPhanDTO> findAll(String maHocKy, String maHocPhan, Pageable pageable);

    // BCN gan GV cho lop HP (sau khi auto-create)
    void ganGiangVien(String maLopHP, String maGiangVien);
    // WARN (khong block) neu GV khong trong DoiNguGiangVienHP

    // Queries theo user
    List<LopHocPhanDTO> findByGiangVien(String maGiangVien);    // cho GV xem lop cua minh
    List<LopHocPhanDTO> findByHocPhan(String maHocPhan);        // cho CNHP
    Page<LopHocPhanDTO> findChuaCoDeCuong(Pageable pageable);   // cho bao cao
}
```

---

## 6. TaiLieuMonHocService

```java
public interface TaiLieuMonHocService {

    TaiLieuDTO findById(Integer maTaiLieu);
    List<TaiLieuDTO> findByLopHP(String maLopHP);

    // GV nop tai lieu (INSERT hoac UPDATE neu da co ban cu voi cung MaLopHP+Loai)
    TaiLieuDTO nopTaiLieu(String maLopHP, LoaiTaiLieu loai, MultipartFile file, String maNguoiNop);

    // CNHP duyet
    void pheDuyet(Integer maTaiLieu, String maNguoiDuyet, String nhanXet);
    void tuChoi(Integer maTaiLieu, String maNguoiDuyet, String nhanXet);

    // Kiem tra deadline (DeCuongChiTiet)
    boolean isQuaHanDeCuong(String maLopHP);
    LocalDate getDeadlineDeCuong(String maLopHP);  // = HocKy.NgayBatDau + 14

    // Queries
    List<TaiLieuDTO> findChoDuyet(String maHocPhan);    // cho CNHP
    List<LopHocPhanDTO> findLopChuaCoTaiLieu(LoaiTaiLieu loai); // cho bao cao
}
```

---

## 7. DanhGiaVaCanhBaoService

```java
public interface DanhGiaVaCanhBaoService {

    // GV nhap nhan xet
    // Side effect: neu TieuCuc -> gui email SV + CVHT (async @Async)
    DanhGiaDTO taoNhanXet(DanhGiaCreateDTO dto, String maNguoiNhanXet);

    List<DanhGiaDTO> findByLopHP(String maLopHP);
    List<DanhGiaDTO> findBySinhVien(String maSV);

    // CVHT xu ly canh bao
    List<DanhGiaDTO> findCanhBaoChuaXuLy(String maLopHC);   // cho CVHT
    void xuLyCanhBao(Integer maDanhGia, String ketQuaXuLy, String maCVHT);
    // Set DaXuLy = true, KetQuaXuLy = ..., UpdatedAt = now

    // Thong ke
    long countCanhBaoChuaXuLy(String maLopHC);
}
```

---

## 8. KienTapService

```java
public interface KienTapService {

    DotKienTapDTO findById(Integer maDotKT);
    Page<DotKienTapDTO> findAll(String maLopHC, String maHocKy, Pageable pageable);
    DotKienTapDTO create(DotKienTapCreateDTO dto);
    DotKienTapDTO update(Integer maDotKT, DotKienTapCreateDTO dto);

    // Workflow
    void nopDuyet(Integer maDotKT);
    void pheDuyet(Integer maDotKT, String maNguoiDuyet);
    void tuChoi(Integer maDotKT, String maNguoiDuyet);
    void danhDauDaThucHien(Integer maDotKT);

    // Nhan xet
    void capNhatNhanXetGV(Integer maDotKT, String nhanXet, String maGV);
    void capNhatNhanXetDN(Integer maDotKT, String nhanXet);  // DN login
}
```

---

## 9. ThucTapService

```java
public interface ThucTapService {

    // Dot thuc tap
    DotThucTapDTO findById(Integer maDotTT);
    Page<DotThucTapDTO> findAll(Pageable pageable);
    DotThucTapDTO createDot(DotThucTapDTO dto);
    void pheDuyetDot(Integer maDotTT, String maNguoiDuyet);
    void tuChoiDot(Integer maDotTT);

    // Phan cong (1 SV - 1 DN - 1 GV giam sat)
    PhanCongDTO taoMotPhanCong(PhanCongCreateDTO dto);
    ImportResultDTO importPhanCongFromExcel(Integer maDotTT, MultipartFile file);
    // Excel format: MaSV | MaDoanhNghiep | MaGiangVienGiamSat

    // Nhap diem va nhan xet
    void nhapDiemDN(Integer maPhanCong, BigDecimal diem, String nhanXet);   // DN role
    void nhapDiemGV(Integer maPhanCong, BigDecimal diem, String nhanXet);   // GV role
    void nhapNhanXetSV(Integer maPhanCong, String nhanXet);                  // SV role

    // Queries
    List<PhanCongDTO> findBySinhVien(String maSV);
    List<PhanCongDTO> findByDotThucTap(Integer maDotTT);
    List<PhanCongDTO> findByGiangVienGiamSat(String maGV);
}
```

---

## 10. BaoCaoService

```java
public interface BaoCaoService {

    // Dashboard data
    DashboardDTO getDashboardData();
    // Includes: so SV, so HP, so LopHP chua co GV, so canh bao chua xu ly, v.v.

    // Xuat Excel
    byte[] xuatBaoCaoNguoiDung();
    byte[] xuatBaoCaoLopHocPhan(String maHocKy);
    byte[] xuatBaoCaoDanhGia(String maLopHC);
    byte[] xuatBaoCaoThucTap(Integer maDotTT);
}
```

---

## 11. EmailService (Utility, khong phai Business Service)

```java
public interface EmailService {

    // Gui email canh bao (async)
    @Async
    void sendCanhBaoToSV(String maSV, String noiDungNhanXet);

    @Async
    void sendCanhBaoToCVHT(String maCVHT, String maSV, String noiDungNhanXet);

    // Gui thong bao tai lieu duoc duyet/tu choi
    @Async
    void sendTaiLieuDaDuyet(String maGV, String maLopHP, LoaiTaiLieu loai);

    @Async
    void sendTaiLieuTuChoi(String maGV, String maLopHP, LoaiTaiLieu loai, String lyDo);
}
```

---

## 12. FileStorageService (Utility)

```java
public interface FileStorageService {

    // Luu file, tra ve duong dan tuong doi
    // Thu muc: uploads/{module}/{tenFile_timestamp.ext}
    String store(MultipartFile file, String module);
    // module = "ctdt" | "hoc-phan" | "tai-lieu" | "excel-import"

    // Lay full path de download
    Resource loadAsResource(String filePath);

    // Xoa file (khi cap nhat tai lieu)
    void delete(String filePath);
}
```

---

## CONTROLLER CONVENTION (Thymeleaf)

```java
// Template cho moi Controller
@Controller
@RequestMapping("/ten-module")
@RequiredArgsConstructor
public class TenModuleController {

    private final TenModuleService service;

    // List page
    @GetMapping
    public String list(Model model,
                       @RequestParam(required = false) String keyword,
                       @PageableDefault(size = 10) Pageable pageable) {
        model.addAttribute("page", service.findAll(keyword, pageable));
        model.addAttribute("keyword", keyword);
        return "ten-module/list";
    }

    // Form tao moi
    @GetMapping("/them")
    public String showCreateForm(Model model) {
        model.addAttribute("dto", new TenModuleDTO());
        // Them cac dropdown data:
        // model.addAttribute("listGV", nguoiDungService.findByVaiTro(VaiTro.GV));
        return "ten-module/form";
    }

    // Xu ly tao moi
    @PostMapping("/them")
    public String create(@Valid @ModelAttribute TenModuleDTO dto,
                         BindingResult result,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (result.hasErrors()) {
            return "ten-module/form";
        }
        try {
            service.create(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Tao thanh cong");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ten-module";
    }

    // Xu ly action (duyet, tu choi, ...) - chi POST, redirect lai
    @PostMapping("/{id}/phe-duyet")
    public String pheDuyet(@PathVariable Integer id,
                           RedirectAttributes redirectAttributes,
                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            service.pheDuyet(id, userDetails.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Da phe duyet");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/ten-module/" + id;
    }
}
```
