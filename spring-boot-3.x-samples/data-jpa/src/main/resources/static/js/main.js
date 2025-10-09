// 用户管理系统核心业务逻辑

// 配置
const config = {
    apiBaseUrl: '/data-jpa/api/users',
    pageSize: 10,
    defaultAvatar: 'https://ui-avatars.com/api/?name=User&background=random&color=fff&size=40',
    notificationTimeout: 3000,
    loadingDelay: 500
};

// 状态管理
const state = {
    users: [],
    filteredUsers: [],
    currentPage: 1,
    currentUserId: null,
    deleteUserId: null,
    searchTerm: '',
    isLoading: false,
    isDarkMode: false,
    lastSuccessRate: 98,
    lastNewUsers: 0
};

// DOM 元素缓存
const elements = {
    userTableBody: document.getElementById('user-table-body'),
    addUserBtn: document.getElementById('add-user-btn'),
    userModal: document.getElementById('user-modal'),
    modalContent: document.getElementById('modal-content'),
    modalTitle: document.getElementById('modal-title'),
    closeModal: document.getElementById('close-modal'),
    cancelModal: document.getElementById('cancel-modal'),
    userForm: document.getElementById('user-form'),
    confirmModal: document.getElementById('confirm-modal'),
    cancelDelete: document.getElementById('cancel-delete'),
    confirmDelete: document.getElementById('confirm-delete'),
    searchInput: document.getElementById('search-input'),
    notification: document.getElementById('notification'),
    notificationMessage: document.getElementById('notification-message'),
    notificationIcon: document.getElementById('notification-icon'),
    totalUsers: document.getElementById('total-users'),
    newUsers: document.getElementById('new-users'),
    activeUsers: document.getElementById('active-users'),
    successRate: document.getElementById('success-rate'),
    showingStart: document.getElementById('showing-start'),
    showingEnd: document.getElementById('showing-end'),
    totalItems: document.getElementById('total-items'),
    prevPageBtn: document.getElementById('prev-page'),
    nextPageBtn: document.getElementById('next-page'),
    themeToggle: document.getElementById('theme-toggle')
};

// 初始化应用
function initApp() {
    setupEventListeners();
    checkDarkModePreference();
    fetchUsers().then(() => {
        updateDashboardStats();
        // 启动自动刷新数据任务
        startAutoRefresh();
    });
}

// 设置事件监听器
function setupEventListeners() {
    // 模态框相关
    elements.addUserBtn.addEventListener('click', showAddUserModal);
    elements.closeModal.addEventListener('click', hideUserModal);
    elements.cancelModal.addEventListener('click', hideUserModal);
    elements.userModal.addEventListener('click', (e) => e.target === elements.userModal && hideUserModal());
    elements.confirmModal.addEventListener('click', (e) => e.target === elements.confirmModal && hideConfirmModal());
    
    // 表单提交
    elements.userForm.addEventListener('submit', handleUserFormSubmit);
    
    // 删除操作
    elements.cancelDelete.addEventListener('click', hideConfirmModal);
    elements.confirmDelete.addEventListener('click', handleConfirmDelete);
    
    // 搜索功能
    elements.searchInput.addEventListener('input', debounce(handleSearch, 300));
    
    // 分页功能
    elements.prevPageBtn.addEventListener('click', goToPrevPage);
    elements.nextPageBtn.addEventListener('click', goToNextPage);
    
    // 主题切换
    elements.themeToggle.addEventListener('click', toggleTheme);
}

// API 调用函数

// 获取所有用户
async function fetchUsers() {
    try {
        showLoading();
        const response = await fetch(config.apiBaseUrl);
        if (!response.ok) throw new Error('获取用户数据失败');
        state.users = await response.json();
        state.filteredUsers = [...state.users];
        renderUserTable();
        updatePagination();
        return state.users;
    } catch (error) {
        console.error('获取用户数据失败:', error);
        showNotification('获取用户数据失败: ' + error.message, 'error');
        renderEmptyState();
        return [];
    } finally {
        hideLoading();
    }
}

// 获取单个用户
async function fetchUser(id) {
    try {
        const response = await fetch(`${config.apiBaseUrl}/${id}`);
        if (!response.ok) throw new Error('获取用户详情失败');
        return await response.json();
    } catch (error) {
        console.error('获取用户详情失败:', error);
        showNotification('获取用户详情失败: ' + error.message, 'error');
        throw error;
    }
}

// 创建用户
async function createUser(userData) {
    try {
        const response = await fetch(config.apiBaseUrl, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(userData)
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({message: '创建用户失败'}));
            throw new Error(errorData.message);
        }
        return await response.json();
    } catch (error) {
        console.error('创建用户失败:', error);
        showNotification(error.message, 'error');
        throw error;
    }
}

