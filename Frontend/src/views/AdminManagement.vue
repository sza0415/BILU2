<template>
  <div class="container-fluid mt-4">
    <!-- 页面标题区域 -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h2 class="display-6 fw-bold text-primary mb-0">
              <i class="bi bi-people me-2"></i>管理员账户
            </h2>
            <p class="text-muted mb-0">管理系统用户和权限设置</p>
          </div>
          <div class="btn-group">
            <button class="btn btn-primary btn-lg" @click="showCreateUserModal = true">
              <i class="bi bi-person-plus me-2"></i>新建用户
            </button>
            <button class="btn btn-success btn-lg" @click="showImportModal = true">
              <i class="bi bi-upload me-2"></i>批量导入
            </button>
            <button class="btn btn-info btn-lg" @click="exportUsers">
              <i class="bi bi-download me-2"></i>导出用户
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
              <i class="bi bi-people"></i>
            </div>
            <h3 class="fw-bold mb-1">{{ stats.totalUsers }}</h3>
            <p class="text-muted mb-0">总用户数</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm stats-card">
          <div class="card-body text-center">
            <div class="stats-icon bg-success bg-opacity-10 text-success mb-3">
              <i class="bi bi-person-check"></i>
            </div>
            <h3 class="fw-bold mb-1">{{ stats.activeUsers }}</h3>
            <p class="text-muted mb-0">活跃用户</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm stats-card">
          <div class="card-body text-center">
            <div class="stats-icon bg-warning bg-opacity-10 text-warning mb-3">
              <i class="bi bi-person-x"></i>
            </div>
            <h3 class="fw-bold mb-1">{{ stats.inactiveUsers }}</h3>
            <p class="text-muted mb-0">禁用用户</p>
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="card border-0 shadow-sm stats-card">
          <div class="card-body text-center">
            <div class="stats-icon bg-info bg-opacity-10 text-info mb-3">
              <i class="bi bi-calendar-day"></i>
            </div>
            <h3 class="fw-bold mb-1">{{ stats.todayLogins }}</h3>
            <p class="text-muted mb-0">今日登录</p>
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
                    placeholder="用户名、姓名、邮箱等"
                    v-model="searchForm.keyword"
                  />
                </div>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">用户角色</label>
                <select class="form-select" v-model="searchForm.role">
                  <option value="">全部角色</option>
                  <option value="admin">管理员</option>
                  <option value="officer">警官</option>
                  <option value="clerk">文员</option>
                  <option value="viewer">查看者</option>
                </select>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">用户状态</label>
                <select class="form-select" v-model="searchForm.status">
                  <option value="">全部状态</option>
                  <option value="active">活跃</option>
                  <option value="inactive">禁用</option>
                  <option value="pending">待激活</option>
                </select>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">部门</label>
                <select class="form-select" v-model="searchForm.department">
                  <option value="">全部部门</option>
                  <option value="刑侦科">刑侦科</option>
                  <option value="交警队">交警队</option>
                  <option value="治安科">治安科</option>
                  <option value="技术科">技术科</option>
                </select>
              </div>
              <div class="col-md-2">
                <label class="form-label fw-bold">创建日期</label>
                <input type="date" class="form-control" v-model="searchForm.dateFrom">
              </div>
              <div class="col-md-1">
                <label class="form-label fw-bold">&nbsp;</label>
                <button class="btn btn-primary w-100" @click="searchUsers">
                  <i class="bi bi-search"></i>
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="row">
      <div class="col-12">
        <div class="card border-0 shadow-sm">
          <div class="card-header bg-white border-0 py-3">
            <div class="d-flex justify-content-between align-items-center">
              <h5 class="fw-bold mb-0">
                <i class="bi bi-list-ul me-2 text-primary"></i>用户列表
                <span class="badge bg-primary ms-2">{{ filteredUsers.length }}</span>
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
                    <th width="10%">头像</th>
                    <th width="15%">用户信息</th>
                    <th width="10%">角色</th>
                    <th width="10%">部门</th>
                    <th width="10%">状态</th>
                    <th width="10%">最后登录</th>
                    <th width="10%">创建时间</th>
                    <th width="20%">操作</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="user in paginatedUsers" :key="user.id" class="user-row">
                    <td>
                      <input 
                        type="checkbox" 
                        class="form-check-input" 
                        :value="user.id"
                        v-model="selectedUsers"
                      >
                    </td>
                    <td>
                      <div class="user-avatar">
                        <img 
                          v-if="user.avatar" 
                          :src="user.avatar" 
                          :alt="user.name"
                          class="rounded-circle"
                          width="40"
                          height="40"
                        >
                        <div v-else class="avatar-placeholder rounded-circle d-flex align-items-center justify-content-center">
                          <i class="bi bi-person"></i>
                        </div>
                      </div>
                    </td>
                    <td>
                      <div>
                        <div class="fw-medium">{{ user.name }}</div>
                        <small class="text-muted">{{ user.username }}</small>
                        <br>
                        <small class="text-muted">{{ user.email }}</small>
                      </div>
                    </td>
                    <td>
                      <span class="badge" :class="getRoleClass(user.role)">
                        {{ getRoleText(user.role) }}
                      </span>
                    </td>
                    <td>
                      <span class="text-muted">{{ user.department }}</span>
                    </td>
                    <td>
                      <span class="badge" :class="getStatusClass(user.status)">
                        {{ getStatusText(user.status) }}
                      </span>
                    </td>
                    <td>
                      <small class="text-muted">{{ formatDate(user.lastLogin) }}</small>
                    </td>
                    <td>
                      <small class="text-muted">{{ formatDate(user.createdAt) }}</small>
                    </td>
                    <td>
                      <div class="btn-group btn-group-sm">
                        <button class="btn btn-outline-primary" @click="viewUser(user)" title="查看详情">
                          <i class="bi bi-eye"></i>
                        </button>
                        <button class="btn btn-outline-success" @click="editUser(user)" title="编辑">
                          <i class="bi bi-pencil"></i>
                        </button>
                        <button 
                          class="btn" 
                          :class="user.status === 'active' ? 'btn-outline-warning' : 'btn-outline-success'"
                          @click="toggleUserStatus(user)"
                          :title="user.status === 'active' ? '禁用' : '启用'"
                        >
                          <i :class="user.status === 'active' ? 'bi bi-person-x' : 'bi bi-person-check'"></i>
                        </button>
                        <button class="btn btn-outline-info" @click="resetPassword(user)" title="重置密码">
                          <i class="bi bi-key"></i>
                        </button>
                        <button class="btn btn-outline-danger" @click="deleteUser(user.id)" title="删除">
                          <i class="bi bi-trash"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                  <tr v-if="filteredUsers.length === 0">
                    <td colspan="9" class="text-center py-5">
                      <div class="empty-state">
                        <i class="bi bi-people fa-3x mb-3 text-secondary opacity-50"></i>
                        <h5 class="text-secondary">暂无用户记录</h5>
                        <p class="text-muted">点击"新建用户"按钮创建第一个用户</p>
                      </div>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- 卡片视图 -->
            <div v-else class="row g-3 p-3">
              <div v-for="user in paginatedUsers" :key="user.id" class="col-md-6 col-lg-4">
                <div class="card h-100 user-card">
                  <div class="card-header d-flex justify-content-between align-items-center">
                    <span class="badge" :class="getRoleClass(user.role)">
                      {{ getRoleText(user.role) }}
                    </span>
                    <span class="badge" :class="getStatusClass(user.status)">
                      {{ getStatusText(user.status) }}
                    </span>
                  </div>
                  <div class="card-body text-center">
                    <div class="user-avatar-large mb-3">
                      <img 
                        v-if="user.avatar" 
                        :src="user.avatar" 
                        :alt="user.name"
                        class="rounded-circle"
                        width="80"
                        height="80"
                      >
                      <div v-else class="avatar-placeholder-large rounded-circle d-flex align-items-center justify-content-center mx-auto">
                        <i class="bi bi-person fa-2x"></i>
                      </div>
                    </div>
                    <h6 class="card-title">{{ user.name }}</h6>
                    <p class="card-text text-muted small">{{ user.username }}</p>
                    <p class="card-text text-muted small">{{ user.email }}</p>
                    <div class="row text-center mb-3">
                      <div class="col-12">
                        <div class="fw-bold text-primary">{{ user.department }}</div>
                        <small class="text-muted">部门</small>
                      </div>
                    </div>
                    <div class="d-flex justify-content-between align-items-center">
                      <small class="text-muted">
                        <i class="bi bi-clock me-1"></i>{{ formatDate(user.lastLogin) }}
                      </small>
                    </div>
                  </div>
                  <div class="card-footer bg-transparent">
                    <div class="btn-group w-100">
                      <button class="btn btn-outline-primary btn-sm" @click="viewUser(user)">
                        <i class="bi bi-eye me-1"></i>详情
                      </button>
                      <button class="btn btn-outline-success btn-sm" @click="editUser(user)">
                        <i class="bi bi-pencil me-1"></i>编辑
                      </button>
                      <button 
                        class="btn btn-sm" 
                        :class="user.status === 'active' ? 'btn-outline-warning' : 'btn-outline-success'"
                        @click="toggleUserStatus(user)"
                      >
                        <i :class="user.status === 'active' ? 'bi bi-person-x' : 'bi bi-person-check'"></i>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
              
              <div v-if="filteredUsers.length === 0" class="col-12">
                <div class="text-center py-5">
                  <div class="empty-state">
                    <i class="bi bi-people fa-3x mb-3 text-secondary opacity-50"></i>
                    <h5 class="text-secondary">暂无用户记录</h5>
                    <p class="text-muted">点击"新建用户"按钮创建第一个用户</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 分页 -->
          <div v-if="filteredUsers.length > 0" class="card-footer bg-white">
            <div class="d-flex justify-content-between align-items-center">
              <div class="text-muted">
                显示 {{ (currentPage - 1) * pageSize + 1 }} - {{ Math.min(currentPage * pageSize, filteredUsers.length) }} 条，共 {{ filteredUsers.length }} 条记录
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

    <!-- 新建用户模态框 -->
    <div class="modal fade" :class="{ show: showCreateUserModal }" :style="{ display: showCreateUserModal ? 'block' : 'none' }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-person-plus me-2"></i>新建用户
            </h5>
            <button type="button" class="btn-close" @click="showCreateUserModal = false"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="createUser">
              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label">用户名</label>
                  <input type="text" class="form-control" v-model="userForm.username" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">姓名</label>
                  <input type="text" class="form-control" v-model="userForm.name" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">邮箱</label>
                  <input type="email" class="form-control" v-model="userForm.email" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">手机号</label>
                  <input type="tel" class="form-control" v-model="userForm.phone">
                </div>
                <div class="col-md-6">
                  <label class="form-label">密码</label>
                  <input type="password" class="form-control" v-model="userForm.password" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">确认密码</label>
                  <input type="password" class="form-control" v-model="userForm.confirmPassword" required>
                </div>
                <div class="col-md-6">
                  <label class="form-label">角色</label>
                  <select class="form-select" v-model="userForm.role" required>
                    <option value="">请选择角色</option>
                    <option value="admin">管理员</option>
                    <option value="officer">警官</option>
                    <option value="clerk">文员</option>
                    <option value="viewer">查看者</option>
                  </select>
                </div>
                <div class="col-md-6">
                  <label class="form-label">部门</label>
                  <select class="form-select" v-model="userForm.department" required>
                    <option value="">请选择部门</option>
                    <option value="刑侦科">刑侦科</option>
                    <option value="交警队">交警队</option>
                    <option value="治安科">治安科</option>
                    <option value="技术科">技术科</option>
                  </select>
                </div>
                <div class="col-12">
                  <label class="form-label">备注</label>
                  <textarea class="form-control" rows="3" v-model="userForm.remarks"></textarea>
                </div>
                <div class="col-12">
                  <div class="form-check">
                    <input class="form-check-input" type="checkbox" v-model="userForm.sendWelcomeEmail" id="sendWelcomeEmail">
                    <label class="form-check-label" for="sendWelcomeEmail">
                      发送欢迎邮件
                    </label>
                  </div>
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showCreateUserModal = false">取消</button>
            <button type="button" class="btn btn-primary" @click="createUser">
              <i class="bi bi-check-circle me-2"></i>保存
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 模态框背景 -->
    <div v-if="showCreateUserModal || showImportModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script>
