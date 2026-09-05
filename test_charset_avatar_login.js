/**
 * 字符集 + 头像 + 登录时间 综合回归验证脚本
 *
 * 测试覆盖：
 *   1) 字符集 CRUD：插入/查询/更新/删除包含中文、emoji、特殊符号、繁体的数据
 *   2) 头像验证：查询 5 个员工的 avatar 字段格式分布
 *   3) 登录时间：连续 3 次登录，校验 last_login_time 是否更新且精确到秒
 */
const http = require('http')

const BASE = { host: '127.0.0.1', port: 8080, pathPrefix: '/smart_restaurant/api/v1' }

function request(method, path, body, token) {
  return new Promise((resolve, reject) => {
    const data = body ? JSON.stringify(body) : null
    const headers = { 'Content-Type': 'application/json; charset=utf-8' }
    if (data) headers['Content-Length'] = Buffer.byteLength(data, 'utf8')
    if (token) headers['Authorization'] = 'Bearer ' + token
    const req = http.request({
      host: BASE.host,
      port: BASE.port,
      path: BASE.pathPrefix + path,
      method,
      headers
    }, (res) => {
      let chunks = []
      res.on('data', (c) => chunks.push(c))
      res.on('end', () => {
        const raw = Buffer.concat(chunks).toString('utf8')
        try { resolve({ status: res.statusCode, body: JSON.parse(raw) }) }
        catch { resolve({ status: res.statusCode, body: raw }) }
      })
    })
    req.on('error', reject)
    if (data) req.write(data)
    req.end()
  })
}

const sleep = (ms) => new Promise(r => setTimeout(r, ms))

// 全部使用的特殊字符集：中文 + 繁体 + emoji + 全角符号 + 特殊标点
const SPECIAL = '餐厅·★※【】「」—Ⅷ😀🎉²³¥¥¥€£♡'

