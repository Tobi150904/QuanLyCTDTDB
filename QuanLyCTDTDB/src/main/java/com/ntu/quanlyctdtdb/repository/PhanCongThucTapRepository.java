package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.PhanCongThucTap;
import com.ntu.quanlyctdtdb.enums.TrangThaiPhanCong;

import java.util.List;

@Repository
public interface PhanCongThucTapRepository extends JpaRepository<PhanCongThucTap, Integer> {

    /**
     * Lay tat ca phan cong cua mot dot thuc tap
     */
    List<PhanCongThucTap> findByDotThucTap_MaDotTT(Integer maDotTT);

    /**
     * Lay phan cong cua mot SV (xem lich su thuc tap)
     */
    List<PhanCongThucTap> findBySinhVien_MaNguoiDung(String maSV);

    /**
     * Rule 7: Kiem tra SV da duoc phan cong trong dot nay chua (tranh duplicate)
     * Dung khi import Excel: neu true -> skip dong do, log loi
     */
    boolean existsByDotThucTap_MaDotTTAndSinhVien_MaNguoiDung(
            Integer maDotTT, String maSV);

    /**
     * Lay phan cong theo GV giam sat (GV xem SV minh giam sat)
     */
    List<PhanCongThucTap> findByGiangVienGiamSat_MaNguoiDung(String maGV);

    /**
     * Lay phan cong theo doanh nghiep (DN xem SV thuc tap tai DN minh)
     */
    List<PhanCongThucTap> findByDoanhNghiep_MaDoanhNghiep(String maDoanhNghiep);

    /**
     * Lay phan cong theo dot va trang thai
     */
    List<PhanCongThucTap> findByDotThucTap_MaDotTTAndTrangThai(
            Integer maDotTT, TrangThaiPhanCong trangThai);

    /**
     * Lay phan cong theo dot va doanh nghiep
     */
    List<PhanCongThucTap> findByDotThucTap_MaDotTTAndDoanhNghiep_MaDoanhNghiep(
            Integer maDotTT, String maDoanhNghiep);
}
