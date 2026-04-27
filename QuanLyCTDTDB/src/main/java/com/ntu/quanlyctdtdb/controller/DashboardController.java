package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.entity.DanhSachSvKienTap;
import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.DanhSachThucTap;
import com.ntu.quanlyctdtdb.entity.DotKienTap;
import com.ntu.quanlyctdtdb.entity.DotThucTap;
import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.SinhVien;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotKT;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiLopHocPhan;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.DanhSachSvKienTapRepository;
import com.ntu.quanlyctdtdb.repository.DanhSachSvLopHocPhanRepository;
import com.ntu.quanlyctdtdb.repository.DanhSachThucTapRepository;
import com.ntu.quanlyctdtdb.repository.DoanhNghiepRepository;
import com.ntu.quanlyctdtdb.repository.DotKienTapRepository;
import com.ntu.quanlyctdtdb.repository.DotThucTapRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import com.ntu.quanlyctdtdb.repository.LopHocPhanRepository;
import com.ntu.quanlyctdtdb.repository.NguoiDungRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dashboard controller — Phase 6 redesign.
 *
 * <p>Trang chu hien thi widget khac nhau theo vai tro nguoi dung dang nhap.
 * Logic chinh:
 * <ul>
 *   <li>Lay role uu tien (highest privilege) tu authorities</li>
 *   <li>Theo role, fetch flat data (extract het truong can thiet vao
 *       Map/DTO truoc khi return) vi spring.jpa.open-in-view=false —
 *       template KHONG duoc cham vao lazy association.</li>
 *   <li>Dat tat ca model attribute vao mot khoi de template render thong nhat.</li>
 * </ul>
 *
 * <p>Mot vai design decision:
 * <ul>
 *   <li>Cac danh sach o widget gioi han <= 5 entry: dashboard la "tom tat",
 *       chi tiet thuoc trang chuyen biet (pending approvals, canh bao SV...).</li>
 *   <li>Stat KPI dung {@code Long} (count) — re ve performance, khong load entity.</li>
 *   <li>Chart data tra ve Map JSON-friendly de Chart.js (frontend) render.</li>
 * </ul>
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final NguoiDungRepository nguoiDungRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;
    private final HocPhanRepository hocPhanRepo;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final LopHocPhanRepository lopHocPhanRepo;
    private final LopHanhChinhRepository lopHanhChinhRepo;
    private final DotKienTapRepository dotKienTapRepo;
    private final DotThucTapRepository dotThucTapRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final DanhSachSvLopHocPhanRepository dsSvLhpRepo;
    private final DanhSachSvKienTapRepository dsSvKtRepo;
    private final DanhSachThucTapRepository dsTtRepo;

    /**
     * Phase 6: dung @Transactional de cac repo call duoc thuc thi trong cung
     * mot session — neu sau nay can lazy fetch (reset/refresh) thi van OK.
     * View rendering KHONG nam trong tx (open-in-view=false), do do mọi
     * du lieu dua vao model PHAI duoc extract ra primitive/String truoc khi
     * return tu method.
     */
    @GetMapping({"/", "/dashboard"})
    @Transactional(readOnly = true)
    public String dashboard(@AuthenticationPrincipal CustomUserDetails currentUser,
                             Model model) {
        model.addAttribute("activeMenu", "dashboard");
        model.addAttribute("currentUser", currentUser);

        // Hoc ky hien tai (chia se cho moi role) — dung try/catch defensive
        // tranh truong hop lech du lieu lam cho landing page 500.
        HocKyNamHoc hocKyHienTai = findHocKyHienTai();
        String maHocKyHienTai = hocKyHienTai != null ? hocKyHienTai.getMaHocKy() : null;
        if (hocKyHienTai != null) {
            model.addAttribute("hocKyHienTai", hocKyHienTai);
        }

        // Xac dinh role uu tien (chi 1 role active tren dashboard)
        String primaryRole = resolvePrimaryRole(currentUser);
        model.addAttribute("primaryRole", primaryRole);

        switch (primaryRole) {
            case "ADMIN", "PDT", "TTDTXS" -> buildAdminDashboard(model, maHocKyHienTai);
            case "CNHP"                   -> buildCnhpDashboard(model, currentUser);
            case "CVHT"                   -> buildCvhtDashboard(model, currentUser);
            case "GIANG_VIEN"             -> buildGiangVienDashboard(model, currentUser, maHocKyHienTai);
            case "SINH_VIEN"              -> buildSinhVienDashboard(model, currentUser, maHocKyHienTai);
            case "DOANH_NGHIEP"           -> buildDoanhNghiepDashboard(model, currentUser);
            default                       -> log.info("[Dashboard] No role-specific block for {}", primaryRole);
        }

        return "dashboard/dashboard";
    }

    // -------------------------------------------------------------------------
    // ROLE: ADMIN / PDT / TTDTXS  -- operational dashboard
    // -------------------------------------------------------------------------
    private void buildAdminDashboard(Model model, String maHocKyHienTai) {
        // KPI row
        Map<String, Long> kpi = new LinkedHashMap<>();
        kpi.put("tongNguoiDung",   nguoiDungRepo.count());
        kpi.put("giangVien",       nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.GiangVien));
        kpi.put("sinhVien",        nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.SinhVien));
        kpi.put("doanhNghiep",     (long) doanhNghiepRepo.findAll().size());
        kpi.put("hocPhanDaDuyet",  hocPhanRepo.countByTrangThai(TrangThaiHocPhan.DaDuyet));
        kpi.put("hocPhanChoDuyet", hocPhanRepo.countByTrangThai(TrangThaiHocPhan.ChoDuyet));
        kpi.put("ctdtDaDuyet",     ctdtRepo.countByTrangThai(TrangThaiCTDT.DaDuyet));
        kpi.put("ctdtChoDuyet",    ctdtRepo.countByTrangThai(TrangThaiCTDT.ChoDuyet));
        kpi.put("lopHpDangMo",     lopHocPhanRepo.countByTrangThai(TrangThaiLopHocPhan.DangMo));
        kpi.put("kienTapDangHD",   dotKienTapRepo.countByTrangThaiIn(
                List.of(TrangThaiDotKT.ChuanBi, TrangThaiDotKT.ChoDuyet, TrangThaiDotKT.DaDuyet)));
        kpi.put("thucTapDangHD",   dotThucTapRepo.countByTrangThai(TrangThaiDotTT.DangThucHien));
        model.addAttribute("kpi", kpi);

        // Pending approvals widget — top 5
        List<Map<String, String>> pendingHocPhan = hocPhanRepo
                .findByTrangThai(TrangThaiHocPhan.ChoDuyet).stream()
                .sorted(Comparator.comparing(HocPhan::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(hp -> Map.of(
                        "id",    hp.getMaHocPhan(),
                        "title", hp.getTenHocPhan(),
                        "meta",  hp.getMaHocPhan() + " · " + hp.getSoTinChi() + " TC",
                        "href",  "/hoc-phan/chi-tiet/" + hp.getMaHocPhan()))
                .collect(Collectors.toList());
        model.addAttribute("pendingHocPhan", pendingHocPhan);
        model.addAttribute("pendingHocPhanTotal", kpi.get("hocPhanChoDuyet"));

        List<Map<String, String>> pendingCtdt = ctdtRepo
                .findByTrangThai(TrangThaiCTDT.ChoDuyet).stream()
                .sorted(Comparator.comparing(ChuongTrinhDaoTao::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(5)
                .map(c -> Map.of(
                        "id",    c.getMaCTDT(),
                        "title", c.getTenCTDT(),
                        "meta",  c.getMaCTDT() + " · K" + (c.getKhoa() == null ? "--" : c.getKhoa()),
                        "href",  "/ctdt/chi-tiet/" + c.getMaCTDT()))
                .collect(Collectors.toList());
        model.addAttribute("pendingCtdt", pendingCtdt);
        model.addAttribute("pendingCtdtTotal", kpi.get("ctdtChoDuyet"));

        // Đợt KT/TT chờ duyệt
        List<Map<String, String>> pendingKienTap = dotKienTapRepo.findPendingDots().stream()
                .limit(5)
                .map(d -> {
                    String tenLop = d.getLopHanhChinh() != null
                            ? d.getLopHanhChinh().getMaLopHC() : "--";
                    String trangThai = d.getTrangThai() == null ? "--" : d.getTrangThai().name();
                    return Map.of(
                            "id",    String.valueOf(d.getMaDotKT()),
                            "title", d.getTenDotKT() == null ? "(chua dat ten)" : d.getTenDotKT(),
                            "meta",  tenLop + " · " + trangThai,
                            "href",  "/kien-tap/chi-tiet/" + d.getMaDotKT());
                })
                .collect(Collectors.toList());
        model.addAttribute("pendingKienTap", pendingKienTap);

        // Lop HP chua co GV
        List<LopHocPhan> chuaCoGv = lopHocPhanRepo.findChuaPhanCongGiangVien();
        long chuaCoGvCount = chuaCoGv.size();
        model.addAttribute("chuaCoGvCount", chuaCoGvCount);
        model.addAttribute("chuaCoGvList", chuaCoGv.stream()
                .limit(5)
                .map(lhp -> Map.of(
                        "id",    lhp.getId().getMaCTDT() + "/" + lhp.getId().getMaHocPhan()
                                + "/" + lhp.getId().getMaHocKy()
                                + "/" + lhp.getId().getMaLopHocPhan(),
                        "title", lhp.getId().getMaHocPhan() + " · Lớp " + lhp.getId().getMaLopHocPhan(),
                        "meta",  "CTĐT " + lhp.getId().getMaCTDT() + " · " + lhp.getId().getMaHocKy(),
                        "href",  "#"))
                .collect(Collectors.toList()));

        // Canh bao chua xu ly — top 5
        List<DanhSachSvLopHocPhan> canhBao = dsSvLhpRepo.findCanhBaoChuaXuLy();
        long canhBaoCount = canhBao.size();
        model.addAttribute("canhBaoChuaXuLyCount", canhBaoCount);
        model.addAttribute("canhBaoList", canhBao.stream()
                .limit(5)
                .map(d -> {
                    SinhVien sv = d.getSinhVien();
                    String hoTen = sv != null && sv.getNguoiDung() != null
                            ? sv.getNguoiDung().getHoTen()
                            : (sv != null ? sv.getMaSV() : "--");
                    String lop = sv != null && sv.getLopHanhChinh() != null
                            ? sv.getLopHanhChinh().getMaLopHC() : "--";
                    return Map.of(
                            "id",    sv != null ? sv.getMaSV() : "--",
                            "title", hoTen,
                            "meta",  d.getId().getMaHocPhan() + " · " + lop,
                            "href",  "/danh-gia/canh-bao");
                })
                .collect(Collectors.toList()));

        // Chart 1: SV cảnh báo / tổng SV (donut)
        long tongSv = nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.SinhVien);
        long svCanhBao = canhBaoCount; // chua xu ly
        Map<String, Object> chartCanhBao = new LinkedHashMap<>();
        chartCanhBao.put("labels", List.of("Cảnh báo chưa xử lý", "An toàn"));
        chartCanhBao.put("data", List.of(svCanhBao, Math.max(0L, tongSv - svCanhBao)));
        chartCanhBao.put("colors", List.of("#dc2626", "#16a34a"));
        model.addAttribute("chartCanhBao", chartCanhBao);

        // Chart 2: Lớp HP chưa có GV / có GV (bar)
        long lopHpDangMo = kpi.get("lopHpDangMo");
        long coGv = Math.max(0L, lopHpDangMo - chuaCoGvCount);
        Map<String, Object> chartLopHp = new LinkedHashMap<>();
        chartLopHp.put("labels", List.of("Đã có GV", "Chưa có GV"));
        chartLopHp.put("data", List.of(coGv, chuaCoGvCount));
        chartLopHp.put("colors", List.of("#16a34a", "#d97706"));
        model.addAttribute("chartLopHp", chartLopHp);
    }

    // -------------------------------------------------------------------------
    // ROLE: CNHP (Chu Nhiem Hoc Phan)
    // -------------------------------------------------------------------------
    private void buildCnhpDashboard(Model model, CustomUserDetails currentUser) {
        Optional<GiangVien> gvOpt = giangVienRepo
                .findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
        if (gvOpt.isEmpty()) {
            log.warn("[Dashboard] CNHP {} khong co GV record", currentUser.getUsername());
            return;
        }
        GiangVien gv = gvOpt.get();
        String maGV = gv.getMaGV();

        List<HocPhan> myHps = hocPhanRepo.findByChuNhiemHP_MaGV(maGV);
        long myHpCount = myHps.size();
        long myHpDaDuyet  = myHps.stream().filter(h -> h.getTrangThai() == TrangThaiHocPhan.DaDuyet).count();
        long myHpChoDuyet = myHps.stream().filter(h -> h.getTrangThai() == TrangThaiHocPhan.ChoDuyet).count();
        long myHpBanNhap  = myHps.stream().filter(h -> h.getTrangThai() == TrangThaiHocPhan.BanNhap).count();

        Map<String, Long> kpi = new LinkedHashMap<>();
        kpi.put("hpPhuTrach", myHpCount);
        kpi.put("hpDaDuyet",  myHpDaDuyet);
        kpi.put("hpChoDuyet", myHpChoDuyet);
        kpi.put("hpBanNhap",  myHpBanNhap);
        model.addAttribute("kpi", kpi);

        List<Map<String, String>> myHpList = myHps.stream()
                .sorted(Comparator.comparing(HocPhan::getUpdatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(6)
                .map(hp -> Map.of(
                        "id",    hp.getMaHocPhan(),
                        "title", hp.getTenHocPhan(),
                        "meta",  hp.getMaHocPhan() + " · " + hp.getSoTinChi() + " TC · "
                                + (hp.getTrangThai() == null ? "--" : hp.getTrangThai().name()),
                        "href",  "/hoc-phan/chi-tiet/" + hp.getMaHocPhan(),
                        "trangThai", hp.getTrangThai() == null ? "--" : hp.getTrangThai().name()))
                .collect(Collectors.toList());
        model.addAttribute("myHocPhanList", myHpList);
    }

    // -------------------------------------------------------------------------
    // ROLE: CVHT (Co Van Hoc Tap)
    // -------------------------------------------------------------------------
    private void buildCvhtDashboard(Model model, CustomUserDetails currentUser) {
        Optional<GiangVien> gvOpt = giangVienRepo
                .findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
        if (gvOpt.isEmpty()) {
            log.warn("[Dashboard] CVHT {} khong co GV record", currentUser.getUsername());
            return;
        }
        GiangVien gv = gvOpt.get();
        String maGV = gv.getMaGV();

        List<LopHanhChinh> myLopList = lopHanhChinhRepo.findByCoVan_MaGV(maGV);
        long tongSV = myLopList.stream()
                .mapToLong(l -> sinhVienRepo.countByLopHanhChinh_MaLopHC(l.getMaLopHC()))
                .sum();

        List<DanhSachSvLopHocPhan> canhBao = dsSvLhpRepo.findCanhBaoByCoVan(maGV);
        long canhBaoChuaXL = canhBao.stream()
                .filter(d -> d.getKetQuaXuLy() == null || d.getKetQuaXuLy().isBlank())
                .count();

        Map<String, Long> kpi = new LinkedHashMap<>();
        kpi.put("soLop",          (long) myLopList.size());
        kpi.put("tongSV",         tongSV);
        kpi.put("canhBaoTong",    (long) canhBao.size());
        kpi.put("canhBaoChuaXL",  canhBaoChuaXL);
        model.addAttribute("kpi", kpi);

        List<Map<String, String>> myLopRows = myLopList.stream()
                .map(l -> Map.of(
                        "id",    l.getMaLopHC(),
                        "title", l.getTenLop() == null ? l.getMaLopHC() : l.getTenLop(),
                        "meta",  l.getMaLopHC() + " · K" + (l.getKhoaHoc() == null ? "--" : l.getKhoaHoc())
                                + " · " + sinhVienRepo.countByLopHanhChinh_MaLopHC(l.getMaLopHC()) + " SV",
                        "href",  "/lop-hanh-chinh/chi-tiet/" + l.getMaLopHC()))
                .collect(Collectors.toList());
        model.addAttribute("myLopList", myLopRows);

        List<Map<String, String>> canhBaoRows = canhBao.stream()
                .limit(6)
                .map(d -> {
                    SinhVien sv = d.getSinhVien();
                    String hoTen = sv != null && sv.getNguoiDung() != null
                            ? sv.getNguoiDung().getHoTen()
                            : (sv != null ? sv.getMaSV() : "--");
                    String lop = sv != null && sv.getLopHanhChinh() != null
                            ? sv.getLopHanhChinh().getMaLopHC() : "--";
                    boolean daXL = d.getKetQuaXuLy() != null && !d.getKetQuaXuLy().isBlank();
                    return Map.of(
                            "id",        sv != null ? sv.getMaSV() : "--",
                            "title",     hoTen,
                            "meta",      d.getId().getMaHocPhan() + " · " + lop,
                            "trangThai", daXL ? "Đã xử lý" : "Chưa xử lý",
                            "href",      "/danh-gia/canh-bao");
                })
                .collect(Collectors.toList());
        model.addAttribute("canhBaoList", canhBaoRows);
    }

    // -------------------------------------------------------------------------
    // ROLE: GIANG_VIEN
    // -------------------------------------------------------------------------
    private void buildGiangVienDashboard(Model model, CustomUserDetails currentUser,
                                         String maHocKyHienTai) {
        Optional<GiangVien> gvOpt = giangVienRepo
                .findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
        if (gvOpt.isEmpty()) {
            log.warn("[Dashboard] GV {} khong co GV record", currentUser.getUsername());
            return;
        }
        GiangVien gv = gvOpt.get();
        String maGV = gv.getMaGV();

        List<LopHocPhan> allLopHp = lopHocPhanRepo.findByGiangVien_MaGV(maGV);
        // Tach lop hoc ky hien tai vs khac
        List<LopHocPhan> currentTermLopHp = allLopHp.stream()
                .filter(lhp -> maHocKyHienTai != null
                        && maHocKyHienTai.equals(lhp.getId().getMaHocKy()))
                .toList();

        Map<String, Long> kpi = new LinkedHashMap<>();
        kpi.put("lopHocKyNay",  (long) currentTermLopHp.size());
        kpi.put("tongLop",      (long) allLopHp.size());
        kpi.put("dangMo",       allLopHp.stream()
                .filter(l -> l.getTrangThai() == TrangThaiLopHocPhan.DangMo).count());
        model.addAttribute("kpi", kpi);

        List<Map<String, String>> myLopList = (currentTermLopHp.isEmpty() ? allLopHp : currentTermLopHp).stream()
                .limit(6)
                .map(lhp -> Map.of(
                        "id",    lhp.getId().getMaCTDT() + "/" + lhp.getId().getMaHocPhan()
                                + "/" + lhp.getId().getMaHocKy()
                                + "/" + lhp.getId().getMaLopHocPhan(),
                        "title", lhp.getId().getMaHocPhan() + " · Lớp " + lhp.getId().getMaLopHocPhan(),
                        "meta",  "CTĐT " + lhp.getId().getMaCTDT() + " · " + lhp.getId().getMaHocKy()
                                + " · " + (lhp.getTrangThai() == null ? "--" : lhp.getTrangThai().name()),
                        "href",  "/lop-hoc-phan"))
                .collect(Collectors.toList());
        model.addAttribute("myLopList", myLopList);

        // Đợt kien tap minh phu trach
        List<DotKienTap> myKt = dotKienTapRepo.findAllFetchAll().stream()
                .filter(d -> d.getGvPhuTrach() != null
                        && maGV.equals(d.getGvPhuTrach().getMaGV()))
                .toList();
        List<Map<String, String>> myKtList = myKt.stream()
                .limit(5)
                .map(d -> Map.of(
                        "id",    String.valueOf(d.getMaDotKT()),
                        "title", d.getTenDotKT() == null ? "(chua dat ten)" : d.getTenDotKT(),
                        "meta",  (d.getLopHanhChinh() != null ? d.getLopHanhChinh().getMaLopHC() : "--")
                                + " · "
                                + (d.getTrangThai() == null ? "--" : d.getTrangThai().name()),
                        "href",  "/kien-tap/chi-tiet/" + d.getMaDotKT()))
                .collect(Collectors.toList());
        model.addAttribute("myKtList", myKtList);
        kpi.put("ktPhuTrach", (long) myKt.size());
    }

    // -------------------------------------------------------------------------
    // ROLE: SINH_VIEN
    // -------------------------------------------------------------------------
    private void buildSinhVienDashboard(Model model, CustomUserDetails currentUser,
                                        String maHocKyHienTai) {
        Optional<SinhVien> svOpt = sinhVienRepo
                .findByNguoiDung_MaNguoiDung(currentUser.getMaNguoiDung());
        if (svOpt.isEmpty()) {
            log.warn("[Dashboard] SV {} khong co SV record", currentUser.getUsername());
            return;
        }
        SinhVien sv = svOpt.get();
        String maSV = sv.getMaSV();

        List<DanhSachSvLopHocPhan> myLhp = dsSvLhpRepo.findById_MaSVFetch(maSV);
        List<DanhSachSvLopHocPhan> currentTermLhp = myLhp.stream()
                .filter(d -> maHocKyHienTai != null
                        && maHocKyHienTai.equals(d.getId().getMaHocKy()))
                .toList();

        Map<String, Long> kpi = new LinkedHashMap<>();
        kpi.put("hpKyNay",     (long) currentTermLhp.size());
        kpi.put("tongHpDaHoc", (long) myLhp.size());
        long canhBaoCount = myLhp.stream()
                .filter(d -> Boolean.TRUE.equals(d.getDaCanhBao())).count();
        kpi.put("soCanhBao", canhBaoCount);

        // Kien tap & Thuc tap cua SV
        List<DanhSachSvKienTap> myKt = dsSvKtRepo.findBySinhVien_MaSV(maSV);
        List<DanhSachThucTap>   myTt = dsTtRepo.findBySinhVien_MaSV(maSV);
        kpi.put("kienTap", (long) myKt.size());
        kpi.put("thucTap", (long) myTt.size());
        model.addAttribute("kpi", kpi);

        List<Map<String, String>> myLhpList = (currentTermLhp.isEmpty() ? myLhp : currentTermLhp).stream()
                .limit(6)
                .map(d -> Map.of(
                        "id",    d.getId().getMaHocPhan(),
                        "title", d.getId().getMaHocPhan() + " · Lớp " + d.getId().getMaLopHocPhan(),
                        "meta",  "Học kỳ " + d.getId().getMaHocKy()
                                + (Boolean.TRUE.equals(d.getDaCanhBao()) ? " · ⚠ Cảnh báo" : ""),
                        "href",  "/profile"))
                .collect(Collectors.toList());
        model.addAttribute("myLhpList", myLhpList);

        // Sinh vien thay
        Map<String, String> profile = new LinkedHashMap<>();
        profile.put("hoTen", sv.getNguoiDung() != null ? sv.getNguoiDung().getHoTen() : "--");
        profile.put("maSV", maSV);
        profile.put("lop", sv.getLopHanhChinh() != null ? sv.getLopHanhChinh().getMaLopHC() : "--");
        profile.put("trangThai", sv.getTrangThaiSV() == null ? "--" : sv.getTrangThaiSV().name());
        model.addAttribute("profile", profile);
    }

    // -------------------------------------------------------------------------
    // ROLE: DOANH_NGHIEP
    // -------------------------------------------------------------------------
    private void buildDoanhNghiepDashboard(Model model, CustomUserDetails currentUser) {
        // SV doanh nghiep dang phu trach kien tap / thuc tap
        // Don gian: dem theo maNguoiDung -> tim doanh nghiep tuong ung
        // Truong hop he thong cho phep 1 NguoiDung-> 1 DoanhNghiep, neu khong
        // co relationship thi de thong ke = 0.
        // (Tuong lai mo rong khi co bang lien ket NguoiDung-DoanhNghiep.)
        Map<String, Long> kpi = new LinkedHashMap<>();
        kpi.put("tongKienTap", (long) dotKienTapRepo.findAllFetchAll().size());
        kpi.put("tongThucTap", (long) dotThucTapRepo.findAllFetchAll().size());
        model.addAttribute("kpi", kpi);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    /**
     * Tra ve role uu tien (cao nhat) cua user.
     * Thu tu: ADMIN > PDT > TTDTXS > CNHP > CVHT > GIANG_VIEN > SINH_VIEN > DOANH_NGHIEP.
     */
    private String resolvePrimaryRole(CustomUserDetails u) {
        if (u == null || u.getAuthorities() == null) return "GUEST";
        Set<String> roles = u.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        List<String> priority = List.of(
                "ROLE_ADMIN", "ROLE_PDT", "ROLE_TTDTXS",
                "ROLE_CNHP", "ROLE_CVHT",
                "ROLE_GIANG_VIEN", "ROLE_SINH_VIEN", "ROLE_DOANH_NGHIEP");
        for (String r : priority) {
            if (roles.contains(r)) return r.substring("ROLE_".length());
        }
        return "GUEST";
    }

    private HocKyNamHoc findHocKyHienTai() {
        try {
            return hocKyRepo.findByTrangThai(TrangThaiHocKy.DangDienRa).orElse(null);
        } catch (IncorrectResultSizeDataAccessException ex) {
            log.warn("[Dashboard] Phat hien >1 hoc ky DangDienRa. Lay ban ghi DangDienRa dau tien. Msg={}",
                    ex.getMessage());
            return hocKyRepo.findByTrangThaiNot(TrangThaiHocKy.DaKetThuc).stream()
                    .filter(hk -> hk.getTrangThai() == TrangThaiHocKy.DangDienRa)
                    .findFirst()
                    .orElse(null);
        }
    }
}
