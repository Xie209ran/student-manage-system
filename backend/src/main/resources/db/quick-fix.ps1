# ============================================
# 快速修复脚本 - 一键解决所有问题
# ============================================
# 用途：修复数据库 + 重启后端服务
# 使用方法：右键点击，选择"使用PowerShell运行"
# ============================================

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  教学数字平台 - 快速修复工具" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 获取MySQL连接信息
$username = Read-Host "MySQL用户名 (默认: root)"
if ([string]::IsNullOrWhiteSpace($username)) {
    $username = "root"
}

$password = Read-Host "MySQL密码" -AsSecureString
$plainPassword = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($password))

Write-Host ""
Write-Host "🔧 开始修复..." -ForegroundColor Yellow
Write-Host ""

# 第1步：修复数据库
Write-Host "📊 第1步：修复数据库..." -ForegroundColor Cyan

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$fixScript = Join-Path $scriptDir "fix_database.sql"
$checkScript = Join-Path $scriptDir "check_database.sql"

if (-not (Test-Path $fixScript)) {
    Write-Host "❌ 错误：找不到 fix_database.sql" -ForegroundColor Red
    Read-Host "按回车键退出"
    exit 1
}

Write-Host "执行修复脚本..." -ForegroundColor Gray
& mysql -u $username --password=$plainPassword edum < $fixScript

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "✅ 数据库修复成功！" -ForegroundColor Green
    
    Write-Host ""
    Write-Host "验证数据库状态..." -ForegroundColor Cyan
    & mysql -u $username --password=$plainPassword edum < $checkScript
} else {
    Write-Host ""
    Write-Host "❌ 数据库修复失败！" -ForegroundColor Red
    Write-Host "请检查错误信息" -ForegroundColor Yellow
    Read-Host "按回车键退出"
    exit 1
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# 第2步：提示重启后端
Write-Host "🚀 第2步：重启后端服务" -ForegroundColor Cyan
Write-Host ""
Write-Host "⚠️  重要：需要重启后端服务以使CORS配置生效！" -ForegroundColor Yellow
Write-Host ""
Write-Host "请选择操作：" -ForegroundColor White
Write-Host "  1. 自动重启后端（如果后端正在运行）" -ForegroundColor White
Write-Host "  2. 手动重启后端（我会自己处理）" -ForegroundColor White
Write-Host ""

$choice = Read-Host "请输入选项 (1/2)"

if ($choice -eq "1") {
    Write-Host ""
    Write-Host "🔍 查找后端进程..." -ForegroundColor Cyan
    
    # 查找Java进程
    $javaProcess = Get-Process -Name java -ErrorAction SilentlyContinue | Where-Object {
        $_.MainWindowTitle -like "*edum*" -or $_.CommandLine -like "*edum-backend*"
    }
    
    if ($javaProcess) {
        Write-Host "✅ 找到后端进程，PID: $($javaProcess.Id)" -ForegroundColor Green
        Write-Host "⏹️  停止后端服务..." -ForegroundColor Yellow
        
        Stop-Process -Id $javaProcess.Id -Force
        
        Start-Sleep -Seconds 2
        
        Write-Host "✅ 后端已停止" -ForegroundColor Green
        Write-Host ""
        Write-Host "🔄 启动后端服务..." -ForegroundColor Cyan
        Write-Host ""
        
        $backendDir = Join-Path $scriptDir "..\.."
        Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir'; mvn spring-boot:run"
        
        Write-Host "✅ 后端正在启动..." -ForegroundColor Green
        Write-Host ""
        Write-Host "⏳ 等待10秒让后端完全启动..." -ForegroundColor Yellow
        Start-Sleep -Seconds 10
        
        Write-Host ""
        Write-Host "✅ 后端应该已经启动完成！" -ForegroundColor Green
    } else {
        Write-Host "⚠️  未找到运行中的后端进程" -ForegroundColor Yellow
        Write-Host "请手动启动后端服务：" -ForegroundColor White
        Write-Host ""
        Write-Host "  cd D:\Date\studentmanagetwo\backend" -ForegroundColor Gray
        Write-Host "  mvn spring-boot:run" -ForegroundColor Gray
    }
} else {
    Write-Host ""
    Write-Host "📝 请手动重启后端服务：" -ForegroundColor White
    Write-Host ""
    Write-Host "  1. 停止当前运行的后端服务（Ctrl+C）" -ForegroundColor Gray
    Write-Host "  2. 重新启动：" -ForegroundColor Gray
    Write-Host "     cd D:\Date\studentmanagetwo\backend" -ForegroundColor Gray
    Write-Host "     mvn spring-boot:run" -ForegroundColor Gray
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "  ✅ 修复完成！" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "📋 下一步：" -ForegroundColor Cyan
Write-Host ""
Write-Host "  1. 确认后端服务已启动" -ForegroundColor White
Write-Host "     访问：http://localhost:8080/api/auth/current" -ForegroundColor Gray
Write-Host "     应该返回：{`"code`":401,`"message`":`"未授权`"}" -ForegroundColor Gray
Write-Host ""
Write-Host "  2. 打开前端页面测试登录" -ForegroundColor White
Write-Host "     文件：D:\Date\studentmanagetwo\front\index.html" -ForegroundColor Gray
Write-Host "     账号：admin / admin123" -ForegroundColor Gray
Write-Host ""
Write-Host "  3. 如果仍有问题，查看：" -ForegroundColor White
Write-Host "     doc/TROUBLESHOOTING.md" -ForegroundColor Gray
Write-Host ""

Read-Host "按回车键退出"
