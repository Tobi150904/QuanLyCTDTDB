package com.ntu.quanlyctdtdb.config;

import com.ntu.quanlyctdtdb.security.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * BAT BUOC khi dung {@code sessionManagement().maximumSessions(...)}.
     * Spring Security dung {@link org.springframework.security.core.session.SessionRegistry}
     * de theo doi session active cua moi user. Neu khong co publisher nay,
     * registry khong biet khi session bi invalidate (logout, timeout) - hau
     * qua: sau khi logout, registry van giu session cu -> user LOGIN LAI bi
     * tu choi boi concurrent-session logic (session cu "tranh cho" voi session
     * moi). Dang ky bean nay de {@code HttpSessionListener} bubble su kien den
     * registry -> dam bao logout -> login lien tuc hoat dong binh thuong.
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * Dang ky UserDetailsService + PasswordEncoder voi Spring Security 6 (Boot 3.5)
     * thong qua AuthenticationManagerBuilder.
     * @Primary de Spring biet dung bean nay thay vi AuthenticationManager auto-config
     * cua Spring Boot (tranh NoUniqueBeanDefinitionException).
     */
    @Bean
    @Primary
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService)
               .passwordEncoder(passwordEncoder());
        return builder.build();
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/login?error");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // ============================================================
        // CSRF token repository: COOKIE thay vi SESSION.
        //
        // Ly do: khi logout (session bi invalidate), user duoc redirect sang
        // /login?logout. Thymeleaf render login.html co khoi <style> inline
        // ~360 dong -> response buffer 8KB cua Tomcat auto-commit TRUOC khi
        // <form th:action> duoc xu ly. Luc SpringActionTagProcessor chay, nó
        // goi CsrfRequestDataValueProcessor -> HttpSessionCsrfTokenRepository
        // .saveToken() -> request.getSession(true). Vi session cu da invalid
        // va response da commit -> "Cannot create a session after the response
        // has been committed" -> template crash, user ket luu ly.
        //
        // Dung CookieCsrfTokenRepository: token luu trong cookie XSRF-TOKEN,
        // khong can session -> render login page xa may dong cung an toan.
        // withHttpOnlyFalse() de tuong thich voi frontend framework/JS neu
        // sau nay co SPA call - khong anh huong form-hidden input hien tai.
        // CsrfTokenRequestAttributeHandler (thay cho XorCsrfTokenRequestAttributeHandler
        // mac dinh): giu _csrf.token raw -> phu hop voi cach
        // template render "<input type='hidden' th:value='${_csrf.token}'>".
        // ============================================================
        CookieCsrfTokenRepository csrfRepo = CookieCsrfTokenRepository.withHttpOnlyFalse();
        CsrfTokenRequestAttributeHandler csrfHandler = new CsrfTokenRequestAttributeHandler();
        csrfHandler.setCsrfRequestAttributeName(null);

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfRepo)
                .csrfTokenRequestHandler(csrfHandler)
            )
            .authorizeHttpRequests(auth -> auth
                // Public resources
                .requestMatchers("/login", "/403", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                // Favicon + robots: browser auto-fetch, khong can auth.
                // Neu khong permit, Spring Security redirect -> /login -> browser
                // retry -> infinite log spam. Them o day de dep.
                .requestMatchers("/favicon.ico", "/robots.txt", "/apple-touch-icon*.png")
                    .permitAll()
                // Uploads: chi authenticated moi xem
                .requestMatchers("/uploads/**").authenticated()

                // ============================================================
                // URL-level authorization — OUTER GATE.
                // Nguyen tac (bam sat docs/03_WORKFLOW.md §"SO DO TONG HOP QUYEN"):
                //   (1) O tang URL, CHI kiem tra "role nao duoc vao khu vuc nay".
                //       Tat ca role co it nhat R (read) deu duoc di qua day.
                //   (2) Phan biet R vs RW (ai sua, ai duyet) chuyen ve lop Controller
                //       bang @PreAuthorize method-level — tranh bung no requestMatchers
                //       va khoi phai split GET/POST o day.
                //   (3) Admin luon hien o moi ruleset (super-user).
                // Tham khao them: 00_MASTER_REFERENCE.md §4 (8 roles) va
                //                 08_REVIEW_REPORT.md P0-1 (Phase 5 templates chua co).
                // ============================================================

                // Nguoi Dung: PDT(RW), TTDTXS(R), ADMIN(RW)
                //   Truoc day TTDTXS bi block toan bo -> khong xem duoc user list de
                //   lam bao cao. Nay cho duyet tra cuu, chan writes o Controller.
                .requestMatchers("/nguoi-dung/**")
                    .hasAnyRole("PDT", "TTDTXS", "ADMIN")

                // Quan ly Doanh Nghiep / Hoc Ky / Lop Hanh Chinh: PDT, TTDTXS, ADMIN (RW)
                .requestMatchers("/doanh-nghiep/**").hasAnyRole("PDT", "TTDTXS", "ADMIN")
                .requestMatchers("/hoc-ky/**").hasAnyRole("PDT", "TTDTXS", "ADMIN")
                .requestMatchers("/lop-hanh-chinh/**").hasAnyRole("PDT", "TTDTXS", "ADMIN")

                // Hoc Phan: CNHP(RW), TTDTXS(W duyet/tu choi), PDT(R), ADMIN, GV(R), SV(R).
                //   SV can xem HP de tra cuu mon hoc, GV can xem HP minh day,
                //   PDT theo doi tien do tao HP. Writes chan bang @PreAuthorize
                //   o HocPhanController (class + method).
                .requestMatchers("/hoc-phan/**")
                    .hasAnyRole("PDT", "TTDTXS", "CNHP", "ADMIN",
                                "GIANG_VIEN", "SINH_VIEN")

                // CTDT: PDT(R), TTDTXS(RW), CNHP/BCN(RW), GV(R), SV(R), ADMIN.
                //   Docs matrix cho PDT=R, TTDTXS=RW, CNHP=RW. Mo rong cho GV/SV
                //   xem khung CTDT minh hoc / day.
                .requestMatchers("/ctdt/**")
                    .hasAnyRole("PDT", "TTDTXS", "CNHP", "ADMIN",
                                "GIANG_VIEN", "SINH_VIEN")

                // Lop Hoc Phan: PDT(RW), TTDTXS(RW), CNHP(RW), GV(R lop minh day),
                //   SV(R lop minh hoc), ADMIN.
                //   SV truoc day bi chan — nay mo de xem thoi khoa bieu.
                .requestMatchers("/lop-hoc-phan/**")
                    .hasAnyRole("PDT", "TTDTXS", "CNHP", "ADMIN",
                                "GIANG_VIEN", "SINH_VIEN")

                // Danh Gia / Canh Bao [Phase 4]:
                //   GV   : RW cho lop minh day (nhap nhan xet, danh dau canh bao).
                //   CVHT : RW xu ly canh bao cua lop hanh chinh minh phu trach.
                //   SV   : R  xem nhan xet/canh bao ve minh.
                //   PDT  : R  view tat ca canh bao toan truong (giam sat hoc vu).
                //   ADMIN: RW (super-user).
                //   Writes (POST nhan-xet/xu-ly) chan them o Controller @PreAuthorize.
                .requestMatchers("/danh-gia/**")
                    .hasAnyRole("GIANG_VIEN", "CVHT", "SINH_VIEN", "PDT", "ADMIN")

                // Kien Tap: TTDTXS(RW), CNHP(RW), GV(W nhan-xet-gv), DN(W nhan-xet-dn),
                //   SV(R dot minh tham gia), PDT(R theo doi), ADMIN.
                .requestMatchers("/kien-tap/**")
                    .hasAnyRole("PDT", "TTDTXS", "CNHP", "ADMIN",
                                "GIANG_VIEN", "DOANH_NGHIEP", "SINH_VIEN")

                // Thuc Tap: PDT(RW), TTDTXS(RW), GV(W ket-qua), CVHT(W ket-qua),
                //   DN(W ket-qua), SV(R "cua-toi" + xem diem), ADMIN.
                .requestMatchers("/thuc-tap/**")
                    .hasAnyRole("PDT", "TTDTXS", "ADMIN", "GIANG_VIEN", "CVHT",
                                "DOANH_NGHIEP", "SINH_VIEN")

                // Bao Cao: PDT/TTDTXS/CNHP/ADMIN (R)
                //   CNHP xuat bao cao lop HP minh quan ly -> add CNHP.
                .requestMatchers("/bao-cao/**")
                    .hasAnyRole("PDT", "TTDTXS", "CNHP", "ADMIN")

                // Profile
                .requestMatchers("/profile/**").authenticated()

                // Dashboard - moi logged-in user deu xem duoc
                .requestMatchers("/dashboard", "/").authenticated()

                // Tat ca con lai phai authenticated
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                // Xoa ca JSESSIONID (session-based auth) va XSRF-TOKEN (csrf
                // cookie). Khong xoa XSRF-TOKEN thi cookie cu van con, lan
                // login sau se dung token cu -> FE framework co the mismatch.
                .deleteCookies("JSESSIONID", "XSRF-TOKEN")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/403")
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired")
            );

        return http.build();
    }
}
