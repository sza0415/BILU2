<template>
  <div class="container mt-4">
    <!-- 页面标题区域 -->
    <div class="row mb-4">
      <div class="col-12 text-center">
        <h2 class="display-5 fw-bold text-primary">案件笔录搜索</h2>
        <p class="text-muted">快速查找和浏览所有案件笔录</p>
      </div>
    </div>
    
    <!-- 搜索表单 -->
    <div class="row justify-content-center mb-5">
      <div class="col-md-8">
        <form @submit.prevent="searchRecords" class="search-form">
          <div class="input-group input-group-lg shadow-sm mb-3">
            <input 
              type="text" 
              v-model="searchParams.q" 
              class="form-control border-0" 
              placeholder="搜索笔录名称、地点、询问员、被询问人或内容..."
            >
            <button class="btn btn-primary px-4" type="submit">
              <i class="bi bi-search me-2"></i>搜索
            </button>
          </div>
          
          <!-- 高级搜索选项切换按钮 -->
          <div class="text-center mb-3">
            <button 
              type="button" 
              class="btn btn-sm btn-outline-secondary" 
              @click="toggleAdvancedSearch"
            >
              <i class="bi" :class="advancedSearchVisible ? 'bi-chevron-up' : 'bi-sliders'"></i>
              {{ advancedSearchVisible ? '收起高级检索' : '高级检索选项' }}
            </button>
          </div>
          
          <!-- 高级搜索选项 -->
          <div 
            class="card p-3 shadow-sm mb-3" 
            v-show="advancedSearchVisible"
          >
            <div class="row g-3">
              <!-- 时间范围 -->
              <div class="col-md-6">
                <label class="form-label"><i class="bi bi-calendar-range text-primary me-1"></i>询问时间范围</label>
                <div class="input-group input-group-sm">
                  <span class="input-group-text">从</span>
                  <input 
                    type="date" 
                    v-model="searchParams.start_date" 
                    class="form-control"
                  >
                  <span class="input-group-text">至</span>
                  <input 
                    type="date" 
                    v-model="searchParams.end_date" 
                    class="form-control"
                  >
                </div>
              </div>
              
              <!-- 询问地点 -->
              <div class="col-md-6">
                <label class="form-label"><i class="bi bi-geo-alt text-danger me-1"></i>询问地点</label>
                <input 
                  type="text" 
                  v-model="searchParams.location" 
                  class="form-control form-control-sm" 
                  placeholder="输入地点关键词"
                >
              </div>
              
              <!-- 询问员 -->
              <div class="col-md-6">
                <label class="form-label"><i class="bi bi-person text-primary me-1"></i>询问员</label>
                <input 
                  type="text" 
                  v-model="searchParams.assistant" 
                  class="form-control form-control-sm" 
                  placeholder="输入询问员姓名"
                >
              </div>
              
              <!-- 被询问人 -->
              <div class="col-md-6">
                <label class="form-label"><i class="bi bi-person-badge text-success me-1"></i>被询问人</label>
                <input 
                  type="text" 
                  v-model="searchParams.inquirer" 
                  class="form-control form-control-sm" 
                  placeholder="输入被询问人姓名"
                >
              </div>
            </div>
          </div>
        </form>
      </div>
    </div>
    
    <!-- 搜索结果 -->
    <div v-if="results.length > 0">
      <div class="results-header d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold"><i class="bi bi-file-earmark-text me-2"></i>搜索结果 
          <span class="badge bg-primary rounded-pill ms-2">{{ results.length }}</span>
        </h3>
        <div class="text-muted">找到 {{ results.length }} 条匹配笔录</div>
      </div>
      
      <div class="row g-4">
        <div 
          v-for="record in results" 
          :key="record.id" 
          class="col-md-6 mb-3 search-result-item"
        >
          <div class="card h-100 border-0 shadow-sm hover-card">
            <div class="card-header bg-gradient d-flex justify-content-between align-items-center">
              <h5 class="card-title mb-0 text-primary" v-html="highlightText(record.recordName)"></h5>
              <span class="badge bg-primary rounded-pill">{{ formatDate(record.inquiryTime) }}</span>
            </div>
            <div class="card-body">
              <div class="mb-3">
                <div class="d-flex mb-2">
                  <div class="icon-box me-2"><i class="bi bi-geo-alt-fill text-danger"></i></div>
                  <div><strong>询问地点:</strong> <span v-html="highlightText(record.inquiryAddress)"></span></div>
                </div>
                <div class="d-flex mb-2">
                  <div class="icon-box me-2"><i class="bi bi-person-fill text-primary"></i></div>
                  <div><strong>询问员:</strong> <span v-html="highlightText(record.assistantName)"></span></div>
                </div>
                <div class="d-flex mb-2">
                  <div class="icon-box me-2"><i class="bi bi-person-badge text-success"></i></div>
                  <div><strong>被询问人:</strong> <span v-html="highlightText(record.inquirerName)"></span></div>
                </div>
                <div class="d-flex mb-2">
                  <div class="icon-box me-2"><i class="bi bi-credit-card-2-front text-warning"></i></div>
                  <div><strong>身份证号:</strong> <span v-html="highlightText(record.inquiryId)"></span></div>
                </div>
              </div>
              <div class="content-preview p-3 bg-light rounded">
                <i class="bi bi-quote me-2 text-secondary"></i>
                <span class="fst-italic" v-html="truncateText(highlightText(record.textContent), 100)"></span>
              </div>
            </div>
            <div class="card-footer bg-white border-0 text-end">
              <button 
                @click="viewRecordDetail(record.id)" 
                class="btn btn-outline-primary btn-sm"
              >
                <i class="bi bi-eye me-1"></i>查看详情
              </button>
              <button 
                @click="editRecord(record.id)" 
                class="btn btn-outline-warning btn-sm ms-1"
              >
                <i class="bi bi-pencil me-1"></i>修改
              </button>
              <button 
                @click="confirmDelete(record.id, record.record_name)" 
                class="btn btn-outline-danger btn-sm ms-1"
              >
                <i class="bi bi-trash me-1"></i>删除
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 相关记录 -->
      <div v-if="relatedRecords.length > 0">
        <div class="related-header mt-5 mb-4">
          <h3 class="fw-bold"><i class="bi bi-link-45deg me-2"></i>相关笔录</h3>
          <div class="border-bottom border-2 border-primary" style="width: 50px;"></div>
        </div>
        <div class="row g-4">
          <div 
            v-for="record in relatedRecords" 
            :key="record.id" 
            class="col-md-4 mb-3 related-item"
          >
            <div class="card h-100 border-0 shadow-sm hover-card">
              <div class="card-header bg-light">
                <h5 class="card-title text-secondary">{{ highlightText(record.record_name) }}</h5>
              </div>
              <div class="card-body">
                <div class="d-flex align-items-center mb-2">
                  <i class="bi bi-person-fill text-primary me-2"></i>
                  <div><strong>询问员:</strong> {{ highlightText(record.assistant_name) }}</div>
                </div>
                <div class="d-flex align-items-center mb-2">
                  <i class="bi bi-person-badge text-success me-2"></i>
                  <div><strong>被询问人:</strong> {{ highlightText(record.inquirer_name) }}</div>
                </div>
                <div class="d-flex align-items-center">
                  <i class="bi bi-calendar-date text-info me-2"></i>
                  <div><strong>时间:</strong> {{ formatDate(record.inquiry_time) }}</div>
                </div>
              </div>
              <div class="card-footer bg-white border-0 text-end">
                <button 
                  @click="viewRecordDetail(record.id)" 
                  class="btn btn-outline-secondary btn-sm"
                >
                  <i class="bi bi-eye me-1"></i>查看详情
                </button>
                <button 
                  @click="editRecord(record.id)" 
                  class="btn btn-outline-warning btn-sm ms-1"
                >
                  <i class="bi bi-pencil me-1"></i>修改
                </button>
                <button 
                  @click="confirmDelete(record.id, record.recordName)" 
                  class="btn btn-outline-danger btn-sm ms-1"
                >
                  <i class="bi bi-trash me-1"></i>删除
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="searchSubmitted">
      <div class="alert alert-info shadow-sm border-0 d-flex align-items-center">
        <i class="bi bi-info-circle-fill me-3 fs-4"></i>
        <div>没有找到与 "<strong>{{ searchParams.q }}</strong>" 相关的笔录。</div>
      </div>
    </div>
    <div v-else>
      <div class="empty-state text-center py-3">
        <i class="bi bi-search fs-1 text-secondary mb-2 d-block"></i>
        <h5 class="text-secondary">请输入搜索关键词</h5>
        <p class="text-muted small">输入关键词以查找相关案件笔录</p>
      </div>
      
      <!-- 最近笔录 - 时间线形式 -->
      <div v-if="recentRecords.length > 0" class="recent-records mt-5">
        <div class="recent-header mb-4">
          <h3 class="fw-bold"><i class="bi bi-clock-history me-2"></i>最近笔录</h3>
          <div class="border-bottom border-2 border-primary" style="width: 50px;"></div>
        </div>
        <div class="timeline-container">
          <div 
            v-for="record in recentRecords" 
            :key="record.id" 
            class="timeline-item recent-item"
          >
            <div class="timeline-dot">
              <i class="bi bi-file-earmark-text"></i>
            </div>
            <div class="timeline-date">
              <span>{{ formatDate(record.inquiryTime) }}</span>
              <span class="timeline-time">{{ formatTime(record.inquiryTime) }}</span>
            </div>
            <div class="timeline-content">
              <div class="card border-0 shadow-sm hover-card">
                <div class="card-header bg-light d-flex justify-content-between align-items-center">
                  <h5 class="card-title text-primary mb-0">{{ record.recordName }}</h5>
                  <span class="badge bg-primary rounded-pill">{{ record.caseName }}</span>
                </div>
                <div class="card-body">
                  <div class="row">
                    <div class="col-md-6">
                      <div class="d-flex align-items-center mb-2">
                        <i class="bi bi-person-fill text-primary me-2"></i>
                        <div><strong>询问员:</strong> {{ record.assistantName }}</div>
                      </div>
                      <div class="d-flex align-items-center mb-2">
                        <i class="bi bi-person-badge text-success me-2"></i>
                        <div><strong>被询问人:</strong> {{ record.inquirerName }}</div>
                      </div>
                    </div>
                    <div class="col-md-6">
                      <div class="d-flex align-items-center mb-2">
                        <i class="bi bi-geo-alt-fill text-danger me-2"></i>
                        <div><strong>询问地点:</strong> {{ record.inquiryAddress }}</div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="card-footer bg-white border-0 text-end">
                  <button 
                    @click="viewRecordDetail(record.id)" 
                    class="btn btn-outline-primary btn-sm"
                  >
                    <i class="bi bi-eye me-1"></i>查看详情
                  </button>
                  <button 
                    @click="editRecord(record.id)" 
                    class="btn btn-outline-warning btn-sm ms-1"
                  >
                    <i class="bi bi-pencil me-1"></i>修改
                  </button>
                  <button 
                    @click="confirmDelete(record.id, record.recordName)" 
                    class="btn btn-outline-danger btn-sm ms-1"
                  >
                    <i class="bi bi-trash me-1"></i>删除
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 删除确认模态框 -->
    <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title" id="deleteConfirmModalLabel">确认删除</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>您确定要删除笔录 <strong>{{ recordToDelete.name }}</strong> 吗？</p>
            <p class="text-danger"><i class="bi bi-exclamation-triangle-fill me-2"></i>此操作不可逆，删除后数据将无法恢复！</p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
            <button type="button" class="btn btn-danger" @click="deleteRecord">
              <i class="bi bi-trash me-1"></i>确认删除
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { searchRecords, getRecordList, deleteRecord } from '@/api/record'
import { Modal } from 'bootstrap'

