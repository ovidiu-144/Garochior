# Garochior - Script de instalare
# Rulează ca Administrator în PowerShell

# Auto-elevare la Administrator
if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")) {
    Start-Process PowerShell -Verb RunAs -ArgumentList "-ExecutionPolicy Bypass -File `"$PSCommandPath`""
    exit
}


Write-Host "=== Instalare Garochior ===" -ForegroundColor Cyan

if (Get-Command java -ErrorAction SilentlyContinue) {
    Write-Host "Java este deja instalat." -ForegroundColor Green
} else {
    Write-Host "Instalare Java 21..." -ForegroundColor Yellow

    # Încearcă winget mai întâi
    if (Get-Command winget -ErrorAction SilentlyContinue) {
        winget install EclipseAdoptium.Temurin.21.JDK --silent --accept-package-agreements --accept-source-agreements
    } else {
        # Fallback - download direct MSI
        Write-Host "winget nu e disponibil, descărcare directă..." -ForegroundColor Yellow
        $javaUrl = "https://github.com/adoptium/temurin21-binaries/releases/download/jdk-21.0.7%2B6/OpenJDK21U-jdk_x64_windows_hotspot_21.0.7_6.msi"
        $javaInstaller = "$env:TEMP\java21.msi"
        Invoke-WebRequest -Uri $javaUrl -OutFile $javaInstaller
        Start-Process msiexec.exe -ArgumentList "/i `"$javaInstaller`" /quiet ADDLOCAL=FeatureMain,FeatureEnvironment,FeatureJarFileRunWith,FeatureJavaHome" -Wait
        Remove-Item $javaInstaller
    }
    Write-Host "Java instalat." -ForegroundColor Green
}

# Verifică dacă maven e deja instalat
if (Get-Command mvn -ErrorAction SilentlyContinue) {
    Write-Host "Maven este deja instalat." -ForegroundColor Green
} else {
    # Instalare Chocolatey dacă nu e instalat
    if (-not (Get-Command choco -ErrorAction SilentlyContinue)) {
        Write-Host "Instalare Chocolatey..." -ForegroundColor Yellow
        Set-ExecutionPolicy Bypass -Scope Process -Force
        [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072
        Invoke-Expression ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
    }

    Write-Host "Instalare Maven..." -ForegroundColor Yellow
    choco install maven -y
    Write-Host "Maven instalat." -ForegroundColor Green
}

# Reîncarcă PATH
$env:Path = [System.Environment]::GetEnvironmentVariable("Path", "Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path", "User")

Write-Host ""
Write-Host "=== Verificare ===" -ForegroundColor Cyan
java -version
mvn -version

Write-Host ""
Write-Host "=== Pornire joc ===" -ForegroundColor Cyan
Write-Host "Rulează: mvn clean javafx:run" -ForegroundColor White
