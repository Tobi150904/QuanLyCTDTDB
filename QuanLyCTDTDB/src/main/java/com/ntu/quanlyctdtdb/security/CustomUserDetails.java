package com.ntu.quanlyctdtdb.security;

import com.ntu.quanlyctdtdb.entity.NguoiDung;
import com.ntu.quanlyctdtdb.entity.NhomNguoiDung;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CustomUserDetails implements UserDetails {

    private final NguoiDung nguoiDung;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(NguoiDung nguoiDung) {
        this.nguoiDung = nguoiDung;
        this.authorities = buildAuthorities(nguoiDung);
    }

    private static Collection<? extends GrantedAuthority> buildAuthorities(NguoiDung nguoiDung) {
        Set<SimpleGrantedAuthority> auths = new HashSet<>();

        // Map LoaiNguoiDung -> ROLE_*
        switch (nguoiDung.getLoaiNguoiDung()) {
            case Admin       -> auths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            case GiangVien   -> auths.add(new SimpleGrantedAuthority("ROLE_GIANG_VIEN"));
            case SinhVien    -> auths.add(new SimpleGrantedAuthority("ROLE_SINH_VIEN"));
            case DoanhNghiep -> auths.add(new SimpleGrantedAuthority("ROLE_DOANH_NGHIEP"));
        }

        // Map VaiTro nghiep vu trong NhomNguoiDung -> ROLE_*
        if (nguoiDung.getNhomNguoiDungs() != null) {
            for (NhomNguoiDung nhom : nguoiDung.getNhomNguoiDungs()) {
                switch (nhom.getId().getVaiTro()) {
                    case PDT    -> auths.add(new SimpleGrantedAuthority("ROLE_PDT"));
                    case TTDTXS -> auths.add(new SimpleGrantedAuthority("ROLE_TTDTXS"));
                    case CVHT   -> auths.add(new SimpleGrantedAuthority("ROLE_CVHT"));
                    case CNHP   -> auths.add(new SimpleGrantedAuthority("ROLE_CNHP"));
                }
            }
        }
        return auths;
    }

    // ---- Accessor helpers ----
    public NguoiDung getNguoiDung() { return nguoiDung; }
    public String getMaNguoiDung()  { return nguoiDung.getMaNguoiDung(); }
    public String getHoTen()        { return nguoiDung.getHoTen(); }
    public String getEmail()        { return nguoiDung.getEmail(); }

    // ---- UserDetails ----
    @Override public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override public String getPassword() { return nguoiDung.getMatKhauHash(); }
    @Override public String getUsername() { return nguoiDung.getTenDangNhap(); }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() {
        return nguoiDung.getTrangThaiTK() != null && nguoiDung.getTrangThaiTK();
    }
}
