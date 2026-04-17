package com.ntu.quanlyctdtdb.service;

import java.util.List;

import com.ntu.quanlyctdtdb.dto.DanhGiaDTO;
import com.ntu.quanlyctdtdb.entity.DanhGiaVaCanhBao;

/**
 * Contract cho DanhGiaVaCanhBaoService.
 * Impl: DanhGiaVaCanhBaoServiceImpl.java
 */
public interface DanhGiaVaCanhBaoService {

    List<DanhGiaVaCanhBao> findByLopHocPhan(String maLopHP);

    List<DanhGiaVaCanhBao> findBySinhVien(String maSV);

    /**
     * Lay danh sach canh bao CHUA XU LY cua lop hanh chinh (cho CVHT).
     */
    List<DanhGiaVaCanhBao> findCanhBaoChuaXuLy(String maLopHC);

    /**
     * Dem so canh bao chua xu ly (dung cho badge tren dashboard CVHT).
     */
    long countCanhBaoChuaXuLy(String maLopHC);

    /**
     * GV / CVHT tao nhan xet moi.
     *
     * QUAN TRONG - Side effect (Giai Doan 5 - Workflow):
     *   Neu dto.loaiNhanXet = TieuCuc:
     *     1. Luu DanhGiaVaCanhBao voi DaXuLy=0
     *     2. emailService.guiCanhBaoDenSV(maSV)
     *     3. emailService.guiCanhBaoDenCVHT(maLopHC)
     *   Neu TichCuc: chi luu, KHONG gui email.
     *   Ca hai truong hop deu trong cung @Transactional.
     */
    DanhGiaVaCanhBao taoNhanXet(DanhGiaDTO dto);

    /**
     * CVHT xu ly canh bao: cap nhat KetQuaXuLy va DaXuLy=1.
     */
    DanhGiaVaCanhBao xuLyCanhBao(Integer maDanhGia, String ketQuaXuLy, String maCVHT);
}
