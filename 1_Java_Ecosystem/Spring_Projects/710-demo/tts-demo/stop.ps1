$ErrorActionPreference = 'Stop'
$recordPath = Join-Path $PSScriptRoot '.local\server.json'
if (-not (Test-Path -LiteralPath $recordPath)) {
    Write-Host '没有后台服务记录。前台服务请在其终端按 Ctrl+C 停止。'
    return
}
$record = Get-Content -Raw -LiteralPath $recordPath | ConvertFrom-Json
$service = Get-Process -Id ([int]$record.pid) -ErrorAction SilentlyContinue
if (-not $service) { Write-Host '后台服务已经停止。'; return }
$expectedJar = Join-Path $PSScriptRoot 'target\tts-demo.jar'
$processInfo = Get-CimInstance Win32_Process -Filter "ProcessId = $([int]$record.pid)"
$sameStart = $service.StartTime.ToUniversalTime().Ticks -eq ([datetime]$record.startedAt).ToUniversalTime().Ticks
if ($service.ProcessName -ne 'java' -or -not $sameStart -or $record.jar -ne $expectedJar -or
    -not $processInfo.CommandLine.Contains($expectedJar)) {
    throw '进程身份与本项目记录不符，已拒绝停止。'
}
Stop-Process -Id $service.Id
Write-Host "TTS Demo 后台服务已停止（PID $($service.Id)）。"
