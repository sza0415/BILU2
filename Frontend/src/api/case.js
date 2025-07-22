import request from './request'

/**
 * 案件管理相关API
 */

// 获取案件列表
export function getCaseList() {
  return request({
    url: '/case/list',
    method: 'get'
  })
}

// 获取案件类型
export function getCaseTypes() {
  return request({
    url: '/case/types',
    method: 'get'
  })
}

// 获取案件状态
export function getCaseStatuses() {
  return request({
    url: '/case/statuses',
    method: 'get'
  })
}

// 搜索案件
export function searchCases(params) {
  console.log('调用searchCases API，参数:', params)
  return request({
    url: '/case/search',
    method: 'get',
    params
  })
}

// 获取案件详情
export function getCaseDetail(id) {
  return request({
    url: `/case/${id}`,
    method: 'get'
  })
}

// 添加案件
export function addCase(data) {
  return request({
    url: '/case/add',
    method: 'post',
    data
  })
}

// 更新案件
export function updateCase(data) {
  return request({
    url: `/case/${data.id}`,
    method: 'put',
    data
  })
}

// 删除案件
export function deleteCase(id) {
  return request({
    url: `/case/${id}`,
    method: 'delete'
  })
}

// 获取案件下的笔录列表
export function getCaseRecords(id) {
  return request({
    url: `/case/${id}/records`,
    method: 'get'
  })
}

// 打包案件笔录
export function packageCase(id) {
  return request({
    url: `/case/package/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}

// 打包选定笔录
export function packageSelectedRecords(id, recordIds) {
  return request({
    url: `/case/${id}/package-selected`,
    method: 'post',
    data: recordIds,
    responseType: 'blob'
  });
}

// 获取案件统计数据
export function getCaseStatistics() {
  return request({
    url: '/case/statistics',
    method: 'get'
  });
}