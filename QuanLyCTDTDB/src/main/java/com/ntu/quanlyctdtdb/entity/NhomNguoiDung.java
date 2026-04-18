package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "NhomNguoiDung")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NhomNguoiDung {

    @EmbeddedId
    private NhomNguoiDungId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("maNguoiDung")
    @JoinColumn(name = "MaNguoiDung")
    private NguoiDung nguoiDung;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
