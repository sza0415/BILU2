<template>
  <div class="container-fluid mt-4">
    <!-- 页面标题区域 -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h2 class="display-6 fw-bold text-primary mb-0">
              <i class="bi bi-pencil-square me-2"></i>笔录管理
            </h2>
            <p class="text-muted mb-0">创建、编辑和管理笔录记录</p>
          </div>
          <div class="btn-group">
            <button class="btn btn-primary btn-lg" @click="showCreateModal = true">
              <i class="bi bi-plus-circle me-2"></i>新建笔录
            </button>
            <button class="btn btn-success btn-lg" @click="showBatchModal = true">
              <i class="bi bi-files me-2"></i>批量操作
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card border-0 shadow-sm">
          <div class="card-body">
            <div class="row g-3">
              <div class="col-md-4">
                <label class="form-label fw-bold">搜索关键词</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="bi bi-search"></i>
                  </span>
                  <input 
                    type="text" 
                    class="form-control" 
                    placeholder="搜索笔录内容、案件编号等"
                    v-model="searchForm.keyword"
                  />
                </div>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">状态筛选</label>
                <select class="form-select" v-model="searchForm.status">
                  <option value="">全部状态</option>
                  <option value="draft">草稿</option>
                  <option value="completed">已完成</option>
                  <option value="verified">已审核</option>
                </select>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">日期范围</label>
                <input 
                  type="date" 
                  class="form-control" 
                  v-model="searchForm.dateFrom"
                />
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">&nbsp;</label>
                <input 
                  type="date" 
                  class="form-control" 
                  v-model="searchForm.dateTo"
                />
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">&nbsp;</label>
                <button class="btn btn-primary w-100" @click="searchRecords">
                  <i class="bi bi-search me-2"></i>搜索
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 笔录列表 -->
    <div class="row">
      <div class="col-12">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white border-0 py-3">
            <div class="d-flex justify-content-between align-items-center">
              <h5 class="fw-bold mb-0">
                <i class="bi bi-list-ul me-2 text-primary"></i>笔录列表
                <span class="badge bg-primary ms-2">{{ filteredRecords.length }}</span>
              </h5>
              <div class="btn-group btn-group-sm">
                <button 
                  class="btn" 
                  :class="viewMode === 'table' ? 'btn-primary' : 'btn-outline-primary'"
                  @click="viewMode = 'table'"
                >
                  <i class="bi bi-table"></i>
                </button>
                <button 
                  class="btn" 
                  :class="viewMode === 'card' ? 'btn-primary' : 'btn-outline-primary'"
                  @click="viewMode = 'card'"
                >
                  <i class="bi bi-grid-3x3-gap"></i>
                </button>
              </div>
            </div>
          </div>
          
          <div class="card-body p-0">
            <!-- 表格视图 -->
            <div v-if="viewMode === 'table'" class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="bg-light">
                  <tr>
                    <th width="5%">
                      <input type="checkbox" class="form-check-input" @change="toggleSelectAll">
                    </th>
                    <th width="15%">案件编号</th>
                    <th width="20%">标题</th>
                    <th width="15%">记录人</th>
                    <th width="15%">创建时间</th>
                    <th width="10%">状态</th>
                    <th width="20%">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="record in paginatedRecords" :key="record.id" class="record-row">
                    <td>
                      <input 
                        type="checkbox" 
                        class="form-check-input" 
                        :value="record.id"
                        v-model="selectedRecords"
                      >
                    </td>
                    <td>
                      <span class="fw-bold text-primary">{{ record.caseNumber }}</span>
                    </td>
                    <td>
                      <div class="record-title">
                        {{ record.title }}
                        <small class="text-muted d-block">{{ record.summary }}</small>
                      </div>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        <i class="bi bi-person-circle me-2 text-muted"></i>
                        {{ record.recorder }}
                      </div>
                    </td>
                    <td>
                      <small class="text-muted">{{ formatDate(record.createdAt) }}</small>
                    </td>
                    <td>
                      <span class="badge" :class="getStatusClass(record.status)">
                        {{ getStatusText(record.status) }}
                      </span>
                    </td>
                    <td>
                      <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" @click="viewRecord(record)">
                          <i class="bi bi-eye"></i>
                        </button>
                        <button class="btn btn-outline-success" @click="editRecord(record)">
                          <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn btn-outline-danger" @click="deleteRecord(record.id)">
                          <i class="bi bi-trash"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="filteredRecords.length === 0">
                    <td colspan="7" class="text-center py-5">
                      <div class="empty-state">
                        <i class="bi bi-inbox fa-3x mb-3 text-secondary opacity-50"></i>
                        <h5 class="text-secondary">暂无笔录记录</h5>
                        <p class="text-muted">点击"新建笔录"按钮创建第一条记录</p>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- 卡片视图 -->
            <div v-else class="row g-3 p-3">
              <div v-for="record in paginatedRecords" :key="record.id" class="col-md-6 col-lg-4">
                <div class="card h-100 record-card">
                  <div class="card-header d-flex justify-content-between align-items-center">
                    <small class="text-muted">{{ record.caseNumber }}</small>
                    <span class="badge" :class="getStatusClass(record.status)">
                      {{ getStatusText(record.status) }}
                    </span>
                  </div>
                  <div class="card-body">
                    <h6 class="card-title">{{ record.title }}</h6>
                    <p class="card-text text-muted small">{{ record.summary }}</p>
                    <div class="d-flex justify-content-between align-items-center">
                      <small class="text-muted">
                        <i class="bi bi-person me-1"></i>{{ record.recorder }}
                      </small>
                      <small class="text-muted">{{ formatDate(record.createdAt) }}</small>
                    </div>
                  </div>
                  <div class="card-footer bg-transparent">
                    <div class="btn-group w-100">
                      <button class="btn btn-outline-primary btn-sm" @click="viewRecord(record)">
                        <i class="bi bi-eye me-1"></i>查看
                      </button>
                      <button class="btn btn-outline-success btn-sm" @click="editRecord(record)">
                        <i class="bi bi-pencil me-1"></i>编辑
                      </button>
                      <button class="btn btn-outline-danger btn-sm" @click="deleteRecord(record.id)">
                        <i class="bi bi-trash me-1"></i>删除
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-if="filteredRecords.length === 0" class="col-12">
                <div class="text-center py-5">
                  <div class="empty-state">
                    <i class="bi bi-inbox fa-3x mb-3 text-secondary opacity-50"></i>
                    <h5 class="text-secondary">暂无笔录记录</h5>
                    <p class="text-muted">点击"新建笔录"按钮创建第一条记录</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div v-if="filteredRecords.length > 0" class="card-footer bg-white">
            <div class="d-flex justify-content-between align-items-center">
              <div class="text-muted">
                显示 {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, filteredRecords.length) }} 条，共 {{ filteredRecords.length }} 条记录
              </div>
              <nav>
                <ul class="pagination pagination-sm mb-0">
                  <li class="page-item" :class="{ disabled: currentPage === 1 }">
                    <button class="page-link" @click="currentPage = 1" :disabled="currentPage === 1">
                      <i class="bi bi-chevron-double-left"></i>
                    </button>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage === 1 }">
                    <button class="page-link" @click="currentPage--" :disabled="currentPage === 1">
                      <i class="bi bi-chevron-left"></i>
                    </button>
                  </li>
                  <li 
                    v-for="page in visiblePages" 
                    :key="page" 
                    class="page-item" 
                    :class="{ active: page === currentPage }"
                  >
                    <button class="page-link" @click="currentPage = page">{{ page }}</button>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                    <button class="page-link" @click="currentPage++" :disabled="currentPage === totalPages">
                      <i class="bi bi-chevron-right"></i>
                    </button>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                    <button class="page-link" @click="currentPage = totalPages" :disabled="currentPage === totalPages">
                      <i class="bi bi-chevron-double-right"></i>
                    </button>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新建笔录模态框 -->
    <div class="modal fade" :class="{ show: showCreateModal }" :style="{ display: showCreateModal ? 'block' : 'none' }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-plus-circle me-2"></i>新建笔录
            </h5>
            <button type="button" class="btn-close" @click="showCreateModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="createRecord">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label">案件编号</label>
                  <input type="text" class="form-control" v-model="recordForm.caseNumber" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">笔录标题</label>
                  <input type="text" class="form-control" v-model="recordForm.title" required>
                </div>
                <div class="col-12">
                  <label class="form-label">笔录内容</label>
                  <textarea class="form-control" rows="8" v-model="recordForm.content" required></textarea>
                </div>
                <div class="col-md-6">
                  <label class="form-label">记录人</label>
                  <input type="text" class="form-control" v-model="recordForm.recorder" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">状态</label>
                  <select class="form-select" v-model="recordForm.status">
                    <option value="draft">草稿</option>
                    <option value="completed">已完成</option>
                  </select>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showCreateModal = false">取消</button>
            <button type="button" class="btn btn-primary" @click="createRecord">
              <i class="bi bi-check-circle me-2"></i>保存
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 模态框背景 -->
    <div v-if="showCreateModal || showBatchModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script>
