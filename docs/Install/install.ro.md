## Inapoi la [Pagina principală](../README.ro.md)


## Instrucțiuni de instalare pe Windows 10/11

### Instalare automată (recomandat)

1. Descarcă proiectul (`Code -> Download ZIP`) și dezarhivează
2. Dă dublu click pe **`install.bat`** — instalează Java 21 și Maven automat
3. După instalare, dă dublu click pe **`open_game.bat`** pentru a porni jocul

---

### Instalare manuală (dacă instalarea automată nu a funcționat)

#### Instalare Java 21

1. Deschide PowerShell ca Administrator
2. Rulează:
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```
Sau descarcă direct de pe <a href="https://www.oracle.com/java/technologies/downloads/#java21" target="_blank">link</a>

3. Închide și redeschide PowerShell
4. Verifică: `java -version`

---

#### Instalare Maven

1. Rulează în PowerShell:
```powershell
winget install Chocolatey.Chocolatey
choco install maven
```
Sau descarcă direct de pe <a href="https://maven.apache.org/download.cgi" target="_blank">link</a>

2. Închide și redeschide PowerShell
3. Verifică: `mvn -version`

---