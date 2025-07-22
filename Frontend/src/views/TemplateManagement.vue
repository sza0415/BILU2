<template>
  <div class="container mt-4">
    <!-- 页面标题区域 -->
    <div class="row mb-4">
      <div class="col-12 text-center">
        <h2 class="display-5 fw-bold text-primary">保密问答模板管理</h2>
        <p class="text-muted">管理和维护各类保密审查问题模板</p>
      </div>
    </div>
    
    <div class="row">
      <div class="col-12">
        <div class="card border-0 shadow-lg rounded-4 overflow-hidden">
          <!-- 操作按钮区域 -->
          <div class="card-header bg-white border-0 p-4">
            <div class="d-flex justify-content-between align-items-center">
              <div>
                <h4 class="fw-bold text-primary mb-0">
                  <i class="bi bi-gear me-2"></i>模板管理
                </h4>
              </div>
              <div class="btn-group">
                <button class="btn btn-primary" @click="showAddModal = true" :disabled="loading">
                  <i class="bi bi-plus-circle me-2"></i>添加模板类别
                </button>
                <button class="btn btn-success" @click="showImportModal = true" :disabled="loading">
                  <i class="bi bi-upload me-2"></i>导入模板
                </button>
                <button class="btn btn-info" @click="exportTemplates" :disabled="loading">
                  <i class="bi bi-download me-2"></i>
                  <span v-if="loading">
                    <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    导出中...
                  </span>
                  <span v-else>导出模板</span>
                </button>
              </div>
            </div>
          </div>
          
          <!-- 标签导航 -->
          <div class="card-header bg-white border-0 p-0">
            <ul class="nav nav-tabs nav-fill custom-tabs" role="tablist">
              <li class="nav-item" role="presentation" v-for="type in templateTypes" :key="type.key">
                <button 
                  class="nav-link" 
                  :class="{ active: activeTab === type.key }"
                  @click="switchTab(type.key)"
                  type="button" 
                  role="tab"
                >
                  <i class="fas fa-shield-alt me-2"></i>{{ type.name }}
                  <span class="badge bg-primary rounded-pill ms-2">{{ getTemplateCount(type.key) }}</span>
                </button>
              </li>
            </ul>
          </div>
          
          <!-- 标签内容 -->
          <div class="card-body p-4">
            <div class="tab-content">
              <div class="tab-pane fade show active">
                <div class="d-flex justify-content-between align-items-center mb-4">
                  <div>
                    <h3 class="fw-bold text-primary mb-0">
                      <i class="fas fa-list-alt me-2"></i>{{ getCurrentTypeName() }}
                      <span class="badge bg-primary rounded-pill ms-2">{{ getCurrentTemplates().length }}</span>
                    </h3>
                    <div class="border-bottom border-2 border-primary mt-2" style="width: 50px;"></div>
                  </div>
                  <div class="btn-group">
                    <button 
                      class="btn btn-primary px-4 py-2 d-flex align-items-center shadow-sm" 
                      @click="showAddModal = true"
                      :disabled="loading"
                    >
                      <i class="fas fa-plus-circle me-2"></i> 添加问题
                    </button>
                    <button 
                      v-if="getCurrentTemplates().length > 0"
                      class="btn btn-danger px-4 py-2 d-flex align-items-center shadow-sm" 
                      @click="confirmDeleteTemplateType"
                      :disabled="loading"
                    >
                      <i class="fas fa-trash-alt me-2"></i> 删除类型
                    </button>
                  </div>
                </div>
                
                <!-- 问题列表 -->
                <div class="table-responsive rounded-4 shadow-sm" style="min-height: 400px;">
                  <table class="table table-hover mb-0">
                    <thead>
                      <tr class="bg-light">
                        <th width="5%" class="py-3 ps-4 text-primary">序号</th>
                        <th width="80%" class="py-3 text-primary">问题</th>
                        <th width="15%" class="py-3 text-center text-primary">操作</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr 
                        v-for="(template, index) in getCurrentTemplates()" 
                        :key="template.id" 
                        class="border-bottom question-item"
                        :ref="'row-' + template.id"
                      >
                        <td class="py-3 ps-4 fw-bold text-muted">{{ index + 1 }}</td>
                        <td class="py-3 question-text">{{ template.question }}</td>
                        <td class="py-3 text-center">
                          <div class="btn-group">
                            <button 
                              class="btn btn-sm btn-outline-primary edit-btn" 
                              @click="editTemplate(template)"
                              :disabled="loading"
                            >
                              <i class="fas fa-edit me-1"></i> 编辑
                            </button>
                            <button 
                              class="btn btn-sm btn-outline-danger delete-btn" 
                              @click="confirmDeleteTemplate(template.id)"
                              :disabled="loading"
                            >
                              <i class="fas fa-trash-alt me-1"></i> 删除
                            </button>
                          </div>
                        </td>
                      </tr>
                      <tr v-if="getCurrentTemplates().length === 0">
                        <td colspan="3" class="text-center py-5">
                          <div class="empty-state">
                            <i class="fas fa-inbox fa-3x mb-3 text-secondary opacity-50"></i>
                            <h5 class="text-secondary">暂无问题模板</h5>
                            <p class="text-muted">点击"添加问题"按钮创建新的问题模板</p>
                          </div>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑模板模态框 -->
    <div class="modal fade" :class="{ show: showAddModal }" :style="{ display: showAddModal ? 'block' : 'none' }" tabindex="-1">
      <div class="modal-dialog modal-lg">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-plus-circle me-2"></i>{{ editingTemplate ? '编辑模板' : '添加模板类别' }}
            </h5>
            <button type="button" class="btn-close" @click="closeAddModal"></button>
          </div>
          <div class="modal-body">
            <form @submit.prevent="saveTemplate">
              <div class="mb-3">
                <label for="templateType" class="form-label">模板类型</label>
                <div class="d-flex align-items-center mb-2">
                  <div class="input-group">
                    <select class="form-select" id="templateType" v-model="templateForm.templateType" :disabled="showNewCategoryFields" required>
                      <option value="">请选择模板类型</option>
                      <option v-for="type in templateTypes" :key="type.key" :value="type.key">
                        {{ type.name }}
                      </option>
                    </select>
                    <button type="button" class="btn btn-outline-primary" @click="toggleNewCategory">
                      <i class="fas fa-plus" v-if="!showNewCategoryFields"></i>
                      <i class="fas fa-minus" v-else></i>
                      {{ showNewCategoryFields ? ' 取消新类别' : ' 新类别' }}
                    </button>
                  </div>
                </div>
              </div>
              
              <!-- 新类别输入字段 -->
              <div v-if="showNewCategoryFields" class="mb-3 new-category-fields">
                <div class="card border-primary p-3 mb-3">
                  <div class="mb-3">
                    <label for="newTypeKey" class="form-label">类别名称</label>
                    <div class="input-group">
                      <span class="input-group-text"><i class="fas fa-code"></i></span>
                      <input type="text" class="form-control" id="newTypeKey" v-model="newCategoryForm.key" placeholder="例如：信息安全">
                    </div>
                    <div class="form-text text-muted">
                      <i class="fas fa-info-circle me-1"></i>
                      类别名称只能包含中文、英文、数字、下划线和连字符，不能包含空格
                    </div>
                  </div>
                </div>
              </div>
              <div class="mb-3">
                <label for="question" class="form-label">问题内容</label>
                <textarea 
                  class="form-control" 
                  id="question" 
                  rows="4" 
                  v-model="templateForm.question" 
                  placeholder="请输入问题内容"
                  required
                ></textarea>
              </div>
              <div class="mb-3">
                <label for="sortOrder" class="form-label">排序</label>
                <input 
                  type="number" 
                  class="form-control" 
                  id="sortOrder" 
                  v-model="templateForm.sortOrder" 
                  placeholder="排序号（数字越小越靠前）"
                />
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="closeAddModal" :disabled="loading">取消</button>
            <button type="button" class="btn btn-primary" @click="saveTemplate" :disabled="loading">
              <i class="bi bi-check-circle me-2"></i>
              <span v-if="loading">
                <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                保存中...
              </span>
              <span v-else>保存</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 导入模板模态框 -->
    <div class="modal fade" :class="{ show: showImportModal }" :style="{ display: showImportModal ? 'block' : 'none' }" tabindex="-1">
      <div class="modal-dialog">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">
              <i class="bi bi-upload me-2"></i>导入模板
            </h5>
            <button type="button" class="btn-close" @click="showImportModal = false"></button>
          </div>
          <div class="modal-body">
            <div class="mb-3">
              <label for="importFile" class="form-label">选择文件</label>
              <input 
                type="file" 
                class="form-control" 
                id="importFile" 
                @change="handleFileSelect"
                accept=".xlsx,.xls,.csv"
              />
              <div class="form-text">支持Excel (.xlsx, .xls) 和CSV (.csv) 格式</div>
            </div>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" @click="showImportModal = false" :disabled="loading">取消</button>
            <button type="button" class="btn btn-primary" @click="confirmImportTemplates" :disabled="!selectedFile || loading">
              <i class="bi bi-upload me-2"></i>
              <span v-if="loading">
                <span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                导入中...
              </span>
              <span v-else>导入</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 模态框背景 -->
    <div v-if="showAddModal || showImportModal" class="modal-backdrop fade show"></div>
  </div>
