@echo off
echo ============================================================
echo   SMART COMPLAINT TRACKER - BUILD SCRIPT
echo ============================================================
if not exist "bin" mkdir bin
powershell -Command "Get-ChildItem -Recurse src -Filter *.java | Resolve-Path -Relative | Out-File -Encoding ascii sources.txt; javac -d bin -encoding UTF-8 '@sources.txt'; Remove-Item sources.txt -Force"
if %ERRORLEVEL% EQU 0 (
    echo Build Successful!
) else (
    echo Build Failed!
)
