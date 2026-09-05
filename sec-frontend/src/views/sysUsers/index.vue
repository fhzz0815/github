<template>
  <div class="sysUsers-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入姓名/员工工号/登录账号/邮箱" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="账号状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.status" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="userStore.isGeneralManager" label="门店">
          <el-select v-model="queryForm.storeId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="queryForm.roleId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.roles" :key="o.id" :label="o.roleName" :value="o.id" />
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
        <el-button v-if="canCreate" type="primary" @click="handleAdd">新增</el-button>
        <el-button @click="handleRefresh">刷新</el-button>
      </div>

      <el-table :data="sysUserStore.list" v-loading="sysUserStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeName" label="所属门店" width="160" show-overflow-tooltip />
        <el-table-column prop="realName" label="姓名" width="120" show-overflow-tooltip />
        <el-table-column prop="staffNo" label="员工工号" width="120" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" show-overflow-tooltip />
        <el-table-column prop="roleName" label="角色" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canEdit(row)" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="canDelete(row)" type="danger" link @click="handleDelete(row)">删除</el-button>
            <span v-if="!canEdit(row) && !canDelete(row)" class="text-muted">仅查看</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="sysUserStore.page"
          v-model:page-size="sysUserStore.size"
          :total="sysUserStore.total"
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
        <el-form-item label="所属门店">
          <el-select v-model="formData.storeId" clearable filterable placeholder="请选择门店（总店长可空）" style="width: 100%" :disabled="!userStore.isGeneralManager">
            <el-option v-for="o in searchOptions.stores" :key="o.id" :label="o.storeName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="登录账号（手机号）">
          <el-input v-model="formData.username" placeholder="请输入登录账号（手机号）" />
        </el-form-item>
        <el-form-item label="登录密码">
          <el-input v-model="formData.password" placeholder="留空则默认123456" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="formData.realName" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="员工工号">
          <el-input v-model="formData.staffNo" placeholder="请输入员工工号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formData.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="formData.roleId" clearable filterable placeholder="请选择角色" style="width: 100%">
            <el-option
              v-for="o in searchOptions.roles"
              :key="o.id"
              :label="o.roleName + (o.level != null ? ' (等级 ' + o.level + ')' : '')"
              :value="o.id"
              :disabled="isRoleDisabled(o)"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="头像URL">
          <el-input v-model="formData.avatar" placeholder="请输入头像URL" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useSysUserStore } from '@/stores/sysUser'
import { useUserStore } from '@/stores/user'
import sysUserApi from '@/api/sysUser'

const sysUserStore = useSysUserStore()
const userStore = useUserStore()

import storeApi from '@/api/store'
import sysRoleApi from '@/api/sysRole'

// ===== 权限判断 =====
// 是否显示新增按钮：店长及以上
const canCreate = computed(() => userStore.canManageUsers)
// 是否可编辑某行：总店长全可；店长仅本门店且目标等级严格更低
const canEdit = (row) => {
  if (userStore.isGeneralManager) return true
  if (userStore.isStoreManager) {
    if (row.storeId !== userStore.storeId) return false
    const targetLevel = row.roleLevel
    if (targetLevel == null) return false
    return targetLevel < userStore.roleLevel
  }
  return false
}
// 是否可删除某行：同编辑规则
const canDelete = (row) => canEdit(row)
// 表单角色下拉是否禁用某项：店长不能选总店长/店长角色
const isRoleDisabled = (role) => {
  if (userStore.isGeneralManager) return false
  if (userStore.isStoreManager) {
    return role.level == null || role.level >= userStore.roleLevel
  }
  return true
}

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  status: [{ value: 1, label: '在职' }, { value: 0, label: '离职' }]
}
const searchOptions = reactive({
  stores: [],
  roles: []
})
const loadSearchOptions = () => {
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
  sysRoleApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.roles = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  status: null,
  storeId: null,
  roleId: null,
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
  sysUserStore.page = 1
  sysUserStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  sysUserStore.page = 1
  sysUserStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  sysUserStore.fetchList(buildParams())
}

const dialogVisible = ref(false)
const dialogTitle = ref('新增员工')
const formData = reactive({
  id: null,
  storeId: '',
  username: '',
  password: '',
  realName: '',
  staffNo: '',
  email: '',
  phone: '',
  idCard: '',
  roleId: '',
  avatar: ''
})



const handleAdd = () => {
  dialogTitle.value = '新增员工'
  formData.id = null
  formData.storeId = ''
  formData.username = ''
  formData.password = ''
  formData.realName = ''
  formData.staffNo = ''
  formData.email = ''
  formData.phone = ''
  formData.idCard = ''
  formData.roleId = ''
  formData.avatar = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑员工'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await sysUserApi.delete(row.id)
      ElMessage.success('删除成功')
      sysUserStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  sysUserStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await sysUserApi.update(formData.id, formData)
  } else {
    await sysUserApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  sysUserStore.fetchList()
}

onMounted(() => {
  sysUserStore.fetchList()
})
</script>

<style scoped>
.sysUsers-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
.text-muted { color: #999; font-size: 13px; }
</style>
