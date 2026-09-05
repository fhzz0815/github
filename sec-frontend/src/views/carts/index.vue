<template>
  <div class="carts-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入菜品名称快照/规格快照/口味快照" clearable style="width: 220px" @keyup.enter="handleSearch" />
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
        <el-form-item label="菜品">
          <el-select v-model="queryForm.dishId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.dishes" :key="o.id" :label="o.dishName" :value="o.id" />
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
          @size-change="onPageChange"
          @current-change="onPageChange"
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

import memberApi from '@/api/member'
import storeApi from '@/api/store'
import dishApi from '@/api/dish'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {

}
const searchOptions = reactive({
  members: [],
  stores: [],
  dishes: []
})
const loadSearchOptions = () => {
  memberApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.members = r.data?.list || [] }).catch(() => {})
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
  dishApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.dishes = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  memberId: null,
  storeId: null,
  dishId: null,
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
  cartStore.page = 1
  cartStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  cartStore.page = 1
  cartStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  cartStore.fetchList(buildParams())
}

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
