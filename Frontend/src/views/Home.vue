<template>
  <div class="container mt-4">
    <!-- 页面标题区域 -->
    <div class="row mb-4">
      <div class="col-12 text-center">
        <h1 class="display-4 fw-bold text-primary">笔录管理系统</h1>
        <p class="lead text-muted">高效、安全、智能的笔录记录与管理平台</p>
      </div>
    </div>

    <!-- 功能卡片区域 -->
    <div class="row g-4">
      <!-- 笔录页面 -->
      <div class="col-md-6 col-lg-4">
        <div class="card h-100 border-0 shadow-sm hover-card">
          <div class="card-body text-center p-4">
            <div class="feature-icon mb-3">
              <i class="bi bi-pencil-square text-primary" style="font-size: 3rem;"></i>
            </div>
            <h5 class="card-title fw-bold">笔录页面</h5>
            <p class="card-text text-muted">创建和编辑笔录记录，支持多种格式输入</p>
            <router-link to="/task" class="btn btn-primary btn-lg">
              <i class="bi bi-arrow-right me-2"></i>进入
            </router-link>
          </div>
        </div>
      </div>

      <!-- 笔录搜索 -->
      <div class="col-md-6 col-lg-4">
        <div class="card h-100 border-0 shadow-sm hover-card">
          <div class="card-body text-center p-4">
            <div class="feature-icon mb-3">
              <i class="bi bi-search text-success" style="font-size: 3rem;"></i>
            </div>
            <h5 class="card-title fw-bold">笔录搜索</h5>
            <p class="card-text text-muted">快速查找和检索历史笔录记录</p>
            <router-link to="/search" class="btn btn-success btn-lg">
              <i class="bi bi-arrow-right me-2"></i>进入
            </router-link>
          </div>
        </div>
      </div>

      <!-- 案件管理 -->
      <div class="col-md-6 col-lg-4">
        <div class="card h-100 border-0 shadow-sm hover-card">
          <div class="card-body text-center p-4">
            <div class="feature-icon mb-3">
              <i class="bi bi-folder2-open text-warning" style="font-size: 3rem;"></i>
            </div>
            <h5 class="card-title fw-bold">案件管理</h5>
            <p class="card-text text-muted">管理案件信息，关联相关笔录</p>
            <router-link to="/cases" class="btn btn-warning btn-lg">
              <i class="bi bi-arrow-right me-2"></i>进入
            </router-link>
          </div>
        </div>
      </div>

      <!-- 模版管理 -->
      <div class="col-md-6 col-lg-4">
        <div class="card h-100 border-0 shadow-sm hover-card">
          <div class="card-body text-center p-4">
            <div class="feature-icon mb-3">
              <i class="bi bi-gear text-info" style="font-size: 3rem;"></i>
            </div>
            <h5 class="card-title fw-bold">模版管理</h5>
            <p class="card-text text-muted">管理保密问答模板，提高工作效率</p>
            <router-link to="/templates" class="btn btn-info btn-lg">
              <i class="bi bi-arrow-right me-2"></i>进入
            </router-link>
          </div>
        </div>
      </div>

      <!-- 打包管理 -->
      <div class="col-md-6 col-lg-4">
        <div class="card h-100 border-0 shadow-sm hover-card">
          <div class="card-body text-center p-4">
            <div class="feature-icon mb-3">
              <i class="bi bi-archive text-secondary" style="font-size: 3rem;"></i>
            </div>
            <h5 class="card-title fw-bold">打包管理</h5>
            <p class="card-text text-muted">批量处理和打包笔录文件</p>
            <router-link to="/package" class="btn btn-secondary btn-lg">
              <i class="bi bi-arrow-right me-2"></i>进入
            </router-link>
          </div>
        </div>
      </div>

      <!-- 管理员账户 -->
      <div class="col-md-6 col-lg-4">
        <div class="card h-100 border-0 shadow-sm hover-card">
          <div class="card-body text-center p-4">
            <div class="feature-icon mb-3">
              <i class="bi bi-shield-lock text-danger" style="font-size: 3rem;"></i>
            </div>
            <h5 class="card-title fw-bold">管理员账户</h5>
            <p class="card-text text-muted">管理系统用户和权限设置</p>
            <router-link to="/admin" class="btn btn-danger btn-lg">
              <i class="bi bi-arrow-right me-2"></i>进入
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计信息区域 -->
    <div class="row mt-5">
      <div class="col-12">
        <div class="card border-0 shadow-sm">
          <div class="card-body">
            <h5 class="card-title fw-bold mb-4">
              <i class="bi bi-bar-chart me-2 text-primary"></i>系统统计
            </h5>
            <div class="row text-center">
              <div class="col-md-3">
                <div class="stat-item">
                  <h3 class="text-primary fw-bold">{{ stats.totalRecords }}</h3>
                  <p class="text-muted mb-0">总笔录数</p>
                </div>
              </div>
              <div class="col-md-3">
                <div class="stat-item">
                  <h3 class="text-success fw-bold">{{ stats.totalCases }}</h3>
                  <p class="text-muted mb-0">总案件数</p>
                </div>
              </div>
              <div class="col-md-3">
                <div class="stat-item">
                  <h3 class="text-warning fw-bold">{{ stats.totalTemplates }}</h3>
                  <p class="text-muted mb-0">模板数量</p>
                </div>
              </div>
              <div class="col-md-3">
                <div class="stat-item">
                  <h3 class="text-info fw-bold">{{ stats.todayRecords }}</h3>
                  <p class="text-muted mb-0">今日新增</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomePage',
  data() {
    return {
      stats: {
        totalRecords: 1248,
        totalCases: 156,
        totalTemplates: 45,
        todayRecords: 12
      }
    }
  },
  mounted() {
    // 这里可以调用API获取真实的统计数据
    this.loadStats();
  },
  methods: {
    loadStats() {
      // 模拟API调用
      // 实际项目中这里应该调用后端API
      console.log('加载统计数据...');
    }
  }
}
</script>

<style scoped>
.hover-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.hover-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0,0,0,0.15) !important;
}

.feature-icon {
  transition: all 0.3s ease;
}

.hover-card:hover .feature-icon i {
  transform: scale(1.1);
}

.stat-item {
  padding: 1rem;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
}

.btn {
  transition: all 0.3s ease;
}

.btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
</style>