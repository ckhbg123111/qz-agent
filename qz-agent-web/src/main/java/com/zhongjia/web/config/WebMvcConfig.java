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
                        "/api/b2b/single-disease/patient/adt-a01-mz",
                        "/api/b2b/single-disease/check/orm-o01",
                        "/api/b2b/single-disease/check/reservation/siu-s12",
                        "/api/b2b/single-disease/check/status",
                        "/api/b2b/single-disease/check/report",
                        "/api/b2b/single-disease/lab/oml-o21",
                        "/api/b2b/single-disease/lab/status",
                        "/api/b2b/single-disease/lab/report",
                        "/api/b2b/single-disease/order/omp-o09",
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/**"
                );
    }
}
