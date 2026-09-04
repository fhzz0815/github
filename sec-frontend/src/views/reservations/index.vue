<template>
  <div class="reservations-page">
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

      <el-table :data="reservationStore.list" v-loading="reservationStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="tableId" label="台桌ID" width="120" show-overflow-tooltip />
        <el-table-column prop="tableTypeId" label="桌型ID" width="120" show-overflow-tooltip />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="contactName" label="联系人姓名" width="120" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系电话" width="120" show-overflow-tooltip />
        <el-table-column prop="reservationDate" label="预约日期" width="160" show-overflow-tooltip />
        <el-table-column prop="reservationTime" label="预约时间" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="reservationStore.page"
          v-model:page-size="reservationStore.size"
          :total="reservationStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="reservationStore.fetchList"
          @current-change="reservationStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="台桌ID">
          <el-input v-model="formData.tableId" placeholder="请输入台桌ID" />
        </el-form-item>
        <el-form-item label="桌型ID">
          <el-input v-model="formData.tableTypeId" placeholder="请输入桌型ID" />
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="联系人姓名">
          <el-input v-model="formData.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="预约日期">
          <el-date-picker v-model="formData.reservationDate" type="datetime" placeholder="请选择预约日期" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="预约时间">
          <el-date-picker v-model="formData.reservationTime" type="datetime" placeholder="请选择预约时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="就餐人数">
          <el-input v-model="formData.persons" placeholder="请输入就餐人数" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { useReservationStore } from '@/stores/reservation'
import reservationApi from '@/api/reservation'

const reservationStore = useReservationStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增预约')
const formData = reactive({
  id: null,
  storeId: '',
  tableId: '',
  tableTypeId: '',
  memberId: '',
  contactName: '',
  contactPhone: '',
  reservationDate: '',
  reservationTime: '',
  persons: '',
  remark: ''
})

const handleSearch = () => {
  reservationStore.page = 1
  reservationStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增预约'
  formData.id = null
  formData.storeId = ''
  formData.tableId = ''
  formData.tableTypeId = ''
  formData.memberId = ''
  formData.contactName = ''
  formData.contactPhone = ''
  formData.reservationDate = ''
  formData.reservationTime = ''
  formData.persons = ''
  formData.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑预约'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await reservationApi.delete(row.id)
      ElMessage.success('删除成功')
      reservationStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  reservationStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await reservationApi.update(formData.id, formData)
  } else {
    await reservationApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  reservationStore.fetchList()
}

onMounted(() => {
  reservationStore.fetchList()
})
</script>

<style scoped>
.reservations-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
