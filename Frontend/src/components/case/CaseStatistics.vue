<template>
  <div class="row stats-cards">
    <!-- 总案件数 -->
    <div class="stats-card-wrapper">
      <div class="card border-0 shadow-sm stats-card">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="stats-icon bg-primary bg-opacity-10 text-primary">
              <i class="bi bi-folder2"></i>
            </div>
            <div class="ms-3">
              <h3 class="fw-bold mb-0">{{ statistics.totalCases || 0 }}</h3>
              <p class="text-muted mb-0">总案件数</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 刑事案件 -->
    <div class="stats-card-wrapper">
      <div class="card border-0 shadow-sm stats-card">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="stats-icon bg-danger bg-opacity-10 text-danger">
              <i class="bi bi-shield-lock"></i>
            </div>
            <div class="ms-3">
              <h3 class="fw-bold mb-0">{{ statistics.criminalCases || 0 }}</h3>
              <p class="text-muted mb-0">刑事案件</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 民事案件 -->
    <div class="stats-card-wrapper">
      <div class="card border-0 shadow-sm stats-card">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="stats-icon bg-success bg-opacity-10 text-success">
              <i class="bi bi-people"></i>
            </div>
            <div class="ms-3">
              <h3 class="fw-bold mb-0">{{ statistics.civilCases || 0 }}</h3>
              <p class="text-muted mb-0">民事案件</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 行政案件 -->
    <div class="stats-card-wrapper">
      <div class="card border-0 shadow-sm stats-card">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="stats-icon bg-info bg-opacity-10 text-info">
              <i class="bi bi-building"></i>
            </div>
            <div class="ms-3">
              <h3 class="fw-bold mb-0">{{ statistics.administrativeCases || 0 }}</h3>
              <p class="text-muted mb-0">行政案件</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 调查中 -->
    <div class="stats-card-wrapper">
      <div class="card border-0 shadow-sm stats-card">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="stats-icon bg-warning bg-opacity-10 text-warning">
              <i class="bi bi-search"></i>
            </div>
            <div class="ms-3">
              <h3 class="fw-bold mb-0">{{ statistics.investigatingCases || 0 }}</h3>
              <p class="text-muted mb-0">调查中</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 审理中 -->
    <div class="stats-card-wrapper">
      <div class="card border-0 shadow-sm stats-card">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="stats-icon bg-secondary bg-opacity-10 text-secondary">
              <i class="bi bi-hourglass-split"></i>
            </div>
            <div class="ms-3">
              <h3 class="fw-bold mb-0">{{ statistics.inTrialCases || 0 }}</h3>
              <p class="text-muted mb-0">审理中</p>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 已结案 -->
    <div class="stats-card-wrapper">
      <div class="card border-0 shadow-sm stats-card">
        <div class="card-body">
          <div class="d-flex align-items-center">
            <div class="stats-icon bg-dark bg-opacity-10 text-dark">
              <i class="bi bi-check2-circle"></i>
            </div>
            <div class="ms-3">
              <h3 class="fw-bold mb-0">{{ statistics.closedCases || 0 }}</h3>
              <p class="text-muted mb-0">已结案</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { computed } from 'vue'
import { getCaseStatistics } from '@/api/case'

export default {
  name: 'CaseStatistics',
  props: {
    statistics: {
      type: Object,
      default: () => ({
        totalCases: 0,
        criminalCases: 0,
        civilCases: 0,
        administrativeCases: 0,
        investigatingCases: 0,
        inTrialCases: 0,
        closedCases: 0
      })
    },
    loading: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    // 使用计算属性来处理父组件传递的数据
    const statisticsLoading = computed(() => props.loading)
    
    // 加载统计数据的方法保留，以便组件可以独立使用
    const loadStatistics = async () => {
      // 此方法现在仅在组件独立使用时才会被调用
      try {
        const response = await getCaseStatistics()
        if (response.data.code === 200) {
          // 这里不再直接修改 statistics，因为它现在是从父组件传递的
          console.log('统计数据已加载，但不会更新组件状态，因为使用的是父组件传递的数据')
          return response.data.data || {
            totalCases: 0,
            criminalCases: 0,
            civilCases: 0,
            administrativeCases: 0,
            investigatingCases: 0,
            inTrialCases: 0,
            closedCases: 0
          }
        } else {
          console.error('加载统计数据失败:', response.data.msg)
          return null
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
        return null
      }
    }
    
    // 组件挂载时不再自动加载统计数据，因为数据由父组件提供
    // 如果需要在组件独立使用时加载数据，可以取消下面的注释
    // onMounted(() => {
    //   if (!props.statistics || Object.keys(props.statistics).length === 0) {
    //     loadStatistics()
    //   }
    // })
    
    // 直接返回 props 中的 statistics，不需要额外的计算属性
    return {
      statisticsLoading,
      loadStatistics
    }
  }
}
</script>

<style scoped>
.stats-cards {
  display: flex;
  flex-wrap: wrap;
  margin: 0 -10px;
}

.stats-card-wrapper {
  flex: 0 0 calc(25% - 20px);
  padding: 0 10px;
  margin-bottom: 20px;
}

.stats-card {
  transition: all 0.3s ease;
  border-radius: 10px;
  overflow: hidden;
}

.stats-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1) !important;
}

.stats-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
}

@media (max-width: 1200px) {
  .stats-card-wrapper {
    flex: 0 0 calc(33.333% - 20px);
  }
}

@media (max-width: 992px) {
  .stats-card-wrapper {
    flex: 0 0 calc(50% - 20px);
  }
}

@media (max-width: 576px) {
  .stats-card-wrapper {
    flex: 0 0 calc(100% - 20px);
  }
}
</style>