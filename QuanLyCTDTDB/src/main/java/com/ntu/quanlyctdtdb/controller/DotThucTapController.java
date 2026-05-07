package com.ntu.quanlyctdtdb.controller;

import com.ntu.quanlyctdtdb.dto.DotThucTapDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachThucTap;
import com.ntu.quanlyctdtdb.entity.DotThucTap;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import com.ntu.quanlyctdtdb.enums.LoaiThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDotTT;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.DanhSachThucTapRepository;
import com.ntu.quanlyctdtdb.repository.DoanhNghiepRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.repository.NguoiDungRepository;
import com.ntu.quanlyctdtdb.security.CustomUserDetails;
import com.ntu.quanlyctdtdb.service.DotThucTapService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Controller cho DotThucTap. Quy tac day du: docs/03 §WF-08.*
 *
 * <p>Role (docs/03 §"SO DO TONG HOP QUYEN"):</p>
 * <ul>
 *   <li>PDT, TTDTXS, ADMIN: RW (tao, sua, gui duyet, them SV).</li>
 *   <li>TTDTXS, ADMIN: phe duyet rieng.</li>
 *   <li>GV, CVHT, DN: W cap-nhat-kq (cho SV minh phu trach), R chi-tiet.</li>
 *   <li>SV: R chi-tiet (dot minh tham gia).</li>
 * </ul>
 *
 * <p>Class-level @PreAuthorize chan tat ca request den /thuc-tap/** chi cho
 * cac role co tham chieu nghiep vu. Method-level fine-grained guard:</p>
 * <ul>
 *   <li>Tao/sua/gui-duyet/them-sv : PDT, TTDTXS, ADMIN.</li>
 *   <li>Phe duyet                : TTDTXS, ADMIN.</li>
 *   <li>Cap nhat ket qua          : PDT, TTDTXS, ADMIN, GIANG_VIEN, CVHT, DOANH_NGHIEP.</li>
 *   <li>Danh sach + chi tiet      : tat ca role tren + SINH_VIEN (read).</li>
 * </ul>
 */
@Controller
@RequestMapping("/thuc-tap")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN','GIANG_VIEN','CVHT','DOANH_NGHIEP','SINH_VIEN')")
public class DotThucTapController {

    private final DotThucTapService dotTTService;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final HocPhanRepository hocPhanRepo;
    private final HocKyNamHocRepository hocKyRepo;
    private final DoanhNghiepRepository doanhNghiepRepo;
    // Phase 7 — 2 cot diem (nguoi danh gia = NguoiDung — co the la GV hoac NV DN)
    private final GiangVienRepository giangVienRepo;
    // Refactor: NV DN giờ là NguoiDung loại DoanhNghiep với FK doanhNghiep
    private final NguoiDungRepository nguoiDungRepo;
    // Bug-fix phan quyen: lookup ds + sv.lopHC.coVan + ds.doanhNghiep de
    // verify ownership truoc khi cho phep cham diem (chong impersonation).
    private final DanhSachThucTapRepository dsThucTapRepo;

