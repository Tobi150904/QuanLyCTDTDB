package com.ntu.quanlyctdtdb.service;

import com.ntu.quanlyctdtdb.dto.HocPhanDTO;
import com.ntu.quanlyctdtdb.entity.HocPhan;

import java.util.List;

public interface HocPhanService {

    List<HocPhan> findAll(String keyword);

    HocPhan findById(String ma);

    /** CNHP de nghi tao moi hoc phan → trang thai ChuaDuyet */
    HocPhan create(HocPhanDTO dto, String maNguoiDungTao);

    /** CNHP chinh sua truoc khi gui duyet */
    HocPhan update(String ma, HocPhanDTO dto);

    /** TTDTXS phe duyet → trang thai DaDuyet + gui email CNHP */
    HocPhan pheduyet(String ma, String maNguoiDungDuyet);

    /** TTDTXS tu choi + ly do */
    HocPhan tuChoi(String ma, String lyDo, String maNguoiDungTuChoi);

    /** Tat/bat hieu luc */
    HocPhan toggleTrangThai(String ma);

    /** Upload file de cuong va luu ten file vao HP */
    HocPhan uploadDeCuong(String ma, String tenFile);
}
