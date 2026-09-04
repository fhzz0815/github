<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
      <div class="logo">
        <span v-if="!isCollapse">智慧餐厅</span>
        <span v-else>智</span>
      </div>
      <el-menu
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
        <el-sub-menu v-for="group in menuGroups" :key="group.title" :index="group.title">
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
              <el-avatar :size="32" :src="userStore.userInfo?.avatar" />
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

// 菜单分组
const menuGroups = [
  {
    title: '门店管理',
    icon: 'Shop',
    children: [
      { path: '/stores', title: '门店列表' },
      { path: '/store-payment-settings', title: '支付设置' },
      { path: '/dining-tables', title: '台桌管理' },
      { path: '/table-types', title: '桌型管理' }
    ]
  },
  {
    title: '员工管理',
    icon: 'User',
    children: [
      { path: '/sys-users', title: '员工列表' },
      { path: '/sys-roles', title: '角色管理' },
      { path: '/staff-schedules', title: '排班管理' },
      { path: '/staff-login-logs', title: '登录日志' }
    ]
  },
  {
    title: '菜品管理',
    icon: 'Food',
    children: [
      { path: '/dishes', title: '菜品列表' },
      { path: '/dish-categories', title: '菜品分类' },
      { path: '/dish-tastes', title: '菜品口味' },
      { path: '/dish-specs', title: '菜品规格' },
      { path: '/ingredients', title: '原料管理' },
      { path: '/ingredient-categories', title: '原料类别' }
    ]
  },
  {
    title: '订单管理',
    icon: 'List',
    children: [
      { path: '/orders', title: '订单列表' },
      { path: '/order-details', title: '订单明细' },
      { path: '/order-status-logs', title: '订单日志' },
      { path: '/payment-records', title: '支付记录' },
      { path: '/refunds', title: '退单管理' }
    ]
  },
  {
    title: '会员管理',
    icon: 'Avatar',
    children: [
      { path: '/members', title: '会员列表' },
      { path: '/member-categories', title: '会员类别' },
      { path: '/member-addresses', title: '收货地址' },
      { path: '/member-bank-cards', title: '银行卡' },
      { path: '/member-balance-records', title: '钱包流水' },
      { path: '/member-points-records', title: '积分流水' },
      { path: '/member-recharge-records', title: '充值记录' }
    ]
  },
  {
    title: '库存管理',
    icon: 'Box',
    children: [
      { path: '/dish-stocks', title: '菜品库存' },
      { path: '/ingredient-stocks', title: '原料库存' },
      { path: '/stock-check-dishes', title: '菜品盘点' },
      { path: '/stock-check-ingredients', title: '原料盘点' }
    ]
  },
  {
    title: '营销中心',
    icon: 'Present',
    children: [
      { path: '/coupons', title: '优惠券' },
      { path: '/member-coupons', title: '用户优惠券' },
      { path: '/red-packets', title: '红包管理' }
    ]
  },
  {
    title: '系统设置',
    icon: 'Setting',
    children: [
      { path: '/printers', title: '打印机' },
      { path: '/receipt-templates', title: '小票模板' },
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
    router.push('/dashboard')
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
