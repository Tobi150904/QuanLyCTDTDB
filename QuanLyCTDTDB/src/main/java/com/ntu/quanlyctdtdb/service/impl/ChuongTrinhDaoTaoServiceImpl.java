package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.ChuongTrinhDaoTaoDTO;
import com.ntu.quanlyctdtdb.dto.CtdtHocPhanDTO;
import com.ntu.quanlyctdtdb.entity.*;
import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.*;
import com.ntu.quanlyctdtdb.service.ChuongTrinhDaoTaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChuongTrinhDaoTaoServiceImpl implements ChuongTrinhDaoTaoService {

    private final ChuongTrinhDaoTaoRepository ctdtRepo;
    private final CtdtHocPhanRepository ctdtHocPhanRepo;
    private final HocPhanRepository hocPhanRepo;
    private final NguoiDungRepository nguoiDungRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ChuongTrinhDaoTao> findAll() {
        return ctdtRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ChuongTrinhDaoTao findById(String ma) {
        return ctdtRepo.findById(ma)
                .orElseThrow(() -> new ResourceNotFoundException("ChuongTrinhDaoTao", "MaCTDT", ma));
    }

    @Override
    public ChuongTrinhDaoTao create(ChuongTrinhDaoTaoDTO dto, String maNguoiDungTao) {
        if (ctdtRepo.existsById(dto.getMaCTDT())) {
            throw new BusinessException("Ma CTDT da ton tai: " + dto.getMaCTDT());
        }
        NguoiDung nguoiTao = nguoiDungRepo.findById(maNguoiDungTao)
                .orElseThrow(() -> new ResourceNotFoundException("NguoiDung", "Ma", maNguoiDungTao));

        ChuongTrinhDaoTao ctdt = ChuongTrinhDaoTao.builder()
                .maCTDT(dto.getMaCTDT())
                .tenCTDT(dto.getTenCTDT().trim())
                .khoa(dto.getKhoa())
                .trangThai(TrangThaiCTDT.BanNhap)
                .nguoiTao(nguoiTao)
                .build();
        return ctdtRepo.save(ctdt);
    }

    @Override
    public ChuongTrinhDaoTao update(String ma, ChuongTrinhDaoTaoDTO dto) {
        ChuongTrinhDaoTao ctdt = findById(ma);
        if (ctdt.getTrangThai() == TrangThaiCTDT.DaDuyet) {
            throw new BusinessException("Khong the chinh sua CTDT da duoc phe duyet");
        }
        ctdt.setTenCTDT(dto.getTenCTDT().trim());
        ctdt.setKhoa(dto.getKhoa());
        return ctdtRepo.save(ctdt);
    }

    @Override
    public ChuongTrinhDaoTao guiChoDuyet(String ma) {
        ChuongTrinhDaoTao ctdt = findById(ma);
        if (ctdt.getTrangThai() != TrangThaiCTDT.BanNhap) {
            throw new BusinessException("Chi co the gui cho duyet CTDT o trang thai BanNhap");
        }
        ctdt.setTrangThai(TrangThaiCTDT.ChoDuyet);
        return ctdtRepo.save(ctdt);
    }

    @Override
    public ChuongTrinhDaoTao pheduyet(String ma, String maNguoiDungDuyet) {
        ChuongTrinhDaoTao ctdt = findById(ma);
        if (ctdt.getTrangThai() != TrangThaiCTDT.ChoDuyet) {
            throw new BusinessException("Chi phe duyet CTDT o trang thai ChoDuyet");
        }
        ctdt.setTrangThai(TrangThaiCTDT.DaDuyet);
        return ctdtRepo.save(ctdt);
    }

    @Override
    public CtdtHocPhan themHocPhan(String maCTDT, CtdtHocPhanDTO dto) {
        ChuongTrinhDaoTao ctdt = findById(maCTDT);
        HocPhan hp = hocPhanRepo.findById(dto.getMaHocPhan())
                .orElseThrow(() -> new ResourceNotFoundException("HocPhan", "MaHocPhan", dto.getMaHocPhan()));

        CtdtHocPhanId id = new CtdtHocPhanId(maCTDT, dto.getMaHocPhan());
        if (ctdtHocPhanRepo.existsById(id)) {
            throw new BusinessException("Hoc phan " + dto.getMaHocPhan() + " da co trong CTDT " + maCTDT);
        }

        CtdtHocPhan entry = CtdtHocPhan.builder()
                .id(id)
                .chuongTrinhDaoTao(ctdt)
                .hocPhan(hp)
                .hocKyThu(dto.getHocKyThu())
                .soLopDuKien(dto.getSoLopDuKien())
                .batBuoc(dto.getBatBuoc())
                .ghiChu(dto.getGhiChu())
                .build();
        return ctdtHocPhanRepo.save(entry);
    }

    @Override
    public void xoaHocPhan(String maCTDT, String maHocPhan) {
        CtdtHocPhanId id = new CtdtHocPhanId(maCTDT, maHocPhan);
        if (!ctdtHocPhanRepo.existsById(id)) {
            throw new ResourceNotFoundException("CtdtHocPhan", "id", maCTDT + "+" + maHocPhan);
        }
        ctdtHocPhanRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HocPhan> findHocPhanChuaThuoc(String maCTDT) {
        return hocPhanRepo.findHocPhanChuaCoTrongCTDT(maCTDT);
    }
}
