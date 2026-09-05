<template>
  <div class="stores-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入门店名称/负责人/门店编号/门店电话" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="营业状态">
          <el-select v-model="queryForm.businessStatus" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.businessStatus" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否允许堂食">
          <el-select v-model="queryForm.dineInEnabled" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.dineInEnabled" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否允许外卖">
          <el-select v-model="queryForm.takeoutEnabled" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.takeoutEnabled" :key="o.value" :label="o.label" :value="o.value" />
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

      <el-table :data="storeStore.list" v-loading="storeStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeNo" label="门店编号" width="120" show-overflow-tooltip />
        <el-table-column prop="storeName" label="门店名称" width="120" show-overflow-tooltip />
        <el-table-column prop="province" label="省份" width="120" show-overflow-tooltip />
        <el-table-column prop="city" label="城市" width="120" show-overflow-tooltip />
        <el-table-column prop="district" label="区县" width="120" show-overflow-tooltip />
        <el-table-column prop="address" label="详细地址" width="120" show-overflow-tooltip />
        <el-table-column prop="longitude" label="经度" width="120" show-overflow-tooltip />
        <el-table-column prop="latitude" label="纬度" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="storeStore.page"
          v-model:page-size="storeStore.size"
          :total="storeStore.total"
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
        <el-form-item label="门店编号">
          <el-input v-model="formData.storeNo" placeholder="请输入门店编号" />
        </el-form-item>
        <el-form-item label="门店名称">
          <el-input v-model="formData.storeName" placeholder="请输入门店名称" />
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
          <el-input v-model="formData.address" type="textarea" :rows="3" placeholder="请输入详细地址" />
        </el-form-item>
        <el-form-item label="经度">
          <el-input-number v-model="formData.longitude" placeholder="请输入经度" style="width:100%" />
        </el-form-item>
        <el-form-item label="纬度">
          <el-input-number v-model="formData.latitude" placeholder="请输入纬度" style="width:100%" />
        </el-form-item>
        <el-form-item label="门店电话">
          <el-input v-model="formData.phone" placeholder="请输入门店电话" />
        </el-form-item>
        <el-form-item label="负责人">
          <el-input v-model="formData.contactName" placeholder="请输入负责人" />
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
import { useStoreStore } from '@/stores/store'
import storeApi from '@/api/store'

const storeStore = useStoreStore()



// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  businessStatus: [{ value: 1, label: '营业' }, { value: 0, label: '打烊' }],
  dineInEnabled: [{ value: 1, label: '是' }, { value: 0, label: '否' }],
  takeoutEnabled: [{ value: 1, label: '是' }, { value: 0, label: '否' }]
}
const searchOptions = reactive({

})
const loadSearchOptions = () => {
  
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  businessStatus: null,
  dineInEnabled: null,
  takeoutEnabled: null,
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
  storeStore.page = 1
  storeStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  storeStore.page = 1
  storeStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  storeStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增门店')
const formData = reactive({
  id: null,
  storeNo: '',
  storeName: '',
  province: '',
  city: '',
  district: '',
  address: '',
  longitude: null,
  latitude: null,
  phone: '',
  contactName: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增门店'
  formData.id = null
  formData.storeNo = ''
  formData.storeName = ''
  formData.province = ''
  formData.city = ''
  formData.district = ''
  formData.address = ''
  formData.longitude = null
  formData.latitude = null
  formData.phone = ''
  formData.contactName = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑门店'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await storeApi.delete(row.id)
      ElMessage.success('删除成功')
      storeStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  storeStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await storeApi.update(formData.id, formData)
  } else {
    await storeApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  storeStore.fetchList()
}

onMounted(() => {
  storeStore.fetchList()
})
</script>

<style scoped>
.stores-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
