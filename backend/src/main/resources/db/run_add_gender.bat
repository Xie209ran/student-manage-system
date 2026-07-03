@echo off
chcp 65001 >nul
echo 正在添加gender字段到sys_user表...
mysql -u root -p123456 edum < "%~dp0add_gender_field.sql"
if %errorlevel% equ 0 (
    echo ✅ gender字段添加成功！
) else (
    echo ❌ 执行失败，请检查错误信息
)
pause
