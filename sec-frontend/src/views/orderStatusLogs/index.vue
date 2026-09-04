<template>
  <div class="orderStatusLogs-page">
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

      <el-table :data="orderStatusLogStore.list" v-loading="orderStatusLogStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderId" label="订单ID" width="120" show-overflow-tooltip />
        <el-table-column prop="fromStatus" label="原状态" width="120" show-overflow-tooltip />
        <el-table-column prop="toStatus" label="新状态" width="120" show-overflow-tooltip />
        <el-table-column prop="operatorType" label="操作方 CLIENT客户端 STAFF员工 SYSTEM系统" width="160" show-overflow-tooltip />
        <el-table-column prop="operatorId" label="操作者ID" width="120" show-overflow-tooltip />
        <el-table-column prop="remark" label="说明" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="orderStatusLogStore.page"
          v-model:page-size="orderStatusLogStore.size"
          :total="orderStatusLogStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="orderStatusLogStore.fetchList"
          @current-change="orderStatusLogStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="订单ID">
          <el-input v-model="formData.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="原状态">
          <el-switch v-model="formData.fromStatus" />
        </el-form-item>
        <el-form-item label="新状态">
          <el-switch v-model="formData.toStatus" />
        </el-form-item>
        <el-form-item label="操作方 CLIENT客户端 STAFF员工 SYSTEM系统">
          <el-input v-model="formData.operatorType" placeholder="请输入操作方 CLIENT客户端 STAFF员工 SYSTEM系统" />
        </el-form-item>
        <el-form-item label="操作者ID">
          <el-input v-model="formData.operatorId" placeholder="请输入操作者ID" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入说明" />
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
import { useOrderStatusLogStore } from '@/stores/orderStatusLog'
import orderStatusLogApi from '@/api/orderStatusLog'

const orderStatusLogStore = useOrderStatusLogStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增订单日志')
const formData = reactive({
  id: null,
  orderId: '',
  fromStatus: '',
  toStatus: '',
  operatorType: '',
  operatorId: '',
  remark: ''
})

const handleSearch = () => {
  orderStatusLogStore.page = 1
  orderStatusLogStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增订单日志'
  formData.id = null
  formData.orderId = ''
  formData.fromStatus = ''
  formData.toStatus = ''
  formData.operatorType = ''
  formData.operatorId = ''
  formData.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑订单日志'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await orderStatusLogApi.delete(row.id)
      ElMessage.success('删除成功')
      orderStatusLogStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  orderStatusLogStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await orderStatusLogApi.update(formData.id, formData)
  } else {
    await orderStatusLogApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  orderStatusLogStore.fetchList()
}

onMounted(() => {
  orderStatusLogStore.fetchList()
})
</script>

<style scoped>
.orderStatusLogs-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
