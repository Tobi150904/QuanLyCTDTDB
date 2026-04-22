package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.CtdtHocPhan;
import com.ntu.quanlyctdtdb.entity.CtdtHocPhanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CtdtHocPhanRepository extends JpaRepository<CtdtHocPhan, CtdtHocPhanId> {
    List<CtdtHocPhan> findById_MaCTDT(String maCTDT);
    List<CtdtHocPhan> findById_MaCTDTAndHocKyThu(String maCTDT, Integer hocKyThu);
    List<CtdtHocPhan> findById_MaCTDTAndBatBuoc(String maCTDT, Boolean batBuoc);

    /**
     * Fetch version JOIN FETCH {@code hocPhan} — dung cho trang
     * "Ke Hoach Mo Lop" (template truy cap {@code ch.hocPhan.tenHocPhan}
     * khi open-in-view=false).
     */
    @Query("""
        SELECT ch FROM CtdtHocPhan ch
        LEFT JOIN FETCH ch.hocPhan
        WHERE ch.id.maCTDT = :maCTDT AND ch.hocKyThu = :hocKyThu
        ORDER BY ch.batBuoc DESC, ch.id.maHocPhan
        """)
    List<CtdtHocPhan> findByCtdtAndKyFetch(String maCTDT, Integer hocKyThu);
}
