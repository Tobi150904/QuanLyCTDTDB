package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DanhSachSvKienTapId implements Serializable {

    @Column(name = "MaDotKT")
    private Integer maDotKT;

    @Column(name = "MaSV", length = 20)
    private String maSV;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DanhSachSvKienTapId that)) return false;
        return Objects.equals(maDotKT, that.maDotKT) && Objects.equals(maSV, that.maSV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maDotKT, maSV);
    }
}
