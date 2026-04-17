package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.ChuongTrinhDaoTaoDTO;
import com.ntu.quanlyctdtdb.entity.ChuongTrinhDaoTao;
import com.ntu.quanlyctdtdb.enums.TrangThaiCTDT;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface ChuongTrinhDaoTaoService {

    Page<ChuongTrinhDaoTao> findAll(String keyword, TrangThaiCTDT trangThai, Pageable pageable);
    ChuongTrinhDaoTao findById(String maCTDT);
    ChuongTrinhDaoTao create(ChuongTrinhDaoTaoDTO dto, MultipartFile fileWord, String maNguoiTao);
    ChuongTrinhDaoTao update(String maCTDT, ChuongTrinhDaoTaoDTO dto, MultipartFile fileWord);
    ChuongTrinhDaoTao chuyenTrangThai(String maCTDT, TrangThaiCTDT trangThaiMoi, String maNguoiDuyet, String lyDo);
    void autoCreateLopHocPhan(ChuongTrinhDaoTao ctdt);
}