<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in cards" :key="card.title">
        <el-card shadow="hover">
          <div class="stat-card">
            <el-icon :size="40" :color="card.color"><component :is="card.icon" /></el-icon>
            <div class="stat-info">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-title">{{ card.title }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>欢迎使用智慧餐厅后台管理系统</template>
          <p>本系统提供门店、菜品、订单、会员、库存等全量管理功能。</p>
          <p>请通过左侧菜单进入各功能模块。</p>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never">
          <template #header>快捷入口</template>
          <div class="quick-entry">
            <el-button @click="$router.push('/stores')">门店管理</el-button>
            <el-button @click="$router.push('/dishes')">菜品管理</el-button>
            <el-button @click="$router.push('/orders')">订单管理</el-button>
            <el-button @click="$router.push('/members')">会员管理</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
// 首页统计卡片
const cards = [
  { title: '门店数量', value: 3, icon: 'Shop', color: '#409EFF' },
  { title: '菜品数量', value: 15, icon: 'Food', color: '#67C23A' },
  { title: '会员数量', value: 5, icon: 'User', color: '#E6A23C' },
  { title: '今日订单', value: 128, icon: 'List', color: '#F56C6C' }
]
</script>

<style scoped>
.dashboard {
  padding: 0;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
}
.stat-info {
  flex: 1;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}
.stat-title {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}
.quick-entry {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
</style>
