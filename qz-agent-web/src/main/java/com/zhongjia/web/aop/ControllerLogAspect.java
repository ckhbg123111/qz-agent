package com.zhongjia.web.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
public class ControllerLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ControllerLogAspect.class);
    private final ObjectMapper objectMapper;

    public ControllerLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Around("execution(* com.zhongjia.web.controller..*.*(..))")
    public Object printRequestAndResponseLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method = signature.getDeclaringTypeName() + "." + signature.getName();
        String paramsJson = toJson(sanitizeArgs(joinPoint.getArgs()));

        log.info("接口请求 method={}, params={}", method, paramsJson);

        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - startTime;
            log.info("接口响应 method={}, cost={}ms, result={}", method, cost, toJson(result));
            return result;
        } catch (Throwable ex) {
            long cost = System.currentTimeMillis() - startTime;
            log.error("接口异常 method={}, cost={}ms, params={}", method, cost, paramsJson, ex);
            throw ex;
        }
    }

    private Object[] sanitizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return new Object[0];
        }
        return Arrays.stream(args)
                .filter(Objects::nonNull)
                .filter(this::canSerializeDirectly)
                .toArray();
    }

    private boolean canSerializeDirectly(Object arg) {
        return !(arg instanceof ServletRequest
                || arg instanceof ServletResponse
                || arg instanceof MultipartFile
                || arg instanceof BindingResult);
    }

    private String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            return "\"SERIALIZE_FAILED\"";
        }
    }
}
