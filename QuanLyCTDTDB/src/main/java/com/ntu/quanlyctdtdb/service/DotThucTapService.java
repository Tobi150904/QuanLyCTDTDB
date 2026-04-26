package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.DotThucTapDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachThucTap;
import com.ntu.quanlyctdtdb.entity.DotThucTap;
import com.ntu.quanlyctdtdb.enums.LoaiThucTap;

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

    /** Import sinh vien (csv maSV list) — default LoaiThucTap = Truong, khong DN.
     *  Tra ve map: "success" -> int, "errors" -> List&lt;String&gt; */
    Map<String, Object> importSinhVien(Integer maDotTT, List<String> dsMaSV);

    /**
     * Import sinh vien voi default LoaiThucTap + (optional) MaDoanhNghiep
     * cho ca lo. Per-SV customization van co the lam tiep qua
     * {@link #capNhatKetQua(Integer, String, String, String)} sau khi import.
     *
     * <p>Validate: dot phai chua DaKetThuc/DaHuy; SV phai DangHoc; DN phai
     * DangHopTac neu loai=DoanhNghiep.</p>
     */
    Map<String, Object> importSinhVien(Integer maDotTT, List<String> dsMaSV,
                                        LoaiThucTap loaiTT, String maDoanhNghiep);

    List<DanhSachThucTap> findDanhSachSV(Integer maDotTT);

    /** Nhap ket qua thuc tap cho SV */
    DanhSachThucTap capNhatKetQua(Integer maDanhSach, String loaiThucTap,
                                    String maDoanhNghiep, String nhanXet);
}
