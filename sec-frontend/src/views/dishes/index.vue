<template>
  <div class="dishes-page">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入菜品名称/编号" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="是否推荐">
          <el-select v-model="queryForm.isRecommend" clearable placeholder="全部" style="width: 110px">
            <el-option v-for="o in YES_NO" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否新品">
          <el-select v-model="queryForm.isNew" clearable placeholder="全部" style="width: 110px">
            <el-option v-for="o in YES_NO" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部" style="width: 110px">
            <el-option v-for="o in DISH_STATUS" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="门店">
          <el-select v-model="queryForm.storeId" clearable filterable placeholder="全部门店" style="width: 150px">
            <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品分类">
          <el-select v-model="queryForm.categoryId" clearable filterable placeholder="全部分类" style="width: 150px">
            <el-option v-for="o in searchOptions.dishCategories" :key="o.id" :label="o.categoryName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="创建时间">
          <el-date-picker v-model="queryForm.dateRange" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" style="width: 240px" />
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
        <el-button type="primary" @click="handleAdd">新增菜品</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>

      <el-table :data="dishStore.list" v-loading="dishStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column label="所属门店" width="140" show-overflow-tooltip>
          <template #default="{ row }">
            {{ findLabel(searchOptions.stores, row.storeId, 'storeName') }}
          </template>
        </el-table-column>
        <el-table-column label="菜品分类" width="120" show-overflow-tooltip>
          <template #default="{ row }">
            {{ findLabel(searchOptions.dishCategories, row.categoryId, 'categoryName') }}
          </template>
        </el-table-column>
        <el-table-column prop="dishName" label="菜品名称" width="130" show-overflow-tooltip />
        <el-table-column prop="dishNo" label="编号" width="100" />
        <el-table-column prop="price" label="单价" width="80" align="right">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column prop="unit" label="单位" width="60" align="center" />
        <el-table-column prop="saleCount" label="销量" width="65" align="center" />
        <el-table-column label="状态" width="70" align="center">
          <template #default="{ row }">
            <el-tag :type="getEnumType('dishStatus', row.status)" size="small">
              {{ getEnumLabel('dishStatus', row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="60" align="center" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="dishStore.page"
          v-model:page-size="dishStore.size"
          :total="dishStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="onPageChange"
          @current-change="onPageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="650px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="所属门店" prop="storeId">
              <el-select v-model="formData.storeId" filterable placeholder="请选择门店" style="width: 100%">
                <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="菜品分类" prop="categoryId">
              <el-select v-model="formData.categoryId" filterable placeholder="请选择分类" style="width: 100%">
                <el-option v-for="o in searchOptions.dishCategories" :key="o.id" :label="o.categoryName" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="菜品名称" prop="dishName">
          <el-input v-model="formData.dishName" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="8">
            <el-form-item label="编号" prop="dishNo">
              <el-input v-model="formData.dishNo" placeholder="自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单价" prop="price">
              <el-input-number v-model="formData.price" :min="0" :precision="2" style="width: 100%" placeholder="请输入单价" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单位" prop="unit">
              <el-input v-model="formData.unit" placeholder="份/杯/例" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="菜品描述">
          <el-input v-model="formData.description" type="textarea" :rows="2" placeholder="请简单描述这道菜品" />
        </el-form-item>
        <el-form-item label="菜品图片">
          <el-input v-model="formData.image" placeholder="粘贴图片链接地址" />
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
import { useDishStore } from '@/stores/dish'
import dishApi from '@/api/dish'
import storeApi from '@/api/store'
import dishCategoryApi from '@/api/dishCategory'
import { YES_NO, DISH_STATUS, getEnumLabel, getEnumType } from '@/constants/enums'
import { buildSearchParams, findLabel } from '@/utils/helpers'

const dishStore = useDishStore()

// 加载搜索下拉数据
const searchOptions = reactive({ stores: [], dishCategories: [] })
const loadSearchOptions = () => {
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
  dishCategoryApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.dishCategories = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  isRecommend: null,
  isNew: null,
  status: null,
  storeId: null,
  categoryId: null,
  dateRange: null
})

const buildParams = () => buildSearchParams(queryForm)
const handleSearch = () => { dishStore.page = 1; dishStore.fetchList(buildParams()) }
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  dishStore.page = 1
  dishStore.fetchList()
}
const onPageChange = () => dishStore.fetchList(buildParams())
const handleRefresh = () => dishStore.fetchList()

// 表单校验规则
const formRef = ref(null)
const formRules = {
  storeId: [{ required: true, message: '请选择所属门店', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择菜品分类', trigger: 'change' }],
  dishName: [{ required: true, message: '请输入菜品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入售卖单位', trigger: 'blur' }]
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增菜品')
const formData = reactive({
  id: null, storeId: '', categoryId: '', dishName: '',
  dishNo: '', image: '', description: '', price: null,
  originPrice: null, unit: '', tasteId: ''
})

const handleAdd = () => {
  dialogTitle.value = '新增菜品'
  Object.keys(formData).forEach((k) => { formData[k] = k === 'price' || k === 'originPrice' ? null : '' })
  formData.id = null
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜品'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这道菜品吗？', '提示', { type: 'warning' })
    .then(async () => { await dishApi.delete(row.id); ElMessage.success('删除成功'); dishStore.fetchList() })
    .catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.id) {
    await dishApi.update(formData.id, formData)
  } else {
    await dishApi.create(formData)
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  dishStore.fetchList()
}

onMounted(() => { dishStore.fetchList() })
</script>

<style scoped>
.dishes-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
