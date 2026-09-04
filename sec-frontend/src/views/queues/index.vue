<template>
  <div class="queues-page">
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

      <el-table :data="queueStore.list" v-loading="queueStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="queueNo" label="排队号(如A001)" width="160" show-overflow-tooltip />
        <el-table-column prop="tableTypeId" label="期望桌型ID" width="120" show-overflow-tooltip />
        <el-table-column prop="persons" label="就餐人数" width="120" show-overflow-tooltip />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系电话" width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="状态 1排队中 2已叫号 3已过号 4已就位 5已取消" width="160" show-overflow-tooltip />
        <el-table-column prop="callCount" label="叫号次数" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queueStore.page"
          v-model:page-size="queueStore.size"
          :total="queueStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="queueStore.fetchList"
          @current-change="queueStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="排队号(如A001)">
          <el-input v-model="formData.queueNo" placeholder="请输入排队号(如A001)" />
        </el-form-item>
        <el-form-item label="期望桌型ID">
          <el-input v-model="formData.tableTypeId" placeholder="请输入期望桌型ID" />
        </el-form-item>
        <el-form-item label="就餐人数">
          <el-input v-model="formData.persons" placeholder="请输入就餐人数" />
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="状态 1排队中 2已叫号 3已过号 4已就位 5已取消">
          <el-switch v-model="formData.status" />
        </el-form-item>
        <el-form-item label="叫号次数">
          <el-input-number v-model="formData.callCount" placeholder="请输入叫号次数" style="width:100%" />
        </el-form-item>
        <el-form-item label="过号次数">
          <el-input-number v-model="formData.overCount" placeholder="请输入过号次数" style="width:100%" />
        </el-form-item>
        <el-form-item label="最近叫号时间">
          <el-date-picker v-model="formData.callTime" type="datetime" placeholder="请选择最近叫号时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
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
import { useQueueStore } from '@/stores/queue'
import queueApi from '@/api/queue'

const queueStore = useQueueStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增排队')
const formData = reactive({
  id: null,
  storeId: '',
  queueNo: '',
  tableTypeId: '',
  persons: '',
  memberId: '',
  phone: '',
  status: '',
  callCount: null,
  overCount: null,
  callTime: ''
})

const handleSearch = () => {
  queueStore.page = 1
  queueStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增排队'
  formData.id = null
  formData.storeId = ''
  formData.queueNo = ''
  formData.tableTypeId = ''
  formData.persons = ''
  formData.memberId = ''
  formData.phone = ''
  formData.status = ''
  formData.callCount = null
  formData.overCount = null
  formData.callTime = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑排队'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await queueApi.delete(row.id)
      ElMessage.success('删除成功')
      queueStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  queueStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await queueApi.update(formData.id, formData)
  } else {
    await queueApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  queueStore.fetchList()
}

onMounted(() => {
  queueStore.fetchList()
})
</script>

<style scoped>
.queues-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
