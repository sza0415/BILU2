<template>
  <div class="container-fluid mt-4">
    <!-- 页面标题区域 -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h2 class="display-6 fw-bold text-primary mb-0">
              <i class="bi bi-archive me-2"></i>打包管理
            </h2>
            <p class="text-muted mb-0">管理和下载笔录打包文件</p>
          </div>
          <div class="btn-group">
            <button class="btn btn-primary btn-lg" @click="showCreatePackageModal = true">
              <i class="bi bi-plus-circle me-2"></i>新建打包
            </button>
            <button class="btn btn-success btn-lg" @click="batchDownload" :disabled="selectedPackages.length === 0">
              <i class="bi bi-download me-2"></i>批量下载
            </button>
            <button class="btn btn-warning btn-lg" @click="cleanupPackages">
              <i class="bi bi-trash3 me-2"></i>清理过期
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="row mb-4">
      <div class="col-md-3">
        <div class="card border-0 shadow-sm stats-card">
          <div class="card-body text-center">
            <div class="stats-icon bg-primary bg-opacity-10 text-primary mb-3">
              <i class="bi bi-archive"></i>
            </div>
            <h3 class="fw-bold mb-1">{{ stats.totalPackages }}</h3>
            <p class="text-muted mb-0">总打包数</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm stats-card">
          <div class="card-body text-center">
            <div class="stats-icon bg-success bg-opacity-10 text-success mb-3">
              <i class="bi bi-check-circle"></i>
            </div>
            <h3 class="fw-bold mb-1">{{ stats.completedPackages }}</h3>
            <p class="text-muted mb-0">已完成</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm stats-card">
          <div class="card-body text-center">
            <div class="stats-icon bg-warning bg-opacity-10 text-warning mb-3">
              <i class="bi bi-clock-history"></i>
            </div>
            <h3 class="fw-bold mb-1">{{ stats.processingPackages }}</h3>
            <p class="text-muted mb-0">处理中</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm stats-card">
          <div class="card-body text-center">
            <div class="stats-icon bg-info bg-opacity-10 text-info mb-3">
              <i class="bi bi-hdd"></i>
            </div>
            <h3 class="fw-bold mb-1">{{ formatFileSize(stats.totalSize) }}</h3>
            <p class="text-muted mb-0">总大小</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索和筛选区域 -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card border-0 shadow-sm">
          <div class="card-body">
            <div class="row g-3">
              <div class="col-md-3">
                <label class="form-label fw-bold">搜索关键词</label>
                <div class="input-group">
                  <span class="input-group-text">
                    <i class="bi bi-search"></i>
                  </span>
                  <input 
                    type="text" 
                    class="form-control" 
                    placeholder="打包名称、描述等"
                    v-model="searchForm.keyword"
                  />
                </div>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">打包状态</label>
                <select class="form-select" v-model="searchForm.status">
                  <option value="">全部状态</option>
                  <option value="pending">等待中</option>
                  <option value="processing">处理中</option>
                  <option value="completed">已完成</option>
                  <option value="failed">失败</option>
                  <option value="expired">已过期</option>
                </select>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">打包类型</label>
                <select class="form-select" v-model="searchForm.packageType">
                  <option value="">全部类型</option>
                  <option value="case">按案件</option>
                  <option value="date">按日期</option>
                  <option value="custom">自定义</option>
                  <option value="template">模板导出</option>
                </select>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">创建人</label>
                <select class="form-select" v-model="searchForm.creator">
                  <option value="">全部创建人</option>
                  <option value="李警官">李警官</option>
                  <option value="张警官">张警官</option>
                  <option value="王警官">王警官</option>
                </select>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">创建日期</label>
                <input type="date" class="form-control" v-model="searchForm.dateFrom">
              </div>
              <div class="col-md-1">
                <label class="form-label fw-bold">&nbsp;</label>
                <button class="btn btn-primary w-100" @click="searchPackages">
                  <i class="bi bi-search"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 打包列表 -->
    <div class="row">
      <div class="col-12">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white border-0 py-3">
            <div class="d-flex justify-content-between align-items-center">
              <h5 class="fw-bold mb-0">
                <i class="bi bi-list-ul me-2 text-primary"></i>打包列表
                <span class="badge bg-primary ms-2">{{ filteredPackages.length }}</span>
              </h5>
              <div class="btn-group btn-group-sm">
                <button 
                  class="btn" 
                  :class="viewMode === 'table' ? 'btn-primary' : 'btn-outline-primary'"
                  @click="viewMode = 'table'"
                >
                  <i class="bi bi-table"></i>
                </button>
                <button 
                  class="btn" 
                  :class="viewMode === 'card' ? 'btn-primary' : 'btn-outline-primary'"
                  @click="viewMode = 'card'"
                >
                  <i class="bi bi-grid-3x3-gap"></i>
                </button>
              </div>
            </div>
          </div>
          
          <div class="card-body p-0">
            <!-- 表格视图 -->
            <div v-if="viewMode === 'table'" class="table-responsive">
              <table class="table table-hover mb-0">
                <thead class="bg-light">
                  <tr>
                    <th width="5%">
                      <input type="checkbox" class="form-check-input" @change="toggleSelectAll">
                    </th>
                    <th width="20%">打包名称</th>
                    <th width="10%">类型</th>
                    <th width="10%">状态</th>
                    <th width="10%">文件数量</th>
                    <th width="10%">文件大小</th>
                    <th width="10%">创建人</th>
                    <th width="10%">创建时间</th>
                    <th width="10%">过期时间</th>
                    <th width="5%">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="pkg in paginatedPackages" :key="pkg.id" class="package-row">
                    <td>
                      <input 
                        type="checkbox" 
                        class="form-check-input" 
                        :value="pkg.id"
                        v-model="selectedPackages"
                      >
                    </td>
                    <td>
                      <div>
                        <div class="fw-medium">{{ pkg.name }}</div>
                        <small class="text-muted">{{ pkg.description }}</small>
                      </div>
                    </td>
                    <td>
                      <span class="badge" :class="getPackageTypeClass(pkg.packageType)">
                        {{ getPackageTypeText(pkg.packageType) }}
                      </span>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        <span class="badge" :class="getStatusClass(pkg.status)">
                          {{ getStatusText(pkg.status) }}
                        </span>
                        <div v-if="pkg.status === 'processing'" class="ms-2">
                          <div class="progress" style="width: 60px; height: 6px;">
                            <div 
                              class="progress-bar" 
                              :style="{ width: pkg.progress + '%' }"
                            ></div>
                          </div>
                          <small class="text-muted">{{ pkg.progress }}%</small>
                        </div>
                      </div>
                    </td>
                    <td>
                      <span class="badge bg-info">{{ pkg.fileCount }}</span>
                    </td>
                    <td>
                      <span class="text-muted">{{ formatFileSize(pkg.fileSize) }}</span>
                    </td>
                    <td>
                      <div class="d-flex align-items-center">
                        <i class="bi bi-person-circle me-2 text-muted"></i>
                        {{ pkg.creator }}
                      </div>
                    </td>
                    <td>
                      <small class="text-muted">{{ formatDate(pkg.createdAt) }}</small>
                    </td>
                    <td>
                      <small class="text-muted" :class="{ 'text-danger': isExpired(pkg.expiresAt) }">
                        {{ formatDate(pkg.expiresAt) }}
                      </small>
                    </td>
                    <td>
                      <div class="btn-group btn-group-sm">
                        <button 
                          class="btn btn-outline-success" 
                          @click="downloadPackage(pkg)" 
                          :disabled="pkg.status !== 'completed'"
                          title="下载"
                        >
                          <i class="bi bi-download"></i>
                        </button>
                        <button 
                          class="btn btn-outline-info" 
                          @click="viewPackageDetails(pkg)" 
                          title="详情"
                        >
                          <i class="bi bi-eye"></i>
                        </button>
                        <button 
                          class="btn btn-outline-danger" 
                          @click="deletePackage(pkg.id)" 
                          title="删除"
                        >
                          <i class="bi bi-trash"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="filteredPackages.length === 0">
                    <td colspan="10" class="text-center py-5">
                      <div class="empty-state">
                        <i class="bi bi-archive fa-3x mb-3 text-secondary opacity-50"></i>
                        <h5 class="text-secondary">暂无打包记录</h5>
                        <p class="text-muted">点击"新建打包"按钮创建第一个打包任务</p>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- 卡片视图 -->
            <div v-else class="row g-3 p-3">
              <div v-for="pkg in paginatedPackages" :key="pkg.id" class="col-md-6 col-lg-4">
                <div class="card h-100 package-card">
                  <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="badge" :class="getPackageTypeClass(pkg.packageType)">
                      {{ getPackageTypeText(pkg.packageType) }}
                    </span>
                    <span class="badge" :class="getStatusClass(pkg.status)">
                      {{ getStatusText(pkg.status) }}
                    </span>
                  </div>
                  <div class="card-body">
                    <h6 class="card-title">{{ pkg.name }}</h6>
                    <p class="card-text text-muted small">{{ pkg.description }}</p>
                    
                    <!-- 处理进度 -->
                    <div v-if="pkg.status === 'processing'" class="mb-3">
                      <div class="d-flex justify-content-between align-items-center mb-1">
                        <small class="text-muted">处理进度</small>
                        <small class="text-muted">{{ pkg.progress }}%</small>
                      </div>
                      <div class="progress" style="height: 6px;">
                        <div 
                          class="progress-bar" 
                          :style="{ width: pkg.progress + '%' }"
                        ></div>
                      </div>
                    </div>
                    
                    <div class="row text-center mb-3">
                      <div class="col-6">
                        <div class="border-end">
                          <div class="fw-bold text-primary">{{ pkg.fileCount }}</div>
                          <small class="text-muted">文件数量</small>
                        </div>
                      </div>
                      <div class="col-6">
                        <div class="fw-bold text-success">{{ formatFileSize(pkg.fileSize) }}</div>
                        <small class="text-muted">文件大小</small>
                      </div>
                    </div>
                    
                    <div class="d-flex justify-content-between align-items-center">
                      <small class="text-muted">
                        <i class="bi bi-person me-1"></i>{{ pkg.creator }}
                      </small>
                      <small class="text-muted">{{ formatDate(pkg.createdAt) }}</small>
                    </div>
                    
                    <div class="mt-2">
                      <small class="text-muted" :class="{ 'text-danger': isExpired(pkg.expiresAt) }">
                        <i class="bi bi-clock me-1"></i>
                        过期时间: {{ formatDate(pkg.expiresAt) }}
                      </small>
                    </div>
                  </div>
                  <div class="card-footer bg-transparent">
                    <div class="btn-group w-100">
                      <button 
                        class="btn btn-outline-success btn-sm" 
                        @click="downloadPackage(pkg)" 
                        :disabled="pkg.status !== 'completed'"
                      >
                        <i class="bi bi-download me-1"></i>下载
                      </button>
                      <button class="btn btn-outline-info btn-sm" @click="viewPackageDetails(pkg)">
                        <i class="bi bi-eye me-1"></i>详情
                      </button>
                      <button class="btn btn-outline-danger btn-sm" @click="deletePackage(pkg.id)">
                        <i class="bi bi-trash me-1"></i>删除
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-if="filteredPackages.length === 0" class="col-12">
                <div class="text-center py-5">
                  <div class="empty-state">
                    <i class="bi bi-archive fa-3x mb-3 text-secondary opacity-50"></i>
                    <h5 class="text-secondary">暂无打包记录</h5>
                    <p class="text-muted">点击"新建打包"按钮创建第一个打包任务</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div v-if="filteredPackages.length > 0" class="card-footer bg-white">
            <div class="d-flex justify-content-between align-items-center">
              <div class="text-muted">
                显示 {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, filteredPackages.length) }} 条，共 {{ filteredPackages.length }} 条记录
              </div>
              <nav>
                <ul class="pagination pagination-sm mb-0">
                  <li class="page-item" :class="{ disabled: currentPage === 1 }">
                    <button class="page-link" @click="currentPage = 1" :disabled="currentPage === 1">
                      <i class="bi bi-chevron-double-left"></i>
                    </button>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage === 1 }">
                    <button class="page-link" @click="currentPage--" :disabled="currentPage === 1">
                      <i class="bi bi-chevron-left"></i>
                    </button>
                  </li>
                  <li 
                    v-for="page in visiblePages" 
                    :key="page" 
                    class="page-item" 
                    :class="{ active: page === currentPage }"
                  >
                    <button class="page-link" @click="currentPage = page">{{ page }}</button>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                    <button class="page-link" @click="currentPage++" :disabled="currentPage === totalPages">
                      <i class="bi bi-chevron-right"></i>
                    </button>
                  </li>
                  <li class="page-item" :class="{ disabled: currentPage === totalPages }">
                    <button class="page-link" @click="currentPage = totalPages" :disabled="currentPage === totalPages">
                      <i class="bi bi-chevron-double-right"></i>
                    </button>
                  </li>
                </ul>
              </nav>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 新建打包模态框 -->
    <div class="modal fade" :class="{ show: showCreatePackageModal }" :style="{ display: showCreatePackageModal ? 'block' : 'none' }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-plus-circle me-2"></i>新建打包任务
            </h5>
            <button type="button" class="btn-close" @click="showCreatePackageModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="createPackage">
              <div class="row g-3">
                <div class="col-12">
                  <label class="form-label">打包名称</label>
                  <input type="text" class="form-control" v-model="packageForm.name" required>
                </div>
                <div class="col-12">
                  <label class="form-label">打包描述</label>
                  <textarea class="form-control" rows="3" v-model="packageForm.description"></textarea>
                </div>
                <div class="col-md-6">
                  <label class="form-label">打包类型</label>
                  <select class="form-select" v-model="packageForm.packageType" required>
                    <option value="">请选择打包类型</option>
                    <option value="case">按案件打包</option>
                    <option value="date">按日期打包</option>
                    <option value="custom">自定义选择</option>
                    <option value="template">模板导出</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label">过期时间</label>
                  <select class="form-select" v-model="packageForm.expiryDays">
                    <option value="7">7天后过期</option>
                    <option value="15">15天后过期</option>
                    <option value="30">30天后过期</option>
                    <option value="90">90天后过期</option>
                  </select>
                </div>
                
                <!-- 按案件打包 -->
                <div v-if="packageForm.packageType === 'case'" class="col-12">
                  <label class="form-label">选择案件</label>
                  <div class="border rounded p-3" style="max-height: 200px; overflow-y: auto;">
                    <div v-for="caseItem in availableCases" :key="caseItem.id" class="form-check">
                      <input 
                        class="form-check-input" 
                        type="checkbox" 
                        :value="caseItem.id" 
                        v-model="packageForm.selectedCases"
                        :id="'case-' + caseItem.id"
                      >
                      <label class="form-check-label" :for="'case-' + caseItem.id">
                        {{ caseItem.caseNumber }} - {{ caseItem.title }}
                      </label>
                    </div>
                  </div>
                </div>
                
                <!-- 按日期打包 -->
                <div v-if="packageForm.packageType === 'date'" class="col-12">
                  <div class="row g-3">
                    <div class="col-md-6">
                      <label class="form-label">开始日期</label>
                      <input type="date" class="form-control" v-model="packageForm.dateFrom">
                    </div>
                    <div class="col-md-6">
                      <label class="form-label">结束日期</label>
                      <input type="date" class="form-control" v-model="packageForm.dateTo">
                    </div>
                  </div>
                </div>
                
                <!-- 自定义选择 -->
                <div v-if="packageForm.packageType === 'custom'" class="col-12">
                  <label class="form-label">选择笔录</label>
                  <div class="border rounded p-3" style="max-height: 200px; overflow-y: auto;">
                    <div v-for="record in availableRecords" :key="record.id" class="form-check">
                      <input 
                        class="form-check-input" 
                        type="checkbox" 
                        :value="record.id" 
                        v-model="packageForm.selectedRecords"
                        :id="'record-' + record.id"
                      >
                      <label class="form-check-label" :for="'record-' + record.id">
                        {{ record.caseNumber }} - {{ record.title }}
                      </label>
                    </div>
                  </div>
                </div>
                
                <!-- 模板导出 -->
                <div v-if="packageForm.packageType === 'template'" class="col-12">
                  <label class="form-label">选择模板</label>
                  <select class="form-select" v-model="packageForm.templateId">
                    <option value="">请选择模板</option>
                    <option value="1">保密问答模板</option>
                    <option value="2">询问笔录模板</option>
                    <option value="3">调查笔录模板</option>
                  </select>
                </div>
                
                <div class="col-12">
                  <div class="form-check">
                    <input class="form-check-input" type="checkbox" v-model="packageForm.includeAttachments" id="includeAttachments">
                    <label class="form-check-label" for="includeAttachments">
                      包含附件文件
                    </label>
                  </div>
                </div>
                
                <div class="col-12">
                  <div class="form-check">
                    <input class="form-check-input" type="checkbox" v-model="packageForm.compressFiles" id="compressFiles">
                    <label class="form-check-label" for="compressFiles">
                      压缩文件（减小文件大小）
                    </label>
                  </div>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showCreatePackageModal = false">取消</button>
            <button type="button" class="btn btn-primary" @click="createPackage">
              <i class="bi bi-check-circle me-2"></i>开始打包
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 模态框背景 -->
    <div v-if="showCreatePackageModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script>
