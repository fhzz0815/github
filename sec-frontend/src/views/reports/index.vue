<template>
  <div class="reports-page">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="16" class="summary-row">
      <el-col :span="6">
        <el-card shadow="never">
          <div class="summary-item">
            <div class="summary-label">今日订单数</div>
            <div class="summary-value">{{ todaySummary.todayOrderCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="summary-item">
            <div class="summary-label">今日收入</div>
            <div class="summary-value income">¥{{ todaySummary.todayIncome }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="summary-item">
            <div class="summary-label">待处理订单</div>
            <div class="summary-value pending">{{ todaySummary.pendingOrderCount }}</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never">
          <div class="summary-item">
            <div class="summary-label">门店</div>
            <div class="summary-value" style="font-size: 14px">
              <el-select v-model="queryStoreId" size="small" placeholder="选择门店" @change="loadTodaySummary">
                <el-option v-for="s in searchOptions.stores" :key="s.id" :label="s.storeName" :value="s.id" />
              </el-select>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 报表查询区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true">
        <el-form-item label="门店">
          <el-select v-model="reportQuery.storeId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始日期">
          <el-date-picker v-model="reportQuery.beginDate" type="date" value-format="YYYY-MM-DD" placeholder="开始日期" />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker v-model="reportQuery.endDate" type="date" value-format="YYYY-MM-DD" placeholder="结束日期" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadSalesReport">查询报表</el-button>
          <el-button @click="resetReportQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 报表统计 -->
    <el-card shadow="never">
      <div class="table-toolbar">
        <span class="toolbar-title">销售日报表</span>
        <span class="toolbar-summary" v-if="reportData.totalOrders">
          合计：{{ reportData.totalOrders }} 单，总收入 ¥{{ reportData.totalIncome }}
        </span>
      </div>

      <el-table :data="reportData.dailyData" border stripe v-loading="reportLoading">
        <el-table-column prop="payDate" label="日期" width="120" />
        <el-table-column prop="orderCount" label="订单数" width="80" align="center" />
        <el-table-column prop="dishIncome" label="菜品收入" width="120" align="right" />
        <el-table-column prop="actualIncome" label="实收金额" width="120" align="right" />
        <el-table-column prop="discountTotal" label="优惠合计" width="120" align="right" />
        <el-table-column label="客单价" width="100" align="right">
          <template #default="{ row }">
            {{ row.orderCount > 0 ? (row.actualIncome / row.orderCount).toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!reportData.dailyData?.length && !reportLoading" description="暂无报表数据" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import ordersApi from '@/api/orders'
import storeApi from '@/api/store'

const searchOptions = reactive({
  stores: []
})
const loadStores = () => {
  storeApi.list({ page: 1, size: 1000 }).then((r) => {
    searchOptions.stores = r.data?.list || []
  }).catch(() => {})
}

// 今日概况
const todaySummary = reactive({
  todayOrderCount: 0,
  todayIncome: '0.00',
  pendingOrderCount: 0
})
const queryStoreId = ref(null)

const loadTodaySummary = async () => {
  const res = await ordersApi.todaySummary(queryStoreId.value)
  if (res.code === 0 && res.data) {
    Object.assign(todaySummary, res.data)
  }
}

// 报表查询
const reportQuery = reactive({
  storeId: null,
  beginDate: '',
  endDate: ''
})
const reportData = reactive({
  dailyData: [],
  totalOrders: 0,
  totalIncome: '0.00'
})
const reportLoading = ref(false)

const loadSalesReport = async () => {
  const params = { ...reportQuery }
  if (!params.beginDate) {
    // 默认最近7天
    const end = new Date()
    const begin = new Date()
    begin.setDate(begin.getDate() - 7)
    params.beginDate = formatDate(begin)
    params.endDate = formatDate(end)
    reportQuery.beginDate = params.beginDate
    reportQuery.endDate = params.endDate
  }
  reportLoading.value = true
  try {
    const res = await ordersApi.salesReport(params.storeId, params.beginDate, params.endDate)
    if (res.code === 0 && res.data) {
      reportData.dailyData = res.data.dailyData || []
      reportData.totalOrders = res.data.totalOrders || 0
      reportData.totalIncome = res.data.totalIncome || '0.00'
    }
  } finally {
    reportLoading.value = false
  }
}

const resetReportQuery = () => {
  reportQuery.storeId = null
  reportQuery.beginDate = ''
  reportQuery.endDate = ''
  loadSalesReport()
}

const formatDate = (date) => {
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

onMounted(() => {
  loadStores()
  loadTodaySummary()
  loadSalesReport()
})
</script>

<style scoped>
.reports-page { padding: 16px; }
.summary-row { margin-bottom: 16px; }
.summary-item { text-align: center; }
.summary-label { font-size: 14px; color: #999; margin-bottom: 8px; }
.summary-value { font-size: 28px; font-weight: bold; color: #333; }
.summary-value.income { color: #67c23a; }
.summary-value.pending { color: #e6a23c; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; }
.toolbar-title { font-size: 16px; font-weight: bold; }
.toolbar-summary { font-size: 14px; color: #409eff; }
</style>
