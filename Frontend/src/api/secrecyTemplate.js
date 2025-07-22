import request from './request'

// 保密模板API接口
const secrecyTemplateApi = {
  // 获取模板列表（分页）
  getTemplates(params = {}) {
    return request.get('/module/secrecy', { params })
  },

  // 获取所有模板（不分页）
  getAllTemplates(templateType = null) {
    const params = templateType ? { templateType } : {}
    return request.get('/module/secrecy/get_templates', { params })
  },

  // 获取所有模板类型
  getAllTemplateTypes() {
    return request.get('/module/secrecy/types')
  },

  // 添加模板
  addTemplate(data) {
    return request.post('/module/secrecy/add', data)
  },

  // 添加带分类的模板
  addTemplateWithCategory(templateType, question, sortOrder = 0) {
    return request.post('/module/secrecy/add_with_category', null, {
      params: { templateType, question, sortOrder }
    })
  },

  // 编辑模板
  editTemplate(id, data) {
    return request.put(`/module/secrecy/${id}/edit`, data)
  },

  // 删除模板
  deleteTemplate(id) {
    return request.delete('/module/secrecy/delete', {
      params: { id }
    })
  },

  // 删除模板类型
  deleteTemplateType(templateType) {
    return request.delete('/module/secrecy/delete_type', {
      params: { templateType }
    })
  },

  // 导入模板
  importTemplate(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/module/secrecy/import', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 导入CSV模板
  importCsvTemplate(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/module/secrecy/import_csv', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },

  // 导出模板
  exportTemplates(templateType = null) {
    const params = templateType ? { templateType } : {}
    return request.get('/module/secrecy/export', {
      params,
      responseType: 'blob'
    })
  }
}

export default secrecyTemplateApi