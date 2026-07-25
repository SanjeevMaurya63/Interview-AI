Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  IntervueAI - Starting Frontend Server  " -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

Set-Location (Join-Path $PSScriptRoot "frontend")
Write-Host "Starting Vite React dev server..." -ForegroundColor Yellow
npm run dev

