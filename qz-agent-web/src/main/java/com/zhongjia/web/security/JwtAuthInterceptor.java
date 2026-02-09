package com.zhongjia.web.security;

import com.zhongjia.web.config.JwtProperties;
import com.zhongjia.web.exception.BizException;
import com.zhongjia.web.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtProperties jwtProperties;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    public JwtAuthInterceptor(JwtProperties jwtProperties, JwtUtil jwtUtil, StringRedisTemplate stringRedisTemplate) {
        this.jwtProperties = jwtProperties;
        this.jwtUtil = jwtUtil;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getCode(), "Missing token");
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = jwtUtil.parseToken(token, jwtProperties.getSecret());
        } catch (JwtException ex) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getCode(), "Invalid token");
        }

        String subject = claims.getSubject();
        String key = "jwt:token:" + subject;
        String cachedToken = stringRedisTemplate.opsForValue().get(key);
        if (!Objects.equals(token, cachedToken)) {
            throw new BizException(ErrorCode.PARAMS_ERROR.getCode(), "Token expired");
        }
        return true;
    }
}
