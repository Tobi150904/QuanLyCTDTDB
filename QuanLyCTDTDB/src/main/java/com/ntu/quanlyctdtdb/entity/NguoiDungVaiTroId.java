package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

import com.ntu.quanlyctdtdb.enums.VaiTro;

/**
 * Composite PK cho NguoiDungVaiTro: (MaNguoiDung, VaiTro)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class NguoiDungVaiTroId implements Serializable {

    @Column(name = "MaNguoiDung", length = 10, nullable = false)
    private String maNguoiDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "VaiTro", length = 10, nullable = false)
    private VaiTro vaiTro;
}
