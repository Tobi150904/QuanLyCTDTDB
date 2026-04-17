package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.TaiLieuMonHocDTO;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;
import com.ntu.quanlyctdtdb.entity.TaiLieuMonHoc;
import com.ntu.quanlyctdtdb.enums.TrangThaiTaiLieu;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaiLieuMonHocService {

    List<TaiLieuMonHoc> findByLopHocPhan(LopHocPhanId lopHocPhanId);
    TaiLieuMonHoc findById(Integer maTaiLieu);
    TaiLieuMonHoc nopTaiLieu(TaiLieuMonHocDTO dto, MultipartFile file, String maGiangVien);
    TaiLieuMonHoc duyetTaiLieu(Integer maTaiLieu, String maCNHP, String nhanXet);
    TaiLieuMonHoc tuChoiTaiLieu(Integer maTaiLieu, String maCNHP, String lyDo);
    boolean isNopQuaHanDeCuong(LopHocPhanId lopHocPhanId);
    List<TaiLieuMonHoc> findByTrangThai(TrangThaiTaiLieu trangThai, String maCNHP);
}