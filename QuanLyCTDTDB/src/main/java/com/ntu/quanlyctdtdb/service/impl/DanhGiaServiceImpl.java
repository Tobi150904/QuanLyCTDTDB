package com.ntu.quanlyctdtdb.service.impl;

import com.ntu.quanlyctdtdb.dto.NhapNhanXetDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhanId;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.entity.LopHanhChinh;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.entity.SinhVien;
import com.ntu.quanlyctdtdb.exception.BusinessException;
import com.ntu.quanlyctdtdb.exception.ResourceNotFoundException;
import com.ntu.quanlyctdtdb.repository.DanhSachSvLopHocPhanRepository;
import com.ntu.quanlyctdtdb.repository.HocPhanRepository;
import com.ntu.quanlyctdtdb.repository.LopHanhChinhRepository;
import com.ntu.quanlyctdtdb.repository.LopHocPhanRepository;
import com.ntu.quanlyctdtdb.repository.SinhVienRepository;
import com.ntu.quanlyctdtdb.service.DanhGiaService;
import com.ntu.quanlyctdtdb.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Phase 4 — Danh Gia & Canh Bao SV.
 * Cong viec chinh: GV nhap nhan xet -> neu DaCanhBao moi bat -> trigger email
 * den CVHT (lay tu LopHanhChinh.coVan.nguoiDung.email).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DanhGiaServiceImpl implements DanhGiaService {

    private final DanhSachSvLopHocPhanRepository dssvRepo;
    private final LopHocPhanRepository lopHpRepo;
    private final HocPhanRepository hocPhanRepo;
    private final SinhVienRepository sinhVienRepo;
    private final LopHanhChinhRepository lopHcRepo;
    private final EmailService emailService;

    // ---------------- READ ----------------

    @Override
    @Transactional(readOnly = true)
    public List<LopHocPhan> findLopHpCuaGv(String maGV) {
        if (maGV == null || maGV.isBlank()) return List.of();
        return lopHpRepo.findByGiangVien_MaGV(maGV);
    }

    @Override
    @Transactional(readOnly = true)
    public long demSoCanhBao(LopHocPhanId id) {
        if (id == null) return 0L;
        return dssvRepo.countCanhBaoTrongLop(
                id.getMaCTDT(), id.getMaHocPhan(), id.getMaHocKy(), id.getMaLopHocPhan());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhSachSvLopHocPhan> findDanhSachSvTrongLop(LopHocPhanId id) {
        return dssvRepo.findDanhSachSinhVienLop(
                id.getMaCTDT(), id.getMaHocPhan(), id.getMaHocKy(), id.getMaLopHocPhan());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhSachSvLopHocPhan> findNhanXetCuaSv(String maSV) {
        if (maSV == null || maSV.isBlank()) return List.of();
        return dssvRepo.findById_MaSVFetch(maSV);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhSachSvLopHocPhan> findCanhBaoChoCvht(String maGV) {
        // Bug-fix #3: NEVER fallback to "findCanhBaoChuaXuLy()" (list toan bo
        // canh bao chua xu ly toan truong) khi CVHT khong co ma GV. Truoc day
        // leak toan bo canh bao cho user co ROLE_CVHT nhung thieu record
        // GiangVien (vi pham privacy). Tra ve list rong -> template hien
        // "khong co canh bao nao" dung voi quyen thuc te.
        if (maGV == null || maGV.isBlank()) {
            return List.of();
        }
        return dssvRepo.findCanhBaoByCoVan(maGV);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DanhSachSvLopHocPhan> findCanhBaoTatCa() {
        // Bug fix Phase 4: PDT/ADMIN can xem CA da xu ly + chua xu ly de
        // co cai nhin tong hop. Truoc day goi findCanhBaoChuaXuLy()
        // -> stat-card "Da Xu Ly" luon = 0 va mat lich su giam sat.
        return dssvRepo.findCanhBaoToanBo();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DanhSachSvLopHocPhan> findNhanXetChoCvht(String maGV) {
        // P2-A5 — Tra ve list rong neu maGV null thay vi fallback toan he
        // (defense-in-depth tuong tu findCanhBaoChoCvht).
        if (maGV == null || maGV.isBlank()) {
            return List.of();
        }
        return dssvRepo.findNhanXetByCoVan(maGV);
    }

    // ---------------- WRITE ----------------

    @Override
    @Transactional
    public DanhSachSvLopHocPhan nhapNhanXet(NhapNhanXetDTO dto) {
        if (dto == null) {
            throw new BusinessException("Du lieu nhan xet rong.");
        }
        DanhSachSvLopHocPhanId id = new DanhSachSvLopHocPhanId(
                dto.getMaSV(), dto.getMaCTDT(), dto.getMaHocPhan(),
                dto.getMaHocKy(), dto.getMaLop());

        DanhSachSvLopHocPhan ds = dssvRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay ban ghi dang ky lop cua SV " + dto.getMaSV()
                        + " trong lop " + dto.getMaHocPhan() + "/" + dto.getMaLop()
                        + " (HK " + dto.getMaHocKy() + "). SV phai duoc dang ky vao "
                        + "lop hoc phan truoc khi nhap nhan xet."));

        boolean truocKhiCanhBao = Boolean.TRUE.equals(ds.getDaCanhBao());
        boolean nayCanhBao      = Boolean.TRUE.equals(dto.getDaCanhBao());

        // Nhan xet rong -> luu null thay vi chuoi rong (de NULL trong DB
        // semantically dung hon va query "co nhan xet" don gian hon).
        String nx = dto.getNhanXet();
        if (nx != null && nx.isBlank()) nx = null;
        ds.setNhanXet(nx);
        ds.setDaCanhBao(nayCanhBao);
        // Khi user bat lai canh bao moi (chua xu ly), reset KetQuaXuLy de
        // canh bao moi nay xuat hien lai trong "chua xu ly" cua CVHT.
        // Nguoc lai, neu user tat canh bao (nayCanhBao = false), giu nguyen
        // KetQuaXuLy de bao toan history (tranh mat ban ghi xu ly da co).
        if (nayCanhBao && !truocKhiCanhBao) {
            ds.setKetQuaXuLy(null);
        }

        DanhSachSvLopHocPhan saved = dssvRepo.save(ds);

        // Edge transition false -> true: gui email canh bao den CVHT.
        // Khong gui khi nayCanhBao van la true (tranh spam moi lan GV sua nhan xet).
        if (nayCanhBao && !truocKhiCanhBao) {
            try {
                triggerEmailCanhBao(dto, saved);
            } catch (Exception e) {
                // Khong rollback transaction chi vi loi gui email — luu nhan xet
                // van quan trong hon. Log warn de admin biet.
                log.warn("[Phase 4] Loi gui email canh bao SV {}: {}",
                         dto.getMaSV(), e.getMessage());
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public DanhSachSvLopHocPhan xuLyCanhBao(String maCTDT, String maHocPhan, String maHocKy,
                                             Integer maLop, String maSV, String ketQuaXuLy) {
        if (ketQuaXuLy == null || ketQuaXuLy.isBlank()) {
            throw new BusinessException("Ket qua xu ly khong duoc rong.");
        }
        DanhSachSvLopHocPhanId id = new DanhSachSvLopHocPhanId(
                maSV, maCTDT, maHocPhan, maHocKy, maLop);
        DanhSachSvLopHocPhan ds = dssvRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Khong tim thay canh bao cho SV " + maSV));
        if (!Boolean.TRUE.equals(ds.getDaCanhBao())) {
            throw new BusinessException(
                    "SV " + maSV + " hien khong o trang thai canh bao — khong can xu ly.");
        }
        ds.setKetQuaXuLy(ketQuaXuLy.trim());
        // DaCanhBao van la 1 — luu lich su canh bao da xay ra (docs/03 §WF-06).
        return dssvRepo.save(ds);
    }

    // ---------------- HELPERS ----------------

    /**
     * Lay email CVHT tu chuoi (sv -> lopHanhChinh -> coVan -> nguoiDung -> email),
     * lay ten HP qua HocPhanRepository, va goi {@link EmailService}.
     * Method nay duoc tach rieng de logic nhanh chong (try/catch) ro rang.
     */
    private void triggerEmailCanhBao(NhapNhanXetDTO dto, DanhSachSvLopHocPhan saved) {
        SinhVien sv = saved.getSinhVien();
        if (sv == null) {
            sv = sinhVienRepo.findById(dto.getMaSV()).orElse(null);
        }
        if (sv == null) {
            log.warn("[Phase 4] Khong tim thay SV {} de gui email canh bao", dto.getMaSV());
            return;
        }
        // Lay LopHanhChinh kem coVan + nguoiDung qua findByIdFetch (LHC trong
        // sv co the la lazy proxy chua duoc init).
        String maLopHC = sv.getLopHanhChinh() != null ? sv.getLopHanhChinh().getMaLopHC() : null;
        if (maLopHC == null) {
            log.warn("[Phase 4] SV {} chua co lop hanh chinh — bo qua canh bao email", dto.getMaSV());
            return;
        }
        LopHanhChinh lhc = lopHcRepo.findByIdFetch(maLopHC).orElse(null);
        if (lhc == null || lhc.getCoVan() == null
                || lhc.getCoVan().getNguoiDung() == null) {
            log.warn("[Phase 4] Lop {} chua co CVHT — canh bao SV {} luu thanh cong nhung khong gui email",
                     maLopHC, dto.getMaSV());
            return;
        }
        String emailCVHT = lhc.getCoVan().getNguoiDung().getEmail();
        if (emailCVHT == null || emailCVHT.isBlank()) {
            log.warn("[Phase 4] CVHT cua lop {} khong co email — bo qua", maLopHC);
            return;
        }
        String hoTenSV = sv.getNguoiDung() != null ? sv.getNguoiDung().getHoTen() : sv.getMaSV();
        HocPhan hp = hocPhanRepo.findById(dto.getMaHocPhan()).orElse(null);
        String tenHP = hp != null ? hp.getTenHocPhan() : dto.getMaHocPhan();

        emailService.guiCanhBaoSinhVien(
                emailCVHT, hoTenSV, tenHP,
                dto.getNhanXet() == null ? "(khong co nhan xet)" : dto.getNhanXet());
    }
}
