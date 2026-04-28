package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.NhapNhanXetDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.entity.SinhVien;
import com.ntu.quanlyctdtdb.repository.DanhSachSvLopHocPhanRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.repository.LopHocPhanRepository;
import com.ntu.quanlyctdtdb.service.DanhGiaService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Phase 4 — Danh Gia & Canh Bao Sinh Vien.
 *
 * <p>Three roles, three views (xem docs/05_UI_DESIGN_SYSTEM §13.6):</p>
 * <ul>
 *   <li><b>GV</b>: GET {@code /danh-gia} -> danh sach lop dang day +
 *       so SV canh bao moi lop. Click 1 lop -> {@code /danh-gia/lop} de
 *       nhap nhan xet va danh dau canh bao tung SV.</li>
 *   <li><b>CVHT</b>: GET {@code /danh-gia/canh-bao} -> danh sach SV
 *       canh bao trong cac lop hanh chinh minh phu trach (filter by
 *       coVan.maGV). POST {@code /danh-gia/canh-bao/xu-ly} de luu KetQuaXuLy.</li>
 *   <li><b>SV</b>: GET {@code /danh-gia} -> danh sach nhan xet/canh bao
 *       ve minh trong tat ca cac HK.</li>
 * </ul>
 *
 * <p>Class-level @PreAuthorize la ngam dinh; chi tiet quyen ghi (POST)
 * dat o method-level. URL pattern da duoc cau hinh trong
 * {@code SecurityConfig} (Phase 4 outer gate).</p>
 */
@Controller
@RequestMapping("/danh-gia")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('GIANG_VIEN','CVHT','SINH_VIEN','PDT','ADMIN')")
public class DanhGiaController {

    private final DanhGiaService danhGiaService;
    private final HocPhanRepository hocPhanRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;
    private final DanhSachSvLopHocPhanRepository dssvRepo;
    // Phase 7 — Bug fix: load HocKy de hien thi friendly name (truoc day chi
    // hien ma "HK1-2024" -> user nham la khong dung HK).
    private final HocKyNamHocRepository hocKyRepo;
    // Phase 7 — Bug fix: kiem tra GV thuc su la giao vien cua lop khi vao
    // GET /danh-gia/lop. Truoc day URL tay co the truy cap lop khong phai
    // cua minh (path traversal).
    private final LopHocPhanRepository lopHocPhanRepo;

    @ModelAttribute("activeMenu")
    public String activeMenu() { return "danh-gia"; }

    // ==================================================================
    // INDEX — branch theo role.
    // ==================================================================

    @GetMapping
    public String index(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        String maNguoiDung = ud != null ? ud.getMaNguoiDung() : null;
        boolean isGV = ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> "ROLE_GIANG_VIEN".equals(a.getAuthority()));
        boolean isSV = ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SINH_VIEN".equals(a.getAuthority()));

        // Phase 7 fix: Bug "danh gia chua dung voi hoc ky lop hoc phan".
        // Truoc day template chi hien lhp.id.maHocKy (vi du "HK1-2024") khien
        // user khong nhan ra dung HK do. Load full HocKyNamHoc va build map
        // de template hien th friendly name "HK1 nam hoc 2024-2025".
        Map<String, HocKyNamHoc> hocKyMap = hocKyRepo.findAll().stream()
                .collect(Collectors.toMap(HocKyNamHoc::getMaHocKy, Function.identity()));

        if (isGV) {
            // Lookup MaGV tu MaNguoiDung (NguoiDung 1-1 GiangVien).
            GiangVien gv = giangVienRepo.findByNguoiDung_MaNguoiDung(maNguoiDung).orElse(null);
            String maGV = gv != null ? gv.getMaGV() : null;
            List<LopHocPhan> lopList = danhGiaService.findLopHpCuaGv(maGV);
            // Map maHocPhan -> HocPhan de template hien thi tenHocPhan
            // (LopHocPhan khong @ManyToOne HocPhan vi EmbeddedId duplicate column).
            Map<String, HocPhan> hocPhanMap = hocPhanRepo.findAll().stream()
                    .collect(Collectors.toMap(HocPhan::getMaHocPhan, Function.identity()));
            // Map LopHocPhanId -> so SV canh bao
            Map<String, Long> canhBaoMap = new HashMap<>();
            for (LopHocPhan lhp : lopList) {
                canhBaoMap.put(keyOf(lhp.getId()), danhGiaService.demSoCanhBao(lhp.getId()));
            }
            model.addAttribute("vaiTro", "GV");
            model.addAttribute("lopList", lopList);
            model.addAttribute("hocPhanMap", hocPhanMap);
            model.addAttribute("hocKyMap", hocKyMap);
            model.addAttribute("canhBaoMap", canhBaoMap);
            return "danh-gia/index";
        }

