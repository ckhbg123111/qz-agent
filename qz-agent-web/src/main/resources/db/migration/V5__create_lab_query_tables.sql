CREATE TABLE IF NOT EXISTS lab_query_cursor (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_name VARCHAR(64) NOT NULL,
    last_query_start_time DATETIME DEFAULT NULL,
    last_query_end_time DATETIME DEFAULT NULL,
    last_status VARCHAR(32) NOT NULL,
    last_error_message VARCHAR(255) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_lab_query_cursor_task_name (task_name)
);

CREATE TABLE IF NOT EXISTS lab_query_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    task_name VARCHAR(64) NOT NULL,
    patient_id VARCHAR(64) NOT NULL,
    query_tag VARCHAR(32) NOT NULL,
    query_start_time DATETIME NOT NULL,
    query_end_time DATETIME NOT NULL,
    msg_id VARCHAR(64) NOT NULL,
    request_xml LONGTEXT NOT NULL,
    response_xml LONGTEXT DEFAULT NULL,
    result_code VARCHAR(16) DEFAULT NULL,
    result_desc VARCHAR(255) DEFAULT NULL,
    order_count INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL,
    error_message VARCHAR(255) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_lab_query_record_task_time (task_name, create_time),
    KEY idx_lab_query_record_patient_time (patient_id, create_time),
    KEY idx_lab_query_record_msg_id (msg_id)
);
