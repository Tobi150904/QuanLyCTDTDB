package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.DoiNguGvDTO;
import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHp;
import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHpId;
import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.DoiNguGiangVienHpRepository;
import com.ntu.quanlyctdtdb.repository.GiangVienRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.service.DoiNguGvService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation cho DoiNguGvService.
 * <p><b>Luu y</b>: service nay <i>khong</i> chan cung viec phan cong GV ngoai
 * doi ngu. Kiem tra soft-check nam o {@link com.ntu.quanlyctdtdb.controller.LopHocPhanController#phanCong}
 * — chi hien warningMsg de PDT biet va xu ly thu cong.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DoiNguGvServiceImpl implements DoiNguGvService {

    private final DoiNguGiangVienHpRepository repo;
    private final HocPhanRepository hocPhanRepo;
    private final GiangVienRepository giangVienRepo;

    @Override
    @Transactional(readOnly = true)
    public List<DoiNguGiangVienHp> findByHocPhan(String maHocPhan) {
        return repo.findByHocPhanFetch(maHocPhan);
    }

    @Override
    public void them(DoiNguGvDTO dto) {
        DoiNguGiangVienHpId id = new DoiNguGiangVienHpId(dto.getMaHocPhan(), dto.getMaGV());
        if (repo.existsById(id)) {
            throw new BusinessException(
                    "GV " + dto.getMaGV() + " da co trong doi ngu cua HP " + dto.getMaHocPhan() + ".");
        }
        HocPhan hp = hocPhanRepo.findById(dto.getMaHocPhan())
                .orElseThrow(() -> new ResourceNotFoundException("HocPhan", "MaHocPhan", dto.getMaHocPhan()));
        GiangVien gv = giangVienRepo.findById(dto.getMaGV())
                .orElseThrow(() -> new ResourceNotFoundException("GiangVien", "MaGV", dto.getMaGV()));

        // Khong set ngayThem — bang DoiNguGiangVienHP khong co cot do, chi co
        // {@code created_at} do DB tu sinh ({@code @CreationTimestamp}).
        DoiNguGiangVienHp dn = DoiNguGiangVienHp.builder()
                .id(id)
                .hocPhan(hp)
                .giangVien(gv)
                .trangThai(dto.getTrangThai() != null ? dto.getTrangThai() : Boolean.TRUE)
                .build();
        repo.save(dn);
        log.info("[DoiNguGV] Them GV={} vao HP={}", dto.getMaGV(), dto.getMaHocPhan());
    }

    @Override
    public void toggleTrangThai(String maHocPhan, String maGV) {
        DoiNguGiangVienHpId id = new DoiNguGiangVienHpId(maHocPhan, maGV);
        DoiNguGiangVienHp dn = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "DoiNguGiangVienHp", "id", maHocPhan + "/" + maGV));
        dn.setTrangThai(!Boolean.TRUE.equals(dn.getTrangThai()));
        repo.save(dn);
        log.info("[DoiNguGV] Toggle HP={}, GV={} -> {}", maHocPhan, maGV, dn.getTrangThai());
    }

    @Override
    public void xoa(String maHocPhan, String maGV) {
        DoiNguGiangVienHpId id = new DoiNguGiangVienHpId(maHocPhan, maGV);
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("DoiNguGiangVienHp", "id", maHocPhan + "/" + maGV);
        }
        repo.deleteById(id);
        log.info("[DoiNguGV] Xoa HP={}, GV={}", maHocPhan, maGV);
    }
}
