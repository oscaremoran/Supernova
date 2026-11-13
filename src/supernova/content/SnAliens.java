package supernova.content;

import arc.graphics.Color;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.game.SpawnGroup;
import mindustry.gen.LegsUnit;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

import arc.struct.Seq;

/**
 * The Aliens: Aphelion's hostile faction.
 * Ground: chitinous walkers (Skitter, Stalker, Brute) plus the Spawnling swarm.
 * Air: Wisp, Shrike, Harrower.
 * Higher vanilla tiers in generated waves map onto Brute and Harrower.
 * Black shells with violet bioluminescence.
 */
public final class SnAliens {
    public static final Color GLOW = Color.valueOf("b98cff");
    public static final Color GLOW_DARK = Color.valueOf("6a3fd1");
    public static final Color OUTLINE = Color.valueOf("3a3148");

    public static UnitType spawnling, skitter, stalker, brute;
    public static UnitType wisp, shrike, harrower;

    /** Vanilla unit name -> Alien replacement, used to convert generated waves. */
    private static final ObjectMap<String, UnitType> replacements = new ObjectMap<>();

    private SnAliens() {}

    public static void load() {
        // ---------------- ground ----------------
        spawnling = walker("spawnling", 4, 140f, 1.1f, 7f, 0f);
        spawnling.speed = 1.15f;
        spawnling.legLength = 6f;
        spawnling.weapons.add(new Weapon() {{
            shootOnDeath = true;
            reload = 24f;
            shootCone = 180f;
            x = 0f;
            mirror = false;
            shootSound = Sounds.none;
            bullet = new BulletType() {{
                collidesTiles = false;
                collides = false;
                hitSound = Sounds.none;
                rangeOverride = 28f;
                hitEffect = Fx.sapped;
                speed = 0f;
                splashDamageRadius = 44f;
                instantDisappear = true;
                splashDamage = 75f;
                killShooter = true;
                hittable = false;
                collidesAir = true;
            }};
        }});

        skitter = walker("skitter", 4, 170f, 0.6f, 9f, 0f);
        skitter.weapons.add(spit(20f, 9f, 2.6f, 55f, 3f));

        stalker = walker("stalker", 4, 520f, 0.5f, 13f, 3f);
        stalker.weapons.add(spit(30f, 18f, 3f, 60f, 6f));

        brute = walker("brute", 6, 1000f, 0.42f, 18f, 6f);
        brute.weapons.add(new Weapon() {{
            reload = 70f;
            x = 0f;
            y = 0f;
            mirror = false;
            shootSound = Sounds.shootLancer;
            bullet = new ArtilleryBulletType(2.6f, 10f) {{
                lifetime = 85f;
                width = height = 14f;
                collidesTiles = false;
                splashDamageRadius = 34f;
                splashDamage = 55f;
                backColor = GLOW_DARK;
                frontColor = GLOW;
                hitColor = GLOW;
                hitEffect = Fx.sapped;
            }};
        }});
        brute.weapons.add(spit(26f, 14f, 3f, 50f, 9f));

        // ---------------- air ----------------
        wisp = flyer("wisp", 70f, 3.6f, 8f, 0f);
        wisp.circleTarget = true;
        wisp.weapons.add(spit(16f, 6f, 3f, 45f, 2f));

        shrike = flyer("shrike", 340f, 2.4f, 13f, 1f);
        shrike.weapons.add(spit(24f, 14f, 3.2f, 50f, 5f));

        harrower = flyer("harrower", 700f, 1.7f, 19f, 5f);
        harrower.weapons.add(new Weapon() {{
            reload = 45f;
            x = 6f;
            y = 0f;
            alternate = true;
            shootSound = Sounds.shootLancer;
            bullet = new BasicBulletType(3.2f, 32f) {{
                width = 9f;
                height = 13f;
                lifetime = 55f;
                homingPower = 0.06f;
                splashDamageRadius = 22f;
                splashDamage = 20f;
                backColor = GLOW_DARK;
                frontColor = GLOW;
                hitColor = GLOW;
                trailColor = GLOW_DARK;
                trailLength = 6;
                trailWidth = 2f;
            }};
        }});

        // Wave replacements: vanilla tiers map onto Alien tiers.
        map(spawnling, "crawler");
        map(skitter, "dagger", "nova", "risso", "retusa");
        map(stalker, "mace", "pulsar", "atrax", "minke", "oxynoe");
        map(brute, "fortress", "quasar", "spiroct", "bryde", "cyerce");
        map(brute, "scepter", "vela", "arkyid", "sei", "aegires");
        map(brute, "reign", "corvus", "toxopid", "omura", "navanax");
                map(shrike, "horizon", "poly");
        map(harrower, "zenith", "mega");
        map(harrower, "antumbra", "eclipse", "quad", "oct");
        map(wisp, "flare", "mono");
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
        t.allowLegStep = hitSize >= 18f;
        t.hovering = true;
        t.rotateSpeed = 4f;
        t.shadowElevation = 0.2f;
        t.groundLayer = mindustry.graphics.Layer.legUnit;
        t.drawCell = true;
        return t;
    }

    private static UnitType flyer(String name, float health, float speed, float hitSize, float armor) {
        UnitType t = new UnitType(name);
        t.constructor = UnitEntity::create;
        t.flying = true;
        t.health = health;
        t.speed = speed;
        t.hitSize = hitSize;
        t.armor = armor;
        t.outlineColor = OUTLINE;
        t.accel = 0.08f;
        t.drag = 0.03f;
        t.rotateSpeed = 6f;
        t.engineOffset = hitSize * 0.55f;
        t.engineSize = Math.max(1.6f, hitSize * 0.14f);
        t.engineColor = GLOW;
        t.lowAltitude = hitSize >= 30f;
        t.targetGround = true;
        return t;
    }

    /** Twin violet acid bolts from either side of the body. */
    private static Weapon spit(float reload, float damage, float speed, float lifetime, float x) {
        return new Weapon() {{
            this.reload = reload;
            this.x = x;
            this.y = 2f;
            mirror = x > 0f;
            alternate = true;
            shootSound = Sounds.shootLancer;
            bullet = new BasicBulletType(speed, damage) {{
                width = 7f + damage * 0.12f;
                height = 9f + damage * 0.15f;
                this.lifetime = lifetime;
                backColor = GLOW_DARK;
                frontColor = GLOW;
                hitColor = GLOW;
                hitEffect = Fx.hitLaser;
            }};
        }};
    }

    private static void map(UnitType alien, String... vanilla) {
        for (String v : vanilla) replacements.put(v, alien);
    }

    /** Swaps vanilla units in wave spawn groups for their Alien equivalents. */
    public static void convertWaves(Seq<SpawnGroup> groups) {
        if (groups == null) return;
        for (SpawnGroup g : groups) {
            if (g.type == null || g.type.name.startsWith("supernova-")) continue;
            UnitType alien = replacements.get(g.type.name, skitter);
            g.type = alien;
        }
    }

    /** True if any wave group still uses a vanilla unit. */
    public static boolean hasVanillaUnits(Seq<SpawnGroup> groups) {
        if (groups == null) return false;
        for (SpawnGroup g : groups) {
            if (g.type != null && !g.type.name.startsWith("supernova-") && Vars.content.unit(g.type.name) != null) return true;
        }
        return false;
    }
}
