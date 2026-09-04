// 会员管理 状态管理
import { defineStore } from 'pinia'
import memberApi from '@/api/member'

export const useMemberStore = defineStore('member', {
  state: () => ({
    // 列表数据
    list: [],
    // 总数
    total: 0,
    // 当前页
    page: 1,
    // 每页条数
    size: 10,
    // 加载状态
    loading: false,
    // 详情数据
    detail: null
  }),
  getters: {
    // 是否有数据
    hasData: (state) => state.list.length > 0
  },
  actions: {
    // 获取列表
    async fetchList(params = {}) {
      this.loading = true
      try {
        const res = await memberApi.list({ page: this.page, size: this.size, ...params })
        this.list = res.data?.list || []
        this.total = res.data?.total || 0
      } finally {
        this.loading = false
      }
    },
    // 获取详情
    async fetchDetail(id) {
      const res = await memberApi.get(id)
      this.detail = res.data
      return res.data
    },
    // 新增
    async create(data) {
      const res = await memberApi.create(data)
      return res
    },
    // 修改
    async update(id, data) {
      const res = await memberApi.update(id, data)
      return res
    },
    // 删除
    async remove(id) {
      const res = await memberApi.delete(id)
      return res
    }
  },
  persist: true
})
