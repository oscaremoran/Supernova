# Supernova: Where We Are

*Snapshot: 2026-11-15. Mindustry V8, build 160.7. Java mod, about 1,600 lines across 17 commits.*

Supernova adds a new star system, the **Prime System**, and a new campaign planet, **Aphelion**. Aphelion is a cold, rocky world whose real wealth is in the asteroid belt overhead. You build an economy that reaches into orbit while fighting off the **Aliens**.

Content currently stops at the **silicon-equivalent tier** (Conductium).

---

## 1. The world

| | |
|---|---|
| **Star** | Prime System. A separate solar system from Serpulo and Erekir, so it shows as its own header in the planet list. |
| **Planet** | Aphelion. Terrain is stone, basalt, dacite, ice and snow, with frozen lakes. |
| **Sectors** | All procedurally generated (from a re-skinned Serpulo generator). Each has a name: the start sector is **Landing Site**, and others get names like *Pale Expanse* or *Ember Gulch*. |
| **Enemy** | The **Aliens**, a black team (with a violet rim so it stays visible) in team slot 77. |
| **Starting items** | Landing Site: 80 ferrite (a lean start, like Ground Zero). Other sectors: 300 ferrite, 150 cobaltine, 100 quartzite. |
| **Research** | All Aphelion research costs **30%** of normal. |

Supernova content only appears on Aphelion, and vanilla Serpulo content doesn't appear there.

---

## 2. Resources

| Tier | Resource | How you get it | Used for |
|---|---|---|---|
| 1 | **Ferrite** | Surface ore (gray-brown) | Almost everything early |
| 1 | **Cobaltine** | Surface ore (blue) | Turrets, power, electronics |
| 1 | **Quartzite** | Surface ore (pale pink) | Solar, smelting flux |
| 2 | **Ferrosteel** | Smelter: 2 ferrite + 1 quartzite | Advanced buildings, ammo, walls |
| 2 | **Iridium** | **Orbital Mining Station only** (Metallic Belt) | Conductium, Haulers |
| 2 | **Cryo-Ice** | **Orbital Mining Station only** (Icy Belt) | Melted into Volatiles |
| 3 | **Conductium** | Fabricator: 2 cobaltine + 1 iridium | The "silicon": advanced electronics |
| Fluid | **Volatiles** | Ice Melter: cryo-ice | Uplink booster, Volatile Burner fuel |

**The core loop:** surface ores → ferrosteel → **Orbital Mining Station** → iridium and cryo-ice from space → conductium and volatiles. Volatiles then speed up the uplink, so the belt pays for itself.

---

## 3. Buildings (22)

Power is shown in the game's units: 1.0 in code is 60 power/second in-game.

### Production
| Block | Size | Cost | Notes |
|---|---|---|---|
| Ferrite Drill | 2×2 | 12 ferrite | Tier 2 (mines all three surface ores). Unlocked from the start. |
| **Orbital Mining Station** | 3×3 | 150 ferrite, 80 cobaltine, 120 ferrosteel | Uses 120 power/s. Click it to pick a zone. Volatiles make missions **2.5× faster**. A drone pod launches and lands. |

**Uplink zones:**
| Zone | Mission time | Cargo per mission |
|---|---|---|
| Metallic Belt | 30 s | 6–10 iridium, 4–8 ferrite, 0–4 cobaltine |
| Icy Belt | 25 s | 8–12 cryo-ice, 2–5 quartzite |

Switching zones aborts the current mission. If the uplink's storage is full, it pauses.

### Crafting
| Block | Size | Recipe | Time | Power |
|---|---|---|---|---|
| Ferrosteel Smelter | 2×2 | 2 ferrite + 1 quartzite → 1 ferrosteel | 0.8 s | 30/s |
| Ice Melter | 2×2 | 1 cryo-ice → volatiles (12/s) | 0.7 s | 36/s |
| Conductium Wirer | 2×2 | 2 cobaltine + 1 iridium → 1 conductium | 1.2 s | 72/s |