export default {
  name: 'PackageManagement',
  data() {
    return {
      viewMode: 'table',
      currentPage: 1,
      pageSize: 10,
      showCreatePackageModal: false,
      selectedPackages: [],
      searchForm: {
        keyword: '',
        status: '',
        packageType: '',
        creator: '',
        dateFrom: ''
      },
      packageForm: {
        name: '',
        description: '',
        packageType: '',
        expiryDays: '30',
        selectedCases: [],
        selectedRecords: [],
        dateFrom: '',
        dateTo: '',
        templateId: '',
        includeAttachments: true,
        compressFiles: true
      },
      stats: {
        totalPackages: 12,
        completedPackages: 8,
        processingPackages: 2,
        totalSize: 1024 * 1024 * 150 // 150MB
      },
      packages: [
        {
          id: 1,
          name: '2024年1月刑事案件打包',
          description: '包含2024年1月所有刑事案件的笔录文件',
          packageType: 'date',
          status: 'completed',
          fileCount: 25,
          fileSize: 1024 * 1024 * 45, // 45MB
          creator: '李警官',
          createdAt: '2024-01-20 10:30:00',
          expiresAt: '2024-02-19 10:30:00',
          progress: 100,
          downloadUrl: '/downloads/package1.zip'
        },
        {
          id: 2,
          name: 'CASE-2024-001案件资料',
          description: '张某盗窃案相关所有笔录和附件',
          packageType: 'case',
          status: 'processing',
          fileCount: 8,
          fileSize: 1024 * 1024 * 12, // 12MB
          creator: '张警官',
          createdAt: '2024-01-21 14:15:00',
          expiresAt: '2024-02-20 14:15:00',
          progress: 65,
          downloadUrl: null
        },
        {
          id: 3,
          name: '保密问答模板导出',
          description: '所有保密问答模板的导出文件',
          packageType: 'template',
          status: 'completed',
          fileCount: 15,
          fileSize: 1024 * 1024 * 8, // 8MB
          creator: '王警官',
          createdAt: '2024-01-19 16:45:00',
          expiresAt: '2024-02-18 16:45:00',
          progress: 100,
          downloadUrl: '/downloads/package3.zip'
        },
        {
          id: 4,
          name: '自定义笔录选择',
          description: '手动选择的重要笔录文件',
          packageType: 'custom',
          status: 'failed',
          fileCount: 0,
          fileSize: 0,
          creator: '李警官',
          createdAt: '2024-01-18 09:20:00',
          expiresAt: '2024-02-17 09:20:00',
          progress: 0,
          downloadUrl: null
        }
      ],
      availableCases: [
        { id: 1, caseNumber: 'CASE-2024-001', title: '张某涉嫌盗窃案' },
        { id: 2, caseNumber: 'CASE-2024-002', title: '王某交通肇事案' },
        { id: 3, caseNumber: 'CASE-2024-003', title: '刘某诈骗案' }
      ],
      availableRecords: [
        { id: 1, caseNumber: 'CASE-2024-001', title: '张某涉嫌盗窃案询问笔录' },
        { id: 2, caseNumber: 'CASE-2024-002', title: '王某交通肇事案询问笔录' },
        { id: 3, caseNumber: 'CASE-2024-003', title: '刘某诈骗案询问笔录' }
      ]
    }
  },
  computed: {
    filteredPackages() {
      let filtered = this.packages;
      
      if (this.searchForm.keyword) {
        const keyword = this.searchForm.keyword.toLowerCase();
        filtered = filtered.filter(pkg => 
          pkg.name.toLowerCase().includes(keyword) ||
          pkg.description.toLowerCase().includes(keyword)
        );
      }
      
      if (this.searchForm.status) {
        filtered = filtered.filter(pkg => pkg.status === this.searchForm.status);
      }
      
      if (this.searchForm.packageType) {
        filtered = filtered.filter(pkg => pkg.packageType === this.searchForm.packageType);
      }
      
      if (this.searchForm.creator) {
        filtered = filtered.filter(pkg => pkg.creator === this.searchForm.creator);
      }
      
      if (this.searchForm.dateFrom) {
        filtered = filtered.filter(pkg => pkg.createdAt >= this.searchForm.dateFrom);
      }
      
      return filtered;
    },
    totalPages() {
      return Math.ceil(this.filteredPackages.length / this.pageSize);
    },
    paginatedPackages() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredPackages.slice(start, end);
    },
    visiblePages() {
      const pages = [];
      const total = this.totalPages;
      const current = this.currentPage;
      
      if (total <= 7) {
        for (let i = 1; i <= total; i++) {
          pages.push(i);
        }
      } else {
        if (current <= 4) {
          for (let i = 1; i <= 5; i++) {
            pages.push(i);
          }
          pages.push('...');
          pages.push(total);
        } else if (current >= total - 3) {
          pages.push(1);
          pages.push('...');
          for (let i = total - 4; i <= total; i++) {
            pages.push(i);
          }
        } else {
          pages.push(1);
          pages.push('...');
          for (let i = current - 1; i <= current + 1; i++) {
            pages.push(i);
          }
          pages.push('...');
          pages.push(total);
        }
      }
      
      return pages;
    }
  },
  methods: {
    searchPackages() {
      this.currentPage = 1;
      console.log('搜索打包:', this.searchForm);
    },
    createPackage() {
      if (!this.packageForm.name || !this.packageForm.packageType) {
        alert('请填写完整信息');
        return;
      }
      
      // 验证选择项
      if (this.packageForm.packageType === 'case' && this.packageForm.selectedCases.length === 0) {
        alert('请选择至少一个案件');
        return;
      }
      
      if (this.packageForm.packageType === 'custom' && this.packageForm.selectedRecords.length === 0) {
        alert('请选择至少一条笔录');
        return;
      }
      
      if (this.packageForm.packageType === 'date' && (!this.packageForm.dateFrom || !this.packageForm.dateTo)) {
        alert('请选择日期范围');
        return;
      }
      
      if (this.packageForm.packageType === 'template' && !this.packageForm.templateId) {
        alert('请选择模板');
        return;
      }
      
      const expiresAt = new Date();
      expiresAt.setDate(expiresAt.getDate() + parseInt(this.packageForm.expiryDays));
      
      const newPackage = {
        id: Date.now(),
        name: this.packageForm.name,
        description: this.packageForm.description,
        packageType: this.packageForm.packageType,
        status: 'pending',
        fileCount: 0,
        fileSize: 0,
        creator: '当前用户', // 这里应该从用户信息获取
        createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        expiresAt: expiresAt.toISOString().slice(0, 19).replace('T', ' '),
        progress: 0,
        downloadUrl: null
      };
      
      this.packages.unshift(newPackage);
      this.showCreatePackageModal = false;
      this.resetPackageForm();
      
      // 模拟打包过程
      this.simulatePackaging(newPackage.id);
      
      // 更新统计数据
      this.updateStats();
    },
    simulatePackaging(packageId) {
      const pkg = this.packages.find(p => p.id === packageId);
      if (!pkg) return;
      
      pkg.status = 'processing';
      
      const interval = setInterval(() => {
        pkg.progress += Math.random() * 20;
        if (pkg.progress >= 100) {
          pkg.progress = 100;
          pkg.status = 'completed';
          pkg.fileCount = Math.floor(Math.random() * 20) + 5;
          pkg.fileSize = Math.floor(Math.random() * 50 * 1024 * 1024) + 5 * 1024 * 1024; // 5-55MB
          pkg.downloadUrl = `/downloads/package${packageId}.zip`;
          clearInterval(interval);
          this.updateStats();
        }
      }, 1000);
    },
    downloadPackage(pkg) {
      if (pkg.status !== 'completed' || !pkg.downloadUrl) {
        alert('文件尚未准备就绪');
        return;
      }
      
      console.log('下载打包文件:', pkg.name);
      // 这里实现实际的下载逻辑
      alert(`开始下载: ${pkg.name}`);
    },
    viewPackageDetails(pkg) {
      console.log('查看打包详情:', pkg);
      // 这里可以打开详情模态框
    },
    deletePackage(id) {
      if (confirm('确定要删除这个打包吗？删除后将无法恢复！')) {
        const index = this.packages.findIndex(p => p.id === id);
        if (index > -1) {
          this.packages.splice(index, 1);
          this.updateStats();
        }
      }
    },
    batchDownload() {
      const selectedPkgs = this.packages.filter(p => this.selectedPackages.includes(p.id));
      const completedPkgs = selectedPkgs.filter(p => p.status === 'completed');
      
      if (completedPkgs.length === 0) {
        alert('没有可下载的文件');
        return;
      }
      
      console.log('批量下载:', completedPkgs);
      alert(`开始批量下载 ${completedPkgs.length} 个文件`);
    },
    cleanupPackages() {
      const expiredPackages = this.packages.filter(p => this.isExpired(p.expiresAt));
      
      if (expiredPackages.length === 0) {
        alert('没有过期的打包文件');
        return;
      }
      
      if (confirm(`发现 ${expiredPackages.length} 个过期文件，确定要清理吗？`)) {
        expiredPackages.forEach(pkg => {
          const index = this.packages.findIndex(p => p.id === pkg.id);
          if (index > -1) {
            this.packages.splice(index, 1);
          }
        });
        this.updateStats();
        alert('过期文件清理完成');
      }
    },
    toggleSelectAll(event) {
      if (event.target.checked) {
        this.selectedPackages = this.paginatedPackages.map(p => p.id);
      } else {
        this.selectedPackages = [];
      }
    },
    resetPackageForm() {
      this.packageForm = {
        name: '',
        description: '',
        packageType: '',
        expiryDays: '30',
        selectedCases: [],
        selectedRecords: [],
        dateFrom: '',
        dateTo: '',
        templateId: '',
        includeAttachments: true,
        compressFiles: true
      };
    },
    updateStats() {
      this.stats.totalPackages = this.packages.length;
      this.stats.completedPackages = this.packages.filter(p => p.status === 'completed').length;
      this.stats.processingPackages = this.packages.filter(p => p.status === 'processing').length;
      this.stats.totalSize = this.packages.reduce((total, pkg) => total + pkg.fileSize, 0);
    },
    getPackageTypeClass(packageType) {
      const classes = {
        case: 'bg-primary',
        date: 'bg-success',
        custom: 'bg-warning',
        template: 'bg-info'
      };
      return classes[packageType] || 'bg-secondary';
    },
    getPackageTypeText(packageType) {
      const texts = {
        case: '按案件',
        date: '按日期',
        custom: '自定义',
        template: '模板导出'
      };
      return texts[packageType] || '未知';
    },
    getStatusClass(status) {
      const classes = {
        pending: 'bg-secondary',
        processing: 'bg-warning',
        completed: 'bg-success',
        failed: 'bg-danger',
        expired: 'bg-dark'
      };
      return classes[status] || 'bg-secondary';
    },
    getStatusText(status) {
      const texts = {
        pending: '等待中',
        processing: '处理中',
        completed: '已完成',
        failed: '失败',
        expired: '已过期'
      };
      return texts[status] || '未知';
    },
    formatFileSize(bytes) {
      if (bytes === 0) return '0 B';
      const k = 1024;
      const sizes = ['B', 'KB', 'MB', 'GB'];
      const i = Math.floor(Math.log(bytes) / Math.log(k));
      return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    },
    formatDate(dateStr) {
      return new Date(dateStr).toLocaleString('zh-CN');
    },
    isExpired(expiresAt) {
      return new Date(expiresAt) < new Date();
    }
  },
  mounted() {
    console.log('打包管理页面已加载');
    this.updateStats();
  }
}
</script>

<style scoped>
.stats-card {
  transition: all 0.3s ease;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.5rem;
  margin: 0 auto;
}

.package-row {
  transition: all 0.3s ease;
}

.package-row:hover {
  background-color: rgba(13, 110, 253, 0.05);
}

.package-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.package-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.empty-state {
  padding: 2rem;
}

.modal {
  background-color: rgba(0, 0, 0, 0.5);
}

.btn {
  transition: all 0.3s ease;
}

.btn:hover {
  transform: translateY(-1px);
}

.progress {
  background-color: #e9ecef;
}

.progress-bar {
  background-color: #0d6efd;
  transition: width 0.3s ease;
}

.page-link {
  transition: all 0.3s ease;
}

.pagination .page-item.active .page-link {
  background-color: #0d6efd;
  border-color: #0d6efd;
}

.table th {
  font-weight: 600;
  border-bottom: 2px solid #dee2e6;
  background-color: #f8f9fa;
}

.badge {
  font-size: 0.75rem;
}

.input-group-text {
  background-color: #f8f9fa;
  border-color: #dee2e6;
}

.form-control:focus, .form-select:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

.border-end {
  border-right: 1px solid #dee2e6 !important;
}

.text-danger {
  color: #dc3545 !important;
}
</style>