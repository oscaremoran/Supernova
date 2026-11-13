# Supernova

A Mindustry (V8) Java mod. Mine the asteroid belts of **Aphelion**, build a deep resource economy, and hold back the **Aliens**.

## Building

Requires JDK 17. The mod compiles against your installed game's `desktop.jar` so it always matches your exact build (currently 160.7).

```sh
./gradlew modJar    # -> build/libs/Supernova.jar
./gradlew deploy    # build and copy into your Mindustry mods folder
```

If the game isn't at the default macOS Steam path, create `local.properties`:

```
mindustryJar=/path/to/Mindustry/desktop.jar
```

See [docs/DESIGN.md](docs/DESIGN.md) for the design plan.
