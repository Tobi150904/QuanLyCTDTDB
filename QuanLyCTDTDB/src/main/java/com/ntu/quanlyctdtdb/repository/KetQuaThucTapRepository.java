package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.KetQuaThucTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KetQuaThucTapRepository extends JpaRepository<KetQuaThucTap, Integer> {
    List<KetQuaThucTap> findByDanhSachThucTap_MaThucTap(Integer maThucTap);

    /**
     * Phase 7 — find KetQua cua mot DanhSachThucTap theo VaiTro (GV/CVHT/DN).
     * Dung de upsert (update neu da co, insert neu chua) khi nhap/cap nhat
     * diem 1 vai tro cu the. UNIQUE(MaThucTap, MaVaiTro) trong SQL bao dam
     * chi co toi da 1 ban ghi mot bo (thuc tap, vai tro).
     */
    Optional<KetQuaThucTap> findByDanhSachThucTap_MaThucTapAndVaiTroThucTap_MaVaiTro(
            Integer maThucTap, String maVaiTro);

    /**
     * Phase 7 — fetch tat ca KetQua cua 1 dot thuc tap (nhieu SV).
     * Tra ve da kem fetch vaiTro + nguoiDanhGia.nguoiDung de template
     * render maNguoiDanhGia + tenVaiTro mà khong bi LazyInitException
     * (open-in-view=false).
     */
    @Query("""
        SELECT kq FROM KetQuaThucTap kq
        LEFT JOIN FETCH kq.vaiTroThucTap
        LEFT JOIN FETCH kq.nguoiDanhGia gv
        LEFT JOIN FETCH gv.nguoiDung
        WHERE kq.danhSachThucTap.dotThucTap.maDotTT = :maDotTT
        """)
    List<KetQuaThucTap> findByDotFetchAll(@Param("maDotTT") Integer maDotTT);

    @Query("""
        SELECT kq FROM KetQuaThucTap kq
        JOIN kq.danhSachThucTap dst
        WHERE dst.sinhVien.maSV = :maSV
        ORDER BY kq.createdAt DESC
        """)
    List<KetQuaThucTap> findBySinhVien(@Param("maSV") String maSV);

    /**
     * DanhSachThucTap khong co field 'maDotTT' truc tiep - no la cot FK cho association 'dotThucTap'.
     * Phai navigate qua: dst.dotThucTap.maDotTT
     */
    @Query("""
        SELECT AVG(kq.diem) FROM KetQuaThucTap kq
        JOIN kq.danhSachThucTap dst
        WHERE dst.dotThucTap.maDotTT = :maDotTT
        AND kq.vaiTroThucTap.maVaiTro = :maVaiTro
        """)
    Double avgDiemByDotAndVaiTro(@Param("maDotTT") Integer maDotTT,
                                 @Param("maVaiTro") String maVaiTro);
}
