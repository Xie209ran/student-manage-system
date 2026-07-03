// 课程排课页面组件
const CoursePage = {
    template: `
        <div class="course-page">
            <!-- 顶部工具栏 -->
            <div class="search-form">
                <el-form :inline="true">
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
                    <el-form-item>
                        <el-button type="primary" @click="handleAdd">
                            <el-icon><Plus /></el-icon> 添加课程
                        </el-button>
                    </el-form-item>
                </el-form>
            </div>
            
            <!-- 课表容器 -->
            <div class="table-container">
                <div class="table-header">
                    <span class="table-title">周课表 - {{ currentClassName }}</span>
                </div>
                
                <el-table :data="scheduleData" style="width: 100%" border>
                    <el-table-column prop="period" label="节次" width="100" fixed></el-table-column>
                    <el-table-column label="周一" min-width="150">
                        <template #default="{ row }">
                            <div v-if="row.monday" class="course-cell" :style="{ backgroundColor: getCourseColor(row.monday.subject) }">
                                <div class="course-subject">{{ row.monday.subject }}</div>
                                <div class="course-teacher">{{ row.monday.teacherName }}</div>
                                <div class="course-room">{{ row.monday.classroom }}</div>
                                <el-button size="small" type="danger" link @click="handleDelete(row.monday)">删除</el-button>
                            </div>
                            <div v-else class="empty-cell" @click="handleAddToCell(1, row.period)">
                                <el-icon><Plus /></el-icon>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="周二" min-width="150">
                        <template #default="{ row }">
                            <div v-if="row.tuesday" class="course-cell" :style="{ backgroundColor: getCourseColor(row.tuesday.subject) }">
                                <div class="course-subject">{{ row.tuesday.subject }}</div>
                                <div class="course-teacher">{{ row.tuesday.teacherName }}</div>
                                <div class="course-room">{{ row.tuesday.classroom }}</div>
                                <el-button size="small" type="danger" link @click="handleDelete(row.tuesday)">删除</el-button>
                            </div>
                            <div v-else class="empty-cell" @click="handleAddToCell(2, row.period)">
                                <el-icon><Plus /></el-icon>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="周三" min-width="150">
                        <template #default="{ row }">
                            <div v-if="row.wednesday" class="course-cell" :style="{ backgroundColor: getCourseColor(row.wednesday.subject) }">
                                <div class="course-subject">{{ row.wednesday.subject }}</div>
                                <div class="course-teacher">{{ row.wednesday.teacherName }}</div>
                                <div class="course-room">{{ row.wednesday.classroom }}</div>
                                <el-button size="small" type="danger" link @click="handleDelete(row.wednesday)">删除</el-button>
                            </div>
                            <div v-else class="empty-cell" @click="handleAddToCell(3, row.period)">
                                <el-icon><Plus /></el-icon>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="周四" min-width="150">
                        <template #default="{ row }">
                            <div v-if="row.thursday" class="course-cell" :style="{ backgroundColor: getCourseColor(row.thursday.subject) }">
                                <div class="course-subject">{{ row.thursday.subject }}</div>
                                <div class="course-teacher">{{ row.thursday.teacherName }}</div>
                                <div class="course-room">{{ row.thursday.classroom }}</div>
                                <el-button size="small" type="danger" link @click="handleDelete(row.thursday)">删除</el-button>
                            </div>
                            <div v-else class="empty-cell" @click="handleAddToCell(4, row.period)">
                                <el-icon><Plus /></el-icon>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="周五" min-width="150">
                        <template #default="{ row }">
                            <div v-if="row.friday" class="course-cell" :style="{ backgroundColor: getCourseColor(row.friday.subject) }">
                                <div class="course-subject">{{ row.friday.subject }}</div>
                                <div class="course-teacher">{{ row.friday.teacherName }}</div>
                                <div class="course-room">{{ row.friday.classroom }}</div>
                                <el-button size="small" type="danger" link @click="handleDelete(row.friday)">删除</el-button>
                            </div>
                            <div v-else class="empty-cell" @click="handleAddToCell(5, row.period)">
                                <el-icon><Plus /></el-icon>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
            
            <!-- 添加/编辑课程对话框 -->
            <el-dialog
                v-model="dialogVisible"
                :title="dialogTitle"
                width="600px"
            >
                <el-form :model="courseForm" :rules="rules" ref="courseFormRef" label-width="100px">
                    <el-form-item label="班级" prop="classId">
                        <el-select v-model="courseForm.classId" placeholder="请选择班级" style="width: 100%">
                            <el-option
                                v-for="cls in classList"
                                :key="cls.id"
                                :label="cls.className"
                                :value="cls.id"
                            ></el-option>
                        </el-select>
                    </el-form-item>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="星期" prop="dayOfWeek">
                                <el-select v-model="courseForm.dayOfWeek" placeholder="请选择星期" style="width: 100%">
                                    <el-option label="周一" :value="1"></el-option>
                                    <el-option label="周二" :value="2"></el-option>
                                    <el-option label="周三" :value="3"></el-option>
                                    <el-option label="周四" :value="4"></el-option>
                                    <el-option label="周五" :value="5"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="节次" prop="period">
                                <el-select v-model="courseForm.period" placeholder="请选择节次" style="width: 100%">
                                    <el-option v-for="i in 8" :key="i" :label="'第' + i + '节'" :value="i"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="科目" prop="subject">
                                <el-select v-model="courseForm.subject" placeholder="请选择科目" style="width: 100%">
                                    <el-option label="语文" value="语文"></el-option>
                                    <el-option label="数学" value="数学"></el-option>
                                    <el-option label="英语" value="英语"></el-option>
                                    <el-option label="物理" value="物理"></el-option>
                                    <el-option label="化学" value="化学"></el-option>
                                    <el-option label="生物" value="生物"></el-option>
                                    <el-option label="历史" value="历史"></el-option>
                                    <el-option label="地理" value="地理"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="教师" prop="teacherId">
                                <el-select v-model="courseForm.teacherId" placeholder="请选择教师" style="width: 100%">
                                    <el-option
                                        v-for="teacher in teacherList"
                                        :key="teacher.id"
                                        :label="teacher.name"
                                        :value="teacher.id"
                                    ></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    
                    <el-form-item label="教室" prop="classroom">
                        <el-input v-model="courseForm.classroom" placeholder="如：A栋101"></el-input>
                    </el-form-item>
                    
                    <el-row :gutter="20">
                        <el-col :span="12">
                            <el-form-item label="开始日期" prop="startDate">
                                <el-date-picker
                                    v-model="courseForm.startDate"
                                    type="date"
                                    placeholder="选择日期"
                                    style="width: 100%"
                                ></el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="结束日期" prop="endDate">
                                <el-date-picker
                                    v-model="courseForm.endDate"
                                    type="date"
                                    placeholder="选择日期"
                                    style="width: 100%"
                                ></el-date-picker>
                            </el-form-item>
                        </el-col>
                    </el-row>
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
        
        // 教师列表
        const teacherList = Vue.ref([]);
        
        const selectedClassId = Vue.ref(null);
        const currentClassName = Vue.computed(() => {
            const cls = classList.value.find(c => c.id === selectedClassId.value);
            return cls ? cls.className : '';
        });
        
        // 课表数据（8节课 x 5天）
        const scheduleData = Vue.ref([
            { period: '第1节', monday: null, tuesday: null, wednesday: null, thursday: null, friday: null },
            { period: '第2节', monday: null, tuesday: null, wednesday: null, thursday: null, friday: null },
            { period: '第3节', monday: null, tuesday: null, wednesday: null, thursday: null, friday: null },
            { period: '第4节', monday: null, tuesday: null, wednesday: null, thursday: null, friday: null },
            { period: '第5节', monday: null, tuesday: null, wednesday: null, thursday: null, friday: null },
            { period: '第6节', monday: null, tuesday: null, wednesday: null, thursday: null, friday: null },
            { period: '第7节', monday: null, tuesday: null, wednesday: null, thursday: null, friday: null },
            { period: '第8节', monday: null, tuesday: null, wednesday: null, thursday: null, friday: null }
        ]);
        
        // 对话框
        const dialogVisible = Vue.ref(false);
        const dialogTitle = Vue.ref('添加课程');
        const submitLoading = Vue.ref(false);
        const courseFormRef = Vue.ref(null);
        
        const courseForm = Vue.ref({
            id: null,
            classId: null,
            dayOfWeek: null,
            period: null,
            subject: '',
            teacherId: null,
            classroom: '',
            startDate: '',
            endDate: ''
        });
        
        const rules = {
            classId: [{ required: true, message: '请选择班级', trigger: 'change' }],
            dayOfWeek: [{ required: true, message: '请选择星期', trigger: 'change' }],
            period: [{ required: true, message: '请选择节次', trigger: 'change' }],
            subject: [{ required: true, message: '请选择科目', trigger: 'change' }],
            teacherId: [{ required: true, message: '请选择教师', trigger: 'change' }],
            classroom: [{ required: true, message: '请输入教室', trigger: 'blur' }],
            startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
            endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
        };
        
        // 加载班级列表
        const loadClassList = async () => {
            try {
                const classes = await http.get('/common/classes');
                classList.value = classes;
                // 默认选择第一个班级
                if (classes.length > 0 && !selectedClassId.value) {
                    selectedClassId.value = classes[0].id;
                    loadSchedule();
                }
            } catch (error) {
                console.error('加载班级列表失败:', error);
            }
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
        
        // 加载课表数据
        const loadSchedule = async () => {
            if (!selectedClassId.value) return;
            
            try {
                const courses = await http.get('/courses/schedule', {
                    classId: selectedClassId.value
                });
                
                // 重置课表
                scheduleData.value.forEach(row => {
                    row.monday = null;
                    row.tuesday = null;
                    row.wednesday = null;
                    row.thursday = null;
                    row.friday = null;
                });
                
                // 填充课表
                const dayMap = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday'];
                courses.forEach(course => {
                    const dayKey = dayMap[course.dayOfWeek - 1];
                    const periodIndex = course.period - 1;
                    if (periodIndex >= 0 && periodIndex < 8) {
                        scheduleData.value[periodIndex][dayKey] = {
                            id: course.id,
                            subject: course.subject,
                            teacherName: course.teacherName,
                            classroom: course.classroom
                        };
                    }
                });
            } catch (error) {
                console.error('加载课表失败:', error);
            }
        };
        
        // 获取课程颜色
        const getCourseColor = (subject) => {
            const colorMap = {
                '语文': '#e3f2fd',
                '数学': '#f3e5f5',
                '英语': '#e8f5e9',
                '物理': '#fff3e0',
                '化学': '#fce4ec',
                '生物': '#e0f2f1',
                '历史': '#f1f8e9',
                '地理': '#fff8e1'
            };
            return colorMap[subject] || '#f5f5f5';
        };
        
        // 切换班级
        const handleClassChange = () => {
            loadSchedule();
        };
        
        // 添加课程
        const handleAdd = () => {
            dialogTitle.value = '添加课程';
            courseForm.value = {
                id: null,
                classId: selectedClassId.value,
                dayOfWeek: null,
                period: null,
                subject: '',
                teacherId: null,
                classroom: '',
                startDate: '',
                endDate: ''
            };
            dialogVisible.value = true;
        };
        
        // 点击空白单元格添加
        const handleAddToCell = (dayOfWeek, period) => {
            dialogTitle.value = '添加课程';
            const periodNum = parseInt(period.replace('第', '').replace('节', ''));
            courseForm.value = {
                id: null,
                classId: selectedClassId.value,
                dayOfWeek: dayOfWeek,
                period: periodNum,
                subject: '',
                teacherId: null,
                classroom: '',
                startDate: '',
                endDate: ''
            };
            dialogVisible.value = true;
        };
        
        // 提交
        const handleSubmit = async () => {
            if (!courseFormRef.value) return;
            
            await courseFormRef.value.validate(async (valid) => {
                if (valid) {
                    submitLoading.value = true;
                    
                    try {
                        if (courseForm.value.id) {
                            // 编辑
                            await http.put(`/courses/schedule/${courseForm.value.id}`, courseForm.value);
                            ElementPlus.ElMessage.success('编辑成功');
                        } else {
                            // 新增
                            await http.post('/courses/schedule', courseForm.value);
                            ElementPlus.ElMessage.success('添加成功');
                        }
                        
                        dialogVisible.value = false;
                        loadSchedule(); // 刷新课表
                    } catch (error) {
                        console.error('操作失败:', error);
                        // 后端返回的错误消息会显示（如：时间冲突）
                    } finally {
                        submitLoading.value = false;
                    }
                }
            });
        };
        
        // 删除课程
        const handleDelete = (course) => {
            ElementPlus.ElMessageBox.confirm(
                `确定要删除${course.subject}课程吗？`,
                '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            ).then(async () => {
                try {
                    await http.delete(`/courses/schedule/${course.id}`);
                    ElementPlus.ElMessage.success('删除成功');
                    loadSchedule(); // 刷新课表
                } catch (error) {
                    console.error('删除失败:', error);
                }
            }).catch(() => {});
        };
        
        return {
            classList,
            teacherList,
            selectedClassId,
            currentClassName,
            scheduleData,
            dialogVisible,
            dialogTitle,
            submitLoading,
            courseFormRef,
            courseForm,
            rules,
            getCourseColor,
            handleClassChange,
            handleAdd,
            handleAddToCell,
            handleSubmit,
            handleDelete
        };
        
        // 组件挂载时加载数据
        Vue.onMounted(() => {
            loadClassList();
            loadTeacherList();
        });
    }
};
