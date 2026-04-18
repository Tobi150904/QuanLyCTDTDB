package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.VaiTroThucTap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VaiTroThucTapRepository extends JpaRepository<VaiTroThucTap, String> {
}
