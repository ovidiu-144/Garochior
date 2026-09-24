>ro <span style="font-size: 20px;">[Versiunea română](README.ro.md)</span>
# Garochior

###  Card game learned from my dad

---

## [Rules](docs/Rules/rules.md)

---

## [Installation Instructions for Windows 10/11](docs/Install/install.md)

---

## Running the Game

#### double click on **`open_game.bat`**

### Manual Execution

```powershell
cd path/to/the/project/folder
mvn clean javafx:run
```

Or through git:
```powershell
git clone https://github.com/ovidiu-144/Garochior
cd Garochior
mvn clean javafx:run
```

## How to Start a Game

---

**Host:**
1. Press **Create Server**
2. Enter a room code, ex: `GAME-1234`
3. Send the code to the other 3 players
4. After all players have connected, press **Start Game**

**Clients:**
1. Press **Join Server**
2. Enter the code received from the host
3. Press **Confirm**