// 更新用户
async function updateUser(id, userData) {
    try {
        const response = await fetch(`${config.apiBaseUrl}/${id}`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(userData)
        });
        if (!response.ok) {
            const errorData = await response.json().catch(() => ({message: '更新用户失败'}));
            throw new Error(errorData.message);
        }
        return await response.json();
    } catch (error) {
        console.error('更新用户失败:', error);
        showNotification(error.message, 'error');
        throw error;
    }
}

// 删除用户
async function deleteUser(id) {
    try {
        const response = await fetch(`${config.apiBaseUrl}/${id}`, {
            method: 'DELETE'
        });
        if (!response.ok) throw new Error('删除用户失败');
        return true;
    } catch (error) {
        console.error('删除用户失败:', error);
        showNotification('删除用户失败: ' + error.message, 'error');
        throw error;
    }
}

// 渲染函数

// 渲染用户表格
function renderUserTable() {
    elements.userTableBody.innerHTML = '';
    
    if (state.filteredUsers.length === 0) {
        renderEmptyState();
        return;
    }
    
    // 分页逻辑
    const startIndex = (state.currentPage - 1) * config.pageSize;
    const endIndex = Math.min(startIndex + config.pageSize, state.filteredUsers.length);
    const paginatedUsers = state.filteredUsers.slice(startIndex, endIndex);
    
    paginatedUsers.forEach(user => {
        const row = createUserTableRow(user);
        elements.userTableBody.appendChild(row);
    });
    
    // 更新显示信息
    elements.showingStart.textContent = state.filteredUsers.length > 0 ? (startIndex + 1) : '0';
    elements.showingEnd.textContent = endIndex;
}

// 创建用户表格行
function createUserTableRow(user) {
    const createdAt = new Date(user.createdAt).toLocaleString('zh-CN');
    const row = document.createElement('tr');
    row.className = 'hover:bg-gray-50 transition-colors';
    
    row.innerHTML = `
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            ${user.id}
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
            <div class="flex items-center">
                <div class="flex-shrink-0 h-10 w-10 bg-blue-100 rounded-full flex items-center justify-center text-primary">
                    ${user.username.charAt(0).toUpperCase()}
                </div>
                <div class="ml-4">
                    <div class="text-sm font-medium text-gray-900">${user.username}</div>
                </div>
            </div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap">
            <div class="text-sm text-gray-900">${user.email}</div>
        </td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            ${user.fullName || '-'}
        </td>
        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
            ${createdAt}
        </td>
        <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
            <button onclick="editUser(${user.id})" class="text-primary hover:text-primary/80 mr-3 transition-colors">
                <i class="fa fa-pencil mr-1"></i> 编辑
            </button>
            <button onclick="deleteUserConfirm(${user.id}, '${user.username}')" class="text-danger hover:text-danger/80 transition-colors">
                <i class="fa fa-trash mr-1"></i> 删除
            </button>
        </td>
    `;
    
    return row;
}

// 渲染空状态
function renderEmptyState() {
    elements.userTableBody.innerHTML = `
        <tr>
            <td colspan="6" class="px-6 py-8 text-center text-gray-500">
                <div class="flex flex-col items-center">
                    <div class="w-16 h-16 bg-gray-100 rounded-full flex items-center justify-center mb-4">
                        <i class="fa fa-users text-gray-400 text-2xl"></i>
                    </div>
                    <p class="text-gray-500">
                        ${state.isLoading ? '加载中...' : (state.searchTerm ? '没有找到匹配的用户' : '暂无用户数据')}
                    </p>
                    ${!state.isLoading && !state.searchTerm ? 
                        `<button onclick="showAddUserModal()" class="mt-3 px-4 py-2 bg-primary text-white rounded-md text-sm">
                            <i class="fa fa-plus mr-1"></i> 添加第一个用户
                        </button>` : ''
                    }
                </div>
            </td>
        </tr>
    `;
}

// 模态框相关函数

// 显示添加用户模态框
function showAddUserModal() {
    elements.modalTitle.textContent = '添加用户';
    elements.userForm.reset();
    document.getElementById('user-id').value = '';
    state.currentUserId = null;
    showUserModal();
}

// 编辑用户
window.editUser = async function(id) {
    try {
        showLoading(true);
        const user = await fetchUser(id);
        
        elements.modalTitle.textContent = '编辑用户';
        document.getElementById('user-id').value = user.id;
        document.getElementById('username').value = user.username;
        document.getElementById('password').value = ''; // 密码不回显
        document.getElementById('email').value = user.email;
        document.getElementById('fullName').value = user.fullName || '';
        
        state.currentUserId = user.id;
        showUserModal();
    } catch (error) {
        console.error('编辑用户失败:', error);
    } finally {
        hideLoading();
    }
};

