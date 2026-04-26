package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.NhapNhanXetDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachSvLopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhan;
import com.ntu.quanlyctdtdb.entity.LopHocPhanId;

import java.util.List;

/**
 * Service Phase 4 — Danh Gia & Canh Bao Sinh Vien.
 * <ul>
 *   <li>GV nhap nhan xet tung SV trong lop HP minh day. Neu tick "Canh Bao",
 *       he thong tu dong gui email den CVHT cua lop hanh chinh ma SV thuoc.</li>
 *   <li>CVHT thay danh sach canh bao chua xu ly, nhap KetQuaXuLy de dong.
 *       DaCanhBao van giu = 1 (lich su), KetQuaXuLy duoc luu (xem docs/03 §WF-06).</li>
 * </ul>
 */
public interface DanhGiaService {

    /** Lay danh sach lop HP do GV dang day (dung trang index cho GV). */
    List<LopHocPhan> findLopHpCuaGv(String maGV);

    /** Dem so SV bi canh bao trong 1 lop HP. */
    long demSoCanhBao(LopHocPhanId lopId);

    /** Lay danh sach SV trong lop HP voi nhan xet hien co (sap xep theo ho ten). */
    List<DanhSachSvLopHocPhan> findDanhSachSvTrongLop(LopHocPhanId lopId);

    /**
     * GV nhap/cap nhat nhan xet cho 1 SV. Neu daCanhBao chuyen tu false -> true,
     * tu dong gui email canh bao den CVHT cua lop hanh chinh ma SV thuoc.
     *
     * @return ban ghi sau khi cap nhat
     */
    DanhSachSvLopHocPhan nhapNhanXet(NhapNhanXetDTO dto);

    /** SV xem cac nhan xet/canh bao ve minh (sap xep HK moi nhat truoc). */
    List<DanhSachSvLopHocPhan> findNhanXetCuaSv(String maSV);

    /**
     * Canh bao chua xu ly cho CVHT — chi cac SV thuoc lop HC do CVHT phu trach.
     * Neu maGV null hoac role khong phai CVHT, tra ve toan bo (PDT/ADMIN view).
     */
    List<DanhSachSvLopHocPhan> findCanhBaoChoCvht(String maGV);

    /** Tat ca canh bao trong he thong (PDT/ADMIN view, khong loc theo CVHT). */
    List<DanhSachSvLopHocPhan> findCanhBaoTatCa();

    /**
     * CVHT xu ly canh bao -> set KetQuaXuLy. Neu khong tim thay throw
     * ResourceNotFoundException. Neu KetQuaXuLy trong throw BusinessException.
     */
    DanhSachSvLopHocPhan xuLyCanhBao(String maCTDT, String maHocPhan, String maHocKy,
                                      Integer maLop, String maSV, String ketQuaXuLy);
}
