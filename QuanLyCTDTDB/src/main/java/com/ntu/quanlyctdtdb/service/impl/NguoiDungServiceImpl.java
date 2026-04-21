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
            throw new BusinessException("Ten dang nhap da ton tai: " + dto.getTenDangNhap());
        }
        if (nguoiDungRepo.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email da ton tai: " + dto.getEmail());
        }
        if (dto.getMatKhau() == null || dto.getMatKhau().isBlank()) {
            throw new BusinessException("Mat khau khong duoc de trong khi tao moi");
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
                throw new BusinessException("Sinh vien phai co lop hanh chinh");
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
            throw new BusinessException("Email da ton tai: " + dto.getEmail());
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
            try {
                // Validate
                if (row.getTenDangNhap() == null || row.getTenDangNhap().isBlank()) {
                    errors.add("Dong " + row.getRowNum() + ": TenDangNhap trong");
                    continue;
                }
                if (row.getEmail() == null || row.getEmail().isBlank()) {
                    errors.add("Dong " + row.getRowNum() + ": Email trong");
                    continue;
                }
                if (nguoiDungRepo.existsByTenDangNhap(row.getTenDangNhap())) {
                    errors.add("Dong " + row.getRowNum() + ": TenDangNhap '" +
                               row.getTenDangNhap() + "' da ton tai");
                    continue;
                }
                if (nguoiDungRepo.existsByEmail(row.getEmail())) {
                    errors.add("Dong " + row.getRowNum() + ": Email '" +
                               row.getEmail() + "' da ton tai");
                    continue;
                }

                LoaiNguoiDung loai;
                try {
                    loai = LoaiNguoiDung.valueOf(row.getLoaiNguoiDung());
                } catch (Exception e) {
                    errors.add("Dong " + row.getRowNum() + ": LoaiNguoiDung '" +
                               row.getLoaiNguoiDung() + "' khong hop le");
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
                errors.add("Dong " + row.getRowNum() + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        result.put("errors", errors);
        return result;
    }

    @Override
    public String sinhMaNguoiDung(LoaiNguoiDung loai) {
        return switch (loai) {
            case Admin -> {
                long count = nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.Admin);
                yield String.format("AD%03d", count + 1);
            }
            case GiangVien -> {
                long count = nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.GiangVien);
                yield String.format("GV%03d", count + 1);
            }
            case SinhVien -> {
                long count = nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.SinhVien);
                int year = java.time.LocalDate.now().getYear();
                yield String.format("SV%d%03d", year, count + 1);
            }
            case DoanhNghiep -> {
                long count = nguoiDungRepo.countByLoaiNguoiDung(LoaiNguoiDung.DoanhNghiep);
                yield String.format("DN%03d", count + 1);
            }
        };
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
