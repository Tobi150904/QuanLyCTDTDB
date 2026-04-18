package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.HocPhanDTO;
import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.service.EmailService;
import com.ntu.quanlyctdtdb.service.HocPhanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HocPhanServiceImpl implements HocPhanService {

    private final HocPhanRepository hocPhanRepo;
    private final GiangVienRepository giangVienRepo;
    private final EmailService emailService;

    @Override
    @Transactional(readOnly = true)
    public List<HocPhan> findAll(String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return hocPhanRepo.findByTenHocPhanContainingIgnoreCase(keyword.trim());
        }
        return hocPhanRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public HocPhan findById(String ma) {
        return hocPhanRepo.findById(ma)
                .orElseThrow(() -> new ResourceNotFoundException("HocPhan", "MaHocPhan", ma));
    }

    @Override
    public HocPhan create(HocPhanDTO dto, String maNguoiDungTao) {
        GiangVien chuNhiem = giangVienRepo.findById(dto.getMaChuNhiemHP())
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", dto.getMaChuNhiemHP()));

        // Sinh ma hoc phan tu dong: HP + 3 so
        long count = hocPhanRepo.count();
        String maHP = String.format("HP%03d", count + 1);

        HocPhan hp = HocPhan.builder()
                .maHocPhan(maHP)
                .tenHocPhan(dto.getTenHocPhan().trim())
                .soTinChi(dto.getSoTinChi())
                .loaiHocPhan(dto.getLoaiHocPhan())
                .chuNhiemHP(chuNhiem)
                .trangThai(TrangThaiHocPhan.ChuaDuyet)
                .build();
        return hocPhanRepo.save(hp);
    }

    @Override
    public HocPhan update(String ma, HocPhanDTO dto) {
        HocPhan hp = findById(ma);
        if (hp.getTrangThai() == TrangThaiHocPhan.DaDuyet) {
            throw new BusinessException("Khong the chinh sua hoc phan da duoc phe duyet");
        }
        hp.setTenHocPhan(dto.getTenHocPhan().trim());
        hp.setSoTinChi(dto.getSoTinChi());
        hp.setLoaiHocPhan(dto.getLoaiHocPhan());

        if (dto.getMaChuNhiemHP() != null) {
            GiangVien chuNhiem = giangVienRepo.findById(dto.getMaChuNhiemHP())
                    .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", dto.getMaChuNhiemHP()));
            hp.setChuNhiemHP(chuNhiem);
        }

        hp.setTrangThai(TrangThaiHocPhan.ChuaDuyet); // reset neu da bi tu choi
        return hocPhanRepo.save(hp);
    }

    @Override
    public HocPhan pheduyet(String ma, String maNguoiDungDuyet) {
        HocPhan hp = findById(ma);
        if (hp.getTrangThai() != TrangThaiHocPhan.ChuaDuyet) {
            throw new BusinessException("Chi co the phe duyet hoc phan o trang thai ChuaDuyet");
        }
        hp.setTrangThai(TrangThaiHocPhan.DaDuyet);
        hocPhanRepo.save(hp);

        // Gui email thong bao CNHP
        try {
            String emailCNHP = hp.getChuNhiemHP().getNguoiDung().getEmail();
            emailService.guiPheDuyetHocPhan(emailCNHP, hp.getMaHocPhan(),
                    hp.getTenHocPhan(), LocalDate.now().toString());
        } catch (Exception e) {
            log.warn("Khong gui duoc email phe duyet HP {}: {}", ma, e.getMessage());
        }
        return hp;
    }

    @Override
    public HocPhan tuChoi(String ma, String lyDo, String maNguoiDungTuChoi) {
        HocPhan hp = findById(ma);
        if (hp.getTrangThai() != TrangThaiHocPhan.ChuaDuyet) {
            throw new BusinessException("Chi co the tu choi hoc phan o trang thai ChuaDuyet");
        }
        hp.setTrangThai(TrangThaiHocPhan.TuChoi);
        hocPhanRepo.save(hp);

        // Gui email thong bao CNHP
        try {
            String emailCNHP = hp.getChuNhiemHP().getNguoiDung().getEmail();
            emailService.guiTuChoiHocPhan(emailCNHP, hp.getMaHocPhan(), hp.getTenHocPhan(), lyDo);
        } catch (Exception e) {
            log.warn("Khong gui duoc email tu choi HP {}: {}", ma, e.getMessage());
        }
        return hp;
    }

    @Override
    public HocPhan toggleTrangThai(String ma) {
        HocPhan hp = findById(ma);
        hp.setTrangThai(hp.getTrangThai() == TrangThaiHocPhan.HetHieuLuc
                ? TrangThaiHocPhan.DaDuyet
                : TrangThaiHocPhan.HetHieuLuc);
        return hocPhanRepo.save(hp);
    }

    @Override
    public HocPhan uploadDeCuong(String ma, String tenFile) {
        HocPhan hp = findById(ma);
        hp.setFileDeCuong(tenFile);
        return hocPhanRepo.save(hp);
    }
}
