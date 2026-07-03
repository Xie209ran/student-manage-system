// 班级管理页面组件
const ClassPage = {
    template: `
        <div class="class-page">
            <!-- 搜索表单 -->
            <div class="search-form">
                <el-form :inline="true" :model="searchForm">
                    <el-form-item label="班级名称">
                        <el-input v-model="searchForm.className" placeholder="请输入班级名称" clearable></el-input>
                    </el-form-item>
                    <el-form-item label="年级">
                        <el-select v-model="searchForm.grade" placeholder="请选择年级" clearable>
                            <el-option v-for="i in 12" :key="i" :label="i + '年级'" :value="i"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="负责老师">
                        <el-select v-model="searchForm.teacherId" placeholder="请选择老师" clearable filterable>
                            <el-option
                                v-for="teacher in teacherList"
                                :key="teacher.id"
                                :label="teacher.realName"
                                :value="teacher.id"
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
                    <span class="table-title">班级列表</span>
                    <div class="table-actions">
                        <el-button type="primary" @click="handleAdd">
                            <el-icon><Plus /></el-icon> 新增班级
                        </el-button>
                    </div>
                </div>
                
                <el-table :data="paginatedData" style="width: 100%">
                    <el-table-column prop="className" label="班级名称" width="150"></el-table-column>
                    <el-table-column prop="grade" label="年级" width="100">
                        <template #default="{ row }">
                            {{ row.grade }}年级
                        </template>
                    </el-table-column>
                    <el-table-column prop="teacherName" label="负责老师" width="120"></el-table-column>
                    <el-table-column label="学生人数" width="150">
                        <template #default="{ row }">
                            <span :class="getCapacityClass(row)">
                                {{ row.studentCount }}/{{ row.maxCapacity }}
                            </span>
                            <el-tag v-if="isNearCapacity(row)" size="small" type="warning" style="margin-left: 8px;">
                                已满{{ getCapacityPercent(row) }}%
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="classroom" label="教室位置" width="120"></el-table-column>
                    <el-table-column prop="establishDate" label="成立日期" width="120"></el-table-column>
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
                <el-form :model="classForm" :rules="rules" ref="classFormRef" label-width="100px">
                    <el-form-item label="班级名称" prop="className">
                        <el-input v-model="classForm.className" placeholder="2-30个字符"></el-input>
                    </el-form-item>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="年级" prop="grade">
                                <el-select v-model="classForm.grade" placeholder="请选择年级" style="width: 100%">
                                    <el-option v-for="i in 12" :key="i" :label="i + '年级'" :value="i"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="负责老师" prop="teacherId">
                                <el-select v-model="classForm.teacherId" placeholder="请选择老师" style="width: 100%" filterable>
                                    <el-option
                                        v-for="teacher in teacherList"
                                        :key="teacher.id"
                                        :label="teacher.realName"
                                        :value="teacher.id"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="教室位置" prop="classroom">
                                <el-input v-model="classForm.classroom" placeholder="如：A栋101"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="最大人数" prop="maxCapacity">
                                <el-input-number v-model="classForm.maxCapacity" :min="10" :max="100" style="width: 100%"></el-input-number>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-form-item label="成立日期" prop="establishDate">
                        <el-date-picker
                            v-model="classForm.establishDate"
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
        // 教师列表
        const teacherList = Vue.ref([]);
        
        // 班级列表
        const classList = Vue.ref([]);
        
        // 搜索表单
        const searchForm = Vue.ref({
            className: '',
            grade: null,
            teacherId: null
        });
        
        // 分页
        const pagination = Vue.ref({
            pageNum: 1,
            pageSize: 10,
            total: 0
        });
        
        // 对话框
        const dialogVisible = Vue.ref(false);
        const dialogTitle = Vue.ref('新增班级');
        const submitLoading = Vue.ref(false);
        const classFormRef = Vue.ref(null);
        
        // 班级表单
        const classForm = Vue.ref({
            id: null,
            className: '',
            grade: null,
            teacherId: null,
            classroom: '',
            maxCapacity: 50,
            establishDate: ''
        });
        
        // 表单校验规则
        const rules = {
            className: [
                { required: true, message: '请输入班级名称', trigger: 'blur' },
                { min: 2, max: 30, message: '班级名称长度为2-30个字符', trigger: 'blur' }
            ],
            grade: [
                { required: true, message: '请选择年级', trigger: 'change' }
            ],
            teacherId: [
                { required: true, message: '请选择负责老师', trigger: 'change' }
            ],
            maxCapacity: [
                { required: true, message: '请输入最大人数', trigger: 'blur' }
            ],
            establishDate: [
                { required: true, message: '请选择成立日期', trigger: 'change' }
            ]
        };
        
        // 加载教师列表
        const loadTeacherList = async () => {
            try {
                const teachers = await http.get('/common/teachers');
                teacherList.value = teachers;
            } catch (error) {
                console.error('加载教师列表失败:', error);
            }
        };
        
        // 加载班级列表
        const loadClassList = async () => {
            try {
                const result = await http.get('/classes', {
                    page: pagination.value.pageNum,
                    pageSize: pagination.value.pageSize,
                    className: searchForm.value.className,
                    grade: searchForm.value.grade,
                    teacherId: searchForm.value.teacherId
                });
                
                classList.value = result.list || [];
                pagination.value.total = result.total || 0;
            } catch (error) {
                console.error('加载班级列表失败:', error);
            }
        };
        
        // 过滤后的数据（后端已处理，直接返回）
        const filteredData = Vue.computed(() => {
            return classList.value;
        });
        
        // 分页后的数据（后端已分页，直接返回）
        const paginatedData = Vue.computed(() => {
            return classList.value;
        });
        
        // 计算容量百分比
        const getCapacityPercent = (row) => {
            return Math.round((row.studentCount / row.maxCapacity) * 100);
        };
        
        // 判断是否接近满员（90%）
        const isNearCapacity = (row) => {
            return getCapacityPercent(row) >= 90;
        };
        
        // 获取容量样式类
        const getCapacityClass = (row) => {
            const percent = getCapacityPercent(row);
            if (percent >= 95) return 'capacity-danger';
            if (percent >= 90) return 'capacity-warning';
            return 'capacity-normal';
        };
        
        // 搜索
        const handleSearch = () => {
            pagination.value.pageNum = 1;
            loadClassList();
        };
        
        // 重置
        const handleReset = () => {
            searchForm.value = {
                className: '',
                grade: null,
                teacherId: null
            };
            pagination.value.pageNum = 1;
            loadClassList();
        };
        
        // 新增
        const handleAdd = () => {
            dialogTitle.value = '新增班级';
            classForm.value = {
                id: null,
                className: '',
                grade: null,
                teacherId: null,
                classroom: '',
                maxCapacity: 50,
                establishDate: ''
            };
            dialogVisible.value = true;
        };
        
        // 编辑
        const handleEdit = (row) => {
            dialogTitle.value = '编辑班级';
            classForm.value = { ...row };
            dialogVisible.value = true;
        };
        
        // 提交
        const handleSubmit = async () => {
            if (!classFormRef.value) return;
            
            await classFormRef.value.validate(async (valid) => {
                if (valid) {
                    submitLoading.value = true;
                    
                    try {
                        // 准备提交数据
                        const submitData = { ...classForm.value };
                        
                        // 将日期对象转换为字符串格式
                        if (submitData.establishDate instanceof Date) {
                            const year = submitData.establishDate.getFullYear();
                            const month = String(submitData.establishDate.getMonth() + 1).padStart(2, '0');
                            const day = String(submitData.establishDate.getDate()).padStart(2, '0');
                            submitData.establishDate = `${year}-${month}-${day}`;
                        }
                        
                        if (submitData.id) {
                            // 编辑
                            await http.put(`/classes/${submitData.id}`, submitData);
                            ElementPlus.ElMessage.success('编辑成功');
                        } else {
                            // 新增
                            await http.post('/classes', submitData);
                            ElementPlus.ElMessage.success('新增成功');
                        }
                        
                        dialogVisible.value = false;
                        loadClassList(); // 刷新列表
                    } catch (error) {
                        console.error('操作失败:', error);
                        // 错误消息已在http.js中显示（如：班级名称已存在）
                    } finally {
                        submitLoading.value = false;
                    }
                }
            });
        };
        
        // 删除
        const handleDelete = (row) => {
            ElementPlus.ElMessageBox.confirm(
                `确定要删除班级“${row.className}”吗？`,
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            ).then(async () => {
                try {
                    await http.delete(`/classes/${row.id}`);
                    ElementPlus.ElMessage.success('删除成功');
                    loadClassList(); // 刷新列表
                } catch (error) {
                    console.error('删除失败:', error);
                    // 后端返回的错误消息会显示（如：该班级下有X名学生）
                }
            }).catch(() => {});
        };
        
        // 分页大小变化
        const handleSizeChange = (val) => {
            pagination.value.pageSize = val;
            pagination.value.pageNum = 1;
            loadClassList();
        };
        
        // 页码变化
        const handlePageChange = (val) => {
            pagination.value.pageNum = val;
            loadClassList();
        };
        
        // 组件挂载时加载数据
        Vue.onMounted(() => {
            loadTeacherList();
            loadClassList();
        });
        
        return {
            teacherList,
            searchForm,
            pagination,
            dialogVisible,
            dialogTitle,
            submitLoading,
            classFormRef,
            classForm,
            rules,
            filteredData,
            paginatedData,
            getCapacityPercent,
            isNearCapacity,
            getCapacityClass,
            handleSearch,
            handleReset,
            handleAdd,
            handleEdit,
            handleSubmit,
            handleDelete,
            handleSizeChange,
            handlePageChange
        };
    }
};
