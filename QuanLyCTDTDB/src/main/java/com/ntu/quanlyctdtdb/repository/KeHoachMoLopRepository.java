package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.KeHoachMoLop;
import com.ntu.quanlyctdtdb.entity.KeHoachMoLopId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KeHoachMoLopRepository extends JpaRepository<KeHoachMoLop, KeHoachMoLopId> {

    /** Ke hoach mo lop trong 1 hoc ky (fetch HocPhan de render ten). */
    @Query("""
        SELECT kh FROM KeHoachMoLop kh
        LEFT JOIN FETCH kh.hocPhan
        WHERE kh.id.maHocKy = :maHocKy
        ORDER BY kh.id.maHocPhan
        """)
    List<KeHoachMoLop> findByHocKyFetchHocPhan(String maHocKy);

    /** Danh sach ke hoach cua 1 HocPhan qua cac ky (de hien tren trang chi tiet HP). */
    @Query("""
        SELECT kh FROM KeHoachMoLop kh
        LEFT JOIN FETCH kh.hocKy
        WHERE kh.id.maHocPhan = :maHocPhan
        ORDER BY kh.id.maHocKy DESC
        """)
    List<KeHoachMoLop> findByHocPhanFetchHocKy(String maHocPhan);
}