### Logistics
| Block | Cost | Notes |
|---|---|---|
| Ferrite Conveyor | 1 ferrite | About 4.8 items/s. Unlocked from the start. |
| Ferrite Junction | 3 ferrite | |
| Ferrite Router | 3 ferrite | |
| Ferrite Bridge | 6 ferrite, 6 cobaltine | Range 4 |

### Power
| Block | Size | Output | Notes |
|---|---|---|---|
| Solar Array | 2×2 | 30/s | 25 ferrite, 15 cobaltine, 20 quartzite |
| Volatile Burner | 2×2 | 180/s | Burns 6 volatiles/s |
| Ferrite Power Node | 1×1 | | Up to 10 links, range 6 |

### Defense
| Block | Notes |
|---|---|
| **Bolter** (2×2 turret) | Twin barrels, range about 16 tiles. **Ferrite ammo**: 14 damage. **Ferrosteel ammo**: 26 damage and pierces 2 enemies. |
| Ferrite Wall / Large | 340 / 1,360 HP |
| Ferrosteel Wall / Large | 560 / 2,240 HP, 3 armor |

### Expansion blocks
| Block | Size | Cost | Notes |
|---|---|---|---|
| **Pressure Drill** | 3×3 | 40 ferrite, 30 ferrosteel, 20 cobaltine | Tier 3, faster; 24 power/s; Volatiles boost it 1.6× |
| **Igniter** | 1×1 turret | 30 ferrite, 20 ferrosteel, 15 cobaltine | Flamethrower burning Volatiles; range 100; pierces 3 and sets enemies on fire. The swarm counter. |
| **Arc Coil** | 1×1 turret | 40 ferrosteel, 30 conductium, 50 cobaltine | Chain lightning (22 damage); range 110; 198 power/s, no ammo |
| **Holding Station** | 2×2 | 60 ferrite, 40 ferrosteel | Stores 300 of each item |
| **Retriever** | 1×1 | 25 ferrosteel, 15 conductium | Pulls items out of Holding Stations, the core or crafters |
| **Overflow / Ferrite Underflow** | 1×1 | 4 ferrite, 2 cobaltine | Standard gates |
| **Power Station** | 1×1 | 10 ferrite, 20 cobaltine | Stores 1,000 power |
| **Large Ferrite Power Node** | 2×2 | 10 ferrosteel, 15 cobaltine, 3 conductium | 15 links, range 15 |
| **Repairer** | 1×1 | 25 ferrite, 10 cobaltine | Heals 12% every ~3 s within 5 tiles; quartzite boosts it |
| **Foundry** | 3×3 | 120 ferrite, 80 cobaltine, 60 ferrosteel | Builds Sentry (25 s), Warden (40 s), Bastion (60 s); 90 power/s |

### Units and core
| Block | Notes |
|---|---|
| Drone Bay (3×3) | Builds Prospectors (20 s), Surveyors (35 s) and Haulers (40 s). Uses 72 power/s. |
| Core: Outpost (3×3) | 1,400 HP, stores 4,000 of each item, spawns the Pioneer. |

---

## 4. Your units

| Unit | Role | Key stats |
|---|---|---|
| **Pioneer** | Core unit: flies, builds, mines | Fast (speed 4.2). **Cobalt laser** for 20 damage, about 11 tiles. Mines tier 2. |
| **Prospector** | Mining drone, runs on its own | Mines ferrite, cobaltine and quartzite and delivers them to the core. Carries 20. |
| **Surveyor** | Better mining drone | Faster mining, carries 40, armored. |
| **Hauler** | Cargo drone | Flies uplink cargo to the core. Carries 60. |

