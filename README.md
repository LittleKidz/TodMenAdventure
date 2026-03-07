
<div align="center">

```
████████╗ ██████╗ ██████╗ ███╗   ███╗███████╗███╗   ██╗
╚══██╔══╝██╔═══██╗██╔══██╗████╗ ████║██╔════╝████╗  ██║
   ██║   ██║   ██║██║  ██║██╔████╔██║█████╗  ██╔██╗ ██║
   ██║   ██║   ██║██║  ██║██║╚██╔╝██║██╔══╝  ██║╚██╗██║
   ██║   ╚██████╔╝██████╔╝██║ ╚═╝ ██║███████╗██║ ╚████║
   ╚═╝    ╚═════╝ ╚═════╝ ╚═╝     ╚═╝╚══════╝╚═╝  ╚═══╝
 █████╗ ██████╗ ██╗   ██╗███████╗███╗   ██╗████████╗██╗   ██╗██████╗ ███████╗
██╔══██╗██╔══██╗██║   ██║██╔════╝████╗  ██║╚══██╔══╝██║   ██║██╔══██╗██╔════╝
███████║██║  ██║██║   ██║█████╗  ██╔██╗ ██║   ██║   ██║   ██║██████╔╝█████╗
██╔══██║██║  ██║╚██╗ ██╔╝██╔══╝  ██║╚██╗██║   ██║   ██║   ██║██╔══██╗██╔══╝
██║  ██║██████╔╝ ╚████╔╝ ███████╗██║ ╚████║   ██║   ╚██████╔╝██║  ██║███████╗
╚═╝  ╚═╝╚═════╝   ╚═══╝  ╚══════╝╚═╝  ╚═══╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝╚══════╝
```

### A 2-Player Turn-Based Strategy Battle Game

