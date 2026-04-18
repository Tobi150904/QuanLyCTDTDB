package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.ChucDanhBCN;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BcnThanhVienId implements Serializable {

    @Column(name = "MaCTDT", length = 20)
    private String maCTDT;

    @Column(name = "MaGV", length = 20)
    private String maGV;

    @Enumerated(EnumType.STRING)
    @Column(name = "ChucDanh", length = 20)
    private ChucDanhBCN chucDanh;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BcnThanhVienId that)) return false;
        return Objects.equals(maCTDT, that.maCTDT) &&
               Objects.equals(maGV, that.maGV) &&
               chucDanh == that.chucDanh;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maCTDT, maGV, chucDanh);
    }
}
