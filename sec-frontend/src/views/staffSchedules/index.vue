<template>
  <div class="staffSchedules-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="班次">
          <el-select v-model="queryForm.shiftType" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.shiftType" :key="o.value" :label="o.label" :value="o.value" />
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

      <el-table :data="staffScheduleStore.list" v-loading="staffScheduleStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="staffId" label="员工ID" width="120" show-overflow-tooltip />
        <el-table-column prop="workDate" label="排班日期" width="120" show-overflow-tooltip />
        <el-table-column prop="shiftType" label="班次 1早班 2中班 3晚班" width="160" show-overflow-tooltip />
        <el-table-column prop="startTime" label="上班时间" width="120" show-overflow-tooltip />
        <el-table-column prop="endTime" label="下班时间" width="120" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="staffScheduleStore.page"
          v-model:page-size="staffScheduleStore.size"
          :total="staffScheduleStore.total"
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
        <el-form-item label="排班日期">
          <el-date-picker v-model="formData.workDate" type="datetime" placeholder="请选择排班日期" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="班次 1早班 2中班 3晚班">
          <el-input v-model="formData.shiftType" placeholder="请输入班次 1早班 2中班 3晚班" />
        </el-form-item>
        <el-form-item label="上班时间">
          <el-date-picker v-model="formData.startTime" type="datetime" placeholder="请选择上班时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="下班时间">
          <el-date-picker v-model="formData.endTime" type="datetime" placeholder="请选择下班时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
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
import { useStaffScheduleStore } from '@/stores/staffSchedule'
import staffScheduleApi from '@/api/staffSchedule'

const staffScheduleStore = useStaffScheduleStore()



// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  shiftType: [{ value: 1, label: '早班' }, { value: 2, label: '中班' }, { value: 3, label: '晚班' }]
}
const searchOptions = reactive({

})
const loadSearchOptions = () => {
  
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  shiftType: null,
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
  staffScheduleStore.page = 1
  staffScheduleStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  staffScheduleStore.page = 1
  staffScheduleStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  staffScheduleStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增员工排班')
const formData = reactive({
  id: null,
  staffId: '',
  workDate: '',
  shiftType: '',
  startTime: '',
  endTime: '',
  remark: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增员工排班'
  formData.id = null
  formData.staffId = ''
  formData.workDate = ''
  formData.shiftType = ''
  formData.startTime = ''
  formData.endTime = ''
  formData.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑员工排班'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await staffScheduleApi.delete(row.id)
      ElMessage.success('删除成功')
      staffScheduleStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  staffScheduleStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await staffScheduleApi.update(formData.id, formData)
  } else {
    await staffScheduleApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  staffScheduleStore.fetchList()
}

onMounted(() => {
  staffScheduleStore.fetchList()
})
</script>

<style scoped>
.staffSchedules-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
