# Supernova: Design Document

> Living document. Names and numbers marked **(TBD)** are placeholders to argue about.

## 1. Pitch

**Supernova** adds **Aphelion**, a standalone planet at the edge of a dying star's system. The planet's surface is poor. The real wealth is in the asteroid belt overhead. Players build an economy that reaches into orbit: **orbital mining stations** send drone fleets into the belt, and a dedicated tree of **mining units** works the surface and processes what comes down. Meanwhile the **Aliens**, a hostile faction with a black team color, contest every sector.

**What sets it apart:** large mods usually compete on content volume, such as more turrets and bigger units. Supernova competes on economy depth. Extracting resources is a strategic layer of its own, not a drill placed on an ore patch.

## 2. Pillars

1. **Look up for wealth.** The best resources come only from orbit. Surface ore gets you started, and the asteroid belt gets you to the late game.
2. **Logistics is gameplay.** Mining missions take time, cost propellant, and can fail. Throughput is something you plan, not something you place.
3. **Readable depth.** About 9 items and 1–2 fluids, with clear tiers. Every resource has a reason to exist. No item is used only to craft another single item.
4. **Multiplayer-safe from day one.** All custom state is server-authoritative and serialized.

## 3. Setting

| | |
|---|---|
| Planet | **Aphelion**: a cold, rocky world. Mostly ferrite plains, cobaltine ridges and ice fields. |
| Star | (TBD) an unstable, dying star. Its eventual supernova gives the mod its name and works as a late-campaign story beat. |
| Belt | The Aphelion Belt: zones of asteroids (metallic, icy, rare) reached by orbital missions. |
| Enemy | **The Aliens**: team slot `77`, black palette (`#141414`, rim `#3a3344`). |

## 4. Resource economy

### Tiers

| Tier | Item | Source | Main uses |
|---|---|---|---|
| 1 | **Ferrite** ✅ | Surface ore | Basic construction, walls, conveyors |
| 1 | **Cobaltine** ✅ | Surface ore (harder) | Power, wiring, early electronics |
| 1 | **Quartzite** | Surface sand/rock | Glass, refining flux |
| 2 | **Ferrosteel** | Smelter: ferrite + quartzite | Structural tier 2 and turrets |
| 2 | **Iridium** | **Belt only** (metallic asteroids) | Hard alloys, drill heads, armor |
| 2 | **Cryo-Ice** | **Belt only** (icy asteroids) | Melted into Volatiles |
| 3 | **Conductium** | Fabricator: cobaltine + iridium | Advanced electronics, logic, shields |
| 3 | **Novium** | **Belt only**, rare | Endgame power and weapons |
| 4 | **Singularity Composite** | Ferrosteel + conductium + novium | Top-tier units, cores, superweapons |

**Fluid: Volatiles** (from Cryo-Ice). Used as propellant for orbital missions and as fuel and coolant. This creates the core loop: **the belt pays for itself**. Ice from orbit becomes the propellant for the next launch.

✅ = implemented.

## 5. Asteroid mining

### 5.1 Orbital mining stations

- **Orbital Uplink** (2 tiers, TBD): a large block that consumes Volatiles and power to launch a **mining mission** into a chosen belt zone.
- A mission has a duration, a cargo capacity and a yield table per zone:
  - *Metallic*: iridium-heavy
  - *Icy*: cryo-ice-heavy
  - *Deep*: rare novium, longer and riskier
- Cargo returns as raw items into the uplink's storage. From there it goes out by conveyor or to the core.
- **Upgrades:** extra drone bays (more capacity), guidance arrays (shorter missions), and cargo returned as *asteroid chunks* that must be cracked on the surface (more yield, more logistics).
- **Risk (later):** Alien interception events can lose part of the cargo. Defensive escort modules reduce the loss.
- **Multiplayer:** mission state (zone, progress, seed) lives in the building and is saved with `write`/`read`. Yield rolls use a seeded RNG on the server only.

### 5.2 Mining unit tree

| Tier | Unit | Role |
|---|---|---|
| T1 | **Prospector** (TBD) | Small flying miner for surface ores |
| T2 | **Surveyor** (TBD) | Mines faster and reveals hidden ore deposits nearby |
| T3 | **Hauler** (TBD) | Moves cargo from uplinks and outposts to the core |
| T4 | **Cracker** (TBD) | Breaks asteroid chunks in the field for bonus yield |
| T5 | **Leviathan** (TBD) | Mobile mining mothership that acts as a small mobile core and drone carrier |

## 6. The Aliens

- Their own unit tree: ground, air and naval/burrowing branches (TBD, decided at the faction milestone).
- Visual language: black chitin and armored shells with faint violet bioluminescence. This keeps them readable against dark terrain while staying "black".
- They appear in sector waves, enemy-base sectors, and later as interceptors against orbital missions.

## 7. Campaign structure (draft)

1. **Landing Site**: ferrite and cobaltine only. Teaches the basics.
2. **Ice Fields**: quartzite and ferrosteel. First Alien waves.
3. **Uplink Ridge**: unlocks the first Orbital Uplink and the icy belt zone.
4. Mid-game sectors: iridium, then conductium, then the first Alien base assault.
5. Deep belt access: novium.
6. Endgame: Singularity Composite, and the star's collapse.

## 8. Technical notes

- **Target:** Mindustry V8 build 160.7. Compiled against the installed `desktop.jar` (see README).
- **Package layout:** `supernova.content.*` for registries (`SnItems`, `SnTeams`, `SnBlocks`, `SnUnits`, `SnPlanets`, `SnTechTree`) and `supernova.world.*` for custom block and building classes.
- **Naming:** content names are short (`ferrite`). The game prefixes them to `supernova-ferrite`, and sprites follow the same rule.
- **Network rule:** never change a released team ID, content name or `write`/`read` layout without a revision bump.
- **Art:** made by Claude with feedback from the user. Item icons are 32×32, and blocks use 32 px per tile. Silhouettes come first, then a 3-tone shading ramp per material.

## 9. Milestones

| # | Milestone | Status |
|---|---|---|
| M0 | Gradle skeleton, mod loads, Aliens team, 2 items | ✅ built, awaiting in-game test |
| M1 | Remaining 7 items + Volatiles, ores, drills, smelter and fabricator | |
| M2 | Orbital Uplink and the mission system (multiplayer-safe) | |
| M3 | Mining units T1–T3 | |
| M4 | Planet Aphelion: generator, first 3 sectors, tech tree | |
| M5 | Aliens unit tree and wave AI | |
| M6 | Mining units T4–T5, deep belt, endgame tier | |
