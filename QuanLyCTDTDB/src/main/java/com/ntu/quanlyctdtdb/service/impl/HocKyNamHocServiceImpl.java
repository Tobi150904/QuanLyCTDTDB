package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.HocKyNamHocDTO;
import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.DotKienTapRepository;
import com.ntu.quanlyctdtdb.repository.DotThucTapRepository;
import com.ntu.quanlyctdtdb.repository.HocKyNamHocRepository;
import com.ntu.quanlyctdtdb.service.HocKyNamHocService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service quan ly HocKyNamHoc.
 *
 * Business rules:
 *  - MaHocKy format: HK[1-3]-YYYY (YYYY la nam bat dau nam hoc) — enforced o DTO via @Pattern.
 *  - NgayBatDau phai truoc NgayKetThuc.
 *  - Khong duoc chuyen TrangThai tu `DaKetThuc` nguoc tro lai `DangDienRa` / `SapDienRa`.
 *  - Toi da 1 HocKy `DangDienRa` tai moi thoi diem.
 *  - Khong xoa duoc HocKy neu dang co DotKienTap / DotThucTap tham chieu.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HocKyNamHocServiceImpl implements HocKyNamHocService {

    private final HocKyNamHocRepository hocKyRepo;
    private final DotKienTapRepository dotKienTapRepo;
    private final DotThucTapRepository dotThucTapRepo;

    @Override
    public List<HocKyNamHoc> findAll() {
        // Auto-resync trang thai moi lan list danh sach: neu ngay hien tai
        // da qua/vao khoang ngayBatDau..ngayKetThuc ma trang thai chua cap
        // nhat (do nguoi tao set sai / du lieu cu trong DB), tu dong dong
        // bo lai de hien thi dung. Khong dong "DaKetThuc" vi state do la
        // final (xem update()).
        resyncStatuses();
        return hocKyRepo.findAllByOrderByNgayBatDauDesc();
    }

    /**
     * Dong bo trang thai cua tat ca HK theo ngay hien tai. Quy tac:
     *  - today < ngayBatDau           -&gt; SapDienRa
     *  - ngayBatDau &lt;= today &lt;= ngayKetThuc -&gt; DangDienRa
     *  - today &gt; ngayKetThuc         -&gt; DaKetThuc
     * State {@code DaKetThuc} la final: da dong thi giu nguyen du ngay co
     * bi doi (hanh vi nay match logic trong {@link #update}).
     */
    private void resyncStatuses() {
        hocKyRepo.findAll().forEach(h -> {
            if (h.getTrangThai() == TrangThaiHocKy.DaKetThuc) return;
            TrangThaiHocKy expected = deriveStatus(h.getNgayBatDau(), h.getNgayKetThuc());
            if (expected != h.getTrangThai()) {
                h.setTrangThai(expected);
                hocKyRepo.save(h);
            }
        });
    }

    @Override
    @Transactional(readOnly = true)
    public HocKyNamHoc findById(String maHocKy) {
        return hocKyRepo.findById(maHocKy)
                .orElseThrow(() -> new ResourceNotFoundException("HocKyNamHoc", "MaHocKy", maHocKy));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HocKyNamHoc> findDangDienRa() {
        return hocKyRepo.findByTrangThai(TrangThaiHocKy.DangDienRa);
    }

    @Override
    public HocKyNamHoc create(HocKyNamHocDTO dto) {
        validateDates(dto.getNgayBatDau(), dto.getNgayKetThuc());
        validateNamHoc(dto);

        // Suy MaHocKy neu user chua cung cap (form chi nhap hocKyThu + namBatDau)
        String maHocKy = dto.getMaHocKy();
        if (maHocKy == null || maHocKy.isBlank()) {
            maHocKy = buildMaHocKy(dto.getHocKyThu(), dto.getNamBatDau());
        }
        if (hocKyRepo.existsById(maHocKy)) {
            throw new BusinessException("Hoc ky voi ma " + maHocKy + " da ton tai.");
        }

        String tenHocKy = dto.getTenHocKy();
        if (tenHocKy == null || tenHocKy.isBlank()) {
            tenHocKy = buildTenHocKy(dto.getHocKyThu(), dto.getNamBatDau(), dto.getNamKetThuc());
        }

        // FIX: Luon quy dinh trang thai dua tren ngay bat dau/ket thuc so voi
        // ngay hien tai - tranh case user chon sai trong form (vi du HK-2025
        // co ngay bat dau 01/02/2026 - 01/06/2026 nhung user chon SapDienRa
        // trong khi hom nay da 23/04/2026). Neu user co ban chat da pick
        // trang thai "DangDienRa" hoac "DaKetThuc" nhung ngay khong khop thi
        // he thong vuon dung dates la source of truth.
        TrangThaiHocKy trangThai = deriveStatus(dto.getNgayBatDau(), dto.getNgayKetThuc());
        if (trangThai == TrangThaiHocKy.DangDienRa) {
            // Tu dong dong HK cu dang DangDienRa (neu co) truoc khi kich hoat HK moi.
            autoCloseOtherActive(null);
        }
        HocKyNamHoc e = HocKyNamHoc.builder()
                .maHocKy(maHocKy)
                .tenHocKy(tenHocKy)
                .ngayBatDau(dto.getNgayBatDau())
                .ngayKetThuc(dto.getNgayKetThuc())
                .trangThai(trangThai)
                .build();
        return hocKyRepo.save(e);
    }

    @Override
    public HocKyNamHoc update(String maHocKy, HocKyNamHocDTO dto) {
        HocKyNamHoc e = findById(maHocKy);
        validateDates(dto.getNgayBatDau(), dto.getNgayKetThuc());
        validateNamHoc(dto);

        // Khong duoc doi primary key: neu form submit khac, tu chi nhan gia tri URL.
        // Dong thoi hocKyThu/namBatDau khi sua se readonly o template; neu bi doi
        // du lieu POST -> bao loi ro rang.
        String derived = buildMaHocKy(dto.getHocKyThu(), dto.getNamBatDau());
        if (!maHocKy.equals(derived)) {
            throw new BusinessException(
                    "Khong duoc doi Ky / Nam Bat Dau cua Hoc Ky. Neu can, hay xoa va tao moi.");
        }
        if (dto.getMaHocKy() != null && !dto.getMaHocKy().isBlank()
                && !maHocKy.equals(dto.getMaHocKy())) {
            throw new BusinessException(
                    "Khong duoc doi Ma Hoc Ky. Neu can, hay xoa va tao moi.");
        }

        // FIX: trang thai luon duoc suy tu ngay bat dau/ket thuc - tranh
        // user set tay nhung sai voi realities (xem comment tuong tu trong
        // create()). Giu ngoai le: HK da DaKetThuc thi KHONG tu dong "hoi
        // sinh" khi ngay bat dau/ket thuc bi doi - van coi nhu da ket thuc.
        TrangThaiHocKy cu = e.getTrangThai();
        TrangThaiHocKy moi = cu == TrangThaiHocKy.DaKetThuc
                ? TrangThaiHocKy.DaKetThuc
                : deriveStatus(dto.getNgayBatDau(), dto.getNgayKetThuc());

        if (moi == TrangThaiHocKy.DangDienRa && cu != TrangThaiHocKy.DangDienRa) {
            autoCloseOtherActive(maHocKy);
        }

        String tenHocKy = dto.getTenHocKy();
        if (tenHocKy == null || tenHocKy.isBlank()) {
            tenHocKy = buildTenHocKy(dto.getHocKyThu(), dto.getNamBatDau(), dto.getNamKetThuc());
        }
        e.setTenHocKy(tenHocKy);
        e.setNgayBatDau(dto.getNgayBatDau());
        e.setNgayKetThuc(dto.getNgayKetThuc());
        e.setTrangThai(moi);
        return hocKyRepo.save(e);
    }

    @Override
    public HocKyNamHoc doiTrangThai(String maHocKy, TrangThaiHocKy moi) {
        HocKyNamHoc e = findById(maHocKy);
        if (e.getTrangThai() == TrangThaiHocKy.DaKetThuc && moi != TrangThaiHocKy.DaKetThuc) {
            throw new BusinessException("Hoc ky da ket thuc, khong the chuyen trang thai.");
        }
        if (moi == TrangThaiHocKy.DangDienRa && e.getTrangThai() != TrangThaiHocKy.DangDienRa) {
            // Roadmap §3.1: Khi chuyen 1 HK sang DangDienRa, tu dong chuyen HK truoc
            // ve DaKetThuc thay vi throw lam block user flow.
            autoCloseOtherActive(maHocKy);
        }
        e.setTrangThai(moi);
        return hocKyRepo.save(e);
    }

    @Override
    public void delete(String maHocKy) {
        HocKyNamHoc e = findById(maHocKy);
        long dotKT = dotKienTapRepo.findByHocKy_MaHocKy(maHocKy).size();
        long dotTT = dotThucTapRepo.findByHocKy_MaHocKy(maHocKy).size();
        if (dotKT > 0 || dotTT > 0) {
            throw new BusinessException(
                    "Khong the xoa hoc ky: dang co " + dotKT + " dot kien tap va "
                  + dotTT + " dot thuc tap tham chieu.");
        }
        hocKyRepo.delete(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getThongKe() {
        List<HocKyNamHoc> all = hocKyRepo.findAll();
        Map<String, Object> m = new HashMap<>();
        m.put("tong", all.size());
        m.put("dangDienRa", all.stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.DangDienRa).count());
        m.put("sapDienRa", all.stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.SapDienRa).count());
        m.put("daKetThuc", all.stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.DaKetThuc).count());
        return m;
    }

    /* ================ Helpers ================ */

    /**
     * Tu dong chuyen cac HK dang DangDienRa khac sang DaKetThuc (tru HK duoc
     * loai tru). Dam bao rang buoc "toi da 1 HK DangDienRa cung luc" van dung,
     * nhung cho phep user kich hoat HK moi ma khong phai tat HK cu tay.
     * Roadmap §3.1.
     */
    private void autoCloseOtherActive(String excludeMaHocKy) {
        hocKyRepo.findAll().stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.DangDienRa)
                .filter(h -> excludeMaHocKy == null || !excludeMaHocKy.equals(h.getMaHocKy()))
                .forEach(h -> {
                    h.setTrangThai(TrangThaiHocKy.DaKetThuc);
                    hocKyRepo.save(h);
                });
    }

    /**
     * Suy ra trang thai HK theo ngay hien tai.
     * Tham chieu {@link #resyncStatuses} cho quy tac day du.
     */
    private TrangThaiHocKy deriveStatus(LocalDate start, LocalDate end) {
        LocalDate today = LocalDate.now();
        if (start == null || end == null) return TrangThaiHocKy.SapDienRa;
        if (today.isBefore(start))   return TrangThaiHocKy.SapDienRa;
        if (today.isAfter(end))      return TrangThaiHocKy.DaKetThuc;
        return TrangThaiHocKy.DangDienRa;
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new BusinessException("Ngay bat dau va ngay ket thuc khong duoc de trong.");
        }
        if (!start.isBefore(end)) {
            throw new BusinessException("Ngay bat dau phai truoc ngay ket thuc.");
        }
    }

    private void validateNamHoc(HocKyNamHocDTO dto) {
        if (dto.getHocKyThu() == null || dto.getNamBatDau() == null || dto.getNamKetThuc() == null) {
            throw new BusinessException("Ky, Nam Bat Dau va Nam Ket Thuc khong duoc de trong.");
        }
        if (dto.getHocKyThu() < 1 || dto.getHocKyThu() > 3) {
            throw new BusinessException("Ky phai tu 1 den 3 (1, 2 hoac 3 - Hoc Ky He).");
        }
        if (dto.getNamKetThuc() != dto.getNamBatDau() + 1) {
            throw new BusinessException("Nam Ket Thuc phai bang Nam Bat Dau + 1.");
        }
    }

    /** Format PK theo quy uoc {@code HKn-YYYY} — xem {@code docs/02_Data § 1}. */
    private String buildMaHocKy(Integer hocKyThu, Integer namBatDau) {
        return "HK" + hocKyThu + "-" + namBatDau;
    }

    private String buildTenHocKy(Integer hocKyThu, Integer namBatDau, Integer namKetThuc) {
        String kyLabel = hocKyThu == 3 ? "He" : String.valueOf(hocKyThu);
        return "Hoc Ky " + kyLabel + " Nam " + namBatDau + "-" + namKetThuc;
    }
}
