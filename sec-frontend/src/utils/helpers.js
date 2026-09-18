/**
 * 通用工具函数
 * 抽离各页面重复的逻辑：搜索参数拼接、时间格式化等
 */

/**
 * 把搜索表单整理成接口参数
 * - 剔除空值（null / undefined / 空字符串）
 * - 将 keyword 映射为 searchKeyword
 * - 将 dateRange 拆分为 searchBeginTime / searchEndTime
 */
export function buildSearchParams(queryForm, extra = {}) {
  const params = { ...queryForm }

  // 默认映射：keyword 转 searchKeyword
  if ('keyword' in params) {
    params.searchKeyword = params.keyword
    delete params.keyword
  }

  // 自定义映射
  Object.entries(extra).forEach(([from, to]) => {
    if (from in params) {
      params[to] = params[from]
      delete params[from]
    }
  })

  // 拆分时间范围
  if (params.dateRange && Array.isArray(params.dateRange) && params.dateRange.length === 2) {
    params.searchBeginTime = params.dateRange[0]
    params.searchEndTime = params.dateRange[1]
  }
  delete params.dateRange

  // 剔除空值
  Object.keys(params).forEach((k) => {
    if (params[k] === '' || params[k] === null || params[k] === undefined) {
      delete params[k]
    }
  })

  return params
}

/**
 * 格式化日期为 YYYY-MM-DD
 */
export function formatDate(date) {
  if (!date) return ''
  const d = date instanceof Date ? date : new Date(date)
  if (isNaN(d.getTime())) return ''
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return y + '-' + m + '-' + day
}

/**
 * 格式化时间为 YYYY-MM-DD HH:mm:ss
 */
export function formatTime(date) {
  if (!date) return ''
  const d = date instanceof Date ? date : new Date(date)
  if (isNaN(d.getTime())) return date
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const h = String(d.getHours()).padStart(2, '0')
  const min = String(d.getMinutes()).padStart(2, '0')
  const s = String(d.getSeconds()).padStart(2, '0')
  return y + '-' + m + '-' + day + ' ' + h + ':' + min + ':' + s
}

/**
 * 根据 id 从数组中查找对应的 label（ID 转名称）
 */
export function findLabel(list, id, labelField = 'name', idField = 'id', fallback = '--') {
  if (!list || !id && id !== 0) return fallback
  const found = list.find((i) => i[idField] === id)
  return found ? found[labelField] : fallback
}

/**
 * 重置搜索表单为初始值
 */
export function resetForm(form, initial = {}) {
  Object.keys(form).forEach((k) => {
    form[k] = k in initial ? initial[k] : (k === 'dateRange' ? null : '')
  })
}
