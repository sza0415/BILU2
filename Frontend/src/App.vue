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
            <li class="nav-item">
              <router-link class="nav-link deepseek-review-link" to="/deepseek-review" active-class="active">
                <i class="bi bi-robot deepseek-icon"></i>DeepSeek点评
                <span class="deepseek-badge">AI</span>
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
              <a class="nav-link exit-system-btn" href="#" @click="exitSystem">
                <i class="bi bi-power"></i>
                <span>退出系统</span>
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
// 导入Bootstrap的JavaScript
import 'bootstrap/dist/js/bootstrap.bundle.min.js';

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
    
    // 添加滚动事件监听，实现导航栏滚动效果
    window.addEventListener('scroll', () => {
      const navbar = document.querySelector('.navbar');
      if (window.scrollY > 50) {
        navbar.style.padding = '8px 0';
        navbar.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.3)';
      } else {
        navbar.style.padding = '12px 0';
        navbar.style.boxShadow = '0 8px 32px rgba(0, 0, 0, 0.2)';
      }
    });
  },
  beforeUnmount() {
    // 移除滚动事件监听
    // 由于我们使用了匿名函数，这里不能直接移除
    // 在实际项目中，最好将事件处理函数定义为类方法以便正确移除
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

/* 导航栏基本样式 - 现代玻璃态效果 */
.navbar {
  background: linear-gradient(135deg, rgba(41, 128, 185, 0.95), rgba(52, 152, 219, 0.9));
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  padding: 12px 0;
  position: sticky;
  top: 0;
  z-index: 1000;
  transition: all 0.4s ease;
}

/* 设置导航栏文字颜色和悬停效果 */
.navbar .nav-link, .navbar .navbar-brand {
  color: rgba(255, 255, 255, 0.9) !important;
  position: relative;
  transition: all 0.3s ease;
  font-weight: 500;
  padding: 0.6rem 1rem;
  margin: 0 0.2rem;
  border-radius: 8px;
  letter-spacing: 0.5px;
}

/* 导航链接悬停效果 */
.navbar .nav-link:hover, .navbar .navbar-brand:hover {
  color: #ffffff !important;
  background-color: rgba(255, 255, 255, 0.15);
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

/* 激活链接样式 */
.navbar .nav-link.active {
  color: #ffffff !important;
  font-weight: 600;
  background-color: rgba(255, 255, 255, 0.2);
  box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.1);
}

/* 添加底部指示器 */
.navbar .nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #ffffff, rgba(255, 255, 255, 0.5));
  transition: all 0.3s ease;
  transform: translateX(-50%);
  opacity: 0;
  border-radius: 3px;
}

.navbar .nav-link:hover::after,
.navbar .nav-link.active::after {
  width: 70%;
  opacity: 1;
}

/* 设置下拉菜单的背景颜色和边框效果 */
.navbar .dropdown-menu {
  background: rgba(30, 136, 229, 0.85);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.25);
  margin-top: 15px;
  overflow: hidden;
  padding: 10px;
  transform-origin: top center;
  animation: dropdown-appear 0.3s cubic-bezier(0.68, -0.55, 0.27, 1.55) forwards;
  position: relative;
}

@keyframes dropdown-appear {
  from {
    opacity: 0;
    transform: translateY(-10px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.navbar .dropdown-menu::before {
  content: '';
  position: absolute;
  top: -8px;
  left: 20px;
  width: 16px;
  height: 16px;
  background: rgba(30, 136, 229, 0.85);
  transform: rotate(45deg);
  border-top: 1px solid rgba(255, 255, 255, 0.15);
  border-left: 1px solid rgba(255, 255, 255, 0.15);
  z-index: -1;
}

.navbar .dropdown-menu a {
  color: white !important;
  padding: 12px 20px;
  transition: all 0.3s ease;
  border-radius: 10px;
  margin: 5px 0;
  position: relative;
  overflow: hidden;
}

/* 下拉菜单项悬停效果 */
.navbar .dropdown-menu a:hover {
  background-color: rgba(255, 255, 255, 0.15);
  transform: translateX(5px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.navbar .dropdown-menu a::after {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: 0.5s;
}

.navbar .dropdown-menu a:hover::after {
  left: 100%;
}

/* 设置下拉菜单分隔线的颜色 */
.navbar .dropdown-divider {
  border-color: rgba(255, 255, 255, 0.15);
  margin: 10px 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

/* 调整图标大小和颜色 */
.navbar .bi {
  font-size: 1.5rem;
  vertical-align: middle;
  margin-right: 12px;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 2.2rem;
  height: 2.2rem;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

/* 图标悬停效果 */
.navbar .nav-link:hover .bi, .navbar .dropdown-item:hover .bi {
  transform: scale(1.15) rotate(5deg);
  background: rgba(255, 255, 255, 0.2);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

/* 设置不同图标的颜色和特效 */
.navbar .bi-journal-text {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(255, 215, 0, 0.6));
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.2), rgba(255, 165, 0, 0.1));
}

.navbar .bi-house-door {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(152, 251, 152, 0.6));
  background: linear-gradient(135deg, rgba(152, 251, 152, 0.2), rgba(46, 204, 113, 0.1));
}

.navbar .bi-pencil-square {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(255, 99, 71, 0.6));
  background: linear-gradient(135deg, rgba(255, 99, 71, 0.2), rgba(231, 76, 60, 0.1));
}

.navbar .bi-search {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(52, 152, 219, 0.6));
  background: linear-gradient(135deg, rgba(52, 152, 219, 0.2), rgba(41, 128, 185, 0.1));
}

.navbar .bi-file-text {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(155, 89, 182, 0.6));
  background: linear-gradient(135deg, rgba(155, 89, 182, 0.2), rgba(142, 68, 173, 0.1));
}

.navbar .bi-folder2-open {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(241, 196, 15, 0.6));
  background: linear-gradient(135deg, rgba(241, 196, 15, 0.2), rgba(243, 156, 18, 0.1));
}

