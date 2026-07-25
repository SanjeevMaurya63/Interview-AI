@echo off
echo ========================================
echo   IntervueAI - Starting Frontend Server
echo ========================================
echo.
cd /d "%~dp0frontend"
echo Starting Vite React dev server...
npm run dev
pause

