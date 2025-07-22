<template>
  <div class="record-edit">
    <div class="container-fluid py-3">
      <!-- 加载状态 -->
      <div v-if="loading" class="d-flex justify-content-center align-items-center" style="min-height: 300px;">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">加载中...</span>
        </div>
      </div>
      
      <!-- 编辑表单 -->
      <div v-else class="row justify-content-center">
        <div class="col-md-10 col-lg-8">
          <div class="card shadow-sm">
            <div class="card-header bg-primary bg-gradient text-white">
              <h5 class="mb-0">
                <i class="bi bi-pencil-square me-2"></i>
                {{ isEdit ? '编辑笔录' : '新建笔录' }}
              </h5>
            </div>
            <div class="card-body">
              <!-- 加密警告 -->
              <div v-if="isEdit && record.isEncrypted" class="alert alert-warning">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>
                <strong>警告：</strong> 此笔录已加密保护，编辑后将移除加密哈希值，可能影响验证结果。
              </div>
              
              <form @submit.prevent="saveRecord">
                <!-- 笔录名称 -->
                <div class="mb-3">
                  <label for="recordName" class="form-label">笔录名称 <span class="text-danger">*</span></label>
                  <input
                    type="text"
                    class="form-control"
                    id="recordName"
                    v-model="record.recordName"
                    required
                    placeholder="请输入笔录名称"
                  >
                </div>
                
                <div class="row">
                  <!-- 询问时间 -->
                  <div class="col-md-6 mb-3">
                    <label for="inquiryTime" class="form-label">询问时间 <span class="text-danger">*</span></label>
                    <input
                      type="datetime-local"
                      class="form-control"
                      id="inquiryTime"
                      v-model="record.inquiryTime"
                      required
                    >
                  </div>
                  
                  <!-- 询问地点 -->
                  <div class="col-md-6 mb-3">
                    <label for="inquiryAddress" class="form-label">询问地点 <span class="text-danger">*</span></label>
                    <input
                      type="text"
                      class="form-control"
                      id="inquiryAddress"
                      v-model="record.inquiryAddress"
                      required
                      placeholder="请输入询问地点"
                    >
                  </div>
                </div>
                
                <div class="row">
                  <!-- 询问员 -->
                  <div class="col-md-6 mb-3">
                    <label for="assistantName" class="form-label">询问员 <span class="text-danger">*</span></label>
                    <input
                      type="text"
                      class="form-control"
                      id="assistantName"
                      v-model="record.assistantName"
                      required
                      placeholder="请输入询问员姓名"
                    >
                  </div>
                  
                  <!-- 被询问人 -->
                  <div class="col-md-6 mb-3">
                    <label for="inquirerName" class="form-label">被询问人 <span class="text-danger">*</span></label>
                    <input
                      type="text"
                      class="form-control"
                      id="inquirerName"
                      v-model="record.inquirerName"
                      required
                      placeholder="请输入被询问人姓名"
                    >
                  </div>
                </div>
                
                <!-- 关联案件 -->
                <div class="mb-3">
                  <label for="caseId" class="form-label">关联案件</label>
                  <select class="form-select" id="caseId" v-model="record.caseId">
                    <option value="">无关联案件</option>
                    <option v-for="caseItem in caseList" :key="caseItem.id" :value="caseItem.id">
                      {{ caseItem.caseName || caseItem.id }}
                    </option>
                  </select>
                </div>
                
                <!-- 笔录内容 -->
                <div class="mb-3">
                  <label for="recordContent" class="form-label">笔录内容 <span class="text-danger">*</span></label>
                  <textarea
                    class="form-control"
                    id="recordContent"
                    v-model="record.recordContent"
                    rows="10"
                    required
                    placeholder="请输入笔录内容"
                  ></textarea>
                </div>
                
                <!-- 视频文件上传 -->
                <div class="mb-3">
                  <label for="videoFile" class="form-label">视频文件</label>
                  <input
                    type="file"
                    class="form-control"
                    id="videoFile"
                    @change="handleFileUpload"
                    accept="video/mp4,video/avi,video/webm"
                  >
                  <div class="form-text" v-if="record.videoPath">
                    当前视频: {{ record.videoPath.split('/').pop() }}
                  </div>
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
                  <button type="submit" class="btn btn-primary" :disabled="saving">
                    <i class="bi bi-save me-1"></i>
                    {{ saving ? '保存中...' : '保存' }}
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getRecordDetail, addRecord, updateRecord, uploadRecordAttachment } from '@/api/record'

