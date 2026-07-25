Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  IntervueAI - Starting Backend Server  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location (Join-Path $PSScriptRoot "backend")
Write-Host "Building and running Spring Boot backend..." -ForegroundColor Yellow
mvn spring-boot:run

