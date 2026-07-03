# ============================================
# 教学数字平台 - 数据库初始化脚本 (PowerShell)
# ============================================
# 使用方法：
# 1. 右键点击此文件，选择"使用PowerShell运行"
# 2. 或在PowerShell中执行：.\init-database.ps1
# ============================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  教学数字平台后台管理系统" -ForegroundColor Cyan
Write-Host "  数据库初始化工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 检查MySQL是否安装
Write-Host "🔍 检查MySQL是否安装..." -ForegroundColor Yellow
$mysqlPath = Get-Command mysql -ErrorAction SilentlyContinue

if (-not $mysqlPath) {
    Write-Host "❌ 错误：未找到MySQL命令" -ForegroundColor Red
    Write-Host "请确保MySQL已安装并添加到系统PATH环境变量中" -ForegroundColor Red
    Write-Host ""
    Read-Host "按回车键退出"
    exit 1
}

Write-Host "✅ MySQL已找到：$($mysqlPath.Source)" -ForegroundColor Green
Write-Host ""

# 获取MySQL连接信息
Write-Host "请输入MySQL连接信息：" -ForegroundColor Cyan
$username = Read-Host "用户名 (默认: root)"
if ([string]::IsNullOrWhiteSpace($username)) {
    $username = "root"
}

$password = Read-Host "密码" -AsSecureString
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))

$host_name = Read-Host "主机 (默认: localhost)"
if ([string]::IsNullOrWhiteSpace($host_name)) {
    $host_name = "localhost"
}

$port = Read-Host "端口 (默认: 3306)"
if ([string]::IsNullOrWhiteSpace($port)) {
    $port = "3306"
}

Write-Host ""
Write-Host "📋 连接信息确认：" -ForegroundColor Cyan
Write-Host "  主机: $host_name" -ForegroundColor White
Write-Host "  端口: $port" -ForegroundColor White
Write-Host "  用户: $username" -ForegroundColor White
Write-Host ""

# 选择操作
Write-Host "请选择操作：" -ForegroundColor Cyan
Write-Host "  1. 完整初始化（删除旧数据库，重新创建）" -ForegroundColor White
Write-Host "  2. 仅创建数据库和表（保留现有数据）" -ForegroundColor White
Write-Host "  3. 仅添加优化索引" -ForegroundColor White
Write-Host "  4. 验证数据库" -ForegroundColor White
Write-Host ""

$choice = Read-Host "请输入选项 (1/2/3/4)"

# 获取脚本路径
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$completeInitScript = Join-Path $scriptDir "..\backend\src\main\resources\db\complete_init.sql"
$initScript = Join-Path $scriptDir "..\backend\src\main\resources\db\init.sql"
$optimizationScript = Join-Path $scriptDir "..\backend\src\main\resources\db\performance_optimization.sql"

# 检查脚本文件是否存在
if (-not (Test-Path $completeInitScript)) {
    Write-Host "❌ 错误：找不到 complete_init.sql" -ForegroundColor Red
    Write-Host "   路径: $completeInitScript" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

Write-Host ""

switch ($choice) {
    "1" {
        Write-Host "⚠️  警告：此操作将删除现有的 edum 数据库！" -ForegroundColor Yellow
        $confirm = Read-Host "确定要继续吗？(y/n)"
        
        if ($confirm -ne "y" -and $confirm -ne "Y") {
            Write-Host "❌ 操作已取消" -ForegroundColor Red
            Read-Host "按回车键退出"
            exit 0
        }
        
        Write-Host ""
        Write-Host "🗑️  删除旧数据库..." -ForegroundColor Yellow
        $dropCmd = "DROP DATABASE IF EXISTS edum;"
        echo $dropCmd | & mysql -h $host_name -P $port -u $username --password=$plainPassword 2>&1
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ 旧数据库已删除" -ForegroundColor Green
        } else {
            Write-Host "⚠️  删除数据库时出现警告（可能数据库不存在）" -ForegroundColor Yellow
        }
        
        Write-Host ""
        Write-Host "🚀 执行完整初始化脚本..." -ForegroundColor Cyan
        & mysql -h $host_name -P $port -u $username --password=$plainPassword < $completeInitScript
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host ""
            Write-Host "========================================" -ForegroundColor Green
            Write-Host "  ✅ 数据库初始化成功！" -ForegroundColor Green
            Write-Host "========================================" -ForegroundColor Green
            Write-Host ""
            Write-Host "📊 数据库信息：" -ForegroundColor Cyan
            Write-Host "  数据库名: edum" -ForegroundColor White
            Write-Host "  表数量: 8张" -ForegroundColor White
            Write-Host "  管理员账号: admin / admin123" -ForegroundColor White
            Write-Host ""
        } else {
            Write-Host ""
            Write-Host "❌ 数据库初始化失败！" -ForegroundColor Red
            Write-Host "请检查错误信息并重试" -ForegroundColor Red
        }
    }
    
    "2" {
        Write-Host "🚀 执行基础初始化脚本..." -ForegroundColor Cyan
        & mysql -h $host_name -P $port -u $username --password=$plainPassword < $initScript
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ 基础初始化完成" -ForegroundColor Green
        } else {
            Write-Host "⚠️  基础初始化出现错误（可能表已存在）" -ForegroundColor Yellow
        }
    }
    
    "3" {
        Write-Host "🚀 执行索引优化脚本..." -ForegroundColor Cyan
        & mysql -h $host_name -P $port -u $username --password=$plainPassword < $optimizationScript
        
        if ($LASTEXITCODE -eq 0) {
            Write-Host "✅ 索引优化完成" -ForegroundColor Green
        } else {
            Write-Host "⚠️  索引优化出现错误（可能索引已存在）" -ForegroundColor Yellow
        }
    }
    
    "4" {
        Write-Host "🔍 验证数据库..." -ForegroundColor Cyan
        Write-Host ""
        
        # 检查数据库是否存在
        $checkDb = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = 'edum';"
        $dbExists = echo $checkDb | & mysql -h $host_name -P $port -u $username --password=$plainPassword -N 2>&1
        
        if ([string]::IsNullOrWhiteSpace($dbExists)) {
            Write-Host "❌ 数据库 'edum' 不存在" -ForegroundColor Red
            Write-Host "请先执行选项1或2进行初始化" -ForegroundColor Yellow
        } else {
            Write-Host "✅ 数据库 'edum' 存在" -ForegroundColor Green
            
            # 检查表
            Write-Host ""
            Write-Host "📊 表列表：" -ForegroundColor Cyan
            $showTables = "USE edum; SHOW TABLES;"
            echo $showTables | & mysql -h $host_name -P $port -u $username --password=$plainPassword -N 2>&1
            
            # 检查管理员账号
            Write-Host ""
            Write-Host "👤 管理员账号：" -ForegroundColor Cyan
            $checkAdmin = "USE edum; SELECT id, username, real_name, role, status FROM sys_user WHERE username = 'admin';"
            echo $checkAdmin | & mysql -h $host_name -P $port -u $username --password=$plainPassword -t 2>&1
            
            # 统计索引数量
            Write-Host ""
            Write-Host "📈 索引统计：" -ForegroundColor Cyan
            $indexCount = "USE edum; SELECT COUNT(*) as index_count FROM information_schema.statistics WHERE table_schema = 'edum';"
            echo $indexCount | & mysql -h $host_name -P $port -u $username --password=$plainPassword -t 2>&1
        }
    }
    
    default {
        Write-Host "❌ 无效选项" -ForegroundColor Red
    }
}

Write-Host ""
Read-Host "按回车键退出"
