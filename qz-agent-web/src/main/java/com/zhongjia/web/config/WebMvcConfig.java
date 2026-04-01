package com.zhongjia.web.config;

import com.zhongjia.web.security.JwtAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;

    public WebMvcConfig(JwtAuthInterceptor jwtAuthInterceptor) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/hello",
                        "/api/auth/login",
                        "/api/test/**",
                        "/api/b2b/qz/hp/lab-appointment",
                        "/api/b2b/qz/hp/report",
                        "/api/b2b/qz/hp/prescription",
                        "/api/b2b/sdhp/patient/adt-a01-mz",
                        "/api/b2b/sdhp/check/orm-o01",
                        "/api/b2b/sdhp/check/reservation/siu-s12",
                        "/api/b2b/sdhp/check/status",
                        "/api/b2b/sdhp/check/report",
                        "/api/b2b/sdhp/lab/oml-o21",
                        "/api/b2b/sdhp/lab/status",
                        "/api/b2b/sdhp/lab/report",
                        "/api/b2b/sdhp/order/omp-o09",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/**"
                );
    }
}
