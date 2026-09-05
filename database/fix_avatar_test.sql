-- =====================================================================
-- 头像显示测试数据：给5个员工设置5种不同格式/尺寸的头像
-- 测试目的：验证 el-avatar 能正确加载多种格式（PNG/SVG/JPEG/小尺寸/WebP）
-- =====================================================================

USE `smart_restaurant`;
SET NAMES utf8mb4;

-- ---------------------------------------------------------------------
-- 5 种测试头像：
-- 1. 张总  -> PNG 256x256（标准尺寸，DiceBear 生成）
-- 2. 张店长 -> SVG 矢量（DiceBear 生成，无损缩放）
-- 3. 李店长 -> JPEG 256x256（iPravatar 真人照片）
-- 4. 王店长 -> PNG 64x64（小尺寸，验证小图加载）
-- 5. 小李   -> JPEG 512x512（大尺寸，验证大图加载）
-- 其他员工 -> NULL（验证兜底首字母显示）
-- ---------------------------------------------------------------------
UPDATE `sys_user` SET `avatar` = 'https://api.dicebear.com/7.x/avataaars/png?seed=zhangboss&size=256'           WHERE `id` = 1;
UPDATE `sys_user` SET `avatar` = 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhangdianzhang'                WHERE `id` = 2;
UPDATE `sys_user` SET `avatar` = 'https://i.pravatar.cc/256?img=12'                                              WHERE `id` = 3;
UPDATE `sys_user` SET `avatar` = 'https://api.dicebear.com/7.x/avataaars/png?seed=wangdianzhang&size=64'         WHERE `id` = 4;
UPDATE `sys_user` SET `avatar` = 'https://i.pravatar.cc/512?img=33'                                              WHERE `id` = 5;

-- 校验
SELECT id, username, real_name, avatar FROM `sys_user` WHERE `id` BETWEEN 1 AND 5 ORDER BY id;
