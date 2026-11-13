package supernova.content;

import arc.graphics.Color;
import mindustry.ctype.UnlockableContent;
import mindustry.graphics.g3d.HexMesh;
import mindustry.graphics.g3d.HexSkyMesh;
import mindustry.graphics.g3d.MultiMesh;
import mindustry.graphics.g3d.SunMesh;
import mindustry.type.Planet;
import supernova.world.gen.AphelionPlanetGenerator;

public final class SnPlanets {
    /** Aphelion's dying star, at the center of the Prime System (separate from Serpulo's). */
    public static Planet prime;
    /** The Supernova campaign planet. */
    public static Planet aphelion;

    private SnPlanets() {}

    public static void load() {
        prime = new Planet("prime", null, 4.5f) {{
            bloom = true;
            accessible = false;
            meshLoader = () -> new SunMesh(this, 4, 5, 0.3, 1.7, 1.2, 1, 1.1f,
                Color.valueOf("ff6a4d"), Color.valueOf("ff8a5c"), Color.valueOf("ffb26b"),
                Color.valueOf("ffd08a"), Color.valueOf("ff9a6b"), Color.valueOf("e8553d"));
        }};

        aphelion = new Planet("aphelion", prime, 1f, 3) {{
            generator = new AphelionPlanetGenerator();
            meshLoader = () -> new HexMesh(this, 6);
            cloudMeshLoader = () -> new MultiMesh(
                new HexSkyMesh(this, 7, 0.12f, 0.13f, 5, Color.valueOf("d8e4f5").a(0.7f), 2, 0.45f, 0.9f, 0.38f),
                new HexSkyMesh(this, 3, 0.4f, 0.15f, 5, Color.valueOf("b9c9e6").a(0.6f), 2, 0.45f, 1f, 0.41f)
            );
            orbitRadius = 22f;
            sectorSeed = 7;
            startSector = 15;
            alwaysUnlocked = true;
            allowWaves = true;
            allowLaunchToNumbered = true;
            allowSectorInvasion = false;
            clearSectorOnLose = true;
            defaultCore = SnBlocks.coreOutpost;
            iconColor = Color.valueOf("8fb4d9");
            atmosphereColor = Color.valueOf("4a6fa5");
            landCloudColor = Color.valueOf("c8d8f0");
            atmosphereRadIn = 0.02f;
            atmosphereRadOut = 0.3f;
            ruleSetter = r -> {
                r.waveTeam = SnTeams.aliens;
                r.placeRangeCheck = false;
            };
            unlockedOnLand.add(SnBlocks.coreOutpost);
        }};

        // Supernova content only shows on Aphelion. Blocks inherit this from their build costs,
        // so vanilla blocks (copper, lead...) stay off Aphelion and ours stay off Serpulo/Erekir.
        for (UnlockableContent c : new UnlockableContent[]{
            SnItems.ferrite, SnItems.cobaltine, SnItems.quartzite, SnItems.ferrosteel, SnItems.iridium,
            SnItems.cryoIce, SnItems.conductium, SnItems.novium, SnItems.singularityComposite,
            SnLiquids.volatiles,
            SnUnits.pioneer, SnUnits.prospector, SnUnits.surveyor, SnUnits.hauler
        }) {
            c.shownPlanets.add(aphelion);
        }
    }
}
