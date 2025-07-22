<template>
  <div class="modal fade" id="editCaseModal" tabindex="-1" aria-labelledby="editCaseModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="editCaseModalLabel">编辑案件</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="submitForm">
            <div class="mb-3">
              <label for="editCaseName" class="form-label">案件名称</label>
              <input type="text" class="form-control" id="editCaseName" v-model="form.caseName" required>
            </div>
            <div class="mb-3">
              <label for="editCaseType" class="form-label">案件类型</label>
              <select class="form-select" id="editCaseType" v-model="form.caseType" required>
                <option value="">请选择案件类型</option>
                <option value="刑事案件">刑事案件</option>
                <option value="民事案件">民事案件</option>
                <option value="行政案件">行政案件</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div class="mb-3">
              <label for="editStatus" class="form-label">案件状态</label>
              <select class="form-select" id="editStatus" v-model="form.status" required>
                <option value="">请选择案件状态</option>
                <option value="调查中">调查中</option>
                <option value="审理中">审理中</option>
                <option value="已结案">已结案</option>
              </select>
            </div>
            <div class="mb-3">
              <label for="editCaseNumber" class="form-label">案件编号</label>
              <input type="text" class="form-control" id="editCaseNumber" v-model="form.caseNumber">
            </div>
            <div class="row">
              <div class="col-md-6 mb-3">
                <label for="editStartDate" class="form-label">立案日期</label>
                <input type="date" class="form-control" id="editStartDate" v-model="form.startDate">
              </div>
              <div class="col-md-6 mb-3">
                <label for="editEndDate" class="form-label">结案日期</label>
                <input type="date" class="form-control" id="editEndDate" v-model="form.endDate">
              </div>
            </div>
            <div class="mb-3">
              <label for="editDescription" class="form-label">案件描述</label>
              <textarea class="form-control" id="editDescription" rows="3" v-model="form.description"></textarea>
            </div>
            <div class="modal-footer px-0 pb-0">
              <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
              <button type="submit" class="btn btn-primary">保存</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { reactive, onMounted } from 'vue'
import { Modal } from 'bootstrap'

export default {
  name: 'EditCaseModal',
  emits: ['submit'],
  setup(props, { emit }) {
    // 表单数据
    const form = reactive({
      id: null,
      caseName: '',
      caseType: '',
      status: '',
      caseNumber: '',
      startDate: '',
      endDate: '',
      description: ''
    })
    
    // 模态框实例
    let modalInstance = null
    
    // 初始化模态框
    const initModal = () => {
      const modalEl = document.getElementById('editCaseModal')
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
      // 填充表单数据
      if (caseData) {
        Object.keys(form).forEach(key => {
          if (key in caseData) {
            form[key] = caseData[key]
          }
        })
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
    
    // 重置表单
    const resetForm = () => {
      form.id = null
      form.caseName = ''
      form.caseType = ''
      form.status = ''
      form.caseNumber = ''
      form.startDate = ''
      form.endDate = ''
      form.description = ''
    }
    
    // 提交表单
    const submitForm = () => {
      emit('submit', { ...form })
      hideModal()
    }
    
    // 组件挂载时初始化模态框
    onMounted(() => {
      initModal()
    })
    
    return {
      form,
      showModal,
      hideModal,
      submitForm,
      resetForm
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
}

.form-label {
  font-weight: 500;
  margin-bottom: 0.5rem;
  color: #495057;
}

.form-control, .form-select {
  padding: 0.5rem 0.75rem;
  border-radius: 0.375rem;
  border: 1px solid #ced4da;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.form-control:focus, .form-select:focus {
  border-color: #4a6cf7;
  box-shadow: 0 0 0 0.25rem rgba(74, 108, 247, 0.25);
}

.btn-primary {
  background: linear-gradient(to right, #4a6cf7, #2651f0);
  border: none;
  padding: 0.5rem 1.5rem;
  font-weight: 500;
}

.btn-primary:hover {
  background: linear-gradient(to right, #2651f0, #1a3fd9);
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