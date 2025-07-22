// 增强版消息提示工具
class Message {
  static show(message, type = 'info', duration = 3000, options = {}) {
    // 创建消息元素
    const messageEl = document.createElement('div')
    messageEl.className = `alert alert-${this.getBootstrapType(type)} alert-dismissible fade show position-fixed`
    messageEl.style.cssText = `
      top: 20px;
      right: 20px;
      z-index: 9999;
      min-width: 300px;
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
      transition: all 0.3s ease-in-out;
      opacity: 0;
      transform: translateY(-20px);
    `
    
    // 构建消息内容
    const title = options.title ? `<div class="fw-bold mb-1">${options.title}</div>` : ''
    const content = `<div>${message}</div>`
    
    messageEl.innerHTML = `
      <div class="d-flex align-items-start">
        <i class="${this.getIcon(type)} me-2 mt-1"></i>
        <div class="flex-grow-1">
          ${title}
          ${content}
        </div>
        <button type="button" class="btn-close ms-auto" data-bs-dismiss="alert"></button>
      </div>
    `
    
    // 添加到页面
    document.body.appendChild(messageEl)
    
    // 显示动画
    setTimeout(() => {
      messageEl.style.opacity = '1'
      messageEl.style.transform = 'translateY(0)'
    }, 10)
    
    // 自动移除
    let timer = setTimeout(() => {
      this.close(messageEl)
    }, duration)
    
    // 点击关闭按钮
    const closeBtn = messageEl.querySelector('.btn-close')
    closeBtn.addEventListener('click', () => {
      clearTimeout(timer)
      this.close(messageEl)
    })
    
    // 鼠标悬停暂停自动关闭
    messageEl.addEventListener('mouseenter', () => {
      clearTimeout(timer)
    })
    
    // 鼠标离开恢复自动关闭
    messageEl.addEventListener('mouseleave', () => {
      timer = setTimeout(() => {
        this.close(messageEl)
      }, duration / 2)
    })
    
    return messageEl
  }
  
  static close(messageEl) {
    if (!messageEl || !messageEl.parentNode) return
    
    // 关闭动画
    messageEl.style.opacity = '0'
    messageEl.style.transform = 'translateY(-20px)'
    
    setTimeout(() => {
      if (messageEl.parentNode) {
        messageEl.parentNode.removeChild(messageEl)
      }
    }, 300) // 等待动画完成
  }
  
  static success(message, duration, options = {}) {
    return this.show(message, 'success', duration, options)
  }
  
  static error(message, duration, options = {}) {
    return this.show(message, 'error', duration, options)
  }
  
  static warning(message, duration, options = {}) {
    return this.show(message, 'warning', duration, options)
  }
  
  static info(message, duration, options = {}) {
    return this.show(message, 'info', duration, options)
  }
  
  static loading(message = '加载中...', options = {}) {
    const loadingEl = this.show(message, 'loading', 0, options)
    return {
      close: () => this.close(loadingEl)
    }
  }
  
  static confirm(message, options = {}) {
    return new Promise((resolve) => {
      const confirmEl = document.createElement('div')
      confirmEl.className = 'alert alert-primary position-fixed'
      confirmEl.style.cssText = `
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: 10000;
        min-width: 300px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        padding: 20px;
        border-radius: 4px;
      `
      
      const title = options.title ? `<div class="fw-bold mb-3">${options.title}</div>` : ''
      
      confirmEl.innerHTML = `
        <div class="text-center mb-3">
          ${title}
          <div>${message}</div>
        </div>
        <div class="d-flex justify-content-center gap-2 mt-3">
          <button class="btn btn-secondary btn-sm cancel-btn">取消</button>
          <button class="btn btn-primary btn-sm confirm-btn">确认</button>
        </div>
      `
      
      // 创建遮罩层
      const overlay = document.createElement('div')
      overlay.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        background-color: rgba(0, 0, 0, 0.5);
        z-index: 9999;
      `
      
      // 添加到页面
      document.body.appendChild(overlay)
      document.body.appendChild(confirmEl)
      
      // 确认按钮
      const confirmBtn = confirmEl.querySelector('.confirm-btn')
      confirmBtn.addEventListener('click', () => {
        document.body.removeChild(overlay)
        document.body.removeChild(confirmEl)
        resolve(true)
      })
      
      // 取消按钮
      const cancelBtn = confirmEl.querySelector('.cancel-btn')
      cancelBtn.addEventListener('click', () => {
        document.body.removeChild(overlay)
        document.body.removeChild(confirmEl)
        resolve(false)
      })
      
      // 点击遮罩层关闭
      if (options.closeOnClickOverlay !== false) {
        overlay.addEventListener('click', () => {
          document.body.removeChild(overlay)
          document.body.removeChild(confirmEl)
          resolve(false)
        })
      }
    })
  }
  
  static getBootstrapType(type) {
    const typeMap = {
      success: 'success',
      error: 'danger',
      warning: 'warning',
      info: 'info',
      loading: 'info'
    }
    return typeMap[type] || 'info'
  }
  
  static getIcon(type) {
    const iconMap = {
      success: 'bi bi-check-circle-fill',
      error: 'bi bi-exclamation-triangle-fill',
      warning: 'bi bi-exclamation-triangle-fill',
      info: 'bi bi-info-circle-fill',
      loading: 'spinner-border spinner-border-sm'
    }
    return iconMap[type] || 'bi bi-info-circle-fill'
  }
}

// 创建全局实例
const message = {
  success: (msg, duration, options) => Message.success(msg, duration, options),
  error: (msg, duration, options) => Message.error(msg, duration, options),
  warning: (msg, duration, options) => Message.warning(msg, duration, options),
  info: (msg, duration, options) => Message.info(msg, duration, options),
  loading: (msg, options) => Message.loading(msg, options),
  confirm: (msg, options) => Message.confirm(msg, options)
}

export default message