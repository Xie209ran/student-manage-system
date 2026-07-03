// 考勤打卡页面组件
const AttendancePage = {
    template: `
        <div class="attendance-page">
            <!-- 批量打卡区域 -->
            <div class="table-container" style="margin-bottom: 20px;">
                <div class="table-header">
                    <span class="table-title">批量打卡</span>
                </div>
                
                <el-form :inline="true" style="margin-bottom: 20px;">
                    <el-form-item label="选择班级">
                        <el-select v-model="selectedClassId" placeholder="请选择班级" @change="handleClassChange">
                            <el-option
                                v-for="cls in classList"
                                :key="cls.id"
                                :label="cls.className"
                                :value="cls.id"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="考勤日期">
                        <el-date-picker
                            v-model="attendanceDate"
                            type="date"
                            placeholder="选择日期"
                        ></el-date-picker>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="success" @click="handleSetAll('present')">全部出勤</el-button>
                        <el-button type="primary" @click="handleSubmitCheckin" :loading="submitLoading">提交打卡</el-button>
                    </el-form-item>
                </el-form>
                
                <el-table :data="studentAttendanceList" style="width: 100%" v-if="selectedClassId">
                    <el-table-column prop="name" label="学生姓名" width="120"></el-table-column>
                    <el-table-column label="出勤状态" width="200">
                        <template #default="{ row }">
                            <el-select v-model="row.status" placeholder="请选择状态">
                                <el-option label="出勤" value="present"></el-option>
                                <el-option label="迟到" value="late"></el-option>
                                <el-option label="请假" value="leave"></el-option>
                                <el-option label="缺勤" value="absent"></el-option>
                            </el-select>
                        </template>
                    </el-table-column>
                    <el-table-column label="备注">
                        <template #default="{ row }">
                            <el-input v-model="row.remark" placeholder="选填" size="small"></el-input>
                        </template>
                    </el-table-column>
                </el-table>
                
                <el-empty v-else description="请先选择班级"></el-empty>
            </div>
            
            <!-- 考勤记录查询 -->
            <div class="table-container">
                <div class="table-header">
                    <span class="table-title">考勤记录查询</span>
                </div>
                
                <div class="search-form">
                    <el-form :inline="true" :model="searchForm">
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
                        <el-form-item label="学生">
                            <el-input v-model="searchForm.studentName" placeholder="请输入学生姓名" clearable></el-input>
                        </el-form-item>
                        <el-form-item label="日期范围">
                            <el-date-picker
                                v-model="searchForm.dateRange"
                                type="daterange"
                                range-separator="至"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                            ></el-date-picker>
                        </el-form-item>
                        <el-form-item label="出勤状态">
                            <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
                                <el-option label="出勤" value="present"></el-option>
                                <el-option label="迟到" value="late"></el-option>
                                <el-option label="请假" value="leave"></el-option>
                                <el-option label="缺勤" value="absent"></el-option>
                            </el-select>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" @click="handleSearch">搜索</el-button>
                            <el-button @click="handleReset">重置</el-button>
                        </el-form-item>
                    </el-form>
                </div>
                
                <el-table :data="paginatedData" style="width: 100%">
                    <el-table-column prop="studentName" label="学生姓名" width="120"></el-table-column>
                    <el-table-column prop="className" label="班级" width="150"></el-table-column>
                    <el-table-column prop="attendanceDate" label="考勤日期" width="120"></el-table-column>
                    <el-table-column label="出勤状态" width="100">
                        <template #default="{ row }">
                            <el-tag :type="getStatusType(row.status)">
                                {{ getStatusText(row.status) }}
                            </el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="remark" label="备注" min-width="150"></el-table-column>
                    <el-table-column prop="checkinUser" label="打卡人" width="100"></el-table-column>
                    <el-table-column prop="checkinTime" label="打卡时间" width="180"></el-table-column>
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
        
        const selectedClassId = Vue.ref(null);
        const attendanceDate = Vue.ref(new Date());
        const submitLoading = Vue.ref(false);
        
        // 学生列表（用于批量打卡）
        const studentList = Vue.ref([]);
        
        const studentAttendanceList = Vue.computed(() => {
            if (!selectedClassId.value) return [];
            return studentList.value.map(s => ({
                ...s,
                status: 'present',
                remark: ''
            }));
        });
        
        // 考勤记录
        const attendanceRecords = Vue.ref([]);
        
        const searchForm = Vue.ref({
            classId: null,
            studentName: '',
            dateRange: null,
            status: ''
        });
        
        const pagination = Vue.ref({ pageNum: 1, pageSize: 10, total: 0 });
        
        // 过滤后的数据（后端已处理，直接返回）
        const filteredData = Vue.computed(() => {
            return attendanceRecords.value;
        });
        
        // 分页后的数据（后端已分页，直接返回）
        const paginatedData = Vue.computed(() => {
            return attendanceRecords.value;
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
        
        // 加载学生列表（用于批量打卡）
        const loadStudentList = async () => {
            if (!selectedClassId.value) return;
            
            try {
                // 假设有一个接口可以根据班级ID获取学生列表
                const students = await http.get('/students', {
                    classId: selectedClassId.value,
                    pageSize: 100 // 获取所有学生
                });
                studentList.value = students.list || [];
            } catch (error) {
                console.error('加载学生列表失败:', error);
            }
        };
        
        // 加载考勤记录
        const loadAttendanceRecords = async () => {
            try {
                const params = {
                    page: pagination.value.pageNum,
                    pageSize: pagination.value.pageSize,
                    classId: searchForm.value.classId,
                    studentName: searchForm.value.studentName,
                    status: searchForm.value.status
                };
                
                // 处理日期范围
                if (searchForm.value.dateRange && searchForm.value.dateRange.length === 2) {
                    params.startDate = searchForm.value.dateRange[0];
                    params.endDate = searchForm.value.dateRange[1];
                }
                
                const result = await http.get('/attendance', params);
                attendanceRecords.value = result.list || [];
                pagination.value.total = result.total || 0;
            } catch (error) {
                console.error('加载考勤记录失败:', error);
            }
        };
        
        const handleClassChange = () => {
            loadStudentList();
        };
        
        const handleSetAll = (status) => {
            studentAttendanceList.value.forEach(s => {
                s.status = status;
            });
            const statusText = status === 'present' ? '出勤' : status === 'late' ? '迟到' : status === 'leave' ? '请假' : '缺勤';
            ElementPlus.ElMessage.success(`已设置为${statusText}`);
        };
        
        const handleSubmitCheckin = async () => {
            if (!selectedClassId.value) {
                ElementPlus.ElMessage.warning('请先选择班级');
                return;
            }
            
            submitLoading.value = true;
            
            try {
                // 构建批量打卡数据
                const checkinData = studentAttendanceList.value.map(s => ({
                    studentId: s.id,
                    classId: selectedClassId.value,
                    attendanceDate: attendanceDate.value,
                    status: s.status,
                    remark: s.remark || ''
                }));
                
                await http.post('/attendance/batch', checkinData);
                ElementPlus.ElMessage.success('打卡成功');
                loadAttendanceRecords(); // 刷新考勤记录
            } catch (error) {
                console.error('打卡失败:', error);
                // 后端会返回重复打卡等错误消息
            } finally {
                submitLoading.value = false;
            }
        };
        
        const handleSearch = () => {
            pagination.value.pageNum = 1;
            loadAttendanceRecords();
        };
        
        const handleReset = () => {
            searchForm.value = { classId: null, studentName: '', dateRange: null, status: '' };
            pagination.value.pageNum = 1;
            loadAttendanceRecords();
        };
        
        const getStatusType = (status) => {
            const map = { present: 'success', late: 'warning', leave: 'info', absent: 'danger' };
            return map[status] || '';
        };
        
        const getStatusText = (status) => {
            const map = { present: '出勤', late: '迟到', leave: '请假', absent: '缺勤' };
            return map[status] || status;
        };
        
        const handleSizeChange = (val) => { 
            pagination.value.pageSize = val; 
            pagination.value.pageNum = 1;
            loadAttendanceRecords();
        };
        const handlePageChange = (val) => { 
            pagination.value.pageNum = val;
            loadAttendanceRecords();
        };
        
        // 组件挂载时加载数据
        Vue.onMounted(() => {
            loadClassList();
            loadAttendanceRecords();
        });
        
        return {
            classList, selectedClassId, attendanceDate, submitLoading,
            studentAttendanceList, searchForm, pagination, filteredData, paginatedData,
            handleClassChange, handleSetAll, handleSubmitCheckin, handleSearch, handleReset,
            getStatusType, getStatusText, handleSizeChange, handlePageChange
        };
    }
};
