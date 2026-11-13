# Supernova: Where We Are

*Snapshot: 2026-11-13. Mindustry V8, build 160.7. Java mod, about 1,600 lines across 17 commits.*

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
| 2 | **Iridium** | **Orbital Uplink only** (Metallic Belt) | Conductium, Haulers |
| 2 | **Cryo-Ice** | **Orbital Uplink only** (Icy Belt) | Melted into Volatiles |
| 3 | **Conductium** | Fabricator: 2 cobaltine + 1 iridium | The "silicon": advanced electronics |
| Fluid | **Volatiles** | Ice Melter: cryo-ice | Uplink booster, Volatile Burner fuel |

**The core loop:** surface ores → ferrosteel → **Orbital Uplink** → iridium and cryo-ice from space → conductium and volatiles. Volatiles then speed up the uplink, so the belt pays for itself.

---

## 3. Buildings (22)

Power is shown in the game's units: 1.0 in code is 60 power/second in-game.

### Production
| Block | Size | Cost | Notes |
|---|---|---|---|
| Ferrite Drill | 2×2 | 12 ferrite | Tier 2 (mines all three surface ores). Unlocked from the start. |
| **Orbital Uplink** | 3×3 | 150 ferrite, 80 cobaltine, 120 ferrosteel | Uses 120 power/s. Click it to pick a zone. Volatiles make missions **2.5× faster**. A drone pod launches and lands. |

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
| Conductium Fabricator | 2×2 | 2 cobaltine + 1 iridium → 1 conductium | 1.2 s | 72/s |

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

---

## 5. The Aliens

Black chitin, all legged walkers, in two unit trees. **Bugs** glow violet; **Predators** glow crimson.

| Tree | Unit | Tier | HP / Armor | Attack |
|---|---|---|---|---|
| Bugs (light) | **Hatchling** | T1 | 130 / 0 | Fast, rapid little acid spits (range 96). Weak but annoying. |
| Bugs (light) | **Malign** | T2 | 480 / 3 | Lobs purple orbs (range ~125) that **split into a ring of 5 smaller orbs**. |
| Predators (beatdown) | **Leacher** | T1 | 600 / 3 | Short-range bites with **lifesteal** (heals 60% of damage dealt). |
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
| **Acid Spire** | 2×2 turret | Range 165; acid orbs burst into 6 smaller orbs. |
| **Chitin Wall** / Large | 1×1 / 2×2 | 440 / 1,760 HP, armor 2, **regrows 1% HP per second**. |
| **Brood Core** | 4×4 | The Alien core: 5,000 HP. Destroy them all to win attack sectors. |

## 5c. Balance notes (the Bolter benchmark)

The Bolter is the only early turret, so everything is tuned against it: 38 DPS with ferrite ammo (14 damage), 64 DPS with ferrosteel ammo (26 damage, pierces), range 130.

| Matchup | Result |
|---|---|
| Bolter vs Hatchling | ~3.4 s per kill. Bolters out-range Hatchlings (130 vs 96), so they get free shots. |
| Bolter vs Malign | Ferrite: ~16 s per kill (armor 3). **Ferrosteel ammo recommended.** |
| Bolter vs Leacher | Ferrite: ~20 s per kill; ferrosteel ~10 s. Walls in front matter (its range is ~50). |
| Bolter vs Ripper | Ferrite barely scratches it (armor 7). **Ferrosteel needed**: ~24 s per Bolter. |
| Spore Pod vs Bolter | The Bolter wins on range (130 vs 110). |
| Acid Spire vs Bolter | The Spire out-ranges it (165). Push it with units or your Pioneer. |

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
11. **Build an Orbital Uplink and get iridium**

---

## 7. Tech tree (Aphelion)

```
Core: Outpost
├─ Ferrite Conveyor → Junction → Router → Bridge
├─ Ferrite Drill → Power Node → Solar Array → Ferrosteel Smelter → Orbital Uplink
│                                                                  ├─ Ice Melter → Volatile Burner
│                                                                  └─ Conductium Fabricator
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
| 5 | The Hive | `the-hive.msav` | 23 | **Destroy all Brood Cores** | All Aliens |

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
| `src/supernova/world/` | Orbital Uplink, Hive, Chitin Wall, editor tweaks, sector names, planet generator, tutorial, Alien wave designs |
| `src/supernova/ai/HaulerAI.java` | Hauler behavior |
| `assets/bundles/bundle.properties` | All in-game text |
| `tools/sprites.py` | Generates every sprite |
| `tools/conveyor.py` | Builds conveyor and bridge frames from the game's layout |
| `docs/DESIGN.md` | Design doc and milestones |
| `./gradlew deploy` | Build and sync to Steam Mindustry |
