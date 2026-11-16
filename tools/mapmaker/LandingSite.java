import mindustry.content.Blocks;
import mindustry.game.Team;
import supernova.content.SnBlocks;

/**
 * Landing Site (150x150): the tutorial sector.
 * Core in the southwest beside ferrite; one winding path from the Alien spawn (northeast)
 * with a single chokepoint halfway; side pockets for cobaltine and quartzite; a frozen lake.
 */
public class LandingSite extends MapMaker {
    static void build() throws Exception {
        begin(150, 150, 15);
        baseFloors(15);
        iceField(16, 82, 12);
        iceField(104, 58, 10);
        iceField(110, 132, 9);

        int[] core = {26, 28}, spawn = {126, 126}, choke = {80, 74};

        // Open areas
        clearing(core[0], core[1], 15, 2.5f);                         // home
        path(new int[][]{{30, 30}, {55, 38}, {72, 52}, choke, {76, 96}, {98, 112}, {124, 124}},
            new float[]{14, 12, 12, 7, 12, 12, 14}, 2f);               // main Alien path
        clearing(spawn[0], spawn[1], 10, 2f);                          // spawn
        path(new int[][]{{64, 46}, {56, 58}}, new float[]{8, 8}, 1.5f); // cobaltine branch
        clearing(54, 62, 8, 2f);
        path(new int[][]{{22, 40}, {20, 64}}, new float[]{9, 9}, 1.5f); // quartzite branch
        clearing(22, 72, 10, 2f);
        path(new int[][]{{76, 92}, {54, 92}}, new float[]{8, 8}, 1.5f); // expansion plateau
        clearing(46, 92, 12, 2.5f);
        clearing(70, 22, 12, 2f);                                      // lakeside
        path(new int[][]{{40, 24}, {60, 22}}, new float[]{8, 8}, 1.5f);
        lake(72, 20, 6);

        walls();

        // Ores
        ore(SnBlocks.oreFerrite, 40, 22, 3.5f);
        ore(SnBlocks.oreFerrite, 18, 40, 3f);
        ore(SnBlocks.oreFerrite, 34, 40, 2.5f);
        ore(SnBlocks.oreCobaltine, 54, 63, 3.5f);
        ore(SnBlocks.oreQuartzite, 22, 75, 3.5f);
        ore(SnBlocks.oreQuartzite, 29, 68, 2f);
        ore(SnBlocks.oreFerrite, 42, 92, 3f);
        ore(SnBlocks.oreCobaltine, 50, 99, 2.5f);
        ore(SnBlocks.oreQuartzite, 62, 26, 2.5f);

        boulders(0.012f, new int[][]{core, spawn, choke}, 7);
        clearBlocks(core[0], core[1], 4);
        clearBlocks(spawn[0], spawn[1], 4);

        // Core and Alien spawn
        place(SnBlocks.coreOutpost, core[0], core[1], Team.sharded);
        tiles.get(spawn[0], spawn[1]).setOverlay(Blocks.spawn);

        // Sanity checks
        System.out.println("Landing Site checks:");
        System.out.println("  spawn -> core path: " + pathLength(spawn[0], spawn[1], core[0], core[1]) + " tiles");
        System.out.println("  core -> ferrite:    " + pathLength(core[0], core[1], 40, 22));
        System.out.println("  core -> cobaltine:  " + pathLength(core[0], core[1], 54, 63));
        System.out.println("  core -> quartzite:  " + pathLength(core[0], core[1], 22, 75));

        save("landing-site", "Landing Site",
            "Your first foothold on Aphelion. Learn to mine, build and defend against the first Hatchlings.");
    }
}
