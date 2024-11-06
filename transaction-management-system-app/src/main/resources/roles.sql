INSERT INTO `transaction-management`.`roles` (`id`, `name`)
SELECT '1', 'USER'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `transaction-management`.`roles` WHERE `name` = 'USER'
);
