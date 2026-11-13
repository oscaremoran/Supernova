package supernova.content;

import arc.func.Prov;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.Rules;
import mindustry.game.SpawnGroup;
import mindustry.type.ItemStack;
import mindustry.type.SectorPreset;
import supernova.world.gen.AlienWaves;

/**
 * Aphelion's hand-made story sectors.
 *
 * <p>Each one is only created if its map file exists at {@code assets/maps/<file>.msav}; until then
 * that sector stays procedural. To add a map: drop the .msav in assets/maps with the file name below.
 *
 * <p>Sector IDs form a chain of neighbours on Aphelion's globe: 15 -> 170 -> 64 -> 218 -> 23.
 */
public final class SnSectors {
    public static SectorPreset landingSite, frostRidge, uplinkCrater, iceShelf, theHive;

    private SnSectors() {}

    public static void load() {
        landingSite = preset("landing-site", 15, 15, 0.1f, AlienWaves::landingSite, false,
            ItemStack.list(SnItems.ferrite, 80));
        if (landingSite != null) {
            landingSite.alwaysUnlocked = true;
            landingSite.startWaveTimeMultiplier = 3f; // time to learn the basics before the first wave
        }

        frostRidge = preset("frost-ridge", 170, 20, 0.3f, AlienWaves::frostRidge, false,
            ItemStack.list(SnItems.ferrite, 300, SnItems.cobaltine, 150, SnItems.quartzite, 100));
        uplinkCrater = preset("uplink-crater", 64, 25, 0.45f, AlienWaves::uplinkCrater, false,
            ItemStack.list(SnItems.ferrite, 500, SnItems.cobaltine, 300, SnItems.quartzite, 200, SnItems.ferrosteel, 100));
        iceShelf = preset("ice-shelf", 218, 30, 0.6f, AlienWaves::iceShelf, false,
            ItemStack.list(SnItems.ferrite, 800, SnItems.cobaltine, 500, SnItems.quartzite, 300, SnItems.ferrosteel, 200));
        theHive = preset("the-hive", 23, 0, 0.8f, AlienWaves::theHive, true,
            ItemStack.list(SnItems.ferrite, 1200, SnItems.cobaltine, 800, SnItems.quartzite, 400, SnItems.ferrosteel, 400, SnItems.conductium, 100));
        if (theHive != null) theHive.isLastSector = true;
    }

    /** All story sectors whose maps exist, in campaign order. */
    public static Seq<SectorPreset> available() {
        Seq<SectorPreset> out = new Seq<>();
        for (SectorPreset p : new SectorPreset[]{landingSite, frostRidge, uplinkCrater, iceShelf, theHive}) {
            if (p != null) out.add(p);
        }
        return out;
    }

    private static SectorPreset preset(String file, int sector, int captureWave, float difficulty,
                                       Prov<Seq<SpawnGroup>> waves, boolean attack, Seq<ItemStack> loadout) {
        if (!Vars.tree.get("maps/" + file + ".msav").exists()) return null;
        try {
            SectorPreset p = new SectorPreset(file, SnPlanets.aphelion, sector);
            p.captureWave = captureWave;
            p.difficulty = difficulty;
            p.addStartingItems = true;
            p.rules = r -> applyRules(r, waves, attack, loadout);
            return p;
        } catch (Throwable t) {
            Log.err("[Supernova] Could not load story sector map '" + file + "'; it will stay procedural.", t);
            return null;
        }
    }

    private static void applyRules(Rules r, Prov<Seq<SpawnGroup>> waves, boolean attack, Seq<ItemStack> loadout) {
        r.waveTeam = SnTeams.aliens;
        r.loadout = loadout.copy();
        r.attackMode = attack;
        r.waves = true;
        // Keep waves the map designer made from Aliens; otherwise use this sector's designed set.
        if (r.spawns == null || r.spawns.isEmpty() || SnAliens.needsConversion(r.spawns)) {
            r.spawns = waves.get();
        }
    }
}
