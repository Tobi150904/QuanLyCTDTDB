package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.DotKienTapDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachSvKienTap;
import com.ntu.quanlyctdtdb.entity.DotKienTap;

import java.util.List;
import java.util.Map;

public interface DotKienTapService {

    List<DotKienTap> findAll();

    DotKienTap findById(Integer id);

    /** CVHT tao dot kien tap moi */
    DotKienTap create(DotKienTapDTO dto, String maNguoiDungTao);

    /** Cap nhat thong tin dot kien tap */
    DotKienTap update(Integer id, DotKienTapDTO dto);

    /** Gui dot len cho lanh dao phe duyet */
    DotKienTap guiPheDuyet(Integer id);

    /** Lanh dao / quan tri phe duyet */
    DotKienTap pheduyet(Integer id, String maNguoiDung);

    /** Them sinh vien vao dot kien tap tu file Excel.
     *  Tra ve map: "success" -> int, "errors" -> List<String> */
    Map<String, Object> importSinhVienTuExcel(Integer maDotKT, List<String> dsMaSV);

    List<DanhSachSvKienTap> findDanhSachSVKienTap(Integer maDotKT);
}
