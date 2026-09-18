-- ============================================================
-- 报表中心视图
-- 用于 P1-5 报表中心的 6 个报表接口
-- 基于实际数据库表结构创建
-- ============================================================

-- 1. 菜品流水视图
-- 说明：orders 表没有 pay_method 字段，支付方式存在 payment_record.pay_type 中
--       一笔订单可能有多种支付方式，这里用 GROUP_CONCAT 汇总展示
CREATE OR REPLACE VIEW v_dish_flow AS
SELECT
    od.id AS flow_id,
    o.id AS order_id,
    o.order_no,
    o.store_id,
    s.store_name,
    od.dish_id,
    od.dish_name,
    od.spec_name,
    od.quantity,
    od.dish_price AS unit_price,
    od.subtotal,
    o.order_status,
    (SELECT GROUP_CONCAT(DISTINCT pr.pay_type ORDER BY pr.pay_time SEPARATOR ',')
     FROM payment_record pr WHERE pr.order_id = o.id AND pr.status = 2) AS pay_type,
    o.create_time AS order_time,
    o.pay_time
FROM order_detail od
JOIN orders o ON od.order_id = o.id
LEFT JOIN store s ON o.store_id = s.id;

-- 2. 收款报表视图
-- 说明：orders 表没有 pay_method 和 pay_amount 字段，改用 payment_record 表
--       支付方式用 pay_type（字符串：WECHAT/ALIPAY/CASH/MEMBER_BALANCE/SCAN）
--       订单金额用 orders.actual_amount（实付金额）
CREATE OR REPLACE VIEW v_receipt_report AS
SELECT
    DATE(pr.pay_time) AS receipt_date,
    o.store_id,
    s.store_name,
    COUNT(DISTINCT o.id) AS order_count,
    COALESCE(SUM(pr.amount), 0) AS total_amount,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'WECHAT' THEN pr.amount ELSE 0 END), 0) AS wechat_amount,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'ALIPAY' THEN pr.amount ELSE 0 END), 0) AS alipay_amount,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'CASH' THEN pr.amount ELSE 0 END), 0) AS cash_amount,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'MEMBER_BALANCE' THEN pr.amount ELSE 0 END), 0) AS member_amount,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'SCAN' THEN pr.amount ELSE 0 END), 0) AS scan_amount,
    COALESCE(AVG(o.actual_amount), 0) AS avg_amount
FROM payment_record pr
JOIN orders o ON pr.order_id = o.id
LEFT JOIN store s ON o.store_id = s.id
WHERE pr.status = 2 -- 支付成功
  AND o.order_status IN (3, 4, 7) -- 已支付/制作中/已完成
GROUP BY DATE(pr.pay_time), o.store_id, s.store_name;

-- 3. 菜品销售排行视图
CREATE OR REPLACE VIEW v_dish_sales_ranking AS
SELECT
    d.id AS dish_id,
    d.dish_name,
    d.store_id,
    s.store_name,
    d.price,
    d.dish_category_id,
    dc.category_name,
    COUNT(od.id) AS sale_count,
    COALESCE(SUM(od.quantity), 0) AS total_quantity,
    COALESCE(SUM(od.subtotal), 0) AS total_amount
FROM dish d
LEFT JOIN order_detail od ON d.id = od.dish_id
LEFT JOIN orders o ON od.order_id = o.id AND o.order_status IN (3, 4)
LEFT JOIN store s ON d.store_id = s.id
LEFT JOIN dish_category dc ON d.dish_category_id = dc.id
GROUP BY d.id, d.dish_name, d.store_id, s.store_name, d.price, d.dish_category_id, dc.category_name;

-- 4. 门店销售排行视图
-- 说明：orders 表没有 pay_amount 字段，改用 actual_amount（实付金额）
CREATE OR REPLACE VIEW v_store_sales_ranking AS
SELECT
    s.id AS store_id,
    s.store_name,
    s.province,
    s.city,
    s.district,
    COUNT(DISTINCT o.id) AS order_count,
    COALESCE(SUM(o.actual_amount), 0) AS total_amount,
    COALESCE(AVG(o.actual_amount), 0) AS avg_amount,
    COUNT(DISTINCT o.member_id) AS member_count
FROM store s
LEFT JOIN orders o ON s.id = o.store_id AND o.order_status IN (3, 4, 7)
GROUP BY s.id, s.store_name, s.province, s.city, s.district;

-- 5. 今日收入视图
-- 说明：orders 表没有 pay_method 和 pay_amount 字段，改用 payment_record 表
CREATE OR REPLACE VIEW v_today_income AS
SELECT
    o.store_id,
    s.store_name,
    COUNT(DISTINCT o.id) AS order_count,
    COALESCE(SUM(pr.amount), 0) AS total_income,
    COALESCE(AVG(o.actual_amount), 0) AS avg_amount,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'WECHAT' THEN pr.amount ELSE 0 END), 0) AS wechat_income,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'ALIPAY' THEN pr.amount ELSE 0 END), 0) AS alipay_income,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'CASH' THEN pr.amount ELSE 0 END), 0) AS cash_income,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'MEMBER_BALANCE' THEN pr.amount ELSE 0 END), 0) AS member_income,
    COALESCE(SUM(CASE WHEN pr.pay_type = 'SCAN' THEN pr.amount ELSE 0 END), 0) AS scan_income
FROM payment_record pr
JOIN orders o ON pr.order_id = o.id
LEFT JOIN store s ON o.store_id = s.id
WHERE pr.status = 2 -- 支付成功
  AND o.order_status IN (3, 4, 7) -- 已支付/制作中/已完成
  AND DATE(pr.pay_time) = CURDATE()
GROUP BY o.store_id, s.store_name;

-- 6. 退款记录视图
CREATE OR REPLACE VIEW v_refund_record AS
SELECT
    r.id AS refund_id,
    r.refund_no,
    r.order_id,
    o.order_no,
    r.store_id,
    s.store_name,
    r.member_id,
    r.amount AS refund_amount,
    r.reason AS refund_reason,
    r.refund_type,
    r.status AS refund_status,
    r.create_time AS refund_time,
    r.refund_time AS approve_time,
    r.operator_id,
    u.real_name AS operator_name
FROM refund r
LEFT JOIN orders o ON r.order_id = o.id
LEFT JOIN store s ON r.store_id = s.id
LEFT JOIN sys_user u ON r.operator_id = u.id;