<template>
  <div class="refunds-page">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入关键字" clearable />
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

      <el-table :data="refundStore.list" v-loading="refundStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="refundNo" label="退单号" width="120" show-overflow-tooltip />
        <el-table-column prop="orderId" label="订单ID" width="120" show-overflow-tooltip />
        <el-table-column prop="orderDetailId" label="订单明细ID(退菜时)" width="160" show-overflow-tooltip />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="refundType" label="退单类型 1整单退 2退菜" width="160" show-overflow-tooltip />
        <el-table-column prop="dishName" label="退菜名称" width="120" show-overflow-tooltip />
        <el-table-column prop="quantity" label="退菜数量" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="refundStore.page"
          v-model:page-size="refundStore.size"
          :total="refundStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="refundStore.fetchList"
          @current-change="refundStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="退单号">
          <el-input v-model="formData.refundNo" placeholder="请输入退单号" />
        </el-form-item>
        <el-form-item label="订单ID">
          <el-input v-model="formData.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="订单明细ID(退菜时)">
          <el-input v-model="formData.orderDetailId" placeholder="请输入订单明细ID(退菜时)" />
        </el-form-item>
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="退单类型 1整单退 2退菜">
          <el-input v-model="formData.refundType" placeholder="请输入退单类型 1整单退 2退菜" />
        </el-form-item>
        <el-form-item label="退菜名称">
          <el-input v-model="formData.dishName" placeholder="请输入退菜名称" />
        </el-form-item>
        <el-form-item label="退菜数量">
          <el-input v-model="formData.quantity" placeholder="请输入退菜数量" />
        </el-form-item>
        <el-form-item label="退款金额(元)">
          <el-input-number v-model="formData.amount" placeholder="请输入退款金额(元)" style="width:100%" />
        </el-form-item>
        <el-form-item label="退单原因">
          <el-input v-model="formData.reason" placeholder="请输入退单原因" />
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
import { useRefundStore } from '@/stores/refund'
import refundApi from '@/api/refund'

const refundStore = useRefundStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增退单')
const formData = reactive({
  id: null,
  refundNo: '',
  orderId: '',
  orderDetailId: '',
  storeId: '',
  memberId: '',
  refundType: '',
  dishName: '',
  quantity: '',
  amount: null,
  reason: ''
})

const handleSearch = () => {
  refundStore.page = 1
  refundStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增退单'
  formData.id = null
  formData.refundNo = ''
  formData.orderId = ''
  formData.orderDetailId = ''
  formData.storeId = ''
  formData.memberId = ''
  formData.refundType = ''
  formData.dishName = ''
  formData.quantity = ''
  formData.amount = null
  formData.reason = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑退单'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await refundApi.delete(row.id)
      ElMessage.success('删除成功')
      refundStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  refundStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await refundApi.update(formData.id, formData)
  } else {
    await refundApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  refundStore.fetchList()
}

onMounted(() => {
  refundStore.fetchList()
})
</script>

<style scoped>
.refunds-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
