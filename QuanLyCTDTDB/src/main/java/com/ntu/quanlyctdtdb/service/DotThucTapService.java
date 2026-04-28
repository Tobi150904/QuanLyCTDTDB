package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.DotThucTapDTO;
import com.ntu.quanlyctdtdb.entity.DanhSachThucTap;
import com.ntu.quanlyctdtdb.entity.DotThucTap;
import com.ntu.quanlyctdtdb.entity.KetQuaThucTap;
import com.ntu.quanlyctdtdb.enums.LoaiThucTap;

import java.math.BigDecimal;
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

    /**
     * Phase 3 — thong ke nhanh cho stat-card row tren danh sach.
     * Key:
     *   tong         : tong so dot TT
     *   chuanBi      : ChuanBi
     *   choDuyet     : ChoDuyet
     *   daDuyet      : DaDuyet
     *   dangThucHien : DangThucHien
     *   daKetThuc    : DaKetThuc
     */
    Map<String, Long> getThongKe();

    // -------------------------------------------------------------------------
    // Phase 7 — He thong 2 cot diem cho Thuc Tap
    //
    // Yeu cau (theo nguoi dung):
    //   - Tai truong:    Cot 1 = GV_HD (giang vien huong dan)
    //                    Cot 2 = GV_PB (giang vien phan bien)
    //   - Tai DN:        Cot 1 = DN    (nhan vien DN)
    //                    Cot 2 = GV_HD (giang vien giam sat tu phia truong)
    //
    // Mo hinh du lieu: KetQuaThucTap UNIQUE(MaThucTap, MaVaiTro) -> moi
    // (SV, vai tro) chi co 1 row diem. Co the upsert.
    // -------------------------------------------------------------------------

    /**
     * Lay tat ca KetQuaThucTap cua mot dot, group theo MaThucTap (= maDanhSach
     * = primary key cua DanhSachThucTap), trong moi nhom map theo MaVaiTro.
     *
     * <p>Output: Map&lt;maThucTap, Map&lt;maVaiTro, KetQuaThucTap&gt;&gt;
     * giup template render bang voi 2 cot diem ma chi 1 lan query.</p>
     */
    Map<Integer, Map<String, KetQuaThucTap>> getKetQuaMapByDot(Integer maDotTT);

    /**
     * Upsert KetQuaThucTap cho 1 (DanhSach, VaiTro). Neu da co thi update
     * Diem + NhanXet (va MaNguoiDanhGia neu duoc cap nhat); neu chua co thi
     * insert moi.
     *
     * <p>Validate:</p>
     * <ul>
     *   <li>{@code maVaiTro} phai ton tai (GV_HD / GV_PB / DN / CVHT).</li>
     *   <li>{@code diem} (neu co) phai trong [0, 10].</li>
     *   <li>{@code maGiangVienDanhGia} phai ton tai trong bang GiangVien.</li>
     *   <li>Khong cho phep nhap diem khi DanhSachThucTap dang o trang thai
     *       DaHuy.</li>
     * </ul>
     *
     * @param maDanhSach        primary key cua DanhSachThucTap (= MaThucTap)
     * @param maVaiTro          GV_HD | GV_PB | DN | CVHT
     * @param diem              null neu chi nhap nhan xet, hoac 0..10
     * @param nhanXet           text feedback, co the null/blank
     * @param maGiangVienDanhGia ma GV nguoi dang nhap nhap diem (audit). Neu null,
     *                          giu nguyen nguoi danh gia hien co.
     */
    KetQuaThucTap capNhatDiem(Integer maDanhSach, String maVaiTro,
                                BigDecimal diem, String nhanXet,
                                String maGiangVienDanhGia);
}
