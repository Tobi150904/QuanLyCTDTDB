package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiTaiLieu {
    ChoDuyet("Cho duyet"),
    DaDuyet("Da duyet"),
    TuChoi("Tu choi");

    private final String tenHienThi;

    TrangThaiTaiLieu(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
