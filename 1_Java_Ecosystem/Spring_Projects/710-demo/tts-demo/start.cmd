@echo off
cd /d "%~dp0"
pwsh -NoLogo -NoProfile -ExecutionPolicy Bypass -File "%~dp0start.ps1" %*
pause
