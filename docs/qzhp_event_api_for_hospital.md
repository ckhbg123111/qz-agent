# 消化内科科普宣教接口文档2.0

## 1. 基本信息

- 服务ip：`http://192.168.1.177`
- 服务前缀：`/api/b2b/qz/hp`
- 请求方式：`POST`
- 数据格式：`application/json`
- 字符集：`UTF-8`
- 返回格式：事件接收接口统一 `Result<Boolean>`
- 当前业务行为：事件接收接口返回成功时 `data = true`
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

## 4. 检验开单事件接口

- 接口名称：检验开单事件
- URL：`/api/b2b/qz/hp/lab-order-event`
- Method：`POST`
- 接口说明：用于接收医生在 HIS/EMR/CPOE 中开立检验申请单的事件，不等同于检验预约，也不等同于检验报告结果

### 4.1 请求参数

| 字段名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| eventId | String | 是 | 事件唯一标识，建议作为幂等键 |
| traceId | String | 否 | 链路追踪号 |
| eventTime | String | 是 | 事件发生时间，建议 `ISO-8601` |
| sourceSystem | String | 是 | 来源系统，如 `HIS`、`EMR`、`CPOE` |
| patientId | String | 是 | 患者唯一标识 |
| patientName | String | 否 | 患者姓名 |
| gender | String | 否 | 患者性别（男/女/未知） |
| age | Integer | 否 | 患者年龄 |
| visitId | String | 否 | 本次就诊唯一标识 |
| visitNo | String | 否 | 门诊号/住院号/就诊号 |
| encounterType | String | 否 | 就诊类型，如 `OUTPATIENT`、`EMERGENCY`、`INPATIENT` |
| departmentCode | String | 否 | 申请科室编码 |
| departmentName | String | 否 | 申请科室名称 |
| labApplyNo | String | 是 | 检验申请单号 |
| orderNo | String | 否 | 医嘱号/开单号 |
| orderTime | String | 是 | 开单时间，建议 `ISO-8601` |
| applyDoctorId | String | 否 | 申请医生工号 |
| applyDoctorName | String | 否 | 申请医生姓名 |
| executeDepartmentCode | String | 否 | 执行科室编码 |
| executeDepartmentName | String | 否 | 执行科室名称 |
| priority | String | 否 | 优先级，如 `ROUTINE`、`URGENT`、`STAT` |
| diagnosisCode | String | 否 | 主诊断编码，优先 `ICD-10`，糖尿病示例 `E14.90` |
| diagnosisCodeSystem | String | 否 | 诊断编码体系，默认 `ICD-10` |
| diagnosis | String | 否 | 主诊断名称 |
| specimenTypeCode | String | 否 | 标本类型编码，如 `BLOOD`、`SERUM`、`BREATH` |
| specimenTypeName | String | 否 | 标本类型名称 |
| specimenCollectionSite | String | 否 | 采样部位 |
| clinicalPurpose | String | 否 | 临床诊断依据或申请目的 |
| chiefComplaint | String | 否 | 主诉 |
| labItems | Array<Object> | 是 | 检验项目列表 |
| remark | String | 否 | 备注 |

`labItems` 子结构：

| 字段名 | 类型 | 必填 | 说明 |
|---|---|---|---|
| labItemCode | String | 是 | 检验项目编码，优先院内 `LIS` 项目编码 |
| labItemName | String | 是 | 检验项目名称 |
| labItemCodeSystem | String | 否 | 项目编码体系，默认院内 `LIS` 项目字典 |
| specimenType | String | 否 | 项目默认标本类型 |
| itemCategory | String | 否 | 项目分组，如生化、免疫、呼气试验 |

### 4.2 请求示例

```json
{
  "eventId": "evt-lab-order-20260422-0001",
  "traceId": "trace-0f92ca1490d14e12",
  "eventTime": "2026-04-22T09:20:00+08:00",
  "sourceSystem": "HIS",
  "patientId": "P202604220001",
  "patientName": "张三",
  "gender": "男",
  "age": 58,
  "visitId": "V202604220001",
  "visitNo": "MZ202604220015",
  "encounterType": "OUTPATIENT",
  "departmentCode": "ENDO001",
  "departmentName": "内分泌科门诊",
  "labApplyNo": "LIS202604220001",
  "orderNo": "ORD202604220015",
  "orderTime": "2026-04-22T09:18:00+08:00",
  "applyDoctorId": "D0108",
  "applyDoctorName": "王医生",
  "executeDepartmentCode": "LAB001",
  "executeDepartmentName": "检验科",
  "priority": "ROUTINE",
  "diagnosisCode": "E14.90",
  "diagnosisCodeSystem": "ICD-10",
  "diagnosis": "2型糖尿病",
  "specimenTypeCode": "BLOOD",
  "specimenTypeName": "全血",
  "specimenCollectionSite": "静脉血",
  "clinicalPurpose": "糖尿病血糖及糖化控制评估",
  "chiefComplaint": "口干多饮 2 月",
  "labItems": [
    {
      "labItemCode": "GLU_FAST",
      "labItemName": "空腹血糖",
      "labItemCodeSystem": "LIS",
      "specimenType": "静脉血",
      "itemCategory": "生化"
    },
    {
      "labItemCode": "HBA1C",
      "labItemName": "糖化血红蛋白",
      "labItemCodeSystem": "LIS",
      "specimenType": "全血",
      "itemCategory": "生化"
    }
  ],
  "remark": "门诊首诊开单"
}
```

### 4.3 成功响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": true
}
```

### 4.4 节点说明

- `lab-order-event`：检验申请单开立事件
- `lab-appointment`：检验预约/触达接口
- `report`：检验报告结果接口

---

## ~~5. 挂号事件接口~~

- ~~接口名称：挂号事件~~
- ~~URL：`/api/b2b/qz/hp/registration-event`~~
- ~~Method：`POST`~~

### ~~5.1 请求参数~~

| ~~字段名~~ | ~~类型~~ | ~~必填~~ | ~~说明~~                    |
|---|---|---|-----------------------|
| ~~patientId~~ | ~~String~~ | ~~是~~ | ~~患者ID~~                  |
| ~~patientName~~ | ~~String~~ | ~~否~~ | ~~患者名称~~                  |
| ~~gender~~ | ~~String~~ | ~~否~~ | ~~患者性别（男/女/未知）~~          |
| ~~age~~ | ~~Integer~~ | ~~否~~ | ~~患者年龄~~                  |
| ~~department~~ | ~~String~~ | ~~是~~ | ~~科室（暂定String类型，视情况可修改）~~ |

---

## 6. 对接注意事项

- `patientId`、`department`、`diseaseType` 请按病历确诊事件要求传递，缺失会触发参数错误（`code=400`）。
- `diseaseType` 必须传枚举英文值，不传中文。
- `lab-order-event` 建议始终传递 `eventId`、`eventTime`、`sourceSystem`、`labApplyNo`、`orderTime` 和 `labItems`。
- 检验项目编码优先使用院内 `LIS` 项目编码，诊断编码优先使用 `ICD-10`。
- 建议请求头统一设置：`Content-Type: application/json`。
