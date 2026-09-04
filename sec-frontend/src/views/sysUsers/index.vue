<template>
  <div class="sysUsers-page">
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

      <el-table :data="sysUserStore.list" v-loading="sysUserStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="storeId" label="所属门店ID（总店长为空）" width="160" show-overflow-tooltip />
        <el-table-column prop="username" label="登录账号（手机号）" width="160" show-overflow-tooltip />
        <el-table-column prop="password" label="登录密码（生产用bcrypt加密存储）" width="160" show-overflow-tooltip />
        <el-table-column prop="realName" label="姓名" width="120" show-overflow-tooltip />
        <el-table-column prop="staffNo" label="员工工号" width="120" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" width="120" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="120" show-overflow-tooltip />
        <el-table-column prop="idCard" label="身份证号" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
          @size-change="sysUserStore.fetchList"
          @current-change="sysUserStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="所属门店ID（总店长为空）">
          <el-input v-model="formData.storeId" placeholder="请输入所属门店ID（总店长为空）" />
        </el-form-item>
        <el-form-item label="登录账号（手机号）">
          <el-input v-model="formData.username" placeholder="请输入登录账号（手机号）" />
        </el-form-item>
        <el-form-item label="登录密码（生产用bcrypt加密存储）">
          <el-input v-model="formData.password" placeholder="请输入登录密码（生产用bcrypt加密存储）" />
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
        <el-form-item label="角色ID">
          <el-input v-model="formData.roleId" placeholder="请输入角色ID" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useSysUserStore } from '@/stores/sysUser'
import sysUserApi from '@/api/sysUser'

const sysUserStore = useSysUserStore()

const queryForm = reactive({ keyword: '' })

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

const handleSearch = () => {
  sysUserStore.page = 1
  sysUserStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

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
</style>
