<template>
  <div id="app">
    <!-- 导航栏 -->
    <nav class="navbar navbar-expand-lg">
      <div class="container-fluid">
        <a class="navbar-brand" href="#">
          <i class="bi bi-journal-text"></i>笔录系统
        </a>
        <button 
          class="navbar-toggler" 
          type="button" 
          data-bs-toggle="collapse" 
          data-bs-target="#navbarSupportedContent" 
          aria-controls="navbarSupportedContent" 
          aria-expanded="false" 
          aria-label="Toggle navigation"
        >
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarSupportedContent">
          <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            <li class="nav-item">
              <router-link class="nav-link" to="/" active-class="active">
                <i class="bi bi-house-door"></i>首页
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/task" active-class="active">
                <i class="bi bi-pencil-square"></i>笔录页面
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/search" active-class="active">
                <i class="bi bi-search"></i>笔录搜索
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/record" active-class="active">
                <i class="bi bi-file-text"></i>笔录管理
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/cases" active-class="active">
                <i class="bi bi-folder2-open"></i>案件管理
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/templates" active-class="active">
                <i class="bi bi-gear"></i>模版管理
              </router-link>
            </li>
            <li class="nav-item">
              <router-link class="nav-link" to="/package" active-class="active">
                <i class="bi bi-archive"></i>打包管理
              </router-link>
            </li>
            <li class="nav-item dropdown">
              <a 
                class="nav-link dropdown-toggle" 
                href="#" 
                role="button" 
                data-bs-toggle="dropdown" 
                aria-expanded="false"
              >
                <i class="bi bi-person-circle"></i>
                {{ userInfo.name || '请登录' }}
              </a>
              <ul class="dropdown-menu">
                <li v-if="!userInfo.name">
                  <router-link class="dropdown-item" to="/login">
                    <i class="bi bi-box-arrow-in-right"></i>登录
                  </router-link>
                </li>
                <li v-else>
                  <a class="dropdown-item" href="#" @click="logout">
                    <i class="bi bi-box-arrow-right"></i>注销
                  </a>
                </li>
                <li><hr class="dropdown-divider"></li>
                <li>
                  <router-link class="dropdown-item" to="/admin">
                    <i class="bi bi-shield-lock"></i>管理员账户
                  </router-link>
                </li>
              </ul>
            </li>
          </ul>
          
          <!-- 退出系统按钮 -->
          <ul class="navbar-nav ms-auto">
            <li class="nav-item">
              <a class="nav-link" href="#" @click="exitSystem" style="color: white; font-weight: 500;">
                <i class="bi bi-power" style="color: rgb(255, 255, 255); font-size: 1.5rem; margin-right: 5px;"></i>退出系统
              </a>
            </li>
          </ul>
        </div>
      </div>
    </nav>

    <!-- 路由视图 -->
    <div class="main-content">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      userInfo: {
        name: ''
      }
    }
  },
  methods: {
    logout() {
      if (confirm('确定要注销吗？')) {
        // 清除本地存储的用户信息
        localStorage.removeItem('userInfo');
        // 重置用户信息
        this.userInfo = { name: '' };
        // 跳转到登录页面
        this.$router.push('/login');
      }
    },
    exitSystem() {
      if (confirm('确定要退出系统吗？')) {
        // 清除本地存储的用户信息
        localStorage.removeItem('userInfo');
        // 这里可以调用后端接口关闭系统
        window.close();
      }
    },
    // 从localStorage获取用户信息
    loadUserInfo() {
      const storedInfo = localStorage.getItem('userInfo');
      if (storedInfo) {
        try {
          const parsedInfo = JSON.parse(storedInfo);
          this.userInfo = parsedInfo;
        } catch (error) {
          console.error('解析用户信息失败:', error);
          // 如果解析失败，清除可能损坏的数据
          localStorage.removeItem('userInfo');
        }
      }
    }
  },
  mounted() {
    // 组件挂载时加载用户信息
    this.loadUserInfo();
  }
}
</script>

<style>
/* 导入Bootstrap CSS */
@import url('https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css');
@import url('https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css');