// 删除用户确认
window.deleteUserConfirm = function(id, username) {
    state.deleteUserId = id;
    document.getElementById('confirm-message').textContent = 
        `您确定要删除用户 "${username}" 吗？此操作不可撤销。`;
    elements.confirmModal.classList.remove('hidden');
};

// 显示用户模态框
function showUserModal() {
    elements.userModal.classList.remove('hidden');
    // 触发重排以启用动画
    void elements.modalContent.offsetWidth;
    elements.modalContent.classList.remove('scale-95', 'opacity-0');
    elements.modalContent.classList.add('scale-100', 'opacity-100');
    
    // 自动聚焦第一个输入框
    setTimeout(() => {
        document.getElementById('username').focus();
    }, 300);
}

// 隐藏用户模态框
function hideUserModal() {
    elements.modalContent.classList.remove('scale-100', 'opacity-100');
    elements.modalContent.classList.add('scale-95', 'opacity-0');
    
    setTimeout(() => {
        elements.userModal.classList.add('hidden');
        elements.userForm.reset();
    }, 300);
}

// 隐藏确认删除模态框
function hideConfirmModal() {
    elements.confirmModal.classList.add('hidden');
    state.deleteUserId = null;
}

// 表单处理

// 处理用户表单提交
async function handleUserFormSubmit(e) {
    e.preventDefault();
    
    const formData = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value,
        email: document.getElementById('email').value,
        fullName: document.getElementById('fullName').value
    };
    
    // 表单验证
    if (!formData.username.trim()) {
        showNotification('用户名不能为空', 'error');
        document.getElementById('username').focus();
        return;
    }
    
    if (!formData.password.trim()) {
        showNotification('密码不能为空', 'error');
        document.getElementById('password').focus();
        return;
    }
    
    if (!formData.email.trim()) {
        showNotification('邮箱不能为空', 'error');
        document.getElementById('email').focus();
        return;
    }
    
    if (!isValidEmail(formData.email)) {
        showNotification('请输入有效的邮箱地址', 'error');
        document.getElementById('email').focus();
        return;
    }
    
    try {
        showLoading(true);
        
        if (state.currentUserId) {
            // 更新用户
            await updateUser(state.currentUserId, formData);
            showNotification('用户更新成功', 'success');
        } else {
            // 添加用户
            await createUser(formData);
            showNotification('用户添加成功', 'success');
        }
        
        hideUserModal();
        await fetchUsers();
        updateDashboardStats();
    } catch (error) {
        console.error('表单提交失败:', error);
    } finally {
        hideLoading();
    }
}

// 处理确认删除
async function handleConfirmDelete() {
    if (!state.deleteUserId) return;
    
    try {
        showLoading(true);
        await deleteUser(state.deleteUserId);
        hideConfirmModal();
        await fetchUsers();
        updateDashboardStats();
        showNotification('用户删除成功', 'success');
    } catch (error) {
        console.error('删除用户失败:', error);
    } finally {
        hideLoading();
    }
}

// 搜索功能

// 处理搜索
function handleSearch() {
    state.searchTerm = elements.searchInput.value.toLowerCase().trim();
    state.currentPage = 1; // 重置到第一页
    
    if (!state.searchTerm) {
        state.filteredUsers = [...state.users];
    } else {
        state.filteredUsers = state.users.filter(user => 
            user.username.toLowerCase().includes(state.searchTerm) ||
            user.email.toLowerCase().includes(state.searchTerm) ||
            (user.fullName && user.fullName.toLowerCase().includes(state.searchTerm))
        );
    }
    
    renderUserTable();
    updatePagination();
}

// 分页功能

// 更新分页
function updatePagination() {
    const totalPages = Math.ceil(state.filteredUsers.length / config.pageSize);
    
    elements.prevPageBtn.disabled = state.currentPage === 1;
    elements.nextPageBtn.disabled = state.currentPage === totalPages || totalPages === 0;
    
    elements.totalItems.textContent = state.filteredUsers.length;
}

// 上一页
function goToPrevPage() {
    if (state.currentPage > 1) {
        state.currentPage--;
        renderUserTable();
        updatePagination();
    }
}

// 下一页
function goToNextPage() {
    const totalPages = Math.ceil(state.filteredUsers.length / config.pageSize);
    if (state.currentPage < totalPages) {
        state.currentPage++;
        renderUserTable();
        updatePagination();
    }
}

// 通知功能

