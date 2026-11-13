package supernova.content;

import mindustry.content.Fx;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;
import mindustry.world.blocks.production.Drill;
import mindustry.world.blocks.production.GenericCrafter;

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
    }
}
