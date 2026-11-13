package supernova.world.gen;

import arc.struct.ObjectMap;
import arc.struct.ObjectSet;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.blocks.storage.CoreBlock;
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
        super.generate();
        SnAliens.convertWaves(Vars.state.rules.spawns);
        convertTerrain();
        replaceStartingCore();
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
                if (center.build != null) {
                    center.build.items.clear();
                    if (sector != null && sector == sector.planet.getStartSector()) {
                        // Landing Site: a lean start, like Ground Zero. The tutorial begins with mining by hand.
                        center.build.items.add(SnItems.ferrite, 80);
                    } else {
                        center.build.items.add(SnItems.ferrite, 300);
                        center.build.items.add(SnItems.cobaltine, 150);
                        center.build.items.add(SnItems.quartzite, 100);
                    }
                }
                return;
            }
        }
    }
}
