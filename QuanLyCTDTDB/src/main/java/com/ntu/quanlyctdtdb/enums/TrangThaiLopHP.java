package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiLopHP {
    DangMo("Dang mo"),
    DaDong("Da dong"),
    DaHuy("Da huy");

    private final String tenHienThi;

    TrangThaiLopHP(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
