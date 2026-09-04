<template>
  <div class="diningTables-page">
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

      <el-table :data="diningTableStore.list" v-loading="diningTableStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="tableTypeId" label="桌型ID" width="120" show-overflow-tooltip />
        <el-table-column prop="tableNo" label="桌号" width="120" show-overflow-tooltip />
        <el-table-column prop="tableName" label="桌位名称" width="120" show-overflow-tooltip />
        <el-table-column prop="qrCode" label="桌位二维码(扫码点餐)" width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="台桌状态 1空闲 2待点餐 3用餐中 4预结账 5已结账" width="160" show-overflow-tooltip />
        <el-table-column prop="personCount" label="当前用餐人数" width="120" show-overflow-tooltip />
        <el-table-column prop="enableReservation" label="是否可预约 1是 0否" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="diningTableStore.page"
          v-model:page-size="diningTableStore.size"
          :total="diningTableStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="diningTableStore.fetchList"
          @current-change="diningTableStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="桌型ID">
          <el-input v-model="formData.tableTypeId" placeholder="请输入桌型ID" />
        </el-form-item>
        <el-form-item label="桌号">
          <el-input v-model="formData.tableNo" placeholder="请输入桌号" />
        </el-form-item>
        <el-form-item label="桌位名称">
          <el-input v-model="formData.tableName" placeholder="请输入桌位名称" />
        </el-form-item>
        <el-form-item label="桌位二维码(扫码点餐)">
          <el-input v-model="formData.qrCode" placeholder="请输入桌位二维码(扫码点餐)" />
        </el-form-item>
        <el-form-item label="台桌状态 1空闲 2待点餐 3用餐中 4预结账 5已结账">
          <el-switch v-model="formData.status" />
        </el-form-item>
        <el-form-item label="当前用餐人数">
          <el-input-number v-model="formData.personCount" placeholder="请输入当前用餐人数" style="width:100%" />
        </el-form-item>
        <el-form-item label="是否可预约 1是 0否">
          <el-input v-model="formData.enableReservation" placeholder="请输入是否可预约 1是 0否" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input v-model="formData.sort" placeholder="请输入排序号" />
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
import { useDiningTableStore } from '@/stores/diningTable'
import diningTableApi from '@/api/diningTable'

const diningTableStore = useDiningTableStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增台桌')
const formData = reactive({
  id: null,
  storeId: '',
  tableTypeId: '',
  tableNo: '',
  tableName: '',
  qrCode: '',
  status: '',
  personCount: null,
  enableReservation: '',
  sort: ''
})

const handleSearch = () => {
  diningTableStore.page = 1
  diningTableStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增台桌'
  formData.id = null
  formData.storeId = ''
  formData.tableTypeId = ''
  formData.tableNo = ''
  formData.tableName = ''
  formData.qrCode = ''
  formData.status = ''
  formData.personCount = null
  formData.enableReservation = ''
  formData.sort = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑台桌'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await diningTableApi.delete(row.id)
      ElMessage.success('删除成功')
      diningTableStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  diningTableStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await diningTableApi.update(formData.id, formData)
  } else {
    await diningTableApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  diningTableStore.fetchList()
}

onMounted(() => {
  diningTableStore.fetchList()
})
</script>

<style scoped>
.diningTables-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
