<template>
  <div class="memberBalanceRecords-page">
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

      <el-table :data="memberBalanceRecordStore.list" v-loading="memberBalanceRecordStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="changeAmount" label="变动金额（正=收入 负=支出）" width="160" show-overflow-tooltip />
        <el-table-column prop="balanceAfter" label="变动后余额" width="120" show-overflow-tooltip />
        <el-table-column prop="changeType" label="变动类型 RECHARGE充值 CONSUME消费 REFUND退款 RED_PACKET红包 SCAN_PAY扫码付" width="160" show-overflow-tooltip />
        <el-table-column prop="sourceType" label="来源类型 ORDER订单 RECHARGE充值单等" width="160" show-overflow-tooltip />
        <el-table-column prop="sourceId" label="来源ID" width="120" show-overflow-tooltip />
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
          v-model:current-page="memberBalanceRecordStore.page"
          v-model:page-size="memberBalanceRecordStore.size"
          :total="memberBalanceRecordStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="memberBalanceRecordStore.fetchList"
          @current-change="memberBalanceRecordStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="变动金额（正=收入 负=支出）">
          <el-input-number v-model="formData.changeAmount" placeholder="请输入变动金额（正=收入 负=支出）" style="width:100%" />
        </el-form-item>
        <el-form-item label="变动后余额">
          <el-input-number v-model="formData.balanceAfter" placeholder="请输入变动后余额" style="width:100%" />
        </el-form-item>
        <el-form-item label="变动类型 RECHARGE充值 CONSUME消费 REFUND退款 RED_PACKET红包 SCAN_PAY扫码付">
          <el-input v-model="formData.changeType" placeholder="请输入变动类型 RECHARGE充值 CONSUME消费 REFUND退款 RED_PACKET红包 SCAN_PAY扫码付" />
        </el-form-item>
        <el-form-item label="来源类型 ORDER订单 RECHARGE充值单等">
          <el-input v-model="formData.sourceType" placeholder="请输入来源类型 ORDER订单 RECHARGE充值单等" />
        </el-form-item>
        <el-form-item label="来源ID">
          <el-input v-model="formData.sourceId" placeholder="请输入来源ID" />
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
import { useMemberBalanceRecordStore } from '@/stores/memberBalanceRecord'
import memberBalanceRecordApi from '@/api/memberBalanceRecord'

const memberBalanceRecordStore = useMemberBalanceRecordStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增钱包流水')
const formData = reactive({
  id: null,
  memberId: '',
  changeAmount: null,
  balanceAfter: null,
  changeType: '',
  sourceType: '',
  sourceId: '',
  remark: ''
})

const handleSearch = () => {
  memberBalanceRecordStore.page = 1
  memberBalanceRecordStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增钱包流水'
  formData.id = null
  formData.memberId = ''
  formData.changeAmount = null
  formData.balanceAfter = null
  formData.changeType = ''
  formData.sourceType = ''
  formData.sourceId = ''
  formData.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑钱包流水'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await memberBalanceRecordApi.delete(row.id)
      ElMessage.success('删除成功')
      memberBalanceRecordStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  memberBalanceRecordStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await memberBalanceRecordApi.update(formData.id, formData)
  } else {
    await memberBalanceRecordApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  memberBalanceRecordStore.fetchList()
}

onMounted(() => {
  memberBalanceRecordStore.fetchList()
})
</script>

<style scoped>
.memberBalanceRecords-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
