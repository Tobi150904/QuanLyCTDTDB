package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.GiangVien;
import com.ntu.quanlyctdtdb.enums.LoaiGiangVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiangVienRepository extends JpaRepository<GiangVien, String> {
    Optional<GiangVien> findByNguoiDung_MaNguoiDung(String maNguoiDung);
    List<GiangVien> findByLoaiGiangVien(LoaiGiangVien loai);

    @Query("SELECT gv FROM GiangVien gv JOIN gv.nguoiDung nd WHERE nd.trangThaiTK = true ORDER BY nd.hoTen")
    List<GiangVien> findAllActive();
}
