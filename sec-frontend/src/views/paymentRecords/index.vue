<template>
  <div class="paymentRecords-page">
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

      <el-table :data="paymentRecordStore.list" v-loading="paymentRecordStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="payNo" label="支付流水号" width="120" show-overflow-tooltip />
        <el-table-column prop="orderId" label="订单ID" width="120" show-overflow-tooltip />
        <el-table-column prop="storeId" label="门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="payType" label="支付类型 WECHAT微信 ALIPAY支付宝 MEMBER_BALANCE会员余额 CASH现金 SCAN扫码付" width="160" show-overflow-tooltip />
        <el-table-column prop="payChannel" label="支付渠道说明" width="120" show-overflow-tooltip />
        <el-table-column prop="amount" label="支付金额(元)" width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="状态 1待支付 2成功 3失败 4已退款" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="paymentRecordStore.page"
          v-model:page-size="paymentRecordStore.size"
          :total="paymentRecordStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="paymentRecordStore.fetchList"
          @current-change="paymentRecordStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="支付流水号">
          <el-input v-model="formData.payNo" placeholder="请输入支付流水号" />
        </el-form-item>
        <el-form-item label="订单ID">
          <el-input v-model="formData.orderId" placeholder="请输入订单ID" />
        </el-form-item>
        <el-form-item label="门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID" />
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="支付类型 WECHAT微信 ALIPAY支付宝 MEMBER_BALANCE会员余额 CASH现金 SCAN扫码付">
          <el-input v-model="formData.payType" placeholder="请输入支付类型 WECHAT微信 ALIPAY支付宝 MEMBER_BALANCE会员余额 CASH现金 SCAN扫码付" />
        </el-form-item>
        <el-form-item label="支付渠道说明">
          <el-input v-model="formData.payChannel" placeholder="请输入支付渠道说明" />
        </el-form-item>
        <el-form-item label="支付金额(元)">
          <el-input-number v-model="formData.amount" placeholder="请输入支付金额(元)" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态 1待支付 2成功 3失败 4已退款">
          <el-switch v-model="formData.status" />
        </el-form-item>
        <el-form-item label="第三方交易号">
          <el-input v-model="formData.transactionId" placeholder="请输入第三方交易号" />
        </el-form-item>
        <el-form-item label="幂等键（防重复支付回调，同一订单同渠道唯一）">
          <el-input v-model="formData.idempotencyKey" placeholder="请输入幂等键（防重复支付回调，同一订单同渠道唯一）" />
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
import { usePaymentRecordStore } from '@/stores/paymentRecord'
import paymentRecordApi from '@/api/paymentRecord'

const paymentRecordStore = usePaymentRecordStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增支付记录')
const formData = reactive({
  id: null,
  payNo: '',
  orderId: '',
  storeId: '',
  memberId: '',
  payType: '',
  payChannel: '',
  amount: null,
  status: '',
  transactionId: '',
  idempotencyKey: ''
})

const handleSearch = () => {
  paymentRecordStore.page = 1
  paymentRecordStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增支付记录'
  formData.id = null
  formData.payNo = ''
  formData.orderId = ''
  formData.storeId = ''
  formData.memberId = ''
  formData.payType = ''
  formData.payChannel = ''
  formData.amount = null
  formData.status = ''
  formData.transactionId = ''
  formData.idempotencyKey = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑支付记录'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await paymentRecordApi.delete(row.id)
      ElMessage.success('删除成功')
      paymentRecordStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  paymentRecordStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await paymentRecordApi.update(formData.id, formData)
  } else {
    await paymentRecordApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  paymentRecordStore.fetchList()
}

onMounted(() => {
  paymentRecordStore.fetchList()
})
</script>

<style scoped>
.paymentRecords-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
