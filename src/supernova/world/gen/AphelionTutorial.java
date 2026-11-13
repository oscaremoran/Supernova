package supernova.world.gen;

import arc.Core;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.game.MapObjectives.*;
import mindustry.game.Rules;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;

import static supernova.content.SnBlocks.*;
import static supernova.content.SnItems.*;
import static supernova.content.SnUnits.*;

/**
 * Ground Zero-style tutorial for Aphelion's Landing Site.
 *
 * <p>Short steps that unlock one after another, with markers drawn on the map (labels on the ore,
 * the core and the Alien spawn) that show while their step is active. Marker positions are found
 * from the actual sector: the nearest ore of each kind to the player's core, and the first enemy spawn.
 */
public final class AphelionTutorial {
    /** Bump when the tutorial changes, so existing Landing Site saves get the new version. */
    public static final int VERSION = 2;
    public static final String TAG = "supernova-tutorial";

    private static final Color ORE = Color.valueOf("e0b04a");
    private static final Color DANGER = Color.valueOf("ff5c5c");
    private static final Color INFO = Color.valueOf("8fb0f0");

    private AphelionTutorial() {}

    /** True if this save has no tutorial, or an older version of it. */
    public static boolean outdated(Rules rules) {
        return rules.tags.getInt(TAG, 0) < VERSION;
    }

    public static void apply(Rules rules) {
        rules.objectives.all.clear();
        rules.tags.put(TAG, String.valueOf(VERSION));

        Building core = Vars.state.rules.defaultTeam.core();
        if (core == null) return;
        Tile coreTile = core.tile;
        Tile ferriteOre = nearestOre(oreFerrite, coreTile);
        Tile cobaltineOre = nearestOre(oreCobaltine, coreTile);
        Tile quartziteOre = nearestOre(oreQuartzite, coreTile);
        Tile spawn = Vars.spawner.getFirstSpawn();

        MapObjective mine = step(new ItemObjective(ferrite, 120), "mine",
            label(ferriteOre, text("marker.ore-ferrite"), ORE));
        MapObjective drills = step(new BuildCountObjective(ferriteDrill, 4), "drills",
            label(ferriteOre, text("marker.drills"), ORE)).parent(mine);
        MapObjective conveyors = step(new CoreItemObjective(ferrite, 400), "conveyors",
            label(coreTile, text("marker.core"), INFO)).parent(drills);
        MapObjective mineCobaltine = step(new ItemObjective(cobaltine, 150), "cobaltine",
            label(cobaltineOre, text("marker.ore-cobaltine"), ORE)).parent(conveyors);
        MapObjective research = step(new ResearchObjective(bolter), "research").parent(mineCobaltine);
        MapObjective defend = step(new BuildCountObjective(bolter, 2), "defend",
            label(spawn, text("marker.spawn"), DANGER)).parent(research);
        MapObjective mineQuartzite = step(new ItemObjective(quartzite, 150), "quartzite",
            label(quartziteOre, text("marker.ore-quartzite"), ORE)).parent(defend);
        MapObjective power = step(new BuildCountObjective(solarArray, 2), "power").parent(mineQuartzite);
        MapObjective smelt = step(new CoreItemObjective(ferrosteel, 50), "smelt").parent(power);
        MapObjective drones = step(new UnitCountObjective(prospector, 2), "drones").parent(smelt);
        MapObjective uplink = step(new CoreItemObjective(iridium, 30), "uplink",
            label(coreTile, text("marker.uplink"), INFO)).parent(drones);

        rules.objectives.add(mine, drills, conveyors, mineCobaltine, research, defend, mineQuartzite, power, smelt, drones, uplink);
    }

    private static MapObjective step(MapObjective objective, String key, ObjectiveMarker... markers) {
        objective.details(text("tutorial.supernova." + key));
        int n = 0;
        for (ObjectiveMarker m : markers) if (m != null) n++;
        if (n > 0) {
            ObjectiveMarker[] list = new ObjectiveMarker[n];
            int i = 0;
            for (ObjectiveMarker m : markers) if (m != null) list[i++] = m;
            objective.markers(list);
        }
        return objective;
    }

    /** A circled label on the map, shown while its step is active. Null if there's no target. */
    private static ObjectiveMarker label(Tile tile, String text, Color color) {
        if (tile == null) return null;
        ShapeTextMarker m = new ShapeTextMarker(text, tile.worldx(), tile.worldy());
        m.radius = 12f;
        m.textHeight = 14f;
        m.fontSize = 1.1f;
        m.color = color;
        return m;
    }

    private static Tile nearestOre(Block ore, Tile from) {
        Tile best = null;
        float bestDst = Float.MAX_VALUE;
        for (Tile t : Vars.world.tiles) {
            if (t.overlay() != ore || t.solid() || t.build != null) continue;
            float d = t.dst2(from);
            if (d < bestDst) {
                bestDst = d;
                best = t;
            }
        }
        return best;
    }

    private static String text(String key) {
        return Core.bundle == null ? key : Core.bundle.get(key, key);
    }
}
