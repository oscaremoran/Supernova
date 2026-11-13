package supernova.world;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
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
                    Fx.spawn.at(x, y);
                }
            }
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
            return 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.f(progress);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) progress = read.f();
        }
    }
}
