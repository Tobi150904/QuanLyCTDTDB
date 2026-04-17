package com.ntu.quanlyctdtdb.enums;

public enum LoaiTaiLieu {
    DeCuongChiTiet("De cuong chi tiet"),
    DeThiGiuaKy("De thi giua ky"),
    DeThiCuoiKy("De thi cuoi ky");

    private final String tenHienThi;

    LoaiTaiLieu(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
