package supernova.world;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

/**
 * Sends drone missions into the Aphelion Belt and returns with asteroid cargo.
 *
 * <p>Missions run on power. The propellant liquid is an optional booster that speeds missions up,
 * and some zones (the deep belt) can't be reached without it.
 *
 * <p>Multiplayer: the zone is changed only through {@link Building#configure}, which the game
 * syncs. Cargo rolls are seeded from the building position and mission count, so server and
 * clients compute the same result. All mission state is saved in {@link OrbitalUplinkBuild#write}.
 */
public class OrbitalUplink extends Block {
    /** A belt zone that missions can target. */
    public static class Zone {
        public final String name;
        public final Color color;
        /** Mission duration in ticks at normal speed. */
        public final float time;
        /** If true, missions here need the propellant liquid to make progress at all. */
        public final boolean needsPropellant;
        public final Seq<Yield> yields = new Seq<>();

        public Zone(String name, Color color, float time, boolean needsPropellant) {
            this.name = name;
            this.color = color;
            this.time = time;
            this.needsPropellant = needsPropellant;
        }

        public Zone yield(Item item, int min, int max) {
            yields.add(new Yield(item, min, max));
            return this;
        }

        public int maxCargo() {
            int total = 0;
            for (Yield y : yields) total += y.max;
            return total;
        }

        public String localized() {
            return Core.bundle.get("zone.supernova-" + name + ".name", name);
        }
    }

    public static class Yield {
        public final Item item;
        public final int min, max;

        public Yield(Item item, int min, int max) {
            this.item = item;
            this.min = min;
            this.max = max;
        }
    }

    public final Seq<Zone> zones = new Seq<>();
    /** Liquid that acts as propellant/booster. Must also be added with consumeLiquid(...).boost(). */
    public Liquid propellant;
    /** Speed multiplier while propellant is supplied. */
    public float boostMultiplier = 2.5f;
    /** Fraction of a mission spent on the launch and landing animations. */
    public float animFraction = 0.12f;

    public TextureRegion podRegion;

    public OrbitalUplink(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        configurable = true;
        saveConfig = true;
        buildType = OrbitalUplinkBuild::new;

        config(Integer.class, (OrbitalUplinkBuild build, Integer index) -> build.setZone(index));
    }

    public OrbitalUplink zone(Zone zone) {
        zones.add(zone);
        return this;
    }

    @Override
    public void init() {
        // Leave room for a full cargo on top of whatever is already waiting to be unloaded.
        int maxCargo = 0;
        for (Zone z : zones) maxCargo = Math.max(maxCargo, z.maxCargo());
        itemCapacity = Math.max(itemCapacity, maxCargo * 2);
        super.init();
    }

