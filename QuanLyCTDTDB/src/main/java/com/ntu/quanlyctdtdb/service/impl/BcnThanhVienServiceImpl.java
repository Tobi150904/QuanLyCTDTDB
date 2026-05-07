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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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
        return bcnRepo.findByCtdtFetch(maCTDT);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BcnThanhVien> findChuNhiem(String maCTDT) {
        return bcnRepo.findFirstByChuongTrinhDaoTao_MaCTDTAndId_ChucDanh(
                maCTDT, ChucDanhBCN.ChuNhiem);
    }

    @Override
    public BcnThanhVien themThanhVien(String maCTDT, BcnThanhVienDTO dto) {
        ChuongTrinhDaoTao ctdt = ctdtRepo.findById(maCTDT)
                .orElseThrow(() -> new ResourceNotFoundException("ChuongTrinhDaoTao", "MaCTDT", maCTDT));
        GiangVien gv = giangVienRepo.findById(dto.getMaGV())
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", dto.getMaGV()));

        BcnThanhVienId id = new BcnThanhVienId(maCTDT, dto.getMaGV(), dto.getChucDanh());
        if (bcnRepo.existsById(id)) {
            throw new BusinessException("GV " + dto.getMaGV()
                    + " da la " + dto.getChucDanh() + " cua CTDT " + maCTDT);
        }

        // Rule: 1 CTDT chi 1 ChuNhiem. Neu chuc danh la ChuNhiem va da ton tai
        // chu nhiem khac thi reject — BCN can chinh thuc mien nhiem truoc.
        if (dto.getChucDanh() == ChucDanhBCN.ChuNhiem) {
            bcnRepo.findFirstByChuongTrinhDaoTao_MaCTDTAndId_ChucDanh(maCTDT, ChucDanhBCN.ChuNhiem)
                    .ifPresent(existed -> {
                        throw new BusinessException(
                                "CTDT " + maCTDT + " da co Chu Nhiem (GV " + existed.getId().getMaGV()
                                + "). Hay xoa chu nhiem cu truoc khi gan moi.");
                    });
        }

        BcnThanhVien tv = BcnThanhVien.builder()
                .id(id)
                .chuongTrinhDaoTao(ctdt)
                .giangVien(gv)
                .ngayBoNhiem(dto.getNgayBoNhiem() != null ? dto.getNgayBoNhiem() : LocalDate.now())
                .ghiChu(dto.getGhiChu())
                .build();
        return bcnRepo.save(tv);
    }

    @Override
    public void xoaThanhVien(String maCTDT, String maGV, ChucDanhBCN chucDanh) {
        BcnThanhVienId id = new BcnThanhVienId(maCTDT, maGV, chucDanh);
        if (!bcnRepo.existsById(id)) {
            throw new ResourceNotFoundException(
                    "BcnThanhVien", "id", maCTDT + "+" + maGV + "+" + chucDanh);
        }
        bcnRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean laChuNhiem(String maCTDT, String maGV) {
        BcnThanhVienId id = new BcnThanhVienId(maCTDT, maGV, ChucDanhBCN.ChuNhiem);
        return bcnRepo.existsById(id);
    }
}
