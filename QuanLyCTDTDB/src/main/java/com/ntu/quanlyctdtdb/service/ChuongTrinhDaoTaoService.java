package com.ntu.quanlyctdtdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.ntu.quanlyctdtdb.dto.ChuongTrinhDaoTaoDTO;
import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.entity.HocPhan;
import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;

import java.util.List;

/**
 * Contract cho ChuongTrinhDaoTaoService.
 * Impl: ChuongTrinhDaoTaoServiceImpl.java
 */
public interface ChuongTrinhDaoTaoService {

    Page<ChuongTrinhDaoTao> findAll(String keyword, TrangThaiCTDT trangThai, Pageable pageable);

    ChuongTrinhDaoTao findById(String maCTDT);

    ChuongTrinhDaoTao create(ChuongTrinhDaoTaoDTO dto, MultipartFile fileWord);

    ChuongTrinhDaoTao update(String maCTDT, ChuongTrinhDaoTaoDTO dto, MultipartFile fileWord);

    /**
     * Workflow phe duyet CTDT.
     * Chuyen trang thai dung thu tu:
     *   BanNhap -> ChoDuyet (BCN nop len)
     *   ChoDuyet -> DaDuyet (TTDTXS/PDT duyet) -> TRIGGER autoCreateLopHocPhan()
     *   ChoDuyet -> BanNhap (TTDTXS/PDT tra lai)
     *   DaDuyet  -> DaHuy  (PDT huy)
     *
     * QUAN TRONG: khi chuyenTrangThai -> DaDuyet,
     *   phai goi autoCreateLopHocPhan() trong cung @Transactional.
     */
    ChuongTrinhDaoTao chuyenTrangThai(String maCTDT, TrangThaiCTDT trangThaiMoi,
                                       String maNguoiDuyet, String lyDo);

    /**
     * Tu dong tao LopHocPhan cho tung HocPhan trong CTDT sau khi duoc duyet.
     * Moi HP -> 1 LopHocPhan moi voi:
     *   - MaGiangVien = null (chua gan)
     *   - TrangThai   = DangMo
     *   - MaHocKy     = HocKy hien tai (TrangThai = DangDienRa)
     * Goi noi bo tu chuyenTrangThai() khi -> DaDuyet.
     * KHONG goi truc tiep tu Controller.
     */
    void autoCreateLopHocPhan(ChuongTrinhDaoTao ctdt);

    /**
     * Lay danh sach hoc phan thuoc CTDT.
     * (CTDT -> LopHanhChinh -> SinhVien la mo quan he, HP liet ke rieng)
     */
    List<HocPhan> findHocPhanByCTDT(String maCTDT);
}
