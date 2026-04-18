package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DanhSachSvKienTap;
import com.ntu.quanlyctdtdb.entity.DanhSachSvKienTapId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhSachSvKienTapRepository extends JpaRepository<DanhSachSvKienTap, DanhSachSvKienTapId> {
    List<DanhSachSvKienTap> findById_MaDotKT(Integer maDotKT);
    List<DanhSachSvKienTap> findBySinhVien_MaSV(String maSV);
}
