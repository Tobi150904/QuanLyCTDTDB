package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode
public class LopHocPhanId implements Serializable {

    @Column(name = "MaHocPhan", length = 20, nullable = false)
    private String maHocPhan;

    @Column(name = "MaHocKy", length = 20, nullable = false)
    private String maHocKy;

    @Column(name = "MaLopHC", length = 20, nullable = false)
    private String maLopHC;
}