package com.zhongjia.web.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "QZ Agent API",
                version = "1.0.0",
                description = "QZ Agent Spring Boot Starter 示例接口"
        )
)
public class SwaggerConfig {
}
