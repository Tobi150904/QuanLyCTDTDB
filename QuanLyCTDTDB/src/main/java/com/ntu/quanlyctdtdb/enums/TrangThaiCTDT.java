package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiCTDT {
    BanNhap("Ban nhap"),
    ChoDuyet("Cho duyet"),
    DaDuyet("Da duyet"),
    DaHuy("Da huy");

    private final String tenHienThi;

    TrangThaiCTDT(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
