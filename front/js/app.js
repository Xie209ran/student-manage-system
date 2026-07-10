const { createApp, ref, computed, onMounted } = Vue;

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
        HomeworkPage,
        ChatPage
    },
    setup() {
        const currentPage = ref('login');
        const currentMenu = ref('dashboard');
        const userInfo = ref({});

        const pageTitleMap = {
            dashboard: 'Dashboard',
            student: 'Students',
            teacher: 'Teachers',
            class: 'Classes',
            course: 'Schedule',
            attendance: 'Attendance',
            score: 'Scores',
            homework: 'Homework',
            chat: 'AI Chat'
        };

        const pageTitle = computed(() => pageTitleMap[currentMenu.value] || 'Dashboard');

        onMounted(async () => {
            const token = localStorage.getItem('token');
            const storedUserInfo = localStorage.getItem('userInfo');

            if (token && storedUserInfo) {
                try {
                    const currentUser = await http.get('/auth/current');
                    userInfo.value = currentUser;
                    localStorage.setItem('userInfo', JSON.stringify(currentUser));
                    currentPage.value = 'main';
                } catch (error) {
                    console.error('get user failed:', error);
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
                ElementPlus.ElMessageBox.confirm('Logout?', 'Confirm', {
                    confirmButtonText: 'Yes',
                    cancelButtonText: 'Cancel',
                    type: 'warning'
                }).then(async () => {
                    try {
                        await http.post('/auth/logout');
                    } catch (error) {
                        console.error('logout failed:', error);
                    } finally {
                        localStorage.removeItem('token');
                        localStorage.removeItem('userInfo');
                        currentPage.value = 'login';
                        ElementPlus.ElMessage.success('Logged out');
                    }
                }).catch(() => {});
            } else if (command === 'profile') {
                ElementPlus.ElMessage.info('Profile page coming soon');
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
