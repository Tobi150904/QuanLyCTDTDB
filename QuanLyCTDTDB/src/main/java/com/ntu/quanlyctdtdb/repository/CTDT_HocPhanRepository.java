package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.CTDT_HocPhan;
import com.ntu.quanlyctdtdb.entity.CTDT_HocPhanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CTDT_HocPhanRepository extends JpaRepository<CTDT_HocPhan, CTDT_HocPhanId> {
    List<CTDT_HocPhan> findById_MaCTDT(String maCTDT);
    void deleteById_MaCTDT(String maCTDT);
}