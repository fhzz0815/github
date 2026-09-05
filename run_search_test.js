// 搜索功能实测：关键字模糊 / 枚举精确 / 外键过滤 / 时间范围 / 组合条件
const B = 'http://localhost:8080/smart_restaurant/api/v1'
let pass = 0, fail = 0
const ok = (name, cond, extra = '') => {
  if (cond) { pass++; console.log(`PASS  ${name} ${extra}`) }
  else { fail++; console.log(`FAIL  ${name} ${extra}`) }
}

async function login() {
  const r = await fetch(B + '/auth/login', {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: '13800000001', password: '123456' })
  }).then((x) => x.json())
  return r.data.token
}
async function get(token, path) {
  const r = await fetch(B + path, { headers: { Authorization: 'Bearer ' + token } })
  return r.json()
}

;(async () => {
  const token = await login()
  ok('登录获取token', !!token)

  // ---------- 1. 关键字模糊：菜品 ----------
  const dishesAll = await get(token, '/dishes?page=1&size=100')
  const firstDish = dishesAll.data.list[0]
  const kw = firstDish.dishName.slice(0, 2) // 取菜名前两个字
  const dishesKw = await get(token, `/dishes?page=1&size=100&searchKeyword=${encodeURIComponent(kw)}`)
  ok('菜品-关键字模糊(菜名片段)', dishesKw.data.total >= 1 && dishesKw.data.list.every(d => d.dishName.includes(kw)),
    `关键字="${kw}" 命中${dishesKw.data.total}条`)
  const dishesNone = await get(token, '/dishes?page=1&size=100&searchKeyword=' + encodeURIComponent('不存在的菜名ZZZ9'))
  ok('菜品-关键字无命中返回0', dishesNone.data.total === 0, `total=${dishesNone.data.total}`)

  // ---------- 2. 关键字模糊：员工（姓名/手机号） ----------
  const usersKw = await get(token, '/sysUsers?page=1&size=100&searchKeyword=' + encodeURIComponent('张'))
  ok('员工-关键字按姓名模糊', usersKw.data.total >= 1 && usersKw.data.list.every(u => (u.realName || '').includes('张')),
    `命中${usersKw.data.total}条`)
  const usersPhone = await get(token, '/sysUsers?page=1&size=100&searchKeyword=138')
  ok('员工-关键字按手机号模糊', usersPhone.data.total >= 1, `命中${usersPhone.data.total}条`)

  // ---------- 3. 关键字模糊：会员（卡号/姓名/手机） ----------
  const membersKw = await get(token, '/members?page=1&size=100&searchKeyword=' + encodeURIComponent('138'))
  ok('会员-关键字模糊', membersKw.code === 0, `total=${membersKw.data.total}`)

  // ---------- 4. 枚举精确：菜品上下架状态 ----------
  const dOn = await get(token, '/dishes?page=1&size=100&status=1')
  const dOff = await get(token, '/dishes?page=1&size=100&status=0')
  ok('菜品-状态=上架 过滤', dOn.data.list.every(d => d.status === 1), `${dOn.data.total}条上架`)
  ok('菜品-状态=下架 过滤', dOff.data.list.every(d => d.status === 0), `${dOff.data.total}条下架`)
  ok('菜品-状态枚举分区互斥', dOn.data.total + dOff.data.total === dishesAll.data.total,
    `${dOn.data.total}+${dOff.data.total}=${dishesAll.data.total}`)

  // ---------- 5. 枚举精确：优惠券类型(1满减2折扣3立减) ----------
  const couponT = await get(token, '/coupons?page=1&size=100&couponType=1')
  ok('优惠券-类型=满减 过滤', couponT.data.list.every(c => c.couponType === 1), `命中${couponT.data.total}条`)

  // ---------- 6. 外键过滤：菜品按门店 ----------
  const sid = firstDish.storeId
  const dishesStore = await get(token, `/dishes?page=1&size=100&storeId=${sid}`)
  ok('菜品-按门店ID过滤', dishesStore.data.total >= 1 && dishesStore.data.list.every(d => d.storeId === sid),
    `门店${sid} 命中${dishesStore.data.total}条`)
  // 菜品按分类
  const cid = firstDish.categoryId
  const dishesCat = await get(token, `/dishes?page=1&size=100&categoryId=${cid}`)
  ok('菜品-按分类ID过滤', dishesCat.data.list.every(d => d.categoryId === cid), `分类${cid} 命中${dishesCat.data.total}条`)

  // ---------- 7. 时间范围：创建时间 ----------
  const inRange = await get(token, '/dishes?page=1&size=100&searchBeginTime=2020-01-01&searchEndTime=2030-12-31')
  const beforeRange = await get(token, '/dishes?page=1&size=100&searchBeginTime=2000-01-01&searchEndTime=2000-12-31')
  ok('时间范围-宽区间包含全部', inRange.data.total === dishesAll.data.total, `${inRange.data.total}=${dishesAll.data.total}`)
  ok('时间范围-早区间返回0', beforeRange.data.total === 0, `total=${beforeRange.data.total}`)

  // ---------- 8. 组合条件：门店+状态+关键字 ----------
  const combo = await get(token, `/dishes?page=1&size=100&storeId=${sid}&status=1&searchKeyword=${encodeURIComponent(kw)}`)
  ok('组合条件-门店+状态+关键字', combo.data.list.every(d => d.storeId === sid && d.status === 1 && d.dishName.includes(kw)),
    `命中${combo.data.total}条`)

  // ---------- 9. 订单模块（含枚举+关键字+外键） ----------
  const ordersAll = await get(token, '/orders?page=1&size=100')
  ok('订单-列表正常', ordersAll.code === 0 && ordersAll.data.list.length >= 1)
  const orderKw = ordersAll.data.list[0]?.orderNo
  if (orderKw) {
    const o = await get(token, '/orders?page=1&size=100&searchKeyword=' + encodeURIComponent(orderKw.slice(0, 6)))
    ok('订单-按订单号模糊', o.data.total >= 1, `命中${o.data.total}条`)
  }

  // ---------- 10. 全模块带搜索参数不报错（冒烟） ----------
  const mods = ['stores', 'members', 'sysUsers', 'sysRoles', 'orders', 'paymentRecords', 'refunds', 'reservations',
    'queues', 'diningTables', 'ingredients', 'coupons', 'feedbacks', 'printers', 'staffSchedules']
  let smokeOk = 0
  for (const m of mods) {
    const r = await get(token, `/${m}?page=1&size=10&searchKeyword=a&searchBeginTime=2020-01-01&searchEndTime=2030-12-31`)
    if (r.code === 0) smokeOk++
    else console.log('  冒烟失败:', m, JSON.stringify(r).slice(0, 120))
  }
  ok('15个代表模块带搜索参数冒烟', smokeOk === mods.length, `${smokeOk}/${mods.length}`)

  console.log(`\n结果：${pass} 通过 / ${fail} 失败`)
  process.exit(fail ? 1 : 0)
})().catch((e) => { console.error('测试异常', e); process.exit(2) })
