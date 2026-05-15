$root = Split-Path -Parent $MyInvocation.MyCommand.Path

function Start-ServiceWindow {
    param(
        [string]$Name,
        [string]$Path,
        [string]$Command
    )

    Start-Process powershell -ArgumentList @(
        "-NoExit",
        "-Command",
        "Set-Location '$Path'; Write-Host 'Starting $Name'; $Command"
    )
}

Start-ServiceWindow "Auth" "$root\MyChat-Auth" ".\mvnw.cmd spring-boot:run"
Start-ServiceWindow "UserMicroservice" "$root\MyChat-UserMicroservice" ".\mvnw.cmd spring-boot:run"
Start-ServiceWindow "Recommendation" "$root\MyChat-Recomendation" "mvn spring-boot:run"
Start-ServiceWindow "Swipes" "$root\MyChat-Swipes" "mvn spring-boot:run"
Start-ServiceWindow "Notification" "$root\MyChat-Notification" "mvn spring-boot:run"

Write-Host "Services are starting. Open web-client\index.html in your browser."