.navbar .bi-gear {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(189, 195, 199, 0.6));
  background: linear-gradient(135deg, rgba(189, 195, 199, 0.2), rgba(127, 140, 141, 0.1));
}

.navbar .bi-archive {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(230, 126, 34, 0.6));
  background: linear-gradient(135deg, rgba(230, 126, 34, 0.2), rgba(211, 84, 0, 0.1));
}

.navbar .bi-person-circle {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(255, 218, 185, 0.6));
  background: linear-gradient(135deg, rgba(255, 218, 185, 0.2), rgba(240, 147, 43, 0.1));
}

.navbar .dropdown-menu .bi {
  font-size: 1.2rem;
  width: 1.8rem;
  height: 1.8rem;
  margin-right: 10px;
}

.navbar .bi-box-arrow-in-right {
  color: #f7f7f7;
  filter: drop-shadow(0 0 4px rgba(152, 251, 152, 0.6));
  background: linear-gradient(135deg, rgba(46, 204, 113, 0.2), rgba(39, 174, 96, 0.1));
}

.navbar .bi-box-arrow-right {
  color: #ffffff;
  filter: drop-shadow(0 0 4px rgba(255, 99, 71, 0.6));
  background: linear-gradient(135deg, rgba(231, 76, 60, 0.2), rgba(192, 57, 43, 0.1));
}

.navbar .bi-shield-lock {
  color: #fcfcfc;
  filter: drop-shadow(0 0 4px rgba(255, 215, 0, 0.6));
  background: linear-gradient(135deg, rgba(241, 196, 15, 0.2), rgba(243, 156, 18, 0.1));
}

.navbar .bi-power {
  color: #ffffff;
  filter: drop-shadow(0 0 5px rgba(231, 76, 60, 0.7));
  background: linear-gradient(135deg, rgba(231, 76, 60, 0.3), rgba(192, 57, 43, 0.2));
  animation: pulse-power 2s infinite;
}

