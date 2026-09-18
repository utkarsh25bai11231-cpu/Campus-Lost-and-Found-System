@echo off
echo ========================================================
echo Compiling Campus Lost and Found System (CSE2006 Java)
echo ========================================================
if not exist bin mkdir bin
powershell -NoProfile -Command "Get-ChildItem -Recurse -Filter '*.java' src\main\java | ForEach-Object { '\"' + $_.FullName.Replace('\', '/') + '\"' } | Set-Content -Encoding ASCII sources.txt"
javac --release 21 -cp "lib/*" -d bin @sources.txt
del sources.txt
if %errorlevel% equ 0 (
    copy /Y db.properties bin\ >nul
    echo.
    echo [OK] Compilation successful! Classes placed in bin/ directory.
    echo Run the project using: run.bat
) else (
    echo.
    echo [ERROR] Compilation failed.
)