</template>

<script>
import secrecyTemplateApi from '@/api/secrecyTemplate'
import message from '@/utils/message'

export default {
  name: 'TemplateManagement',
  data() {
    return {
      activeTab: 'secrecy_system',
      showAddModal: false,
      showImportModal: false,
      showNewCategoryFields: false,
      editingTemplate: null,
      selectedFile: null,
      loading: false,
      templateForm: {
        templateType: '',
        question: '',
        sortOrder: 0
      },
      newCategoryForm: {
        key: ''
      },
      templateTypes: [],
      templates: {}
    }
  },
  methods: {
    // UI交互方法
    switchTab(tabKey) {
      // 添加标签切换动画效果
      const tabContent = document.querySelector('.tab-content');
      if (tabContent) {
        tabContent.style.animation = 'none';
        setTimeout(() => {
          this.activeTab = tabKey;
          tabContent.style.animation = 'fadeIn 0.5s ease-in-out';
        }, 10);
      } else {
        this.activeTab = tabKey;
      }
    },

    confirmDeleteTemplate(id) {
      if (!id) {
        this.showToast('错误', '无法获取问题ID，请刷新页面重试', 'error');
        return;
      }

      // 使用浏览器原生确认对话框（可以后续替换为更美观的组件）
      if (confirm('确定要删除这个问题吗？删除后将无法恢复！')) {
        this.deleteTemplate(id);
      }
    },

    confirmDeleteTemplateType() {
      const typeName = this.getCurrentTypeName();
      if (confirm(`确定要删除整个"${typeName}"类型的所有模板吗？删除后将无法恢复"${typeName}"类型下的所有问题！`)) {
        this.deleteTemplateType();
      }
    },

    confirmImportTemplates() {
      if (!this.selectedFile) {
        this.showToast('错误', '请选择文件', 'error');
        return;
      }

      if (confirm('确定要导入模板吗？这将覆盖数据库中的现有数据！')) {
        this.importTemplates();
      }
    },

    showToast(title, messageText, type) {
      // 简化的消息提示，可以后续替换为更美观的组件
      if (type === 'success') {
        message.success(messageText);
      } else if (type === 'error') {
        message.error(messageText);
      } else if (type === 'warning') {
        message.warning(messageText);
      }
    },

    toggleNewCategory() {
      this.showNewCategoryFields = !this.showNewCategoryFields;
      if (this.showNewCategoryFields) {
        // 清空模板类型选择
        this.templateForm.templateType = '';
        // 添加动画效果
        this.$nextTick(() => {
          const newCategoryFields = document.querySelector('.new-category-fields');
          if (newCategoryFields) {
            newCategoryFields.style.animation = 'fadeIn 0.3s ease-in-out';
          }
        });
      } else {
        // 清空新类别输入框
        this.newCategoryForm.key = '';
      }
    },

    validateNewCategory() {
      const newTypeKey = this.newCategoryForm.key.trim();
      
      if (!newTypeKey) {
        this.showToast('错误', '新类别名称不能为空', 'error');
        return false;
      }
      
      // 验证类别名称格式（不能包含空格等特殊字符）
      if (!/^[\u4e00-\u9fa5a-zA-Z0-9_-]+$/.test(newTypeKey)) {
        this.showToast('错误', '类别名称只能包含中文、英文、数字、下划线和连字符，不能包含空格', 'error');
        return false;
      }
      
      return true;
    },

    // API调用方法
    async loadTemplateTypes() {
      try {
        const response = await secrecyTemplateApi.getAllTemplateTypes();
        
        if (response.data.code === 200) {
          const types = response.data.data;
          // 将字符串类型转换为对象格式
          this.templateTypes = types.map(type => ({
            key: type,
            name: this.getTemplateTypeName(type)
          }));
          
          // 初始化templates对象
          types.forEach(type => {
            this.templates[type] = [];
          });
          
          // 设置默认激活标签
          if (types.length > 0) {
            this.activeTab = types[0];
          }
        } else {
          message.error(response.data.message || '加载模板类型失败');
        }
      } catch (error) {
        console.error('加载模板类型失败:', error);
        message.error('加载模板类型失败');
      }
    },

    // 获取模板类型的显示名称
    getTemplateTypeName(type) {
      return type;
    },

    async loadTemplates(templateType = null) {
      try {
        this.loading = true;
        
        if (templateType) {
          // 加载指定类型的模板
          const response = await secrecyTemplateApi.getAllTemplates(templateType);
          if (response.data.code === 200) {
             this.templates[templateType] = response.data.data;
          } else {
            message.error(response.data.message || '加载模板失败');
          }
        } else {
          // 为每个模板类型分别加载数据
          for (const type of this.templateTypes) {
            const response = await secrecyTemplateApi.getAllTemplates(type.key);
            if (response.data.code === 200) {
               this.templates[type.key] = response.data.data;
             } else {
               console.error(`加载类型 ${type.key} 的模板失败:`, response.data.message);
               this.templates[type.key] = [];
            }
          }
        }
      } catch (error) {
        console.error('加载模板失败:', error);
        message.error('加载模板失败');
      } finally {
        this.loading = false;
      }
    },

    async saveTemplate() {
      try {
        // 检查是否是新类别
        const isNewCategory = this.showNewCategoryFields;
        let templateType = this.templateForm.templateType;
        
        if (isNewCategory) {
          // 验证新类别输入
          if (!this.validateNewCategory()) {
            return;
          }
          templateType = this.newCategoryForm.key.trim();
        }
        
        if (!templateType || !this.templateForm.question) {
          message.warning('请填写完整信息');
          return;
        }

        this.loading = true;
        let response;
        
        if (this.editingTemplate) {
          // 编辑模板
          response = await secrecyTemplateApi.editTemplate(this.editingTemplate.id, {
            templateType: templateType,
            question: this.templateForm.question,
            sortOrder: this.templateForm.sortOrder || 0
          });
        } else {
          // 添加模板（使用新类别或现有类别）
          response = await secrecyTemplateApi.addTemplateWithCategory(
            templateType,
            this.templateForm.question,
            this.templateForm.sortOrder || 0
          );
        }

        if (response.data.code === 200) {
          message.success(this.editingTemplate ? '更新成功' : '问题已成功添加');
          this.closeAddModal();
          
          // 如果是新类别，需要重新加载模板类型和模板数据
          if (isNewCategory) {
            await this.loadTemplateTypes();
            await this.loadTemplates();
            // 切换到新创建的类别
            this.activeTab = templateType;
          } else {
            await this.loadTemplates(templateType);
          }
        } else {
          message.error(response.data.message || '操作失败');
        }
      } catch (error) {
        console.error('保存模板失败:', error);
        message.error('保存模板失败');
      } finally {
        this.loading = false;
      }
    },

    async deleteTemplate(id) {
      try {
        this.loading = true;
        const response = await secrecyTemplateApi.deleteTemplate(id);

        if (response.data.code === 200) {
          // 添加行删除动画效果
          const rowElement = this.$refs[`row-${id}`];
          if (rowElement && rowElement[0]) {
            // 添加淡出动画
            rowElement[0].style.transition = 'opacity 0.3s ease';
            rowElement[0].style.opacity = '0';
            
            setTimeout(() => {
              message.success('问题已成功删除');
              this.loadTemplates(this.activeTab);
            }, 300);
          } else {
            message.success('删除成功');
            await this.loadTemplates(this.activeTab);
          }
        } else {
          message.error(response.data.message || '删除失败');
        }
      } catch (error) {
        console.error('删除模板失败:', error);
        message.error('删除模板失败');
      } finally {
        this.loading = false;
      }
    },

    async deleteTemplateType() {
      const typeName = this.getCurrentTypeName();
      if (!confirm(`确定要删除整个"${typeName}"类型的所有模板吗？`)) {
        return;
      }

      try {
        this.loading = true;
        const response = await secrecyTemplateApi.deleteTemplateType(this.activeTab);

        if (response.data.code === 200) {
          message.success('删除类型成功');
          await this.loadTemplates(this.activeTab);
        } else {
          message.error(response.data.message || '删除失败');
        }
      } catch (error) {
        console.error('删除模板类型失败:', error);
        message.error('删除模板类型失败');
      } finally {
        this.loading = false;
      }
    },

    async importTemplates() {
      if (!this.selectedFile) {
        message.warning('请选择文件');
        return;
      }

      try {
        this.loading = true;
        let response;
        
        if (this.selectedFile.name.endsWith('.csv')) {
          response = await secrecyTemplateApi.importCsvTemplate(this.selectedFile);
        } else {
          response = await secrecyTemplateApi.importTemplate(this.selectedFile);
        }

        if (response.data.code === 200) {
          message.success(`成功导入${response.data.data}条数据`);
          this.showImportModal = false;
          this.selectedFile = null;
          await this.loadTemplates();
        } else {
          message.error(response.data.message || '导入失败');
        }
      } catch (error) {
        console.error('导入模板失败:', error);
        message.error('导入模板失败');
      } finally {
        this.loading = false;
      }
    },

    async exportTemplates() {
      try {
        this.loading = true;
        const response = await secrecyTemplateApi.exportTemplates(this.activeTab);

        // 创建下载链接
        const blob = new Blob([response.data], {
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
        });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `secrecy_templates_${this.activeTab || 'all'}.xlsx`;
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);

        // 延迟恢复按钮状态（因为是下载操作，没有完成回调）
        setTimeout(() => {
          this.loading = false;
          message.success('模板导出请求已发送');
        }, 1000);
      } catch (error) {
        console.error('导出模板失败:', error);
        message.error('导出模板失败');
        this.loading = false;
      }
    },

    handleFileSelect(event) {
      this.selectedFile = event.target.files[0];
    },

    closeAddModal() {
      this.showAddModal = false;
      this.showNewCategoryFields = false;
      this.editingTemplate = null;
      this.templateForm = {
        templateType: '',
        question: '',
        sortOrder: 0
      };
      this.newCategoryForm = {
        key: ''
      };
    },

    // UI辅助方法
    getCurrentTemplates() {
      return this.templates[this.activeTab] || [];
    },

    getCurrentTypeName() {
      const type = this.templateTypes.find(t => t.key === this.activeTab);
      return type ? type.name : '';
    },

    getTemplateCount(typeKey) {
      return this.templates[typeKey] ? this.templates[typeKey].length : 0;
    },

    editTemplate(template) {
      this.editingTemplate = template;
      this.templateForm = {
        templateType: this.activeTab,
        question: template.question,
        sortOrder: template.sortOrder
      };
      this.showAddModal = true;
    },

    formatDate(dateStr) {
      return new Date(dateStr).toLocaleString('zh-CN');
    }
  },
  watch: {
    activeTab(newTab) {
      // 当切换标签时，如果该类型的模板还没有加载，则加载
      if (!this.templates[newTab] || this.templates[newTab].length === 0) {
        this.loadTemplates(newTab);
      }
    }
  },
  async mounted() {
    console.log('模版管理页面已加载');
    // 先加载模板类型，然后加载模板数据
    await this.loadTemplateTypes();
    await this.loadTemplates();
  }
}
</script>

