<template>
  <div class="record-verify">
    <div class="container-fluid py-3">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-6">
          <div class="card shadow-sm">
            <div class="card-header bg-primary bg-gradient text-white">
              <h5 class="mb-0">
                <i class="bi bi-shield-check me-2"></i>
                笔录验证
              </h5>
            </div>
            <div class="card-body">
              <!-- 验证说明 -->
              <div class="alert alert-info mb-4">
                <i class="bi bi-info-circle-fill me-2"></i>
                <strong>验证说明：</strong> 请上传导出的Word文档(.docx)，系统将验证文档的完整性和真实性。
              </div>
              
              <!-- 验证表单 -->
              <form @submit.prevent="verifyRecord" v-if="!verifying && !verificationResult">
                <!-- 笔录ID显示 -->
                <div class="mb-3">
                  <label class="form-label">笔录ID</label>
                  <input type="text" class="form-control" :value="recordId" readonly>
                </div>
                
                <!-- 文件上传 -->
                <div class="mb-3">
                  <label for="docxFile" class="form-label">Word文档 <span class="text-danger">*</span></label>
                  <input
                    type="file"
                    class="form-control"
                    id="docxFile"
                    @change="handleFileUpload"
                    accept=".docx"
                    required
                  >
                  <div class="form-text">请上传.docx格式的文档</div>
                </div>
                
                <!-- 错误提示 -->
                <div v-if="error" class="alert alert-danger">
                  <i class="bi bi-exclamation-triangle-fill me-2"></i>
                  {{ error }}
                </div>
                
                <!-- 按钮组 -->
                <div class="d-flex justify-content-end gap-2 mt-4">
                  <button type="button" class="btn btn-secondary" @click="cancel">
                    <i class="bi bi-x-circle me-1"></i>取消
                  </button>
                  <button type="submit" class="btn btn-primary" :disabled="!docxFile">
                    <i class="bi bi-shield-check me-1"></i>开始验证
                  </button>
                </div>
              </form>
              
              <!-- 验证中状态 -->
              <div v-if="verifying" class="verification-progress">
                <div class="text-center mb-4">
                  <div class="verification-icon">
                    <i class="bi bi-shield"></i>
                  </div>
                </div>
                
                <div class="progress mb-3">
                  <div
                    class="progress-bar progress-bar-striped progress-bar-animated"
                    role="progressbar"
                    :style="{ width: `${progress}%` }"
                    :aria-valuenow="progress"
                    aria-valuemin="0"
                    aria-valuemax="100"
                  ></div>
                </div>
                
                <p class="text-center mb-4">{{ statusMessage }}</p>
              </div>
              
              <!-- 验证结果 -->
              <div v-if="verificationResult" class="verification-result">
                <div class="text-center mb-4">
                  <div
                    class="verification-icon"
                    :class="{
                      'success-pulse': verificationResult.success,
                      'error-pulse': !verificationResult.success
                    }"
                  >
                    <i
                      class="bi"
                      :class="{
                        'bi-shield-check': verificationResult.success,
                        'bi-shield-x': !verificationResult.success
                      }"
                    ></i>
                  </div>
                </div>
                
                <div
                  class="alert"
                  :class="{
                    'alert-success': verificationResult.success,
                    'alert-danger': !verificationResult.success
                  }"
                >
                  <div class="d-flex align-items-center">
                    <i
                      class="bi me-2"
                      :class="{
                        'bi-check-circle-fill': verificationResult.success,
                        'bi-x-circle-fill': !verificationResult.success
                      }"
                    ></i>
                    <strong>
                      {{ verificationResult.success ? '验证成功' : '验证失败' }}
                    </strong>
                  </div>
                </div>
                
                <!-- 验证详情 -->
                <div class="card mb-3">
                  <div class="card-header bg-light">验证详情</div>
                  <div class="card-body">
                    <div class="mb-2">
                      <strong>笔录名称：</strong> {{ verificationResult.recordName || '未知' }}
                    </div>
                    <div class="mb-2">
                      <strong>询问时间：</strong> {{ formatDateTime(verificationResult.inquiryTime) || '未知' }}
                    </div>
                    <div class="mb-2">
                      <strong>验证状态：</strong>
                      <span
                        class="badge"
                        :class="{
                          'bg-success': verificationResult.success,
                          'bg-danger': !verificationResult.success
                        }"
                      >
                        {{ verificationResult.success ? '完整性验证通过' : '完整性验证失败' }}
                      </span>
                    </div>
                    <div v-if="verificationResult.message">
                      <strong>验证信息：</strong> {{ verificationResult.message }}
                    </div>
                  </div>
                </div>
                
                <!-- 按钮组 -->
                <div class="d-flex justify-content-end gap-2 mt-4">
                  <button type="button" class="btn btn-secondary" @click="cancel">
                    <i class="bi bi-arrow-left me-1"></i>返回
                  </button>
                  <button type="button" class="btn btn-primary" @click="resetVerification">
                    <i class="bi bi-arrow-repeat me-1"></i>重新验证
                  </button>
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
// 假设有一个验证笔录的API
// import { verifyRecord as verifyRecordApi } from '@/api/record'

