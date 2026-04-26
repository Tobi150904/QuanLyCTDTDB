package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DanhSachSvKienTap;
import com.ntu.quanlyctdtdb.entity.DanhSachSvKienTapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhSachSvKienTapRepository extends JpaRepository<DanhSachSvKienTap, DanhSachSvKienTapId> {
    List<DanhSachSvKienTap> findById_MaDotKT(Integer maDotKT);
    List<DanhSachSvKienTap> findBySinhVien_MaSV(String maSV);

    /**
     * Phase 4 - kien-tap/chi-tiet danh sach SV bang: mỗi row hien thi
     * maSV, hoTen (qua sv.nguoiDung), maLopHC. Tat ca FetchType.LAZY -
     * phai JOIN FETCH vi OSIV=false.
     */
    @Query("SELECT r FROM DanhSachSvKienTap r "
         + "JOIN FETCH r.sinhVien sv "
         + "LEFT JOIN FETCH sv.nguoiDung "
         + "LEFT JOIN FETCH sv.lopHanhChinh "
         + "WHERE r.id.maDotKT = :maDotKT "
         + "ORDER BY r.id.maSV")
    List<DanhSachSvKienTap> findById_MaDotKTFetchSV(@Param("maDotKT") Integer maDotKT);
}
