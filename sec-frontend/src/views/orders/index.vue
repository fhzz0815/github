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
        <el-form-item label="外卖取餐方式">
          <el-select v-model="queryForm.takeType" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.takeType" :key="o.value" :label="o.label" :value="o.value" />
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
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>

      <el-table :data="ordersStore.list" v-loading="ordersStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="id" label="订单ID" width="110" show-overflow-tooltip />
        <el-table-column prop="orderNo" label="订单号" width="160" show-overflow-tooltip />
        <el-table-column prop="storeId" label="门店ID" width="110" show-overflow-tooltip />
        <el-table-column prop="memberId" label="客户ID" width="110" show-overflow-tooltip />
        <el-table-column prop="orderType" label="订单类型" width="110">
          <template #default="{ row }">
            <el-tag :type="row.orderType === 7 ? 'success' : row.orderType === 8 || row.orderType === 9 ? 'danger' : 'info'" size="small">
              {{ (enumOptions.orderType.find((i) => i.value === row.orderType) || {}).label || row.orderType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="personCount" label="用餐人数" width="110" show-overflow-tooltip />
        <el-table-column prop="orderStatus" label="订单状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.orderStatus === 7 ? 'success' : row.orderStatus === 8 || row.orderStatus === 9 ? 'danger' : 'info'" size="small">
              {{ (enumOptions.orderStatus.find((i) => i.value === row.orderStatus) || {}).label || row.orderStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payStatus" label="支付状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.payStatus === 7 ? 'success' : row.payStatus === 8 || row.payStatus === 9 ? 'danger' : 'info'" size="small">
              {{ (enumOptions.payStatus.find((i) => i.value === row.payStatus) || {}).label || row.payStatus }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="takeType" label="外卖取餐方式" width="110">
          <template #default="{ row }">
            <el-tag :type="row.takeType === 7 ? 'success' : row.takeType === 8 || row.takeType === 9 ? 'danger' : 'info'" size="small">
              {{ (enumOptions.takeType.find((i) => i.value === row.takeType) || {}).label || row.takeType }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payableAmount" label="应付金额" width="110" show-overflow-tooltip />
        <el-table-column prop="actualAmount" label="实付金额" width="110" show-overflow-tooltip />
        <el-table-column prop="source" label="订单来源" width="110" show-overflow-tooltip />
        <el-table-column prop="orderTime" label="下单时间" width="110" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="110" show-overflow-tooltip />
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
        <el-form-item label="订单号">
          <el-input v-model="formData.orderNo"  placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="门店ID">
          <el-input-number v-model="formData.storeId" style="width:100%" />
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input-number v-model="formData.memberId" style="width:100%" />
        </el-form-item>
        <el-form-item label="订单类型">
          <el-select v-model="formData.orderType" placeholder="请选择订单类型" style="width:100%">
            <el-option v-for="o in enumOptions.orderType" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="台桌ID">
          <el-input-number v-model="formData.tableId" style="width:100%" />
        </el-form-item>
        <el-form-item label="桌型ID">
          <el-input-number v-model="formData.tableTypeId" style="width:100%" />
        </el-form-item>
        <el-form-item label="排队ID">
          <el-input-number v-model="formData.queueId" style="width:100%" />
        </el-form-item>
        <el-form-item label="用餐人数">
          <el-input-number v-model="formData.personCount" style="width:100%" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="formData.orderStatus" placeholder="请选择订单状态" style="width:100%">
            <el-option v-for="o in enumOptions.orderStatus" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="formData.payStatus" placeholder="请选择支付状态" style="width:100%">
            <el-option v-for="o in enumOptions.payStatus" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="外卖取餐方式">
          <el-select v-model="formData.takeType" placeholder="请选择外卖取餐方式" style="width:100%">
            <el-option v-for="o in enumOptions.takeType" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="收货地址ID">
          <el-input-number v-model="formData.addressId" style="width:100%" />
        </el-form-item>
        <el-form-item label="地址快照">
          <el-input v-model="formData.addressSnapshot" type="textarea" :rows="2" placeholder="请输入地址快照" />
        </el-form-item>
        <el-form-item label="菜品原价合计">
          <el-input-number v-model="formData.dishAmount" style="width:100%" />
        </el-form-item>
        <el-form-item label="优惠/折扣金额">
          <el-input-number v-model="formData.discountAmount" style="width:100%" />
        </el-form-item>
        <el-form-item label="免单金额">
          <el-input-number v-model="formData.freeAmount" style="width:100%" />
        </el-form-item>
        <el-form-item label="抹零金额">
          <el-input-number v-model="formData.roundingAmount" style="width:100%" />
        </el-form-item>
        <el-form-item label="使用的优惠券ID">
          <el-input-number v-model="formData.couponId" style="width:100%" />
        </el-form-item>
        <el-form-item label="优惠券抵扣金额">
          <el-input-number v-model="formData.couponAmount" style="width:100%" />
        </el-form-item>
        <el-form-item label="配送费">
          <el-input-number v-model="formData.deliveryFee" style="width:100%" />
        </el-form-item>
        <el-form-item label="应付金额">
          <el-input-number v-model="formData.payableAmount" style="width:100%" />
        </el-form-item>
        <el-form-item label="实付金额">
          <el-input-number v-model="formData.actualAmount" style="width:100%" />
        </el-form-item>
        <el-form-item label="会员余额支付金额">
          <el-input-number v-model="formData.memberPayAmount" style="width:100%" />
        </el-form-item>
        <el-form-item label="订单来源">
          <el-input-number v-model="formData.source" style="width:100%" />
        </el-form-item>
        <el-form-item label="操作员工ID">
          <el-input-number v-model="formData.operatorId" style="width:100%" />
        </el-form-item>
        <el-form-item label="订单备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入订单备注" />
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker v-model="formData.orderTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择下单时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="支付时间">
          <el-date-picker v-model="formData.payTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择支付时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="预计送达/自取时间">
          <el-date-picker v-model="formData.expectedTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择预计送达/自取时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="配送/出餐时间">
          <el-date-picker v-model="formData.shippingTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择配送/出餐时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="完成时间">
          <el-date-picker v-model="formData.finishTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择完成时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="取消时间">
          <el-date-picker v-model="formData.cancelTime" type="datetime" value-format="YYYY-MM-DD HH:mm:ss" placeholder="请选择取消时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="取消原因">
          <el-input v-model="formData.cancelReason" type="textarea" :rows="2" placeholder="请输入取消原因" />
        </el-form-item>
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
import storeApi from '@/api/store'
import memberApi from '@/api/member'
import diningTableApi from '@/api/diningTable'

const ordersStore = useOrdersStore()

const enumOptions = {
  orderType: [{ value: 1, label: '堂食' }, { value: 2, label: '外卖' }, { value: 3, label: '自取' }],
  orderStatus: [{ value: 1, label: '待支付' }, { value: 2, label: '待制作' }, { value: 3, label: '制作中' }, { value: 4, label: '待配送' }, { value: 5, label: '配送中' }, { value: 6, label: '待自取' }, { value: 7, label: '已完成' }, { value: 8, label: '已取消' }, { value: 9, label: '已退款' }],
  payStatus: [{ value: 1, label: '待支付' }, { value: 2, label: '已支付' }, { value: 3, label: '已退款' }, { value: 4, label: '部分退款' }],
  takeType: [{ value: 1, label: '外送' }, { value: 2, label: '自取' }]
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
  takeType: null,
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

const dialogVisible = ref(false)
const dialogTitle = ref('新增订单')
const formData = reactive({
  orderNo: '',
  storeId: null,
  memberId: null,
  orderType: null,
  tableId: null,
  tableTypeId: null,
  queueId: null,
  personCount: null,
  orderStatus: null,
  payStatus: null,
  takeType: null,
  addressId: null,
  addressSnapshot: '',
  dishAmount: null,
  discountAmount: null,
  freeAmount: null,
  roundingAmount: null,
  couponId: null,
  couponAmount: null,
  deliveryFee: null,
  payableAmount: null,
  actualAmount: null,
  memberPayAmount: null,
  source: null,
  operatorId: null,
  remark: '',
  orderTime: '',
  payTime: '',
  expectedTime: '',
  shippingTime: '',
  finishTime: '',
  cancelTime: '',
  cancelReason: ''
})

const resetForm = () => {
  formData.orderNo = ''
  formData.storeId = null
  formData.memberId = null
  formData.orderType = null
  formData.tableId = null
  formData.tableTypeId = null
  formData.queueId = null
  formData.personCount = null
  formData.orderStatus = null
  formData.payStatus = null
  formData.takeType = null
  formData.addressId = null
  formData.addressSnapshot = ''
  formData.dishAmount = null
  formData.discountAmount = null
  formData.freeAmount = null
  formData.roundingAmount = null
  formData.couponId = null
  formData.couponAmount = null
  formData.deliveryFee = null
  formData.payableAmount = null
  formData.actualAmount = null
  formData.memberPayAmount = null
  formData.source = null
  formData.operatorId = null
  formData.remark = ''
  formData.orderTime = ''
  formData.payTime = ''
  formData.expectedTime = ''
  formData.shippingTime = ''
  formData.finishTime = ''
  formData.cancelTime = ''
  formData.cancelReason = ''
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
