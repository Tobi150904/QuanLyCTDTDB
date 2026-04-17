package com.ntu.quanlyctdtdb.enums;

public enum TrangThaiPhanCong {
    DaPhanCong("Da phan cong"),
    DangThucTap("Dang thuc tap"),
    DaKetThuc("Da ket thuc"),
    DaHuy("Da huy");

    private final String tenHienThi;

    TrangThaiPhanCong(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