export default {
  name: 'RecordSearch',
  data() {
    return {
      searchParams: {
        q: '',
        start_date: '',
        end_date: '',
        location: '',
        assistant: '',
        inquirer: ''
      },
      results: [],
      relatedRecords: [],
      recentRecords: [],
      advancedSearchVisible: false,
      searchSubmitted: false,
      recordToDelete: {
        id: null,
        name: ''
      },
      deleteModal: null
    }
  },
  mounted() {
    // 初始化删除确认模态框
    this.deleteModal = new Modal(document.getElementById('deleteConfirmModal'))
    
    // 加载最近笔录
    this.loadRecentRecords()
    
    // 检查URL参数，如果有搜索参数，则自动展开高级搜索选项
    const query = this.$route.query
    if (query.q || query.start_date || query.end_date || 
        query.location || query.assistant || query.inquirer) {
      this.searchParams = {
        q: query.q || '',
        start_date: query.start_date || '',
        end_date: query.end_date || '',
        location: query.location || '',
        assistant: query.assistant || '',
        inquirer: query.inquirer || ''
      }
      this.advancedSearchVisible = !!(query.start_date || query.end_date || 
                                     query.location || query.assistant || 
                                     query.inquirer)
      this.searchRecords()
    }
  },
  methods: {
    // 切换高级搜索选项的显示/隐藏
    toggleAdvancedSearch() {
      this.advancedSearchVisible = !this.advancedSearchVisible
    },
    
    // 搜索笔录
    async searchRecords() {
      this.searchSubmitted = true
      try {
        console.log('搜索参数:', this.searchParams)
        const response = await searchRecords(this.searchParams)
        console.log('搜索结果:', response)
        
        // 后端返回的数据结构是 {code, message, data}，其中data是PageResult对象
        // PageResult对象包含 {total, pages, current, size, records, extra}
        if (response.data && response.data.code === 200 && response.data.data) {
          console.log('搜索结果records:', response.data.data.records)
          this.results = response.data.data.records || []
          this.relatedRecords = [] // 相关记录暂时为空，可以根据需要从后端获取
          console.log('设置结果后this.results:', this.results)
        } else {
          console.error('搜索笔录返回数据格式不正确:', response.data)
          this.results = []
          this.relatedRecords = []
        }
        
        // 更新URL参数
        this.updateUrlParams()
      } catch (error) {
        console.error('搜索笔录失败:', error)
        this.results = []
        this.relatedRecords = []
      }
    },
    
    // 加载最近笔录
    async loadRecentRecords() {
      try {
        const response = await getRecordList()
        // 后端返回的数据结构是 {code, message, data}，其中data是CaseRecord数组
        if (response.data && response.data.code === 200 && Array.isArray(response.data.data)) {
          this.recentRecords = response.data.data.slice(0, 5) // 只显示最近5条
        } else {
          console.error('加载最近笔录返回数据格式不正确:', response.data)
          this.recentRecords = []
        }
      } catch (error) {
        console.error('加载最近笔录失败:', error)
        this.recentRecords = []
      }
    },
    
    // 查看笔录详情
    viewRecordDetail(id) {
      console.log('Navigating to record detail with id:', id, typeof id)
      this.$router.push(`/record/${id}`)
    },
    
    // 编辑笔录
    editRecord(id) {
      this.$router.push(`/record/edit/${id}`)
    },
    
    // 确认删除笔录
    confirmDelete(id, name) {
      this.recordToDelete = {
        id,
        name
      }
      this.deleteModal.show()
    },
    
    // 删除笔录
    async deleteRecord() {
      try {
        await deleteRecord(this.recordToDelete.id)
        // 从结果列表中移除已删除的笔录
        this.results = this.results.filter(record => record.id !== this.recordToDelete.id)
        this.relatedRecords = this.relatedRecords.filter(record => record.id !== this.recordToDelete.id)
        this.recentRecords = this.recentRecords.filter(record => record.id !== this.recordToDelete.id)
        
        // 关闭模态框
        this.deleteModal.hide()
        
        // 显示成功消息
        this.$bvToast.toast('笔录删除成功', {
          title: '操作成功',
          variant: 'success',
          solid: true
        })
      } catch (error) {
        console.error('删除笔录失败:', error)
        this.$bvToast.toast(`删除失败: ${error.message || '未知错误'}`, {
          title: '操作失败',
          variant: 'danger',
          solid: true
        })
      }
    },
    
    // 更新URL参数
    updateUrlParams() {
      const query = {}
      if (this.searchParams.q) query.q = this.searchParams.q
      if (this.searchParams.start_date) query.start_date = this.searchParams.start_date
      if (this.searchParams.end_date) query.end_date = this.searchParams.end_date
      if (this.searchParams.location) query.location = this.searchParams.location
      if (this.searchParams.assistant) query.assistant = this.searchParams.assistant
      if (this.searchParams.inquirer) query.inquirer = this.searchParams.inquirer
      
      this.$router.replace({ query })
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' })
    },
    
    // 格式化时间
    formatTime(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
    },
    
    // 截断文本
    truncateText(text, length) {
      if (!text) return ''
      return text.length > length ? text.substring(0, length) + '...' : text
    },
    
    // 高亮搜索关键词
    highlightText(text) {
      if (!text || !this.searchParams.q) return text
      const regex = new RegExp(this.searchParams.q, 'gi')
      return text.replace(regex, match => `<span class="highlight">${match}</span>`)
    }
  }
}
</script>

