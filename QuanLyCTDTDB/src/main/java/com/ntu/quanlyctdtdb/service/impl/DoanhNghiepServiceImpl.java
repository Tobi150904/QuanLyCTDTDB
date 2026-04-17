package com.ntu.quanlyctdtdb.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntu.quanlyctdtdb.dto.DoanhNghiepDTO;
import com.ntu.quanlyctdtdb.entity.DoanhNghiep;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.entity.NguoiDungVaiTro;
import com.ntu.quanlyctdtdb.entity.NguoiDungVaiTroId;
import com.ntu.quanlyctdtdb.enums.TrangThaiDoanhNghiep;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.DoanhNghiepRepository;
import com.ntu.quanlyctdtdb.repository.NguoiDungRepository;
import com.ntu.quanlyctdtdb.repository.NguoiDungVaiTroRepository;
import com.ntu.quanlyctdtdb.service.DoanhNghiepService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoanhNghiepServiceImpl implements DoanhNghiepService {

    private final DoanhNghiepRepository doanhNghiepRepository;
    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungVaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<DoanhNghiep> findAll(String keyword, TrangThaiDoanhNghiep trangThai, Pageable pageable) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        boolean hasTrangThai = trangThai != null;

        if (hasTrangThai && hasKeyword) {
            return doanhNghiepRepository
                    .findByTrangThaiAndTenDoanhNghiepContainingIgnoreCase(trangThai, keyword, pageable);
        }
        if (hasTrangThai) {
            // Wrap List -> Page
            List<DoanhNghiep> list = doanhNghiepRepository.findByTrangThai(trangThai);
            int start = (int) pageable.getOffset();
            int end   = Math.min(start + pageable.getPageSize(), list.size());
            return new org.springframework.data.domain.PageImpl<>(
                    list.subList(start, end), pageable, list.size());
        }
        if (hasKeyword) {
            return doanhNghiepRepository
                    .findByTenDoanhNghiepContainingIgnoreCase(keyword, pageable);
        }
        return doanhNghiepRepository.findAll(pageable);
    }

    @Override
    public DoanhNghiep findById(String maDoanhNghiep) {
        return doanhNghiepRepository.findById(maDoanhNghiep)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay doanh nghiep voi ma: " + maDoanhNghiep));
    }

    /**
     * Tao moi Doanh Nghiep.
     * Side effect: Tu dong tao NguoiDung voi VaiTro=DN trong cung transaction.
     * - MaNguoiDung = MaDoanhNghiep
     * - TenDangNhap = MaDoanhNghiep
     * - MatKhau mac dinh = BCrypt(MaDoanhNghiep)
     * - Email = emailDN cua DoanhNghiep
     */
    @Override
    @Transactional
    public DoanhNghiep create(DoanhNghiepDTO dto) {
        // Kiem tra unique
        if (doanhNghiepRepository.existsById(dto.getMaDoanhNghiep())) {
            throw new BusinessException("Ma doanh nghiep '" + dto.getMaDoanhNghiep() + "' da ton tai.");
        }
        if (nguoiDungRepository.existsById(dto.getMaDoanhNghiep())) {
            throw new BusinessException(
                    "Ma tai khoan '" + dto.getMaDoanhNghiep() + "' da duoc su dung boi nguoi dung khac.");
        }
        if (dto.getEmailDN() != null && !dto.getEmailDN().isBlank()
                && nguoiDungRepository.existsByEmail(dto.getEmailDN())) {
            throw new BusinessException("Email '" + dto.getEmailDN() + "' da duoc dang ky.");
        }

        // Luu Doanh Nghiep
        DoanhNghiep dn = mapDtoToEntity(dto, new DoanhNghiep());
        DoanhNghiep savedDN = doanhNghiepRepository.save(dn);

        // Tu dong tao NguoiDung voi VaiTro=DN
        NguoiDung taiKhoanDN = new NguoiDung();
        taiKhoanDN.setMaNguoiDung(dto.getMaDoanhNghiep());
        taiKhoanDN.setTenDangNhap(dto.getMaDoanhNghiep());
        taiKhoanDN.setHoTen(dto.getTenDoanhNghiep());
        taiKhoanDN.setEmail(dto.getEmailDN() != null ? dto.getEmailDN() : dto.getMaDoanhNghiep() + "@dn.ntu.edu.vn");
        taiKhoanDN.setMatKhauHash(passwordEncoder.encode(dto.getMaDoanhNghiep()));
        taiKhoanDN.setTrangThaiTK(true);
        taiKhoanDN.setDoanhNghiep(savedDN);
        NguoiDung savedND = nguoiDungRepository.save(taiKhoanDN);

        // Gan vai tro DN
        NguoiDungVaiTroId vaiTroId = new NguoiDungVaiTroId();
        vaiTroId.setMaNguoiDung(savedND.getMaNguoiDung());
        vaiTroId.setVaiTro(VaiTro.DN);
        NguoiDungVaiTro vt = new NguoiDungVaiTro();
        vt.setId(vaiTroId);
        vt.setNguoiDung(savedND);
        vaiTroRepository.save(vt);

        return savedDN;
    }

    @Override
    @Transactional
    public DoanhNghiep update(String maDoanhNghiep, DoanhNghiepDTO dto) {
        DoanhNghiep dn = findById(maDoanhNghiep);

        // Kiem tra email trung (tru chinh no)
        if (dto.getEmailDN() != null && !dto.getEmailDN().isBlank()) {
            String emailCu = dn.getEmailDN();
            if (!dto.getEmailDN().equalsIgnoreCase(emailCu)
                    && nguoiDungRepository.existsByEmail(dto.getEmailDN())) {
                throw new BusinessException(
                        "Email '" + dto.getEmailDN() + "' da duoc su dung boi nguoi khac.");
            }
        }

        mapDtoToEntity(dto, dn);
        doanhNghiepRepository.save(dn);

        // Dong bo email sang tai khoan NguoiDung tuong ung
        nguoiDungRepository.findById(maDoanhNghiep).ifPresent(nd -> {
            nd.setHoTen(dto.getTenDoanhNghiep());
            if (dto.getEmailDN() != null && !dto.getEmailDN().isBlank()) {
                nd.setEmail(dto.getEmailDN());
            }
            nguoiDungRepository.save(nd);
        });

        return dn;
    }

    /**
     * Doi trang thai hop tac.
     * Khi TamNgung -> khoa luon tai khoan NguoiDung DN.
     * Khi DangHopTac -> mo khoa lai tai khoan NguoiDung DN.
     */
    @Override
    @Transactional
    public void doiTrangThai(String maDoanhNghiep, TrangThaiDoanhNghiep trangThai) {
        DoanhNghiep dn = findById(maDoanhNghiep);
        dn.setTrangThai(trangThai);
        doanhNghiepRepository.save(dn);

        // Khoa / mo khoa tai khoan DN tuong ung
        nguoiDungRepository.findById(maDoanhNghiep).ifPresent(nd -> {
            nd.setTrangThaiTK(trangThai == TrangThaiDoanhNghiep.DangHopTac);
            nguoiDungRepository.save(nd);
        });
    }

    @Override
    public List<DoanhNghiep> findAllDangHopTac() {
        return doanhNghiepRepository.findByTrangThai(TrangThaiDoanhNghiep.DangHopTac);
    }

    // ---- Private helpers ----

    private DoanhNghiep mapDtoToEntity(DoanhNghiepDTO dto, DoanhNghiep dn) {
        dn.setMaDoanhNghiep(dto.getMaDoanhNghiep());
        dn.setTenDoanhNghiep(dto.getTenDoanhNghiep());
        dn.setLinhVucHoatDong(dto.getLinhVucHoatDong());
        dn.setNguoiDaiDien(dto.getNguoiDaiDien());
        dn.setEmailDN(dto.getEmailDN());
        dn.setSoDienThoaiDN(dto.getSoDienThoaiDN());
        dn.setDiaChiDN(dto.getDiaChiDN());
        dn.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : TrangThaiDoanhNghiep.DangHopTac);
        return dn;
    }
}
