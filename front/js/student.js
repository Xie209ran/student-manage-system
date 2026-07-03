// 学生管理页面组件
const StudentPage = {
    template: `
        <div class="student-page">
            <!-- 搜索表单 -->
            <div class="search-form">
                <el-form :inline="true" :model="searchForm">
                    <el-form-item label="姓名">
                        <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable></el-input>
                    </el-form-item>
                    <el-form-item label="班级">
                        <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable>
                            <el-option
                                v-for="cls in classList"
                                :key="cls.id"
                                :label="cls.className"
                                :value="cls.id"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="handleSearch">
                            <el-icon><Search /></el-icon> 搜索
                        </el-button>
                        <el-button @click="handleReset">
                            <el-icon><Refresh /></el-icon> 重置
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
            
            <!-- 表格容器 -->
            <div class="table-container">
                <div class="table-header">
                    <span class="table-title">学生列表</span>
                    <div class="table-actions">
                        <el-button type="primary" @click="handleAdd">
                            <el-icon><Plus /></el-icon> 新增学生
                        </el-button>
                        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
                            <el-icon><Delete /></el-icon> 批量删除
                        </el-button>
                    </div>
                </div>
                
                <el-table
                    :data="paginatedData"
                    style="width: 100%"
                    @selection-change="handleSelectionChange"
                >
                    <el-table-column type="selection" width="55"></el-table-column>
                    <el-table-column prop="studentNo" label="学号" width="120"></el-table-column>
                    <el-table-column prop="name" label="姓名" width="100"></el-table-column>
                    <el-table-column prop="gender" label="性别" width="80">
                        <template #default="{ row }">
                            {{ row.gender === 1 ? '男' : '女' }}
                        </template>
                    </el-table-column>
                    <el-table-column prop="className" label="班级" width="120"></el-table-column>
                    <el-table-column prop="phone" label="联系电话" width="130"></el-table-column>
                    <el-table-column prop="address" label="家庭住址" min-width="200" show-overflow-tooltip></el-table-column>
                    <el-table-column label="操作" width="180" fixed="right">
                        <template #default="{ row }">
                            <el-button size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
                            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
                <!-- 分页 -->
                <div class="pagination">
                    <el-pagination
                        v-model:current-page="pagination.pageNum"
                        v-model:page-size="pagination.pageSize"
                        :page-sizes="[10, 20, 50]"
                        :total="pagination.total"
                        layout="total, sizes, prev, pager, next, jumper"
                        @size-change="handleSizeChange"
                        @current-change="handlePageChange"
                    ></el-pagination>
                </div>
            </div>
            
            <!-- 新增/编辑对话框 -->
            <el-dialog
                v-model="dialogVisible"
                :title="dialogTitle"
                width="600px"
            >
                <el-form :model="studentForm" :rules="rules" ref="studentFormRef" label-width="100px">
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="学号" prop="studentNo">
                                <el-input v-model="studentForm.studentNo" placeholder="8-12位数字或字母" :disabled="!!studentForm.id"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="姓名" prop="name">
                                <el-input v-model="studentForm.name" placeholder="2-20个字符"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="性别" prop="gender">
                                <el-radio-group v-model="studentForm.gender">
                                    <el-radio :label="1">男</el-radio>
                                    <el-radio :label="2">女</el-radio>
                                </el-radio-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="出生日期" prop="birthDate">
                                <el-date-picker
                                    v-model="studentForm.birthDate"
                                    type="date"
                                    placeholder="选择日期"
                                    style="width: 100%"
                                ></el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="班级" prop="classId">
                                <el-select v-model="studentForm.classId" placeholder="请选择班级" style="width: 100%">
                                    <el-option
                                        v-for="cls in classList"
                                        :key="cls.id"
                                        :label="cls.className"
                                        :value="cls.id"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="联系电话" prop="phone">
                                <el-input v-model="studentForm.phone" placeholder="11位手机号"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-form-item label="家庭住址" prop="address">
                        <el-input v-model="studentForm.address" type="textarea" :rows="2" placeholder="最多200字符"></el-input>
                    </el-form-item>
                    
                    <el-form-item label="入学日期" prop="enrollmentDate">
                        <el-date-picker
                            v-model="studentForm.enrollmentDate"
                            type="date"
                            placeholder="选择日期"
                            style="width: 100%"
                        ></el-date-picker>
                    </el-form-item>
                </el-form>
                
                <template #footer>
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    setup() {
        // 班级列表
        const classList = Vue.ref([]);
        
        // 学生列表
        const studentList = Vue.ref([]);
        
        // 搜索表单
        const searchForm = Vue.ref({
            name: '',
            classId: null
        });
        
        // 分页
        const pagination = Vue.ref({
            pageNum: 1,
            pageSize: 10,
            total: 0
        });
        
        // 选中的ID
        const selectedIds = Vue.ref([]);
        
        // 对话框
        const dialogVisible = Vue.ref(false);
        const dialogTitle = Vue.ref('新增学生');
        const submitLoading = Vue.ref(false);
        const studentFormRef = Vue.ref(null);
        
        // 学生表单
        const studentForm = Vue.ref({
            id: null,
            studentNo: '',
            name: '',
            gender: 1,
            birthDate: '',
            classId: null,
            phone: '',
            address: '',
            enrollmentDate: ''
        });
        
        // 表单校验规则
        const rules = {
            studentNo: [
                { required: true, message: '请输入学号', trigger: 'blur' },
                { pattern: /^[a-zA-Z0-9]{8,12}$/, message: '学号格式不正确（8-12位数字或字母）', trigger: 'blur' }
            ],
            name: [
                { required: true, message: '请输入姓名', trigger: 'blur' },
                { min: 2, max: 20, message: '姓名长度为2-20个字符', trigger: 'blur' }
            ],
            gender: [
                { required: true, message: '请选择性别', trigger: 'change' }
            ],
            birthDate: [
                { required: true, message: '请选择出生日期', trigger: 'change' }
            ],
            classId: [
                { required: true, message: '请选择班级', trigger: 'change' }
            ],
            phone: [
                { required: true, message: '请输入联系电话', trigger: 'blur' },
                { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
            ],
            address: [
                { max: 200, message: '地址不能超过200字符', trigger: 'blur' }
            ],
            enrollmentDate: [
                { required: true, message: '请选择入学日期', trigger: 'change' }
            ]
        };
        
        // 加载班级列表
        const loadClassList = async () => {
            try {
                const classes = await http.get('/common/classes');
                classList.value = classes;
            } catch (error) {
                console.error('加载班级列表失败:', error);
            }
        };
        
        // 加载学生列表
        const loadStudentList = async () => {
            try {
                const result = await http.get('/students', {
                    page: pagination.value.pageNum,
                    pageSize: pagination.value.pageSize,
                    name: searchForm.value.name,
                    classId: searchForm.value.classId
                });
                
                studentList.value = result.list || [];
                pagination.value.total = result.total || 0;
            } catch (error) {
                console.error('加载学生列表失败:', error);
            }
        };
        
        // 过滤后的数据（后端已处理，直接返回）
        const filteredData = Vue.computed(() => {
            return studentList.value;
        });
        
        // 分页后的数据（后端已分页，直接返回）
        const paginatedData = Vue.computed(() => {
            return studentList.value;
        });
        
        // 搜索
        const handleSearch = () => {
            pagination.value.pageNum = 1;
            loadStudentList();
        };
        
        // 重置
        const handleReset = () => {
            searchForm.value = {
                name: '',
                classId: null
            };
            pagination.value.pageNum = 1;
            loadStudentList();
        };
        
        // 选择变化
        const handleSelectionChange = (selection) => {
            selectedIds.value = selection.map(s => s.id);
        };
        
        // 新增
        const handleAdd = () => {
            dialogTitle.value = '新增学生';
            studentForm.value = {
                id: null,
                studentNo: '',
                name: '',
                gender: 1,
                birthDate: '',
                classId: null,
                phone: '',
                address: '',
                enrollmentDate: ''
            };
            dialogVisible.value = true;
        };
        
        // 编辑
        const handleEdit = (row) => {
            dialogTitle.value = '编辑学生';
            studentForm.value = { ...row };
            dialogVisible.value = true;
        };
        
        // 提交
        const handleSubmit = async () => {
            if (!studentFormRef.value) return;
            
            await studentFormRef.value.validate(async (valid) => {
                if (valid) {
                    submitLoading.value = true;
                    
                    try {
                        // 准备提交数据
                        const submitData = { ...studentForm.value };
                        
                        // 将日期对象转换为字符串格式
                        if (submitData.birthDate instanceof Date) {
                            const year = submitData.birthDate.getFullYear();
                            const month = String(submitData.birthDate.getMonth() + 1).padStart(2, '0');
                            const day = String(submitData.birthDate.getDate()).padStart(2, '0');
                            submitData.birthDate = `${year}-${month}-${day}`;
                        }
                        
                        if (submitData.enrollmentDate instanceof Date) {
                            const year = submitData.enrollmentDate.getFullYear();
                            const month = String(submitData.enrollmentDate.getMonth() + 1).padStart(2, '0');
                            const day = String(submitData.enrollmentDate.getDate()).padStart(2, '0');
                            submitData.enrollmentDate = `${year}-${month}-${day}`;
                        }
                        
                        if (submitData.id) {
                            // 编辑
                            await http.put(`/students/${submitData.id}`, submitData);
                            ElementPlus.ElMessage.success('编辑成功');
                        } else {
                            // 新增
                            await http.post('/students', submitData);
                            ElementPlus.ElMessage.success('新增成功');
                        }
                        
                        dialogVisible.value = false;
                        loadStudentList(); // 刷新列表
                    } catch (error) {
                        console.error('操作失败:', error);
                        // 错误消息已在http.js中显示
                    } finally {
                        submitLoading.value = false;
                    }
                }
            });
        };
        
        // 删除
        const handleDelete = (row) => {
            ElementPlus.ElMessageBox.confirm(
                `确定要删除学生“${row.name}”吗？`,
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            ).then(async () => {
                try {
                    await http.delete(`/students/${row.id}`);
                    ElementPlus.ElMessage.success('删除成功');
                    loadStudentList(); // 刷新列表
                } catch (error) {
                    console.error('删除失败:', error);
                    // 后端返回的错误消息会显示（如：该学生有待批改作业）
                }
            }).catch(() => {});
        };
                
        // 批量删除
        const handleBatchDelete = () => {
            if (selectedIds.value.length === 0) {
                ElementPlus.ElMessage.warning('请至少选择一条记录');
                return;
            }
                    
            ElementPlus.ElMessageBox.confirm(
                `确定要删除选中的${selectedIds.value.length}名学生吗？`,
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            ).then(async () => {
                try {
                    await http.delete('/students/batch', {
                        ids: selectedIds.value
                    });
                    ElementPlus.ElMessage.success('批量删除成功');
                    selectedIds.value = [];
                    loadStudentList(); // 刷新列表
                } catch (error) {
                    console.error('批量删除失败:', error);
                }
            }).catch(() => {});
        };
        
        // 分页大小变化
        const handleSizeChange = (val) => {
            pagination.value.pageSize = val;
            pagination.value.pageNum = 1;
            loadStudentList();
        };
        
        // 页码变化
        const handlePageChange = (val) => {
            pagination.value.pageNum = val;
            loadStudentList();
        };
        
        // 组件挂载时加载数据
        Vue.onMounted(() => {
            loadClassList();
            loadStudentList();
        });
        
        return {
            classList,
            searchForm,
            pagination,
            selectedIds,
            dialogVisible,
            dialogTitle,
            submitLoading,
            studentFormRef,
            studentForm,
            rules,
            filteredData,
            paginatedData,
            handleSearch,
            handleReset,
            handleSelectionChange,
            handleAdd,
            handleEdit,
            handleSubmit,
            handleDelete,
            handleBatchDelete,
            handleSizeChange,
            handlePageChange
        };
    }
};
