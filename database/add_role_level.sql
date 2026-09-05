-- =====================================================================
-- 给 sys_role 表增加 level 列，用于权限层级判断
-- 数字越大权限越高：99=总店长 / 50=店长 / 10=服务员、收银、后厨
-- =====================================================================

ALTER TABLE `sys_role`
  ADD COLUMN `level` INT NOT NULL DEFAULT 10
  COMMENT '角色等级（数字越大权限越高 99/50/10）'
  AFTER `role_code`;

-- 回填 5 个预设角色的等级
UPDATE `sys_role` SET `level` = 99 WHERE `role_code` = 'GENERAL_MANAGER';
UPDATE `sys_role` SET `level` = 50 WHERE `role_code` = 'STORE_MANAGER';
UPDATE `sys_role` SET `level` = 10 WHERE `role_code` IN ('WAITER', 'CASHIER', 'KITCHEN');
