package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.NhomNguoiDung;
import com.ntu.quanlyctdtdb.entity.NhomNguoiDungId;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhomNguoiDungRepository extends JpaRepository<NhomNguoiDung, NhomNguoiDungId> {
    List<NhomNguoiDung> findByNguoiDung_MaNguoiDung(String maNguoiDung);
    List<NhomNguoiDung> findByVaiTro(VaiTro vaiTro);
    boolean existsByNguoiDung_MaNguoiDungAndVaiTro(String maNguoiDung, VaiTro vaiTro);
}
