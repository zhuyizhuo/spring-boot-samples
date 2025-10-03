// RabbitMQ Demo 测试页面 JavaScript

// 配置
const config = {
    baseUrl: '/rabbitmq-demo/api/v1/messages',
    timeout: 10000,
    defaultHeaders: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
};

// 全局变量
let currentSection = 'direct-section';
let messageTypes = [];

// 初始化
document.addEventListener('DOMContentLoaded', function() {
    initializePage();
    showSection('direct-section');
    updateMenuActiveState('direct-section');
});

// 页面初始化
function initializePage() {
    // 配置 axios 默认设置
    axios.defaults.timeout = config.timeout;
    axios.defaults.headers.common = config.defaultHeaders;
    
    // 设置 axios 拦截器
    setupAxiosInterceptors();
    
    // 检查连接状态
    checkConnection();
    
    // 获取消息类型
    getMessageTypes();
    
    // 添加第一条批量消息的删除按钮事件
    updateBatchMessageButtons();
}

// 设置 axios 拦截器
function setupAxiosInterceptors() {
    // 请求拦截器
    axios.interceptors.request.use(
        config => {
            showLoading(true);
            return config;
        },
        error => {
            showLoading(false);
            return Promise.reject(error);
        }
    );

    // 响应拦截器
    axios.interceptors.response.use(
        response => {
            showLoading(false);
            return response;
        },
        error => {
            showLoading(false);
            handleError(error);
            return Promise.reject(error);
        }
    );
}

// 显示/隐藏加载状态
function showLoading(show) {
    const buttons = document.querySelectorAll('button[type="submit"], .btn');
    buttons.forEach(button => {
        if (show) {
            button.disabled = true;
            const originalText = button.innerHTML;
            button.dataset.originalText = originalText;
            button.innerHTML = '<span class="loading"></span> 发送中...';
        } else {
            button.disabled = false;
            if (button.dataset.originalText) {
                button.innerHTML = button.dataset.originalText;
                delete button.dataset.originalText;
            }
        }
    });
}

// 错误处理
function handleError(error) {
    let message = '操作失败';
    
    if (error.response) {
        // 服务器响应错误
        const status = error.response.status;
        const data = error.response.data;
        
        if (status === 404) {
            message = '服务未找到，请检查 RabbitMQ 服务是否正常运行';
        } else if (status === 500) {
            message = '服务器内部错误: ' + (data.message || '未知错误');
        } else if (status === 400) {
            message = '请求参数错误: ' + (data.message || '请检查输入参数');
        } else {
            message = `HTTP ${status}: ${data.message || error.response.statusText}`;
        }
    } else if (error.request) {
        // 网络错误
        message = '网络连接失败，请检查网络连接和服务状态';
    } else {
        // 其他错误
        message = error.message || '未知错误';
    }
    
    showResponse({
        success: false,
        message: message,
        timestamp: Date.now()
    }, 'danger');
}

// 显示响应结果
function showResponse(data, type = 'success') {
    const responseArea = document.getElementById('response-area');
    const responseContent = document.getElementById('response-content');
    
    // 清除之前的样式
    responseArea.className = 'alert';
    responseArea.classList.add(`alert-${type}`);
    
    // 格式化响应内容
    let content = '';
    if (data.success) {
        content = `
            <div class="d-flex align-items-center mb-2">
                <span class="status-indicator success"></span>
                <strong>操作成功</strong>
            </div>
            <div><strong>消息:</strong> ${data.message}</div>
        `;
        
        if (data.data) {
            content += `
                <div class="mt-2">
                    <strong>详细信息:</strong>
                    <pre class="data-display mt-1">${JSON.stringify(data.data, null, 2)}</pre>
                </div>
            `;
        }
        
        if (data.routingKey) {
            content += `<div><strong>路由键:</strong> <code>${data.routingKey}</code></div>`;
        }
        
        if (data.delaySeconds) {
            content += `<div><strong>延迟时间:</strong> ${data.delaySeconds} 秒</div>`;
        }
        
        if (data.count) {
            content += `<div><strong>消息数量:</strong> ${data.count} 条</div>`;
        }
        
        if (data.headers) {
            content += `
                <div><strong>Headers:</strong> 
                    <code>${JSON.stringify(data.headers)}</code>
                </div>
            `;
        }
    } else {
        content = `
            <div class="d-flex align-items-center mb-2">
                <span class="status-indicator error"></span>
                <strong>操作失败</strong>
            </div>
            <div class="text-danger">${data.message}</div>
        `;
    }
    
    content += `
        <div class="mt-2">
            <small class="text-muted">时间: ${new Date(data.timestamp).toLocaleString()}</small>
        </div>
    `;
    
    responseContent.innerHTML = content;
    responseArea.style.display = 'block';
    
    // 自动滚动到响应区域
    responseArea.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
    
    // 如果是成功消息，5秒后自动隐藏
    if (data.success) {
        setTimeout(() => {
            hideResponse();
        }, 5000);
    }
}

