# -*- coding: utf-8 -*-
"""交付前内容校验（V3.0：逻辑外键 + utf8mb4_general_ci + 三部分字典）"""
import re

sql = open(r"D:\TraeProject\Smart Restaurant\database\smart_restaurant.sql", encoding="utf-8").read()
dic = open(r"D:\TraeProject\Smart Restaurant\docs\智慧餐厅_数据库字典.md", encoding="utf-8").read()
plan = open(r"D:\TraeProject\Smart Restaurant\docs\智慧餐厅_产品规划书.md", encoding="utf-8").read()

checks = []

# ---------- SQL ----------
checks.append(("SQL含CREATE TABLE orders", "CREATE TABLE `orders`" in sql))
checks.append(("SQL含CREATE TABLE member", "CREATE TABLE `member`" in sql))
checks.append(("SQL含建库语句", "CREATE DATABASE IF NOT EXISTS `smart_restaurant`" in sql))
create_cnt = len(re.findall(r"CREATE TABLE", sql))
checks.append((f"SQL建表数={create_cnt}(应为43)", create_cnt == 43))
view_cnt = len(re.findall(r"CREATE OR REPLACE VIEW", sql))
checks.append((f"SQL视图数={view_cnt}(应为6)", view_cnt == 6))
fk_cnt = len(re.findall(r"CONSTRAINT `fk_", sql))
checks.append((f"SQL物理外键约束数={fk_cnt}(应为0-逻辑外键)", fk_cnt == 0))
uni_cnt = sql.count("utf8mb4_unicode_ci")
checks.append((f"SQL残留unicode_ci={uni_cnt}(应为0)", uni_cnt == 0))
gen_cnt = sql.count("utf8mb4_general_ci")
checks.append((f"SQL含general_ci引用={gen_cnt}(应>=44:库+43表)", gen_cnt >= 44))
fkidx_cnt = len(re.findall(r"KEY `idx_fk_", sql))
checks.append((f"SQL逻辑外键补充索引数={fkidx_cnt}(应为7)", fkidx_cnt == 7))
checks.append(("SQL含乐观锁version", "`version`" in sql))
checks.append(("SQL含幂等键idempotency_key", "`idempotency_key`" in sql))
checks.append(("SQL含登录日志/红包/退单演示数据",
               "INSERT INTO `staff_login_log`" in sql and "INSERT INTO `refund`" in sql and "INSERT INTO `red_packet`" in sql))

# ---------- 字典 ----------
for sec in ["第一部分 数据表基础说明", "第二部分 表间关联关系（ER）", "第三部分 字段详细说明", "附录 报表视图"]:
    checks.append((f"字典含『{sec}』", sec in dic))
tables_expected = ["sys_role","sys_permission","sys_role_permission","sys_user","staff_schedule",
"staff_login_log","store","store_payment_setting","member_category","member","member_address",
"member_bank_card","member_balance_record","member_points_record","member_recharge_record","red_packet",
"dish_category","dish_taste","dish_spec","ingredient_category","ingredient","dish","dish_ingredient_rel",
"dish_review","table_type","dining_table","queue","reservation","cart","orders","order_detail",
"order_status_log","payment_record","refund","coupon","member_coupon","dish_stock","ingredient_stock",
"stock_check_dish","stock_check_ingredient","printer","receipt_template","feedback"]
missing = [t for t in tables_expected if f"### {t} " not in dic and f"### {t} —" not in dic]
checks.append((f"字典含43张表标题，缺失={missing or '无'}", not missing))
# 表基础说明 43 行
base_rows = len(re.findall(r"^\| \d+ \| ", dic, re.M))
checks.append((f"字典表基础说明行数={base_rows}(应为43)", base_rows == 43))
# 字段行 505（第三部分字段表）
field_rows = len(re.findall(r"^\| `\w+` \| (?:BIGINT|INT|VARCHAR|DECIMAL|TINYINT|DATETIME|TEXT|CHAR|DOUBLE|SMALLINT|DATE|TIME)", dic, re.M))
checks.append((f"字典字段行数={field_rows}(应为505)", field_rows == 505))
# ER 行 62（子表|父表|子.字段=父.字段|类型|策略）
er_rows = len(re.findall(r"^\| `\w+` \| `\w+` \| `\w+\.\w+` = `\w+\.\w+` \|", dic, re.M))
checks.append((f"字典ER关系行数={er_rows}(应为62)", er_rows == 62))

# ---------- 规划书 ----------
for kw in ["产品概述","技术架构规划","数据架构规划","接口设计规范","幂等与并发控制","支付与对账","安全与合规","部署架构与高可用","V2.0 专家优化清单"]:
    checks.append((f"规划书含『{kw}』", kw in plan))

ok = True
for name, passed in checks:
    print(("PASS " if passed else "FAIL ") + name)
    ok = ok and passed
print("\n总体:", "全部通过" if ok else "存在失败项")
