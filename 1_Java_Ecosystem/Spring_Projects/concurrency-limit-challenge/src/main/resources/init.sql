CREATE DATABASE IF NOT EXISTS concurrency_challenge DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE concurrency_challenge;

CREATE TABLE IF NOT EXISTS `zhihu_keyword` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `keyword` VARCHAR(255) NOT NULL UNIQUE COMMENT '抓取关键词',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

DELIMITER $$
CREATE PROCEDURE IF NOT EXISTS InsertKeywords()
BEGIN
    DECLARE i INT DEFAULT 1;
    WHILE i <= 1000 DO
        INSERT IGNORE INTO zhihu_keyword (keyword) VALUES (CONCAT('模拟关键词_', i));
        SET i = i + 1;
    END WHILE;
END$$
DELIMITER ;

CALL InsertKeywords();

DROP PROCEDURE InsertKeywords;
