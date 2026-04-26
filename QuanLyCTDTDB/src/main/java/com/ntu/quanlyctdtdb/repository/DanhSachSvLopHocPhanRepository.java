package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DanhSachSvLopHocPhanRepository extends JpaRepository<DanhSachSvLopHocPhan, DanhSachSvLopHocPhanId> {
    List<DanhSachSvLopHocPhan> findById_MaSV(String maSV);

    /**
     * Lay tat ca nhan xet/canh bao cua MOT sinh vien — kem fetch HocPhan
     * thong tin nhe (chi maHocPhan trong embedded id, repo nay khong join HP).
     * Dung cho man hinh "Diem cua toi" / "Nhan xet ve toi".
     */
    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        LEFT JOIN FETCH d.sinhVien sv
        LEFT JOIN FETCH sv.nguoiDung
        WHERE d.id.maSV = :maSV
        ORDER BY d.id.maHocKy DESC, d.id.maHocPhan
        """)
    List<DanhSachSvLopHocPhan> findById_MaSVFetch(@Param("maSV") String maSV);

    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        LEFT JOIN FETCH d.sinhVien sv
        LEFT JOIN FETCH sv.nguoiDung
        WHERE d.id.maCTDT = :maCTDT
        AND d.id.maHocPhan = :maHocPhan
        AND d.id.maHocKy = :maHocKy
        AND d.id.maLopHocPhan = :maLop
        ORDER BY sv.nguoiDung.hoTen
        """)
    List<DanhSachSvLopHocPhan> findDanhSachSinhVienLop(String maCTDT, String maHocPhan, String maHocKy, Integer maLop);

    /** Dem so SV bi canh bao trong 1 lop HP — dung cho stat-card man hinh GV. */
    @Query("""
        SELECT COUNT(d) FROM DanhSachSvLopHocPhan d
        WHERE d.id.maCTDT = :maCTDT
        AND d.id.maHocPhan = :maHocPhan
        AND d.id.maHocKy = :maHocKy
        AND d.id.maLopHocPhan = :maLop
        AND d.daCanhBao = true
        """)
    long countCanhBaoTrongLop(String maCTDT, String maHocPhan, String maHocKy, Integer maLop);

    // SV bi canh bao trong hoc ky
    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        LEFT JOIN FETCH d.sinhVien sv
        LEFT JOIN FETCH sv.nguoiDung
        LEFT JOIN FETCH sv.lopHanhChinh lhc
        LEFT JOIN FETCH lhc.coVan gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE d.id.maHocKy = :maHocKy
        AND d.daCanhBao = true
        ORDER BY sv.lopHanhChinh.maLopHC, sv.nguoiDung.hoTen
        """)
    List<DanhSachSvLopHocPhan> findCanhBaoByHocKy(String maHocKy);

    /**
     * Phase 4 — Canh bao chua xu ly: DaCanhBao=1 va KetQuaXuLy IS NULL.
     * Dung cho man hinh CVHT "/danh-gia/canh-bao".
     */
    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        LEFT JOIN FETCH d.sinhVien sv
        LEFT JOIN FETCH sv.nguoiDung
        LEFT JOIN FETCH sv.lopHanhChinh lhc
        LEFT JOIN FETCH lhc.coVan gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE d.daCanhBao = true
        AND (d.ketQuaXuLy IS NULL OR d.ketQuaXuLy = '')
        ORDER BY d.updatedAt DESC NULLS LAST, sv.lopHanhChinh.maLopHC
        """)
    List<DanhSachSvLopHocPhan> findCanhBaoChuaXuLy();

    /**
     * Phase 4 — Canh bao thuoc cac lop hanh chinh do CVHT phu trach
     * (filter by GV co van.maGV). Dung cho CVHT view chi danh sach SV thuoc
     * lop minh quan ly.
     */
    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        LEFT JOIN FETCH d.sinhVien sv
        LEFT JOIN FETCH sv.nguoiDung
        LEFT JOIN FETCH sv.lopHanhChinh lhc
        LEFT JOIN FETCH lhc.coVan gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE d.daCanhBao = true
        AND lhc.coVan.maGV = :maGV
        ORDER BY (CASE WHEN d.ketQuaXuLy IS NULL OR d.ketQuaXuLy = '' THEN 0 ELSE 1 END),
                 d.updatedAt DESC
        """)
    List<DanhSachSvLopHocPhan> findCanhBaoByCoVan(@Param("maGV") String maGV);
}
