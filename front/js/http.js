/**
 * HTTP请求封装 - 基于axios
 */

// 配置后端API基础URL
const API_BASE_URL = 'http://localhost:8080/api';

// 创建axios实例
const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    timeout: 30000, // 30秒超时
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
axiosInstance.interceptors.request.use(
    config => {
        // 从localStorage获取token
        const token = localStorage.getItem('token');
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
axiosInstance.interceptors.response.use(
    response => {
        const res = response.data;
        
        // 统一返回格式：{ code: 200, message: "success", data: {...} }
        if (res.code === 200) {
            return res.data; // 直接返回data部分
        } else if (res.code === 401) {
            // Token失效，跳转到登录页
            ElementPlus.ElMessage.error('登录已过期，请重新登录');
            localStorage.removeItem('token');
            localStorage.removeItem('userInfo');
            setTimeout(() => {
                window.location.href = '/login.html';
            }, 1500);
            return Promise.reject(new Error(res.message || '未授权'));
        } else {
            // 其他错误，显示错误消息
            ElementPlus.ElMessage.error(res.message || '请求失败');
            return Promise.reject(new Error(res.message || '请求失败'));
        }
    },
    error => {
        console.error('响应错误:', error);
        
        // 网络错误处理
        if (error.message.includes('Network Error')) {
            ElementPlus.ElMessage.error('网络连接失败，请检查后端服务是否启动');
        } else if (error.message.includes('timeout')) {
            ElementPlus.ElMessage.error('请求超时，请稍后重试');
        } else if (error.response) {
            // HTTP状态码错误
            const status = error.response.status;
            switch (status) {
                case 401:
                    ElementPlus.ElMessage.error('登录已过期，请重新登录');
                    localStorage.removeItem('token');
                    localStorage.removeItem('userInfo');
                    setTimeout(() => {
                        window.location.href = '/login.html';
                    }, 1500);
                    break;
                case 403:
                    ElementPlus.ElMessage.error('没有权限访问');
                    break;
                case 404:
                    ElementPlus.ElMessage.error('请求的资源不存在');
                    break;
                case 500:
                    ElementPlus.ElMessage.error('服务器内部错误');
                    break;
                default:
                    ElementPlus.ElMessage.error(error.message || '请求失败');
            }
        } else {
            ElementPlus.ElMessage.error('请求失败，请稍后重试');
        }
        
        return Promise.reject(error);
    }
);

// 导出封装的请求方法
const http = {
    // GET请求
    get(url, params = {}) {
        return axiosInstance.get(url, { params });
    },
    
    // POST请求
    post(url, data = {}) {
        return axiosInstance.post(url, data);
    },
    
    // PUT请求
    put(url, data = {}) {
        return axiosInstance.put(url, data);
    },
    
    // DELETE请求
    delete(url, params = {}) {
        return axiosInstance.delete(url, { params });
    }
};

// 导出供其他模块使用
window.http = http;
