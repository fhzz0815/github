<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse">智慧餐厅</span>
        <span v-else>智</span>
      </div>
      <el-menu
        class="sidebar-menu"
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        background-color="#001529"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard">
          <el-icon><Odometer /></el-icon>
          <template #title>首页</template>
        </el-menu-item>
        <el-sub-menu v-for="group in filteredMenuGroups" :key="group.title" :index="group.title">
          <template #title>
            <el-icon><component :is="group.icon" /></el-icon>
            <span>{{ group.title }}</span>
          </template>
          <el-menu-item v-for="item in group.children" :key="item.path" :index="item.path">
            {{ item.title }}
          </el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <!-- 头像：有图片时显示图片；为空或加载失败时显示用户名首字母作为兜底 -->
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" @error="onAvatarError">
                <span class="avatar-fallback">{{ avatarFallbackText }}</span>
              </el-avatar>
              <span class="username">{{ userStore.userInfo?.realName || '管理员' }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区域 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const activeMenu = computed(() => route.path)

// 仅总店长可见的菜单路径（角色管理、权限管理、角色权限、门店管理）
const GM_ONLY_PATHS = [
  '/sysRoles',
  '/sysPermissions',
  '/sysRolePermissions',
  '/stores'
]

// 判断某个菜单项当前用户是否可见
function canSeeMenu(path) {
  if (GM_ONLY_PATHS.includes(path)) {
    return userStore.isGeneralManager
  }
  return true
}

// 按角色过滤后的菜单（移除不可见的菜单项，若分组无可见项则整个分组隐藏）
const filteredMenuGroups = computed(() => {
  return menuGroups
    .map((g) => ({ ...g, children: (g.children || []).filter((c) => canSeeMenu(c.path)) }))
    .filter((g) => g.children.length > 0)
})

// 头像兜底：当用户没设置头像或图片加载失败时，显示姓名首字母
const avatarFallbackText = computed(() => {
  const name = userStore.userInfo?.realName || userStore.userInfo?.username || '管'
  // 取第一个字符（中文取首字，英文取首字母大写）
  return name.charAt(0).toUpperCase()
})
// 头像图片加载失败时返回 true，让 el-avatar 显示 default slot 兜底内容
const onAvatarError = () => true

// 菜单分组（path 必须与 router 里的路由地址完全一致，使用驼峰命名）
const menuGroups = [
  {
    title: '门店管理',
    icon: 'Shop',
    children: [
      { path: '/stores', title: '门店列表' },
      { path: '/storePaymentSettings', title: '支付设置' },
      { path: '/diningTables', title: '台桌管理' },
      { path: '/tableTypes', title: '桌型管理' },
      { path: '/queues', title: '排队管理' },
      { path: '/reservations', title: '预约管理' }
    ]
  },
  {
    title: '员工管理',
    icon: 'User',
    children: [
      { path: '/sysUsers', title: '员工列表' },
      { path: '/sysRoles', title: '角色管理' },
      { path: '/sysPermissions', title: '权限管理' },
      { path: '/sysRolePermissions', title: '角色权限' },
      { path: '/staffSchedules', title: '排班管理' },
      { path: '/staffLoginLogs', title: '登录日志' }
    ]
  },
  {
    title: '菜品管理',
    icon: 'Food',
    children: [
      { path: '/dishes', title: '菜品列表' },
      { path: '/dishCategories', title: '菜品分类' },
      { path: '/dishTastes', title: '菜品口味' },
      { path: '/dishSpecs', title: '菜品规格' },
      { path: '/dishReviews', title: '菜品评价' },
      { path: '/ingredients', title: '原料管理' },
      { path: '/ingredientCategories', title: '原料类别' },
      { path: '/dishIngredientRels', title: '菜品原料' }
    ]
  },
  {
    title: '订单管理',
    icon: 'List',
    children: [
      { path: '/orders', title: '订单列表' },
      { path: '/orderDetails', title: '订单明细' },
      { path: '/orderStatusLogs', title: '订单日志' },
      { path: '/paymentRecords', title: '支付记录' },
      { path: '/refunds', title: '退单管理' },
      { path: '/carts', title: '购物车' }
    ]
  },
  {
    title: '会员管理',
    icon: 'Avatar',
    children: [
      { path: '/members', title: '会员列表' },
      { path: '/memberCategories', title: '会员类别' },
      { path: '/memberAddresses', title: '收货地址' },
      { path: '/memberBankCards', title: '银行卡' },
      { path: '/memberBalanceRecords', title: '钱包流水' },
      { path: '/memberPointsRecords', title: '积分流水' },
      { path: '/memberRechargeRecords', title: '充值记录' },
      { path: '/redPackets', title: '红包管理' }
    ]
  },
  {
    title: '库存管理',
    icon: 'Box',
    children: [
      { path: '/dishStocks', title: '菜品库存' },
      { path: '/ingredientStocks', title: '原料库存' },
      { path: '/stockCheckDishes', title: '菜品盘点' },
      { path: '/stockCheckIngredients', title: '原料盘点' }
    ]
  },
  {
    title: '营销中心',
    icon: 'Present',
    children: [
      { path: '/coupons', title: '优惠券' },
      { path: '/memberCoupons', title: '用户优惠券' }
    ]
  },
  {
    title: '系统设置',
    icon: 'Setting',
    children: [
      { path: '/printers', title: '打印机' },
      { path: '/receiptTemplates', title: '小票模板' },
      { path: '/feedbacks', title: '意见反馈' }
    ]
  }
]

const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
      .then(() => {
        userStore.logout()
        router.push('/login')
      })
      .catch(() => {})
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}
.sidebar {
  background-color: #001529;
  transition: width 0.3s;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}
/* 只针对最外层主菜单：固定占满侧边栏剩余高度，内容超出时在菜单内部滚动。
   注意不能用 :deep(.el-menu)，因为展开分组时子菜单 ul.el-menu--inline 也带 el-menu 类，
   会被错误地设成 879px 高，导致每个分组展开后下方出现一大片空白。 */
.sidebar-menu {
  flex: 1;
  height: calc(100vh - 60px);
  overflow-y: auto;
  overflow-x: hidden;
  border-right: none;
}
/* 滚动条样式，贴合深色侧边栏 */
.sidebar-menu::-webkit-scrollbar {
  width: 6px;
}
.sidebar-menu::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}
.sidebar-menu::-webkit-scrollbar-track {
  background: transparent;
}
.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  background: #002140;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  padding: 0 16px;
}
.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.collapse-btn {
  font-size: 20px;
  cursor: pointer;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}
.username {
  font-size: 14px;
}
/* 头像兜底首字母样式 */
.avatar-fallback {
  display: inline-block;
  width: 100%;
  height: 100%;
  line-height: 32px;
  text-align: center;
  font-size: 14px;
  font-weight: bold;
  color: #fff;
  background: linear-gradient(135deg, #409EFF 0%, #6c63ff 100%);
}
.main-content {
  background: #f5f7fa;
  overflow-y: auto;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
