package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DanhSachThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiThucTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhSachThucTapRepository extends JpaRepository<DanhSachThucTap, Integer> {
    List<DanhSachThucTap> findByDotThucTap_MaDotTT(Integer maDotTT);
    List<DanhSachThucTap> findBySinhVien_MaSV(String maSV);
    List<DanhSachThucTap> findByTrangThai(TrangThaiThucTap trangThai);
    boolean existsByDotThucTap_MaDotTTAndSinhVien_MaSV(Integer maDotTT, String maSV);

    /**
     * Phase 4 - thuc-tap/chi-tiet danh sach SV bang. Render: maSV, hoTen,
     * lop, doanh nghiep. Tat ca FetchType.LAZY → JOIN FETCH bat buoc.
     */
    @Query("SELECT r FROM DanhSachThucTap r "
         + "JOIN FETCH r.sinhVien sv "
         + "LEFT JOIN FETCH sv.nguoiDung "
         + "LEFT JOIN FETCH sv.lopHanhChinh "
         + "LEFT JOIN FETCH r.doanhNghiep "
         + "WHERE r.dotThucTap.maDotTT = :maDotTT "
         + "ORDER BY r.maThucTap")
    List<DanhSachThucTap> findByDotThucTap_MaDotTTFetchSV(@Param("maDotTT") Integer maDotTT);

    /**
     * Bug-fix phan quyen: lay 1 row DanhSachThucTap kem du quan he can thiet
     * de verify ownership truoc khi cho phep cham diem:
     *   - ds.doanhNghiep                   -> kiem tra DN cua SV (vai tro DN)
     *   - ds.sinhVien.lopHanhChinh.coVan   -> kiem tra CVHT cua SV (vai tro CVHT)
     *   - ds.dotThucTap.gvPhuTrach         -> N/A (DotThucTap khong co gvPhuTrach
     *     nhu DotKienTap; phan cong GV cham la o LopHocPhan + KetQuaThucTap.nguoiDanhGia)
     * Tranh LazyInitException khi OSIV=false.
     */
    @Query("SELECT r FROM DanhSachThucTap r "
         + "JOIN FETCH r.sinhVien sv "
         + "LEFT JOIN FETCH sv.nguoiDung "
         + "LEFT JOIN FETCH sv.lopHanhChinh lhc "
         + "LEFT JOIN FETCH lhc.coVan cv "
         + "LEFT JOIN FETCH cv.nguoiDung "
         + "LEFT JOIN FETCH r.doanhNghiep "
         + "LEFT JOIN FETCH r.dotThucTap "
         + "WHERE r.maThucTap = :maThucTap")
    java.util.Optional<DanhSachThucTap> findByIdFetchOwnership(
            @Param("maThucTap") Integer maThucTap);

    /**
     * Bug-fix phan quyen (GV danh gia): kiem tra GV co dung la nguoi day HOC
     * PHAN CUA DOT THUC TAP cho sinh vien trong CUNG HOC KY cua dot TT khong.
     *
     * <p>Scope chinh xac (khac voi bug cu chi check "GV co day bat ky HP nao
     * cua SV"):</p>
     * <ul>
     *   <li>HP phai khop: {@code dvgv.hocPhan = dtt.dotThucTap.ctdtHocPhan.hocPhan}.</li>
     *   <li>HK phai khop: {@code lhp.id.maHocKy = dtt.dotThucTap.hocKy.maHocKy}.</li>
     *   <li>DoiNguGiangVienHP phai active ({@code trangThai = true}).</li>
     *   <li>SV phai co ghi danh lop HP tuong ung (HP + HK + CTDT).</li>
     * </ul>
     *
     * <p>Dung property-path (Hibernate auto-join qua ID hoac association MapsId):
     * {@code dvgv.id.maGiangVien} -> GiangVien qua association
     * {@code dvgv.giangVien.nguoiDung.maNguoiDung}. Tranh property sai
     * {@code gv.maGiangVien} (GiangVien co field {@code maGV}).</p>
     */
    @Query("SELECT CASE WHEN COUNT(dvgv) > 0 THEN true ELSE false END "
         + "FROM DanhSachThucTap dtt "
         + "JOIN DanhSachSvLopHocPhan dsvlhp ON dsvlhp.id.maSV = dtt.sinhVien.maSV "
         + "  AND dsvlhp.id.maCTDT = dtt.dotThucTap.ctdtHocPhan.id.maCTDT "
         + "  AND dsvlhp.id.maHocPhan = dtt.dotThucTap.ctdtHocPhan.id.maHocPhan "
         + "  AND dsvlhp.id.maHocKy = dtt.dotThucTap.hocKy.maHocKy "
         + "JOIN DoiNguGiangVienHp dvgv "
         + "  ON dvgv.id.maHocPhan = dtt.dotThucTap.ctdtHocPhan.id.maHocPhan "
         + "WHERE dtt.maThucTap = :maThucTap "
         + "  AND dvgv.trangThai = true "
         + "  AND dvgv.giangVien.nguoiDung.maNguoiDung = :maNguoiDung")
    boolean isGvTeachesStudent(
            @Param("maThucTap") Integer maThucTap,
            @Param("maNguoiDung") String maNguoiDung);

    /**
     * Bug-fix #4 - Ownership check cho SV: SV chi duoc xem chi tiet dot TT ma
     * minh dang tham gia.
     */
    boolean existsByDotThucTap_MaDotTTAndSinhVien_NguoiDung_MaNguoiDung(
            Integer maDotTT, String maNguoiDung);

    /**
     * Bug-fix #4 - Ownership check cho GV: GV chi duoc xem chi tiet dot TT
     * neu GV day HOC PHAN cua dot TT trong cung HOC KY.
     */
    @Query("SELECT CASE WHEN COUNT(dvgv) > 0 THEN true ELSE false END "
         + "FROM DotThucTap dot "
         + "JOIN DoiNguGiangVienHp dvgv "
         + "  ON dvgv.id.maHocPhan = dot.ctdtHocPhan.id.maHocPhan "
         + "WHERE dot.maDotTT = :maDotTT "
         + "  AND dvgv.trangThai = true "
         + "  AND dvgv.giangVien.nguoiDung.maNguoiDung = :maNguoiDung")
    boolean isGvOfDotTT(@Param("maDotTT") Integer maDotTT,
                          @Param("maNguoiDung") String maNguoiDung);

    /**
     * Bug-fix #4 - Ownership check cho DN: NV DN chi duoc xem chi tiet dot TT
     * neu DN co it nhat 1 SV trong dot duoc phan cong cho chinh DN do.
     */
    @Query("SELECT CASE WHEN COUNT(dtt) > 0 THEN true ELSE false END "
         + "FROM DanhSachThucTap dtt "
         + "WHERE dtt.dotThucTap.maDotTT = :maDotTT "
         + "  AND dtt.doanhNghiep.maDoanhNghiep = :maDoanhNghiep")
    boolean existsByDotAndDoanhNghiep(@Param("maDotTT") Integer maDotTT,
                                        @Param("maDoanhNghiep") String maDoanhNghiep);
}
