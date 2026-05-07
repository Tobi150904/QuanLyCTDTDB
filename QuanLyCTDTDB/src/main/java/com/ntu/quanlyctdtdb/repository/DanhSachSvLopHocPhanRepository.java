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

    @Query("""
            SELECT d.id.maCTDT, d.id.maHocPhan, d.id.maHocKy,
                   d.id.maLopHocPhan, COUNT(d)
            FROM DanhSachSvLopHocPhan d
            GROUP BY d.id.maCTDT, d.id.maHocPhan, d.id.maHocKy, d.id.maLopHocPhan
            """)
    List<Object[]> demSiSoThucTeTatCaLop();
    
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

    /**
      Phase 4 — Toan bo canh bao trong he thong (chua + da xu ly).
      Dung cho PDT / ADMIN view "/danh-gia/canh-bao": yeu cau giam sat
      tat ca canh bao da xay ra, khong chi loc theo trang thai xu ly.
      Truoc day controller goi {@link #findCanhBaoChuaXuLy()} cho ca CVHT
      va PDT — gay sai lech thong ke (soDaXuLy luon = 0, mat lich su).
      Tach query mới nay de PDT/ADMIN co cai nhin day du.
      Sap xep: chua xu ly len truoc (uu tien xem xet), sau do moi den
      cac canh bao da xu ly (theo {@code updatedAt} giam dan).
     */
    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        LEFT JOIN FETCH d.sinhVien sv
        LEFT JOIN FETCH sv.nguoiDung
        LEFT JOIN FETCH sv.lopHanhChinh lhc
        LEFT JOIN FETCH lhc.coVan gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE d.daCanhBao = true
        ORDER BY (CASE WHEN d.ketQuaXuLy IS NULL OR d.ketQuaXuLy = '' THEN 0 ELSE 1 END),
                 d.updatedAt DESC
        """)
    List<DanhSachSvLopHocPhan> findCanhBaoToanBo();
    
    /**
     Tat ca nhan xet (co hoac khong canh bao) cua SV thuoc cac
     lop hanh chinh do CVHT phu trach. Khac voi {@link #findCanhBaoByCoVan}
     (chi loc {@code daCanhBao=true}), method nay tra ve moi ban ghi co
     {@code nhanXet} hoac {@code daCanhBao=true} de CVHT co cai nhin
     tong hop ve toan bo phan hoi cua GV ve lop minh phu trach.
     Sap xep: hoc ky moi nhat truoc, sau do canh bao chua xu ly len dau de CVHT uu tien xem.
     */
    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        LEFT JOIN FETCH d.sinhVien sv
        LEFT JOIN FETCH sv.nguoiDung
        LEFT JOIN FETCH sv.lopHanhChinh lhc
        LEFT JOIN FETCH lhc.coVan gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE lhc.coVan.maGV = :maGV
          AND ((d.nhanXet IS NOT NULL AND d.nhanXet <> '')
               OR d.daCanhBao = true)
        ORDER BY d.id.maHocKy DESC,
                 (CASE WHEN d.daCanhBao = true
                        AND (d.ketQuaXuLy IS NULL OR d.ketQuaXuLy = '')
                       THEN 0 ELSE 1 END),
                 sv.lopHanhChinh.maLopHC,
                 sv.nguoiDung.hoTen
        """)
    List<DanhSachSvLopHocPhan> findNhanXetByCoVan(@Param("maGV") String maGV);
}
