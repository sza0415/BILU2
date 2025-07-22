<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <div class="logo-section">
          <i class="bi bi-journal-text logo-icon"></i>
          <h2 class="system-title">笔录管理系统</h2>
          <p class="system-subtitle">Record Management System</p>
        </div>
      </div>
      
      <div class="login-body">
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username" class="form-label">
              <i class="bi bi-person me-2"></i>用户名
            </label>
            <input 
              type="text" 
              class="form-control" 
              id="username" 
              v-model="loginForm.username"
              placeholder="请输入用户名"
              required
            />
          </div>
          
          <div class="form-group">
            <label for="password" class="form-label">
              <i class="bi bi-lock me-2"></i>密码
            </label>
            <div class="password-input">
              <input 
                :type="showPassword ? 'text' : 'password'" 
                class="form-control" 
                id="password" 
                v-model="loginForm.password"
                placeholder="请输入密码"
                required
              />
              <button 
                type="button" 
                class="password-toggle" 
                @click="showPassword = !showPassword"
              >
                <i :class="showPassword ? 'bi bi-eye-slash' : 'bi bi-eye'"></i>
              </button>
            </div>
          </div>
          
          <div class="form-group">
            <div class="form-check">
              <input 
                class="form-check-input" 
                type="checkbox" 
                id="rememberMe" 
                v-model="loginForm.rememberMe"
              />
              <label class="form-check-label" for="rememberMe">
                记住我
              </label>
            </div>
          </div>
          
          <button type="submit" class="btn btn-primary login-btn" :disabled="isLoading">
            <span v-if="isLoading" class="spinner-border spinner-border-sm me-2"></span>
            <i v-else class="bi bi-box-arrow-in-right me-2"></i>
            {{ isLoading ? '登录中...' : '登录' }}
          </button>
        </form>
      </div>
      
      <div class="login-footer">
        <div class="footer-links">
          <a href="#" class="footer-link">忘记密码？</a>
          <span class="divider">|</span>
          <a href="#" class="footer-link">联系管理员</a>
        </div>
        <div class="copyright">
          <p>&copy; 2024 笔录管理系统. All rights reserved.</p>
        </div>
      </div>
    </div>
    
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="decoration-circle circle-1"></div>
      <div class="decoration-circle circle-2"></div>
      <div class="decoration-circle circle-3"></div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'LoginPage',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        rememberMe: false
      },
      showPassword: false,
      isLoading: false
    }
  },
  methods: {
    async handleLogin() {
      if (!this.loginForm.username || !this.loginForm.password) {
        alert('请填写完整的登录信息');
        return;
      }
      
      this.isLoading = true;
      
      try {
        // 检查是否使用模拟登录（用于开发测试）
        const useMockLogin = this.loginForm.username === 'admin' && this.loginForm.password === 'admin';
        
        if (useMockLogin) {
          // 模拟登录成功
          console.log('使用模拟登录...');
          await new Promise(resolve => setTimeout(resolve, 800)); // 模拟网络延迟
          
          const userInfo = {
            name: this.loginForm.username,
            token: 'mock-token-' + Date.now(),
            roles: ['admin'],
            permissions: ['*']
          };
          
          console.log('模拟登录成功，用户信息:', userInfo);
          localStorage.setItem('userInfo', JSON.stringify(userInfo));
          
          // 跳转到首页
          this.$router.push('/');
          return;
        }
        
        // 调用真实的登录API
        const response = await this.$axios.post('/login', {
          username: this.loginForm.username,
          password: this.loginForm.password,
          rememberMe: this.loginForm.rememberMe
        });
        
        // 处理登录响应
        if (response.data && response.data.code === 200) {
          // 保存用户信息到本地存储
          const userInfo = {
            name: response.data.data.username || this.loginForm.username,
            token: response.data.data.token || 'token-' + Date.now(),
            // 可以添加其他用户信息
            roles: response.data.data.roles || [],
            permissions: response.data.data.permissions || []
          };
          
          console.log('登录成功，用户信息:', userInfo);
          localStorage.setItem('userInfo', JSON.stringify(userInfo));
          
          // 跳转到首页
          this.$router.push('/');
        } else {
          alert('登录失败: ' + (response.data.message || '用户名或密码错误'));
        }
      } catch (error) {
        console.error('登录失败:', error);
        // 如果后端返回了错误信息，显示给用户
        const errorMsg = error.response?.data?.message || '登录失败，请稍后重试';
        alert(errorMsg);
      } finally {
        this.isLoading = false;
      }
    }
  },
  mounted() {
    // 检查是否已经登录
    const userInfo = localStorage.getItem('userInfo');
    if (userInfo) {
      this.$router.push('/');
    }
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  position: relative;
  overflow: hidden;
}

.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  width: 100%;
  max-width: 400px;
  padding: 0;
  overflow: hidden;
  position: relative;
  z-index: 10;
  animation: slideUp 0.6s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-header {
  background: linear-gradient(135deg, #0099ff 0%, #0066cc 100%);
  color: white;
  padding: 2rem;
  text-align: center;
}

.logo-section {
  animation: fadeIn 0.8s ease-out 0.2s both;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.logo-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  display: block;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
}

.system-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  letter-spacing: 1px;
}

.system-subtitle {
  font-size: 0.9rem;
  opacity: 0.9;
  margin: 0;
  font-weight: 300;
}

.login-body {
  padding: 2rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-label {
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
}

.form-control {
  border: 2px solid #e9ecef;
  border-radius: 10px;
  padding: 0.75rem 1rem;
  font-size: 1rem;
  transition: all 0.3s ease;
  background-color: #f8f9fa;
}

.form-control:focus {
  border-color: #0099ff;
  box-shadow: 0 0 0 0.2rem rgba(0, 153, 255, 0.25);
  background-color: white;
}

.password-input {
  position: relative;
}

.password-toggle {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: #6c757d;
  cursor: pointer;
  padding: 0.5rem;
  transition: color 0.3s ease;
}

.password-toggle:hover {
  color: #0099ff;
}

.form-check {
  display: flex;
  align-items: center;
}

.form-check-input {
  margin-right: 0.5rem;
}

.form-check-label {
  color: #6c757d;
  font-size: 0.9rem;
}

.login-btn {
  width: 100%;
  padding: 0.75rem;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #0099ff 0%, #0066cc 100%);
  border: none;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 153, 255, 0.3);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.login-footer {
  padding: 1.5rem 2rem;
  background-color: #f8f9fa;
  text-align: center;
}

.footer-links {
  margin-bottom: 1rem;
}

.footer-link {
  color: #6c757d;
  text-decoration: none;
  font-size: 0.9rem;
  transition: color 0.3s ease;
}

.footer-link:hover {
  color: #0099ff;
  text-decoration: underline;
}

.divider {
  margin: 0 1rem;
  color: #dee2e6;
}

.copyright {
  color: #adb5bd;
  font-size: 0.8rem;
  margin: 0;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.decoration-circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: -50px;
  animation-delay: 0s;
}

.circle-2 {
  width: 150px;
  height: 150px;
  top: 60%;
  right: -30px;
  animation-delay: 2s;
}

.circle-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-container {
    padding: 10px;
  }
  
  .login-card {
    margin: 0;
  }
  
  .login-header {
    padding: 1.5rem;
  }
  
  .system-title {
    font-size: 1.5rem;
  }
  
  .login-body {
    padding: 1.5rem;
  }
}
</style>