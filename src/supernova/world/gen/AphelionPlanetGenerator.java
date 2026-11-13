package supernova.world.gen;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.type.ItemStack;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.storage.CoreBlock;
import supernova.content.SnAlienBlocks;
import supernova.content.SnAliens;
import supernova.content.SnBlocks;
import supernova.content.SnItems;
import supernova.content.SnTeams;

import java.lang.reflect.Field;

/**
 * Terrain for Aphelion: a cold, rocky world of stone plains, basalt and ice fields.
 *
 * <p>Builds on the vanilla Serpulo generator (rooms, paths, enemy bases, spawns), then
 * converts its terrain to Aphelion's: warm floors become cold ones, shallow water freezes,
 * vegetation turns to rock, and vanilla ores become Aphelion ores. The player's starting core
 * is replaced with the Core: Outpost.
 */
public class AphelionPlanetGenerator extends SerpuloPlanetGenerator {
    private static final ObjectMap<Block, Block> floorMap = new ObjectMap<>();
    private static final ObjectMap<Block, Block> oreMap = new ObjectMap<>();
    private static final ObjectSet<Block> allowedWalls = new ObjectSet<>();
    private static final ObjectSet<Block> allowedProps = new ObjectSet<>();
    /** Floors that were sand before conversion get scattered quartzite. */
    private static final ObjectSet<Block> quartziteFloors = new ObjectSet<>();

    public AphelionPlanetGenerator() {
        if (floorMap.isEmpty()) initMaps();
        remapTerrainTable();
    }

    private static void initMaps() {
        floorMap.putAll(
            Blocks.sand, Blocks.dacite,
            Blocks.darksand, Blocks.basalt,
            Blocks.grass, Blocks.snow,
            Blocks.moss, Blocks.stone,
            Blocks.sporeMoss, Blocks.shale,
            Blocks.dirt, Blocks.stone,
            Blocks.mud, Blocks.basalt,
            Blocks.salt, Blocks.ice,
            Blocks.tar, Blocks.shale,
            Blocks.hotrock, Blocks.basalt,
            Blocks.magmarock, Blocks.basalt,
            Blocks.water, Blocks.ice,
            Blocks.sandWater, Blocks.ice,
            Blocks.darksandWater, Blocks.ice,
            Blocks.taintedWater, Blocks.ice,
            Blocks.darksandTaintedWater, Blocks.ice,
            Blocks.deepTaintedWater, Blocks.deepwater
        );
        quartziteFloors.addAll(Blocks.sand, Blocks.darksand, Blocks.salt);

        oreMap.putAll(
            Blocks.oreCopper, SnBlocks.oreFerrite,
            Blocks.oreLead, SnBlocks.oreCobaltine,
            Blocks.oreCoal, SnBlocks.oreQuartzite,
            Blocks.oreTitanium, SnBlocks.oreQuartzite,
            Blocks.oreScrap, SnBlocks.oreQuartzite,
            Blocks.oreThorium, SnBlocks.oreCobaltine
        );

        allowedWalls.addAll(Blocks.stoneWall, Blocks.daciteWall, Blocks.iceWall, Blocks.snowWall, Blocks.duneWall, Blocks.shaleWall);
        allowedProps.addAll(Blocks.boulder, Blocks.snowBoulder, Blocks.daciteBoulder, Blocks.basaltBoulder);
    }

    /** Swaps floors in the parent's height/temperature table so the planet globe shows Aphelion colors. */
    private void remapTerrainTable() {
        try {
            Field f = SerpuloPlanetGenerator.class.getDeclaredField("arr");
            f.setAccessible(true);
            Block[][] arr = (Block[][]) f.get(this);
            Block[][] copy = new Block[arr.length][];
            for (int i = 0; i < arr.length; i++) {
                copy[i] = arr[i].clone();
                for (int j = 0; j < copy[i].length; j++) copy[i][j] = floorMap.get(copy[i][j], copy[i][j]);
            }
            f.set(this, copy);
        } catch (Exception e) {
            Log.err("[Supernova] Could not remap Aphelion terrain table; the globe will use Serpulo colors.", e);
        }
    }

    @Override
    protected void generate() {
        Vars.state.rules.waveTeam = SnTeams.aliens;
        // The game fills the core from rules.loadout on first landing (the vanilla default is 100 copper).
        boolean start = sector != null && sector == sector.planet.getStartSector();
        Vars.state.rules.loadout = start
            // Landing Site: a lean start, like Ground Zero. The tutorial begins with mining by hand.
            ? ItemStack.list(SnItems.ferrite, 80)
            : ItemStack.list(SnItems.ferrite, 300, SnItems.cobaltine, 150, SnItems.quartzite, 100);
        super.generate();
        SnAliens.convertWaves(Vars.state.rules.spawns);
        convertTerrain();
        replaceStartingCore();
        convertEnemyBases();
    }

