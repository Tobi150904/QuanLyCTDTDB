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
}
