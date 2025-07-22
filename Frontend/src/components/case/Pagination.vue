<template>
  <div class="d-flex flex-column flex-md-row justify-content-between align-items-center mt-5 pt-4 border-top">
    <div class="d-flex align-items-center text-muted mb-3 mb-md-0">
      <span class="badge bg-primary bg-opacity-10 text-primary me-2 p-2">{{ totalItems }}</span>
      条记录，当前第 <span class="fw-bold text-primary">{{ currentPage }}</span> / {{ totalPages }} 页
      
      <div class="ms-3 d-flex align-items-center">
        <span class="me-2">每页显示:</span>
        <select class="form-select form-select-sm" v-model.number="localPageSize" @change="changePageSize" style="width: 80px;">
          <option :value="5">5</option>
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
          <option :value="100">100</option>
        </select>
      </div>
    </div>
    <nav aria-label="Page navigation" class="pagination-container">
      <ul class="pagination pagination-md">
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link rounded-start" href="#" @click.prevent="changePage(1)" title="首页">
            <i class="bi bi-chevron-double-left"></i>
          </a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <a class="page-link" href="#" @click.prevent="changePage(currentPage - 1)" title="上一页">
            <i class="bi bi-chevron-left"></i>
          </a>
        </li>
        <li v-for="page in displayedPages" :key="page" class="page-item" :class="{ active: currentPage === page }">
          <a class="page-link" href="#" @click.prevent="changePage(page)">{{ page }}</a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link" href="#" @click.prevent="changePage(currentPage + 1)" title="下一页">
            <i class="bi bi-chevron-right"></i>
          </a>
        </li>
        <li class="page-item" :class="{ disabled: currentPage === totalPages }">
          <a class="page-link rounded-end" href="#" @click.prevent="changePage(totalPages)" title="末页">
            <i class="bi bi-chevron-double-right"></i>
          </a>
        </li>
      </ul>
    </nav>
  </div>
</template>

<script>
import { ref, computed, watch } from 'vue'

export default {
  name: 'CasePagination',
  props: {
    currentPage: {
      type: Number,
      required: true
    },
    pageSize: {
      type: Number,
      required: true
    },
    totalItems: {
      type: Number,
      required: true
    }
  },
  emits: ['page-change', 'page-size-change'],
  setup(props, { emit }) {
    const localPageSize = ref(props.pageSize)
    
    // 监听 pageSize 变化
    watch(() => props.pageSize, (newSize) => {
      localPageSize.value = newSize
    })
    
    // 计算总页数
    const totalPages = computed(() => {
      return Math.max(1, Math.ceil(props.totalItems / props.pageSize))
    })
    
    // 计算要显示的页码
    const displayedPages = computed(() => {
      const pages = []
      const maxDisplayed = 5 // 最多显示5个页码
      
      if (totalPages.value <= maxDisplayed) {
        // 如果总页数小于等于最大显示数，则显示所有页码
        for (let i = 1; i <= totalPages.value; i++) {
          pages.push(i)
        }
      } else {
        // 否则，显示当前页附近的页码
        let start = Math.max(1, props.currentPage - Math.floor(maxDisplayed / 2))
        let end = start + maxDisplayed - 1
        
        if (end > totalPages.value) {
          end = totalPages.value
          start = Math.max(1, end - maxDisplayed + 1)
        }
        
        for (let i = start; i <= end; i++) {
          pages.push(i)
        }
      }
      
      return pages
    })
    
    // 切换页码
    const changePage = (page) => {
      if (page < 1 || page > totalPages.value || page === props.currentPage) {
        return
      }
      emit('page-change', page)
    }
    
    // 更改每页显示数量
    const changePageSize = () => {
      emit('page-size-change', localPageSize.value)
    }
    
    return {
      localPageSize,
      totalPages,
      displayedPages,
      changePage,
      changePageSize
    }
  }
}
</script>

<style scoped>
/* 分页控件美化 */
.pagination .page-link {
  border-radius: 4px;
  margin: 0 2px;
  color: #4a6cf7;
  border: 1px solid rgba(74, 108, 247, 0.2);
}

.pagination .page-item.active .page-link {
  background: linear-gradient(to right, #4a6cf7, #2651f0);
  border-color: #2651f0;
}

.pagination .page-item.disabled .page-link {
  color: #6c757d;
  pointer-events: none;
  background-color: #fff;
  border-color: #dee2e6;
}

.pagination .page-link:hover {
  background-color: rgba(74, 108, 247, 0.1);
  border-color: rgba(74, 108, 247, 0.3);
}

.pagination .page-item.active .page-link:hover {
  background: linear-gradient(to right, #4a6cf7, #2651f0);
}
</style>