#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 导航栏基本样式 */
.navbar {
  background: rgb(0, 153, 255);
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
  padding: 10px 0;
}

/* 设置导航栏文字颜色和悬停效果 */
.navbar .nav-link, .navbar .navbar-brand {
  color: white !important;
  position: relative;
  transition: all 0.3s ease;
  font-weight: 500;
}

/* 导航链接悬停效果 */
.navbar .nav-link:hover, .navbar .navbar-brand:hover {
  color: #ffffff !important;
  transform: translateY(-2px);
}

/* 激活链接样式 */
.navbar .nav-link.active {
  color: #ffffff !important;
  font-weight: 600;
}

/* 设置下拉菜单的背景颜色和边框效果 */
.navbar .dropdown-menu {
  background: linear-gradient(to bottom, #1a2a6c, #2d4284);
  border-radius: 8px;
  border: none;
  box-shadow: 0 8px 16px rgba(0,0,0,0.2);
  margin-top: 10px;
  overflow: hidden;
}

.navbar .dropdown-menu a {
  color: white !important;
  padding: 10px 20px;
  transition: all 0.3s ease;
}

/* 下拉菜单项悬停效果 */
.navbar .dropdown-menu a:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateX(5px);
}

/* 设置下拉菜单分隔线的颜色 */
.navbar .dropdown-divider {
  border-color: rgba(255, 255, 255, 0.2);
  margin: 8px 0;
}

/* 调整图标大小和颜色 */
.navbar .bi {
  font-size: 1.6rem;
  vertical-align: middle;
  margin-right: 10px;
  transition: all 0.3s ease;
}

/* 图标悬停效果 */
.navbar .nav-link:hover .bi, .navbar .dropdown-item:hover .bi {
  transform: scale(1.2);
}

/* 设置不同图标的颜色 */
.navbar .bi-journal-text {
  color: #ffffff;
  filter: drop-shadow(0 0 3px rgba(255, 215, 0, 0.5));
}

.navbar .bi-house-door {
  color: #ffffff;
  filter: drop-shadow(0 0 3px rgba(152, 251, 152, 0.5));
}

.navbar .bi-pencil-square {
  color: #ffffff;
  filter: drop-shadow(0 0 3px rgba(255, 99, 71, 0.5));
}

.navbar .bi-clipboard-data {
  color: #ffffff;
  filter: drop-shadow(0 0 3px rgba(64, 224, 208, 0.5));
}

.navbar .bi-person-circle {
  color: #ffffff;
  filter: drop-shadow(0 0 3px rgba(255, 218, 185, 0.5));
}

.navbar .bi-gear {
  color: #ffffff;
  filter: drop-shadow(0 0 3px rgba(192, 192, 192, 0.5));
}

.navbar .dropdown-menu .bi {
  font-size: 1.3rem;
}

.navbar .bi-box-arrow-in-right {
  color: #f7f7f7;
  filter: drop-shadow(0 0 2px rgba(152, 251, 152, 0.5));
}

.navbar .bi-box-arrow-right {
  color: #ffffff;
  filter: drop-shadow(0 0 2px rgba(255, 99, 71, 0.5));
}

.navbar .bi-shield-lock {
  color: #fcfcfc;
  filter: drop-shadow(0 0 2px rgba(255, 215, 0, 0.5));
}

/* 调整文字位置以适应更大的图标 */
.navbar .nav-link, .navbar .navbar-brand, .navbar .dropdown-menu a {
  display: flex;
  align-items: center;
}

/* 导航栏品牌名称样式 */
.navbar .navbar-brand {
  font-size: 1.6rem;
  font-weight: 700;
  letter-spacing: 1px;
  padding: 0 15px;
  border-right: 1px solid rgba(255, 255, 255, 0.2);
  margin-right: 20px;
}

/* 添加响应式调整 */
@media (max-width: 992px) {
  .navbar .navbar-brand {
    border-right: none;
  }

  .navbar .nav-link:hover {
    transform: none;
  }
}

.main-content {
  min-height: calc(100vh - 76px);
}
</style>
