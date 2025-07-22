<template>
  <div class="record-detail">
    <div class="container-fluid py-3">
      <!-- 加载状态 -->
      <div v-if="loading" class="d-flex justify-content-center align-items-center" style="min-height: 300px;">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">加载中...</span>
        </div>
      </div>
      
      <!-- 加载失败 -->
      <div v-else-if="error" class="alert alert-danger">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        {{ error }}
      </div>
      
      <!-- 笔录详情 -->
      <div v-else-if="record" class="row">
        <!-- 笔录信息卡片 -->
        <div class="col-md-12 mb-4">
          <div class="card shadow-sm">
            <div class="card-header bg-primary bg-gradient text-white d-flex justify-content-between align-items-center">
              <h5 class="mb-0">
                <i class="bi bi-file-text me-2"></i>
                {{ record.recordName }}
              </h5>
              <span class="badge bg-light text-dark">
                <i class="bi bi-hash me-1"></i>{{ record.id }}
              </span>
            </div>
            <div class="card-body">
              <div class="row">
                <div class="col-md-6 mb-3">
                  <div class="d-flex">
                    <div class="text-muted me-2">询问时间：</div>
                    <div>{{ formatDateTime(record.inquiryTime) }}</div>
                  </div>
                </div>
                <div class="col-md-6 mb-3">
                  <div class="d-flex">
                    <div class="text-muted me-2">询问地点：</div>
                    <div>{{ record.inquiryAddress }}</div>
                  </div>
                </div>
                <div class="col-md-6 mb-3">
                  <div class="d-flex">
                    <div class="text-muted me-2">询问员：</div>
                    <div>
                      <span class="badge bg-info text-dark me-1">
                        <i class="bi bi-person-fill me-1"></i>
                        {{ record.assistantName }}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 mb-3">
                  <div class="d-flex">
                    <div class="text-muted me-2">被询问人：</div>
                    <div>
                      <span class="badge bg-secondary me-1">
                        <i class="bi bi-person me-1"></i>
                        {{ record.inquirerName }}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 mb-3" v-if="record.caseId">
                  <div class="d-flex">
                    <div class="text-muted me-2">关联案件：</div>
                    <div>
                      <a href="#" @click.prevent="viewCase(record.caseId)" class="text-decoration-none">
                        <span class="badge bg-warning text-dark">
                          <i class="bi bi-folder me-1"></i>
                          {{ record.caseName || record.caseId }}
                        </span>
                      </a>
                    </div>
                  </div>
                </div>
                <div class="col-md-6 mb-3" v-if="record.createdTime">
                  <div class="d-flex">
                    <div class="text-muted me-2">创建时间：</div>
                    <div>{{ formatDateTime(record.createdTime) }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 视频播放器 (如果有视频) -->
        <div class="col-md-12 mb-4" v-if="record.videoPath">
          <div class="card shadow-sm">
            <div class="card-header bg-dark text-white">
              <i class="bi bi-camera-video me-2"></i>询问视频
            </div>
            <div class="card-body p-0 position-relative">
              <div class="ratio ratio-16x9">
                <video ref="videoPlayer" class="video-player" controls>
                  <source :src="record.videoPath" type="video/mp4">
                  您的浏览器不支持视频播放
                </video>
              </div>
              <button @click="toggleFullscreen" class="btn btn-sm btn-light position-absolute top-0 end-0 m-2">
                <i class="bi bi-fullscreen"></i>
              </button>
            </div>
          </div>
        </div>
        
        <!-- 笔录内容 -->
        <div class="col-md-12 mb-4">
          <div class="card shadow-sm">
            <div class="card-header bg-light">
              <i class="bi bi-file-text me-2"></i>笔录内容
            </div>
            <div class="card-body">
              <div class="record-content-container">
                <pre class="record-content">{{ record.recordContent }}</pre>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 操作按钮 -->
        <div class="col-md-12 mb-4">
          <div class="d-flex flex-wrap gap-2">
            <button @click="goBack" class="btn btn-secondary">
              <i class="bi bi-arrow-left me-1"></i>返回
            </button>
            <button @click="editRecord" class="btn btn-warning">
              <i class="bi bi-pencil me-1"></i>编辑
            </button>
            <button @click="confirmDelete" class="btn btn-danger">
              <i class="bi bi-trash me-1"></i>删除
            </button>
            <button @click="exportRecord" class="btn btn-success">
              <i class="bi bi-file-earmark-word me-1"></i>导出Word
            </button>
            <button @click="verifyRecord" class="btn btn-info">
              <i class="bi bi-shield-check me-1"></i>验证
            </button>
          </div>
        </div>
      </div>
      
      <!-- 记录不存在 -->
      <div v-else class="alert alert-warning">
        <i class="bi bi-exclamation-circle me-2"></i>
        未找到笔录记录
      </div>
    </div>
    
    <!-- 删除确认模态框 -->
    <div class="modal fade" id="deleteConfirmModal" tabindex="-1" aria-labelledby="deleteConfirmModalLabel" aria-hidden="true">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header bg-danger text-white">
            <h5 class="modal-title" id="deleteConfirmModalLabel">确认删除</h5>
            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
          </div>
          <div class="modal-body">
            <p>您确定要删除笔录 <strong>{{ record?.recordName }}</strong> 吗？</p>
            <p class="text-danger">
              <i class="bi bi-exclamation-triangle-fill me-2"></i>
              此操作不可逆，删除后数据将无法恢复！
            </p>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
            <button type="button" class="btn btn-danger" @click="deleteRecord">
              <i class="bi bi-trash me-1"></i>确认删除
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getRecordDetail, deleteRecord, exportRecord as exportRecordApi } from '@/api/record'
import { Modal } from 'bootstrap'

