<template>
  <div class="members-page">
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

      <el-table :data="memberStore.list" v-loading="memberStore.loading" border stripe style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="openid" label="微信openid" width="160" show-overflow-tooltip />
        <el-table-column prop="unionid" label="微信unionid" width="160" show-overflow-tooltip />
        <el-table-column prop="nickname" label="昵称" width="120" show-overflow-tooltip />
        <el-table-column prop="avatar" label="头像URL" width="120" show-overflow-tooltip />
        <el-table-column prop="gender" label="性别 0未知 1男 2女" width="160" show-overflow-tooltip />
        <el-table-column prop="phone" label="绑定手机号" width="120" show-overflow-tooltip />
        <el-table-column prop="realName" label="真实姓名" width="120" show-overflow-tooltip />
        <el-table-column prop="idCard" label="身份证号（钱包实名）" width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="160" fixed="right">
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
          @size-change="memberStore.fetchList"
          @current-change="memberStore.fetchList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="微信openid">
          <el-input v-model="formData.openid" placeholder="请输入微信openid" />
        </el-form-item>
        <el-form-item label="微信unionid">
          <el-input v-model="formData.unionid" placeholder="请输入微信unionid" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="formData.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="头像URL">
          <el-input v-model="formData.avatar" placeholder="请输入头像URL" />
        </el-form-item>
        <el-form-item label="性别 0未知 1男 2女">
          <el-input v-model="formData.gender" placeholder="请输入性别 0未知 1男 2女" />
        </el-form-item>
        <el-form-item label="绑定手机号">
          <el-input v-model="formData.phone" placeholder="请输入绑定手机号" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号（钱包实名）">
          <el-input v-model="formData.idCard" placeholder="请输入身份证号（钱包实名）" />
        </el-form-item>
        <el-form-item label="会员卡号">
          <el-input v-model="formData.memberNo" placeholder="请输入会员卡号" />
        </el-form-item>
        <el-form-item label="会员类别ID">
          <el-input v-model="formData.memberCategoryId" placeholder="请输入会员类别ID" />
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

const memberStore = useMemberStore()

const queryForm = reactive({ keyword: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('新增会员')
const formData = reactive({
  id: null,
  openid: '',
  unionid: '',
  nickname: '',
  avatar: '',
  gender: '',
  phone: '',
  realName: '',
  idCard: '',
  memberNo: '',
  memberCategoryId: ''
})

const handleSearch = () => {
  memberStore.page = 1
  memberStore.fetchList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增会员'
  formData.id = null
  formData.openid = ''
  formData.unionid = ''
  formData.nickname = ''
  formData.avatar = ''
  formData.gender = ''
  formData.phone = ''
  formData.realName = ''
  formData.idCard = ''
  formData.memberNo = ''
  formData.memberCategoryId = ''
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑会员'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除这条记录吗？', '提示', { type: 'warning' })
    .then(async () => {
      await memberApi.delete(row.id)
      ElMessage.success('删除成功')
      memberStore.fetchList()
    })
    .catch(() => {})
}

const handleRefresh = () => {
  memberStore.fetchList()
}

const handleSubmit = async () => {
  if (formData.id) {
    await memberApi.update(formData.id, formData)
  } else {
    await memberApi.create(formData)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  memberStore.fetchList()
}

onMounted(() => {
  memberStore.fetchList()
})
</script>

<style scoped>
.members-page { padding: 16px; }
.search-card { margin-bottom: 16px; }
.table-toolbar { margin-bottom: 16px; }
.pagination { margin-top: 16px; display: flex; justify-content: flex-end; }
</style>
