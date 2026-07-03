// 教师管理页面组件
const TeacherPage = {
    template: `
        <div class="teacher-page">
            <!-- 搜索和筛选 -->
            <el-card class="search-card">
                <el-form :inline="true" :model="searchForm" class="search-form">
                    <el-form-item label="教师姓名">
                        <el-input v-model="searchForm.name" placeholder="请输入教师姓名" clearable></el-input>
                    </el-form-item>
                    <el-form-item label="状态">
                        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                            <el-option label="启用" :value="1"></el-option>
                            <el-option label="禁用" :value="0"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadTeacherList">搜索</el-button>
                        <el-button @click="resetSearch">重置</el-button>
                        <el-button type="success" @click="showAddDialog">添加教师</el-button>
                    </el-form-item>
                </el-form>
            </el-card>

            <!-- 教师列表 -->
            <el-card class="table-card">
                <el-table 
                    :data="teacherList" 
                    v-loading="loading" 
                    stripe 
                    border
                    style="width: 100%">
                    <el-table-column prop="id" label="ID" width="80"></el-table-column>
                    <el-table-column prop="username" label="用户名" width="150"></el-table-column>
                    <el-table-column prop="realName" label="教师姓名" width="120"></el-table-column>
                    <el-table-column prop="phone" label="联系电话" width="150"></el-table-column>
                    <el-table-column prop="email" label="邮箱" width="200"></el-table-column>
                    <el-table-column label="性别" width="80">
                        <template #default="scope">
                            <el-tag :type="scope.row.gender === 1 ? '' : 'danger'" size="small">
                                {{ scope.row.gender === 1 ? '男' : '女' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="状态" width="100">
                        <template #default="scope">
                            <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
                                {{ scope.row.status === 1 ? '启用' : '禁用' }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" width="180">
                        <template #default="scope">
                            {{ formatDate(scope.row.createTime) }}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="200" fixed="right">
                        <template #default="scope">
                            <el-button 
                                size="small" 
                                type="primary" 
                                @click="showEditDialog(scope.row)">
                                编辑
                            </el-button>
                            <el-button 
                                size="small" 
                                :type="scope.row.status === 1 ? 'warning' : 'success'"
                                @click="toggleStatus(scope.row)">
                                {{ scope.row.status === 1 ? '禁用' : '启用' }}
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <!-- 分页 -->
                <el-pagination
                    v-model:current-page="pagination.page"
                    v-model:page-size="pagination.pageSize"
                    :total="pagination.total"
                    :page-sizes="[10, 20, 50, 100]"
                    layout="total, sizes, prev, pager, next, jumper"
                    @size-change="loadTeacherList"
                    @current-change="loadTeacherList"
                    style="margin-top: 20px; justify-content: flex-end;">
                </el-pagination>
            </el-card>

            <!-- 添加/编辑对话框 -->
            <el-dialog 
                v-model="dialogVisible" 
                :title="isEdit ? '编辑教师' : '添加教师'" 
                width="600px"
                @close="resetForm">
                <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
                    <el-form-item label="用户名" prop="username">
                        <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit"></el-input>
                    </el-form-item>
                    <el-form-item label="密码" prop="password" v-if="!isEdit">
                        <el-input v-model="form.password" type="password" placeholder="请输入密码（至少6位）" show-password></el-input>
                    </el-form-item>
                    <el-form-item label="教师姓名" prop="realName">
                        <el-input v-model="form.realName" placeholder="请输入教师姓名"></el-input>
                    </el-form-item>
                    <el-form-item label="性别" prop="gender">
                        <el-radio-group v-model="form.gender">
                            <el-radio :value="1">男</el-radio>
                            <el-radio :value="0">女</el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="联系电话" prop="phone">
                        <el-input v-model="form.phone" placeholder="请输入联系电话"></el-input>
                    </el-form-item>
                    <el-form-item label="邮箱" prop="email">
                        <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
                    </el-form-item>
                    <el-form-item label="状态" prop="status">
                        <el-radio-group v-model="form.status">
                            <el-radio :value="1">启用</el-radio>
                            <el-radio :value="0">禁用</el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    setup() {
        const teacherList = Vue.ref([]);
        const loading = Vue.ref(false);
        const dialogVisible = Vue.ref(false);
        const isEdit = Vue.ref(false);
        const submitting = Vue.ref(false);
        const formRef = Vue.ref(null);

        const searchForm = Vue.ref({
            name: '',
            status: null
        });

        const pagination = Vue.ref({
            page: 1,
            pageSize: 10,
            total: 0
        });

        const form = Vue.ref({
            id: null,
            username: '',
            password: '',
            realName: '',
            gender: 1,
            phone: '',
            email: '',
            status: 1
        });

        const rules = {
            username: [
                { required: true, message: '请输入用户名', trigger: 'blur' },
                { min: 3, max: 20, message: '用户名长度3-20个字符', trigger: 'blur' }
            ],
            password: [
                { required: true, message: '请输入密码', trigger: 'blur' },
                { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
            ],
            realName: [
                { required: true, message: '请输入教师姓名', trigger: 'blur' }
            ],
            phone: [
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
            ],
            email: [
                { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
            ]
        };

        // 加载教师列表
        const loadTeacherList = async () => {
            loading.value = true;
            try {
                const params = {
                    page: pagination.value.page,
                    pageSize: pagination.value.pageSize,
                    name: searchForm.value.name,
                    status: searchForm.value.status
                };
                const result = await http.get('/user/list', params);
                teacherList.value = result.list || [];
                pagination.value.total = result.total || 0;
            } catch (error) {
                console.error('加载教师列表失败:', error);
            } finally {
                loading.value = false;
            }
        };

        // 重置搜索
        const resetSearch = () => {
            searchForm.value = { name: '', status: null };
            pagination.value.page = 1;
            loadTeacherList();
        };

        // 显示添加对话框
        const showAddDialog = () => {
            isEdit.value = false;
            dialogVisible.value = true;
        };

        // 显示编辑对话框
        const showEditDialog = (row) => {
            isEdit.value = true;
            form.value = {
                id: row.id,
                username: row.username,
                realName: row.realName,
                gender: row.gender,
                phone: row.phone,
                email: row.email,
                status: row.status
            };
            dialogVisible.value = true;
        };

        // 重置表单
        const resetForm = () => {
            if (formRef.value) {
                formRef.value.resetFields();
            }
            form.value = {
                id: null,
                username: '',
                password: '',
                realName: '',
                gender: 1,
                phone: '',
                email: '',
                status: 1
            };
        };

        // 提交表单
        const handleSubmit = async () => {
            if (!formRef.value) return;
            
            await formRef.value.validate(async (valid) => {
                if (valid) {
                    submitting.value = true;
                    try {
                        if (isEdit.value) {
                            await http.put('/user/update', form.value);
                            ElementPlus.ElMessage.success('更新成功');
                        } else {
                            await http.post('/user/add', form.value);
                            ElementPlus.ElMessage.success('添加成功');
                        }
                        dialogVisible.value = false;
                        loadTeacherList();
                    } catch (error) {
                        console.error('提交失败:', error);
                    } finally {
                        submitting.value = false;
                    }
                }
            });
        };

        // 切换状态
        const toggleStatus = async (row) => {
            const newStatus = row.status === 1 ? 0 : 1;
            const action = newStatus === 1 ? '启用' : '禁用';
            
            try {
                await ElementPlus.ElMessageBox.confirm(`确定要${action}该教师吗？`, '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                });
                
                await http.put('/user/update', {
                    id: row.id,
                    status: newStatus
                });
                
                ElementPlus.ElMessage.success(`${action}成功`);
                loadTeacherList();
            } catch (error) {
                if (error !== 'cancel') {
                    console.error(`${action}失败:`, error);
                }
            }
        };

        // 格式化日期
        const formatDate = (dateStr) => {
            if (!dateStr) return '-';
            return new Date(dateStr).toLocaleString('zh-CN');
        };

        // 初始化
        Vue.onMounted(() => {
            loadTeacherList();
        });

        return {
            teacherList,
            loading,
            dialogVisible,
            isEdit,
            submitting,
            formRef,
            searchForm,
            pagination,
            form,
            rules,
            loadTeacherList,
            resetSearch,
            showAddDialog,
            showEditDialog,
            resetForm,
            handleSubmit,
            toggleStatus,
            formatDate
        };
    }
};
