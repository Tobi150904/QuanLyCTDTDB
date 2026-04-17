package com.ntu.quanlyctdtdb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.ntu.quanlyctdtdb.security.CustomAuthenticationProvider;
import com.ntu.quanlyctdtdb.security.UserDetailsServiceImpl;

/**
 * Spring Security Config
 * 8 roles: SV, GV, CVHT, BCN, CNHP, PDT, TTDTXS, DN
 * Khong dung @EnableGlobalMethodSecurity (cu) - dung @EnableMethodSecurity (Spring Security 6+)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final CustomAuthenticationProvider customAuthenticationProvider;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, CustomAuthenticationProvider customAuthenticationProvider) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationProvider = customAuthenticationProvider;
    }

    // ---- Beans ----

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return customAuthenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Custom success handler: redirect theo role sau khi dang nhap thanh cong.
     * Thu tu uu tien: PDT > TTDTXS > BCN > CNHP > GV > CVHT > DN > SV
     */
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            String redirectUrl = "/dashboard";

            boolean hasPDT      = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_PDT"));
            boolean hasTTDTXS   = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_TTDTXS"));
            boolean hasBCN      = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_BCN"));
            boolean hasCNHP     = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CNHP"));
            boolean hasGV       = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_GV"));
            boolean hasCVHT     = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_CVHT"));
            boolean hasDN       = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_DN"));

            // Tat ca cac role deu ve /dashboard, Controller se xu ly hien thi khac nhau
            // Giu logic don gian, khong redirect den tung URL rieng theo role
            response.sendRedirect(request.getContextPath() + redirectUrl);
        };
    }

    // ---- Security Filter Chain ----

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // Static resources: public
                .requestMatchers(
                    "/css/**", "/js/**", "/images/**",
                    "/webjars/**", "/favicon.ico"
                ).permitAll()
                // Auth pages: public
                .requestMatchers("/login", "/error").permitAll()

                // ---- PDT: quan ly nguoi dung, hoc ky, bao cao ----
                .requestMatchers("/nguoi-dung/**").hasAnyRole("PDT", "TTDTXS")
                .requestMatchers("/hoc-ky/**").hasRole("PDT")

                // ---- BCN + PDT + TTDTXS: CTDT ----
                .requestMatchers("/ctdt/**").hasAnyRole("BCN", "PDT", "TTDTXS")

                // ---- BCN + CNHP + PDT: Hoc phan ----
                .requestMatchers("/hoc-phan/**").hasAnyRole("BCN", "CNHP", "PDT")

                // ---- BCN + GV + CNHP: Lop Hoc Phan ----
                .requestMatchers("/lop-hoc-phan/**").hasAnyRole("BCN", "CNHP", "GV", "PDT")

                // ---- GV + CNHP + PDT: Tai lieu ----
                .requestMatchers("/tai-lieu/**").hasAnyRole("GV", "CNHP", "PDT")

                // ---- GV + CVHT + PDT: Danh gia ----
                .requestMatchers("/danh-gia/**").hasAnyRole("GV", "CVHT", "PDT")

                // ---- CVHT + PDT: Xu ly canh bao ----
                .requestMatchers("/canh-bao/**").hasAnyRole("CVHT", "PDT")

                // ---- BCN + GV + TTDTXS + PDT + DN: Kien tap ----
                .requestMatchers("/kien-tap/**")
                    .hasAnyRole("BCN", "GV", "TTDTXS", "PDT", "DN")

                // ---- BCN + GV + TTDTXS + PDT + SV + DN: Thuc tap ----
                .requestMatchers("/thuc-tap/**")
                    .hasAnyRole("BCN", "GV", "TTDTXS", "PDT", "SV", "DN")

                // ---- Bao cao: PDT + TTDTXS + BCN ----
                .requestMatchers("/bao-cao/**")
                    .hasAnyRole("PDT", "TTDTXS", "BCN")

                // Dashboard: tat ca nguoi dung da dang nhap
                .requestMatchers("/dashboard").authenticated()

                // Con lai: phai dang nhap
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler(customSuccessHandler())
                .failureUrl("/login?error=true")
                .usernameParameter("tenDangNhap")
                .passwordParameter("matKhau")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/login?expired=true")
            );

        return http.build();
    }
}
