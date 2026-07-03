// 成绩管理页面组件
const ScorePage = {
    template: `
        <div class="score-page">
            <!-- 批量录入区域 -->
            <div class="table-container" style="margin-bottom: 20px;">
                <div class="table-header">
                    <span class="table-title">成绩批量录入</span>
                </div>
                
                <el-form :inline="true" style="margin-bottom: 20px;">
                    <el-form-item label="班级">
                        <el-select v-model="scoreInputForm.classId" placeholder="请选择班级" @change="handleClassChangeForInput">
                            <el-option v-for="cls in classList" :key="cls.id" :label="cls.className" :value="cls.id"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="科目">
                        <el-select v-model="scoreInputForm.subject" placeholder="请选择科目">
                            <el-option label="语文" value="语文"></el-option>
                            <el-option label="数学" value="数学"></el-option>
                            <el-option label="英语" value="英语"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="考试名称">
                        <el-input v-model="scoreInputForm.examName" placeholder="如：期中考试"></el-input>
                    </el-form-item>
                    <el-form-item label="考试日期">
                        <el-date-picker v-model="scoreInputForm.examDate" type="date" placeholder="选择日期"></el-date-picker>
                    </el-form-item>
                    <el-form-item label="满分值">
                        <el-input-number v-model="scoreInputForm.fullScore" :min="1" :max="150"></el-input-number>
                    </el-form-item>
                </el-form>
                
                <el-table :data="studentScoreList" style="width: 100%" v-if="scoreInputForm.classId">
                    <el-table-column prop="name" label="学生姓名" width="120"></el-table-column>
                    <el-table-column label="成绩" width="150">
                        <template #default="{ row }">
                            <el-input-number 
                                v-model="row.score" 
                                :min="0" 
                                :max="scoreInputForm.fullScore"
                                :disabled="row.isAbsent"
                                size="small"
                            ></el-input-number>
                        </template>
                    </el-table-column>
                    <el-table-column label="缺考" width="80">
                        <template #default="{ row }">
                            <el-checkbox v-model="row.isAbsent" @change="handleAbsentChange(row)"></el-checkbox>
                        </template>
                    </el-table-column>
                    <el-table-column label="备注">
                        <template #default="{ row }">
                            <el-input v-model="row.remark" placeholder="选填" size="small"></el-input>
                        </template>
                    </el-table-column>
                </el-table>
                
                <el-button type="primary" @click="handleSubmitScores" :loading="submitLoading" style="margin-top: 20px;" v-if="scoreInputForm.classId">
                    批量保存
                </el-button>
            </div>
            
            <!-- 成绩查询 -->
            <div class="table-container">
                <div class="table-header">
                    <span class="table-title">成绩查询</span>
                </div>
                
                <div class="search-form">
                    <el-form :inline="true" :model="searchForm">
                        <el-form-item label="学生">
                            <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable></el-input>
                        </el-form-item>
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
                        <el-form-item>
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleReset">重置</el-button>
                        </el-form-item>
                    </el-form>
                </div>
                
                <!-- 统计信息 -->
                <el-row :gutter="20" style="margin-bottom: 20px;">
                    <el-col :span="6">
                        <div class="stat-card-mini">
                            <div class="stat-label">平均分</div>
                            <div class="stat-value">{{ statistics.avgScore }}</div>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="stat-card-mini">
                            <div class="stat-label">最高分</div>
                            <div class="stat-value">{{ statistics.maxScore }}</div>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="stat-card-mini">
                            <div class="stat-label">最低分</div>
                            <div class="stat-value">{{ statistics.minScore }}</div>
                        </div>
                    </el-col>
                    <el-col :span="6">
                        <div class="stat-card-mini">
                            <div class="stat-label">及格率</div>
                            <div class="stat-value">{{ statistics.passRate }}%</div>
                        </div>
                    </el-col>
                </el-row>
                
                <el-table :data="paginatedData" style="width: 100%">
                    <el-table-column prop="studentName" label="学生" width="120"></el-table-column>
                    <el-table-column prop="className" label="班级" width="150"></el-table-column>
                    <el-table-column prop="subject" label="科目" width="100"></el-table-column>
                    <el-table-column prop="examName" label="考试名称" width="150"></el-table-column>
                    <el-table-column prop="score" label="成绩" width="100"></el-table-column>
                    <el-table-column prop="fullScore" label="满分" width="100"></el-table-column>
                    <el-table-column prop="examDate" label="考试日期" width="120"></el-table-column>
                    <el-table-column prop="rank" label="排名" width="80"></el-table-column>
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
        </div>
    `,
    setup() {
        const classList = Vue.ref([]);
        
        const scoreInputForm = Vue.ref({
            classId: null,
            subject: '',
            examName: '',
            examDate: '',
            fullScore: 100
        });
        
        const studentList = Vue.ref([]);
        
        const studentScoreList = Vue.computed(() => {
            if (!scoreInputForm.value.classId) return [];
            return studentList.value.map(s => ({ ...s, score: 0, isAbsent: false, remark: '' }));
        });
        
        const submitLoading = Vue.ref(false);
        
        // 成绩记录
        const scoreRecords = Vue.ref([]);
        
        const searchForm = Vue.ref({ studentName: '', classId: null, subject: '' });
        const pagination = Vue.ref({ pageNum: 1, pageSize: 10, total: 0 });
        
        // 过滤后的数据（后端已处理，直接返回）
        const filteredData = Vue.computed(() => {
            return scoreRecords.value;
        });
        
        // 分页后的数据（后端已分页，直接返回）
        const paginatedData = Vue.computed(() => {
            return scoreRecords.value;
        });
        
        const statistics = Vue.computed(() => {
            const scores = filteredData.value.map(r => r.score);
            if (scores.length === 0) return { avgScore: 0, maxScore: 0, minScore: 0, passRate: 0 };
            
            const avg = (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(1);
            const max = Math.max(...scores);
            const min = Math.min(...scores);
            const passCount = scores.filter(s => s >= 60).length;
            const passRate = ((passCount / scores.length) * 100).toFixed(1);
            
            return { avgScore: avg, maxScore: max, minScore: min, passRate };
        });
        
        // 加载班级列表
        const loadClassList = async () => {
            try {
                const classes = await http.get('/common/classes');
                classList.value = classes;
            } catch (error) {
                console.error('加载班级列表失败:', error);
            }
        };
        
        // 加载学生列表（用于成绩录入）
        const loadStudentList = async () => {
            if (!scoreInputForm.value.classId) return;
            
            try {
                const students = await http.get('/students', {
                    classId: scoreInputForm.value.classId,
                    pageSize: 100
                });
                studentList.value = students.list || [];
            } catch (error) {
                console.error('加载学生列表失败:', error);
            }
        };
        
        // 加载成绩记录
        const loadScoreRecords = async () => {
            try {
                const result = await http.get('/scores', {
                    page: pagination.value.pageNum,
                    pageSize: pagination.value.pageSize,
                    studentName: searchForm.value.studentName,
                    classId: searchForm.value.classId,
                    subject: searchForm.value.subject
                });
                
                scoreRecords.value = result.list || [];
                pagination.value.total = result.total || 0;
            } catch (error) {
                console.error('加载成绩记录失败:', error);
            }
        };
        
        const handleClassChangeForInput = () => {
            loadStudentList();
        };
        
        const handleAbsentChange = (row) => {
            if (row.isAbsent) {
                row.score = 0;
            }
        };
        
        const handleSubmitScores = async () => {
            submitLoading.value = true;
            
            try {
                // 构建批量成绩数据
                const scoresData = studentScoreList.value.map(s => ({
                    studentId: s.id,
                    classId: scoreInputForm.value.classId,
                    subject: scoreInputForm.value.subject,
                    examName: scoreInputForm.value.examName,
                    examDate: scoreInputForm.value.examDate,
                    score: s.isAbsent ? 0 : s.score,
                    fullScore: scoreInputForm.value.fullScore,
                    isAbsent: s.isAbsent,
                    remark: s.remark || ''
                }));
                
                await http.post('/scores/batch', scoresData);
                ElementPlus.ElMessage.success('成绩保存成功');
                loadScoreRecords(); // 刷新成绩记录
            } catch (error) {
                console.error('保存成绩失败:', error);
                // 后端会返回重复录入等错误消息
            } finally {
                submitLoading.value = false;
            }
        };
        
        const handleSearch = () => {
            pagination.value.pageNum = 1;
            loadScoreRecords();
        };
        
        const handleReset = () => {
            searchForm.value = { studentName: '', classId: null, subject: '' };
            pagination.value.pageNum = 1;
            loadScoreRecords();
        };
        
        const handleSizeChange = (val) => {
            pagination.value.pageSize = val;
            pagination.value.pageNum = 1;
            loadScoreRecords();
        };
        
        const handlePageChange = (val) => {
            pagination.value.pageNum = val;
            loadScoreRecords();
        };
        
        // 组件挂载时加载数据
        Vue.onMounted(() => {
            loadClassList();
            loadScoreRecords();
        });
        
        return {
            classList, scoreInputForm, studentScoreList, submitLoading,
            searchForm, pagination, filteredData, paginatedData, statistics,
            handleClassChangeForInput, handleAbsentChange, handleSubmitScores,
            handleSearch, handleReset, handleSizeChange, handlePageChange
        };
    }
};
