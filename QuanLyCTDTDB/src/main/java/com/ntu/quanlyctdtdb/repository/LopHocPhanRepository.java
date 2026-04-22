package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.enums.TrangThaiLopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, LopHocPhanId> {
    List<LopHocPhan> findById_MaCTDTAndId_MaHocKy(String maCTDT, String maHocKy);
    List<LopHocPhan> findById_MaHocKy(String maHocKy);
    List<LopHocPhan> findByTrangThai(TrangThaiLopHocPhan trangThai);
    List<LopHocPhan> findByGiangVien_MaGV(String maGV);

    long countByTrangThai(TrangThaiLopHocPhan trangThai);

    @Query("""
        SELECT lhp FROM LopHocPhan lhp
        WHERE lhp.id.maCTDT = :maCTDT
        AND lhp.id.maHocPhan = :maHocPhan
        AND lhp.id.maHocKy = :maHocKy
        ORDER BY lhp.id.maLopHocPhan
        """)
    List<LopHocPhan> findByCtdtHocPhanAndHocKy(String maCTDT, String maHocPhan, String maHocKy);

    // Lop khong co GV (can phan cong) - fetch GV.nguoiDung de render template
    @Query("""
        SELECT lhp FROM LopHocPhan lhp
        LEFT JOIN FETCH lhp.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE lhp.giangVien IS NULL AND lhp.trangThai = 'DangMo'
        """)
    List<LopHocPhan> findChuaPhanCongGiangVien();

    /**
     * List view theo CTDT + HocKy: fetch GV + NguoiDung tranh LazyInit
     * khi template hien thi hoTen giang vien (open-in-view=false).
     */
    @Query("""
        SELECT DISTINCT lhp FROM LopHocPhan lhp
        LEFT JOIN FETCH lhp.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE lhp.id.maCTDT = :maCTDT
        AND lhp.id.maHocKy = :maHocKy
        ORDER BY lhp.id.maHocPhan, lhp.id.maLopHocPhan
        """)
    List<LopHocPhan> findByCtdtAndHocKyFetch(String maCTDT, String maHocKy);

    /**
     * List view chi theo CTDT (tat ca hoc ky): dung khi nguoi dung muon
     * xem toan bo lop cua mot CTDT across nhieu ky — phuc vu bao cao
     * tong hop ("tat ca cac lop da/dang mo cho CTDT X").
     * <p>ORDER theo maHocKy DESC de nhung ky moi nhat hien len truoc.
     */
    @Query("""
        SELECT DISTINCT lhp FROM LopHocPhan lhp
        LEFT JOIN FETCH lhp.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE lhp.id.maCTDT = :maCTDT
        ORDER BY lhp.id.maHocKy DESC, lhp.id.maHocPhan, lhp.id.maLopHocPhan
        """)
    List<LopHocPhan> findByCtdtFetch(String maCTDT);

    /**
     * List view chi theo HocKy (tat ca CTDT): dung khi TTDTXS/PDT muon
     * xem tong hop toan bo lop mo trong mot hoc ky across CTDT — phuc
     * vu xep lich, theo doi tinh trang phan cong GV cap truong.
     */
    @Query("""
        SELECT DISTINCT lhp FROM LopHocPhan lhp
        LEFT JOIN FETCH lhp.giangVien gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE lhp.id.maHocKy = :maHocKy
        ORDER BY lhp.id.maCTDT, lhp.id.maHocPhan, lhp.id.maLopHocPhan
        """)
    List<LopHocPhan> findByHocKyFetch(String maHocKy);
}
