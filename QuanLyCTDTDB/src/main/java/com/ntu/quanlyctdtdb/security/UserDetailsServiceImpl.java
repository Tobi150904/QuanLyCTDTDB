package com.ntu.quanlyctdtdb.security;

import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.repository.NguoiDungRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final NguoiDungRepository nguoiDungRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tim theo TenDangNhap truoc, neu khong co thi tim theo Email
        NguoiDung nguoiDung = nguoiDungRepository.findByTenDangNhap(username)
                .orElseGet(() -> nguoiDungRepository.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException(
                                "Khong tim thay nguoi dung: " + username)));

        return new CustomUserDetails(nguoiDung);
    }
}
