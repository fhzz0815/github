/**
 * 全局枚举常量定义
 * 所有页面的下拉选项、状态标签统一从这里取，杜绝重复定义
 */

// 通用状态
export const COMMON_STATUS = Object.freeze([
  { value: 1, label: '正常', type: 'success' },
  { value: 0, label: '停用', type: 'danger' }
])

// 通用是否
export const YES_NO = Object.freeze([
  { value: 1, label: '是', type: 'primary' },
  { value: 0, label: '否', type: 'info' }
])

// 性别
export const GENDER = Object.freeze([
  { value: 0, label: '未知' },
  { value: 1, label: '男' },
  { value: 2, label: '女' }
])

// 订单类型
export const ORDER_TYPE = Object.freeze([
  { value: 1, label: '堂食', type: 'primary' },
  { value: 2, label: '外卖', type: 'warning' },
  { value: 3, label: '自取', type: 'info' }
])

// 订单状态（对应后端 OrderStatusEnum）
export const ORDER_STATUS = Object.freeze([
  { value: 1, label: '待支付', type: 'warning' },
  { value: 2, label: '已支付', type: 'processing' },
  { value: 3, label: '制作中', type: 'processing' },
  { value: 4, label: '已上齐', type: 'primary' },
  { value: 5, label: '已完成', type: 'success' },
  { value: 6, label: '已取消', type: 'info' },
  { value: 7, label: '已退款', type: 'danger' }
])

// 支付状态
export const PAY_STATUS = Object.freeze([
  { value: 1, label: '未支付', type: 'warning' },
  { value: 2, label: '已支付', type: 'success' },
  { value: 3, label: '已退款', type: 'danger' }
])

// 菜品状态：上架/下架
export const DISH_STATUS = Object.freeze([
  { value: 1, label: '上架', type: 'success' },
  { value: 0, label: '下架', type: 'danger' }
])

// 制作状态（订单明细）
export const MAKE_STATUS = Object.freeze([
  { value: 1, label: '待制作', type: 'warning' },
  { value: 2, label: '制作中', type: 'primary' },
  { value: 3, label: '已完成', type: 'success' },
  { value: 4, label: '已退菜', type: 'danger' }
])

// 盘点审核状态
export const CHECK_STATUS = Object.freeze([
  { value: 1, label: '待审核', type: 'warning' },
  { value: 2, label: '已通过', type: 'success' },
  { value: 3, label: '已驳回', type: 'danger' }
])

// 退款状态
export const REFUND_STATUS = Object.freeze([
  { value: 1, label: '待审核', type: 'warning' },
  { value: 2, label: '已通过', type: 'success' },
  { value: 3, label: '已驳回', type: 'danger' }
])

// 就餐方式（外卖/自取）
export const TAKE_TYPE = Object.freeze([
  { value: 1, label: '外卖配送' },
  { value: 2, label: '到店自取' }
])

// 优惠券类型
export const COUPON_TYPE = Object.freeze([
  { value: 1, label: '满减券' },
  { value: 2, label: '折扣券' },
  { value: 3, label: '代金券' }
])

// 红包状态
export const RED_PACKET_STATUS = Object.freeze([
  { value: 1, label: '待领取', type: 'warning' },
  { value: 2, label: '已领取', type: 'success' },
  { value: 3, label: '已过期', type: 'info' },
  { value: 4, label: '已使用', type: 'default' }
])

// 员工角色级别（用于显示层级）
export const ROLE_LEVEL_MAP = Object.freeze({
  99: { label: '总店长', type: 'danger' },
  50: { label: '店长', type: 'warning' },
  10: { label: '员工', type: 'primary' },
  5: { label: '临时', type: 'info' }
})

// 所有枚举的汇总映射，方便通过枚举名查找
const ENUM_MAP = Object.freeze({
  status: COMMON_STATUS,
  isRecommend: YES_NO,
  isNew: YES_NO,
  isSoldOut: YES_NO,
  gender: GENDER,
  orderType: ORDER_TYPE,
  orderStatus: ORDER_STATUS,
  payStatus: PAY_STATUS,
  dishStatus: DISH_STATUS,
  makeStatus: MAKE_STATUS,
  checkStatus: CHECK_STATUS,
  refundStatus: REFUND_STATUS,
  takeType: TAKE_TYPE,
  couponType: COUPON_TYPE,
  redPacketStatus: RED_PACKET_STATUS
})

/**
 * 根据枚举名和值，获取对应的标签文字
 */
export function getEnumLabel(enumName, value, fallback = '--') {
  if (value === null || value === undefined || value === '') return fallback
  const list = ENUM_MAP[enumName]
  if (!list) return fallback
  const found = list.find((i) => i.value === value)
  return found ? found.label : fallback
}

/**
 * 根据枚举名和值，获取对应的标签类型（用于 el-tag 的 type）
 */
export function getEnumType(enumName, value) {
  const list = ENUM_MAP[enumName]
  if (!list) return 'default'
  const found = list.find((i) => i.value === value)
  return found?.type || 'default'
}

export default ENUM_MAP
