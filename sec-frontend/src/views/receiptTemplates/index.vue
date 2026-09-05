<template>
  <div class="receiptTemplates-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入模板名称" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="是否默认">
          <el-select v-model="queryForm.isDefault" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.isDefault" :key="o.value" :label="o.label" :value="o.value" />
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

      <el-table :data="receiptTemplateStore.list" v-loading="receiptTemplateStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="templateType" label="类型 DINE_IN堂食 TAKEOUT外卖 KITCHEN后厨 RECHARGE充值" width="160" show-overflow-tooltip />
        <el-table-column prop="templateName" label="模板名称" width="120" show-overflow-tooltip />
        <el-table-column prop="templateContent" label="模板内容" width="160" show-overflow-tooltip />
        <el-table-column prop="isDefault" label="是否默认 1是 0否" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="receiptTemplateStore.page"
          v-model:page-size="receiptTemplateStore.size"
          :total="receiptTemplateStore.total"
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
        <el-form-item label="类型 DINE_IN堂食 TAKEOUT外卖 KITCHEN后厨 RECHARGE充值">
          <el-input v-model="formData.templateType" placeholder="请输入类型 DINE_IN堂食 TAKEOUT外卖 KITCHEN后厨 RECHARGE充值" />
        </el-form-item>
        <el-form-item label="模板名称">
          <el-input v-model="formData.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板内容">
          <el-input v-model="formData.templateContent" type="textarea" :rows="3" placeholder="请输入模板内容" />
        </el-form-item>
        <el-form-item label="是否默认 1是 0否">
          <el-switch v-model="formData.isDefault" />
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
import { useReceiptTemplateStore } from '@/stores/receiptTemplate'
import receiptTemplateApi from '@/api/receiptTemplate'

const receiptTemplateStore = useReceiptTemplateStore()



// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  isDefault: [{ value: 1, label: '是' }, { value: 0, label: '否' }]
}
const searchOptions = reactive({

})
const loadSearchOptions = () => {
  
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  isDefault: null,
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
  receiptTemplateStore.page = 1
  receiptTemplateStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  receiptTemplateStore.page = 1
  receiptTemplateStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  receiptTemplateStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增小票模板')
const formData = reactive({
  id: null,
  templateType: '',
  templateName: '',
  templateContent: '',
  isDefault: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增小票模板'
  formData.id = null
  formData.templateType = ''
  formData.templateName = ''
  formData.templateContent = ''
  formData.isDefault = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑小票模板'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await receiptTemplateApi.delete(row.id)
      ElMessage.success('删除成功')
      receiptTemplateStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  receiptTemplateStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await receiptTemplateApi.update(formData.id, formData)
  } else {
    await receiptTemplateApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  receiptTemplateStore.fetchList()
}

onMounted(() => {
  receiptTemplateStore.fetchList()
})
</script>

<style scoped>
.receiptTemplates-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
