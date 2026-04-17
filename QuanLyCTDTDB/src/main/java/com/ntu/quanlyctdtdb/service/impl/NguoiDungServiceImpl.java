package com.ntu.quanlyctdtdb.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ntu.quanlyctdtdb.dto.DoiMatKhauDTO;
import com.ntu.quanlyctdtdb.dto.NguoiDungDTO;
import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.entity.NguoiDungVaiTro;
import com.ntu.quanlyctdtdb.entity.NguoiDungVaiTroId;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import com.ntu.quanlyctdtdb.repository.NguoiDungRepository;
import com.ntu.quanlyctdtdb.repository.NguoiDungVaiTroRepository;
import com.ntu.quanlyctdtdb.service.NguoiDungService;
import com.ntu.quanlyctdtdb.util.ExcelImportUtil;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NguoiDungServiceImpl implements NguoiDungService {

    private final NguoiDungRepository nguoiDungRepository;
    private final NguoiDungVaiTroRepository vaiTroRepository;
    private final LopHanhChinhRepository lopHanhChinhRepository;
    private final PasswordEncoder passwordEncoder;
    private final ExcelImportUtil excelImportUtil;

    @Override
    public Page<NguoiDung> findAll(String keyword, VaiTro vaiTro, Pageable pageable) {
        if (vaiTro != null && keyword != null && !keyword.isBlank()) {
            return nguoiDungRepository.searchByVaiTroAndKeyword(vaiTro, keyword, pageable);
        }
        if (vaiTro != null) {
            // Lay tat ca theo vai tro, boc vao Page
            List<NguoiDung> list = nguoiDungRepository.findByVaiTro(vaiTro);
            int start = (int) pageable.getOffset();
            int end   = Math.min(start + pageable.getPageSize(), list.size());
            List<NguoiDung> sub = list.subList(start, end);
            return new org.springframework.data.domain.PageImpl<>(sub, pageable, list.size());
        }
        if (keyword != null && !keyword.isBlank()) {
            return nguoiDungRepository.searchByKeyword(keyword, pageable);
        }
        return nguoiDungRepository.findAllByOrderByMaNguoiDungAsc(pageable);
    }

    @Override
    public NguoiDung findById(String maNguoiDung) {
        return nguoiDungRepository.findById(maNguoiDung)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay nguoi dung voi ma: " + maNguoiDung));
    }

    @Override
    @Transactional
    public NguoiDung create(NguoiDungDTO dto) {
        // Kiem tra unique
        if (nguoiDungRepository.existsById(dto.getMaNguoiDung())) {
            throw new BusinessException("Ma nguoi dung '" + dto.getMaNguoiDung() + "' da ton tai.");
        }
        if (nguoiDungRepository.existsByTenDangNhap(dto.getTenDangNhap())) {
            throw new BusinessException("Ten dang nhap '" + dto.getTenDangNhap() + "' da duoc su dung.");
        }
        if (nguoiDungRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email '" + dto.getEmail() + "' da duoc su dung.");
        }

        NguoiDung nd = mapDtoToEntity(dto, new NguoiDung());
        String matKhauRaw = (dto.getMatKhau() != null && !dto.getMatKhau().isBlank())
                ? dto.getMatKhau() : dto.getMaNguoiDung();
        nd.setMatKhauHash(passwordEncoder.encode(matKhauRaw));
        NguoiDung saved = nguoiDungRepository.save(nd);

        // Gan vai tro
        ganVaiTro(saved, dto.getVaiTros());
        return saved;
    }

    @Override
    @Transactional
    public NguoiDung update(String maNguoiDung, NguoiDungDTO dto) {
        NguoiDung nd = findById(maNguoiDung);

        // Kiem tra email trung (tru chinh no)
        if (!nd.getEmail().equalsIgnoreCase(dto.getEmail())
                && nguoiDungRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Email '" + dto.getEmail() + "' da duoc su dung boi nguoi khac.");
        }

        mapDtoToEntity(dto, nd);
        // Khong doi mat khau o day
        nguoiDungRepository.save(nd);

        // Cap nhat vai tro: xoa cu, them moi
        vaiTroRepository.deleteByNguoiDung_MaNguoiDung(maNguoiDung);
        ganVaiTro(nd, dto.getVaiTros());
        return nd;
    }

    @Override
    @Transactional
    public void doiTrangThaiTK(String maNguoiDung, boolean trangThai) {
        NguoiDung nd = findById(maNguoiDung);
        nd.setTrangThaiTK(trangThai);
        nguoiDungRepository.save(nd);
    }

    @Override
    @Transactional
    public void doiMatKhau(String maNguoiDung, DoiMatKhauDTO dto, boolean isAdminReset) {
        NguoiDung nd = findById(maNguoiDung);
        if (!isAdminReset) {
            if (!passwordEncoder.matches(dto.getMatKhauHienTai(), nd.getMatKhauHash())) {
                throw new BusinessException("Mat khau hien tai khong dung.");
            }
        }
        if (!dto.getMatKhauMoi().equals(dto.getXacNhanMatKhau())) {
            throw new BusinessException("Mat khau moi va xac nhan mat khau khong khop.");
        }
        nd.setMatKhauHash(passwordEncoder.encode(dto.getMatKhauMoi()));
        nguoiDungRepository.save(nd);
    }

    @Override
    @Transactional
    public int importFromExcel(MultipartFile file) {
        List<NguoiDungDTO> dtos = excelImportUtil.readNguoiDung(file);
        int count = 0;
        for (NguoiDungDTO dto : dtos) {
            // Skip neu ma da ton tai (khong throw, bo qua va tiep tuc)
            if (nguoiDungRepository.existsById(dto.getMaNguoiDung())) continue;
            if (nguoiDungRepository.existsByEmail(dto.getEmail())) continue;
            create(dto);
            count++;
        }
        return count;
    }

    @Override
    public List<NguoiDung> findAllGiangVien() {
        return nguoiDungRepository.findByVaiTro(VaiTro.GV);
    }

    @Override
    public List<NguoiDung> findSVByLopHanhChinh(String maLopHC) {
        return nguoiDungRepository.findByLopHanhChinh_MaLopHC(maLopHC);
    }

    // ---- Private helpers ----

    private NguoiDung mapDtoToEntity(NguoiDungDTO dto, NguoiDung nd) {
        nd.setMaNguoiDung(dto.getMaNguoiDung());
        nd.setHoTen(dto.getHoTen());
        nd.setEmail(dto.getEmail());
        nd.setTenDangNhap(dto.getTenDangNhap());
        nd.setSoDienThoai(dto.getSoDienThoai());
        nd.setTrangThaiTK(dto.getTrangThaiTK() != null ? dto.getTrangThaiTK() : true);

        if (dto.getTrangThaiSV() != null) {
            nd.setTrangThaiSV(dto.getTrangThaiSV());
        }

        // Gan lop hanh chinh neu co
        if (dto.getMaLopHC() != null && !dto.getMaLopHC().isBlank()) {
            LopHanhChinh lhc = lopHanhChinhRepository.findById(dto.getMaLopHC())
                    .orElseThrow(() -> new BusinessException(
                            "Lop hanh chinh '" + dto.getMaLopHC() + "' khong ton tai."));
            nd.setLopHanhChinh(lhc);
        } else {
            nd.setLopHanhChinh(null);
        }
        return nd;
    }

    private void ganVaiTro(NguoiDung nd, List<VaiTro> vaiTros) {
        if (vaiTros == null || vaiTros.isEmpty()) return;
        for (VaiTro vt : vaiTros) {
            NguoiDungVaiTroId id = new NguoiDungVaiTroId();
            id.setMaNguoiDung(nd.getMaNguoiDung());
            id.setVaiTro(vt);

            NguoiDungVaiTro ndvt = new NguoiDungVaiTro();
            ndvt.setId(id);
            ndvt.setNguoiDung(nd);
            vaiTroRepository.save(ndvt);
        }
    }
}