[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17%2B-0076C8?style=for-the-badge&logo=java&logoColor=white)](https://openjfx.io/)
[![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)](LICENSE)
[![Version](https://img.shields.io/badge/Version-1.0.0-purple?style=for-the-badge)](https://github.com/2110215-ProgMeth/cedt-project-2025-2-todmen/releases)
[![Javadoc](https://img.shields.io/badge/Javadoc-Available-blue?style=for-the-badge)](https://todmenadventure.netlify.app/)

[🎮 Play Now](#-installation) · [📖 Documentation](https://todmenadventure.netlify.app/) · [🐛 Report Bug](https://github.com/2110215-ProgMeth/cedt-project-2025-2-todmen/issues)

</div>

---

## About The Game

**TodMen Adventure** is a local 2-player tactical battle game built with Java and JavaFX. Two players select unique characters, navigate a randomized 11×11 map, collect items, fight Goblins for permanent stat boosts, and battle each other in turn-based combat — all while a deadly **shrinking lava zone** closes in.

> *Developed as part of the Programming Methodology course (2110215) at the Department of Computer Engineering, Faculty of Engineering, Chulalongkorn University.*

---

##  Key Features

| Feature | Description |
|---|---|
|  **10 Unique Maps** | Randomly selected each game using a shuffle-bag system — no repeat until all 10 are played |
|  **4 Playable Characters** | Each with unique stats, movement rules, and a special skill |
|  **Shrinking Lava Zone** | Every 8 rounds the safe zone contracts — stay inside or take damage |
|  **Goblin Encounters** | Defeat Goblins to earn permanent random stat boosts (ATK / DEF / Max HP) |
|  **Item System** | Collect HP Potions, ATK Elixirs, and DEF Elixirs scattered across the map |
|  **Skill Cooldowns** | Each character's special skill has a 3-turn cooldown after use |
|  **Simultaneous Combat** | Both players choose their action secretly, then actions resolve at the same time |

---

##  Characters

<table>
  <thead>
    <tr>
      <th align="center">Character</th>
      <th align="center">HP</th>
      <th align="center">ATK</th>
      <th align="center">DEF</th>
      <th>Special Skill</th>
      <th>Unique Trait</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td align="center"><strong> Knight</strong></td>
      <td align="center">120</td>
      <td align="center">18</td>
      <td align="center">7</td>
      <td><b>Slash</b> — Attacks twice per turn</td>
      <td>Highest HP & DEF — the tank</td>
    </tr>
    <tr>
      <td align="center"><strong> Archer</strong></td>
      <td align="center">85</td>
      <td align="center">24</td>
      <td align="center">3</td>
      <td><b>Strike</b> — Bypasses all defense</td>
      <td>Can move through <code>TREE</code> cells</td>
    </tr>
    <tr>
      <td align="center"><strong> Reborn</strong></td>
      <td align="center">100</td>
      <td align="center">16</td>
      <td align="center">5</td>
      <td><b>Heal</b> — Restores 30 HP</td>
      <td>5-slot inventory + walks on <code>RIVER</code></td>
    </tr>
    <tr>
      <td align="center"><strong> Alien</strong></td>
      <td align="center">70</td>
      <td align="center">20</td>
      <td align="center">2</td>
      <td><b>Fireball</b> — Magic damage ×1.5, pierces defense</td>
      <td>All attacks bypass defense + diagonal movement</td>
    </tr>
  </tbody>
</table>

---

##  Terrain Types

| Tile | Symbol | Description |
|---|:---:|---|
|  **Normal** | `·` | Open ground — all characters can pass |
|  **River** | `~` | Impassable — only **Reborn** can cross |
|  **Tree** | `T` | Impassable — only **Archer** can pass through |
|  **Rock** | `#` | Blocked for all characters |
|  **Item** | `I` | Step on it to auto-collect a random item |
|  **Goblin** | `G` | Step on it to trigger a battle — win for a permanent stat boost |
|  **Lava** | `X` | Shrinking zone boundary — deals **10 HP** damage per round |

---

##  Battle System

Each battle turn is **simultaneous** — both players choose an action secretly and they resolve at the same time.

```
┌─────────────────────────────────────────┐
│            BATTLE ACTIONS               │
├────────────┬────────────────────────────┤
│  ATTACK    │  Deal ATK − DEF damage     │
│  DEFEND    │  Halve all incoming damage │
│  USE ITEM  │  Consume an item from bag  │
│  USE SKILL │  Activate special skill    │
└────────────┴────────────────────────────┘
```

> **Note:** If your skill is on cooldown or your bag is empty when you choose that action, the game automatically falls back to a normal attack.

---

##  Controls

### Player 1
| Action | Key |
|---|:---:|
| Move Up | `W` |
| Move Down | `S` |
| Move Left | `A` |
| Move Right | `D` |
| Diagonal (Alien only) | `Q` `E` `Z` `X` |

### Player 2
| Action | Key |
|---|:---:|
| Move Up | `I` |
| Move Down | `K` |
| Move Left | `J` |
| Move Right | `L` |
| Diagonal (Alien only) | `Y` `O` `N` `M` |

---

##  Installation

### Prerequisites

- **Java 17+** — [Download here](https://adoptium.net/)
- **JavaFX 17+** — [Download here](https://openjfx.io/)

### Run the Game

**1. Download the JAR** from [Releases](https://github.com/2110215-ProgMeth/cedt-project-2025-2-todmen/releases) or build from source.

**3. Launch with JavaFX**
```bash
java --module-path {javafx_path} --add-modules
javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.web,javafx.swing 
--enable-nativeaccess=javafx.graphics,javafx.media 
-jar TodMenAdventure-1.ja

```

> 💡 **Tip:** Replace `/path/to/javafx/lib` with your actual JavaFX SDK `lib` folder path.

---

##  Project Structure

```
TodMenAdventure/
├── application/                  # JavaFX UI Layer
│   ├── Main.java                 # Entry point
│   ├── GameController.java       # Connects View ↔ Model
│   ├── BattleScene.java          # Battle phase UI
│   ├── BoardScene.java           # Map movement UI
│   ├── CharacterSelectScene.java # Character selection UI
│   ├── MainMenuScene.java        # Main menu UI
│   └── GameOverScene.java        # Game over screen
│
├── model/                        # Game Logic (Pure Java)
│   ├── battle/                   # Turn-based combat engine
│   │   ├── BattleManager.java    # Resolves simultaneous actions
│   │   ├── BattleAction.java     # Action enum (ATTACK, DEFEND, ...)
│   │   ├── ActionResult.java     # Per-entity turn result
│   │   └── BattleResult.java     # Full turn outcome
│   │
│   ├── entity/                   # Characters & enemies
│   │   ├── Entity.java           # Abstract base (HP, ATK, DEF)
│   │   ├── Player.java           # Abstract player base
│   │   ├── KnightPlayer.java     # Knight implementation
│   │   ├── ArcherPlayer.java     # Archer implementation
│   │   ├── RebornPlayer.java     # Reborn implementation
│   │   ├── AlienPlayer.java      # Alien implementation
│   │   ├── Goblin.java           # AI-controlled enemy
│   │   └── CharacterType.java    # Factory enum
│   │
│   ├── game/                     # Game state & flow
│   │   ├── GameManager.java      # Central game controller
│   │   ├── GameState.java        # State enum
│   │   ├── ZoneManager.java      # Shrinking lava zone
│   │   └── GameEventListener.java # Observer interface
│   │
│   ├── interfaces/               # Core contracts
│   │   ├── AIControllable.java   # AI decision-making
│   │   ├── Applicable.java       # Applier (items & skills)
│   │   └── Cooldownable.java     # Cooldown management
│   │
│   ├── item/                     # Collectible items
│   │   ├── Item.java             # Abstract base
│   │   ├── HealItem.java         # +20 HP
│   │   ├── BuffItem.java         # +3 ATK (battle only)
│   │   ├── DefenseItem.java      # +3 DEF (battle only)
│   │   └── TomeItem.java         # Goblin reward (permanent boost)
│   │
│   ├── map/                      # Map system
│   │   ├── MapGrid.java          # 11×11 cell grid
│   │   ├── Cell.java             # Individual cell data
│   │   ├── CellType.java         # Terrain type enum
│   │   └── MapLoader.java        # 10 preset maps + shuffle bag
│   │
│   └── skill/                    # Skill system
│       ├── Skill.java            # Cooldown + apply logic
│       └── SkillEffect.java      # Effect enum
│
└── resources/                    # Assets (sprites, audio, fonts)
```

---

##  Architecture

TodMen Adventure follows the **MVC (Model-View-Controller)** pattern:

```
┌──────────────┐      events       ┌──────────────────────┐
│   JavaFX UI  │ ◄──────────────── │    GameManager       │
│  (View)      │                   │    (Model)           │
│              │ ──── calls ──────► │                      │
└──────┬───────┘                   └──────────┬───────────┘
       │                                      │
       └──────── GameController ──────────────┘
                    (Controller)
```

**Key Design Patterns:**
- **Observer** — `GameEventListener` decouples UI from game logic
- **Factory** — `CharacterType` enum creates player subclasses
- **Strategy** — `AIControllable` interface for Goblin AI behaviour
- **Template Method** — `Entity` → `Player` → concrete characters

---

## 📚 Documentation

Full Javadoc is available at:

**🔗 [https://todmenadventure.netlify.app/](https://todmenadventure.netlify.app/)**

---

##  Team

| Name |
|---|---|
| **Wan-Ek Phothavorn** |
| **Chawin Sinman** |
| **Kirakorn Vitayawatanakul** |
| **Worasret Kulkit** |

>  Department of Computer Engineering, Faculty of Engineering, **Chulalongkorn University**
> Course: **2110215 Programming Methodology** (2025)

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

<div align="center">

Made with ❤️ by Team TodMen · Chulalongkorn University 2025

</div>
=======

>>>>>>> 75860cc53165b6253fd1361fb5ff5e5deaa8aca0
