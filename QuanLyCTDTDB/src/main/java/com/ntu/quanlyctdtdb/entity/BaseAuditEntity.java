package com.ntu.quanlyctdtdb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Base class cho tat ca entity co created_at / updated_at.
 * Cac entity extend class nay de tu dong co 2 truong audit.
 *
 * Su dung @MappedSuperclass thay vi @EntityListeners de tranh phu thuoc
 * vao Spring Data JPA Auditing (don gian hon, phu hop voi MySQL XAMPP).
 */
@MappedSuperclass
@Getter
@Setter
public abstract class BaseAuditEntity {

    @Column(name = "created_at", nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at",
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