export default {
  name: 'AdminManagement',
  data() {
    return {
      viewMode: 'table',
      currentPage: 1,
      pageSize: 10,
      showCreateUserModal: false,
      showImportModal: false,
      selectedUsers: [],
      searchForm: {
        keyword: '',
        role: '',
        status: '',
        department: '',
        dateFrom: ''
      },
      userForm: {
        username: '',
        name: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        role: '',
        department: '',
        remarks: '',
        sendWelcomeEmail: true
      },
      stats: {
        totalUsers: 15,
        activeUsers: 12,
        inactiveUsers: 3,
        todayLogins: 8
      },
      users: [
        {
          id: 1,
          username: 'admin',
          name: '系统管理员',
          email: 'admin@police.gov.cn',
          phone: '13800138000',
          role: 'admin',
          department: '技术科',
          status: 'active',
          avatar: null,
          lastLogin: '2024-01-21 09:30:00',
          createdAt: '2024-01-01 10:00:00',
          remarks: '系统超级管理员'
        },
        {
          id: 2,
          username: 'li_officer',
          name: '李警官',
          email: 'li@police.gov.cn',
          phone: '13800138001',
          role: 'officer',
          department: '刑侦科',
          status: 'active',
          avatar: null,
          lastLogin: '2024-01-21 08:45:00',
          createdAt: '2024-01-05 14:20:00',
          remarks: '刑侦科资深警官'
        },
        {
          id: 3,
          username: 'zhang_officer',
          name: '张警官',
          email: 'zhang@police.gov.cn',
          phone: '13800138002',
          role: 'officer',
          department: '交警队',
          status: 'active',
          avatar: null,
          lastLogin: '2024-01-20 16:30:00',
          createdAt: '2024-01-08 11:15:00',
          remarks: '交警队队长'
        },
        {
          id: 4,
          username: 'wang_officer',
          name: '王警官',
          email: 'wang@police.gov.cn',
          phone: '13800138003',
          role: 'officer',
          department: '治安科',
          status: 'active',
          avatar: null,
          lastLogin: '2024-01-21 07:20:00',
          createdAt: '2024-01-10 09:30:00',
          remarks: '治安科副科长'
        },
        {
          id: 5,
          username: 'clerk01',
          name: '文员小刘',
          email: 'clerk01@police.gov.cn',
          phone: '13800138004',
          role: 'clerk',
          department: '刑侦科',
          status: 'inactive',
          avatar: null,
          lastLogin: '2024-01-18 17:00:00',
          createdAt: '2024-01-12 13:45:00',
          remarks: '负责文档整理'
        }
      ]
    }
  },
  computed: {
    filteredUsers() {
      let filtered = this.users;
      
      if (this.searchForm.keyword) {
        const keyword = this.searchForm.keyword.toLowerCase();
        filtered = filtered.filter(user => 
          user.name.toLowerCase().includes(keyword) ||
          user.username.toLowerCase().includes(keyword) ||
          user.email.toLowerCase().includes(keyword)
        );
      }
      
      if (this.searchForm.role) {
        filtered = filtered.filter(user => user.role === this.searchForm.role);
      }
      
      if (this.searchForm.status) {
        filtered = filtered.filter(user => user.status === this.searchForm.status);
      }
      
      if (this.searchForm.department) {
        filtered = filtered.filter(user => user.department === this.searchForm.department);
      }
      
      if (this.searchForm.dateFrom) {
        filtered = filtered.filter(user => user.createdAt >= this.searchForm.dateFrom);
      }
      
      return filtered;
    },
    totalPages() {
      return Math.ceil(this.filteredUsers.length / this.pageSize);
    },
    paginatedUsers() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.filteredUsers.slice(start, end);
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
    searchUsers() {
      this.currentPage = 1;
      console.log('搜索用户:', this.searchForm);
    },
    createUser() {
      if (!this.userForm.username || !this.userForm.name || !this.userForm.email || !this.userForm.password) {
        alert('请填写完整信息');
        return;
      }
      
      if (this.userForm.password !== this.userForm.confirmPassword) {
        alert('两次输入的密码不一致');
        return;
      }
      
      // 检查用户名是否已存在
      if (this.users.some(u => u.username === this.userForm.username)) {
        alert('用户名已存在');
        return;
      }
      
      // 检查邮箱是否已存在
      if (this.users.some(u => u.email === this.userForm.email)) {
        alert('邮箱已存在');
        return;
      }
      
      const newUser = {
        id: Date.now(),
        username: this.userForm.username,
        name: this.userForm.name,
        email: this.userForm.email,
        phone: this.userForm.phone,
        role: this.userForm.role,
        department: this.userForm.department,
        status: 'active',
        avatar: null,
        lastLogin: null,
        createdAt: new Date().toISOString().slice(0, 19).replace('T', ' '),
        remarks: this.userForm.remarks
      };
      
      this.users.unshift(newUser);
      this.showCreateUserModal = false;
      this.resetUserForm();
      
      // 更新统计数据
      this.updateStats();
      
      if (this.userForm.sendWelcomeEmail) {
        console.log('发送欢迎邮件给:', newUser.email);
      }
    },
    viewUser(user) {
      console.log('查看用户详情:', user);
      // 这里可以打开用户详情模态框
    },
    editUser(user) {
      console.log('编辑用户:', user);
      // 这里可以打开编辑模态框
    },
    toggleUserStatus(user) {
      const action = user.status === 'active' ? '禁用' : '启用';
      if (confirm(`确定要${action}用户 ${user.name} 吗？`)) {
        user.status = user.status === 'active' ? 'inactive' : 'active';
        this.updateStats();
      }
    },
    resetPassword(user) {
      if (confirm(`确定要重置用户 ${user.name} 的密码吗？`)) {
        console.log('重置密码:', user);
        alert('密码重置成功，新密码已发送到用户邮箱');
      }
    },
    deleteUser(id) {
      const user = this.users.find(u => u.id === id);
      if (!user) return;
      
      if (confirm(`确定要删除用户 ${user.name} 吗？删除后将无法恢复！`)) {
        const index = this.users.findIndex(u => u.id === id);
        if (index > -1) {
          this.users.splice(index, 1);
          this.updateStats();
        }
      }
    },
    toggleSelectAll(event) {
      if (event.target.checked) {
        this.selectedUsers = this.paginatedUsers.map(u => u.id);
      } else {
        this.selectedUsers = [];
      }
    },
    exportUsers() {
      console.log('导出用户数据');
      alert('用户数据导出功能开发中');
    },
    resetUserForm() {
      this.userForm = {
        username: '',
        name: '',
        email: '',
        phone: '',
        password: '',
        confirmPassword: '',
        role: '',
        department: '',
        remarks: '',
        sendWelcomeEmail: true
      };
    },
    updateStats() {
      this.stats.totalUsers = this.users.length;
      this.stats.activeUsers = this.users.filter(u => u.status === 'active').length;
      this.stats.inactiveUsers = this.users.filter(u => u.status === 'inactive').length;
      
      const today = new Date().toISOString().split('T')[0];
      this.stats.todayLogins = this.users.filter(u => u.lastLogin && u.lastLogin.startsWith(today)).length;
    },
    getRoleClass(role) {
      const classes = {
        admin: 'bg-danger',
        officer: 'bg-primary',
        clerk: 'bg-success',
        viewer: 'bg-info'
      };
      return classes[role] || 'bg-secondary';
    },
    getRoleText(role) {
      const texts = {
        admin: '管理员',
        officer: '警官',
        clerk: '文员',
        viewer: '查看者'
      };
      return texts[role] || '未知';
    },
    getStatusClass(status) {
      const classes = {
        active: 'bg-success',
        inactive: 'bg-warning',
        pending: 'bg-secondary'
      };
      return classes[status] || 'bg-secondary';
    },
    getStatusText(status) {
      const texts = {
        active: '活跃',
        inactive: '禁用',
        pending: '待激活'
      };
      return texts[status] || '未知';
    },
    formatDate(dateStr) {
      if (!dateStr) return '从未登录';
      return new Date(dateStr).toLocaleString('zh-CN');
    }
  },
  mounted() {
    console.log('管理员账户页面已加载');
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

.user-row {
  transition: all 0.3s ease;
}

.user-row:hover {
  background-color: rgba(13, 110, 253, 0.05);
}

.user-card {
  transition: all 0.3s ease;
  cursor: pointer;
}

.user-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.user-avatar {
  width: 40px;
  height: 40px;
}

.user-avatar-large {
  width: 80px;
  height: 80px;
}

.avatar-placeholder {
  width: 40px;
  height: 40px;
  background-color: #f8f9fa;
  color: #6c757d;
  font-size: 1.2rem;
}

.avatar-placeholder-large {
  width: 80px;
  height: 80px;
  background-color: #f8f9fa;
  color: #6c757d;
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

.card-header {
  background-color: rgba(13, 110, 253, 0.05);
}
</style>