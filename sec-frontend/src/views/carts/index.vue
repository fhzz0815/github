<template>
  <div class="carts-page">
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

      <el-table :data="cartStore.list" v-loading="cartStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="dishId" label="菜品ID" width="120" show-overflow-tooltip />
        <el-table-column prop="dishName" label="菜品名称快照" width="120" show-overflow-tooltip />
        <el-table-column prop="dishImage" label="菜品图片快照" width="120" show-overflow-tooltip />
        <el-table-column prop="specName" label="规格快照" width="120" show-overflow-tooltip />
        <el-table-column prop="tasteName" label="口味快照" width="120" show-overflow-tooltip />
        <el-table-column prop="price" label="加入时单价" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="cartStore.page"
          v-model:page-size="cartStore.size"
          :total="cartStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="cartStore.fetchList"
          @current-change="cartStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="菜品ID">
          <el-input v-model="formData.dishId" placeholder="请输入菜品ID" />
        </el-form-item>
        <el-form-item label="菜品名称快照">
          <el-input v-model="formData.dishName" placeholder="请输入菜品名称快照" />
        </el-form-item>
        <el-form-item label="菜品图片快照">
          <el-input v-model="formData.dishImage" placeholder="请输入菜品图片快照" />
        </el-form-item>
        <el-form-item label="规格快照">
          <el-input v-model="formData.specName" placeholder="请输入规格快照" />
        </el-form-item>
        <el-form-item label="口味快照">
          <el-input v-model="formData.tasteName" placeholder="请输入口味快照" />
        </el-form-item>
        <el-form-item label="加入时单价">
          <el-input-number v-model="formData.price" placeholder="请输入加入时单价" style="width:100%" />
        </el-form-item>
        <el-form-item label="数量">
          <el-input v-model="formData.quantity" placeholder="请输入数量" />
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
import { useCartStore } from '@/stores/cart'
import cartApi from '@/api/cart'

const cartStore = useCartStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增购物车')
const formData = reactive({
  id: null,
  memberId: '',
  storeId: '',
  dishId: '',
  dishName: '',
  dishImage: '',
  specName: '',
  tasteName: '',
  price: null,
  quantity: ''
})

const handleSearch = () => {
  cartStore.page = 1
  cartStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增购物车'
  formData.id = null
  formData.memberId = ''
  formData.storeId = ''
  formData.dishId = ''
  formData.dishName = ''
  formData.dishImage = ''
  formData.specName = ''
  formData.tasteName = ''
  formData.price = null
  formData.quantity = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑购物车'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await cartApi.delete(row.id)
      ElMessage.success('删除成功')
      cartStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  cartStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await cartApi.update(formData.id, formData)
  } else {
    await cartApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  cartStore.fetchList()
}

onMounted(() => {
  cartStore.fetchList()
})
</script>

<style scoped>
.carts-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
