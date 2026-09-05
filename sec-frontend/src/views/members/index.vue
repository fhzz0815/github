<template>
  <div class="members-page">
        <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryForm">
        <el-form-item label="关键字">
          <el-input v-model="queryForm.keyword" placeholder="请输入真实姓名/会员卡号/绑定手机号" clearable style="width: 220px" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="queryForm.gender" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.gender" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" clearable placeholder="全部" style="width: 130px">
            <el-option v-for="o in enumOptions.status" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="会员类别">
          <el-select v-model="queryForm.memberCategoryId" clearable filterable placeholder="全部" style="width: 150px">
            <el-option v-for="o in searchOptions.memberCategories" :key="o.id" :label="o.categoryName" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册门店">
          <el-select v-model="queryForm.registerStoreId" clearable filterable placeholder="全部" style="width: 150px">
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
          @size-change="onPageChange"
          @current-change="onPageChange"
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

import memberCategoryApi from '@/api/memberCategory'
import storeApi from '@/api/store'

// ===== 搜索专用：枚举选项 / 外键下拉数据 =====
const enumOptions = {
  gender: [{ value: 0, label: '未知' }, { value: 1, label: '男' }, { value: 2, label: '女' }],
  status: [{ value: 1, label: '正常' }, { value: 0, label: '冻结' }]
}
const searchOptions = reactive({
  memberCategories: [],
  stores: []
})
const loadSearchOptions = () => {
  memberCategoryApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.memberCategories = r.data?.list || [] }).catch(() => {})
  storeApi.list({ page: 1, size: 1000 }).then((r) => { searchOptions.stores = r.data?.list || [] }).catch(() => {})
}
loadSearchOptions()

// 搜索表单
const queryForm = reactive({
  keyword: '',
  gender: null,
  status: null,
  memberCategoryId: null,
  registerStoreId: null,
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
  memberStore.page = 1
  memberStore.fetchList(buildParams())
}
const handleReset = () => {
  Object.keys(queryForm).forEach((k) => { queryForm[k] = k === 'dateRange' ? null : '' })
  memberStore.page = 1
  memberStore.fetchList()
}
// 翻页时保留搜索条件
const onPageChange = () => {
  memberStore.fetchList(buildParams())
}

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
