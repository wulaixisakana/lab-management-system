@echo off
chcp 65001 >nul
echo ======================================
echo    停止所有服务
echo ======================================
echo.

echo 正在停止后端服务...
taskkill /f /im java.exe 2>nul
echo 正在停止内网穿透...
taskkill /f /im cpolar.exe 2>nul
echo 正在停止前端服务...
taskkill /f /im node.exe 2>nul

echo.
echo 所有服务已停止！
pause
