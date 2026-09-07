<template>
  <div class="orders-page">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入订单号" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="queryForm.orderType" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.orderType" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="queryForm.orderStatus" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.orderStatus" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="queryForm.payStatus" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.payStatus" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="门店">
          <el-select v-model="queryForm.storeId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员">
          <el-select v-model="queryForm.memberId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.members" :key="o.id" :label="o.realName || o.nickname || o.phone" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="台桌">
          <el-select v-model="queryForm.tableId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.diningTables" :key="o.id" :label="o.tableName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker v-model="queryForm.dateRange" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 260px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" @click="handleRefresh">刷新</el-button>
      </div>

      <el-table :data="ordersStore.list" v-loading="ordersStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderNo" label="订单号" width="160" show-overflow-tooltip />
        <el-table-column prop="orderType" label="订单类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.orderType === 1 ? 'primary' : row.orderType === 2 ? 'warning' : 'info'" size="small">
              {{ (enumOptions.orderType.find((i) => i.value === row.orderType) || {}).label || row.orderType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="personCount" label="人数" width="70" align="center" />
        <el-table-column prop="orderStatus" label="订单状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getOrderStatusTagType(row.orderStatus)" size="small">
              {{ (enumOptions.orderStatus.find((i) => i.value === row.orderStatus) || {}).label || row.orderStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 2 ? 'success' : 'warning'" size="small">
              {{ (enumOptions.payStatus.find((i) => i.value === row.payStatus) || {}).label || row.payStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payableAmount" label="应付" width="90" align="right" />
        <el-table-column prop="actualAmount" label="实付" width="90" align="right" />
        <el-table-column prop="orderTime" label="下单时间" width="170" />
        <el-table-column label="操作" width="260" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleViewDetail(row)">查看详情</el-button>
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

    <!-- 详情弹窗：订单信息 + 订单明细 + 状态日志 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="800px" destroy-on-close>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="订单信息" name="basic">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="订单号">{{ currentOrder.orderNo }}</el-descriptions-item>
            <el-descriptions-item label="订单类型">{{ getEnumLabel('orderType', currentOrder.orderType) }}</el-descriptions-item>
            <el-descriptions-item label="订单状态">
              <el-tag :type="getOrderStatusTagType(currentOrder.orderStatus)" size="small">
                {{ getEnumLabel('orderStatus', currentOrder.orderStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="支付状态">
              <el-tag :type="currentOrder.payStatus === 2 ? 'success' : 'warning'" size="small">
                {{ getEnumLabel('payStatus', currentOrder.payStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="菜品合计">{{ currentOrder.dishAmount }}</el-descriptions-item>
            <el-descriptions-item label="优惠折扣">{{ currentOrder.discountAmount }}</el-descriptions-item>
            <el-descriptions-item label="优惠券抵扣">{{ currentOrder.couponAmount }}</el-descriptions-item>
            <el-descriptions-item label="应付金额">{{ currentOrder.payableAmount }}</el-descriptions-item>
            <el-descriptions-item label="实际支付">{{ currentOrder.actualAmount }}</el-descriptions-item>
            <el-descriptions-item label="会员余额支付">{{ currentOrder.memberPayAmount }}</el-descriptions-item>
            <el-descriptions-item label="用餐人数">{{ currentOrder.personCount }}</el-descriptions-item>
            <el-descriptions-item label="下单时间">{{ formatTime(currentOrder.orderTime) }}</el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.payTime" label="支付时间">{{ formatTime(currentOrder.payTime) }}</el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.finishTime" label="完成时间">{{ formatTime(currentOrder.finishTime) }}</el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.cancelReason" label="取消原因" :span="2">
              {{ currentOrder.cancelReason }}
            </el-descriptions-item>
            <el-descriptions-item v-if="currentOrder.remark" label="备注" :span="2">
              {{ currentOrder.remark }}
            </el-descriptions-item>
          </el-descriptions>

          <!-- 操作按钮 -->
          <div class="detail-actions" style="margin-top: 20px">
            <template v-if="currentOrder.orderStatus === 1 && currentOrder.payStatus === 1">
              <el-button type="success" @click="handlePayCurrentOrder">支付</el-button>
              <el-button type="danger" @click="handleCancelCurrentOrder">取消订单</el-button>
            </template>
            <template v-if="currentOrder.orderStatus === 2 || currentOrder.orderStatus === 3">
              <el-button type="primary" @click="handleBatchMakeStatus(3)">全部上齐</el-button>
            </template>
          </div>
        </el-tab-pane>

        <el-tab-pane label="订单明细" name="details">
          <el-table :data="currentDetails" border stripe>
            <el-table-column prop="dishName" label="菜品名称" min-width="150" />
            <el-table-column prop="specName" label="规格" width="100" />
            <el-table-column prop="tasteName" label="口味" width="100" />
            <el-table-column prop="dishPrice" label="单价" width="80" align="right" />
            <el-table-column prop="quantity" label="数量" width="70" align="center" />
            <el-table-column prop="subtotal" label="小计" width="80" align="right" />
            <el-table-column prop="status" label="制作状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'warning' : row.status === 2 ? 'primary' : row.status === 3 ? 'success' : 'danger'" size="small">
                  {{ getMakeStatusLabel(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="140" fixed="right">
              <template #default="{ row }">
                <template v-if="row.status === 1">
                  <el-button type="primary" size="small" link @click="updateDetailStatus(row, 2)">开始制作</el-button>
                </template>
                <template v-if="row.status === 2">
                  <el-button type="success" size="small" link @click="updateDetailStatus(row, 3)">制作完成</el-button>
                </template>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>

        <el-tab-pane label="状态日志" name="logs">
          <el-timeline>
            <el-timeline-item v-for="log in currentStatusLogs" :key="log.id" :timestamp="formatTime(log.createTime)">
              <el-card>
                <div><strong>状态变更：</strong>{{ getEnumLabel('orderStatus', log.fromStatus) }} → {{ getEnumLabel('orderStatus', log.toStatus) }}</div>
                <div v-if="log.remark"><strong>备注：</strong>{{ log.remark }}</div>
              </el-card>
            </el-timeline-item>
            <el-empty v-if="!currentStatusLogs.length" description="暂无状态日志" />
          </el-timeline>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useOrdersStore } from '@/stores/orders'
import ordersApi from '@/api/orders'
import storeApi from '@/api/store'
import memberApi from '@/api/member'
import diningTableApi from '@/api/diningTable'

const ordersStore = useOrdersStore()

const enumOptions = {
  orderType: [{ value: 1, label: '堂食' }, { value: 2, label: '外卖' }, { value: 3, label: '自取' }],
  orderStatus: [
    { value: 1, label: '待支付' },
    { value: 2, label: '待制作' },
    { value: 3, label: '制作中' },
    { value: 4, label: '待配送' },
    { value: 5, label: '配送中' },
    { value: 6, label: '待自取' },
    { value: 7, label: '已完成' },
    { value: 8, label: '已取消' },
    { value: 9, label: '已退款' }
  ],
  payStatus: [{ value: 1, label: '待支付' }, { value: 2, label: '已支付' }, { value: 3, label: '已退款' }, { value: 4, label: '部分退款' }],
  takeType: [{ value: 1, label: '外送' }, { value: 2, label: '自取' }],
  makeStatus: [{ value: 1, label: '待制作' }, { value: 2, label: '制作中' }, { value: 3, label: '已上齐' }, { value: 4, label: '退菜' }]
}

const getEnumLabel = (type, value) => {
  return (enumOptions[type]?.find((i) => i.value === value) || {}).label || value
}

const getMakeStatusLabel = (status) => getEnumLabel('makeStatus', status)

const getOrderStatusTagType = (status) => {
  if (status === 7) return 'success'
  if (status === 8 || status === 9) return 'danger'
  if (status === 1) return 'warning'
  return 'primary'
}

const searchOptions = reactive({
  stores: [],
  members: [],
  diningTables: []
})
const loadSearchOptions = () => {
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
  memberApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.members = r.data?.list || [] }).catch(() => {})
  diningTableApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.diningTables = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

const queryForm = reactive({
  keyword: '',
  orderType: null,
  orderStatus: null,
  payStatus: null,
  storeId: null,
  memberId: null,
  tableId: null,
  dateRange: null
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
const handleRefresh = () => {
  ordersStore.fetchList(buildParams())
}

const detailDialogVisible = ref(false)
const activeTab = ref('basic')
const currentOrder = ref({})
const currentDetails = ref([])
const currentStatusLogs = ref([])

const formatTime = (timeStr) => {
  if (!timeStr) return '-'
  return timeStr
}

const handleViewDetail = async (row) => {
  activeTab.value = 'basic'
  const res = await ordersApi.getWithDetails(row.id)
  if (res.code === 0 && res.data) {
    currentOrder.value = res.data.order
    currentDetails.value = res.data.details || []
    currentStatusLogs.value = res.data.statusLogs || []
    detailDialogVisible.value = true
  } else {
    ElMessage.error(res.message || '获取详情失败')
  }
}

const updateDetailStatus = async (detail, status) => {
  await ordersApi.updateMakeStatus(currentOrder.value.id, { detailId: detail.id, makeStatus: status })
  ElMessage.success('更新成功')
  detail.status = status
  ordersStore.fetchList(buildParams())
}

const handleBatchMakeStatus = async (status) => {
  await ordersApi.updateMakeStatus(currentOrder.value.id, { detailId: null, makeStatus: status })
  ElMessage.success('更新成功')
  detailDialogVisible.value = false
  ordersStore.fetchList(buildParams())
}

const handlePayCurrentOrder = () => {
  ElMessageBox.prompt('请选择支付方式', '支付订单', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /^WECHAT|ALIPAY|MEMBER_BALANCE|CASH$/,
    inputErrorMessage: '请输入 WECHAT/ALIPAY/MEMBER_BALANCE/CASH'
  }).then(async ({ value }) => {
    const actualAmount = currentOrder.value.payableAmount
    const memberPayAmount = 0
    await ordersApi.pay(currentOrder.value.id, { payType: value, actualAmount, memberPayAmount })
    ElMessage.success('支付成功')
    detailDialogVisible.value = false
    ordersStore.fetchList(buildParams())
  }).catch(() => {})
}

const handleCancelCurrentOrder = () => {
  ElMessageBox.prompt('请输入取消原因', '取消订单', {
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async ({ value }) => {
    await ordersApi.cancel(currentOrder.value.id, { reason: value || '用户取消' })
    ElMessage.success('取消成功')
    detailDialogVisible.value = false
    ordersStore.fetchList(buildParams())
  }).catch(() => {})
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

onMounted(() => {
  ordersStore.fetchList()
})
</script>

<style scoped>
.orders-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.detail-actions { text-align: center; }
</style>
