package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.HocKyNamHoc;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HocKyNamHocRepository extends JpaRepository<HocKyNamHoc, String> {
    List<HocKyNamHoc> findAllByOrderByNgayBatDauDesc();
    Optional<HocKyNamHoc> findByTrangThai(TrangThaiHocKy trangThai);
    List<HocKyNamHoc> findByTrangThaiNot(TrangThaiHocKy trangThai);
}
