<template>
  <div class="staffLoginLogs-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="结果">
          <el-select v-model="queryForm.loginResult" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.loginResult" :key="o.value" :label="o.label" :value="o.value" />
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

      <el-table :data="staffLoginLogStore.list" v-loading="staffLoginLogStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="staffId" label="员工ID" width="120" show-overflow-tooltip />
        <el-table-column prop="loginTime" label="登录时间" width="120" show-overflow-tooltip />
        <el-table-column prop="loginIp" label="登录IP" width="120" show-overflow-tooltip />
        <el-table-column prop="device" label="登录设备" width="120" show-overflow-tooltip />
        <el-table-column prop="loginResult" label="结果 1成功 0失败" width="160" show-overflow-tooltip />
        <el-table-column prop="failReason" label="失败原因" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="staffLoginLogStore.page"
          v-model:page-size="staffLoginLogStore.size"
          :total="staffLoginLogStore.total"
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
        <el-form-item label="员工ID">
          <el-input v-model="formData.staffId" placeholder="请输入员工ID" />
        </el-form-item>
        <el-form-item label="登录时间">
          <el-date-picker v-model="formData.loginTime" type="datetime" placeholder="请选择登录时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="登录IP">
          <el-input v-model="formData.loginIp" placeholder="请输入登录IP" />
        </el-form-item>
        <el-form-item label="登录设备">
          <el-input v-model="formData.device" placeholder="请输入登录设备" />
        </el-form-item>
        <el-form-item label="结果 1成功 0失败">
          <el-input v-model="formData.loginResult" placeholder="请输入结果 1成功 0失败" />
        </el-form-item>
        <el-form-item label="失败原因">
          <el-input v-model="formData.failReason" placeholder="请输入失败原因" />
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
import { useStaffLoginLogStore } from '@/stores/staffLoginLog'
import staffLoginLogApi from '@/api/staffLoginLog'

const staffLoginLogStore = useStaffLoginLogStore()



// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  loginResult: [{ value: 1, label: '成功' }, { value: 0, label: '失败' }]
}
const searchOptions = reactive({

})
const loadSearchOptions = () => {
  
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  loginResult: null,
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
  staffLoginLogStore.page = 1
  staffLoginLogStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  staffLoginLogStore.page = 1
  staffLoginLogStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  staffLoginLogStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增登录日志')
const formData = reactive({
  id: null,
  staffId: '',
  loginTime: '',
  loginIp: '',
  device: '',
  loginResult: '',
  failReason: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增登录日志'
  formData.id = null
  formData.staffId = ''
  formData.loginTime = ''
  formData.loginIp = ''
  formData.device = ''
  formData.loginResult = ''
  formData.failReason = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑登录日志'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await staffLoginLogApi.delete(row.id)
      ElMessage.success('删除成功')
      staffLoginLogStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  staffLoginLogStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await staffLoginLogApi.update(formData.id, formData)
  } else {
    await staffLoginLogApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  staffLoginLogStore.fetchList()
}

onMounted(() => {
  staffLoginLogStore.fetchList()
})
</script>

<style scoped>
.staffLoginLogs-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
