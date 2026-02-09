package com.zhongjia.web.controller;

import com.zhongjia.web.config.JwtProperties;
import com.zhongjia.web.security.JwtUtil;
import com.zhongjia.web.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import java.time.Duration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "JWT + Redis 示例")
public class AuthController {

    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public AuthController(JwtProperties jwtProperties, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.jwtProperties = jwtProperties;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @PostMapping("/login")
    @Operation(summary = "登录", description = "创建 JWT 并写入 Redis")
    public Result<String> login(@RequestParam @NotBlank String username) {
        String token = jwtUtil.generateToken(username, jwtProperties.getSecret(), jwtProperties.getExpireSeconds());
        String key = "jwt:token:" + username;
        stringRedisTemplate.opsForValue()
                .set(key, token, Duration.ofSeconds(jwtProperties.getExpireSeconds()));
        return Result.success(token);
    }
}
