CREATE TABLE IF NOT EXISTS qz_education_push_rule (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_code VARCHAR(64) NOT NULL,
    rule_name VARCHAR(128) NOT NULL,
    event_type VARCHAR(32) NOT NULL,
    trigger_type VARCHAR(16) NOT NULL,
    tag VARCHAR(64) NOT NULL,
    previous_rule_code VARCHAR(64) DEFAULT NULL,
    delay_amount INT DEFAULT NULL,
    delay_unit VARCHAR(16) DEFAULT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    remark VARCHAR(255) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_qz_education_push_rule_code (rule_code),
    KEY idx_qz_education_push_rule_event (event_type, trigger_type, enabled),
    KEY idx_qz_education_push_rule_previous (previous_rule_code, trigger_type, enabled)
);

CREATE TABLE IF NOT EXISTS qz_education_push_rule_condition (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    rule_id BIGINT NOT NULL,
    field_name VARCHAR(64) NOT NULL,
    match_type VARCHAR(32) NOT NULL,
    match_value VARCHAR(255) NOT NULL,
    enabled TINYINT NOT NULL DEFAULT 1,
    sort_order INT NOT NULL DEFAULT 0,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_qz_education_push_rule_condition_rule (rule_id, enabled),
    KEY idx_qz_education_push_rule_condition_field (field_name)
);

CREATE TABLE IF NOT EXISTS wechat_push_success_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    patient_id VARCHAR(64) NOT NULL,
    tag VARCHAR(64) NOT NULL,
    source_rule_code VARCHAR(64) DEFAULT NULL,
    task_id BIGINT DEFAULT NULL,
    push_log_id BIGINT DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_wechat_push_success_patient_tag (patient_id, tag),
    KEY idx_wechat_push_success_task (task_id),
    KEY idx_wechat_push_success_rule (source_rule_code)
);

INSERT INTO qz_education_push_rule
    (rule_code, rule_name, event_type, trigger_type, tag, previous_rule_code, delay_amount, delay_unit, enabled, sort_order, remark)
VALUES
    ('WY_QZ', '胃炎确诊宣教', 'DIAGNOSIS_EVENT', 'IMMEDIATE', 'WY_QZ', NULL, NULL, NULL, 1, 0, '诊断码包含K29'),
    ('WY_FCTX', '胃炎复查提醒', 'DIAGNOSIS_EVENT', 'DELAYED', 'WY_FCTX', 'WY_QZ', 14, 'DAYS', 1, 1, '胃炎确诊14天后'),
    ('XHKY_QZ', '消化性溃疡确诊宣教', 'DIAGNOSIS_EVENT', 'IMMEDIATE', 'XHKY_QZ', NULL, NULL, NULL, 1, 2, '诊断码包含K26/K25/K27'),
    ('XHKY_FCTX', '消化性溃疡复查提醒', 'DIAGNOSIS_EVENT', 'DELAYED', 'XHKY_FCTX', 'XHKY_QZ', 1, 'MONTHS', 1, 3, '消化性溃疡确诊1个月后'),
    ('YZCB_QZ', '炎症性肠病确诊宣教', 'DIAGNOSIS_EVENT', 'IMMEDIATE', 'YZCB_QZ', NULL, NULL, NULL, 1, 4, '诊断码包含K50/K51'),
    ('YZCB_FCTX', '炎症性肠病复查提醒', 'DIAGNOSIS_EVENT', 'DELAYED', 'YZCB_FCTX', 'YZCB_QZ', 1, 'MONTHS', 1, 5, '炎症性肠病确诊1个月后'),
    ('TNB_ZBQD', '糖尿病诊前清单', 'DIAGNOSIS_EVENT', 'IMMEDIATE', 'TNB_ZBQD', NULL, NULL, NULL, 1, 7, '诊断码包含E10/E11/E14'),
    ('TNB_ZN', '糖尿病指南', 'DIAGNOSIS_EVENT', 'DELAYED', 'TNB_ZN', 'TNB_ZBQD', 4, 'HOURS', 1, 8, '糖尿病确诊4小时后'),
    ('TNB_CF', '糖尿病处方宣教', 'PRESCRIPTION_EVENT', 'IMMEDIATE', 'TNB_CF', NULL, NULL, NULL, 1, 9, '糖尿病且开具指定药品'),
    ('TNB_SF', '糖尿病随访', 'PRESCRIPTION_EVENT', 'DELAYED', 'TNB_SF', 'TNB_CF', 14, 'DAYS', 1, 10, '糖尿病处方14天后');

INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCodeSystem', 'EQUALS_IGNORE_CASE', 'ICD-10', 0 FROM qz_education_push_rule WHERE rule_code = 'WY_QZ';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'K29', 1 FROM qz_education_push_rule WHERE rule_code = 'WY_QZ';

INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCodeSystem', 'EQUALS_IGNORE_CASE', 'ICD-10', 0 FROM qz_education_push_rule WHERE rule_code = 'XHKY_QZ';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'K26', 1 FROM qz_education_push_rule WHERE rule_code = 'XHKY_QZ';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'K25', 2 FROM qz_education_push_rule WHERE rule_code = 'XHKY_QZ';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'K27', 3 FROM qz_education_push_rule WHERE rule_code = 'XHKY_QZ';

INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCodeSystem', 'EQUALS_IGNORE_CASE', 'ICD-10', 0 FROM qz_education_push_rule WHERE rule_code = 'YZCB_QZ';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'K50', 1 FROM qz_education_push_rule WHERE rule_code = 'YZCB_QZ';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'K51', 2 FROM qz_education_push_rule WHERE rule_code = 'YZCB_QZ';

INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCodeSystem', 'EQUALS_IGNORE_CASE', 'ICD-10', 0 FROM qz_education_push_rule WHERE rule_code = 'TNB_ZBQD';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'E10', 1 FROM qz_education_push_rule WHERE rule_code = 'TNB_ZBQD';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'E11', 2 FROM qz_education_push_rule WHERE rule_code = 'TNB_ZBQD';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'E14', 3 FROM qz_education_push_rule WHERE rule_code = 'TNB_ZBQD';

INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCodeSystem', 'EQUALS_IGNORE_CASE', 'ICD-10', 0 FROM qz_education_push_rule WHERE rule_code = 'TNB_CF';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'E10', 1 FROM qz_education_push_rule WHERE rule_code = 'TNB_CF';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'E11', 2 FROM qz_education_push_rule WHERE rule_code = 'TNB_CF';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'diagnosisCode', 'CONTAINS_IGNORE_CASE', 'E14', 3 FROM qz_education_push_rule WHERE rule_code = 'TNB_CF';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'medicineName', 'CONTAINS_IGNORE_CASE', '胰岛素', 4 FROM qz_education_push_rule WHERE rule_code = 'TNB_CF';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'medicineName', 'CONTAINS_IGNORE_CASE', '利拉鲁肽', 5 FROM qz_education_push_rule WHERE rule_code = 'TNB_CF';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'medicineName', 'CONTAINS_IGNORE_CASE', '司美格鲁肽', 6 FROM qz_education_push_rule WHERE rule_code = 'TNB_CF';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'medicineName', 'CONTAINS_IGNORE_CASE', '替尔泊肽', 7 FROM qz_education_push_rule WHERE rule_code = 'TNB_CF';
