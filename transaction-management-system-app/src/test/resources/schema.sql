CREATE SCHEMA IF NOT EXISTS `transaction-management`;
CREATE TABLE IF NOT EXISTS `transaction-management`.`roles` (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);
