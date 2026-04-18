package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhSachSvLopHocPhanRepository extends JpaRepository<DanhSachSvLopHocPhan, DanhSachSvLopHocPhanId> {
    List<DanhSachSvLopHocPhan> findById_MaSV(String maSV);

    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        WHERE d.id.maCTDT = :maCTDT
        AND d.id.maHocPhan = :maHocPhan
        AND d.id.maHocKy = :maHocKy
        AND d.id.maLopHocPhan = :maLop
        ORDER BY d.sinhVien.nguoiDung.hoTen
        """)
    List<DanhSachSvLopHocPhan> findDanhSachSinhVienLop(String maCTDT, String maHocPhan, String maHocKy, Integer maLop);

    // SV bi canh bao trong hoc ky
    @Query("""
        SELECT d FROM DanhSachSvLopHocPhan d
        WHERE d.id.maHocKy = :maHocKy
        AND d.daCanhBao = true
        ORDER BY d.sinhVien.lopHanhChinh.maLopHC, d.sinhVien.nguoiDung.hoTen
        """)
    List<DanhSachSvLopHocPhan> findCanhBaoByHocKy(String maHocKy);
}
