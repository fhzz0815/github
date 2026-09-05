/**
 * 批量搜索能力增强生成器
 * 后端：每个 Mapper.xml 的 selectList 升级为 关键字模糊 + 字段精确/模糊 + 创建时间范围 的动态SQL；
 *       每个实体类追加 keyword/beginTime/endTime 三个非数据库查询字段。
 * 前端：每个模块页面替换搜索区为“按业务字段定制”的表单（关键字/状态类型下拉/外键下拉/时间范围），
 *       并修复翻页丢失搜索条件、补齐 orders 骨架页。
 */
const fs = require('fs')
const path = require('path')

const ROOT = __dirname
const BE = path.join(ROOT, 'sec-backend')
const FE = path.join(ROOT, 'sec-frontend', 'src')
const MAPPER_DIR = path.join(BE, 'src', 'main', 'resources', 'mapper')
const ENTITY_DIR = path.join(BE, 'src', 'main', 'java', 'com', 'iwe3', 'sec', 'entity')
const VIEWS_DIR = path.join(FE, 'views')

const meta = JSON.parse(fs.readFileSync(path.join(ROOT, 'schema_meta.json'), 'utf8').replace(/^﻿/, ''))
const metaByTable = {}
for (const m of meta) {
  if (!metaByTable[m.t]) metaByTable[m.t] = []
  metaByTable[m.t].push(m)
}

// ---------- 工具 ----------
const camel = (s) => s.replace(/_([a-z0-9])/g, (_, c) => c.toUpperCase())
const cap = (s) => s.charAt(0).toUpperCase() + s.slice(1)
// 视图文件夹 -> 实体简单类名
function singular(folder) {
  if (/ies$/.test(folder)) return folder.slice(0, -3) + 'y'
  if (/(ses|shes|ches|xes)$/.test(folder)) return folder.slice(0, -2)
  if (/s$/.test(folder)) return folder.slice(0, -1)
  return folder
}
// 注释里解析枚举，如 "订单状态 1待支付 2已支付" -> [{value:1,label:'待支付'}]
function parseEnum(comment) {
  if (!comment) return null
  const start = comment.search(/\d\s*[一-龥A-Za-z]/)
  if (start < 0) return null
  const tail = comment.slice(start)
  const re = /(\d+)\s*([一-龥A-Za-z][一-龥A-Za-z/]*)/g
  const out = []
  const seen = new Set()
  let m
  while ((m = re.exec(tail))) {
    const v = Number(m[1])
    if (!seen.has(v)) {
      seen.add(v)
      out.push({ value: v, label: m[2] })
    }
  }
  return out.length >= 2 ? out : null
}
// 注释里取字段中文名（去掉枚举部分）
function labelOf(cm) {
  if (!cm) return ''
  const s = cm.split(/\d[、.\s]*[一-龥A-Za-z]/)[0]
  return (s || cm).replace(/[（(].*$/, '').trim()
}

// ---------- 解析 Mapper.xml ----------
function parseMapper(file) {
  const xml = fs.readFileSync(file, 'utf8')
  const typeM = xml.match(/<resultMap[^>]*type="([^"]+)"/)
  const fqcn = typeM[1]
  const entitySimple = fqcn.split('.').pop()
  const fromM = xml.match(/FROM\s+`?(\w+)`?/i)
  const table = fromM[1]
  const col2prop = {}
  const prop2col = {}
  for (const mm of xml.matchAll(/<(id|result)\s+column="([^"]+)"\s+property="([^"]+)"/g)) {
    col2prop[mm[2]] = mm[3]
    prop2col[mm[3]] = mm[2]
  }
  const hasSoftDelete = 'is_deleted' in col2prop
  return { file, xml, fqcn, entitySimple, table, col2prop, prop2col, hasSoftDelete }
}

