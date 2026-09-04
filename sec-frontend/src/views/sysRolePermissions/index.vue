<template>
  <div class="sysRolePermissions-page">
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

      <el-table :data="sysRolePermissionStore.list" v-loading="sysRolePermissionStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="roleId" label="角色ID" width="120" show-overflow-tooltip />
        <el-table-column prop="permissionId" label="权限ID" width="120" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="sysRolePermissionStore.page"
          v-model:page-size="sysRolePermissionStore.size"
          :total="sysRolePermissionStore.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="sysRolePermissionStore.fetchList"
          @current-change="sysRolePermissionStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="角色ID">
          <el-input v-model="formData.roleId" placeholder="请输入角色ID" />
        </el-form-item>
        <el-form-item label="权限ID">
          <el-input v-model="formData.permissionId" placeholder="请输入权限ID" />
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
import { useSysRolePermissionStore } from '@/stores/sysRolePermission'
import sysRolePermissionApi from '@/api/sysRolePermission'

const sysRolePermissionStore = useSysRolePermissionStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增角色权限')
const formData = reactive({
  id: null,
  roleId: '',
  permissionId: ''
})

const handleSearch = () => {
  sysRolePermissionStore.page = 1
  sysRolePermissionStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增角色权限'
  formData.id = null
  formData.roleId = ''
  formData.permissionId = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色权限'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await sysRolePermissionApi.delete(row.id)
      ElMessage.success('删除成功')
      sysRolePermissionStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  sysRolePermissionStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await sysRolePermissionApi.update(formData.id, formData)
  } else {
    await sysRolePermissionApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  sysRolePermissionStore.fetchList()
}

onMounted(() => {
  sysRolePermissionStore.fetchList()
})
</script>

<style scoped>
.sysRolePermissions-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
