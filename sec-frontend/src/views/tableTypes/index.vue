<template>
  <div class="tableTypes-page">
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

      <el-table :data="tableTypeStore.list" v-loading="tableTypeStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="typeName" label="桌型名称" width="120" show-overflow-tooltip />
        <el-table-column prop="maxPersons" label="最大容纳人数" width="120" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序号" width="120" show-overflow-tooltip />
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
          v-model:current-page="tableTypeStore.page"
          v-model:page-size="tableTypeStore.size"
          :total="tableTypeStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="tableTypeStore.fetchList"
          @current-change="tableTypeStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="桌型名称">
          <el-input v-model="formData.typeName" placeholder="请输入桌型名称" />
        </el-form-item>
        <el-form-item label="最大容纳人数">
          <el-input v-model="formData.maxPersons" placeholder="请输入最大容纳人数" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input v-model="formData.sort" placeholder="请输入排序号" />
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
import { useTableTypeStore } from '@/stores/tableType'
import tableTypeApi from '@/api/tableType'

const tableTypeStore = useTableTypeStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增桌型')
const formData = reactive({
  id: null,
  storeId: '',
  typeName: '',
  maxPersons: '',
  sort: '',
  status: ''
})

const handleSearch = () => {
  tableTypeStore.page = 1
  tableTypeStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增桌型'
  formData.id = null
  formData.storeId = ''
  formData.typeName = ''
  formData.maxPersons = ''
  formData.sort = ''
  formData.status = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑桌型'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await tableTypeApi.delete(row.id)
      ElMessage.success('删除成功')
      tableTypeStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  tableTypeStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await tableTypeApi.update(formData.id, formData)
  } else {
    await tableTypeApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  tableTypeStore.fetchList()
}

onMounted(() => {
  tableTypeStore.fetchList()
})
</script>

<style scoped>
.tableTypes-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
