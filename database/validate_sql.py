# -*- coding: utf-8 -*-
"""校验 smart_restaurant.sql：语法解析 + 结构完整性检查"""
import sqlglot
from sqlglot import exp

SQL_PATH = r"D:\TraeProject\Smart Restaurant\database\smart_restaurant.sql"

with open(SQL_PATH, encoding="utf-8") as f:
    sql = f.read()

# ---------- 1. 语法解析 ----------
errors = []
try:
    statements = sqlglot.parse(sql, read="mysql")
    print(f"[OK] 解析通过，共 {len(statements)} 条语句")
except Exception as e:
    print(f"[FAIL] 解析失败: {e}")
    raise SystemExit(1)

# ---------- 2. 提取表结构 ----------
tables = {}       # name -> set(columns)
create_order = []
for stmt in statements:
    if isinstance(stmt, exp.Create) and isinstance(stmt.this, exp.Schema):
        # CREATE TABLE 的表名在 Schema.this（Table）下
        try:
            tname = stmt.this.this.name.lower()
        except AttributeError:
            tname = str(stmt.this.name or "").lower()
        create_order.append(tname)
        cols = set()
        for c in stmt.this.expressions:
            if isinstance(c, exp.ColumnDef):
                cols.add(c.name.lower())
            elif isinstance(c, exp.PrimaryKey):
                pass
            elif isinstance(c, exp.Index):
                pass
            elif isinstance(c, exp.ForeignKey):
                # 提取外键引用列
                try:
                    for ref in c.args.get("expressions", []):
                        if isinstance(ref, exp.Column):
                            cols.add(ref.name.lower())
                except Exception:
                    pass
        tables[tname] = cols
    elif isinstance(stmt, exp.Insert):
        pass

print(f"[OK] 共 {len(tables)} 张表: {', '.join(create_order)}")

# ---------- 3. 检查外键引用 ----------
fk_issues = []
for stmt in statements:
    if isinstance(stmt, exp.Create) and isinstance(stmt.this, exp.Schema):
        try:
            tname = stmt.this.this.name.lower()
        except AttributeError:
            tname = str(stmt.this.name or "").lower()
        for c in stmt.this.expressions:
            if isinstance(c, exp.ForeignKey):
                fk_tbl = c.find(exp.Table)
                if fk_tbl:
                    ref_tbl = fk_tbl.name.lower()
                    if ref_tbl not in tables:
                        fk_issues.append(f"表 {tname} 外键引用不存在的表 {ref_tbl}")

if fk_issues:
    print("[FAIL] 外键引用问题:")
    for i in fk_issues:
        print("  -", i)
else:
    print("[OK] 外键引用的表均存在")

# ---------- 4. 检查外键列与引用列存在性（基础） ----------
col_issues = []
for stmt in statements:
    if isinstance(stmt, exp.Create) and isinstance(stmt.this, exp.Schema):
        try:
            tname = stmt.this.this.name.lower()
        except AttributeError:
            tname = str(stmt.this.name or "").lower()
        for c in stmt.this.expressions:
            if isinstance(c, exp.ForeignKey):
                # 本表引用列
                fk_cols = [e.name.lower() for e in c.args.get("expressions", [])]
                ref_table = c.find(exp.Table).name.lower()
                ref_cols = [e.name.lower() for e in c.find_all(exp.Column)]
                # 简化：检查本表引用列存在
                for col in fk_cols:
                    if col not in tables.get(tname, set()):
                        col_issues.append(f"表 {tname} 外键列 {col} 未在表中定义")

if col_issues:
    print("[FAIL] 外键列问题:")
    for i in col_issues:
        print("  -", i)
else:
    print("[OK] 外键列均已在定义表中声明")

# ---------- 5. 检查 INSERT 目标表存在 ----------
insert_tables = set()
for stmt in statements:
    if isinstance(stmt, exp.Insert):
        t = stmt.this
        if isinstance(t, exp.Schema):
            t = t.this  # INSERT INTO tbl(...) 时 this 为 Schema
        name = t.name.lower()
        insert_tables.add(name)
missing = [t for t in insert_tables if t not in tables]
if missing:
    print(f"[FAIL] INSERT 到不存在的表: {missing}")
else:
    print(f"[OK] 所有 {len(insert_tables)} 个 INSERT 目标表均存在")

print("\n校验完成")
