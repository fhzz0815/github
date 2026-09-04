<template>
  <div class="dishReviews-page">
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
          @size-change="dishReviewStore.fetchList"
          @current-change="dishReviewStore.fetchList"
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

const queryForm = reactive({ keyword: '' })

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

const handleSearch = () => {
  dishReviewStore.page = 1
  dishReviewStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

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