        if (isSV) {
            // Lookup MaSV tu MaNguoiDung.
            SinhVien sv = sinhVienRepo.findByNguoiDung_MaNguoiDung(maNguoiDung).orElse(null);
            String maSV = sv != null ? sv.getMaSV() : null;
            List<DanhSachSvLopHocPhan> nhanXetList = danhGiaService.findNhanXetCuaSv(maSV);
            Map<String, HocPhan> hocPhanMap = hocPhanRepo.findAll().stream()
                    .collect(Collectors.toMap(HocPhan::getMaHocPhan, Function.identity()));
            model.addAttribute("vaiTro", "SV");
            model.addAttribute("sinhVien", sv);
            model.addAttribute("nhanXetList", nhanXetList);
            model.addAttribute("hocPhanMap", hocPhanMap);
            model.addAttribute("hocKyMap", hocKyMap);
            return "danh-gia/index";
        }

        // CVHT / PDT / ADMIN: dieu huong sang man hinh canh bao.
        return "redirect:/danh-gia/canh-bao";
    }

    // ==================================================================
    // GV — Nhap nhan xet cho 1 lop HP.
    // ==================================================================

    @PreAuthorize("hasAnyRole('GIANG_VIEN','ADMIN')")
    @GetMapping("/lop")
    public String lopForm(@RequestParam String maCTDT,
                           @RequestParam String maHocPhan,
                           @RequestParam String maHocKy,
                           @RequestParam Integer maLop,
                           @AuthenticationPrincipal CustomUserDetails ud,
                           Model model) {
        LopHocPhanId id = new LopHocPhanId(maCTDT, maHocPhan, maHocKy, maLop);

        // Phase 7 — Ownership guard: GV chi duoc nhap nhan xet cho lop minh
        // duoc phan cong day. ADMIN bypass de ho tro support.
        boolean isAdmin = ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin) {
            LopHocPhan lhp = lopHocPhanRepo
                    .findByIdFetchGv(maCTDT, maHocPhan, maHocKy, maLop).orElse(null);
            if (lhp == null) {
                throw new AccessDeniedException(
                        "Khong tim thay lop hoc phan nay.");
            }
            GiangVien gvHienTai = giangVienRepo
                    .findByNguoiDung_MaNguoiDung(ud.getMaNguoiDung()).orElse(null);
            String maGvHienTai = gvHienTai != null ? gvHienTai.getMaGV() : null;
            String maGvCuaLop = lhp.getGiangVien() != null
                    ? lhp.getGiangVien().getMaGV() : null;
            if (maGvHienTai == null || !maGvHienTai.equals(maGvCuaLop)) {
                throw new AccessDeniedException(
                        "Ban khong phai la giang vien cua lop hoc phan nay.");
            }
        }

        List<DanhSachSvLopHocPhan> danhSachSV = danhGiaService.findDanhSachSvTrongLop(id);
        HocPhan hp = hocPhanRepo.findById(maHocPhan).orElse(null);
        // Phase 7 — load HocKy de hien friendly ten ("HK1 2024-2025") trong hero.
        HocKyNamHoc hocKy = hocKyRepo.findById(maHocKy).orElse(null);

        long soCanhBao = danhSachSV.stream()
                .filter(d -> Boolean.TRUE.equals(d.getDaCanhBao()))
                .count();

        model.addAttribute("lopId", id);
        model.addAttribute("hocPhan", hp);
        model.addAttribute("hocKy", hocKy);
        model.addAttribute("danhSachSV", danhSachSV);
        model.addAttribute("soCanhBao", soCanhBao);
        return "danh-gia/nhan-xet";
    }

    @PreAuthorize("hasAnyRole('GIANG_VIEN','ADMIN')")
    @PostMapping("/lop/nhan-xet")
    public String luuNhanXet(@ModelAttribute NhapNhanXetDTO form,
                              @AuthenticationPrincipal CustomUserDetails ud,
                              RedirectAttributes ra) {
        // Phase 7 — Ownership guard tren write path. URL-level chi check role,
        // can xac minh GV current chinh la giang vien cua lop. Khong dua chi
        // vao GET guard vi user co the goi POST truc tiep (curl/postman).
        try {
            boolean isAdmin = ud != null && ud.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (!isAdmin) {
                LopHocPhan lhp = lopHocPhanRepo.findByIdFetchGv(
                        form.getMaCTDT(), form.getMaHocPhan(),
                        form.getMaHocKy(), form.getMaLop()).orElseThrow(() ->
                        new AccessDeniedException("Lop hoc phan khong ton tai."));
                GiangVien gv = giangVienRepo
                        .findByNguoiDung_MaNguoiDung(ud.getMaNguoiDung()).orElse(null);
                String maGvCuaLop = lhp.getGiangVien() != null
                        ? lhp.getGiangVien().getMaGV() : null;
                if (gv == null || !gv.getMaGV().equals(maGvCuaLop)) {
                    throw new AccessDeniedException(
                            "Ban khong phai la giang vien cua lop hoc phan nay.");
                }
            }
            danhGiaService.nhapNhanXet(form);
            ra.addFlashAttribute("successMsg",
                    "Da luu nhan xet cho SV " + form.getMaSV()
                    + (Boolean.TRUE.equals(form.getDaCanhBao())
                        ? " va gui canh bao den CVHT." : "."));
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/danh-gia/lop?maCTDT=" + form.getMaCTDT()
                + "&maHocPhan=" + form.getMaHocPhan()
                + "&maHocKy=" + form.getMaHocKy()
                + "&maLop=" + form.getMaLop();
    }

    // ==================================================================
    // CVHT — Xu ly canh bao.
    // ==================================================================

    @PreAuthorize("hasAnyRole('CVHT','PDT','ADMIN')")
    @GetMapping("/canh-bao")
    public String canhBao(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        boolean isCvht = ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> "ROLE_CVHT".equals(a.getAuthority()));
        boolean isPdtOrAdmin = ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> "ROLE_PDT".equals(a.getAuthority())
                            || "ROLE_ADMIN".equals(a.getAuthority()));

        List<DanhSachSvLopHocPhan> canhBaoList;
        String filterMode;
        if (isCvht && !isPdtOrAdmin) {
            // CVHT chi thay canh bao cua lop minh phu trach (coVan.maGV = current).
            GiangVien gv = giangVienRepo.findByNguoiDung_MaNguoiDung(ud.getMaNguoiDung())
                    .orElse(null);
            String maGV = gv != null ? gv.getMaGV() : null;
            canhBaoList = danhGiaService.findCanhBaoChoCvht(maGV);
            filterMode = "CVHT";
        } else {
            // PDT / ADMIN thay tat ca.
            canhBaoList = danhGiaService.findCanhBaoTatCa();
            filterMode = "TAT_CA";
        }

        // Map maHocPhan -> HocPhan de template hien thi tenHocPhan
        Map<String, HocPhan> hocPhanMap = hocPhanRepo.findAll().stream()
                .collect(Collectors.toMap(HocPhan::getMaHocPhan, Function.identity()));
        // Phase 7 — Map maHocKy -> HocKyNamHoc de hien friendly name.
        Map<String, HocKyNamHoc> hocKyMap = hocKyRepo.findAll().stream()
                .collect(Collectors.toMap(HocKyNamHoc::getMaHocKy, Function.identity()));

        long soChuaXuLy = canhBaoList.stream()
                .filter(d -> d.getKetQuaXuLy() == null || d.getKetQuaXuLy().isBlank())
                .count();

        model.addAttribute("canhBaoList", canhBaoList);
        model.addAttribute("hocPhanMap", hocPhanMap);
        model.addAttribute("hocKyMap", hocKyMap);
        model.addAttribute("filterMode", filterMode);
        model.addAttribute("soChuaXuLy", soChuaXuLy);
        model.addAttribute("soDaXuLy", canhBaoList.size() - soChuaXuLy);
        // Override activeMenu de sidebar highlight dung menu "Canh Bao" (khong
        // bi trung voi menu "Danh Gia Sinh Vien" / "Nhan Xet Cua Toi").
        model.addAttribute("activeMenu", "danh-gia-canh-bao");
        return "danh-gia/canh-bao";
    }

    @PreAuthorize("hasAnyRole('CVHT','PDT','ADMIN')")
    @PostMapping("/canh-bao/xu-ly")
    public String xuLyCanhBao(@RequestParam String maCTDT,
                                @RequestParam String maHocPhan,
                                @RequestParam String maHocKy,
                                @RequestParam Integer maLop,
                                @RequestParam String maSV,
                                @RequestParam String ketQuaXuLy,
                                @AuthenticationPrincipal CustomUserDetails ud,
                                RedirectAttributes ra) {
        // Phase 7 — Ownership guard cho CVHT: chi xu ly canh bao SV thuoc lop
        // hanh chinh do minh phu trach. PDT/ADMIN bypass de giam sat toan he.
        try {
            boolean isPdtOrAdmin = ud != null && ud.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_PDT".equals(a.getAuthority())
                                || "ROLE_ADMIN".equals(a.getAuthority()));
            if (!isPdtOrAdmin) {
                SinhVien sv = sinhVienRepo.findByIdFetchCoVan(maSV).orElseThrow(() ->
                        new AccessDeniedException("Khong tim thay sinh vien."));
                String maCoVan = sv.getLopHanhChinh() != null
                        && sv.getLopHanhChinh().getCoVan() != null
                        ? sv.getLopHanhChinh().getCoVan().getMaGV() : null;
                GiangVien gv = giangVienRepo
                        .findByNguoiDung_MaNguoiDung(ud.getMaNguoiDung()).orElse(null);
                if (gv == null || maCoVan == null
                        || !gv.getMaGV().equals(maCoVan)) {
                    throw new AccessDeniedException(
                            "Ban khong phai co van hoc tap cua lop sinh vien nay.");
                }
            }
            danhGiaService.xuLyCanhBao(maCTDT, maHocPhan, maHocKy, maLop, maSV, ketQuaXuLy);
            ra.addFlashAttribute("successMsg",
                    "Da luu ket qua xu ly cho SV " + maSV + ".");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/danh-gia/canh-bao";
    }

    // ==================================================================
    // HELPERS
    // ==================================================================

    private static String keyOf(LopHocPhanId id) {
        return id.getMaCTDT() + "|" + id.getMaHocPhan() + "|"
             + id.getMaHocKy() + "|" + id.getMaLopHocPhan();
    }
}
