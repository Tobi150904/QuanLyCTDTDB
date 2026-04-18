package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.BcnThanhVien;
import com.ntu.quanlyctdtdb.entity.BcnThanhVienId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BcnThanhVienRepository extends JpaRepository<BcnThanhVien, BcnThanhVienId> {
    List<BcnThanhVien> findByChuongTrinhDaoTao_MaCTDT(String maCTDT);
    List<BcnThanhVien> findByGiangVien_MaGV(String maGV);
}
