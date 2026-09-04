// 权限管理 接口
import request from './request'

const baseURL = '/sysPermissions'

export default {
  // 查询列表
  list(params) {
    return request({ url: baseURL, method: 'get', params })
  },
  // 查询详情
  get(id) {
    return request({ url: `${baseURL}/${id}`, method: 'get' })
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
  }
}
