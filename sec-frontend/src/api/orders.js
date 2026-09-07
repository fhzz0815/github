// 订单管理 接口
import request from './request'

const baseURL = '/orders'

export default {
  // 查询列表
  list(params) {
    return request({ url: baseURL, method: 'get', params })
  },
  // 查询详情
  get(id) {
    return request({ url: `${baseURL}/${id}`, method: 'get' })
  },
  // 查询订单详情（含明细）
  getWithDetails(id) {
    return request({ url: `${baseURL}/${id}/with-details`, method: 'get' })
  },
  // 查询订单状态日志
  getStatusLogs(id) {
    return request({ url: `${baseURL}/${id}/status-logs`, method: 'get' })
  },
  // 新增
  create(data) {
    return request({ url: baseURL, method: 'post', data })
  },
  // 修改
  update(id, data) {
    return request({ url: `${baseURL}/${id}`, method: 'put', data })
  },
  // 删除
  delete(id) {
    return request({ url: `${baseURL}/${id}`, method: 'delete' })
  },
  // 提交订单
  submit(data) {
    return request({ url: `${baseURL}/submit`, method: 'post', data })
  },
  // 支付订单
  pay(id, data) {
    return request({ url: `${baseURL}/${id}/pay`, method: 'post', data })
  },
  // 取消订单
  cancel(id, data) {
    return request({ url: `${baseURL}/${id}/cancel`, method: 'post', data })
  },
  // 更新制作状态
  updateMakeStatus(id, data) {
    return request({ url: `${baseURL}/${id}/make-status`, method: 'post', data })
  },
  // 获取后厨订单列表
  kitchen(storeId) {
    return request({ url: `${baseURL}/kitchen`, method: 'get', params: { storeId } })
  },
  // 获取销售报表
  salesReport(storeId, beginDate, endDate) {
    return request({
      url: `${baseURL}/report/sales`,
      method: 'get',
      params: { storeId, beginDate, endDate }
    })
  },
  // 获取今日概况
  todaySummary(storeId) {
    return request({
      url: `${baseURL}/report/today`,
      method: 'get',
      params: { storeId }
    })
  }
}
