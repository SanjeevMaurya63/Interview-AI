# run-backend.ps1
# Script to run the Spring Boot backend. If Maven is not installed globally, it downloads a local copy automatically.

Write-Host "Checking Java installation..." -ForegroundColor Cyan
& java -version
if ($LASTEXITCODE -ne 0) {
    Write-Error "Java is not installed or not in PATH. Please install JDK 17 or higher."
    exit 1
}

$mvnCmd = "mvn"
# Check if mvn is installed
if (Get-Command "mvn" -ErrorAction SilentlyContinue) {
    Write-Host "Maven is installed globally." -ForegroundColor Green
} else {
    Write-Host "Maven not found globally. Checking local Maven installation..." -ForegroundColor Yellow
    $localMavenDir = Join-Path (Get-Location) "backend\.maven"
    $localMvnCmd = Join-Path $localMavenDir "apache-maven-3.9.6\bin\mvn.cmd"
    
    if (-not (Test-Path $localMvnCmd)) {
        Write-Host "Local Maven not found. Downloading Apache Maven 3.9.6..." -ForegroundColor Cyan
        New-Item -ItemType Directory -Force -Path $localMavenDir | Out-Null
        
        $zipUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.zip"
        $zipFile = Join-Path $localMavenDir "maven.zip"
        
        Invoke-WebRequest -Uri $zipUrl -OutFile $zipFile
        
        Write-Host "Extracting Maven..." -ForegroundColor Cyan
        Expand-Archive -Path $zipFile -DestinationPath $localMavenDir -Force
        Remove-Item $zipFile -Force
    }
    
    $mvnCmd = $localMvnCmd
    Write-Host "Using local Maven: $mvnCmd" -ForegroundColor Green
}

# Load environment variables from .env.local if present
$envFile = Join-Path (Get-Location) ".env.local"
if (Test-Path $envFile) {
    Write-Host "Loading environment variables from .env.local..." -ForegroundColor Cyan
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()
        if ($line -and -not $line.StartsWith("#")) {
            if ($line -match '^([^=]+)=(.*)$') {
                $key = $Matches[1].Trim()
                $value = $Matches[2].Trim()
                # Trim quotes if any
                $value = $value.Trim('"').Trim("'")
                [System.Environment]::SetEnvironmentVariable($key, $value, [System.EnvironmentVariableTarget]::Process)
            }
        }
    }
}

Write-Host "Starting Spring Boot backend..." -ForegroundColor Green
Set-Location -Path "backend"
& $mvnCmd spring-boot:run
