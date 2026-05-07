package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.DoanhNghiepDTO;
import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.DanhSachThucTapRepository;
import com.ntu.quanlyctdtdb.repository.DoanhNghiepRepository;
import com.ntu.quanlyctdtdb.repository.DotKienTapRepository;
import com.ntu.quanlyctdtdb.service.DoanhNghiepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DoanhNghiepServiceImpl implements DoanhNghiepService {

    private final DoanhNghiepRepository doanhNghiepRepo;
    private final DotKienTapRepository dotKienTapRepo;
    private final DanhSachThucTapRepository danhSachThucTapRepo;

    /* ================== QUERY ================== */

    @Override
    @Transactional(readOnly = true)
    public Page<DoanhNghiep> search(String keyword, TrangThaiDoanhNghiep trangThai, Pageable pageable) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return doanhNghiepRepo.searchDoanhNghiep(kw, trangThai, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public DoanhNghiep findById(String ma) {
        return doanhNghiepRepo.findById(ma)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DoanhNghiep", "MaDoanhNghiep", ma));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoanhNghiep> findAllDangHopTac() {
        return doanhNghiepRepo.findByTrangThai(TrangThaiDoanhNghiep.DangHopTac);
    }

    /* ================== MUTATION ================== */

    @Override
    public DoanhNghiep create(DoanhNghiepDTO dto) {
        // Sinh ma neu khong nhap tay
        String ma = (dto.getMaDoanhNghiep() != null && !dto.getMaDoanhNghiep().isBlank())
                ? dto.getMaDoanhNghiep().trim().toUpperCase()
                : sinhMaDoanhNghiep();

        if (doanhNghiepRepo.existsById(ma)) {
            throw new BusinessException("Ma doanh nghiep da ton tai: " + ma);
        }
        if (dto.getEmail() != null && !dto.getEmail().isBlank()
                && doanhNghiepRepo.existsByEmail(dto.getEmail().trim())) {
            throw new BusinessException("Email da ton tai: " + dto.getEmail());
        }

        DoanhNghiep dn = DoanhNghiep.builder()
                .maDoanhNghiep(ma)
                .tenDoanhNghiep(dto.getTenDoanhNghiep().trim())
                .linhVuc(trimOrNull(dto.getLinhVuc()))
                .nguoiDaiDien(trimOrNull(dto.getNguoiDaiDien()))
                .email(trimOrNull(dto.getEmail()))
                .soDienThoai(trimOrNull(dto.getSoDienThoai()))
                .diaChiDN(trimOrNull(dto.getDiaChiDN()))
                .trangThai(dto.getTrangThai() != null
                        ? dto.getTrangThai()
                        : TrangThaiDoanhNghiep.DangHopTac)
                .build();
        return doanhNghiepRepo.save(dn);
    }

    @Override
    public DoanhNghiep update(String ma, DoanhNghiepDTO dto) {
        DoanhNghiep dn = findById(ma);

        // Kiem tra email unique (ngoai tru chinh no)
        String newEmail = trimOrNull(dto.getEmail());
        if (newEmail != null && !newEmail.equalsIgnoreCase(dn.getEmail())
                && doanhNghiepRepo.existsByEmailAndMaDoanhNghiepNot(newEmail, ma)) {
            throw new BusinessException("Email da ton tai: " + newEmail);
        }

        dn.setTenDoanhNghiep(dto.getTenDoanhNghiep().trim());
        dn.setLinhVuc(trimOrNull(dto.getLinhVuc()));
        dn.setNguoiDaiDien(trimOrNull(dto.getNguoiDaiDien()));
        dn.setEmail(newEmail);
        dn.setSoDienThoai(trimOrNull(dto.getSoDienThoai()));
        dn.setDiaChiDN(trimOrNull(dto.getDiaChiDN()));
        if (dto.getTrangThai() != null) {
            dn.setTrangThai(dto.getTrangThai());
        }
        return doanhNghiepRepo.save(dn);
    }

    @Override
    public void toggleTrangThai(String ma) {
        DoanhNghiep dn = findById(ma);
        dn.setTrangThai(dn.getTrangThai() == TrangThaiDoanhNghiep.DangHopTac
                ? TrangThaiDoanhNghiep.TamNgung
                : TrangThaiDoanhNghiep.DangHopTac);
        doanhNghiepRepo.save(dn);
    }
    
    @Override
    public DoanhNghiep updateThongTinLienHe(String maDN,
            com.ntu.quanlyctdtdb.dto.DoanhNghiepCuaToiDTO dto) {
        DoanhNghiep dn = findById(maDN);

        // Email unique check (ngoai tru chinh DN nay).
        String newEmail = trimOrNull(dto.getEmail());
        if (newEmail != null && !newEmail.equalsIgnoreCase(dn.getEmail())
                && doanhNghiepRepo.existsByEmailAndMaDoanhNghiepNot(newEmail, maDN)) {
            throw new BusinessException("Email da ton tai: " + newEmail);
        }

        // CHI cap nhat 4 field lien he. KHONG dong vao Ten/LinhVuc/TrangThai
        // — nhung field nay chi PDT/TTDTXS/ADMIN duoc sua qua /doanh-nghiep/sua.
        dn.setNguoiDaiDien(trimOrNull(dto.getNguoiDaiDien()));
        dn.setEmail(newEmail);
        dn.setSoDienThoai(trimOrNull(dto.getSoDienThoai()));
        dn.setDiaChiDN(trimOrNull(dto.getDiaChiDN()));
        return doanhNghiepRepo.save(dn);
    }

    @Override
    public void delete(String ma) {
        DoanhNghiep dn = findById(ma);

        // Chan xoa neu con tham chieu tu DotKienTap hoac DanhSachThucTap
        long soDotKT = dotKienTapRepo.findByDoanhNghiep_MaDoanhNghiep(ma).size();
        if (soDotKT > 0) {
            throw new BusinessException(
                    "Khong the xoa. Con " + soDotKT + " dot kien tap dang tham chieu doanh nghiep nay.");
        }
        // DanhSachThucTap.MaDoanhNghiep la FK nullable - check qua native count
        long soThucTap = danhSachThucTapRepo.findAll().stream()
                .filter(d -> d.getDoanhNghiep() != null
                        && ma.equals(d.getDoanhNghiep().getMaDoanhNghiep()))
                .count();
        if (soThucTap > 0) {
            throw new BusinessException(
                    "Khong the xoa. Con " + soThucTap + " phan cong thuc tap tham chieu doanh nghiep nay.");
        }
        doanhNghiepRepo.delete(dn);
    }

    /* ================== HELPER ================== */

    @Override
    public String sinhMaDoanhNghiep() {
        long count = doanhNghiepRepo.count();
        // Sinh ma "DN001" + tang den khi khong trung (phong luong xoa giua chung)
        for (int i = 1; i <= 999; i++) {
            String candidate = String.format("DN%03d", count + i);
            if (!doanhNghiepRepo.existsById(candidate)) {
                return candidate;
            }
        }
        throw new BusinessException("Khong the sinh ma doanh nghiep tu dong, vui long nhap tay.");
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getThongKe() {
        Map<String, Long> map = new LinkedHashMap<>();
        long total = doanhNghiepRepo.count();
        long dangHopTac = doanhNghiepRepo.countByTrangThai(TrangThaiDoanhNghiep.DangHopTac);
        map.put("tongDoanhNghiep", total);
        map.put("dangHopTac", dangHopTac);
        map.put("tamNgung", total - dangHopTac);
        return map;
    }

    private static String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
