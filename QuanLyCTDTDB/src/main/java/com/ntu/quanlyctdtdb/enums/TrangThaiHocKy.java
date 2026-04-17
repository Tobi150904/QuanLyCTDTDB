package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiHocKy {
    SapDienRa("Sap dien ra"),
    DangDienRa("Dang dien ra"),
    DaKetThuc("Da ket thuc");

    private final String tenHienThi;

    TrangThaiHocKy(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
