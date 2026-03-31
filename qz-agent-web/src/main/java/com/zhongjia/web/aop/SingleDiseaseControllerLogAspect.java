package com.zhongjia.web.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
public class SingleDiseaseControllerLogAspect {

    private static final Logger log = LoggerFactory.getLogger(SingleDiseaseControllerLogAspect.class);
    private final ObjectMapper objectMapper;

    public SingleDiseaseControllerLogAspect(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Around("execution(* com.zhongjia.web.controller.SingleDisease*Controller.*(..))")
    public Object printSingleDiseaseRequestAndResponseLog(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method = signature.getDeclaringTypeName() + "." + signature.getName();
        String path = currentRequestPath();
        String paramsJson = toJson(sanitizeArgs(joinPoint.getArgs()));

        log.info("单病种接口请求 path={}, method={}, params={}", path, method, paramsJson);

        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - startTime;
            log.info("单病种接口响应 path={}, method={}, cost={}ms, result={}", path, method, cost, toJson(result));
            return result;
        } catch (Throwable ex) {
            long cost = System.currentTimeMillis() - startTime;
            log.error("单病种接口异常 path={}, method={}, cost={}ms, params={}", path, method, cost, paramsJson, ex);
            throw ex;
        }
    }

    private String currentRequestPath() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return "UNKNOWN";
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return request == null ? "UNKNOWN" : request.getRequestURI();
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