// 隐藏响应结果
function hideResponse() {
    const responseArea = document.getElementById('response-area');
    responseArea.style.display = 'none';
}

// 显示指定节
function showSection(sectionId) {
    // 隐藏所有节
    const sections = document.querySelectorAll('.section');
    sections.forEach(section => {
        section.style.display = 'none';
    });
    
    // 显示指定节
    const targetSection = document.getElementById(sectionId);
    if (targetSection) {
        targetSection.style.display = 'block';
        currentSection = sectionId;
    }
    
    // 更新菜单状态
    updateMenuActiveState(sectionId);
}

// 更新菜单激活状态
function updateMenuActiveState(sectionId) {
    const menuItems = document.querySelectorAll('.list-group-item');
    menuItems.forEach(item => {
        item.classList.remove('active');
    });
    
    const activeItem = document.querySelector(`[onclick="showSection('${sectionId}')"]`);
    if (activeItem) {
        activeItem.classList.add('active');
    }
}

// 健康检查
async function healthCheck() {
    try {
        const response = await axios.get(`${config.baseUrl}/health`);
        showResponse(response.data, 'success');
        updateConnectionStatus(true, '服务运行正常');
    } catch (error) {
        updateConnectionStatus(false, '服务不可用');
    }
}

// 检查连接状态
async function checkConnection() {
    try {
        const response = await axios.get(`${config.baseUrl}/health`);
        updateConnectionStatus(true, '已连接');
    } catch (error) {
        updateConnectionStatus(false, '连接失败');
    }
}

// 更新连接状态显示
function updateConnectionStatus(connected, message) {
    const statusElement = document.getElementById('connection-status');
    if (connected) {
        statusElement.innerHTML = `
            <span class="status-indicator success"></span>
            <span class="status-connected">${message}</span>
        `;
    } else {
        statusElement.innerHTML = `
            <span class="status-indicator error"></span>
            <span class="status-disconnected">${message}</span>
        `;
    }
}

// 获取消息类型
async function getMessageTypes() {
    try {
        const response = await axios.get(`${config.baseUrl}/types`);
        if (response.data.success) {
            messageTypes = response.data.data;
            showResponse({
                success: true,
                message: '消息类型获取成功',
                data: messageTypes,
                timestamp: Date.now()
            }, 'info');
        }
    } catch (error) {
        console.error('获取消息类型失败:', error);
    }
}

