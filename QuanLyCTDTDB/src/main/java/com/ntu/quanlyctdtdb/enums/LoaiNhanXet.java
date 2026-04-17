package com.ntu.quanlyctdtdb.enums;

public enum LoaiNhanXet {
    TichCuc("Tich cuc"),
    TieuCuc("Tieu cuc");

    private final String tenHienThi;

    LoaiNhanXet(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
