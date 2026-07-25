@echo off
echo ========================================
echo   IntervueAI - Starting Backend Server
echo ========================================
echo.
cd /d "%~dp0backend"
echo Building and running Spring Boot backend...
mvn spring-boot:run
pause

