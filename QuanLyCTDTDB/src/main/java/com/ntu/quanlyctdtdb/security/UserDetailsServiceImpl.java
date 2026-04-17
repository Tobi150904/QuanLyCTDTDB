package com.ntu.quanlyctdtdb.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.repository.NguoiDungRepository;

/**
 * Implementation cua UserDetailsService cho Spring Security.
 * Tim nguoi dung theo TenDangNhap.
 * Dung @Transactional de load LAZY collection VaiTros trong cung session.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    public UserDetailsServiceImpl(NguoiDungRepository nguoiDungRepository) {
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String tenDangNhap)
            throws UsernameNotFoundException {

        NguoiDung nguoiDung = nguoiDungRepository
                .findByTenDangNhap(tenDangNhap)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Khong tim thay nguoi dung voi ten dang nhap: " + tenDangNhap));

        // Trigger load LAZY vaiTros collection trong cung @Transactional session
        nguoiDung.getVaiTros().size();

        if (!Boolean.TRUE.equals(nguoiDung.getTrangThaiTK())) {
            throw new UsernameNotFoundException(
                    "Tai khoan da bi khoa: " + tenDangNhap);
        }

        return new CustomUserDetails(nguoiDung);
    }
}
