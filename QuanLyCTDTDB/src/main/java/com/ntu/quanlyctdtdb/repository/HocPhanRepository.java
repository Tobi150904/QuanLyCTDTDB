package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;

import java.util.List;

@Repository
public interface HocPhanRepository extends JpaRepository<HocPhan, String> {

    List<HocPhan> findByTrangThai(TrangThaiHocPhan trangThai);

    /**
     * Lay HP ma mot CNHP quan ly
     */
    List<HocPhan> findByChuNhiemHP(NguoiDung chuNhiemHP);

    List<HocPhan> findByChuNhiemHP_MaNguoiDung(String maCNHP);

    Page<HocPhan> findByTenHocPhanContainingIgnoreCase(String keyword, Pageable pageable);

    Page<HocPhan> findByTrangThaiAndTenHocPhanContainingIgnoreCase(
            TrangThaiHocPhan trangThai, String keyword, Pageable pageable);

    /**
     * Lay HP chua nam trong mot CTDT (de BCN them HP vao CTDT)
     */
    @Query("SELECT hp FROM HocPhan hp WHERE hp NOT IN " +
           "(SELECT hp2 FROM ChuongTrinhDaoTao ctdt JOIN ctdt.hocPhans hp2 WHERE ctdt.maCTDT = :maCTDT) " +
           "AND hp.trangThai = 'DaDuyet'")
    List<HocPhan> findHocPhanNotInCTDT(@Param("maCTDT") String maCTDT);

    List<HocPhan> findAllByOrderByMaHocPhanAsc();
}
