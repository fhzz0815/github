<template>
  <div class="redPackets-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入红包编号" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.status" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
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

      <el-table :data="redPacketStore.list" v-loading="redPacketStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="redPacketNo" label="红包编号" width="120" show-overflow-tooltip />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="storeId" label="所属门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="amount" label="红包金额(元)" width="160" show-overflow-tooltip />
        <el-table-column prop="source" label="来源 RECHARGE_REWARD充值返利 ACTIVITY活动" width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="状态 1未使用 2已使用 3已过期" width="160" show-overflow-tooltip />
        <el-table-column prop="expireTime" label="过期时间" width="120" show-overflow-tooltip />
        <el-table-column prop="useTime" label="使用时间" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="redPacketStore.page"
          v-model:page-size="redPacketStore.size"
          :total="redPacketStore.total"
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
        <el-form-item label="红包编号">
          <el-input v-model="formData.redPacketNo" placeholder="请输入红包编号" />
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="所属门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入所属门店ID" />
        </el-form-item>
        <el-form-item label="红包金额(元)">
          <el-input-number v-model="formData.amount" placeholder="请输入红包金额(元)" style="width:100%" />
        </el-form-item>
        <el-form-item label="来源 RECHARGE_REWARD充值返利 ACTIVITY活动">
          <el-input v-model="formData.source" placeholder="请输入来源 RECHARGE_REWARD充值返利 ACTIVITY活动" />
        </el-form-item>
        <el-form-item label="状态 1未使用 2已使用 3已过期">
          <el-switch v-model="formData.status" />
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker v-model="formData.expireTime" type="datetime" placeholder="请选择过期时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="使用时间">
          <el-date-picker v-model="formData.useTime" type="datetime" placeholder="请选择使用时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="使用订单ID">
          <el-input v-model="formData.orderId" placeholder="请输入使用订单ID" />
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
import { useRedPacketStore } from '@/stores/redPacket'
import redPacketApi from '@/api/redPacket'

const redPacketStore = useRedPacketStore()

import memberApi from '@/api/member'
import storeApi from '@/api/store'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  status: [{ value: 1, label: '未使用' }, { value: 2, label: '已使用' }, { value: 3, label: '已过期' }]
}
const searchOptions = reactive({
  members: [],
  stores: []
})
const loadSearchOptions = () => {
  memberApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.members = r.data?.list || [] }).catch(() => {})
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  status: null,
  memberId: null,
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
  redPacketStore.page = 1
  redPacketStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  redPacketStore.page = 1
  redPacketStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  redPacketStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增红包')
const formData = reactive({
  id: null,
  redPacketNo: '',
  memberId: '',
  storeId: '',
  amount: null,
  source: '',
  status: '',
  expireTime: '',
  useTime: '',
  orderId: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增红包'
  formData.id = null
  formData.redPacketNo = ''
  formData.memberId = ''
  formData.storeId = ''
  formData.amount = null
  formData.source = ''
  formData.status = ''
  formData.expireTime = ''
  formData.useTime = ''
  formData.orderId = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑红包'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await redPacketApi.delete(row.id)
      ElMessage.success('删除成功')
      redPacketStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  redPacketStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await redPacketApi.update(formData.id, formData)
  } else {
    await redPacketApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  redPacketStore.fetchList()
}

onMounted(() => {
  redPacketStore.fetchList()
})
</script>

<style scoped>
.redPackets-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
