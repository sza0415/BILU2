<template>
  <div class="container-fluid mt-4">
    <!-- 页面标题区域 -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h2 class="display-6 fw-bold text-primary mb-0">
              <i class="bi bi-folder2-open me-2"></i>案件管理
            </h2>
            <p class="text-muted mb-0">查看和管理所有案件信息</p>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 案件统计卡片 -->
    <case-statistics :loading="statisticsLoading" :statistics="statistics" />

    <!-- 搜索区域 -->
    <case-search 
      :search-form="searchForm" 
      :show-advanced-search="showAdvancedSearch"
      :case-types="caseTypes"
      :case-statuses="caseStatuses"
      @toggle-advanced="toggleAdvancedSearch"
      @search="searchCases"
      @reset="resetSearch"
    />

    <!-- 案件列表 -->
    <case-list 
      :cases="cases" 
      :loading="loading"
      @view-detail="viewCaseDetail"
      @add="showAddCaseModal"
      @edit="showEditCaseModal"
      @delete="confirmDeleteCase"
    />
    
    <!-- 分页控件 -->
    <pagination 
      :current-page="currentPage" 
      :total-pages="totalPages" 
      :total-items="totalItems" 
      :page-size="pageSize"
      @page-change="changePage"
      @size-change="changePageSize"
    />
    
    <!-- 模态框组件 -->
    <add-case-modal ref="addCaseModalRef" @submit="submitAddCase" />
    <edit-case-modal ref="editCaseModalRef" @submit="submitEditCase" />
    <delete-case-modal ref="deleteCaseModalRef" @confirm="deleteCase" />
    <case-detail-modal ref="caseDetailModalRef" @edit="showEditCaseModal" />
  </div>
</template>

<script>
import { ref, reactive, onMounted, watch } from 'vue'
import { getCaseList, searchCases, addCase, updateCase, deleteCase as deleteCaseApi, getCaseStatistics, getCaseTypes, getCaseStatuses } from '@/api/case'

// 导入组件
import CaseStatistics from '@/components/case/CaseStatistics.vue'
import CaseSearch from '@/components/case/CaseSearch.vue'
import CaseList from '@/components/case/CaseList.vue'
import Pagination from '@/components/case/Pagination.vue'
import AddCaseModal from '@/components/case/modals/AddCaseModal.vue'
import EditCaseModal from '@/components/case/modals/EditCaseModal.vue'
import DeleteCaseModal from '@/components/case/modals/DeleteCaseModal.vue'
import CaseDetailModal from '@/components/case/modals/CaseDetailModal.vue'

