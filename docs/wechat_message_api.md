# 消化道疾病健康教育智能体接口文档

[接口文档在线链接](https://s.apifox.cn/5f4ade91-3ec5-4861-8055-d5159d2af1b4)

## 1. 接口概述
通过 `tag` 获取宣传页消息体，以及跳转链接 `jumpLink`。

- 接口域名： `https://api.aiqikang.com`
- 接口路径：`/api/b2b/wechat/message`
- 请求方式：`POST`
- 鉴权方式： AppKey + 时间戳 + 签名（HMAC-SHA256）
- 响应格式：统一 `Result<T>`

## 2. 请求参数

### 2.1 Body 参数（JSON）
| 参数名 | 类型 | 必填 | 说明 |
| --- | --- |----| --- |
| tag | String | 是  | 消息标签（标签与事件对应文档见附件） |
| patientName | String | 是  | 患者姓名 |
| gender | String | 是  | 性别（男/女/未知） |
| age | Integer | 否  | 年龄 |
| diagnosis | String | 否  | 诊断 |
| prescription | String | 否  | 处方 |
| examTime | String | 是  | 检查时间（ISO-8601） |
| patientId | String | 是  | 患者ID（可唯一标识患者即可） |
| reminderContent | String | 否  | 提醒内容（智能体对话上下文） |

- 可依需求对敏感字段做AES-GCM加密脱敏

### 2.2 标签与事件（附件）
| tag | 业务类型 | 发送时机 | 标题 |
| --- | --- | --- | --- |
| QZ_PAGE_CLINICAL_EXAM_APPOINTMENT_GUIDE | 内镜检查 | 预约成功 | 内镜检查预约指南 |
| QZ_PAGE_COLONOSCOPY_MEDICATION_GUIDE_1 | 内镜检查 | 检查前一天17：00（9：30以前） | 9：30前内镜检查术前1天准备指南（磷酸钠盐散用药） |
| QZ_PAGE_COLONOSCOPY_MEDICATION_GUIDE_2 | 内镜检查 | 检查前一天17：00（9：30以后） | 9：30后内镜检查术前1天准备指南（磷酸钠盐散用药） |
| QZ_PAGE_COLONOSCOPY_MEDICATION_GUIDE_3 | 内镜检查 | 检查前一天17：00（9：30以前） | 9：30前内镜检查术前1天准备指南（复方聚乙二醇电解质散用药） |
| QZ_PAGE_COLONOSCOPY_MEDICATION_GUIDE_4 | 内镜检查 | 检查前一天17：00（9：30以后） | 9：30后内镜检查术前1天准备指南（复方聚乙二醇电解质散用药） |
| QZ_PAGE_COLONOSCOPY_MEDICATION_GUIDE_5 | 内镜检查 | 检查前一天17：00（9：30以前） | 9：30前内镜检查术前1天准备指南（硫酸钠镁钾用药） |
| QZ_PAGE_COLONOSCOPY_MEDICATION_GUIDE_6 | 内镜检查 | 检查前一天17：00（9：30以后） | 9：30后内镜检查术前1天准备指南（硫酸钠镁钾用药） |
| QZ_PAGE_POST_COLONOSCOPY_DIETARY_INSTRUCTIONS | 内镜检查 | 检查结束后24小时 | 内镜检查后饮食须知 |

## 3. 请求头（鉴权）
| Header | 必填 | 说明 |
| --- | --- | --- |
| X-App-Key | 是 | 客户 AppKey |
| X-Timestamp | 是 | 时间戳（秒或毫秒均可） |
| X-Signature | 是 | 签名（HMAC-SHA256，hex 小写） |

appKey：qzhospital
appSecret:  d98e2ab4fcd4d4e552b37ada2eb3b14e934a6a6b1e2570aea232ac4b228229c3

### 3.1 签名算法
服务端校验逻辑：

```
canonical = appKey + "\n" + timestamp + "\n" + httpMethod + "\n" + path + "\n" + bodyHash
signature = HMAC-SHA256(appSecret, canonical) -> hex 小写
```

- httpMethod：`POST`
- path：请求路径（ `request.getRequestURI()`）
- bodyHash：请求 body 的 SHA256 hex（JSON 原文）
  - 若 body 为空，则：
    ```
    bodyHash = SHA256("") = e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
    ```

注意：bodyHash 必须按真实 JSON body 计算，否则签名错误。

## 4. 响应结构

### 4.1 成功响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "replyTitle": "图文消息标题",
    "replyDescription": "图文消息描述",
    "replyImageUrl": "图文消息图片链接",
    "jumpLink": "跳转链接（已加签名）"
  }
}
```

### 4.2 响应字段说明
| 字段 | 类型 | 说明 |
| --- | --- | --- |
| replyTitle | String | 图文消息标题 |
| replyDescription | String | 图文消息描述 |
| replyImageUrl | String | 图文消息图片链接 |
| jumpLink | String | 带签名的跳转链接（支持配置永久访问或合同有效期内可访问） |

## 5. jumpLink 签名规则（供参考，开发者无需关注）
jumpLink 在服务端生成时会追加签名参数，格式：

```
{baseUrl}?code=...&cid=...&exp=...&sign=...
```

| 参数 | 说明 |
| --- | --- |
| cid | 合同/客户标识（当前使用 appKey） |
| exp | 过期时间（Unix 秒），默认取2099-12-31 条件取合同结束时间 |
| sign | Base64UrlEncode(HMAC-SHA256(secret, cid + "|" + exp)) |

## 6. 示例请求（curl）

### 6.1 正确 POST 请求（JSON body）
```bash
curl --request POST "http://localhost:8080/api/b2b/wechat/message" \
  --header "X-App-Key: qzhospital" \
  --header "X-Timestamp: 1769400470392" \
  --header "X-Signature: <签名值>" \
  --header "Content-Type: application/json" \
  --data-raw '{
    "tag": "UUID_EXAMPLE_1",
    "patientId": "PATIENT_001",
    "examTime": "2026-01-26T10:30:00",
    "patientName": "John Doe",
    "gender": "male",
    "age": 45,
    "diagnosis": "example diagnosis",
    "prescription": "example prescription",
    "reminderContent": "example reminder"
  }'
