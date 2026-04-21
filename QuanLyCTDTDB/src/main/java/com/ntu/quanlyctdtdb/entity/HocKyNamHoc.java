package com.ntu.quanlyctdtdb.entity;

import com.ntu.quanlyctdtdb.enums.TrangThaiHocKy;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * HocKyNamHoc — bang doc lap, khop 1-1 voi DDL trong
 * {@code scripts/01_create_tables.sql} va ERD {@code docs/01_ERD_SCHEMA.md}.
 *
 * <p>PK {@code MaHocKy} dung format {@code HKn-YYYY} (n in [1,3], YYYY la
 * nam bat dau nam hoc). Nhu vay {@code HocKyThu}, {@code NamBatDau},
 * {@code NamKetThuc} khong phai la cot rieng ma la gia tri <b>suy ra</b>
 * tu {@code MaHocKy} — duoc expose qua cac getter {@code @Transient} ben
 * duoi de su dung trong template Thymeleaf va controller.
 */
@Entity
@Table(name = "HocKyNamHoc")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HocKyNamHoc {

    @Id
    @Column(name = "MaHocKy", length = 20)
    private String maHocKy;

    @Column(name = "TenHocKy", nullable = false, length = 50)
    private String tenHocKy;

    @Column(name = "NgayBatDau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "NgayKetThuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Enumerated(EnumType.STRING)
    @Column(name = "TrangThai", length = 20)
    private TrangThaiHocKy trangThai = TrangThaiHocKy.SapDienRa;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* ===================== Derived fields ===================== */

    /**
     * Tra ve so hoc ky (1..3) parse tu {@code MaHocKy} (format {@code HKn-YYYY}).
     * @return so hoc ky hoac 0 neu {@code maHocKy} khong hop le.
     */
    @Transient
    public int getHocKyThu() {
        if (maHocKy == null || maHocKy.length() < 3 || !maHocKy.startsWith("HK")) {
            return 0;
        }
        char c = maHocKy.charAt(2);
        return Character.isDigit(c) ? Character.getNumericValue(c) : 0;
    }

    /**
     * Tra ve nam bat dau nam hoc parse tu {@code MaHocKy}.
     * @return nam (YYYY) hoac 0 neu {@code maHocKy} khong hop le.
     */
    @Transient
    public int getNamBatDau() {
        if (maHocKy == null) return 0;
        int dash = maHocKy.indexOf('-');
        if (dash < 0 || dash + 1 >= maHocKy.length()) return 0;
        try {
            return Integer.parseInt(maHocKy.substring(dash + 1));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Tra ve nam ket thuc nam hoc (= NamBatDau + 1 theo quy uoc).
     * Tra ve 0 neu {@code maHocKy} khong hop le.
     */
    @Transient
    public int getNamKetThuc() {
        int nam = getNamBatDau();
        return nam > 0 ? nam + 1 : 0;
    }
}