**Combat gunships** (built in the Foundry):
| Unit | Tier | HP / Armor | Weapon | Cost |
|---|---|---|---|---|
| **Sentry** | T1 | 200 / 1 | Twin cobalt bolts | 20 ferrite, 15 cobaltine |
| **Warden** | T2 | 560 / 4 | Heavy ferrosteel slugs with splash | 30 ferrosteel, 30 cobaltine |
| **Bastion** | T3 | 1,150 / 7 | Conductium beam (70 damage) + bolts | 50 ferrosteel, 30 conductium |

---

## 5. The Aliens

Black chitin, all legged walkers, in two unit trees. **Bugs** glow violet; **Predators** glow crimson.

| Tree | Unit | Tier | HP / Armor | Attack |
|---|---|---|---|---|
| Bugs (light) | **Hatchling** | T1 | 130 / 0 | Fast, rapid little acid spits (range 96). Weak but annoying. |
| Bugs (light) | **Malign** | T2 | 480 / 3 | Lobs purple orbs (range ~125) that **split into a ring of 5 smaller orbs**. |
| Predators (beatdown) | **Leacher** | T1 | 520 / 3 | Short-range bites with **lifesteal** (heals 30% of damage dealt). |
| Predators (beatdown) | **Ripper** | T2 | 1,100 / 7 | **Vela-style sustained crimson beam** (range 120). Slows itself while firing. |

Waves are generated from vanilla wave patterns, then converted: light, fast and air units become Bugs; heavy units become Predators.

---|---|---|---|
| **Spawnling** | Ground swarm | 140 | Bursts in acid when close (75 splash damage) |
| **Skitter** | Ground T1 | 170 | Acid bolts |
| **Stalker** | Ground T2 | 520 | Heavier acid bolts, armored |
| **Brute** | Ground T3 | 1,000 | Explosive acid globs (artillery) plus bolts. **Steps over walls.** |
| **Wisp** | Air T1 | 70 | Fast; circles its targets |
| **Shrike** | Air T2 | 340 | Twin acid bolts |
| **Harrower** | Air T3 | 700 | Homing acid bolts |

Waves are generated from vanilla wave patterns, then converted to Aliens by tier. Vanilla T4–T5 units become Brutes and Harrowers.

---

## 5b. Alien structures

Editor-only: players can't build them, but you can place them in maps. Procedural enemy bases on Aphelion are converted to use them automatically. They need no ammo or power.

