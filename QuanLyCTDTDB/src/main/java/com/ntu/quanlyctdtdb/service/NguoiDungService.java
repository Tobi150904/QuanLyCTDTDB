package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.NguoiDungDTO;
import com.ntu.quanlyctdtdb.dto.NguoiDungExcelDTO;
import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.enums.LoaiNguoiDung;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface NguoiDungService {

    Page<NguoiDung> search(String keyword, LoaiNguoiDung loai, Pageable pageable);

    NguoiDung findById(String ma);

    /** Lay NguoiDung kem fetch nhomNguoiDungs (danh cho chi-tiet/form sua) */
    NguoiDung findByIdWithRoles(String ma);

    NguoiDung create(NguoiDungDTO dto);

    NguoiDung update(String ma, NguoiDungDTO dto);

    void toggleTrangThai(String ma);

    /** Import hang loat tu Excel. Tra ve map: "success" -> count, "errors" -> List<String> */
    Map<String, Object> importFromExcel(List<NguoiDungExcelDTO> rows);

    /** Sinh MaNguoiDung tu dong theo LoaiNguoiDung */
    String sinhMaNguoiDung(LoaiNguoiDung loai);

    /** Lay thong ke tong hop */
    Map<String, Long> getThongKe();
}
