package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiDotTT {
    ChuanBi("Chuan bi"),
    ChoDuyet("Cho duyet"),
    DaDuyet("Da duyet"),
    DangThucHien("Dang thuc hien"),
    DaKetThuc("Da ket thuc");

    private final String tenHienThi;

    TrangThaiDotTT(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
