param([int]$Port = 8787)
$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath (Split-Path $PSScriptRoot -Parent)
New-Item -ItemType Directory -Force '.local' | Out-Null
$health = Invoke-RestMethod "http://localhost:$Port/api/health" -TimeoutSec 5
if ($health.status -ne 'ok') { throw '服务未就绪。' }
foreach ($provider in @('edge', 'gemini')) {
    $body = @{ provider = $provider; text = '你好，这是 Java 语音合成测试。' } | ConvertTo-Json -Compress
    $extension = if ($provider -eq 'gemini') { 'wav' } else { 'mp3' }
    $expectedType = if ($provider -eq 'gemini') { 'audio/wav' } else { 'audio/mpeg' }
    $outputPath = ".local\smoke-$provider.$extension"
    $response = Invoke-WebRequest "http://localhost:$Port/api/tts" -Method Post `
        -ContentType 'application/json' -Body ([Text.Encoding]::UTF8.GetBytes($body)) `
        -TimeoutSec 140 -OutFile $outputPath -PassThru
    if ($response.StatusCode -ne 200 -or $response.Headers['Content-Type'] -notcontains $expectedType -or
        (Get-Item -LiteralPath $outputPath).Length -lt 100) { throw "$provider 没有返回有效音频。" }
    Write-Host "$provider PASS：$expectedType，$((Get-Item -LiteralPath $outputPath).Length) 字节，保存至 $outputPath"
}
