<template>
  <div class="dishIngredientRels-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="菜品">
          <el-select v-model="queryForm.dishId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.dishes" :key="o.id" :label="o.dishName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="原料">
          <el-select v-model="queryForm.ingredientId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.ingredients" :key="o.id" :label="o.ingredientName" :value="o.id" />
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

      <el-table :data="dishIngredientRelStore.list" v-loading="dishIngredientRelStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="dishId" label="菜品ID" width="120" show-overflow-tooltip />
        <el-table-column prop="ingredientId" label="原料ID" width="120" show-overflow-tooltip />
        <el-table-column prop="quantity" label="用量" width="120" show-overflow-tooltip />
        <el-table-column prop="unit" label="单位" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="dishIngredientRelStore.page"
          v-model:page-size="dishIngredientRelStore.size"
          :total="dishIngredientRelStore.total"
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
        <el-form-item label="菜品ID">
          <el-input v-model="formData.dishId" placeholder="请输入菜品ID" />
        </el-form-item>
        <el-form-item label="原料ID">
          <el-input v-model="formData.ingredientId" placeholder="请输入原料ID" />
        </el-form-item>
        <el-form-item label="用量">
          <el-input v-model="formData.quantity" placeholder="请输入用量" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="formData.unit" placeholder="请输入单位" />
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
import { useDishIngredientRelStore } from '@/stores/dishIngredientRel'
import dishIngredientRelApi from '@/api/dishIngredientRel'

const dishIngredientRelStore = useDishIngredientRelStore()

import dishApi from '@/api/dish'
import ingredientApi from '@/api/ingredient'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {

}
const searchOptions = reactive({
  dishes: [],
  ingredients: []
})
const loadSearchOptions = () => {
  dishApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.dishes = r.data?.list || [] }).catch(() => {})
  ingredientApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.ingredients = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  dishId: null,
  ingredientId: null,
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
  dishIngredientRelStore.page = 1
  dishIngredientRelStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  dishIngredientRelStore.page = 1
  dishIngredientRelStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  dishIngredientRelStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增菜品原料')
const formData = reactive({
  id: null,
  dishId: '',
  ingredientId: '',
  quantity: '',
  unit: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增菜品原料'
  formData.id = null
  formData.dishId = ''
  formData.ingredientId = ''
  formData.quantity = ''
  formData.unit = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜品原料'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await dishIngredientRelApi.delete(row.id)
      ElMessage.success('删除成功')
      dishIngredientRelStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  dishIngredientRelStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await dishIngredientRelApi.update(formData.id, formData)
  } else {
    await dishIngredientRelApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  dishIngredientRelStore.fetchList()
}

onMounted(() => {
  dishIngredientRelStore.fetchList()
})
</script>

<style scoped>
.dishIngredientRels-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
