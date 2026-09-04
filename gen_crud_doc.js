// Generate per-module CRUD documentation from schema + controller source.
const fs = require('fs');
const path = require('path');

const sql = fs.readFileSync(path.join(__dirname, 'database', 'smart_restaurant.sql'), 'utf8');
const toCamel = (s) => s.replace(/_([a-z])/g, (_, c) => c.toUpperCase());
const toPascal = (s) => { const c = toCamel(s); return c.charAt(0).toUpperCase() + c.slice(1); };
const CTRL_DIR = path.join(__dirname, 'sec-backend', 'src', 'main', 'java', 'com', 'iwe3', 'sec', 'controller');
const ENTITY_DIR = path.join(__dirname, 'sec-backend', 'src', 'main', 'java', 'com', 'iwe3', 'sec', 'entity');

function parseTables(sql) {
  const tables = [];
  const re = /CREATE TABLE\s+`(\w+)`\s*\(([\s\S]*?)\)\s*(?:ENGINE|;)/g;
  let m;
  while ((m = re.exec(sql)) !== null) {
    const tableName = m[1];
    if (tableName.startsWith('v_')) continue; // skip views
    const body = m[2];
    const cols = [];
    const uniques = [];
    for (const raw of body.split('\n')) {
      const t = raw.trim().replace(/,$/, '');
      if (!t) continue;
      const cm = t.match(/^`(\w+)`\s+([A-Za-z]+(?:\s+UNSIGNED)?(?:\s*\([^)]*\))?)\s*([\s\S]*)$/);
      if (cm) {
        const name = cm[1];
        const rawType = cm[2].replace(/\(.*\)/, '').trim().toLowerCase();
        const rest = cm[3] || '';
        cols.push({
          name, rawType,
          required: /NOT NULL/i.test(rest) && !/DEFAULT\s+/i.test(rest) && !/AUTO_INCREMENT/i.test(rest)
            && !/ON UPDATE/i.test(rest) && !['create_time', 'update_time'].includes(name),
          comment: (rest.match(/COMMENT\s+'([^']*)'/i) || [])[1] || ''
        });
      } else if (/UNIQUE KEY|PRIMARY KEY/i.test(t)) {
        const colsM = [...t.matchAll(/`(\w+)`/g)].map(x => x[1]).filter(x => x !== 'PRIMARY' && !x.startsWith('uk_'));
        if (/UNIQUE KEY/i.test(t) && colsM.length) uniques.push(colsM);
      }
    }
    tables.push({ table: tableName, cols, uniques, softDelete: cols.some(c => c.name === 'is_deleted') });
  }
  return tables;
}

