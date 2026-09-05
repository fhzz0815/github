// 密码泄露修复验证
const B = 'http://localhost:8080/smart_restaurant/api/v1'
let pass = 0, fail = 0
const ok = (n, c, e = '') => { c ? pass++ : fail++; console.log((c ? 'PASS ' : 'FAIL ') + n + ' ' + e) }

;(async () => {
  // 1. 登录正常
  const login = await fetch(B + '/auth/login', {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: '13800000001', password: '123456' })
  }).then(r => r.json())
  ok('登录正常', login.code === 0)
  const token = login.data.token

  // 2. 列表不返回密码
  const list = await fetch(B + '/sysUsers?page=1&size=50', { headers: { Authorization: 'Bearer ' + token } }).then(r => r.json())
  const leak = (list.data.list || []).filter(u => u.password)
  ok('员工列表不含密码字段', leak.length === 0, `共${(list.data.list || []).length}条, 带密码${leak.length}条`)

  // 3. 详情不返回密码
  const detail = await fetch(B + '/sysUsers/1', { headers: { Authorization: 'Bearer ' + token } }).then(r => r.json())
  ok('员工详情不含密码', !detail.data?.password)

  // 4. 修改密码流程仍正常（改回同值）
  const upd = await fetch(B + '/sysUsers/1', {
    method: 'PUT', headers: { Authorization: 'Bearer ' + token, 'Content-Type': 'application/json' },
    body: JSON.stringify({ password: '123456' })
  }).then(r => r.json())
  ok('修改密码接口正常', upd.code === 0)
  const relogin = await fetch(B + '/auth/login', {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: '13800000001', password: '123456' })
  }).then(r => r.json())
  ok('改密后用123456仍可登录', relogin.code === 0)

  console.log(`\n结果: ${pass} 通过 / ${fail} 失败`)
  process.exit(fail ? 1 : 0)
})().catch(e => { console.error(e); process.exit(2) })
