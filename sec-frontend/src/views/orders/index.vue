<template>
  <div class="orderses-page">
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

    <!-- 操作按钮 -->
    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>

      <!-- 数据表格 -->
      <el-table :data="ordersStore.list" v-loading="ordersStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="ordersStore.page"
          v-model:page-size="ordersStore.size"
          :total="ordersStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="ordersStore.fetchList"
          @current-change="ordersStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="名称">
          <el-input v-model="formData.name" placeholder="请输入名称" />
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
import { useOrdersStore } from '@/stores/orders'
import ordersApi from '@/api/orders'

// 引入对应的状态管理
const ordersStore = useOrdersStore()

// 搜索表单
const queryForm = reactive({ keyword: '' })

// 弹窗相关
const dialogVisible = ref(false)
const dialogTitle = ref('新增订单管理')
const formData = reactive({ id: null, name: '' })

// 搜索
const handleSearch = () => {
  ordersStore.page = 1
  ordersStore.fetchList({ keyword: queryForm.keyword })
}

// 重置
const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增订单管理'
  formData.id = null
  formData.name = ''
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑订单管理'
  formData.id = row.id
  formData.name = row.name
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await ordersApi.delete(row.id)
      ElMessage.success('删除成功')
      ordersStore.fetchList()
    })
    .catch(() => {})
}

// 刷新
const handleRefresh = () => {
  ordersStore.fetchList()
}

// 提交
const handleSubmit = async () => {
  if (formData.id) {
    await ordersApi.update(formData.id, formData)
  } else {
    await ordersApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  ordersStore.fetchList()
}

// 页面加载时获取数据
onMounted(() => {
  ordersStore.fetchList()
})
</script>

<style scoped>
.orderses-page {
  padding: 16px;
}
.search-card {
  margin-bottom: 16px;
}
.table-toolbar {
  margin-bottom: 16px;
}
.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