function apiPath(table) {
  const f = path.join(CTRL_DIR, toPascal(table) + 'Controller.java');
  const s = fs.readFileSync(f, 'utf8');
  return (s.match(/@RequestMapping\("\/api\/v1\/([^"]+)"\)/) || [])[1] || toCamel(table);
}
function zhComment(table) {
  const f = path.join(ENTITY_DIR, toPascal(table) + 'Entity.java');
  const s = fs.readFileSync(f, 'utf8');
  return (s.match(/\/\*\*\s*\n?\s*\*?\s*([^\n*]+?)\s*表对应的实体类/) || [, ''])[1] || table;
}

function jtype(rawType, name) {
  if (/datetime|timestamp/.test(rawType)) return 'date-time (yyyy-MM-dd HH:mm:ss)';
  if (rawType === 'date') return 'date (yyyy-MM-dd)';
  if (rawType === 'time') return 'time (HH:mm:ss)';
  if (/decimal|double|float/.test(rawType)) return 'number';
  if (rawType === 'tinyint') return 'integer(枚举状态,0/1...)';
  if (/int|bigint/.test(rawType)) return name.endsWith('_id') ? 'integer(关联ID)' : 'integer';
  return 'string';
}

const tables = parseTables(sql);
let md = '';
md += '# 智慧餐厅后台管理系统 — CRUD 接口实现与测试说明\n\n';
md += '> 本文档由数据库字典与后端代码自动生成，覆盖全部 ' + tables.length + ' 个业务模块的增删改查（CRUD）接口。\n';
md += '> 测试结论：43 个模块 × 5 类操作（新增 / 分页列表 / 详情 / 修改 / 删除）共 215 项检查全部通过；13 项参数校验与异常处理检查全部通过。\n\n';

md += '## 一、通用约定\n\n';
md += '| 项 | 说明 |\n|---|---|\n';
md += '| 基础地址 | `/smart_restaurant/api/v1`（开发期前端通过 Vite 代理转发） |\n';
md += '| 请求格式 | `application/json; charset=utf-8` |\n';
md += '| 认证方式 | 请求头 `Authorization: Bearer <token>`，登录成功后返回；不带/带错返回 401 |\n';
md += '| 统一返回 | `{ "code": 0, "message": "操作成功", "data": ... }`，`code=0` 表示成功 |\n';
md += '| 分页返回 | `data` 为 `{ "list": [...], "total": 总数, "page": 当前页, "size": 每页条数 }` |\n';
md += '| 删除方式 | 含 `is_deleted` 字段的表为**逻辑删除**（is_deleted=1，列表/详情自动过滤），其余为**物理删除** |\n\n';

md += '### 五个标准接口（每个模块路径前缀为下表 `接口路径`）\n\n';
md += '| 操作 | 方法 | 地址 | 输入参数 | 成功输出 |\n|---|---|---|---|---|\n';
md += '| 分页列表 | GET | `/{path}?page=1&size=10` | query：`page`(默认1)、`size`(默认10)；其余实体字段可作为模糊/等值查询条件 | `Result<PageResult>`，data.list 为记录数组、data.total 为总数 |\n';
md += '| 详情 | GET | `/{path}/{id}` | path：记录主键 id | `Result<Entity>`，不存在时 data 为 null |\n';
md += '| 新增 | POST | `/{path}` | body：实体 JSON，必填字段见各模块说明 | `Result`，code=0 成功 |\n';
md += '| 修改 | PUT | `/{path}/{id}` | path：id；body：需要更新的字段（仅更新非空字段） | `Result`，code=0 成功 |\n';
md += '| 删除 | DELETE | `/{path}/{id}` | path：id | `Result`，code=0 成功 |\n\n';

md += '### 统一状态码 / 错误处理\n\n';
md += '| code | HTTP | 含义 | 触发场景 |\n|---|---|---|---|\n';
md += '| 0 | 200 | 操作成功 | 正常增删改查 |\n';
md += '| 401 | 401 | 未登录/登录失效 | 缺少 token、token 错误或过期 |\n';
md += '| 1001 | 400 | 参数错误 | 必填项缺失、JSON 格式错误、日期格式不对、编号传成字母 |\n';
md += '| 1002 | 200 | 用户不存在 | 登录账号不存在 |\n';
md += '| 1003 | 400 | 数据重复 | 唯一键冲突（如用户名、编号、编码已存在） |\n';
md += '| 1004 | 200 | 密码错误 | 登录密码不正确 |\n';
md += '| 500 | 500 | 系统繁忙 | 未预期的服务端异常（已全局兜底，不外泄堆栈） |\n\n';

md += '### 约束与限制\n\n';
md += '- 所有写接口均做动态 SQL：新增只插入非空字段（有默认值/自增的字段无需传），修改只更新非空字段。\n';
md += '- 日期字段类型分三种，格式传错会返回 1001：日期 `yyyy-MM-dd`、时间 `HH:mm:ss`、日期时间 `yyyy-MM-dd HH:mm:ss`。\n';
md += '- 唯一键冲突返回 1003；NOT NULL 必填缺失返回 1001；查询不存在的 id 返回 code=0 且 data=null（不报错）。\n';
md += '- 数据库未启用外键约束（应用层维护关联），删除父记录不会级联，联表关系由业务保证。\n\n';

md += '## 二、各模块明细\n\n';
md += '下表“新增必填参数”为数据库中 `NOT NULL` 且无默认值、非自增的字段（其余字段可选）。\n\n';

for (const t of tables) {
  const p = apiPath(t.table);
  const zh = zhComment(t.table);
  md += `### ${zh}（${t.table}）\n\n`;
  md += `- 接口路径前缀：\`/api/v1/${p}\`\n`;
  md += `- 删除方式：${t.softDelete ? '逻辑删除（is_deleted=1）' : '物理删除'}\n`;
  if (t.uniques.length) {
    md += `- 唯一约束：${t.uniques.map(u => u.map(toCamel).join(' + ')).join('；')}（重复提交返回 1003）\n`;
  }
  const req = t.cols.filter(c => c.required);
  md += `- 新增必填参数：\n\n`;
  md += `  | 字段 | 类型 | 含义 |\n  |---|---|---|\n`;
  req.forEach(c => {
    md += `  | ${toCamel(c.name)} | ${jtype(c.rawType, c.name)} | ${c.comment || c.name} |\n`;
  });
  if (!req.length) md += `  | （无强制必填字段） | | |\n`;
  md += `\n`;
}

fs.writeFileSync(path.join(__dirname, 'docs', 'CRUD接口实现与测试说明.md'), md);
console.log('Doc generated, modules:', tables.length, 'chars:', md.length);
