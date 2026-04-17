package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiDotKT {
    ChuanBi("Chuan bi"),
    ChoDuyet("Cho duyet"),
    DaDuyet("Da duyet"),
    DaThucHien("Da thuc hien"),
    DaHuy("Da huy");

    private final String tenHienThi;

    TrangThaiDotKT(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
