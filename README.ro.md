>en <span style="font-size: 20px;">[English version](README.md)</span>

# Garochior 

###  Joc de cărți invățat de la tata

---

## [Reguli de joc](docs/Rules/rules.ro.md)

---

## [Instrucțiuni de instalare pe Windows 10/11](docs/Install/install.ro.md)

---

## Rulare Joc

#### dublu click pe **`open_game.bat`**

### Rulare manuală

```powershell
cd calea/spre/folderul/proiectului
mvn clean javafx:run
```

Sau prin git:
```powershell
git clone https://github.com/ovidiu-144/Garochior
cd Garochior
mvn clean javafx:run
```

## Cum pornești un joc

---

**Host:**
1. Apasă **Create Server**
2. Introdu un cod de cameră, ex: `GAME-1234`
3. Trimite codul celorlalți 3 jucători
4. După ce toți jucătorii s-au conectat, apasă **Start Game** 

**Clienți:**
1. Apasă **Join Server**
2. Introdu codul primit de la host
3. Apasă **Confirm**

