import request from './request'

/**
 * 笔录记录相关API
 */

// 获取笔录列表
export function getRecordList() {
  return request({
    url: '/record/list',
    method: 'get'
  })
}

// 获取笔录详情
export function getRecordDetail(id) {
  return request({
    url: `/record/${id}`,
    method: 'get'
  })
}

// 搜索笔录记录
export function searchRecords(params) {
  // 转换参数名称以匹配后端API期望的格式
  const convertedParams = {
    q: params.q,
    page: params.page || 1,
    size: params.size || 10,
    startDate: params.start_date,
    endDate: params.end_date,
    location: params.location,
    assistant: params.assistant,
    inquirer: params.inquirer
  }
  
  // 移除undefined的参数
  Object.keys(convertedParams).forEach(key => {
    if (convertedParams[key] === undefined) {
      delete convertedParams[key]
    }
  })
  
  return request({
    url: '/record/search',
    method: 'get',
    params: convertedParams
  })
}

// 添加笔录
export function addRecord(data) {
  return request({
    url: '/record/add',
    method: 'post',
    data
  })
}

// 更新笔录
export function updateRecord(id, data) {
  return request({
    url: `/record/${id}`,
    method: 'put',
    data
  })
}

// 删除笔录
export function deleteRecord(id) {
  return request({
    url: `/record/${id}`,
    method: 'delete'
  })
}

// 上传笔录附件
export function uploadRecordAttachment(id, file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: `/record/${id}/upload`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 导出笔录
export function exportRecord(id) {
  return request({
    url: `/record/export/${id}`,
    method: 'get',
    responseType: 'blob'
  })
}