package com.ntu.quanlyctdtdb.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.entity.NguoiDungVaiTro;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Wrapper cho NguoiDung de Spring Security su dung.
 * Luu them reference toi NguoiDung goc de truy cap trong Controller/Service.
 */
public class CustomUserDetails implements UserDetails {

    private final NguoiDung nguoiDung;
    private final List<GrantedAuthority> authorities;

    public CustomUserDetails(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
        // Map VaiTro enum sang "ROLE_PDT", "ROLE_GV", ... (Spring Security convention)
        this.authorities = nguoiDung.getVaiTros().stream()
                .map(NguoiDungVaiTro::getId)
                .map(id -> new SimpleGrantedAuthority("ROLE_" + id.getVaiTro().name()))
                .collect(Collectors.toList());
    }

    /**
     * Lay thong tin NguoiDung goc (dung trong Controller de lay MaNguoiDung, HoTen,...)
     * Cach dung trong Controller:
     *   @AuthenticationPrincipal CustomUserDetails userDetails
     *   userDetails.getNguoiDung().getMaNguoiDung()
     */
    public NguoiDung getNguoiDung() {
        return nguoiDung;
    }

    public String getMaNguoiDung() {
        return nguoiDung.getMaNguoiDung();
    }

    public String getHoTen() {
        return nguoiDung.getHoTen();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return nguoiDung.getMatKhauHash();
    }

    @Override
    public String getUsername() {
        return nguoiDung.getTenDangNhap();
    }

    // TrangThaiTK = true (1) = Hoat dong = account enabled
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return Boolean.TRUE.equals(nguoiDung.getTrangThaiTK());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(nguoiDung.getTrangThaiTK());
    }
}
