package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LopHocPhanId implements Serializable {

    @Column(name = "MaCTDT", length = 20)
    private String maCTDT;

    @Column(name = "MaHocPhan", length = 20)
    private String maHocPhan;

    @Column(name = "MaHocKy", length = 20)
    private String maHocKy;

    @Column(name = "MaLopHocPhan")
    private Integer maLopHocPhan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LopHocPhanId that)) return false;
        return Objects.equals(maCTDT, that.maCTDT) &&
               Objects.equals(maHocPhan, that.maHocPhan) &&
               Objects.equals(maHocKy, that.maHocKy) &&
               Objects.equals(maLopHocPhan, that.maLopHocPhan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTDT, maHocPhan, maHocKy, maLopHocPhan);
    }
}