// 外键下拉规则
const FK_RULES = [
  { col: 'store_id', api: '@/api/store', var: 'stores', imp: 'storeApi', label: 'storeName', labelCn: '门店' },
  { col: 'register_store_id', api: '@/api/store', var: 'stores', imp: 'storeApi', label: 'storeName', labelCn: '注册门店' },
  { col: 'role_id', api: '@/api/sysRole', var: 'roles', imp: 'sysRoleApi', label: 'roleName', labelCn: '角色' },
  { col: 'member_id', api: '@/api/member', var: 'members', imp: 'memberApi', label: '__member__', labelCn: '会员' },
  { col: 'dish_id', api: '@/api/dish', var: 'dishes', imp: 'dishApi', label: 'dishName', labelCn: '菜品' },
  { col: 'ingredient_id', api: '@/api/ingredient', var: 'ingredients', imp: 'ingredientApi', label: 'ingredientName', labelCn: '原料' },
  { col: 'coupon_id', api: '@/api/coupon', var: 'coupons', imp: 'couponApi', label: 'couponName', labelCn: '优惠券' },
  { col: 'printer_id', api: '@/api/printer', var: 'printers', imp: 'printerApi', label: 'printerName', labelCn: '打印机' },
  { col: 'table_id', api: '@/api/diningTable', var: 'diningTables', imp: 'diningTableApi', label: 'tableName', labelCn: '台桌' },
  { col: 'member_category_id', api: '@/api/memberCategory', var: 'memberCategories', imp: 'memberCategoryApi', label: 'categoryName', labelCn: '会员类别' },
  { col: 'scope_category_id', api: '@/api/dishCategory', var: 'dishCategories', imp: 'dishCategoryApi', label: 'categoryName', labelCn: '适用分类' }
]
function fkFor(table, col) {
  const hit = FK_RULES.find((r) => r.col === col)
  if (hit) return hit
  if (col === 'category_id') {
    if (['dish', 'dish_stock', 'dish_spec', 'dish_taste', 'dish_review', 'stock_check_dish'].includes(table))
      return { col, api: '@/api/dishCategory', var: 'dishCategories', imp: 'dishCategoryApi', label: 'categoryName', labelCn: '菜品分类' }
    if (['ingredient', 'ingredient_stock', 'stock_check_ingredient'].includes(table))
      return { col, api: '@/api/ingredientCategory', var: 'ingredientCategories', imp: 'ingredientCategoryApi', label: 'categoryName', labelCn: '原料类别' }
  }
  return null
}

// 计算某张表的搜索字段配置
function buildSearchSpec(info) {
  const cols = metaByTable[info.table] || []
  const stringCols = []
  const numCols = []
  const enums = []
  const fks = []
  const keywordCols = []
  for (const c of cols) {
    const prop = info.col2prop[c.c]
    if (!prop) continue // 实体未映射的列跳过
    if (['id', 'is_deleted', 'create_time', 'update_time'].includes(c.c)) continue
    const isString = /char|text/.test(c.d)
    const isInt = /tinyint|smallint|mediumint|^int$|bigint/.test(c.d)
    if (isString) stringCols.push({ col: c.c, prop, cm: c.cm })
    if (isInt) {
      numCols.push({ col: c.c, prop })
      const fk = fkFor(info.table, c.c)
      if (fk) fks.push({ ...fk, prop })
      else {
        const opts = parseEnum(c.cm)
        if (opts) enums.push({ col: c.c, prop, label: labelOf(c.cm) || prop, opts })
      }
    }
    // 关键字字段：名称/编号/手机号/账号/标题/邮箱/内容/备注/地址 等
    if (isString && /(_name|_no|phone|username|title|email|content|remark|description|address|reason|note)$/.test(c.c)) {
      keywordCols.push({ col: c.c, prop, cm: c.cm })
    }
  }
  // 关键字最多取4个，优先名称/编号/手机
  keywordCols.sort((a, b) => {
    const score = (x) => (/_name$/.test(x.col) ? 0 : /_no$/.test(x.col) ? 1 : /phone|username|email/.test(x.col) ? 2 : 3)
    return score(a) - score(b)
  })
  const kw = keywordCols.slice(0, 4)
  return { stringCols, numCols, enums: enums.slice(0, 4), fks: fks.slice(0, 3), keywordCols: kw, hasCreateTime: 'create_time' in info.col2prop }
}

// ================== 后端：实体类追加查询字段 ==================
function patchEntity(fqcn) {
  const rel = fqcn.replace(/^com\.iwe3\.sec\./, '').replace(/\./g, '/')
  const file = path.join(BE, 'src', 'main', 'java', 'com', 'iwe3', 'sec', rel + '.java')
  if (!fs.existsSync(file)) return 'entity-missing'
  let src = fs.readFileSync(file, 'utf8')
  if (src.includes('private String keyword;')) return 'entity-skip'
  const inject = `
    // ===== 以下三个字段不是数据库列，只用于列表搜索：关键字 / 开始时间 / 结束时间 =====
    /** 关键字（按名称、编号、手机号等模糊搜索） */
    private String searchKeyword;
    /** 查询开始时间，格式 yyyy-MM-dd */
    private String searchBeginTime;
    /** 查询结束时间，格式 yyyy-MM-dd */
    private String searchEndTime;
}`
  const idx = src.lastIndexOf('}')
  src = src.slice(0, idx) + inject + src.slice(idx + 1)
  fs.writeFileSync(file, src, 'utf8')
  return 'entity-ok'
}

