param(
    [ValidateRange(1, 65535)][int]$Port = 8787,
    [switch]$SkipBuild,
    [switch]$Background
)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

# Refresh variables for terminals opened before the system keys were configured.
foreach ($keyName in @('GEMINI_API_KEY', 'GOOGLE_GENERATIVE_AI_API_KEY')) {
    if (-not [Environment]::GetEnvironmentVariable($keyName, 'Process')) {
        foreach ($scope in @('User', 'Machine')) {
            $keyValue = [Environment]::GetEnvironmentVariable($keyName, $scope)
            if ($keyValue) {
                [Environment]::SetEnvironmentVariable($keyName, $keyValue, 'Process')
                break
            }
        }
    }
}
if (-not (Get-Command java -ErrorAction SilentlyContinue)) { throw '需要 Java 17 或更新版本，请先安装 JDK。' }
# Resolve the real JVM: Oracle javapath/java.exe is a launcher that can spawn a child JVM.
$javaHomeLine = & java -XshowSettings:properties -version 2>&1 | Select-String '^\s+java.home\s*=' | Select-Object -First 1
if (-not $javaHomeLine) { throw '无法识别 Java 安装目录。' }
$runtimeHome = ($javaHomeLine.Line -split '=', 2)[1].Trim()
$javaExecutable = Join-Path $runtimeHome 'bin\java.exe'
if (-not (Test-Path -LiteralPath $javaExecutable)) { throw '找不到 Java 运行程序。' }
if (-not $SkipBuild) {
    if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) { throw '需要 Maven 3.9+。已构建时可使用 .\start.ps1 -SkipBuild。' }
    & mvn -B package
    if ($LASTEXITCODE -ne 0) { throw '构建或测试失败，服务未启动。' }
}
if (-not (Test-Path -LiteralPath 'target\tts-demo.jar')) { throw '找不到 target\tts-demo.jar，请先构建。' }
if ($Background) {
    $recordPath = Join-Path $PSScriptRoot '.local\server.json'
    if (Test-Path -LiteralPath $recordPath) {
        $existing = Get-Content -Raw -LiteralPath $recordPath | ConvertFrom-Json
        $existingProcess = Get-Process -Id ([int]$existing.pid) -ErrorAction SilentlyContinue
        if ($existingProcess) {
            $existingInfo = Get-CimInstance Win32_Process -Filter "ProcessId = $([int]$existing.pid)"
            $sameStart = $existingProcess.StartTime.ToUniversalTime().Ticks -eq ([datetime]$existing.startedAt).ToUniversalTime().Ticks
            if ($sameStart -and $existingProcess.ProcessName -eq 'java' -and $existingInfo.CommandLine.Contains($existing.jar)) {
                throw "本项目已有后台服务在端口 $($existing.port) 运行。请先执行 .\stop.ps1，再启动新实例。"
            }
        }
    }
    if (Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue) {
        throw "端口 $Port 已被占用。请先停止已有服务，或指定 -Port。"
    }
    New-Item -ItemType Directory -Force '.local' | Out-Null
    $jarPath = (Resolve-Path -LiteralPath 'target\tts-demo.jar').Path
    $service = Start-Process -FilePath $javaExecutable `
        -ArgumentList @('-Dfile.encoding=UTF-8', '-jar', "`"$jarPath`"", "$Port") `
        -WindowStyle Hidden -RedirectStandardOutput '.local\server.out.log' `
        -RedirectStandardError '.local\server.err.log' -PassThru
    @{ pid = $service.Id; jar = $jarPath; port = $Port; startedAt = $service.StartTime.ToString('o') } |
        ConvertTo-Json | Set-Content -Encoding utf8 '.local\server.json'
    for ($attempt = 0; $attempt -lt 20; $attempt++) {
        if ($service.HasExited) { throw 'Java 服务启动失败，请查看 .local\server.err.log。' }
        try {
            $health = Invoke-RestMethod "http://127.0.0.1:$Port/api/health" -TimeoutSec 1
            if ($health.status -eq 'ok') {
                Write-Host "服务已在后台启动：http://localhost:$Port（PID $($service.Id)）"
                Write-Host '停止服务：.\stop.ps1'
                return
            }
        } catch { }
        Start-Sleep -Milliseconds 500
    }
    throw '服务未在预期时间内就绪，请查看 .local 日志。'
}
Write-Host "服务地址：http://localhost:$Port（按 Ctrl+C 停止）"
& $javaExecutable -Dfile.encoding=UTF-8 -jar target\tts-demo.jar $Port
if ($LASTEXITCODE -ne 0) { throw "Java 服务退出，代码 $LASTEXITCODE。请检查端口是否已被占用。" }
