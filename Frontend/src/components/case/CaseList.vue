<template>
  <div class="case-list-container">
    <div class="card border-0 shadow-sm rounded-3">
      <div class="card-header bg-white py-3 border-0">
        <div class="d-flex align-items-center justify-content-between">
          <div class="d-flex align-items-center">
            <i class="bi bi-folder2-open fs-4 text-primary me-2"></i>
            <h5 class="card-title mb-0 fw-bold">案件列表</h5>
            <span class="badge bg-primary bg-opacity-10 text-primary ms-3">{{ cases.length }}</span>
          </div>
          <div class="d-flex align-items-center">
            <div class="btn-group me-2">
              <button class="btn btn-sm" :class="viewMode === 'card' ? 'btn-primary' : 'btn-outline-primary'" @click="setViewMode('card')" title="卡片视图">
                <i class="bi bi-grid"></i>
              </button>
              <button class="btn btn-sm" :class="viewMode === 'list' ? 'btn-primary' : 'btn-outline-primary'" @click="setViewMode('list')" title="列表视图">
                <i class="bi bi-list"></i>
              </button>
            </div>
            <button class="btn btn-sm btn-primary" @click="addCase">
              <i class="bi bi-plus-lg me-1"></i> 新增案件
            </button>
          </div>
        </div>
      </div>
      <div class="card-body p-4">
        <div v-if="loading" class="text-center py-5">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">加载中...</span>
          </div>
          <p class="mt-3 text-muted">正在加载案件数据...</p>
        </div>
        <div v-else-if="cases.length === 0" class="text-center py-5">
          <div class="empty-state p-4">
            <i class="bi bi-folder2 text-muted" style="font-size: 4rem;"></i>
            <h5 class="mt-4">暂无案件记录</h5>
            <p class="text-muted">您可以点击"新增案件"按钮添加第一个案件</p>
            <button class="btn btn-primary mt-3" @click="addCase">
              <i class="bi bi-plus-lg me-1"></i> 新增案件
            </button>
          </div>
        </div>
        <div v-else>
          <!-- 卡片视图 -->
          <div v-if="viewMode === 'card'" class="row">
            <div v-for="(caseItem, index) in cases" :key="index" class="col-md-6 col-lg-4 mb-3">
              <case-card 
                :case-item="caseItem" 
                @view-detail="viewCaseDetail" 
                @edit="editCase" 
                @delete="confirmDeleteCase"
              />
            </div>
          </div>
          
          <!-- 表格视图 -->
          <div v-else>
            <case-table 
              :cases="cases" 
              @view-detail="viewCaseDetail" 
              @edit="editCase" 
              @delete="confirmDeleteCase"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import CaseCard from './CaseCard.vue'
import CaseTable from './CaseTable.vue'

export default {
  name: 'CaseList',
  components: {
    CaseCard,
    CaseTable
  },
  props: {
    cases: {
      type: Array,
      required: true
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  emits: ['view-detail', 'add', 'edit', 'delete'],
  setup(props, { emit }) {
    const viewMode = ref('card') // 默认卡片视图
    
    // 设置视图模式
    const setViewMode = (mode) => {
      viewMode.value = mode
    }
    
    // 查看案件详情
    const viewCaseDetail = (id) => {
      emit('view-detail', id)
    }
    
    // 添加案件
    const addCase = () => {
      emit('add')
    }
    
    // 编辑案件
    const editCase = (caseItem) => {
      emit('edit', caseItem)
    }
    
    // 确认删除案件
    const confirmDeleteCase = (caseItem) => {
      emit('delete', caseItem)
    }
    
    return {
      viewMode,
      setViewMode,
      viewCaseDetail,
      addCase,
      editCase,
      confirmDeleteCase
    }
  }
}
</script>

<style scoped>
.case-list-container {
  margin-bottom: 2rem;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.btn-primary {
  background: linear-gradient(to right, #4a6cf7, #2651f0);
  border: none;
  box-shadow: 0 4px 10px rgba(74, 108, 247, 0.3);
}

.btn-primary:hover {
  background: linear-gradient(to right, #2651f0, #1a3fd9);
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(74, 108, 247, 0.4);
}

.btn-outline-primary {
  color: #4a6cf7;
  border-color: #4a6cf7;
}

.btn-outline-primary:hover {
  background-color: rgba(74, 108, 247, 0.1);
  color: #2651f0;
  border-color: #2651f0;
}
</style>