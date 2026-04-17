package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiHocPhan {
    BanNhap("Ban nhap"),
    ChoDuyet("Cho duyet"),
    DaDuyet("Da duyet");

    private final String tenHienThi;

    TrangThaiHocPhan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
