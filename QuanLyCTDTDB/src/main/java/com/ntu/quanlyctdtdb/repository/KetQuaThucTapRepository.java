package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.KetQuaThucTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KetQuaThucTapRepository extends JpaRepository<KetQuaThucTap, Integer> {
    List<KetQuaThucTap> findByDanhSachThucTap_MaThucTap(Integer maThucTap);

    @Query("""
        SELECT kq FROM KetQuaThucTap kq
        JOIN kq.danhSachThucTap dst
        WHERE dst.sinhVien.maSV = :maSV
        ORDER BY kq.createdAt DESC
        """)
    List<KetQuaThucTap> findBySinhVien(String maSV);

    @Query("""
        SELECT AVG(kq.diem) FROM KetQuaThucTap kq
        JOIN kq.danhSachThucTap dst
        WHERE dst.maDotTT = :maDotTT
        AND kq.vaiTroThucTap.maVaiTro = :maVaiTro
        """)
    Double avgDiemByDotAndVaiTro(Integer maDotTT, String maVaiTro);
}
