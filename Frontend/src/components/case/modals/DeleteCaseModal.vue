<template>
  <div class="modal fade" id="deleteCaseModal" tabindex="-1" aria-labelledby="deleteCaseModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="deleteCaseModalLabel">删除案件</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="alert alert-warning">
            <i class="bi bi-exclamation-triangle-fill me-2"></i>
            <span>确定要删除案件 <strong>{{ caseName }}</strong> 吗？此操作不可恢复。</span>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
          <button type="button" class="btn btn-danger" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { Modal } from 'bootstrap'

export default {
  name: 'DeleteCaseModal',
  emits: ['confirm'],
  setup(props, { emit }) {
    // 案件ID和名称
    const caseId = ref(null)
    const caseName = ref('')
    
    // 模态框实例
    let modalInstance = null
    
    // 初始化模态框
    const initModal = () => {
      const modalEl = document.getElementById('deleteCaseModal')
      if (modalEl) {
        // 销毁旧实例
        if (modalInstance) {
          modalInstance.dispose()
        }
        // 创建新实例
        modalInstance = new Modal(modalEl, {
          backdrop: 'static',
          keyboard: false
        })
      }
    }
    
    // 显示模态框
    const showModal = (caseData) => {
      if (caseData) {
        caseId.value = caseData.id
        caseName.value = caseData.caseName
      }
      
      // 确保模态框实例存在
      if (!modalInstance) {
        initModal()
      }
      // 显示模态框
      modalInstance.show()
    }
    
    // 隐藏模态框
    const hideModal = () => {
      if (modalInstance) {
        modalInstance.hide()
      }
    }
    
    // 确认删除
    const confirmDelete = () => {
      emit('confirm', caseId.value)
      hideModal()
      // 重置数据
      caseId.value = null
      caseName.value = ''
    }
    
    // 组件挂载时初始化模态框
    onMounted(() => {
      initModal()
    })
    
    return {
      caseName,
      showModal,
      hideModal,
      confirmDelete
    }
  }
}
</script>

<style scoped>
.modal-content {
  border: none;
  border-radius: 0.5rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.modal-header {
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 1.25rem 1.5rem;
}

.modal-body {
  padding: 1.5rem;
}

.modal-footer {
  border-top: none;
  padding: 0 1.5rem 1.5rem;
}

.alert-warning {
  background-color: rgba(255, 193, 7, 0.1);
  border-color: rgba(255, 193, 7, 0.2);
  color: #856404;
}

.btn-danger {
  background-color: #dc3545;
  border: none;
  padding: 0.5rem 1.5rem;
  font-weight: 500;
}

.btn-danger:hover {
  background-color: #c82333;
}

.btn-secondary {
  background-color: #f8f9fa;
  border: 1px solid #ced4da;
  color: #495057;
  padding: 0.5rem 1.5rem;
  font-weight: 500;
}

.btn-secondary:hover {
  background-color: #e9ecef;
  border-color: #ced4da;
  color: #212529;
}
</style>