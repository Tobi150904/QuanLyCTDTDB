package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHp;
import com.ntu.quanlyctdtdb.entity.DoiNguGiangVienHpId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoiNguGiangVienHpRepository extends JpaRepository<DoiNguGiangVienHp, DoiNguGiangVienHpId> {
    List<DoiNguGiangVienHp> findByHocPhan_MaHocPhan(String maHocPhan);
    List<DoiNguGiangVienHp> findByGiangVien_MaGVAndTrangThai(String maGV, Boolean trangThai);
}