<style scoped>
/* 自定义标签样式 */
.custom-tabs {
  border-bottom: none;
  background: linear-gradient(to right, #f8f9fa, #e9ecef);
}

.custom-tabs .nav-link {
  border: none;
  padding: 1rem 1.5rem;
  font-weight: 500;
  color: #6c757d;
  position: relative;
  transition: all 0.3s ease;
}

.custom-tabs .nav-link:hover {
  color: #4361ee;
}

.custom-tabs .nav-link.active {
  color: #4361ee;
  background-color: transparent;
}

.custom-tabs .nav-link::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 3px;
  background-color: #4361ee;
  transform: scaleX(0);
  transition: transform 0.3s ease;
}

.custom-tabs .nav-link.active::after,
.custom-tabs .nav-link:hover::after {
  transform: scaleX(1);
}

/* 表格样式 */
.table {
  border-collapse: separate;
  border-spacing: 0;
}

.table thead th {
  font-weight: 600;
  border-top: none;
  border-bottom: 2px solid #e0e0e0;
}

.table tbody tr {
  transition: all 0.3s ease;
}

.table tbody tr:hover {
  background-color: rgba(67, 97, 238, 0.05);
}

/* 问题项动画 */
.question-item {
  transition: all 0.3s ease;
}

.question-text {
  line-height: 1.5;
}

