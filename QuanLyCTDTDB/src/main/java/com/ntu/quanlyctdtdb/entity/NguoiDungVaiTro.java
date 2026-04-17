package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Bang: NguoiDung_VaiTro
 * PK (composite): (MaNguoiDung, VaiTro) -> @EmbeddedId
 */
@Entity
@Table(name = "NguoiDung_VaiTro")
@Getter
@Setter
@NoArgsConstructor
public class NguoiDungVaiTro {

    @EmbeddedId
    private NguoiDungVaiTroId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maNguoiDung")
    @JoinColumn(name = "MaNguoiDung", referencedColumnName = "MaNguoiDung")
    private NguoiDung nguoiDung;

    @Column(name = "NgayGan", nullable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime ngayGan = LocalDateTime.now();

    // Constructor tien ich
    public NguoiDungVaiTro(NguoiDung nguoiDung,
                            com.ntu.quanlyctdtdb.enums.VaiTro vaiTro) {
        this.id = new NguoiDungVaiTroId(nguoiDung.getMaNguoiDung(), vaiTro);
        this.nguoiDung = nguoiDung;
        this.ngayGan = LocalDateTime.now();
    }
}
