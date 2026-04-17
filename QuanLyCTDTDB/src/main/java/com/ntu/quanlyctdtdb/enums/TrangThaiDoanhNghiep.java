package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiDoanhNghiep {
    DangHopTac("Dang hop tac"),
    TamNgung("Tam ngung");

    private final String tenHienThi;

    TrangThaiDoanhNghiep(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
