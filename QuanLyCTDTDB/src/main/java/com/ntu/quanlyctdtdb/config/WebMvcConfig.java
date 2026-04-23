package com.ntu.quanlyctdtdb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads/}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files via /uploads/** URL
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String resourceLocation = "file:" + uploadPath.toString() + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(resourceLocation);
    }

    /**
     * Register {@link MultipartFilter} CHAY TRUOC Spring Security filter.
     *
     * <p><b>Ly do:</b> cac form <code>enctype="multipart/form-data"</code>
     * (vd: {@code /hoc-phan/them|sua/{ma}}, {@code /ctdt/them|sua/{ma}} — upload
     * file de cuong + file Word) POST kem CSRF token duoi dang parameter
     * {@code _csrf}. Mac dinh Spring Boot 3 <b>KHONG</b> dat
     * {@code MultipartFilter} truoc {@code SpringSecurityFilterChain}, nen khi
     * request di qua:
     * <ol>
     *   <li>{@code CsrfFilter} goi {@code request.getParameter("_csrf")} nhung
     *       body multipart CHUA duoc parse → tra ve {@code null}.</li>
     *   <li>{@code CsrfFilter} tuong CSRF token bi thieu → tra ve
     *       <b>403 Forbidden</b>.</li>
     *   <li>User thay "khong tao/sua duoc HP, CTDT" — form bi chan am tham.</li>
     * </ol>
     *
     * <p>Register bean nay voi order {@link Ordered#HIGHEST_PRECEDENCE} de parse
     * body multipart SOM HON ca security filter (order mac dinh {@code -100}),
     * giup {@code CsrfFilter} doc duoc <code>_csrf</code> binh thuong. Tham khao:
     * <a href="https://docs.spring.io/spring-security/reference/servlet/integrations/servlet-api.html#servletapi-multipart">
     * Spring Security — Multipart Requests</a>.
     */
    @Bean
    public FilterRegistrationBean<MultipartFilter> multipartFilterRegistration() {
        FilterRegistrationBean<MultipartFilter> registration =
                new FilterRegistrationBean<>(new MultipartFilter());
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName("multipartFilter");
        return registration;
    }
}
