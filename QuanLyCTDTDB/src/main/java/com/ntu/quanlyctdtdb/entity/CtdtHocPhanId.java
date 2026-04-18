package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CtdtHocPhanId implements Serializable {

    @Column(name = "MaCTDT", length = 20)
    private String maCTDT;

    @Column(name = "MaHocPhan", length = 20)
    private String maHocPhan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CtdtHocPhanId that)) return false;
        return Objects.equals(maCTDT, that.maCTDT) && Objects.equals(maHocPhan, that.maHocPhan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTDT, maHocPhan);
    }
}