    @Override
    public void load() {
        super.load();
        podRegion = Core.atlas.find(name + "-pod");
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.booster, Strings.autoFixed(boostMultiplier, 1) + "x " + Core.bundle.get("stat.supernova-mission-speed", "mission speed"));
        stats.add(Stat.output, table -> {
            table.row();
            for (Zone zone : zones) {
                table.table(Styles.grayPanel, t -> {
                    t.left().defaults().left().pad(3);
                    t.add("[#" + zone.color + "]" + zone.localized() + "[]  " + Strings.autoFixed(zone.time / 60f, 1) + " " + StatUnit.seconds.localized()
                        + (zone.needsPropellant && propellant != null ? "  [lightgray](" + propellant.localizedName + ")" : ""));
                    t.row();
                    t.table(items -> {
                        for (Yield y : zone.yields) {
                            items.image(y.item.uiIcon).size(24f).padRight(2);
                            items.add(y.min + "-" + y.max).padRight(10);
                        }
                    });
                }).growX().pad(4).row();
            }
        });
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("mission", (OrbitalUplinkBuild b) -> new Bar(
            () -> b.zone().localized(),
            () -> b.zone().color,
            () -> b.progress
        ));
    }

    public class OrbitalUplinkBuild extends Building {
        /** Index into {@link #zones}. */
        public int zoneIndex = 0;
        /** Mission progress from 0 to 1. */
        public float progress;
        /** Missions completed; part of the cargo RNG seed. */
        public int missions;

        public Zone zone() {
            return zones.get(Mathf.clamp(zoneIndex, 0, zones.size - 1));
        }

        public void setZone(int index) {
            if (index < 0 || index >= zones.size || index == zoneIndex) return;
            zoneIndex = index;
            // Retargeting recalls the drones, so the current mission is lost.
            progress = 0f;
        }

        /** Propellant in the tank. Checked directly, because optionalEfficiency only updates while consuming. */
        public boolean hasPropellant() {
            return propellant != null && liquids != null && liquids.get(propellant) > 0.001f;
        }

        public boolean cargoBlocked() {
            return items.total() + zone().maxCargo() > itemCapacity;
        }

        public boolean canRun() {
            return !cargoBlocked() && (!zone().needsPropellant || hasPropellant());
        }

        @Override
        public boolean shouldConsume() {
            return canRun();
        }

        @Override
        public void updateTile() {
            if (canRun() && efficiency > 0f) {
                float speed = efficiency * (optionalEfficiency > 0f ? boostMultiplier : 1f);
                progress += speed * delta() / zone().time;
                if (progress >= 1f) {
                    progress = 0f;
                    deliver();
                }
            }
            dump();
        }

        /** Rolls and stores a cargo. Deterministic for a given position and mission count. */
        protected void deliver() {
            Rand rand = new Rand(pos() * 7919L + missions * 104729L);
            missions++;
            for (Yield y : zone().yields) {
                int amount = rand.random(y.min, y.max);
                if (amount > 0) items.add(y.item, Math.min(amount, itemCapacity - items.get(y.item)));
            }
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return false;
        }

        @Override
        public float progress() {
            return progress;
        }

        @Override
        public void draw() {
            super.draw();

            // Zone beacon in the centre.
            Draw.z(Layer.blockOver);
            Draw.color(zone().color, 0.6f + Mathf.absin(12f, 0.4f));
            Fill.square(x, y, 2.5f, 45f);
            Draw.reset();

            if (!podRegion.found()) return;

            // Launch: the pod rises and fades out. Landing: it drops back in.
            float rise;
            if (progress < animFraction) {
                rise = Interp.pow2In.apply(progress / animFraction);
            } else if (progress > 1f - animFraction) {
                rise = Interp.pow2Out.apply((1f - progress) / animFraction);
            } else {
                return; // in the belt
            }

            float height = rise * size * 8f * 2.5f;
            Draw.z(Layer.flyingUnit);
            Draw.alpha(1f - rise);
            Draw.rect(podRegion, x, y + height);
            // Engine flare under the pod.
            Draw.color(Color.valueOf("b8f0ff"), (1f - rise) * 0.8f);
            Fill.circle(x, y + height - podRegion.height * Draw.scl * 0.4f, 2f + Mathf.absin(4f, 1f));
            Draw.reset();
        }

        @Override
        public void buildConfiguration(Table table) {
            Table buttons = new Table();
            for (int i = 0; i < zones.size; i++) {
                int index = i;
                Zone zone = zones.get(i);
                Item icon = zone.yields.first().item;
                buttons.button(new TextureRegionDrawable(icon.uiIcon), Styles.clearNoneTogglei, 32f, () -> configure(index))
                    .size(48f).checked(b -> zoneIndex == index)
                    .tooltip(zone.localized());
            }
            table.add(buttons);
        }

        @Override
        public Object config() {
            return zoneIndex;
        }

        @Override
        public byte version() {
            return 1;
        }

        @Override
        public void write(Writes write) {
            super.write(write);
            write.b(zoneIndex);
            write.f(progress);
            write.i(missions);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            if (revision >= 1) {
                zoneIndex = read.b();
                progress = read.f();
                missions = read.i();
            }
        }
    }
}
