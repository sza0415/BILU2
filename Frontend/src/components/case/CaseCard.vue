<template>
  <div class="card shadow-sm h-100 border-0 hover-card" style="position: relative; overflow: visible;">
    <div class="card-body p-3" style="overflow: visible;">
      <!-- 卡片头部：案件名称、状态 -->
      <div class="d-flex justify-content-between align-items-center mb-2">
        <div class="d-flex align-items-center">
          <div class="case-icon rounded-circle p-1 me-2" 
               :class="{
                 'bg-success bg-opacity-10': caseItem.caseType === '民事案件',
                 'bg-danger bg-opacity-10': caseItem.caseType === '刑事案件',
                 'bg-info bg-opacity-10': caseItem.caseType === '行政案件',
                 'bg-primary bg-opacity-10': !['民事案件', '刑事案件', '行政案件'].includes(caseItem.caseType)
               }">
            <i class="bi fs-5" 
               :class="{
                 'bi-people text-success': caseItem.caseType === '民事案件',
                 'bi-shield-lock text-danger': caseItem.caseType === '刑事案件',
                 'bi-building text-info': caseItem.caseType === '行政案件',
                 'bi-folder text-primary': !['民事案件', '刑事案件', '行政案件'].includes(caseItem.caseType)
               }"></i>
          </div>
          <div>
            <h6 class="card-title mb-0 fw-bold">{{ caseItem.caseName }}</h6>
            <div class="text-muted small">{{ caseItem.caseNumber }}</div>
          </div>
        </div>
        <span class="badge rounded-pill px-2 py-1 small" :class="{
          'bg-success': caseItem.status === '已完成' || caseItem.status === '已结案',
          'bg-warning text-dark': caseItem.status === '进行中',
          'bg-secondary': caseItem.status !== '已完成' && caseItem.status !== '已结案' && caseItem.status !== '进行中'
        }">{{ caseItem.status }}</span>
      </div>
      
      <!-- 案件信息 -->
      <div class="row g-2 mt-2 small">
        <div class="col-6">
          <div class="d-flex align-items-center">
            <i class="bi bi-folder text-primary me-1"></i>
            <span class="text-muted">类型:</span> <span class="ms-1">{{ caseItem.caseType }}</span>
          </div>
        </div>
        <div class="col-6">
          <div class="d-flex align-items-center">
            <i class="bi bi-calendar-date text-success me-1"></i>
            <span class="text-muted">立案:</span> <span class="ms-1">{{ formatDate(caseItem.startDate) }}</span>
          </div>
        </div>
        <div class="col-6">
          <div class="d-flex align-items-center">
            <i class="bi bi-card-text text-info me-1"></i>
            <span class="text-muted">笔录:</span> <span class="ms-1">{{ caseItem.recordCount || 0 }}</span>
          </div>
        </div>
        <div class="col-6">
          <div class="d-flex align-items-center">
            <i class="bi bi-clock-history text-warning me-1"></i>
            <span class="text-muted">创建:</span> <span class="ms-1">{{ formatDateTime(caseItem.createdAt) }}</span>
          </div>
        </div>
      </div>
      
      <!-- 操作按钮 -->
      <div class="mt-3 pt-2 border-top d-flex justify-content-between">
        <button class="btn btn-sm btn-primary" @click.stop="viewDetail(caseItem.id)">
          <i class="bi bi-eye me-1"></i> 查看
        </button>
        <div class="dropdown dropup">
          <button class="btn btn-sm btn-outline-secondary dropdown-toggle" type="button" data-bs-toggle="dropdown" aria-expanded="false">
            <i class="bi bi-three-dots"></i> 操作
          </button>
          <ul class="dropdown-menu dropdown-menu-end" data-bs-popper="static">
            <li><a class="dropdown-item" href="#" @click.prevent.stop="editCase(caseItem)"><i class="bi bi-pencil me-2"></i>编辑</a></li>
            <li><a class="dropdown-item" href="#" @click.prevent.stop="confirmDelete(caseItem)"><i class="bi bi-trash me-2"></i>删除</a></li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
// 不再需要ref
import { } from 'vue'

export default {
  name: 'CaseCard',
  props: {
    caseItem: {
      type: Object,
      required: true
    }
  },
  emits: ['view-detail', 'edit', 'delete'],
  setup(props, { emit }) {
    // 下拉菜单由Bootstrap处理
    
    // 查看详情
    const viewDetail = (id) => {
      emit('view-detail', id)
    }
    
    // 编辑案件
    const editCase = (caseItem) => {
      emit('edit', caseItem)
    }
    
    // 确认删除
    const confirmDelete = (caseItem) => {
      emit('delete', caseItem)
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
      return `${date.toISOString().split('T')[0]} ${date.toTimeString().split(' ')[0].substring(0, 5)}`
    }
    
    return {
      viewDetail,
      editCase,
      confirmDelete,
      formatDate,
      formatDateTime
    }
  }
}
</script>

<style scoped>
/* 卡片悬停效果 */
.hover-card {
  transition: all 0.3s ease;
  border: none;
  overflow: hidden;
  position: relative;
  z-index: 1; /* 确保卡片有正确的层叠顺序 */
}

.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15) !important;
}

.hover-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, #4a6cf7, #2651f0);
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: -1; /* 确保伪元素不会干扰点击事件 */
  pointer-events: none; /* 确保伪元素不会捕获点击事件 */
}

.hover-card:hover::before {
  opacity: 1;
}

.case-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 下拉菜单样式 */
.dropdown-menu {
  min-width: 120px;
  border: none;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  padding: 0.5rem 0;
}

.dropdown-item {
  padding: 0.5rem 1rem;
  font-size: 0.875rem;
}

.dropdown-item:hover {
  background-color: rgba(74, 108, 247, 0.1);
}

.dropdown-item i {
  width: 16px;
  text-align: center;
}
</style>