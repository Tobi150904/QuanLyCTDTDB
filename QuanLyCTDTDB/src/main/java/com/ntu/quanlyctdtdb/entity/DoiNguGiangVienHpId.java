package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DoiNguGiangVienHpId implements Serializable {

    @Column(name = "MaHocPhan", length = 20)
    private String maHocPhan;

    @Column(name = "MaGiangVien", length = 20)
    private String maGiangVien;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoiNguGiangVienHpId that)) return false;
        return Objects.equals(maHocPhan, that.maHocPhan) && Objects.equals(maGiangVien, that.maGiangVien);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHocPhan, maGiangVien);
    }
}
