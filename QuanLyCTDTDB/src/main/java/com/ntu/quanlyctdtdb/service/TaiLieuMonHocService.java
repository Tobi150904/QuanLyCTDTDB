package com.ntu.quanlyctdtdb.service;

import org.springframework.web.multipart.MultipartFile;

import com.ntu.quanlyctdtdb.dto.TaiLieuMonHocDTO;
import com.ntu.quanlyctdtdb.entity.TaiLieuMonHoc;
import com.ntu.quanlyctdtdb.enums.LoaiTaiLieu;
import com.ntu.quanlyctdtdb.enums.TrangThaiTaiLieu;

import java.util.List;

/**
 * Contract cho TaiLieuMonHocService.
 * Impl: TaiLieuMonHocServiceImpl.java
 */
public interface TaiLieuMonHocService {

    List<TaiLieuMonHoc> findByLopHocPhan(String maLopHP);

    TaiLieuMonHoc findById(Integer maTaiLieu);

    /**
     * GV nop tai lieu (DeCuongChiTiet, DeThiGiuaKy, DeThiCuoiKy).
     *
     * QUAN TRONG - Rule 6: UNIQUE(MaLopHP, Loai)
     *   Neu ban (maLopHP, loai) DA TON TAI -> UPDATE ban cu (ke ca khi TrangThai = DaDuyet/TuChoi).
     *   Neu CHUA TON TAI -> INSERT moi.
     *   Khi nop lai: reset TrangThai = ChoDuyet, xoa NguoiDuyet/NgayDuyet/NhanXet cu.
     *
     * @return TaiLieuMonHoc sau khi luu (INSERT hoac UPDATE)
     */
    TaiLieuMonHoc nopTaiLieu(TaiLieuMonHocDTO dto, MultipartFile file, String maGiangVien);

    /**
     * CNHP duyet tai lieu.
     * Chuyen TrangThai: ChoDuyet -> DaDuyet.
     * Ghi nhan NguoiDuyet va NgayDuyet.
     */
    TaiLieuMonHoc duyetTaiLieu(Integer maTaiLieu, String maCNHP, String nhanXet);

    /**
     * CNHP tu choi tai lieu, yeu cau chinh sua.
     * Chuyen TrangThai: ChoDuyet -> TuChoi.
     * @param lyDo ly do tu choi (bat buoc)
     */
    TaiLieuMonHoc tuChoiTaiLieu(Integer maTaiLieu, String maCNHP, String lyDo);

    /**
     * Kiem tra de cuong chi tiet co bi nop qua han khong.
     * Han nop: 2 tuan (14 ngay) dau hoc ky (tinh tu NgayBatDau cua HocKyNamHoc).
     */
    boolean isNopQuaHanDeCuong(String maLopHP);

    /**
     * Lay danh sach tai lieu cho man hinh duyet cua CNHP.
     * Filter theo TrangThai (mac dinh: ChoDuyet).
     */
    List<TaiLieuMonHoc> findByTrangThai(TrangThaiTaiLieu trangThai, String maCNHP);
}
