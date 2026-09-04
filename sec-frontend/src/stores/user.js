// 用户状态管理
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    // 用户信息
    userInfo: null,
    // 权限列表
    permissions: [],
    // 角色
    roles: []
  }),
  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!localStorage.getItem('token')
  },
  actions: {
    // 设置用户信息
    setUserInfo(info) {
      this.userInfo = info
    },
    // 设置权限
    setPermissions(perms) {
      this.permissions = perms
    },
    // 退出登录
    logout() {
      this.userInfo = null
      this.permissions = []
      localStorage.removeItem('token')
    }
  },
  persist: true
})
