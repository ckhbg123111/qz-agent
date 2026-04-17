# 消化内科科普宣教接口文档2.0

## 1. 基本信息

- 服务ip：`http://192.168.1.177`
- 服务前缀：`/api/b2b/qz/hp`
- 请求方式：`POST`
- 数据格式：`application/json`
- 字符集：`UTF-8`
- 返回格式：统一 `Result<Boolean>`
- 当前业务行为：两个接口当前均返回成功（`data = true`）
- 日志说明：服务端会记录请求中的全部参数用于对接排查

[在线接口文档](https://s.apifox.cn/d66b6e43-b22f-42a2-b9e4-fc0e5de899ec?pwd=DqKH7gPD)

---

## 2. 通用返回结构

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

字段说明：

- `code`：状态码，`200` 表示成功
- `message`：状态描述，成功为 `success`
- `data`：业务结果，当前固定 `true`

常见失败返回（参数错误）：

```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

---

## 3. 病历确诊事件接口

- 接口名称：病历确诊事件
- URL：`/api/b2b/qz/hp/diagnosis-event`
- Method：`POST`

### 3.1 请求参数

| 字段名 | 类型 | 必填 | 说明                             |
|---|---|---|--------------------------------|
| patientId | String | 是 | 患者ID                           |
| patientName | String | 否 | 患者名称                           |
| gender | String | 否 | 患者性别（男/女/未知）                   |
| age | Integer | 否 | 患者年龄                           |
| department | String | 否 | 科室                             |
| diagnosis | String | 否 | 诊断                             |
| remark | String | 否 | 备注                             |
| date | String | 否 | 日期（建议 `yyyy-MM-dd` 或 ISO-8601） |
| doctor | String | 否 | 医生                             |
| diseaseType | String(Enum) | 是 | 病种枚举（见下文 3.2）                  |

### 3.2 病种枚举说明（含中文备注）

| 枚举值 | 中文备注 | 对接备注                |
|---|---|---------------------|
| GASTRITIS | 胃炎 | 出现胃炎关键词、确诊胃炎相关疾病    |
| PEPTIC_ULCER | 消化性溃疡 | 消化性溃疡 十二指肠溃疡 胃溃疡关键词 |
| INFLAMMATORY_BOWEL_DISEASE | 炎症性肠病 | 溃疡性结肠炎 克罗恩病 炎症性肠病   |

### 3.3 请求示例

```json
{
  "patientId": "P202604160001",
  "patientName": "张三",
  "gender": "男",
  "age": 36,
  "department": "消化内科",
  "diagnosis": "慢性胃炎",
  "remark": "门诊复诊",
  "date": "2026-04-16",
  "doctor": "李医生",
  "diseaseType": "GASTRITIS"
}
```

### 3.4 成功响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

---

## ~~4. 挂号事件接口~~

- ~~接口名称：挂号事件~~
- ~~URL：`/api/b2b/qz/hp/registration-event`~~
- ~~Method：`POST`~~

### ~~4.1 请求参数~~

| ~~字段名~~ | ~~类型~~ | ~~必填~~ | ~~说明~~                    |
|---|---|---|-----------------------|
| ~~patientId~~ | ~~String~~ | ~~是~~ | ~~患者ID~~                  |
| ~~patientName~~ | ~~String~~ | ~~否~~ | ~~患者名称~~                  |
| ~~gender~~ | ~~String~~ | ~~否~~ | ~~患者性别（男/女/未知）~~          |
| ~~age~~ | ~~Integer~~ | ~~否~~ | ~~患者年龄~~                  |
| ~~department~~ | ~~String~~ | ~~是~~ | ~~科室（暂定String类型，视情况可修改）~~ |

---

## 5. 对接注意事项

- `patientId`、`department`、`diseaseType` 请按文档要求传递，缺失会触发参数错误（`code=400`）。
- `diseaseType` 必须传枚举英文值，不传中文。
- 建议请求头统一设置：`Content-Type: application/json`。
