package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;

import java.util.List;
import java.util.Optional;

@Repository
public interface HocKyNamHocRepository extends JpaRepository<HocKyNamHoc, String> {

    List<HocKyNamHoc> findByTrangThai(TrangThaiHocKy trangThai);

    /**
     * Lay hoc ky dang dien ra hoac sap toi dau tien (sap xep theo ngay bat dau)
     * Dung trong Rule 3: auto-create LopHocPhan
     */
    Optional<HocKyNamHoc> findFirstByTrangThaiOrderByNgayBatDauAsc(TrangThaiHocKy trangThai);

    List<HocKyNamHoc> findAllByOrderByNgayBatDauDesc();
}
