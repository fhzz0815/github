<template>
  <div class="orderDetails-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入菜品名称快照/规格快照/口味快照" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="制作状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.status" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品">
          <el-select v-model="queryForm.dishId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.dishes" :key="o.id" :label="o.dishName" :value="o.id" />
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

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>

      <el-table :data="orderDetailStore.list" v-loading="orderDetailStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderId" label="订单ID" width="120" show-overflow-tooltip />
        <el-table-column prop="dishId" label="菜品ID" width="120" show-overflow-tooltip />
        <el-table-column prop="dishName" label="菜品名称快照" width="120" show-overflow-tooltip />
        <el-table-column prop="dishImage" label="菜品图片快照" width="120" show-overflow-tooltip />
        <el-table-column prop="dishPrice" label="成交单价(元)" width="160" show-overflow-tooltip />
        <el-table-column prop="specName" label="规格快照" width="120" show-overflow-tooltip />
        <el-table-column prop="tasteName" label="口味快照" width="120" show-overflow-tooltip />
        <el-table-column prop="quantity" label="数量" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="orderDetailStore.page"
          v-model:page-size="orderDetailStore.size"
          :total="orderDetailStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="onPageChange"
          @current-change="onPageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="订单ID">
          <el-input v-model="formData.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="菜品ID">
          <el-input v-model="formData.dishId" placeholder="请输入菜品ID" />
        </el-form-item>
        <el-form-item label="菜品名称快照">
          <el-input v-model="formData.dishName" placeholder="请输入菜品名称快照" />
        </el-form-item>
        <el-form-item label="菜品图片快照">
          <el-input v-model="formData.dishImage" placeholder="请输入菜品图片快照" />
        </el-form-item>
        <el-form-item label="成交单价(元)">
          <el-input-number v-model="formData.dishPrice" placeholder="请输入成交单价(元)" style="width:100%" />
        </el-form-item>
        <el-form-item label="规格快照">
          <el-input v-model="formData.specName" placeholder="请输入规格快照" />
        </el-form-item>
        <el-form-item label="口味快照">
          <el-input v-model="formData.tasteName" placeholder="请输入口味快照" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="formData.quantity" placeholder="请输入数量" />
        </el-form-item>
        <el-form-item label="小计金额">
          <el-input v-model="formData.subtotal" placeholder="请输入小计金额" />
        </el-form-item>
        <el-form-item label="已退金额(元)，退菜时累加">
          <el-input-number v-model="formData.refundAmount" placeholder="请输入已退金额(元)，退菜时累加" style="width:100%" />
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
import { useOrderDetailStore } from '@/stores/orderDetail'
import orderDetailApi from '@/api/orderDetail'

const orderDetailStore = useOrderDetailStore()

import dishApi from '@/api/dish'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  status: [{ value: 1, label: '待制作' }, { value: 2, label: '制作中' }, { value: 3, label: '已上齐' }, { value: 4, label: '退菜' }]
}
const searchOptions = reactive({
  dishes: []
})
const loadSearchOptions = () => {
  dishApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.dishes = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  status: null,
  dishId: null,
  dateRange: null
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
  orderDetailStore.page = 1
  orderDetailStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  orderDetailStore.page = 1
  orderDetailStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  orderDetailStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增订单明细')
const formData = reactive({
  id: null,
  orderId: '',
  dishId: '',
  dishName: '',
  dishImage: '',
  dishPrice: null,
  specName: '',
  tasteName: '',
  quantity: '',
  subtotal: '',
  refundAmount: null
})



const handleAdd = () => {
  dialogTitle.value = '新增订单明细'
  formData.id = null
  formData.orderId = ''
  formData.dishId = ''
  formData.dishName = ''
  formData.dishImage = ''
  formData.dishPrice = null
  formData.specName = ''
  formData.tasteName = ''
  formData.quantity = ''
  formData.subtotal = ''
  formData.refundAmount = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑订单明细'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await orderDetailApi.delete(row.id)
      ElMessage.success('删除成功')
      orderDetailStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  orderDetailStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await orderDetailApi.update(formData.id, formData)
  } else {
    await orderDetailApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  orderDetailStore.fetchList()
}

onMounted(() => {
  orderDetailStore.fetchList()
})
</script>

<style scoped>
.orderDetails-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
