package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DanhSachThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiThucTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhSachThucTapRepository extends JpaRepository<DanhSachThucTap, Integer> {
    List<DanhSachThucTap> findByDotThucTap_MaDotTT(Integer maDotTT);
    List<DanhSachThucTap> findBySinhVien_MaSV(String maSV);
    List<DanhSachThucTap> findByTrangThai(TrangThaiThucTap trangThai);
    boolean existsByDotThucTap_MaDotTTAndSinhVien_MaSV(Integer maDotTT, String maSV);
}
