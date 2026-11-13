package supernova.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.Junction;
import mindustry.world.blocks.distribution.Router;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.power.SolarGenerator;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.units.UnitFactory;
import mindustry.world.meta.BuildVisibility;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;
import supernova.world.OrbitalUplink;
import supernova.world.OrbitalUplink.Zone;


import static mindustry.type.ItemStack.with;
import static supernova.content.SnItems.*;
import static supernova.content.SnLiquids.*;

public final class SnBlocks {
    // Environment
    public static Block oreFerrite, oreCobaltine, oreQuartzite;
    // Production
    public static Block ferriteDrill;
    // Crafting
    public static Block ferrosteelSmelter, iceMelter, conductiumFabricator, singularityForge;
    // Orbital
    public static Block orbitalUplink;
    // Logistics
    public static Block ferriteConveyor, ferriteJunction, ferriteRouter, ferriteBridge;
    // Power
    public static Block solarArray, volatileBurner, ferritePowerNode;
    // Defense
    public static Block ferriteWall, ferriteWallLarge, ferrosteelWall, ferrosteelWallLarge, bolter;
    // Units
    public static Block droneBay;
    // Storage
    public static Block coreOutpost;

    private SnBlocks() {}

    public static void load() {
        // ---- Environment ----
        oreFerrite = new OreBlock("ore-ferrite", ferrite);
        oreCobaltine = new OreBlock("ore-cobaltine", cobaltine);
        oreQuartzite = new OreBlock("ore-quartzite", quartzite);

        // ---- Production ----
        ferriteDrill = new Drill("ferrite-drill") {{
            requirements(Category.production, with(ferrite, 12));
            alwaysUnlocked = true;
            size = 2;
            tier = 2;
            drillTime = 560f;
        }};

        // ---- Crafting ----
        ferrosteelSmelter = new GenericCrafter("ferrosteel-smelter") {{
            requirements(Category.crafting, with(ferrite, 40, cobaltine, 25));
            size = 2;
            craftTime = 50f;
            craftEffect = Fx.smeltsmoke;
            consumeItems(with(ferrite, 2, quartzite, 1));
            consumePower(0.5f);
            outputItem = new ItemStack(ferrosteel, 1);
        }};

        iceMelter = new GenericCrafter("ice-melter") {{
            requirements(Category.crafting, with(ferrite, 30, ferrosteel, 20));
            size = 2;
            craftTime = 40f;
            hasLiquids = true;
            consumeItem(cryoIce, 1);
            consumePower(0.6f);
            outputLiquid = new LiquidStack(volatiles, 12f / 60f);
        }};

        conductiumFabricator = new GenericCrafter("conductium-fabricator") {{
            requirements(Category.crafting, with(ferrosteel, 40, cobaltine, 50, iridium, 20));
            size = 2;
            craftTime = 70f;
            craftEffect = Fx.formsmoke;
            consumeItems(with(cobaltine, 2, iridium, 1));
            consumePower(1.2f);
            outputItem = new ItemStack(conductium, 1);
        }};

        singularityForge = new GenericCrafter("singularity-forge") {{
            requirements(Category.crafting, with(ferrosteel, 150, conductium, 100, iridium, 80, novium, 30));
            size = 3;
            craftTime = 150f;
            craftEffect = Fx.smeltsmoke;
            hasLiquids = true;
            consumeItems(with(ferrosteel, 2, conductium, 2, novium, 1));
            consumeLiquid(volatiles, 6f / 60f);
            consumePower(4f);
            outputItem = new ItemStack(singularityComposite, 1);
        }};

        // ---- Orbital ----
        orbitalUplink = new OrbitalUplink("orbital-uplink") {{
            requirements(Category.production, with(ferrite, 150, cobaltine, 80, ferrosteel, 120));
            size = 3;
            consumePower(2f);
            hasLiquids = true;
            liquidCapacity = 30f;
            propellant = volatiles;
            consumeLiquid(volatiles, 4f / 60f).boost();

            zone(new Zone("metallic", Color.valueOf("d8dde6"), 30f * 60f, false)
                .yield(iridium, 6, 10)
                .yield(ferrite, 4, 8)
                .yield(cobaltine, 0, 4));
            zone(new Zone("icy", Color.valueOf("a8e6f5"), 25f * 60f, false)
                .yield(cryoIce, 8, 12)
                .yield(quartzite, 2, 5));
            zone(new Zone("deep", Color.valueOf("ff7a3d"), 60f * 60f, true)
                .yield(novium, 1, 3)
                .yield(iridium, 3, 6)
                .yield(cryoIce, 2, 4));
        }};
    

        // ---- Logistics ----
        ferriteConveyor = new Conveyor("ferrite-conveyor") {{
            requirements(Category.distribution, with(ferrite, 1));
            alwaysUnlocked = true;
            health = 50;
            speed = 0.035f;
            displayedSpeed = 4.8f;
        }};

        ferriteJunction = new Junction("ferrite-junction") {{
            requirements(Category.distribution, with(ferrite, 3));
            speed = 26;
            capacity = 6;
            health = 40;
        }};

        ferriteRouter = new Router("ferrite-router") {{
            requirements(Category.distribution, with(ferrite, 3));
        }};

        ferriteBridge = new BufferedItemBridge("ferrite-bridge") {{
            requirements(Category.distribution, with(ferrite, 6, cobaltine, 6));
            fadeIn = moveArrows = false;
            range = 4;
            speed = 74f;
            arrowSpacing = 6f;
            bufferCapacity = 14;
        }};

        // ---- Power ----
        solarArray = new SolarGenerator("solar-array") {{
            requirements(Category.power, with(ferrite, 25, cobaltine, 15, quartzite, 20));
            size = 2;
            powerProduction = 0.5f;
        }};

        volatileBurner = new ConsumeGenerator("volatile-burner") {{
            requirements(Category.power, with(ferrite, 40, ferrosteel, 25, cobaltine, 20));
            size = 2;
            powerProduction = 3f;
            itemDuration = 1f;
            hasLiquids = true;
            consumeLiquid(volatiles, 6f / 60f);
            generateEffect = Fx.generatespark;
        }};

        ferritePowerNode = new PowerNode("ferrite-power-node") {{
            requirements(Category.power, with(ferrite, 2, cobaltine, 4));
            maxNodes = 10;
            laserRange = 6f;
        }};

        // ---- Defense ----
        int wallHealth = 85 * 4;
        ferriteWall = new Wall("ferrite-wall") {{
            requirements(Category.defense, with(ferrite, 6));
            health = wallHealth;
        }};
        ferriteWallLarge = new Wall("ferrite-wall-large") {{
            requirements(Category.defense, with(ferrite, 24));
            size = 2;
            health = wallHealth * 4;
        }};
        ferrosteelWall = new Wall("ferrosteel-wall") {{
            requirements(Category.defense, with(ferrosteel, 6));
            health = 140 * 4;
            armor = 3f;
        }};
        ferrosteelWallLarge = new Wall("ferrosteel-wall-large") {{
            requirements(Category.defense, with(ferrosteel, 24));
            size = 2;
            health = 140 * 4 * 4;
            armor = 3f;
        }};

        bolter = new ItemTurret("bolter") {{
            requirements(Category.turret, with(ferrite, 45, cobaltine, 20));
            size = 2;
            health = 600;
            range = 130f;
            reload = 22f;
            recoil = 1.5f;
            shootY = 5f;
            rotateSpeed = 9f;
            inaccuracy = 2f;
            shootCone = 15f;
            ammo(
                ferrite, new BasicBulletType(3.2f, 14f) {{
                    width = 8f;
                    height = 11f;
                    lifetime = 42f;
                    ammoMultiplier = 2f;
                }},
                ferrosteel, new BasicBulletType(3.6f, 26f) {{
                    width = 9f;
                    height = 13f;
                    lifetime = 38f;
                    pierceCap = 2;
                    pierce = true;
                    ammoMultiplier = 2f;
                    reloadMultiplier = 0.9f;
                }}
            );
            limitRange();
        }};

        // ---- Units ----
        droneBay = new UnitFactory("drone-bay") {{
            requirements(Category.units, with(ferrite, 80, cobaltine, 60, quartzite, 40));
            size = 3;
            consumePower(1.2f);
            plans.add(
                new UnitPlan(SnUnits.prospector, 60f * 20f, with(ferrite, 20, cobaltine, 15)),
                new UnitPlan(SnUnits.surveyor, 60f * 35f, with(ferrosteel, 25, cobaltine, 30)),
                new UnitPlan(SnUnits.hauler, 60f * 40f, with(ferrosteel, 40, cobaltine, 30, iridium, 10))
            );
        }};

        // ---- Storage ----
        coreOutpost = new CoreBlock("core-outpost") {{
            requirements(Category.effect, BuildVisibility.editorOnly, with(ferrite, 1200, cobaltine, 600));
            alwaysUnlocked = true;
            isFirstTier = true;
            unitType = SnUnits.pioneer;
            health = 1400;
            itemCapacity = 4000;
            size = 3;
            unitCapModifier = 8;
        }};
    }
}
