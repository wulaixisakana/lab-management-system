@echo off
chcp 65001 >nul
cls
echo ======================================
echo    Deploy to Vercel
echo ======================================
echo.

echo [1/4] Checking cpolar...
echo Please make sure cpolar is running!
echo.
echo Current cpolar address (check cpolar window):
echo Example: https://xxxx.cpolar.cn
echo.
set /p CPOLAR_URL="Enter cpolar URL (press Enter to skip): "

if not "%CPOLAR_URL%"=="" (
    echo.
    echo Updating config with: %CPOLAR_URL%
    cd /d "%~dp0frontend\src\utils"
    powershell -Command "(Get-Content 'request.js') -replace 'https://[^\s'']+\.cpolar\.[^\s'']+', '%CPOLAR_URL%' | Set-Content 'request.js'"
    echo Config updated!
    echo.
)
echo.

echo [2/4] Building frontend...
cd /d "%~dp0frontend"
call npm run build
if errorlevel 1 (
    echo.
    echo ERROR: Build failed
    pause
    exit /b 1
)
echo.

echo [3/4] Deploying to Vercel...
call vercel --prod
if errorlevel 1 (
    echo.
    echo ERROR: Deploy failed
    pause
    exit /b 1
)
echo.

echo ======================================
echo    Deploy Complete!
echo ======================================
echo.
echo Visit: https://sysgl.ccwu.cc
echo.
pause
