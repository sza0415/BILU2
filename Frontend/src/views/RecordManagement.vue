<template>
  <div class="record-management">
    <div class="container-fluid py-3">
      <!-- 页面标题 -->
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="mb-0">
          <i class="bi bi-file-text me-2"></i>笔录管理
        </h2>
        <button class="btn btn-primary" @click="createRecord">
          <i class="bi bi-plus-circle me-1"></i>新建笔录
        </button>
      </div>
      
      <!-- 搜索过滤区域 -->
      <div class="card shadow-sm mb-4">
        <div class="card-body">
          <form @submit.prevent="searchRecords">
            <div class="row g-3">
              <div class="col-md-4">
                <div class="input-group">
                  <span class="input-group-text"><i class="bi bi-search"></i></span>
                  <input
                    type="text"
                    class="form-control"
                    v-model="searchParams.q"
                    placeholder="搜索笔录名称、内容..."
                  >
                </div>
              </div>
              <div class="col-md-3">
                <div class="input-group">
                  <span class="input-group-text"><i class="bi bi-geo-alt"></i></span>
                  <input
                    type="text"
                    class="form-control"
                    v-model="searchParams.location"
                    placeholder="询问地点"
                  >
                </div>
              </div>
              <div class="col-md-3">
                <div class="input-group">
                  <span class="input-group-text"><i class="bi bi-person"></i></span>
                  <input
                    type="text"
                    class="form-control"
                    v-model="searchParams.inquirer"
                    placeholder="被询问人"
                  >
                </div>
              </div>
              <div class="col-md-2">
                <button type="submit" class="btn btn-primary w-100">
                  <i class="bi bi-search me-1"></i>搜索
                </button>
              </div>
            </div>
            
            <!-- 高级搜索选项 -->
            <div class="row g-3 mt-2" v-if="showAdvanced">
              <div class="col-md-4">
                <div class="input-group">
                  <span class="input-group-text"><i class="bi bi-calendar-event"></i></span>
                  <input
                    type="date"
                    class="form-control"
                    v-model="searchParams.start_date"
                    placeholder="开始日期"
                  >
                </div>
              </div>
              <div class="col-md-4">
                <div class="input-group">
                  <span class="input-group-text"><i class="bi bi-calendar-event"></i></span>
                  <input
                    type="date"
                    class="form-control"
                    v-model="searchParams.end_date"
                    placeholder="结束日期"
                  >
                </div>
              </div>
              <div class="col-md-4">
                <div class="input-group">
                  <span class="input-group-text"><i class="bi bi-person-badge"></i></span>
                  <input
                    type="text"
                    class="form-control"
                    v-model="searchParams.assistant"
                    placeholder="询问员"
                  >
                </div>
              </div>
            </div>
            
            <!-- 高级搜索切换 -->
            <div class="mt-2">
              <button type="button" class="btn btn-link p-0" @click="toggleAdvanced">
                <i class="bi" :class="showAdvanced ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
                {{ showAdvanced ? '收起高级搜索' : '展开高级搜索' }}
              </button>
            </div>
          </form>
        </div>
      </div>
      
      <!-- 笔录列表 -->
      <record-list
        :record-list="records"
        :auto-load="false"
        :pagination="pagination"
        @view-detail="viewRecordDetail"
        @edit-record="editRecord"
        @record-deleted="handleRecordDeleted"
        @page-changed="handlePageChanged"
      />
    </div>
  </div>
</template>

<script>
import { searchRecords } from '@/api/record'
import { RecordList } from '@/components/record'

export default {
  name: 'RecordManagement',
  components: {
    RecordList
  },
  data() {
    return {
      records: [],
      loading: false,
      showAdvanced: false,
      searchParams: {
        q: '',
        location: '',
        inquirer: '',
        assistant: '',
        start_date: '',
        end_date: '',
        page: 1,
        size: 10
      },
      pagination: {
        page: 1,
        size: 10,
        total: 0
      }
    }
  },
  mounted() {
    // 从URL参数中恢复搜索条件
    this.restoreSearchParams()
    
    // 加载笔录列表
    this.searchRecords()
  },
  methods: {
    // 切换高级搜索选项
    toggleAdvanced() {
      this.showAdvanced = !this.showAdvanced
    },
    
    // 搜索笔录
    async searchRecords() {
      this.loading = true
      
      try {
        // 更新URL参数
        this.updateUrlParams()
        
        const response = await searchRecords(this.searchParams)
        if (response.data && response.data.code === 200) {
          if (Array.isArray(response.data.data)) {
            // 直接返回数组
            this.records = response.data.data
            this.pagination.total = response.data.data.length
          } else if (response.data.data && response.data.data.records) {
            // 返回分页对象
            this.records = response.data.data.records
            this.pagination.total = response.data.data.total
            this.pagination.page = response.data.data.current || 1
            this.pagination.size = response.data.data.size || 10
          } else {
            this.records = []
            this.pagination.total = 0
          }
        } else {
          console.error('搜索笔录返回数据格式不正确:', response.data)
          this.records = []
          this.pagination.total = 0
        }
      } catch (error) {
        console.error('搜索笔录失败:', error)
        this.records = []
        this.pagination.total = 0
      } finally {
        this.loading = false
      }
    },
    
    // 创建新笔录
    createRecord() {
      this.$router.push('/record/edit')
    },
    
    // 查看笔录详情
    viewRecordDetail(id) {
      this.$router.push(`/record/${id}`)
    },
    
    // 编辑笔录
    editRecord(id) {
      this.$router.push(`/record/edit/${id}`)
    },
    
    // 处理笔录删除事件
    handleRecordDeleted(id) {
      // 从列表中移除已删除的笔录
      this.records = this.records.filter(record => record.id !== id)
      this.pagination.total--
    },
    
    // 处理页码变更
    handlePageChanged(pageInfo) {
      this.searchParams.page = pageInfo.page
      this.searchParams.size = pageInfo.size
      this.searchRecords()
    },
    
    // 更新URL参数
    updateUrlParams() {
      const query = {}
      
      // 只添加有值的参数
      Object.keys(this.searchParams).forEach(key => {
        if (this.searchParams[key]) {
          query[key] = this.searchParams[key]
        }
      })
      
      // 更新路由查询参数，但不触发路由变化
      this.$router.replace({ query })
    },
    
    // 从URL参数中恢复搜索条件
    restoreSearchParams() {
      const query = this.$route.query
      
      // 恢复搜索参数
      Object.keys(this.searchParams).forEach(key => {
        if (query[key]) {
          this.searchParams[key] = query[key]
          
          // 如果有高级搜索参数，展开高级搜索区域
          if (['start_date', 'end_date', 'assistant'].includes(key) && query[key]) {
            this.showAdvanced = true
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.btn-link {
  text-decoration: none;
  color: #0d6efd;
}

.card {
  transition: box-shadow 0.3s ease;
}

.card:hover {
  box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15) !important;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .card-body {
    padding: 1rem;
  }
}
</style>