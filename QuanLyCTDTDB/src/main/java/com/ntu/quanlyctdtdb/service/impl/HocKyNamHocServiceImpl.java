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
import java.util.Objects;
import java.util.Optional;

/**
 * Service quan ly HocKyNamHoc.
 *
 * Business rules:
 *  - Ma HocKy theo format pattern HK{N}_{nam1}_{nam2}, vi du HK1_2024_2025.
 *  - Ngay bat dau phai truoc ngay ket thuc.
 *  - Khong duoc sua TrangThai "DaKetThuc" nguoc tro lai "DangDienRa".
 *  - Khong duoc xoa HocKy neu dang co DotKienTap/DotThucTap tham chieu.
 *  - Chi co toi da 1 HocKy dang `DangDienRa` tai moi thoi diem (business validation soft).
 */
@Service
@RequiredArgsConstructor
@Transactional
public class HocKyNamHocServiceImpl implements HocKyNamHocService {

    private final HocKyNamHocRepository hocKyRepo;
    private final DotKienTapRepository dotKienTapRepo;
    private final DotThucTapRepository dotThucTapRepo;

    @Override
    @Transactional(readOnly = true)
    public List<HocKyNamHoc> findAll() {
        return hocKyRepo.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.DESC, "namBatDau", "hocKyThu"));
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
        return hocKyRepo.findAll().stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.DangDienRa)
                .findFirst();
    }

    @Override
    public HocKyNamHoc create(HocKyNamHocDTO dto) {
        validateDto(dto);
        String ma = buildMaHocKy(dto.getHocKyThu(), dto.getNamBatDau(), dto.getNamKetThuc());
        if (hocKyRepo.existsById(ma)) {
            throw new BusinessException("HocKyNamHoc voi ma " + ma + " da ton tai.");
        }
        // Chi cho phep 1 hoc ky `DangDienRa`
        if (dto.getTrangThai() == TrangThaiHocKy.DangDienRa) {
            long active = hocKyRepo.findAll().stream()
                    .filter(h -> h.getTrangThai() == TrangThaiHocKy.DangDienRa).count();
            if (active >= 1) {
                throw new BusinessException(
                        "Da co HocKy dang DangDienRa. Chi 1 hoc ky duoc active tai moi thoi diem.");
            }
        }
        HocKyNamHoc e = new HocKyNamHoc();
        e.setMaHocKy(ma);
        e.setHocKyThu(dto.getHocKyThu());
        e.setNamBatDau(dto.getNamBatDau());
        e.setNamKetThuc(dto.getNamKetThuc());
        e.setNgayBatDau(dto.getNgayBatDau());
        e.setNgayKetThuc(dto.getNgayKetThuc());
        e.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : TrangThaiHocKy.ChuanBi);
        return hocKyRepo.save(e);
    }

    @Override
    public HocKyNamHoc update(String maHocKy, HocKyNamHocDTO dto) {
        HocKyNamHoc e = findById(maHocKy);
        validateDto(dto);

        // Block chuyen nguoc DaKetThuc -> DangDienRa / ChuanBi
        if (e.getTrangThai() == TrangThaiHocKy.DaKetThuc
                && dto.getTrangThai() != TrangThaiHocKy.DaKetThuc) {
            throw new BusinessException(
                    "Hoc ky da ket thuc, khong the chuyen lai trang thai truoc do.");
        }

        // Neu update dto tinh toan lai ma, phai dong bo (vi ma la primary key - khong duoc doi)
        String expectedMa = buildMaHocKy(dto.getHocKyThu(), dto.getNamBatDau(), dto.getNamKetThuc());
        if (!Objects.equals(expectedMa, maHocKy)) {
            throw new BusinessException(
                    "Khong duoc doi ky (HocKyThu / NamBatDau / NamKetThuc) sau khi tao. "
                  + "Hay xoa roi tao moi voi ma " + expectedMa + ".");
        }

        // Chi cho phep 1 active
        if (dto.getTrangThai() == TrangThaiHocKy.DangDienRa
                && e.getTrangThai() != TrangThaiHocKy.DangDienRa) {
            long active = hocKyRepo.findAll().stream()
                    .filter(h -> h.getTrangThai() == TrangThaiHocKy.DangDienRa
                                 && !h.getMaHocKy().equals(maHocKy))
                    .count();
            if (active >= 1) {
                throw new BusinessException(
                        "Da co HocKy khac dang DangDienRa. Hay ket thuc no truoc khi kich hoat ky moi.");
            }
        }

        e.setNgayBatDau(dto.getNgayBatDau());
        e.setNgayKetThuc(dto.getNgayKetThuc());
        e.setTrangThai(dto.getTrangThai());
        return hocKyRepo.save(e);
    }

    @Override
    public void delete(String maHocKy) {
        HocKyNamHoc e = findById(maHocKy);
        // Guard: khong xoa khi dang co DotKienTap / DotThucTap tham chieu
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
    public HocKyNamHoc doiTrangThai(String maHocKy, TrangThaiHocKy moi) {
        HocKyNamHoc e = findById(maHocKy);
        if (e.getTrangThai() == TrangThaiHocKy.DaKetThuc && moi != TrangThaiHocKy.DaKetThuc) {
            throw new BusinessException("Hoc ky da ket thuc, khong the chuyen trang thai.");
        }
        if (moi == TrangThaiHocKy.DangDienRa) {
            long active = hocKyRepo.findAll().stream()
                    .filter(h -> h.getTrangThai() == TrangThaiHocKy.DangDienRa
                                 && !h.getMaHocKy().equals(maHocKy))
                    .count();
            if (active >= 1) {
                throw new BusinessException(
                        "Da co HocKy khac dang DangDienRa. Hay ket thuc no truoc.");
            }
        }
        e.setTrangThai(moi);
        return hocKyRepo.save(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getThongKe() {
        List<HocKyNamHoc> all = hocKyRepo.findAll();
        Map<String, Object> m = new HashMap<>();
        m.put("tong", all.size());
        m.put("dangDienRa", all.stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.DangDienRa).count());
        m.put("chuanBi", all.stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.ChuanBi).count());
        m.put("daKetThuc", all.stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.DaKetThuc).count());
        return m;
    }

    /* ================ Helpers ================ */

    /**
     * Build ma HocKy theo pattern chuan cua du an: HK{N}_{nam1}_{nam2}
     * (Vi du HK1_2024_2025 cho ky 1 nam hoc 2024-2025).
     */
    static String buildMaHocKy(int hocKyThu, int namBatDau, int namKetThuc) {
        return "HK" + hocKyThu + "_" + namBatDau + "_" + namKetThuc;
    }

    private void validateDto(HocKyNamHocDTO dto) {
        if (dto.getNamKetThuc() != dto.getNamBatDau() + 1) {
            throw new BusinessException(
                    "Nam ket thuc phai lien ke nam bat dau (namKetThuc = namBatDau + 1).");
        }
        LocalDate start = dto.getNgayBatDau();
        LocalDate end = dto.getNgayKetThuc();
        if (start != null && end != null && !start.isBefore(end)) {
            throw new BusinessException(
                    "Ngay bat dau phai truoc ngay ket thuc.");
        }
    }
}