// ================== 后端：重写 selectList ==================
function genSelectList(info, spec) {
  const L = []
  L.push('    <select id="selectList" resultMap="BaseResultMap">')
  L.push('        SELECT <include refid="Base_Column_List"/>')
  L.push(`        FROM \`${info.table}\``)
  L.push('        <where>')
  if (info.hasSoftDelete) L.push('            is_deleted = 0')
  if (spec.keywordCols.length) {
    L.push('            <if test="query.searchKeyword != null and query.searchKeyword != \'\'">')
    L.push('                AND (')
    spec.keywordCols.forEach((k, i) => {
      const prefix = i === 0 ? '' : '                OR '
      L.push(`${prefix}${k.col} LIKE CONCAT('%', #{query.searchKeyword}, '%')`)
    })
    L.push('                )')
    L.push('            </if>')
  }
  for (const c of spec.stringCols) {
    L.push(`            <if test="query.${c.prop} != null and query.${c.prop} != ''">AND ${c.col} LIKE CONCAT('%', #{query.${c.prop}}, '%')</if>`)
  }
  for (const c of spec.numCols) {
    L.push(`            <if test="query.${c.prop} != null">AND ${c.col} = #{query.${c.prop}}</if>`)
  }
  if (spec.hasCreateTime) {
    L.push('            <if test="query.searchBeginTime != null and query.searchBeginTime != \'\'">AND create_time &gt;= CONCAT(#{query.searchBeginTime}, \' 00:00:00\')</if>')
    L.push('            <if test="query.searchEndTime != null and query.searchEndTime != \'\'">AND create_time &lt;= CONCAT(#{query.searchEndTime}, \' 23:59:59\')</if>')
  }
  L.push('        </where>')
  L.push('        ORDER BY id DESC')
  L.push('    </select>')
  return L.join('\n')
}

function patchMapper(info, spec) {
  const start = info.xml.indexOf('<select id="selectList"')
  const end = info.xml.indexOf('</select>', start) + '</select>'.length
  const newBlock = genSelectList(info, spec)
  const out = info.xml.slice(0, start) + newBlock + info.xml.slice(end)
  fs.writeFileSync(info.file, out, 'utf8')
}

// ================== 前端：搜索区 ==================
function feFieldInit(spec) {
  const fields = { keyword: "''" }
  for (const e of spec.enums) fields[e.prop] = 'null'
  for (const f of spec.fks) fields[f.prop] = 'null'
  fields.dateRange = 'null'
  return fields
}

function genSearchCard(spec) {
  const L = []
  L.push('    <!-- 搜索区域 -->')
  L.push('    <el-card class="search-card" shadow="never">')
  L.push('      <el-form :inline="true" :model="queryForm">')
  if (spec.keywordCols.length) {
    const ph = spec.keywordCols.map((k) => labelOf(k.cm) || k.col).join('/')
    L.push('        <el-form-item label="关键字">')
    L.push(`          <el-input v-model="queryForm.keyword" placeholder="请输入${ph}" clearable style="width: 220px" @keyup.enter="handleSearch" />`)
    L.push('        </el-form-item>')
  }
  for (const e of spec.enums) {
    L.push(`        <el-form-item label="${e.label}">`)
    L.push(`          <el-select v-model="queryForm.${e.prop}" clearable placeholder="全部" style="width: 130px">`)
    L.push(`            <el-option v-for="o in enumOptions.${e.prop}" :key="o.value" :label="o.label" :value="o.value" />`)
    L.push('          </el-select>')
    L.push('        </el-form-item>')
  }
  for (const f of spec.fks) {
    L.push(`        <el-form-item label="${f.labelCn}">`)
    L.push(`          <el-select v-model="queryForm.${f.prop}" clearable filterable placeholder="全部" style="width: 150px">`)
    if (f.label === '__member__') {
      L.push(`            <el-option v-for="o in searchOptions.${f.var}" :key="o.id" :label="o.realName || o.nickname || o.phone" :value="o.id" />`)
    } else {
      L.push(`            <el-option v-for="o in searchOptions.${f.var}" :key="o.id" :label="o.${f.label}" :value="o.id" />`)
    }
    L.push('          </el-select>')
    L.push('        </el-form-item>')
  }
  L.push('        <el-form-item label="创建时间">')
  L.push('          <el-date-picker v-model="queryForm.dateRange" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 260px" />')
  L.push('        </el-form-item>')
  L.push('        <el-form-item>')
  L.push('          <el-button type="primary" @click="handleSearch">搜索</el-button>')
  L.push('          <el-button @click="handleReset">重置</el-button>')
  L.push('        </el-form-item>')
  L.push('      </el-form>')
  L.push('    </el-card>')
  return L.join('\n')
}