| Structure | Size | What it does |
|---|---|---|
| **Hive** | 3×3 | Hatches a Hatchling every 40 s, up to 3 alive nearby. |
| **Spore Pod** | 1×1 turret | Acid spit, range 110 (Bolters out-range it). |
| **Acid Spire** | 2×2 turret | Range 125 (just inside the Bolter's 130); acid orbs burst into 6 smaller orbs. |
| **Chitin Wall** / Large | 1×1 / 2×2 | 440 / 1,760 HP, armor 2, **regrows 1% HP per second**. |
| **Core: Brood** | 4×4 | The Alien core: 5,000 HP. Destroy them all to win attack sectors. |

## 5c. Balance (tested in the simulator)

`tools/balance-sim/BalanceSim.java` runs the **real game simulation headlessly** (no window): a flat arena, Bolters fed ammo continuously, and Aliens that walk straight at them. Rerun it after any stat change:

```sh
./gradlew modJar
GAME="$HOME/Library/Application Support/Steam/steamapps/common/Mindustry/Mindustry.app/Contents/Resources/desktop.jar"
javac -cp "$GAME:build/classes/java/main" -d build/sim tools/balance-sim/BalanceSim.java
java  -cp "$GAME:build/classes/java/main:build/sim" BalanceSim
```

**Takeaways:**
- **Hatchlings:** 2 ferrite Bolters beat 8 at once. With walls, the Bolters barely take damage.
- **Malign:** needs 2 Bolters, or ferrosteel ammo.
- **Leacher:** beats 1 ferrite Bolter; 2 Bolters, or 1 with ferrosteel ammo, win.
- **Ripper:** ferrite ammo loses even with 2 Bolters; **ferrosteel is required**.
- **Spore Pod** loses to 1 Bolter. **Acid Spire** beats 1 Bolter, loses to 2.
- **Hive:** one Hatchling per 40 s, capped at 3. **Chitin Wall:** regrows 1%/s.
- **Gunships:** Sentries beat Hatchlings but lose to Leachers; Wardens handle Leachers and Malign; 1 Bastion beats a Ripper, and 2 Bastions clear an Alien turret cluster with no losses.
- **Igniter** is the swarm counter (1 beats 14 Hatchlings). **Arc Coil** beats 6, loses to 10.

**Latest results:**
```
=== Supernova balance sim (60 ticks = 1 s) ===

1 Bolter(ferrite) vs 1 hatchling       PLAYER WINS    9.8s  bolters left 1/1 (568 HP)  aliens left 0/1
2 Bolter(ferrite) vs 1 hatchling       PLAYER WINS    8.1s  bolters left 2/2 (1192 HP)  aliens left 0/1
1 Bolter(ferrite) vs 1 malign          ALIENS WIN    24.4s  bolters left 0/1 (0 HP)  aliens left 1/1
2 Bolter(ferrite) vs 1 malign          PLAYER WINS   17.3s  bolters left 2/2 (874 HP)  aliens left 0/1
1 Bolter(ferrite) vs 1 leacher         ALIENS WIN    24.1s  bolters left 0/1 (0 HP)  aliens left 1/1
2 Bolter(ferrite) vs 1 leacher         PLAYER WINS   19.6s  bolters left 2/2 (888 HP)  aliens left 0/1
1 Bolter(ferrite) vs 1 ripper          ALIENS WIN    27.1s  bolters left 0/1 (0 HP)  aliens left 1/1
2 Bolter(ferrite) vs 1 ripper          ALIENS WIN    44.1s  bolters left 0/2 (0 HP)  aliens left 1/1

1 Bolter(ferrosteel) vs 1 hatchling    PLAYER WINS    8.2s  bolters left 1/1 (592 HP)  aliens left 0/1
2 Bolter(ferrosteel) vs 1 hatchling    PLAYER WINS    7.4s  bolters left 2/2 (1200 HP)  aliens left 0/1
1 Bolter(ferrosteel) vs 1 malign       PLAYER WINS   17.7s  bolters left 1/1 (256 HP)  aliens left 0/1
2 Bolter(ferrosteel) vs 1 malign       PLAYER WINS   13.7s  bolters left 2/2 (1002 HP)  aliens left 0/1
1 Bolter(ferrosteel) vs 1 leacher      PLAYER WINS   19.9s  bolters left 1/1 (264 HP)  aliens left 0/1
2 Bolter(ferrosteel) vs 1 leacher      PLAYER WINS   13.9s  bolters left 2/2 (1200 HP)  aliens left 0/1
1 Bolter(ferrosteel) vs 1 ripper       ALIENS WIN    27.0s  bolters left 0/1 (0 HP)  aliens left 1/1
2 Bolter(ferrosteel) vs 1 ripper       PLAYER WINS   23.6s  bolters left 2/2 (705 HP)  aliens left 0/1

2 Bolter(ferrite) vs 2 hatchling                 PLAYER WINS    9.9s  bolters left 2/2 (1160 HP)  aliens left 0/2
2 Bolter(ferrite) vs 4 hatchling                 PLAYER WINS   13.6s  bolters left 2/2 (992 HP)  aliens left 0/4
2 Bolter(ferrite) vs 6 hatchling                 PLAYER WINS   16.9s  bolters left 2/2 (688 HP)  aliens left 0/6
2 Bolter(ferrite) vs 8 hatchling                 PLAYER WINS   21.0s  bolters left 2/2 (196 HP)  aliens left 0/8
2 Bolter(ferrite)+walls vs 4 hatchling           PLAYER WINS   13.5s  bolters left 2/2 (1200 HP)  aliens left 0/4
2 Bolter(ferrite)+walls vs 8 hatchling           PLAYER WINS   21.0s  bolters left 2/2 (1176 HP)  aliens left 0/8

2 Bolter(ferrosteel)+walls vs 1 leacher          PLAYER WINS   14.3s  bolters left 2/2 (1200 HP)  aliens left 0/1
2 Bolter(ferrosteel)+walls vs 2 leacher          PLAYER WINS   14.3s  bolters left 2/2 (1200 HP)  aliens left 0/2
2 Bolter(ferrosteel)+walls vs 3 leacher          PLAYER WINS   19.5s  bolters left 2/2 (1128 HP)  aliens left 0/3
3 Bolter(ferrosteel)+walls vs 1 ripper           PLAYER WINS   20.7s  bolters left 3/3 (1485 HP)  aliens left 0/1
3 Bolter(ferrosteel)+walls vs 2 ripper           PLAYER WINS   20.6s  bolters left 3/3 (1323 HP)  aliens left 0/2

Hive (idle Hatchlings stay put): 45s:1  85s:2  125s:3  180s:3  
Chitin Wall regrowth: 220 -> 264 HP in 10 s (max 440)
Spore Pod vs 1 Bolter(ferrite)                   PLAYER WINS   11.5s  bolters left 1/1   (alien range 110, bolter range 130)
Acid Spire vs 1 Bolter(ferrite)                  ALIENS WIN    17.0s  bolters left 0/1   (alien range 125, bolter range 130)
Acid Spire vs 2 Bolter(ferrite)                  PLAYER WINS   22.9s  bolters left 1/2   (alien range 125, bolter range 130)

2 Sentry vs 3 hatchling                          PLAYER WINS    9.6s  mine left 2/2   aliens left 0/3
2 Sentry vs 1 leacher                            ALIENS WIN    16.6s  mine left 0/2   aliens left 1/1
2 Warden vs 1 leacher                            PLAYER WINS    7.7s  mine left 2/2   aliens left 0/1
2 Warden vs 2 malign                             PLAYER WINS   11.9s  mine left 2/2   aliens left 0/2
1 Bastion vs 1 ripper                            PLAYER WINS   18.3s  mine left 1/1   aliens left 0/1
2 Bastion vs 2 ripper                            PLAYER WINS   18.3s  mine left 2/2   aliens left 0/2
2 Bastion vs Acid Spire + 2 Spore Pods           PLAYER WINS   18.0s  mine left 2/2   alien structures left 0/3
4 Warden vs Acid Spire + 2 Spore Pods            PLAYER WINS   16.2s  mine left 4/4   alien structures left 0/3

1 Igniter (volatiles) vs 6 hatchling             PLAYER WINS    9.0s  turret HP 372   aliens left 0/6
1 Igniter (volatiles) vs 10 hatchling            PLAYER WINS   10.2s  turret HP 236   aliens left 0/10
1 Igniter (volatiles) vs 14 hatchling            PLAYER WINS   11.1s  turret HP 88   aliens left 0/14
1 Arc Coil (powered) vs 6 hatchling              PLAYER WINS   16.4s  turret HP 188   aliens left 0/6
1 Arc Coil (powered) vs 10 hatchling             ALIENS WIN    12.9s  turret HP 0   aliens left 4/10
1 Arc Coil (powered) vs 14 hatchling             ALIENS WIN    11.2s  turret HP 0   aliens left 8/14
1 Bolter (ferrite) vs 6 hatchling                ALIENS WIN    15.3s  turret HP 0   aliens left 5/6
1 Bolter (ferrite) vs 10 hatchling               ALIENS WIN    12.1s  turret HP 0   aliens left 9/10
1 Bolter (ferrite) vs 14 hatchling               ALIENS WIN    10.8s  turret HP 0   aliens left 14/14
```

## 6. Tutorial## 6. Tutorial (Landing Site)

Ground Zero style. Steps unlock one at a time, and labels appear on the map while their step is active.

1. **Mine ferrite by hand** (label on the nearest ferrite ore)
2. **Place 4 Ferrite Drills** (on that ore)
3. **Conveyor ore to the core** (label on the core)
4. **Get cobaltine** (label on cobaltine ore)
5. **Research the Bolter**
6. **Build 2 Bolters** (red label at the Alien spawn)
7. **Get quartzite** (label on quartzite ore)
8. **Build 2 Solar Arrays**
9. **Make ferrosteel**
10. **Build a Drone Bay and 2 Prospectors**
11. **Build an Orbital Mining Station and get iridium**

---

## 7. Tech tree (Aphelion)

```
Core: Outpost
├─ Ferrite Conveyor → Junction → Router → Bridge
├─ Ferrite Drill → Power Node → Solar Array → Ferrosteel Smelter → Orbital Mining Station
│                                                                  ├─ Ice Melter → Volatile Burner
│                                                                  └─ Conductium Wirer
├─ Bolter → Ferrite Wall → Large Ferrite Wall
│                        └─ Ferrosteel Wall → Large Ferrosteel Wall
├─ Drone Bay → Prospector → Surveyor, Hauler
└─ (items) Ferrite → Cobaltine → Iridium → Conductium
                   └ Quartzite → Ferrosteel, Cryo-Ice → Volatiles
```

---

## 7b. Story sectors (hand-made maps)

Each sector activates automatically once its map file is in `assets/maps/`. Until then, that spot on the globe stays procedural.

| # | Sector | Map file | Globe sector | Win | Waves |
|---|---|---|---|---|---|
| 1 | Landing Site | `landing-site.msav` | 15 (start) | Survive 15 waves | Hatchlings only; first wave 3× later than normal |
| 2 | Frost Ridge | `frost-ridge.msav` | 170 | 20 waves | + Leachers from wave 6 |
| 3 | Uplink Crater | `uplink-crater.msav` | 64 | 25 waves | + Malign from wave 8 |
| 4 | Ice Shelf | `ice-shelf.msav` | 218 | 30 waves | + Rippers from wave 18 |
| 5 | The Hive | `the-hive.msav` | 23 | **Destroy all Core: Brood blocks** | All Aliens |

- Each sector unlocks after the previous one is captured, and appears in the tech tree.
- **Waves:** if your map's waves use Alien units, I keep them. Otherwise the designed set above is used.
- **Starting items** grow with each sector.

## 8. Known gaps

- **Not yet tested in-game:** the latest batch (new sprites, laser Pioneer, GZ tutorial, Aliens, copper fix, research costs).
- No **hand-made story sectors**; every sector is procedural.
- The Uplink's "needs propellant" zone feature is built in, but no zone uses it now that the Deep Belt is cut.
- **Desktop only**: no Android build.
- Sprites are generated by code (`tools/sprites.py`) and are meant to be iterated on.
- Hauler logistics haven't been balanced. Haulers cost iridium, so they only come after the first uplink.

## 9. Project files

| Path | What |
|---|---|
| `src/supernova/content/` | All content: items, liquids, blocks, units, Aliens, planets, tech tree, teams |
| `src/supernova/world/` | Orbital Mining Station, Hive, Chitin Wall, editor tweaks, sector names, planet generator, tutorial, Alien wave designs |
| `src/supernova/ai/HaulerAI.java` | Hauler behavior |
| `assets/bundles/bundle.properties` | All in-game text |
| `tools/sprites.py` | Generates every sprite |
| `tools/conveyor.py` | Builds conveyor and bridge frames from the game's layout |
| `docs/DESIGN.md` | Design doc and milestones |
| `./gradlew deploy` | Build and sync to Steam Mindustry |
