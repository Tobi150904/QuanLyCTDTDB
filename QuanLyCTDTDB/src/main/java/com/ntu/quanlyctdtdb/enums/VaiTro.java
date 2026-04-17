package com.ntu.quanlyctdtdb.enums;

public enum VaiTro {
    SV("Sinh vien"),
    GV("Giang vien"),
    CVHT("Co van hoc tap"),
    BCN("Ban chu nhiem"),
    CNHP("Chu nhiem hoc phan"),
    PDT("Phong dao tao"),
    TTDTXS("Truong trung tam dao tao xuat sac"),
    DN("Doanh nghiep");

    private final String tenHienThi;

    VaiTro(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