function genScriptAddon(spec, storeVar) {
  const fkUsed = [...new Map(spec.fks.map((f) => [f.var, f])).values()]
  const impLines = fkUsed.map((f) => `import ${f.imp} from '${f.api}'`)
  const stateLines = fkUsed.map((f) => `  ${f.var}: []`)
  const loadLines = fkUsed.map((f) => `${f.imp}.list({ page: 1, size: 1000 }).then((r) => { searchOptions.${f.var} = r.data?.list || [] }).catch(() => {})`)
  const enumLines = []
  for (const e of spec.enums) {
    enumLines.push(`  ${e.prop}: [${e.opts.map((o) => `{ value: ${o.value}, label: '${o.label}' }`).join(', ')}]`)
  }
  const fieldInit = feFieldInit(spec)
  const queryFields = Object.keys(fieldInit).map((k) => `  ${k}: ${fieldInit[k]}`).join(',\n')
  return `${impLines.join('\n')}

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
${enumLines.join(',\n')}
}
const searchOptions = reactive({
${stateLines.join(',\n')}
})
const loadSearchOptions = () => {
  ${loadLines.join('\n  ')}
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
${queryFields}
})
// 把搜索表单整理成接口参数（空值剔除、时间范围拆分）
const buildParams = () => {
  const params = { ...queryForm }
  params.searchKeyword = queryForm.keyword
  delete params.keyword
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.searchBeginTime = queryForm.dateRange[0]
    params.searchEndTime = queryForm.dateRange[1]
  }
  delete params.dateRange
  Object.keys(params).forEach((k) => {
    if (params[k] === '' || params[k] === null || params[k] === undefined) delete params[k]
  })
  return params
}
const handleSearch = () => {
  ${storeVar}.page = 1
  ${storeVar}.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  ${storeVar}.page = 1
  ${storeVar}.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  ${storeVar}.fetchList(buildParams())
}
`
}

// 对普通页面做搜索区改造
function patchFrontendPage(folder, spec) {
  const dir = path.join(VIEWS_DIR, folder)
  const file = path.join(dir, 'index.vue')
  if (!fs.existsSync(file)) return 'view-missing'
  let src = fs.readFileSync(file, 'utf8')

  // 解析 api / store 引用
  const storeImpM = src.match(/import\s*\{\s*(\w+)\s*\}\s*from\s*'(@\/stores\/[^']+)'/)
  const storeInstM = src.match(/const\s+(\w+)\s*=\s*\w+\(\)/)
  const storeVar = storeInstM ? storeInstM[1] : folder.replace(/s$/, '') + 'Store'

  // 1) 替换搜索卡片
  const cardStart = src.indexOf('<!-- 搜索区域 -->')
  const cardEndTag = src.indexOf('</el-card>', cardStart)
  if (cardStart < 0 || cardEndTag < 0) return 'search-card-not-found'
  const cardEnd = cardEndTag + '</el-card>'.length
  src = src.slice(0, cardStart) + genSearchCard(spec) + src.slice(cardEnd)

  // 2) 删除旧的 queryForm / handleSearch / handleReset
  src = src.replace(/const queryForm = reactive\(\{[^}]*\}\)\s*\n/, '')
  src = src.replace(/const handleSearch = [\s\S]*?\n}\n/, '')
  src = src.replace(/const handleReset = [\s\S]*?\n}\n/, '')

  // 3) 注入新脚本块（放在 store 实例化之后）
  const addon = genScriptAddon(spec, storeVar)
  const anchor = new RegExp(`const\\s+${storeVar.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')}\\s*=`)
  const anchorM = src.match(anchor)
  if (anchorM) {
    const lineEnd = src.indexOf('\n', src.indexOf(anchorM[0]))
    src = src.slice(0, lineEnd + 1) + '\n' + addon + src.slice(lineEnd + 1)
  }

  // 4) 翻页事件保留搜索条件
  src = src.replace(/@size-change="[^"]*fetchList"/g, '@size-change="onPageChange"')
  src = src.replace(/@current-change="[^"]*fetchList"/g, '@current-change="onPageChange"')

  fs.writeFileSync(file, src, 'utf8')
  return 'view-ok'
}