export default {
  name: 'RecordVerify',
  props: {
    // 笔录ID
    recordId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      docxFile: null,
      verifying: false,
      progress: 0,
      statusMessage: '',
      verificationResult: null,
      error: null
    }
  },
  methods: {
    // 处理文件上传
    handleFileUpload(event) {
      const file = event.target.files[0]
      if (file) {
        // 检查文件类型
        if (!file.name.endsWith('.docx')) {
          this.error = '请上传.docx格式的文档'
          this.docxFile = null
          return
        }
        
        this.docxFile = file
        this.error = null
      }
    },
    
    // 验证笔录
    async verifyRecord() {
      if (!this.docxFile) {
        this.error = '请选择要验证的文档'
        return
      }
      
      this.verifying = true
      this.error = null
      this.progress = 0
      this.statusMessage = '正在准备验证...'
      
      // 模拟进度条
      this.simulateProgress()
      
      try {
        // 创建FormData对象
        const formData = new FormData()
        formData.append('file', this.docxFile)
        
        // 调用验证API
        // const response = await verifyRecordApi(this.recordId, formData)
        
        // 模拟API调用延迟
        await new Promise(resolve => setTimeout(resolve, 3000))
        
        // 模拟验证结果
        const success = Math.random() > 0.3 // 70%概率成功
        
        // 设置验证结果
        this.verificationResult = {
          success,
          recordName: '示例笔录名称',
          inquiryTime: new Date().toISOString(),
          message: success ? '文档完整性验证通过，内容未被篡改' : '文档内容已被修改，验证失败'
        }
        
        // 完成进度条
        this.progress = 100
        this.statusMessage = '验证完成'
      } catch (error) {
        console.error('验证笔录失败:', error)
        this.error = error.message || '验证笔录失败'
        this.verificationResult = {
          success: false,
          message: '验证过程发生错误'
        }
      } finally {
        // 延迟关闭验证中状态，让用户看到100%进度
        setTimeout(() => {
          this.verifying = false
        }, 500)
      }
    },
    
    // 模拟进度条
    simulateProgress() {
      const interval = setInterval(() => {
        if (this.progress >= 90) {
          clearInterval(interval)
          return
        }
        
        // 随机增加进度
        const increment = Math.floor(Math.random() * 10) + 1
        this.progress = Math.min(this.progress + increment, 90)
        
        // 更新状态消息
        if (this.progress < 30) {
          this.statusMessage = '正在读取文档内容...';
        } else if (this.progress < 60) {
          this.statusMessage = '正在验证文档完整性...';
        } else {
          this.statusMessage = '正在比对加密哈希值...';
        }
      }, 300)
    },
    
    // 重置验证
    resetVerification() {
      this.docxFile = null
      this.verifying = false
      this.progress = 0
      this.statusMessage = ''
      this.verificationResult = null
      this.error = null
      
      // 重置文件输入框
      const fileInput = document.getElementById('docxFile')
      if (fileInput) {
        fileInput.value = ''
      }
    },
    
    // 取消验证
    cancel() {
      this.$router.push(`/record/${this.recordId}`)
    },
    
    // 格式化日期时间
    formatDateTime(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      })
    }
  }
}
</script>

<style scoped>
.verification-icon {
  font-size: 4rem;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background-color: #f8f9fa;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  color: #6c757d;
}

.verification-icon.success-pulse {
  animation: success-pulse 2s infinite;
  color: #198754;
}

.verification-icon.error-pulse {
  animation: error-pulse 2s infinite;
  color: #dc3545;
}

@keyframes success-pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(25, 135, 84, 0.7);
  }
  70% {
    box-shadow: 0 0 0 15px rgba(25, 135, 84, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(25, 135, 84, 0);
  }
}

@keyframes error-pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(220, 53, 69, 0.7);
  }
  70% {
    box-shadow: 0 0 0 15px rgba(220, 53, 69, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(220, 53, 69, 0);
  }
}

.progress {
  height: 0.75rem;
  border-radius: 1rem;
}

.progress-bar {
  background-color: #0d6efd;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .verification-icon {
    font-size: 3rem;
    width: 80px;
    height: 80px;
  }
}
</style>