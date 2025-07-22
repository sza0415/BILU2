<template>
  <div class="modal fade" id="caseDetailModal" tabindex="-1" aria-labelledby="caseDetailModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered modal-lg">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="caseDetailModalLabel">案件详情</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div v-if="loading" class="text-center py-5">
            <div class="spinner-border text-primary" role="status">
              <span class="visually-hidden">加载中...</span>
            </div>
            <p class="mt-3 text-muted">正在加载案件详情...</p>
          </div>
          <div v-else-if="!caseDetail" class="text-center py-5">
            <div class="alert alert-warning">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>
              <span>未找到案件详情</span>
            </div>
          </div>
          <div v-else>
            <div class="case-header mb-4">
              <div class="d-flex align-items-center">
                <div class="case-icon rounded-circle p-2 me-3" 
                     :class="{
                       'bg-success bg-opacity-10': caseDetail.caseType === '民事案件',
                       'bg-danger bg-opacity-10': caseDetail.caseType === '刑事案件',
                       'bg-info bg-opacity-10': caseDetail.caseType === '行政案件',
                       'bg-primary bg-opacity-10': !['民事案件', '刑事案件', '行政案件'].includes(caseDetail.caseType)
                     }">
                  <i class="bi fs-4" 
                     :class="{
                       'bi-people text-success': caseDetail.caseType === '民事案件',
                       'bi-shield-lock text-danger': caseDetail.caseType === '刑事案件',
                       'bi-building text-info': caseDetail.caseType === '行政案件',
                       'bi-folder text-primary': !['民事案件', '刑事案件', '行政案件'].includes(caseDetail.caseType)
                     }"></i>
                </div>
                <div>
                  <h4 class="mb-1">{{ caseDetail.caseName }}</h4>
                  <div class="d-flex align-items-center">
                    <span class="badge rounded-pill px-2 py-1 me-2" :class="{
                      'bg-success': caseDetail.status === '已完成' || caseDetail.status === '已结案',
                      'bg-warning text-dark': caseDetail.status === '进行中' || caseDetail.status === '调查中' || caseDetail.status === '审理中',
                      'bg-secondary': !['已完成', '已结案', '进行中', '调查中', '审理中'].includes(caseDetail.status)
                    }">{{ caseDetail.status }}</span>
                    <span class="text-muted small">{{ caseDetail.caseNumber }}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="row mb-4">
              <div class="col-md-6">
                <div class="card border-0 shadow-sm h-100">
                  <div class="card-body">
                    <h6 class="card-title mb-3">基本信息</h6>
                    <div class="detail-item">
                      <span class="detail-label">案件类型</span>
                      <span class="detail-value">{{ caseDetail.caseType }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="detail-label">立案日期</span>
                      <span class="detail-value">{{ formatDate(caseDetail.startDate) }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="detail-label">结案日期</span>
                      <span class="detail-value">{{ formatDate(caseDetail.endDate) || '未结案' }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="detail-label">创建时间</span>
                      <span class="detail-value">{{ formatDateTime(caseDetail.createdAt) }}</span>
                    </div>

                  </div>
                </div>
              </div>
              <div class="col-md-6">
                <div class="card border-0 shadow-sm h-100">
                  <div class="card-body">
                    <h6 class="card-title mb-3">案件描述</h6>
                    <p class="card-text">{{ caseDetail.description || '暂无描述' }}</p>
                  </div>
                </div>
              </div>
            </div>
            
            <div class="card border-0 shadow-sm mb-3">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-3">
                  <h6 class="card-title mb-0">笔录列表</h6>
                  <span class="badge bg-primary rounded-pill">{{ caseDetail.records ? caseDetail.records.length : 0 }}</span>
                </div>
                <div v-if="!caseDetail.records || caseDetail.records.length === 0" class="text-center py-3">
                  <p class="text-muted mb-0">暂无笔录记录</p>
                </div>
                <div v-else class="table-responsive">
                  <table class="table table-hover">
                    <thead class="table-light">
                      <tr>
                        <th>笔录名称</th>
                        <th>创建时间</th>
                        <th>状态</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(record, index) in caseDetail.records" :key="index">
                        <td>{{ record.recordName }}</td>
                        <td>{{ formatDateTime(record.createdAt || record.createTime || record.inquiryTime) }}</td>
                        <td>
                          <span class="badge rounded-pill px-2 py-1" :class="{
                            'bg-success': record.status === '已完成',
                            'bg-warning text-dark': record.status === '进行中',
                            'bg-secondary': record.status !== '已完成' && record.status !== '进行中'
                          }">{{ record.status }}</span>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">关闭</button>
          <button v-if="caseDetail" type="button" class="btn btn-primary" @click="editCase">编辑案件</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { Modal } from 'bootstrap'
import { getCaseDetail } from '@/api/case'

export default {
  name: 'CaseDetailModal',
  emits: ['edit'],
  setup(props, { emit }) {
    // 案件详情数据
    const caseDetail = ref(null)
    const loading = ref(false)
    
    // 模态框实例
    let modalInstance = null
    
    // 初始化模态框
    const initModal = () => {
      const modalEl = document.getElementById('caseDetailModal')
      if (modalEl) {
        // 销毁旧实例
        if (modalInstance) {
          modalInstance.dispose()
        }
        // 创建新实例
        modalInstance = new Modal(modalEl, {
          backdrop: 'static',
          keyboard: false
        })
      }
    }
    
    // 加载案件详情
    const loadCaseDetail = async (id) => {
      if (!id) return
      
      console.log('开始加载案件详情, ID:', id)
      loading.value = true
      try {
        const response = await getCaseDetail(id)
        console.log('API响应:', response)
        
        if (response.data && response.data.code === 200) {
          console.log('案件详情数据:', response.data.data)
          // 后端返回的数据结构是一个包含 'case' 和 'records' 两个键的 Map
          // 我们需要将 'case' 对象和 'records' 数组合并到 caseDetail 中
          const responseData = response.data.data;
          if (responseData && responseData.case) {
            // 将 case 对象的属性复制到 caseDetail
            caseDetail.value = {
              ...responseData.case,
              // 将 records 数组添加到 caseDetail
              records: responseData.records || []
            }
            console.log('设置后的caseDetail值:', caseDetail.value)
          } else {
            caseDetail.value = null
            console.error('案件详情数据格式不正确')
          }
        } else {
          caseDetail.value = null
          console.error('加载案件详情失败:', response.data ? response.data.msg : '未知错误')
        }
      } catch (error) {
        console.error('加载案件详情异常:', error)
        caseDetail.value = null
      } finally {
        loading.value = false
      }
    }
    
    // 显示模态框
    const showModal = (id) => {
      // 重置数据
      caseDetail.value = null
      
      // 确保模态框实例存在
      if (!modalInstance) {
        initModal()
      }
      
      // 显示模态框
      modalInstance.show()
      
      // 加载案件详情
      loadCaseDetail(id)
    }
    
    // 隐藏模态框
    const hideModal = () => {
      if (modalInstance) {
        modalInstance.hide()
      }
    }
    
    // 编辑案件
    const editCase = () => {
      emit('edit', caseDetail.value)
      hideModal()
    }
    
    // 格式化日期
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toISOString().split('T')[0]
    }
    
    // 格式化日期时间
    const formatDateTime = (dateTimeString) => {
      if (!dateTimeString) return ''
      const date = new Date(dateTimeString)
      return `${date.toISOString().split('T')[0]} ${date.toTimeString().split(' ')[0]}`
    }
    
    // 组件挂载时初始化模态框
    onMounted(() => {
      initModal()
    })
    
    return {
      caseDetail,
      loading,
      showModal,
      hideModal,
      editCase,
      formatDate,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.modal-content {
  border: none;
  border-radius: 0.5rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.modal-header {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 1.25rem 1.5rem;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  border-top: none;
  padding: 0 1.5rem 1.5rem;
}

.case-icon {
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.detail-item {
  display: flex;
  margin-bottom: 0.75rem;
}

.detail-label {
  width: 80px;
  color: #6c757d;
  font-weight: 500;
}

.detail-value {
  flex: 1;
}

.card {
  border-radius: 0.5rem;
  overflow: hidden;
}

.card-title {
  font-weight: 600;
  color: #495057;
}

.table {
  margin-bottom: 0;
}

.table th {
  font-weight: 600;
  padding: 0.75rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.table td {
  padding: 0.75rem;
  vertical-align: middle;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.btn-primary {
  background: linear-gradient(to right, #4a6cf7, #2651f0);
  border: none;
  padding: 0.5rem 1.5rem;
  font-weight: 500;
}

.btn-primary:hover {
  background: linear-gradient(to right, #2651f0, #1a3fd9);
}

.btn-secondary {
  background-color: #f8f9fa;
  border: 1px solid #ced4da;
  color: #495057;
  padding: 0.5rem 1.5rem;
  font-weight: 500;
}

.btn-secondary:hover {
  background-color: #e9ecef;
  border-color: #ced4da;
  color: #212529;
}
</style>