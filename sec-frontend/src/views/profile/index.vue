<template>
  <div class="profile-page">
    <!-- 顶部个人名片 -->
    <el-card shadow="never" class="head-card">
      <div class="head">
        <el-avatar :size="72" :src="userInfo.avatar">{{ avatarText }}</el-avatar>
        <div class="head-info">
          <div class="head-name">
            {{ userInfo.realName || '未命名' }}
            <el-tag :type="userInfo.status === 1 ? 'success' : 'info'" size="small" style="margin-left: 8px">
              {{ userInfo.status === 1 ? '在职' : '离职' }}
            </el-tag>
          </div>
          <div class="head-sub">账号：{{ userInfo.username }}　|　角色：{{ roleName }}　|　门店：{{ storeName }}</div>
        </div>
      </div>
    </el-card>

    <el-row :gutter="16" style="margin-top: 16px">
      <!-- 左：个人信息 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>个人信息</template>
          <el-descriptions :column="1" border>
            <el-descriptions-item label="姓名">{{ userInfo.realName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="登录账号">{{ userInfo.username || '-' }}</el-descriptions-item>
            <el-descriptions-item label="员工工号">{{ userInfo.staffNo || '-' }}</el-descriptions-item>
            <el-descriptions-item label="手机号">{{ userInfo.phone || '-' }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ userInfo.email || '-' }}</el-descriptions-item>
            <el-descriptions-item label="角色">{{ roleName }}</el-descriptions-item>
            <el-descriptions-item label="所属门店">{{ storeName }}</el-descriptions-item>
            <el-descriptions-item label="最后登录时间">{{ formatTime(userInfo.lastLoginTime) }}</el-descriptions-item>
            <el-descriptions-item label="账号创建时间">{{ formatTime(userInfo.createTime) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>

      <!-- 右：资料编辑 + 修改密码 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>编辑基本资料</template>
          <el-form :model="profileForm" label-width="80px">
            <el-form-item label="姓名">
              <el-input v-model="profileForm.realName" placeholder="请输入姓名" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="profileForm.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="profileForm.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="savingProfile" @click="saveProfile">保存资料</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" style="margin-top: 16px">
          <template #header>修改密码</template>
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="90px">
            <el-form-item label="原密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入原密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="6-20位新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="warning" :loading="savingPwd" @click="changePassword">确认修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import dayjs from 'dayjs'
import request from '@/api/request'
import sysUserApi from '@/api/sysUser'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref({})
const roleList = ref([])
const storeList = ref([])
const savingProfile = ref(false)
const savingPwd = ref(false)
const pwdFormRef = ref(null)

const profileForm = reactive({ realName: '', phone: '', email: '' })
const pwdForm = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

// 头像占位文字：取姓名第一个字
const avatarText = computed(() => (userInfo.value.realName || '用').charAt(0))
// 角色名称：用角色ID在角色列表里找
const roleName = computed(() => {
  const r = roleList.value.find((x) => String(x.id) === String(userInfo.value.roleId))
  return r ? r.roleName : (userInfo.value.roleId ? '角色' + userInfo.value.roleId : '-')
})
// 门店名称：总店长没有门店
const storeName = computed(() => {
  if (!userInfo.value.storeId) return '全国（总部）'
  const s = storeList.value.find((x) => String(x.id) === String(userInfo.value.storeId))
  return s ? s.storeName : '门店' + userInfo.value.storeId
})

// 兼容后端时间可能是时间戳、数组或字符串
function formatTime(t) {
  if (!t) return '-'
  if (Array.isArray(t)) {
    const [y, m, d, h = 0, mi = 0, s = 0] = t
    return dayjs(new Date(y, m - 1, d, h, mi, s)).format('YYYY-MM-DD HH:mm:ss')
  }
  const d = dayjs(t)
  return d.isValid() ? d.format('YYYY-MM-DD HH:mm:ss') : String(t)
}

// 从 token 里解析当前登录人ID
function getUserId() {
  try {
    const token = localStorage.getItem('token') || ''
    const b64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    return JSON.parse(atob(b64)).userId
  } catch {
    return null
  }
}

async function loadData() {
  const userId = getUserId()
  if (!userId) return
  // 并发拉取本人资料、角色列表、门店列表；_noCancel 保证字典数据不被重复请求机制误取消
  const [detail, roles, stores] = await Promise.all([
    request({ url: `/sysUsers/${userId}`, method: 'get', _noCancel: true }),
    request({ url: '/sysRoles', method: 'get', params: { page: 1, size: 1000 }, _noCancel: true }),
    request({ url: '/stores', method: 'get', params: { page: 1, size: 1000 }, _noCancel: true })
  ])
  userInfo.value = detail.data || {}
  roleList.value = (roles.data && roles.data.list) || []
  storeList.value = (stores.data && stores.data.list) || []
  profileForm.realName = userInfo.value.realName || ''
  profileForm.phone = userInfo.value.phone || ''
  profileForm.email = userInfo.value.email || ''
  // 同步到全局状态，顶部栏显示真实姓名
  userStore.setUserInfo({ ...userInfo.value })
}

async function saveProfile() {
  const userId = getUserId()
  if (!userId) return
  savingProfile.value = true
  try {
    await sysUserApi.update(userId, {
      realName: profileForm.realName,
      phone: profileForm.phone,
      email: profileForm.email
    })
    ElMessage.success('资料保存成功')
    await loadData()
  } finally {
    savingProfile.value = false
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为 6-20 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    {
      validator: (rule, value, cb) => {
        if (value !== pwdForm.newPassword) cb(new Error('两次输入的新密码不一致'))
        else cb()
      },
      trigger: 'blur'
    }
  ]
}

async function changePassword() {
  await pwdFormRef.value.validate()
  const userId = getUserId()
  if (!userId) return
  savingPwd.value = true
  try {
    // 先用原密码走一次登录校验，确认原密码正确（错误会返回1004）
    await request.post('/auth/login', { username: userInfo.value.username, password: pwdForm.oldPassword })
    // 原密码正确，再提交新密码（后端会自动MD5加密存储）
    await sysUserApi.update(userId, { password: pwdForm.newPassword })
    ElMessage.success('密码修改成功，请重新登录')
    // 密码已变，旧token失效，退出并回到登录页
    setTimeout(() => {
      userStore.logout()
      router.push('/login')
    }, 1200)
  } finally {
    savingPwd.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.profile-page {
  padding: 0;
}
.head-card {
  background: linear-gradient(135deg, #409eff 0%, #6a8df6 100%);
  border: none;
}
.head-card :deep(.el-card__body) {
  padding: 20px;
}
.head {
  display: flex;
  align-items: center;
  gap: 16px;
}
.head :deep(.el-avatar) {
  background: #fff;
  color: #409eff;
  font-size: 28px;
  font-weight: bold;
}
.head-name {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}
.head-sub {
  margin-top: 8px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
}
</style>
