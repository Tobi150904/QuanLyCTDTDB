package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.HocPhanDTO;
import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.service.EmailService;
import com.ntu.quanlyctdtdb.service.HocPhanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        // Luon fetch eager ChuNhiemHP + NguoiDung vi template hien thi hoTen GV
        // (open-in-view=false nen phai load trong transaction).
        if (keyword != null && !keyword.isBlank()) {
            return hocPhanRepo.searchFetchChuNhiem(keyword.trim());
        }
        return hocPhanRepo.findAllFetchChuNhiem();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HocPhan> findPaged(String keyword, LoaiHocPhan loai,
                                    TrangThaiHocPhan trangThai, Pageable pageable) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        return hocPhanRepo.searchPaged(kw, loai, trangThai, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HocPhan> findForExport(String keyword, LoaiHocPhan loai,
                                        TrangThaiHocPhan trangThai) {
        // Dung Pageable.unpaged() voi sort co dinh theo maHocPhan de file CSV
        // co thu tu deterministic (tranh phu thuoc thu tu xuat hien tu DB).
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        Pageable all = org.springframework.data.domain.PageRequest.of(
                0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("maHocPhan")));
        return hocPhanRepo.searchPaged(kw, loai, trangThai, all).getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public HocPhan findById(String ma) {
        // Dung findByIdFetch de load ChuNhiemHP + NguoiDung mot lan trong
        // transaction — tranh LazyInitializationException khi template
        // hoc-phan/chi-tiet goi hocPhan.chuNhiemHP.hoTen (open-in-view=false).
        return hocPhanRepo.findByIdFetch(ma)
                .orElseThrow(() -> new ResourceNotFoundException("HocPhan", "MaHocPhan", ma));
    }

    @Override
    public HocPhan create(HocPhanDTO dto, String maNguoiDungTao) {
        GiangVien chuNhiem = giangVienRepo.findById(dto.getMaChuNhiemHP())
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", dto.getMaChuNhiemHP()));

        // Ma hoc phan: UU TIEN gia tri user nhap o form (vd "HP-LTW", "HP-CSDL"
        // theo quy uoc docs/02 §1 "HP + MA-THE"). Neu de trong thi fallback
        // sang pattern HP + 3 so tang dan.
        String maHP;
        String maFromDto = dto.getMaHocPhan();
        if (maFromDto != null && !maFromDto.isBlank()) {
            maHP = maFromDto.trim();
            if (hocPhanRepo.existsById(maHP)) {
                throw new BusinessException("Ma hoc phan da ton tai: " + maHP);
            }
        } else {
            long count = hocPhanRepo.count();
            maHP = String.format("HP%03d", count + 1);
            // Phong truong hop xung dot khi seed da dung HP001..HP00N
            while (hocPhanRepo.existsById(maHP)) {
                count++;
                maHP = String.format("HP%03d", count + 1);
            }
        }

        HocPhan hp = HocPhan.builder()
                .maHocPhan(maHP)
                .tenHocPhan(dto.getTenHocPhan().trim())
                .soTinChi(dto.getSoTinChi())
                .loaiHocPhan(dto.getLoaiHocPhan())
                .chuNhiemHP(chuNhiem)
                .trangThai(TrangThaiHocPhan.BanNhap)
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

        hp.setTrangThai(TrangThaiHocPhan.BanNhap); // reset ve BanNhap de trinh duyet lai
        return hocPhanRepo.save(hp);
    }

    @Override
    public HocPhan guiChoDuyet(String ma) {
        HocPhan hp = findById(ma);
        if (hp.getTrangThai() != TrangThaiHocPhan.BanNhap) {
            throw new BusinessException("Chi co the gui cho duyet hoc phan o trang thai BanNhap");
        }
        hp.setTrangThai(TrangThaiHocPhan.ChoDuyet);
        return hocPhanRepo.save(hp);
    }

    @Override
    public HocPhan pheduyet(String ma, String maNguoiDungDuyet) {
        HocPhan hp = findById(ma);
        if (hp.getTrangThai() != TrangThaiHocPhan.ChoDuyet) {
            throw new BusinessException("Chi co the phe duyet hoc phan o trang thai ChoDuyet");
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
        if (hp.getTrangThai() != TrangThaiHocPhan.ChoDuyet) {
            throw new BusinessException("Chi co the tu choi hoc phan o trang thai ChoDuyet");
        }
        hp.setTrangThai(TrangThaiHocPhan.BanNhap); // tra ve BanNhap de CNHP chinh sua lai
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
        // Toggle giua DaDuyet va BanNhap (vo hieu hoa / kich hoat lai)
        hp.setTrangThai(hp.getTrangThai() == TrangThaiHocPhan.DaDuyet
                ? TrangThaiHocPhan.BanNhap
                : TrangThaiHocPhan.DaDuyet);
        return hocPhanRepo.save(hp);
    }

    @Override
    public HocPhan uploadDeCuong(String ma, String tenFile) {
        HocPhan hp = findById(ma);
        hp.setFileDeCuong(tenFile);
        return hocPhanRepo.save(hp);
    }
}
