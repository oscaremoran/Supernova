package supernova.content;

import arc.struct.Seq;
import mindustry.ai.types.BuilderAI;
import mindustry.ai.types.MinerAI;
import mindustry.entities.bullet.BasicBulletType;
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
            speed = 3f;
            accel = 0.09f;
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
                reload = 18f;
                x = 0f;
                y = 4f;
                mirror = false;
                top = false;
                bullet = new BasicBulletType(3f, 11f) {{
                    width = 7f;
                    height = 9f;
                    lifetime = 55f;
                    buildingDamageMultiplier = 0.01f;
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
    }
}