@keyframes pulse-power {
  0% {
    box-shadow: 0 0 0 0 rgba(231, 76, 60, 0.4);
  }
  70% {
    box-shadow: 0 0 0 10px rgba(231, 76, 60, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(231, 76, 60, 0);
  }
}

/* 调整文字位置以适应更大的图标 */
.navbar .nav-link, .navbar .navbar-brand, .navbar .dropdown-menu a {
  display: flex;
  align-items: center;
}

/* 导航栏品牌名称样式 */
.navbar .navbar-brand {
  font-size: 1.8rem;
  font-weight: 700;
  letter-spacing: 1.2px;
  padding: 0.5rem 1.5rem;
  margin-right: 1.5rem;
  position: relative;
  background: linear-gradient(135deg, rgba(25, 118, 210, 0.8), rgba(3, 169, 244, 0.6));
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
  border: 1px solid rgba(255, 255, 255, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.navbar .navbar-brand::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: 0.5s;
}

.navbar .navbar-brand:hover::before {
  left: 100%;
}

/* 退出系统按钮样式 */
.exit-system-btn {
  background: linear-gradient(135deg, #f44336, #d32f2f);
  border-radius: 30px;
  padding: 8px 20px !important;
  margin-left: 15px;
  box-shadow: 0 4px 15px rgba(244, 67, 54, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease !important;
  position: relative;
  overflow: hidden;
}

.exit-system-btn span {
  font-weight: 600;
  letter-spacing: 0.5px;
}

.exit-system-btn:hover {
  transform: translateY(-3px) !important;
  box-shadow: 0 6px 20px rgba(244, 67, 54, 0.4);
  background: linear-gradient(135deg, #d32f2f, #f44336);
}

.exit-system-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: 0.5s;
}

.exit-system-btn:hover::before {
  left: 100%;
}

/* 添加响应式调整 */
@media (max-width: 992px) {
  .navbar {
    padding: 10px 15px;
  }
  
  .navbar .navbar-brand {
    border-right: none;
    margin-right: 0;
    padding: 0.4rem 1rem;
  }

  .navbar .nav-link {
    padding: 0.5rem 0.8rem;
    margin: 0.2rem 0;
  }

  .navbar .nav-link:hover {
    transform: none;
  }
  
  .navbar .bi {
    font-size: 1.3rem;
    width: 1.8rem;
    height: 1.8rem;
  }
  
  .exit-system-btn {
    margin: 10px 0;
    text-align: center;
    justify-content: center;
  }
  
  .navbar .dropdown-menu::before {
    display: none;
  }
}

/* 移动设备额外调整 */
@media (max-width: 576px) {
  .navbar .navbar-brand {
    font-size: 1.4rem;
  }
  
  .navbar .bi {
    font-size: 1.2rem;
    width: 1.6rem;
    height: 1.6rem;
    margin-right: 8px;
  }
  
  .navbar .nav-link {
    font-size: 0.9rem;
  }
}

.main-content {
  min-height: calc(100vh - 76px);
}

/* DeepSeek点评 样式隔离 - 防止退出系统按钮红色影响 */
.deepseek-review-link,
.deepseek-review-link:link,
.deepseek-review-link:visited,
.deepseek-review-link:hover,
.deepseek-review-link:active,
.deepseek-review-link:focus,
.deepseek-review-link.active {
  border: 0 !important;
  outline: 0 !important;
  box-shadow: none !important;
  text-decoration: none !important;
  background: transparent !important;
  color: rgba(255, 255, 255, 0.9) !important;
  z-index: 10 !important;
  isolation: isolate !important;
}

.deepseek-review-link *,
.deepseek-review-link *:before,
.deepseek-review-link *:after {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
  background-color: transparent !important;
  isolation: isolate !important;
}

/* 确保DeepSeek图标和徽章不受红色影响 */
.deepseek-icon,
.deepseek-badge {
  isolation: isolate !important;
  z-index: 11 !important;
}

.deepseek-review-link {
  position: relative;
  transition: all 0.3s ease;
}

.deepseek-icon {
  font-size: 1.5rem !important;
  width: 2.2rem !important;
  height: 2.2rem !important;
  background: linear-gradient(135deg, #10B981, #3B82F6, #8B5CF6) !important;
  color: #ffffff !important;
  border-radius: 50% !important;
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
  box-shadow: 0 4px 8px rgba(16, 185, 129, 0.1) !important;
  border: none !important;
  outline: none !important;
  transition: all 0.3s ease !important;
  margin-right: 12px !important;
  vertical-align: middle !important;
}

.deepseek-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: linear-gradient(135deg, #10B981, #3B82F6);
  color: white;
  font-size: 0.6rem;
  padding: 1px 4px;
  border-radius: 6px;
  font-weight: 600;
  box-shadow: 0 2px 6px rgba(16, 185, 129, 0.4);
  transition: all 0.3s ease;
  z-index: 3;
  border: none !important;
  outline: none !important;
}

/* 悬停效果 */
.deepseek-review-link:hover .deepseek-icon {
  transform: scale(1.15) rotate(5deg) !important;
  box-shadow: 0 6px 15px rgba(16, 185, 129, 0.4) !important;
  filter: brightness(1.1) !important;
}

.deepseek-review-link:hover .deepseek-badge {
  transform: scale(1.1) rotate(-3deg);
  box-shadow: 0 3px 10px rgba(16, 185, 129, 0.6);
}

/* 激活状态 */
.deepseek-review-link.active .deepseek-icon {
  box-shadow: 0 0 15px rgba(16, 185, 129, 0.6) !important;
  animation: deepseek-pulse 2s ease-in-out infinite;
  filter: brightness(1.2) !important;
}

@keyframes deepseek-pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

.deepseek-review-link.active .deepseek-badge {
  animation: deepseek-badge-glow 2s ease-in-out infinite;
}

@keyframes deepseek-badge-glow {
  0%, 100% {
    box-shadow: 0 2px 6px rgba(16, 185, 129, 0.4);
  }
  50% {
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.8);
  }
}

/* 响应式调整 */
@media (max-width: 992px) {
  .deepseek-icon {
    font-size: 1.3rem !important;
    width: 1.8rem !important;
    height: 1.8rem !important;
    margin-right: 8px !important;
  }
  
  .deepseek-badge {
    font-size: 0.55rem;
    padding: 1px 3px;
  }
}

@media (max-width: 576px) {
  .deepseek-icon {
    font-size: 1.2rem !important;
    width: 1.6rem !important;
    height: 1.6rem !important;
  }
  
  .deepseek-badge {
    font-size: 0.5rem;
    padding: 0px 2px;
  }
}

@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}
</style>
