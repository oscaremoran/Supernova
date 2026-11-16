import arc.*;
import arc.files.Fi;
import arc.mock.MockSettings;
import arc.struct.*;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.core.*;
import mindustry.game.Team;
import mindustry.io.MapIO;
import mindustry.maps.Map;
import mindustry.net.Net;
import mindustry.world.*;
import mindustry.world.blocks.environment.Floor;
import supernova.Supernova;
import supernova.content.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayDeque;
import java.util.Random;

/**
 * Builds Supernova's story-sector maps in code and saves them as real .msav files.
 *
 * Usage (repo root, after ./gradlew modJar):
 *   javac -cp "$GAME:build/classes/java/main" -d build/mapmaker tools/mapmaker/MapMaker.java
 *   java  -cp "$GAME:build/classes/java/main:build/mapmaker" MapMaker [mapName...]
 * Writes assets/maps/<name>.msav and a preview image to build/map-previews/<name>.png.
 */
public class MapMaker {
    static Tiles tiles;
    static int W, H;
    static Random rand;
    /** Open (walkable) mask: everything not opened becomes wall. */
    static boolean[][] open;

    public static void main(String[] args) throws Exception {
        boot();
        if (args.length > 0 && args[0].equals("verify")) {
            System.out.println("Story sectors found: " + SnSectors.available().map(p -> p.name + " @ sector " + p.sector.id + " (capture wave " + p.captureWave + ")"));
            return;
        }
        Seq<String> which = args.length == 0 ? Seq.with("landing-site") : Seq.with(args);
        for (String name : which) {
            switch (name) {
                case "landing-site" -> LandingSite.build();
                default -> throw new IllegalArgumentException("Unknown map: " + name);
            }
        }
    }

    // ================================================================ boot

    static void boot() {
        Core.app = new Application() {
            final Seq<ApplicationListener> l = new Seq<>();
            public Seq<ApplicationListener> getListeners() { return l; }
            public ApplicationType getType() { return ApplicationType.headless; }
            public String getClipboardText() { return ""; }
            public void setClipboardText(String t) {}
            public void post(Runnable r) { r.run(); }
            public void exit() {}
        };
        Core.settings = new MockSettings();
        Core.files = new arc.backend.sdl.SdlFiles();
        Core.graphics = new arc.mock.MockGraphics();
        Core.audio = new arc.mock.MockAudio();
        Vars.headless = true;
        Vars.net = new Net(null);
        Vars.tree = new FileTree();
        Vars.init();
        Vars.content.createBaseContent();
        mindustry.mod.Mods.ModMeta meta = new mindustry.mod.Mods.ModMeta();
        meta.name = "supernova";
        Supernova main = new Supernova();
        Vars.content.setCurrentMod(new mindustry.mod.Mods.LoadedMod(new Fi("."), new Fi("."), main, MapMaker.class.getClassLoader(), meta));
        main.loadContent();
        Vars.content.setCurrentMod(null);
        Vars.content.init();
        Vars.logic = new Logic();
        Vars.world = new World();
        Vars.state = new GameState();
    }

    // ================================================================ canvas

    static void begin(int w, int h, long seed) {
        W = w; H = h;
        rand = new Random(seed);
        Vars.world.beginMapLoad();
        tiles = Vars.world.resize(w, h);
        open = new boolean[w][h];
        for (int x = 0; x < w; x++) for (int y = 0; y < h; y++) tiles.set(x, y, new Tile(x, y, Blocks.stone, Blocks.air, Blocks.air));
    }

    static boolean in(int x, int y) { return x >= 0 && y >= 0 && x < W && y < H; }

    /** Smooth value noise in [0,1], deterministic per seed. */
    static float noise(int seed, float x, float y, float scale) {
        x /= scale; y /= scale;
        int x0 = (int) Math.floor(x), y0 = (int) Math.floor(y);
        float fx = x - x0, fy = y - y0;
        fx = fx * fx * (3 - 2 * fx); fy = fy * fy * (3 - 2 * fy);
        float a = hash(seed, x0, y0), b = hash(seed, x0 + 1, y0), c = hash(seed, x0, y0 + 1), d = hash(seed, x0 + 1, y0 + 1);
        return (a + (b - a) * fx) + ((c + (d - c) * fx) - (a + (b - a) * fx)) * fy;
    }

    static float hash(int seed, int x, int y) {
        long h = seed * 374761393L + x * 668265263L + y * 2147483647L;
        h = (h ^ (h >>> 13)) * 1274126177L;
        return ((h ^ (h >>> 16)) & 0xffffff) / (float) 0xffffff;
    }

