<template>
  <div class="table-responsive">
    <table class="table table-hover align-middle">
      <thead class="table-light">
        <tr>
          <th scope="col" width="30%">案件信息</th>
          <th scope="col">类型</th>
          <th scope="col">立案日期</th>
          <th scope="col">状态</th>
          <th scope="col">笔录数</th>
          <th scope="col" width="150">操作</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(caseItem, index) in cases" :key="index">
          <td>
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
                <div class="fw-medium">{{ caseItem.caseName }}</div>
                <div class="text-muted small">{{ caseItem.caseNumber }}</div>
              </div>
            </div>
          </td>
          <td>{{ caseItem.caseType }}</td>
          <td>{{ formatDate(caseItem.startDate) }}</td>
          <td>
            <span class="badge rounded-pill px-2 py-1" :class="{
              'bg-success': caseItem.status === '已完成' || caseItem.status === '已结案',
              'bg-warning text-dark': caseItem.status === '进行中',
              'bg-secondary': caseItem.status !== '已完成' && caseItem.status !== '已结案' && caseItem.status !== '进行中'
            }">{{ caseItem.status }}</span>
          </td>
          <td>{{ caseItem.recordCount || 0 }}</td>
          <td>
            <div class="btn-group">
              <button class="btn btn-sm btn-outline-primary" @click.stop.prevent="viewDetail(caseItem.id)">
                <i class="bi bi-eye"></i>
              </button>
              <button class="btn btn-sm btn-outline-secondary" @click.stop.prevent="editCase(caseItem)">
                <i class="bi bi-pencil"></i>
              </button>
              <button class="btn btn-sm btn-outline-danger" @click.stop.prevent="confirmDelete(caseItem)">
                <i class="bi bi-trash"></i>
              </button>
            </div>
          </td>
        </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
import { onMounted } from 'vue';

export default {
  name: 'CaseTable',
  props: {
    cases: {
      type: Array,
      required: true
    }
  },
  emits: ['view-detail', 'edit', 'delete'],
  setup(props, { emit }) {
     // 组件挂载后初始化Bootstrap下拉菜单
     onMounted(() => {
       // 确保Bootstrap的dropdown功能正常工作
       if (typeof window.bootstrap !== 'undefined') {
         const dropdownElementList = document.querySelectorAll('.dropdown-toggle');
         dropdownElementList.forEach(dropdownToggleEl => {
           new window.bootstrap.Dropdown(dropdownToggleEl);
         });
       }
     });
     
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
    
    return {
      viewDetail,
      editCase,
      confirmDelete,
      formatDate
    }
  }
}
</script>

<style scoped>
.case-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.table {
  border-collapse: separate;
  border-spacing: 0;
}

.table th {
  font-weight: 600;
  padding: 1rem 0.75rem;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.table td {
  padding: 0.75rem;
  vertical-align: middle;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
}

.table tbody tr:hover {
  background-color: rgba(74, 108, 247, 0.05);
}

.btn-group .btn {
  margin-right: 0.25rem;
}

.btn-group .btn:last-child {
  margin-right: 0;
}

/* 确保下拉菜单显示在其他元素之上 */
.dropdown-menu {
  z-index: 1030;
}
</style>