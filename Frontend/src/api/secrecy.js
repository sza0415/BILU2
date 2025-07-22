import request from './request'

/**
 * 保密模板相关API
 */

// 获取所有模板
export function getAllTemplates() {
  return request({
    url: '/module/secrecy/get_templates',
    method: 'get'
  })
}

// 获取模板详情
export function getTemplateDetail(id) {
  return request({
    url: `/module/secrecy/${id}`,
    method: 'get'
  })
}

// 添加模板
export function addTemplate(data) {
  return request({
    url: '/module/secrecy/add',
    method: 'post',
    data
  })
}

// 更新模板
export function updateTemplate(id, data) {
  return request({
    url: `/module/secrecy/${id}`,
    method: 'put',
    data
  })
}

// 删除模板
export function deleteTemplate(id) {
  return request({
    url: `/module/secrecy/${id}`,
    method: 'delete'
  })
}

// 上传模板文件
export function uploadTemplateFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/api/module/secrecy/upload',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}