    @GetMapping
    public String danhSach(@AuthenticationPrincipal CustomUserDetails ud, Model model) {
        // Bug-fix #8: SV chi thay cac dot TT ma minh tham gia (tranh information
        // disclosure: truoc day SV thay toan bo dot TT moi khoa). Staff/GV/CVHT/DN
        // van thay toan bo de nghiep vu.
        boolean isSV = ud != null && ud.getAuthorities().stream()
                .anyMatch(a -> "ROLE_SINH_VIEN".equals(a.getAuthority()));
        if (isSV) {
            model.addAttribute("danhSach",
                    dotTTService.findDotCuaSinhVien(ud.getMaNguoiDung()));
        } else {
            model.addAttribute("danhSach", dotTTService.findAll());
        }
        // Phase 3 — stat-card row dong bo voi kien-tap. Truoc day count tinh
        // inline trong template (#lists.size + .?[trangThai==…]) — chuyen
        // sang COUNT() o DB de tach logic.
        model.addAttribute("thongKe", dotTTService.getThongKe());
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/danh-sach";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @GetMapping("/them")
    public String themForm(Model model) {
        model.addAttribute("dotTTDTO", new DotThucTapDTO());
        populateModel(model);
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/form";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/them")
    public String them(@Valid @ModelAttribute("dotTTDTO") DotThucTapDTO dto,
                        BindingResult br,
                        @AuthenticationPrincipal CustomUserDetails ud,
                        Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            populateModel(model);
            model.addAttribute("activeMenu", "thuc-tap");
            return "thuc-tap/form";
        }
        try {
            // Bug fix Phase 5.2: redirect ve chi-tiet de user thay form import
            // SV ngay sau khi tao — truoc day chi redirect ve list mat 1 cu nhip.
            DotThucTap saved = dotTTService.create(dto, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg",
                    "Da tao dot thuc tap. Tiep tuc them sinh vien o phan ben duoi.");
            return "redirect:/thuc-tap/chi-tiet/" + saved.getMaDotTT();
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            // Re-render form de user khong mat input — giu errorMsg trong flash
            // de banner alert hien thi.
            return "redirect:/thuc-tap/them";
        }
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @GetMapping("/sua/{id}")
    public String suaForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        var dot = dotTTService.findById(id);
        // Phase 7 — UX guard: tra ve chi-tiet kem flash error neu state khong cho phep sua
        // (service block, nhung neu khong chan o controller -> user dien xong moi bi reject).
        TrangThaiDotTT tt = dot.getTrangThai();
        if (tt != TrangThaiDotTT.ChuanBi && tt != TrangThaiDotTT.ChoDuyet) {
            ra.addFlashAttribute("errorMsg",
                    "Khong the chinh sua dot dang o trang thai " + tt
                    + ". Chi co the sua khi dot o trang thai ChuanBi hoac ChoDuyet.");
            return "redirect:/thuc-tap/chi-tiet/" + id;
        }
        DotThucTapDTO dto = new DotThucTapDTO();
        dto.setTenDotTT(dot.getTenDotTT());
        dto.setMaCTDT(dot.getCtdtHocPhan() != null ? dot.getCtdtHocPhan().getId().getMaCTDT() : null);
        dto.setMaHocPhan(dot.getCtdtHocPhan() != null ? dot.getCtdtHocPhan().getId().getMaHocPhan() : null);
        dto.setMaHocKy(dot.getHocKy() != null ? dot.getHocKy().getMaHocKy() : null);
        dto.setNgayBatDau(dot.getNgayBatDau());
        dto.setNgayKetThuc(dot.getNgayKetThuc());
        model.addAttribute("dotTTDTO", dto);
        model.addAttribute("dotId", id);
        populateModel(model);
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/form";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/sua/{id}")
    public String sua(@PathVariable Integer id,
                       @Valid @ModelAttribute("dotTTDTO") DotThucTapDTO dto,
                       BindingResult br,
                       Model model, RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("dotId", id);
            populateModel(model);
            model.addAttribute("activeMenu", "thuc-tap");
            return "thuc-tap/form";
        }
        try {
            dotTTService.update(id, dto);
            ra.addFlashAttribute("successMsg", "Da cap nhat dot thuc tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
            return "redirect:/thuc-tap/sua/" + id;
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/gui-phe-duyet/{id}")
    public String guiPheDuyet(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotTTService.guiPheDuyet(id);
            ra.addFlashAttribute("successMsg", "Da gui yeu cau phe duyet.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        // Stay on chi-tiet so user can see the new state immediately.
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    // Phe duyet dot thuc tap — chi TTDTXS hoac ADMIN (review P0-4).
    // URL rule cho PDT, GV, CVHT, DN, SV cung vao /thuc-tap/**, nen phai chan cap method.
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
    @PostMapping("/phe-duyet/{id}")
    public String pheduyet(@PathVariable Integer id,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            RedirectAttributes ra) {
        try {
            dotTTService.pheduyet(id, ud.getMaNguoiDung());
            ra.addFlashAttribute("successMsg", "Da phe duyet dot thuc tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        // Sau phe duyet -> ve chi-tiet de TTDTXS thay status moi va next-step.
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    /**
     * Phase 7 — TTDTXS bat dau dot thuc tap (DaDuyet -> DangThucHien).
     */
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
    @PostMapping("/bat-dau/{id}")
    public String batDau(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotTTService.batDau(id);
            ra.addFlashAttribute("successMsg",
                    "Da bat dau dot thuc tap. Sinh vien co the nhap diem va nhan xet.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    /**
     * Phase 7 — TTDTXS ket thuc dot thuc tap (DangThucHien -> DaKetThuc).
     * Cascade: cap nhat trang thai DanhSachThucTap chua chot sang DaKetThuc.
     */
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
    @PostMapping("/ket-thuc/{id}")
    public String ketThuc(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotTTService.ketThuc(id);
            ra.addFlashAttribute("successMsg",
                    "Da ket thuc dot thuc tap. Trang thai sinh vien duoc chot.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    /**
     * Phase 7 — TTDTXS huy dot thuc tap (truoc DaKetThuc).
     */
    @PreAuthorize("hasAnyRole('TTDTXS','ADMIN')")
    @PostMapping("/huy/{id}")
    public String huy(@PathVariable Integer id, RedirectAttributes ra) {
        try {
            dotTTService.huy(id);
            ra.addFlashAttribute("successMsg", "Da huy dot thuc tap.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    /**
     * Chi tiet 1 dot thuc tap.
     *
     * <p>Bug-fix #4, #12: Ownership check nghiem ngat cho role khong phai staff.
     * Class-level @PreAuthorize chi chan role, KHONG check ownership -> truoc
     * day SV/GV/DN bat ky xem duoc diem + nhan xet cua MOI dot TT (information
     * disclosure nghiem trong, vi pham privacy).</p>
     *
     * <p>Quy tac ownership:</p>
     * <ul>
     *   <li>PDT/TTDTXS/ADMIN: bypass (staff).</li>
     *   <li>SV: phai co trong danh sach SV cua dot.</li>
     *   <li>GV/CVHT: phai la GV day HP cua dot TT (theo DoiNguGiangVienHP)
     *       HOAC phai la co van cua SV trong dot.</li>
     *   <li>DN: phai co SV trong dot duoc phan cong cho chinh DN nay.</li>
     * </ul>
     */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN','GIANG_VIEN','CVHT','DOANH_NGHIEP','SINH_VIEN')")
    @GetMapping("/chi-tiet/{id}")
    public String chiTiet(@PathVariable Integer id,
                            @AuthenticationPrincipal CustomUserDetails ud,
                            Model model) {
        // Bug-fix #4: ownership check truoc khi load chi-tiet.
        boolean isStaff = ud != null && ud.getAuthorities().stream().anyMatch(a ->
                  "ROLE_PDT".equals(a.getAuthority())
               || "ROLE_TTDTXS".equals(a.getAuthority())
               || "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isStaff && ud != null) {
            boolean allowed = dotTTService.canUserViewDot(id, ud);
            if (!allowed) {
                throw new AccessDeniedException(
                        "Ban khong co quyen xem chi tiet dot thuc tap nay.");
            }
        }

        model.addAttribute("dot", dotTTService.findById(id));
        model.addAttribute("danhSachSV", dotTTService.findDanhSachSV(id, ud));
        // DN list cho dropdown trong cap-nhat-kq + import — chi DN dang hop tac.
        model.addAttribute("doanhNghiepList",
                doanhNghiepRepo.findByTrangThai(TrangThaiDoanhNghiep.DangHopTac));
        // Phase 7 — 2 cot diem:
        //   ketQuaMap     : Map<maThucTap, Map<maVaiTro, KetQuaThucTap>>
        //                   de template render diem cho tung SV theo vai tro.
        //   giangVienList : dropdown chon nguoi cham (cot GV — vai tro GV_HD/GV_PB).
        //   nhanVienDNList: dropdown chon nguoi cham (cot DN — vai tro DN).
        //                   Refactor: NV DN giờ là NguoiDung loại DoanhNghiep với FK
        //                   doanhNghiep trực tiếp (bỏ bảng NhanVienDoanhNghiep).
        //                   Đã fetch doanhNghiep để render hoTen + tenDN trong template.
        model.addAttribute("ketQuaMap", dotTTService.getKetQuaMapByDot(id));
        model.addAttribute("giangVienList", giangVienRepo.findAllFetchNguoiDung());
        // Refactor: NV DN giờ là NguoiDung loại DoanhNghiep với FK doanhNghiep (bỏ bảng NhanVienDoanhNghiep)
        model.addAttribute("nhanVienDNList", nguoiDungRepo.findAllNhanVienDNFetch());
        String maDnHienTai = null;
        if (ud != null) {
            NguoiDung currentUser = nguoiDungRepo.findById(ud.getMaNguoiDung()).orElse(null);
            if (currentUser != null && currentUser.getLoaiNguoiDung() == LoaiNguoiDung.DoanhNghiep) {
                maDnHienTai = currentUser.getMaDoanhNghiep();
            }
        }
        model.addAttribute("maDnHienTai", maDnHienTai);
        model.addAttribute("activeMenu", "thuc-tap");
        return "thuc-tap/chi-tiet";
    }

    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/chi-tiet/{id}/them-sv")
    public String themSV(@PathVariable Integer id,
                          @RequestParam String maSVList,
                          @RequestParam(required = false) String loaiThucTap,
                          @RequestParam(required = false) String maDoanhNghiep,
                          RedirectAttributes ra) {
        try {
            List<String> dsSV = Arrays.stream(maSVList.split("[,\\n;\\s]+"))
                    .map(String::trim).filter(s -> !s.isBlank()).toList();
            if (dsSV.isEmpty()) {
                ra.addFlashAttribute("errorMsg", "Vui long nhap it nhat 1 ma sinh vien.");
                return "redirect:/thuc-tap/chi-tiet/" + id;
            }

            // Default loai = Truong neu user khong chon. Truong hop chon
            // DoanhNghiep ma quen chon DN -> service se loi (rang buoc nghiep vu).
            LoaiThucTap loai = LoaiThucTap.Truong;
            if (loaiThucTap != null && !loaiThucTap.isBlank()) {
                try {
                    loai = LoaiThucTap.valueOf(loaiThucTap.trim());
                } catch (IllegalArgumentException ignore) { /* fallback Truong */ }
            }

            var result = dotTTService.importSinhVien(id, dsSV, loai, maDoanhNghiep);
            ra.addFlashAttribute("importResult", result);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    /**
     * Cap nhat metadata phan cong (loaiThucTap, doanhNghiep tiep nhan, ghi chu)
     * cho 1 SV trong dot thuc tap.
     *
     * <p>Bug-fix phan quyen: TRUOC day endpoint nay mo cho ca GV/CVHT/DN — sai
     * voi nghiep vu vi day la cap nhat metadata phan cong (vd doi SV tu thuc
     * tap o truong sang DN tiep nhan), thuoc trach nhiem cua phong dao tao /
     * TTDTXS / ADMIN. GV/CVHT/DN chi duoc cham diem qua endpoint
     * {@code /cap-nhat-diem} — KHONG duoc doi loai TT, doi DN tiep nhan.</p>
     */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN')")
    @PostMapping("/chi-tiet/{id}/cap-nhat-kq/{maDanhSach}")
    public String capNhatKetQua(@PathVariable Integer id,
                                  @PathVariable Integer maDanhSach,
                                  @RequestParam(required = false) String loaiThucTap,
                                  @RequestParam(required = false) String maDoanhNghiep,
                                  @RequestParam(required = false) String nhanXet,
                                  RedirectAttributes ra) {
        try {
            dotTTService.capNhatKetQua(maDanhSach, loaiThucTap, maDoanhNghiep, nhanXet);
            ra.addFlashAttribute("successMsg", "Da cap nhat ket qua sinh vien.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    /**
     * Phase 7 — Nhap/cap nhat 1 cot diem cho SV theo vai tro.
     *
     * <p>UI cho phep nhap doc lap tung cot:</p>
     * <ul>
     *   <li>Tai Truong: vaiTro=GV_HD (cot 1) hoac vaiTro=GV_PB (cot 2)</li>
     *   <li>Tai DN: vaiTro=DN (cot 1) hoac vaiTro=GV_HD (cot 2)</li>
     * </ul>
     *
     * <p>Form post 4 fields: vaiTro, diem, nhanXet, maGiangVien (nguoi danh gia).
     * Diem null cho phep "xoa" diem; nhanXet blank cho phep "xoa" nhan xet.</p>
     */
    /**
     * Phase 7 — Nhap/cap nhat 1 cot diem cho SV theo vai tro.
     *
     * <p>UI cho phep nhap doc lap tung cot:</p>
     * <ul>
     *   <li>Tai Truong: vaiTro=GV_HD (cot 1) hoac vaiTro=GV_PB (cot 2)</li>
     *   <li>Tai DN: vaiTro=DN (cot 1) hoac vaiTro=GV_HD (cot 2)</li>
     * </ul>
     *
     * <p>Form post 4 fields: vaiTro, diem, nhanXet, maGiangVien (nguoi danh gia).
     * Diem null cho phep "xoa" diem; nhanXet blank cho phep "xoa" nhan xet.</p>
     *
     * <p>Bug-fix #5: vai tro 'CVHT' khong duoc su dung trong cham diem TT
     * (theo spec nghiep vu da duoc chu dong lam ro: CVHT khong xuat hien
     * trong KetQuaThucTap). Vai tro hop le: GV_HD, GV_PB (loai=Truong) hoac
     * GV_HD, DN (loai=DoanhNghiep).</p>
     */
    @PreAuthorize("hasAnyRole('PDT','TTDTXS','ADMIN','GIANG_VIEN','DOANH_NGHIEP')")
    @PostMapping("/chi-tiet/{id}/cap-nhat-diem/{maDanhSach}")
    public String capNhatDiem(@PathVariable Integer id,
                                @PathVariable Integer maDanhSach,
                                @RequestParam String vaiTro,
                                @RequestParam(required = false) BigDecimal diem,
                                @RequestParam(required = false) String nhanXet,
                                @RequestParam(name = "maNguoiDanhGia", required = false) String maNguoiDanhGia,
                                @RequestParam(name = "maGiangVien", required = false) String maGiangVienLegacy,
                                @AuthenticationPrincipal CustomUserDetails ud,
                                RedirectAttributes ra) {
        try {
            // Phase 7 refactor: param mac dinh la maNguoiDanhGia (NguoiDung — co the
            // la GV hoac NV DN). Fallback maGiangVien de tuong thich form cu.
            String maNDG = (maNguoiDanhGia != null && !maNguoiDanhGia.isBlank())
                    ? maNguoiDanhGia : maGiangVienLegacy;

            // ================================================================
            // Bug-fix phan quyen (user complaint "giang vien con sua diem,
            // giang vien trong thuc tap?"):
            //   Truoc day: bat ky GV/CVHT/DN nao da auth deu cham duoc diem
            //   cho BAT KY SV nao cua BAT KY DotTT nao -> security hole +
            //   du lieu lon xon (GV chua giang lop nay van cham diem SV cua
            //   lop khac; DN ngoai cuoc co the cham diem cho SV cua DN khac).
            //
            //   Quy tac dung (docs/03 §WF-08.3 + so do quyen):
            //   - PDT/TTDTXS/ADMIN: bypass ownership cua nguoi chinh cham
            //     (sua loi, cham thay), NHUNG van verify maNguoiDanhGia match
            //     role va dung la GV day HP / NV DN tiep nhan (bug-fix #6).
            //   - GIANG_VIEN: vai tro phai la GV_HD / GV_PB; force
            //     maNguoiDanhGia = current.maNguoiDung (chong impersonate).
            //   - DOANH_NGHIEP: vai tro phai la DN; SV phai dang thuc tap
            //     tai chinh DN cua nhan vien nay (dn.maDoanhNghiep match);
            //     force maNguoiDanhGia.
            // ================================================================

            // Bug-fix #13: verify path var id khop voi ds.dotThucTap.maDotTT.
            DanhSachThucTap ds = dsThucTapRepo
                    .findByIdFetchOwnership(maDanhSach)
                    .orElseThrow(() -> new AccessDeniedException(
                            "Khong tim thay ban ghi thuc tap."));
            Integer dotIdThucTe = ds.getDotThucTap() != null
                    ? ds.getDotThucTap().getMaDotTT() : null;
            if (dotIdThucTe == null || !dotIdThucTe.equals(id)) {
                throw new AccessDeniedException(
                        "Ban ghi thuc tap khong thuoc dot duoc chi dinh trong URL.");
            }

            boolean isStaff = ud != null && ud.getAuthorities().stream().anyMatch(a ->
                      "ROLE_PDT".equals(a.getAuthority())
                   || "ROLE_TTDTXS".equals(a.getAuthority())
                   || "ROLE_ADMIN".equals(a.getAuthority()));

            if (!isStaff && ud != null) {
                String vaiTroTrim = vaiTro != null ? vaiTro.trim() : "";
                NguoiDung caller = nguoiDungRepo.findById(ud.getMaNguoiDung())
                        .orElseThrow(() -> new AccessDeniedException(
                                "Khong tim thay nguoi dung hien tai."));
                LoaiNguoiDung callerLoai = caller.getLoaiNguoiDung();

                boolean isGv = callerLoai == LoaiNguoiDung.GiangVien;
                boolean isDn = callerLoai == LoaiNguoiDung.DoanhNghiep;

                // Bug-fix #5: vai tro CVHT khong con hop le trong cham diem TT
                // (spec nghiep vu). Chi chap nhan GV_HD, GV_PB, DN.
                if ("GV_HD".equals(vaiTroTrim) || "GV_PB".equals(vaiTroTrim)) {
                    if (!isGv) {
                        throw new AccessDeniedException(
                                "Vai tro '" + vaiTroTrim + "' yeu cau ban la giang vien.");
                    }
                    // Bug-fix: GV chi duoc cham diem cho SV co trong lop giang vien nay day.
                    // Kiem tra: GiangVien -> DoiNguGiangVienHP.HocPhan cua dot TT
                    //           -> SV co ghi danh lop HP trong cung HK.
                    boolean gvTeachesStudent = dsThucTapRepo.isGvTeachesStudent(
                            maDanhSach, ud.getMaNguoiDung());
                    if (!gvTeachesStudent) {
                        throw new AccessDeniedException(
                                "Ban khong phai la giang vien day hoc phan nay trong hoc ky "
                                + "cua dot thuc tap. Chi co the cham diem cho sinh vien trong "
                                + "lop hoc phan cua ban.");
                    }
                } else if ("DN".equals(vaiTroTrim)) {
                    if (!isDn) {
                        throw new AccessDeniedException(
                                "Vai tro 'DN' yeu cau ban la nhan vien doanh nghiep.");
                    }
                    String dnSV = ds.getDoanhNghiep() != null
                            ? ds.getDoanhNghiep().getMaDoanhNghiep() : null;
                    String dnCaller = caller.getMaDoanhNghiep();
                    if (dnCaller == null || dnSV == null
                            || !dnCaller.equals(dnSV)) {
                        throw new AccessDeniedException(
                                "Ban chi duoc cham diem cho sinh vien thuc tap tai "
                                + "doanh nghiep cua minh.");
                    }
                } else {
                    throw new AccessDeniedException(
                            "Vai tro '" + vaiTro + "' khong hop le. "
                            + "Chap nhan: GV_HD, GV_PB, DN.");
                }

                // Force maNguoiDanhGia = current de chong impersonation —
                // GV/DN khong duoc thay nguoi cham la nguoi khac.
                maNDG = ud.getMaNguoiDung();
            }

            dotTTService.capNhatDiem(maDanhSach, vaiTro, diem, nhanXet, maNDG);
            ra.addFlashAttribute("successMsg",
                    "Da cap nhat diem (" + vaiTro + ") cho sinh vien.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/thuc-tap/chi-tiet/" + id;
    }

    private void populateModel(Model model) {
        model.addAttribute("ctdtList", ctdtRepo.findAll());
        model.addAttribute("hocPhanList", hocPhanRepo.findAllDaDuyet());
        model.addAttribute("hocKyList", hocKyRepo.findAllByOrderByNgayBatDauDesc());
        // DN list cho form (truoc day chua co — gay form thieu dropdown
        // khi muon set DN tiep nhan ngay khi tao dot). Chi load DN dang hop tac.
        model.addAttribute("doanhNghiepList",
                doanhNghiepRepo.findByTrangThai(TrangThaiDoanhNghiep.DangHopTac));
    }
}
