package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "VaiTroThucTap")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class VaiTroThucTap {

    @Id
    @Column(name = "MaVaiTro", length = 10)
    private String maVaiTro;

    @Column(name = "TenVaiTro", nullable = false, length = 100)
    private String tenVaiTro;

    @Column(name = "MoTa", length = 255)
    private String moTa;
}
