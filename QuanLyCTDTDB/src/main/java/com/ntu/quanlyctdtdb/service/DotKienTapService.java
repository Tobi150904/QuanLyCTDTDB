package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.DotKienTapDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachSvKienTap;
import com.ntu.quanlyctdtdb.entity.DotKienTap;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Service cho DotKienTap. Quy tac Hybrid DaThamGia:
 *   - Khi tao dot: auto-add tat ca SV TrangThaiSV=DangHoc cua lop voi DaThamGia=1.
 *   - Admin/BCN co the toggle DaThamGia (khong xoa ban ghi - giu de audit).
 *   - Khi can: dong bo lai danh sach sau khi lop bien dong.
 * Chi tiet: docs/03_WORKFLOW.md WF-07.*
 */
public interface DotKienTapService {

    List<DotKienTap> findAll();

    DotKienTap findById(Integer id);

    /**
     * Tao dot kien tap moi.
     *  - SET NguoiTao = currentUser
     *  - Validate DN.TrangThai = DangHopTac
     *  - AUTO-ADD TAT CA SV DangHoc cua lop vao DanhSachSinhVienKienTap (DaThamGia=1)
     */
    DotKienTap create(DotKienTapDTO dto, MultipartFile fileMinhChung, String maNguoiDungTao);

    /** Cap nhat thong tin dot (chi cho phep khi ChuanBi/ChoDuyet). */
    DotKienTap update(Integer id, DotKienTapDTO dto, MultipartFile fileMinhChung);

    /** BCN: ChuanBi -> ChoDuyet. Yeu cau co it nhat 1 SV DaThamGia=1. */
    DotKienTap guiPheDuyet(Integer id);

    /** TTDTXS: ChoDuyet -> DaDuyet. SET NguoiDuyet + NgayDuyet. */
    DotKienTap pheduyet(Integer id, String maNguoiDuyet);

    /** BCN/TTDTXS: DaDuyet -> DaThucHien. */
    DotKienTap hoanThanh(Integer id);

    /** BCN/TTDTXS: {ChuanBi,ChoDuyet,DaDuyet,DaThucHien} -> DaHuy. */
    DotKienTap huy(Integer id);

    // ============== WF-07.2: Toggle DaThamGia ==============

    /**
     * Cap nhat co DaThamGia cho 1 SV trong 1 dot.
     * Khoa khi dot o trang thai DaHuy. KHONG xoa ban ghi (audit).
     */
    DanhSachSvKienTap capNhatDaThamGia(Integer maDotKT, String maSV, boolean daThamGia);

    // ============== WF-07.3: Dong bo danh sach ==============

    /**
     * Them cac SV DangHoc moi (chuyen vao lop sau khi tao dot) vao DanhSachSinhVienKienTap.
     * KHONG xoa ban ghi hien co.
     * Tra ve so ban ghi moi duoc them.
     */
    int dongBoDanhSachSV(Integer maDotKT);

    // ============== WF-07.4: Nhan xet ==============

    /** GV phu trach nhap nhan xet. Validate currentUser.maGV == MaGVPhuTrach. */
    DotKienTap nhanXetGV(Integer maDotKT, String maNguoiDungHienTai, String nhanXet);

    /** DN tiep don nhap nhan xet. Validate currentUser.maNguoiDung == MaDoanhNghiep. */
    DotKienTap nhanXetDN(Integer maDotKT, String maNguoiDungHienTai, String nhanXet);

    // ============== Doc danh sach ==============

    List<DanhSachSvKienTap> findDanhSachSVKienTap(Integer maDotKT);

    /**
     * Phase 3 — thong ke nhanh cho stat-card row tren danh sach.
     * Key:
     *   tong       : tong so dot KT
     *   chuanBi    : ChuanBi
     *   choDuyet   : ChoDuyet
     *   daDuyet    : DaDuyet
     *   daThucHien : DaThucHien
     */
    Map<String, Long> getThongKe();
}
