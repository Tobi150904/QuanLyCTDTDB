package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.NguoiDungDTO;
import com.ntu.quanlyctdtdb.dto.NguoiDungExcelDTO;
import com.ntu.quanlyctdtdb.entity.*;
import com.ntu.quanlyctdtdb.enums.LoaiGiangVien;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import com.ntu.quanlyctdtdb.enums.TrangThaiSinhVien;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.service.NguoiDungService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NguoiDungServiceImpl implements NguoiDungService {

    private final NguoiDungRepository nguoiDungRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;
    private final LopHanhChinhRepository lopHanhChinhRepo;
    private final NhomNguoiDungRepository nhomNguoiDungRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public Page<NguoiDung> search(String keyword, LoaiNguoiDung loai, Pageable pageable) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return nguoiDungRepo.searchNguoiDung(kw, loai, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public NguoiDung findById(String ma) {
        return nguoiDungRepo.findById(ma)
                .orElseThrow(() -> new ResourceNotFoundException("NguoiDung", "MaNguoiDung", ma));
    }

    @Override
    @Transactional(readOnly = true)
    public NguoiDung findByIdWithRoles(String ma) {
        return nguoiDungRepo.findWithRolesByMaNguoiDung(ma)
                .orElseThrow(() -> new ResourceNotFoundException("NguoiDung", "MaNguoiDung", ma));
    }

    @Override
    public NguoiDung create(NguoiDungDTO dto) {
        // Validate unique
        if (nguoiDungRepo.existsByTenDangNhap(dto.getTenDangNhap())) {
            throw new BusinessException(
                    "Tên đăng nhập đã tồn tại: " + dto.getTenDangNhap());
        }
        if (nguoiDungRepo.existsByEmail(dto.getEmail())) {
            throw new BusinessException(
                    "Email đã tồn tại: " + dto.getEmail());
        }
        if (dto.getMatKhau() == null || dto.getMatKhau().isBlank()) {
            throw new BusinessException(
                    "Mật khẩu không được để trống khi tạo mới.");
        }

        String ma = sinhMaNguoiDung(dto.getLoaiNguoiDung());

        NguoiDung nd = NguoiDung.builder()
                .maNguoiDung(ma)
                .tenDangNhap(dto.getTenDangNhap().trim())
                .matKhauHash(passwordEncoder.encode(dto.getMatKhau()))
                .email(dto.getEmail().trim())
                .hoTen(dto.getHoTen().trim())
                .soDienThoai(dto.getSoDienThoai())
                .loaiNguoiDung(dto.getLoaiNguoiDung())
                .trangThaiTK(true)
                .build();
        nguoiDungRepo.save(nd);

        // Tao ban ghi mo rong
        if (dto.getLoaiNguoiDung() == LoaiNguoiDung.GiangVien) {
            GiangVien gv = GiangVien.builder()
                    .maGV(ma)
                    .nguoiDung(nd)
                    .hocHam(dto.getHocHam())
                    .hocVi(dto.getHocVi())
                    .chuyenNganh(dto.getChuyenNganh())
                    .loaiGiangVien(LoaiGiangVien.GiangVienTruong)
                    .build();
            giangVienRepo.save(gv);
        } else if (dto.getLoaiNguoiDung() == LoaiNguoiDung.SinhVien) {
            if (dto.getMaLopHC() == null || dto.getMaLopHC().isBlank()) {
                throw new BusinessException(
                        "Sinh viên bắt buộc phải thuộc một lớp hành chính.");
            }
            LopHanhChinh lopHC = lopHanhChinhRepo.findById(dto.getMaLopHC())
                    .orElseThrow(() -> new ResourceNotFoundException("LopHanhChinh", "MaLopHC", dto.getMaLopHC()));
            SinhVien sv = SinhVien.builder()
                    .maSV(ma)
                    .nguoiDung(nd)
                    .lopHanhChinh(lopHC)
                    .trangThaiSV(TrangThaiSinhVien.DangHoc)
                    .build();
            sinhVienRepo.save(sv);
        }

        // Them vai tro nghiep vu
        if (dto.getVaiTros() != null) {
            for (VaiTro vaiTro : dto.getVaiTros()) {
                NhomNguoiDungId id = new NhomNguoiDungId(ma, vaiTro);
                if (!nhomNguoiDungRepo.existsById(id)) {
                    NhomNguoiDung nhom = NhomNguoiDung.builder()
                            .id(id).nguoiDung(nd).build();
                    nhomNguoiDungRepo.save(nhom);
                }
            }
        }

        return nd;
    }

    @Override
    public NguoiDung update(String ma, NguoiDungDTO dto) {
        NguoiDung nd = findById(ma);

        // Kiem tra email unique (ngoai tru chinh no)
        if (!nd.getEmail().equals(dto.getEmail()) &&
                nguoiDungRepo.existsByEmailAndMaNguoiDungNot(dto.getEmail(), ma)) {
            throw new BusinessException(
                    "Email đã tồn tại: " + dto.getEmail());
        }

        nd.setEmail(dto.getEmail().trim());
        nd.setHoTen(dto.getHoTen().trim());
        nd.setSoDienThoai(dto.getSoDienThoai());

        // Cap nhat mat khau neu co nhap
        if (dto.getMatKhau() != null && !dto.getMatKhau().isBlank()) {
            nd.setMatKhauHash(passwordEncoder.encode(dto.getMatKhau()));
        }

        // Cap nhat GiangVien fields (chi set neu co gia tri gui len; tranh ghi de null)
        if (nd.getLoaiNguoiDung() == LoaiNguoiDung.GiangVien) {
            giangVienRepo.findById(ma).ifPresent(gv -> {
                if (dto.getHocHam() != null)     gv.setHocHam(dto.getHocHam().trim());
                if (dto.getHocVi() != null)      gv.setHocVi(dto.getHocVi().trim());
                if (dto.getChuyenNganh() != null)gv.setChuyenNganh(dto.getChuyenNganh().trim());
                giangVienRepo.save(gv);
            });
        }

        // Cap nhat SinhVien.LopHanhChinh neu doi lop
        if (nd.getLoaiNguoiDung() == LoaiNguoiDung.SinhVien
                && dto.getMaLopHC() != null && !dto.getMaLopHC().isBlank()) {
            sinhVienRepo.findById(ma).ifPresent(sv -> {
                if (sv.getLopHanhChinh() == null
                        || !dto.getMaLopHC().equals(sv.getLopHanhChinh().getMaLopHC())) {
                    LopHanhChinh newLop = lopHanhChinhRepo.findById(dto.getMaLopHC())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "LopHanhChinh", "MaLopHC", dto.getMaLopHC()));
                    sv.setLopHanhChinh(newLop);
                    sinhVienRepo.save(sv);
                }
            });
        }

        // Cap nhat VaiTro: xoa cu, them moi
        nhomNguoiDungRepo.deleteByNguoiDung_MaNguoiDung(ma);
        if (dto.getVaiTros() != null) {
            for (VaiTro vaiTro : dto.getVaiTros()) {
                NhomNguoiDungId id = new NhomNguoiDungId(ma, vaiTro);
                NhomNguoiDung nhom = NhomNguoiDung.builder().id(id).nguoiDung(nd).build();
                nhomNguoiDungRepo.save(nhom);
            }
        }

        return nguoiDungRepo.save(nd);
    }

    @Override
    public void toggleTrangThai(String ma) {
        NguoiDung nd = findById(ma);
        nd.setTrangThaiTK(!Boolean.TRUE.equals(nd.getTrangThaiTK()));
        nguoiDungRepo.save(nd);
    }

    @Override
    public Map<String, Object> importFromExcel(List<NguoiDungExcelDTO> rows) {
        int success = 0;
        List<String> errors = new ArrayList<>();

        for (NguoiDungExcelDTO row : rows) {
            String rowLabel = "Dòng " + row.getRowNum() + ": ";
            try {
                // Validate
                if (row.getTenDangNhap() == null || row.getTenDangNhap().isBlank()) {
                    errors.add(rowLabel + "Tên đăng nhập trống.");
                    continue;
                }
                if (row.getEmail() == null || row.getEmail().isBlank()) {
                    errors.add(rowLabel + "Email trống.");
                    continue;
                }
                if (nguoiDungRepo.existsByTenDangNhap(row.getTenDangNhap())) {
                    errors.add(rowLabel + "Tên đăng nhập '" + row.getTenDangNhap()
                            + "' đã tồn tại.");
                    continue;
                }
                if (nguoiDungRepo.existsByEmail(row.getEmail())) {
                    errors.add(rowLabel + "Email '" + row.getEmail() + "' đã tồn tại.");
                    continue;
                }

                LoaiNguoiDung loai;
                try {
                    loai = LoaiNguoiDung.valueOf(row.getLoaiNguoiDung());
                } catch (Exception e) {
                    errors.add(rowLabel + "Loại người dùng '" + row.getLoaiNguoiDung()
                            + "' không hợp lệ.");
                    continue;
                }

                NguoiDungDTO dto = new NguoiDungDTO();
                dto.setTenDangNhap(row.getTenDangNhap());
                dto.setMatKhau(row.getMatKhau() != null ? row.getMatKhau() : "Password@123");
                dto.setEmail(row.getEmail());
                dto.setHoTen(row.getHoTen() != null ? row.getHoTen() : row.getTenDangNhap());
                dto.setSoDienThoai(row.getSoDienThoai());
                dto.setLoaiNguoiDung(loai);
                dto.setMaLopHC(row.getMaLopHC());

                // Parse vai tro
                if (row.getVaiTro() != null && !row.getVaiTro().isBlank()) {
                    List<VaiTro> vaiTros = new ArrayList<>();
                    for (String v : row.getVaiTro().split(",")) {
                        try {
                            vaiTros.add(VaiTro.valueOf(v.trim()));
                        } catch (Exception ignored) { /* skip sai vai tro */ }
                    }
                    dto.setVaiTros(vaiTros);
                }

                create(dto);
                success++;
            } catch (Exception e) {
                errors.add(rowLabel + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("errors", errors);
        return result;
    }

    /**
     * Sinh MaNguoiDung moi theo prefix.
     *
     * <p><b>Sua 2026-Q2</b>: truoc day dung {@code count(...) + 1} → gay trung
     * ID sau khi xoa record (vi du xoa SV2025003 roi tao moi → count = 2 →
     * SV2025003 bi tai su dung → vi pham UNIQUE). Nay dung MAX(maNguoiDung)
     * theo prefix, parse so thu tu cuoi, +1. Deterministic va an toan voi
     * soft-delete/re-create.
     *
     * <p><b>Luu y concurrency</b>: neu 2 request goi dong thoi, ca hai co the
     * doc cung MAX va sinh trung ID. Bao ve bang UNIQUE constraint o tang DB
     * (luon co san vi PK) + retry ben controller (caller) hoac chuyen sang
     * dung sequence / auto-increment ID o tuong lai.
     */
    @Override
    public String sinhMaNguoiDung(LoaiNguoiDung loai) {
        return switch (loai) {
            case Admin        -> nextIdWithPrefix("AD", 3);
            case GiangVien    -> nextIdWithPrefix("GV", 3);
            case DoanhNghiep  -> nextIdWithPrefix("DN", 3);
            case SinhVien     -> {
                // SV co them 4 chu so nam trong prefix → "SV2025001"
                String prefix = "SV" + java.time.LocalDate.now().getYear();
                yield nextIdWithPrefix(prefix, 3);
            }
        };
    }

    /**
     * Helper: tinh so thu tu ke tiep cho ma co dang {prefix}{soThuTu}.
     * @param prefix     phan dau co dinh, vd "GV" hoac "SV2025"
     * @param padDigits  do dai phan so (zero-pad)
     */
    private String nextIdWithPrefix(String prefix, int padDigits) {
        String maxMa = nguoiDungRepo.findMaxMaNguoiDungByPrefix(prefix);
        int next = 1;
        if (maxMa != null && maxMa.length() > prefix.length()) {
            try {
                next = Integer.parseInt(maxMa.substring(prefix.length())) + 1;
            } catch (NumberFormatException e) {
                // Record cu co dinh dang khac - fallback ve 1 va log canh bao
                log.warn("Khong parse duoc suffix cua ma '{}' (prefix='{}'), fallback=1",
                        maxMa, prefix);
            }
        }
        return String.format("%s%0" + padDigits + "d", prefix, next);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getThongKe() {
        Map<String, Long> map = new LinkedHashMap<>();
        map.put("tongNguoiDung", nguoiDungRepo.count());
        map.put("giangVien", nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.GiangVien));
        map.put("sinhVien", nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.SinhVien));
        map.put("doanhNghiep", nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.DoanhNghiep));
        return map;
    }
}
