package supernova.content;

import arc.struct.Seq;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.MinerAI;
import arc.graphics.Color;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.content.Fx;
import mindustry.gen.Sounds;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import supernova.ai.HaulerAI;

import static supernova.content.SnItems.*;

/** Aphelion's mining unit tree. The Aliens' units come in M5. */
public final class SnUnits {
    /** Core unit: builds and mines by hand. */
    public static UnitType pioneer;
    /** T1 mining drone: mines surface ore automatically and delivers it to the core. */
    public static UnitType prospector;
    /** T2 mining drone: faster, carries more. */
    public static UnitType surveyor;
    /** Cargo drone: carries Orbital Uplink cargo to the core. */
    public static UnitType hauler;
    /** Combat line (built in the Foundry): light gunship, armored gunship, heavy beam gunship. */
    public static UnitType sentry, warden, bastion;

    private SnUnits() {}

    public static void load() {
        Seq<mindustry.type.Item> ores = Seq.with(ferrite, cobaltine, quartzite);

        pioneer = new UnitType("pioneer") {{
            constructor = UnitEntity::create;
            aiController = BuilderAI::new;
            isEnemy = false;
            flying = true;
            lowAltitude = true;
            coreUnitDock = true;
            useUnitCap = false;

            health = 350f;
            speed = 4.2f;
            accel = 0.11f;
            drag = 0.05f;
            hitSize = 10f;
            rotateSpeed = 15f;
            engineOffset = 6f;
            engineSize = 2.4f;

            mineTier = 2;
            mineSpeed = 6.5f;
            mineItems = ores;
            buildSpeed = 0.75f;
            itemCapacity = 40;

            weapons.add(new Weapon() {{
                reload = 32f;
                x = 0f;
                y = 5f;
                mirror = false;
                top = false;
                shootSound = Sounds.shootLaser;
                bullet = new LaserBulletType(20f) {{
                    length = 88f;
                    width = 9f;
                    colors = new Color[]{Color.valueOf("5670b2").a(0.5f), Color.valueOf("8fb0f0"), Color.white};
                    buildingDamageMultiplier = 0.01f;
                    hitColor = Color.valueOf("8fb0f0");
                }};
            }});
        }};

        prospector = new UnitType("prospector") {{
            constructor = UnitEntity::create;
            controller = u -> new MinerAI();
            isEnemy = false;
            flying = true;
            lowAltitude = true;

            health = 120f;
            speed = 2.2f;
            accel = 0.08f;
            drag = 0.05f;
            hitSize = 8f;
            rotateSpeed = 12f;
            engineOffset = 5f;
            engineSize = 1.8f;

            mineTier = 2;
            mineSpeed = 4.5f;
            mineItems = ores;
            itemCapacity = 20;
        }};

        surveyor = new UnitType("surveyor") {{
            constructor = UnitEntity::create;
            controller = u -> new MinerAI();
            isEnemy = false;
            flying = true;
            lowAltitude = true;

            health = 260f;
            armor = 2f;
            speed = 2.5f;
            accel = 0.08f;
            drag = 0.05f;
            hitSize = 11f;
            rotateSpeed = 10f;
            engineOffset = 6.5f;
            engineSize = 2.2f;

            mineTier = 3;
            mineSpeed = 8f;
            mineItems = ores;
            itemCapacity = 40;
        }};

        hauler = new UnitType("hauler") {{
            constructor = UnitEntity::create;
            controller = u -> new HaulerAI();
            isEnemy = false;
            flying = true;
            lowAltitude = true;

            health = 300f;
            armor = 3f;
            speed = 2.6f;
            accel = 0.06f;
            drag = 0.04f;
            hitSize = 13f;
            rotateSpeed = 8f;
            engineOffset = 8f;
            engineSize = 2.8f;

            itemCapacity = 60;
        }};

        // ================= Combat line =================
        Color cobalt = Color.valueOf("8fb0f0"), cobaltDark = Color.valueOf("5670b2");

        // T1: fast, cheap gunship with twin cobalt bolts.
        sentry = new UnitType("sentry") {{
            constructor = UnitEntity::create;
            flying = true;
            health = 200f;
            armor = 1f;
            speed = 3f;
            accel = 0.08f;
            drag = 0.04f;
            hitSize = 10f;
            rotateSpeed = 10f;
            engineOffset = 6f;
            engineSize = 2f;
            circleTarget = true;
            weapons.add(new Weapon() {{
                reload = 18f;
                x = 3.5f;
                y = 2f;
                alternate = true;
                shootSound = Sounds.shootLaser;
                bullet = new BasicBulletType(3.6f, 9f) {{
                    width = 6f;
                    height = 10f;
                    lifetime = 32f;
                    backColor = cobaltDark;
                    frontColor = cobalt;
                    hitColor = cobalt;
                    hitEffect = Fx.hitLaser;
                }};
            }});
        }};

        // T2: armored gunship; heavy ferrosteel slugs with splash.
        warden = new UnitType("warden") {{
            constructor = UnitEntity::create;
            flying = true;
            lowAltitude = true;
            health = 560f;
            armor = 4f;
            speed = 2.2f;
            accel = 0.06f;
            drag = 0.04f;
            hitSize = 14f;
            rotateSpeed = 6f;
            engineOffset = 8f;
            engineSize = 2.8f;
            weapons.add(new Weapon() {{
                reload = 38f;
                x = 5f;
                y = 1f;
                alternate = true;
                shake = 1f;
                shootSound = Sounds.shootLaser;
                bullet = new BasicBulletType(3.2f, 26f) {{
                    width = 9f;
                    height = 13f;
                    lifetime = 40f;
                    splashDamage = 16f;
                    splashDamageRadius = 22f;
                    backColor = Color.valueOf("474c56");
                    frontColor = Color.valueOf("c9cfda");
                    hitColor = Color.valueOf("c9cfda");
                    hitEffect = Fx.flakExplosion;
                }};
            }});
        }};

        // T3: heavy gunship with a conductium beam, for pushing into Alien bases.
        bastion = new UnitType("bastion") {{
            constructor = UnitEntity::create;
            flying = true;
            lowAltitude = true;
            health = 1150f;
            armor = 7f;
            speed = 1.5f;
            accel = 0.05f;
            drag = 0.04f;
            hitSize = 20f;
            rotateSpeed = 4f;
            engineOffset = 11f;
            engineSize = 3.6f;
            weapons.add(new Weapon() {{
                reload = 60f;
                x = 0f;
                y = 8f;
                mirror = false;
                shake = 2f;
                shootSound = Sounds.shootLaser;
                bullet = new LaserBulletType(70f) {{
                    length = 140f;
                    width = 14f;
                    colors = new Color[]{Color.valueOf("e0b04a").a(0.5f), Color.valueOf("e0b04a"), Color.white};
                    hitColor = Color.valueOf("e0b04a");
                }};
            }});
            weapons.add(new Weapon() {{
                reload = 20f;
                x = 8f;
                y = -2f;
                alternate = true;
                shootSound = Sounds.shootLaser;
                bullet = new BasicBulletType(3.6f, 10f) {{
                    width = 6f;
                    height = 10f;
                    lifetime = 34f;
                    backColor = cobaltDark;
                    frontColor = cobalt;
                    hitColor = cobalt;
                    hitEffect = Fx.hitLaser;
                }};
            }});
        }};
    }
}
