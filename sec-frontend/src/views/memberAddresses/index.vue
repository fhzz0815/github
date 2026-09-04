<template>
  <div class="memberAddresses-page">
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

      <el-table :data="memberAddressStore.list" v-loading="memberAddressStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="memberId" label="客户ID" width="120" show-overflow-tooltip />
        <el-table-column prop="contactName" label="联系人姓名" width="120" show-overflow-tooltip />
        <el-table-column prop="contactPhone" label="联系人电话" width="120" show-overflow-tooltip />
        <el-table-column prop="province" label="省份" width="120" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="120" show-overflow-tooltip />
        <el-table-column prop="district" label="区县" width="120" show-overflow-tooltip />
        <el-table-column prop="detailAddress" label="详细地址" width="160" show-overflow-tooltip />
        <el-table-column prop="longitude" label="经度" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="memberAddressStore.page"
          v-model:page-size="memberAddressStore.size"
          :total="memberAddressStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="memberAddressStore.fetchList"
          @current-change="memberAddressStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="客户ID">
          <el-input v-model="formData.memberId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="联系人姓名">
          <el-input v-model="formData.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>
        <el-form-item label="联系人电话">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系人电话" />
        </el-form-item>
        <el-form-item label="省份">
          <el-input v-model="formData.province" placeholder="请输入省份" />
        </el-form-item>
        <el-form-item label="城市">
          <el-input v-model="formData.city" placeholder="请输入城市" />
        </el-form-item>
        <el-form-item label="区县">
          <el-input v-model="formData.district" placeholder="请输入区县" />
        </el-form-item>
        <el-form-item label="详细地址">
          <el-input v-model="formData.detailAddress" type="textarea" :rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="经度">
          <el-input-number v-model="formData.longitude" placeholder="请输入经度" style="width:100%" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input-number v-model="formData.latitude" placeholder="请输入纬度" style="width:100%" />
        </el-form-item>
        <el-form-item label="地址标签 家/公司">
          <el-input v-model="formData.tag" placeholder="请输入地址标签 家/公司" />
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
import { useMemberAddressStore } from '@/stores/memberAddress'
import memberAddressApi from '@/api/memberAddress'

const memberAddressStore = useMemberAddressStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增收货地址')
const formData = reactive({
  id: null,
  memberId: '',
  contactName: '',
  contactPhone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  longitude: null,
  latitude: null,
  tag: ''
})

const handleSearch = () => {
  memberAddressStore.page = 1
  memberAddressStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增收货地址'
  formData.id = null
  formData.memberId = ''
  formData.contactName = ''
  formData.contactPhone = ''
  formData.province = ''
  formData.city = ''
  formData.district = ''
  formData.detailAddress = ''
  formData.longitude = null
  formData.latitude = null
  formData.tag = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑收货地址'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await memberAddressApi.delete(row.id)
      ElMessage.success('删除成功')
      memberAddressStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  memberAddressStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await memberAddressApi.update(formData.id, formData)
  } else {
    await memberAddressApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  memberAddressStore.fetchList()
}

onMounted(() => {
  memberAddressStore.fetchList()
})
</script>

<style scoped>
.memberAddresses-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