    /** Opens a disc with a noisy edge (radius jitter = rough). */
    static void clearing(int cx, int cy, float r, float rough) {
        for (int x = (int) (cx - r - rough - 2); x <= cx + r + rough + 2; x++)
            for (int y = (int) (cy - r - rough - 2); y <= cy + r + rough + 2; y++) {
                if (!in(x, y)) continue;
                float edge = r + (noise(7, x, y, 6f) - 0.5f) * 2f * rough;
                if (Math.hypot(x - cx, y - cy) <= edge) open[x][y] = true;
            }
    }

    /** Opens a winding corridor through waypoints, with a width per segment. */
    static void path(int[][] pts, float[] widths, float rough) {
        for (int i = 0; i < pts.length - 1; i++) {
            float w0 = widths[Math.min(i, widths.length - 1)], w1 = widths[Math.min(i + 1, widths.length - 1)];
            int steps = (int) Math.hypot(pts[i + 1][0] - pts[i][0], pts[i + 1][1] - pts[i][1]);
            for (int s = 0; s <= steps; s++) {
                float t = s / (float) Math.max(1, steps);
                float x = pts[i][0] + (pts[i + 1][0] - pts[i][0]) * t;
                float y = pts[i][1] + (pts[i + 1][1] - pts[i][1]) * t;
                clearing(Math.round(x), Math.round(y), (w0 + (w1 - w0) * t) / 2f, rough);
            }
        }
    }

    /** Converts the open mask into terrain: closed tiles become walls matching their floor. */
    static void walls() {
        for (int x = 0; x < W; x++) for (int y = 0; y < H; y++) {
            Tile t = tiles.get(x, y);
            if (open[x][y]) continue;
            Floor f = t.floor();
            t.setBlock(f == Blocks.ice || f == Blocks.snow || f == Blocks.iceSnow ? Blocks.iceWall
                : f == Blocks.dacite ? Blocks.daciteWall : Blocks.stoneWall);
        }
    }

    /** Base floor: stone with basalt and dacite patches. */
    static void baseFloors(int seed) {
        for (int x = 0; x < W; x++) for (int y = 0; y < H; y++) {
            float n = noise(seed, x, y, 14f), m = noise(seed + 1, x, y, 9f);
            Block f = n > 0.72f ? Blocks.basalt : m > 0.74f ? Blocks.dacite : Blocks.stone;
            tiles.get(x, y).setFloor(f.asFloor());
        }
    }

    /** An ice/snow field (blob). */
    static void iceField(int cx, int cy, float r) {
        for (int x = (int) (cx - r - 3); x <= cx + r + 3; x++)
            for (int y = (int) (cy - r - 3); y <= cy + r + 3; y++) {
                if (!in(x, y)) continue;
                float d = (float) Math.hypot(x - cx, y - cy) + (noise(11, x, y, 5f) - 0.5f) * 6f;
                if (d <= r) tiles.get(x, y).setFloor((d < r * 0.55f ? Blocks.ice : noise(12, x, y, 3f) > 0.5f ? Blocks.snow : Blocks.iceSnow).asFloor());
            }
    }

    /** Frozen lake: deep water centre, ice ring. Also opens it (it's flat). */
    static void lake(int cx, int cy, float r) {
        iceField(cx, cy, r + 4);
        for (int x = (int) (cx - r - 1); x <= cx + r + 1; x++)
            for (int y = (int) (cy - r - 1); y <= cy + r + 1; y++)
                if (in(x, y) && Math.hypot(x - cx, y - cy) + (noise(13, x, y, 4f) - 0.5f) * 3f <= r) tiles.get(x, y).setFloor(Blocks.deepwater.asFloor());
    }

    /** Ore blob on open, non-liquid tiles. */
    static void ore(Block ore, int cx, int cy, float r) {
        for (int x = (int) (cx - r - 2); x <= cx + r + 2; x++)
            for (int y = (int) (cy - r - 2); y <= cy + r + 2; y++) {
                if (!in(x, y) || !open[x][y]) continue;
                Tile t = tiles.get(x, y);
                if (t.floor().isLiquid) continue;
                if (Math.hypot(x - cx, y - cy) + (noise(21, x, y, 2.5f) - 0.5f) * 2.5f <= r) t.setOverlay(ore);
            }
    }

    /** Scatter boulders on open tiles (not on ore, not near the given keep-clear points). */
    static void boulders(float density, int[][] keepClear, float clearRadius) {
        for (int x = 0; x < W; x++) for (int y = 0; y < H; y++) {
            Tile t = tiles.get(x, y);
            if (!open[x][y] || t.overlay() != Blocks.air || t.floor().isLiquid || rand.nextFloat() > density) continue;
            boolean near = false;
            for (int[] k : keepClear) if (Math.hypot(x - k[0], y - k[1]) < clearRadius) near = true;
            if (near) continue;
            Floor f = t.floor();
            t.setBlock(f == Blocks.snow || f == Blocks.ice || f == Blocks.iceSnow ? Blocks.snowBoulder : f == Blocks.dacite ? Blocks.daciteBoulder : Blocks.boulder);
        }
    }

