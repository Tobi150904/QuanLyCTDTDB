package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.BcnThanhVienDTO;
import com.ntu.quanlyctdtdb.entity.BcnThanhVien;
import com.ntu.quanlyctdtdb.entity.BcnThanhVienId;
import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.enums.ChucDanhBCN;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.BcnThanhVienRepository;
import com.ntu.quanlyctdtdb.repository.ChuongTrinhDaoTaoRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.service.BcnThanhVienService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BcnThanhVienServiceImpl implements BcnThanhVienService {

    private final BcnThanhVienRepository bcnRepo;
    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final GiangVienRepository giangVienRepo;

    @Override
    @Transactional(readOnly = true)
    public List<BcnThanhVien> findByCtdt(String maCTDT) {
        return bcnRepo.findByCtdtFetchGv(maCTDT);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BcnThanhVien> findChuNhiem(String maCTDT) {
        return bcnRepo.findChuNhiemByCtdt(maCTDT);
    }

    @Override
    public BcnThanhVien themThanhVien(String maCTDT, BcnThanhVienDTO dto) {
        ChuongTrinhDaoTao ctdt = ctdtRepo.findById(maCTDT)
                .orElseThrow(() -> new ResourceNotFoundException("ChuongTrinhDaoTao", "MaCTDT", maCTDT));
        GiangVien gv = giangVienRepo.findById(dto.getMaGV())
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", dto.getMaGV()));

        BcnThanhVienId id = new BcnThanhVienId(maCTDT, dto.getMaGV(), dto.getChucDanh());
        if (bcnRepo.existsById(id)) {
            throw new BusinessException("GV " + dto.getMaGV() + " da o chuc danh " + dto.getChucDanh()
                    + " trong BCN cua CTDT " + maCTDT);
        }

        // Rang buoc: Chu nhiem la duy nhat trong 1 CTDT
        if (dto.getChucDanh() == ChucDanhBCN.ChuNhiem) {
            long count = bcnRepo.countByIdMaCTDTAndIdChucDanh(maCTDT, ChucDanhBCN.ChuNhiem);
            if (count > 0) {
                throw new BusinessException("CTDT " + maCTDT + " da co Chu nhiem. "
                        + "Vui long xoa Chu nhiem cu truoc khi bo nhiem moi.");
            }
        }

        BcnThanhVien bcn = BcnThanhVien.builder()
                .id(id)
                .chuongTrinhDaoTao(ctdt)
                .giangVien(gv)
                .ngayBoNhiem(dto.getNgayBoNhiem() != null ? dto.getNgayBoNhiem() : LocalDate.now())
                .ghiChu(dto.getGhiChu())
                .build();
        log.info("[BCN] Them thanh vien CTDT={} GV={} ChucDanh={}", maCTDT, dto.getMaGV(), dto.getChucDanh());
        return bcnRepo.save(bcn);
    }

    @Override
    public void xoaThanhVien(String maCTDT, String maGV, ChucDanhBCN chucDanh) {
        BcnThanhVienId id = new BcnThanhVienId(maCTDT, maGV, chucDanh);
        if (!bcnRepo.existsById(id)) {
            throw new ResourceNotFoundException("BcnThanhVien", "id",
                    maCTDT + "+" + maGV + "+" + chucDanh);
        }
        bcnRepo.deleteById(id);
        log.info("[BCN] Xoa thanh vien CTDT={} GV={} ChucDanh={}", maCTDT, maGV, chucDanh);
    }
}