// ================== orders 骨架页完整重建 ==================
function buildOrdersFullPage(info, spec) {
  const cols = metaByTable['orders']
  const skipForm = new Set(['id', 'create_time', 'update_time', 'is_deleted', 'version'])
  const tableCols = ['id', 'order_no', 'store_id', 'member_id', 'order_type', 'person_count', 'order_status', 'pay_status', 'take_type', 'payable_amount', 'actual_amount', 'source', 'order_time', 'create_time']
  const colMeta = {}
  for (const c of cols) colMeta[c.c] = c

  const tableColLines = ['        <el-table-column type="index" label="序号" width="60" align="center" />']
  for (const tc of tableCols) {
    const c = colMeta[tc]
    if (!c) continue
    const prop = camel(tc)
    const label = labelOf(c.cm) || prop
    const enumSpec = spec.enums.find((e) => e.col === tc)
    if (enumSpec) {
      tableColLines.push(`        <el-table-column prop="${prop}" label="${label}" width="110">
          <template #default="{ row }">
            <el-tag :type="row.${prop} === 7 ? 'success' : row.${prop} === 8 || row.${prop} === 9 ? 'danger' : 'info'" size="small">
              {{ (enumOptions.${prop}.find((i) => i.value === row.${prop}) || {}).label || row.${prop} }}
            </el-tag>
          </template>
        </el-table-column>`)
    } else {
      tableColLines.push(`        <el-table-column prop="${prop}" label="${label}" width="${tc === 'order_no' ? 160 : 110}" show-overflow-tooltip />`)
    }
  }

  const formLines = []
  for (const c of cols) {
    if (skipForm.has(c.c)) continue
    const prop = camel(c.c)
    const label = labelOf(c.cm) || prop
    const enumSpec = spec.enums.find((e) => e.col === c.c)
    const isDateTime = c.d === 'datetime'
    const isNumber = /decimal|double|float|int/.test(c.d)
    const isLongText = /remark|reason|snapshot|address/.test(c.c)
    if (enumSpec) {
      formLines.push(`        <el-form-item label="${label}">
          <el-select v-model="formData.${prop}" placeholder="请选择${label}" style="width:100%">
            <el-option v-for="o in enumOptions.${prop}" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>`)
    } else if (isDateTime) {
      formLines.push(`        <el-form-item label="${label}">
          <el-date-picker v-model="formData.${prop}" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择${label}" style="width:100%" />
        </el-form-item>`)
    } else if (isNumber) {
      formLines.push(`        <el-form-item label="${label}">
          <el-input-number v-model="formData.${prop}" style="width:100%" />
        </el-form-item>`)
    } else {
      formLines.push(`        <el-form-item label="${label}">
          <el-input v-model="formData.${prop}" ${isLongText ? 'type="textarea" :rows="2"' : ''} placeholder="请输入${label}" />
        </el-form-item>`)
    }
  }
  const formDataFields = cols.filter((c) => !skipForm.has(c.c)).map((c) => `  ${camel(c.c)}: ${/int|decimal|double|float/.test(c.d) ? 'null' : "''"}`).join(',\n')

  const searchCard = genSearchCard(spec)
  const fkUsed = [...new Map(spec.fks.map((f) => [f.var, f])).values()]
  const impLines = fkUsed.map((f) => `import ${f.imp} from '${f.api}'`)
  const stateLines = fkUsed.map((f) => `  ${f.var}: []`)
  const loadLines = fkUsed.map((f) => `${f.imp}.list({ page: 1, size: 1000 }).then((r) => { searchOptions.${f.var} = r.data?.list || [] }).catch(() => {})`)
  const enumLines = spec.enums.map((e) => `  ${e.prop}: [${e.opts.map((o) => `{ value: ${o.value}, label: '${o.label}' }`).join(', ')}]`)
  const fieldInit = feFieldInit(spec)
  const queryFields = Object.keys(fieldInit).map((k) => `  ${k}: ${fieldInit[k]}`).join(',\n')

  return `<template>
  <div class="orders-page">
${searchCard}

    <!-- 操作按钮 -->
    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>

      <el-table :data="ordersStore.list" v-loading="ordersStore.loading" border stripe style="width: 100%">
${tableColLines.join('\n')}
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="ordersStore.page"
          v-model:page-size="ordersStore.size"
          :total="ordersStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="onPageChange"
          @current-change="onPageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="110px">
${formLines.join('\n')}
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useOrdersStore } from '@/stores/orders'
import ordersApi from '@/api/orders'
${impLines.join('\n')}

const ordersStore = useOrdersStore()

const enumOptions = {
${enumLines.join(',\n')}
}
const searchOptions = reactive({
${stateLines.join(',\n')}
})
const loadSearchOptions = () => {
  ${loadLines.join('\n  ')}
}
loadSearchOptions()

const queryForm = reactive({
${queryFields}
})
const buildParams = () => {
  const params = { ...queryForm }
  params.searchKeyword = queryForm.keyword
  delete params.keyword
  if (queryForm.dateRange && queryForm.dateRange.length === 2) {
    params.searchBeginTime = queryForm.dateRange[0]
    params.searchEndTime = queryForm.dateRange[1]
  }
  delete params.dateRange
  Object.keys(params).forEach((k) => {
    if (params[k] === '' || params[k] === null || params[k] === undefined) delete params[k]
  })
  return params
}
const handleSearch = () => {
  ordersStore.page = 1
  ordersStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  ordersStore.page = 1
  ordersStore.fetchList()
}
const onPageChange = () => {
  ordersStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增订单')
const formData = reactive({
${formDataFields}
})

const resetForm = () => {
${cols.filter((c) => !skipForm.has(c.c)).map((c) => `  formData.${camel(c.c)} = ${/int|decimal|double|float/.test(c.d) ? 'null' : "''"}`).join('\n')}
}
const handleAdd = () => {
  dialogTitle.value = '新增订单'
  resetForm()
  dialogVisible.value = true
}
const handleEdit = (row) => {
  dialogTitle.value = '编辑订单'
  Object.assign(formData, row)
  dialogVisible.value = true
}
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条订单吗？', '提示', { type: 'warning' })
    .then(async () => {
      await ordersApi.delete(row.id)
      ElMessage.success('删除成功')
      ordersStore.fetchList(buildParams())
    })
    .catch(() => {})
}
const handleRefresh = () => {
  ordersStore.fetchList(buildParams())
}
const handleSubmit = async () => {
  if (formData.id) {
    await ordersApi.update(formData.id, formData)
  } else {
    await ordersApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  ordersStore.fetchList(buildParams())
}

onMounted(() => {
  ordersStore.fetchList()
})
</script>

<style scoped>
.orders-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
`
}