export default {
  name: 'RecordEdit',
  props: {
    // 笔录ID，如果有则为编辑模式，否则为新建模式
    recordId: {
      type: [String, Number],
      default: null
    }
  },
  data() {
    return {
      record: {
        recordName: '',
        inquiryTime: this.formatDateTimeForInput(new Date()),
        inquiryAddress: '',
        assistantName: '',
        inquirerName: '',
        caseId: '',
        recordContent: '',
        videoPath: '',
        isEncrypted: false
      },
      videoFile: null,
      caseList: [],
      loading: false,
      saving: false,
      error: null
    }
  },
  computed: {
    isEdit() {
      return !!this.recordId
    }
  },
  mounted() {
    // 如果是编辑模式，加载笔录详情
    if (this.isEdit) {
      this.loadRecordDetail()
    }
    
    // 加载案件列表
    this.loadCaseList()
  },
  methods: {
    // 加载笔录详情
    async loadRecordDetail() {
      this.loading = true
      this.error = null
      
      try {
        const response = await getRecordDetail(this.recordId)
        if (response.data && response.data.code === 200) {
          const recordData = response.data.data
          
          // 格式化日期时间为input元素可接受的格式
          if (recordData.inquiryTime) {
            recordData.inquiryTime = this.formatDateTimeForInput(new Date(recordData.inquiryTime))
          }
          
          this.record = {
            ...this.record,
            ...recordData
          }
        } else {
          this.error = response.data?.message || '加载笔录详情失败'
        }
      } catch (error) {
        console.error('加载笔录详情失败:', error)
        this.error = error.message || '加载笔录详情失败'
      } finally {
        this.loading = false
      }
    },
    
    // 加载案件列表
    async loadCaseList() {
      try {
        // 这里假设有一个获取案件列表的API
        // const response = await getCaseList()
        // this.caseList = response.data.data
        
        // 临时使用空数组
        this.caseList = []
      } catch (error) {
        console.error('加载案件列表失败:', error)
      }
    },
    
    // 处理文件上传
    handleFileUpload(event) {
      const file = event.target.files[0]
      if (file) {
        this.videoFile = file
      }
    },
    
    // 保存笔录
    async saveRecord() {
      this.saving = true
      this.error = null
      
      try {
        let response
        
        // 准备提交的数据
        const recordData = { ...this.record }
        
        // 转换日期格式
        if (recordData.inquiryTime) {
          recordData.inquiryTime = new Date(recordData.inquiryTime).toISOString()
        }
        
        if (this.isEdit) {
          // 编辑模式
          response = await updateRecord(this.recordId, recordData)
        } else {
          // 新建模式
          response = await addRecord(recordData)
        }
        
        if (response.data && response.data.code === 200) {
          const savedRecordId = this.isEdit ? this.recordId : response.data.data.id
          
          // 如果有视频文件，上传视频
          if (this.videoFile) {
            await uploadRecordAttachment(savedRecordId, this.videoFile)
          }
          
          // 显示成功消息
          this.$bvToast?.toast(`笔录${this.isEdit ? '更新' : '创建'}成功`, {
            title: '操作成功',
            variant: 'success',
            solid: true
          })
          
          // 跳转到详情页
          this.$router.push(`/record/${savedRecordId}`)
        } else {
          this.error = response.data?.message || `${this.isEdit ? '更新' : '创建'}笔录失败`
        }
      } catch (error) {
        console.error(`${this.isEdit ? '更新' : '创建'}笔录失败:`, error)
        this.error = error.message || `${this.isEdit ? '更新' : '创建'}笔录失败`
      } finally {
        this.saving = false
      }
    },
    
    // 取消编辑
    cancel() {
      if (this.isEdit) {
        // 返回详情页
        this.$router.push(`/record/${this.recordId}`)
      } else {
        // 返回列表页
        this.$router.push('/record')
      }
    },
    
    // 格式化日期时间为input元素可接受的格式 (YYYY-MM-DDTHH:MM)
    formatDateTimeForInput(date) {
      if (!date) return ''
      
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      
      return `${year}-${month}-${day}T${hours}:${minutes}`
    }
  }
}
</script>

<style scoped>
.form-control:focus,
.form-select:focus {
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.25);
}

textarea.form-control {
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9rem;
  line-height: 1.5;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .card {
    border-radius: 0;
  }
  
  .card-body {
    padding: 1rem;
  }
}
</style>