    /** Clears blocks in a radius (e.g. around the core and spawn). */
    static void clearBlocks(int cx, int cy, float r) {
        for (int x = (int) (cx - r); x <= cx + r; x++) for (int y = (int) (cy - r); y <= cy + r; y++)
            if (in(x, y) && Math.hypot(x - cx, y - cy) <= r) { open[x][y] = true; tiles.get(x, y).setBlock(Blocks.air); }
    }

    // ================================================================ checks + output

    /** Ground path length (tiles) between two points, or -1 if unreachable. */
    static int pathLength(int sx, int sy, int tx, int ty) {
        int[][] dist = new int[W][H];
        for (int[] row : dist) java.util.Arrays.fill(row, -1);
        ArrayDeque<int[]> q = new ArrayDeque<>();
        q.add(new int[]{sx, sy});
        dist[sx][sy] = 0;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!q.isEmpty()) {
            int[] p = q.poll();
            if (Math.abs(p[0] - tx) <= 2 && Math.abs(p[1] - ty) <= 2) return dist[p[0]][p[1]];
            for (int[] d : dirs) {
                int nx = p[0] + d[0], ny = p[1] + d[1];
                if (!in(nx, ny) || dist[nx][ny] >= 0) continue;
                Tile t = tiles.get(nx, ny);
                boolean blocked = (t.block() != Blocks.air && !(t.block() instanceof mindustry.world.blocks.environment.Prop) && t.build == null && !t.block().breakable) || t.floor().isDeep();
                if (t.build != null) blocked = false;
                if (blocked) continue;
                dist[nx][ny] = dist[p[0]][p[1]] + 1;
                q.add(new int[]{nx, ny});
            }
        }
        return -1;
    }

    static void save(String file, String displayName, String description) throws Exception {
        Vars.world.endMapLoad();
        StringMap tags = new StringMap();
        tags.put("name", displayName);
        tags.put("author", "Supernova");
        tags.put("description", description);
        Map map = new Map(tags);
        Fi out = new Fi("assets/maps/" + file + ".msav");
        out.parent().mkdirs();
        MapIO.writeMap(out, map);
        preview(file);
        System.out.println("Wrote " + out.path() + " (" + out.length() / 1024 + " KB)");
    }

    /** PNG preview with a fixed palette (floors, walls, ores, core, spawn). 4 px per tile. */
    static void preview(String file) throws Exception {
        int s = 4;
        BufferedImage img = new BufferedImage(W * s, H * s, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < W; x++) for (int y = 0; y < H; y++) {
            Tile t = tiles.get(x, y);
            int c = color(t);
            for (int dx = 0; dx < s; dx++) for (int dy = 0; dy < s; dy++) img.setRGB(x * s + dx, (H - 1 - y) * s + dy, c);
        }
        File f = new File("build/map-previews/" + file + ".png");
        f.getParentFile().mkdirs();
        ImageIO.write(img, "png", f);
    }

    static int color(Tile t) {
        if (t.build != null) return t.team() == SnTeams.aliens ? 0x6a3fd1 : 0xffd37f;
        if (t.overlay() == Blocks.spawn) return 0xff3030;
        Block b = t.block();
        if (b == Blocks.stoneWall) return 0x3d3a40;
        if (b == Blocks.daciteWall) return 0x5c564f;
        if (b == Blocks.iceWall) return 0x8fb3c7;
        if (b != Blocks.air) return 0x6b6670;
        Block o = t.overlay();
        if (o == SnBlocks.oreFerrite) return 0xb08a6e;
        if (o == SnBlocks.oreCobaltine) return 0x4f74c9;
        if (o == SnBlocks.oreQuartzite) return 0xe8b7c4;
        Floor f = t.floor();
        if (f == Blocks.deepwater) return 0x2c4d7a;
        if (f == Blocks.ice) return 0xbfe3ef;
        if (f == Blocks.snow) return 0xe6eef2;
        if (f == Blocks.iceSnow) return 0xd3e7ee;
        if (f == Blocks.basalt) return 0x58555c;
        if (f == Blocks.dacite) return 0x9a9188;
        return 0x7d7a80; // stone
    }

    static void place(Block block, int x, int y, Team team) {
        tiles.get(x, y).setBlock(block, team, 0);
    }
}
