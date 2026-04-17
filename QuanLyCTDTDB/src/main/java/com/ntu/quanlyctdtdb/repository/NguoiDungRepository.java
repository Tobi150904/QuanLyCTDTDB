package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.VaiTro;

import java.util.List;
import java.util.Optional;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, String> {

    /**
     * UserDetailsService su dung - tim theo ten dang nhap
     */
    Optional<NguoiDung> findByTenDangNhap(String tenDangNhap);

    /**
     * Kiem tra email ton tai (khi them moi nguoi dung)
     */
    boolean existsByEmail(String email);

    /**
     * Kiem tra ten dang nhap ton tai
     */
    boolean existsByTenDangNhap(String tenDangNhap);

    /**
     * Lay danh sach SV theo lop hanh chinh
     * Dung trong: Dot Kien Tap, Dot Thuc Tap
     */
    List<NguoiDung> findByLopHanhChinh_MaLopHC(String maLopHC);

    /**
     * Lay danh sach nguoi dung theo vai tro
     * Dung trong: dropdown chon GV, chon CVHT, chon GV giam sat...
     */
    @Query("SELECT DISTINCT nd FROM NguoiDung nd " +
           "JOIN nd.vaiTros ndvt " +
           "WHERE ndvt.id.vaiTro = :vaiTro " +
           "AND nd.trangThaiTK = true " +
           "ORDER BY nd.hoTen ASC")
    List<NguoiDung> findByVaiTro(@Param("vaiTro") VaiTro vaiTro);

    /**
     * Tim kiem nguoi dung voi keyword (ho ten hoac email)
     * Co phan trang
     */
    @Query("SELECT nd FROM NguoiDung nd WHERE " +
           "LOWER(nd.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(nd.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(nd.maNguoiDung) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<NguoiDung> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /**
     * Tim kiem nguoi dung theo vai tro va keyword
     */
    @Query("SELECT DISTINCT nd FROM NguoiDung nd " +
           "JOIN nd.vaiTros ndvt " +
           "WHERE ndvt.id.vaiTro = :vaiTro " +
           "AND (LOWER(nd.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(nd.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<NguoiDung> searchByVaiTroAndKeyword(@Param("vaiTro") VaiTro vaiTro,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);

    /**
     * Lay CVHT cua mot SV (de gui email canh bao - Rule 4)
     */
    @Query("SELECT cvht FROM NguoiDung cvht " +
           "JOIN LopHanhChinh lhc ON lhc.maCoVan = cvht.maNguoiDung " +
           "JOIN NguoiDung sv ON sv.lopHanhChinh.maLopHC = lhc.maLopHC " +
           "WHERE sv.maNguoiDung = :maSV")
    Optional<NguoiDung> findCVHTByMaSV(@Param("maSV") String maSV);

    /**
     * Lay tai khoan cua doanh nghiep (role DN)
     */
    Optional<NguoiDung> findByDoanhNghiep_MaDoanhNghiep(String maDoanhNghiep);

    /**
     * Lay tat ca nguoi dung co phan trang
     */
    Page<NguoiDung> findAllByOrderByMaNguoiDungAsc(Pageable pageable);
}
