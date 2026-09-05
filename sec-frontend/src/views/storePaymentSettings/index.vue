<template>
  <div class="storePaymentSettings-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="是否启用">
          <el-select v-model="queryForm.isEnabled" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.isEnabled" :key="o.value" :label="o.label" :value="o.value" />
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

      <el-table :data="storePaymentSettingStore.list" v-loading="storePaymentSettingStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="payType" label="支付类型 WECHAT微信 ALIPAY支付宝 MEMBER会员余额 CASH现金" width="160" show-overflow-tooltip />
        <el-table-column prop="isEnabled" label="是否启用 1启用 0停用" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="storePaymentSettingStore.page"
          v-model:page-size="storePaymentSettingStore.size"
          :total="storePaymentSettingStore.total"
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
        <el-form-item label="支付类型 WECHAT微信 ALIPAY支付宝 MEMBER会员余额 CASH现金">
          <el-input v-model="formData.payType" placeholder="请输入支付类型 WECHAT微信 ALIPAY支付宝 MEMBER会员余额 CASH现金" />
        </el-form-item>
        <el-form-item label="是否启用 1启用 0停用">
          <el-switch v-model="formData.isEnabled" />
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
import { useStorePaymentSettingStore } from '@/stores/storePaymentSetting'
import storePaymentSettingApi from '@/api/storePaymentSetting'

const storePaymentSettingStore = useStorePaymentSettingStore()

import storeApi from '@/api/store'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  isEnabled: [{ value: 1, label: '启用' }, { value: 0, label: '停用' }]
}
const searchOptions = reactive({
  stores: []
})
const loadSearchOptions = () => {
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  isEnabled: null,
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
  storePaymentSettingStore.page = 1
  storePaymentSettingStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  storePaymentSettingStore.page = 1
  storePaymentSettingStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  storePaymentSettingStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增门店支付设置')
const formData = reactive({
  id: null,
  storeId: '',
  payType: '',
  isEnabled: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增门店支付设置'
  formData.id = null
  formData.storeId = ''
  formData.payType = ''
  formData.isEnabled = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑门店支付设置'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await storePaymentSettingApi.delete(row.id)
      ElMessage.success('删除成功')
      storePaymentSettingStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  storePaymentSettingStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await storePaymentSettingApi.update(formData.id, formData)
  } else {
    await storePaymentSettingApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  storePaymentSettingStore.fetchList()
}

onMounted(() => {
  storePaymentSettingStore.fetchList()
})
</script>

<style scoped>
.storePaymentSettings-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
