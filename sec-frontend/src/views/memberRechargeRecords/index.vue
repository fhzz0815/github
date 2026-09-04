<template>
  <div class="memberRechargeRecords-page">
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

      <el-table :data="memberRechargeRecordStore.list" v-loading="memberRechargeRecordStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="rechargeNo" label="充值单号" width="120" show-overflow-tooltip />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="storeId" label="充值门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="rechargeAmount" label="充值金额(元)" width="160" show-overflow-tooltip />
        <el-table-column prop="giftAmount" label="赠送金额(元)" width="160" show-overflow-tooltip />
        <el-table-column prop="payType" label="支付方式 WECHAT/ALIPAY/CASH" width="160" show-overflow-tooltip />
        <el-table-column prop="status" label="状态 1成功 0失败" width="160" show-overflow-tooltip />
        <el-table-column prop="operatorId" label="操作收银员工ID" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="memberRechargeRecordStore.page"
          v-model:page-size="memberRechargeRecordStore.size"
          :total="memberRechargeRecordStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="memberRechargeRecordStore.fetchList"
          @current-change="memberRechargeRecordStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="充值单号">
          <el-input v-model="formData.rechargeNo" placeholder="请输入充值单号" />
        </el-form-item>
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="充值门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入充值门店ID" />
        </el-form-item>
        <el-form-item label="充值金额(元)">
          <el-input-number v-model="formData.rechargeAmount" placeholder="请输入充值金额(元)" style="width:100%" />
        </el-form-item>
        <el-form-item label="赠送金额(元)">
          <el-input-number v-model="formData.giftAmount" placeholder="请输入赠送金额(元)" style="width:100%" />
        </el-form-item>
        <el-form-item label="支付方式 WECHAT/ALIPAY/CASH">
          <el-input v-model="formData.payType" placeholder="请输入支付方式 WECHAT/ALIPAY/CASH" />
        </el-form-item>
        <el-form-item label="状态 1成功 0失败">
          <el-switch v-model="formData.status" />
        </el-form-item>
        <el-form-item label="操作收银员工ID">
          <el-input v-model="formData.operatorId" placeholder="请输入操作收银员工ID" />
        </el-form-item>
        <el-form-item label="充值时间">
          <el-date-picker v-model="formData.rechargeTime" type="datetime" placeholder="请选择充值时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
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
import { useMemberRechargeRecordStore } from '@/stores/memberRechargeRecord'
import memberRechargeRecordApi from '@/api/memberRechargeRecord'

const memberRechargeRecordStore = useMemberRechargeRecordStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增充值记录')
const formData = reactive({
  id: null,
  rechargeNo: '',
  memberId: '',
  storeId: '',
  rechargeAmount: null,
  giftAmount: null,
  payType: '',
  status: '',
  operatorId: '',
  rechargeTime: '',
  remark: ''
})

const handleSearch = () => {
  memberRechargeRecordStore.page = 1
  memberRechargeRecordStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增充值记录'
  formData.id = null
  formData.rechargeNo = ''
  formData.memberId = ''
  formData.storeId = ''
  formData.rechargeAmount = null
  formData.giftAmount = null
  formData.payType = ''
  formData.status = ''
  formData.operatorId = ''
  formData.rechargeTime = ''
  formData.remark = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑充值记录'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await memberRechargeRecordApi.delete(row.id)
      ElMessage.success('删除成功')
      memberRechargeRecordStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  memberRechargeRecordStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await memberRechargeRecordApi.update(formData.id, formData)
  } else {
    await memberRechargeRecordApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  memberRechargeRecordStore.fetchList()
}

onMounted(() => {
  memberRechargeRecordStore.fetchList()
})
</script>

<style scoped>
.memberRechargeRecords-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
