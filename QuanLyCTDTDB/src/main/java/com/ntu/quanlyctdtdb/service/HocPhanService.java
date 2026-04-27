package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.HocPhanDTO;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.LoaiHocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface HocPhanService {

    List<HocPhan> findAll(String keyword);

    /**
     * Phase 2 — server-side Pageable + Sort.
     * Tham so {@code loai} va {@code trangThai} co the {@code null} de bo qua filter.
     */
    Page<HocPhan> findPaged(String keyword, LoaiHocPhan loai,
                             TrangThaiHocPhan trangThai, Pageable pageable);

    /**
     * Phase 2 — du lieu cho export CSV (khong phan trang, khong sort theo
     * pageable). Lay theo cung filter voi findPaged de output trung khop voi
     * UI nguoi dung dang xem.
     */
    List<HocPhan> findForExport(String keyword, LoaiHocPhan loai,
                                 TrangThaiHocPhan trangThai);

    HocPhan findById(String ma);

    /** CNHP de nghi tao moi hoc phan → trang thai ChuaDuyet */
    HocPhan create(HocPhanDTO dto, String maNguoiDungTao);

    /** CNHP chinh sua truoc khi gui duyet */
    HocPhan update(String ma, HocPhanDTO dto);

    /** CNHP gui cho duyet BanNhap → ChoDuyet */
    HocPhan guiChoDuyet(String ma);

    /** TTDTXS phe duyet → trang thai DaDuyet + gui email CNHP */
    HocPhan pheduyet(String ma, String maNguoiDungDuyet);

    /** TTDTXS tu choi + ly do */
    HocPhan tuChoi(String ma, String lyDo, String maNguoiDungTuChoi);

    /** Tat/bat hieu luc */
    HocPhan toggleTrangThai(String ma);

    /** Upload file de cuong va luu ten file vao HP */
    HocPhan uploadDeCuong(String ma, String tenFile);

    /**
     * Phase 2 — thong ke nhanh cho stat-card row tren danh sach.
     * Key:
     *   tongHocPhan : tong so hoc phan
     *   daDuyet     : so HP da duyet
     *   choDuyet    : so HP cho duyet
     *   banNhap     : so HP ban nhap
     */
    Map<String, Long> getThongKe();
}
