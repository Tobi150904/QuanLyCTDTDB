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
    @Transactional(readOnly = true)
    public List<HocKyNamHoc> findAll() {
        return hocKyRepo.findAllByOrderByNgayBatDauDesc();
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
        if (hocKyRepo.existsById(dto.getMaHocKy())) {
            throw new BusinessException("Hoc ky voi ma " + dto.getMaHocKy() + " da ton tai.");
        }
        TrangThaiHocKy trangThai = dto.getTrangThai() != null
                ? dto.getTrangThai() : TrangThaiHocKy.SapDienRa;
        if (trangThai == TrangThaiHocKy.DangDienRa) {
            ensureNoOtherActiveExcept(null);
        }
        HocKyNamHoc e = HocKyNamHoc.builder()
                .maHocKy(dto.getMaHocKy())
                .tenHocKy(dto.getTenHocKy())
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

        // Khong duoc doi primary key
        if (!maHocKy.equals(dto.getMaHocKy())) {
            throw new BusinessException(
                    "Khong duoc doi Ma Hoc Ky. Neu can, hay xoa va tao moi.");
        }

        TrangThaiHocKy moi = dto.getTrangThai();
        TrangThaiHocKy cu = e.getTrangThai();

        if (cu == TrangThaiHocKy.DaKetThuc && moi != TrangThaiHocKy.DaKetThuc) {
            throw new BusinessException(
                    "Hoc ky da ket thuc, khong the chuyen lai trang thai truoc do.");
        }
        if (moi == TrangThaiHocKy.DangDienRa && cu != TrangThaiHocKy.DangDienRa) {
            ensureNoOtherActiveExcept(maHocKy);
        }

        e.setTenHocKy(dto.getTenHocKy());
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
            ensureNoOtherActiveExcept(maHocKy);
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

    private void ensureNoOtherActiveExcept(String excludeMaHocKy) {
        long active = hocKyRepo.findAll().stream()
                .filter(h -> h.getTrangThai() == TrangThaiHocKy.DangDienRa)
                .filter(h -> excludeMaHocKy == null || !excludeMaHocKy.equals(h.getMaHocKy()))
                .count();
        if (active >= 1) {
            throw new BusinessException(
                    "Da co hoc ky khac dang DangDienRa. Hay ket thuc no truoc khi kich hoat hoc ky moi.");
        }
    }

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new BusinessException("Ngay bat dau va ngay ket thuc khong duoc de trong.");
        }
        if (!start.isBefore(end)) {
            throw new BusinessException("Ngay bat dau phai truoc ngay ket thuc.");
        }
    }
}