;(async () => {
  console.log('\n========================================')
  console.log('  字符集 + 头像 + 登录时间 综合回归测试')
  console.log('========================================')

  // ----------------------------------------------------------
  // 步骤 1：登录获取 token
  // ----------------------------------------------------------
  console.log('\n[1] 登录测试账号 13800000001 / 123456')
  const loginRes = await request('POST', '/auth/login', {
    username: '13800000001',
    password: '123456'
  })
  if (loginRes.status !== 200 || loginRes.body.code !== 0) {
    console.error('登录失败:', loginRes)
    process.exit(1)
  }
  const token = loginRes.body.data.token
  console.log('登录成功，token=' + token.substring(0, 30) + '...')

  // ----------------------------------------------------------
  // 步骤 2：字符集 CRUD - 创建门店（含特殊字符）
  // ----------------------------------------------------------
  console.log('\n[2] 字符集 CRUD - 创建门店（含特殊字符）')
  const storeNo = 'CS' + Date.now().toString(36)
  const storeName = '测试门店_' + SPECIAL
  const createRes = await request('POST', '/stores', {
    storeNo: storeNo,
    storeName: storeName,
    province: '四川省',
    city: '成都市',
    district: '锦江区',
    address: '春熙路★1号「测试」—3D—Ⅷ',
    phone: '028-88888888',
    contactName: '张三😀'
  }, token)
  console.log('创建返回：', createRes.status, JSON.stringify(createRes.body).substring(0, 120))
  if (createRes.body.code !== 0) {
    console.error('创建门店失败')
    process.exit(1)
  }
  // 创建接口未返回 id，通过列表查询刚创建的门店
  const listRes = await request('GET', '/stores?pageNum=1&pageSize=10&searchKeyword=' + encodeURIComponent(storeNo), null, token)
  const found = (listRes.body.data.list || []).find(s => s.storeNo === storeNo)
  if (!found) {
    console.error('✗ 创建后通过 storeNo 查不到门店')
    process.exit(1)
  }
  const storeId = found.id
  console.log('门店 ID=' + storeId)

  await sleep(200)

  // ----------------------------------------------------------
  // 步骤 3：查询门店详情，校验字符集读取
  // ----------------------------------------------------------
  console.log('\n[3] 查询门店详情，校验字符集读取')
  const getRes = await request('GET', '/stores/' + storeId, null, token)
  console.log('查询返回：', getRes.status)
  const store = getRes.body.data
  const expectedName = storeName
  const expectedAddr = '春熙路★1号「测试」—3D—Ⅷ'
  console.log('  门店名称：', store.storeName)
  console.log('  期望名称：', expectedName)
  console.log('  门店地址：', store.address)
  console.log('  期望地址：', expectedAddr)
  if (store.storeName !== expectedName) {
    console.error('✗ 门店名称字符集不一致')
    process.exit(1)
  }
  if (store.address !== expectedAddr) {
    console.error('✗ 门店地址字符集不一致')
    process.exit(1)
  }
  console.log('✓ 创建后字符集读取正确')

  // ----------------------------------------------------------
  // 步骤 4：更新门店（特殊字符变更）
  // ----------------------------------------------------------
  console.log('\n[4] 更新门店（特殊字符变更）')
  const newName = '更新店名☆' + SPECIAL + '²'
  const updRes = await request('PUT', '/stores/' + storeId, {
    id: storeId,
    storeNo: storeNo,
    storeName: newName,
    province: '北京★市',
    city: '北京★市',
    district: '朝阳★区',
    address: '朝阳路※1号【测试】',
    phone: '010-66666666',
    contactName: '李四🎉'
  }, token)
  console.log('更新返回：', updRes.status, JSON.stringify(updRes.body).substring(0, 80))
  const getRes2 = await request('GET', '/stores/' + storeId, null, token)
  const store2 = getRes2.body.data
  console.log('  更新后名称：', store2.storeName)
  if (store2.storeName !== newName) {
    console.error('✗ 更新后字符集不一致')
    process.exit(1)
  }
  console.log('✓ 更新后字符集读取正确')

  // ----------------------------------------------------------
  // 步骤 5：关键字搜索（特殊字符）
  // ----------------------------------------------------------
  console.log('\n[5] 关键字搜索（使用特殊字符）')
  const searchRes = await request('GET', '/stores?pageNum=1&pageSize=10&searchKeyword=' + encodeURIComponent('☆'), null, token)
  console.log('搜索返回：', searchRes.status, '总数=', searchRes.body.data.total)
  const foundInSearch = (searchRes.body.data.list || []).find(s => s.id === storeId)
  if (!foundInSearch) {
    console.error('✗ 搜索不到包含☆的门店')
    process.exit(1)
  }
  console.log('✓ 特殊字符搜索正常，匹配到门店：', foundInSearch.storeName)

  // ----------------------------------------------------------
  // 步骤 6：删除门店
  // ----------------------------------------------------------
  console.log('\n[6] 删除门店')
  const delRes = await request('DELETE', '/stores/' + storeId, null, token)
  console.log('删除返回：', delRes.status, JSON.stringify(delRes.body).substring(0, 80))
  const getRes3 = await request('GET', '/stores/' + storeId, null, token)
  if (getRes3.body.data !== null) {
    console.error('✗ 删除后仍能查到门店')
    process.exit(1)
  }
  console.log('✓ 删除成功')

  // ----------------------------------------------------------
  // 步骤 7：验证历史乱码数据已修复
  // ----------------------------------------------------------
  console.log('\n[7] 验证历史乱码数据已修复（dish.id=1 应为"麻辣香锅"，member.id=1 real_name 应为"张小明"）')
  const dishRes = await request('GET', '/dishes/1', null, token)
  if (dishRes.body.data && dishRes.body.data.dishName === '麻辣香锅') {
    console.log('✓ dish.id=1 名称正确：', dishRes.body.data.dishName)
  } else {
    console.error('✗ dish.id=1 名称错误：', dishRes.body.data && dishRes.body.data.dishName)
    process.exit(1)
  }
  const memberRes = await request('GET', '/members/1', null, token)
  if (memberRes.body.data && memberRes.body.data.realName === '张小明') {
    console.log('✓ member.id=1 real_name 正确：', memberRes.body.data.realName)
  } else {
    console.error('✗ member.id=1 real_name 错误：', memberRes.body.data && memberRes.body.data.realName)
    process.exit(1)
  }

  // ----------------------------------------------------------
  // 步骤 8：头像格式分布检查（5 种格式）
  // 直接查询 5 个员工详情（sysUsers 列表按 id DESC 排序，分页不一定覆盖前5个）
  // ----------------------------------------------------------
  console.log('\n[8] 头像格式分布检查（5 种格式，直接查询 5 个员工详情）')
  const avatarSpecs = [
    { id: 1, expect: 'PNG 256x256', match: (u) => u.avatar && u.avatar.includes('png?seed=zhangboss') && u.avatar.includes('size=256') },
    { id: 2, expect: 'SVG 矢量',    match: (u) => u.avatar && u.avatar.includes('svg?seed=') },
    { id: 3, expect: 'JPEG 256x256', match: (u) => u.avatar && u.avatar.includes('pravatar.cc/256') },
    { id: 4, expect: 'PNG 64x64',   match: (u) => u.avatar && u.avatar.includes('png?seed=wangdianzhang') && u.avatar.includes('size=64') },
    { id: 5, expect: 'JPEG 512x512', match: (u) => u.avatar && u.avatar.includes('pravatar.cc/512') }
  ]
  let avatarPass = 0
  for (const spec of avatarSpecs) {
    const u = await request('GET', '/sysUsers/' + spec.id, null, token)
    const user = u.body.data
    if (user && spec.match(user)) {
      console.log('  ✓ 员工 id=' + spec.id + ' ' + user.realName + ' 头像格式：' + spec.expect + ' URL=' + user.avatar.substring(0, 60) + '...')
      avatarPass++
    } else {
      console.error('  ✗ 员工 id=' + spec.id + ' 头像不匹配：' + (user && user.avatar))
    }
  }
  if (avatarPass < 5) {
    console.error('✗ 头像格式校验通过 ' + avatarPass + '/5')
    process.exit(1)
  }
  console.log('✓ 5 种头像格式校验全部通过')

  // ----------------------------------------------------------
  // 步骤 9：连续 3 次登录，验证 last_login_time 更新
  // ----------------------------------------------------------
  console.log('\n[9] 连续 3 次登录，验证 last_login_time 更新（精确到秒）')
  const beforeUser = await request('GET', '/sysUsers/1', null, token)
  const beforeTime = beforeUser.body.data.lastLoginTime
  console.log('  登录前 last_login_time：', beforeTime)

  for (let i = 1; i <= 3; i++) {
    await sleep(1100) // 确保秒级时间戳不同
    const r = await request('POST', '/auth/login', {
      username: '13800000001',
      password: '123456'
    })
    if (r.body.code !== 0) {
      console.error('✗ 第 ' + i + ' 次登录失败')
      process.exit(1)
    }
    // 用新 token 查询用户信息
    const newToken = r.body.data.token
    const u = await request('GET', '/sysUsers/1', null, newToken)
    const t = u.body.data.lastLoginTime
    console.log('  第 ' + i + ' 次登录后 last_login_time：', t)
    if (!t) {
      console.error('✗ 第 ' + i + ' 次登录后 last_login_time 为空')
      process.exit(1)
    }
  }

  const afterUser = await request('GET', '/sysUsers/1', null, token)
  const afterTime = afterUser.body.data.lastLoginTime
  console.log('  最终 last_login_time：', afterTime)
  if (afterTime && afterTime !== beforeTime) {
    console.log('✓ 最后登录时间已更新，与登录前不同')
  } else {
    console.error('✗ 最后登录时间未更新')
    process.exit(1)
  }

  // ----------------------------------------------------------
  // 步骤 10：时间精确到秒验证
  // ----------------------------------------------------------
  console.log('\n[10] 时间精确到秒验证')
  const timePattern = /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/
  if (timePattern.test(afterTime)) {
    console.log('✓ 时间格式符合 yyyy-MM-dd HH:mm:ss，精确到秒')
  } else {
    console.error('✗ 时间格式不符合秒级要求：', afterTime)
    process.exit(1)
  }

  console.log('\n========================================')
  console.log('  所有验证通过 ✓')
  console.log('========================================')
})().catch(err => {
  console.error('测试异常：', err)
  process.exit(1)
})
