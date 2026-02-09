package com.zhongjia.web.integration.wechat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhongjia.web.config.WechatMessageProperties;
import com.zhongjia.web.exception.BizException;
import com.zhongjia.web.vo.wechat.WechatMessageRequest;
import com.zhongjia.web.vo.wechat.WechatMessageResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class WechatMessageClient {

    private static final String HEADER_APP_KEY = "X-App-Key";
    private static final String HEADER_TIMESTAMP = "X-Timestamp";
    private static final String HEADER_SIGNATURE = "X-Signature";

    private final WechatMessageProperties properties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public WechatMessageClient(WechatMessageProperties properties, ObjectMapper objectMapper) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.restTemplate = new RestTemplate();
    }

    public WechatMessageResponse fetchMessage(WechatMessageRequest request) {
        String appKey = properties.getAppKey();
        String appSecret = properties.getAppSecret();
        if (!StringUtils.hasText(appKey) || !StringUtils.hasText(appSecret)) {
            throw new BizException(500, "微信消息接口密钥未配置");
        }

        String body = toJson(request);
        long timestamp = System.currentTimeMillis();
        String signature = buildSignature(appKey, appSecret, timestamp, "POST", properties.getPath(), body);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HEADER_APP_KEY, appKey);
        headers.set(HEADER_TIMESTAMP, String.valueOf(timestamp));
        headers.set(HEADER_SIGNATURE, signature);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        String url = properties.getBaseUrl() + properties.getPath();
        ResponseEntity<WechatMessageResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, WechatMessageResponse.class);
        WechatMessageResponse responseBody = response.getBody();
        if (responseBody == null) {
            throw new BizException(502, "微信消息接口无响应");
        }
        if (responseBody.getCode() != 200) {
            throw new BizException(responseBody.getCode(), responseBody.getMessage());
        }
        if (responseBody.getData() == null) {
            throw new BizException(502, "微信消息接口返回空数据");
        }
        return responseBody;
    }

    private String toJson(WechatMessageRequest request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new BizException(500, "微信消息请求序列化失败");
        }
    }

    private String buildSignature(String appKey, String secret, long timestamp, String method, String path, String body) {
        String bodyHash = sha256Hex(body == null ? "" : body);
        String canonical = appKey + "\n" + timestamp + "\n" + method + "\n" + path + "\n" + bodyHash;
        return hmacSha256Hex(secret, canonical);
    }

    private String sha256Hex(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (Exception e) {
            throw new BizException(500, "微信消息签名计算失败");
        }
    }

    private String hmacSha256Hex(String secret, String message) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] raw = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (Exception e) {
            throw new BizException(500, "微信消息签名计算失败");
        }
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
