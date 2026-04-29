package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DanhSachThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiThucTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhSachThucTapRepository extends JpaRepository<DanhSachThucTap, Integer> {
    List<DanhSachThucTap> findByDotThucTap_MaDotTT(Integer maDotTT);
    List<DanhSachThucTap> findBySinhVien_MaSV(String maSV);
    List<DanhSachThucTap> findByTrangThai(TrangThaiThucTap trangThai);
    boolean existsByDotThucTap_MaDotTTAndSinhVien_MaSV(Integer maDotTT, String maSV);

    /**
     * Phase 4 - thuc-tap/chi-tiet danh sach SV bang. Render: maSV, hoTen,
     * lop, doanh nghiep. Tat ca FetchType.LAZY → JOIN FETCH bat buoc.
     */
    @Query("SELECT r FROM DanhSachThucTap r "
         + "JOIN FETCH r.sinhVien sv "
         + "LEFT JOIN FETCH sv.nguoiDung "
         + "LEFT JOIN FETCH sv.lopHanhChinh "
         + "LEFT JOIN FETCH r.doanhNghiep "
         + "WHERE r.dotThucTap.maDotTT = :maDotTT "
         + "ORDER BY r.maThucTap")
    List<DanhSachThucTap> findByDotThucTap_MaDotTTFetchSV(@Param("maDotTT") Integer maDotTT);

    /**
     * Bug-fix phan quyen: lay 1 row DanhSachThucTap kem du quan he can thiet
     * de verify ownership truoc khi cho phep cham diem:
     *   - ds.doanhNghiep                   -> kiem tra DN cua SV (vai tro DN)
     *   - ds.sinhVien.lopHanhChinh.coVan   -> kiem tra CVHT cua SV (vai tro CVHT)
     *   - ds.dotThucTap.gvPhuTrach         -> N/A (DotThucTap khong co gvPhuTrach
     *     nhu DotKienTap; phan cong GV cham la o LopHocPhan + KetQuaThucTap.nguoiDanhGia)
     * Tranh LazyInitException khi OSIV=false.
     */
    @Query("SELECT r FROM DanhSachThucTap r "
         + "JOIN FETCH r.sinhVien sv "
         + "LEFT JOIN FETCH sv.nguoiDung "
         + "LEFT JOIN FETCH sv.lopHanhChinh lhc "
         + "LEFT JOIN FETCH lhc.coVan cv "
         + "LEFT JOIN FETCH cv.nguoiDung "
         + "LEFT JOIN FETCH r.doanhNghiep "
         + "LEFT JOIN FETCH r.dotThucTap "
         + "WHERE r.maThucTap = :maThucTap")
    java.util.Optional<DanhSachThucTap> findByIdFetchOwnership(
            @Param("maThucTap") Integer maThucTap);
}
