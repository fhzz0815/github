<template>
  <div class="dashboard">
    <!-- 数据加载失败提示 -->
    <el-alert
      v-if="loadError"
      :title="loadError"
      type="error"
      show-icon
      closable
      style="margin-bottom: 16px"
    />
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16">
      <el-col :xs="12" :sm="12" :md="8" :lg="6" :xl="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover" class="stat-card-wrap">
          <div class="stat-card">
            <el-icon :size="40" :color="card.color"><component :is="card.icon" /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="12" :md="8" :lg="6" :xl="6">
        <el-card shadow="hover" class="stat-card-wrap">
          <div class="stat-card">
            <el-icon :size="40" color="#9b59b6"><Money /></el-icon>
            <div class="stat-info">
              <div class="stat-value">￥{{ totalRevenue }}</div>
              <div class="stat-title">营业总额(元)</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表第一行：订单趋势 + 订单状态 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :lg="16">
        <el-card shadow="never">
          <template #header>近 7 天订单趋势</template>
          <div ref="trendChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="never">
          <template #header>订单状态分布</template>
          <div ref="statusChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表第二行：订单类型 + 菜品分类 + 门店订单 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :xs="24" :md="12" :lg="8">
        <el-card shadow="never">
          <template #header>订单类型占比</template>
          <div ref="typeChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" :lg="8">
        <el-card shadow="never">
          <template #header>菜品分类数量</template>
          <div ref="categoryChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="24" :lg="8">
        <el-card shadow="never">
          <template #header>各门店订单量</template>
          <div ref="storeChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card shadow="never">
          <template #header>快捷入口</template>
          <div class="quick-entry">
            <el-button type="primary" @click="$router.push('/stores')">门店管理</el-button>
            <el-button type="success" @click="$router.push('/dishes')">菜品管理</el-button>
            <el-button type="warning" @click="$router.push('/orders')">订单管理</el-button>
            <el-button type="danger" @click="$router.push('/members')">会员管理</el-button>
            <el-button @click="$router.push('/reservations')">预约管理</el-button>
            <el-button @click="$router.push('/dishStocks')">库存管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import dayjs from 'dayjs'
import storeApi from '@/api/store'
import dishApi from '@/api/dish'
import memberApi from '@/api/member'
import ordersApi from '@/api/orders'
import dishCategoryApi from '@/api/dishCategory'

// 加载失败时的错误提示信息（为空表示没有错误）
const loadError = ref('')

// 顶部四个数量统计
const cards = ref([
  { title: '门店数量', value: '-', icon: 'Shop', color: '#409EFF' },
  { title: '菜品数量', value: '-', icon: 'Food', color: '#67C23A' },
  { title: '会员数量', value: '-', icon: 'User', color: '#E6A23C' },
  { title: '订单数量', value: '-', icon: 'List', color: '#F56C6C' }
])
const totalRevenue = ref('0.00')

// 图表挂载点
const trendChartRef = ref(null)
const statusChartRef = ref(null)
const typeChartRef = ref(null)
const categoryChartRef = ref(null)
const storeChartRef = ref(null)
const charts = []

// 订单状态字典：数据库里 1待支付 2待制作 3制作中 4待配送 5配送中 6待自取 7已完成 8已取消 9已退款
const statusMap = {
  1: '待支付', 2: '待制作', 3: '制作中', 4: '待配送', 5: '配送中',
  6: '待自取', 7: '已完成', 8: '已取消', 9: '已退款'
}
// 订单类型字典：1堂食 2外卖 3自取
const typeMap = { 1: '堂食', 2: '外卖', 3: '自取' }

// 安全取列表数据
function pickList(res) {
  return res && res.data ? res.data.list || [] : []
}
function pickTotal(res) {
  return res && res.data ? res.data.total || 0 : 0
}

function renderTrend(orders) {
  // 生成近 7 天日期刻度，没有订单的天补 0
  const days = []
  const countMap = {}
  const amountMap = {}
  for (let i = 6; i >= 0; i--) {
    const d = dayjs().subtract(i, 'day').format('YYYY-MM-DD')
    days.push(d)
    countMap[d] = 0
    amountMap[d] = 0
  }
  orders.forEach((o) => {
    const t = o.orderTime || o.createTime
    if (!t) return
    const d = dayjs(t).format('YYYY-MM-DD')
    if (d in countMap) {
      countMap[d] += 1
      amountMap[d] += Number(o.actualAmount || 0)
    }
  })
  const chart = echarts.init(trendChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单量', '营业额(元)'] },
    grid: { left: 40, right: 40, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: days.map((d) => d.slice(5)) },
    yAxis: [
      { type: 'value', name: '订单量', minInterval: 1 },
      { type: 'value', name: '营业额' }
    ],
    series: [
      {
        name: '订单量',
        type: 'bar',
        data: days.map((d) => countMap[d]),
        itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] },
        barWidth: '40%'
      },
      {
        name: '营业额(元)',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        data: days.map((d) => Number(amountMap[d].toFixed(2))),
        itemStyle: { color: '#F56C6C' }
      }
    ]
  })
  charts.push(chart)
}

