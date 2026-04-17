package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Composite PK cho DoiNguGiangVienHP: (MaHocPhan, MaGiangVien)
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DoiNguGiangVienHPId implements Serializable {

    @Column(name = "MaHocPhan", length = 10, nullable = false)
    private String maHocPhan;

    @Column(name = "MaGiangVien", length = 10, nullable = false)
    private String maGiangVien;
}
