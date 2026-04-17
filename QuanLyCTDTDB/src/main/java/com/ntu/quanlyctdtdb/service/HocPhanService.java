package com.ntu.quanlyctdtdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.ntu.quanlyctdtdb.dto.HocPhanDTO;
import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHP;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiHocPhan;

import java.util.List;

/**
 * Contract cho HocPhanService.
 * Impl: HocPhanServiceImpl.java
 */
public interface HocPhanService {

    Page<HocPhan> findAll(String keyword, TrangThaiHocPhan trangThai, Pageable pageable);

    HocPhan findById(String maHocPhan);

    HocPhan create(HocPhanDTO dto, MultipartFile fileDeCuong);

    HocPhan update(String maHocPhan, HocPhanDTO dto, MultipartFile fileDeCuong);

    /**
     * Workflow phe duyet hoc phan.
     * Chuyen trang thai theo dung thu tu:
     *   BanNhap -> ChoDuyet (BCN nop len)
     *   ChoDuyet -> DaDuyet (TTDTXS/PDT duyet)
     *   ChoDuyet -> BanNhap (TTDTXS/PDT tra lai)
     * Throw BusinessException neu chuyen trang thai khong hop le.
     */
    HocPhan chuyenTrangThai(String maHocPhan, TrangThaiHocPhan trangThaiMoi,
                            String maNguoiDuyet, String lyDo);

    // ---- Doi ngu giang vien ----

    /**
     * Lay danh sach doi ngu GV cua mot hoc phan.
     */
    List<DoiNguGiangVienHP> findDoiNgu(String maHocPhan);

    /**
     * Them GV vao doi ngu hoc phan.
     * Throw BusinessException neu GV da co trong doi ngu.
     */
    void themGVVaoDoiNgu(String maHocPhan, String maGiangVien);

    /**
     * Xoa GV khoi doi ngu (set TrangThai=0, khong xoa row).
     * Throw BusinessException neu GV dang giang day LopHocPhan cua HP nay trong HK hien tai.
     */
    void xoaGVKhoiDoiNgu(String maHocPhan, String maGiangVien);

    /**
     * Kiem tra GV co thuoc doi ngu active cua HP khong.
     * Dung truoc khi gan GV day LopHocPhan.
     */
    boolean isGVThuocDoiNgu(String maHocPhan, String maGiangVien);
}