<style scoped>
/* 搜索表单样式 */
.search-form .form-control:focus {
  box-shadow: none;
  border-color: #0d6efd;
}

/* 卡片悬停效果 */
.hover-card {
  transition: all 0.3s ease;
}

.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0,0,0,0.1) !important;
}

/* 卡片头部渐变背景 */
.card-header.bg-gradient {
  background: linear-gradient(to right, #f8f9fa, #e9ecef);
  border-bottom: none;
}

/* 图标样式 */
.icon-box {
  width: 24px;
  text-align: center;
}

/* 内容预览区域 */
.content-preview {
  background-color: rgba(0,0,0,0.02);
  border-left: 3px solid #0d6efd;
}

/* 搜索结果、相关项目和最近项目的动画效果 */
.search-result-item, .related-item, .recent-item {
  animation: fadeIn 0.5s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 空状态样式 */
.empty-state {
  padding: 3rem;
  background-color: #f8f9fa;
  border-radius: 1rem;
}

/* 时间线样式 */
.timeline-container {
  position: relative;
  padding-left: 30px;
  margin-left: 90px;
}

.timeline-container::before {
  content: '';
  position: absolute;
  left: 15px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: #0d6efd;
}

.timeline-item {
  position: relative;
  margin-bottom: 20px;
}

.timeline-dot {
  position: absolute;
  left: -30px;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #0d6efd;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  box-shadow: 0 0 0 4px rgba(13, 110, 253, 0.2);
  font-size: 0.8rem;
}

.timeline-date {
  position: absolute;
  left: -120px;
  top: 5px;
  width: 85px;
  text-align: right;
  color: #6c757d;
  font-weight: bold;
  display: flex;
  flex-direction: column;
}

.timeline-time {
  font-size: 0.75rem;
  color: #adb5bd;
}

.timeline-content {
  padding-left: 15px;
  padding-bottom: 5px;
}

/* 高亮样式 */
.highlight {
  background-color: rgba(255, 193, 7, 0.3);
  padding: 0 2px;
  border-radius: 2px;
  font-weight: bold;
}

/* 响应式时间线样式 */
@media (max-width: 768px) {
  .timeline-container {
    margin-left: 50px;
    padding-left: 25px;
  }
  
  .timeline-container::before {
    left: 40px;
  }
  
  .timeline-dot {
    width: 25px;
    height: 25px;
    left: -25px;
    font-size: 0.7rem;
  }
  
  .timeline-date {
    left: -85px;
    width: 75px;
    font-size: 0.85rem;
  }
  
  .timeline-time {
    font-size: 0.7rem;
  }
  
  .timeline-content {
    padding-left: 10px;
  }
}
</style>