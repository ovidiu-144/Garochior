## Back to [Main page](../README.md)

## Installation instructions for Windows 10/11

### Automatic installation (recommended)

1. Download the project (`Code -> Download ZIP`) and extract
2. Double click **`install.bat`** — installs Java 21 and Maven automatically
3. After installation, double click **`open_game.bat`** to launch the game

---

### Manual installation (if automatic installation failed)

#### Install Java 21

1. Open PowerShell as Administrator
2. Run:
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```
Or download directly from <a href="https://www.oracle.com/java/technologies/downloads/#java21" target="_blank">here</a>

3. Close and reopen PowerShell
4. Verify: `java -version`

---

#### Install Maven

1. Run in PowerShell:
```powershell
winget install Chocolatey.Chocolatey
choco install maven
```
Or download directly from <a href="https://maven.apache.org/download.cgi" target="_blank">here</a>

2. Close and reopen PowerShell
3. Verify: `mvn -version`

---