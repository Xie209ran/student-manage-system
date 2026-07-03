// 作业管理页面组件
const HomeworkPage = {
    template: `
        <div class="homework-page">
            <!-- 发布作业 -->
            <div class="table-container" style="margin-bottom: 20px;">
                <div class="table-header">
                    <span class="table-title">发布作业</span>
                </div>
                
                <el-form :model="publishForm" :rules="publishRules" ref="publishFormRef" label-width="100px">
                    <el-form-item label="作业标题" prop="title">
                        <el-input v-model="publishForm.title" placeholder="2-100个字符"></el-input>
                    </el-form-item>
                    
                    <el-form-item label="作业内容" prop="content">
                        <el-input v-model="publishForm.content" type="textarea" :rows="4" placeholder="最多2000字符"></el-input>
                    </el-form-item>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="布置班级" prop="classIds">
                                <el-select v-model="publishForm.classIds" multiple placeholder="请选择班级" style="width: 100%">
                                    <el-option v-for="cls in classList" :key="cls.id" :label="cls.className" :value="cls.id"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="布置科目" prop="subject">
                                <el-select v-model="publishForm.subject" placeholder="请选择科目" style="width: 100%">
                                    <el-option label="语文" value="语文"></el-option>
                                    <el-option label="数学" value="数学"></el-option>
                                    <el-option label="英语" value="英语"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="截止时间" prop="deadline">
                                <el-date-picker
                                    v-model="publishForm.deadline"
                                    type="datetime"
                                    placeholder="选择日期时间"
                                    style="width: 100%"
                                ></el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="满分分值" prop="fullScore">
                                <el-input-number v-model="publishForm.fullScore" :min="1" :max="150" style="width: 100%"></el-input-number>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-form-item label="附件上传">
                        <el-upload action="#" :auto-upload="false" :on-change="handleFileChange">
                            <el-button type="primary">选择文件</el-button>
                            <div slot="tip" class="el-upload__tip">{{ fileName || '支持任意格式文件' }}</div>
                        </el-upload>
                    </el-form-item>
                    
                    <el-form-item>
                        <el-button type="primary" @click="handlePublish" :loading="publishLoading">发布作业</el-button>
                    </el-form-item>
                </el-form>
            </div>
            
            <!-- 作业列表 -->
            <div class="table-container">
                <div class="table-header">
                    <span class="table-title">作业列表</span>
                </div>
                
                <div class="search-form">
                    <el-form :inline="true" :model="searchForm">
                        <el-form-item label="班级">
                            <el-select v-model="searchForm.classId" placeholder="请选择班级" clearable>
                                <el-option v-for="cls in classList" :key="cls.id" :label="cls.className" :value="cls.id"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="科目">
                            <el-select v-model="searchForm.subject" placeholder="请选择科目" clearable>
                                <el-option label="语文" value="语文"></el-option>
                                <el-option label="数学" value="数学"></el-option>
                                <el-option label="英语" value="英语"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item label="状态">
                            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                                <el-option label="进行中" value="ongoing"></el-option>
                                <el-option label="已结束" value="ended"></el-option>
                                <el-option label="已批改" value="graded"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleReset">重置</el-button>
                        </el-form-item>
                    </el-form>
                </div>
                
                <el-table :data="paginatedData" style="width: 100%">
                    <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip></el-table-column>
                    <el-table-column prop="className" label="班级" width="150"></el-table-column>
                    <el-table-column prop="subject" label="科目" width="100"></el-table-column>
                    <el-table-column prop="teacherName" label="布置教师" width="120"></el-table-column>
                    <el-table-column prop="publishTime" label="发布时间" width="180"></el-table-column>
                    <el-table-column prop="deadline" label="截止时间" width="180"></el-table-column>
                    <el-table-column label="状态" width="100">
                        <template #default="{ row }">
                            <el-tag :type="getStatusType(row.status)">
                                {{ getStatusText(row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="提交进度" width="120">
                        <template #default="{ row }">
                            {{ row.submittedCount }}/{{ row.totalCount }}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" width="180" fixed="right">
                        <template #default="{ row }">
                            <el-button size="small" type="primary" @click="handleViewDetail(row)">查看详情</el-button>
                            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                
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
            
            <!-- 作业详情对话框 -->
            <el-dialog v-model="detailVisible" title="作业详情" width="900px">
                <div v-if="currentHomework">
                    <h3>{{ currentHomework.title }}</h3>
                    <p><strong>科目：</strong>{{ currentHomework.subject }}</p>
                    <p><strong>班级：</strong>{{ currentHomework.className }}</p>
                    <p><strong>截止时间：</strong>{{ currentHomework.deadline }}</p>
                    <p><strong>作业内容：</strong></p>
                    <div style="padding: 10px; background: #f5f7fa; border-radius: 4px;">{{ currentHomework.content }}</div>
                    
                    <h4 style="margin-top: 20px;">学生提交情况</h4>
                    <el-table :data="submissionList" style="width: 100%; margin-top: 10px;">
                        <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
                        <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
                        <el-table-column label="是否迟交" width="100">
                            <template #default="{ row }">
                                <el-tag v-if="row.isLate" type="danger" size="small">迟交</el-tag>
                                <el-tag v-else type="success" size="small">按时</el-tag>
                            </template>
                        </el-table-column>
                        <el-table-column prop="score" label="得分" width="100"></el-table-column>
                        <el-table-column prop="comment" label="评语" min-width="150"></el-table-column>
                        <el-table-column label="操作" width="100">
                            <template #default="{ row }">
                                <el-button v-if="!row.score && row.submitTime" size="small" type="primary" @click="handleGrade(row)">批改</el-button>
                                <span v-else-if="!row.submitTime" style="color: #909399;">未提交</span>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>
            </el-dialog>
            
            <!-- 批改对话框 -->
            <el-dialog v-model="gradeVisible" title="批改作业" width="500px">
                <el-form :model="gradeForm" label-width="80px">
                    <el-form-item label="学生">
                        <span>{{ gradeForm.studentName }}</span>
                    </el-form-item>
                    <el-form-item label="得分">
                        <el-input-number v-model="gradeForm.score" :min="0" :max="currentHomework ? currentHomework.fullScore : 100"></el-input-number>
                    </el-form-item>
                    <el-form-item label="评语">
                        <el-input v-model="gradeForm.comment" type="textarea" :rows="3" placeholder="最多500字符"></el-input>
                    </el-form-item>
                </el-form>
                <template #footer>
                    <el-button @click="gradeVisible = false">取消</el-button>
                    <el-button type="primary" @click="handleSubmitGrade">提交</el-button>
                </template>
            </el-dialog>
        </div>
    `,
    setup() {
        const classList = Vue.ref([]);
        
        const publishForm = Vue.ref({
            title: '',
            content: '',
            classIds: [],
            subject: '',
            deadline: '',
            fullScore: 100
        });
        
        const fileName = Vue.ref('');
        const publishLoading = Vue.ref(false);
        const publishFormRef = Vue.ref(null);
        
        const publishRules = {
            title: [
                { required: true, message: '请输入作业标题', trigger: 'blur' },
                { min: 2, max: 100, message: '标题长度为2-100个字符', trigger: 'blur' }
            ],
            content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }],
            classIds: [{ required: true, message: '至少选择一个班级', trigger: 'change', type: 'array' }],
            subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
            deadline: [{ required: true, message: '请选择截止时间', trigger: 'change' }],
            fullScore: [{ required: true, message: '请输入满分分值', trigger: 'blur' }]
        };
        
        const handleFileChange = (file) => {
            fileName.value = file.name;
        };
        
        const handlePublish = async () => {
            if (!publishFormRef.value) return;
            
            await publishFormRef.value.validate(async (valid) => {
                if (valid) {
                    publishLoading.value = true;
                    
                    try {
                        await http.post('/homeworks', publishForm.value);
                        ElementPlus.ElMessage.success('作业发布成功');
                        publishForm.value = { title: '', content: '', classIds: [], subject: '', deadline: '', fullScore: 100 };
                        fileName.value = '';
                        loadHomeworkList(); // 刷新作业列表
                    } catch (error) {
                        console.error('发布作业失败:', error);
                    } finally {
                        publishLoading.value = false;
                    }
                }
            });
        };
        
        // 作业列表
        const homeworkList = Vue.ref([]);
        
        const searchForm = Vue.ref({ classId: null, subject: '', status: '' });
        const pagination = Vue.ref({ pageNum: 1, pageSize: 10, total: 0 });
        
        // 过滤后的数据（后端已处理，直接返回）
        const filteredData = Vue.computed(() => {
            return homeworkList.value;
        });
        
        // 分页后的数据（后端已分页，直接返回）
        const paginatedData = Vue.computed(() => {
            return homeworkList.value;
        });
        
        const detailVisible = Vue.ref(false);
        const currentHomework = Vue.ref(null);
        const submissionList = Vue.ref([]);
        
        const gradeVisible = Vue.ref(false);
        const gradeForm = Vue.ref({ studentName: '', score: 0, comment: '' });
        
        // 加载班级列表
        const loadClassList = async () => {
            try {
                const classes = await http.get('/common/classes');
                classList.value = classes;
            } catch (error) {
                console.error('加载班级列表失败:', error);
            }
        };
        
        // 加载作业列表
        const loadHomeworkList = async () => {
            try {
                const result = await http.get('/homeworks', {
                    page: pagination.value.pageNum,
                    pageSize: pagination.value.pageSize,
                    classId: searchForm.value.classId,
                    subject: searchForm.value.subject,
                    status: searchForm.value.status
                });
                
                homeworkList.value = result.list || [];
                pagination.value.total = result.total || 0;
            } catch (error) {
                console.error('加载作业列表失败:', error);
            }
        };
        
        const handleViewDetail = async (row) => {
            currentHomework.value = row;
            detailVisible.value = true;
            
            // 加载提交情况
            try {
                const submissions = await http.get(`/homeworks/${row.id}/submissions`);
                submissionList.value = submissions || [];
            } catch (error) {
                console.error('加载提交情况失败:', error);
            }
        };
        
        const handleGrade = (row) => {
            gradeForm.value = { 
                id: row.id,
                studentName: row.studentName, 
                score: 0, 
                comment: '' 
            };
            gradeVisible.value = true;
        };
        
        const handleSubmitGrade = async () => {
            try {
                await http.put(`/homeworks/submissions/${gradeForm.value.id}`, {
                    score: gradeForm.value.score,
                    comment: gradeForm.value.comment
                });
                ElementPlus.ElMessage.success('批改成功');
                gradeVisible.value = false;
                // 刷新提交情况
                if (currentHomework.value) {
                    const submissions = await http.get(`/homeworks/${currentHomework.value.id}/submissions`);
                    submissionList.value = submissions || [];
                }
            } catch (error) {
                console.error('批改失败:', error);
            }
        };
        
        const handleDelete = (row) => {
            ElementPlus.ElMessageBox.confirm(
                '确定要删除该作业吗？',
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            ).then(async () => {
                try {
                    await http.delete(`/homeworks/${row.id}`);
                    ElementPlus.ElMessage.success('删除成功');
                    loadHomeworkList(); // 刷新列表
                } catch (error) {
                    console.error('删除失败:', error);
                    // 后端会返回是否有提交的警告
                }
            }).catch(() => {});
        };
        
        const getStatusType = (status) => {
            const map = { ongoing: 'success', ended: 'info', graded: 'primary' };
            return map[status] || '';
        };
        
        const getStatusText = (status) => {
            const map = { ongoing: '进行中', ended: '已结束', graded: '已批改' };
            return map[status] || status;
        };
        
        const handleSearch = () => {
            pagination.value.pageNum = 1;
            loadHomeworkList();
        };
        
        const handleReset = () => {
            searchForm.value = { classId: null, subject: '', status: '' };
            pagination.value.pageNum = 1;
            loadHomeworkList();
        };
        
        const handleSizeChange = (val) => {
            pagination.value.pageSize = val;
            pagination.value.pageNum = 1;
            loadHomeworkList();
        };
        
        const handlePageChange = (val) => {
            pagination.value.pageNum = val;
            loadHomeworkList();
        };
        
        // 组件挂载时加载数据
        Vue.onMounted(() => {
            loadClassList();
            loadHomeworkList();
        });
        
        return {
            classList, publishForm, fileName, publishLoading, publishFormRef, publishRules,
            searchForm, pagination, filteredData, paginatedData,
            detailVisible, currentHomework, submissionList,
            gradeVisible, gradeForm,
            handleFileChange, handlePublish, handleViewDetail, handleGrade, handleSubmitGrade,
            handleDelete, getStatusType, getStatusText,
            handleSearch, handleReset, handleSizeChange, handlePageChange
        };
    }
};
