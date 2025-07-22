<template>
  <case-management />
</template>

<script>
import CaseManagement from '@/components/case/CaseManagement.vue'

export default {
  name: 'CaseManagementView',
  components: {
    CaseManagement
  },
  setup() {
    // 视图组件不需要复杂逻辑，所有功能已移至CaseManagement组件
    return {}
  }
}
</script>

<style scoped>
/* 视图容器样式 */
.case-management-container {
  width: 100%;
  height: 100%;
}
</style>