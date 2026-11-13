package supernova.content;

import arc.graphics.Color;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.game.SpawnGroup;
import mindustry.gen.LegsUnit;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

/**
 * The Aliens: Aphelion's hostile faction. Two unit trees, all legged walkers with black shells.
 *
 * <ul>
 *   <li><b>Bugs</b> (violet glow), the light units: Hatchling (T1), Malign (T2)</li>
 *   <li><b>Predators</b> (crimson glow), the beatdown units: Leacher (T1), Ripper (T2)</li>
 * </ul>
 */
public final class SnAliens {
    public static final Color BUG_GLOW = Color.valueOf("b98cff");
    public static final Color BUG_GLOW_DARK = Color.valueOf("6a3fd1");
    public static final Color PRED_GLOW = Color.valueOf("ff4d6a");
    public static final Color PRED_GLOW_DARK = Color.valueOf("a3203a");
    public static final Color OUTLINE = Color.valueOf("3a3148");

    // Bugs
    public static UnitType hatchling, malign;
    // Predators
    public static UnitType leacher, ripper;

    /** Vanilla unit name -> Alien replacement, used to convert generated waves. */
    private static final ObjectMap<String, UnitType> replacements = new ObjectMap<>();

    private SnAliens() {}

    public static void load() {
        // ================= Bugs =================

        // T1: weak, fast and annoying. Rapid little acid spits.
        hatchling = walker("hatchling", 4, 130f, 0.95f, 8f, 0f);
        hatchling.weapons.add(new Weapon() {{
            reload = 13f;
            x = 2.5f;
            y = 2f;
            alternate = true;
            shootSound = Sounds.shootLancer;
            bullet = new BasicBulletType(3.2f, 6f) {{
                width = 6f;
                height = 8f;
                lifetime = 42f;
                backColor = BUG_GLOW_DARK;
                frontColor = BUG_GLOW;
                hitColor = BUG_GLOW;
                hitEffect = Fx.hitLaser;
            }};
        }});

        // T2: tougher. Lobs purple orbs that burst into a ring of smaller orbs.
        malign = walker("malign", 4, 560f, 0.55f, 14f, 3f);
        malign.weapons.add(new Weapon() {{
            reload = 48f;
            x = 0f;
            y = 4f;
            mirror = false;
            shootSound = Sounds.shootLancer;
            bullet = new BasicBulletType(2.4f, 16f) {{
                sprite = "circle-bullet";
                width = height = 12f;
                shrinkY = 0f;
                lifetime = 62f;
                backColor = BUG_GLOW_DARK;
                frontColor = BUG_GLOW;
                hitColor = BUG_GLOW;
                hitEffect = Fx.hitLaser;
                trailColor = BUG_GLOW_DARK;
                trailLength = 6;
                trailWidth = 2.5f;
                fragBullets = 5;
                fragRandomSpread = 0f;
                fragSpread = 72f;
                fragVelocityMin = 0.9f;
                fragBullet = new BasicBulletType(2.6f, 8f) {{
                    sprite = "circle-bullet";
                    width = height = 7f;
                    shrinkY = 0f;
                    lifetime = 20f;
                    backColor = BUG_GLOW_DARK;
                    frontColor = BUG_GLOW;
                    hitColor = BUG_GLOW;
                    hitEffect = Fx.hitLaser;
                }};
            }};
        }});

        // ================= Predators =================

        // T1: tough brawler. Short-range bites heal it for 60% of the damage dealt.
        leacher = walker("leacher", 6, 900f, 0.5f, 16f, 6f);
        leacher.weapons.add(new Weapon() {{
            reload = 24f;
            x = 4f;
            y = 6f;
            alternate = true;
            shootSound = Sounds.shootLancer;
            bullet = new BasicBulletType(3f, 24f) {{
                width = 9f;
                height = 10f;
                lifetime = 17f;
                lifesteal = 0.6f;
                backColor = PRED_GLOW_DARK;
                frontColor = PRED_GLOW;
                hitColor = PRED_GLOW;
                hitEffect = Fx.hitLaser;
            }};
        }});

        // T2: heavy hitter with a sustained Vela-style beam. Slows itself while firing.
        ripper = walker("ripper", 6, 1700f, 0.4f, 22f, 8f);
        ripper.weapons.add(new Weapon() {{
            reload = 170f;
            x = 0f;
            y = 9f;
            mirror = false;
            continuous = true;
            cooldownTime = 180f;
            shake = 1.5f;
            shootSound = Sounds.beamLustre;
            shootStatus = StatusEffects.slow;
            shootStatusDuration = 110f;
            bullet = new ContinuousLaserBulletType(13f) {{
                length = 130f;
                lifetime = 100f;
                width = 6f;
                shake = 1f;
                largeHit = false;
                hitEffect = Fx.hitLaser;
                hitColor = PRED_GLOW;
                colors = new Color[]{PRED_GLOW_DARK.cpy().a(0.4f), PRED_GLOW_DARK, PRED_GLOW, Color.white};
                drawSize = 300f;
            }};
        }});

        // Wave conversion: light/fast/air vanilla units become Bugs, heavy ones become Predators.
        map(hatchling, "dagger", "crawler", "flare", "nova", "mono", "poly", "risso", "retusa");
        map(malign, "horizon", "zenith", "atrax", "mega", "antumbra", "eclipse", "quad", "oct", "oxynoe");
        map(leacher, "mace", "pulsar", "minke", "cyerce");
        map(ripper, "fortress", "quasar", "spiroct", "scepter", "vela", "arkyid", "reign", "corvus", "toxopid",
            "bryde", "sei", "omura", "aegires", "navanax");
    }

    private static UnitType walker(String name, int legs, float health, float speed, float hitSize, float armor) {
        UnitType t = new UnitType(name);
        t.constructor = LegsUnit::create;
        t.health = health;
        t.speed = speed;
        t.hitSize = hitSize;
        t.armor = armor;
        t.outlineColor = OUTLINE;
        t.legCount = legs;
        t.legLength = hitSize * 0.9f + 4f;
        t.legMoveSpace = 1.4f;
        t.legBaseOffset = hitSize * 0.15f;
        t.legExtension = -2f;
        t.legSpeed = 0.12f;
        t.legPhysicsLayer = false;
        t.allowLegStep = false;
        t.hovering = true;
        t.rotateSpeed = 4f;
        t.shadowElevation = 0.2f;
        t.groundLayer = Layer.legUnit;
        return t;
    }

    private static void map(UnitType alien, String... vanilla) {
        for (String v : vanilla) replacements.put(v, alien);
    }

    /** Swaps non-Alien units in wave spawn groups (vanilla, or Aliens since removed) for current Aliens. */
    public static void convertWaves(Seq<SpawnGroup> groups) {
        if (groups == null) return;
        for (SpawnGroup g : groups) {
            if (g.type != null && isCurrentAlien(g.type)) continue;
            g.type = g.type == null ? hatchling : replacements.get(g.type.name, hatchling);
        }
    }

    /** True if any wave group uses something other than a current Alien unit. */
    public static boolean needsConversion(Seq<SpawnGroup> groups) {
        if (groups == null) return false;
        for (SpawnGroup g : groups) {
            if (g.type == null || !isCurrentAlien(g.type)) return true;
        }
        return false;
    }

    private static boolean isCurrentAlien(UnitType t) {
        return t == hatchling || t == malign || t == leacher || t == ripper;
    }
}
