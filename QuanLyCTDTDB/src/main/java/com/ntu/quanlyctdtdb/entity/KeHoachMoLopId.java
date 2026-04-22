package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite PK cho {@link KeHoachMoLop}.
 * Khoa: (MaHocPhan, MaHocKy) — 1 hoc phan mo bao nhieu lop trong 1 hoc ky cu the.
 */
@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class KeHoachMoLopId implements Serializable {

    @Column(name = "MaHocPhan", length = 20)
    private String maHocPhan;

    @Column(name = "MaHocKy", length = 20)
    private String maHocKy;
}
