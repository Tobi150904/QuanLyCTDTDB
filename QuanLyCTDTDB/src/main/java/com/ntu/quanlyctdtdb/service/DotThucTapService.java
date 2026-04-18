package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.DotThucTapDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachThucTap;
import com.ntu.quanlyctdtdb.entity.DotThucTap;

import java.util.List;
import java.util.Map;

public interface DotThucTapService {

    List<DotThucTap> findAll();

    DotThucTap findById(Integer id);

    /** CVHT tao dot thuc tap moi cho mot HP CTDT */
    DotThucTap create(DotThucTapDTO dto, String maNguoiDungTao);

    DotThucTap update(Integer id, DotThucTapDTO dto);

    /** Gui duyet → trang thai ChoDuyet */
    DotThucTap guiPheDuyet(Integer id);

    /** Lanh dao / quan tri phe duyet */
    DotThucTap pheduyet(Integer id, String maNguoiDung);

    /** Import sinh vien (csv maSV list).
     *  Tra ve map: "success" -> int, "errors" -> List<String> */
    Map<String, Object> importSinhVien(Integer maDotTT, List<String> dsMaSV);

    List<DanhSachThucTap> findDanhSachSV(Integer maDotTT);

    /** Nhap ket qua thuc tap cho SV */
    DanhSachThucTap capNhatKetQua(Integer maDanhSach, String loaiThucTap,
                                    String maDoanhNghiep, String nhanXet);
}