export default {
  name: 'TaskList',
  data() {
    return {
      viewMode: 'table', // 'table' 或 'card'
      currentPage: 1,
      pageSize: 10,
      showCreateModal: false,
      showBatchModal: false,
      selectedRecords: [],
      searchForm: {
        keyword: '',
        status: '',
        dateFrom: '',
        dateTo: ''
      },
      recordForm: {
        caseNumber: '',
        title: '',
        content: '',
        recorder: '',
        status: 'draft'
      },
      records: [
        {
          id: 1,
          caseNumber: 'CASE-2024-001',
          title: '张某涉嫌盗窃案询问笔录',
          summary: '对犯罪嫌疑人张某进行询问，了解案件基本情况...',
          content: '详细的笔录内容...',
          recorder: '李警官',
          createdAt: '2024-01-15 14:30:00',
          status: 'completed'
        },
        {
          id: 2,
          caseNumber: 'CASE-2024-002',
          title: '王某交通肇事案询问笔录',
          summary: '对当事人王某进行询问，了解交通事故经过...',
          content: '详细的笔录内容...',
          recorder: '张警官',
          createdAt: '2024-01-16 09:15:00',
          status: 'verified'
        },
        {
          id: 3,
          caseNumber: 'CASE-2024-003',
          title: '刘某诈骗案询问笔录',
          summary: '对犯罪嫌疑人刘某进行询问...',
          content: '详细的笔录内容...',
          recorder: '王警官',
          createdAt: '2024-01-17 16:45:00',
          status: 'draft'
        }
      ]
    }
  },
  computed: {
    filteredRecords() {
      let filtered = this.records;
      
      // 关键词搜索
      if (this.searchForm.keyword) {
        const keyword = this.searchForm.keyword.toLowerCase();
        filtered = filtered.filter(record => 
          record.title.toLowerCase().includes(keyword) ||
          record.caseNumber.toLowerCase().includes(keyword) ||
          record.content.toLowerCase().includes(keyword)
        );
      }
      
      // 状态筛选
      if (this.searchForm.status) {
        filtered = filtered.filter(record => record.status === this.searchForm.status);
      }
      
      // 日期筛选
      if (this.searchForm.dateFrom) {
        filtered = filtered.filter(record => record.createdAt >= this.searchForm.dateFrom);
      }
      
      if (this.searchForm.dateTo) {
        filtered = filtered.filter(record => record.createdAt <= this.searchForm.dateTo);
      }
      
      return filtered;
    },
    totalPages() {
      return Math.ceil(this.filteredRecords.length / this.pageSize);
    },
    paginatedRecords() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredRecords.slice(start, end);
    },
    visiblePages() {
      const pages = [];
      const total = this.totalPages;
      const current = this.currentPage;
      
      if (total <= 7) {
        for (let i = 1; i <= total; i++) {
          pages.push(i);
        }
      } else {
        if (current <= 4) {
          for (let i = 1; i <= 5; i++) {
            pages.push(i);
          }
          pages.push('...');
          pages.push(total);
        } else if (current >= total - 3) {
          pages.push(1);
          pages.push('...');
          for (let i = total - 4; i <= total; i++) {
            pages.push(i);
          }
        } else {
          pages.push(1);
          pages.push('...');
          for (let i = current - 1; i <= current + 1; i++) {
            pages.push(i);
          }
          pages.push('...');
          pages.push(total);
        }
      }
      
      return pages;
    }
  },
  methods: {
    searchRecords() {
      this.currentPage = 1;
      console.log('搜索笔录:', this.searchForm);
    },
    createRecord() {
      if (!this.recordForm.caseNumber || !this.recordForm.title || !this.recordForm.content) {
        alert('请填写完整信息');
        return;
      }
      
      const newRecord = {
        id: Date.now(),
        ...this.recordForm,
        summary: this.recordForm.content.substring(0, 50) + '...',
        createdAt: new Date().toISOString().slice(0, 19).replace('T', ' ')
      };
      
      this.records.unshift(newRecord);
      this.showCreateModal = false;
      this.recordForm = {
        caseNumber: '',
        title: '',
        content: '',
        recorder: '',
        status: 'draft'
      };
    },
    viewRecord(record) {
      console.log('查看笔录:', record);
      // 这里可以跳转到详情页面或打开详情模态框
    },
    editRecord(record) {
      console.log('编辑笔录:', record);
      // 这里可以跳转到编辑页面或打开编辑模态框
    },
    deleteRecord(id) {
      if (confirm('确定要删除这条笔录吗？')) {
        const index = this.records.findIndex(r => r.id === id);
        if (index > -1) {
          this.records.splice(index, 1);
        }
      }
    },
    toggleSelectAll(event) {
      if (event.target.checked) {
        this.selectedRecords = this.paginatedRecords.map(r => r.id);
      } else {
        this.selectedRecords = [];
      }
    },
    getStatusClass(status) {
      const classes = {
        draft: 'bg-warning',
        completed: 'bg-success',
        verified: 'bg-primary'
      };
      return classes[status] || 'bg-secondary';
    },
    getStatusText(status) {
      const texts = {
        draft: '草稿',
        completed: '已完成',
        verified: '已审核'
      };
      return texts[status] || '未知';
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleString('zh-CN');
    }
  },
  mounted() {
    console.log('笔录管理页面已加载');
  }
}
</script>

<style scoped>
.record-row {
  transition: all 0.3s ease;
}

.record-row:hover {
  background-color: rgba(13, 110, 253, 0.05);
}

.record-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.record-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.record-title {
  font-weight: 500;
}

.empty-state {
  padding: 2rem;
}

.modal {
  background-color: rgba(0, 0, 0, 0.5);
}

.btn {
  transition: all 0.3s ease;
}

.btn:hover {
  transform: translateY(-1px);
}

.page-link {
  transition: all 0.3s ease;
}

.pagination .page-item.active .page-link {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.table th {
  font-weight: 600;
  border-bottom: 2px solid #dee2e6;
  background-color: #f8f9fa;
}

.badge {
  font-size: 0.75rem;
}

.input-group-text {
  background-color: #f8f9fa;
  border-color: #dee2e6;
}

.form-control:focus, .form-select:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}
</style>