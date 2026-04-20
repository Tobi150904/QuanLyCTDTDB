package com.ntu.quanlyctdtdb.repository;

import com.ntu.quanlyctdtdb.entity.NhomNguoiDung;
import com.ntu.quanlyctdtdb.entity.NhomNguoiDungId;
import com.ntu.quanlyctdtdb.enums.VaiTro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhomNguoiDungRepository extends JpaRepository<NhomNguoiDung, NhomNguoiDungId> {

    /** Tim tat ca vai tro cua 1 nguoi dung (nguoiDung la field thuc tren entity) */
    List<NhomNguoiDung> findByNguoiDung_MaNguoiDung(String maNguoiDung);

    /**
     * VaiTro nam TRONG @EmbeddedId (NhomNguoiDungId.vaiTro) -> phai dung @Query
     * vi Spring Data JPA khong parse duoc derived query cho property trong composite key.
     */
    @Query("SELECT n FROM NhomNguoiDung n WHERE n.id.vaiTro = :vaiTro")
    List<NhomNguoiDung> findByVaiTro(@Param("vaiTro") VaiTro vaiTro);

    @Query("SELECT COUNT(n) > 0 FROM NhomNguoiDung n "
         + "WHERE n.nguoiDung.maNguoiDung = :maNguoiDung AND n.id.vaiTro = :vaiTro")
    boolean existsByNguoiDung_MaNguoiDungAndVaiTro(
            @Param("maNguoiDung") String maNguoiDung,
            @Param("vaiTro") VaiTro vaiTro);

    void deleteByNguoiDung_MaNguoiDung(String maNguoiDung);
}
