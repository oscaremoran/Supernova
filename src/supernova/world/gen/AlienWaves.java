package supernova.world.gen;

import arc.struct.Seq;
import mindustry.content.StatusEffects;
import mindustry.game.SpawnGroup;
import mindustry.type.UnitType;

import static supernova.content.SnAliens.*;

/**
 * Hand-designed Alien wave sets for Aphelion's story sectors.
 * Wave numbers here are 0-based (begin = 0 is the first wave), as in SpawnGroup.
 */
public final class AlienWaves {
    private AlienWaves() {}

    /** Landing Site: Hatchlings only. A gentle ramp; small boss swarms on waves 10 and 15. */
    public static Seq<SpawnGroup> landingSite() {
        return Seq.with(
            group(hatchling, 0, 2, 2.5f, 12, 0, 0),
            group(hatchling, 4, 1, 3f, 6, 0, 10f),
            boss(hatchling, 9, 5, 3, 30f)
        );
    }

    /** Frost Ridge: Hatchlings, with Leachers from wave 6. Boss Leacher on wave 15. */
    public static Seq<SpawnGroup> frostRidge() {
        return Seq.with(
            group(hatchling, 0, 2, 2f, 16, 0, 0),
            group(hatchling, 3, 2, 3f, 10, 0, 12f),
            group(leacher, 5, 1, 4f, 5, 0, 15f),
            boss(leacher, 14, 6, 1, 120f)
        );
    }

    /** Uplink Crater: adds Malign from wave 8. */
    public static Seq<SpawnGroup> uplinkCrater() {
        return Seq.with(
            group(hatchling, 0, 3, 2f, 18, 0, 0),
            group(leacher, 4, 1, 3.5f, 6, 0, 15f),
            group(malign, 7, 1, 4f, 5, 0, 20f),
            boss(malign, 19, 6, 2, 100f)
        );
    }

    /** Ice Shelf: everything; Rippers from wave 18. */
    public static Seq<SpawnGroup> iceShelf() {
        return Seq.with(
            group(hatchling, 0, 3, 1.8f, 20, 0, 5f),
            group(leacher, 3, 1, 3f, 8, 0, 18f),
            group(malign, 6, 1, 3f, 7, 0, 22f),
            group(ripper, 17, 1, 5f, 4, 0, 40f),
            boss(ripper, 24, 6, 1, 250f)
        );
    }

    /** The Hive: attack sector. Constant pressure while the player pushes into the Brood Cores. */
    public static Seq<SpawnGroup> theHive() {
        return Seq.with(
            group(hatchling, 0, 4, 1.5f, 24, 0, 8f),
            group(malign, 2, 1, 2.5f, 8, 0, 25f),
            group(leacher, 2, 2, 2.5f, 10, 0, 20f),
            group(ripper, 8, 1, 4f, 5, 0, 50f)
        );
    }

    private static SpawnGroup group(UnitType type, int begin, int amount, float scaling, int max, int spacing, float shieldScaling) {
        SpawnGroup g = new SpawnGroup(type);
        g.begin = begin;
        g.unitAmount = amount;
        g.unitScaling = scaling;
        g.max = max;
        if (spacing > 0) g.spacing = spacing;
        g.shieldScaling = shieldScaling;
        return g;
    }

    private static SpawnGroup boss(UnitType type, int begin, int spacing, int amount, float shields) {
        SpawnGroup g = new SpawnGroup(type);
        g.begin = begin;
        g.spacing = spacing;
        g.unitAmount = amount;
        g.unitScaling = 999f;
        g.shields = shields;
        g.effect = StatusEffects.boss;
        return g;
    }
}
