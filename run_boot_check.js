// 启动后综合功能验证
const B = 'http://localhost:8080/smart_restaurant'
let pass = 0, fail = 0
const ok = (n, c, e = '') => { c ? pass++ : fail++; console.log((c ? 'PASS ' : 'FAIL ') + n + (e ? ' ' + e : '')) }

;(async () => {
  // 1. 登录（走完整 Controller-Service-Mapper-MySQL 链路）
  const login = await fetch(B + '/api/v1/auth/login', {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: '13800000001', password: '123456' })
  }).then(r => r.json())
  ok('登录接口(鉴权链路)', login.code === 0)
  const token = login.data?.token

  // 2. 43 个模块列表（验证全部 43 个 Mapper.xml 加载与 SQL 可执行）
  const mods = ['stores','storePaymentSettings','diningTables','queues','reservations',
    'sysUsers','sysRoles','sysPermissions','sysRolePermissions','staffSchedules','staffLoginLogs',
    'dishes','dishCategories','dishTastes','dishSpecs','dishReviews','ingredients','ingredientCategories',
    'dishIngredientRels','orders','orderDetails','orderStatusLogs','paymentRecords','refunds','carts',
    'members','memberCategories','memberAddresses','memberBankCards','memberBalanceRecords','memberPointsRecords',
    'memberRechargeRecords','memberCoupons','coupons','redPackets','dishStocks','ingredientStocks',
    'stockCheckDishes','stockCheckIngredients','printers','receiptTemplates','feedbacks','tableTypes']
  let okCnt = 0, failMods = []
  for (const m of mods) {
    try {
      const r = await fetch(`${B}/api/v1/${m}?page=1&size=1`, { headers: { Authorization: 'Bearer ' + token } }).then(x => x.json())
      if (r.code === 0) okCnt++; else failMods.push(m + ':' + r.code)
    } catch (e) { failMods.push(m + ':ERR') }
  }
  ok('43模块列表全部可用', okCnt === mods.length, `${okCnt}/${mods.length}` + (failMods.length ? ' 失败:' + failMods.join(',') : ''))

  // 3. Swagger 文档
  const sw = await fetch(B + '/swagger-ui/index.html').then(r => r.status).catch(() => 0)
  const apiDocs = await fetch(B + '/v3/api-docs').then(r => r.json()).catch(() => null)
  ok('Swagger UI 可访问', sw === 200, 'HTTP ' + sw)
  ok('OpenAPI 文档生成', !!apiDocs && Object.keys(apiDocs.paths || {}).length > 40, Object.keys(apiDocs?.paths || {}).length + ' 个接口')

  // 4. Druid 监控页
  const druid = await fetch(B + '/druid/index.html').then(r => r.status).catch(() => 0)
  ok('Druid 监控可访问', druid === 200, 'HTTP ' + druid)

  console.log(`\n结果: ${pass} 通过 / ${fail} 失败`)
  process.exit(fail ? 1 : 0)
})().catch(e => { console.error(e); process.exit(2) })
