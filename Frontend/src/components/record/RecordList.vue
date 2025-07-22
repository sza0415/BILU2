<template>
  <div class="record-list">
    <!-- 笔录列表 -->
    <div class="card shadow-sm">
      <div class="card-body p-0">
        <div class="table-responsive">
          <table class="table table-hover mb-0">
            <thead class="table-light">
              <tr>
                <th>笔录名称</th>
                <th>询问时间</th>
                <th>询问地点</th>
                <th>询问员</th>
                <th>被询问人</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <template v-if="records && records.length > 0">
                <tr v-for="record in records" :key="record.id">
                  <td>{{ record.recordName }}</td>
                  <td>{{ formatDate(record.inquiryTime) }}</td>
                  <td>{{ record.inquiryAddress }}</td>
                  <td>{{ record.assistantName }}</td>
                  <td>{{ record.inquirerName }}</td>
                  <td>
                    <button @click="viewDetail(record.id)" class="btn btn-sm btn-outline-primary me-1">
                      <i class="bi bi-eye me-1"></i>查看详情
                    </button>
                    <button @click="editRecord(record.id)" class="btn btn-sm btn-outline-warning me-1">
                      <i class="bi bi-pencil me-1"></i>修改
                    </button>
                    <button @click="confirmDelete(record.id, record.recordName)" class="btn btn-sm btn-outline-danger">
                      <i class="bi bi-trash me-1"></i>删除
                    </button>
                  </td>
                </tr>
              </template>
              <tr v-else>
                <td colspan="6" class="text-center py-4">
                  <div class="alert alert-info mb-0">
                    <i class="bi bi-info-circle me-2"></i>暂无笔录记录
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

    <!-- 分页组件 -->
    <div class="d-flex justify-content-center mt-4" v-if="totalPages > 1">
      <nav>
        <ul class="pagination">
          <li class="page-item" :class="{ disabled: currentPage === 1 }">
            <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)">
              <i class="bi bi-chevron-left"></i>
            </a>
          </li>
          <li v-for="page in displayedPages" :key="page" class="page-item" :class="{ active: currentPage === page }">
            <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
          </li>
          <li class="page-item" :class="{ disabled: currentPage === totalPages }">
            <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)">
              <i class="bi bi-chevron-right"></i>
            </a>
          </li>
        </ul>
      </nav>
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
import { getRecordList, deleteRecord } from '@/api/record'
import { Modal } from 'bootstrap'

export default {
  name: 'RecordList',
  props: {
    // 可以接收外部传入的记录列表，如果没有则自行加载
    recordList: {
      type: Array,
      default: () => []
    },
    // 是否自动加载数据
    autoLoad: {
      type: Boolean,
      default: true
    },
    // 分页相关属性
    pagination: {
      type: Object,
      default: () => ({
        page: 1,
        size: 10,
        total: 0
      })
    }
  },
  data() {
    return {
      records: [],
      loading: false,
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      recordToDelete: {
        id: null,
        name: ''
      },
      deleteModal: null
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.totalItems / this.pageSize)
    },
    displayedPages() {
      // 显示当前页附近的页码
      const pages = []
      const maxPagesToShow = 5
      let startPage = Math.max(1, this.currentPage - Math.floor(maxPagesToShow / 2))
      let endPage = Math.min(this.totalPages, startPage + maxPagesToShow - 1)
      
      if (endPage - startPage + 1 < maxPagesToShow) {
        startPage = Math.max(1, endPage - maxPagesToShow + 1)
      }
      
      for (let i = startPage; i <= endPage; i++) {
        pages.push(i)
      }
      
      return pages
    }
  },
  watch: {
    recordList: {
      handler(newVal) {
        if (newVal && newVal.length > 0) {
          this.records = [...newVal]
        }
      },
      immediate: true
    },
    pagination: {
      handler(newVal) {
        if (newVal) {
          this.currentPage = newVal.page || 1
          this.pageSize = newVal.size || 10
          this.totalItems = newVal.total || 0
        }
      },
      immediate: true,
      deep: true
    }
  },
  mounted() {
    // 初始化删除确认模态框
    this.deleteModal = new Modal(document.getElementById('deleteConfirmModal'))
    
    // 如果没有传入记录列表且需要自动加载，则加载数据
    if (this.autoLoad && (!this.recordList || this.recordList.length === 0)) {
      this.loadRecords()
    }
  },
  methods: {
    // 加载笔录记录
    async loadRecords() {
      this.loading = true
      try {
        const response = await getRecordList()
        if (response.data && response.data.code === 200) {
          if (Array.isArray(response.data.data)) {
            // 直接返回数组
            this.records = response.data.data
            this.totalItems = response.data.data.length
          } else if (response.data.data && response.data.data.records) {
            // 返回分页对象
            this.records = response.data.data.records
            this.totalItems = response.data.data.total
          } else {
            this.records = []
            this.totalItems = 0
          }
        } else {
          console.error('加载笔录列表返回数据格式不正确:', response.data)
          this.records = []
          this.totalItems = 0
        }
      } catch (error) {
        console.error('加载笔录列表失败:', error)
        this.records = []
        this.totalItems = 0
      } finally {
        this.loading = false
      }
    },
    
    // 查看笔录详情
    viewDetail(id) {
      this.$emit('view-detail', id)
      this.$router.push(`/record/${id}`)
    },
    
    // 编辑笔录
    editRecord(id) {
      this.$emit('edit-record', id)
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
        // 从记录列表中移除已删除的笔录
        this.records = this.records.filter(record => record.id !== this.recordToDelete.id)
        this.totalItems--
        
        // 关闭模态框
        this.deleteModal.hide()
        
        // 发出删除成功事件
        this.$emit('record-deleted', this.recordToDelete.id)
        
        // 显示成功消息
        this.$bvToast?.toast('笔录删除成功', {
          title: '操作成功',
          variant: 'success',
          solid: true
        })
      } catch (error) {
        console.error('删除笔录失败:', error)
        this.$bvToast?.toast(`删除失败: ${error.message || '未知错误'}`, {
          title: '操作失败',
          variant: 'danger',
          solid: true
        })
      }
    },
    
    // 切换页码
    changePage(page) {
      if (page < 1 || page > this.totalPages || page === this.currentPage) {
        return
      }
      
      this.currentPage = page
      this.$emit('page-changed', {
        page: this.currentPage,
        size: this.pageSize
      })
      
      // 如果是自动加载模式，则重新加载数据
      if (this.autoLoad) {
        this.loadRecords()
      }
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit' })
    }
  }
}
</script>

<style scoped>
.table th {
  font-weight: 500;
  color: #6c757d;
}

.table td {
  vertical-align: middle;
}

.table-hover tbody tr:hover {
  background-color: rgba(13, 110, 253, 0.05);
}

.pagination .page-link {
  color: #0d6efd;
  border-radius: 0;
  margin: 0 2px;
}

.pagination .page-item.active .page-link {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.pagination .page-item.disabled .page-link {
  color: #6c757d;
}
</style>