    private void convertTerrain() {
        for (Tile tile : tiles) {
            Floor original = tile.floor();
            Block floor = floorMap.get(original, original);
            if (floor != original) tile.setFloor(floor.asFloor());

            Block overlay = tile.overlay();
            if (overlay != Blocks.air && overlay != Blocks.spawn) {
                Block ore = oreMap.get(overlay);
                tile.setOverlay(ore != null ? ore : Blocks.air);
            } else if (overlay == Blocks.air && quartziteFloors.contains(original) && !tile.solid()
                && noise(tile.x + 300, tile.y, 2, 0.5, 14, 1) > 0.66f) {
                tile.setOverlay(SnBlocks.oreQuartzite);
            }

            if (tile.build != null) continue;
            Block block = tile.block();
            if (block == Blocks.air) continue;
            if (block instanceof StaticWall) {
                if (!allowedWalls.contains(block)) {
                    Block wall = tile.floor().wall;
                    tile.setBlock(allowedWalls.contains(wall) ? wall : Blocks.stoneWall);
                }
            } else if (!block.synthetic() && !allowedProps.contains(block)) {
                tile.setBlock(tile.floor() == Blocks.snow || tile.floor() == Blocks.ice ? Blocks.snowBoulder : Blocks.boulder);
            }
        }
    }

    /** The vanilla launch loadout places a Shard core; swap it for Aphelion's Core: Outpost. */
    private void replaceStartingCore() {
        Team team = Vars.state.rules.defaultTeam;
        for (Tile tile : tiles) {
            if (tile.build instanceof CoreBlock.CoreBuild && tile.build.team == team && tile.block() != SnBlocks.coreOutpost && tile.isCenter()) {
                Tile center = tile.build.tile;
                center.setBlock(SnBlocks.coreOutpost, team);
                return;
            }
        }
    }

    /**
     * Enemy bases come from the vanilla base generator. Swap their buildings for Alien structures:
     * cores become Core: Brood blocks (each guarded by a Hive), turrets become Spore Pods or Acid Spires,
     * walls become Chitin Walls, and everything else (conveyors, drills, factories) is removed.
     */
    private void convertEnemyBases() {
        Team aliens = SnTeams.aliens;
        arc.struct.Seq<mindustry.gen.Building> builds = new arc.struct.Seq<>();
        arc.struct.ObjectSet<mindustry.gen.Building> seen = new arc.struct.ObjectSet<>();
        for (Tile tile : tiles) {
            if (tile.build != null && tile.build.team == aliens && seen.add(tile.build)) builds.add(tile.build);
        }
        arc.struct.Seq<Tile> cores = new arc.struct.Seq<>();
        for (mindustry.gen.Building b : builds) {
            Block block = b.block;
            if (block.name.startsWith("supernova-")) continue;
            Tile center = b.tile;
            if (block instanceof CoreBlock) {
                center.setBlock(SnAlienBlocks.broodCore, aliens);
                cores.add(center);
            } else if (block instanceof mindustry.world.blocks.defense.turrets.Turret) {
                center.setAir();
                center.setBlock(block.size <= 1 ? SnAlienBlocks.sporePod : SnAlienBlocks.acidSpire, aliens);
            } else if (block instanceof mindustry.world.blocks.defense.Wall) {
                center.setAir();
                if (block.size == 1) center.setBlock(SnAlienBlocks.chitinWall, aliens);
                else if (block.size == 2) center.setBlock(SnAlienBlocks.chitinWallLarge, aliens);
            } else {
                center.setAir();
            }
        }
        // One Hive near each Core: Brood, wherever there's room.
        for (Tile core : cores) {
            for (int attempt = 0; attempt < 24; attempt++) {
                float angle = attempt * 15f;
                int hx = core.x + Math.round(arc.math.Mathf.cosDeg(angle) * 7f);
                int hy = core.y + Math.round(arc.math.Mathf.sinDeg(angle) * 7f);
                if (fits(SnAlienBlocks.hive, hx, hy)) {
                    tiles.getn(hx, hy).setBlock(SnAlienBlocks.hive, aliens);
                    break;
                }
            }
        }
    }

    private boolean fits(Block block, int cx, int cy) {
        int offset = (block.size - 1) / 2;
        for (int dx = 0; dx < block.size; dx++) {
            for (int dy = 0; dy < block.size; dy++) {
                Tile t = tiles.get(cx - offset + dx, cy - offset + dy);
                if (t == null || t.block() != Blocks.air || t.floor().isDeep() || t.floor().solid) return false;
            }
        }
        return true;
    }
}
