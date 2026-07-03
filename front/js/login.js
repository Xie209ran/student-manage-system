// 登录页面组件
const LoginPage = {
    template: `
        <div class="login-container">
            <div class="login-box">
                <h2 class="login-title">教学数字平台后台管理系统</h2>
                <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
                    <el-form-item prop="username">
                        <el-input 
                            v-model="loginForm.username" 
                            placeholder="请输入用户名"
                            prefix-icon="User"
                            size="large"
                            clearable
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input 
                            v-model="loginForm.password" 
                            type="password"
                            placeholder="请输入密码"
                            prefix-icon="Lock"
                            size="large"
                            show-password
                            @keyup.enter="handleLogin"
                        ></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button 
                            type="primary" 
                            size="large" 
                            class="login-button"
                            :loading="loading"
                            @click="handleLogin"
                        >
                            {{ loading ? '登录中...' : '登 录' }}
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
        </div>
    `,
    setup(props, { emit }) {
        const loginForm = Vue.ref({
            username: '',
            password: ''
        });
        const loginFormRef = Vue.ref(null);
        const loading = Vue.ref(false);
        
        // 表单校验规则
        const rules = {
            username: [
                { required: true, message: '请输入用户名', trigger: 'blur' }
            ],
            password: [
                { required: true, message: '请输入密码', trigger: 'blur' },
                { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
            ]
        };
        
        // 登录处理
        const handleLogin = async () => {
            if (!loginFormRef.value) return;
            
            await loginFormRef.value.validate(async (valid) => {
                if (valid) {
                    loading.value = true;
                    
                    try {
                        // 调用后端登录接口
                        const result = await http.post('/auth/login', {
                            username: loginForm.value.username,
                            password: loginForm.value.password
                        });
                        
                        // 登录成功，保存token和用户信息
                        localStorage.setItem('token', result.token);
                        localStorage.setItem('userInfo', JSON.stringify({
                            id: result.userId,
                            username: result.username,
                            realName: result.realName,
                            role: result.role
                        }));
                        
                        ElementPlus.ElMessage.success('登录成功');
                        emit('login-success');
                    } catch (error) {
                        console.error('登录失败:', error);
                        // 错误消息已在http.js中显示
                    } finally {
                        loading.value = false;
                    }
                } else {
                    ElementPlus.ElMessage.warning('请填写完整的登录信息');
                    return false;
                }
            });
        };
        
        return {
            loginForm,
            loginFormRef,
            loading,
            rules,
            handleLogin
        };
    }
};