/* 按钮样式 */
.btn-outline-primary {
  color: #4361ee;
  border-color: #4361ee;
}

.btn-outline-primary:hover {
  background-color: #4361ee;
  border-color: #4361ee;
}

.btn-primary {
  background-color: #4361ee;
  border-color: #4361ee;
}

.btn-primary:hover {
  background-color: #3f37c9;
  border-color: #3f37c9;
}

.btn {
  transition: all 0.3s ease;
}

.btn:hover {
  transform: translateY(-1px);
}

/* 表单控件样式 */
.form-control:focus, .form-select:focus {
  border-color: #4361ee;
  box-shadow: 0 0 0 0.25rem rgba(67, 97, 238, 0.25);
}

/* 模态框样式 */
.modal {
  background-color: rgba(0, 0, 0, 0.5);
}

.modal-content {
  border-radius: 0.75rem;
  overflow: hidden;
}

.modal-header {
  padding: 1.25rem 1.5rem;
}

/* 空状态样式 */
.empty-state {
  padding: 2rem;
  text-align: center;
}

/* 卡片悬停效果 */
.card {
  transition: all 0.3s ease;
}

.template-card {
  transition: all 0.3s ease;
}

.template-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.badge {
  font-size: 0.75rem;
}

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

/* 动画效果 */
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

.tab-content {
  animation: fadeIn 0.5s ease-in-out;
}

/* 加载状态样式 */
.spinner-border-sm {
  width: 1rem;
  height: 1rem;
}

/* 新类别字段样式 */
.new-category-fields {
  animation: fadeIn 0.3s ease-in-out;
}

.new-category-fields .card {
  border: 1px solid #4361ee;
  background-color: rgba(67, 97, 238, 0.05);
}

.new-category-fields .form-text {
  font-size: 0.875rem;
}

.input-group-text {
  background-color: #f8f9fa;
  border-color: #dee2e6;
}
</style>