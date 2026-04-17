package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.DanhGiaDTO;
import com.ntu.quanlyctdtdb.entity.DanhGiaVaCanhBao;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;

import java.util.List;

public interface DanhGiaVaCanhBaoService {

    List<DanhGiaVaCanhBao> findByLopHocPhan(LopHocPhanId lopHocPhanId);
    List<DanhGiaVaCanhBao> findBySinhVien(String maSV);
    List<DanhGiaVaCanhBao> findCanhBaoChuaXuLy(String maLopHC);
    long countCanhBaoChuaXuLy(String maLopHC);
    DanhGiaVaCanhBao taoNhanXet(DanhGiaDTO dto, String maNguoiNhanXet);
    DanhGiaVaCanhBao xuLyCanhBao(Integer maDanhGia, String ketQuaXuLy);
}