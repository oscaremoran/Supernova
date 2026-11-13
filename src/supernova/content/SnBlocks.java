package supernova.content;

import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
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

    private SnBlocks() {}

    public static void load() {
        // ---- Environment ----
        oreFerrite = new OreBlock("ore-ferrite", ferrite);
        oreCobaltine = new OreBlock("ore-cobaltine", cobaltine);
        oreQuartzite = new OreBlock("ore-quartzite", quartzite);

        // ---- Production ----
        ferriteDrill = new Drill("ferrite-drill") {{
            requirements(Category.production, with(ferrite, 12));
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
    }
}
