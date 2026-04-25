# Get cpolar URL and update config
$ErrorActionPreference = "Continue"

Write-Host "Fetching cpolar address..." -NoNewline

# Try to get tunnels from cpolar API
$maxAttempts = 20
$attempt = 0
$httpsUrl = $null

while ($attempt -lt $maxAttempts -and -not $httpsUrl) {
    $attempt++
    try {
        $response = Invoke-RestMethod -Uri "http://127.0.0.1:4042/api/tunnels" -TimeoutSec 3
        $httpsUrl = ($response | Where-Object { $_.public_url -like "https://*" }).public_url
    } catch {
        Start-Sleep -Milliseconds 500
    }
}

if ($httpsUrl) {
    Write-Host " OK" -ForegroundColor Green
    Write-Host "Current URL: $httpsUrl" -ForegroundColor Cyan

    # Update request.js
    $requestPath = "frontend\src\utils\request.js"
    if (Test-Path $requestPath) {
        $content = Get-Content $requestPath -Raw -Encoding UTF8

        # Find and replace the cpolar URL
        $pattern = 'https://[^\s'''']+\.cpolar\.[^\s'''']+'
        if ($content -match $pattern) {
            $newContent = $content -replace $pattern, $httpsUrl
            Set-Content -Path $requestPath -Value $newContent -Encoding UTF8 -NoNewline
            Write-Host "Config updated!" -ForegroundColor Green
        } else {
            Write-Host "No cpolar URL found in config" -ForegroundColor Yellow
        }
    } else {
        Write-Host "Config file not found" -ForegroundColor Red
    }
} else {
    Write-Host " FAILED" -ForegroundColor Red
    Write-Host "Could not connect to cpolar API" -ForegroundColor Red
}
