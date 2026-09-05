// 通过 Vite 代理(5173) 模拟浏览器加载各页面时发出的请求
const B = 'http://localhost:5173/api/v1'
let fail = 0
async function login() {
  const r = await fetch(B + '/auth/login', {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: '13800000001', password: '123456' })
  }).then((x) => x.json())
  return r.data.token
}
async function req(token, path) {
  const res = await fetch(B + path, { headers: { Authorization: 'Bearer ' + token } })
  const body = await res.text()
  let code = null
  try { code = JSON.parse(body).code } catch {}
  if (res.status !== 200 || (code !== null && code !== 0)) {
    fail++
    console.log(`FAIL status=${res.status} code=${code} ${path} -> ${body.slice(0, 150)}`)
  }
}
;(async () => {
  const token = await login()
  console.log('登录:', !!token)

  // 各页面：列表请求 + 下拉选项请求（与浏览器 loadSearchOptions 一致）
  const pages = {
    dishes: ['/dishes?page=1&size=10', '/stores?page=1&size=1000', '/dishCategories?page=1&size=1000'],
    orders: ['/orders?page=1&size=10', '/stores?page=1&size=1000', '/members?page=1&size=1000', '/diningTables?page=1&size=1000'],
    sysUsers: ['/sysUsers?page=1&size=10', '/stores?page=1&size=1000', '/sysRoles?page=1&size=1000'],
    members: ['/members?page=1&size=10', '/memberCategories?page=1&size=1000', '/stores?page=1&size=1000'],
    coupons: ['/coupons?page=1&size=10', '/stores?page=1&size=1000', '/dishCategories?page=1&size=1000'],
    reservations: ['/reservations?page=1&size=10', '/stores?page=1&size=1000', '/members?page=1&size=1000', '/diningTables?page=1&size=1000'],
    refunds: ['/refunds?page=1&size=10', '/stores?page=1&size=1000'],
    ingredients: ['/ingredients?page=1&size=10', '/stores?page=1&size=1000', '/ingredientCategories?page=1&size=1000']
  }
  for (const [p, paths] of Object.entries(pages)) {
    for (const path of paths) await req(token, path)
    console.log('页面', p, '请求完成')
  }

  // 带搜索条件的请求（通过代理）
  await req(token, '/dishes?page=1&size=10&searchKeyword=%E5%8F%AF%E4%B9%90&status=1')
  await req(token, '/orders?page=1&size=10&orderStatus=1')
  await req(token, '/sysUsers?page=1&size=10&searchKeyword=%E5%BC%A0')
  console.log(fail === 0 ? '\n全部通过：代理链路上没有 500/非0 响应' : `\n共 ${fail} 个失败`)
  process.exit(fail ? 1 : 0)
})().catch((e) => { console.error(e); process.exit(2) })
