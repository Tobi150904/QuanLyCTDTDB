package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.VaiTro;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class NhomNguoiDungId implements Serializable {

    @Column(name = "MaNguoiDung", length = 20)
    private String maNguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "VaiTro", length = 20)
    private VaiTro vaiTro;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NhomNguoiDungId that)) return false;
        return Objects.equals(maNguoiDung, that.maNguoiDung) && vaiTro == that.vaiTro;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNguoiDung, vaiTro);
    }
}
