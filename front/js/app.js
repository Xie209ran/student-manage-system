// 主应用脚本
const { createApp, ref, computed, onMounted } = Vue;

// 主应用
const app = createApp({
    components: {
        LoginPage,
        DashboardPage,
        StudentPage,
        TeacherPage,
        ClassPage,
        CoursePage,
        AttendancePage,
        ScorePage,
        HomeworkPage
    },
    setup() {
        const currentPage = ref('login');
        const currentMenu = ref('dashboard');
        const userInfo = ref({});
        
        const pageTitleMap = {
            dashboard: '仪表盘',
            student: '学生管理',
            teacher: '教师管理',
            class: '班级管理',
            course: '课程排课',
            attendance: '考勤打卡',
            score: '成绩管理',
            homework: '作业管理'
        };
        
        const pageTitle = computed(() => pageTitleMap[currentMenu.value] || '仪表盘');
        
        onMounted(async () => {
            const token = localStorage.getItem('token');
            const storedUserInfo = localStorage.getItem('userInfo');
            
            if (token && storedUserInfo) {
                // 尝试从后端获取最新的用户信息
                try {
                    const currentUser = await http.get('/auth/current');
                    userInfo.value = currentUser;
                    localStorage.setItem('userInfo', JSON.stringify(currentUser));
                    currentPage.value = 'main';
                } catch (error) {
                    // Token失效，清除本地存储
                    console.error('获取用户信息失败:', error);
                    localStorage.removeItem('token');
                    localStorage.removeItem('userInfo');
                }
            }
        });
        
        const handleLoginSuccess = () => {
            const storedUserInfo = localStorage.getItem('userInfo');
            if (storedUserInfo) {
                userInfo.value = JSON.parse(storedUserInfo);
            }
            currentPage.value = 'main';
        };
        
        const handleMenuSelect = (index) => {
            currentMenu.value = index;
        };
        
        const handleCommand = async (command) => {
            if (command === 'logout') {
                ElementPlus.ElMessageBox.confirm('确定要退出登录吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(async () => {
                    try {
                        // 调用后端退出接口
                        await http.post('/auth/logout');
                    } catch (error) {
                        console.error('退出登录失败:', error);
                    } finally {
                        // 无论成功失败，都清除本地存储
                        localStorage.removeItem('token');
                        localStorage.removeItem('userInfo');
                        currentPage.value = 'login';
                        ElementPlus.ElMessage.success('已退出登录');
                    }
                }).catch(() => {});
            } else if (command === 'profile') {
                ElementPlus.ElMessage.info('个人信息功能开发中');
            }
        };
        
        return {
            currentPage,
            currentMenu,
            userInfo,
            pageTitle,
            handleLoginSuccess,
            handleMenuSelect,
            handleCommand
        };
    }
});

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component);
}

app.use(ElementPlus);
app.mount('#app');
