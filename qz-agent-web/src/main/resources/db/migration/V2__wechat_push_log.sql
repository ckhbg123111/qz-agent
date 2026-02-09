CREATE TABLE IF NOT EXISTS wechat_push_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    bizcode VARCHAR(64) NOT NULL,
    patient_id VARCHAR(64) NOT NULL,
    tag VARCHAR(64) NOT NULL,
    message TEXT NOT NULL,
    wechat_api_code INT DEFAULT NULL,
    wechat_api_message VARCHAR(255) DEFAULT NULL,
    jump_link TEXT DEFAULT NULL,
    push_status VARCHAR(32) NOT NULL,
    error_message VARCHAR(255) DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
