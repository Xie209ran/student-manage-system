// 仪表盘页面组件 - 包含统计卡片和图表
const DashboardPage = {
    template: `
        <div class="dashboard-page">
            <!-- 统计卡片 -->
            <el-row :gutter="20" class="stat-cards">
                <el-col :xs="24" :sm="12" :md="6">
                    <div class="stat-card">
                        <div class="stat-header">
                            <span class="stat-title">学生总数</span>
                            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                                <el-icon><User /></el-icon>
                            </div>
                        </div>
                        <div class="stat-value">{{ statistics.totalStudents }}</div>
                        <div class="stat-footer">较上月 +{{ statistics.studentGrowth }}%</div>
                    </div>
                </el-col>
                
                <el-col :xs="24" :sm="12" :md="6">
                    <div class="stat-card">
                        <div class="stat-header">
                            <span class="stat-title">班级总数</span>
                            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                                <el-icon><School /></el-icon>
                            </div>
                        </div>
                        <div class="stat-value">{{ statistics.totalClasses }}</div>
                        <div class="stat-footer">较上月 +{{ statistics.classGrowth }}%</div>
                    </div>
                </el-col>
                
                <el-col :xs="24" :sm="12" :md="6">
                    <div class="stat-card">
                        <div class="stat-header">
                            <span class="stat-title">今日出勤率</span>
                            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                                <el-icon><Checked /></el-icon>
                            </div>
                        </div>
                        <div class="stat-value">{{ statistics.todayAttendance }}%</div>
                        <div class="stat-footer">较昨日 {{ statistics.attendanceChange > 0 ? '+' : '' }}{{ statistics.attendanceChange }}%</div>
                    </div>
                </el-col>
                
                <el-col :xs="24" :sm="12" :md="6">
                    <div class="stat-card">
                        <div class="stat-header">
                            <span class="stat-title">待批改作业</span>
                            <div class="stat-icon" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
                                <el-icon><Document /></el-icon>
                            </div>
                        </div>
                        <div class="stat-value">{{ statistics.pendingHomework }}</div>
                        <div class="stat-footer">来自 {{ statistics.homeworkTeachers }} 位教师</div>
                    </div>
                </el-col>
            </el-row>
            
            <!-- 图表区域 -->
            <el-row :gutter="20" class="charts-row">
                <el-col :xs="24" :md="12">
                    <div class="chart-container">
                        <div class="chart-title">班级人数分布</div>
                        <div ref="classDistributionChart" class="chart-body"></div>
                    </div>
                </el-col>
                
                <el-col :xs="24" :md="12">
                    <div class="chart-container">
                        <div class="chart-title">近7天出勤趋势</div>
                        <div ref="attendanceTrendChart" class="chart-body"></div>
                    </div>
                </el-col>
            </el-row>
        </div>
    `,
    setup() {
        const classDistributionChart = Vue.ref(null);
        const attendanceTrendChart = Vue.ref(null);
        
        // 统计数据
        const statistics = Vue.ref({
            totalStudents: 0,
            totalClasses: 0,
            todayAttendanceRate: 0,
            pendingHomeworks: 0
        });
        
        // 加载统计数据
        const loadStatistics = async () => {
            try {
                const data = await http.get('/dashboard/statistics');
                statistics.value = {
                    totalStudents: data.totalStudents || 0,
                    studentGrowth: 0, // 后端未提供，可后续扩展
                    totalClasses: data.totalClasses || 0,
                    classGrowth: 0, // 后端未提供，可后续扩展
                    todayAttendance: data.todayAttendanceRate || 0,
                    attendanceChange: 0, // 后端未提供，可后续扩展
                    pendingHomework: data.pendingHomeworks || 0,
                    homeworkTeachers: 0 // 后端未提供，可后续扩展
                };
            } catch (error) {
                console.error('加载统计数据失败:', error);
            }
        };
        
        // 初始化班级人数分布图
        const initClassDistributionChart = async () => {
            if (!classDistributionChart.value) return;
            
            try {
                // 从后端获取班级分布数据
                const classData = await http.get('/dashboard/class-distribution');
                
                const chart = echarts.init(classDistributionChart.value);
                
                const option = {
                    tooltip: {
                        trigger: 'axis',
                        axisPointer: { type: 'shadow' }
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'value',
                        name: '学生人数'
                    },
                    yAxis: {
                        type: 'category',
                        data: classData.map(item => item.className).reverse(),
                        axisLabel: {
                            interval: 0
                        }
                    },
                    series: [
                        {
                            name: '学生人数',
                            type: 'bar',
                            data: classData.map(item => item.studentCount).reverse(),
                            itemStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                                    { offset: 0, color: '#667eea' },
                                    { offset: 1, color: '#764ba2' }
                                ])
                            },
                            label: {
                                show: true,
                                position: 'right'
                            }
                        }
                    ]
                };
                
                chart.setOption(option);
                
                // 响应式
                window.addEventListener('resize', () => {
                    chart.resize();
                });
            } catch (error) {
                console.error('加载班级分布数据失败:', error);
            }
        };
        
        // 初始化出勤趋势图
        const initAttendanceTrendChart = async () => {
            if (!attendanceTrendChart.value) return;
            
            try {
                // 从后端获取近7天出勤趋势数据
                const trendData = await http.get('/dashboard/attendance-trend');
                
                const chart = echarts.init(attendanceTrendChart.value);
                
                const dates = trendData.map(item => item.date.substring(5)); // 取MM-DD部分
                const attendanceData = trendData.map(item => item.attendanceRate);
                
                const option = {
                    tooltip: {
                        trigger: 'axis',
                        formatter: '{b}<br/>{a}: {c}%'
                    },
                    grid: {
                        left: '3%',
                        right: '4%',
                        bottom: '3%',
                        containLabel: true
                    },
                    xAxis: {
                        type: 'category',
                        boundaryGap: false,
                        data: dates
                    },
                    yAxis: {
                        type: 'value',
                        name: '出勤率(%)',
                        min: 90,
                        max: 100,
                        axisLabel: {
                            formatter: '{value}%'
                        }
                    },
                    series: [
                        {
                            name: '出勤率',
                            type: 'line',
                            smooth: true,
                            data: attendanceData,
                            itemStyle: {
                                color: '#4facfe'
                            },
                            areaStyle: {
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                                    { offset: 0, color: 'rgba(79, 172, 254, 0.3)' },
                                    { offset: 1, color: 'rgba(79, 172, 254, 0.05)' }
                                ])
                            },
                            markPoint: {
                                data: [
                                    { type: 'max', name: '最高' },
                                    { type: 'min', name: '最低' }
                                ]
                            },
                            markLine: {
                                data: [
                                    { type: 'average', name: '平均' }
                                ]
                            }
                        }
                    ]
                };
                
                chart.setOption(option);
                
                // 响应式
                window.addEventListener('resize', () => {
                    chart.resize();
                });
            } catch (error) {
                console.error('加载出勤趋势数据失败:', error);
            }
        };
        
        // 组件挂载后初始化图表
        Vue.onMounted(async () => {
            // 先加载统计数据
            await loadStatistics();
            
            setTimeout(() => {
                initClassDistributionChart();
                initAttendanceTrendChart();
            }, 100);
        });
        
        return {
            statistics,
            classDistributionChart,
            attendanceTrendChart
        };
    }
};