// 显示通知
function showNotification(message, type = 'success') {
    elements.notificationMessage.textContent = message;
    
    // 设置图标和样式
    if (type === 'success') {
        elements.notificationIcon.className = 'fa fa-check-circle text-success mr-3';
        elements.notification.className = 'fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg bg-green-50 border border-green-200 text-green-800 z-50';
    } else if (type === 'error') {
        elements.notificationIcon.className = 'fa fa-times-circle text-danger mr-3';
        elements.notification.className = 'fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg bg-red-50 border border-red-200 text-red-800 z-50';
    } else if (type === 'info') {
        elements.notificationIcon.className = 'fa fa-info-circle text-info mr-3';
        elements.notification.className = 'fixed top-4 right-4 px-6 py-3 rounded-lg shadow-lg bg-blue-50 border border-blue-200 text-blue-800 z-50';
    }
    
    // 显示通知
    elements.notification.style.transform = 'translateX(0)';
    
    // 自动隐藏
    setTimeout(() => {
        hideNotification();
    }, config.notificationTimeout);
}

// 隐藏通知
function hideNotification() {
    elements.notification.style.transform = 'translateX(calc(100% + 1rem))';
}

// 加载状态

// 显示加载状态
function showLoading(isModal = false) {
    state.isLoading = true;
    
    if (!isModal) {
        renderEmptyState();
    }
    
    // 显示全局加载指示器（如果需要）
    const loadingIndicator = document.getElementById('global-loading');
    if (loadingIndicator) {
        loadingIndicator.classList.remove('hidden');
    }
}

// 隐藏加载状态
function hideLoading() {
    setTimeout(() => {
        state.isLoading = false;
        
        // 隐藏全局加载指示器
        const loadingIndicator = document.getElementById('global-loading');
        if (loadingIndicator) {
            loadingIndicator.classList.add('hidden');
        }
    }, config.loadingDelay);
}

// 仪表板统计

// 更新仪表板统计数据
function updateDashboardStats() {
    // 更新用户总数
    elements.totalUsers.textContent = state.users.length;
    
    // 模拟计算今日新增用户（实际应用中应从API获取）
    const today = new Date().toDateString();
    const todayUsers = state.users.filter(user => {
        const userDate = new Date(user.createdAt).toDateString();
        return userDate === today;
    });
    
    state.lastNewUsers = todayUsers.length;
    elements.newUsers.textContent = state.lastNewUsers;
    
    // 模拟活跃用户数（实际应用中应从API获取）
    const activeUsersCount = Math.floor(state.users.length * 0.8);
    elements.activeUsers.textContent = activeUsersCount;
    
    // 模拟成功率（添加一些随机波动）
    const fluctuation = (Math.random() - 0.5) * 2; // -1% 到 +1% 的波动
    state.lastSuccessRate = Math.max(90, Math.min(100, Math.round(state.lastSuccessRate + fluctuation)));
    elements.successRate.textContent = `${state.lastSuccessRate}%`;
}

// 主题切换

// 检查深色模式偏好
function checkDarkModePreference() {
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
    const savedTheme = localStorage.getItem('theme');
    
    if (savedTheme === 'dark' || (!savedTheme && prefersDark)) {
        enableDarkMode();
    } else {
        disableDarkMode();
    }
}

// 切换主题
function toggleTheme() {
    if (state.isDarkMode) {
        disableDarkMode();
    } else {
        enableDarkMode();
    }
}

// 启用深色模式
function enableDarkMode() {
    document.documentElement.classList.add('dark');
    elements.themeToggle.innerHTML = '<i class="fa fa-sun-o"></i>';
    localStorage.setItem('theme', 'dark');
    state.isDarkMode = true;
}

// 禁用深色模式
function disableDarkMode() {
    document.documentElement.classList.remove('dark');
    elements.themeToggle.innerHTML = '<i class="fa fa-moon-o"></i>';
    localStorage.setItem('theme', 'light');
    state.isDarkMode = false;
}

// 辅助函数

// 防抖函数
function debounce(func, delay) {
    let timeoutId;
    return function(...args) {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func.apply(this, args), delay);
    };
}

// 验证邮箱
function isValidEmail(email) {
    const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(email);
}

// 自动刷新数据
function startAutoRefresh() {
    // 每30秒自动刷新一次数据
    setInterval(() => {
        if (!state.isLoading) {
            fetchUsers().then(() => {
                updateDashboardStats();
            });
        }
    }, 30000);
}

// 当DOM加载完成后初始化应用
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initApp);
} else {
    initApp();
}

// 导出一些公共函数供其他脚本使用
export {
    fetchUsers,
    createUser,
    updateUser,
    deleteUser,
    showNotification
};