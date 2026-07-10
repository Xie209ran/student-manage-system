// login component
const LoginPage = {
    template: `
        <div class="login-container">
            <div class="login-box">
                <h2 class="login-title">EduM Platform</h2>
                <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
                    <el-form-item prop="username">
                        <el-input
                            v-model="loginForm.username"
                            placeholder="username"
                            prefix-icon="User"
                            size="large"
                            clearable
                        ></el-input>
                    </el-form-item>
                    <el-form-item prop="password">
                        <el-input
                            v-model="loginForm.password"
                            type="password"
                            placeholder="password"
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
                            {{ loading ? 'logging in...' : 'Login' }}
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

        const rules = {
            username: [
                { required: true, message: 'username required', trigger: 'blur' }
            ],
            password: [
                { required: true, message: 'password required', trigger: 'blur' },
                { min: 6, message: 'min 6 chars', trigger: 'blur' }
            ]
        };

        const handleLogin = async () => {
            if (!loginFormRef.value) return;

            await loginFormRef.value.validate(async (valid) => {
                if (valid) {
                    loading.value = true;

                    try {
                        const result = await http.post('/auth/login', {
                            username: loginForm.value.username,
                            password: loginForm.value.password
                        });

                        localStorage.setItem('token', result.token);
                        localStorage.setItem('userInfo', JSON.stringify({
                            id: result.userInfo.id,
                            username: result.userInfo.username,
                            realName: result.userInfo.realName,
                            role: result.userInfo.role
                        }));

                        ElementPlus.ElMessage.success('login success');
                        emit('login-success');
                    } catch (error) {
                        console.error('login error:', error);
                    } finally {
                        loading.value = false;
                    }
                } else {
                    ElementPlus.ElMessage.warning('please complete form');
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
