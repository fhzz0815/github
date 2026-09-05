<template>
  <div class="memberBankCards-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入开户行/持卡人姓名/银行卡号" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="是否默认">
          <el-select v-model="queryForm.isDefault" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.isDefault" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员">
          <el-select v-model="queryForm.memberId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.members" :key="o.id" :label="o.realName || o.nickname || o.phone" :value="o.id" />
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

      <el-table :data="memberBankCardStore.list" v-loading="memberBankCardStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="bankName" label="开户行" width="120" show-overflow-tooltip />
        <el-table-column prop="cardNo" label="银行卡号" width="120" show-overflow-tooltip />
        <el-table-column prop="holderName" label="持卡人姓名" width="120" show-overflow-tooltip />
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
          v-model:current-page="memberBankCardStore.page"
          v-model:page-size="memberBankCardStore.size"
          :total="memberBankCardStore.total"
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
        <el-form-item label="开户行">
          <el-input v-model="formData.bankName" placeholder="请输入开户行" />
        </el-form-item>
        <el-form-item label="银行卡号">
          <el-input v-model="formData.cardNo" placeholder="请输入银行卡号" />
        </el-form-item>
        <el-form-item label="持卡人姓名">
          <el-input v-model="formData.holderName" placeholder="请输入持卡人姓名" />
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
import { useMemberBankCardStore } from '@/stores/memberBankCard'
import memberBankCardApi from '@/api/memberBankCard'

const memberBankCardStore = useMemberBankCardStore()

import memberApi from '@/api/member'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  isDefault: [{ value: 1, label: '是' }, { value: 0, label: '否' }]
}
const searchOptions = reactive({
  members: []
})
const loadSearchOptions = () => {
  memberApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.members = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  isDefault: null,
  memberId: null,
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
  memberBankCardStore.page = 1
  memberBankCardStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  memberBankCardStore.page = 1
  memberBankCardStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  memberBankCardStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增会员银行卡')
const formData = reactive({
  id: null,
  memberId: '',
  bankName: '',
  cardNo: '',
  holderName: '',
  isDefault: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增会员银行卡'
  formData.id = null
  formData.memberId = ''
  formData.bankName = ''
  formData.cardNo = ''
  formData.holderName = ''
  formData.isDefault = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑会员银行卡'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await memberBankCardApi.delete(row.id)
      ElMessage.success('删除成功')
      memberBankCardStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  memberBankCardStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await memberBankCardApi.update(formData.id, formData)
  } else {
    await memberBankCardApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  memberBankCardStore.fetchList()
}

onMounted(() => {
  memberBankCardStore.fetchList()
})
</script>

<style scoped>
.memberBankCards-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