export default {
  name: 'CaseManagement',
  components: {
    CaseStatistics,
    CaseSearch,
    CaseList,
    Pagination,
    AddCaseModal,
    EditCaseModal,
    DeleteCaseModal,
    CaseDetailModal
  },
  setup() {
    // 状态变量
    const loading = ref(false)
    const statisticsLoading = ref(false)
    const showAdvancedSearch = ref(false)
    const cases = ref([])
    const caseTypes = ref([])
    const caseStatuses = ref([])
    const statistics = ref({
      totalCases: 0,
      criminalCases: 0,
      civilCases: 0,
      administrativeCases: 0,
      investigatingCases: 0,
      inTrialCases: 0,
      closedCases: 0
    })
    
    // 分页相关
    const currentPage = ref(1)
    const pageSize = ref(10)
    const totalItems = ref(0)
    const totalPages = ref(0)
    
    // 搜索表单
    const searchForm = reactive({
      q: '',
      caseType: '',
      status: '',
      startDateFrom: '',
      startDateTo: '',
      createdAtFrom: '',
      createdAtTo: '',
      sortBy: 'createdAt_desc',
      page: 1,
      size: pageSize.value
    })
    
    // 模态框引用
    const addCaseModalRef = ref(null)
    const editCaseModalRef = ref(null)
    const deleteCaseModalRef = ref(null)
    const caseDetailModalRef = ref(null)
    
    // 加载案件列表
    const loadCases = async () => {
      loading.value = true
      try {
        const response = await getCaseList()
        if (response.data && response.data.code === 200) {
          cases.value = response.data.data || []
          // 确保totalItems正确设置为案件数量
          totalItems.value = cases.value.length
        } else {
          console.error('加载案件列表失败:', response.data ? response.data.msg : '未知错误')
        }
      } catch (error) {
        console.error('加载案件列表失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 加载案件统计数据
    const loadStatistics = async () => {
      statisticsLoading.value = true
      try {
        const response = await getCaseStatistics()
        if (response.data && response.data.code === 200) {
          statistics.value = response.data.data || {
            totalCases: 0,
            criminalCases: 0,
            civilCases: 0,
            administrativeCases: 0,
            investigatingCases: 0,
            inTrialCases: 0,
            closedCases: 0
          }
        } else {
          console.error('加载案件统计失败:', response.data ? response.data.msg : '未知错误')
        }
      } catch (error) {
        console.error('加载案件统计失败:', error)
      } finally {
        statisticsLoading.value = false
      }
    }
    
    // 搜索案件
    const searchCasesData = async () => {
      console.log('开始执行searchCasesData方法')
      loading.value = true
      try {
        // 构建搜索参数
        const params = { ...searchForm }
        
        // 确保使用当前的 pageSize 值，并且是数字类型
        params.size = Number(pageSize.value)
        params.page = Number(params.page)
        
        // 将 keyword 参数重命名为 q，以匹配后端 API 期望的参数名
        if (params.keyword !== undefined) {
          params.q = params.keyword
          delete params.keyword
        }
        
        // 设置默认排序
        params.sortField = 'createdAt'
        params.sortOrder = 'desc'
        delete params.sortBy
        
        // 调试：检查参数是否正确
        console.log('searchForm:', searchForm)
        console.log('params:', params)
        
        console.log('准备发送API请求，参数:', params)
        const response = await searchCases(params)
        console.log('API响应:', response)
        
        console.log('处理API响应数据:', response)
        if (response.data) {
          console.log('响应数据:', response.data)
          if (response.data.code === 200) {
            const result = response.data.data
            console.log('结果数据:', result)
            cases.value = result.records || []
            // 确保totalItems正确设置为案件数量
            totalItems.value = result.total !== undefined ? result.total : cases.value.length
            totalPages.value = result.pages || 0
            currentPage.value = result.current || 1
          } else {
            console.error('搜索案件失败:', response.data.msg || '未知错误')
          }
        } else {
          console.error('搜索案件失败: 响应数据为空')
        }
      } catch (error) {
        console.error('搜索案件失败:', error)
      } finally {
        loading.value = false
      }
    }
    
    // 搜索案件（用于搜索按钮点击事件）
    const handleSearch = (formData) => {
      console.log('CaseManagement收到搜索事件，接收到的数据:', formData)
      // 将CaseSearch组件传来的keyword字段映射到searchForm的q字段
      if (formData) {
        // 检查formData是否包含keyword属性
        console.log('formData包含keyword属性:', Object.prototype.hasOwnProperty.call(formData, 'keyword'))
        console.log('formData.keyword值:', formData.keyword)
        
        // 使用q作为参数名，与后端API保持一致
        searchForm.q = formData.keyword || ''
        delete searchForm.keyword // 删除keyword参数
        
        searchForm.caseType = formData.caseType || ''
        searchForm.status = formData.status || ''
        searchForm.startDateFrom = formData.startDateFrom || ''
        searchForm.startDateTo = formData.startDateTo || ''
        searchForm.createdAtFrom = formData.createdAtFrom || ''
        searchForm.createdAtTo = formData.createdAtTo || ''
      }
      console.log('处理后的searchForm:', { ...searchForm })
      searchForm.page = 1 // 重置到第一页
      searchCasesData()
    }
    
    // 重置搜索
    const resetSearch = (formData) => {
      // 重置搜索表单
      if (formData) {
        // 如果CaseSearch组件传来了重置后的表单数据，使用它
        searchForm.q = formData.keyword || ''
        delete searchForm.keyword // 删除keyword参数
        
        searchForm.caseType = formData.caseType || ''
        searchForm.status = formData.status || ''
        searchForm.startDateFrom = formData.startDateFrom || ''
        searchForm.startDateTo = formData.startDateTo || ''
        searchForm.createdAtFrom = formData.createdAtFrom || ''
        searchForm.createdAtTo = formData.createdAtTo || ''
      } else {
        // 否则清空所有搜索条件
        searchForm.q = ''
        delete searchForm.keyword // 删除keyword参数
        
        searchForm.caseType = ''
        searchForm.status = ''
        searchForm.startDateFrom = ''
        searchForm.startDateTo = ''
        searchForm.createdAtFrom = ''
        searchForm.createdAtTo = ''
      }
      // 不再使用这个循环，因为它会清除所有字段，包括我们刚设置的keyword
      // 而是明确指定需要保留的字段
      searchForm.page = 1 // 重置到第一页
      searchCasesData() // 使用searchCasesData函数执行搜索
    }
    
    // 切换高级搜索
    const toggleAdvancedSearch = (value) => {
      // 如果传入了值，使用传入的值；否则切换当前值
      showAdvancedSearch.value = value !== undefined ? value : !showAdvancedSearch.value
    }
    
    // 切换页码
    const changePage = (page) => {
      searchForm.page = page
      searchCasesData()
    }
    
    // 更改每页显示数量
    const changePageSize = (size) => {
      if (size) {
        // 确保 size 是数字类型
        const numSize = typeof size === 'string' ? parseInt(size) : size
        pageSize.value = numSize
      }
      
      // 更新搜索表单中的每页数量
      searchForm.size = Number(pageSize.value) // 确保是数字类型
      
      // 重置到第一页
      searchForm.page = 1
      
      // 重新加载数据
      searchCasesData()
    }
    
    // 显示添加案件模态框
    const showAddCaseModal = () => {
      if (addCaseModalRef.value) {
        addCaseModalRef.value.showModal()
      }
    }
    
    // 显示编辑案件模态框
    const showEditCaseModal = (caseItem) => {
      if (editCaseModalRef.value) {
        editCaseModalRef.value.showModal(caseItem)
      }
    }
    
    // 确认删除案件
    const confirmDeleteCase = (caseItem) => {
      if (deleteCaseModalRef.value) {
        deleteCaseModalRef.value.showModal(caseItem)
      }
    }
    
    // 查看案件详情
    const viewCaseDetail = (caseId) => {
      if (caseDetailModalRef.value) {
        caseDetailModalRef.value.showModal(caseId)
      }
    }
    
    // 提交添加案件
    const submitAddCase = async (formData) => {
      try {
        const response = await addCase(formData)
        if (response.data && response.data.code === 200) {
          // 重新加载数据
          searchCasesData()
          loadStatistics()
        } else {
          console.error('添加案件失败:', response.data ? response.data.msg : '未知错误')
        }
      } catch (error) {
        console.error('添加案件失败:', error)
      }
    }
    
    // 提交编辑案件
    const submitEditCase = async (formData) => {
      try {
        const response = await updateCase(formData)
        if (response.data && response.data.code === 200) {
          // 重新加载数据
          searchCasesData()
          loadStatistics()
        } else {
          console.error('更新案件失败:', response.data ? response.data.msg : '未知错误')
        }
      } catch (error) {
        console.error('更新案件失败:', error)
      }
    }
    
    // 删除案件
    const deleteCase = async (caseId) => {
      try {
        const response = await deleteCaseApi(caseId)
        if (response.data && response.data.code === 200) {
          // 重新加载数据
          searchCasesData()
          loadStatistics()
        } else {
          console.error('删除案件失败:', response.data ? response.data.msg : '未知错误')
        }
      } catch (error) {
        console.error('删除案件失败:', error)
      }
    }
    
    // 加载案件类型
    const loadCaseTypes = async () => {
      try {
        const response = await getCaseTypes()
        if (response.data && response.data.code === 200) {
          caseTypes.value = response.data.data || []
        } else {
          console.error('加载案件类型失败:', response.data ? response.data.msg : '未知错误')
        }
      } catch (error) {
        console.error('加载案件类型失败:', error)
      }
    }
    
    // 加载案件状态
    const loadCaseStatuses = async () => {
      try {
        const response = await getCaseStatuses()
        if (response.data && response.data.code === 200) {
          caseStatuses.value = response.data.data || []
        } else {
          console.error('加载案件状态失败:', response.data ? response.data.msg : '未知错误')
        }
      } catch (error) {
        console.error('加载案件状态失败:', error)
      }
    }
    
    // 组件挂载时加载数据
    onMounted(() => {
      searchCasesData() // 使用searchCasesData方法替代loadCases方法
      loadStatistics()
      loadCaseTypes()
      loadCaseStatuses()
    })
    
    // 监听页码变化
    watch(currentPage, (newPage) => {
      searchForm.page = newPage
      searchCasesData() // 当页码变化时，重新加载数据
    })
    
    return {
      // 状态
      loading,
      statisticsLoading,
      showAdvancedSearch,
      cases,
      caseTypes,
      caseStatuses,
      statistics,
      
      // 分页
      currentPage,
      pageSize,
      totalItems,
      totalPages,
      
      // 表单
      searchForm,
      
      // 模态框引用
      addCaseModalRef,
      editCaseModalRef,
      deleteCaseModalRef,
      caseDetailModalRef,
      
      // 方法
      toggleAdvancedSearch,
      searchCases: handleSearch,
      resetSearch,
      changePage,
      changePageSize,
      showAddCaseModal,
      showEditCaseModal,
      confirmDeleteCase,
      viewCaseDetail,
      submitAddCase,
      submitEditCase,
      deleteCase,
      loadCases
    }
  }
}
</script>

<style scoped>
.container-fluid {
  max-width: 1600px;
  margin: 0 auto;
}

.display-6 {
  font-size: 2rem;
}

.btn-success {
  background: linear-gradient(to right, #28a745, #20c997);
  border: none;
  box-shadow: 0 4px 10px rgba(40, 167, 69, 0.3);
}

.btn-success:hover {
  background: linear-gradient(to right, #218838, #1e9e7f);
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(40, 167, 69, 0.4);
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