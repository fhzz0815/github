<template>
  <div class="dishes-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入菜品名称/菜品编号" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="是否推荐">
          <el-select v-model="queryForm.isRecommend" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.isRecommend" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否新品">
          <el-select v-model="queryForm.isNew" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.isNew" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否售罄">
          <el-select v-model="queryForm.isSoldOut" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.isSoldOut" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.status" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="门店">
          <el-select v-model="queryForm.storeId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品分类">
          <el-select v-model="queryForm.categoryId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.dishCategories" :key="o.id" :label="o.categoryName" :value="o.id" />
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

      <el-table :data="dishStore.list" v-loading="dishStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="categoryId" label="菜品分类ID" width="120" show-overflow-tooltip />
        <el-table-column prop="dishName" label="菜品名称" width="120" show-overflow-tooltip />
        <el-table-column prop="dishNo" label="菜品编号" width="120" show-overflow-tooltip />
        <el-table-column prop="image" label="菜品图片URL" width="160" show-overflow-tooltip />
        <el-table-column prop="description" label="菜品描述" width="120" show-overflow-tooltip />
        <el-table-column prop="price" label="单价(元)" width="120" show-overflow-tooltip />
        <el-table-column prop="originPrice" label="划线原价(元)，用于展示促销" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="菜品分类ID">
          <el-input v-model="formData.categoryId" placeholder="请输入菜品分类ID" />
        </el-form-item>
        <el-form-item label="菜品名称">
          <el-input v-model="formData.dishName" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="菜品编号">
          <el-input v-model="formData.dishNo" placeholder="请输入菜品编号" />
        </el-form-item>
        <el-form-item label="菜品图片URL">
          <el-input v-model="formData.image" placeholder="请输入菜品图片URL" />
        </el-form-item>
        <el-form-item label="菜品描述">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入菜品描述" />
        </el-form-item>
        <el-form-item label="单价(元)">
          <el-input-number v-model="formData.price" placeholder="请输入单价(元)" style="width:100%" />
        </el-form-item>
        <el-form-item label="划线原价(元)，用于展示促销">
          <el-input-number v-model="formData.originPrice" placeholder="请输入划线原价(元)，用于展示促销" style="width:100%" />
        </el-form-item>
        <el-form-item label="售卖单位(份/杯/例等)">
          <el-input v-model="formData.unit" placeholder="请输入售卖单位(份/杯/例等)" />
        </el-form-item>
        <el-form-item label="默认口味ID">
          <el-input v-model="formData.tasteId" placeholder="请输入默认口味ID" />
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

const dishStore = useDishStore()

import storeApi from '@/api/store'
import dishCategoryApi from '@/api/dishCategory'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  isRecommend: [{ value: 1, label: '是' }, { value: 0, label: '否' }],
  isNew: [{ value: 1, label: '是' }, { value: 0, label: '否' }],
  isSoldOut: [{ value: 1, label: '是' }, { value: 0, label: '否' }],
  status: [{ value: 1, label: '上架' }, { value: 0, label: '下架' }]
}
const searchOptions = reactive({
  stores: [],
  dishCategories: []
})
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
  isSoldOut: null,
  status: null,
  storeId: null,
  categoryId: null,
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
  dishStore.page = 1
  dishStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  dishStore.page = 1
  dishStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  dishStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增菜品')
const formData = reactive({
  id: null,
  storeId: '',
  categoryId: '',
  dishName: '',
  dishNo: '',
  image: '',
  description: '',
  price: null,
  originPrice: null,
  unit: '',
  tasteId: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增菜品'
  formData.id = null
  formData.storeId = ''
  formData.categoryId = ''
  formData.dishName = ''
  formData.dishNo = ''
  formData.image = ''
  formData.description = ''
  formData.price = null
  formData.originPrice = null
  formData.unit = ''
  formData.tasteId = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜品'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await dishApi.delete(row.id)
      ElMessage.success('删除成功')
      dishStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  dishStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await dishApi.update(formData.id, formData)
  } else {
    await dishApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  dishStore.fetchList()
}

onMounted(() => {
  dishStore.fetchList()
})
</script>

<style scoped>
.dishes-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