```

## 7. 常见错误与返回
| code | message | 说明 |
| --- | --- | --- |
| 401 | 缺少鉴权信息 | 未带必要 Header |
| 401 | 时间戳无效 | 时间戳格式错误 |
| 401 | 请求已过期 | 超过时间窗口（默认 300s） |
| 401 | AppKey无效 | appKey 不存在 |
| 401 | 客户已停用 | status=0 |
| 401 | 合同未生效/已过期 | 合同时间不在有效期 |
| 401 | 签名错误 | 签名不一致 |
| 404 | 消息不存在 | tag 未命中消息 |

## 8. 注意事项
- POST 请求必须使用真实 JSON body 计算 bodyHash。
- 时间戳必须在服务器允许窗口内（默认 300 秒）。
- X-Timestamp 若为毫秒，服务端会自动转为秒进行时间窗校验。

## 9. 请求url生成示例（java）
```java
public class B2bWechatMessageControllerTest {

    @Test
    void getByTagReturnsSignedMessage() throws Exception {
        String tag = "UUID_EXAMPLE_1";
        String appKey = "your-app-key";
        String appSecret = "your-app-secret";
        long timestamp = System.currentTimeMillis();

        String path = "/api/b2b/wechat/message";
        String body = "{"
                + "\"tag\":\"" + tag + "\","
                + "\"patientId\":\"PATIENT_001\","
                + "\"examTime\":\"2026-01-26T10:30:00\","
                + "\"patientName\":\"John Doe\","
                + "\"gender\":\"male\","
                + "\"age\":45,"
                + "\"diagnosis\":\"example diagnosis\","
                + "\"prescription\":\"example prescription\","
                + "\"reminderContent\":\"example reminder\""
                + "}";
        String signature = buildB2bSignature(appKey, appSecret, timestamp, "POST", path, body);
        String curl = buildCurlCommand("https://aiqikang.com", path, appKey, timestamp, signature, body);
        System.out.println(curl);
    }

    private String buildB2bSignature(String appKey, String secret, long timestamp, String method, String path, String body) {
        String bodyHash = sha256Hex(body == null ? "" : body);
        String canonical = appKey + "\n" + timestamp + "\n" + method + "\n" + path + "\n" + bodyHash;
        return hmacSha256Hex(secret, canonical);
    }

    private String buildCurlCommand(String host, String path, String appKey, long timestamp, String signature, String body) {
        String url = host + path;
        return "curl -X POST \"" + url + "\" "
                + "-H \"Content-Type: application/json\" "
                + "-H \"X-App-Key: " + appKey + "\" "
                + "-H \"X-Timestamp: " + timestamp + "\" "
                + "-H \"X-Signature: " + signature + "\" "
                + "-d '" + body + "'";
    }

    private String sha256Hex(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            return toHex(hash);
        } catch (Exception e) {
            return "";
        }
    }

    private String hmacSha256Hex(String secret, String message) {
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec keySpec =
                    new javax.crypto.spec.SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] raw = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return toHex(raw);
        } catch (Exception e) {
            return "";
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

```