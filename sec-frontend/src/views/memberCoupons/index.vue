<template>
  <div class="memberCoupons-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
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
        <el-form-item label="优惠券">
          <el-select v-model="queryForm.couponId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.coupons" :key="o.id" :label="o.couponName" :value="o.id" />
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

      <el-table :data="memberCouponStore.list" v-loading="memberCouponStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="couponId" label="优惠券ID" width="120" show-overflow-tooltip />
        <el-table-column prop="storeId" label="适用门店ID" width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="状态 1未使用 2已使用 3已过期" width="160" show-overflow-tooltip />
        <el-table-column prop="receiveTime" label="领取时间" width="120" show-overflow-tooltip />
        <el-table-column prop="expireTime" label="过期时间" width="120" show-overflow-tooltip />
        <el-table-column prop="useTime" label="使用时间" width="120" show-overflow-tooltip />
        <el-table-column prop="orderId" label="使用订单ID" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="memberCouponStore.page"
          v-model:page-size="memberCouponStore.size"
          :total="memberCouponStore.total"
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
        <el-form-item label="优惠券ID">
          <el-input v-model="formData.couponId" placeholder="请输入优惠券ID" />
        </el-form-item>
        <el-form-item label="适用门店ID">
          <el-input v-model="formData.storeId" placeholder="请输入适用门店ID" />
        </el-form-item>
        <el-form-item label="状态 1未使用 2已使用 3已过期">
          <el-switch v-model="formData.status" />
        </el-form-item>
        <el-form-item label="领取时间">
          <el-date-picker v-model="formData.receiveTime" type="datetime" placeholder="请选择领取时间" value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
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
import { useMemberCouponStore } from '@/stores/memberCoupon'
import memberCouponApi from '@/api/memberCoupon'

const memberCouponStore = useMemberCouponStore()

import memberApi from '@/api/member'
import couponApi from '@/api/coupon'
import storeApi from '@/api/store'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  status: [{ value: 1, label: '未使用' }, { value: 2, label: '已使用' }, { value: 3, label: '已过期' }]
}
const searchOptions = reactive({
  members: [],
  coupons: [],
  stores: []
})
const loadSearchOptions = () => {
  memberApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.members = r.data?.list || [] }).catch(() => {})
  couponApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.coupons = r.data?.list || [] }).catch(() => {})
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  status: null,
  memberId: null,
  couponId: null,
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
  memberCouponStore.page = 1
  memberCouponStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  memberCouponStore.page = 1
  memberCouponStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  memberCouponStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增用户优惠券')
const formData = reactive({
  id: null,
  memberId: '',
  couponId: '',
  storeId: '',
  status: '',
  receiveTime: '',
  expireTime: '',
  useTime: '',
  orderId: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增用户优惠券'
  formData.id = null
  formData.memberId = ''
  formData.couponId = ''
  formData.storeId = ''
  formData.status = ''
  formData.receiveTime = ''
  formData.expireTime = ''
  formData.useTime = ''
  formData.orderId = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑用户优惠券'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await memberCouponApi.delete(row.id)
      ElMessage.success('删除成功')
      memberCouponStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  memberCouponStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await memberCouponApi.update(formData.id, formData)
  } else {
    await memberCouponApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  memberCouponStore.fetchList()
}

onMounted(() => {
  memberCouponStore.fetchList()
})
</script>

<style scoped>
.memberCoupons-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
