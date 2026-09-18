# PowerShell compilation script for Campus Lost and Found System
if (!(Test-Path "bin")) { 
    New-Item -ItemType Directory -Path "bin" | Out-Null 
}

$files = (Get-ChildItem -Recurse -Filter "*.java" "src\main\java").FullName
javac --release 21 -cp "lib/*" -d bin $files

if ($LASTEXITCODE -eq 0) {
    Copy-Item "db.properties" "bin\" -Force
    Write-Host "`n[OK] Compilation successful! Classes placed in bin/ directory." -ForegroundColor Green
    Write-Host "Run the system using: .\run.ps1" -ForegroundColor Cyan
} else {
    Write-Host "`n[ERROR] Compilation failed." -ForegroundColor Red
}
