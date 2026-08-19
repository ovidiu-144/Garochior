# Garochior — Instalare și Rulare

## Instructiuni de instalare pw Windows 10/11

## Instalare Java 21

1. Deschide PowerShell ca Administrator
2. Rulează:
```powershell
winget install EclipseAdoptium.Temurin.21.JDK
```
Sau direct de pe [link](https://www.oracle.com/java/technologies/downloads/#java21)

3. Închide și redeschide PowerShell
4. Verifică: `java -version`

---

## Instalare Maven

1. Rulează în PowerShell:
```powershell
winget install Apache.Maven
```
Sau direct de pe [link](https://maven.apache.org/download.cgi)
2. Închide și redeschide PowerShell
3. Verifică: `mvn -version`

---

## Rulare joc

```powershell
git clone https://github.com/tu/garochior.git
cd garochior
mvn clean javafx:run
```
### Instalare Zip
1. Descarcă ultima versiune `Code -> Download ZIP`
2. Dezarhivează fișierul într-un folder
3. Deschide PowerShell în folderul dezarhivat
4. Ruleaza (dupa ce ai instalat Java 21 și Maven):
```powershell
mvn clean javafx:run
``` 

---

## Cum pornești un joc

**Host:**
1. Apasă **Create Server**
2. Introdu un cod de cameră, ex: `GAME-1234`
3. Trimite codul celorlalți 3 jucători

**Clienți:**
1. Apasă **Join Server**
2. Introdu codul primit de la host
3. Apasă **Confirm**
