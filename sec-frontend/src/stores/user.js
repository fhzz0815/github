// 用户状态管理
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => ({
    // 用户信息
    userInfo: null,
    // 权限列表
    permissions: [],
    // 角色
    roles: [],
    // 当前用户角色等级（99=总店长 / 50=店长 / 10=普通员工）
    roleLevel: null,
    // 当前用户所属门店（总店长为 null）
    storeId: null
  }),
  getters: {
    // 是否已登录
    isLoggedIn: (state) => !!localStorage.getItem('token'),
    // 是否总店长（level 99）
    isGeneralManager: (state) => state.roleLevel != null && state.roleLevel >= 99,
    // 是否店长及以上（level 50-98）
    isStoreManager: (state) => state.roleLevel != null && state.roleLevel >= 50 && state.roleLevel < 99,
    // 是否可以管理员工（店长及以上）
    canManageUsers: (state) => state.roleLevel != null && state.roleLevel >= 50,
    // 是否可以管理角色/权限等系统模块（仅总店长）
    canManageSystem: (state) => state.roleLevel != null && state.roleLevel >= 99
  },
  actions: {
    // 设置用户信息，同时同步角色等级和门店ID
    setUserInfo(info) {
      this.userInfo = info
      // 后端返回的 SysUserEntity 已带 roleLevel / storeId（联表回显）
      this.roleLevel = info?.roleLevel ?? null
      this.storeId = info?.storeId ?? null
    },
    // 设置权限
    setPermissions(perms) {
      this.permissions = perms
    },
    // 退出登录
    logout() {
      this.userInfo = null
      this.permissions = []
      this.roleLevel = null
      this.storeId = null
      localStorage.removeItem('token')
    }
  },
  persist: true
})
