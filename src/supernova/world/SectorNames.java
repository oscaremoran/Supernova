package supernova.world;

import mindustry.type.Planet;
import mindustry.type.Sector;

/**
 * Gives Aphelion's procedural sectors names instead of bare numbers.
 * Names are derived from the sector ID, so every player sees the same name for the same sector.
 * A name the player chose (or one already saved) is never overwritten.
 */
public final class SectorNames {
    private static final String[] PREFIX = {
        "Frost", "Rust", "Basalt", "Pale", "Cobalt", "Shard", "Hollow", "Glacier",
        "Iron", "Silent", "Dusk", "Ember", "Grey", "Shattered", "Cinder", "Rime",
        "Quartz", "Drift", "Cold", "Fallen", "Ashen", "Lonely", "Broken", "Frozen"
    };
    private static final String[] SUFFIX = {
        "Reach", "Fields", "Ridge", "Basin", "Expanse", "Crater", "Flats", "Shelf",
        "Rift", "Hollow", "Plateau", "Gulch", "Steppe", "Scar", "Wastes", "Terrace"
    };

    private SectorNames() {}

    public static void apply(Planet planet) {
        if (planet == null || planet.sectors.isEmpty()) return;
        Sector start = planet.getStartSector();
        for (Sector sector : planet.sectors) {
            if (sector.preset != null || sector.info == null || sector.info.name != null) continue;
            sector.info.name = sector == start ? "Landing Site" : generate(sector.id);
        }
    }

    private static int[] order;

    /** Unique name for each ID until all prefix/suffix pairs are used, then a numeral is appended. */
    public static String generate(int id) {
        int total = PREFIX.length * SUFFIX.length;
        if (order == null) {
            // Fixed-seed shuffle: same order on every machine, no clustering of similar names.
            order = new int[total];
            for (int i = 0; i < total; i++) order[i] = i;
            java.util.Random rand = new java.util.Random(0x5EC7A11L);
            for (int i = total - 1; i > 0; i--) {
                int j = rand.nextInt(i + 1);
                int t = order[i]; order[i] = order[j]; order[j] = t;
            }
        }
        int combo = order[id % total];
        String name = PREFIX[combo / SUFFIX.length] + " " + SUFFIX[combo % SUFFIX.length];
        return id >= total ? name + " " + (id / total + 1) : name;
    }
}
