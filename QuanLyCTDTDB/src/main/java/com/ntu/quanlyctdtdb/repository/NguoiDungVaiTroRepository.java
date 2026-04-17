package com.ntu.quanlyctdtdb.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ntu.quanlyctdtdb.entity.NguoiDungVaiTro;
import com.ntu.quanlyctdtdb.entity.NguoiDungVaiTroId;
import com.ntu.quanlyctdtdb.enums.VaiTro;

import java.util.List;

@Repository
public interface NguoiDungVaiTroRepository
        extends JpaRepository<NguoiDungVaiTro, NguoiDungVaiTroId> {

    /**
     * Lay danh sach vai tro cua nguoi dung
     */
    List<NguoiDungVaiTro> findByNguoiDung_MaNguoiDung(String maNguoiDung);

    /**
     * Kiem tra nguoi dung co vai tro cu the khong
     */
    boolean existsById_MaNguoiDungAndId_VaiTro(String maNguoiDung, VaiTro vaiTro);

    /**
     * Xoa tat ca vai tro cua nguoi dung
     */
    void deleteByNguoiDung_MaNguoiDung(String maNguoiDung);
}
