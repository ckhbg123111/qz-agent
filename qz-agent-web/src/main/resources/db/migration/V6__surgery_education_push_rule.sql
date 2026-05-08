ALTER TABLE qz_education_push_rule
    ADD COLUMN trigger_time_strategy VARCHAR(32) DEFAULT NULL AFTER delay_unit,
    ADD COLUMN anchor_field VARCHAR(64) DEFAULT NULL AFTER trigger_time_strategy,
    ADD COLUMN anchor_day_offset INT DEFAULT NULL AFTER anchor_field,
    ADD COLUMN anchor_time VARCHAR(8) DEFAULT NULL AFTER anchor_day_offset,
    ADD COLUMN late_policy VARCHAR(32) DEFAULT NULL AFTER anchor_time,
    ADD COLUMN window_end_field VARCHAR(64) DEFAULT NULL AFTER late_policy;

UPDATE qz_education_push_rule
SET trigger_time_strategy = CASE
    WHEN trigger_type = 'DELAYED' THEN 'RELATIVE_OFFSET'
    ELSE 'IMMEDIATE'
END
WHERE trigger_time_strategy IS NULL;

INSERT INTO qz_education_push_rule
    (rule_code, rule_name, event_type, trigger_type, tag, previous_rule_code, delay_amount, delay_unit,
     trigger_time_strategy, anchor_field, anchor_day_offset, anchor_time, late_policy, window_end_field,
     enabled, sort_order, remark)
VALUES
    ('QGSH_SQZY', '屈光手术术前注意宣教', 'SURGERY_CONFIRMATION_EVENT', 'DELAYED', 'QGSH_SQZY',
     NULL, NULL, NULL, 'ANCHOR_TIME', 'plannedStartTime', -1, '19:00:00', 'SKIP', 'plannedStartTime',
     1, 15, '术前一天19:00推送；到达时间介于19:00至计划手术开始时间则立即推送；晚于计划手术开始时间跳过'),
    ('QGSH_SHZY', '屈光手术术后注意宣教', 'SURGERY_COMPLETION_EVENT', 'IMMEDIATE', 'QGSH_SHZY',
     NULL, NULL, NULL, 'IMMEDIATE', NULL, NULL, NULL, NULL, NULL,
     1, 16, '屈光手术完成后立即推送'),
    ('QGSH_SHZY2', '屈光手术术后第二天宣教', 'SURGERY_COMPLETION_EVENT', 'DELAYED', 'QGSH_SHZY2',
     NULL, NULL, NULL, 'ANCHOR_TIME', 'actualEndTime', 1, '20:00:00', 'IMMEDIATE', NULL,
     1, 17, '以实际结束时间日期为基准，次日20:00推送；晚于该时间到达则立即推送');

INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'preoperativeDiagnosisCode', 'CONTAINS_IGNORE_CASE', 'H52', 0
FROM qz_education_push_rule
WHERE rule_code = 'QGSH_SQZY';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'plannedOperationCode', 'CONTAINS_IGNORE_CASE', '11.7', 1
FROM qz_education_push_rule
WHERE rule_code = 'QGSH_SQZY';

INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'preoperativeDiagnosisCode', 'CONTAINS_IGNORE_CASE', 'H52', 0
FROM qz_education_push_rule
WHERE rule_code = 'QGSH_SHZY';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'performedOperationCode', 'CONTAINS_IGNORE_CASE', '11.7', 1
FROM qz_education_push_rule
WHERE rule_code = 'QGSH_SHZY';

INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'preoperativeDiagnosisCode', 'CONTAINS_IGNORE_CASE', 'H52', 0
FROM qz_education_push_rule
WHERE rule_code = 'QGSH_SHZY2';
INSERT INTO qz_education_push_rule_condition (rule_id, field_name, match_type, match_value, sort_order)
SELECT id, 'performedOperationCode', 'CONTAINS_IGNORE_CASE', '11.7', 1
FROM qz_education_push_rule
WHERE rule_code = 'QGSH_SHZY2';
