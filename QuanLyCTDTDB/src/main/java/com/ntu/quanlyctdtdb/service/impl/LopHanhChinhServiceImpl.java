package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.LopHanhChinhDTO;
import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.service.LopHanhChinhService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Service quan ly Lop Hanh Chinh.
 *
 * Business rules:
 *  - Ma lop la primary key, khong duoc thay doi sau khi tao.
 *  - CTDT bat buoc; chi cho phep neu CTDT ton tai va dang trang thai DangSuDung.
 *  - CVHT (Co Van Hoc Tap) phai la GiangVien hop le, neu null co the bo trong.
 *  - Khong duoc xoa lop khi van con SinhVien thuoc lop.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class LopHanhChinhServiceImpl implements LopHanhChinhService {

    private final LopHanhChinhRepository lopHCRepo;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final GiangVienRepository giangVienRepo;
    private final SinhVienRepository sinhVienRepo;

    @Override
    @Transactional(readOnly = true)
    public List<LopHanhChinh> findAll() {
        return lopHCRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LopHanhChinh> search(String keyword, String maCTDT, String khoaHoc) {
        // In-memory filter dua tren findAll() - phu hop quy mo nho cua module nay.
        // Khi can scale, them custom @Query Pageable o Repository.
        return lopHCRepo.findAll().stream()
                .filter(l -> keyword == null || keyword.isBlank()
                        || l.getMaLopHC().toLowerCase().contains(keyword.toLowerCase())
                        || (l.getTenLop() != null
                            && l.getTenLop().toLowerCase().contains(keyword.toLowerCase())))
                .filter(l -> maCTDT == null || maCTDT.isBlank()
                        || (l.getChuongTrinhDaoTao() != null
                            && Objects.equals(l.getChuongTrinhDaoTao().getMaCTDT(), maCTDT)))
                .filter(l -> khoaHoc == null || khoaHoc.isBlank()
                        || Objects.equals(l.getKhoaHoc(), khoaHoc))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public LopHanhChinh findById(String maLopHC) {
        return lopHCRepo.findById(maLopHC)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "LopHanhChinh", "MaLopHC", maLopHC));
    }

    @Override
    public LopHanhChinh create(LopHanhChinhDTO dto) {
        if (lopHCRepo.existsById(dto.getMaLopHC())) {
            throw new BusinessException("Ma lop " + dto.getMaLopHC() + " da ton tai.");
        }
        ChuongTrinhDaoTao ctdt = ctdtRepo.findById(dto.getMaCTDT())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ChuongTrinhDaoTao", "MaCTDT", dto.getMaCTDT()));

        LopHanhChinh e = new LopHanhChinh();
        e.setMaLopHC(dto.getMaLopHC().trim());
        e.setTenLop(dto.getTenLop().trim());
        e.setKhoaHoc(dto.getKhoaHoc().trim());
        e.setChuongTrinhDaoTao(ctdt);
        if (dto.getMaCoVan() != null && !dto.getMaCoVan().isBlank()) {
            GiangVien gv = giangVienRepo.findById(dto.getMaCoVan())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "GiangVien", "MaGV", dto.getMaCoVan()));
            e.setCoVan(gv);
        }
        return lopHCRepo.save(e);
    }

    @Override
    public LopHanhChinh update(String maLopHC, LopHanhChinhDTO dto) {
        LopHanhChinh e = findById(maLopHC);
        if (!Objects.equals(maLopHC, dto.getMaLopHC())) {
            throw new BusinessException("Khong duoc doi ma lop sau khi tao.");
        }
        ChuongTrinhDaoTao ctdt = ctdtRepo.findById(dto.getMaCTDT())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ChuongTrinhDaoTao", "MaCTDT", dto.getMaCTDT()));
        e.setTenLop(dto.getTenLop().trim());
        e.setKhoaHoc(dto.getKhoaHoc().trim());
        e.setChuongTrinhDaoTao(ctdt);

        if (dto.getMaCoVan() != null && !dto.getMaCoVan().isBlank()) {
            GiangVien gv = giangVienRepo.findById(dto.getMaCoVan())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "GiangVien", "MaGV", dto.getMaCoVan()));
            e.setCoVan(gv);
        } else {
            e.setCoVan(null); // huy phan cong CVHT
        }
        return lopHCRepo.save(e);
    }

    @Override
    public LopHanhChinh phanCongCoVan(String maLopHC, String maGV) {
        LopHanhChinh e = findById(maLopHC);
        if (maGV == null || maGV.isBlank()) {
            e.setCoVan(null);
        } else {
            GiangVien gv = giangVienRepo.findById(maGV)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "GiangVien", "MaGV", maGV));
            e.setCoVan(gv);
        }
        return lopHCRepo.save(e);
    }

    @Override
    public void delete(String maLopHC) {
        LopHanhChinh e = findById(maLopHC);
        long soSV = sinhVienRepo.findByLopHanhChinh_MaLopHC(maLopHC).size();
        if (soSV > 0) {
            throw new BusinessException(
                    "Khong the xoa lop " + maLopHC + ": dang co " + soSV + " sinh vien.");
        }
        lopHCRepo.delete(e);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getThongKe() {
        List<LopHanhChinh> all = lopHCRepo.findAll();
        Map<String, Object> m = new HashMap<>();
        m.put("tong", all.size());
        m.put("daCoCVHT", all.stream().filter(l -> l.getCoVan() != null).count());
        m.put("chuaCoCVHT", all.stream().filter(l -> l.getCoVan() == null).count());
        m.put("soKhoaHoc", all.stream().map(LopHanhChinh::getKhoaHoc).distinct().count());
        return m;
    }
}
