<template>
  <div class="dishReviews-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="是否匿名">
          <el-select v-model="queryForm.isAnonymous" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.isAnonymous" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品">
          <el-select v-model="queryForm.dishId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.dishes" :key="o.id" :label="o.dishName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员">
          <el-select v-model="queryForm.memberId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.members" :key="o.id" :label="o.realName || o.nickname || o.phone" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="门店">
          <el-select v-model="queryForm.storeId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
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

      <el-table :data="dishReviewStore.list" v-loading="dishReviewStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="orderId" label="订单ID" width="120" show-overflow-tooltip />
        <el-table-column prop="orderDetailId" label="订单明细ID" width="160" show-overflow-tooltip />
        <el-table-column prop="dishId" label="菜品ID" width="120" show-overflow-tooltip />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="rating" label="评分 1-5" width="120" show-overflow-tooltip />
        <el-table-column prop="content" label="评价内容" width="120" show-overflow-tooltip />
        <el-table-column prop="images" label="评价图片(逗号分隔)" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="dishReviewStore.page"
          v-model:page-size="dishReviewStore.size"
          :total="dishReviewStore.total"
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
        <el-form-item label="订单ID">
          <el-input v-model="formData.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="订单明细ID">
          <el-input v-model="formData.orderDetailId" placeholder="请输入订单明细ID" />
        </el-form-item>
        <el-form-item label="菜品ID">
          <el-input v-model="formData.dishId" placeholder="请输入菜品ID" />
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="评分 1-5">
          <el-input v-model="formData.rating" placeholder="请输入评分 1-5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="formData.content" type="textarea" :rows="3" placeholder="请输入评价内容" />
        </el-form-item>
        <el-form-item label="评价图片(逗号分隔)">
          <el-input v-model="formData.images" placeholder="请输入评价图片(逗号分隔)" />
        </el-form-item>
        <el-form-item label="是否匿名 1是 0否">
          <el-switch v-model="formData.isAnonymous" />
        </el-form-item>
        <el-form-item label="商家回复">
          <el-input v-model="formData.reply" placeholder="请输入商家回复" />
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
import { useDishReviewStore } from '@/stores/dishReview'
import dishReviewApi from '@/api/dishReview'

const dishReviewStore = useDishReviewStore()

import dishApi from '@/api/dish'
import memberApi from '@/api/member'
import storeApi from '@/api/store'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  isAnonymous: [{ value: 1, label: '是' }, { value: 0, label: '否' }]
}
const searchOptions = reactive({
  dishes: [],
  members: [],
  stores: []
})
const loadSearchOptions = () => {
  dishApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.dishes = r.data?.list || [] }).catch(() => {})
  memberApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.members = r.data?.list || [] }).catch(() => {})
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  isAnonymous: null,
  dishId: null,
  memberId: null,
  storeId: null,
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
  dishReviewStore.page = 1
  dishReviewStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  dishReviewStore.page = 1
  dishReviewStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  dishReviewStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增菜品评价')
const formData = reactive({
  id: null,
  orderId: '',
  orderDetailId: '',
  dishId: '',
  memberId: '',
  storeId: '',
  rating: '',
  content: '',
  images: '',
  isAnonymous: '',
  reply: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增菜品评价'
  formData.id = null
  formData.orderId = ''
  formData.orderDetailId = ''
  formData.dishId = ''
  formData.memberId = ''
  formData.storeId = ''
  formData.rating = ''
  formData.content = ''
  formData.images = ''
  formData.isAnonymous = ''
  formData.reply = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜品评价'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await dishReviewApi.delete(row.id)
      ElMessage.success('删除成功')
      dishReviewStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  dishReviewStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await dishReviewApi.update(formData.id, formData)
  } else {
    await dishReviewApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  dishReviewStore.fetchList()
}

onMounted(() => {
  dishReviewStore.fetchList()
})
</script>

<style scoped>
.dishReviews-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
