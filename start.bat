@echo off
chcp 65001 >nul
cls
echo ======================================
echo    Lab Management System - Start
echo ======================================
echo.

REM Save project root directory
set PROJECT_ROOT=%~dp0

echo [1/4] Starting Backend...
cd /d "%PROJECT_ROOT%backend"
start "Backend" cmd /k "mvn spring-boot:run"
echo Backend starting...
timeout /t 10 /nobreak >nul
echo.

echo [2/4] Starting Frontend...
cd /d "%PROJECT_ROOT%frontend"
start "Frontend" cmd /k "npm run dev"
echo Frontend starting...
echo Waiting for frontend to be ready...
timeout /t 8 /nobreak >nul
echo.

echo [3/4] Starting cpolar (Frontend only)...
cd /d "D:\cpolar"
start "cpolar-frontend" cmd /k "cpolar.exe http 3000"
echo cpolar frontend starting...
echo Waiting for cpolar to be ready...
timeout /t 5 /nobreak >nul
echo.

echo [4/4] Auto-updating cpolar address...
cd /d "%PROJECT_ROOT%"
powershell -ExecutionPolicy Bypass -NoProfile -File "%PROJECT_ROOT%update-cpolar.ps1"
echo.

echo ======================================
echo    All Services Started!
echo ======================================
echo.
echo Backend:  http://localhost:8081 (internal only)
echo Frontend: http://localhost:3000
echo.
echo Check the cpolar-frontend window for the public URL
echo Frontend will access Backend via Vite proxy
echo.
echo NOTE: If cpolar address update failed,
echo check the cpolar window and run deploy.bat
echo.
echo Press any key to close this window...
pause >nul
