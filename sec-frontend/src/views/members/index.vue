<template>
  <div class="members-page">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="姓名/卡号/手机号" clearable style="width: 200px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="queryForm.gender" clearable placeholder="全部" style="width: 100px">
            <el-option v-for="o in GENDER" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部" style="width: 100px">
            <el-option v-for="o in COMMON_STATUS" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员类别">
          <el-select v-model="queryForm.memberCategoryId" clearable filterable placeholder="全部" style="width: 140px">
            <el-option v-for="o in searchOptions.memberCategories" :key="o.id" :label="o.categoryName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册门店">
          <el-select v-model="queryForm.registerStoreId" clearable filterable placeholder="全部" style="width: 140px">
            <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册时间">
          <el-date-picker v-model="queryForm.dateRange" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始" end-placeholder="结束" style="width: 240px" />
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
        <el-button type="primary" @click="handleAdd">新增会员</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>

      <el-table :data="memberStore.list" v-loading="memberStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="55" align="center" />
        <el-table-column prop="nickname" label="昵称" width="100" show-overflow-tooltip />
        <el-table-column prop="realName" label="真实姓名" width="100" show-overflow-tooltip />
        <el-table-column label="性别" width="55" align="center">
          <template #default="{ row }">{{ getEnumLabel('gender', row.gender) }}</template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="120" />
        <el-table-column prop="memberNo" label="会员卡号" width="130" />
        <el-table-column label="会员类别" width="100" show-overflow-tooltip>
          <template #default="{ row }">{{ findLabel(searchOptions.memberCategories, row.memberCategoryId, 'categoryName') }}</template>
        </el-table-column>
        <el-table-column prop="balance" label="余额" width="80" align="right">
          <template #default="{ row }">¥{{ row.balance || 0 }}</template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="65" align="center" />
        <el-table-column label="状态" width="65" align="center">
          <template #default="{ row }">
            <el-tag :type="getEnumType('status', row.status)" size="small">{{ getEnumLabel('status', row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="memberStore.page"
          v-model:page-size="memberStore.size"
          :total="memberStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="onPageChange"
          @current-change="onPageChange"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="formData.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="formData.phone" placeholder="请输入手机号" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="性别">
              <el-select v-model="formData.gender" placeholder="请选择" style="width: 100%">
                <el-option v-for="o in GENDER" :key="o.value" :label="o.label" :value="o.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="会员类别">
              <el-select v-model="formData.memberCategoryId" filterable placeholder="请选择" style="width: 100%">
                <el-option v-for="o in searchOptions.memberCategories" :key="o.id" :label="o.categoryName" :value="o.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="昵称">
          <el-input v-model="formData.nickname" placeholder="微信昵称（可选）" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="formData.idCard" placeholder="钱包实名认证用（可选）" maxlength="18" />
        </el-form-item>
        <el-form-item label="会员卡号">
          <el-input v-model="formData.memberNo" placeholder="不填则自动生成" />
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
import { useMemberStore } from '@/stores/member'
import memberApi from '@/api/member'
import memberCategoryApi from '@/api/memberCategory'
import storeApi from '@/api/store'
import { COMMON_STATUS, GENDER, getEnumLabel, getEnumType } from '@/constants/enums'
import { buildSearchParams, findLabel } from '@/utils/helpers'

const memberStore = useMemberStore()

// 加载搜索下拉数据
const searchOptions = reactive({ memberCategories: [], stores: [] })
const loadSearchOptions = () => {
  memberCategoryApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.memberCategories = r.data?.list || [] }).catch(() => {})
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '', gender: null, status: null,
  memberCategoryId: null, registerStoreId: null, dateRange: null
})

const buildParams = () => buildSearchParams(queryForm)
const handleSearch = () => { memberStore.page = 1; memberStore.fetchList(buildParams()) }
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  memberStore.page = 1; memberStore.fetchList()
}
const onPageChange = () => memberStore.fetchList(buildParams())
const handleRefresh = () => memberStore.fetchList()

// 表单校验规则
const formRef = ref(null)
const formRules = {
  realName: [{ required: true, message: '请输入会员真实姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增会员')
const formData = reactive({
  id: null, openid: '', unionid: '', nickname: '', avatar: '',
  gender: '', phone: '', realName: '', idCard: '', memberNo: '', memberCategoryId: ''
})

const handleAdd = () => {
  dialogTitle.value = '新增会员'
  Object.keys(formData).forEach((k) => { formData[k] = '' })
  formData.id = null; dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑会员'
  Object.assign(formData, row); dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这个会员吗？', '提示', { type: 'warning' })
    .then(async () => { await memberApi.delete(row.id); ElMessage.success('删除成功'); memberStore.fetchList() })
    .catch(() => {})
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (formData.id) { await memberApi.update(formData.id, formData) }
  else { await memberApi.create(formData) }
  ElMessage.success('保存成功'); dialogVisible.value = false; memberStore.fetchList()
}

onMounted(() => { memberStore.fetchList() })
</script>

<style scoped>
.members-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
