<template>
  <div class="printers-page">
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

      <el-table :data="printerStore.list" v-loading="printerStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="printerName" label="打印机名称" width="120" show-overflow-tooltip />
        <el-table-column prop="printerNo" label="打印机编号" width="120" show-overflow-tooltip />
        <el-table-column prop="printerType" label="用途 KITCHEN后厨 RECEIPT前台 TAKEOUT外卖" width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="状态 1启用 0停用" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="printerStore.page"
          v-model:page-size="printerStore.size"
          :total="printerStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="printerStore.fetchList"
          @current-change="printerStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="打印机名称">
          <el-input v-model="formData.printerName" placeholder="请输入打印机名称" />
        </el-form-item>
        <el-form-item label="打印机编号">
          <el-input v-model="formData.printerNo" placeholder="请输入打印机编号" />
        </el-form-item>
        <el-form-item label="用途 KITCHEN后厨 RECEIPT前台 TAKEOUT外卖">
          <el-input v-model="formData.printerType" placeholder="请输入用途 KITCHEN后厨 RECEIPT前台 TAKEOUT外卖" />
        </el-form-item>
        <el-form-item label="状态 1启用 0停用">
          <el-switch v-model="formData.status" />
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
import { usePrinterStore } from '@/stores/printer'
import printerApi from '@/api/printer'

const printerStore = usePrinterStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增打印机')
const formData = reactive({
  id: null,
  storeId: '',
  printerName: '',
  printerNo: '',
  printerType: '',
  status: ''
})

const handleSearch = () => {
  printerStore.page = 1
  printerStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增打印机'
  formData.id = null
  formData.storeId = ''
  formData.printerName = ''
  formData.printerNo = ''
  formData.printerType = ''
  formData.status = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑打印机'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await printerApi.delete(row.id)
      ElMessage.success('删除成功')
      printerStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  printerStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await printerApi.update(formData.id, formData)
  } else {
    await printerApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  printerStore.fetchList()
}

onMounted(() => {
  printerStore.fetchList()
})
</script>

<style scoped>
.printers-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