// Direct 消息发送
async function sendDirectMessage(event) {
    event.preventDefault();
    
    const content = document.getElementById('direct-content').value;
    const messageType = document.getElementById('direct-type').value;
    const sender = document.getElementById('direct-sender').value;
    
    const messageDto = {
        content: content,
        messageType: messageType,
        sender: sender || 'WEB_CLIENT'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/direct`, messageDto);
        showResponse(response.data, 'success');
        document.getElementById('direct-form').reset();
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// Topic 消息发送
async function sendTopicMessage(event) {
    event.preventDefault();
    
    const content = document.getElementById('topic-content').value;
    const routingKey = document.getElementById('topic-routing-key').value;
    const messageType = document.getElementById('topic-type').value;
    
    const messageDto = {
        content: content,
        messageType: messageType,
        sender: 'WEB_CLIENT'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/topic?routingKey=${encodeURIComponent(routingKey)}`, messageDto);
        showResponse(response.data, 'success');
        document.getElementById('topic-form').reset();
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 发送用户消息
async function sendUserMessage() {
    const content = document.getElementById('predefined-content').value || '用户相关消息';
    
    const messageDto = {
        content: content,
        messageType: 'USER_MESSAGE',
        sender: 'WEB_CLIENT'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/topic/user`, messageDto);
        showResponse(response.data, 'success');
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 发送订单消息
async function sendOrderMessage() {
    const content = document.getElementById('predefined-content').value || '订单相关消息';
    
    const messageDto = {
        content: content,
        messageType: 'ORDER',
        sender: 'WEB_CLIENT'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/topic/order`, messageDto);
        showResponse(response.data, 'success');
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// Fanout 消息发送
async function sendFanoutMessage(event) {
    event.preventDefault();
    
    const content = document.getElementById('fanout-content').value;
    const messageType = document.getElementById('fanout-type').value;
    const sender = document.getElementById('fanout-sender').value;
    
    const messageDto = {
        content: content,
        messageType: messageType,
        sender: sender || 'SYSTEM'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/fanout`, messageDto);
        showResponse(response.data, 'success');
        document.getElementById('fanout-form').reset();
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 快捷广播消息
async function sendQuickNotification() {
    const messageDto = {
        content: '系统维护通知：系统将在今晚22:00-23:00进行维护，请提前保存工作内容。',
        messageType: 'NOTIFICATION',
        sender: 'SYSTEM'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/fanout`, messageDto);
        showResponse(response.data, 'success');
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

async function sendQuickEmail() {
    const messageDto = {
        content: '您有新的邮件通知，请查收。',
        messageType: 'EMAIL',
        sender: 'EMAIL_SERVICE'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/fanout`, messageDto);
        showResponse(response.data, 'success');
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

async function sendQuickSms() {
    const messageDto = {
        content: '【系统通知】您的验证码是：123456，5分钟内有效。',
        messageType: 'SMS',
        sender: 'SMS_SERVICE'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/fanout`, messageDto);
        showResponse(response.data, 'success');
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// Headers 消息发送
async function sendHeadersMessage(event) {
    event.preventDefault();
    
    const content = document.getElementById('headers-content').value;
    const messageType = document.getElementById('headers-message-type').value;
    const type = document.getElementById('headers-type').value;
    const priority = document.getElementById('headers-priority').value;
    
    const messageDto = {
        content: content,
        messageType: messageType,
        sender: 'WEB_CLIENT'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/headers?type=${encodeURIComponent(type)}&priority=${encodeURIComponent(priority)}`, messageDto);
        showResponse(response.data, 'success');
        document.getElementById('headers-form').reset();
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 延迟消息发送
async function sendDelayedMessage(event) {
    event.preventDefault();
    
    const content = document.getElementById('delayed-content').value;
    const delaySeconds = document.getElementById('delayed-seconds').value;
    const messageType = document.getElementById('delayed-type').value;
    
    const messageDto = {
        content: content,
        messageType: messageType,
        sender: 'WEB_CLIENT'
    };
    
    try {
        const response = await axios.post(`${config.baseUrl}/delayed?delaySeconds=${delaySeconds}`, messageDto);
        showResponse(response.data, 'success');
        document.getElementById('delayed-form').reset();
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 设置延迟时间
function setDelayTime(seconds) {
    document.getElementById('delayed-seconds').value = seconds;
}

// 添加批量消息
function addBatchMessage() {
    const container = document.getElementById('batch-messages-container');
    const messageCount = container.children.length + 1;
    
    const messageItem = document.createElement('div');
    messageItem.className = 'message-item border rounded p-3 mb-3';
    messageItem.innerHTML = `
        <div class="row">
            <div class="col-md-6">
                <div class="mb-2">
                    <label class="form-label">消息内容</label>
                    <textarea class="form-control batch-content" rows="2" placeholder="消息 ${messageCount} 内容..."></textarea>
                </div>
            </div>
            <div class="col-md-4">
                <div class="mb-2">
                    <label class="form-label">消息类型</label>
                    <select class="form-select batch-type">
                        <option value="NORMAL">普通消息</option>
                        <option value="NOTIFICATION">系统通知</option>
                        <option value="ORDER">订单消息</option>
                        <option value="USER_MESSAGE">用户消息</option>
                        <option value="EMAIL">邮件消息</option>
                        <option value="SMS">短信消息</option>
                    </select>
                </div>
            </div>
            <div class="col-md-2 d-flex align-items-end">
                <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeBatchMessage(this)">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        </div>
    `;
    
    container.appendChild(messageItem);
    updateBatchMessageButtons();
}

// 删除批量消息
function removeBatchMessage(button) {
    const messageItem = button.closest('.message-item');
    messageItem.remove();
    updateBatchMessageButtons();
}

// 更新批量消息按钮状态
function updateBatchMessageButtons() {
    const container = document.getElementById('batch-messages-container');
    const deleteButtons = container.querySelectorAll('.btn-outline-danger');
    
    // 如果只有一条消息，禁用删除按钮
    if (deleteButtons.length <= 1) {
        deleteButtons.forEach(btn => btn.disabled = true);
    } else {
        deleteButtons.forEach(btn => btn.disabled = false);
    }
}

// 发送批量消息
async function sendBatchMessages() {
    const container = document.getElementById('batch-messages-container');
    const messageItems = container.querySelectorAll('.message-item');
    
    const messages = [];
    let hasEmptyContent = false;
    
    messageItems.forEach((item, index) => {
        const content = item.querySelector('.batch-content').value.trim();
        const messageType = item.querySelector('.batch-type').value;
        
        if (!content) {
            hasEmptyContent = true;
            return;
        }
        
        messages.push({
            content: content,
            messageType: messageType,
            sender: 'WEB_CLIENT'
        });
    });
    
    if (hasEmptyContent) {
        showResponse({
            success: false,
            message: '请填写所有消息的内容',
            timestamp: Date.now()
        }, 'warning');
        return;
    }
    
    if (messages.length === 0) {
        showResponse({
            success: false,
            message: '请至少添加一条消息',
            timestamp: Date.now()
        }, 'warning');
        return;
    }
    
    try {
        const response = await axios.post(`${config.baseUrl}/batch`, messages);
        showResponse(response.data, 'success');
        
        // 清空所有消息内容
        messageItems.forEach(item => {
            item.querySelector('.batch-content').value = '';
        });
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 发送系统通知
async function sendSystemNotification(event) {
    event.preventDefault();
    
    const content = document.getElementById('notification-content').value || '系统通知消息';
    const receiver = document.getElementById('notification-receiver').value;
    
    try {
        const response = await axios.post(`${config.baseUrl}/notification?content=${encodeURIComponent(content)}&receiver=${encodeURIComponent(receiver)}`);
        showResponse(response.data, 'success');
        document.getElementById('notification-content').value = '';
        document.getElementById('notification-receiver').value = '';
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 发送邮件通知
async function sendEmailNotification(event) {
    event.preventDefault();
    
    const content = document.getElementById('email-content').value || '邮件通知内容';
    const receiver = document.getElementById('email-receiver').value;
    
    if (!receiver) {
        showResponse({
            success: false,
            message: '请输入收件人邮箱',
            timestamp: Date.now()
        }, 'warning');
        return;
    }
    
    try {
        const response = await axios.post(`${config.baseUrl}/email?content=${encodeURIComponent(content)}&receiver=${encodeURIComponent(receiver)}`);
        showResponse(response.data, 'success');
        document.getElementById('email-content').value = '';
        document.getElementById('email-receiver').value = '';
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 发送短信通知
async function sendSmsNotification(event) {
    event.preventDefault();
    
    const content = document.getElementById('sms-content').value || '短信通知内容';
    const receiver = document.getElementById('sms-receiver').value;
    
    if (!receiver) {
        showResponse({
            success: false,
            message: '请输入手机号码',
            timestamp: Date.now()
        }, 'warning');
        return;
    }
    
    try {
        const response = await axios.post(`${config.baseUrl}/sms?content=${encodeURIComponent(content)}&receiver=${encodeURIComponent(receiver)}`);
        showResponse(response.data, 'success');
        document.getElementById('sms-content').value = '';
        document.getElementById('sms-receiver').value = '';
    } catch (error) {
        // 错误已在拦截器中处理
    }
}

// 工具函数
function generateRandomMessage() {
    const messages = [
        '这是一条测试消息',
        '用户操作日志记录',
        '系统状态监控信息',
        '订单处理完成通知',
        '支付结果确认',
        '库存变更提醒',
        '新用户注册通知',
        '密码修改确认',
        '登录异常警告',
        '数据同步完成'
    ];
    return messages[Math.floor(Math.random() * messages.length)];
}

function formatTimestamp(timestamp) {
    return new Date(timestamp).toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    });
}

// 键盘快捷键支持
document.addEventListener('keydown', function(event) {
    // Ctrl/Cmd + Enter 快速发送当前表单
    if ((event.ctrlKey || event.metaKey) && event.key === 'Enter') {
        event.preventDefault();
        const currentForm = document.querySelector(`#${currentSection} form`);
        if (currentForm) {
            currentForm.dispatchEvent(new Event('submit'));
        }
    }
    
    // Esc 键隐藏响应区域
    if (event.key === 'Escape') {
        hideResponse();
    }
});

// 页面可见性变化时检查连接
document.addEventListener('visibilitychange', function() {
    if (!document.hidden) {
        checkConnection();
    }
});

// 定期检查连接状态（每30秒）
setInterval(checkConnection, 30000);
