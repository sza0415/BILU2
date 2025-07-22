<template>
  <div class="search-section mb-4">
    <div class="card border-0 shadow-sm rounded-3">
      <div class="card-body p-4">
        <div class="row g-3 align-items-center">
          <div class="col-md-7">
            <div class="input-group">
              <span class="input-group-text bg-light border-0">
                <i class="bi bi-search text-muted"></i>
              </span>
              <input 
                type="text" 
                class="form-control border-0 bg-light" 
                placeholder="搜索案件名称、编号或描述" 
                v-model="formData.keyword"
                @keyup.enter="searchCases"
              >
            </div>
          </div>
          <div class="col-md-3">
            <div class="d-flex">
              <button class="btn btn-primary w-100" @click="searchCases">
                <i class="bi bi-search me-2"></i>搜索
              </button>
            </div>
          </div>
          <div class="col-md-2 text-end">
            <button class="btn btn-outline-secondary w-100" @click="toggleAdvancedSearch">
              <i class="bi" :class="advancedSearchVisible ? 'bi-chevron-up' : 'bi-chevron-down'"></i>
              {{ advancedSearchVisible ? '收起' : '展开' }}高级搜索选项
            </button>
          </div>
        </div>

        <!-- 高级搜索 -->
        <div v-show="advancedSearchVisible" class="advanced-search-panel">
          <div class="p-4 bg-light rounded-3 border border-light mt-3">
            <h6 class="fw-bold mb-3 d-flex align-items-center">
              <span class="badge bg-primary me-2"><i class="bi bi-funnel-fill"></i></span>高级搜索选项
            </h6>
            <div class="row g-3">
              <div class="col-md-3">
                <label class="form-label">案件类型</label>
                <select class="form-select shadow-sm" v-model="formData.caseType">
                  <option value="">全部类型</option>
                  <option v-for="type in caseTypes" :key="type" :value="type">{{ type }}</option>
                </select>
              </div>
              <div class="col-md-3">
                <label class="form-label">案件状态</label>
                <select class="form-select shadow-sm" v-model="formData.status">
                  <option value="">全部状态</option>
                  <option v-for="status in caseStatuses" :key="status" :value="status">{{ status }}</option>
                </select>
              </div>
              <div class="col-md-3">
                <label class="form-label">立案日期（从）</label>
                <input type="date" class="form-control shadow-sm" v-model="formData.startDateFrom">
              </div>
              <div class="col-md-3">
                <label class="form-label">立案日期（至）</label>
                <input type="date" class="form-control shadow-sm" v-model="formData.startDateTo">
              </div>
              <div class="col-md-3">
                <label class="form-label">创建日期（从）</label>
                <input type="date" class="form-control shadow-sm" v-model="formData.createdAtFrom">
              </div>
              <div class="col-md-3">
                <label class="form-label">创建日期（至）</label>
                <input type="date" class="form-control shadow-sm" v-model="formData.createdAtTo">
              </div>
              <div class="col-12 text-end mt-4">
                <button class="btn btn-outline-secondary me-2" @click="resetSearch">
                  <i class="bi bi-arrow-counterclockwise me-2"></i>重置
                </button>
                <button class="btn btn-primary px-4" @click="searchCases">
                  <i class="bi bi-search me-2"></i>搜索
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'

export default {
  name: 'CaseSearch',
  props: {
    caseTypes: {
      type: Array,
      default: () => []
    },
    caseStatuses: {
      type: Array,
      default: () => []
    },
    searchForm: {
      type: Object,
      default: () => ({})
    },
    showAdvancedSearch: {
      type: Boolean,
      default: false
    }
  },
  emits: ['search', 'reset', 'toggle-advanced'],
  setup(props, { emit }) {
    // 使用传入的props初始化本地状态
    const advancedSearchVisible = ref(props.showAdvancedSearch)
    
    // 将父组件传入的searchForm.keyword映射到本地的keyword
    console.log('初始化formData，props.searchForm:', props.searchForm)
    const formData = reactive({
      keyword: props.searchForm?.keyword || '',
      caseType: props.searchForm?.caseType || '',
      status: props.searchForm?.status || '',
      startDateFrom: props.searchForm?.startDateFrom || '',
      startDateTo: props.searchForm?.startDateTo || '',
      createdAtFrom: props.searchForm?.createdAtFrom || '',
      createdAtTo: props.searchForm?.createdAtTo || '',
      page: props.searchForm?.page || 1,
      size: props.searchForm?.size || 10,
      sortBy: props.searchForm?.sortBy || 'createdAt,desc'
    })
    console.log('初始化后的formData:', formData)
    
    // 切换高级搜索面板
    const toggleAdvancedSearch = () => {
      advancedSearchVisible.value = !advancedSearchVisible.value
      emit('toggle-advanced', advancedSearchVisible.value)
    }
    
    // 搜索案件
    const searchCases = () => {
      console.log('搜索按钮被点击，发送数据:', { ...formData })
      // 检查formData对象
      console.log('formData对象属性:', Object.keys(formData))
      console.log('formData.keyword值:', formData.keyword)
      
      formData.page = 1 // 重置页码
      const dataToEmit = { ...formData }
      console.log('准备发送的数据:', dataToEmit)
      emit('search', dataToEmit)
    }
    
    // 重置搜索表单
    const resetSearch = () => {
      Object.keys(formData).forEach(key => {
        if (key !== 'page' && key !== 'size' && key !== 'sortBy') {
          formData[key] = ''
        }
      })
      formData.page = 1
      emit('reset', { ...formData })
    }
    
    // 返回组件需要的数据和方法
    return {
      // 使用不同的变量名，避免与props冲突
      advancedSearchVisible,
      formData,
      toggleAdvancedSearch,
      searchCases,
      resetSearch
    }
  }
}
</script>

<style scoped>
.search-section {
  margin-bottom: 1.5rem;
}

.advanced-search-panel {
  margin-top: 1rem;
}

.form-control, .form-select {
  border-radius: 6px;
  border: 1px solid rgba(0, 0, 0, 0.1);
  padding: 0.6rem 1rem;
  transition: all 0.3s ease;
}

.form-control:focus, .form-select:focus {
  border-color: #4a6cf7;
  box-shadow: 0 0 0 0.25rem rgba(74, 108, 247, 0.25);
}
</style>