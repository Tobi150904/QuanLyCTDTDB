package com.ntu.quanlyctdtdb.enums;

/**
 * Trang thai vong doi cua DotThucTap.
 * Khop state machine: docs/02 §3.8 + docs/03 WF-08.1.
 *
 *   ChuanBi --(nop duyet)--> ChoDuyet --(phe duyet)--> DaDuyet
 *   DaDuyet --(bat dau)--> DangThucHien --(ket thuc)--> DaKetThuc
 *   {ChuanBi,ChoDuyet,DaDuyet,DangThucHien} --(huy)--> DaHuy
 */
public enum TrangThaiDotTT {
    ChuanBi, ChoDuyet, DaDuyet, DangThucHien, DaKetThuc, DaHuy
}
