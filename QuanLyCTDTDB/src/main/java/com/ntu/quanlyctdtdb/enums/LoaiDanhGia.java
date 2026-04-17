package com.ntu.quanlyctdtdb.enums;

public enum LoaiDanhGia {
    QuaTrinh("Quá trình"),
    TongKetKy("Tổng kết kỳ");

    private final String tenHienThi;

    LoaiDanhGia(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}