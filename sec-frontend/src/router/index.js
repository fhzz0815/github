import { createRouter, createWebHistory } from 'vue-router'

// 路由配置
const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/index.vue'),
        meta: { title: '首页', requiresAuth: true }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/index.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
    {
      path: '/sysRoles',
      name: 'SysRole',
      component: () => import('@/views/sysRoles/index.vue'),
      meta: { title: '角色管理', requiresAuth: true }
    },
    {
      path: '/sysPermissions',
      name: 'SysPermission',
      component: () => import('@/views/sysPermissions/index.vue'),
      meta: { title: '权限管理', requiresAuth: true }
    },
    {
      path: '/sysRolePermissions',
      name: 'SysRolePermission',
      component: () => import('@/views/sysRolePermissions/index.vue'),
      meta: { title: '角色权限', requiresAuth: true }
    },
    {
      path: '/sysUsers',
      name: 'SysUser',
      component: () => import('@/views/sysUsers/index.vue'),
      meta: { title: '员工管理', requiresAuth: true }
    },
    {
      path: '/staffSchedules',
      name: 'StaffSchedule',
      component: () => import('@/views/staffSchedules/index.vue'),
      meta: { title: '员工排班', requiresAuth: true }
    },
    {
      path: '/staffLoginLogs',
      name: 'StaffLoginLog',
      component: () => import('@/views/staffLoginLogs/index.vue'),
      meta: { title: '登录日志', requiresAuth: true }
    },
    {
      path: '/stores',
      name: 'Store',
      component: () => import('@/views/stores/index.vue'),
      meta: { title: '门店管理', requiresAuth: true }
    },
    {
      path: '/storePaymentSettings',
      name: 'StorePaymentSetting',
      component: () => import('@/views/storePaymentSettings/index.vue'),
      meta: { title: '门店支付设置', requiresAuth: true }
    },
    {
      path: '/memberCategories',
      name: 'MemberCategory',
      component: () => import('@/views/memberCategories/index.vue'),
      meta: { title: '会员类别', requiresAuth: true }
    },
    {
      path: '/members',
      name: 'Member',
      component: () => import('@/views/members/index.vue'),
      meta: { title: '会员管理', requiresAuth: true }
    },
    {
      path: '/memberAddresses',
      name: 'MemberAddress',
      component: () => import('@/views/memberAddresses/index.vue'),
      meta: { title: '收货地址', requiresAuth: true }
    },
    {
      path: '/memberBankCards',
      name: 'MemberBankCard',
      component: () => import('@/views/memberBankCards/index.vue'),
      meta: { title: '会员银行卡', requiresAuth: true }
    },
    {
      path: '/memberBalanceRecords',
      name: 'MemberBalanceRecord',
      component: () => import('@/views/memberBalanceRecords/index.vue'),
      meta: { title: '钱包流水', requiresAuth: true }
    },
    {
      path: '/memberPointsRecords',
      name: 'MemberPointsRecord',
      component: () => import('@/views/memberPointsRecords/index.vue'),
      meta: { title: '积分流水', requiresAuth: true }
    },
    {
      path: '/memberRechargeRecords',
      name: 'MemberRechargeRecord',
      component: () => import('@/views/memberRechargeRecords/index.vue'),
      meta: { title: '充值记录', requiresAuth: true }
    },
    {
      path: '/redPackets',
      name: 'RedPacket',
      component: () => import('@/views/redPackets/index.vue'),
      meta: { title: '红包管理', requiresAuth: true }
    },
    {
      path: '/dishCategories',
      name: 'DishCategory',
      component: () => import('@/views/dishCategories/index.vue'),
      meta: { title: '菜品分类', requiresAuth: true }
    },
    {
      path: '/dishTastes',
      name: 'DishTaste',
      component: () => import('@/views/dishTastes/index.vue'),
      meta: { title: '菜品口味', requiresAuth: true }
    },
    {
      path: '/dishSpecs',
      name: 'DishSpec',
      component: () => import('@/views/dishSpecs/index.vue'),
      meta: { title: '菜品规格', requiresAuth: true }
    },
    {
      path: '/ingredientCategories',
      name: 'IngredientCategory',
      component: () => import('@/views/ingredientCategories/index.vue'),
      meta: { title: '原料类别', requiresAuth: true }
    },
    {
      path: '/ingredients',
      name: 'Ingredient',
      component: () => import('@/views/ingredients/index.vue'),
      meta: { title: '原料管理', requiresAuth: true }
    },
    {
      path: '/dishes',
      name: 'Dish',
      component: () => import('@/views/dishes/index.vue'),
      meta: { title: '菜品管理', requiresAuth: true }
    },
    {
      path: '/dishIngredientRels',
      name: 'DishIngredientRel',
      component: () => import('@/views/dishIngredientRels/index.vue'),
      meta: { title: '菜品原料', requiresAuth: true }
    },
    {
      path: '/dishReviews',
      name: 'DishReview',
      component: () => import('@/views/dishReviews/index.vue'),
      meta: { title: '菜品评价', requiresAuth: true }
    },
    {
      path: '/tableTypes',
      name: 'TableType',
      component: () => import('@/views/tableTypes/index.vue'),
      meta: { title: '桌型管理', requiresAuth: true }
    },
    {
      path: '/diningTables',
      name: 'DiningTable',
      component: () => import('@/views/diningTables/index.vue'),
      meta: { title: '台桌管理', requiresAuth: true }
    },
    {
      path: '/queues',
      name: 'Queue',
      component: () => import('@/views/queues/index.vue'),
      meta: { title: '排队管理', requiresAuth: true }
    },
    {
      path: '/reservations',
      name: 'Reservation',
      component: () => import('@/views/reservations/index.vue'),
      meta: { title: '预约管理', requiresAuth: true }
    },
    {
      path: '/carts',
      name: 'Cart',
      component: () => import('@/views/carts/index.vue'),
      meta: { title: '购物车', requiresAuth: true }
    },
    {
      path: '/orders',
      name: 'Orders',
      component: () => import('@/views/orders/index.vue'),
      meta: { title: '订单管理', requiresAuth: true }
    },
    {
      path: '/orderDetails',
      name: 'OrderDetail',
      component: () => import('@/views/orderDetails/index.vue'),
      meta: { title: '订单明细', requiresAuth: true }
    },
    {
      path: '/orderStatusLogs',
      name: 'OrderStatusLog',
      component: () => import('@/views/orderStatusLogs/index.vue'),
      meta: { title: '订单日志', requiresAuth: true }
    },
    {
      path: '/paymentRecords',
      name: 'PaymentRecord',
      component: () => import('@/views/paymentRecords/index.vue'),
      meta: { title: '支付记录', requiresAuth: true }
    },
    {
      path: '/refunds',
      name: 'Refund',
      component: () => import('@/views/refunds/index.vue'),
      meta: { title: '退单管理', requiresAuth: true }
    },
    {
      path: '/coupons',
      name: 'Coupon',
      component: () => import('@/views/coupons/index.vue'),
      meta: { title: '优惠券', requiresAuth: true }
    },
    {
      path: '/memberCoupons',
      name: 'MemberCoupon',
      component: () => import('@/views/memberCoupons/index.vue'),
      meta: { title: '用户优惠券', requiresAuth: true }
    },
    {
      path: '/dishStocks',
      name: 'DishStock',
      component: () => import('@/views/dishStocks/index.vue'),
      meta: { title: '菜品库存', requiresAuth: true }
    },
    {
      path: '/ingredientStocks',
      name: 'IngredientStock',
      component: () => import('@/views/ingredientStocks/index.vue'),
      meta: { title: '原料库存', requiresAuth: true }
    },
    {
      path: '/stockCheckDishes',
      name: 'StockCheckDish',
      component: () => import('@/views/stockCheckDishes/index.vue'),
      meta: { title: '菜品盘点', requiresAuth: true }
    },
    {
      path: '/stockCheckIngredients',
      name: 'StockCheckIngredient',
      component: () => import('@/views/stockCheckIngredients/index.vue'),
      meta: { title: '原料盘点', requiresAuth: true }
    },
    {
      path: '/printers',
      name: 'Printer',
      component: () => import('@/views/printers/index.vue'),
      meta: { title: '打印机', requiresAuth: true }
    },
    {
      path: '/receiptTemplates',
      name: 'ReceiptTemplate',
      component: () => import('@/views/receiptTemplates/index.vue'),
      meta: { title: '小票模板', requiresAuth: true }
    },
    {
      path: '/feedbacks',
      name: 'Feedback',
      component: () => import('@/views/feedbacks/index.vue'),
      meta: { title: '意见反馈', requiresAuth: true }
    }
    ]
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { title: '404' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：登录校验
router.beforeEach((to, from, next) => {
  document.title = `${to.meta.title || ''} - 智慧餐厅后台`
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
