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

    /**
     * Phase 7 — TTDTXS bat dau dot thuc tap.
     * <p>DaDuyet -> DangThucHien. Block neu khong o trang thai DaDuyet.</p>
     */
    DotThucTap batDau(Integer id);

    /**
     * Phase 7 — TTDTXS ket thuc dot thuc tap.
     *
     * <p>DangThucHien -> DaKetThuc. Cascade: tat ca DanhSachThucTap thuoc dot
     * dang o trang thai DaPhanCong / DangThucTap se chuyen sang DaKetThuc
     * (giu nguyen DaHuy de bao toan audit).</p>
     */
    DotThucTap ketThuc(Integer id);

    /**
     * Phase 7 — TTDTXS huy dot thuc tap (truoc khi DaKetThuc).
     *
     * <p>{ChuanBi, ChoDuyet, DaDuyet, DangThucHien} -> DaHuy.
     * Khong cho phep huy khi dot da DaKetThuc hoac da DaHuy.</p>
     */
    DotThucTap huy(Integer id);

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
     * <p>Phase 7 refactor: nguoi danh gia la {@code NguoiDung} (khong qua
     * GiangVien) — co the la NV DN (vai tro DN) hoac GV (vai tro GV_HD/GV_PB/CVHT).
     * Validate role-consistency:</p>
     * <ul>
     *   <li>vai tro {@code DN}            -> NguoiDung phai la {@link com.ntu.quanlyctdtdb.enums.LoaiNguoiDung#DOANH_NGHIEP}</li>
     *   <li>vai tro {@code GV_HD/GV_PB}   -> NguoiDung phai la GIANG_VIEN hoac CVHT</li>
     *   <li>vai tro {@code CVHT}          -> NguoiDung phai la CVHT</li>
     * </ul>
     *
     * <p>Other validate:</p>
     * <ul>
     *   <li>{@code maVaiTro} phai ton tai (GV_HD / GV_PB / DN / CVHT).</li>
     *   <li>{@code diem} (neu co) phai trong [0, 10].</li>
     *   <li>{@code maNguoiDanhGia} phai ton tai trong bang NguoiDung.</li>
     *   <li>Khong cho phep nhap diem khi DanhSachThucTap dang o trang thai
     *       DaHuy.</li>
     * </ul>
     *
     * @param maDanhSach     primary key cua DanhSachThucTap (= MaThucTap)
     * @param maVaiTro       GV_HD | GV_PB | DN | CVHT
     * @param diem           null neu chi nhap nhan xet, hoac 0..10
     * @param nhanXet        text feedback, co the null/blank
     * @param maNguoiDanhGia ma NguoiDung nguoi danh gia (audit). Neu null,
     *                       giu nguyen nguoi danh gia hien co.
     */
    KetQuaThucTap capNhatDiem(Integer maDanhSach, String maVaiTro,
                                BigDecimal diem, String nhanXet,
                                String maNguoiDanhGia);
}
