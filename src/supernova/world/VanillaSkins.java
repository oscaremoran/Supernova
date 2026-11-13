package supernova.world;

import arc.graphics.g2d.TextureRegion;
import arc.struct.ObjectMap;
import arc.util.Log;
import mindustry.ctype.UnlockableContent;
import mindustry.world.Block;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * Lets a Supernova block borrow a vanilla block's sprites (placeholder art).
 * After all textures load, every TextureRegion field is copied from the vanilla block.
 * Visual only: gameplay and network state are unaffected.
 */
public final class VanillaSkins {
    private static final ObjectMap<Block, Block> skins = new ObjectMap<>();

    private VanillaSkins() {}

    public static <T extends Block> T skin(T block, Block vanilla) {
        skins.put(block, vanilla);
        return block;
    }

    /** Call once textures are loaded (ClientLoadEvent). */
    public static void apply() {
        for (ObjectMap.Entry<Block, Block> e : skins) {
            Block mine = e.key, vanilla = e.value;
            if (!vanilla.getClass().isAssignableFrom(mine.getClass()) && !mine.getClass().getSuperclass().isAssignableFrom(vanilla.getClass())) {
                Log.warn("[Supernova] Skin type mismatch: @ <- @", mine.name, vanilla.name);
            }
            for (Class<?> c = vanilla.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
                if (!c.isInstance(mine)) continue;
                for (Field f : c.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers())) continue;
                    Class<?> t = f.getType();
                    if (t == TextureRegion.class || t == TextureRegion[].class || t == TextureRegion[][].class) {
                        try {
                            f.setAccessible(true);
                            f.set(mine, f.get(vanilla));
                        } catch (Exception ex) {
                            Log.err("[Supernova] Could not skin " + mine.name + "." + f.getName(), ex);
                        }
                    }
                }
            }
        }
    }
}
