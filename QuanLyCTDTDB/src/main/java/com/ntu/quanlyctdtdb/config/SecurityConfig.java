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
        http
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

                // Quan ly Nguoi Dung
                .requestMatchers("/nguoi-dung/**").hasAnyRole("PDT", "ADMIN")

                // Quan ly Doanh Nghiep, Hoc Ky, Lop Hanh Chinh
                .requestMatchers("/doanh-nghiep/**").hasAnyRole("PDT", "TTDTXS", "ADMIN")
                .requestMatchers("/hoc-ky/**").hasAnyRole("PDT", "TTDTXS", "ADMIN")
                .requestMatchers("/lop-hanh-chinh/**").hasAnyRole("PDT", "TTDTXS", "ADMIN")

                // Quan ly Hoc Phan
                .requestMatchers("/hoc-phan/**").hasAnyRole("CNHP", "TTDTXS", "ADMIN")

                // CTDT
                .requestMatchers("/ctdt/**").hasAnyRole("PDT", "TTDTXS", "CNHP", "ADMIN")

                // Lop Hoc Phan
                .requestMatchers("/lop-hoc-phan/**")
                    .hasAnyRole("PDT", "TTDTXS", "CNHP", "ADMIN", "GIANG_VIEN")

                // Danh Gia / Canh Bao
                .requestMatchers("/danh-gia/**")
                    .hasAnyRole("GIANG_VIEN", "CVHT", "ADMIN")

                // Kien Tap
                .requestMatchers("/kien-tap/**")
                    .hasAnyRole("TTDTXS", "CNHP", "ADMIN", "GIANG_VIEN", "DOANH_NGHIEP")

                // Thuc Tap
                .requestMatchers("/thuc-tap/**")
                    .hasAnyRole("PDT", "TTDTXS", "ADMIN", "GIANG_VIEN", "CVHT",
                                "DOANH_NGHIEP", "SINH_VIEN")

                // Bao Cao
                .requestMatchers("/bao-cao/**").hasAnyRole("PDT", "TTDTXS", "ADMIN")

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
                .deleteCookies("JSESSIONID")
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
