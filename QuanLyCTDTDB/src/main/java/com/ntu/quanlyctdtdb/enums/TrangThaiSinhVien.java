package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiSinhVien {
    DangHoc("Dang hoc"),
    BaoLuu("Bao luu"),
    ThoiHoc("Thoi hoc"),
    TotNghiep("Tot nghiep");

    private final String tenHienThi;

    TrangThaiSinhVien(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
