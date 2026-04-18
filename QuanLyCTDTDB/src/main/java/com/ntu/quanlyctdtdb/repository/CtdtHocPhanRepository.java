package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.CtdtHocPhan;
import com.ntu.quanlyctdtdb.entity.CtdtHocPhanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CtdtHocPhanRepository extends JpaRepository<CtdtHocPhan, CtdtHocPhanId> {
    List<CtdtHocPhan> findById_MaCTDT(String maCTDT);
    List<CtdtHocPhan> findById_MaCTDTAndHocKyThu(String maCTDT, Integer hocKyThu);
    List<CtdtHocPhan> findById_MaCTDTAndBatBuoc(String maCTDT, Boolean batBuoc);
}
