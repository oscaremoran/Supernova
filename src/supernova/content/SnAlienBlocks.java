package supernova.content;

import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BuildVisibility;
import supernova.world.AlienHive;
import supernova.world.ChitinWall;

import static supernova.content.SnAliens.*;

/**
 * Alien structures for enemy bases. Editor-only: players can't build them, but map makers can
 * place them, and procedural enemy bases on Aphelion are converted to use them.
 * They need no ammo or power.
 */
public final class SnAlienBlocks {
    public static Block hive, sporePod, acidSpire, chitinWall, chitinWallLarge, broodCore;

    private SnAlienBlocks() {}

    public static void load() {
        hive = new AlienHive("hive") {{
            requirements(Category.units, BuildVisibility.editorOnly, ItemStack.empty);
            size = 3;
            health = 2600;
            armor = 4f;
            unitType = hatchling;
            spawnTime = 40f * 60f;
            maxUnits = 3;
        }};

        sporePod = new PowerTurret("spore-pod") {{
            requirements(Category.turret, BuildVisibility.editorOnly, ItemStack.empty);
            size = 1;
            health = 420;
            range = 110f;
            reload = 26f;
            rotateSpeed = 8f;
            shootCone = 20f;
            inaccuracy = 3f;
            shootSound = Sounds.shootLancer;
            shootType = new BasicBulletType(3f, 11f) {{
                width = 7f;
                height = 9f;
                lifetime = 37f;
                backColor = BUG_GLOW_DARK;
                frontColor = BUG_GLOW;
                hitColor = BUG_GLOW;
                hitEffect = Fx.hitLaser;
            }};
        }};

        acidSpire = new PowerTurret("acid-spire") {{
            requirements(Category.turret, BuildVisibility.editorOnly, ItemStack.empty);
            size = 2;
            health = 1100;
            armor = 3f;
            range = 125f; // just inside Bolter range (130): players can siege it by building forward
            reload = 70f;
            rotateSpeed = 4f;
            shootCone = 10f;
            shootSound = Sounds.shootLancer;
            shootType = new BasicBulletType(2.6f, 18f) {{
                sprite = "circle-bullet";
                width = height = 13f;
                shrinkY = 0f;
                lifetime = 50f;
                backColor = BUG_GLOW_DARK;
                frontColor = BUG_GLOW;
                hitColor = BUG_GLOW;
                hitEffect = Fx.hitLaser;
                trailColor = BUG_GLOW_DARK;
                trailLength = 6;
                trailWidth = 2.5f;
                fragBullets = 6;
                fragSpread = 60f;
                fragRandomSpread = 0f;
                fragBullet = new BasicBulletType(2.6f, 7f) {{
                    sprite = "circle-bullet";
                    width = height = 7f;
                    shrinkY = 0f;
                    lifetime = 18f;
                    backColor = BUG_GLOW_DARK;
                    frontColor = BUG_GLOW;
                    hitColor = BUG_GLOW;
                }};
            }};
        }};

        chitinWall = new ChitinWall("chitin-wall") {{
            requirements(Category.defense, BuildVisibility.editorOnly, ItemStack.empty);
            health = 110 * 4;
            armor = 2f;
        }};

        chitinWallLarge = new ChitinWall("chitin-wall-large") {{
            requirements(Category.defense, BuildVisibility.editorOnly, ItemStack.empty);
            size = 2;
            health = 110 * 4 * 4;
            armor = 2f;
        }};

        broodCore = new CoreBlock("brood-core") {{
            requirements(Category.effect, BuildVisibility.editorOnly, ItemStack.empty);
            size = 4;
            health = 5000;
            armor = 5f;
            itemCapacity = 6000;
            unitType = hatchling;
            unitCapModifier = 12;
        }};
    }
}
