@echo off
npm run build
if not exist "dist\functions" mkdir dist\functions
copy functions\_middleware.js dist\functions\
echo Build completed with functions