function renderStatus(orders) {
  const map = {}
  orders.forEach((o) => {
    const key = statusMap[o.orderStatus] || ('状态' + o.orderStatus)
    map[key] = (map[key] || 0) + 1
  })
  const data = Object.keys(map).map((name) => ({ name, value: map[name] }))
  const chart = echarts.init(statusChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} 单 ({d}%)' },
    legend: { bottom: 0, type: 'scroll' },
    series: [
      {
        type: 'pie',
        radius: ['40%', '65%'],
        center: ['50%', '45%'],
        label: { formatter: '{b}\n{c}单' },
        data: data.length ? data : [{ name: '暂无订单', value: 1 }]
      }
    ]
  })
  charts.push(chart)
}

function renderType(orders) {
  const map = { 堂食: 0, 外卖: 0, 自取: 0 }
  orders.forEach((o) => {
    const name = typeMap[o.orderType]
    if (name) map[name] += 1
  })
  const data = Object.keys(map).map((name) => ({ name, value: map[name] }))
  const chart = echarts.init(typeChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} 单 ({d}%)' },
    color: ['#67C23A', '#E6A23C', '#409EFF'],
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: '60%',
        center: ['50%', '45%'],
        label: { formatter: '{b}: {c}' },
        data
      }
    ]
  })
  charts.push(chart)
}

function renderCategory(dishes, categories) {
  // 分类 id -> 名称
  const nameMap = {}
  categories.forEach((c) => { nameMap[c.id] = c.categoryName || ('分类' + c.id) })
  const countMap = {}
  dishes.forEach((d) => {
    const name = nameMap[d.categoryId] || '未分类'
    countMap[name] = (countMap[name] || 0) + 1
  })
  const entries = Object.entries(countMap).sort((a, b) => b[1] - a[1]).slice(0, 8)
  const chart = echarts.init(categoryChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 10, right: 20, top: 20, bottom: 10, containLabel: true },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: entries.map((e) => e[0]).reverse() },
    series: [
      {
        type: 'bar',
        data: entries.map((e) => e[1]).reverse(),
        itemStyle: { color: '#67C23A', borderRadius: [0, 4, 4, 0] },
        barWidth: '55%',
        label: { show: true, position: 'right' }
      }
    ]
  })
  charts.push(chart)
}

function renderStore(orders, stores) {
  const nameMap = {}
  stores.forEach((s) => { nameMap[s.id] = s.storeName || ('门店' + s.id) })
  const countMap = {}
  orders.forEach((o) => {
    const name = nameMap[o.storeId] || ('门店' + o.storeId)
    countMap[name] = (countMap[name] || 0) + 1
  })
  // 没有订单的门店也展示出来
  stores.forEach((s) => {
    const name = s.storeName || ('门店' + s.id)
    if (!(name in countMap)) countMap[name] = 0
  })
  const names = Object.keys(countMap)
  const chart = echarts.init(storeChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: 10, right: 20, top: 20, bottom: 10, containLabel: true },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: names.reverse(), axisLabel: { width: 90, overflow: 'truncate' } },
    series: [
      {
        type: 'bar',
        data: names.map((n) => countMap[n]).reverse(),
        itemStyle: { color: '#409EFF', borderRadius: [0, 4, 4, 0] },
        barWidth: '55%',
        label: { show: true, position: 'right' }
      }
    ]
  })
  charts.push(chart)
}

function resizeAll() {
  charts.forEach((c) => c && c.resize())
}

onMounted(async () => {
  try {
    // 并发拉取各模块数据（取较大页以覆盖全量）
    const [storeRes, dishRes, memberRes, orderRes, catRes] = await Promise.all([
      storeApi.list({ page: 1, size: 1000 }),
      dishApi.list({ page: 1, size: 1000 }),
      memberApi.list({ page: 1, size: 1000 }),
      ordersApi.list({ page: 1, size: 1000 }),
      dishCategoryApi.list({ page: 1, size: 1000 })
    ])

    const stores = pickList(storeRes)
    const dishes = pickList(dishRes)
    const members = pickList(memberRes)
    const orders = pickList(orderRes)
    const categories = pickList(catRes)

    cards.value[0].value = pickTotal(storeRes)
    cards.value[1].value = pickTotal(dishRes)
    cards.value[2].value = pickTotal(memberRes)
    cards.value[3].value = pickTotal(orderRes)
    totalRevenue.value = orders
      .reduce((sum, o) => sum + Number(o.actualAmount || 0), 0)
      .toFixed(2)

    await nextTick()
    renderTrend(orders)
    renderStatus(orders)
    renderType(orders)
    renderCategory(dishes, categories)
    renderStore(orders, stores)
    window.addEventListener('resize', resizeAll)
  } catch (e) {
    // 接口异常时给出友好提示，不阻塞页面其他部分正常显示
    const msg = e?.response?.data?.message || e?.message || '网络异常，请检查后端服务是否正常启动'
    loadError.value = '数据加载失败：' + msg
    console.error('首页数据加载失败', e)
  }
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeAll)
  charts.forEach((c) => c && c.dispose())
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}
.stat-card-wrap {
  margin-bottom: 4px;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 26px;
  font-weight: bold;
  color: #303133;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
.chart {
  width: 100%;
  height: 300px;
}
.quick-entry {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
