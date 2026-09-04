<template>
  <div class="ingredients-page">
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

      <el-table :data="ingredientStore.list" v-loading="ingredientStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="categoryId" label="原料类别ID" width="120" show-overflow-tooltip />
        <el-table-column prop="ingredientName" label="原料名称" width="160" show-overflow-tooltip />
        <el-table-column prop="unit" label="计量单位" width="120" show-overflow-tooltip />
        <el-table-column prop="spec" label="规格说明" width="120" show-overflow-tooltip />
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
          v-model:current-page="ingredientStore.page"
          v-model:page-size="ingredientStore.size"
          :total="ingredientStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="ingredientStore.fetchList"
          @current-change="ingredientStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="原料类别ID">
          <el-input v-model="formData.categoryId" placeholder="请输入原料类别ID" />
        </el-form-item>
        <el-form-item label="原料名称">
          <el-input v-model="formData.ingredientName" placeholder="请输入原料名称" />
        </el-form-item>
        <el-form-item label="计量单位">
          <el-input v-model="formData.unit" placeholder="请输入计量单位" />
        </el-form-item>
        <el-form-item label="规格说明">
          <el-input v-model="formData.spec" placeholder="请输入规格说明" />
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
import { useIngredientStore } from '@/stores/ingredient'
import ingredientApi from '@/api/ingredient'

const ingredientStore = useIngredientStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增原料')
const formData = reactive({
  id: null,
  storeId: '',
  categoryId: '',
  ingredientName: '',
  unit: '',
  spec: '',
  status: ''
})

const handleSearch = () => {
  ingredientStore.page = 1
  ingredientStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增原料'
  formData.id = null
  formData.storeId = ''
  formData.categoryId = ''
  formData.ingredientName = ''
  formData.unit = ''
  formData.spec = ''
  formData.status = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑原料'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await ingredientApi.delete(row.id)
      ElMessage.success('删除成功')
      ingredientStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  ingredientStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await ingredientApi.update(formData.id, formData)
  } else {
    await ingredientApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  ingredientStore.fetchList()
}

onMounted(() => {
  ingredientStore.fetchList()
})
</script>

<style scoped>
.ingredients-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