export default {
  name: 'RecordDetail',
  props: {
    // 笔录ID
    recordId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      record: null,
      loading: true,
      error: null,
      deleteModal: null
    }
  },
  mounted() {
    // 初始化删除确认模态框
    this.deleteModal = new Modal(document.getElementById('deleteConfirmModal'))
    
    // 加载笔录详情
    this.loadRecordDetail()
    
    // 调试信息
    console.log('RecordDetail mounted with recordId:', this.recordId, typeof this.recordId)
  },
  methods: {
    // 加载笔录详情
    async loadRecordDetail() {
      this.loading = true
      this.error = null
      
      try {
        const response = await getRecordDetail(this.recordId)
        if (response.data && response.data.code === 200) {
          // 处理后端返回的数据格式
          const data = response.data.data
          this.record = {
            id: data.record_id,
            recordName: data.record_name,
            inquiryTime: data.inquiry_time,
            inquiryAddress: data.inquiry_address,
            assistantName: data.assistant_name,
            inquirerName: data.inquirer_name,
            inquiryId: data.inquiry_id,
            recordContent: data.text_content,
            videoPath: data.video_file_path,
            createdTime: data.created_at
          }
          
          // 处理案件信息（如果存在）
          if (data.case) {
            this.record.caseId = data.case.case_id
            this.record.caseName = data.case.case_name
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
    
    // 返回上一页
    goBack() {
      this.$router.back()
    },
    
    // 编辑笔录
    editRecord() {
      this.$router.push(`/record/edit/${this.recordId}`)
    },
    
    // 确认删除
    confirmDelete() {
      this.deleteModal.show()
    },
    
    // 删除笔录
    async deleteRecord() {
      try {
        await deleteRecord(this.recordId)
        
        // 关闭模态框
        this.deleteModal.hide()
        
        // 显示成功消息
        this.$bvToast?.toast('笔录删除成功', {
          title: '操作成功',
          variant: 'success',
          solid: true
        })
        
        // 返回列表页
        this.$router.push('/record')
      } catch (error) {
        console.error('删除笔录失败:', error)
        this.$bvToast?.toast(`删除失败: ${error.message || '未知错误'}`, {
          title: '操作失败',
          variant: 'danger',
          solid: true
        })
      }
    },
    
    // 导出笔录为Word文档
    async exportRecord() {
      try {
        const response = await exportRecordApi(this.recordId)
        
        // 创建Blob对象
        const blob = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' })
        
        // 创建下载链接
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = `${this.record.recordName || '笔录'}_${this.recordId}.docx`
        link.click()
        
        // 释放URL对象
        URL.revokeObjectURL(link.href)
      } catch (error) {
        console.error('导出笔录失败:', error)
        this.$bvToast?.toast(`导出失败: ${error.message || '未知错误'}`, {
          title: '操作失败',
          variant: 'danger',
          solid: true
        })
      }
    },
    
    // 验证笔录
    verifyRecord() {
      this.$router.push(`/record/verify/${this.recordId}`)
    },
    
    // 查看关联案件
    viewCase(caseId) {
      this.$router.push(`/case/${caseId}`)
    },
    
    // 切换视频全屏
    toggleFullscreen() {
      const video = this.$refs.videoPlayer
      if (!video) return
      
      if (document.fullscreenElement) {
        document.exitFullscreen()
      } else {
        video.requestFullscreen()
      }
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
.record-content-container {
  max-height: 500px;
  overflow-y: auto;
  background-color: #f8f9fa;
  border-radius: 0.25rem;
  padding: 1rem;
}

.record-content {
  white-space: pre-wrap;
  font-family: 'Courier New', Courier, monospace;
  font-size: 0.9rem;
  line-height: 1.5;
  color: #212529;
  margin: 0;
}

.video-player {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: #000;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .record-content-container {
    max-height: 300px;
  }
}
</style>