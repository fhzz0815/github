<template>
  <div class="coupons-page">
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

      <el-table :data="couponStore.list" v-loading="couponStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="门店ID(NULL为通用券)" width="160" show-overflow-tooltip />
        <el-table-column prop="couponName" label="优惠券名称" width="120" show-overflow-tooltip />
        <el-table-column prop="couponType" label="类型 1满减 2折扣 3立减" width="160" show-overflow-tooltip />
        <el-table-column prop="thresholdAmount" label="使用门槛金额(元)" width="160" show-overflow-tooltip />
        <el-table-column prop="discountAmount" label="减免金额(元)(满减/立减)" width="160" show-overflow-tooltip />
        <el-table-column prop="discountRate" label="折扣率(折扣券)" width="160" show-overflow-tooltip />
        <el-table-column prop="totalCount" label="发行总量(0不限量)" width="160" show-overflow-tooltip />
        <el-table-column prop="issuedCount" label="已发放数量" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="couponStore.page"
          v-model:page-size="couponStore.size"
          :total="couponStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="couponStore.fetchList"
          @current-change="couponStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="门店ID(NULL为通用券)">
          <el-input v-model="formData.storeId" placeholder="请输入门店ID(NULL为通用券)" />
        </el-form-item>
        <el-form-item label="优惠券名称">
          <el-input v-model="formData.couponName" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="类型 1满减 2折扣 3立减">
          <el-input v-model="formData.couponType" placeholder="请输入类型 1满减 2折扣 3立减" />
        </el-form-item>
        <el-form-item label="使用门槛金额(元)">
          <el-input-number v-model="formData.thresholdAmount" placeholder="请输入使用门槛金额(元)" style="width:100%" />
        </el-form-item>
        <el-form-item label="减免金额(元)(满减/立减)">
          <el-input-number v-model="formData.discountAmount" placeholder="请输入减免金额(元)(满减/立减)" style="width:100%" />
        </el-form-item>
        <el-form-item label="折扣率(折扣券)">
          <el-input-number v-model="formData.discountRate" placeholder="请输入折扣率(折扣券)" style="width:100%" />
        </el-form-item>
        <el-form-item label="发行总量(0不限量)">
          <el-input-number v-model="formData.totalCount" placeholder="请输入发行总量(0不限量)" style="width:100%" />
        </el-form-item>
        <el-form-item label="已发放数量">
          <el-input-number v-model="formData.issuedCount" placeholder="请输入已发放数量" style="width:100%" />
        </el-form-item>
        <el-form-item label="每人限领数量">
          <el-input v-model="formData.perUserLimit" placeholder="请输入每人限领数量" />
        </el-form-item>
        <el-form-item label="有效期类型 1固定时间 2领取后N天">
          <el-input v-model="formData.validType" placeholder="请输入有效期类型 1固定时间 2领取后N天" />
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
import { useCouponStore } from '@/stores/coupon'
import couponApi from '@/api/coupon'

const couponStore = useCouponStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增优惠券')
const formData = reactive({
  id: null,
  storeId: '',
  couponName: '',
  couponType: '',
  thresholdAmount: null,
  discountAmount: null,
  discountRate: null,
  totalCount: null,
  issuedCount: null,
  perUserLimit: '',
  validType: ''
})

const handleSearch = () => {
  couponStore.page = 1
  couponStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增优惠券'
  formData.id = null
  formData.storeId = ''
  formData.couponName = ''
  formData.couponType = ''
  formData.thresholdAmount = null
  formData.discountAmount = null
  formData.discountRate = null
  formData.totalCount = null
  formData.issuedCount = null
  formData.perUserLimit = ''
  formData.validType = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑优惠券'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await couponApi.delete(row.id)
      ElMessage.success('删除成功')
      couponStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  couponStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await couponApi.update(formData.id, formData)
  } else {
    await couponApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  couponStore.fetchList()
}

onMounted(() => {
  couponStore.fetchList()
})
</script>

<style scoped>
.coupons-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
