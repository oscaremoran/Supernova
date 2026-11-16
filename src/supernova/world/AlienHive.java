package supernova.world;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.Env;

/**
 * An Alien nest that hatches units on a timer, up to a cap of living units nearby.
 * Spawning only happens on the server (or in singleplayer); units sync to clients normally.
 */
public class AlienHive extends Block {
    public UnitType unitType;
    /** Ticks between spawns. */
    public float spawnTime = 40f * 60f;
    /** Max living units of {@link #unitType} on this team within {@link #capRange}. */
    public int maxUnits = 3;
    public float capRange = 18f * 8f;

    /** Ticks between attempts to bud a new Hive nearby (0 disables spreading). */
    public float spreadTime = 4f * 60f * 60f;
    /** New Hives appear this many tiles away (min to max). */
    public int spreadMin = 9, spreadMax = 15;
    /** No budding if this many Hives of the team are already within {@link #clusterRange} tiles. */
    public int maxCluster = 3;
    public int clusterRange = 30;
    /** Map-wide cap on Hives per team. */
    public int maxTotal = 12;
    /** Never bud within this many tiles of an enemy building. */
    public int keepAway = 12;

    public AlienHive(String name) {
        super(name);
        update = true;
        solid = true;
        envEnabled = Env.any;
        buildType = HiveBuild::new;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (HiveBuild b) -> new Bar("bar.progress", SnHiveColor.GLOW, () -> b.progress / spawnTime));
    }

    /** Small holder so the bar colour doesn't depend on load order. */
    private static final class SnHiveColor {
        static final arc.graphics.Color GLOW = arc.graphics.Color.valueOf("b98cff");
    }

    public class HiveBuild extends Building {
        public float progress;
        public float spread;

        @Override
        public void updateTile() {
            if (unitType == null) return;
            progress += delta() * Vars.state.rules.unitBuildSpeed(team);
            if (progress >= spawnTime) {
                progress = 0f;
                if (!Vars.net.client() && Units.count(x, y, capRange, u -> u.team == team && u.type == unitType) < maxUnits) {
                    float angle = Mathf.random(360f);
                    float r = size * 4f + 6f;
                    unitType.spawn(team, x + Mathf.cosDeg(angle) * r, y + Mathf.sinDeg(angle) * r);
                    supernova.content.SnFx.hiveHatch.at(x, y);
                }
            }

            if (spreadTime > 0f) {
                spread += delta();
                if (spread >= spreadTime) {
                    spread = 0f;
                    if (!Vars.net.client()) trySpread();
                }
            }
        }

        /** Buds a new Hive nearby if the cluster and map caps allow and there's clear ground. */
        protected void trySpread() {
            int total = 0, cluster = 0;
            for (Building b : team.data().buildings) {
                if (b.block != AlienHive.this) continue;
                total++;
                if (b.within(this, clusterRange * 8f)) cluster++;
            }
            if (total >= maxTotal || cluster >= maxCluster) return;

            for (int attempt = 0; attempt < 16; attempt++) {
                float angle = Mathf.random(360f);
                int dist = Mathf.random(spreadMin, spreadMax);
                int cx = tile.x + Math.round(Mathf.cosDeg(angle) * dist);
                int cy = tile.y + Math.round(Mathf.sinDeg(angle) * dist);
                if (canBud(cx, cy)) {
                    mindustry.world.Tile t = Vars.world.tile(cx, cy);
                    t.setNet(AlienHive.this, team, 0);
                    supernova.content.SnFx.hiveHatch.at(t.worldx(), t.worldy());
                    return;
                }
            }
        }

        protected boolean canBud(int cx, int cy) {
            int offset = (size - 1) / 2;
            for (int dx = 0; dx < size; dx++) {
                for (int dy = 0; dy < size; dy++) {
                    mindustry.world.Tile t = Vars.world.tile(cx - offset + dx, cy - offset + dy);
                    if (t == null || t.block() != mindustry.content.Blocks.air || t.floor().isDeep() || t.floor().solid || t.overlay() == mindustry.content.Blocks.spawn) return false;
                }
            }
            return Vars.indexer.findEnemyTile(team, cx * 8f, cy * 8f, keepAway * 8f, b -> true) == null;
        }

        @Override
        public void draw() {
            super.draw();
            // Pulse as the next hatching approaches.
            float f = progress / spawnTime;
            Draw.z(mindustry.graphics.Layer.blockOver);
            Draw.color(SnHiveColor.GLOW, f * (0.35f + Mathf.absin(6f, 0.25f)));
            arc.graphics.g2d.Fill.circle(x, y, size * 2.2f * (0.6f + f * 0.4f));
            Draw.reset();
        }

        @Override
        public byte version() {
            return 2;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
            write.f(spread);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) progress = read.f();
            if (revision >= 2) spread = read.f();
        }
    }
}