// ================== 主流程 ==================
function main() {
  const MODE = process.env.GEN_MODE || 'all' // 'mappers' 时只重生成 Mapper.xml
  const mapperFiles = fs.readdirSync(MAPPER_DIR).filter((f) => f.endsWith('.xml'))
  const viewFolders = fs.readdirSync(VIEWS_DIR).filter((f) =>
    fs.existsSync(path.join(VIEWS_DIR, f, 'index.vue')) && !['login', 'dashboard', 'profile', 'error'].includes(f)
  )
  const entityToFolder = {}
  for (const folder of viewFolders) {
    entityToFolder[cap(singular(folder)) + 'Entity'] = folder
    // orders 等本身以 s 结尾的实体（OrdersEntity）做兜底
    entityToFolder[cap(folder) + 'Entity'] = folder
  }

  const report = []
  for (const f of mapperFiles) {
    const info = parseMapper(path.join(MAPPER_DIR, f))
    if (!metaByTable[info.table]) { report.push(`跳过(非业务表/视图): ${f}`); continue }
    const spec = buildSearchSpec(info)
    const eRes = MODE === 'mappers' ? 'skip' : patchEntity(info.fqcn)
    patchMapper(info, spec)
    const folder = entityToFolder[info.entitySimple]
    let vRes = 'no-view'
    if (MODE === 'mappers') {
      vRes = 'skip'
    } else if (folder === 'orders') {
      fs.writeFileSync(path.join(VIEWS_DIR, 'orders', 'index.vue'), buildOrdersFullPage(info, spec), 'utf8')
      vRes = 'orders-rebuilt'
    } else if (folder) {
      vRes = patchFrontendPage(folder, spec)
    }
    report.push(`${info.table} -> 实体:${eRes} XML:selectList已升级 视图:${folder || '?'}:${vRes} (关键字${spec.keywordCols.length} 枚举${spec.enums.length} 外键${spec.fks.length})`)
  }
  console.log(report.join('\n'))
  console.log(`\n共处理 ${mapperFiles.length} 个 Mapper`)
}
main()
